package r2rml.model;

import java.util.List;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import r2rml.engine.R2RML;

/**
 * Join Class.
 * 
 * @author Christophe Debruyne
 * @version 0.1
 *
 */
public class Join extends R2RMLResource {

	private static Logger logger = LogManager.getLogger(Join.class);

	private String child;
	private String parent;

	public Join(Resource description) {
		super(description);
	}

	@Override
	public boolean preProcessAndValidate() {
		logger.info("Processing Join " + description);

		List<Statement> list = description.listProperties(R2RML.child).toList();
		if(list.size() != 1) {
			logger.error("Join must have exactly one rr:child.");
			logger.error(description);
			return false;
		}

		RDFNode node = list.get(0).getObject();
		if(!node.isLiteral()) {
			logger.error("rr:child has to be a literal.");
			logger.error(description);
			return false;
		}

		child = node.asLiteral().getValue().toString();
		
		list = description.listProperties(R2RML.parent).toList();
		if(list.size() != 1) {
			logger.error("Join must have exactly one rr:parent.");
			logger.error(description);
			return false;
		}

		node = list.get(0).getObject();
		if(!node.isLiteral()) {
			logger.error("rr:parent has to be a literal.");
			logger.error(description);
			return false;
		}

		parent = node.asLiteral().getValue().toString();

		return true;
	}

	public String getChild() {
		return child;
	}

	public String getParent() {
		return parent;
	}

}
