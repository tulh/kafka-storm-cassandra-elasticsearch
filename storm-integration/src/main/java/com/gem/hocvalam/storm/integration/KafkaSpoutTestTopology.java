package com.gem.hocvalam.storm.integration;

import com.datastax.driver.core.*;
import com.datastax.driver.core.utils.UUIDs;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.kafka.*;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tulh on 05/08/2016.
 */
public class KafkaSpoutTestTopology
{
    public static final Logger LOG = LoggerFactory.getLogger(KafkaSpoutTestTopology.class);

    private final static String USERNAME = "cassandra";
    private final static String PASSWORD = "cassandra";
    private final static String HOST = "172.16.10.254";
    private final static String KEYSPACE = "hocvalam";


    public static class CassandraWriterBolt extends BaseRichBolt
    {
        Session session;
        OutputCollector collector;

        @Override
        public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector)
        {
            this.collector = outputCollector;
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer declarer)
        {
            declarer.declare(new Fields("name", "post_id", "channel_ids", "published_time"));
        }

        @Override
        public void execute(Tuple tuple)
        {
            try
            {
                CassandraConnection connection = new CassandraConnection(HOST, KEYSPACE, USERNAME, PASSWORD);
                session = connection.getSession();
                LOG.info("content " + tuple.getString(0));
                UserActivity userActivity = new UserActivity(new Gson().fromJson(tuple.getString(0), Post.class));
                boundCQLStatement(tuple, userActivity);
                connection.close();
            }
            catch (Throwable t)
            {
                collector.reportError(t);
                collector.fail(tuple);
                LOG.error("tuple data error " + t.toString());
            }
        }

        public void boundCQLStatement(Tuple input, UserActivity userActivity)
        {
            session.execute("use hocvalam;");
            PreparedStatement statement = session.prepare(
                    "INSERT INTO user_activity " +
                            "(user_id, interaction_date, interaction_time, activity_id, activity_type, data) " +
                            "VALUES (?, ?, ?, ?, ?, ?);");
            BoundStatement boundStatement = new BoundStatement(statement);
            session.execute(boundStatement.bind(
                    userActivity.getUserId(),
                    userActivity.getInteractionDate(),
                    UUIDs.startOf(userActivity.getInteractionTime()),
                    userActivity.getActivityId(),
                    userActivity.getActivityType(),
                    userActivity.getData()));
            Set<String> userIdSet = new HashSet<>();
            final Pattern pattern = Pattern.compile("(?:\\s|\\A)[@]+([A-Za-z0-9-_]+)");
            final Matcher matcher = pattern.matcher(userActivity.getData());
            while (matcher.find())
            {
                userIdSet.add(matcher.group(1));
            }
            if (!userIdSet.isEmpty())
            {
                String name = "";
                PreparedStatement selectStatement = session.prepare("select user_id, first_name, last_name from user where user_id = ?;");
                BoundStatement bst = new BoundStatement(selectStatement);
                ResultSet results = session.execute(bst.bind(userActivity.getUserId()));
                for (Row row : results)
                {
                    name = row.getString("first_name") + StringUtils.SPACE + row.getString("last_name");
                    break;
                }
                collector.emit(input, new Values(name, userActivity.getActivityId().toString(),
                        StringUtils.join(userIdSet, ","), userActivity.getInteractionTime().toString()));
            }
            collector.ack(input);
        }
    }

    public static class NotifyBolt extends BaseRichBolt
    {
        @Override
        public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector)
        {
        }

        @Override
        public void execute(Tuple tuple)
        {
            HttpPost request = new HttpPost("http://172.16.10.140:8080/send-push");
            request.addHeader("Content-Type", "application/x-www-form-urlencoded");

            // Request parameters and other properties.
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("name", tuple.getStringByField("name")));
            params.add(new BasicNameValuePair("post_id", tuple.getStringByField("post_id")));
            params.add(new BasicNameValuePair("channel_ids", tuple.getStringByField("channel_ids")));
            params.add(new BasicNameValuePair("published_time", tuple.getStringByField("published_time")));
            try
            {
                request.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            }
            catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }

            HttpClientBuilder bld = HttpClientBuilder.create();
            HttpClient client = bld.build();
            try
            {
                HttpResponse response = client.execute(request);
                HttpEntity entity = response.getEntity();
                LOG.info("Post to socket: " + EntityUtils.toString(entity, "UTF-8"));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer)
        {

        }
    }

    private final BrokerHosts brokerHosts;

    public KafkaSpoutTestTopology(String kafkaZookeeper)
    {
        brokerHosts = new ZkHosts(kafkaZookeeper);
    }

    public StormTopology buildTopology()
    {
        SpoutConfig kafkaConfig = new SpoutConfig(brokerHosts, "test", "", "storm");
        kafkaConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("words", new KafkaSpout(kafkaConfig), 10);
        builder.setBolt("print", new CassandraWriterBolt()).shuffleGrouping("words");
        builder.setBolt("notify", new NotifyBolt()).shuffleGrouping("print");
        return builder.createTopology();
    }

    public static void main(String[] args) throws Exception
    {

        String kafkaZk = "172.16.10.132:2181";//args[0];
        KafkaSpoutTestTopology kafkaSpoutTestTopology = new KafkaSpoutTestTopology(kafkaZk);
        Config config = new Config();
        config.put(Config.TOPOLOGY_TRIDENT_BATCH_EMIT_INTERVAL_MILLIS, 2000);
        config.put(Config.TOPOLOGY_MESSAGE_TIMEOUT_SECS, 30);

        StormTopology stormTopology = kafkaSpoutTestTopology.buildTopology();
        if (args != null && args.length > 1)
        {
            String name = args[0];
            config.put(Config.NIMBUS_HOST, "172.16.10.127"); //YOUR NIMBUS'S IP
            config.put(Config.NIMBUS_THRIFT_PORT, 6627);    //int is expected here
            config.setNumWorkers(20);
            config.setMaxSpoutPending(5000);
            StormSubmitter.submitTopology(name, config, stormTopology);
        }
        else
        {
            config.setNumWorkers(2);
            config.setMaxTaskParallelism(Runtime.getRuntime().availableProcessors());
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("kafka", config, stormTopology);
        }
    }
}
