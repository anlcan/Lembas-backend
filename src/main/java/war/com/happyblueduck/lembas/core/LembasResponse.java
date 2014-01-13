package com.happyblueduck.lembas.core;

/**
 * Created by IntelliJ IDEA.
 * User: anlcan
 * Date: 9/13/11
 * Time: 4:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class LembasResponse extends LembasObject {

    public String status = "OK";
    public LembasObject info;

    public LembasResponse(LembasObject info){
        this.info = info;
    }

    public LembasResponse() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
