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

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class HTTPResponse {
	
	HTTPResponseStatus status;
	String version;
	String content;
	Map<String, String> parameters;
	
	
	public HTTPResponse() {
		this.status = HTTPResponseStatus.S500;
		this.version = "HTTP/1.1";
		this.content = "";
		this.parameters = new HashMap<String, String>();
	}

	public HTTPResponseStatus getStatus() {
		return this.status;
	}

	public void setStatus(HTTPResponseStatus status) {
		this.status = status;
	}

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Map<String, String> getParameters() {
		return this.parameters;
	}

	public String putParameter(String name, String value) {
		return parameters.put(name, value);
	}

	public boolean containsParameter(String name) {
		return parameters.containsKey(name);
	}

	public String removeParameter(String name) {
		return parameters.remove(name);
	}

	public void clearParameters() {
		this.parameters = null;
	}

	public List<String> listParameters() {
		List<Entry<String, String>> temp = parameters.entrySet().stream().collect(Collectors.toList());//Creamos una lista de entradas de Map
		List<String> toret = new LinkedList<String>();
		for(int i = 0; i < temp.size(); i++) {
			toret.add(temp.get(i).toString().replace("=", ": "));//Cambiamos el formato de cada par para adecuarse a HTTP
		}
		return toret;
	}

	public void print(Writer writer) throws IOException {
		String temp = new String(); //Para construir Strings de cara a la salida
		
		temp = this.getVersion();
		temp += " ";
		temp += Integer.toString(this.getStatus().getCode());
		temp += " ";
		temp += this.getStatus().getStatus();
		
		writer.write(temp);
		
		if(this.getContent().length() > 0) {
			temp = "\r\nContent-Length: ";
			temp += Integer.toString(this.getContent().length());
			writer.write(temp);
		}
		for(int i = 0; i < this.listParameters().size(); i++) {//Bucle para cada parametro insertado
			temp = " ";
			temp += this.listParameters().get(i);
			writer.write(temp);
		}
		temp = "\r\n\r\n";//Salto de linea entre parametros y contenido
		if(this.getContent().length() > 0) {
			temp += this.getContent();
		}
		writer.write(temp);
		writer.flush();
	}

	@Override
	public String toString() {
		final StringWriter writer = new StringWriter();

		try {
			this.print(writer);
		} catch (IOException e) {
		}

		return writer.toString();
	}
}
