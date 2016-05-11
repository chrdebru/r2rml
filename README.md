# R2RML Implementation

## Building and using the code

To build the project and copy its dependencies, execute

```bash
$ mvn package
$ mvn dependency:copy-dependencies
```

The run the R2RML processor, execute the following command

```bash
$ java -jar r2rml-0.0.1-SNAPSHOT.jar config.properties
```

Where `config.properties` is a properties file containing:

- connectionURL, a JDBC connection URL to a database (required)
- user, username for the user connecting to the database
- password, password for the user connecting to the database
- mappingFile, the R2RML mapping file (required)
- outputFile, the output file (required)
- format, format of the output files (default "TURTLE")
- filePerGraph, flag to write the different graphs in separate files (default "false")
- baseIRI, used in resolving relative IRIs produced by the R2RML mapping

When named graphs are used in the R2RML mapping, one should use serelizations that support graphs such as N-QUADS and TRIG. The use of other serializations formats (such as TURTLE) results in all triples of all graphs being written away to that file. When setting the flag `filePerGraph` to `true` for seralization formats that do not support graphs, however, the value for `outputFile` will be used to create a directory in which a file will be created for each graph in RDF dataset.

## License
This implementation of R2RML is written by [Christophe Debruyne](http://www.christophedebruyne.be/). 

This code is copyrighted by [ADAPT - Trinity College Dublin](http://www.adaptcentre.ie/) and released under the [MIT license](http://opensource.org/licenses/MIT).