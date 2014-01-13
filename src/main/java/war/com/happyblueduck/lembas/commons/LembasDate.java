package com.happyblueduck.lembas.commons;

import com.happyblueduck.lembas.core.LembasObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: anlcan
 * Date: 1/31/13
 * Time: 5:17 PM
 */
public class LembasDate extends LembasObject {

    private final static SimpleDateFormat clientFormat      = new SimpleDateFormat("dd-MM-yyyy HH:mm:ssZ");
    private final static SimpleDateFormat serverFormat      = new SimpleDateFormat("HH:mm:ss-dd/MM/yyyy-Z");

    public String time;
    public String date;
    public String zone;

    public LembasDate() {
    }

    public LembasDate(String s){
        try {
            Date d =  clientFormat.parse(s);
            update(d);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public LembasDate(Date d){
        update(d);
    }

    private void update(Date d){
        String[] parts = serverFormat.format(d).split("-");

        this.time = parts[0];
        this.date = parts[1];
        this.zone = parts[2];

    }
}
