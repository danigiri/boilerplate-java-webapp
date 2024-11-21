package cat.calidos.boilerplate.webapp;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import cat.calidos.boilerplate.model.HelloWorld;


/**
 * @author daniel giribet
 *//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public class HelloWorldAsnycServlet extends HttpServlet {

protected final static Logger log = LoggerFactory.getLogger(HelloWorldAsnycServlet.class);

private static final int TASK_TERMINATION_TIMEOUT = 1;

private ExecutorService	executorService;
private int				timeout;

@Override
public void init(ServletConfig config) throws ServletException {
	super.init(config);

	// the fixed thread pool includes a unbounded queue
	this.executorService = Executors.newFixedThreadPool(2);
	this.timeout = 10000;
}


@Override
protected void doGet(	HttpServletRequest req,
						HttpServletResponse resp)
		throws ServletException, IOException {

	final AsyncContext context = req.startAsync(req, resp);
	context.setTimeout(timeout);
	// async context handling modifies the path info, and adds back the servlet prefix, uncomment
	// this to keep it accessible later
	// req.setAttribute(ORIGINAL_PATH_INFO, req.getPathInfo());
	Future<?> future = executorService.submit(new AsyncRequestHandler(context));
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
		if (!executorService.awaitTermination(TASK_TERMINATION_TIMEOUT, TimeUnit.SECONDS)) {
			log.error("Did not finish awaiting the completion of pending tasks, forcing shutdown");
			executorService.shutdownNow();
		}
	} catch (InterruptedException e) {
		executorService.shutdownNow();
		log.error("Interrupted awaiting completion of pending tasks, forcing shutdown");
		e.printStackTrace();
	}
}




private final class AsyncRequestHandler implements Runnable {

protected final static Logger log = LoggerFactory.getLogger(AsyncRequestHandler.class);

private AsyncContext context;

private AsyncRequestHandler(AsyncContext context) {
	this.context = context;
}


@Override
public void run() {

	HttpServletRequest req = (HttpServletRequest) context.getRequest();
	HttpServletResponse resp = (HttpServletResponse) context.getResponse();
	String secondsParam = req.getParameter("seconds");
	secondsParam = secondsParam==null ? "1" : secondsParam;
	int seconds;
	try {
		seconds = Integer.parseInt(secondsParam);
	}
	catch (NumberFormatException e) {
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
