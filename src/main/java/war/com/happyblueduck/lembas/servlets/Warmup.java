package com.happyblueduck.lembas.servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: anlcan
 * Date: 2/20/13
 * Time: 3:21 PM
 */
public class Warmup extends HttpServlet {

    public static final Logger logger = Logger.getLogger(Warmup.class.getSimpleName());


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        logger.info("WARMUP");
    }


}
