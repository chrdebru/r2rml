package r2rml.model;

import java.util.ArrayList;
import java.util.List;

/**
 * FunctionCall Class.
 * 
 * @author Christophe Debruyne
 * @version 0.1
 *
 */
public class FunctionCall {
	
	private List<TermMap> termMaps = new ArrayList<TermMap>();
	private String functionName;
	
	public FunctionCall(String functionName) {
		this.setFunctionName(functionName);
	}

	public List<TermMap> getTermMaps() {
		return termMaps;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public void addParameter(TermMap termMap) {
		termMaps.add(termMap);
	}

}
