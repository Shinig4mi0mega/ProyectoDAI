package es.uvigo.esei.dai.hybridserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.Socket;
import java.util.Properties;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import es.uvigo.esei.dai.hybridserver.dao.htmlDAO;
import es.uvigo.esei.dai.hybridserver.dao.page;
import es.uvigo.esei.dai.hybridserver.dao.xmlDAO;
import es.uvigo.esei.dai.hybridserver.dao.xsdDAO;
import es.uvigo.esei.dai.hybridserver.dao.xsltDAO;
import es.uvigo.esei.dai.hybridserver.http.HTTPParseException;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequestMethod;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;
import es.uvigo.esei.dai.hybridserver.http.MIME;

public class ServiceThread implements Runnable {
    private Socket socket;
    private htmlDAO htmldao;
    private xmlDAO xmlDAO;
    private xsdDAO xsdDAO;
    private xsltDAO xsltDAO;

    public ServiceThread(Socket socket, Properties properties) {

        this.socket = socket;
        this.htmldao = new htmlDAO(properties);
        this.xmlDAO = new xmlDAO(properties);
        this.xsdDAO = new xsdDAO(properties);
        this.xsltDAO = new xsltDAO(properties);
    }

    @Override
    public void run() {

        try (Socket socket = this.socket) {

            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            BufferedWriter output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            HTTPRequest request = null;

            try {
                request = new HTTPRequest(input);
            } catch (HTTPParseException e) {

            }

            System.out.println(request.toString());

            HTTPRequestMethod method = request.getMethod();

            HTTPResponse response = methodHandler(request, method);

            // System.out.println(response.toString());

            response.print(output);

        } catch (Exception e) {
        }

    }

    private HTTPResponse methodHandler(HTTPRequest request, HTTPRequestMethod method) {

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

    // GET HANDLE -----------------------------

    private HTTPResponse getHandler(HTTPRequest request) {
        HTTPResponse response = new HTTPResponse();

        String Resource = (request).getResourceName();

        if (request.getResourceChain().equals("/")) {
            response.setStatus(HTTPResponseStatus.S200);
            response.setContent("Hybrid Server");
            response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
            return response;
        }

        // Si no solicita el resource html o los de XML el get no es valido

        if (request.getResourceName().equals("xml")) {
            return XmlGetHandler(request, response);
        }

        if (request.getResourceName().equals("html"))
            return HtmlGetHandler(request, response);

        if (request.getResourceName().equals("xsd"))
            return XsdGetHandler(request, response);

        if (request.getResourceName().equals("xslt"))
            return XsltGetHandler(request, response);

        // Si el recurso no existe se devuelve un response de 400
        response.setStatus(HTTPResponseStatus.S400);
        response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
        return response;

    }

    private HTTPResponse XsltGetHandler(HTTPRequest request, HTTPResponse response) {
        // Se pide un xslt

        // comprobamos si hay uuid

        String uuid = request.getResourceParameters().get("uuid");

        // si no hay, devolvmos lista
        if (uuid == null) {

            response.setContent(xsltDAO.listPages());
            response.setStatus(HTTPResponseStatus.S200);
            response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
            return response;

        }

        if (!xsltDAO.exist(uuid)) {

            response.setStatus(HTTPResponseStatus.S404);
            response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
            return response;
        }

        response.setContent(xsltDAO.get(uuid).getContent());
        response.setStatus(HTTPResponseStatus.S200);

        response.putParameter("Content-Type", MIME.APPLICATION_XML.getMime());

        return response;
    }

    private HTTPResponse XsdGetHandler(HTTPRequest request, HTTPResponse response) {
        // Se pide un xsd

        // comprobamos si hay uuid

        String uuid = request.getResourceParameters().get("uuid");

        // si no hay, devolvmos lista
        if (uuid == null) {

            response.setContent(xsdDAO.listPages());
            response.setStatus(HTTPResponseStatus.S200);
            response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
            return response;

        }

        if (!xsdDAO.exist(uuid)) {

            response.setStatus(HTTPResponseStatus.S404);
            response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
            return response;
        }

        response.setContent(xsdDAO.get(uuid).getContent());
        response.setStatus(HTTPResponseStatus.S200);

        response.putParameter("Content-Type", MIME.APPLICATION_XML.getMime());

        return response;
    }

    private HTTPResponse XmlGetHandler(HTTPRequest request, HTTPResponse response) {
        // Se pide un xml

        // comprobamos si hay uuid

        String uuid = request.getResourceParameters().get("uuid");

        // si no hay, devolvmos lista
        if (uuid == null) {

            response.setContent(xmlDAO.listPages());
            response.setStatus(HTTPResponseStatus.S200);
            response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
            return response;

        }

        if (!xmlDAO.exist(uuid)) {
            response.setStatus(HTTPResponseStatus.S404);
            response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
            return response;
        }

        if (request.getResourceParameters().keySet().contains("xslt")) {
            String content = "";
            String xml = xmlDAO.get(uuid).getContent();

            // VERIFICACIONES DEL XSLT Y XSD--------------------------------
            if (!(xsltDAO.exist(request.getResourceParameters().get("xslt")))) {
                response.setStatus(HTTPResponseStatus.S404);
                response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
                return response;
            }

            String xsdUuid = xsltDAO.getXsdId(request.getResourceParameters().get("xslt")).getContent();
            String xsd = xsdDAO.get(xsdUuid).getContent();

            if (!validateXML(xml, xsd)) {
                response.setStatus(HTTPResponseStatus.S400);
                response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
                return response;
            }

            // VERIFICACIONES DEL XSLT Y XSD-----------------------------------

            String xsltContent = xsltDAO.get(request.getResourceParameters().get("xslt")).getContent();
            try {
                content = parseXmlToHtml(xml, xsltContent);
            } catch (Exception e) {
            }

            response.setContent(content);
            response.setStatus(HTTPResponseStatus.S200);
            response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
            return response;
        }

        response.setContent(xmlDAO.get(uuid).getContent());
        response.setStatus(HTTPResponseStatus.S200);
        response.putParameter("Content-Type", MIME.APPLICATION_XML.getMime());

        return response;
    }

    private boolean validateXML(String xml, String xsd) {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            Schema schema = schemaFactory.newSchema(new StreamSource(new StringReader(xsd)));

            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new StringReader(xml)));
            return true;
        } catch (SAXException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String parseXmlToHtml(String xml, String xslt) throws Exception {

        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer(new StreamSource(new StringReader(xslt)));

        StringWriter writer = new StringWriter();

        transformer.transform(
                new StreamSource(new StringReader(xml)),
                new StreamResult(writer));

        return writer.toString();

    }

    private HTTPResponse HtmlGetHandler(HTTPRequest request, HTTPResponse response) {
        // Se pide un html

        // comprobamos si hay uuid

        String uuid = request.getResourceParameters().get("uuid");

        // si no hay, devolvmos lista
        if (uuid == null) {

            response.setContent(htmldao.listPages());
            response.setStatus(HTTPResponseStatus.S200);
            response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
            return response;

        }

        if (!htmldao.exist(uuid)) {

            response.setStatus(HTTPResponseStatus.S404);
            response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
            return response;
        }

        response.setContent(htmldao.get(uuid).getContent());
        response.setStatus(HTTPResponseStatus.S200);

        response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());

        return response;
    }

    // DELETE HANDLE -----------------------------

    private HTTPResponse deleteHandler(HTTPRequest request) {

        HTTPResponse response = new HTTPResponse();
        String Resource = (request).getResourceName();

        if (request.getResourceName().equals("html"))
            return HtmlDeleteHandler(request, response);

        if (request.getResourceName().equals("xml"))
            return XmlDeleteHandler(request, response);

        if (request.getResourceName().equals("xsd"))
            return XsdDeleteHandler(request, response);

        if (request.getResourceName().equals("xslt"))
            return XsltDeleteHandler(request, response);

        response.setStatus(HTTPResponseStatus.S400);
        response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
        return response;

    }

    private HTTPResponse XsltDeleteHandler(HTTPRequest request, HTTPResponse response) {
        String uuid = request.getResourceParameters().get("uuid");

        if (!xsltDAO.exist(uuid)) {

            response.setStatus(HTTPResponseStatus.S404);
            response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
            response.setContent("<h1>dfsf</h1>");
            return response;
        }

        xsltDAO.deletePage(uuid);
        response.setStatus(HTTPResponseStatus.S200);
        response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());

        return response;
    }

    private HTTPResponse XsdDeleteHandler(HTTPRequest request, HTTPResponse response) {
        String uuid = request.getResourceParameters().get("uuid");

        if (!xsdDAO.exist(uuid)) {

            response.setStatus(HTTPResponseStatus.S404);
            response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
            response.setContent("<h1>dfsf</h1>");
            return response;
        }

        xsdDAO.deletePage(uuid);
        response.setStatus(HTTPResponseStatus.S200);
        response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());

        return response;
    }

    private HTTPResponse XmlDeleteHandler(HTTPRequest request, HTTPResponse response) {
        String uuid = request.getResourceParameters().get("uuid");

        if (!xmlDAO.exist(uuid)) {

            response.setStatus(HTTPResponseStatus.S404);
            response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
            response.setContent("<h1>dfsf</h1>");
            return response;
        }

        xmlDAO.deletePage(uuid);
        response.setStatus(HTTPResponseStatus.S200);
        response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());

        return response;
    }

    private HTTPResponse HtmlDeleteHandler(HTTPRequest request, HTTPResponse response) {
        String uuid = request.getResourceParameters().get("uuid");

        if (!htmldao.exist(uuid)) {

            response.setStatus(HTTPResponseStatus.S404);
            response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
            response.setContent("<h1>dfsf</h1>");
            return response;
        }

        htmldao.deletePage(uuid);

        response.setStatus(HTTPResponseStatus.S200);
        response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());

        return response;
    }

    // POST HANDLE -----------------------------

    private HTTPResponse PostHandler(HTTPRequest request) {
        HTTPResponse response = new HTTPResponse();

        response.setStatus(HTTPResponseStatus.S400);
        response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());

        if (request.ContentLength < 0) {
            return response;
        }

        String content = request.getContent().split("=")[1];
        String uuid = "";
        String link = "";

        if (request.getResourceName().equals("xml")) {
            uuid = xmlDAO.addPage(content);
            link = buildXMLLink(uuid);
        }

        if (request.getResourceName().equals("html")) {

            if (!request.getResourceParameters().keySet().contains("html")) {
                response.setStatus(HTTPResponseStatus.S400);
                response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
                return response;
            }
            uuid = htmldao.addPage(content);
            link = buildHTMLLink(uuid);
        }

        if (request.getResourceName().equals("xsd")) {
            uuid = xsdDAO.addPage(content);
            link = buildXSDLink(uuid);
        }

        // System.out.println(request.getResourceParameters().toString());
        if (request.getResourceName().equals("xslt")) {

            if (!(request.getResourceParameters().keySet().contains("xsd")
                    && request.getResourceParameters().keySet().contains("xslt")))
                return response;

            String xsd = request.getResourceParameters().get("xsd");
            String xslt = request.getResourceParameters().get("xslt");
            System.out.println(!xsdDAO.exist(xsd));
            if (!xsdDAO.exist(xsd)) {
                response.setStatus(HTTPResponseStatus.S404);
                return response;
            }

            uuid = xsltDAO.addPage(xslt, xsd);
            link = buildXSLTLink(uuid);
        }

        response.setContent(link);
        response.setStatus(HTTPResponseStatus.S200);
        response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());

        return response;
    }

    // TODO: hacer que sea el mismo para todos, si es posible
    private String buildHTMLLink(String uuid) {

        StringBuilder toret = new StringBuilder();
        toret.append("<a href=\"html?uuid=");
        toret.append(uuid);
        toret.append("\">");
        toret.append(uuid);
        toret.append("</a>");

        return toret.toString();
    }

    private String buildXMLLink(String uuid) {

        // "<a href=\"" + getResourceName() + "?uuid=" + uuid + "\">" + uuid + "</a>";
        StringBuilder toret = new StringBuilder();
        toret.append("<a href=\"xml?uuid=");
        toret.append(uuid);
        toret.append("\">");
        toret.append(uuid);
        toret.append("</a>");

        // System\.out\.println\(toret\.toString\(\)\);

        return toret.toString();
    }

    private String buildXSDLink(String uuid) {

        StringBuilder toret = new StringBuilder();
        toret.append("<a href=\"xsd?uuid=");
        toret.append(uuid);
        toret.append("\">");
        toret.append(uuid);
        toret.append("</a>");

        // System\.out\.println\(toret\.toString\(\)\);

        return toret.toString();
    }

    private String buildXSLTLink(String uuid) {

        StringBuilder toret = new StringBuilder();
        toret.append("<a href=\"xslt?uuid=");
        toret.append(uuid);
        toret.append("\">");
        toret.append(uuid);
        toret.append("</a>");

        // System\.out\.println\(toret\.toString\(\)\);

        return toret.toString();
    }
}
