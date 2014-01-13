package com.happyblueduck.lembas.core;


import com.happyblueduck.lembas.commons.LembasDate;
import com.happyblueduck.lembas.settings.Config;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: anlcan
 * Date: 9/14/11
 * Time: 2:42 PM
 */
public class LembasUtil {

    private static final Logger log = Logger.getLogger(LembasUtil.class.getName());

    // each serialized JSON object have its class name on typeIdentifier key.
    public static final String typeIdentifier   = "_type";
    public static final String objectKey        = "objectKey";

    public static final HashMap<URL, HashSet<String>> jarMap = new HashMap<>();

    public static void parseJar(URL packageURL) throws IOException {
        HashSet<String> entries = new HashSet<>();
        String jarFileName;
        JarFile jf;
        Enumeration<JarEntry> jarEntries;
        String entryName;

        // build jar file name, then loop through zipped entries
        jarFileName = URLDecoder.decode(packageURL.getFile(), "UTF-8");
        jarFileName = jarFileName.substring(5, jarFileName.indexOf("!"));
        log.info("found jar:" + jarFileName);
        jf = new JarFile(jarFileName);
        jarEntries = jf.entries();
        while (jarEntries.hasMoreElements()) {
            entryName = jarEntries.nextElement().getName();
            if (entryName.endsWith(".class"))
                entries.add(entryName);
        }

        jarMap.put(packageURL, entries);
    }

    public static ArrayList<String> getClassNamesFromPackage(String packageName) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL packageURL = null;
        ArrayList<String> names = new ArrayList<String>();

        log.info("getting classes for in "+packageName);
        if (classLoader == null) {

            log.info("failed to obtain classLoader");
            return null;
        }

        packageName = packageName.replace(".", System.getProperty("file.separator"));
        packageURL = classLoader.getResource(packageName);

        if (packageURL == null) {

            log.info("failed to obtain packageURL: " + packageName);
            return null;
        }

        log.info("found package url:"+packageURL.toString());

        if (packageURL.getProtocol().equals("jar")) {
            if (jarMap.get(packageURL) == null)
                parseJar(packageURL);

            HashSet<String> jarEntries = jarMap.get(packageURL);

            for(String entryName : jarEntries) {
                // adding dot the differentiate com.dodo.thing & com.dodo.thing2
                if (entryName.startsWith(packageName + "/") && entryName.length() > packageName.length() + 5) { //5 =  class.length
                    log.info(packageName + " adding " + entryName);
                    entryName = entryName.substring(packageName.length()+1, entryName.lastIndexOf('.')); //dot of the .class
                    if ( entryName.lastIndexOf("/") == -1)
                        names.add(entryName);
                    else
                        log.warning("found mismatched class "+entryName);
                }
            }

            // loop through files in classpath
        } else {
            File folder = new File(packageURL.getFile());
            File[] contenuti = folder.listFiles();
            String entryName;
            for (File actual : contenuti) {
                entryName = actual.getName();
                if (entryName.endsWith("class")) {
                    entryName = entryName.substring(0, entryName.lastIndexOf('.'));
                    names.add(entryName);
                } // TODO dealing with packages goes here
            }
        }
        return names;
    }

    public static Field[] getAllFields(Class<?> clazz) {
        List<Class<?>> classes = getAllSuperclasses(clazz);
        classes.add(clazz);
        return getAllFields(classes);
    }

    /**
     * As {@link #getAllFields(Class)} but acts on a list of {@link Class}s and
     * uses only {@link Class#getDeclaredFields()}.
     *
     * @param classes The list of classes to reflect on
     * @return The complete list of fields
     */
    private static Field[] getAllFields(List<Class<?>> classes) {
        Set<Field> fields = new HashSet<Field>();
        for (Class<?> clazz : classes) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        }

        return fields.toArray(new Field[fields.size()]);
    }

    /**
     * Return a List of super-classes for the given class.
     *
     * @param clazz the class to look up
     * @return the List of super-classes in order going up from this one
     */
    public static List<Class<?>> getAllSuperclasses(Class<?> clazz) {
        List<Class<?>> classes = new ArrayList<Class<?>>();

        Class<?> superclass = clazz.getSuperclass();
        while (superclass != null) {
            classes.add(superclass);
            superclass = superclass.getSuperclass();
        }

        return classes;
    }

    private static Object parseValue(Object f) throws UtilSerializeException {

        Class type = f.getClass();


        if (type == String.class
                || type == Number.class
                || type == Integer.class
                || Long.class == type
                || Double.class == type
                || Boolean.class == type) {

            return f;

        } else if (type.isEnum()) {

            Enum e = (Enum) f;
            return e.ordinal();

        } else if (type == ArrayList.class) {

            ArrayList<Object> result = new ArrayList<Object>();
            ArrayList input = (ArrayList) f;

            for (Object inner : input) {
                Object o = parseValue(inner);
                result.add(o);
            }

            return result;
        } else if (type == Date.class){
             LembasDate date =  new LembasDate((Date) f);
            return serialize(date);
        }
        else {

            return serialize(f);
        }
    }


    public static JSONObject serialize(Object o) throws UtilSerializeException {
        return serialize(o, true);
    }

    public static JSONObject serialize(Object o, boolean skipUnderScore) throws UtilSerializeException {

        JSONObject result = new JSONObject();

        String className = o.getClass().getSimpleName();
        result.put(typeIdentifier, className);

        for (Field f : getAllFields(o.getClass())) {

            String key = f.getName();

            int modifiers = f.getModifiers();
            if (Modifier.isPrivate(modifiers)) continue;
            if (Modifier.isProtected(modifiers)) continue;
            if (Modifier.isStatic(modifiers)) continue;
            if (Modifier.isTransient(modifiers)) continue;
            if (Modifier.isFinal(modifiers)) continue;
            if (Modifier.isVolatile(modifiers)) continue;

            if (skipUnderScore && f.getName().startsWith("_")) continue;

//            log.info("parsing "+ className + " " + key);
            try {
                //log.info("parsing "+ className +"."+ key);
                Object fieldValue = f.get(o);
                if (fieldValue == null) continue;

                Object value = LembasUtil.parseValue(fieldValue);
                result.put(key, value);

            } catch (IllegalAccessException iae) {

                iae.printStackTrace();
                log.warning(iae.getMessage());
                throw new UtilSerializeException(className, f.toString(), "");
            }

        }

        return result;
    }

    // DESERIALIZATION
    private static Object evaluateValue(Object f) throws UtilSerializeException {

        Class type = f.getClass();
        if (type == JSONObject.class) {
            return deserialize((JSONObject) f);
        }

        if (type == JSONArray.class) {

            ArrayList<Object> result = new ArrayList<Object>();
            ArrayList input = (ArrayList) f;

            for (Object inner : input) {
                result.add(evaluateValue(inner));
            }

            return result;

        }

//        if (type == Long.class || type == String.class || type == Number.class || type == Integer.class || Double.class == type || Boolean.class == type) {
//            return f;
//            // TODO hacked for long
//
//        }  else  else

//        log.info("failed to evaluate:" + type.getName() + " " + f);

        return f;
    }


    public static Class searchClass(String packageName, String className) {
        try {
            return Class.forName(packageName + "." + className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private static Class getaClass(String className) throws ClassNotFoundException {
        Class c = null;
        for (String packageName : Config.getPackages()) {
            c = searchClass(packageName, className);
            if (c != null)
                break;
        }
        if (c == null) {
            throw new ClassNotFoundException(className);
        }
        return c;
    }

    public static Object deserialize(Map map) throws UtilSerializeException {

        String className = (String) map.get(typeIdentifier);
        map.remove(typeIdentifier);
        Class c = null;
        Field currentField = null;
        Object value = null;

        try {

            c = getaClass(className);

            //Object n = c.newInstance();
            Object n = c.getConstructor().newInstance();

            //for (Object o : map.keySet()) {
                //String key = (String) o;
                //f = c.getField(key);
            for ( Field f : c.getFields()){
                String key = f.getName();
                currentField = f;

                Object jsonValue = map.get(key);
                if (jsonValue == null) continue;

                value = evaluateValue(jsonValue);

                if (value != null) {
                    // enum exception
                    if (f.getType().isEnum()) {

                        Class z = f.getType();
                        Object[] cons = z.getEnumConstants();

                        int intValue = -1;
                        if ( value instanceof Long)
                            intValue = ((Long)value).intValue();

                        for (int i = 0; i < cons.length; i++) {
                            if (i == intValue) {
                                f.set(n, Enum.valueOf((Class<Enum>) f.getType(), cons[i].toString()));
                            }
                        }
                    } else {

                        //f.set(n, value);
                        ((LembasObject)n).setField(f, value);

                    }
                }
            }

            return n;


        } catch (ClassNotFoundException e) {
            log.severe("class not found for " + map);
            //e.printStackTrace();
            throw new UtilSerializeException("", "", map.toString());
        } catch (InstantiationException e) {
            log.severe("instantiation exception for " + c);
            log.severe(map.toString());
            throw new UtilSerializeException(c.toString(), "", map.toString());

        } catch (IllegalAccessException e) {
            log.severe("illegal acces for class" + c);
            log.severe("illegal acces for field" + e.toString());
            log.severe(map.toString());
            throw new UtilSerializeException(c.toString(), currentField.toString(), map.toString());

        } /*catch (NoSuchFieldException e) {
            log.info("no such field exception for " + c);
            log.info("no such field exception for " + e.toString());
            log.info(map.toString());
            throw new UtilSerializeException(c.toString(), f.toString(), map.toString());
        }  */ catch (NoSuchMethodException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            throw new UtilSerializeException(c.toString(), "", map.toString());
        } catch (InvocationTargetException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            throw new UtilSerializeException(c.toString(), "", map.toString());
        }

    }


}
