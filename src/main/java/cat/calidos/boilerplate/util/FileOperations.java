package cat.calidos.boilerplate.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import cat.calidos.boilerplate.domain.exceptions.SavingException;


/**
 * File utilities:
 * - Save a stream into a file, useful for uploads
 * - remove file
 * 
 * @author daniel giribet
 *//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public class FileOperations {

/**
 * Save a stream to a local file, Important: Needs to be closed, use inside a try with resources
 * `try () {}` block
 * 
 * @param stream
 * @param path
 * @throws SavingException
 */
public static File save(InputStream stream,
						String path)
		throws SavingException {

	var destination = new File(path);
	if (destination.isDirectory()) {
		throw new SavingException("Destination path '" + path + "' is a directory");
	}
	try {
		IOUtils.copy(stream, new FileOutputStream(destination));
	} catch (IOException e) {
		throw new SavingException("Cannot save to '" + path + "'", e);
	}
	return destination;

}


public static boolean nuke(String path) {
	var destination = new File(path);
	if (destination.exists()) {
		destination.delete();
		return true;
	}
	return false;
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
