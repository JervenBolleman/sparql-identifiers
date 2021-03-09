package ch.isbsib.sparql.identifiers;

import java.io.File;
import java.util.concurrent.Callable;

import org.eclipse.rdf4j.query.BooleanQuery;
import org.eclipse.rdf4j.query.GraphQuery;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.Query;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResultHandlerException;
import org.eclipse.rdf4j.query.resultio.BooleanQueryResultFormat;
import org.eclipse.rdf4j.query.resultio.QueryResultIO;
import org.eclipse.rdf4j.query.resultio.QueryResultWriter;
import org.eclipse.rdf4j.query.resultio.text.csv.SPARQLResultsCSVWriter;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.turtle.TurtleWriter;
import org.eclipse.rdf4j.sail.SailException;
import org.identifiers.api.ApiDao;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

/**
 * A basic CLI that takes a SPARQL query and tries to translate owl:sameAs IRI
 * patterns using current data from the Identifier.org API.
 *
 * @author Jerven Bolleman <jerven.bolleman@sib.swiss>
 */
@Command(name = "sparqlViaIdentifiers", mixinStandardHelpOptions = true, version = "iri conversion via identifiers.org 0.0.1",
        description = "Converts IRIs in SPARQL queres")

public class CLI implements Callable<Integer> {

    @Parameters(index = "0", description = "The SPARQL query to test")
    public String query;

    @Override
    public Integer call() throws Exception {
        IdentifiersOrgStore rep = new IdentifiersOrgStore(new ApiDao());
        File dataDir = mkTempDir();
        try {
            rep.setDataDir(dataDir);
            SailRepository sr = new SailRepository(rep);
            rep.initialize();
            Query pTQ = sr.getConnection().prepareTupleQuery(QueryLanguage.SPARQL, query);
            if (pTQ instanceof TupleQuery) {
                SPARQLResultsCSVWriter handler = new SPARQLResultsCSVWriter(System.out);
                ((TupleQuery) pTQ).evaluate(handler);
            } else if (pTQ instanceof GraphQuery) {
                RDFHandler createWriter = new TurtleWriter(System.out);
                ((GraphQuery) pTQ).evaluate(createWriter);
            } else if (pTQ instanceof BooleanQuery) {
                QueryResultWriter createWriter = QueryResultIO.createWriter(BooleanQueryResultFormat.TEXT, System.out);
                boolean evaluate = ((BooleanQuery) pTQ).evaluate();
                createWriter.handleBoolean(evaluate);
            }
            return 0;
        } catch (MalformedQueryException e) {
            System.err.println("Query syntax is broken");
            return 2;
        } catch (SailException | QueryEvaluationException | RDFHandlerException | TupleQueryResultHandlerException
                | RepositoryException e) {
            System.err.println("failed in sesame code");
            return 1;
        } finally {
            deleteDir(dataDir);
        }
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new CLI()).execute(args);
        System.exit(exitCode);
    }

    protected static void deleteDir(File dataDir) {
        for (File file : dataDir.listFiles()) {
            if (file.isFile()) {
                if (!file.delete()) {
                    file.deleteOnExit();
                }
            } else if (file.isDirectory()) {
                deleteDir(file);
            }
        }
        if (!dataDir.delete()) {
            dataDir.deleteOnExit();
        }
    }

    protected static File mkTempDir() {
        File dataDir = new File(System.getProperty("java.io.tmpdir") + "/sparql-identifiers-temp");
        int i = 0;
        while (dataDir.exists()) {
            dataDir = new File(System.getProperty("java.io.tmpdir") + "/sparql-identifiers-temp" + i++);
        }
        dataDir.mkdir();
        return dataDir;
    }
}
