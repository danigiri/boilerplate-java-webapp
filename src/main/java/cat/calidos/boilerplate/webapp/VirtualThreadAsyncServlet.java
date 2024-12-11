package cat.calidos.boilerplate.webapp;

import java.util.concurrent.Executors;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;


/**
 * Async servlet that uses unbounded virtual threads, see superclass to customise behaviour using
 * handlers.
 * 
 * @author daniel giribet
 *///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public class VirtualThreadAsyncServlet extends ThreadAsnycServlet {

@Override
public void init(ServletConfig config) throws ServletException {
	super.init(config);

	executorService = Executors.newVirtualThreadPerTaskExecutor();
	log.info("{}: Initialised virtual thread async servlet", config.getServletName());
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
