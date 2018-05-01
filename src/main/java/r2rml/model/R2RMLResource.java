package r2rml.model;

import org.apache.jena.rdf.model.Resource;

/**
 * R2RMLResource Class.
 * 
 * @author Christophe Debruyne
 * @version 0.1
 *
 */
public abstract class R2RMLResource {
	
	abstract protected boolean preProcessAndValidate();

	protected Resource description = null;
	
	public R2RMLResource(Resource description) {
		this.description = description;
	}
	
}
