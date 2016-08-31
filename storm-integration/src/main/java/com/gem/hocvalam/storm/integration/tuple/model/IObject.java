package com.gem.hocvalam.storm.integration.tuple.model;

import java.util.UUID;

/**
 * Created by tulh on 05/08/2016.
 */
public class IObject
{
// ------------------------------ FIELDS ------------------------------

    private String type;
    private UUID id;
    private String content;
    private Long published;

// --------------------- GETTER / SETTER METHODS ---------------------

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }

    public Long getPublished()
    {
        return published;
    }

    public void setPublished(Long published)
    {
        this.published = published;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }
}
