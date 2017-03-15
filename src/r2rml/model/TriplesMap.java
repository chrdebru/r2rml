package r2rml.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Alt;
import org.apache.jena.rdf.model.Bag;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Seq;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.apache.log4j.Logger;

import r2rml.database.DB;
import r2rml.database.Row;
import r2rml.database.Rows;
import r2rml.engine.R2RML;
import r2rml.engine.R2RMLException;

/**
 * TriplesMap Class.
 * 
 * @author Christophe Debruyne
 * @version 0.1
 *
 */
public class TriplesMap extends R2RMLResource {

	private static Logger logger = Logger.getLogger(TriplesMap.class.getName());

	private LogicalTable logicalTable = null;
	private SubjectMap subjectMap = null;
	private List<PredicateObjectMap> predicateObjectMaps = new ArrayList<PredicateObjectMap> ();
	private String baseIRI = null;

	private int count = 0;

	public TriplesMap(Resource description, String baseIRI) {
		super(description);
		this.setBaseIRI(baseIRI);
	}

	public LogicalTable getLogicalTable() {
		return logicalTable;
	}

	public void setLogicalTable(LogicalTable logicalTable) {
		this.logicalTable = logicalTable;
	}

	public SubjectMap getSubjectMap() {
		return subjectMap;
	}

	public void setSubjectMap(SubjectMap subjectMap) {
		this.subjectMap = subjectMap;
	}

	public List<PredicateObjectMap> getPredicateObjectMaps() {
		return predicateObjectMaps;
	}

	public void setPredicateObjectMaps(List<PredicateObjectMap> predicateObjectMaps) {
		this.predicateObjectMaps = predicateObjectMaps;
	}

	@Override
	public boolean preProcessAndValidate() {
		logger.info("Processing TriplesMap " + description);

		// TermMap must have an rr:logicalTable property (exactly one?)
		List<Statement> list = description.listProperties(R2RML.logicalTable).toList();
		if(list.size() != 1) {
			logger.error("TriplesMap must have exactly one rr:logicalTable property.");
			logger.error(description);
			return false;
		}

		RDFNode node = list.get(0).getObject();
		if(!node.isResource()) {
			logger.error("LogicalTable of TriplesMap is not a resource.");
			logger.error(description);
			return false;
		}

		// Pre-process and validate LogicalTable
		logicalTable = new LogicalTable(node.asResource());
		if(!logicalTable.preProcessAndValidate())
			return false;

		// TermMap must have exactly one of rr:subject and rr:subjectMap
		// But we constructed rr:subjectMap from rr:subject, thus only check one!
		list = description.listProperties(R2RML.subjectMap).toList();		
		if(list.size() != 1) {
			logger.error("TriplesMap must have exactly one one of rr:subject and rr:subjectMap.");
			logger.error(description);
			return false;
		}

		node = list.get(0).getObject();
		if(!node.isResource()) {
			logger.error("SubjectMap of TriplesMap is not a resource.");
			logger.error(description);
			return false;
		}

		// Pre-process and validate SubjectMap
		subjectMap = new SubjectMap(node.asResource(), baseIRI);
		if(!subjectMap.preProcessAndValidate())
			return false;

		// Pre-process and validate PredicateObjectMaps
		// TriplesMaps may have zero or more PredicateObjectMaps
		// Just iterate over them.
		list = description.listProperties(R2RML.predicateObjectMap).toList();
		for(Statement s : list) {
			node = s.getObject();
			if(!node.isResource()) {
				logger.error("PredicateObjectMap is not a resource.");
				logger.error(description);
				return false;
			}

			PredicateObjectMap opm = new PredicateObjectMap(s.getObject().asResource(), baseIRI);
			if(!opm.preProcessAndValidate())
				return false;
			predicateObjectMaps.add(opm);
		}

		return true;
	}

	public boolean generateTriples(
			DB database, 
			Dataset dataset, 
			Map<Resource, TriplesMap> triplesMaps) {
		
		try {
			String query = getLogicalTable().generateQuery();
			Rows rows = database.getRows(query);

			List<Resource> classes = getSubjectMap().getClasses();
			List<GraphMap> sgm = getSubjectMap().getGraphMaps();

			logger.info("TriplesMap " + description + ": start generating triples.");

			int child_column_count = rows.getColumnCount();

			Row row = null;
			while((row = rows.nextRow()) != null) {
				Resource subject = getSubjectMap().generateRDFTerm(row);

				// According to term generation rules, if the value is null,
				// then no RDFTerm is generated. If that is the case for the
				// subject, then we do not generate RDF for that resource.
				if(subject != null) {

					Set<String> subjectGraphs = new HashSet<String>();
					for(GraphMap gm : sgm) {
						Resource gmiri = gm.generateRDFTerm(row);
						if(gmiri != null)
							subjectGraphs.add(gmiri.getURI());
					}

					for(Resource c : classes) {
						addTriplesToDataset(dataset, subject, RDF.type, c, subjectGraphs);
					}

					// Now process each predicate-object map of the triples map
					for(PredicateObjectMap opm : getPredicateObjectMaps()) {
						// Let predicates be the set of generated RDF terms that 
						// result from applying each of the predicate-object 
						// map's predicate maps to row
						List<Property> predicates = new ArrayList<Property>();
						for(PredicateMap pm : opm.getPredicateMaps()) {
							Property p = pm.generateRDFTerm(row);
							if(p != null)
								predicates.add(p);
						}

						// Let objects be the set of generated RDF terms that result 
						// from applying each of the predicate-object map's object 
						// maps (but not referencing object maps) to row
						List<RDFNode> objects = new ArrayList<RDFNode>();
						for(ObjectMap om : opm.getObjectMaps()) {
							RDFNode o = om.generateRDFTerm(row); 
							if(o != null)
								objects.add(o);
						}

						// Let pogm be the set of graph maps of the predicate-object 
						// map. Let predicate-object_graphs be the set of generated 
						// RDF terms that result from applying each graph map in pogm 
						// to row
						Set<String> pogs = new HashSet<String>();
						for(GraphMap gm : opm.getGraphMaps()) {
							Resource gmiri = gm.generateRDFTerm(row);
							if(gmiri != null)
								pogs.add(gmiri.getURI());
						}

						// Target graphs: If sgm and pogm are empty: rr:defaultGraph; 
						// otherwise: union of subject_graphs and predicate-object_graphs
						pogs.addAll(subjectGraphs);
						for(Property p : predicates) {
							for(RDFNode o : objects) {
								addTriplesToDataset(dataset, subject, p, o, pogs);
							}
						}


					}
				}
			} // end while

			// For each referencing object map of a predicate-object map of 
			// the triples map, apply the following steps:
			for(PredicateObjectMap opm : getPredicateObjectMaps()) {
				for(RefObjectMap rof : opm.getRefObjectMaps()) {
					TriplesMap ptm = triplesMaps.get(rof.getParentTriplesMap());
					SubjectMap psm = ptm.getSubjectMap();
					List<GraphMap> pogm = opm.getGraphMaps();
					
					String jointQuery = R2RMLUtil.createJointQuery(this, ptm, rof.getJoins());
					if(jointQuery == null)
						return false;

					Rows rows2 = database.getRows(jointQuery);
					int column_count = rows2.getColumnCount();
					// For each row in rows2...
					while(rows2.nextRow() != null) {
						Row child_row = rows2.projectCurrentRow(1, child_column_count);
						Row parent_row = rows2.projectCurrentRow(child_column_count + 1, column_count);

						// Let subject be the generated RDF term that results 
						// from applying sm to "child_row"
						Resource subject = getSubjectMap().generateRDFTerm(child_row);

						// Let object be the generated RDF term that results 
						// from applying psm to parent_row
						Resource object = psm.generateRDFTerm(parent_row);
						
						// if subject or object is NULL, don't generate triples
						if(subject != null || object != null) {

							// Let predicates be the set of generated RDF terms that result 
							// from applying each of the predicate-object map's predicate 
							// maps to child_row
							List<Property> predicates = new ArrayList<Property>();
							for(PredicateMap pm : opm.getPredicateMaps()) {
								Property p = pm.generateRDFTerm(child_row);
								if(p != null)
									predicates.add(p);
							}

							// Let subject_graphs be the set of generated RDF terms 
							// that result from applying each graph map of sgm to 
							// child_row
							Set<String> subjectGraphs = new HashSet<String>();
							for(GraphMap gm : sgm) {
								Resource gmiri = gm.generateRDFTerm(child_row);
								if(gmiri != null)
									subjectGraphs.add(gmiri.getURI());
							}

							// Let predicate-object_graphs be the set of generated 
							// RDF terms that result from applying each graph map in 
							// pogm to child_row
							Set<String> pogs = new HashSet<String>();
							for(GraphMap gm : pogm) {
								Resource gmiri = gm.generateRDFTerm(child_row);
								if(gmiri != null)
									pogs.add(gmiri.getURI());
							}

							// Target graphs: If sgm and pogm are empty: rr:defaultGraph; 
							// otherwise: union of subject_graphs and predicate-object_graphs
							pogs.addAll(subjectGraphs);
							for(Property p : predicates) {
								addTriplesToDataset(dataset, subject, p, object, pogs);
							}
						}
					}
				}
			}

			logger.info("TriplesMap " + description + ": generated triples = " + count);

		} catch (R2RMLException e) {
			logger.error("R2RMLException.", e);
			logger.error(description);
			return false;
		} finally {
			if(database != null) {
				try {
					database.close();
				} catch (R2RMLException e) {
					logger.error("R2RMLException.", e);
					logger.error(description);
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Adding triples to the dataset. If the set of named graphs is empty, they
	 * added to the default model.
	 * 
	 * @param ds The dataset
	 * @param s The subject
	 * @param p The predicate
	 * @param o The object
	 * @param ngs The set of named graphs
	 */
	private void addTriplesToDataset(Dataset ds, Resource s, Property p, RDFNode o, Set<String> ngs) {
		// If empty, then it will be stored in the default graph
		// Explicitly add the default graph
		if(ngs.isEmpty())
			ngs.add(R2RML.defaultGraph.getURI());

		// If container or list, add the default model of to each of the graphs!
		if(isListOrContainer(o)) {
			for(String ng : ngs) {
				if(ng.equals(R2RML.defaultGraph.getURI())) {
					ds.getDefaultModel().add(o.getModel());
				} else {
					ds.getNamedModel(ng).add(o.getModel());
				}
			}
		}
		
		for(String ng : ngs) {
			if(ng.equals(R2RML.defaultGraph.getURI())) {
				ds.getDefaultModel().add(s, p, o);
				count++;
			} else {
				ds.getNamedModel(ng).add(s, p, o);
				count++;
			}
		}
	}

	private boolean isListOrContainer(RDFNode o) {
		if(o.isResource()) {
			Resource r = o.asResource();
			if(r.canAs(RDFList.class) || r.canAs(Bag.class) || r.canAs(Seq.class) || r.canAs(Alt.class)) {
				return true;
			}
		}
		return false;
	}

	public String getBaseIRI() {
		return baseIRI;
	}

	public void setBaseIRI(String baseIRI) {
		this.baseIRI = baseIRI;
	}

}
