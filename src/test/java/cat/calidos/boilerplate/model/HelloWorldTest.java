package cat.calidos.boilerplate.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import org.mockito.MockedStatic;


public class HelloWorldTest {

@Test
public void testGreeting() {
	assertEquals("hello static world", HelloWorld.staticGreeting());
	assertEquals("hello instance world", new HelloWorld().greeting());
	assertEquals("hello param world", new HelloWorld().greetingWithParam("param world"));
	var duration = Duration.ofSeconds(1);
	assertEquals("slept for 1 seconds... hello world", new HelloWorld().sleepAndGreet(duration));
}


@Test
public void testGreetingWithMocking() {

	HelloWorld mocked = mock();
	when(mocked.greeting()).thenReturn("mocked instance world");
	assertEquals("mocked instance world", mocked.greeting());
	verify(mocked).greeting(); // verify was called

	HelloWorld mocked2 = mock();
	when(mocked2.greetingWithParam(anyString())).thenReturn("foo");
	assertEquals("foo", mocked2.greetingWithParam("bar"));
}


@Test
public void testStaticGreetingWithMocking() {

	try (MockedStatic<HelloWorld> mocked = mockStatic(HelloWorld.class)) {
		mocked.when(HelloWorld::staticGreeting).thenReturn("mocked static world");
		assertEquals("mocked static world", HelloWorld.staticGreeting());
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
