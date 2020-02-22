package r2rml.model;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.log4j.Logger;

public class R2RMLUtil {

	private static Logger logger = Logger.getLogger(R2RMLUtil.class.getName());
	
	public static boolean isValidLanguageTag(RDFNode node) {
		if(!node.isLiteral()) return false;
		// TODO: Implement language tag check
		return true;
	}
	
	/**
	 * Utility function to check whether the string denoting a column name is valid
	 * 
	 * @param columnName
	 * @return True if a valid column name
	 */
	public static boolean isValidColumnName(String columnName) {
		// TODO: check the actual value of the column is valid
		return true;
	}

	public static String createJointQuery(TriplesMap child, TriplesMap parent, List<Join> joins) {
		// If the child query and parent query of a referencing object 
		// map are not identical, then the referencing object map must 
		// have at least one join condition.
		
		String cquery = child.getLogicalTable().generateQuery();
		String pquery = parent.getLogicalTable().generateQuery();
		
		if(!cquery.equals(pquery) && joins.isEmpty()) {
			logger.error("If the child query and parent query of a referencing object map are not identical, then the referencing object map must have at least one join condition.");
			return null;
		}
		
		// If the referencing object map has no join condition
		if(joins.isEmpty())
			return "SELECT * FROM (" + cquery + ") AS tmp";
		
		String query = "SELECT * FROM (" + cquery + ") AS child, ";
        query += "(" + pquery + ") AS parent WHERE ";

		query += joins.stream().map(join -> "child." + join.getChild() + "=parent." + join.getParent()).collect(Collectors.joining(" AND "));
        
        return query;
	}
	
}
