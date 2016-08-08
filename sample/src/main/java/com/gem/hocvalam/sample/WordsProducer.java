package com.gem.hocvalam.sample;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;
import java.util.UUID;

/**
 * Created by tulh on 03/08/2016.
 */


public class WordsProducer
{
    public static void main(String[] args) throws Exception
    {

        //Assign topicName to string variable
        String topicName = "hocvalam-post";

        // create instance for properties to access producer configs
        Properties props = new Properties();

        //Assign localhost id
        props.put("bootstrap.servers", "172.16.10.132:9092");

        //Set acknowledgements for producer requests.
        props.put("acks", "all");

        //If the request fails, the producer can automatically retry,
        props.put("retries", 0);

        //Specify buffer size in config
        props.put("batch.size", 16384);

        //Reduce the no of requests less than 0
        props.put("linger.ms", 1);

        //The buffer.memory controls the total amount of memory available to the producer for buffering.
        props.put("buffer.memory", 33554432);

        props.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");

        props.put("value.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<>(props);

        //for (int i = 0; i < 10; i++)
        while (true)
        {
            producer.send(new ProducerRecord<>(topicName,
                    "User ID ", UUID.randomUUID().toString()));
        }
//        System.out.println("Message sent successfully");
//        producer.close();
    }
}
