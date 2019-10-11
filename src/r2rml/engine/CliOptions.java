package r2rml.engine;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "r2rml")
public class CliOptions {

	@Option(names = {"-h", "--help" }, usageHelp = true, description = "Display a help message")
	boolean help = false;

	@Option(names = {"--connectionURL"}, description = "A JDBC connection URL to a database", required = true)
	String connectionURL = null;

	@Option(names= {"-u", "--user"}, description = "Username for the user connecting to the database")
	String user = null;

	@Option(names= {"-p", "--password"}, description = "Password for the user connecting to the database")
	String password = null;

	@Option(names= {"--mappingFile"}, description = "The R2RML mapping file", required = true)
	String mappingFile = null;

	@Option(names= {"-o", "--outputFile"}, description = "The output file", required = true)
	String outputFile = null;

	@Option(names = {"-f", "--format"}, description = "Format of the output files (default: TURTLE)" )
	String format = "TURTLE";
	
	@Option(names = {"--filePerGraph"}, description = "Flag to write the different graphs in separate files (default: false)" )
	boolean filePerGraph = false;

	@Option(names = {"-b", "--baseIRI"}, description = "Used in resolving relative IRIs produced by the R2RML mapping" )
	String baseIRI = null;
	
	@Option(names = {"--CSVFiles"}, description = "A list of paths to CSV files that are separated by semicolons (cannot be used with connectionURL)" )
	String CSVFiles = null;


	public CliOptions(String[] args) {
		try {
			CliOptions cliOptions = CommandLine.populateCommand(this, args);
			if (cliOptions.help) {
				new CommandLine(this).usage(System.out);
				System.exit(0);
			}
		} catch (CommandLine.ParameterException pe) {
			System.out.println(pe.getMessage());
			new CommandLine(this).usage(System.out);
			// System.out.println("  * required parameter");
			System.exit(64);
		}
	}
}