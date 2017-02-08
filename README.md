# R2RML Implementation

## Building and using the code

To build the project and copy its dependencies, execute

```bash
$ mvn package
$ mvn dependency:copy-dependencies
```

The run the R2RML processor, execute the following command:

```bash
$ java -jar r2rml.jar config.properties
```
A fat jar is also provided with the [Apache Maven Shade Plugin](https://maven.apache.org/plugins/maven-shade-plugin/). It does not depend on the `dependency` folder and can be executed as follows:

```bash
$ java -jar r2rml-fat.jar config.properties
```

Where `config.properties` is a properties file containing:

- `connectionURL`, a JDBC connection URL to a database (required)
- `user`, username for the user connecting to the database
- `password`, password for the user connecting to the database
- `mappingFile`, the R2RML mapping file (required)
- `outputFile`, the output file (required)
- `format`, format of the output files (default "TURTLE")
- `filePerGraph`, flag to write the different graphs in separate files (default "false")
- `baseIRI`, used in resolving relative IRIs produced by the R2RML mapping
- `CSVFiles`, a list of paths to CSV files that are separated by semicolons.

When named graphs are used in the R2RML mapping, one should use serialization that support graphs such as N-QUADS and TRIG. The use of other serializations formats (such as TURTLE) results in all triples of all graphs being written away to that file. When setting the flag `filePerGraph` to `true` for serialization formats that do not support graphs, however, the value for `outputFile` will be used to create a directory in which a file will be created for each graph in the RDF dataset.

Note that you cannot use both `CSVFiles` and `connectionURL` at the same time. For each CSV file, the name of the table will be the base name of that file.

## Example

The directory `example` contains an example of a mapping and configuration file. The example assumes the MySQL database to be called `r2rml`, be running on `localhost` and accessible to the user `foo` with password `bar`. The configuration file looks as follows:

```
connectionURL = jdbc:mysql://localhost/r2rml
user = foo
password = bar
mappingFile = mapping.ttl
outputFile = output.ttl
format = TURTLE
```   

The output, after passing the properties file as an argument to the R2RML processor, should look as follows:

```
<http://data.example.com/employee/7369>
        a                             <http://example.com/ns#Employee> ;
        <http://example.com/ns#name>  "SMITH" .
```

## Function with R2RML-F
This implementation of R2RML re-implemented the ideas presented in [1], allowing one to declare and use functions in ECMAScript as (Function Valued) TermMaps in the mapping. R2RML-F extends R2RML's vocabulary with predicates for declaring functions, function calls and parameter bindings. These are declared in the namespace [rrf](http://kdeg.scss.tcd.ie/ns/rrf/index.html).

```
@prefix rr: <http://www.w3.org/ns/r2rml#> .
@prefix ex: <http://example.com/ns#> .
@prefix rrf: <http://kdeg.scss.tcd.ie/ns/rrf#>

<#TriplesMap1>
    rr:logicalTable [ rr:tableName "EMP" ];
    rr:subjectMap [
        rr:template "http://data.example.com/employee/{EMPNO}";
        rr:class ex:Employee;
    ];
    rr:predicateObjectMap [
        rr:predicate ex:name;
        rr:objectMap [ rr:column "ENAME" ];
    ];
    rr:predicateObjectMap [
        rr:predicate ex:test;
        rr:objectMap [
	        rrf:functionCall [
	 			rrf:function <#Concat> ;
	 			rrf:parameterBindings (
	 				[ rr:column "ENAME" ]
	 				[ rr:column "EMPNO" ]
	 			) ;
	 		] ; 
	 	]
    ]    
    .
    
<#Concat>
	rrf:functionName "concat" ;
	rrf:functionBody """
		function concat(var1, var2) {
		return var1 + " " + var2 ;
	}
	""" ;
.
```

## License
This implementation of R2RML is written by [Christophe Debruyne](http://www.christophedebruyne.be/).

This code is copyrighted by [ADAPT - Trinity College Dublin](http://www.adaptcentre.ie/) and released under the [MIT license](http://opensource.org/licenses/MIT).

## References

[1]  C. Debruyne and D. O'Sullivan. R2RML-F: Towards Sharing and Executing Domain Logic in R2RML Mappings. In Proceedings of the Workshop on Linked Data on the Web, LDOW 2016, co-located with the 25th International World Wide Web Conference (WWW 2016), Montreal, Canada, April 12th, 2016, 2016
