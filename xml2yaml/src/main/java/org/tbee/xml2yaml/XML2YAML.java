package org.tbee.xml2yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private static final Logger logger = LoggerFactory.getLogger(XML2YAML.class);
	

	// =============================================================================================================
	// XML processing
	
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
			XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
			XMLEventReader reader = xmlInputFactory.createXMLEventReader(inputStream);
			while (reader.hasNext()) {
			    XMLEvent nextEvent = reader.nextEvent();
			    			
			    // setup a new node
			    if (nextEvent.isStartElement()) {				    
				    Node parentNode = stack.peek();
				    Node currentNode = new Node();
				    
			        StartElement startElement = nextEvent.asStartElement();
			        currentNode.name = startElement.getName().getLocalPart();
			        currentNode.isXml2Yaml = "xml2yaml".equals(currentNode.name);
				    currentNode.isItem = "_".equals(currentNode.name);
					currentNode.indent = stack.size() - 2;
					currentNode.firstPrint = firstPrint;
					
				    if (!currentNode.isXml2Yaml) {
					    startElement(startElement, currentNode, parentNode, writer);
					    firstPrint = currentNode.firstPrint;
				    }
			    	stack.push(currentNode);
			    }
			    
			    // write content
		        if (nextEvent.isCharacters()) {
		        	String content = nextEvent.asCharacters().getData();
		        	if (!content.isBlank()) {
	            		writer.append(content);
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
		int indent;
		boolean firstPrint = true;
	}

	/*
	 * 
	 */
	public void startElement(StartElement startElement, Node currentNode, Node parentNode, Writer writer) throws Exception {
		String key = attr(startElement, "key"); // key can overwrite name as the yaml key
		if (key == null) {
			key = currentNode.name; // but apparently it did not
		}
		String id = attr(startElement, "id"); // id can be referenced by ref
		String ref = attr(startElement, "ref"); // ref can reference an id
	    
    	// first tag after an item does not have a new line
	    if (parentNode.isItem && parentNode.previousNode == null) {
	    }
	    else {
	    	if (!currentNode.firstPrint) {
		    	writer.append("\n");		    	
	    	}
	    	currentNode.firstPrint = false;
	    	writer.append(indent(currentNode.indent));
	    }
		
    	if (currentNode.isItem) {
    		writer.append("- ");
    	}
    	else {
    		writer.append(key);
    		writer.append(": ");
        	if (id != null) {
        		writer.append("&" + id);
        	}
        	if (ref != null) {
        		writer.append("*" + ref);
        	}
    	}
	}
	
	// =============================================================================================================
	// Support
	
	/*
	 * 
	 */
	private String attr(StartElement startElement, String name) {
    	Attribute idAttribute = startElement.getAttributeByName(new QName(name));
		String id = (idAttribute == null ? null : idAttribute.getValue());
		return id;
	}
	
	/*
	 * 
	 */
	private String indent(int n) {
		return "  ".repeat(n);
	}
	
	// =============================================================================================================
	// Standalone
	
	public static void main(String[] args) {
		
		try {
			// create the command line parser
			CommandLineParser parser = new DefaultParser();
	
			// create the Options
			Options options = new Options();
			options.addOption( Option.builder("h")
					.longOpt("help")
			        .desc("show help")
			        .build());
			options.addOption( Option.builder("o")
					.longOpt("out")
			        .desc("file to write to")
			        .hasArg()
			        .argName("FILE")
			        .build());

		    // parse the command line arguments
		    CommandLine commandLine = parser.parse( options, args );
		    if (commandLine.hasOption("help")) {
		        new HelpFormatter().printHelp("java -jar xml2yaml.jar [OPTIONS] [FILE]", options);
		        System.exit(1);
		    }
		    String[] remainingArgs = commandLine.getArgs();		    
		    
		    // determine input
		    InputStream inputStream = System.in;
		    if (remainingArgs.length == 1) {
			    File file = new File(remainingArgs[0]);
			    if (!file.exists()) {
			    	System.err.print("File does not exist: "  + file.getAbsolutePath());
			    	System.exit(2);
			    }
			    logger.debug("Input: " + file.getAbsolutePath());
			    inputStream = new FileInputStream(file);
		    }
		    else {
			    logger.debug("Input: STDIN");
		    }
		    
		    // determine output
		    OutputStream outputStream = System.out;
		    if (commandLine.hasOption("out")) {
			    File file = new File(commandLine.getOptionValue("out"));
			    outputStream = new FileOutputStream(file);
			    logger.debug("Output: " + file.getAbsolutePath());
		    }
		    else {
			    logger.debug("Input: STDOUT");
		    }
		    
		    // and convert
		    new XML2YAML().convert(inputStream, outputStream);
		}
		catch(Exception exp) {
		    System.out.println( "Unexpected exception:" + exp.getMessage() );
		}		
	}
}
