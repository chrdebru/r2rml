package r2rml.model;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.RDF;
import org.apache.log4j.Logger;

import r2rml.engine.R2RML;
import r2rml.engine.RRF;

/**
 * R2RMLMappingFactory Class.
 * 
 * @author Christophe Debruyne
 * @version 0.1
 *
 */
public class R2RMLMappingFactory {

	private static Logger logger = Logger.getLogger(R2RMLMappingFactory.class.getName());

	private static String CONSTRUCTSMAPS = "PREFIX rr: <http://www.w3.org/ns/r2rml#> CONSTRUCT { ?x rr:subjectMap [ rr:constant ?y ]. } WHERE { ?x rr:subject ?y. }";
	private static String CONSTRUCTOMAPS = "PREFIX rr: <http://www.w3.org/ns/r2rml#> CONSTRUCT { ?x rr:objectMap [ rr:constant ?y ]. } WHERE { ?x rr:object ?y. }";
	private static String CONSTRUCTPMAPS = "PREFIX rr: <http://www.w3.org/ns/r2rml#> CONSTRUCT { ?x rr:predicateMap [ rr:constant ?y ]. } WHERE { ?x rr:predicate ?y. }";
	private static String CONSTRUCTGMAPS = "PREFIX rr: <http://www.w3.org/ns/r2rml#> CONSTRUCT { ?x rr:graphMap [ rr:constant ?y ]. } WHERE { ?x rr:graph ?y. }";

	// MAKE CONSTANTS EXPLICITELY LITERALS TO DEAL WITH TYPED CONSTANTS
	// EVEN THOUGH IT IS OUT OF THE SPEC
	private static String CONSTRUCTLITERAL = "PREFIX rr: <http://www.w3.org/ns/r2rml#> CONSTRUCT { ?x rr:termType rr:Literal . } WHERE { ?x rr:constant ?y . FILTER (isLiteral(?y)) }" ;
	
	public static R2RMLMapping createR2RMLMapping(String mappingFile, String baseIRI) {
		R2RMLMapping mapping = new R2RMLMapping();

		// We reason over the mapping to facilitate retrieval of the mappings
		Model data = FileManager.get().loadModel(mappingFile);
		
		// We construct triples to replace the shortcuts.
		data.add(QueryExecutionFactory.create(CONSTRUCTSMAPS, data).execConstruct());
		data.add(QueryExecutionFactory.create(CONSTRUCTOMAPS, data).execConstruct());
		data.add(QueryExecutionFactory.create(CONSTRUCTPMAPS, data).execConstruct());
		data.add(QueryExecutionFactory.create(CONSTRUCTGMAPS, data).execConstruct());
		
		data.add(QueryExecutionFactory.create(CONSTRUCTLITERAL, data).execConstruct());
		
		Model schema = ModelFactory.createDefaultModel();
		schema.read(R2RMLMappingFactory.class.getResourceAsStream("/r2rml.rdf"), null);

		// Model schema = FileManager.get().loadModel("./resources/r2rml.rdf");
		InfModel mappingmodel = ModelFactory.createRDFSModel(schema, data);

		// Check to see if we have functions that we can retrieve over HTTP
		List<RDFNode> functions = mappingmodel.listObjectsOfProperty(RRF.function).toList();
		for(RDFNode n : functions) {
			if(n.isURIResource()) {
				String uri = n.asResource().getURI();
				if(isValidURL(uri)) {
					logger.info("Detected function with URI that is a URL. Try to fetch it.");
					try {
						Model m = ModelFactory.createDefaultModel();
						m.read(uri);
						mappingmodel.add(m);
					} catch (Exception e) {
						logger.warn("Couldn't fetch resource via URI " + uri + " (" + e.getMessage() + ")");
						logger.warn("We will continue and hope for the best. :-)");
					}
				}

			}
		}

		// Look for the TriplesMaps
		List<Resource> list = mappingmodel.listSubjectsWithProperty(RDF.type, R2RML.TriplesMap).toList();

		if(list.isEmpty()) {
			logger.error("R2RML Mapping File has no TriplesMaps.");
			return null;
		}

		for(Resource tm : list) {
			TriplesMap triplesMap = new TriplesMap(tm, baseIRI);
			if(!triplesMap.preProcessAndValidate()) {
				// Something went wrong, abort.
				return null;
			}
			mapping.addTriplesMap(tm, triplesMap);
		}		

		return mapping;
	}

	/**
	 * Small utility function  to test URL based on 
	 * http://stackoverflow.com/questions/1600291/validating-url-in-java
	 * 
	 * @param uri
	 * @return
	 */
	private static boolean isValidURL(String uri) {
		try {  
			URL u = new URL(uri);  
			u.toURI();
		} catch (MalformedURLException e) {  
			return false;  
		} catch (URISyntaxException e) {
			return false;
		}
		return true; 
	}


}
