package com.gem.hocvalam.storm.integration;

import com.datastax.driver.core.*;
import com.datastax.driver.core.utils.UUIDs;
import com.gem.hocvalam.storm.integration.tuple.model.Post;
import com.gem.hocvalam.storm.integration.tuple.model.UserActivity;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tulh on 08/08/2016.
 */
public class CassandraWriterBolt extends BaseRichBolt
{
    public static final Logger LOG = LoggerFactory.getLogger(CassandraWriterBolt.class);

    private final static String USERNAME = "cassandra";
    private final static String PASSWORD = "cassandra";
    private final static String HOST = "172.16.10.254";
    private final static String KEYSPACE = "hocvalam";

    private Session session;
    private OutputCollector collector;

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector)
    {
        this.collector = outputCollector;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer)
    {
        declarer.declare(new Fields("name", "post_id", "author_id", "content", "channel_ids", "published_time"));
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

        String name = "";
        PreparedStatement selectStatement = session.prepare("select user_id, first_name, last_name from user where user_id = ?;");
        BoundStatement bst = new BoundStatement(selectStatement);
        ResultSet results = session.execute(bst.bind(userActivity.getUserId()));
        for (Row row : results)
        {
            name = row.getString("first_name") + StringUtils.SPACE + row.getString("last_name");
            break;
        }
        collector.emit(input, new Values(name, userActivity.getActivityId().toString(), userActivity.getUserId().toString(), userActivity.getData(),
                !userIdSet.isEmpty() ? StringUtils.join(userIdSet, ",") : "", userActivity.getInteractionTime().toString()));
        collector.ack(input);
    }
}
