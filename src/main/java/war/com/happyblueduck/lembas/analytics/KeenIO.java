package com.happyblueduck.lembas.analytics;

import com.happyblueduck.lembas.net.URLFetch;

import java.util.HashMap;

/**
 * https://keen.io/docs/getting-started-guide/
 */
public class KeenIO{

    //https://api.keen.io/3.0/projects/<PROJECT_ID>/events/<EVENT_COLLECTION>?api_key=<WRITE_KEY>
    // -H "Content-Type: application/json
    public static String KEENIO_HOST    = "https://api.keen.io";
    public static String KEENIO_VERSION = "3.0";

    public static String PROJECT_ID;
    public static String API_KEY;

    public static String getPath(String collection){
        return String.format("/%s/projects/%s/events/%s", KEENIO_VERSION, PROJECT_ID, collection);
    }

    public static String sendEvent(String collection, String jsonContent){

        if (API_KEY == null){
            return "KEEN-IO API KEY IS MISSING. Please setup the api key";
        }

        if (PROJECT_ID == null){
            return "KEEN-IO PROJECT_ID IS MISSING. Please setup the project id";
        }


        URLFetch fetch = new URLFetch(KEENIO_HOST);
        fetch.headers.put("Content-Type", "application/json");

        String path = getPath(collection);
        HashMap<String, String>params = new HashMap<>();
        params.put("api_key", API_KEY);

        String result = fetch.POST(path,params, jsonContent);

        return result;
    }



}
