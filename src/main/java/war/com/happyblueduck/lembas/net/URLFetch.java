package com.happyblueduck.lembas.net;

//import com.google.appengine.api.urlfetch.*;
//import com.google.common.base.Joiner;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;


/**
 * Created by IntelliJ IDEA.
 * User: anlcan
 * Date: 9/26/12
 * Time: 5:30 PM
 */
public class URLFetch {

    private static final Logger logger = Logger.getLogger(URLFetch.class.getName());

    public String baseUrl;
    public String urlString;
    public HashMap<String, String> params;
    public String basicAuthentication;
    public HashMap<String, String> headers;
    public int statusCode;

    public  String encoding = "UTF-8";
    public static final int CONNECTION_TIMEOUT = 45 * 1000;

    public URLFetch() {
        this.headers = new HashMap<String, String>();

    }

    public URLFetch(String url) {
        this.baseUrl = url;
        this.headers = new HashMap<String, String>();
    }

    public static String serializeParams(HashMap<String, String> params){
        // FIXME srsly?
        String queryParam = "";

        if (params != null) {
            for (String key : params.keySet()) {
                String value = params.get(key);
                queryParam = queryParam.concat(key+"="+value+"&");
            }
        }

        return  queryParam;
    }


    public String POST(String path, HashMap<String, String> params, String data){
        return fetch(path, params, "POST", data);
    }

    public String GET(String path, HashMap<String, String> params){
        return fetch(path, params, "GET", null);
    }

    public String DELETE(String path){
        return fetch(path, null, "DELETE", null);
    }
    public String DELETE(String path,  HashMap<String, String>  params){
        return fetch(path, params, "DELETE", null);
    }

    public String PUT(String path, String data) {
        return fetch(path, null, "PUT", data);
    }



    public String fetch(String path, HashMap<String, String> params, String method, String data)  {

        String query = serializeParams(params);
        if (params != null && params.size() > 0)
            urlString = baseUrl+path + "?" + query;
        else
            urlString = baseUrl+path;

        try {
            return execute(urlString, method, data);

        } catch (IOException e) {
            e.printStackTrace();
            logger.error("URLFetch failed: " + urlString);
            logger.error(e.getLocalizedMessage());
        }

        return null;
    }

    public String execute(String urlString, String method,String data) throws IOException {
        return readStream(getURL(urlString, method, data), encoding);
    }

    public InputStream getURL(String urlString, String method, String data){

        long start = System.currentTimeMillis();

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //connection.setRequestProperty("Content-Type", "text/xml");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setRequestMethod(method);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);  // in milisecounds

            for (String name : headers.keySet())
                connection.setRequestProperty(name, headers.get(name));

            connection.addRequestProperty("Cache-Control", "no-cache,max-age=0");
            connection.addRequestProperty("Pragma", "no-cache");

            if ( basicAuthentication != null){
                connection.setRequestProperty ("Authorization", basicAuthentication);
            }

            if (data != null) {

                connection.setDoOutput(true);
                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write(data);
                writer.close();
            }

            long end =  System.currentTimeMillis();
            long duration   = end - start;
            this.statusCode = connection.getResponseCode();

            logger.info(String.format("%s finished with %d in %d", url.toString(), statusCode,duration));

            if (statusCode == HttpURLConnection.HTTP_OK) {
                // OK
                return connection.getInputStream();
            } else {
                // Server returned HTTP error code.
                return connection.getInputStream();
            }

        } catch (MalformedURLException e) {
            logger.error(e.toString());
        } catch (Exception e) {
            logger.error(e.toString());
        }

        return null;
    }

    public static String readStream(InputStream is) throws IOException {
        return readStream(is, "UTF-8");
    }


    public static String readStream ( InputStream is, String encoding) throws IOException {

        InputStreamReader isr = new InputStreamReader(is,encoding);

        StringBuffer buffer = new StringBuffer();
        Reader in = new BufferedReader(isr);
        int ch;
        while ((ch = in.read()) > -1) {
            buffer.append((char) ch);
        }

        return new String(buffer.toString().getBytes(), encoding);
    }
}
