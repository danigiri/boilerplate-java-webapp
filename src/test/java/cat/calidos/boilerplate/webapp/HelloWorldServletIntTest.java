package cat.calidos.boilerplate.webapp;

import static org.junit.jupiter.api.Assertions.*;

import org.eclipse.jetty.client.ContentResponse;
import org.eclipse.jetty.client.HttpClient;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


public class HelloWorldServletIntTest {

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
public void testGreeting() throws Exception {

	var response = get();
	assertEquals(200, response.status());
	assertEquals("hello static world", response.content().trim());

}

private record HelloWorldServletIntTestResponse(String content, int status) {
};

private static HelloWorldServletIntTestResponse get() throws Exception {

	ContentResponse response = httpClient.GET("http://localhost:8080/hello");
	String content = response.getContentAsString();
	int status = response.getStatus();

	// ContentResponse response = httpClient.POST("http://domain.com/entity/1")
	// .param("p", "value")
	// .send();
	return new HelloWorldServletIntTestResponse(content, status);

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
