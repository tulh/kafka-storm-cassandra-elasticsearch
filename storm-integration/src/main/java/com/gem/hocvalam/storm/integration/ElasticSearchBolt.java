package com.gem.hocvalam.storm.integration;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.storm.shade.org.json.simple.JSONObject;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by tulh on 22/08/2016.
 */
class ElasticSearchBolt extends BaseRichBolt
{
// ------------------------------ FIELDS ------------------------------

    private static final Logger LOG = LoggerFactory.getLogger(BaseRichBolt.class);
    private OutputCollector collector;

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface IBolt ---------------------

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector)
    {
        this.collector = outputCollector;
    }

    @Override
    public void execute(Tuple tuple)
    {
        String postId = tuple.getStringByField("post_id");
        HttpPost request = new HttpPost("http://172.16.10.132:9200/hocvalam-social/post/" + postId);
        request.addHeader("Content-Type", "application/json;charset=UTF-8");

        try
        {
            StringEntity entity = new StringEntity("{\n" +
                    "    \"post_id\": \"" + tuple.getStringByField("post_id") + "\",\n" +
                    "    \"author_id\": \"" + tuple.getStringByField("author_id") + "\",\n" +
                    "    \"created_date\": \"" + tuple.getStringByField("published_time") + "\",\n" +
                    "    \"content\": \"" + JSONObject.escape(tuple.getStringByField("content")) + "\"\n" +
                    "}", "UTF-8");
            request.setEntity(entity);
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
            collector.ack(tuple);
            LOG.info("Post to ElasticSearch: " + EntityUtils.toString(entity, "UTF-8"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

// --------------------- Interface IComponent ---------------------


    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer)
    {

    }
}
