package com.happyblueduck.lembas.pipeline;

import com.ideaimpl.patterns.pipeline.PipelineContextAdaptor;
import com.happyblueduck.lembas.core.LembasRequest;
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
public class LembasActionContext extends PipelineContextAdaptor{

    public HttpServletRequest  servletRequest;
    public HttpServletResponse servletResponse;

    public LembasRequest request;
    public LembasResponse response;

    public String inputString;
    public String outputString;

    public int responseStatusCode = HttpServletResponse.SC_OK;

    public Logger logger;

}
