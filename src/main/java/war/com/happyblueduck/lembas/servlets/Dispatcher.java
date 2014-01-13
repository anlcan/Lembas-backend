package com.happyblueduck.lembas.servlets;

import com.google.common.collect.Lists;
import com.happyblueduck.lembas.pipeline.ErrorStage;
import com.happyblueduck.lembas.pipeline.ParserStage;
import com.happyblueduck.lembas.pipeline.ProcessStage;
import com.happyblueduck.lembas.pipeline.ResultStage;
import com.happyblueduck.lembas.processing.LembasActionContext;
import com.ideaimpl.patterns.pipeline.Pipeline;
import com.ideaimpl.patterns.pipeline.SequentialPipeline;
import com.ideaimpl.patterns.pipeline.Stage;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: anlcan
 * Date: 2/13/12
 * Time: 3:54 PM
 */


public abstract class Dispatcher extends HttpServlet {

    public static final Logger logger = Logger.getLogger(Dispatcher.class.getSimpleName());

    public static  final Pipeline HANDSOME_PIPELINE = new SequentialPipeline();
    protected LembasActionContext context;


    @Override
    public void init() throws ServletException {
        super.init();

        for ( Stage  s : processingStages()){
            HANDSOME_PIPELINE.addStage(s);
        }
        /* */
        HANDSOME_PIPELINE.addFinalStage(finalStage());
        /* */
        HANDSOME_PIPELINE.addErrorStage(errorStage());
    }


    /**
     * SUBCLASSES MAY OVERRIDE THESE METHODS AND RETURN THEIR OWN PROCESSING STAGES
     * @return ArrayList of stages to be executed sequentially
     */
    public ArrayList<? extends Stage> processingStages(){
       return Lists.newArrayList(new ParserStage(), new ProcessStage());
    }

    public LembasActionContext actionContext(){
        return new LembasActionContext();
    }

    public Logger actionLogger(){
        return logger;
    }

    public Stage finalStage(){
        return new ResultStage();
    }

    public Stage errorStage(){
        return new ErrorStage();
    }

    /**
     * @param req is nice
     * @param res is good
     * @throws IOException
     */
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {

        context = actionContext();

        context.servletRequest  = req;
        context.servletResponse = res;
        context.logger = actionLogger();

        HANDSOME_PIPELINE.execute(context);

    }

}
