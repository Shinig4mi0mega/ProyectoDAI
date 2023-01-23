package es.uvigo.esei.dai.hybridserver;

import javax.jws.WebMethod;
import javax.jws.WebService;

import javax.jws.soap.SOAPBinding;

@WebService
@SOAPBinding(style=SOAPBinding.Style.RPC)
public interface HybridServerService {
    @WebMethod
    public String getAllHTMLUUIDs();
    @WebMethod
    public String getAllXMLUUIDs();
    @WebMethod
    public String getAllXSDUUIDs();
    @WebMethod
    public String getAllXSLTUUIDs();
    @WebMethod
    public String getHTMLfromUUID(String UUID);
    @WebMethod
    public String getXMLfromUUID(String UUID);
    @WebMethod
    public String getXSDfromUUID(String UUID);
    @WebMethod
    public String getXSLTfromUUID(String UUID);
    @WebMethod
    public String getXSDUUIDfromXSLTUUID(String UUID);
}
