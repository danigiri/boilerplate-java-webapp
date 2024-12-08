package cat.calidos.boilerplate.webapp;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;


public class SimpleAsyncServlet extends ConfigurableServlet {

protected final static Logger log = LoggerFactory.getLogger(SimpleAsyncServlet.class);

protected static final int		DEFAULT_SHUTDOWN_TIMEOUT_SEC	= 1;
protected static final String	SHUTDOWN_TIMEOUT_NAME			= "__SHUTDOWN_TIMEOUT_SEC";

protected static final int		DEFAULT_REQUEST_TIMEOUT_MS	= 2_000;
protected static final String	REQUEST_TIMEOUT_NAME		= "__REQUEST_TIMEOUT_MS";

protected ExecutorService	executorService;
protected int				requestTimeout;
protected int				shutdownTimeout;

@Override
public void init(ServletConfig config) throws ServletException {
	super.init(config);

	requestTimeout = getConfigInt(properties, REQUEST_TIMEOUT_NAME, DEFAULT_REQUEST_TIMEOUT_MS);
	shutdownTimeout = getConfigInt(properties, REQUEST_TIMEOUT_NAME, DEFAULT_SHUTDOWN_TIMEOUT_SEC);
	String name = config.getServletName();
	log.info("Configuring servlet {} with {}='{}'", name, REQUEST_TIMEOUT_NAME, requestTimeout);

}


@Override
public void destroy() {
	super.destroy();
	executorService.shutdown();
	try {
		if (!executorService.awaitTermination(shutdownTimeout, TimeUnit.SECONDS)) {
			log.error("Did not finish awaiting the completion of pending tasks, forcing shutdown");
			executorService.shutdownNow();
		}
	} catch (InterruptedException e) {
		executorService.shutdownNow();
		log.error("Interrupted awaiting completion of pending tasks, forcing shutdown");
		e.printStackTrace();
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
