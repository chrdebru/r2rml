package r2rml.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Gather Class.
 * 
 * @author Christophe Debruyne
 * @version 0.1
 *
 */

public class Gather {
	
	private List<TermMap> termMaps = new ArrayList<TermMap>();
		
	public List<TermMap> getTermMaps() {
		return termMaps;
	}

	public void addTermMap(TermMap termMap) {
		termMaps.add(termMap);
	}
}
