package es.uvigo;

public interface ServiceInterface {
    public String[] getAllHTMLUUIDs();
    public String[] getAllXMLUUIDs();
    public String[] getAllXSDUUIDs();
    public String[] getAllXSLTUUIDs();
    public String getHTMLfromUUID();
    public String getXMLfromUUID();
    public String getXSDfromUUID();
    public String getXSLTfromUUID();
    public String getXSDUUIDfromXSLTUUID();
}
