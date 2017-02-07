package r2rml.engine;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

/**
 * R2RML Class.
 * 
 * @author Christophe Debruyne
 * @version 0.1
 *
 */
public final class R2RML {
	
	public static final String NS = "http://www.w3.org/ns/r2rml#";
	
	public static final Resource BLANKNODE = ResourceFactory.createResource(NS + "BlankNode");
	public static final Resource IRI = ResourceFactory.createResource(NS + "IRI");
	public static final Resource LITERAL = ResourceFactory.createResource(NS + "Literal");
	
	// Special resource for default graph
	public static final Resource defaultGraph = ResourceFactory.createResource(NS + "defaultGraph");

	// Classes
	public static final Resource BaseTableOrView = ResourceFactory.createResource(NS + "BaseTableOrView");
	public static final Resource GraphMap = ResourceFactory.createResource(NS + "GraphMap");
	public static final Resource LogicalTable = ResourceFactory.createResource(NS + "LogicalTable");
	public static final Resource ObjectMap = ResourceFactory.createResource(NS + "ObjectMap");
	public static final Resource R2RMLView = ResourceFactory.createResource(NS + "R2RMLView");
	public static final Resource RefObjectMap = ResourceFactory.createResource(NS + "RefObjectMap");
	public static final Resource SubjectMap = ResourceFactory.createResource(NS + "SubjectMap");
	public static final Resource TermMap = ResourceFactory.createResource(NS + "TermMap");
	public static final Resource TriplesMap = ResourceFactory.createResource(NS + "TriplesMap");
	
	// Properties
	public static final Property child = ResourceFactory.createProperty(NS + "child");
	public static final Property clazz = ResourceFactory.createProperty(NS + "class");
	public static final Property column = ResourceFactory.createProperty(NS + "column");
	public static final Property constant = ResourceFactory.createProperty(NS + "constant");
	public static final Property datatype = ResourceFactory.createProperty(NS + "datatype");
	public static final Property graph = ResourceFactory.createProperty(NS + "graph");
	public static final Property graphMap = ResourceFactory.createProperty(NS + "graphMap");
	public static final Property joinCondition = ResourceFactory.createProperty(NS + "joinCondition");
	public static final Property language = ResourceFactory.createProperty(NS + "language");
	public static final Property logicalTable = ResourceFactory.createProperty(NS + "logicalTable");
	public static final Property object = ResourceFactory.createProperty(NS + "object");
	public static final Property objectMap = ResourceFactory.createProperty(NS + "objectMap");
	public static final Property parent = ResourceFactory.createProperty(NS + "parent");
	public static final Property parentTriplesMap = ResourceFactory.createProperty(NS + "parentTriplesMap");
	public static final Property predicate = ResourceFactory.createProperty(NS + "predicate");
	public static final Property predicateMap = ResourceFactory.createProperty(NS + "predicateMap");
	public static final Property predicateObjectMap = ResourceFactory.createProperty(NS + "predicateObjectMap");
	public static final Property sqlQuery = ResourceFactory.createProperty(NS + "sqlQuery");
	public static final Property subject = ResourceFactory.createProperty(NS + "subject");
	public static final Property subjectMap = ResourceFactory.createProperty(NS + "subjectMap");
	public static final Property tableName = ResourceFactory.createProperty(NS + "tableName");
	public static final Property template = ResourceFactory.createProperty(NS + "template");
	public static final Property termType = ResourceFactory.createProperty(NS + "termType");
	
}
