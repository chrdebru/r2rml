package r2rml.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import r2rml.engine.R2RMLException;

/**
 * DB Class.
 * 
 * @author Christophe Debruyne
 * @version 0.1
 *
 */
public class DB {

	private Connection connection = null;
	private List<Statement> statements = new ArrayList<Statement>();

	public DB(Connection connection) {
		this.connection = connection;
	}

	public Rows getRows(String query) throws R2RMLException {
		try{
			Statement statement = connection.createStatement();
			statements.add(statement);
			ResultSet resultset = statement.executeQuery(query);
			return new Rows(resultset);
		} catch(SQLException e) {
			throw new R2RMLException(e.getMessage(), e);
		}
	}

	public void close() throws R2RMLException {
		if (!statements.isEmpty()) {
			try {
				for(Statement statement : statements) {
					statement.close();
				}
				statements.clear();
			} catch (SQLException e) {
				throw new R2RMLException(e.getMessage(), e);
			}
		}
		
	}

}
