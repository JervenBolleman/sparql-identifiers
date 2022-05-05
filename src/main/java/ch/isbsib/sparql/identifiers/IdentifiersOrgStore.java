package ch.isbsib.sparql.identifiers;

import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.sail.SailConnection;
import org.eclipse.rdf4j.sail.SailException;
import org.eclipse.rdf4j.sail.helpers.AbstractSail;
import org.identifiers.db.Dao;

public class IdentifiersOrgStore extends AbstractSail {
	private ValueFactory vf;

	private final Dao dao;

	public IdentifiersOrgStore(Dao dao) {
	    super();
	    this.dao = dao;
	    this.vf = SimpleValueFactory.getInstance();
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
		return new IdentifiersOrgConnection(getValueFactory(), dao);
	}

	public void setValueFactory(ValueFactory vf) {
		this.vf = vf;
	}

	public Dao getDao() {
		return dao;
	}

	
}
