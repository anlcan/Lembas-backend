package com.happyblueduck;

import com.happyblueduck.lembas.net.URLFetch;
import com.happyblueduck.lembas.net.URLFetchWriter;
import org.junit.Test;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

import static org.junit.Assert.assertEquals;

/**
 * User: anlcan
 * Date: 27/11/14
 * Time: 17:56
 */
public class UrlTest {

    //http://www.mocky.io/v2/5185415ba171ea3a00704eed
    public static final String mockyIO = "http://www.mocky.io/v2/";

    public URLFetch  mock = new URLFetch(mockyIO);

    @Test
    public void test(){
        String result = mock.GET("5185415ba171ea3a00704eed", null);
        System.out.println(result);
        assertEquals("{\"hello\": \"world\"}", result);
    }

    @Test
    public void testPOST(){
        String result = mock.POST("5185415ba171ea3a00704eed", null, "this is nice");
        System.out.println(result);
        assertEquals("{\"hello\": \"world\"}", result);
    }

    @Test
    public void testWriter() {


        mock.urlFetchWriter = new URLFetchWriter() {
            @Override
            public void write(HttpURLConnection connection) throws IOException {
                connection.setDoOutput(true);
                OutputStreamWriter writer2 = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
                writer2.write("ÖdeAl".toCharArray());
                writer2.close();
            }
        };

        String result = mock.GET("5185415ba171ea3a00704eed", null);
        System.out.println(result);
    }

}
