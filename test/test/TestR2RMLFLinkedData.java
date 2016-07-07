package test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLNonTransientConnectionException;
import java.sql.Statement;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;

import junit.framework.TestCase;
import r2rml.engine.Configuration;
import r2rml.engine.R2RMLProcessor;

/**
 * Unit test for testing the functionality of this implementation using an
 * in memory database.
 * 
 * @author Christophe Debruyne
 *
 */
public class TestR2RMLFLinkedData extends TestCase {

	private static Logger logger = Logger.getLogger(TestR2RMLFLinkedData.class.getName());
	private static String connectionURL = "jdbc:derby:memory:testing";

	public TestR2RMLFLinkedData(String testName) {
		super(testName);
	}

	@BeforeClass
	public static void init() throws Exception {
		// Log4J junit configuration.
		BasicConfigurator.configure();
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		try {
			logger.info("Starting in-memory database for unit tests");
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
			DriverManager.getConnection(connectionURL + ";create=true").close();
		} catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception during database startup.");
		}
		try {
			Connection connection = DriverManager.getConnection(connectionURL);
			Statement statement = connection.createStatement();
			statement.execute("CREATE TABLE EMP(EMPNO INT PRIMARY KEY, FNAME VARCHAR(20), LNAME VARCHAR(20))");
			statement.execute("INSERT INTO EMP VALUES (1, 'CHRISTOPHE', 'DEBRUYNE')");
			statement.execute("INSERT INTO EMP VALUES (2, 'ADEMAR', 'CROTTI JUNIOR')");
			statement.execute("INSERT INTO EMP VALUES (3, 'DECLAN', 'O''SULLIVAN')");
			statement.close();
			connection.close();

		} catch (Exception ex) {
			ex.printStackTrace();
			fail("Failure setting up the database.");
		}
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		logger.info("Stopping in-memory database.");
		try {
			DriverManager.getConnection(connectionURL + ";drop=true").close();
		} catch (SQLNonTransientConnectionException ex) {
			if (ex.getErrorCode() != 45000) {
				throw ex;
			}
			// Shutdown success
		}
	}

	public void testExampleF01() {
		Configuration configuration = new Configuration();
		configuration.setMappingFile("./test/resources/F02.mapping.ttl");
		configuration.setConnectionURL(connectionURL);
		R2RMLProcessor engine = new R2RMLProcessor(configuration);
		engine.execute();
		Model model = engine.getDataset().getDefaultModel();
		Model target = ModelFactory.createDefaultModel();
		target.read("./test/resources/F02.output.ttl");
		assertEquals(true, model.difference(target).isEmpty());
		assertEquals(true, target.difference(model).isEmpty());	
	}

}
