package com.gem.hocvalam.storm.integration.tuple.model;

import java.util.UUID;

/**
 * Created by tulh on 05/08/2016.
 */
public class Actor
{
// ------------------------------ FIELDS ------------------------------

    private UUID id;

// --------------------- GETTER / SETTER METHODS ---------------------

    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }
}
