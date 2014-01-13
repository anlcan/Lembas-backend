package com.happyblueduck.lembas.core;

/**
 * Created by IntelliJ IDEA.
 * User: anlcan
 * Date: 3/29/12
 * Time: 2:57 PM
 */
public class UtilSerializeException extends Exception {
    
    String className; 
    String fieldName; 
    String payload;

    public UtilSerializeException(String className, String fieldName, String payload) {
        this.className  = className;
        this.fieldName  = fieldName;
        this.payload    = payload;
    }
}
