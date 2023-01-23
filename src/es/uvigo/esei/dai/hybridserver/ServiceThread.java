package es.uvigo.esei.dai.hybridserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.Socket;
import java.util.List;
import java.util.Properties;

import javax.xml.XMLConstants;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

import es.uvigo.esei.dai.hybridserver.dao.htmlDAO;
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
    private htmlDAO htmlDAO;
    private xmlDAO xmlDAO;
    private xsdDAO xsdDAO;
    private xsltDAO xsltDAO;
    private htmlController htmlController;
    private xmlController xmlController;
    private xsdController xsdController;
    private xsltController xsltController;

    public ServiceThread(Socket socket, Properties properties, List<ServerConfiguration> serverConfigurations) {
        this.socket = socket;
        this.htmlDAO = new htmlDAO(properties);
        this.xmlDAO = new xmlDAO(properties);
        this.xsdDAO = new xsdDAO(properties);
        this.xsltDAO = new xsltDAO(properties);
        this.htmlController = new htmlController(htmlDAO);
        this.xmlController = new xmlController(xmlDAO);
        this.xsdController = new xsdController(xsdDAO);
        this.xsltController = new xsltController(xsltDAO);
        htmlController.setServer(serverConfigurations);
        xmlController.setServer(serverConfigurations);
        xsdController.setServer(serverConfigurations);
        xsltController.setServer(serverConfigurations);
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

            System.out.println(response.toString());

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

        if (request.getResourceChain().equals("/")) {
            response.setStatus(HTTPResponseStatus.S200);
            response.setContent("<html><head></head><body><p>Hybrid Server</p><h2>Santiago Barca Fernandez</h2><br/><h2>Andres Garcia Figueroa</h2></body>");
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

            response.setContent(xsltController.listPages());
            response.setStatus(HTTPResponseStatus.S200);
            response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
            return response;

        }

        /*if (!xsltController.exist(uuid)) {

            response.setStatus(HTTPResponseStatus.S404);
            response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
            return response;
        }*/
        
        String ctrResponse= xsltController.get(uuid); 

        if(ctrResponse.equals("")) {
        	//NO ha habido respuesta en ningun servidor
        	response.setStatus(HTTPResponseStatus.S404);
            response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
            return response;
        }else {   
        	response.setContent(xsltController.get(uuid));
        	response.setStatus(HTTPResponseStatus.S200);

        	response.putParameter("Content-Type", MIME.APPLICATION_XML.getMime());

        	return response;
        }
    }

    private HTTPResponse XsdGetHandler(HTTPRequest request, HTTPResponse response) {
        // Se pide un xsd

        // comprobamos si hay uuid

        String uuid = request.getResourceParameters().get("uuid");

        // si no hay, devolvmos lista
        if (uuid == null) {

            response.setContent(xsdController.listPages());
            response.setStatus(HTTPResponseStatus.S200);
            response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
            return response;

        }

        String ctrResponse= xsdController.get(uuid); 

        if(ctrResponse.equals("")) {
        	//NO ha habido respuesta en ningun servidor
        	response.setStatus(HTTPResponseStatus.S404);
            response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
            return response;
        }else {   
        	response.setContent(ctrResponse);
        	response.setStatus(HTTPResponseStatus.S200);

        	response.putParameter("Content-Type", MIME.APPLICATION_XML.getMime());

        	return response;
        }
    }

    private HTTPResponse XmlGetHandler(HTTPRequest request, HTTPResponse response) {
        // Se pide un xml

        // comprobamos si hay uuid

        String uuid = request.getResourceParameters().get("uuid");

        // si no hay, devolvmos lista
        if (uuid == null) {

            response.setContent(xmlController.listPages());
            response.setStatus(HTTPResponseStatus.S200);
            response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
            return response;

        }

        ///////////////////////////////////////////////////////////////////////////////////
        /*if (!xmlController.exist(uuid)) {
            response.setStatus(HTTPResponseStatus.S404);
            response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
            return response;
        }*/

        /*
        String ctrResponse= xmlController.get(uuid); 

        if(ctrResponse.equals("")) {
        	System.out.println("NO ha habido respuesta en ningun servidor");
        	response.setStatus(HTTPResponseStatus.S404);
            response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
            return response;
        }else {  
        	System.out.println("HA habido respuesta en algun servidor");
        	
        	if (request.getResourceParameters().keySet().contains("xslt")) {
        		System.out.println("CONTIENE XSLT");
        		
        		String content = "";
        		String xml = xmlController.get(uuid);

        		String uuidXSLT= request.getResourceParameters().get("xslt");
        		
        		System.out.println("UUID XSLT"+uuidXSLT);
        		String ctrResponseXSLT= xsltController.get(uuidXSLT);
            
        		// VERIFICACIONES DEL XSLT Y XSD--------------------------------
        		if (ctrResponseXSLT.equals("")) {
        			System.out.println("NO EXISTE XSLT, 404");
        			response.setStatus(HTTPResponseStatus.S404);
        			response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
        			return response;
        		}else {
        			System.out.println("EXISTE XSLT, SEGUIMOS");
        			String xsdUuid = xsltController.getXsdId(request.getResourceParameters().get("xslt"));
            		System.out.println("XSD UUID: "+xsdUuid);
            		
            		String ctrControllerxsd= xsdController.get(xsdUuid);
            		System.out.println("XSD: "+ctrControllerxsd);
            		
            			if (!validateXML(xml, ctrControllerxsd)) {
            				System.out.println("XSD NO VALIDA XML");
            				response.setStatus(HTTPResponseStatus.S400);
            				response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
            				return response;
                        
            			}else {
            				// VERIFICACIONES DEL XSLT Y XSD-----------------------------------
            				System.out.println("XSD VALIDA XML, SE DEVUELVE HTML");
            				String xsltContent = xsltController.get(request.getResourceParameters().get("xslt"));
            				try {
            					content = parseXmlToHtml(xml, xsltContent);
            				} catch (Exception e) {
            				System.err.println("ROJO SOY E INFELIZ ESTOY");
            				}

            				response.setContent(content);
            				response.setStatus(HTTPResponseStatus.S200);
            				response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
            				return response;
            			}
            		}
        	}else {
        		System.out.println("NO EXISTE XSLT");
        		response.setContent(xmlController.get(uuid));
        		response.setStatus(HTTPResponseStatus.S200);
        		response.putParameter("Content-Type", MIME.APPLICATION_XML.getMime());

        		return response;
        	}
        }*/
        
        String ctrResponse= xmlController.get(uuid); 

        if(ctrResponse.equals("")) {
        	System.out.println("NO ha habido respuesta en ningun servidor");
        	response.setStatus(HTTPResponseStatus.S404);
            response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
            return response;
        }else {
            System.out.println("Ha habido respuesta");

            if (request.getResourceParameters().keySet().contains("xslt")) {
            	System.out.println("Trae xslt");
            	
                String content = "";
                String xml = xmlController.get(uuid);
    
                // VERIFICACIONES DEL XSLT Y XSD--------------------------------
                String xsltUuid= request.getResourceParameters().get("xslt");
                System.out.println(xsltUuid);
                String ctrXSLTResponse= xsltController.get(xsltUuid); 
                
                if (ctrXSLTResponse.equals("")) {
                	System.out.println("No existe xslt");
                    response.setStatus(HTTPResponseStatus.S404);
                    response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
                    return response;
                }
    
                String xsdUuid = xsltController.getXsdId(xsltUuid);
                System.out.println("XSD UUID NO ESPERAMOS ESTO "+xsdUuid);
                System.out.println("XSLT UUID "+request.getResourceParameters().get("xslt"));
                if(xsdUuid.equals("")) {
                	System.out.println("NO existe xsd");
                }else {
                	String xsd = xsdController.get(xsdUuid);
                    
                    if (!validateXML(xml, xsd)) {
                        response.setStatus(HTTPResponseStatus.S400);
                        response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
                        return response;
                    }
        
                    // VERIFICACIONES DEL XSLT Y XSD-----------------------------------
        
                    String xsltContent = xsltController.get(request.getResourceParameters().get("xslt"));
                    try {
                        content = parseXmlToHtml(xml, xsltContent);
                    } catch (Exception e) {
                    }
        
                    response.setContent(content);
                    response.setStatus(HTTPResponseStatus.S200);
                    response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
                    return response;
                }
            }
    
            response.setContent(xmlController.get(uuid));
            response.setStatus(HTTPResponseStatus.S200);
            response.putParameter("Content-Type", MIME.APPLICATION_XML.getMime());
    
            return response;

        }
        
    }

    private boolean validateXML(String xml, String xsd) {
    	System.out.println("XML: "+xml);
    	System.out.println("XSD: "+xsd);
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            Schema schema = schemaFactory.newSchema(new StreamSource(new StringReader(xsd)));

            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new StringReader(xml)));
            return true;
        } catch (SAXException | IOException e) {
        	System.out.println("EXCEPCION XD");
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

            response.setContent(htmlController.listPages());
            response.setStatus(HTTPResponseStatus.S200);
            response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
            return response;

        }

        /*if (!htmlController.exist(uuid)) {

            response.setStatus(HTTPResponseStatus.S404);
            response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
            return response;
        }

        response.setContent(htmlController.get(uuid));
        response.setStatus(HTTPResponseStatus.S200);

        response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());

        return response;*/
        
        String ctrResponse= htmlController.get(uuid); 

        if(ctrResponse.equals("")) {
        	//NO ha habido respuesta en ningun servidor
        	response.setStatus(HTTPResponseStatus.S404);
            response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
            return response;
        }else {   
        	response.setContent(ctrResponse);
        	response.setStatus(HTTPResponseStatus.S200);

        	response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());

        	return response;
        }
    }

    // DELETE HANDLE -----------------------------

    private HTTPResponse deleteHandler(HTTPRequest request) {

        HTTPResponse response = new HTTPResponse();

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

        if (!xsltController.exist(uuid)) {

            response.setStatus(HTTPResponseStatus.S404);
            response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
            response.setContent("<h1>dfsf</h1>");
            return response;
        }

        xsltController.deletePage(uuid);
        response.setStatus(HTTPResponseStatus.S200);
        response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());

        return response;
    }

    private HTTPResponse XsdDeleteHandler(HTTPRequest request, HTTPResponse response) {
        String uuid = request.getResourceParameters().get("uuid");

        if (!xsdController.exist(uuid)) {

            response.setStatus(HTTPResponseStatus.S404);
            response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
            response.setContent("<h1>dfsf</h1>");
            return response;
        }

        xsdController.deletePage(uuid);
        response.setStatus(HTTPResponseStatus.S200);
        response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());

        return response;
    }

    private HTTPResponse XmlDeleteHandler(HTTPRequest request, HTTPResponse response) {
        String uuid = request.getResourceParameters().get("uuid");

        if (!xmlController.exist(uuid)) {

            response.setStatus(HTTPResponseStatus.S404);
            response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
            response.setContent("<h1>dfsf</h1>");
            return response;
        }

        xmlController.deletePage(uuid);
        response.setStatus(HTTPResponseStatus.S200);
        response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());

        return response;
    }

    private HTTPResponse HtmlDeleteHandler(HTTPRequest request, HTTPResponse response) {
        String uuid = request.getResourceParameters().get("uuid");

        if (!htmlController.exist(uuid)) {

            response.setStatus(HTTPResponseStatus.S404);
            response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
            response.setContent("<h1>dfsf</h1>");
            return response;
        }

        htmlController.deletePage(uuid);

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
            uuid = xmlController.addPage(content);
            link = buildXMLLink(uuid);
        }

        if (request.getResourceName().equals("html")) {

            if (!request.getResourceParameters().keySet().contains("html")) {
                response.setStatus(HTTPResponseStatus.S400);
                response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
                return response;
            }
            uuid = htmlController.addPage(content);
            link = buildHTMLLink(uuid);
        }

        if (request.getResourceName().equals("xsd")) {
            uuid = xsdController.addPage(content);
            link = buildXSDLink(uuid);
        }

        // System.out.println(request.getResourceParameters().toString());
        if (request.getResourceName().equals("xslt")) {

            if (!(request.getResourceParameters().keySet().contains("xsd")
                    && request.getResourceParameters().keySet().contains("xslt")))
                return response;

            String xsd = request.getResourceParameters().get("xsd");
            String xslt = request.getResourceParameters().get("xslt");
            System.out.println(!xsdController.exist(xsd));
            if (!xsdController.exist(xsd)) {
                response.setStatus(HTTPResponseStatus.S404);
                return response;
            }

            uuid = xsltController.addPage(xslt, xsd);
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
