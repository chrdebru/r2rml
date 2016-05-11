package r2rml.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import r2rml.engine.R2RMLException;

/**
 * Rows Class.
 * 
 * @author Christophe Debruyne
 * @version 0.1
 *
 */
public class Rows {

	private ResultSet resultset = null;
	private Map<String, Integer> indexMap = null;
	private Map<String, Map<String, Integer>> projections = new HashMap<String, Map<String, Integer>>(); 
	
	public Rows(ResultSet resultset) throws R2RMLException {
		this.resultset = resultset;
		try {
			indexMap = makeProjection(1, resultset.getMetaData().getColumnCount());
		} catch (SQLException e) {
			throw new R2RMLException(e.getMessage(), e);
		}
	}

	private Map<String, Integer> makeProjection(int from, int to) throws R2RMLException {
		String key = from + " " + to;
		Map<String, Integer> indexMap = projections.get(key);
		if(indexMap == null) {
			indexMap = new HashMap<String, Integer>();
			for(int i = from; i <= to; i++) {
				try {
					indexMap.put(resultset.getMetaData().getColumnLabel(i), i);
				} catch (SQLException e) {
					throw new R2RMLException(e.getMessage(), e);
				}
			}
			projections.put(key, indexMap);
		}
		return indexMap;
	}

	public Row nextRow() throws R2RMLException {
		try {
			if(resultset.next())
				return new Row(resultset, indexMap);
		} catch (SQLException e) {
			throw new R2RMLException(e.getMessage(), e);
		}
		return null;
	}
	
	public Row projectCurrentRow(int from, int to) throws R2RMLException {
		return new Row(resultset, makeProjection(from, to));
	}
	
	public int getColumnCount() throws R2RMLException {
		try {
			return resultset.getMetaData().getColumnCount();
		} catch (SQLException e) {
			throw new R2RMLException(e.getMessage(), e);
		}
	}

}
