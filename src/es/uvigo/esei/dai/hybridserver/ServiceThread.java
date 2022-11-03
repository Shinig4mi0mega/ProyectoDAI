package es.uvigo.esei.dai.hybridserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import es.uvigo.esei.dai.hybridserver.http.HTTPParseException;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequestMethod;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

public class ServiceThread implements Runnable {
    private Socket socket;
    pagesDAO dao;

    public ServiceThread(Socket socket, pagesDAO dao) {
        System.out.println("Construyendo service thread");
        this.socket = socket;
        this.dao = dao;
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
            return response;
        }


        //Si no solicita l rsourc html el gt no es valido
        System.out.println("Checking if rquesting html");
        if(!request.getResourceName().equals("html")){
            response.setStatus(HTTPResponseStatus.S400);
            return response;
        }


        //Se pide un html
        System.out.println("Rquesting html");

        //comprobamos si hay uuidd
        System.out.println("searching uuid");
        String uuid = request.getResourceParameters().get("uuid");
        System.out.println("uuid found!");
        System.out.println("uuid -->" + uuid);

        //si no hay, devolvmos lista
        if (uuid == null) {
                System.out.println("building list");
                response.setContent(dao.listPages());
                return response;
            
        }

        System.out.println("uuid not null");
        System.out.println("requested uuid=" + uuid);
        System.out.println("Checking if valid uuid");

        if (!dao.exist(uuid)) {
            System.out.println("uui not valid");
            response.setStatus(HTTPResponseStatus.S404);
            return response;
        }

        System.out.println("Building response");

        response.setContent(dao.get(uuid).getContent());

       System.out.println("response build");
       System.out.println(response.toString());
        return response;

    }


    private HTTPResponse PostHandler(HTTPRequest request) {
        HTTPResponse response = new HTTPResponse();
        System.out.println("Method POST found");
        if(request.ContentLength <0){
            response.setStatus(HTTPResponseStatus.S400);
        }
       
        if(!request.content.contains("html=")){
            response.setStatus(HTTPResponseStatus.S400);
        }
        System.out.println("adding new page");
        String content = request.getContent().split("=")[1];
        String uuid = dao.addPage(content);
        System.out.println(request.getContent());

        String link = buildLink(uuid);
        System.out.println("uuid of new pag -->" + uuid);
        System.out.println("ading uuid to response content");
        response.setContent(link);

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
            return response;
        }
        System.out.println("valid uuid");
        System.out.println("Deleting: " + uuid);
        dao.deletePage(uuid);
        System.out.println("Deleted: " + uuid);

        System.out.println("Building response");
        response.setStatus(HTTPResponseStatus.S200);
        
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
