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
		if (!pred.equals(OWL.SAMEAS))
			return new EmptyIteration<StatementImpl, QueryEvaluationException>();
		if (subj == null && obj == null)
			return new EmptyIteration<StatementImpl, QueryEvaluationException>();
		final Iterator<StatementImpl> iter = getIterViaJDBC(subj, obj, contexts);
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

	private Iterator<StatementImpl> getIterViaJDBC(Resource subj, Value obj,
			Resource... contexts) {
		if (obj instanceof Resource) {
			return getResultsViaJDBC(subj, (Resource) obj, contexts);
		} else
			return getResultsViaJDBC(subj, contexts);
	}

	private Iterator<StatementImpl> getResultsViaJDBC(final Resource subj,
			Resource... contexts) {
		Boolean activeflag = getActiveFlag(contexts);
		final Iterator<URIextended> iter = dao.getSameAsURIs(
				subj.stringValue(), activeflag).iterator();

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

	private Iterator<StatementImpl> getResultsViaJDBC(final Resource subj,
			final Resource obj, Resource... contexts) {

		Boolean activeflag = getActiveFlag(contexts);
		if (subj == null) {
			final Iterator<URIextended> iter = dao.getSameAsURIs(
					obj.stringValue(), activeflag).iterator();
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
		} else {
			final Iterator<URIextended> iter = dao.getSameAsURIs(
					obj.stringValue(), activeflag).iterator();
			List<StatementImpl> l = new ArrayList<>(1);
			while (iter.hasNext()) {
				final URIextended next = iter.next();
				if (subj.stringValue().equals(next.getURI())) {
					l.add(new StatementImpl(subj, OWL.SAMEAS, obj));
				}
			}
			return l.iterator();
		}
	}

	private Boolean getActiveFlag(Resource... contexts) {
		for (Resource context : contexts) {
			if (vf.createURI("id:active").equals(context))
				return Boolean.TRUE;
			if (vf.createURI("id:obsolete").equals(context))
				return Boolean.FALSE;
		}
		return null;
	}

	@Override
	public ValueFactory getValueFactory() {
		return vf;
	}

}
