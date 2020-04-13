package r2rml.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Resource;

import r2rml.database.DB;

/**
 * R2RMLMapping Class.
 * 
 * @author Christophe Debruyne
 * @version 0.1
 *
 */
public class R2RMLMapping {

	private Map<Resource, TriplesMap> triplesMaps = new HashMap<Resource, TriplesMap>();

	public void addTriplesMap(Resource triplesMapResource, TriplesMap triplesMap) {
		triplesMaps.put(triplesMapResource , triplesMap);
	}

	public Map<Resource, TriplesMap> getTriplesMaps() {
		return triplesMaps;
	}

	public void setTriplesMaps(Map<Resource, TriplesMap> triplesMaps) {
		this.triplesMaps = triplesMaps;
	}

	public boolean generateTriples(DB database, Dataset dataset) {
		for(TriplesMap tm : triplesMaps.values()) {
			if(!tm.generateTriples(database, dataset, triplesMaps)){
				System.out.println("Something went wrong processing: " + tm);
				return false;
			}
		}
		return true;
	}

}
