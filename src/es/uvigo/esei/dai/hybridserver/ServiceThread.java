package es.uvigo.esei.dai.hybridserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Map;
import java.util.UUID;

import es.uvigo.esei.dai.hybridserver.http.HTTPParseException;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequestMethod;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

public class ServiceThread implements Runnable {
    private Socket socket;
    private Map<String, String> pages;

    public ServiceThread(Socket socket, Map<String, String> pages) {
        System.out.println("Construyendo service thread");
        this.socket = socket;
        this.pages = pages;
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



        System.out.println("Checking if rquesting html");
        if(!request.getResourceName().equals("html")){
            response.setStatus(HTTPResponseStatus.S400);
            return response;
        }
        System.out.println("Rquesting html");

        System.out.println("Requesting uuid");
        String uuid = request.getResourceParameters().get("uuid");
        System.out.println("uuid found!");
        if (uuid == null) {
                System.out.println("building list");
                response.setContent(getUuidsList());
                return response;
            
        }

        System.out.println("uuid not null");
        System.out.println("requested uuid=" + uuid);
        System.out.println("Checking if valid uuid");

        if (!pages.keySet().contains(uuid)) {
            System.out.println("uui not valid");
            response.setStatus(HTTPResponseStatus.S404);
            return response;
        }

        System.out.println("Building response");

        response.setContent(pages.get(uuid));

        System.out.println(response.toString());
        return response;

    }


    private HTTPResponse PostHandler(HTTPRequest request) {
        HTTPResponse response = new HTTPResponse();
        System.out.println("Method POST found");
        if(request.ContentLength <0){
            response.setStatus(HTTPResponseStatus.S400);
        }
        System.out.println("Generating uuid for post");
        UUID randomUuid = UUID.randomUUID();
        String uuid = randomUuid.toString();
        System.out.println("uuid: " + uuid);

        System.out.println("adding new page");
        if(!request.content.contains("html=")){
            response.setStatus(HTTPResponseStatus.S400);
        }
        pages.put(uuid, request.getContent().split("=")[1]);
        System.out.println(request.getContent());

        String link = buildLink(uuid);
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
        if (!pages.keySet().contains(uuid)) {
            System.out.println("uui not valid");
            response.setStatus(HTTPResponseStatus.S404);
            return response;
        }
        System.out.println("valid uuid");
        System.out.println("Deleting: " + uuid);
        pages.remove(uuid);
        System.out.println("Deleted: " + uuid);

        System.out.println("Building response");

        response.setContent(pages.get(uuid));
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

    private String getUuidsList() {
        StringBuilder toret = new StringBuilder();
        for (String k : pages.keySet()) {
            toret.append(k).append("\n");
        }
        System.out.println(toret.toString());
        return toret.toString();
    }
}
