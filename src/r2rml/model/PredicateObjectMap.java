package r2rml.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.apache.log4j.Logger;

import r2rml.engine.R2RML;
import r2rml.engine.RRF;

/**
 * PredicateObjectMap Class.
 * 
 * @author Christophe Debruyne
 * @version 0.2
 *
 */
public class PredicateObjectMap extends R2RMLResource {

	private static Logger logger = Logger.getLogger(PredicateObjectMap.class.getName());

	private List<GraphMap> graphMaps = new ArrayList<GraphMap>();
	private List<PredicateMap> predicateMaps = new ArrayList<PredicateMap>();
	private List<ObjectMap> objectMaps = new ArrayList<ObjectMap>();
	private List<RefObjectMap> refObjectMaps = new ArrayList<RefObjectMap>();

	private String baseIRI = null;

	public PredicateObjectMap(Resource description, String baseIRI) {
		super(description);
		this.baseIRI  = baseIRI;
	}

	public List<PredicateMap> getPredicateMaps() {
		return predicateMaps;
	}

	public List<ObjectMap> getObjectMaps() {
		return objectMaps;
	}

	public List<RefObjectMap> getRefObjectMaps() {
		return refObjectMaps;
	}

	@Override
	public boolean preProcessAndValidate() {
		logger.info("Processing PredicateObjectMap " + description);

		/*
		 * PredicateObjectMaps must have one or more PredicateMaps and one or
		 * more ObjectMaps. Since we generate classes via the shorthands before
		 * processing, we just check rr:predicateMap and rr:objectMap.
		 */
		// Check whether PredicateMaps exists
		List<Statement> listp = description.listProperties(R2RML.predicateMap).toList();
		if(listp.size() == 0) {
			logger.error("PredicateObjectMap must have at least one rr:predicateMap.");
			logger.error(description);
			return false;
		}

		// Pre-process and validate each PredicateMap
		for(Statement s : listp) {
			if(!s.getObject().isResource()) {
				logger.error("rr:predicateMap must refer to a resource.");
				logger.error(description);
				return false;
			}
			PredicateMap pm = new PredicateMap(s.getObject().asResource(), baseIRI);
			if(!pm.preProcessAndValidate())
				return false;
			predicateMaps.add(pm);
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

		// Check whether ObjectMaps exists
		List<Statement> listo = description.listProperties(R2RML.objectMap).toList();
		if(listo.size() == 0) {
			logger.error("PredicateObjectMap must have at least one rr:objectMap.");
			logger.error(description);
			return false;
		}

		// Pre-process and validate each ObjectMap or RefObjectMap
		for(Statement s : listo) {
			if(!s.getObject().isResource()) {
				logger.error("rr:objectMap must refer to a resource.");
				logger.error(description);
				return false;
			}

			Resource r = s.getObject().asResource();

			/*
			 * Because of all the OWL axioms, it is difficult to infer
			 * whether resources are a ObjectMap or a RefObjectMap. This 
			 * is because after reasoning, those instances are members of
			 * concepts that are OM or ROM concept union (!) something
			 * else. One should be able to deduce that from the roles they
			 * play however.
			 * TODO: investigate if we can't configure the reasoner and execute more elegantly.
			 */
			boolean isOM = r.hasProperty(R2RML.column) 
					|| r.hasProperty(R2RML.constant) 
					|| r.hasProperty(R2RML.template)
					|| r.hasProperty(RRF.functionCall)
					|| r.hasProperty(RDF.type, R2RML.ObjectMap);
			boolean isROM = r.hasProperty(R2RML.joinCondition) 
					|| r.hasProperty(RDF.type, R2RML.RefObjectMap);

			// If it plays the roles of a OM, create OM
			if(isOM && !isROM) {
				ObjectMap om = new ObjectMap(r, baseIRI);
				if(!om.preProcessAndValidate())
					return false;
				objectMaps.add(om);
			} 
			// If it plays the role of a ROM, create ROM
			else if(isROM && !isOM) {
				RefObjectMap rom = new RefObjectMap(r);
				if(!rom.preProcessAndValidate())
					return false;
				refObjectMaps.add(rom);
			} 
			// Can't be both!
			else if (isOM && isROM){
				logger.error("Resource cannot be both an ObjectMap or a RefObjectMap.");
				logger.error(description);
				return false;
			} 
			// Can't be neither...
			else {
				logger.error("rr:objectMap must refer to an ObjectMap or a RefObjectMap.");
				logger.error(description);
				return false;
			}
		}

		return true;
	}

	public List<GraphMap> getGraphMaps() {
		return graphMaps;
	}

}
