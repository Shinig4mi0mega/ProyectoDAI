package es.uvigo.esei.dai.hybridserver;

import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import es.uvigo.esei.dai.hybridserver.dao.xsltDAO;

public class xsltController {
    private xsltDAO xsltdao;
    private List<ServerConfiguration> serverConfigurations;

    public xsltController(xsltDAO xsltdao){
        this.xsltdao = xsltdao;
    }

    public String get(String uuid){
        String toret = "";
        if(exist(uuid)){
            toret = xsltdao.get(uuid).getContent();
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
                    String temp = hs.getXSLTfromUUID(uuid);
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

    public String addPage(String content, String xsd) {
        return xsltdao.addPage(content, xsd);
    }

    public void deletePage(String id) {
        xsltdao.deletePage(id);
    }

    public String listPages() {
        String toret = "<html><head></head><body>";
        toret+=xsltdao.listPages();
        for(int i = 0; i< serverConfigurations.size(); i++){
            try {
                URL url = new URL(serverConfigurations.get(i).getWsdl());
                QName name = new QName("http://hybridserver.dai.esei.uvigo.es/",serverConfigurations.get(i).getService()+"ImplService");
                Service webService = Service.create(url, name);
                
                HybridServerService hs = webService.getPort(HybridServerService.class);
                toret += "\n";
                toret+= hs.getAllXSLTUUIDs();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }                
        }
        toret+= "</body></html>";
        return toret;
    }

    public String getXsdId(String id) {
        String toret = "";
        
        if(exist(id)){
        	System.out.println("EXISTE EL UUID EN ESTE SERVER");
            toret = xsltdao.getXsdId(id).getContent();
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
                    String temp = hs.getXSDUUIDfromXSLTUUID(id);
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

    public boolean exist(String id) {
    	return xsltdao.exist(id);
        
    }

    public void setServer(List<ServerConfiguration> serverConfigurationList){
        this.serverConfigurations = serverConfigurationList;
    }
}