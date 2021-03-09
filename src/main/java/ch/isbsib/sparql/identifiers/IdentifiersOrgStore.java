package ch.isbsib.sparql.identifiers;

import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.sail.SailConnection;
import org.eclipse.rdf4j.sail.SailException;
import org.eclipse.rdf4j.sail.helpers.AbstractSail;
import org.identifiers.db.Dao;

public class IdentifiersOrgStore extends AbstractSail {
	private final ValueFactory vf = SimpleValueFactory.getInstance();

	private final Dao dao;

	public IdentifiersOrgStore(Dao dao) {
	    super();
	    this.dao = dao;
	}

	@Override
	public boolean isWritable() throws SailException {
		return false;
	}

	@Override
	public ValueFactory getValueFactory() {
		return vf;
	}

	@Override
	protected void shutDownInternal() throws SailException {

	}

	@Override
	protected SailConnection getConnectionInternal() throws SailException {
		return new IdentifiersOrgConnection(vf, dao);
	}

	public Dao getDao() {
		return dao;
	}

	
}
