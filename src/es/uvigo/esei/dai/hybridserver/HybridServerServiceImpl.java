package es.uvigo.esei.dai.hybridserver;

import es.uvigo.esei.dai.hybridserver.dao.htmlDAO;
import es.uvigo.esei.dai.hybridserver.dao.xmlDAO;
import es.uvigo.esei.dai.hybridserver.dao.xsdDAO;
import es.uvigo.esei.dai.hybridserver.dao.xsltDAO;

import javax.jws.WebService;
@WebService(endpointInterface="es.uvigo.esei.dai.hybridserver.HybridServerService")
public class HybridServerServiceImpl implements HybridServerService{

    htmlDAO htmldao;
    xmlDAO xmlDAO;
    xsdDAO xsdDAO;
    xsltDAO xsltDAO;

    public HybridServerServiceImpl(htmlDAO htmldao, xmlDAO xmlDAO, xsdDAO xsdDAO, xsltDAO xsltDAO){
        this.htmldao = htmldao;
        this.xmlDAO = xmlDAO;
        this.xsdDAO = xsdDAO;
        this.xsltDAO = xsltDAO;
    }

    public String getAllHTMLUUIDs(){
        return htmldao.listPages();
    }
    public String getAllXMLUUIDs(){
        return xmlDAO.listPages();
    }
    public String getAllXSDUUIDs(){
        return xsdDAO.listPages();
    }
    public String getAllXSLTUUIDs(){
        return xsltDAO.listPages();
    }
    public String getHTMLfromUUID(String uuid){
        if (!htmldao.exist(uuid)) {
            return "";
        }
        return htmldao.get(uuid).getContent();
    }
    public String getXMLfromUUID(String uuid){
        if (!xmlDAO.exist(uuid)) {
            return "";
        }
        return xmlDAO.get(uuid).getContent();
    }
    public String getXSDfromUUID(String uuid){
        if (!xsdDAO.exist(uuid)) {
            return "";
        }
        return xsdDAO.get(uuid).getContent();
    }
    public String getXSLTfromUUID(String uuid){
        if (!xsltDAO.exist(uuid)) {
            return "";
        }
        return xsltDAO.get(uuid).getContent();
    }
    public String getXSDUUIDfromXSLTUUID(String uuid){
        if (!xsltDAO.exist(uuid)) {
            return "";
        }
        return xsltDAO.getXsdId(uuid).getId();
    }
}
