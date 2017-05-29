package r2rml.function;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.log4j.Logger;

import r2rml.engine.RRF;

/**
 * JSEnv Class.
 * 
 * @author Christophe Debruyne
 * @version 0.1
 *
 */
public class JSEnv {

	private static Logger logger = Logger.getLogger(JSEnv.class.getName());
	
	private static ScriptEngineManager manager = new ScriptEngineManager();
	private static ScriptEngine engine = manager.getEngineByName("javascript");
	
	private static Map<Resource, String> nameMap = new HashMap<Resource, String>();

	/**
	 * Invoking a function with an array of parameters.
	 * 
	 * @param functionName
	 * @param parameters
	 * @return
	 * @throws NoSuchMethodException
	 * @throws ScriptException
	 */
	public static String invoke(String functionName, Object... parameters) 
			throws NoSuchMethodException, ScriptException {
		Invocable invokeEngine = (Invocable) engine;
		Object o = invokeEngine.invokeFunction(functionName, parameters);
		return o == null ? null : o.toString();
	}

	/**
	 * Loading JavaScript code.
	 * 
	 * @param code
	 * @throws ScriptException
	 */
	public static void loadCode(String code) throws ScriptException {
		engine.eval(code);
	}

	/**
	 * A utility function for setting up a new engine and environment.
	 * 
	 */
	public static void reset() {
		manager = new ScriptEngineManager();
		manager.getEngineByName("javascript");
		nameMap.clear();
	}

	public static String registerFunction(RDFNode node) {
		if(!node.isResource()) {
			logger.error("Function valued TermMap's function must be a resource.");
			logger.error(node);
			return null;
		}
		
		Resource f = node.asResource();
		
		List<Statement> names = f.listProperties(RRF.functionName).toList();
		if(names.size() != 1) {
			logger.error("Functions must have exactly one rrf:functionName.");
			logger.error(f);
			return null;
		}
		
		if(!names.get(0).getObject().isLiteral()) {
			logger.error("rrf:functionName must be a literal.");
			logger.error(f);
			return null;
		}
		
		String name = names.get(0).getObject().asLiteral().getLexicalForm();
		
		if(!nameMap.containsKey(f) && nameMap.values().contains(name)) {
			logger.error("No two functions can have the same rrf:functionName.");
			logger.error(f);
			return null;
		}
		
		nameMap.put(f, name);
		logger.info("Registered function " + name);
		
		List<Statement> bodies = f.listProperties(RRF.functionBody).toList();
		if(bodies.size() != 1) {
			logger.error("Functions must have exactly one rrf:functionBody.");
			logger.error(f);
			return null;
		}
		
		if(!bodies.get(0).getObject().isLiteral()) {
			logger.error("rrf:functionBody must be a literal.");
			logger.error(f);
			return null;
		}
		
		String body = bodies.get(0).getObject().asLiteral().getLexicalForm();
		
		try {
			loadCode(body);
			logger.info("Loaded function " + name);
		} catch (ScriptException e) {
			logger.error("rrf:functionBody contains issues.");
			logger.error(e.getMessage());
			logger.error(f);
			return null;
		}
		
		return name;
	}

}
