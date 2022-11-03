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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Launcher {
	public static void main(String[] args) {
		if (args[1] != null) {
			System.out.println("Too many arguments");
		}
		HybridServer server = null;
		if (args[0] != null) {
			try {
				InputStream input = new FileInputStream(args[0]);
				Properties prop = new Properties();
				prop.load(input);
				server = new HybridServer(prop);
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}else {
			server = new HybridServer();
		}
		server.start();
	}
}
