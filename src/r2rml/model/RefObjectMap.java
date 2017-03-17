package r2rml.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.apache.log4j.Logger;

import r2rml.engine.R2RML;
import r2rml.engine.RRF;

/**
 * RefObjectMap Class.
 * 
 * @author Christophe Debruyne
 * @version 0.1
 *
 */
public class RefObjectMap extends R2RMLResource implements Collectable {
	
	private static Logger logger = Logger.getLogger(RefObjectMap.class.getName());

	private List<Join> joins = new ArrayList<Join>();
	
	private List<Statement> collectasslist = null;
	
	// We keep track of the parent via a resource, as it will be processed
	// by the engine.
	private Resource parentTriplesMap = null;
	
	public RefObjectMap(Resource description) {
		super(description);
		collectasslist = description.listProperties(RRF.collectAs).toList();
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
		
		if(collectasslist.size() > 1) {
			logger.error("ObjectMap can only have at most one rrf:collectAs.");
			logger.error(description);
			return false;
		} else if(collectasslist.size() == 1) {
			RDFNode n = collectasslist.get(0).getObject();
			if(!(n.isResource() && (
					RDF.List.equals(n.asResource()) ||
					RDF.Bag.equals(n.asResource()) ||
					RDF.Seq.equals(n.asResource()) ||
					RDF.Alt.equals(n.asResource())))) {
				logger.error("rrf:collectAs must refer to RDF container or collection.");
				logger.error(description);
				return false;
			}
		}
		
		return true;
	}

	@Override
	public boolean hasCollactAs() {
		return collectasslist.size() == 1;
	}

	@Override
	public Resource getCollectAsTermType() {
		return collectasslist.get(0).getResource();
	}
	
}
