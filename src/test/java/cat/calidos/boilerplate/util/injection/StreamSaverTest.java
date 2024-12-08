package cat.calidos.boilerplate.util.injection;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import cat.calidos.boilerplate.domain.exceptions.SavingException;
import cat.calidos.boilerplate.util.StreamSaver;

@TestInstance(Lifecycle.PER_CLASS)
public class StreamSaverTest {

private String tempPath;

@BeforeAll
public void setup() throws Exception {
	tempPath = setupTempDirectory();
}


@Test
public void testSave() throws Exception {
	var path = tempPath + "stream-save.tmp";
	StreamSaver.nuke(path);
	File f = StreamSaver.save(new ByteArrayInputStream("foo".getBytes()), path);
	f.deleteOnExit();
	String content = FileUtils.readFileToString(f,Charset.defaultCharset());
	assertEquals("foo", content);
	
}


@AfterAll
public void teardown() {
	// not deleting the temp folder by design
}


private String setupTempDirectory() throws Exception {

	tempPath = DaggerConfigPropertyComponent
			.builder()
			.forName("TMP_FOLDER")
			.andDefault("./target/integration-tests-tmp/")
			.build()
			.stringValue()
			.get();
	;
	File tmpFolder = new File(tempPath);
	if (tmpFolder.exists()) {
		if (tmpFolder.isFile()) {
			fail("Temporary folder destination exists and it's not a directory");
		}
	} else {
		FileUtils.forceMkdir(tmpFolder);
	}

	return tempPath;

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
