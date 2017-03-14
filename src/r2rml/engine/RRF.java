package r2rml.engine;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;

/**
 * R2RML Class.
 * 
 * @author Christophe Debruyne
 * @version 0.1
 *
 */
public final class RRF {
	
	public static final String NS = "http://kdeg.scss.tcd.ie/ns/rrf#";
	
	// Classes
	
	// Properties
	public static final Property function = ResourceFactory.createProperty(NS + "function");
	public static final Property functionCall = ResourceFactory.createProperty(NS + "functionCall");
	public static final Property functionBody = ResourceFactory.createProperty(NS + "functionBody");
	public static final Property functionName = ResourceFactory.createProperty(NS + "functionName");
	public static final Property parameterBindings = ResourceFactory.createProperty(NS + "parameterBindings");
	
	// Properties for Containers and Collections
	public static final Property gather = ResourceFactory.createProperty(NS + "gather");
	
}
