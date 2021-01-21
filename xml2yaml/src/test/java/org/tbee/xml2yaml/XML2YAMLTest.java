package org.tbee.xml2yaml;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

public class XML2YAMLTest {

	@Test
	public void sequenceOfScalars() {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		new XML2YAML().convert(openXML("sequenceOfScalars"), outputStream);
		String yaml = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
		System.out.println("sequenceOfScalars:\n" + yaml + "\n==============================");
	}

	@Test
	public void mappingScalarsToScalars() {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		new XML2YAML().convert(openXML("mappingScalarsToScalars"), outputStream);
		String yaml = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
		System.out.println("mappingScalarsToScalars:\n" + yaml + "\n==============================");
	}

	@Test
	public void mappingScalarsToSequences() {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		new XML2YAML().convert(openXML("mappingScalarsToSequences"), outputStream);
		String yaml = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
		System.out.println("mappingScalarsToSequences:\n" + yaml + "\n==============================");
	}

	@Test
	public void sequenceOfMappings() {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		new XML2YAML().convert(openXML("sequenceOfMappings"), outputStream);
		String yaml = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
		System.out.println("sequenceOfMappings:\n" + yaml + "\n==============================");
	}

	@Test
	public void fullLengthExample() {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		new XML2YAML().convert(openXML("fullLengthExample"), outputStream);
		String yaml = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
		System.out.println("fullLengthExample:\n" + yaml + "\n==============================");
	}

	// TODO: test key="..." 
	
	private InputStream openXML(String filename) {
		String name = this.getClass().getSimpleName() + "_"  + filename + ".xml";
		InputStream inputStream = this.getClass().getResourceAsStream(name);
		if (inputStream == null) {
			throw new RuntimeException("Resource not found: " + name);
		}
		return inputStream;
	}
}
