/*
created by LOXO-DONTA
on 2013-01-29 15:04:29.791430
using /base/data/home/apps/s~sync-server/loxo.364902525792969423/generate.pyc
for makina.nmdapps.com
*/

package com.happyblueduck.lembas.commons;

import com.happyblueduck.lembas.core.LembasResponse;


public class LembasFault extends LembasResponse {

    public String stacktrace;
	public String message;
	public String exceptionName;
	public String visibleMessage;


    //default constructor
    public LembasFault(){
        this.status = "LembasFault";
    }

    public LembasFault(String message, String visibleMessage) {
        this.status = "LembasFault";
        this.message = message;
        this.visibleMessage = visibleMessage;
    }

    public LembasFault(String s) {
        this.status = "LembasFault";
        this.message = s;
    }


    public void setStacktrace (String  _stacktrace) {
		this.stacktrace = _stacktrace;
	}

	public String getStacktrace () {
		return  this.stacktrace;
	}

	public void setMessage (String  _message) {
		this.message = _message;
	}

	public String getMessage () {
		return  this.message;
	}

	public void setExceptionName (String  _exceptionName) {
		this.exceptionName = _exceptionName;
	}

	public String getExceptionName () {
		return  this.exceptionName;
	}

	public void setVisibleMessage (String  _visibleMessage) {
		this.visibleMessage = _visibleMessage;
	}

	public String getVisibleMessage () {
		return  this.visibleMessage;
	}


}

