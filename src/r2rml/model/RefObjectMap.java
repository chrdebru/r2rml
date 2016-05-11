package r2rml.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.log4j.Logger;

import r2rml.engine.R2RML;

/**
 * RefObjectMap Class.
 * 
 * @author Christophe Debruyne
 * @version 0.1
 *
 */
public class RefObjectMap extends R2RMLResource {
	
	private static Logger logger = Logger.getLogger(RefObjectMap.class.getName());

	private List<Join> joins = new ArrayList<Join>();
	
	// We keep track of the parent via a resource, as it will be processed
	// by the engine.
	private Resource parentTriplesMap = null;
	
	public RefObjectMap(Resource description) {
		super(description);
	}
	
	public List<Join> getJoins() {
		return joins;
	}
	
	public Resource getParentTriplesMap() {
		return parentTriplesMap;
	}
	
	@Override
	public boolean preProcessAndValidate() {
		logger.info("Processing RefObjectMap " + description);
		
		// Having an rr:parentTriplesMap property
		List<Statement> list = description.listProperties(R2RML.parentTriplesMap).toList();
		if(list.size() != 1) {
			logger.error("RefObjectMap must have exactly one rr:parentTriplesMap.");
			logger.error(description);
			return false;
		}
		
		RDFNode node = list.get(0).getObject();
		if(!node.isResource()) {
			logger.error("rr:parentTriplesMap must be a resource.");
			logger.error(description);
			return false;
		}
		
		parentTriplesMap = node.asResource();
		
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
