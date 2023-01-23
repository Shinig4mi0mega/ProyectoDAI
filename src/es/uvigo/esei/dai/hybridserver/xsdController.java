package es.uvigo.esei.dai.hybridserver;

import java.util.List;
import java.util.ArrayList;

import es.uvigo.esei.dai.hybridserver.dao.xsdDAO;

public class xsdController {
    private xsdDAO xsddao;
    private List<ServerConfiguration> serverConfigurations;

    public xsdController(xsdDAO xsddao){
        this.xsddao = xsddao;
    }

    public String get(String uuid){
        String toret = xsddao.get(uuid).getContent();
        return toret;
    }

    public String addPage(String content) {
        return xsddao.addPage(content);
    }

    public void deletePage(String id) {
        xsddao.deletePage(id);
    }

    public String listPages() {
        return xsddao.listPages();
    }

    public void setServer(List<ServerConfiguration> serverConfigurationList){
        this.serverConfigurations = serverConfigurationList;
    }

    public boolean exist(String id) {

        return xsddao.exist(id);

    }
}