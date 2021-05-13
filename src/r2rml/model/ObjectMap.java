package r2rml.model;

import java.util.List;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.web.LangTag;
import org.apache.log4j.Logger;

import r2rml.engine.R2RML;

/**
 * ObjectMap Class.
 * 
 * TODO: Implement inferring datatypes
 * 
 * @author Christophe Debruyne
 * @version 0.2
 *
 */
public class ObjectMap extends TermMap {

	private static Logger logger = Logger.getLogger(ObjectMap.class.getName());
	private List<Statement> datatypes = null;
	private List<Statement> languages = null;

	public ObjectMap(Resource description, String baseIRI) {
		super(description, baseIRI);
		datatypes = description.listProperties(R2RML.datatype).toList();
		languages = description.listProperties(R2RML.language).toList();
	}

	@Override
	public boolean preProcessAndValidate() {
		logger.info("Processing ObjectMap " + description);

		if(!super.preProcessAndValidate())
			return false;

		if(isTermTypeIRI() && (datatypes.size() > 0 || languages.size() > 0)) {
			logger.error("TermType IRI cannot have a rr:datatype or rr:language.");
			logger.error(description);
			return false;
		}
		
		if(isTermTypeLiteral()) {
			if(datatypes.size() > 1) {
				logger.error("A TermMap must not have more than one rr:datatype value.");
				logger.error(description);
				return false;
			}

			if(languages.size() > 1) {
				logger.error("A TermMap must not have more than one rr:language value.");
				logger.error(description);
				return false;
			}

			if(languages.size() == 1 && datatypes.size() == 1) {
				System.out.println(languages);
				System.out.println(datatypes);
				logger.error("A TermMap cannot have both a rr:datatype and rr:language.");
				logger.error(description);
				return false;
			}

			if(languages.size() == 1) {
				RDFNode node = languages.get(0).getObject();
				if(!node.isLiteral() && !LangTag.check(node.asLiteral().getValue().toString())) {
					logger.error("The value of rr:language must be a valid language tag.");
					logger.error(description);
					return false;
				}

				language = node.asLiteral().toString();
			}

			if(datatypes.size() == 1) {
				RDFNode node = datatypes.get(0).getObject();
				if(!node.isURIResource()) {
					logger.error("The value of rr:datatype must be an IRI.");
					logger.error(description);
					return false;
				}

				datatype = node.asResource();
			}

		}

		return true;
	}

	@Override
	protected RDFNode distillConstant(RDFNode node) {
		// If the constant-valued term map is an object map, then its 
		// constant value must be an IRI or literal.
		if(!node.isURIResource() && !node.isLiteral()) {
			logger.error("Constant for ObjectMap must be an IRI or literal.");
			logger.error(description);
			return null;
		}

		return node;
	}

	@Override
	protected boolean isChosenTermTypeValid() {
		// Check if invalid URI was provided!
		if(!(isTermTypeLiteral() || isTermTypeBlankNode() || isTermTypeIRI())) {
			logger.error("TermType for ObjectMap must be rr:IRI, rr:Literal or rr:BlankNode.");
			logger.error(description);
			return false;
		}
		return true;
	}

	@Override
	protected Resource inferTermType() {
		/* rr:Literal, if it is an object map and at least one
		 * of the following conditions is true:
		 * - It is a column-based term map.
		 * - It has a rr:language property.
		 * - It has a rr:datatype property. 
		 * rr:IRI, otherwise*/

		if(isColumnValuedTermMap() || datatypes.size() > 0 || languages.size() > 0) 
			return R2RML.LITERAL;

		/* We assume that functions are also by default literals */
		if(isFunctionValuedTermMap())
			return R2RML.LITERAL;

		return R2RML.IRI;
	}
}
