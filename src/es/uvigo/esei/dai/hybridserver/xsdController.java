package es.uvigo.esei.dai.hybridserver;

import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import es.uvigo.esei.dai.hybridserver.dao.xsdDAO;

public class xsdController {
    private xsdDAO xsddao;
    private List<ServerConfiguration> serverConfigurations;

    public xsdController(xsdDAO xsddao){
        this.xsddao = xsddao;
    }

    public String get(String uuid){
        String toret = "";
        
        if(exist(uuid)){
            toret = xsddao.get(uuid).getContent();
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
                    String temp = hs.getXSDfromUUID(uuid);
                    System.out.println("Respuesta web service "+temp);
                    
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
        System.out.println("return "+toret);
        return toret;
    }

    public String addPage(String content) {
        return xsddao.addPage(content);
    }

    public void deletePage(String id) {
        xsddao.deletePage(id);
    }

    public String listPages() {
        String toret = "<html><head></head><body>";
        toret+=xsddao.listPages();
        for(int i = 0; i< serverConfigurations.size(); i++){
            try {
                URL url = new URL(serverConfigurations.get(i).getWsdl());
                QName name = new QName("http://hybridserver.dai.esei.uvigo.es/",serverConfigurations.get(i).getService()+"ImplService");
                Service webService = Service.create(url, name);
                
                HybridServerService hs = webService.getPort(HybridServerService.class);
                toret += "\n";
                toret+= hs.getAllXSDUUIDs();
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

        return xsddao.exist(id);

    }
}