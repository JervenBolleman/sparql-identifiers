package ch.isbsib.sparql.identifiers;

import info.aduna.iteration.CloseableIteration;
import info.aduna.iteration.CloseableIteratorIteration;
import info.aduna.iteration.EmptyIteration;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.identifiers.db.RegistryDao;
import org.openrdf.model.Namespace;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.NamespaceImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.query.BindingSet;
import org.openrdf.query.Dataset;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.algebra.TupleExpr;
import org.openrdf.query.algebra.evaluation.EvaluationStrategy;
import org.openrdf.query.algebra.evaluation.impl.BindingAssigner;
import org.openrdf.query.algebra.evaluation.impl.EvaluationStrategyImpl;
import org.openrdf.query.algebra.evaluation.impl.FilterOptimizer;
import org.openrdf.query.impl.EmptyBindingSet;
import org.openrdf.sail.SailConnection;
import org.openrdf.sail.SailException;
import org.openrdf.sail.UnknownSailTransactionStateException;
import org.openrdf.sail.UpdateContext;
//import org.openrdf.sail.UnknownSailTransactionStateException;
//import org.openrdf.sail.UpdateContext;

public class IdentifiersOrgConnection implements SailConnection {
	private final ValueFactory vf;

	private final RegistryDao dao;
	public IdentifiersOrgConnection(ValueFactory vf, RegistryDao dao) {
		super();
		this.vf = vf;
		this.dao = dao;
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
			EvaluationStrategy strategy = new EvaluationStrategyImpl(tripleSource);
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
			private Iterator<Resource> ids = Arrays.asList(new Resource[]{vf.createURI("id:active")}).iterator();
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
			Resource subj, URI pred, Value obj, boolean includeInferred,
			Resource... contexts) throws SailException {

	    final CloseableIteration<StatementImpl, QueryEvaluationException> bedFileFilterReader;
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
	public void addStatement(Resource subj, URI pred, Value obj,
			Resource... contexts) throws SailException {
		throw new SailException("Identifiers files can not be updated via SPARQL");

	}

	@Override
	public void removeStatements(Resource subj, URI pred, Value obj,
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
							new NamespaceImpl(OWL.PREFIX, OWL.NAMESPACE)
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
	public void addStatement(UpdateContext arg0, Resource arg1, URI arg2,
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
	public void removeStatement(UpdateContext arg0, Resource arg1, URI arg2,
			Value arg3, Resource... arg4) throws SailException {
		throw new SailException("Identifiers.org can not be updated via SPARQL");
	}

	@Override
	public void startUpdate(UpdateContext arg0) throws SailException {
		throw new SailException("Identifiers.org can not be updated via SPARQL");
	}

}
