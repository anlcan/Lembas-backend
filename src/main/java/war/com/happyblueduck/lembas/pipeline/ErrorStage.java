package com.happyblueduck.lembas.pipeline;

import com.happyblueduck.lembas.commons.LembasFault;
import com.happyblueduck.lembas.processing.LembasActionContext;
import com.ideaimpl.patterns.pipeline.PipelineContext;
import com.ideaimpl.patterns.pipeline.Stage;

/**
 * Created by IntelliJ IDEA.
 * User: anlcan
 * Date: 2/12/13
 * Time: 6:10 PM
 */
public class ErrorStage implements Stage {

    public static int NUMBER_OF_STACK_TRACE = 5;
    public static boolean SEND_STACKTRACE = true;

    @Override
    public void execute(PipelineContext context) {

        LembasActionContext handsomeContext = (LembasActionContext) context;

        // only the first error needs our attention
        LembasError error = (LembasError) handsomeContext.getErrors().get(0);
        handsomeContext.responseStatusCode = Integer.parseInt(error.getErrorCode());
        // convert error to Handsome's LembasFault
        LembasFault fault = new LembasFault();

        if ( SEND_STACKTRACE){
            String trace  = "";
            int i = 0;
            for ( StackTraceElement element : error.getRelatedException().getStackTrace()){
                trace += element.toString() +"|";
                if ( i++ == NUMBER_OF_STACK_TRACE)
                    break;
            }
            fault.stacktrace = trace;
        }
        fault.exceptionName     = error.getClass().getSimpleName();
        fault.message           = error.getErrorDescription();
        fault.visibleMessage    = error.getRelatedException().getLocalizedMessage();

        // this way, we ensure result stage returns a valid LembasResponse
        handsomeContext.response = fault;
    }
}
