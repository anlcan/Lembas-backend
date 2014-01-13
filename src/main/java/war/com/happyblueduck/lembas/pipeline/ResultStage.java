package com.happyblueduck.lembas.pipeline;

import com.ideaimpl.patterns.pipeline.PipelineContext;
import com.ideaimpl.patterns.pipeline.Stage;
import com.happyblueduck.lembas.commons.LembasFault;
import com.happyblueduck.lembas.core.LembasUtil;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Final stage in the pipeline
*/
public class ResultStage implements Stage{
    @Override
    public void execute(PipelineContext context) {

        LembasActionContext handsomeContext = (LembasActionContext) context;


        HttpServletResponse res = handsomeContext.servletResponse;
        Logger logger = handsomeContext.logger;

        try {

            //response.status = "OK"; .. is now implicit
            JSONObject result = new JSONObject();
            JSONObject response =  LembasUtil.serialize(handsomeContext.response);
            //result.put(handsomeContext.request.verb + "Result", response);
            if ( handsomeContext.response instanceof LembasFault)
                result.put("Error", response);
            else
                result.put("Result", response);

            handsomeContext.outputString = result.toString();


            res.setContentType("application/json");
            res.setCharacterEncoding("UTF-8");
            res.setHeader("Connection", "Keep-Alive");
            res.setStatus(handsomeContext.responseStatusCode);

            if ( !res.isCommitted())
                res.getWriter().println(handsomeContext.outputString);

            logger.info("response sent: "+handsomeContext.outputString);

        } catch (IOException e) {

            logger.error("request failed IOException :\n" + handsomeContext.servletRequest.getRequestURI());
            logger.error("json received: " + handsomeContext.inputString);
            logger.error(e.toString());

        } catch (Exception e) {

            logger.error(String.format("request failed Exception %s:\n%s", e.toString(), e.getMessage()));
            logger.error("json received: " + handsomeContext.inputString);
            e.printStackTrace();
        }


    }
}
