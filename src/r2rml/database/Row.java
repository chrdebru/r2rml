package r2rml.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.apache.commons.text.StringEscapeUtils;

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
			String columnname = StringEscapeUtils.unescapeJava(column);
			Integer index = indexMap.get(columnname);
			// Check whether the user added the right column names in the mappings
			if(index == null)
				// Now try without quotes
				index = indexMap.get(columnname.replace("\"", ""));
			
			if(index == null)
				throw new R2RMLException("Column '" +  column + "' does not exit in the logical table.", null);
			return resultset.getObject(index);
		} catch (SQLException e) {
			throw new R2RMLException(e.getMessage(), e);
		}
	}

}
