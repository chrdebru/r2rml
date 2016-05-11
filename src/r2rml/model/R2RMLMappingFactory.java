package r2rml.model;

import java.util.List;

import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.RDF;
import org.apache.log4j.Logger;

import r2rml.engine.R2RML;

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

	public static R2RMLMapping createR2RMLMapping(String mappingFile, String baseIRI) {
		R2RMLMapping mapping = new R2RMLMapping();
		
		// We reason over the mapping to facilitate retrieval of the mappings
		Model data = FileManager.get().loadModel(mappingFile);

		// We construct triples to replace the shortcuts.
		data.add(QueryExecutionFactory.create(CONSTRUCTSMAPS, data).execConstruct());
		data.add(QueryExecutionFactory.create(CONSTRUCTOMAPS, data).execConstruct());
		data.add(QueryExecutionFactory.create(CONSTRUCTPMAPS, data).execConstruct());
		data.add(QueryExecutionFactory.create(CONSTRUCTGMAPS, data).execConstruct());
		
		Model schema = ModelFactory.createDefaultModel();
		schema.read(R2RMLMappingFactory.class.getResourceAsStream("/r2rml.rdf"), null);
		
		//Model schema = FileManager.get().loadModel("./resources/r2rml.rdf");
		InfModel mappingmodel = ModelFactory.createRDFSModel(schema, data);

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


}
