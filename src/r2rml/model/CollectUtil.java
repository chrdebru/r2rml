package r2rml.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jena.rdf.model.Container;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

public class CollectUtil {

	private static Map<String, ObjectSet> object_set_map = new HashMap<String, ObjectSet>();
	private static Map<String, PropertySet> property_set_map = new HashMap<String, PropertySet>();
	private static Map<String, GraphSet> graph_set_map = new HashMap<String, GraphSet>();
	private static Map<String, Collectable> objectMap_map = new HashMap<String, Collectable>();
	private static Map<String, Resource> subject_map = new HashMap<String, Resource>();
	
	public static void collect(PredicateObjectMap opm, Collectable om, Resource subject, RDFNode o, List<Property> predicates, Set<String> pogs) {
		String pom_om_sub = opm.toString() + om.toString() + subject;
		
		if(!object_set_map.containsKey(pom_om_sub)) {
			object_set_map.put(pom_om_sub, new ObjectSet());
			property_set_map.put(pom_om_sub, new PropertySet());
			graph_set_map.put(pom_om_sub, new GraphSet());
			objectMap_map.put(pom_om_sub, om);
			subject_map.put(pom_om_sub, subject);
		}
		
		object_set_map.get(pom_om_sub).add(o);
		property_set_map.get(pom_om_sub).addAll(predicates);
		graph_set_map.get(pom_om_sub).addAll(pogs);
	}

	public static Set<String> getCollectedKeys() {
		return object_set_map.keySet();
	}

	public static RDFNode getObject(String key) {
		Collectable om = objectMap_map.get(key);
		ObjectSet os = object_set_map.get(key);
		
		if(om.getCollectAsTermType().equals(RDF.List)) {
			RDFNode list = ModelFactory.createDefaultModel().createList(os.toArray(new RDFNode[] {}));
			for(RDFNode n : os) {
				if(n.getModel() != null)
					list.getModel().add(n.getModel());
			}
			return list;
		}
		
		Container c = null;
		
		if(om.getCollectAsTermType().equals(RDF.Bag))
			c = ModelFactory.createDefaultModel().createBag();
		else if(om.getCollectAsTermType().equals(RDF.Seq))
			c = ModelFactory.createDefaultModel().createSeq();
		else if(om.getCollectAsTermType().equals(RDF.Alt))
			c = ModelFactory.createDefaultModel().createAlt();
		else
			return null;
		
		for(RDFNode o : os) {
			if(o.getModel() != null)
				c.getModel().add(o.getModel());
			c.add(o);
		}
		
		return c;
	}

	public static Resource getSubject(String key) {
		return subject_map.get(key);
	}

	public static Set<String> getPredicateObjectGraphs(String key) {
		return graph_set_map.get(key);
	}

	public static Set<Property> getPredicates(String key) {
		return property_set_map.get(key);
	}

	public static void reset() {
		object_set_map.clear();
		property_set_map.clear();
		graph_set_map.clear();
		objectMap_map.clear();
		subject_map.clear();
	}

}

class ObjectSet extends LinkedHashSet<RDFNode> {
	private static final long serialVersionUID = 1L;
}

class PropertySet extends HashSet<Property> {
	private static final long serialVersionUID = 1L;
}

class GraphSet extends HashSet<String> {
	private static final long serialVersionUID = 1L;
}