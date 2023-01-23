package es.uvigo.esei.dai.hybridserver.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import java.util.UUID;

public class htmlDAO implements pagesDAO {
    // añadir objeto connection
    Connection connection;
    Properties properties;
    int port;

    public htmlDAO(Properties properties) {
        // añadir objeto connection
        this.properties = properties;
        String dburl = properties.getProperty("db.url");
        String user = properties.getProperty("db.user");
        String pass = properties.getProperty("db.password");
        this.port = Integer.parseInt(properties.getProperty("port"));
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
            statement.executeUpdate("INSERT INTO HTML(`uuid`, `content`) VALUES ('" + uuid + "','" + content + "')");

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
            statement.executeUpdate("DELETE FROM HTML WHERE uuid=\'" + id + "\'");

        } catch (SQLException e) {
        }

    }

    @Override
    public String listPages() {
        // statement.executeQuery para traer solo cosas
        // Devuelve un objeto ResultSet
        StringBuilder toret = new StringBuilder();
        try (Statement statement = connection.createStatement()) {
            try (ResultSet result = statement.executeQuery("Select uuid from HTML")) {
                while (result.next()) {
                    toret.append("<a href=http://localhost:").append(port).append("/html?uuid=").append(result.getString("uuid")).append(">").append(result.getString("uuid")).append("</a><br/>");
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
            try (ResultSet result = statement.executeQuery("Select * from HTML where uuid=\'" + id + "\'")) {
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
            try (ResultSet result = statement.executeQuery("Select * from HTML where uuid=\'" + id + "\'")) {
                result.next();
                content = result.getString("content");

            }
        }catch (SQLException e) {
        }

        //System.out.println(content);

        return !(content == null);

    }

}
