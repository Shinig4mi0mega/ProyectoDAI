package es.uvigo.esei.dai.hybridserver.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.UUID;

public class xmlDAO implements pagesDAO {
    Connection connection;
    Properties properties;

    public xmlDAO(Properties properties) {
        // añadir objeto connection
        this.properties = properties;
        String dburl = properties.getProperty("db.url");
        String user = properties.getProperty("db.user");
        String pass = properties.getProperty("db.password");
        try {
            this.connection = DriverManager.getConnection(dburl, user, pass);
        } catch (Exception e) {
        }

    }

    @Override
    public String addPage(String content) {
        UUID randomUuid = UUID.randomUUID();
        String uuid = randomUuid.toString();
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("INSERT INTO `xml`(`uuid`, `content`) VALUES ('" + uuid + "','" + content + "')");

        } catch (SQLException e) {
        }

        return uuid;
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
        // TODO Auto-generated method stub
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
