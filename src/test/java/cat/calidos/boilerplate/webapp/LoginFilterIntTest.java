package cat.calidos.boilerplate.webapp;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;

import org.eclipse.jetty.client.Authentication;
import org.eclipse.jetty.client.BasicAuthentication;
import org.eclipse.jetty.client.ContentResponse;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.Request;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


public class LoginFilterIntTest {

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
public void testAuthenticated() throws Exception {
	URI uri = URI.create("http://localhost:8080/hello-auth/");
	Authentication.Result authn = new BasicAuthentication.BasicResult(uri, "configure", "me");
	Request request = httpClient.newRequest(uri);
	authn.apply(request);
	ContentResponse resp = request.send();
	assertEquals(200, resp.getStatus());
	assertEquals("hello static world", resp.getContentAsString().trim());
	assertEquals("text/plain", resp.getMediaType());
}


@Test
public void testNotAuthenticated() throws Exception {
	URI uri = URI.create("http://localhost:8080/hello-auth/");
	Authentication.Result authn = new BasicAuthentication.BasicResult(uri, "foo", "bar");
	Request request = httpClient.newRequest(uri);
	authn.apply(request);
	ContentResponse resp = request.send();
	assertEquals(403, resp.getStatus());
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
