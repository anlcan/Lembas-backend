package com.happyblueduck.lembas.servlets;

import com.happyblueduck.lembas.core.LembasObject;
import com.happyblueduck.lembas.core.LembasUtil;
import com.happyblueduck.lembas.settings.Config;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: anlcan
 * Date: 3/1/12
 * Time: 5:35 PM
 */


/**
 * Discovery service for HANDSOME framework
 * <p/>
 * responds to main server and client HANDSOME
 */
public abstract class DiscoService extends HttpServlet {

    public static final Logger logger = Logger.getLogger(DiscoService.class.getSimpleName());


    /* */
    @Override
    public void init() throws ServletException {
        super.init();
    }

    private static Class getObjectClass(String className) {

        List<String> objectPackages = Config.getEndPointPackages();
        for (String packageName : objectPackages) {

            Class clazz = LembasUtil.searchClass(packageName, className);
            if (clazz != null)
                return clazz;
        }

        return null;
    }

    public static LembasObject getEndPoint(String endPointName) {

        Class clazz = null;
        for (String packageName : Config.getEndPointPackages()) {
            clazz = LembasUtil.searchClass(packageName, endPointName);
            if (clazz != null)
                break;
        }

        if (clazz != null) {

            try {
                LembasObject result = (LembasObject) clazz.newInstance();
                result.init();

                return result;

            } catch (InstantiationException e) {
                e.printStackTrace();
                return null;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return null;
            }

        } else {

            logger.warning("failed to find classs for " +
                    endPointName);

            return null;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        /*
         responds HANDSOME requests
         */

        //MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
        /*
        if (cache.get("disco") != null) {
            logger.info("serving from the cache");
            resp.getWriter().println(cache.get("disco"));
            return;
        }
        */

        // path must be xxxx.appspot.com/{ServiceName}/ServiceDisco
        //String[] path = req.getRequestURI().split("/");
        //ArrayList<String>paths = (ArrayList<String>) Arrays.asList(path);
        //this.serviceName = paths.get(paths.size()-2);

        JSONObject wrapper = new JSONObject();

        JSONArray objects = getServiceDefinition();
        wrapper.put(LembasUtil.typeIdentifier, "MServiceDefinition");
        wrapper.put("objects", objects);

        //cache.put("disco", wrapper.toString());
        logger.info(wrapper.toString());
        resp.setHeader("Content-Type", "application/json;charset=utf-8");
        resp.getWriter().println(wrapper.toString());
    }

    public static JSONArray getServiceDefinition() throws IOException {

        JSONArray objects = new JSONArray();

        ArrayList<String> objectList = new ArrayList<String>();

        for (String packageName : Config.getEndPointPackages()) {
            logger.info("generating endPoints from " + packageName);
            ArrayList<String> classes = LembasUtil.getClassNamesFromPackage(packageName);
            if (classes != null && classes.size() > 0)
                objectList.addAll(classes);
        }


        objects.addAll(generateDisco(objectList));
        return objects;
    }

    /**
     * @param objectList
     * @return
     */
    public static ArrayList<JSONObject> generateDisco(ArrayList<String> objectList) {

        ArrayList<JSONObject> objects = new ArrayList<JSONObject>();
        for (String objectName : objectList) {

            //do not post enums as objects
            Class clazz = getObjectClass(objectName);
            if (clazz == null) continue;
            if (clazz.isEnum()) continue;
            if (objectName.contains("$"))continue;  //skip innerclasses

            if (Modifier.isAbstract(clazz.getModifiers())) continue; // skip abstract

            LembasObject bo = null;
            try {

                bo = (LembasObject) clazz.newInstance();
                objects.add(bo.discoDescription());

            } catch (InstantiationException e) {
                logger.severe("failed generating " + objectName);
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                logger.severe("failed generating " + objectName);
                e.printStackTrace();
            }  catch (Exception e){
                logger.severe("failed generating "+objectName);
                logger.severe(e.getLocalizedMessage());
                e.printStackTrace();
                throw e;
            }

        }

        return objects;
    }
}
