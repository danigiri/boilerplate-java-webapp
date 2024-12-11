package cat.calidos.boilerplate.cli;

import java.util.concurrent.Callable;

import cat.calidos.boilerplate.model.HelloWorld;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;


/**
 * @author daniel giribet
 *//////////////////////////////////////////////////////////////////////////////////////////////////
@Command(name = "HelloWorldCLI", version = "HelloWorldCLI 0.0.1", mixinStandardHelpOptions = true)
public class HelloWorldCLI implements Callable<Integer> {

@Option(names = { "-e", "--stderr" }, description = "print on stderr instead")
boolean stderr = false;

//@Option(names = { "-q", "--quiet" }, description = "do not print anything")
//boolean quiet = false;

@Parameters(description = "message to be printed")
String message;


@Override
public Integer call() throws Exception {
	String finalMessage = new HelloWorld().greetingWithParam(message);
	if (!stderr) {
		System.out.println(finalMessage);
	} else {
		System.err.println(finalMessage);
	}
	return 0;
}


public static void main(String[] args) {
	int exitCode = new CommandLine(new HelloWorldCLI()).execute(args);
	System.exit(exitCode);
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
