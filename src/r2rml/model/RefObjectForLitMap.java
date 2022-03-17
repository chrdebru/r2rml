package r2rml.model;

import java.util.ArrayList;
import java.util.List;

import javax.print.attribute.standard.DialogOwner;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import r2rml.engine.R2RML;
import r2rml.engine.RRF;

/**
 * RefObjectMap Class.
 * 
 * @author Christophe Debruyne
 * @version 0.1
 *
 */
public class RefObjectForLitMap extends ObjectMap {
	
	private static Logger logger = LogManager.getLogger(RefObjectForLitMap.class);

	private List<Join> joins = new ArrayList<Join>();
	
	private LogicalTable logicalTable = null;
	
	public RefObjectForLitMap(Resource description, String baseIRI) {
		super(description, baseIRI);
	}
	
	public List<Join> getJoins() {
		return joins;
	}
	
	public LogicalTable getLogicalTable() {
		return logicalTable;
	}
	
	@Override
	public boolean preProcessAndValidate() {
		super.preProcessAndValidate();
		logger.info("Processing JoinObjectMap " + description);
		
		List<Statement> list = description.listProperties(RRF.parentLogicalTable).toList();
		if(list.size() != 1) {
			logger.error("RefObjectMap must have exactly one rr:parentTriplesMap.");
			logger.error(description);
			return false;
		}
		
		RDFNode node = list.get(0).getObject();
		if(!node.isResource()) {
			logger.error("rrf:parentLogicalTable must be a resource.");
			logger.error(description);
			return false;
		}
		
		logicalTable = new LogicalTable(node.asResource());
		if(!logicalTable.preProcessAndValidate())
			return false;
		
		// process the join conditions
		list = description.listProperties(R2RML.joinCondition).toList();
		for(Statement s : list) {
			node = s.getObject();
			if(!node.isResource()) {
				logger.error("rr:joinCondition must be a resource.");
				logger.error(description);
				return false;
			}
			Join join = new Join(node.asResource());
			if(!join.preProcessAndValidate())
				return false;
			joins.add(join);
		}
		
		return true;
	}
	
}
