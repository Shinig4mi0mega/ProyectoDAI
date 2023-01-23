package es.uvigo.esei.dai.hybridserver;

import java.util.List;
import java.util.ArrayList;

import es.uvigo.esei.dai.hybridserver.dao.xsltDAO;

public class xsltController {
    private xsltDAO xsltdao;
    private List<ServerConfiguration> serverConfigurations;

    public xsltController(xsltDAO xsltdao){
        this.xsltdao = xsltdao;
    }

    public String get(String uuid){
        String toret = xsltdao.get(uuid).getContent();
        return toret;
    }

    public String addPage(String content, String xsd) {
        return xsltdao.addPage(content, xsd);
    }

    public void deletePage(String id) {
        xsltdao.deletePage(id);
    }

    public String listPages() {
        return xsltdao.listPages();
    }

    public String getXsdId(String id) {
        return xsltdao.getXsdId(id).getContent();
    }

    public boolean exist(String id) {

        return xsltdao.exist(id);

    }
    
    public void setServer(List<ServerConfiguration> serverConfigurationList){
        this.serverConfigurations = serverConfigurationList;
    }
}