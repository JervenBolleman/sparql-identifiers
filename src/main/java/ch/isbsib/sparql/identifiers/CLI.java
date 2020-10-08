package ch.isbsib.sparql.identifiers;

import java.io.File;
import java.net.URISyntaxException;

import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
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

public class CLI {
    public static void main(String[] args) throws URISyntaxException {
	IdentifiersOrgStore rep = new IdentifiersOrgStore(new ApiDao());
	File dataDir = mkTempDir();
	if (args[0] == null) {
	    System.err.println("This script expects a variable which contains a SPARQL string ");
	}
	try {
	    rep.setDataDir(dataDir);
	    rep.setValueFactory(SimpleValueFactory.getInstance());
	    SailRepository sr = new SailRepository(rep);
	    rep.initialize();
	    Query pTQ = sr.getConnection().prepareTupleQuery(QueryLanguage.SPARQL, args[0]);
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
	    System.err.println("done");
	} catch (MalformedQueryException e) {
	    System.err.println("Query syntax is broken");
	    System.exit(2);
	} catch (SailException | QueryEvaluationException | RDFHandlerException | TupleQueryResultHandlerException
		| RepositoryException e) {
	    System.err.println("failed in sesame code");
	    System.exit(1);
	} finally {
	    deleteDir(dataDir);
	    System.exit(0);
	}
    }

    protected static void deleteDir(File dataDir) {
	for (File file : dataDir.listFiles()) {
	    if (file.isFile()) {
		if (!file.delete())
		    file.deleteOnExit();
	    } else if (file.isDirectory())
		deleteDir(file);
	}
	if (!dataDir.delete()) {
	    dataDir.deleteOnExit();
	}
    }

    protected static File mkTempDir() {
	File dataDir = new File(System.getProperty("java.io.tmpdir") + "/sparql-bed-temp");
	int i = 0;
	while (dataDir.exists()) {
	    dataDir = new File(System.getProperty("java.io.tmpdir") + "/sparql-bed-temp" + i++);
	}
	dataDir.mkdir();
	return dataDir;
    }
}
