package org.tbee.xml2yaml;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

public class XML2YAMLTest {

	@Test
	public void sequenceOfScalars_Meta() {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		new XML2YAML().convertMeta(openXML("sequenceOfScalars_Meta"), outputStream);
		String yaml = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
		System.out.println("sequenceOfScalars_Meta:\n" + yaml + "\n==============================");
	}
	@Test
	public void sequenceOfScalars_Semantic() {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		new XML2YAML().convertSemantic(openXML("sequenceOfScalars_Semantic"), outputStream);
		String yaml = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
		System.out.println("sequenceOfScalars_Semantic:\n" + yaml + "\n==============================");
	}

	@Test
	public void mappingScalarsToScalars_Meta() {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		new XML2YAML().convertMeta(openXML("mappingScalarsToScalars_Meta"), outputStream);
		String yaml = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
		System.out.println("mappingScalarsToScalars_Meta:\n" + yaml + "\n==============================");
	}

	@Test
	public void mappingScalarsToSequences_Meta() {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		new XML2YAML().convertMeta(openXML("mappingScalarsToSequences_Meta"), outputStream);
		String yaml = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
		System.out.println("mappingScalarsToSequences_Meta:\n" + yaml + "\n==============================");
	}

	@Test
	public void sequenceOfMappings_Meta() {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		new XML2YAML().convertMeta(openXML("sequenceOfMappings_Meta"), outputStream);
		String yaml = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
		System.out.println("sequenceOfMappings_Meta:\n" + yaml + "\n==============================");
	}

	@Test
	public void fullLengthExample_Meta() {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		new XML2YAML().convertMeta(openXML("fullLengthExample_Meta"), outputStream);
		String yaml = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
		System.out.println("fullLengthExample_Meta:\n" + yaml + "\n==============================");
	}
	@Test
	public void fullLengthExample_Semantic() {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		new XML2YAML().convertSemantic(openXML("fullLengthExample_Semantic"), outputStream);
		String yaml = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
		System.out.println("fullLengthExample_Semantic:\n" + yaml + "\n==============================");
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
