package cat.calidos.boilerplate.webapp;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Enumeration;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import cat.calidos.boilerplate.util.injection.DaggerConfigPropertyComponent;


/**
 * This is a filter to require basic authentication to proceed
 * 
 * @author daniel giribet
 *///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public class LoginFilter implements Filter {

protected final static Logger log = LoggerFactory.getLogger(LoginFilter.class);

private static final String	AUTH_HEADER		= "Authorization";
private static final String	AUTH_CREDS_NAME	= "__AUTH_CREDS";

private String	user;
private String	password;
private boolean	alwaysFail;

@Override
public void init(FilterConfig filterConfig) throws ServletException {
	var p = new Properties();
	Enumeration<String> names = filterConfig.getInitParameterNames();
	while (names.hasMoreElements()) {
		String name = names.nextElement();
		p.put(name, filterConfig.getInitParameter(name));
	}
	String creds = DaggerConfigPropertyComponent
			.builder()
			.withProps(p)
			.forName(AUTH_CREDS_NAME)
			.build()
			.stringValue() // note we do not supply a default value
			.orElseGet(() -> null);
	alwaysFail = creds == null || creds.isBlank();
	if (alwaysFail) {
		log.error("Undefined or blank credentials, all requests will be denied");
	} else {
		UserPassword up = extractUserPassword(creds);
		if (up == null) {
			log.error("Credentials should be <user>:<password>, all requests will be denied");
		} else {
			user = up.user;
			password = up.password;
			log.info("Configured authentication creds **********:*********");
		}
	}
}


@Override
public void doFilter(	ServletRequest request,
						ServletResponse response,
						FilterChain chain)
		throws IOException, ServletException {
	HttpServletRequest req = (HttpServletRequest) request;
	String headerValue = req.getHeader(AUTH_HEADER);
	String pathInfo = req.getPathInfo();
	if (!alwaysFail && validateToken(headerValue, pathInfo)) {
		// all is good, continue with request
		log.debug("Authorising request {}", pathInfo);
		chain.doFilter(request, response);
	} else {
		log.debug("Denying request {}", pathInfo);
		// Stop the filter chain and send a custom response
		HttpServletResponse resp = (HttpServletResponse) response;
		resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
		// resp.addHeader("WWW-Authenticate", "Basic realm=\"\"");

	}

}


private boolean validateToken(	String headerValue,
								String pathInfo) {
	// we decode the value, only accepting basic authentication
	try {
		String[] headerTokens = headerValue.split(" ");
		if (headerTokens.length != 2) {
			return false;
		}
		if (!headerTokens[0].equalsIgnoreCase("Basic")) {
			return false;
		}
		String decodedHeaderValue = new String((Base64.getDecoder().decode(headerTokens[1])),
				Charset.forName("UTF-8"));
		UserPassword up = extractUserPassword(decodedHeaderValue);
		if (up == null) {
			return false;
		}
		return user.equals(up.user) && password.equals(up.password);
	} catch (Exception e) {
		log.debug("Incorrect basic auth credentials for {}", pathInfo);
		return false;
	}
}

private record UserPassword(String user, String password) {
}

private UserPassword extractUserPassword(String creds) {
	String[] userPassword = creds.split(":");
	if (userPassword.length != 2) {
		return null;
	}
	return new UserPassword(userPassword[0], userPassword[1]);
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
