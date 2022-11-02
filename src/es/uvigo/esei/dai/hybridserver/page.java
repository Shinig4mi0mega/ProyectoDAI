package es.uvigo.esei.dai.hybridserver;

public class page {
    private String id;
    private String Content;

    

    public page(String id, String content) {
        this.id = id;
        Content = content;
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
