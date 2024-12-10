package cat.calidos.boilerplate.webapp;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.client.ContentResponse;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.Request;
import org.eclipse.jetty.client.StringRequestContent;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


public class PostSaverServletIntTest {

private static HttpClient httpClient;

@BeforeAll
public static void beforeAll() throws Exception {
	// Instantiate HttpClient.
	httpClient = new HttpClient();

	// Configure HttpClient, for example:
	httpClient.setFollowRedirects(false);

	// Start HttpClient.
	httpClient.start();
}


@Test
public void testPost() throws Exception {
	var content = "foo"+Math.random();
	ContentResponse resp = httpClient
			.POST("http://localhost:8080/save/")
			.body(new StringRequestContent(content))
			.send();
	assertEquals(200, resp.getStatus());
	assertEquals("Content saved",resp.getContentAsString().trim());

	File file = new File("./target/foo.txt");
	assertTrue(file.exists());
	assertEquals(content, FileUtils.readFileToString(file, Charset.defaultCharset()));
}


@AfterAll
public static void afterAll() throws Exception {
	if (httpClient != null) {
		httpClient.close();
	}
}
}
/*
 * Copyright 2024 Daniel Giribet
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
