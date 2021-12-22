package org.tbee.xml2yaml;

/*-
 * #%L
 * XML2XXX
 * %%
 * Copyright (C) 2020 - 2021 Tom Eugelink
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

public class XML2YAMLTest {
	private static final Logger logger = LoggerFactory.getLogger(XML2YAMLTest.class);

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
		Assert.assertEquals(4, map.size());
		Assert.assertEquals("Sammy Sosa", map.get("name"));
		Assert.assertEquals(63, map.get("hr"));
		Assert.assertEquals(0.288, map.get("avg"));
		Assert.assertTrue(map.containsKey("enum"));
		List<Object> enumList = (List<Object>) map.get("enum");
		System.out.println(enumList);
		Assert.assertEquals("important", enumList.get(0));
	}

	@Test
	public void fullLengthExample() {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) convert("fullLengthExample");

		Assert.assertEquals("Late afternoon is best. Backup contact is Nancy Billsmer @ 338-4338.\n", map.get("comments"));

		@SuppressWarnings("unchecked")
		Map<String, Object> billToMap = (Map<String, Object>) map.get("bill-to");
		
		@SuppressWarnings("unchecked")
		Map<String, Object> addressMap = (Map<String, Object>) billToMap.get("address");
		Assert.assertEquals("458 Walkman Dr.\nSuite #292\n", addressMap.get("lines"));
	}


	@Test
	public void kubernetes1() {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) convert("kubernetes1");
	}

	// TODO: test key="..." 

	private Object convert(String testname) {
		
		// open input
		String resourceName = this.getClass().getSimpleName() + "_"  + testname + ".xml";
		InputStream inputStream = this.getClass().getResourceAsStream(resourceName);
		if (inputStream == null) {
			throw new RuntimeException("Resource not found: " + resourceName);
		}
		
		// convert
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		new XML2YAML().convert(inputStream, outputStream);
		
		// parse as yaml
		String yamlStr = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
		logger.trace(resourceName + ":\n" + yamlStr);
		Yaml yaml = new Yaml();
		Object obj = yaml.load(new ByteArrayInputStream(yamlStr.getBytes()));		
		logger.trace(obj + "\n==============================");
		return obj;
	}
}
