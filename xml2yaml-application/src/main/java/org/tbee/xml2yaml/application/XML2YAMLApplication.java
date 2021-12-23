package org.tbee.xml2yaml.application;

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
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
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
			options.addOption( Option.builder("?")
					.longOpt("help")
			        .desc("show help")
			        .build());
			options.addOption( Option.builder("d")
					.longOpt("debug")
			        .desc("show info of the process, probably --out should be used as well")
			        .build());
			options.addOption( Option.builder("t")
					.longOpt("trace")
			        .desc("show more info of the process, probably --out should be used as well")
			        .build());
			options.addOption( Option.builder("o")
					.longOpt("out")
			        .desc("file to write to")
			        .hasArg()
			        .argName("YAML-FILE")
			        .build());

		    // parse the command line arguments
		    CommandLine commandLine = parser.parse( options, args );
		    if (commandLine.hasOption("help")) {
		        new HelpFormatter().printHelp("java -jar xml2yaml.exe.jar [OPTIONS] [XML-FILE]\nPer default stdin and stdout is used, so xml2yaml can be used in a pipe.", options);
		        System.exit(1);
		    }
		    String[] remainingArgs = commandLine.getArgs();	
		    
		    
		    // determine output
		    if (commandLine.hasOption("debug")) {
		    	Configurator.setRootLevel(Level.DEBUG);
		    }
		    if (commandLine.hasOption("trace")) {
		    	Configurator.setRootLevel(Level.TRACE);
		    }
		    
		    // determine input
		    InputStream inputStream = System.in;
		    if (remainingArgs.length == 1) {
			    File file = new File(remainingArgs[0]);
			    if (!file.exists()) {
			    	logger.error("File does not exist: "  + file.getAbsolutePath());
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
		    logger.error( "Unexpected exception:" + exp.getMessage() );
		    if (logger.isTraceEnabled()) logger.trace(exp.getMessage(), exp);
		}		
	}
}
