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
package es.uvigo.esei.dai.hybridserver.week2;

import static es.uvigo.esei.dai.hybridserver.utils.TestUtils.deleteStatus;
import static es.uvigo.esei.dai.hybridserver.utils.TestUtils.extractUUIDFromText;
import static es.uvigo.esei.dai.hybridserver.utils.TestUtils.getContent;
import static es.uvigo.esei.dai.hybridserver.utils.TestUtils.getStatus;
import static es.uvigo.esei.dai.hybridserver.utils.TestUtils.postContent;
import static es.uvigo.esei.dai.hybridserver.utils.TestUtils.postStatus;
import static java.util.Collections.singletonMap;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import es.uvigo.esei.dai.hybridserver.HybridServer;
import es.uvigo.esei.dai.hybridserver.utils.HybridServerTestCase;

public class ClientRequetsTest extends HybridServerTestCase {
	private String invalidUUID;
	private String[][] pages;

	@Before
	public void setup() {
		this.invalidUUID = "12345678-abcd-1234-ab12-9876543210ab";
	}

	// Ejercicio 2
	@Test
	public void testGetHtmlPage() throws IOException {
		for (String[] page : pages) {
			final String uuid = page[0];
			final String content = page[1];

			final String pageURL = url + "html?uuid=" + uuid;

			assertThat(getContent(pageURL), containsString(content));
		}
	}


	// Ejercicio 3
	@Test
	public void testGetHtmlList() throws IOException {

		final String pageURL = url + "html";
		final String content = getContent(pageURL);

		for (String[] page : pages) {
			final String uuid = page[0];

			assertThat(content, containsString(uuid));
		}
	}


	// Ejercicio 4
	@Test
	public void testPost() throws IOException {
		final String content = "<html><body>Testing POST</body></html>";

		// Envío de la página y extracción del uuid de la nueva página
		final String responseContent = postContent(url + "html", singletonMap("html", content));
		final String uuid = extractUUIDFromText(responseContent);
		assertThat(uuid, is(notNullValue()));

		// Verificación de que la página de respuesta contiene un enlace a la nueva página
		final String uuidHyperlink = "<a href=\"html?uuid=" + uuid + "\">" + uuid + "</a>";
		assertThat(responseContent, containsString(uuidHyperlink));

		// Recuperación de la nueva página
		final String url = this.url + "html?uuid=" + uuid;
		assertThat("The new page couldn't be retrieved", getContent(url), is(equalTo(content)));
	}

	@Test
	public void testDelete() throws IOException {
		final String uuid = pages[4][0];
		final String url = this.url + "html?uuid=" + uuid;

		assertThat("The page couldn't be deleted", deleteStatus(url), is(equalTo(200)));

		assertThat("The page already exists", getStatus(url), is(equalTo(404)));
	}


	// Ejercicio 5
	@Test
	public void testGetInvalidHtmlPage() throws IOException {
		final String pageURL = url + "html?uuid=" + invalidUUID;

		assertThat(getStatus(pageURL), is(equalTo(404)));
	}

	@Test
	public void testGetInvalidResource() throws IOException {
		final String pageURL = url + "xxx?uuid=" + pages[0];

		assertThat(getStatus(pageURL), is(equalTo(400)));
	}

	@Test
	public void testDeleteNonexistentPage() throws IOException {
		final String pageURL = this.url + "html?uuid=" + invalidUUID;

		assertThat(deleteStatus(pageURL), is(equalTo(404)));
	}

	@Test
	public void testPostInvalidContent() throws IOException {
		final String content = "<html><body>Testing POST</body></html>";

		assertThat(postStatus(url + "html", singletonMap("xxx", content)), is(equalTo(400)));
	}
}
