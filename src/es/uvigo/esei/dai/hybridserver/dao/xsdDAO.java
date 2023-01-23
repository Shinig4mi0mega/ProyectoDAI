package es.uvigo.esei.dai.hybridserver.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import java.util.UUID;

public class xsdDAO implements pagesDAO {

    Connection connection;
    Properties properties;
    int port;
    
    String dburl;
    String user;
    String pass;

    public xsdDAO(Properties properties) {
        // añadir objeto connection
        this.properties = properties;
        this.dburl = properties.getProperty("db.url");
        this.user = properties.getProperty("db.user");
        this.pass = properties.getProperty("db.password");
        
        this.port = Integer.parseInt(properties.getProperty("port"));
    }

    public void createConnection(String dburl, String user, String pass) {
    	try {
        	System.out.println("CONNECTION");
            this.connection = DriverManager.getConnection(dburl, user, pass);
        } catch (Exception e) {
        }
	}

    @Override
    public String addPage(String content) {
    	createConnection(dburl,user,pass);
        UUID randomUuid = UUID.randomUUID();
        String uuid = randomUuid.toString();
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("INSERT INTO `xsd`(`uuid`, `content`) VALUES ('" + uuid + "','" + content + "')");

        } catch (SQLException e) {
        }finally {
        	try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        return uuid;
    }

    @Override
    public void updatePage(String content) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deletePage(String id) {
    	createConnection(dburl,user,pass);
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM xsd WHERE uuid=\'" + id + "\'");

        } catch (SQLException e) {
        }finally {
        	try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

    }

    @Override
    public String listPages() {
        // statement.executeQuery para traer solo cosas
        // Devuelve un objeto ResultSet
    	createConnection(dburl,user,pass);
        StringBuilder toret = new StringBuilder();
        try (Statement statement = connection.createStatement()) {
            try (ResultSet result = statement.executeQuery("Select uuid from xsd")) {
                while (result.next()) {
                    toret.append("<a href=http://localhost:").append(port).append("/xsd?uuid=").append(result.getString("uuid")).append(">").append(result.getString("uuid")).append("</a><br/>");
                }
            }

        } catch (SQLException e) {
        }finally {
        	try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        System.out.println(toret);

        return toret.toString();

    }

    @Override
    public page get(String id) {
    	createConnection(dburl,user,pass);
        page page = new page();
        page.setId(id);
        try (Statement statement = connection.createStatement()) {
            try (ResultSet result = statement.executeQuery("Select * from xsd where uuid=\'" + id + "\'")) {
                result.next();
                String content = result.getString("content");
                page.setContent(content);

            }

        } catch (SQLException e) {
        }finally {
        	try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        return page;
    }

    @Override
    public boolean exist(String id) {
    	createConnection(dburl,user,pass);
        String content = null;
        try (Statement statement = connection.createStatement()) {
            try (ResultSet result = statement.executeQuery("Select * from xsd where uuid=\'" + id + "\'")) {
                result.next();
                content = result.getString("content");

            }
        }catch (SQLException e) {
        }finally {
        	try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        //System.out.println(content);

        return !(content == null);

    }

}