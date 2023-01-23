package es.uvigo.esei.dai.hybridserver;

import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import java.net.MalformedURLException;
import java.net.URL;

import es.uvigo.esei.dai.hybridserver.dao.XmlDAO;

public class XmlController {
    private XmlDAO xmldao;
    private List<ServerConfiguration> serverConfigurations;

    public XmlController(XmlDAO xmldao){
        this.xmldao = xmldao;
    }

    public String get(String uuid){
        String toret = "";
        if(exist(uuid)){
            toret = xmldao.get(uuid).getContent();
        }
        else{
            int i = 0;
            boolean done = false;
            while(i< serverConfigurations.size() && !done){
                try {
                    URL url = new URL(serverConfigurations.get(i).getWsdl());
                    QName name = new QName("http://hybridserver.dai.esei.uvigo.es/",serverConfigurations.get(i).getService()+"ImplService");
                    Service webService = Service.create(url, name);
                    
                    HybridServerService hs = webService.getPort(HybridServerService.class);
                    String temp = hs.getXMLfromUUID(uuid);
                    if(!temp.equals("")){
                        toret = temp;
                        done = true;
                    }
                    i++;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }                
            }
        }
        return toret;
    }

    public String addPage(String content) {
        return xmldao.addPage(content);
    }

    public void deletePage(String id) {
        xmldao.deletePage(id);
    }

    public String listPages() {
        String toret = "<html><head></head><body>";
        toret+=xmldao.listPages();
        for(int i = 0; i< serverConfigurations.size(); i++){
            try {
                URL url = new URL(serverConfigurations.get(i).getWsdl());
                QName name = new QName("http://hybridserver.dai.esei.uvigo.es/",serverConfigurations.get(i).getService()+"ImplService");
                Service webService = Service.create(url, name);
                
                HybridServerService hs = webService.getPort(HybridServerService.class);
                toret += "\n";
                toret+= hs.getAllXMLUUIDs();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }                
        }
        toret+= "</body></html>";
        return toret;
    }

    public void setServer(List<ServerConfiguration> serverConfigurationList){
        this.serverConfigurations = serverConfigurationList;
    }
    public boolean exist(String id) {

        return xmldao.exist(id);

    }
}