package com.happyblueduck.lembas.analytics;

import com.happyblueduck.lembas.net.URLFetch;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

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
    public static String WRITE_API_KEY;
    public static String READ_API_KEY;

    public static String getPath(String collection){
        return String.format("/%s/projects/%s/events/%s", KEENIO_VERSION, PROJECT_ID, collection);
    }

    public static String getQueryPath(String query) {
        return String.format("/%s/projects/%s/queries/%s", KEENIO_VERSION, PROJECT_ID, query);
    }

    public static String sendEvent(String collection, String jsonContent){

        if (WRITE_API_KEY == null){
            return "KEEN-IO API KEY IS MISSING. Please setup the api key";
        }

        if (PROJECT_ID == null){
            return "KEEN-IO PROJECT_ID IS MISSING. Please setup the project id";
        }


        URLFetch fetch = new URLFetch(KEENIO_HOST);
        fetch.headers.put("Content-Type", "application/json");

        String path = getPath(collection);
        HashMap<String, String>params = new HashMap<>();
        params.put("api_key", WRITE_API_KEY);

        String result = fetch.POST(path,params, jsonContent);

        return result;
    }


    /**
     * https://api.keen.io/3.0/projects/5295f0c805cd665152000005/queries/
     * sum?api_key=23ff7dea3e851f081e2be4eb60da8bd6cba9002db08befb8b913804cda276d333d5e400e046072757fb233eebe45ace193273afa571a64cb4a5e775f5516604cd60a60620f6d9b13ec9972b44c27883ee24d35c449623aa4e3cec560ee76be310f24afb4bc987cd8fbb9680c0240bbdf&
     * event_collection=payments&timezone=7200&target_property=_amount
     */

    public static String runQuery(String collection, String query, String targetProperty, String timeFrame){
         return runQuery(collection, query, targetProperty, timeFrame, null);
    }
    public static String runQuery(String collection, String query, String targetProperty, String timeFrame, String filters){

        URLFetch fetch = new URLFetch(KEENIO_HOST);
        fetch.headers.put("Content-Type", "application/json");

        String path = getQueryPath(query);
        HashMap<String, String>params = new HashMap<>();
        params.put("api_key", READ_API_KEY);
        params.put("event_collection", collection);

        params.put("timezone", "7200"); // FIXME

        if ( timeFrame !=null)
            params.put("timeframe", timeFrame);


        if (targetProperty != null)
            params.put("target_property", targetProperty);

        if ( filters != null){
             params.put("filters", filters);
        }

        return fetch.GET(path, params);
    }

    private static Object parseResult(String jsonResult) {

        JSONObject obj = (JSONObject) JSONValue.parse(jsonResult);
        return  obj.get("result");
    }

    public static final class Filters{

        JSONArray filterList;

        public Filters(){
             filterList = new JSONArray();
        }

        /*[{"property_name":"amount","operator":"eq","property_value":"5"}] */
        public void addFilter(String propertyName, String operator, String value) {
            JSONObject filter = new JSONObject();
            filter.put("property_name", propertyName);
            filter.put("property_value", value);
            filter.put("operator", operator);
        }

        public void addEqualFilter(String name, String value){
            addFilter(name, "eq", value);
        }

        public int size(){
            return filterList.size();
        }

        public String json(){
            if ( filterList.size() > 0)
                return filterList.toJSONString();
            else
                return null;
        }
    }

    public static final class Query{

        public String collection;
        public String query;
        public String timeFrame;
        public String targetProperty;
        public Filters filters;

        public Query(String collection) {
            this.collection = collection;
            this.filters = new Filters();
        }

        public Double sum(String targetProperty){
            this.query = "sum";
            this.targetProperty = targetProperty;
            String filters =  this.filters.json();
            Number n = (Number) parseResult(runQuery(collection, query, targetProperty, timeFrame, filters));
            return n.doubleValue();
        }

        public Long count(){
            this.query = "count";
            String filters =  this.filters.json();
            return (Long) parseResult(runQuery(collection, query, null, timeFrame, filters));
        }

    }
}
