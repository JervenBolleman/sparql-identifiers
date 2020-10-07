package ch.isbsib.sparql.identifiers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import org.identifiers.db.RegistryDao;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.sail.SailConnection;
import org.eclipse.rdf4j.sail.SailException;
import org.eclipse.rdf4j.sail.helpers.SailBase;

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
