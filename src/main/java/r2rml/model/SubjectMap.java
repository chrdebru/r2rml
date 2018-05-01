package r2rml.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.log4j.Logger;

import r2rml.database.Row;
import r2rml.engine.R2RML;
import r2rml.engine.R2RMLException;

/**
 * SubjectMap Class.
 * 
 * @author Christophe Debruyne
 * @version 0.1
 *
 */
public class SubjectMap extends TermMap {
	
	private static Logger logger = Logger.getLogger(SubjectMap.class.getName());

	private List<GraphMap> graphMaps = new ArrayList<GraphMap>();
	private List<Resource> classes = new ArrayList<Resource>();

	public SubjectMap(Resource description, String baseIRI) {
		super(description, baseIRI);
	}

	@Override
	public boolean preProcessAndValidate() {
		if(!super.preProcessAndValidate())
			return false;
		
		logger.info("Processing SubjectMap " + description);
		
		// Minimum conditions
		// Being an rr:TermMap -- covered by inference
		// Being value of an rr:subjectMap property
		// TODO: Check "Being value of an rr:subjectMap property" in engine
		
		List<Statement> list = description.listProperties(R2RML.clazz).toList();
		for(Statement s : list) {
			if(!s.getObject().isURIResource()) {
				logger.error("Value of rr:class must be an IRI.");
				logger.error(description);
				return false;
			}
			classes.add(s.getObject().asResource());
		}
		
		// Pre-process and validate each GraphMap
		List<Statement> listg = description.listProperties(R2RML.graphMap).toList();
		for(Statement s : listg) {
			if(!s.getObject().isResource()) {
				logger.error("rr:graphMap must refer to a resource.");
				logger.error(description);
				return false;
			}
			GraphMap gm = new GraphMap(s.getObject().asResource(), baseIRI);
			if(!gm.preProcessAndValidate())
				return false;
			graphMaps.add(gm);
		}
		
		return true;
	}

	@Override
	protected RDFNode distillConstant(RDFNode node) {
		// If the constant-valued term map is a subject map, predicate map or graph map, 
		// then its constant value must be an IRI.
		if(isConstantValuedTermMap()) {
			if(!node.isURIResource()) {
				logger.error("Constant for SubjectMap must be an IRI.");
				logger.error(description);
				return null;
			}
		}
		return node;
	}

	@Override
	protected boolean isChosenTermTypeValid() {
		if(!(isTermTypeIRI() || isTermTypeBlankNode())) {
			logger.error("TermType for SubjectMap must be rr:IRI or rr:BlankNode.");
			logger.error(description);
			return false;
		}
		return true;
	}

	@Override
	protected Resource inferTermType() {
		return R2RML.IRI;
	}

	public List<GraphMap> getGraphMaps() {
		return graphMaps;
	}

	public List<Resource> getClasses() {
		return classes;
	}

	public Resource generateRDFTerm(Row row) throws R2RMLException {
		RDFNode r = super.generateRDFTerm(row);
		if(r != null) 
			return r.asResource();
		return null;
	}
	
}
