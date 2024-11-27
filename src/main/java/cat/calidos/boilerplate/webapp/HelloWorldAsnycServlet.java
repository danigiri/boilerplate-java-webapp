package cat.calidos.boilerplate.webapp;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.AsyncEvent;
import jakarta.servlet.AsyncListener;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import cat.calidos.boilerplate.model.HelloWorld;
import cat.calidos.boilerplate.util.injection.DaggerConfigPropertyComponent;


/**
 * Overall servlet, change HelloAsyncRequestHandler to provide custom logic and the
 * HelloAsyncListener to do timeout logic.
 * 
 * @author daniel giribet
 *//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public class HelloWorldAsnycServlet extends HttpServlet {

protected final static Logger log = LoggerFactory.getLogger(HelloWorldAsnycServlet.class);

private static final int	DEFAULT_THREAD_COUNT		= 2;
private static final int	SHUTDOWN_TIMEOUT_SEC		= 1;
private static final int	DEFAULT_REQUEST_TIMEOUT_MS	= 2_000;

private static final String	THREAD_COUNT_NAME		= "__ASYNC_THREAD_COUNT";
private static final String	REQUEST_TIMEOUT_NAME	= "__REQUEST_TIMEOUT_MS";

private ExecutorService	executorService;
private int				timeout;

@Override
public void init(ServletConfig config) throws ServletException {
	super.init(config);

	Properties p = extractParameters(config);
	int threadPoolCount = getConfigInt(p, THREAD_COUNT_NAME, DEFAULT_THREAD_COUNT);
	this.executorService = Executors.newFixedThreadPool(threadPoolCount);
	this.timeout = getConfigInt(p, REQUEST_TIMEOUT_NAME, DEFAULT_REQUEST_TIMEOUT_MS);
	;
	log.info("Initialised async servlet – {}", HelloWorldAsnycServlet.class.getName());
}


@Override
protected void doGet(	HttpServletRequest req,
						HttpServletResponse resp)
		throws ServletException, IOException {

	final AsyncContext context = req.startAsync(req, resp);
	context.addListener(new HelloAsyncListener());
	context.setTimeout(timeout); // this will interrupt the thread running the runnable
	// async context handling modifies the path info, and adds back the servlet prefix, uncomment
	// this to keep it accessible later
	// req.setAttribute(ORIGINAL_PATH_INFO, req.getPathInfo());

	// the fixed thread pool itself includes a unbounded queue, which means handlers will get queued
	// and de-queued automagically as threads become available
	String pathInfo = req.getPathInfo();
	int contexHash = context.hashCode();
	log.debug("Handling async request '{}' with context '{}'", pathInfo, contexHash);
	Future<?> future = executorService.submit(new HelloAsyncRequestHandler(context));
}


@Override
protected void doPost(	HttpServletRequest req,
						HttpServletResponse resp)
		throws ServletException, IOException {
	doGet(req, resp);
}


@Override
public void destroy() {
	super.destroy();
	executorService.shutdown();
	try {
		if (!executorService.awaitTermination(SHUTDOWN_TIMEOUT_SEC, TimeUnit.SECONDS)) {
			log.error("Did not finish awaiting the completion of pending tasks, forcing shutdown");
			executorService.shutdownNow();
		}
	} catch (InterruptedException e) {
		executorService.shutdownNow();
		log.error("Interrupted awaiting completion of pending tasks, forcing shutdown");
		e.printStackTrace();
	}
}


private Properties extractParameters(ServletConfig config) {
	var p = new Properties();
	ServletContext context = config.getServletContext();
	Enumeration<String> names = context.getInitParameterNames();
	addParameters(context, p, names);
	names = config.getInitParameterNames();
	addParameters(config, p, names);
	return p;
}


private void addParameters(	ServletContext context,
							Properties p,
							Enumeration<String> names) {
	while (names.hasMoreElements()) {
		String name = names.nextElement();
		String value = context.getInitParameter(name);
		if (value != null) {
			p.setProperty(name, value);
		}
	}
}


private void addParameters(	ServletConfig config,
							Properties p,
							Enumeration<String> names) {
	while (names.hasMoreElements()) {
		String name = names.nextElement();
		String value = config.getInitParameter(name);
		if (value != null) {
			p.setProperty(name, value);
		}
	}
}


private int getConfigInt(	Properties p,
							String key,
							int defaultValue) {
	Integer value = DaggerConfigPropertyComponent
			.builder()
			.withProps(p)
			.forName(key)
			.andDefault(defaultValue)
			.build()
			.integerValue()
			.get();
	log.info("Configuring async servlet for {} with '{}'", key, value);
	return value;
}

private final class HelloAsyncRequestHandler implements Runnable {

protected final static Logger log = LoggerFactory.getLogger(HelloAsyncRequestHandler.class);

private AsyncContext context;

private HelloAsyncRequestHandler(AsyncContext context) {
	this.context = context;
}


@Override
public void run() {

	HttpServletRequest req = (HttpServletRequest) context.getRequest();
	HttpServletResponse resp = (HttpServletResponse) context.getResponse();
	String secondsParam = req.getParameter("seconds");
	secondsParam = secondsParam == null ? "1" : secondsParam;
	int seconds;
	try {
		seconds = Integer.parseInt(secondsParam);
	} catch (NumberFormatException e) {
		log.warn("Wrong seconds param '{}', using default", secondsParam);
		seconds = 1;
	}
	try {
		String message = new HelloWorld().sleepAndGreet(Duration.ofSeconds(seconds));
		resp.getWriter().write(message);
	} catch (IOException e) {
		log.error("Could not write message due to " + e.getMessage(), e);
		e.printStackTrace();
	}
	context.complete();
}

}

private class HelloAsyncListener implements AsyncListener {

protected final static Logger log = LoggerFactory.getLogger(HelloAsyncListener.class);

@Override
public void onComplete(AsyncEvent event) throws IOException {
}


@Override
public void onTimeout(AsyncEvent event) throws IOException {
	AsyncContext context = event.getAsyncContext();
	HttpServletResponse response = (HttpServletResponse) context.getResponse();
	String path = ((HttpServletRequest) context.getRequest()).getPathInfo();
	log.error("Request '{}' with context '{}' timeout", path, context.hashCode());
	PrintWriter writer = response.getWriter();
	writer.write(new HelloWorld().greetingWithParam("timed out world"));
	writer.flush();
}


@Override
public void onError(AsyncEvent event) throws IOException {
}


@Override
public void onStartAsync(AsyncEvent event) throws IOException {
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
