package r2rml.engine;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.log4j.Logger;

import r2rml.database.DB;
import r2rml.model.R2RMLMapping;
import r2rml.model.R2RMLMappingFactory;

/**
 * R2RMLProcessor Class.
 * 
 * @author Christophe Debruyne
 * @version 0.1
 *
 */
public class R2RMLProcessor {

	private static Logger logger = Logger.getLogger(R2RMLProcessor.class.getName());

	private Configuration configuration = null;
	private Connection connection = null;
	private Dataset dataset = DatasetFactory.create();
	
	public R2RMLProcessor(Configuration configuration) {
		this.configuration = configuration;
	}

	public void execute() {
		createDatabaseConnection();
		
		String file = configuration.getMappingFile();
		String baseIRI = configuration.getBaseIRI();
		
		R2RMLMapping mapping = R2RMLMappingFactory.createR2RMLMapping(file, baseIRI);
		DB database = new DB(connection);
		
		boolean abort = false;
		
		if(mapping != null) {
			if(!mapping.generateTriples(database, dataset))
				abort = true;
		} else {
			abort = true;
		}
		
		closeDatabaseConnection();

		if(abort) {
			logger.error("We had to abort generation of triples. See log for details.");
		}
	}

	private void createDatabaseConnection() {
		// Determine situation
		if(configuration.hasConnectionURL() && configuration.hasCSVFiles()) {
			logger.error("You cannot provide a connection URL and a list of CSV files at the same time.");
			System.exit(-1);
		}
		
		try {
			Properties props = new Properties();
			
			// If files, create in-memory database
			if(configuration.hasCSVFiles()) {
				try {
					// This method will create a new connection URL that will be added to the configuration
					connection = createTablesFromCSVFiles();
				} catch (Exception ex) {
					logger.error("Exception during database startup.", ex);
					System.exit(-1);
				}
			} else {
				// Connecting to a database
				String user = configuration.getUser();
				String pass = configuration.getPassword();
				if(user != null && !"".equals(user))
					props.setProperty("user", user);
				if(pass != null && !"".equals(pass))
					props.setProperty("password", pass);			
				connection = DriverManager.getConnection(configuration.getConnectionURL(), props);
			}
			
		} catch (SQLException e) {
			logger.error("Error connecting to database.", e);
			System.exit(-1);
		}

	}

	private Connection createTablesFromCSVFiles() throws Exception {
		String connectionURL = "jdbc:h2:mem:" + System.currentTimeMillis();
		configuration.setConnectionURL(connectionURL);
		
		logger.info("Starting in-memory database for unit tests");
		DriverManager.getConnection(connectionURL + ";create=true").close();
		
		Connection connection = DriverManager.getConnection(connectionURL);
		Statement statement = connection.createStatement();
		
		// for each file, load file as table...
		for(String f : configuration.getCSVFiles()) {
			File file = new File(f);
			String name = createTableNameForFile(file);
			logger.info("Loading " + file + " as table " + name);
			String sql = "CREATE TABLE " + name + " AS SELECT * FROM CSVREAD('"  + file.getAbsolutePath() + "', NULL, NULL);";
			statement.execute(sql);
			logger.info("Loaded " + file + " as table " + name);	
		}
		
		// only close the statement. Don't close connection! It will be returned 
		statement.close();
		return connection;
	}

	private String createTableNameForFile(File file) {
		String name = FilenameUtils.getBaseName(file.getAbsolutePath());
		return name;
	}

	private void closeDatabaseConnection() {
		try {
			if(!connection.isClosed())
				connection.close();
		} catch (SQLException e) {
			logger.error("Error closing connection with database.", e);
			System.exit(-1);
		}

	}

	public Dataset getDataset() {
		return dataset;
	}
}
