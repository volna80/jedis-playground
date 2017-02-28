package com.volna80.sandbox.redis.data;

import com.google.gson.Gson;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class Record {

    public String instrument;
    public double midPrice;
    public double dv01;

    public Record leg1;
    public Record leg2;

}
