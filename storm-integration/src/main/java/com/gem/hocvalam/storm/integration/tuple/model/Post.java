package com.gem.hocvalam.storm.integration.tuple.model;

import com.datastax.driver.core.utils.UUIDs;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by tulh on 05/08/2016.
 */
public class Post
{
    private String type;
    private Actor actor;
    private IObject object;

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public Actor getActor()
    {
        return actor;
    }

    public void setActor(Actor actor)
    {
        this.actor = actor;
    }

    public IObject getObject()
    {
        return object;
    }

    public void setObject(IObject object)
    {
        this.object = object;
    }

    public static void main(String args[])
    {
        String s = "{\"type\":\"Create\",\"actor\":{\"id\":\"f316527a-b34e-4329-91ce-1935977959a5\"},\"object\":{\"type\":\"Article\",\"id\":\"f7d390a2-998c-47ef-9c45-b201db73e4be\",\"content\":\"nội dung có dấu\",\"published\":1470379535}}";
        Post post = new Gson().fromJson(s, Post.class);
        Date date = new Date(post.getObject().getPublished() * 1000);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println(dateFormat.format(date));
        System.out.println(UUIDs.startOf(post.getObject().getPublished() * 1000).toString());
        System.out.println(date.toString());
//        System.out.println(post);
    }
}
