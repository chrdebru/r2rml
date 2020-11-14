package r2rml.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

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
	//private List<Statement> statements = new ArrayList<Statement>();
	private Map<Rows, Statement> statements = new HashMap<Rows, Statement>();
	
	public DB(Connection connection) {
		this.connection = connection;
	}

	@SuppressWarnings("resource") // connections are closed elsewhere in the code
	public Rows getRows(String query) throws R2RMLException {
		try{
			Statement statement = connection.createStatement();
			ResultSet resultset = statement.executeQuery(query);
			Rows rows = new Rows(resultset);
			statements.put(rows, statement);
			return rows;
		} catch(SQLException e) {
			throw new R2RMLException(e.getMessage(), e);
		}
	}

	public void close() throws R2RMLException {
		if (!statements.isEmpty()) {
			try {
				for(Statement statement : statements.values()) {
					statement.close();
				}
				statements.clear();
			} catch (SQLException e) {
				throw new R2RMLException(e.getMessage(), e);
			}
		}
		
	}

	public void closeRows(Rows rows) throws R2RMLException {
		Statement s = statements.get(rows);
		if(s != null) {
			try {
				s.close();
			} catch (SQLException e) {
				throw new R2RMLException(e.getMessage(), e);
			}
		}
		statements.remove(rows);
	}

}
