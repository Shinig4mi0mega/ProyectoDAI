package es.uvigo.esei.dai.hybridserver;

public interface pagesDAO {

    public String addPage(String content);
    public void updatePage(String content);
    public void deletePage(String id);
    public String listPages();

    public page get(String id);
    public boolean exist(String id);
    
}