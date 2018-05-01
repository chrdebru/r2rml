package test;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.log4j.BasicConfigurator;
import org.junit.BeforeClass;

import junit.framework.TestCase;
import r2rml.engine.Configuration;
import r2rml.engine.R2RMLProcessor;

/**
 * Unit test for MySQL database "r2rml". Requires a user "foo" with password 
 * "bar". A MySQL dump file can be found in the resources folder.
 *  
 * @author Christophe Debruyne
 *
 */
public class TestR2RMLMySQL extends TestCase {

	private static String connectionURL = "jdbc:mysql://localhost/r2rml";

	public TestR2RMLMySQL(String testName) {
		super(testName);
	}

	@BeforeClass
	public static void init() throws Exception {
		// Log4J junit configuration.
		BasicConfigurator.configure();
	}

	/**
	 * 2.3 Example: Mapping a Simple Table
	 * https://www.w3.org/TR/r2rml/#example-simple
	 */
	public void testExample01() {
		Configuration configuration = new Configuration();
		configuration.setMappingFile("./test/resources/01.mapping.ttl");
		configuration.setConnectionURL(connectionURL);
		configuration.setUser("foo");
		configuration.setPassword("bar");
		R2RMLProcessor engine = new R2RMLProcessor(configuration);
		engine.execute();
		Model model = engine.getDataset().getDefaultModel();
		Model target = ModelFactory.createDefaultModel();
		target.read("./test/resources/01.output.ttl");
		assertEquals(true, model.difference(target).isEmpty());
		assertEquals(true, target.difference(model).isEmpty());	
	}

	/**
	 * 2.4 Example: Computing a Property with an R2RML View
	 */
	public void testExample02() {
		Configuration configuration = new Configuration();
		configuration.setMappingFile("./test/resources/02.mapping.ttl");
		configuration.setConnectionURL(connectionURL);
		configuration.setUser("foo");
		configuration.setPassword("bar");
		R2RMLProcessor engine = new R2RMLProcessor(configuration);
		engine.execute();
		Model model = engine.getDataset().getDefaultModel();
		Model target = ModelFactory.createDefaultModel();
		target.read("./test/resources/02.output.ttl");
		assertEquals(true, model.difference(target).isEmpty());
		assertEquals(true, target.difference(model).isEmpty());	
	}

	/**
	 * 2.5 Example: Linking Two Tables
	 */
	public void testExample03() {
		Configuration configuration = new Configuration();
		configuration.setMappingFile("./test/resources/03.mapping.ttl");
		configuration.setUser("foo");
		configuration.setPassword("bar");
		configuration.setConnectionURL(connectionURL);
		R2RMLProcessor engine = new R2RMLProcessor(configuration);
		engine.execute();
		Model model = engine.getDataset().getDefaultModel();
		Model target = ModelFactory.createDefaultModel();
		target.read("./test/resources/03.output.ttl");
		assertEquals(true, model.difference(target).isEmpty());
		assertEquals(true, target.difference(model).isEmpty());
	}

	/**
	 * Many-to-Many 1
	 */
	public void testExample04() {
		Configuration configuration = new Configuration();
		configuration.setMappingFile("./test/resources/04.mapping.ttl");
		configuration.setUser("foo");
		configuration.setPassword("bar");
		configuration.setConnectionURL(connectionURL);
		R2RMLProcessor engine = new R2RMLProcessor(configuration);
		engine.execute();
		Model model = engine.getDataset().getDefaultModel();
		Model target = ModelFactory.createDefaultModel();
		target.read("./test/resources/04.output.ttl");
		assertEquals(true, model.difference(target).isEmpty());
		assertEquals(true, target.difference(model).isEmpty());
	}

	/**
	 * Many-to-Many 2
	 */
	public void testExample05() {
		Configuration configuration = new Configuration();
		configuration.setMappingFile("./test/resources/05.mapping.ttl");
		configuration.setUser("foo");
		configuration.setPassword("bar");
		configuration.setConnectionURL(connectionURL);
		R2RMLProcessor engine = new R2RMLProcessor(configuration);
		engine.execute();
		Model model = engine.getDataset().getDefaultModel();
		Model target = ModelFactory.createDefaultModel();
		target.read("./test/resources/05.output.ttl");
		assertEquals(true, model.difference(target).isEmpty());
		assertEquals(true, target.difference(model).isEmpty());
	}

	public void testExample06() {
		Configuration configuration = new Configuration();
		configuration.setMappingFile("./test/resources/06.mapping.ttl");
		configuration.setUser("foo");
		configuration.setPassword("bar");
		configuration.setConnectionURL(connectionURL);
		R2RMLProcessor engine = new R2RMLProcessor(configuration);
		engine.execute();
		Model model = engine.getDataset().getDefaultModel();
		Model target = ModelFactory.createDefaultModel();
		target.read("./test/resources/06.output.ttl");
		assertEquals(true, model.difference(target).isEmpty());
		assertEquals(true, target.difference(model).isEmpty());
	}

	public void testExample07() {
		Configuration configuration = new Configuration();
		configuration.setMappingFile("./test/resources/07.mapping.ttl");
		configuration.setUser("foo");
		configuration.setPassword("bar");
		configuration.setConnectionURL(connectionURL);
		R2RMLProcessor engine = new R2RMLProcessor(configuration);
		engine.execute();
		Model model = engine.getDataset().getDefaultModel();
		Model target = ModelFactory.createDefaultModel();
		target.read("./test/resources/07.output.ttl");
		assertEquals(true, model.difference(target).isEmpty());
		assertEquals(true, target.difference(model).isEmpty());
	}

	public void testExample08() {
		Configuration configuration = new Configuration();
		configuration.setMappingFile("./test/resources/08.mapping.ttl");
		configuration.setUser("foo");
		configuration.setPassword("bar");
		configuration.setConnectionURL(connectionURL);
		R2RMLProcessor engine = new R2RMLProcessor(configuration);
		engine.execute();
		Model model = engine.getDataset().getDefaultModel();
		Model target = ModelFactory.createDefaultModel();
		target.read("./test/resources/08.output.ttl");
		assertEquals(true, model.difference(target).isEmpty());
		assertEquals(true, target.difference(model).isEmpty());
	}

	public void testExample09() {
		Configuration configuration = new Configuration();
		configuration.setMappingFile("./test/resources/09.mapping.ttl");
		configuration.setUser("foo");
		configuration.setPassword("bar");
		configuration.setConnectionURL(connectionURL);
		R2RMLProcessor engine = new R2RMLProcessor(configuration);
		engine.execute();
		Model model = engine.getDataset().getDefaultModel();
		Model target = ModelFactory.createDefaultModel();
		target.read("./test/resources/09.output.ttl");
		assertEquals(true, model.difference(target).isEmpty());
		assertEquals(true, target.difference(model).isEmpty());
	}

	public void testExample10() {
		Configuration configuration = new Configuration();
		configuration.setMappingFile("./test/resources/10.mapping.ttl");
		configuration.setUser("foo");
		configuration.setPassword("bar");
		configuration.setConnectionURL(connectionURL);
		R2RMLProcessor engine = new R2RMLProcessor(configuration);
		engine.execute();
		Model model = engine.getDataset().getDefaultModel();
		Model target = ModelFactory.createDefaultModel();
		// HAD TO CREATE AN RDF/XML FILE AS JENA IS UNABLE TO PARSE "\o" AS TTL
		// IT THINKS YOU ARE TRYING TO ESCAPE AN "o"... NO PROBLEM WITH RDF/XML
		target.read("./test/resources/10.output.rdf");
		assertEquals(true, model.difference(target).isEmpty());
		assertEquals(true, target.difference(model).isEmpty());
	}

}
