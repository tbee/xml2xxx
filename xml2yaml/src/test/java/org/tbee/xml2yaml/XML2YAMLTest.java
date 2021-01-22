package org.tbee.xml2yaml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

public class XML2YAMLTest {

	@Test
	public void sequenceOfScalars() {
		@SuppressWarnings("unchecked")
		List<Object> list = (List<Object>) convert("sequenceOfScalars");
		Assert.assertEquals(3, list.size());
		Assert.assertEquals("Mark McGwire", list.get(0));
		Assert.assertEquals("Ken Griffey", list.get(2));
	}

	@Test
	public void mappingScalarsToScalars() {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) convert("mappingScalarsToScalars");
		Assert.assertEquals(3, map.size());
		Assert.assertEquals(65, map.get("hr"));
		Assert.assertEquals(0.278, map.get("avg"));
		Assert.assertEquals(147, map.get("rbi"));
	}

	@Test
	public void mappingScalarsToSequences() {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) convert("mappingScalarsToSequences");
		Assert.assertEquals(2, map.size());
		
		@SuppressWarnings("unchecked")
		List<String> americanList = (List<String>) map.get("american");
		Assert.assertEquals(3, americanList.size());
		Assert.assertEquals("Detroit Tigers", americanList.get(1));
	}

	@Test
	public void sequenceOfMappings() {
		@SuppressWarnings("unchecked")
		List<Object> list = (List<Object>) convert("sequenceOfMappings");
		Assert.assertEquals(2, list.size());
		
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) list.get(1);
		Assert.assertEquals(3, map.size());
		Assert.assertEquals("Sammy Sosa", map.get("name"));
		Assert.assertEquals(63, map.get("hr"));
		Assert.assertEquals(0.288, map.get("avg"));
	}

	@Test
	public void fullLengthExample() {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) convert("fullLengthExample");

		Assert.assertEquals("Late afternoon is best. Backup contact is Nancy Billsmer @ 338-4338.", map.get("comments"));

		@SuppressWarnings("unchecked")
		Map<String, Object> billToMap = (Map<String, Object>) map.get("bill-to");
		
		@SuppressWarnings("unchecked")
		Map<String, Object> addressMap = (Map<String, Object>) billToMap.get("address");
		Assert.assertEquals("458 Walkman Dr.\nSuite #292\n", addressMap.get("lines"));
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
