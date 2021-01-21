package org.tbee.xml2yaml;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Stack;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.Attribute;
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

	/**
	 * 
	 */
	public void convertMeta(InputStream inputStream, OutputStream outputStream) {
		convert(inputStream, outputStream, new Handler() {
			
			@Override
			public void startElement(Node currentNode, Node parentNode, Writer writer) throws Exception {
			    currentNode.isItem = "item".equals(currentNode.name);
			    
			    // new line if previous 
			    if (parentNode.isItem && parentNode.previousNode == null) {
			    	// first entry does not have a new line
			    }
			    else {
			    	writer.append("\n" + indent(currentNode.indent));
			    }
				
		    	if (currentNode.isItem) {
		    		writer.append("- ");
		    	}
		    	else {
		    		writer.append(currentNode.id);
		    		writer.append(": ");
		        	if (currentNode.key != null) {
		        		writer.append("&" + currentNode.key);
		        	}
		        	if (currentNode.ref != null) {
		        		writer.append("*" + currentNode.ref);
		        	}
		    	}
			}
			
			@Override
			public void content(Node currentNode, Node parentNode, String content, Writer writer) throws Exception {
	        	if (!content.isBlank()) {
	        		writer.append(content);
	        	}
			}
			
			@Override
			public void endElement(Node currentNode, Node parentNode, Writer writer) {
			}
		});
	}
	
	/**
	 * 
	 */
	public void convertSemantic(InputStream inputStream, OutputStream outputStream) {
		convert(inputStream, outputStream, new Handler() {
			
			@Override
			public void startElement(Node currentNode, Node parentNode, Writer writer) throws Exception {
			    currentNode.isItem = "_".equals(currentNode.name);
			    
			    // new line if previous 
			    if (parentNode.isItem && parentNode.previousNode == null) {
			    	// first entry does not have a new line
			    }
			    else {
			    	writer.append("\n" + indent(currentNode.indent));
			    }
				
		    	if (currentNode.isItem) {
		    		writer.append("- ");
		    	}
		    	else {
		    		writer.append(currentNode.id != null ? currentNode.id : currentNode.name);
		    		writer.append(": ");
		        	if (currentNode.key != null) {
		        		writer.append("&" + currentNode.key);
		        	}
		        	if (currentNode.ref != null) {
		        		writer.append("*" + currentNode.ref);
		        	}
		    	}
			}
			
			@Override
			public void content(Node currentNode, Node parentNode, String content, Writer writer) throws Exception {
	        	if (!content.isBlank()) {
	        		writer.append(content);
	        	}
			}
			
			@Override
			public void endElement(Node currentNode, Node parentNode, Writer writer) {
			}
		});
	}
	
	
	/*
	 * 
	 */
	private void convert(InputStream inputStream, OutputStream outputStream, Handler handler) {
		// https://www.baeldung.com/java-stax
		try {
			int indent = 0;
			
			// Prepare a stack to keep track of what happened around our current node
			Stack<Node> stack = new Stack<>();
			stack.push(new Node());
			
			// the output 
			Writer writer = new OutputStreamWriter(outputStream,"UTF-8");
			
			// Parse the xml
			XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
			XMLEventReader reader = xmlInputFactory.createXMLEventReader(inputStream);
			while (reader.hasNext()) {
			    XMLEvent nextEvent = reader.nextEvent();
			    			
			    // start
			    if (nextEvent.isStartElement()) {				    
				    Node parentNode = stack.peek();
				    Node currentNode = new Node();
				    
			        StartElement startElement = nextEvent.asStartElement();
			        currentNode.name = startElement.getName().getLocalPart();
					currentNode.key = attr(startElement, "key");
					currentNode.ref = attr(startElement, "ref");
					currentNode.id = attr(startElement, "id");
					currentNode.indent = indent;
					
				    if (!"xml2yaml".equals(currentNode.name)) {
					    handler.startElement(currentNode, parentNode, writer);				    	
			        	indent++;
				    }
			    	stack.push(currentNode);
			    }
			    
		        if (nextEvent.isCharacters()) {
				    Node parentNode = stack.peek();
				    Node currentNode = stack.peek();
		        	String content = nextEvent.asCharacters().getData();
		        	if (!content.isBlank()) {
		        		handler.content(currentNode, parentNode, content, writer);
		        	}
		        }
		        
		        if (nextEvent.isEndElement()) {
				    Node currentNode = stack.pop();
				    Node parentNode = stack.peek();

				    if (!"xml2yaml".equals(currentNode.name)) {
					    handler.endElement(currentNode, parentNode, writer);
			        	indent--;
				    }
				    
				    parentNode.previousNode = currentNode;
		        }
			}
			writer.flush();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	interface Handler {
		void startElement(Node currentNode, Node parentNode, Writer writer) throws Exception;
		void content(Node currentNode, Node parentNode, String content, Writer writer) throws Exception;
		void endElement(Node currentNode, Node parentNode, Writer writer) throws Exception;
	}
	class Node {
		String name;
		boolean isItem = false;
		boolean hasContent = false;
		Node previousNode = null;
		String id = null;
		String key = null;
		String ref = null;
		int indent;
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
