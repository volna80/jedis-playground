package com.volna80.sandbox.redis.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class TestData {

    public static final int NUM = 10000;
    public List<Record> data;

    public static TestData generate(){

        Random random = new Random();

        TestData data = new TestData();

        data.data = new ArrayList<Record>();

        for(int i = 0; i < NUM; i++){

            Record rec = new Record();
            rec.instrument = getInstrumentName(i);
            rec.dv01 = random.nextDouble();
            rec.midPrice = random.nextDouble();

            Record leg1 = new Record();
            leg1.instrument = "IRSUSDXXX" + i;
            leg1.dv01 = random.nextDouble();
            leg1.midPrice = random.nextDouble();

            Record leg2 = new Record();
            leg2.instrument = "IRSUSDYYY" + i;
            leg2.dv01 = random.nextDouble();
            leg2.midPrice = random.nextDouble();


            rec.leg1 = leg1;
            rec.leg2 = leg2;

            data.data.add(rec);

        }

        return data;
    }

    public static String getInstrumentName(int i) {
        return "IRSUSD" + i;
    }

}
