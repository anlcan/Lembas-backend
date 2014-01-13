package com.happyblueduck.lembas.core;

/**
 * Created by IntelliJ IDEA.
 * User: anlcan
 * Date: 3/29/12
 * Time: 2:55 PM
 */
public class RequestProcessException extends Exception {
    
    public String requestName;
    public String reason;
    
    public RequestProcessException(String requestName, String reason){
        super(reason);
        this.requestName = requestName;
        this.reason = reason;

    }
    
    
}


