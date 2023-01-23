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

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLConfigurationLoader {
	static Configuration configuration = new Configuration();

	public static Configuration load(File xmlFile) throws Exception {

		// Construcción del parser del documento
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		// Parsing del documento
		Document parsedXml = builder.parse(xmlFile);

		Node configurationNode = parsedXml.getDocumentElement();

		NodeList configNodes = configurationNode.getChildNodes();

		String currentNodeName;
		int handledNodes = 0;
		for (int i = 0; i < configNodes.getLength(); i++) {
			// System.out.println(configNodes.item(i).getNodeName());

			currentNodeName = configNodes.item(i).getNodeName();

			if (currentNodeName.equals("connections")) {
				handleConections(configNodes.item(i));
				handledNodes++;
			}

			if (currentNodeName.equals("database")) {
				handleDatabase(configNodes.item(i));
				handledNodes++;
			}

			if (currentNodeName.equals("servers")) {
				handleServers(configNodes.item(i));
				handledNodes++;
			}
		}

		if (handledNodes != 3)
			throw new Exception();

		return configuration;
	}

	private static void handleConections(Node item) throws Exception{
		NodeList connectionNodes = item.getChildNodes();
		boolean http = false;
		String currentNodeName;
		for (int i = 0; i < connectionNodes.getLength(); i++) {
			// System.out.println(connectionNodes.item(i).getNodeName());

			currentNodeName = connectionNodes.item(i).getNodeName();

			if (currentNodeName.equals("http")) {
				configuration.setHttpPort(Integer.parseInt(connectionNodes.item(i).getTextContent()));
				http = true;
				continue;
			}

			if (currentNodeName.equals("webservice")) {
				configuration.setWebServiceURL(connectionNodes.item(i).getTextContent());

				continue;
			}

			if (currentNodeName.equals("numClients")) {
				configuration.setNumClients(Integer.parseInt(connectionNodes.item(i).getTextContent()));

				continue;
			}

		}

		if(!http)
			throw new Exception();
	}

	private static void handleDatabase(Node item) {
		NodeList connectionNodes = item.getChildNodes();

		String currentNodeName;
		for (int i = 0; i < connectionNodes.getLength(); i++) {
			// System.out.println(connectionNodes.item(i).getNodeName());

			currentNodeName = connectionNodes.item(i).getNodeName();

			if (currentNodeName.equals("user")) {
				configuration.setDbUser(connectionNodes.item(i).getTextContent());

				continue;
			}

			if (currentNodeName.equals("password")) {
				configuration.setDbPassword(connectionNodes.item(i).getTextContent());

				continue;
			}

			if (currentNodeName.equals("url")) {
				configuration.setDbURL(connectionNodes.item(i).getTextContent());

				continue;
			}

		}
	}

	private static void handleServers(Node item) throws Exception {
		NodeList connectionNodes = item.getChildNodes();
		List<ServerConfiguration> serverConfigs = new LinkedList<ServerConfiguration>();
		String currentNodeName;
		for (int i = 0; i < connectionNodes.getLength(); i++) {
			System.out.println(connectionNodes.item(i).getNodeName());

			currentNodeName = connectionNodes.item(i).getNodeName();

			if (currentNodeName.equals("server"))
				serverConfigs.add(parseServer(connectionNodes.item(i).getAttributes()));

		}

		configuration.setServers(serverConfigs);
	}

	private static ServerConfiguration parseServer(NamedNodeMap serverAtributes) throws Exception {
		ServerConfiguration config = new ServerConfiguration();
		String line[];
		int parsedNodes = 0;
		for (int i = 0; i < serverAtributes.getLength(); i++) {

			line = serverAtributes.item(i).toString().split("=\"");

			if (line[0].equals("httpAddress")) {
				config.setHttpAddress(line[1].substring(0, line[1].length() - 1));
				parsedNodes++;
				continue;
			}

			if (line[0].equals("name")) {
				config.setName(line[1].substring(0, line[1].length() - 1));
				parsedNodes++;
				continue;
			}

			if (line[0].equals("namespace")) {
				config.setNamespace(line[1].substring(0, line[1].length() - 1));
				parsedNodes++;
				continue;
			}

			if (line[0].equals("service")) {
				config.setService(line[1].substring(0, line[1].length() - 1));
				parsedNodes++;
				continue;
			}

			if (line[0].equals("wsdl")) {
				config.setWsdl(line[1].substring(0, line[1].length() - 1));
				parsedNodes++;
				continue;
			}

		}

		if (parsedNodes != 5)
			throw new Exception();

		return config;
	}

}
