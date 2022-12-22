package es.uvigo.esei.dai.hybridserver.dao;

import java.util.Map;
import java.util.UUID;

public class mapDAO implements pagesDAO {
    private final Map<String, String> pages;
    
    public mapDAO(Map<String, String> pages) {
        this.pages = pages;
        
    }

    @Override
    public String addPage(String content) {
        
        UUID randomUuid = UUID.randomUUID();
        String uuid = randomUuid.toString();
        pages.put(uuid, content);
        return uuid;

    }

    @Override
    public void updatePage(String content) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deletePage(String id) {
        pages.remove(id);

    }

    @Override
    public String listPages() {

        StringBuilder toret = new StringBuilder();
        for (String k : pages.keySet()) {
            toret.append(k).append("\n");
        }
        System.out.println(toret.toString());
        return toret.toString();

    }

    @Override
    public page get(String id) {
        pages.get(id);
        page page = new page(id, pages.get(id));
        return page;
    }

    @Override
    public boolean exist(String id) {
        return pages.keySet().contains(id);
    }

}
