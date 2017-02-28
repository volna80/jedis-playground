package com.volna80.sandbox.redis;

import com.google.gson.Gson;
import com.volna80.sandbox.redis.data.Record;
import com.volna80.sandbox.redis.data.TestData;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;

import java.util.ArrayList;
import java.util.List;

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


            TestData data = TestData.generate();

            final long start = System.currentTimeMillis();


            {
//            case1(data, jedis);
//            case2(data, jedis);
//            case3(data, jedis);
//            case4(data, jedis);
                case4_1(data, jedis);
            }

            {
//                data = case5read(jedis);
//                data = case6read(jedis);
//                data = case7read(jedis);
//                data = case8read(jedis);
            }


            final long finish = System.currentTimeMillis();
            final long time = finish - start;
            total += time;

            System.out.println("iteration: " + i + ", time: " + time + " ms. Num of instruments: " + data.data.size());

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

    private void case4_1(TestData data, Jedis jedis) {

        Gson gson = new Gson();

        Pipeline pipelined = jedis.pipelined();

        final String key = "C4_1";

        for(Record rec : data.data){
            pipelined.hset(key, rec.instrument, gson.toJson(rec));
        }

    }

    private TestData case5read(Jedis jedis) {

        Gson gson = new Gson();

        TestData data = new TestData();
        data.data = new ArrayList<Record>(TestData.NUM);

        for(int i = 0; i < TestData.NUM; i++){

            String value = jedis.get("C1:" + TestData.getInstrumentName(i));
            data.data.add(gson.fromJson(value, Record.class));
        }

        return data;

    }

    private TestData case6read(Jedis jedis) {

        Gson gson = new Gson();

        TestData data = gson.fromJson(jedis.get("C3"), TestData.class);

        return data;

    }

    private TestData case7read(Jedis jedis) {

        Gson gson = new Gson();

        TestData data = new TestData();
        data.data = new ArrayList<Record>(TestData.NUM);

        for(int i = 0; i < TestData.NUM; i++){

            final String key = "C4:" + TestData.getInstrumentName(i);

            Record rec = new Record();
            rec.instrument = jedis.hget(key, "instrument");
            rec.dv01 = Double.parseDouble(jedis.hget(key, "mid"));
            rec.midPrice = Double.parseDouble(jedis.hget(key, "dv01"));

            Record leg1 = new Record();
            leg1.instrument = jedis.hget(key, "leg1");
            leg1.dv01 = Double.parseDouble(jedis.hget(key, "leg1:mid"));
            leg1.midPrice = Double.parseDouble(jedis.hget(key, "leg1:dv01"));

            Record leg2 = new Record();
            leg2.instrument = jedis.hget(key, "leg2");
            leg2.dv01 = Double.parseDouble(jedis.hget(key, "leg2:mid"));
            leg2.midPrice = Double.parseDouble(jedis.hget(key, "leg2:dv01"));


            rec.leg1 = leg1;
            rec.leg2 = leg2;

            data.data.add(rec);
        }

        return data;

    }

    private TestData case8read(Jedis jedis) {

        Gson gson = new Gson();

        TestData data = new TestData();
        data.data = new ArrayList<Record>(TestData.NUM);

        String[] keys = new String[TestData.NUM];


        for(int i = 0; i < TestData.NUM; i++){
            keys[i]= "C1:" + TestData.getInstrumentName(i);
        }

        List<String> values = jedis.mget(keys);

        for(String val : values){
            data.data.add(gson.fromJson(val, Record.class));
        }

        return data;

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
