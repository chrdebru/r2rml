package r2rml.model;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import r2rml.database.Row;
import r2rml.engine.R2RML;
import r2rml.engine.R2RMLException;

/**
 * PredicateMap Class.
 * 
 * @author Christophe Debruyne
 * @version 0.1
 *
 */
public class PredicateMap extends TermMap {

	private static Logger logger = LogManager.getLogger(PredicateMap.class);

	public PredicateMap(Resource description, String baseIRI) {
		super(description, baseIRI);
	}

	@Override
	public boolean preProcessAndValidate() {
		logger.info("Processing PredicateMap " + description);
		
		if(!super.preProcessAndValidate())
			return false;

		return true;
	}

	@Override
	protected RDFNode distillConstant(RDFNode node) {
		// If the constant-valued term map is a subject map, predicate map or graph map, 
		// then its constant value must be an IRI.
		if(!node.isURIResource()) {
			logger.error("Constant for PredicateMap must be an IRI.");
			logger.error(description);
			return null;
		}

		return node;
	}

	@Override
	protected boolean isChosenTermTypeValid() {
		if(!isTermTypeIRI()) {
			logger.error("TermType for PredicateMap must be rr:IRI.");
			logger.error(description);
			return false;
		}
		
		return true;
	}

	@Override
	protected Resource inferTermType() {
		return R2RML.IRI;
	}
	
	public Property generateRDFTerm(Row row) throws R2RMLException {
		// PredicateMaps generate terms that are properties.
		RDFNode node = super.generateRDFTerm(row);
		if(node != null)
			return ResourceFactory.createProperty(node.asResource().getURI());
		return null;
	}

}
