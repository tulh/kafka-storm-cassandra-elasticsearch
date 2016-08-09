package com.gem.hocvalam.storm.integration.tuple.model;

import java.util.UUID;

/**
 * Created by tulh on 05/08/2016.
 */
public class IObject
{
    private String type;
    private UUID id;
    private String content;
    private Long published;

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public Long getPublished()
    {
        return published;
    }

    public void setPublished(Long published)
    {
        this.published = published;
    }
}
