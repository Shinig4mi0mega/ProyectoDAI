package es.uvigo.esei.dai.hybridserver;

import java.util.List;
import java.util.ArrayList;

import es.uvigo.esei.dai.hybridserver.dao.htmlDAO;

public class htmlController {
    private htmlDAO htmldao;
    private List<ServerConfiguration> serverConfigurations;

    public htmlController(htmlDAO htmldao){
        this.htmldao = htmldao;
    }

    public String get(String uuid){
        String toret = htmldao.get(uuid).getContent();
        return toret;
    }

    public String addPage(String content) {
        return htmldao.addPage(content);
    }

    public void deletePage(String id) {
        htmldao.deletePage(id);
    }

    public String listPages() {
        return htmldao.listPages();
    }

    public void setServer(List<ServerConfiguration> serverConfigurationList){
        this.serverConfigurations = serverConfigurationList;
    }
    public boolean exist(String id) {

        return htmldao.exist(id);

    }
}
