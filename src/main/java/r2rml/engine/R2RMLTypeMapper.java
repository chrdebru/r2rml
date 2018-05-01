package r2rml.engine;

import org.apache.jena.datatypes.BaseDatatype;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.TypeMapper;
import org.apache.jena.rdf.model.Resource;

/**
 * R2RMLTypeMapper Class.
 * 
 * @author Christophe Debruyne
 * @version 0.1
 *
 */
public class R2RMLTypeMapper {

	/**
	 * Get an existing DataType or register one "on-the-fly"
	 * 
	 * @param datatypeDescription
	 * @return
	 */
	public static RDFDatatype getTypeByName(Resource datatypeDescription) {
		TypeMapper tm = TypeMapper.getInstance();
		RDFDatatype datatype = tm.getTypeByName(datatypeDescription.getURI());
		if(datatype == null) {
			datatype = createNewDataTypeFor(datatypeDescription.getURI());
			tm.registerDatatype(datatype);
		}
		return datatype;
	}

	private static RDFDatatype createNewDataTypeFor(String uri) {
		return new BaseDatatype(uri);
	}

}
