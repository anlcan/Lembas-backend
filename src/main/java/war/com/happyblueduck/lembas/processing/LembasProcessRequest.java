package com.happyblueduck.lembas.processing;

import com.happyblueduck.lembas.core.LembasRequest;
import com.happyblueduck.lembas.core.LembasResponse;
import com.happyblueduck.lembas.core.UtilSerializeException;

/**
 * User: anlcan
 * Date: 1/13/14
 * Time: 4:10 PM
 */
public abstract class LembasProcessRequest extends LembasRequest{
    public abstract LembasResponse process(LembasActionContext context) throws RequestProcessException, UtilSerializeException;

    public void throwProcessingException(String reason) throws RequestProcessException {
        throw new RequestProcessException(this.getClass().getName(), reason);
    }
}
