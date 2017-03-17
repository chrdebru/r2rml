package test;

import org.apache.jena.rdf.model.Model;
import org.apache.log4j.BasicConfigurator;
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
public class TestR2RMLCOL extends TestCase {

	public TestR2RMLCOL(String testName) {
		super(testName);
	}

	@BeforeClass
	public static void init() throws Exception {
		// Log4J junit configuration.
		BasicConfigurator.configure();
	}

	public void testExampleCSV01() {
		Configuration configuration = new Configuration();
		configuration.setMappingFile("./test/resources/COL01.mapping.ttl");
		configuration.getCSVFiles().add("./test/resources/PERSON.CSV");
		configuration.getCSVFiles().add("./test/resources/PET.CSV");
		R2RMLProcessor engine = new R2RMLProcessor(configuration);
		engine.execute();
		Model model = engine.getDataset().getDefaultModel();
		model.write(System.out, "Turtle");
	}

}
