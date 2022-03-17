package test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLNonTransientConnectionException;
import java.sql.Statement;

import org.apache.jena.rdf.model.Model;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
public class TestR2RMLJoinWithLiteral extends TestCase {

	private static Logger logger = LogManager.getLogger(TestR2RMLJoinWithLiteral.class);
	private static String connectionURL = "jdbc:derby:memory:testing";

	public TestR2RMLJoinWithLiteral(String testName) {
		super(testName);
	}

	@BeforeClass
	public static void init() throws Exception {
		Configurator.initialize(new DefaultConfiguration());
		Configurator.setRootLevel(Level.INFO);
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
			statement.execute("CREATE TABLE title (id INT PRIMARY KEY, title VARCHAR(100))");
			statement.execute("CREATE TABLE aka_title (id INT PRIMARY KEY, movie_id INT, title VARCHAR(100))");

			statement.execute("INSERT INTO title VALUES (80889, '(#1.66)')");
			statement.execute("INSERT INTO title VALUES (5156, 'Josie Duggar''s 1st Shoes')");
			statement.execute("INSERT INTO title VALUES (833595, 'Malhação')");
			statement.execute("INSERT INTO title VALUES (2388426, 'The Lord of the Rings: The Fellowship of the Ring')");


			statement.execute("INSERT INTO aka_title VALUES (30289, 1651366, 'Un lac pour la prairie')");
			statement.execute("INSERT INTO aka_title VALUES (301166, 2388426, 'The Lord of the Rings: The Fellowship of the Ring')");
			statement.execute("INSERT INTO aka_title VALUES (301165, 2388426, 'The Fellowship of the Ring')");
			statement.execute("INSERT INTO aka_title VALUES (301163, 2388426, 'Der Herr der Ringe - Die Gefährten')");
			statement.execute("INSERT INTO aka_title VALUES (301167, 2388426, 'The Lord of the Rings: The Fellowship of the Ring: The Motion Picture')");
			statement.execute("INSERT INTO aka_title VALUES (301164, 2388426, 'Il signore degli anelli - La compagnia dell''anello')");
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
		configuration.setMappingFile("./test/resources/joinliteral.mapping.ttl");
		configuration.setConnectionURL(connectionURL); 
		R2RMLProcessor engine = new R2RMLProcessor(configuration); 
		engine.execute(); 
		Model model = engine.getDataset().getDefaultModel();
		model.write(System.out, "Turtle");
	}

}
