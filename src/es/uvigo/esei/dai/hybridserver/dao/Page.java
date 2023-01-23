package es.uvigo.esei.dai.hybridserver.dao;

public class Page {
    private String id;
    private String Content;

    

    public Page(String id, String content) {
        this.id = id;
        Content = content;
    }

    public Page() {
        this.id = null;
        Content = null;
    }
    
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getContent() {
        return Content;
    }
    public void setContent(String content) {
        Content = content;
    } 

}
