package com.happyblueduck.lembas.core;


import com.google.apphosting.api.ApiProxy;
import com.happyblueduck.lembas.pipeline.LembasActionContext;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: anlcan
 * Date: 9/13/11
 * Time: 4:05 PM
 */
public abstract class LembasRequest extends LembasObject {

    private static Logger logger = Logger.getLogger(LembasResponse.class.getName());

    public static String host;

    // these are different for serialization purpose....
    public static String globalSession = UUID.randomUUID().toString().toUpperCase();
    public static String globalRegisterId;    // same as the device ID.

    // ...only public instance vars are going to be serialized
    public String session;
    public String deviceId;

    private HashMap<String, String> additionalHeaders;

    public String verb;

    public void addHeader(String header, String value) {
        if (additionalHeaders == null)
            additionalHeaders = new HashMap<String, String>();

        additionalHeaders.put(header, value);
    }

    public Logger getLogger() {
        if (logger == null) {
            logger = Logger.getLogger(this.getClass().getName());
            // logger.addHandler(new InnerLogger());
        }
        return logger;
    }

    public void throwProcessingException(String reason) throws RequestProcessException {
        throw new RequestProcessException(this.getClass().getName(), reason);
    }

    public String getHost() {
        return host;
    }

    public abstract LembasResponse process(LembasActionContext context) throws RequestProcessException, UtilSerializeException;
    public LembasResponse run() throws UtilSerializeException {

        logger.info("Sending Lembas Request: "+ this.getClass().getSimpleName());
        // By default, resolve VERB from ClassName
        if (null == verb) {

            String className = this.getClass().getSimpleName();
            this.verb = className.substring(0, className.length() - "Request".length());
        }


        LembasResponse response = null;
        try {

            JSONObject request = LembasUtil.serialize(this);
            JSONObject wrapper = new JSONObject();
            wrapper.put("request", request);

            URL url = new URL(getHost() + verb);
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            connection.setRequestProperty("User-Agent", "google app engine");
            connection.setRequestProperty("X-Appengine-Inbound-Appid", ApiProxy.getCurrentEnvironment().getAppId());

            connection.setConnectTimeout(35 * 1000);


            if (additionalHeaders != null)
                for (String key : additionalHeaders.keySet())
                    connection.setRequestProperty(key, additionalHeaders.get(key));
            connection.setDoOutput(true);

            logger.info("sending ->    " + getHost() + verb);
            logger.info("content:      " + wrapper.toString());
            OutputStreamWriter out = new OutputStreamWriter(
                    connection.getOutputStream());
            out.write(wrapper.toString());
            out.close();

            response = this.readStream(connection.getInputStream());

        } catch (Exception e) {
            logger.warning("Exception while reading running");
            e.printStackTrace();
        }

        return response;
    }

    private LembasResponse readStream(InputStream is) throws UtilSerializeException, IOException, RequestProcessException {

        InputStreamReader isr = new InputStreamReader(is, "UTF8");

        StringBuffer buffer = new StringBuffer();
        Reader in = new BufferedReader(isr);
        int ch;
        while ((ch = in.read()) > -1) {
            buffer.append((char) ch);
        }


        String jsonString = buffer.toString().trim();
        logger.info("received->" + jsonString);
        JSONObject json = (JSONObject) JSONValue.parse(jsonString);
        // check for Error
        JSONObject jsonError = (JSONObject) json.get("Error");
        if (jsonError != null) {
            //LembasFault
            logger.severe("REQUEST FAILED!!!!");
            throw new RequestProcessException("Error", (String) jsonError.get("exceptionName") + ":" + jsonError.get("message"));
        } else {
            JSONObject response = (JSONObject) json.get("Result");
            return (LembasResponse) LembasUtil.deserialize((response));
        }


    }

}
