package com.happyblueduck.lembas.servlets;

import com.happyblueduck.lembas.core.LembasUtil;
import com.happyblueduck.lembas.net.URLFetch;
import com.happyblueduck.lembas.settings.Config;
import org.apache.commons.codec.binary.Base64;
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
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: anlcan
 * Date: 2/15/13
 * Time: 2:53 PM
 */
public class CodeGen extends HttpServlet {

    public static final Logger logger = Logger.getLogger(CodeGen.class.getSimpleName());

    public static final String OBJECTIVE_C  = "objc";
    public static final String DOTNET       = "dotnet";
    public static final String JAVA         = "java";
    public static final String ANDROID      = "android";
    public static final String PHONGAP      = "phonegap";


    public static final String HTTP_SYNC_SERVER_APPSPOT_COM     = "http://yoxo.sync-server.appspot.com";
//    public static final String HTTP_SYNC_SERVER_APPSPOT_COM     = "http://localhost:8888";

    public  String codeGenerator(String target)
            throws IOException {

        JSONObject wrapper = new JSONObject();

        //JSONArray objects = service.getServiceDefinition();
        JSONArray objects = DiscoService.getServiceDefinition();
        wrapper.put(LembasUtil.typeIdentifier, "MServiceDefinition");
        wrapper.put("target", target);
        wrapper.put("project", Config.serviceName);
        wrapper.put("endPoint",  Config.serviceName);
        wrapper.put("host", Config.HOST_URL);
        wrapper.put("port", Config.HOST_PORT);
        wrapper.put("objects", objects);
        wrapper.put("package", "com.happyblueduck.lembas."+  Config.serviceName.toLowerCase());

        /* dummy params */
        wrapper.put("version", "1.0");
        wrapper.put("projectId", "123");


        URLFetch urlFetch = new URLFetch(HTTP_SYNC_SERVER_APPSPOT_COM);
        String result = urlFetch.fetch("/", null, "POST", wrapper.toJSONString());

        logger.info(wrapper.toJSONString());
        return result;
    }

//    public  StreamingOutput downloadCode(final String target, final int port){
//
//        StreamingOutput str = new StreamingOutput() {
//            @Override
//            public void write(OutputStream outputStream) throws IOException, WebApplicationException {
//                writeDownloadedCode(target, port, outputStream);
//            }
//        };
//
//        return str;
//    }


    public  void writeDownloadedCode(String target, OutputStream output){


        String containerDictionaryName =  Config.serviceName.toLowerCase();
        if (target.equalsIgnoreCase(JAVA) || target.equalsIgnoreCase(ANDROID)) {
            containerDictionaryName=  "src/com/lembas/lembas/" +  Config.serviceName.toLowerCase();
        }

        final String packageName =   containerDictionaryName + "/";


        try {
            String code = codeGenerator(target);
            logger.info(code);
            //http://stackoverflow.com/questions/357851/in-java-how-to-zip-file-from-byte-array

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(baos);

            if (target.equalsIgnoreCase(DOTNET) || target.equalsIgnoreCase(PHONGAP)){

                byte[] input = code.getBytes();
                ZipEntry fileEntry = new ZipEntry( Config.serviceName+".cs");
                fileEntry.setSize(input.length);
                zos.putNextEntry(fileEntry);

                zos.write(input);
                zos.closeEntry();

            } else {

                JSONArray entries = (JSONArray) JSONValue.parse(code);
                if ( entries == null){
                    throw new RuntimeException("unable to parse generated code:"+code);
                }
                for (Object obj : entries) {
                    JSONObject entry = (JSONObject) obj;

                    String filename = (String) entry.get("fileName");
                    String encodedSource = (String) entry.get("source");
                    if (null == encodedSource){
                        logger.severe("missing source for fileName:"+ filename);
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


        } catch (Exception e) {
            e.printStackTrace();
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
