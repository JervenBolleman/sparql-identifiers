package ch.isbsib.sparql.identifiers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.rdf4j.common.iteration.CloseableIteration;
import org.eclipse.rdf4j.common.iteration.EmptyIteration;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.OWL;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.algebra.evaluation.TripleSource;
import org.identifiers.data.URIextended;
import org.identifiers.db.RegistryDao;

public class IdentifiersOrgTripleSource implements TripleSource {

	private final ValueFactory vf;
	private final RegistryDao dao;

	public IdentifiersOrgTripleSource(ValueFactory vf, RegistryDao dao) {
		this.vf = vf;
		this.dao = dao;
	}

	@Override
	public CloseableIteration<Statement, QueryEvaluationException> getStatements(
			Resource subj, IRI pred, Value obj, Resource... contexts)
			throws QueryEvaluationException {
		if (!pred.equals(OWL.SAMEAS))
			return new EmptyIteration<Statement, QueryEvaluationException>();
		if (subj == null && obj == null)
			return new EmptyIteration<Statement, QueryEvaluationException>();
		final Iterator<Statement> iter = getIterViaJDBC(subj, obj, contexts);
		return new CloseableIteration<Statement, QueryEvaluationException>() {

			@Override
			public boolean hasNext() throws QueryEvaluationException {
				return iter.hasNext();
			}

			@Override
			public Statement next() throws QueryEvaluationException {
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

	private Iterator<Statement> getIterViaJDBC(Resource subj, Value obj,
			Resource... contexts) {
		if (obj instanceof Resource) {
			return getResultsViaJDBC(subj, (Resource) obj, contexts);
		} else
			return getResultsViaJDBC(subj, contexts);
	}

	private Iterator<Statement> getResultsViaJDBC(final Resource subj,
			Resource... contexts) {
		Boolean activeflag = getActiveFlag(contexts);
		final Iterator<URIextended> iter = dao.getSameAsURIs(
				subj.stringValue(), activeflag).iterator();

		return new Iterator<Statement>() {

			@Override
			public boolean hasNext() {
				return iter.hasNext();
			}

			@Override
			public Statement next() {
				final URIextended next = iter.next();
				IRI uri = vf.createIRI(next.getURI());
				return SimpleValueFactory.getInstance().createStatement(subj, OWL.SAMEAS, uri);
			}

			@Override
			public void remove() {
				iter.remove();
			}
		};
	}

	private Iterator<Statement> getResultsViaJDBC(final Resource subj,
			final Resource obj, Resource... contexts) {

		Boolean activeflag = getActiveFlag(contexts);
		if (subj == null) {
			final Iterator<URIextended> iter = dao.getSameAsURIs(
					obj.stringValue(), activeflag).iterator();
			return new Iterator<Statement>() {

				@Override
				public boolean hasNext() {
					return iter.hasNext();
				}

				@Override
				public Statement next() {
					final URIextended next = iter.next();
					IRI uri = vf.createIRI(next.getURI());
					return SimpleValueFactory.getInstance().createStatement(subj, OWL.SAMEAS, uri);
				}

				@Override
				public void remove() {
					iter.remove();
				}
			};
		} else {
			final Iterator<URIextended> iter = dao.getSameAsURIs(
					obj.stringValue(), activeflag).iterator();
			List<Statement> l = new ArrayList<>(1);
			while (iter.hasNext()) {
				final URIextended next = iter.next();
				if (subj.stringValue().equals(next.getURI())) {
					l.add(SimpleValueFactory.getInstance().createStatement(subj, OWL.SAMEAS, obj));
				}
			}
			return l.iterator();
		}
	}

	private Boolean getActiveFlag(Resource... contexts) {
		for (Resource context : contexts) {
			if (vf.createIRI("id:active").equals(context))
				return Boolean.TRUE;
			if (vf.createIRI("id:obsolete").equals(context))
				return Boolean.FALSE;
		}
		return null;
	}

	@Override
	public ValueFactory getValueFactory() {
		return vf;
	}

}
