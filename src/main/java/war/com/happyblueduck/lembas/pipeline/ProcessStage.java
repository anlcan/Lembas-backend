package com.happyblueduck.lembas.pipeline;

import com.ideaimpl.patterns.pipeline.PipelineContext;
import com.ideaimpl.patterns.pipeline.Stage;
import com.happyblueduck.lembas.core.RequestProcessException;
import com.happyblueduck.lembas.core.UtilSerializeException;

/**
 * Created by IntelliJ IDEA.
 * User: anlcan
 * Date: 2/12/13
 * Time: 6:02 PM
 */
public class ProcessStage implements Stage {


    @Override
    public void execute(PipelineContext context) {

        LembasActionContext handsomeContext = (LembasActionContext) context;

        try{

            handsomeContext.response = handsomeContext.request.process(handsomeContext);


        } catch ( UtilSerializeException exception){
            exception.printStackTrace();
            handsomeContext.addError(new LembasError("511", "Cannot serialize action response", exception));

        } catch (RequestProcessException exception) {
            exception.printStackTrace();
            handsomeContext.addError(new LembasError("212", "Failed processing action request", exception));
        } catch (Exception exception){
            exception.printStackTrace();
            handsomeContext.addError(new LembasError("500", "Failed executing action request", exception));
        }
    }
}

