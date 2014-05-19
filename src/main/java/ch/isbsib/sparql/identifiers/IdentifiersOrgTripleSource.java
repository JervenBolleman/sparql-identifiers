package ch.isbsib.sparql.identifiers;

import info.aduna.iteration.CloseableIteration;
import info.aduna.iteration.EmptyIteration;

import java.util.Iterator;

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

	private final ValueFactory vf;
	private final RegistryDao dao;
	
	public IdentifiersOrgTripleSource(ValueFactory vf, RegistryDao dao) {
		this.vf = vf;
		this.dao = dao;
	}

	@Override
	public CloseableIteration<StatementImpl, QueryEvaluationException> getStatements(
			Resource subj, URI pred, Value obj, Resource... contexts)
			throws QueryEvaluationException {
		if (subj == null || ! pred.equals(OWL.SAMEAS) || obj != null)
			return new EmptyIteration<StatementImpl, QueryEvaluationException>();
		final Iterator<StatementImpl> iter = getResultsViaJDBC(subj, contexts);
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

	private Iterator<StatementImpl> getResultsViaJDBC(final Resource subj, Resource... contexts) {
		final Iterator<URIextended> iter = dao.getSameAsURIs(subj.stringValue()).iterator();
		
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
