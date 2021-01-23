package org.tbee.xml2yaml.maven.plugin;

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
import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.tbee.xml2yaml.XML2YAML;

/**
 * Invoke XML2YAML::convert
 * 
 * TODO:
 * - reroute slf4j to plugin logging
 */
@Mojo(name = "convert", defaultPhase = LifecyclePhase.GENERATE_RESOURCES, threadSafe = true)
public class XML2YAMLMojo extends AbstractMojo
{
    /**
     * The input file
     */
    @Parameter(property = "input", required = true)
    private File inputFile;

    /**
     * The optional output file
     */
    @Parameter(property = "output", required = false)
    private File outputFile;

    /**
     * 
     */
    public void execute() throws MojoExecutionException
    {
    	// input file
        if (!inputFile.exists() ) {
            throw new MojoExecutionException("Input file does not exist: " + inputFile.getAbsolutePath());
        }
        
        // output file
        if (outputFile == null) {
    		String inputFileAbsolutePath = inputFile.getAbsolutePath();
        	
        	// if input file ends with ".xml" replace that with ".yaml"
        	if (inputFile.getName().endsWith(".xml")) {
        		inputFileAbsolutePath = inputFileAbsolutePath.substring(0, inputFileAbsolutePath.length() - 4);
        	}
        	
        	// create outputfile
    		outputFile = new File(inputFileAbsolutePath + ".yaml");
        }
        
        // convert
        try (
        	FileInputStream fileInputStream = new FileInputStream(inputFile);
    		FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
        ){
        	new XML2YAML().convert(fileInputStream, fileOutputStream);
        } 
        catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} 
    }
}
