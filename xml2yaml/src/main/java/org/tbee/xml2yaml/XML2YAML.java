 package org.tbee.xml2yaml;

import java.io.IOException;

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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Convert an XML file to YAML 
 */
public class XML2YAML {
	private static final Logger logger = LoggerFactory.getLogger(XML2YAML.class);
	

	// =============================================================================================================
	// XML processing
	//	 * https://yaml.org/spec/1.2/spec.html
	//	 * 
	//	 * https://www.reddit.com/r/ansible/comments/5jhff3/when_to_use_dash_in_yaml/
	//	 *   - symbol denotes the start of a new array item
	//	 *
	//	 * https://stackoverflow.com/questions/15540635/what-is-the-use-of-the-pipe-symbol-in-yaml
	//	 *   | symbol maintains newlines, with the value following determines how many spaces to strip
	//	 *   > symbol replaces newlines with a space
	//	 *   
	//	 * TODO
	
	/**
	 * 
	 */
	public void convert(InputStream inputStream, OutputStream outputStream) {
		try {
			// Prepare a stack to keep track of what happened around our current node
			Stack<Node> stack = new Stack<>();
			stack.push(new Node());
			boolean firstPrint = true;
			
			// the output 
			Writer writer = new OutputStreamWriter(outputStream,"UTF-8");
			
			// Parse the xml
		    StartElement startElement = null;
			XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
			xmlInputFactory.setProperty("javax.xml.stream.isCoalescing", true);  // decode entities into one string
			XMLEventReader reader = xmlInputFactory.createXMLEventReader(inputStream);
			while (reader.hasNext()) {
			    XMLEvent nextEvent = reader.nextEvent();
			    
			    // setup a new node
			    if (nextEvent.isStartElement()) {				    
				    Node parentNode = stack.peek();
				    Node currentNode = new Node();
				    currentNode.parentNode = parentNode;
				    
			        startElement = nextEvent.asStartElement();
			        currentNode.name = startElement.getName().getLocalPart();
			        currentNode.isXml2Yaml = "xml2yaml".equals(currentNode.name);
					String key = attr(startElement, "key", currentNode.name); // key can overwrite name as the yaml key
					if (key != null) {
						currentNode.name = key;
					}
				    currentNode.isItem = "_".equals(currentNode.name);
					currentNode.indent = stack.size() - 2;
					currentNode.firstPrint = firstPrint;
					
				    // has the parent node gotten its colon?
				    if (parentNode != null && parentNode.name != null && !parentNode.isXml2Yaml && !parentNode.isItem && !parentNode.colonPrinted) {
				    	if (logger.isTraceEnabled()) logger.trace("Child node '" + currentNode.name +"' found, appending ':' to parent node '" + listParents(currentNode) + "'");
				    	append(currentNode, writer, ": ");
			    		parentNode.colonPrinted = true;
				    }
				    
				    if (!currentNode.isXml2Yaml) {
					    startElement(startElement, currentNode, parentNode, writer);
					    firstPrint = currentNode.firstPrint;
				    }
			    	stack.push(currentNode);
			    }
			    
			    // write content
		        if (nextEvent.isCharacters() && startElement != null) {
		        	String content = nextEvent.asCharacters().getData();
		        	if (!content.isBlank()) {
					    Node currentNode = stack.peek();
		        		content(content, startElement, nextEvent, currentNode, writer);
		        	}
		        }
		        
		        // clean up at the end of a taf
		        if (nextEvent.isEndElement()) {
				    Node currentNode = stack.pop();
				    Node parentNode = stack.peek();
				    parentNode.previousNode = currentNode;
		        }
			}
			writer.flush();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	private class Node {
		String name;
		boolean isItem = false;
		boolean isXml2Yaml = false;
		Node previousNode = null;
		Node parentNode = null;
		int indent;
		boolean firstPrint = true;
		boolean colonPrinted = false;
	}

	/*
	 * 
	 */
	private void startElement(StartElement startElement, Node currentNode, Node parentNode, Writer writer) throws Exception {
		String id = attr(startElement, "id", null); // id can be referenced by ref
		String ref = attr(startElement, "ref", null); // ref can reference an id
	    
    	// first tag after an item does not have a new line
	    if (parentNode.isItem && parentNode.previousNode == null) {
	    }
	    else {
	    	if (!currentNode.firstPrint) {
		    	append(currentNode, writer, "\n");		    	
	    	}
	    	currentNode.firstPrint = false;
	    	append(currentNode, writer, indent(currentNode.indent));
	    }
		
	    // item tag
    	if (currentNode.isItem) {
    		append(currentNode, writer, "- ");
    	}
    	// normal tag
    	else {
	    	if (logger.isTraceEnabled()) logger.trace("Starting a new node '" + currentNode.name + "'" + (parentNode == null || parentNode.isXml2Yaml ? "" : " child of '" + listParents(currentNode) + "'"));
    		append(currentNode, writer, currentNode.name);
        	if (id != null) {
		    	if (logger.isTraceEnabled()) logger.trace("Node '" + currentNode.name +"' has an id '" + id + "'");
        		append(currentNode, writer, ": &" + id);
        		currentNode.colonPrinted = true;
        	}
        	if (ref != null) {
		    	if (logger.isTraceEnabled()) logger.trace("Node '" + currentNode.name +"' has an reference '" + ref + "'");
        		append(currentNode, writer, ": *" + ref);
        		currentNode.colonPrinted = true;
        	}
    	}
	}
	
	/*
	 * 
	 */
	private void content(String content, StartElement startElement, XMLEvent contentEvent, Node currentNode, Writer writer) throws Exception {
		String id = attr(startElement, "id", null); // id can be referenced by ref
		String ref = attr(startElement, "ref", null); // ref can reference an id
	    
    	// print a colon prior to the contact (a tag may not have content)
    	if (!currentNode.isItem && !currentNode.colonPrinted) {
	    	if (logger.isTraceEnabled()) logger.trace("Content found, appending ':' to node '" + listParents(currentNode) + "'");
    		append(currentNode, writer, ": ");
    		currentNode.colonPrinted = true;
    	}
    	
		// replaceNewlines
		boolean newlinesArePresent = content.contains("\n");
		if (newlinesArePresent) {
			
			// TODO: detect CDATA and assume strip pre- and postfix
			boolean reindent = bool(startElement, "reindent", true);
			if (reindent) {
				// remove extra characters because of the way CDATA is formatted like so:
				//     <comments replaceNewlines="true">	
				//         Late afternoon is best.
				//         Backup contact is Nancy
				//         Billsmer @ 338-4338.
				//    </comments>
				// 
				// There are possible spaces plus a newline after the CDATA-start,
				// and a newline plus white spaces before the CDATA-end
				content = content
						.replaceAll("^\\s*\n", "") // preceding whitespace + first newline
						.replaceAll("\n\\s*$", ""); // last newline + trailing whitespace
				
				// strip indent 
				content = replaceXMLIdentWithYAMLIndent(content, currentNode.indent + 1);
			}

			// replace or keep newlines
			boolean replaceNewlines = bool(startElement, "replaceNewlines", false);
			if (replaceNewlines) {
				append(currentNode, writer, ">");
			}
			else {
				append(currentNode, writer, "|");
			}			
			// optional stripIndent
			String stripIndent = attr(startElement, "stripIndent", null);
			if (stripIndent != null) {
				append(currentNode, writer, stripIndent);
			}
			
			// start on a new line
			append(currentNode, writer, "\n");
		}
		
		
		// write
		append(currentNode, writer, content);
	}

	/*
	 * Strip XML indent and replace with YAML indent
	 */
	private String replaceXMLIdentWithYAMLIndent(String content, int indent) {
		// split in lines
		List<String> lines = content.lines().collect(Collectors.toList());
		
		// determine the minimal number of whitespaces prefixing any of the lines
		int numberOfWhitespaceMin = Integer.MAX_VALUE;		        
		for (String line : lines) {
		    int numberOfWhitespace = 0;		        
		    while (Character.isWhitespace(line.charAt(0))) {
		    	line = line.substring(1);
		    	numberOfWhitespace++;
		    }
		    if (numberOfWhitespace < numberOfWhitespaceMin) {
		    	numberOfWhitespaceMin = numberOfWhitespace;
		    }
		}
		
		// strip that number of whitespace from each line and replace with new ident
		int numberOfWhitespaceMinFinal = numberOfWhitespaceMin;
		String indentStr = indent(indent);
		content = lines.stream()
			.map((line) -> indentStr + line.substring(numberOfWhitespaceMinFinal))
			.collect(Collectors.joining("\n"));
		
		// done
		return content;
	}
	
	// =============================================================================================================
	// Support
	
	/*
	 * 
	 */
	private String attr(StartElement startElement, String name, String defaultValue) {
    	Attribute attribute = startElement.getAttributeByName(new QName(name));
		String value = (attribute == null ? defaultValue : attribute.getValue());
		return value;
	}
	
	/*
	 * This will check for a true or false value, or else jsut being present is a true
	 */
	private boolean bool(StartElement startElement, String name, boolean defaultValue) {
		return Boolean.parseBoolean(attr(startElement, name, "" + defaultValue));
	}
	
	/* */
	private String indent(int n) {
		return "  ".repeat(n);
	}
	
	/* */
	private String listParents(Node currentNode) {
		String s = "";
		while (currentNode.parentNode != null && currentNode.parentNode.name != null && !currentNode.parentNode.isXml2Yaml) {
			s = currentNode.parentNode.name + (s.isEmpty()? "" : ".") + s;
			currentNode = currentNode.parentNode;
		}
		return s;
	}
	
	/* */
	private void append(Node currentNode, Writer writer, String s) throws IOException {
		if (logger.isTraceEnabled() && !s.isBlank()) logger.trace("> " + indent(currentNode.indent) + s.replaceAll("\n", "<newline>"));
		writer.append(s);
	}
}
