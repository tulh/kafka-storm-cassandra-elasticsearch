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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tulh on 05/08/2016.
 */
public class SampleTest
{
    public static void post() throws UnsupportedEncodingException
    {
        HttpPost request = new HttpPost("http://172.16.10.140:8080/send-push");
        request.addHeader("Content-Type", "application/x-www-form-urlencoded");

        // Request parameters and other properties.
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("name", "tulh"));
        params.add(new BasicNameValuePair("message", "test"));
        params.add(new BasicNameValuePair("channel_ids", "[123, 456]"));
        request.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

        HttpClientBuilder bld = HttpClientBuilder.create();
        HttpClient client = bld.build();
        try
        {
            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            System.out.println(EntityUtils.toString(entity, "UTF-8"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) throws UnsupportedEncodingException
    {
//        post();
        final Pattern pattern = Pattern.compile("(?:\\s|\\A)[@]+([A-Za-z0-9-_]+)");
        final Matcher matcher = pattern.matcher("new son test 123 @tulh @abc @f316527a-b34e-4329-91ce-1935977959a5 @51ab2e23-dfee-4cc6-8ea1-6e23fa7a4fa1");
        while (matcher.find())
        {
            System.out.println(matcher.group(1));
        }
    }


}
