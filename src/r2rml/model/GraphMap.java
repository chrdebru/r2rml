package r2rml.model;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import r2rml.database.Row;
import r2rml.engine.R2RML;
import r2rml.engine.R2RMLException;

/**
 * TriplesMap Class.
 * 
 * @author Christophe Debruyne
 * @version 0.1
 *
 */
public class GraphMap extends TermMap {

	private static Logger logger = LogManager.getLogger(GraphMap.class);

	public GraphMap(Resource description, String baseIRI) {
		super(description, baseIRI);
	}

	@Override
	public boolean preProcessAndValidate() {
		if(!super.preProcessAndValidate())
			return false;

		return true;
	}

	@Override
	protected RDFNode distillConstant(RDFNode node) {
		// If the constant-valued term map is a subject map, predicate map or graph map, 
		// then its constant value must be an IRI.
		if(!node.isURIResource()) {
			logger.error("Constant for GraphMap must be an IRI.");
			logger.error(description);
			return null;
		}

		return node;
	}

	@Override
	protected boolean isChosenTermTypeValid() {
		if(!isTermTypeIRI()) {
			logger.error("TermType for GraphMap must be rr:IRI.");
			logger.error(description);
			return false;
		}
		return true;
	}

	@Override
	protected Resource inferTermType() {
		return R2RML.IRI;
	}
	
	public Resource generateRDFTerm(Row row) throws R2RMLException {
		return super.generateRDFTerm(row).asResource();
	}

}
