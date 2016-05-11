package r2rml.engine;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

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
		String user = configuration.getUser();
		String pass = configuration.getPassword();
		Properties props = new Properties();
		if(user != null && !"".equals(user))
			props.setProperty("user", user);
		if(pass != null && !"".equals(pass))
			props.setProperty("password", pass);

		try {
			connection = DriverManager.getConnection(configuration.getConnectionURL(), props);
		} catch (SQLException e) {
			logger.error("Error connecting to database.", e);
			System.exit(-1);
		}

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
