package test;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.junit.BeforeClass;

import junit.framework.TestCase;
import r2rml.engine.Configuration;
import r2rml.engine.R2RMLProcessor;

/**
 * Unit test for testing the functionality CSV to JDBC
 * in memory database.
 * 
 * @author Christophe Debruyne
 *
 */
public class TestR2RMLCSV extends TestCase {

	public TestR2RMLCSV(String testName) {
		super(testName);
	}

	@BeforeClass
	public static void init() throws Exception {
		Configurator.initialize(new DefaultConfiguration());
	    Configurator.setRootLevel(Level.INFO);
	}

	public void testExampleCSV01() {
		Configuration configuration = new Configuration();
		configuration.setMappingFile("./test/resources/CSV01.mapping.ttl");
		configuration.getCSVFiles().add("./test/resources/EMP.CSV");
		configuration.getCSVFiles().add("./test/resources/DEPT.CSV");
		R2RMLProcessor engine = new R2RMLProcessor(configuration);
		engine.execute();
		Model model = engine.getDataset().getDefaultModel();
		Model target = ModelFactory.createDefaultModel();
		target.read("./test/resources/CSV01.output.ttl");
		assertEquals(true, model.difference(target).isEmpty());
		assertEquals(true, target.difference(model).isEmpty());	
	}
	
	public void testExampleCSV02() {
		Configuration configuration = new Configuration();
		configuration.setMappingFile("./test/resources/CSV02.mapping.ttl");
		configuration.getCSVFiles().add("./test/resources/EMP.CSV");
		configuration.getCSVFiles().add("./test/resources/DEPT.CSV");
		R2RMLProcessor engine = new R2RMLProcessor(configuration);
		engine.execute();
		Model model = engine.getDataset().getDefaultModel();
		Model target = ModelFactory.createDefaultModel();
		target.read("./test/resources/CSV02.output.ttl");
		assertEquals(true, model.difference(target).isEmpty());
		assertEquals(true, target.difference(model).isEmpty());	
	}
	
	public void testExampleCSV03() {
		Configuration configuration = new Configuration();
		configuration.setMappingFile("./test/resources/CSV01query.mapping.ttl");
		configuration.getCSVFiles().add("./test/resources/EMP.CSV");
		configuration.getCSVFiles().add("./test/resources/DEPT.CSV");
		R2RMLProcessor engine = new R2RMLProcessor(configuration);
		engine.execute();
		Model model = engine.getDataset().getDefaultModel();
		Model target = ModelFactory.createDefaultModel();
		target.read("./test/resources/CSV01.output.ttl");
		assertEquals(true, model.difference(target).isEmpty());
		assertEquals(true, target.difference(model).isEmpty());	
	}
	
	public void testExampleCSV04() {
		Configuration configuration = new Configuration();
		configuration.setMappingFile("./test/resources/CSV01multiline.mapping.ttl");
		configuration.getCSVFiles().add("./test/resources/EMP.CSV");
		configuration.getCSVFiles().add("./test/resources/DEPT.CSV");
		R2RMLProcessor engine = new R2RMLProcessor(configuration);
		engine.execute();
		Model model = engine.getDataset().getDefaultModel();
		Model target = ModelFactory.createDefaultModel();
		target.read("./test/resources/CSV01.output.ttl");
		assertEquals(true, model.difference(target).isEmpty());
		assertEquals(true, target.difference(model).isEmpty());	
	}
	
	public void testExampleCSV05() {
		Configuration configuration = new Configuration();
		configuration.setMappingFile("./test/resources/CSV02quoted.mapping.ttl");
		configuration.getCSVFiles().add("./test/resources/EMP.CSV");
		configuration.getCSVFiles().add("./test/resources/DEPT.CSV");
		R2RMLProcessor engine = new R2RMLProcessor(configuration);
		engine.execute();
		Model model = engine.getDataset().getDefaultModel();
		Model target = ModelFactory.createDefaultModel();
		target.read("./test/resources/CSV02.output.ttl");
		assertEquals(true, model.difference(target).isEmpty());
		assertEquals(true, target.difference(model).isEmpty());	
	}
	
	public void testExampleCSV06() {
		Configuration configuration = new Configuration();
		configuration.setMappingFile("./test/resources/CSV06.mapping.ttl");
		configuration.getCSVFiles().add("./test/resources/EMP2.CSV");
		R2RMLProcessor engine = new R2RMLProcessor(configuration);
		engine.execute();
		Model model = engine.getDataset().getDefaultModel();
		Model target = ModelFactory.createDefaultModel();
		target.read("./test/resources/CSV06.output.ttl");
		assertEquals(true, model.difference(target).isEmpty());
		assertEquals(true, target.difference(model).isEmpty());	
	}

}
