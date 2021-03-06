# XML2YAML
YAML is very sensitive to indenting and that often is a source of frustration for developers. XML2YAML converts a recognizable XML file to YAML, automatically indenting correctly.

For example this XML input:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<xml2yaml>
	<invoice>34843</invoice>
	<date>2001-01-23</date>
	<bill-to id="id001">
		<given>Chris</given>
		<family>Dumars</family>
		<address>
			<lines>
				458 Walkman Dr.
				Suite #292
			</lines>
			<city>Royal Oak</city>
			<state>MI</state>
			<postal>48046</postal>
		</address>
	</bill-to>
	<ship-to ref="id001"/>
	<product>
		<_>
			<sku>BL394D</sku>
			<quantity>4</quantity>
			<description>Basketball</description>
			<price>450.00</price>
		</_>
		<_>
			<sku>BL4438H</sku>
			<quantity>1</quantity>
			<description>Super Hoop</description>
			<price>2392.00</price>
		</_>
	</product>
	<tax>251.42</tax>
	<total>4443.52</total>
	<comments replaceNewlines="true">
		Late afternoon is best.
		Backup contact is Nancy
		Billsmer @ 338-4338.
	</comments>
	<_ key="brilliant idea">Allowing spaces in keys</_> 
</xml2yaml>

```

Will result in this YAML output:

```yaml
invoice: 34843
date: 2001-01-23
bill-to: &id001
  given: Chris
  family: Dumars
  address: 
    lines: |
      458 Walkman Dr.
      Suite #292
    city: Royal Oak
    state: MI
    postal: 48046
ship-to: *id001
product: 
  - sku: BL394D
    quantity: 4
    description: Basketball
    price: 450.00
  - sku: BL4438H
    quantity: 1
    description: Super Hoop
    price: 2392.00
tax: 251.42
total: 4443.52
comments: >
  Late afternoon is best.
  Backup contact is Nancy
  Billsmer @ 338-4338.
brilliant idea: Allowing spaces in keys 
```

* The XML tag names match the keys in YAML.
* The item markers (minus characters) in YAML are represented by underscore tags in XML.
* If you cannot write a YAML key as an XML tag, for example because it requires a space, use the 'key' attribute. Key will replace the tag name, so any tag name is okay... Then why not use the minimal one: `<_ key="YAML key name">`
* Multi line text having some processing options:
    * Use `reindent="false"` if you want re-indenting not to happen.
    * Use `replaceNewlines="true"` to put the correct YAML marker in place.

It is important to note that XML2YAML uses no XSD for itself, you are free to write one for the specific XML you are using. Anyone care to write a XSD for Kubernetes?

## Maven
For ease of use a maven plugin is available. The most recent version can be found on [Maven Central](https://search.maven.org/search?q=a:xml2yaml-maven-plugin&g:org.tbee.xml2xxx).

```xml
<plugin>
    <groupId>org.tbee.xml2xxx</groupId>
    <artifactId>xml2yaml-maven-plugin</artifactId>
    <version>...</version>
    <configuration>
        <inputFile>src/main/resources/kubernetes.xml</inputFile>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>convert</goal>						
            </goals>
        </execution>
    </executions>
</plugin>
```

* inputFile will denote the XML file
* outputFile is the YAML file, if undefined; the inputfile where '.xml' is replaced with '.yaml'

## Command line
The functionality is also available on the command line as an executable jar. The most recent version can be found on [Maven Central](https://search.maven.org/search?q=a:xml2yaml-application&g:org.tbee.xml2xxx).
* Via stdin and stdout: `cat kubernetes.xml | java -jar xml2yaml-application.jar > kubernetes.yaml`.
* Via command line parameter(s): 
    * `java -jar xml2yaml-application.jar kubernetes.xml > kubernetes.yaml`.
    * `java -jar xml2yaml-application.jar -out kubernetes.yaml kubernetes.xml`.
    
You can of course rename xml2yaml-application.jar to a more practical xml2yaml.jar, but within the Maven project that conflicts with the engine's jar.