package ch.isbsib.sparql.identifiers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.identifiers.data.URIextended;
import org.identifiers.db.RegistryDao;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.SailException;

public class IdentifiersOrgTripleSourceTest extends TestCase {
	private final class RegistryDaoMock extends RegistryDao {
		@Override
		public List<URIextended> getSameAsURIs(String uri) {
			List<URIextended> urls = null;
			urls = new ArrayList<URIextended>();
			if (uri.equals("http://www.ebi.ac.uk/QuickGO/GTerm?id=GO:0006915")) {
				urls.add(new URIextended(
						"http://www.ebi.ac.uk/QuickGO/GTerm?id=GO:0006915",
						0));
				urls.add(new URIextended(
						"http://amigo.geneontology.org/cgi-bin/amigo/term_details?term=GO:0006915",
						0));
				urls.add(new URIextended(
						"http://www.bioinf.ebc.ee/EP/EP/GO/?Q=GO:0006915",
						1));
				urls.add(new URIextended(
						"http://www.informatics.jax.org/searches/GO.cgi?id=GO:0006915",
						0));
				urls.add(new URIextended(
						"http://www.pantherdb.org/panther/category.do?categoryAcc=GO:0006915",
						0));
				urls.add(new URIextended(
						"http://amigo2.berkeleybop.org/cgi-bin/amigo2/amigo/term/GO:0006915",
						0));
				urls.add(new URIextended(
						"http://purl.obolibrary.org/obo/GO_0006915", 0));
				urls.add(new URIextended("http://bio2rdf.org/GO:0006915", 0));
				urls.add(new URIextended(
						"http://identifiers.org/go/GO:0006915", 0));
				urls.add(new URIextended("urn:miriam:go:GO:0006915", 0));
				urls.add(new URIextended(
						"http://www.geneontology.org/GO:0006915", 0));
				urls.add(new URIextended("urn:miriam:obo.go:GO:0006915", 0));
				urls.add(new URIextended(
						"http://identifiers.org/obo.go/GO:0006915", 0));
			}
			return urls;
		}
	}

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	private File dataDir = null;

	@Before
	public void setUp() {
		dataDir = folder.newFolder("data.dir");
	}

	@After
	public void tearDown() {

		dataDir.delete();
	}

	String query1 = "PREFIX "
			+ OWL.PREFIX
			+ ": <"
			+ OWL.NAMESPACE
			+ ">\n SELECT ?target WHERE {<http://www.ebi.ac.uk/QuickGO/GTerm?id=GO:0006915> owl:sameAs ?target}";

	@Test
	public void testBasicMatch() throws IOException, QueryEvaluationException,
			MalformedQueryException, RepositoryException, SailException {

		IdentifiersOrgStore rep = new IdentifiersOrgStore();
		rep.setDao(new RegistryDaoMock());
		rep.setDataDir(dataDir);
		rep.setValueFactory(new ValueFactoryImpl());
		SailRepository sr = new SailRepository(rep);
		rep.initialize();
		TupleQuery pTQ = sr.getConnection().prepareTupleQuery(
				QueryLanguage.SPARQL, query1);
		TupleQueryResult eval = pTQ.evaluate();
		for (int i = 0; i < 13; i++) {
			assertTrue(eval.hasNext());
			assertNotNull(eval.next());
		}
		assertFalse(eval.hasNext());
	}

	String query2 = "PREFIX "
			+ OWL.PREFIX
			+ ": <"
			+ OWL.NAMESPACE
			+ ">\n SELECT ?target WHERE {<http://www.ebi.ac.uk/QuickGO/GTerm?id=GO:0006915> owl:"
			+ OWL.ALLDIFFERENT.getLocalName() + " ?target}";

	@Test
	public void testNoResultsForNotOWLSameAs() throws IOException,
			QueryEvaluationException, MalformedQueryException,
			RepositoryException, SailException {
		IdentifiersOrgStore rep = new IdentifiersOrgStore();
		rep.setDataDir(dataDir);
		rep.setValueFactory(new ValueFactoryImpl());
		SailRepository sr = new SailRepository(rep);
		rep.initialize();
		TupleQuery pTQ = sr.getConnection().prepareTupleQuery(
				QueryLanguage.SPARQL, query2);
		TupleQueryResult eval = pTQ.evaluate();
		assertFalse(eval.hasNext());
	}
}
