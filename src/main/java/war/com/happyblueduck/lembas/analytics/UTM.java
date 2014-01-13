package com.happyblueduck.lembas.analytics;


import com.happyblueduck.lembas.net.URLFetch;
import com.happyblueduck.lembas.settings.Config;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Logger;

/*
 *
 *     <!--***** UTM ******-->
        <servlet>
            <servlet-name>UTM</servlet-name>
            <servlet-class>com.lembas.lembas.analytics.UTM</servlet-class>
        </servlet>
        <servlet-mapping>
            <servlet-name>UTM</servlet-name>
            <url-pattern>/queue/utm</url-pattern>
        </servlet-mapping>
 *
 *
 */
public class UTM extends HttpServlet {

    public static final Logger logger = Logger.getLogger(UTM.class.getSimpleName());

    public static final String UTM_GIF = "http://www.google-analytics.com/__utm.gif";

    /* DEFAULTS */
    public static String CURRENT_ACCOUNT_ID = "";
    public static String CURRENT_VERSION    = "5.3.9";
    public static String CURRENT_LANGUAGE   = "en-us";
    public static String CURRENT_ENCODING   = "ISO-8859-1";
    public static Random REQUEST_RANDOM     = new Random();


    /* PARAMS */
    // https://developers.google.com/analytics/resources/articles/gaTrackingTroubleshooting#gifParameters
    public static final String VERSION      = "utmwv"; // Tracking code version
    public static final String UNIQUE_ID    = "utmn";  // generated at each request
    public static final String HOST_NAME    = "utmhn"; // hostname
    public static final String LANGUAGE     = "utmul"; // en-us
    public static final String ACCOUNT_ID   = "utmac"; //  analytics account
    public static final String ENCODING     = "utmcs";
    public static final String COKIE        = "utmcc"; // cokie params...required
    public static final String REFERRAL     =  "utmr";

    public static final String SCREEN       = "utmsr"; // 2400x1950
    public static final String PAGE_REQUEST = "utmp";  //  	Page request of the current page.
    public static final String PAGE_TITLE   = "utmdt"; // page title
    public static final String REQUEST_ID   = "utms"; // Session requests. Updates every time a __utm.gif request is made. Stops incrementing at 500 (max number of GIF requests per session).



    /*

       utmcc= //cookie settings
    __utma=
                    21661308. //cookie number
                    1850772708. //number under 2147483647
                    1169320752. //time (20-01-2007) cookie first set
                    1172328503. //time (24-02-2007) cookie previous set
                    1172935717. //time (03-03-2007) today
                    3;+
    __utmb=
                    21661308;+ //cookie number
    __utmc=
                    21661308;+ //cookie number
    __utmz=
                    21661308. //cookie number
                    1172936273. //time (03-03-2007) today
                    3.
                    2.
        utmccn=(organic)| //utm_campaign
        utmcsr=google| //utm_source
        utmctr=seo+optimal+keyword+density| //utm_term
        utmcmd=organic;+ //utm_mediu
     */


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String jsonRaw = URLFetch.readStream(req.getInputStream());
        JSONObject params = (JSONObject) JSONValue.parse(jsonRaw);

        URLFetch analytics = new URLFetch(UTM_GIF);
        String result = analytics.fetch("", params, "GET", null);
        logger.info("UTM SENT : " + analytics.urlString);
    }

    public static final class Request {

        private HashMap<String, String> params;

        public Request(String pageName) {
            params = new HashMap<String, String>();

            addParam(ACCOUNT_ID, CURRENT_ACCOUNT_ID);
            addParam(VERSION, CURRENT_VERSION);
            addParam(HOST_NAME, Config.HOST_URL);
            addParam(LANGUAGE, CURRENT_LANGUAGE);
            addParam(UNIQUE_ID, String.valueOf(REQUEST_RANDOM.nextInt(Integer.MAX_VALUE)));
            addParam(REQUEST_ID, "1");
            //addParam(REQUEST_ID, String.valueOf(REQUEST_RANDOM.nextInt(500)));
            addParam(ENCODING, CURRENT_ENCODING);
            // WTF /??
            addParam(COKIE, "__utma%3D79074829.916569474.1361201470.1361201470.1361201470.1%3B%2B__utmz%3D79074829.1361201470.1.1.utmcsr%3Dappengine%7Cutmccn%3D(direct)%7Cutmcmd%3D(none)%3B");
            addParam("utmu", "q~");

            try {
                addParam(PAGE_REQUEST, URLEncoder.encode(pageName, "UTF-8"));
                addParam(HOST_NAME, URLEncoder.encode(Config.HOST_URL, "UTF-8"));

            } catch (UnsupportedEncodingException e) {
                logger.severe("failed to encode URL " + pageName);
            }
        }

        public Request addParam(String param, String value){
            params.put(param, value);
            return this;
        }

        public void setPageTitle(String title){
            try {
                addParam(PAGE_TITLE, URLEncoder.encode(title, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }

        public String run(){
            URLFetch utm = new URLFetch(UTM_GIF);
            utm.headers.put("Content-Type", "text/xml");
            utm.headers.put("Accept", "*");

            return utm.fetch("", params, "GET", null);

        }

    }
}
