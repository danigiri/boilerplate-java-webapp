package cat.calidos.boilerplate.webapp;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cat.calidos.boilerplate.model.HelloWorld;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.AsyncEvent;
import jakarta.servlet.AsyncListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

final class HelloAsyncRequestHandler implements AsyncRequestHandlerRunnable {

protected final static Logger log = LoggerFactory.getLogger(HelloAsyncRequestHandler.class);

private AsyncContext context;

HelloAsyncRequestHandler(AsyncContext context) {
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

public  AsyncListener getAsyncListener() {
	return new HelloAsyncListener();
}


private class HelloAsyncListener implements AsyncListener {

protected final static Logger log = LoggerFactory.getLogger(HelloAsyncListener.class);


public HelloAsyncListener() {
}


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

