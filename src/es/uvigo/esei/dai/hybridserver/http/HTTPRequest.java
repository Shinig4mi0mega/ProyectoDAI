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
import java.util.Map;

public class HTTPRequest {
	public BufferedReader buffReader;

	public HTTPRequest(Reader reader) throws IOException, HTTPParseException {

		buffReader = new BufferedReader(reader);
	}

	public HTTPRequestMethod getMethod() {
		try {
			String method = buffReader.readLine().substring(0, 6);

			if (method.contains("HEAD"))
				return HTTPRequestMethod.HEAD;

			if (method.contains("GET"))
				return HTTPRequestMethod.GET;

			if (method.contains("POST"))
				return HTTPRequestMethod.POST;

			if (method.contains("PUT"))
				return HTTPRequestMethod.PUT;

			if (method.contains("DELETE"))
				return HTTPRequestMethod.DELETE;

			if (method.contains("TRACE"))
				return HTTPRequestMethod.TRACE;

			if (method.contains("OPTIONS"))
				return HTTPRequestMethod.OPTIONS;

			if (method.contains("CONNECT"))
				return HTTPRequestMethod.CONNECT;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public String getResourceChain() {
		String ResourceChain = null;
		try {
			ResourceChain = buffReader.readLine();
			ResourceChain = ResourceChain.substring(ResourceChain.indexOf('/'));
			ResourceChain = ResourceChain.substring(0,ResourceChain.indexOf(' '));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ResourceChain;
	}

	public String[] getResourcePath() {
		String resourceChain;
		String[] splitedResourceChain;
		resourceChain = this.getResourceChain();
		resourceChain = resourceChain.substring(0,resourceChain.indexOf("?"));
		splitedResourceChain = resourceChain.split("/");
		//resourcePath = splitedResourceChain[splitedResourceChain.length]; Pensaba que era asi
		return splitedResourceChain;
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
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, String> getHeaderParameters() {
		// TODO Auto-generated method stub
		return null;
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
