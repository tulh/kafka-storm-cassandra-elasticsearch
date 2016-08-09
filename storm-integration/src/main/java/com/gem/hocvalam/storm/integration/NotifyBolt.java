package com.gem.hocvalam.storm.integration;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by tulh on 08/08/2016.
 */
public class NotifyBolt extends BaseRichBolt
{
    public static final Logger LOG = LoggerFactory.getLogger(NotifyBolt.class);

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
