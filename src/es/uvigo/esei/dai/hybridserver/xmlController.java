package es.uvigo.esei.dai.hybridserver;

import java.util.List;
import java.util.ArrayList;

import es.uvigo.esei.dai.hybridserver.dao.xmlDAO;

public class xmlController {
    private xmlDAO xmldao;
    private List<ServerConfiguration> serverConfigurations;

    public xmlController(xmlDAO xmldao){
        this.xmldao = xmldao;
    }

    public String get(String uuid){
        String toret = xmldao.get(uuid).getContent();
        return toret;
    }

    public String addPage(String content) {
        return xmldao.addPage(content);
    }

    public void deletePage(String id) {
        xmldao.deletePage(id);
    }

    public String listPages() {
        return xmldao.listPages();
    }

    public void setServer(List<ServerConfiguration> serverConfigurationList){
        this.serverConfigurations = serverConfigurationList;
    }
    public boolean exist(String id) {

        return xmldao.exist(id);

    }
}