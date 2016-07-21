package r2rml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;

import org.apache.jena.iri.IRIFactory;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import r2rml.engine.Configuration;
import r2rml.engine.R2RMLException;
import r2rml.engine.R2RMLProcessor;

/**
 * Main Class.
 * 
 * @author Christophe Debruyne
 * @version 0.1
 *
 */
public class Main {

	public static void main(String[] args) {

		try {
			if(args.length != 1) {
				throw new R2RMLException("Only and exactly one config file needs to be passed as an argument", null);
			}

			Configuration configuration = new Configuration(args[0]);

			if(configuration.getMappingFile() == null) {
				throw new R2RMLException("A connection URL is mandatory.", null);
			}

			if(configuration.getMappingFile() == null) {
				throw new R2RMLException("A R2RML mapping file is mandatory.", null);
			}

			R2RMLProcessor engine = new R2RMLProcessor(configuration);
			engine.execute();

			String format = configuration.getFormat();

			if(format.equals("NQUADS") || format.equals("TRIG")) {
				System.out.println("Writing dataset to dataset format. Ignoring irrelevant parameters.");
				writeDatasetAsDatasetFile(configuration, engine);
			} else if(configuration.isFilePerGraph()) {
				System.out.println("Writing dataset as separate files. Ignoring irrelevant parameters.");
				writeDatasetAsFiles(configuration, engine);
			} else {
				System.out.println("Writing dataset to one RDf file. Ignoring irrelevant parameters.");
				writeDatasetAsFile(configuration, engine);
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}
	}

	private static void writeDatasetAsFiles(
			Configuration configuration, 
			R2RMLProcessor engine) 
					throws R2RMLException {
		// Create RDF files out of RDF dataset (losing graphs)
		try {
			File o = new File(configuration.getOutputFile());
			if(!o.exists()) o.mkdirs();
			
			Dataset ds = engine.getDataset();			
			
			String ext = getExtensionForFormat(configuration.getFormat());
			File file = new File(o, "default" + ext);
			writeModelToFile(ds.getDefaultModel(), file, configuration.getFormat());
			
			Iterator<String> graphs = ds.listNames();
			while(graphs.hasNext()) {
				String graph = graphs.next();
				String name = IRIFactory.iriImplementation().construct(graph).toASCIIString();
				name = name.replace("://", "_").replace("/", "_").replace(".", "_");
				file = new File(o, name + ext);
				writeModelToFile(ds.getNamedModel(graph), file, configuration.getFormat());
			}
			
		} catch (Exception e) {
			throw new R2RMLException(e.getMessage(), e);
		}
	}

	private static void writeModelToFile(
			Model model, 
			File file,  
			String format) 
					throws IOException {
		
		writeModelToFile(model, file, format, false);
	}
	
	private static void writeModelToFile(
			Model model, 
			File file,
			String format,
			boolean append) 
					throws IOException {
		
		// Make sure that we write output as UTF-8 as windows machines
		// use a different encoding for the writers...		
		Path path = Paths.get(file.getPath());
		
		if(!file.exists()) file.createNewFile();
		
		BufferedWriter bw = Files.newBufferedWriter(path, StandardCharsets.UTF_8, append ? StandardOpenOption.APPEND : StandardOpenOption.TRUNCATE_EXISTING);
		model.write(bw, format);
		bw.close();
	}

	private static void writeDatasetAsFile(
			Configuration configuration, 
			R2RMLProcessor engine) 
					throws R2RMLException {
		// Create RDF file out of RDF dataset (losing graphs)
		try {
			File o = new File(configuration.getOutputFile());
			if(o.exists()) 
				o.delete();
			o.createNewFile(); // make sure that it exists for some of the APIs.
			
			Dataset ds = engine.getDataset();
			writeModelToFile(ds.getDefaultModel(), o, configuration.getFormat());
			
			Iterator<String> graphs = engine.getDataset().listNames();
			while(graphs.hasNext()) {
				String graph = graphs.next();
				writeModelToFile(ds.getNamedModel(graph), o, configuration.getFormat(), true);
			}

		} catch (Exception e) {
			throw new R2RMLException(e.getMessage(), e);
		}
	}

	private static void writeDatasetAsDatasetFile(
			Configuration configuration, 
			R2RMLProcessor engine)
					throws R2RMLException {
		try {
			File o = new File(configuration.getOutputFile());
			if(o.exists()) 
				o.delete();
			o.createNewFile(); // Make sure that file exists for APIs.

			FileOutputStream out = new FileOutputStream(o);
			Lang lang = configuration.getFormat().equals("NQUADS") ? Lang.NQ : Lang.TRIG;
			RDFDataMgr.write(out, engine.getDataset(), lang);
			out.close();

		} catch (Exception e) {
			throw new R2RMLException(e.getMessage(), e);
		}
	}
	
	/**
	 * Simple method to find the corresponding extension for a format in case graphs
	 * are written to different tiles
	 * 
	 * @param format
	 * @return
	 */
	private static String getExtensionForFormat(String format) {
		if(equals(format, new String[]{"turtle", "ttl"}))
			return ".ttl";
		if(equals(format, new String[]{"n-triples", "n-triple", "nt"}))
			return ".nt";
		if(equals(format, new String[]{"rdf", "rdf/xml", "rdf/xml-abbrev"}))
			return ".rdf";
		if(equals(format, new String[]{"n3"}))
			return ".n3";
		if(equals(format, new String[]{"rdf/json"}))
			return ".rj";
		if(equals(format, new String[]{"jsonld"}))
			return ".jsonld";
		
		return null;
	}

	private static boolean equals(String format, String[] strings) {
		String t = format == null ? "" : format.toLowerCase();
		for(String s : strings) {
			if(s.equals(t)) return true;
		}
		return false;
	}

}
