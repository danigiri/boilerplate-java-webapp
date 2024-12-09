package cat.calidos.boilerplate.webapp;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cat.calidos.boilerplate.domain.exceptions.SavingException;
import cat.calidos.boilerplate.util.FileOperations;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public class PostSaveServlet extends ConfigurableServlet {

protected final static Logger log = LoggerFactory.getLogger(PostSaveServlet.class);

protected static final String	DEFAULT_SAVE_PREFIX	= "/tmp/";
protected static final String	SAVE_PREFIX			= "__SAVE_PREFIX";
protected static final String	DEFAULT_FILENAME	= "foo.txt";
protected static final String	SAVE_FILENAME		= "__SAVE_FILENAME";

private String path;

@Override
public void init(ServletConfig config) throws ServletException {
	super.init(config);
	String name = config.getServletName();
	String pref = getConfigStr(properties, SAVE_PREFIX, DEFAULT_SAVE_PREFIX);
	String filename = getConfigStr(properties, SAVE_FILENAME, DEFAULT_FILENAME);
	path = pref+filename;
	log.info("{}: Configuring with path='{}'", name, path);
	log.info("{}: Initialised regular servlet", name);

}


@Override
protected void doPost(	HttpServletRequest request,
						HttpServletResponse response)
		throws ServletException, IOException {
	log.debug("Handling sync post request '{}'", request.getPathInfo());
	response.setContentType("text/plain");
	response.setStatus(HttpServletResponse.SC_OK);
	try {
		FileOperations.save(request.getInputStream(), path);
		response.getWriter().println("Content saved");
	} catch (SavingException e) {
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		response.getWriter().println("Content not saved ("+e.getMessage()+")");
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
