package ch.isbsib.sparql.identifiers;

import java.io.File;
import java.io.IOException;

import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.Query;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.query.resultio.BooleanQueryResultFormat;
import org.openrdf.query.resultio.BooleanQueryResultWriter;
import org.openrdf.query.resultio.QueryResultIO;
import org.openrdf.query.resultio.text.csv.SPARQLResultsCSVWriter;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.turtle.TurtleWriter;
import org.openrdf.sail.SailException;

public class CLI {
	public static void main(String[] args) throws MalformedQueryException,
			RepositoryException, QueryEvaluationException, SailException,
			RDFHandlerException, IOException, TupleQueryResultHandlerException {
		IdentifiersOrgStore rep = new IdentifiersOrgStore();
		File dataDir = mkTempDir();	
		System.out.println("Query is" + args[1]);
		try {
			rep.setDataDir(dataDir);
			//TODO inject JDBC code.
			rep.setValueFactory(new ValueFactoryImpl());
			SailRepository sr = new SailRepository(rep);
			rep.initialize();
			Query pTQ = sr.getConnection().prepareTupleQuery(
					QueryLanguage.SPARQL, args[1]);
			if (pTQ instanceof TupleQuery) {

				SPARQLResultsCSVWriter handler = new SPARQLResultsCSVWriter(System.out);
				((TupleQuery) pTQ).evaluate(handler);
			} else if (pTQ instanceof GraphQuery) {
				RDFHandler createWriter = new TurtleWriter(System.out);
				((GraphQuery) pTQ).evaluate(createWriter);
			} else if (pTQ instanceof BooleanQuery) {
				BooleanQueryResultWriter createWriter = QueryResultIO
						.createWriter(BooleanQueryResultFormat.TEXT, System.out);
				boolean evaluate = ((BooleanQuery) pTQ).evaluate();
				createWriter.write(evaluate);
			}
		} finally {
			System.out.println("done");
			deleteDir(dataDir);
			System.exit(0);
		}
	}

	protected static void deleteDir(File dataDir) {
		for (File file : dataDir.listFiles()) {
			if (file.isFile()){
				if (!file.delete())
					file.deleteOnExit();
			}
			else if (file.isDirectory())
				deleteDir(file);
		}
		if (!dataDir.delete()){
			dataDir.deleteOnExit();
		}
	}

	protected static File mkTempDir() {
		File dataDir = new File(System.getProperty("java.io.tmpdir")
				+ "/sparql-bed-temp");
		int i = 0;
		while (dataDir.exists()) {
			dataDir = new File(System.getProperty("java.io.tmpdir")
					+ "/sparql-bed-temp" + i++);
		}
		dataDir.mkdir();
		return dataDir;
	}
}
