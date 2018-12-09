package com.kaustav;


import java.util.*;

import org.apache.ignite.stream.StreamTransformer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.ignite.stream.kafka.connect.serialization.CacheEventDeserializer;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteDataStreamer;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.affinity.AffinityUuid;
import org.apache.ignite.configuration.CacheConfiguration;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import java.util.*;

public class kafkaIgniteStreamer {
    private static final String CACHE_NAME = "mlCAche009";


    public static void main(String[] args) throws Exception {



        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9777");
        props.put("group.id", "test2");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList("test11"));



        Ignition.setClientMode(true);

        Ignite ignite = Ignition.start("/home/system/IGNITE/kafka-ignite-streamer/config/example-ignite.xml");
            //if (!ExamplesUtils.hasServerNodes(ignite))
            //return;

            // The cache is configured with sliding window holding 1 second of the streaming data.
            //IgniteCache<AffinityUuid, String> stmCache = ignite.getOrCreateCache(CacheConfig.wordCache());

        CacheConfiguration<Long, String> cfg = new CacheConfiguration<>(CACHE_NAME);

            // Index key and value.
        cfg.setIndexedTypes(Long.class, String.class);

            // Auto-close cache at the end of the example.
         IgniteCache<Long, String> stmCache = ignite.getOrCreateCache(cfg);


        // Get the data streamer reference and stream data.
        try (IgniteDataStreamer<Long, String> stmr1 = ignite.dataStreamer(stmCache.getName()))



        {
            stmr1.allowOverwrite(true);

            consumer.poll(0);
// Now there is heartbeat and consumer is "alive"
            consumer.seekToBeginning(consumer.assignment());
            // Stream entries.
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(100);
                //Map<String, String> entries = new HashMap<>();
                for (ConsumerRecord<String, String> record : records) {

                    stmCache.put(Long.parseLong(record.key()),(String)record.value());
                    //stmr1.addData((String)record.key(),(String)record.value());
                    //record.key();
                    System.out.printf("partition=%d ,offset = %d, key = %s, value = %s", record.partition() ,record.offset(), record.key(), record.value());
                    System.out.println("\n");
                    String a=stmCache.get(Long.parseLong(record.key()));

                    //System.out.printf(a);

                }
            }
        }







    }
}
