package org.tbee.xml2yaml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

public class XML2YAMLTest {

	@Test
	public void sequenceOfScalars() {
		Object object = convert("sequenceOfScalars");
	}

	@Test
	public void mappingScalarsToScalars() {
		Object object = convert("mappingScalarsToScalars");
	}

	@Test
	public void mappingScalarsToSequences() {
		Object object = convert("mappingScalarsToSequences");
	}

	@Test
	public void sequenceOfMappings() {
		Object object = convert("sequenceOfMappings");
	}

	@Test
	public void fullLengthExample() {
		Object object = convert("fullLengthExample");
	}

	// TODO: test key="..." 

	private Object convert(String filename) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		new XML2YAML().convert(openXML(filename), outputStream);
		String yamlStr = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
		Yaml yaml = new Yaml();
		Object obj = yaml.load(new ByteArrayInputStream(yamlStr.getBytes()));
		System.out.println(filename + ":\n" + yamlStr
				+ "\n" + obj
				+ "\n==============================");
		return obj;
	}
	private InputStream openXML(String filename) {
		String name = this.getClass().getSimpleName() + "_"  + filename + ".xml";
		InputStream inputStream = this.getClass().getResourceAsStream(name);
		if (inputStream == null) {
			throw new RuntimeException("Resource not found: " + name);
		}
		return inputStream;
	}
}
