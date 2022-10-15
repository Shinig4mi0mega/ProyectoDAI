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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class HTTPRequest {
	public BufferedReader buffReader;

	HTTPRequestMethod method;
	String ResourceChain;
	String[] ResourcePath;
	String ResourceName;
	String HttpVersion;
	Map<String, String> HeaderParameters;
	Map<String, String> ResourceParameters;
	String content;
	int ContentLength;

	public HTTPRequest(Reader reader) throws IOException, HTTPParseException {

		buffReader = new BufferedReader(reader);
		this.ResourceParameters = new HashMap<String, String>();

		String firstLine = buffReader.readLine();
		// System.out.println("Constructor called");
		// first line constructor parsing methods
		this.method = parseMethod(firstLine);
		// System.out.println("method ->"+ method);
		this.ResourceChain = parseResourceChain(firstLine);
		// System.out.println("ResourceChain ->" + ResourceChain);
		this.ResourcePath = parseResourcePath();
		/*
		 * for(String s: this.ResourcePath) { System.out.println(s); }
		 */
		this.ResourceName = parseResourceName();
		this.HttpVersion = parseHttpVersion(firstLine);
		// System.out.println("HttpVersion ->" + HttpVersion);

		// Parameter parser
		this.HeaderParameters = parseHeaderParameters();
		/*
		 * for(String k: this.HeaderParameters.keySet()) { System.out.println( k +
		 * " ----> " + this.HeaderParameters.get(k)); }
		 */

		// parse ResourceParameters
		parseResourceParameters(firstLine);
		
		parseBodyMessageParameters();

		this.ContentLength = parseContentLength();

	}

	// private constructor methods

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
		resourceChain = resourceChain.substring(0, resourceChain.indexOf(' '));
		if(resourceChain.length() == 1)
			return "/";
		// resourceChain = resourceChain.substring(0,resourceChain.indexOf("?"));
		return resourceChain;

	}

	private String[] parseResourcePath() {
		
		if(this.ResourceChain.length() == 1) {
			String[] aux = {};
			return aux;
		}
		
		String line = this.ResourceChain;
		String[] tmp;
		String[] toret;

		if (line.indexOf("?") != -1)
			line = line.substring(0, line.indexOf("?"));
		
		if(line.length() == 1) {
			String[] aux = {"/"};
			return aux;
		}

		tmp = line.split("/");
		toret = new String[tmp.length - 1];
		int i = 0;
		for (String s : toret) {
			toret[i] = tmp[i + 1];
			i++;
		}

		return toret;
	}

	private String parseResourceName() {

		if(this.ResourceChain.length() == 1)
			return "";
		
		StringBuilder toret = new StringBuilder();
		int i = 0;
		for (String s : this.ResourcePath) {
			toret.append(s);
			if (i + 1 != this.ResourcePath.length)
				toret.append("/");
			i++;
		}

		return toret.toString();
	}

	private String parseHttpVersion(String line) {
		StringBuilder toret = new StringBuilder("HTTP/");
		if (line.indexOf("HTTP/") != -1) {
			int httpIndex = line.indexOf("HTTP/");
			// Sumo 5 quen son los 5 caracteres de HTTP y 8 = 5 + 3 donde 3 son los 3
			// caracteres de la version
			String version = line.substring(httpIndex + 5, httpIndex + 8);
			toret.append(version);
		}
		return toret.toString();
	}

	private Map<String, String> parseHeaderParameters() {
		Map<String, String> toret = new HashMap<>();
		try {
			String line = buffReader.readLine();
			String[] tmp;
			while (line != null && line.length() != 0) {
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

	private void parseBodyMessageParameters() {
		String line;
		try {
			line = buffReader.readLine();
			
			while(line == null)
				line = buffReader.readLine();
			
			this.content = line;
			String resourceParametersArray[] = line.split("&");

			for (String s : resourceParametersArray) {

				String hashAndValue[] = s.split("=");
				this.ResourceParameters.put(hashAndValue[0], hashAndValue[1]);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private int parseContentLength() {
		for (String s : this.HeaderParameters.keySet()) {

			if (s.equals("Content-Length"))
				return Integer.parseInt(this.HeaderParameters.get("Content-Length"));
		}
		return 0;
	}

	private void parseResourceParameters(String line) {
		// check si tiene ? -> tiene ResourceParameters
		if (line.contains("?")) {

			// cojo el substring de donde empiezan los ResourceParameters
			String ResourceParametersString = line.substring(line.indexOf('?') + 1);
			// quito lo que no son los parametros aka version http
			ResourceParametersString = ResourceParametersString.substring(0, ResourceParametersString.indexOf(' '));
			// System.out.println(ResourceParametersString);
			String resourceParametersArray[] = ResourceParametersString.split("&");

			for (String s : resourceParametersArray) {

				String hashAndValue[] = s.split("=");
				this.ResourceParameters.put(hashAndValue[0], hashAndValue[1]);
			}
		}

	}

	// END private constructor methods

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
		return this.ResourceName;
	}

	public Map<String, String> getResourceParameters() {
		return this.ResourceParameters;
	}

	public String getHttpVersion() {
		return this.HttpVersion;
	}

	public Map<String, String> getHeaderParameters() {
		return this.HeaderParameters;
	}

	public String getContent() {
		return this.content;
	}

	public int getContentLength() {
		return this.ContentLength;
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
