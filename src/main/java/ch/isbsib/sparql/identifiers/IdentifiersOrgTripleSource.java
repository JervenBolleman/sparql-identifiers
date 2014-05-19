package ch.isbsib.sparql.identifiers;

import info.aduna.iteration.CloseableIteration;
import info.aduna.iteration.EmptyIteration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.identifiers.data.URIextended;
import org.identifiers.db.RegistryDao;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.algebra.evaluation.TripleSource;

public class IdentifiersOrgTripleSource implements TripleSource {

	private ValueFactory vf;
	private final static String JDBC_CONNECT_STRING = "";
	
	public IdentifiersOrgTripleSource(ValueFactory vf) {
		this.vf = vf;
	}

	@Override
	public CloseableIteration<StatementImpl, QueryEvaluationException> getStatements(
			Resource subj, URI pred, Value obj, Resource... contexts)
			throws QueryEvaluationException {
		if (subj == null || ! pred.equals(OWL.SAMEAS) || obj != null)
			return new EmptyIteration<StatementImpl, QueryEvaluationException>();
		final Iterator<StatementImpl> iter = getResultsViaJDBC(subj);
		return new CloseableIteration<StatementImpl, QueryEvaluationException>() {

			@Override
			public boolean hasNext() throws QueryEvaluationException {
				return iter.hasNext();
			}

			@Override
			public StatementImpl next() throws QueryEvaluationException {
				return iter.next();
			}

			@Override
			public void remove() throws QueryEvaluationException {
				iter.remove();
				
			}

			@Override
			public void close() throws QueryEvaluationException {
				// TODO Auto-generated method stub
			}
		};
	}

	private Iterator<StatementImpl> getResultsViaJDBC(final Resource subj) {
		List<StatementImpl> res = new ArrayList<StatementImpl>();
		
		RegistryDao registryDao = new RegistryDao();
		final Iterator<URIextended> iter = registryDao.getSameAsURIs(subj.stringValue()).iterator();
		
		return new Iterator<StatementImpl>() {

			@Override
			public boolean hasNext() {
				return iter.hasNext();
			}

			@Override
			public StatementImpl next() {
				final URIextended next = iter.next();
				URI uri = vf.createURI(next.getURI());
				return new StatementImpl(subj, OWL.SAMEAS, uri);
			}

			@Override
			public void remove() {
				iter.remove();
				
			}
		};
	}

	@Override
	public ValueFactory getValueFactory() {
		return vf;
	}

}
