package cat.calidos.boilerplate.webapp;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import cat.calidos.boilerplate.model.HelloWorld;


public class HelloWorldServlet extends ConfigurableServlet {

protected final static Logger log = LoggerFactory.getLogger(HelloWorldServlet.class);

@Override
public void init(ServletConfig config) throws ServletException {
	super.init(config);
	log.info("{}: Initialised regular servlet", config.getServletName());
}


protected void doGet(	HttpServletRequest request,
						HttpServletResponse response)
		throws ServletException, IOException {
	log.debug("Handling sync request '{}'", request.getPathInfo());
	response.setContentType("text/plain");
	response.setStatus(HttpServletResponse.SC_OK);
	response.getWriter().println(HelloWorld.staticGreeting());
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
