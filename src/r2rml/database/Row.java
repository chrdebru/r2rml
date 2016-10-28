package r2rml.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import r2rml.engine.R2RMLException;

/**
 * Row Class.
 * 
 * @author Christophe Debruyne
 * @version 0.1
 *
 */
public class Row {

	private ResultSet resultset = null;
	private Map<String, Integer> indexMap = null;

	protected Row(ResultSet resultset, Map<String, Integer> indexMap) {
		this.resultset = resultset;
		this.indexMap = indexMap;
	}

	public Object getObject(String column) throws R2RMLException {
		try {
			Integer index = indexMap.get(column);
			// Check whether the user added the right column names in the mappings
			if(index == null)
				throw new R2RMLException("Column '" +  column + "' does not exit in the logical table.", null);
			return resultset.getObject(index);
		} catch (SQLException e) {
			throw new R2RMLException(e.getMessage(), e);
		}
	}

}
