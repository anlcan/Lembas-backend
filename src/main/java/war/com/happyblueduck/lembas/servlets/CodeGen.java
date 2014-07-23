package com.happyblueduck.lembas.servlets;

import com.happyblueduck.lembas.core.LembasUtil;
import com.happyblueduck.lembas.net.URLFetch;
import com.happyblueduck.lembas.settings.Config;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: anlcan
 * Date: 2/15/13
 * Time: 2:53 PM
 */
public class CodeGen extends HttpServlet {

    public String  CODE = null;

    public static final Logger logger = Logger.getLogger(CodeGen.class.getSimpleName());

    public static final String OBJECTIVE_C  = "objc";
    public static final String DOTNET       = "dotnet";
    public static final String JAVA         = "java";
    public static final String ANDROID      = "android";
    public static final String PHONGAP      = "phonegap";


    public static String PACKAGE_NAME      = "com.happyblueduck.lembas.";

    public static  String HTTP_SYNC_SERVER_APPSPOT_COM     = "http://yoxo.sync-server.appspot.com";
//    public static final String HTTP_SYNC_SERVER_APPSPOT_COM     = "http://localhost:9999";

    private String getPackage() {
        return PACKAGE_NAME + Config.serviceName.toLowerCase();
    }

    public String discoveryContent(String target) throws IOException {
        JSONArray objects = DiscoService.getServiceDefinition();
        JSONObject wrapper = new JSONObject();

        wrapper.put("objects", objects);

        wrapper.put(LembasUtil.typeIdentifier, "MServiceDefinition");
        wrapper.put("target", target);
        wrapper.put("project", Config.serviceName);
        wrapper.put("endPoint", Config.serviceName);
        wrapper.put("host", Config.HOST_URL);
        wrapper.put("port", Config.HOST_PORT);

        wrapper.put("package", getPackage());
        wrapper.put("version", Config.version);
        wrapper.put("projectId", Config.projectId);

        return wrapper.toJSONString();
    }

    public  String codeGenerator(String target)
            throws IOException {

        long start = System.currentTimeMillis();
        String disco = discoveryContent(target);
        logger.info("CODE GENERATED : " + String.valueOf(System.currentTimeMillis() - start));

        URLFetch urlFetch = new URLFetch(HTTP_SYNC_SERVER_APPSPOT_COM);
        String result = urlFetch.fetch("/", null, "POST", disco);
        logger.info("CODE FETCHED : " + String.valueOf(System.currentTimeMillis() - start));


        return result;
    }

    public  void writeDownloadedCode(String target, OutputStream output){

        String containerDictionaryName =  Config.serviceName.toLowerCase();
        if (target.equalsIgnoreCase(JAVA) || target.equalsIgnoreCase(ANDROID)) {
            containerDictionaryName=  "src/"+ PACKAGE_NAME.replaceAll("\\.", "/") +  Config.serviceName.toLowerCase();
        }

        final String packageName =   containerDictionaryName + "/";

        try {

            long start = System.currentTimeMillis();
            CODE = codeGenerator(target);

            //logger.info(CODE);
            //http://stackoverflow.com/questions/357851/in-java-how-to-zip-file-from-byte-array

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(baos);

            if (target.equalsIgnoreCase(DOTNET) || target.equalsIgnoreCase(PHONGAP)){

                byte[] input = CODE.getBytes();
                ZipEntry fileEntry = new ZipEntry( Config.serviceName+".cs");
                fileEntry.setSize(input.length);
                zos.putNextEntry(fileEntry);

                zos.write(input);
                zos.closeEntry();

            } else {

                JSONArray entries = (JSONArray) JSONValue.parse(CODE);
                if ( entries == null){
                    throw new RuntimeException("unable to parse generated code:"+CODE);
                }
                for (Object obj : entries) {
                    JSONObject entry = (JSONObject) obj;

                    String filename = (String) entry.get("fileName");
                    String encodedSource = (String) entry.get("source");
                    if (null == encodedSource){
                        logger.error("missing source for fileName:"+ filename);
                        continue;
                    }

                    //String debug = Base64.base64Decode(encodedSource);
                    //byte[] input = debug.getBytes();
                    //BASE64Decoder decoder = new BASE64Decoder();
                    //byte[] input = decoder.decodeBuffer(encodedSource);
                    byte[] input = Base64.decodeBase64(encodedSource.getBytes());

                    ZipEntry fileEntry = new ZipEntry(packageName + filename);
                    fileEntry.setSize(input.length);
                    zos.putNextEntry(fileEntry);

                    zos.write(input);
                    zos.closeEntry();
                }
            }

            zos.close();
            output.write(baos.toByteArray());

            logger.info("CODE ZIPPED : " + String.valueOf(System.currentTimeMillis() - start));

        } catch (Exception e) {
            logger.trace("WRITE CODE FAILED", e);
            throw new RuntimeException(e);
        }
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        String target = req.getParameter("target");

        String extension  = ".zip";
        String fileName =  Config.serviceName + extension;

        response.setContentType("text/plain");
        response.setHeader("Content-disposition", "attachment; filename="+fileName);


        writeDownloadedCode(target,response.getOutputStream());

    }
}
