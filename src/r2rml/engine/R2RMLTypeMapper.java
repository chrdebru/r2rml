package r2rml.engine;

import org.apache.jena.datatypes.DatatypeFormatException;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.TypeMapper;
import org.apache.jena.graph.impl.LiteralLabel;
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
		return new RDFDatatype() {
			
			@Override
			public String unparse(Object value) { return null; }
			
			@Override
			public Object parse(String lexicalForm) throws DatatypeFormatException { return null; }
			
			@Override
			public RDFDatatype normalizeSubType(Object value, RDFDatatype dt) { return null; }
			
			@Override
			public boolean isValidValue(Object valueForm) { return false; }
			
			@Override
			public boolean isValidLiteral(LiteralLabel lit) { return false; }
			
			@Override
			public boolean isValid(String lexicalForm) { return false; }
			
			@Override
			public boolean isEqual(LiteralLabel value1, LiteralLabel value2) { return false; }
			
			@Override
			public String getURI() { return uri; }
			
			@Override
			public Class<?> getJavaClass() { return null; }
			
			@Override
			public int getHashCode(LiteralLabel lit) { return 0; }
			
			@Override
			public Object extendedTypeDefinition() { return null; }
			
			@Override
			public Object cannonicalise(Object value) { return null; }
		};
	}

}
