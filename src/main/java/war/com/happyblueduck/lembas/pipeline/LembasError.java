package com.happyblueduck.lembas.pipeline;

import com.ideaimpl.patterns.pipeline.BaseError;

/**
 * Created by IntelliJ IDEA.
 * User: anlcan
 * Date: 2/12/13
 * Time: 6:41 PM
 */
public class LembasError extends BaseError {
    public LembasError(String errorCode, String errorDescription, Exception relatedException) {
        super(errorCode, errorDescription, relatedException);
    }

    public LembasError(String errorCode, String errorDescription) {
        super(errorCode, errorDescription, new RuntimeException(errorDescription));
    }

}
