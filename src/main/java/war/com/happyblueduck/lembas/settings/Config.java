package com.happyblueduck.lembas.settings;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: anlcan
 * Date: 3/28/12
 * Time: 3:18 PM
 */
public  class Config {

    public static final Logger logger = Logger.getLogger("Config");

    public static String serviceName;
    public static String HOST_URL;
    public static String HOST_PORT;
    public static Boolean isDevelopment;

    private final static Set<String> endPointPackages = new HashSet<String>();;

    // endpoint -> incoming
    public static List<String> getEndPointPackages() {
        return Lists.newArrayList(endPointPackages);
    }

    public static void addEndPoint(String packageName){
        endPointPackages.add(packageName);
    }

    public static void removeEndPoint(String packageName) {
        endPointPackages.remove(packageName);
    }

    public static List<String> getPackages() {

        ArrayList<String> packages = new ArrayList<String>();
        packages.addAll(getEndPointPackages());

        return packages;
    }


}
