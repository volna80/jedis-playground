package com.volna80.sandbox.redis;

import com.google.gson.Gson;
import com.volna80.sandbox.redis.data.Record;
import com.volna80.sandbox.redis.data.TestData;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.concurrent.TimeUnit;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class Main {

    private static JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");


    public void run() throws InterruptedException {


        Jedis jedis = null;
        try {
            jedis = pool.getResource();




            int i = 0;

            while(i < 100){

                i++;

                final TestData data = TestData.generate();

                final long start = System.currentTimeMillis();

                //case 1: just set without a pipeline

                case1(data, jedis);


                final long finish = System.currentTimeMillis();


                System.out.println("iteration: " + i + ", time: " + (finish - start) + " ms. ");

                Thread.sleep(1000);
            }

        } finally {
            if(jedis != null){
                jedis.close();
            }
        }

    }

    private void case1(TestData data, Jedis jedis) {

        Gson gson = new Gson();

        for(Record rec : data.data){
            jedis.set("C1:" + rec.instrument, gson.toJson(rec));
        }

    }


    public static void main(String[] args) throws InterruptedException {

        Main main = new Main();

        main.run();

        main.shutdown();

    }

    private void shutdown() {
        pool.destroy();
    }
}
