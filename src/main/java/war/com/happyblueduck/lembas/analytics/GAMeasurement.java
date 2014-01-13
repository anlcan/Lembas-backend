package com.happyblueduck.lembas.analytics;

import com.happyblueduck.lembas.net.URLFetch;

import java.util.HashMap;
import java.util.Random;

/**
 * User: anlcan
 * Date: 10/30/13
 * Time: 1:50 PM
 */

/*
https://developers.google.com/analytics/devguides/collection/protocol/v1/devguide
*/
public class GAMeasurement extends URLFetch {

    public static final String HOST = "/collect";
    public static final String METHOD = "POST";
    public static final String VERSION = "1";

    public static String globalTrackingId;

    public HashMap<String, String> params;

    public GAMeasurement() {
        baseUrl = "http://www.google-analytics.com";
        //baseUrl = "https://ssl.google-analytics.com";
        this.params = new HashMap<String, String> ();
        this.params.put("v", VERSION);
        this.params.put("cu","TRY");
        this.params.put("ci", "555");
        if ( globalTrackingId != null){
            setTrackingId(globalTrackingId);
        }

        this.headers.put("Content-Type", "text/xml");
        //this.headers.put("Accept", "*");
    }

    public void setTrackingId(String trackingId){
        this.params.put("tid", trackingId);
    }

    /**
    &t=event        // Event hit type
    &ec=video       // Event Category. Required.
    &ea=play        // Event Action. Required.
    &el=holiday     // Event label.
    &ev=300         // Event value.
     */
    public static GAMeasurement event(String type, String category, String action, String label, String value){

        GAMeasurement m = new GAMeasurement();

        m.params.put("t", type);
        m.params.put("ec", category);
        m.params.put("ea", action);
        m.params.put("el", label);
        m.params.put("ev", value);

        return m;
    }


    /**
     &t=transaction   // Transaction hit type.
     &ti=12345        // transaction ID. Required.
     &ta=westernWear  // Transaction affiliation.
     &tr=50.00        // Transaction revenue.
     &ts=32.00        // Transaction shipping.
     &tt=12.00        // Transaction tax.
     &cu=EUR          // Currency code.
     */

    public static GAMeasurement transaction(String id, String affiliation, String revenue){
        GAMeasurement m = new GAMeasurement();

        m.params.put("t", "transaction");
        m.params.put("ti", id);
        m.params.put("ta", affiliation);
        m.params.put("tr", revenue);

        return m;
    }

    public static GAMeasurement transaction(String id, String affiliation, String revenue, String shipping, String tax){

        GAMeasurement m = new GAMeasurement();

        m.params.put("t", "transaction");
        m.params.put("ti", id);
        m.params.put("ta", affiliation);
        m.params.put("tr", revenue);
        m.params.put("ts", shipping);
        m.params.put("tt", tax);

        return m;
    }


    public String run(){
        params.put("z", String.valueOf(new Random().nextInt()));
        String data = URLFetch.serializeParams(params);
        //return fetch(HOST, null, METHOD, data);
        //return GET(HOST, params);
        return null;
    }
}
