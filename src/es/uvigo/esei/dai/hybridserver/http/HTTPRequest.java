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
package es.uvigo.esei.dai.hybridserver.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class HTTPRequest {
	public BufferedReader buffReader;
	
	HTTPRequestMethod method;
	String ResourceChain;
	String[] ResourcePath;
	String HttpVersion;
	

	public HTTPRequest(Reader reader) throws IOException, HTTPParseException {

		buffReader = new BufferedReader(reader);
		
		String firstLine =  buffReader.readLine();
		
		
		
		//first line constructor parsing methods
		this.method = parseMethod(firstLine);
		System.out.println(method);
		this.ResourceChain = parseResourceChain(firstLine);
		System.out.println(ResourceChain);
		this.ResourcePath = parseResourcePath(firstLine);
		//System.out.println(ResourcePath);
		this.HttpVersion = parseHttpVersion(firstLine);
		System.out.println(HttpVersion);
		
	}
	
	//private constructor methods
	
	private HTTPRequestMethod parseMethod(String line) {
			if (line.contains("HEAD"))
				return HTTPRequestMethod.HEAD;

			if (line.contains("GET"))
				return HTTPRequestMethod.GET;

			if (line.contains("POST"))
				return HTTPRequestMethod.POST;

			if (line.contains("PUT"))
				return HTTPRequestMethod.PUT;

			if (line.contains("DELETE"))
				return HTTPRequestMethod.DELETE;

			if (line.contains("TRACE"))
				return HTTPRequestMethod.TRACE;

			if (line.contains("OPTIONS"))
				return HTTPRequestMethod.OPTIONS;

			if (line.contains("CONNECT"))
				return HTTPRequestMethod.CONNECT;

		return null;
	}
	
	private String parseResourceChain(String line) {
		String resourceChain;
			resourceChain = line.substring(line.indexOf('/'));
			resourceChain = resourceChain.substring(0,resourceChain.indexOf(' '));
			//resourceChain = resourceChain.substring(0,resourceChain.indexOf("?"));
		return resourceChain;
		
	}
	
	private String[] parseResourcePath(String line) {
		line = line.substring(0,line.indexOf("?"));
		return line.split(line);
	}
	
	private String parseHttpVersion(String line) {
		StringBuilder toret = new StringBuilder("HTTP/");
			if(line.indexOf("HTTP/") != -1) {
				int httpIndex = line.indexOf("HTTP/");
				//Sumo 5 quen son los 5 caracteres de HTTP y 8 = 5 + 3 donde 3 son los 3 caracteres de la version
				String version = line.substring(httpIndex + 5, httpIndex + 8);
				toret.append(version);
			}
		return toret.toString();
	}
	
	//END private constructor methods

	public HTTPRequestMethod getMethod() {
		return this.method;
	}

	public String getResourceChain() {
		return this.ResourceChain;
	}

	public String[] getResourcePath() {
		return this.ResourcePath;
	}

	public String getResourceName() {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, String> getResourceParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getHttpVersion() {
		return this.HttpVersion;
	}

	public Map<String, String> getHeaderParameters() {
		Map<String, String> toret = new HashMap<>();
		try {
			String line = buffReader.readLine();
			String[] tmp;
			//La anterior sera la cabecera asi que la ignoro
			 line = buffReader.readLine();
			while(line !=  null && line.length() != 0) {
				System.out.println(line);
				tmp = line.split(": ");
				toret.put(tmp[0], tmp[1]);
				line = buffReader.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return toret;
		
	}

	public String getContent() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getContentLength() {
		// TODO Auto-generated method stub
		return -1;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(this.getMethod().name()).append(' ').append(this.getResourceChain())
				.append(' ').append(this.getHttpVersion()).append("\r\n");

		for (Map.Entry<String, String> param : this.getHeaderParameters().entrySet()) {
			sb.append(param.getKey()).append(": ").append(param.getValue()).append("\r\n");
		}

		if (this.getContentLength() > 0) {
			sb.append("\r\n").append(this.getContent());
		}

		return sb.toString();
	}
}
