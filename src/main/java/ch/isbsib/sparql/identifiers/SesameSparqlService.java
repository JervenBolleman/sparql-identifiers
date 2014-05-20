package ch.isbsib.sparql.identifiers;

import java.io.IOException;
import java.io.OutputStream;

import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.Query;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.query.resultio.sparqlxml.SPARQLResultsXMLWriter;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.sail.SailRepositoryConnection;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.rdfxml.RDFXMLWriter;
import org.openrdf.rio.turtle.TurtleWriter;
import org.openrdf.sail.SailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import uk.ac.ebi.fgpt.lode.exception.LodeException;
import uk.ac.ebi.fgpt.lode.service.SparqlService;
import uk.ac.ebi.fgpt.lode.utils.GraphQueryFormats;
import uk.ac.ebi.fgpt.lode.utils.QueryType;
import uk.ac.ebi.fgpt.lode.utils.TupleQueryFormats;

public class SesameSparqlService implements SparqlService {

	private IdentifiersOrgStore store;
	private SailRepository sr;
	private Logger log = LoggerFactory.getLogger(getClass());

    @Value("${lode.sparql.query.maxlimit}")
    private int maxQueryLimit = -1;
    
	public SesameSparqlService() throws SailException {
		super();

		this.store = new IdentifiersOrgStore();

		// TODO inject JDBC code.
		store.setValueFactory(new ValueFactoryImpl());
		this.sr = new SailRepository(store);
		store.initialize();
	}

	@Override
	public void setMaxQueryLimit(Integer limit) {
		// TODO Auto-generated method stub

	}

	@Override
	public Integer getMaxQueryLimit() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void query(String query, String format, Integer offset,
			Integer limit, boolean inference, OutputStream output)
			throws LodeException {

		try {
			final SailRepositoryConnection connection = sr.getConnection();
			final Query prepareQuery = connection.prepareQuery(
					QueryLanguage.SPARQL, query);
			if (prepareQuery instanceof TupleQuery) {
				if (isNullOrEmpty(format)) {
					format = TupleQueryFormats.XML.toString();
				}
				executeTupleQuery((TupleQuery) prepareQuery, format, offset,
						limit, inference, output);
			} else if (prepareQuery instanceof GraphQuery) {
				if (isNullOrEmpty(format)) {
					format = GraphQueryFormats.RDFXML.toString();
				}
				executeConstructQuery((GraphQuery) prepareQuery, format, output);
			} else if (prepareQuery instanceof BooleanQuery) {
				if (isNullOrEmpty(format)) {
					format = TupleQueryFormats.XML.toString();
				}
				executeBooleanQuery((BooleanQuery) prepareQuery, format, output);
			} else {
				// unknown query type
				log.error("Invalid query type: " + query);
				throw new LodeException(
						"Invalid query type, must be one of TUPLE, DESCRIBE, CONSTRUCT or BOOLEAN");
			}
			connection.close();
		} catch (RepositoryException | MalformedQueryException e) {
			throw new LodeException(e);
		}

	}

	private void executeTupleQuery(TupleQuery prepareQuery, String format,
			Integer offset, Integer limit, boolean inference,
			OutputStream output) throws LodeException {

		try {
			if (TupleQueryFormats.XML.toString().equals(format)) {
				prepareQuery.evaluate(new SPARQLResultsXMLWriter(output));
			} else if (TupleQueryFormats.JSON.toString().equals(format)) {
				prepareQuery.evaluate(new SPARQLResultsXMLWriter(output));
			}
		} catch (TupleQueryResultHandlerException | QueryEvaluationException e) {
			// TODO Auto-generated catch block
			throw new LodeException(e);

		}

	}

	private void executeConstructQuery(GraphQuery prepareQuery, String format,
			OutputStream output) throws LodeException {
		try {
			if (GraphQueryFormats.RDFXML.toString().equals(format)) {
				prepareQuery.evaluate(new RDFXMLWriter(output));
			} else if (GraphQueryFormats.TURTLE.toString().equals(format)) {
				prepareQuery.evaluate(new TurtleWriter(output));
			}
		} catch (QueryEvaluationException | RDFHandlerException e) {
			// TODO Auto-generated catch block
			throw new LodeException(e);

		}

	}

	private void executeBooleanQuery(BooleanQuery prepareQuery, String format,
			OutputStream output) throws LodeException {
		try {

			output.write(String.valueOf(prepareQuery.evaluate()).getBytes());

		} catch (QueryEvaluationException | IOException e) {
			// TODO Auto-generated catch block
			throw new LodeException(e);
		}
	}

	@Override
	public void query(String query, String format, boolean inference,
			OutputStream output) throws LodeException {
		query(query, format, 0, Integer.MAX_VALUE, inference, output);
	}

	@Override
	public void getServiceDescription(OutputStream outputStream, String format) {
		// TODO Auto-generated method stub

	}

	@Override
	public QueryType getQueryType(String query) {
		SailRepositoryConnection connection;
		try {
			connection = sr.getConnection();
			final Query prepareQuery = connection.prepareQuery(
					QueryLanguage.SPARQL, query);
			if (prepareQuery instanceof BooleanQuery)
				return QueryType.BOOLEANQUERY;
			if (prepareQuery instanceof GraphQuery)
				return QueryType.CONSTRUCTQUERY;
			if (prepareQuery instanceof TupleQuery)
				return QueryType.TUPLEQUERY;
		} catch (RepositoryException | MalformedQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return QueryType.DESCRIBEQUERY;
	}

	public static boolean isNullOrEmpty(Object o) {
		if (o == null) {
			return true;
		}
		return "".equals(o);
	}

}
