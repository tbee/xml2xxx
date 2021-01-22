package org.tbee.xml2yaml.application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tbee.xml2yaml.XML2YAML;

/**
 *   
 */
public class XML2YAMLApplication {
	private static final Logger logger = LoggerFactory.getLogger(XML2YAMLApplication.class);
	

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
			    logger.debug("Output: STDOUT");
		    }
		    
		    // and convert
		    new XML2YAML().convert(inputStream, outputStream);
		}
		catch(Exception exp) {
		    System.out.println( "Unexpected exception:" + exp.getMessage() );
		}		
	}
}
