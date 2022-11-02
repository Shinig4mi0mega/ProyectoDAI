package es.uvigo.esei.dai.hybridserver;

public class JDBDAO implements pagesDAO{
    // añadir objeto connection

    public JDBDAO() {
        // añadir objeto connection
    }

    @Override
    public String addPage(String content) {
        // statement.executeUpdate para añadir cosas
        return null;
    }



    @Override
    public void updatePage(String content) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void deletePage(String id) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String listPages() {
         // statement.executeQuery para traer solo cosas
        return null;
    }

    @Override
    public page get(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean exist(String id) {
        // TODO Auto-generated method stub
        return false;
    }
    
}
