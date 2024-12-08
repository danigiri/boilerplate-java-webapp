package cat.calidos.boilerplate.webapp;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


/**
 * Overall servlet, change HelloAsyncRequestHandler to provide custom logic and the
 * HelloAsyncListener to do timeout logic.
 * 
 * @author daniel giribet
 *//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public class ThreadAsnycServlet extends SimpleAsyncServlet {

protected final static Logger log = LoggerFactory.getLogger(ThreadAsnycServlet.class);

private static final int	DEFAULT_THREAD_COUNT	= 2;
private static final String	THREAD_COUNT_NAME		= "__ASYNC_THREAD_COUNT";

@Override
public void init(ServletConfig config) throws ServletException {
	super.init(config);

	int threadPoolCount = getConfigInt(properties, THREAD_COUNT_NAME, DEFAULT_THREAD_COUNT);
	log.info("Configuring async servlet with {}='{}'", THREAD_COUNT_NAME, threadPoolCount);
	executorService = Executors.newFixedThreadPool(threadPoolCount);
	log.info("Initialised async servlet – {}", ThreadAsnycServlet.class.getName());
}


@Override
protected void doGet(	HttpServletRequest req,
						HttpServletResponse resp)
		throws ServletException, IOException {

	final AsyncContext context = req.startAsync(req, resp);
	AsyncRequestHandlerRunnable handler = new HelloAsyncRequestHandler(context);
	context.addListener(handler.getAsyncListener());
	context.setTimeout(requestTimeout); // this will interrupt the thread running the runnable
	// async context handling modifies the path info, and adds back the servlet prefix, uncomment
	// this to keep it accessible later
	// req.setAttribute(ORIGINAL_PATH_INFO, req.getPathInfo());

	// the fixed thread pool itself includes a unbounded queue, which means handlers will get queued
	// and de-queued automagically as threads become available
	String pathInfo = req.getPathInfo();
	int contexHash = context.hashCode();
	log.debug("Handling async request '{}' with context '{}'", pathInfo, contexHash);
	Future<?> future = executorService.submit(handler);
}


@Override
protected void doPost(	HttpServletRequest req,
						HttpServletResponse resp)
		throws ServletException, IOException {
	doGet(req, resp);
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
