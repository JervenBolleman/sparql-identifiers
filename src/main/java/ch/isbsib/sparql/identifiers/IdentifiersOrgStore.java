package ch.isbsib.sparql.identifiers;

import org.identifiers.db.RegistryDao;
import org.openrdf.model.ValueFactory;
import org.openrdf.sail.SailConnection;
import org.openrdf.sail.SailException;
import org.openrdf.sail.helpers.SailBase;

public class IdentifiersOrgStore extends SailBase {
	private ValueFactory vf;

	private RegistryDao dao = new RegistryDao();

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

	public RegistryDao getDao() {
		return dao;
	}

	public void setDao(RegistryDao dao) {
		this.dao = dao;
	}

}
