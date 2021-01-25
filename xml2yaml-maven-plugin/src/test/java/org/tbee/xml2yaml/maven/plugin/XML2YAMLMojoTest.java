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


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Rule;
import org.junit.Test;

public class XML2YAMLMojoTest
{
    @Rule
    public MojoRule rule = new MojoRule()
    {
        @Override
        protected void before() throws Throwable 
        {
        }

        @Override
        protected void after()
        {
        }
    };

    /**
     * @throws Exception if any
     */
    @Test
    public void testSomething()
    throws Exception
    {
        File pom = new File("src/test/resources/project-to-test/pom.xml");
        assertNotNull( pom );
        assertTrue( pom.exists() );

        XML2YAMLMojo mojo = (XML2YAMLMojo)rule.lookupMojo("convert", pom);
        assertNotNull(mojo);
        mojo.execute();

        File inputFile = (File) rule.getVariableValueFromObject(mojo, "inputFile");
        assertNotNull(inputFile);
        assertTrue(inputFile.exists());
    }
}

