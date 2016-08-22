package com.gem.hocvalam.storm.integration;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.kafka.*;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.topology.TopologyBuilder;

/**
 * Created by tulh on 05/08/2016.
 */
public class KafkaSpoutTopology
{
    private final static String KAFKA_HOST = "172.16.10.132:2181";
    private final static String NIMBUS_HOST = "172.16.10.127";
    private final static String KAFKA_TOPIC = "test";

    private final BrokerHosts brokerHosts;

    public KafkaSpoutTopology(String kafkaZookeeper)
    {
        brokerHosts = new ZkHosts(kafkaZookeeper);
    }

    public StormTopology buildTopology()
    {
        SpoutConfig kafkaConfig = new SpoutConfig(brokerHosts, KAFKA_TOPIC, "/opt/zookeeper-3.4.8/data", "storm");
        kafkaConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
//        kafkaConfig.startOffsetTime = System.currentTimeMillis();
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("words", new KafkaSpout(kafkaConfig), 10);
        builder.setBolt("write-to-cassandra", new CassandraWriterBolt()).shuffleGrouping("words");
        builder.setBolt("notify", new NotifyBolt()).shuffleGrouping("write-to-cassandra");
        builder.setBolt("write-to-elasticsearch", new ElasticSearchBolt()).shuffleGrouping("write-to-cassandra");
        return builder.createTopology();
    }

    public static void main(String[] args) throws Exception
    {

        KafkaSpoutTopology kafkaSpoutTopology = new KafkaSpoutTopology(KAFKA_HOST);
        Config config = new Config();
//        config.put(Config.TOPOLOGY_TRIDENT_BATCH_EMIT_INTERVAL_MILLIS, 2000);
        config.put(Config.TOPOLOGY_MESSAGE_TIMEOUT_SECS, 30);

        StormTopology stormTopology = kafkaSpoutTopology.buildTopology();
        if (args != null && args.length > 1)
        {
            String name = args[0];
            config.put(Config.NIMBUS_HOST, NIMBUS_HOST); //YOUR NIMBUS'S IP
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
            cluster.submitTopology("kafka-storm-cassandra", config, stormTopology);
        }
    }
}
