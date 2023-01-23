/**
 *  HybridServer
 *  Copyright (C) 2022 Miguel Reboiro-Jato
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.ws.Endpoint;

import es.uvigo.esei.dai.hybridserver.dao.htmlDAO;
import es.uvigo.esei.dai.hybridserver.dao.pagesDAO;
import es.uvigo.esei.dai.hybridserver.dao.xmlDAO;
import es.uvigo.esei.dai.hybridserver.dao.xsdDAO;
import es.uvigo.esei.dai.hybridserver.dao.xsltDAO;

public class HybridServer {
	private int SERVICE_PORT = 8888;
	private Thread serverThread;
	private boolean stop;
	private Properties DAOProperties;
	pagesDAO dao;
	public int nthreads = 50;
	private String serviceString;
	private List<ServerConfiguration> serverConfigurations= new ArrayList<>();
	private Endpoint endpoint;

	public HybridServer() {
		System.out.println("EMPTY");
		Properties properties = new Properties();
		properties.setProperty("port", Integer.toString(8888));
		properties.setProperty("numClients", "50");
		properties.setProperty("db.url", "jdbc:mysql://localhost/hstestdb");
		properties.setProperty("db.user", "hsdb");
		properties.setProperty("db.password", "hsdbpass");
		
		DAOProperties = properties;
		this.dao = new htmlDAO(properties);
		this.SERVICE_PORT = Integer.parseInt(properties.getProperty("port"));
	}

	//public HybridServer(Map<String, String> pages) {
		//this.dao = new mapDAO(pages);
	//}

	public HybridServer(Properties properties) {
		System.out.println("Propertioes");
		this.nthreads = Integer.parseInt(properties.getProperty("numClients"));
		DAOProperties = properties;
		if (properties.getProperty("port") != null)
			System.out.println("Hay puerto");
			this.SERVICE_PORT = Integer.parseInt(properties.getProperty("port"));
	}

	public HybridServer(Configuration config) {
		System.out.println("Configuration");
		this.nthreads = config.getNumClients();

		this.serviceString = config.getWebServiceURL();

		this.serverConfigurations = new ArrayList<>();

		for(ServerConfiguration temp: config.getServers()){
			if(!temp.getName().equals("Down Server")){
				serverConfigurations.add(temp);
			}
		}
		
		Properties dBProperties = new Properties();
		dBProperties.setProperty("db.url", config.getDbURL());
		dBProperties.setProperty("db.user", config.getDbUser());
		dBProperties.setProperty("db.password", config.getDbPassword());
		dBProperties.setProperty("port", Integer.toString(config.getHttpPort()));

		DAOProperties = dBProperties;
		
		this.SERVICE_PORT = config.getHttpPort();
		
	}

	public int getPort() {
		return SERVICE_PORT;
	}

	public void start() {
		if(!serverConfigurations.isEmpty()){
			this.endpoint = Endpoint.publish(serviceString, new HybridServerServiceImpl(new htmlDAO(DAOProperties), new xmlDAO(DAOProperties), 
				new xsdDAO(DAOProperties), new xsltDAO(DAOProperties)));
		}
		this.serverThread = new Thread() {
			@Override
			public void run() {
				try (final ServerSocket serverSocket = new ServerSocket(SERVICE_PORT)) {
					ExecutorService threadPool = Executors.newFixedThreadPool(nthreads);
					while (true) {

						Socket socket = serverSocket.accept();
						if (stop)
							break;
						ServiceThread st = new ServiceThread(socket, DAOProperties, serverConfigurations);
						threadPool.execute(st);

					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};

		this.stop = false;
		this.serverThread.start();
	}

	public void stop() {
		this.stop = true;

		if(endpoint != null){
			endpoint.stop();
		}

		try (Socket socket = new Socket("localhost", SERVICE_PORT)) {
			// Esta conexión se hace, simplemente, para "despertar" el hilo servidor
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		try {
			this.serverThread.join();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		this.serverThread = null;
	}
}
