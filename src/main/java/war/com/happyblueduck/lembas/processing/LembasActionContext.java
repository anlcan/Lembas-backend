package com.happyblueduck.lembas.processing;

import com.happyblueduck.lembas.core.LembasResponse;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Created by IntelliJ IDEA.
 * User: anlcan
 * Date: 2/12/13
 * Time: 5:08 PM
 */
public class LembasActionContext  {

    public HttpServletRequest servletRequest = null;
    public HttpServletResponse servletResponse = null;

    public int responseStatusCode = HttpServletResponse.SC_OK;

    public LembasProcessRequest request;
    public LembasResponse response;

    public String inputString;
    public String outputString;


    public Logger logger;

}
