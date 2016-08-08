package com.gem.hocvalam.storm.integration;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by tulh on 05/08/2016.
 */
public class UserActivity
{
    private UUID userId;
    private String interactionDate;
    private Long interactionTime;
    private UUID activityId;
    private String activityType;
    private String data;

    public UserActivity()
    {
    }

    public UserActivity(Post post)
    {
        this.userId = post.getActor().getId();
        long timePublished = post.getObject().getPublished() * 1000;
        Date date = new Date(timePublished);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        this.interactionDate = dateFormat.format(date);
        this.interactionTime = timePublished;
        this.activityId = post.getObject().getId();
        this.activityType = post.getObject().getType();
        this.data = post.getObject().getContent();
    }

    public UUID getUserId()
    {
        return userId;
    }

    public void setUserId(UUID userId)
    {
        this.userId = userId;
    }

    public String getInteractionDate()
    {
        return interactionDate;
    }

    public void setInteractionDate(String interactionDate)
    {
        this.interactionDate = interactionDate;
    }

    public Long getInteractionTime()
    {
        return interactionTime;
    }

    public void setInteractionTime(Long interactionTime)
    {
        this.interactionTime = interactionTime;
    }

    public UUID getActivityId()
    {
        return activityId;
    }

    public void setActivityId(UUID activityId)
    {
        this.activityId = activityId;
    }

    public String getActivityType()
    {
        return activityType;
    }

    public void setActivityType(String activityType)
    {
        this.activityType = activityType;
    }

    public String getData()
    {
        return data;
    }

    public void setData(String data)
    {
        this.data = data;
    }
}
