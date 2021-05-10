package r2rml.model;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.enhanced.UnsupportedPolymorphismException;
import org.apache.jena.iri.IRI;
import org.apache.jena.iri.IRIFactory;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.XSD;
import org.apache.log4j.Logger;

import r2rml.database.Row;
import r2rml.engine.R2RML;
import r2rml.engine.R2RMLException;
import r2rml.engine.R2RMLTypeMapper;
import r2rml.engine.RRF;
import r2rml.function.JSEnv;
import r2rml.util.IRISafe;

/**
 * TermMap Class.
 * 
 * @author Christophe Debruyne
 * @version 0.2
 *
 */
public abstract class TermMap extends R2RMLResource {

	private static Logger logger = Logger.getLogger(TermMap.class.getName());
	private Resource termType = null;

	/* 
	 * Term generation rules for blank nodes. If the term type is rr:BlankNode:
	 * Return a blank node that is unique to the natural RDF lexical form 
	 * corresponding to value. This seems to imply that there is a one-on-one
	 * mapping for each value and blank node. We will thus map the outcome
	 * of the constant, template, or column to the same blank node. In other
	 * words, if two TermMaps use "test"^^xsd:string, return same blank node.
	 * "1"^^xsd:string and "1"^^xsd:integer are different.
	 * 
	 */
	private static Map<Object, Resource> blankNodeMap = new HashMap<Object, Resource>();

	private String template;
	protected RDFNode constant;
	private String column;
	private FunctionCall functionCall;

	protected String language = null;
	protected Resource datatype = null;
	protected String baseIRI = null;

	public TermMap(Resource description, String baseIRI) {
		super(description);
		this.baseIRI = baseIRI;
	}

	@Override
	protected boolean preProcessAndValidate() {
		logger.info("Processing TermMap " + description);

		List<Statement> templates = description.listProperties(R2RML.template).toList();
		List<Statement> constants = description.listProperties(R2RML.constant).toList();
		List<Statement> columns = description.listProperties(R2RML.column).toList();
		List<Statement> functions = description.listProperties(RRF.functionCall).toList();

		// Having exactly one of rr:constant, rr:column, rr:template
		if(templates.size() + constants.size() + columns.size() + functions.size() != 1) {
			logger.error("TermMap must have exactly one of rr:constant, rr:column, rr:template, and rrf:functionCall.");
			logger.error(description);
			return false;
		}

		// The value of the rr:column property must be a valid column name.
		if(columns.size() == 1) {
			column = distillColumnName(columns.get(0).getObject());
			if(column == null) {
				logger.error("The value of the rr:column property must be a valid column name.");
				logger.error(description);
				return false;
			}
		} else if(templates.size() == 1) {
			// Check whether it is a valid template
			template = distillTemplate(templates.get(0).getObject());
			if(template == null) {
				logger.error("The value of the rr:template property must be a valid string template.");
				logger.error(description);
				return false;
			}

			// Check whether the referenced column names are valid
			for(String columnName : getReferencedColumns()) {
				if(!R2RMLUtil.isValidColumnName(columnName)) {
					logger.error("Invalid column name in rr:template: " + columnName);
					logger.error(description);
					return false;
				}
			}
		} else if(constants.size() == 1) {
			// the check for ConstantValuedTermMaps are local (different rules
			// for different TermMaps.
			constant = distillConstant(constants.get(0).getObject());
			if(constant == null)
				return false;
		} else if(functions.size() == 1) {
			functionCall = distillFunction(functions.get(0).getObject());
			if(functionCall == null)
				return false;

			// Check whether the referenced column names are valid
			for(String columnName : getReferencedColumns()) {
				if(!R2RMLUtil.isValidColumnName(columnName)) {
					logger.error("Invalid column name in rrf:functionCall " + columnName);
					logger.error(description);
					return false;
				}
			}
		}

		// Validity of the termType is also local. 
		// At most one and compute default one if absent.
		List<Statement> list = description.listProperties(R2RML.termType).toList();
		if(list.size() > 1) {
			logger.error("TermMap can have at most one rr:termType.");
			logger.error(description);
			return false;
		} else if (list.size() == 0) {
			termType = inferTermType();
		} else {
			// We have exactly one value. Check validity.
			// Is it a valid IRI?
			if(!list.get(0).getObject().isURIResource()) {
				logger.error("TermMap's rr:termType must be a valid IRI.");
				logger.error(description);
				return false;
			}

			termType = list.get(0).getObject().asResource();
			// Is it a valid option?
			if(!isChosenTermTypeValid())
				return false;
		}

		return true;
	}

	private FunctionCall distillFunction(RDFNode node) {
		if(node.isLiteral()) {
			logger.error("FunctionCall cannot be a literal.");
			return null;
		}

		// fcn stands for Function Call Node
		Resource fcn = node.asResource();

		List<Statement> functions = fcn.listProperties(RRF.function).toList();
		if(functions.size() != 1) {
			logger.error("Function valued TermMap must have exactly one rrf:function.");
			logger.error(description);
			return null;
		}

		// Process the function, get the function name and then the parameters
		RDFNode f = functions.get(0).getObject();
		String functionname = JSEnv.registerFunction(f);
		if(functionname == null) {
			// Something went wrong, reported by the function. 
			return null;
		}

		List<Statement> pbindings = fcn.listProperties(RRF.parameterBindings).toList();
		if(pbindings.size() != 1) {
			logger.error("Function valued TermMap must have exactly one rrf:parameterBindings.");
			logger.error(description);
			return null;
		}

		RDFList list = null;
		try {
			list = pbindings.get(0).getObject().as(RDFList.class);
		} catch(UnsupportedPolymorphismException e) {
			logger.error("rrf:parameterBindings must be an RDF collection.");
			logger.error(description);
			return null;
		}

		functionCall = new FunctionCall(functionname);

		ExtendedIterator<RDFNode> iter = list.iterator();
		while(iter.hasNext()) {
			RDFNode param = iter.next();
			if(!param.isResource()) {
				logger.error("Parameters in rrf:parameterBindings have to be resources.");
				logger.error(description);
				return null;
			}
			ObjectMap om = new ObjectMap(param.asResource(), baseIRI);
			if(om.preProcessAndValidate()) {
				functionCall.addParameter(om);
			} else {
				logger.error("Something went wrong processing parameter.");
				logger.error(description);
				return null;
			}
		}

		return functionCall;
	}

	/**
	 * Infer "default" termtype.
	 * @return
	 */
	protected abstract Resource inferTermType();

	/**
	 * True if chosen TermType is valid for subclasses.
	 * @return 
	 */
	protected abstract boolean isChosenTermTypeValid();

	/**
	 * Returns RDF term if the conditions for constant values for one of the TermMap's 
	 * subclasses are met. Only to be called if a TermMap has a constant.
	 * @return 
	 */
	protected abstract RDFNode distillConstant(RDFNode node);

	private String distillTemplate(RDFNode node) {
		if(!node.isLiteral()) 
			return null;
		if(!node.asLiteral().getDatatype().getURI().equals(XSD.xstring.getURI()))
			return null;
		// TODO: check the actual value of the template
		return node.asLiteral().toString();
	}

	private Set<String> getReferencedColumns() {
		Set<String> set = new HashSet<String>();
		if(isColumnValuedTermMap()) {
			// Singleton
			set.add(StringEscapeUtils.unescapeJava(column));
		} else if(isTemplateValuedTermMap()) {
			Matcher m = Pattern.compile("(?<!\\\\)\\{(.+?)(?<!\\\\)\\}").matcher(template);
			while(m.find()) {
				String temp = template.substring(m.start(1), m.end(1));
				set.add(StringEscapeUtils.unescapeJava(temp));
			}
		} else if(isFunctionValuedTermMap()) {
			for(TermMap tm : functionCall.getTermMaps()) {
				set.addAll(tm.getReferencedColumns());
			}
		} // else constant and thus empty set.
		return set;
	}

	private String distillColumnName(RDFNode node) {
		if(!node.isLiteral()) 
			return null;
		if(!node.asLiteral().getDatatype().getURI().equals(XSD.xstring.getURI()))
			return null;
		String s = node.asLiteral().toString();
		if(!R2RMLUtil.isValidColumnName(s))
			return null;
		return s;
	}

	public boolean isTemplateValuedTermMap() {
		return template != null;
	}

	public boolean isColumnValuedTermMap() {
		return column != null;
	}

	public boolean isConstantValuedTermMap() {
		return constant != null;
	}

	public boolean isFunctionValuedTermMap() {
		return functionCall != null;
	}

	public Resource getTermType() {
		return termType;
	}

	public boolean isTermTypeBlankNode() {
		return getTermType().getURI().equals(R2RML.BLANKNODE.getURI());
	}

	public boolean isTermTypeIRI() {
		return getTermType().getURI().equals(R2RML.IRI.getURI());
	}

	public boolean isTermTypeLiteral() {
		return getTermType().getURI().equals(R2RML.LITERAL.getURI());
	}

	public RDFNode generateRDFTerm(Row row) throws R2RMLException {
		Object value = getValueForRDFTerm(row);
		// If value is NULL, then no RDF term is generated.
		if(value == null) {
			return null;
		}

		else if(isTermTypeIRI()) {
			IRI iri = IRIFactory.iriImplementation().create(value.toString());
			if(iri.isAbsolute())
				return ResourceFactory.createResource(iri.toString());

			iri = IRIFactory.iriImplementation().create(baseIRI + value);
			if(iri.isAbsolute())
				return ResourceFactory.createResource(iri.toString());

			throw new R2RMLException("Data error. " + baseIRI + value + " is not a valid absolute IRI", null);

		}
		/*
		 * Otherwise, if the term type is rr:BlankNode: Return a blank node 
		 * that is unique to the natural RDF lexical form corresponding to 
		 * value. 
		 */
		else if(isTermTypeBlankNode()) {
			Resource r = blankNodeMap.get(value);
			if(r == null) {
				r = ResourceFactory.createResource();
				blankNodeMap.put(value, r);
			}
			return r;
		}
		/*
		 * Otherwise, if the term type is rr:Literal:
		 * 1. If the term map has a specified language tag, then return a plain
		 *    literal with that language tag and with the natural RDF lexical 
		 *    form corresponding to value.
		 * 2. Otherwise, if the term map has a non-empty specified datatype 
		 *    that is different from the natural RDF datatype corresponding to 
		 *    the term map's implicit SQL datatype, then return the datatype-
		 *    override RDF literal corresponding to value and the specified 
		 *    datatype.
		 *    Otherwise, return the natural RDF literal corresponding to value.
		 *    
		 *    // TODO: we use Jena's converter...
		 */
		else if(isTermTypeLiteral()) {
			if(language != null) {
				return ResourceFactory.createLangLiteral(value.toString(), language);
			}
			if(datatype != null) {
				RDFDatatype d = R2RMLTypeMapper.getTypeByName(datatype);
				return ResourceFactory.createTypedLiteral(value.toString(), d);
			}
			if(value instanceof Literal) {
				return (Literal) value ;
			}
			
			// TODO: Ensure integers are mapped onto xsd:integer, but there must be a more elegant way than via a string...
			if(value instanceof Integer)
				value = new BigInteger(value.toString());
			
			Literal x = ResourceFactory.createTypedLiteral(value);
			return x;
		}
		return null;

	}

	private Object getValueForRDFTerm(Row row) throws R2RMLException {
		if(isConstantValuedTermMap()) {
			return constant;
		} else if(isColumnValuedTermMap()) {
			return row.getObject(StringUtils.strip(column, "\""));
		} else if(isTemplateValuedTermMap()) {
			String value = new String(template);
			for(String reference : getReferencedColumns()) {
				Object object = row.getObject(StringUtils.strip(reference, "\""));
				// If one of the values is NULL, we don't generate the term.
				if(object == null)
					return null;

				// If the term type is rr:IRI, then replace the pair of curly braces with an IRI-safe 
				// version of value; otherwise, replace the pair of curly braces with value				
				String string = object.toString();
				if(isTermTypeIRI())
					string = IRISafe.toIRISafe(string);

				value = StringUtils.replace(value, "{" + StringEscapeUtils.escapeJava(reference) + "}", string);
			}
			value = StringEscapeUtils.unescapeJava(value);
			return value;
		} else if (isFunctionValuedTermMap()) {
			List<Object> arguments = new ArrayList<>();
			for(TermMap tm : functionCall.getTermMaps()) {
				Object argument = tm.getValueForRDFTerm(row);
				arguments.add(argument);
			}
			try {
				return JSEnv.invoke(functionCall.getFunctionName(), arguments.toArray());
			} catch (NoSuchMethodException | ScriptException e) {
				throw new R2RMLException("Error invoking function.", e);
			}
		}
		return null;
	}
}
