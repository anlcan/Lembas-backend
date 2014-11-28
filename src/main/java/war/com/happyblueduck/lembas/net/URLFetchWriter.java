package com.happyblueduck.lembas.net;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * User: anlcan
 * Date: 27/11/14
 * Time: 17:48
 */
public interface URLFetchWriter {

    public void write(HttpURLConnection connection) throws IOException;
}
