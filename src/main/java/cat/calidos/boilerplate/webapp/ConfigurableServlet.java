package cat.calidos.boilerplate.webapp;

import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;

import cat.calidos.boilerplate.util.injection.DaggerConfigPropertyComponent;

/** Simple configurable servlet
*	@author daniel giribet
*///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public class ConfigurableServlet extends HttpServlet {

Properties properties;

@Override
public void init(ServletConfig config) throws ServletException {
	super.init(config);
	properties = extractParameters(config);
}

protected Properties extractParameters(ServletConfig config) {
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


protected int getConfigInt(	Properties p,
							String key,
							int defaultValue) {
	return DaggerConfigPropertyComponent
			.builder()
			.withProps(p)
			.forName(key)
			.andDefault(defaultValue)
			.build()
			.integerValue()
			.get();
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

