package com.happyblueduck.lembas.listener;


import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by IntelliJ IDEA.
 * User: anlcan
 * Date: 3/22/12
 * Time: 4:11 PM
 */
public class LembasContextListener implements ServletContextListener {

    /*
    public static final Logger logger = Logger.getLogger(LembasContextListener.class.getName());
    static {
        logger.addHandler(new InnerLogger());
    }

    */
    public void contextInitialized(ServletContextEvent servletContextEvent) {

//        ApiProxy.Environment env = ApiProxy.getCurrentEnvironment();
//        String hostName = (String) env.getAttributes().get("com.google.appengine.runtime.default_version_hostname");
//        String appId = env.getAppId();
//        String msg = hostName + " "+ appId + "."+ env.getVersionId() +" is starting up...";
//
//        Logger.getAnonymousLogger().info(String.format("%s %s %s" ,hostName, appId, msg));
    
    }
    

    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
