package com.happyblueduck.lembas.pipeline;

import com.happyblueduck.lembas.core.UtilSerializeException;
import com.happyblueduck.lembas.processing.LembasActionContext;
import com.happyblueduck.lembas.processing.RequestProcessException;
import com.ideaimpl.patterns.pipeline.PipelineContext;
import com.ideaimpl.patterns.pipeline.Stage;

/**
 * Created by IntelliJ IDEA.
 * User: anlcan
 * Date: 2/12/13
 * Time: 6:02 PM
 */
public class ProcessStage implements Stage {

    public static final String PROCESS_SERIALIZE_EXCEPTION  = "511";
    public static final String PROCESS_EXECUTION_EXCEPTION  = "512";
    public static final String PROCESS__EXCEPTION           = "500";

    @Override
    public void execute(PipelineContext context) {

        LembasActionContext handsomeContext = (LembasActionContext) context;

        try{
            handsomeContext.response = handsomeContext.request.process(handsomeContext);
        } catch ( UtilSerializeException exception){
            handsomeContext.addError(new LembasError(
                    PROCESS_SERIALIZE_EXCEPTION,
                    "Cannot serialize action response", exception));
        } catch (RequestProcessException exception) {
            handsomeContext.addError(new LembasError(
                    PROCESS_EXECUTION_EXCEPTION,
                    "Failed processing action request", exception));
        } catch (Exception exception){
            handsomeContext.addError(new LembasError(
                    PROCESS__EXCEPTION,
                    "Failed executing action request", exception));
        }
    }
}

