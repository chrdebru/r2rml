package r2rml.model;

import org.apache.jena.rdf.model.Resource;

public interface Collectable {

	public boolean hasCollactAs();

	public Resource getCollectAsTermType();
	
}
