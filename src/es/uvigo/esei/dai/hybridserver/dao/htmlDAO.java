package es.uvigo.esei.dai.hybridserver.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.UUID;

public class htmlDAO implements pagesDAO {
    // añadir objeto connection
    Connection connection;
    Properties properties;

    public htmlDAO(Properties properties) {
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
            statement.executeUpdate("INSERT INTO `html`(`uuid`, `content`) VALUES ('" + uuid + "','" + content + "')");

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
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM `html` WHERE uuid=\'" + id + "\'");

        } catch (SQLException e) {
        }

    }

    @Override
    public String listPages() {
        // statement.executeQuery para traer solo cosas
        // Devuelve un objeto ResultSet
        StringBuilder toret = new StringBuilder();
        try (Statement statement = connection.createStatement()) {
            try (ResultSet result = statement.executeQuery("Select uuid from html")) {
                while (result.next()) {
                    String id = result.getString("uuid");
                    toret.append(id).append("\n");
                }
            }

        } catch (SQLException e) {
        }

        System.out.println(toret);

        return toret.toString();

    }

    @Override
    public page get(String id) {
        page page = new page();
        page.setId(id);
        try (Statement statement = connection.createStatement()) {
            try (ResultSet result = statement.executeQuery("Select * from html where uuid=\'" + id + "\'")) {
                result.next();
                String content = result.getString("content");
                page.setContent(content);

            }

        } catch (SQLException e) {
        }

        return page;
    }

    @Override
    public boolean exist(String id) {
        String content = null;
        try (Statement statement = connection.createStatement()) {
            try (ResultSet result = statement.executeQuery("Select * from html where uuid=\'" + id + "\'")) {
                result.next();
                content = result.getString("content");

            }
        }catch (SQLException e) {
        }

        //System.out.println(content);

        return !(content == null);

    }

}
