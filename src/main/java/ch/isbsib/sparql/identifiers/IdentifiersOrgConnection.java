package ch.isbsib.sparql.identifiers;

import java.util.Arrays;
import java.util.Iterator;

import org.eclipse.rdf4j.IsolationLevel;
import org.eclipse.rdf4j.common.iteration.CloseableIteration;
import org.eclipse.rdf4j.common.iteration.CloseableIteratorIteration;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Namespace;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleNamespace;
import org.eclipse.rdf4j.model.vocabulary.OWL;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.Dataset;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.algebra.TupleExpr;
import org.eclipse.rdf4j.query.algebra.evaluation.EvaluationStrategy;
import org.eclipse.rdf4j.query.algebra.evaluation.impl.BindingAssigner;
import org.eclipse.rdf4j.query.algebra.evaluation.impl.FilterOptimizer;
import org.eclipse.rdf4j.query.algebra.evaluation.impl.StrictEvaluationStrategy;
import org.eclipse.rdf4j.query.impl.EmptyBindingSet;
import org.eclipse.rdf4j.repository.sparql.federation.SPARQLServiceResolver;
import org.eclipse.rdf4j.sail.SailConnection;
import org.eclipse.rdf4j.sail.SailException;
import org.eclipse.rdf4j.sail.UnknownSailTransactionStateException;
import org.eclipse.rdf4j.sail.UpdateContext;
//import org.eclipse.rdf4j.sail.UnknownSailTransactionStateException;
//import org.eclipse.rdf4j.sail.UpdateContext;
import org.identifiers.db.RegistryDao;

public class IdentifiersOrgConnection implements SailConnection {
	private final ValueFactory vf;

	private final RegistryDao dao;

	private SPARQLServiceResolver fd;
	public IdentifiersOrgConnection(ValueFactory vf, RegistryDao dao) {
		super();
		this.vf = vf;
		this.dao = dao;
		this.fd = new SPARQLServiceResolver();
	}

	@Override
	public boolean isOpen() throws SailException {
		return true;
	}

	@Override
	public void close() throws SailException {

	}

	@Override
	public CloseableIteration<? extends BindingSet, QueryEvaluationException> evaluate(
			TupleExpr tupleExpr, Dataset dataset, BindingSet bindings,
			boolean includeInferred) throws SailException {
		try {
			
			IdentifiersOrgTripleSource tripleSource = new IdentifiersOrgTripleSource(vf, dao);
			EvaluationStrategy strategy = new StrictEvaluationStrategy(tripleSource, fd);
			tupleExpr = tupleExpr.clone();
			new BindingAssigner().optimize(tupleExpr, dataset, bindings);

			new FilterOptimizer().optimize(tupleExpr, dataset, bindings);
			return strategy.evaluate(tupleExpr, EmptyBindingSet.getInstance());
		} catch (QueryEvaluationException e) {
			throw new SailException(e);
		}
	}

	@Override
	public CloseableIteration<? extends Resource, SailException> getContextIDs()
			throws SailException {
		return new CloseableIteration<Resource, SailException>() {
			private Iterator<Resource> ids = Arrays.asList(new Resource[]{vf.createIRI("id:active")}).iterator();
			@Override
			public boolean hasNext() throws SailException {
				// TODO Auto-generated method stub
				return ids.hasNext();
			}

			@Override
			public Resource next() throws SailException {
				// TODO Auto-generated method stub
				return ids.next();
			}

			@Override
			public void remove() throws SailException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void close() throws SailException {
				// TODO Auto-generated method stub
				
			}
		};
	}

	@Override
	public CloseableIteration<? extends Statement, SailException> getStatements(
			Resource subj, IRI pred, Value obj, boolean includeInferred,
			Resource... contexts) throws SailException {

	    final CloseableIteration<Statement, QueryEvaluationException> bedFileFilterReader;
		try {
			bedFileFilterReader = new IdentifiersOrgTripleSource(vf, dao).getStatements(subj, pred, obj, contexts);
		} catch (QueryEvaluationException e1) {
			throw new SailException(e1);
		}

		return new CloseableIteratorIteration<Statement, SailException>() {

			@Override
			public boolean hasNext() throws SailException {
				try {
					return bedFileFilterReader.hasNext();
				} catch (QueryEvaluationException e) {
					throw new SailException(e);
				}
			}

			@Override
			public Statement next() throws SailException {
				try {
					return bedFileFilterReader.next();
				} catch (QueryEvaluationException e) {
					throw new SailException(e);
				}
			}

			@Override
			protected void handleClose() throws SailException {
				try {
					bedFileFilterReader.close();
				} catch (QueryEvaluationException e) {
					throw new SailException(e);
				}
				super.handleClose();
			}
		};

	}

	@Override
	public long size(Resource... contexts) throws SailException {
		return 0;
	}

	@Override
	public void commit() throws SailException {
		throw new SailException("Identifiers can not be updated via SPARQL");

	}

	@Override
	public void rollback() throws SailException {
		// TODO Auto-generated method stub

	}

	// @Override
	// public boolean isActive() throws UnknownSailTransactionStateException {
	// return false;
	// }

	@Override
	public void addStatement(Resource subj, IRI pred, Value obj,
			Resource... contexts) throws SailException {
		throw new SailException("Identifiers files can not be updated via SPARQL");

	}

	@Override
	public void removeStatements(Resource subj, IRI pred, Value obj,
			Resource... contexts) throws SailException {
		throw new SailException("Identifiers files can not be updated via SPARQL");

	}


	@Override
	public void clear(Resource... contexts) throws SailException {
		throw new SailException("Identifiers files can not be updated via SPARQL");

	}

	@Override
	public CloseableIteration<? extends Namespace, SailException> getNamespaces()
			throws SailException {

		return new CloseableIteratorIteration<Namespace, SailException>() {
			private Iterator<Namespace> namespaces = Arrays.asList(
					new Namespace[] {
							new SimpleNamespace(OWL.PREFIX, OWL.NAMESPACE)
							//TODO list all supported namespaces from the file 
							})
					.iterator();

			@Override
			public boolean hasNext() throws SailException {
				return namespaces.hasNext();
			}

			@Override
			public Namespace next() throws SailException {
				return namespaces.next();
			};
		};
	}

	@Override
	public String getNamespace(String prefix) throws SailException {
		if (OWL.PREFIX.equals(prefix))
			return OWL.NAMESPACE;
		return null;
	}

	@Override
	public void setNamespace(String prefix, String name) throws SailException {
		throw new SailException("Identifiers files can not be updated via SPARQL");

	}

	@Override
	public void removeNamespace(String prefix) throws SailException {
		throw new SailException("Identifiers files can not be updated via SPARQL");

	}

	@Override
	public void clearNamespaces() throws SailException {
		throw new SailException("Identifiers.org can not be updated via SPARQL");

	}

	@Override
	public void addStatement(UpdateContext arg0, Resource arg1, IRI arg2,
			Value arg3, Resource... arg4) throws SailException {
		throw new SailException("Identifiers.org can not be updated via SPARQL");
	}

	@Override
	public void begin() throws SailException {
		throw new SailException("Identifiers.org can not be updated via SPARQL");

	}

	@Override
	public void endUpdate(UpdateContext arg0) throws SailException {
		throw new SailException("Identifiers.org can not be updated via SPARQL");

	}

	@Override
	public boolean isActive() throws UnknownSailTransactionStateException {
		return false;
	}

	@Override
	public void prepare() throws SailException {
		throw new SailException("Identifiers.org can not be updated via SPARQL");
	}

	@Override
	public void removeStatement(UpdateContext arg0, Resource arg1, IRI arg2,
			Value arg3, Resource... arg4) throws SailException {
		throw new SailException("Identifiers.org can not be updated via SPARQL");
	}

	@Override
	public void startUpdate(UpdateContext arg0) throws SailException {
		throw new SailException("Identifiers.org can not be updated via SPARQL");
	}

	@Override
	public void begin(IsolationLevel level)
	    throws UnknownSailTransactionStateException, SailException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void flush()
	    throws SailException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean pendingRemovals()
	{
		// TODO Auto-generated method stub
		return false;
	}

}
