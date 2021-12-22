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


## jbang
The easiest way is to use [jbang](https://www.jbang.dev/) with Maven coordinates, which will download everything automatically from Maven central.
* Via stdin and stdout:
    * `cat kubernetes.xml | jbang org.tbee.xml2xxx:xml2yaml-application:1.2.0 > kubernetes.yaml`.
* Via command line parameter(s): 
    * `jbang org.tbee.xml2xxx:xml2yaml-application:1.2.0 kubernetes.xml > kubernetes.yaml`.
    * `jbang org.tbee.xml2xxx:xml2yaml-application:1.2.0 -out kubernetes.yaml kubernetes.xml`.
    
The most recent version can be found on [Maven Central](https://search.maven.org/search?q=g:org.tbee.xml2xxx).

## java -jar
XML2YAM is available as an executable jar.
* Via stdin and stdout:
    * `cat kubernetes.xml | java -jar xml2yaml-application-jar-with-dependencies.jar > kubernetes.yaml`.
* Via command line parameter(s): 
    * `java -jar xml2yaml-application-jar-with-dependencies.jar kubernetes.xml > kubernetes.yaml`.
    * `java -jar xml2yaml-application-jar-with-dependencies.jar -out kubernetes.yaml kubernetes.xml`.
    
You can of course rename xml2yaml-application-jar-with-dependencies.jar to a more practical xml2yaml.jar, but when developing that conflicts with the engine's jar.

The most recent version can be found on [Maven Central](https://search.maven.org/search?q=g:org.tbee.xml2xxx).

## Maven
For use during build a maven plugin is available.

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

(No I don't do Gradle, I think it is a great idea which was implemented wrong.)

The most recent version can be found on [Maven Central](https://search.maven.org/search?q=g:org.tbee.xml2xxx).

