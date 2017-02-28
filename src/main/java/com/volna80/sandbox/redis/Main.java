package com.volna80.sandbox.redis;

import com.google.gson.Gson;
import com.volna80.sandbox.redis.data.Record;
import com.volna80.sandbox.redis.data.TestData;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;

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


            StringBuilder result = new StringBuilder();


            //warm up
            long total = testRun(jedis);
            result.append("Run 1: avg ").append(total / 100).append("ms.\n");
            //run 1
            total = testRun(jedis);
            result.append("Run 2: avg ").append(total / 100).append("ms.\n");

            //run 2
            total = testRun(jedis);
            result.append("Run 3: avg ").append(total / 100).append("ms.\n");

            System.out.println(result);

        } finally {
            if(jedis != null){
                jedis.close();
            }
        }

    }

    private long testRun(Jedis jedis) throws InterruptedException {
        int i = 0;
        long total = 0;
        while(i < 100){

            i++;

            final TestData data = TestData.generate();

            final long start = System.currentTimeMillis();

            //case 1: just set without a pipeline

//            case1(data, jedis);
//            case2(data, jedis);
//            case3(data, jedis);
            case4(data, jedis);


            final long finish = System.currentTimeMillis();
            final long time = finish - start;
            total += time;

            System.out.println("iteration: " + i + ", time: " + time + " ms. ");

            Thread.sleep(100);
        }
        return total;
    }

    private void case1(TestData data, Jedis jedis) {

        Gson gson = new Gson();

        for(Record rec : data.data){
            jedis.set("C1:" + rec.instrument, gson.toJson(rec));
        }

    }

    private void case2(TestData data, Jedis jedis) {

        Gson gson = new Gson();

        Pipeline pipelined = jedis.pipelined();

        for(Record rec : data.data){
            pipelined.set("C2:" + rec.instrument, gson.toJson(rec));
        }

    }

    private void case3(TestData data, Jedis jedis) {

        Gson gson = new Gson();

        jedis.set("C3", gson.toJson(data));

    }

    private void case4(TestData data, Jedis jedis) {

        Pipeline pipelined = jedis.pipelined();

        for(Record rec : data.data){
            final String key = "C4:" + rec.instrument;
            pipelined.hset(key, "instrument", rec.instrument );
            pipelined.hset(key, "mid", String.valueOf(rec.midPrice));
            pipelined.hset(key, "dv01", String.valueOf(rec.dv01));
            pipelined.hset(key, "leg1", rec.leg1.instrument);
            pipelined.hset(key, "leg1:mid", String.valueOf(rec.leg1.midPrice));
            pipelined.hset(key, "leg1:dv01", String.valueOf(rec.leg1.dv01));
            pipelined.hset(key, "leg2", rec.leg2.instrument);
            pipelined.hset(key, "leg2:mid", String.valueOf(rec.leg2.midPrice));
            pipelined.hset(key, "leg2:dv01", String.valueOf(rec.leg2.dv01));
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
