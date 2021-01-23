package org.tbee.xml2yaml.maven.plugin;


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Rule;
import org.junit.Test;

public class XML2YAMLMojoTest // extends AbstractMojoTestCase
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
        File basedir = new File("target/test-classes/project-to-test");
        assertNotNull(basedir);
        assertTrue(basedir.exists());

        XML2YAMLMojo mojo = (XML2YAMLMojo)rule.lookupConfiguredMojo(basedir, "convert");
        assertNotNull(mojo);
        mojo.execute();

        File inputFile = (File) rule.getVariableValueFromObject(mojo, "input");
        assertNotNull(inputFile);
        assertTrue(inputFile.exists());
    }
}

