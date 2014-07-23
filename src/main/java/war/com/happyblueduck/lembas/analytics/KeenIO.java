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

    public static String MASTER_KEY;

    public static String getPath(String collection){
        return String.format("/%s/projects/%s/events/%s", KEENIO_VERSION, PROJECT_ID, collection);
    }

    public static String getQueryPath(String query) {
        return String.format("/%s/projects/%s/queries/%s", KEENIO_VERSION, PROJECT_ID, query);
    }

    private static URLFetch vessel(){
        if (WRITE_API_KEY == null){
            throw new RuntimeException( "KEEN-IO API KEY IS MISSING. Please setup the api key");
        }

        if (PROJECT_ID == null){
            throw new RuntimeException( "KEEN-IO PROJECT_ID IS MISSING. Please setup the project id");
        }


        URLFetch fetch = new URLFetch(KEENIO_HOST);
        fetch.headers.put("Content-Type", "application/json");

        return fetch;
    }

    private static HashMap<String, String> getParams() {
        HashMap<String, String>params = new HashMap<>();
        params.put("api_key", WRITE_API_KEY);
        return params;
    }

    public static String sendEvent(String collection, String jsonContent){

        URLFetch fetch = vessel();

        String path = getPath(collection);
        HashMap<String, String> params = getParams();

        return fetch.POST(path,params, jsonContent);
    }


    public static String deleteEvent(String collection, FilterList filters){

        URLFetch fetch = vessel();
        String path = getPath(collection);
        HashMap<String, String> params = getParams();
        params.put("api_key", MASTER_KEY);
        params.put("filters", filters.json());

        return fetch.DELETE(path, params);
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
        if ( obj.get("error_code") != null){
            throw new RuntimeException((String) obj.get("message"));
        }
        return  obj.get("result");
    }

    public static final class FilterList {

        JSONArray filterList;

        public FilterList(){
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
        public FilterList filterList;

        private URLFetch fetch;

        public Query(String collection) {
            this.collection = collection;
            this.filterList = new FilterList();
        }

        public Double sum(String targetProperty){
            this.query = "sum";
            this.targetProperty = targetProperty;
            String filters =  this.filterList.json();
            Number n = (Number) parseResult(runQuery(collection, query, targetProperty, timeFrame, filters));
            return n.doubleValue();
        }

        public Long count(){
            this.query = "count";
            String filters =  this.filterList.json();
            return (Long) parseResult(runQuery(collection, query, null, timeFrame, filters));
        }

        public Boolean delete(String key, String value){

            this.filterList.addEqualFilter(key, value);
            run();

            return fetch.statusCode == 204;
        }


        public String run() {

            fetch = new URLFetch(KEENIO_HOST);
            fetch.headers.put("Content-Type", "application/json");

            String path = getQueryPath(query);
            HashMap<String, String> params = new HashMap<>();
            params.put("api_key", READ_API_KEY);
            params.put("event_collection", collection);

            params.put("timezone", "7200"); // FIXME

            if (timeFrame != null)
                params.put("timeframe", timeFrame);


            if (targetProperty != null)
                params.put("target_property", targetProperty);

            if (filterList != null) {
                params.put("filters", filterList.json());
            }

            return fetch.GET(path, params);
        }

    }
}
