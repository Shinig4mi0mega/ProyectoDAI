package es.uvigo.esei.dai.hybridserver.dao;

public class page {
    private String id;
    private String Content;

    

    public page(String id, String content) {
        this.id = id;
        Content = content;
    }

    public page() {
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
