package es.uvigo.esei.dai.hybridserver;

public interface ServiceInterface {
    public String[] getAllHTMLUUIDs();
    public String[] getAllXMLUUIDs();
    public String[] getAllXSDUUIDs();
    public String[] getAllXSLTUUIDs();
    public String getHTMLfromUUID(String UUID);
    public String getXMLfromUUID(String UUID);
    public String getXSDfromUUID(String UUID);
    public String getXSLTfromUUID(String UUID);
    public String getXSDUUIDfromXSLTUUID(String UUID);
}
