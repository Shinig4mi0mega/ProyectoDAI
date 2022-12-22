package es.uvigo.esei.dai.hybridserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Properties;

import org.apache.http.client.fluent.Request;

import es.uvigo.esei.dai.hybridserver.dao.htmlDAO;
import es.uvigo.esei.dai.hybridserver.dao.pagesDAO;
import es.uvigo.esei.dai.hybridserver.dao.xmlDAO;
import es.uvigo.esei.dai.hybridserver.http.HTTPParseException;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequestMethod;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;
import es.uvigo.esei.dai.hybridserver.http.MIME;

public class ServiceThread implements Runnable {
    private Socket socket;
    private pagesDAO dao;
    private xmlDAO xmlDAO;


    public ServiceThread(Socket socket, Properties properties) {
        System.out.println("Construyendo service thread");
        this.socket = socket;
        this.dao = new htmlDAO(properties);
        this.xmlDAO = new xmlDAO(properties);
    }

    @Override
    public void run() {
        System.out.println("Thread runing");
        try (Socket socket = this.socket) {
            System.out.println("inside try");
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            BufferedWriter output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            HTTPRequest request = null;

            try {
                request = new HTTPRequest(input);
            } catch (HTTPParseException e) {
                System.out.println("Error parse HTTP");
            }

            

            System.out.println(request.toString());
            System.out.println("Checking method");

            HTTPRequestMethod method = request.getMethod();

            HTTPResponse response = methodHandler(request, method);

            System.out.println("Sending response");
            System.out.println(response.toString());
            
            response.print(output);

        } catch (Exception e) {
        }

    }

    private HTTPResponse methodHandler(HTTPRequest request, HTTPRequestMethod method) {
        System.out.println("Method handler called");
        System.out.println();

        if (method == HTTPRequestMethod.HEAD)
            System.out.println("");

        if (method == HTTPRequestMethod.GET)
            return getHandler(request);

        if (method == HTTPRequestMethod.POST)
            return PostHandler(request);

        if (method == HTTPRequestMethod.PUT)
            System.out.println("");

        if (method == HTTPRequestMethod.DELETE)
            return deleteHandler(request);

        if (method == HTTPRequestMethod.TRACE)
            System.out.println("");

        if (method == HTTPRequestMethod.OPTIONS)
            System.out.println("");

        if (method == HTTPRequestMethod.CONNECT)
            System.out.println("");

        return null;

    }

    private HTTPResponse getHandler(HTTPRequest request) {
        HTTPResponse response = new HTTPResponse();

        System.out.println("Method GET found");
        String Resource = (request).getResourceName();
        System.out.println("Requesting " + Resource);


        if(request.getResourceChain().equals("/")){
            response.setStatus(HTTPResponseStatus.S200);
            response.setContent("Hybrid Server");
            response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
            return response;
        }


        //Si no solicita el resource html o los de XML el get no es valido
        System.out.println("Checking if rquesting html");
        if(request.getResourceName().equals("xml")){
            return XmlGetHandler(request,response);
        }

        if(request.getResourceName().equals("html"))
            return HtmlGetHandler(request,response);
        

        //Si el recurso no existe se devuelve un response de 400
        response.setStatus(HTTPResponseStatus.S400);
        response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
        return response;
        

    }

    private HTTPResponse XmlGetHandler(HTTPRequest request, HTTPResponse response) {
        //Se pide un html
        System.out.println("Rquesting xml");

        //comprobamos si hay uuid
        System.out.println("searching uuid");
        String uuid = request.getResourceParameters().get("uuid");
        System.out.println("uuid found!");
        System.out.println("uuid -->" + uuid);

        //si no hay, devolvmos lista
        if (uuid == null) {
                System.out.println("building list");
                response.setContent(dao.listPages());
                response.setStatus(HTTPResponseStatus.S200);
                response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
                return response;
            
        }

        System.out.println("uuid not null");
        System.out.println("requested uuid=" + uuid);
        System.out.println("Checking if valid uuid");

        if (!dao.exist(uuid)) {
            System.out.println("uui not valid");
            response.setStatus(HTTPResponseStatus.S404);
            response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
            return response;
        }

        System.out.println("Building response");

        response.setContent(dao.get(uuid).getContent());
        response.setStatus(HTTPResponseStatus.S200);

       System.out.println("response build");
       response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
       System.out.println(response.toString());
        return response;
    }

    private HTTPResponse HtmlGetHandler(HTTPRequest request, HTTPResponse response){
        //Se pide un html
        System.out.println("Rquesting html");

        //comprobamos si hay uuid
        System.out.println("searching uuid");
        String uuid = request.getResourceParameters().get("uuid");
        System.out.println("uuid found!");
        System.out.println("uuid -->" + uuid);

        //si no hay, devolvmos lista
        if (uuid == null) {
                System.out.println("building list");
                response.setContent(dao.listPages());
                response.setStatus(HTTPResponseStatus.S200);
                response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
                return response;
            
        }

        System.out.println("uuid not null");
        System.out.println("requested uuid=" + uuid);
        System.out.println("Checking if valid uuid");

        if (!dao.exist(uuid)) {
            System.out.println("uui not valid");
            response.setStatus(HTTPResponseStatus.S404);
            response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
            return response;
        }

        System.out.println("Building response");

        response.setContent(dao.get(uuid).getContent());
        response.setStatus(HTTPResponseStatus.S200);

       System.out.println("response build");
       response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
       System.out.println(response.toString());
        return response;
    }

    private HTTPResponse PostHandler(HTTPRequest request) {
        HTTPResponse response = new HTTPResponse();
        System.out.println("Method POST found");
        if(request.ContentLength <0){
            response.setStatus(HTTPResponseStatus.S400);
            response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
            return response;
        }
       
        if(!request.content.contains("html=")){
            response.setStatus(HTTPResponseStatus.S400);
            response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
            return response;
        }
        System.out.println("adding new page");
        String content = request.getContent().split("=")[1];
        String uuid = dao.addPage(content);
        System.out.println(request.getContent());

        String link = buildLink(uuid);
        System.out.println("uuid of new pag -->" + uuid);
        System.out.println("ading uuid to response content");
        response.setContent(link);
        response.setStatus(HTTPResponseStatus.S200);
        response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());

        return response;
    }

    private HTTPResponse deleteHandler(HTTPRequest request) {
        System.out.println("Method DELETE found");
        HTTPResponse response = new HTTPResponse();
        String Resource = (request).getResourceName();
        System.out.println("Requesting " + Resource);



        System.out.println("Checking if rquesting html");
        if(!request.getResourceName().equals("html")){
            response.setStatus(HTTPResponseStatus.S400);
            response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
            return response;
        }
        System.out.println("Rquesting html");

        System.out.println("Requesting uuid");
        String uuid = request.getResourceParameters().get("uuid");
        System.out.println("uuid found!");

        System.out.println("uuid not null");
        System.out.println("requested uuid=" + uuid);
        System.out.println("Checking if valid uuid");
        if (!dao.exist(uuid)) {
            System.out.println("uui not valid");
            response.setStatus(HTTPResponseStatus.S404);
            response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
            response.setContent("<h1>dfsf</h1>");
            return response;
        }
        System.out.println("valid uuid");
        System.out.println("Deleting: " + uuid);
        dao.deletePage(uuid);
        System.out.println("Deleted: " + uuid);

        System.out.println("Building response");
        response.setStatus(HTTPResponseStatus.S200);
        response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
        
        return response;
    }

    private String buildLink(String uuid) {
        // "<a href=\"html?uuid=" + uuid + "\">" + uuid + "</a>";
        System.out.println("Building link");
        StringBuilder toret = new StringBuilder();
        toret.append("<a href=\"html?uuid=");
        toret.append(uuid);
        toret.append("\">");
        toret.append(uuid);
        toret.append("</a>");

        return toret.toString();
    }

}
