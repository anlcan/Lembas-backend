package com.happyblueduck.lembas.pipeline;


import com.happyblueduck.lembas.core.LembasUtil;
import com.happyblueduck.lembas.core.UtilSerializeException;
import com.happyblueduck.lembas.processing.LembasActionContext;
import com.happyblueduck.lembas.processing.LembasProcessRequest;
import com.happyblueduck.lembas.processing.RequestProcessException;
import com.ideaimpl.patterns.pipeline.PipelineContext;
import com.ideaimpl.patterns.pipeline.Stage;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created by IntelliJ IDEA.
 * User: anlcan
 * Date: 2/12/13
 * Time: 5:06 PM
 */
public class ParserStage implements Stage {

    public static final String IO_ERROR_CODE = "504";

    @Override
    public void execute(PipelineContext context) {

        LembasActionContext handsomeContext = (LembasActionContext) context;
        HttpServletRequest req = handsomeContext.servletRequest;

        try {

            InputStreamReader isr = new InputStreamReader(req.getInputStream(), "UTF8");

            StringBuffer buffer = new StringBuffer();
            Reader in = new BufferedReader(isr);
            int ch;
            while ((ch = in.read()) > -1) {
                buffer.append((char) ch);
            }

            handsomeContext.inputString = buffer.toString();

            JSONObject wrappedJsonRequest = (JSONObject) JSONValue.parse(handsomeContext.inputString);
            JSONObject jsonRequest = (JSONObject) wrappedJsonRequest.get("request");
                if ( jsonRequest == null)
                    throw new RequestProcessException("unknown request", "request wrapper not found in :" + wrappedJsonRequest.toJSONString());

            handsomeContext.request = (LembasProcessRequest) LembasUtil.deserialize(jsonRequest);

            if ( handsomeContext.request.verb == null){
                if ( handsomeContext.request.getClass().getSimpleName().endsWith("Request")){
                    String verb = handsomeContext.request.getClass().getSimpleName();
                    handsomeContext.request.verb = verb.substring(0, verb.length() - "Request".length());
                } else {
                    HttpServletRequest httpRequest = handsomeContext.servletRequest;
                    handsomeContext.request.verb= httpRequest.getRequestURI().substring(httpRequest.getRequestURI().lastIndexOf("/") + 1);
                }
            }

        } catch(IOException exception){
            handsomeContext.logger.error(exception.getStackTrace().toString());
            handsomeContext.addError(new LembasError(IO_ERROR_CODE, "Failed to read raw LembasRequest", exception));
        } catch (RequestProcessException e) {
            handsomeContext.addError(new LembasError("512", "Failed create object from input", e));

        } catch (UtilSerializeException e) {
            e.printStackTrace();
            handsomeContext.addError(new LembasError("511", "Cannot serialize action response", e));
        }
    }
}