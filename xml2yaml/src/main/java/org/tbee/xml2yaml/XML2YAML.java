package org.tbee.xml2yaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * https://yaml.org/spec/1.2/spec.html
 * 
 * https://www.reddit.com/r/ansible/comments/5jhff3/when_to_use_dash_in_yaml/
 *   - symbol denotes the start of a new array item
 *
 * https://stackoverflow.com/questions/15540635/what-is-the-use-of-the-pipe-symbol-in-yaml
 *   | symbol maintains newlines, with the value following determines how many spaces to strip
 *   > symbol replaces newlines with a space
 *   
 * TODO
 * - multiline strings
 */
public class XML2YAML {

	public void convert(InputStream inputStream, OutputStream outputStream) {
		// https://www.baeldung.com/java-stax
		try {
			Writer writer = new OutputStreamWriter(outputStream,"UTF-8");
			int indent = 0;
			boolean contentElement = false;
			
			XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
			XMLEventReader reader = xmlInputFactory.createXMLEventReader(inputStream);
			while (reader.hasNext()) {
			    XMLEvent nextEvent = reader.nextEvent();
			    
			    if (nextEvent.isStartElement()) {
			        StartElement startElement = nextEvent.asStartElement();
			        String name = startElement.getName().getLocalPart();
					String id = attr(startElement, "id");
					String key = attr(startElement, "key");
					String ref = attr(startElement, "ref");
					
		        	String idPrefix = (id == null ? "" : indent(indent) + id + ": ");
		        	if (key != null) {
		        		idPrefix += "&" + key;
		        	}
		        	if (ref != null) {
		        		idPrefix += "*" + ref;
		        	}
					switch (name) {
			        	case "grp"  -> { writer.append(idPrefix + "\n"); indent++; }
			        	case "item" -> { writer.append(indent(indent) + "- "); indent++; }
			        	case "str"  -> { writer.append(idPrefix); contentElement = true; }
			        	case "num"  -> { writer.append(idPrefix); contentElement = true; }
			        	case "dat"  -> { writer.append(idPrefix); contentElement = true; }
		        	}
			    }
			    
		        if (nextEvent.isCharacters()) {
		        	String content = nextEvent.asCharacters().getData();
		        	if (contentElement) {
		        		writer.append(content);
		        	}
		        }
		        if (nextEvent.isEndElement()) {
		            EndElement endElement = nextEvent.asEndElement();
			        String name = endElement.getName().getLocalPart();
		        	switch (name) {
			        	case "grp"  -> indent--;
			        	case "item" -> indent--;
			        	case "str"  -> writer.append("\n");
			        	case "num"  -> writer.append("\n");
			        	case "dat"  -> writer.append("\n");
		        	}
		        	contentElement = false;
		        }
			}
			writer.flush();
		}
		catch (XMLStreamException | IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private String attr(StartElement startElement, String name) {
    	Attribute idAttribute = startElement.getAttributeByName(new QName(name));
		String id = (idAttribute == null ? null : idAttribute.getValue());
		return id;
	}
	
	private String indent(int n) {
		return "  ".repeat(n);
	}
	
	public static void main(String[] args) {
		
	}
}
