package cat.calidos.boilerplate.model;

import java.time.Duration;


public class HelloWorld {

public String greeting() {
	return "hello instance world";
}


public static String staticGreeting() {
	return "hello static world";
}


public String greetingWithParam(String what) {
	return "hello " + what;
}


public String sleepAndGreet(Duration duration) {
	try {
		Thread.sleep(duration);
	} catch (InterruptedException e) {
		e.printStackTrace();
		return "slept but was interrupted and hello world";
	}
	return "slept for " + duration.toSeconds() + " seconds... hello world";
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
