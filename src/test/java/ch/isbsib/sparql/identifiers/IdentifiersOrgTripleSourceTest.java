package ch.isbsib.sparql.identifiers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.OWL;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.BooleanQuery;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.SailException;
import org.identifiers.data.URIextended;
import org.identifiers.api.ApiDao;
import org.identifiers.db.Dao;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class IdentifiersOrgTripleSourceTest {

    private final class RegistryDaoMock implements Dao {

        @Override
        public List<URIextended> getSameAsURIs(String uri, Boolean activeFlag) {
            List<URIextended> urls = new ArrayList<URIextended>();
            if (uri.equals("http://www.ebi.ac.uk/QuickGO/GTerm?id=GO:0006915")) {
                urls.add(new URIextended("http://www.ebi.ac.uk/QuickGO/GTerm?id=GO:0006915", false));
                urls.add(new URIextended("http://amigo.geneontology.org/cgi-bin/amigo/term_details?term=GO:0006915",
                        false));
                urls.add(new URIextended("http://www.bioinf.ebc.ee/EP/EP/GO/?Q=GO:0006915", true));
                urls.add(new URIextended("http://www.informatics.jax.org/searches/GO.cgi?id=GO:0006915", false));
                urls.add(new URIextended("http://www.pantherdb.org/panther/category.do?categoryAcc=GO:0006915", false));
                urls.add(new URIextended("http://amigo2.berkeleybop.org/cgi-bin/amigo2/amigo/term/GO:0006915", false));
                urls.add(new URIextended("http://purl.uniprot.org/go/0006915", false));
                urls.add(new URIextended("http://bio2rdf.org/GO:0006915", false));
                urls.add(new URIextended("http://identifiers.org/go/GO:0006915", false));
                urls.add(new URIextended("urn:miriam:go:GO:0006915", false));
                urls.add(new URIextended("http://www.geneontology.org/GO:0006915", false));
                urls.add(new URIextended("urn:miriam:obo.go:GO:0006915", false));
                urls.add(new URIextended("http://identifiers.org/obo.go/GO:0006915", false));
            } else if (uri.contains("uniprot")) {
                urls.add(new URIextended("http://www.ebi.uniprot.org/entry/P05067", true));
                urls.add(new URIextended("http://www.pir.uniprot.org/cgi-bin/upEntry?id=P05067", true));
                urls.add(new URIextended("http://us.expasy.org/uniprot/P05067", true));
                urls.add(new URIextended("http://www.uniprot.org/uniprot/P05067", false));
                urls.add(new URIextended("http://purl.uniprot.org/uniprot/P05067", false));
                urls.add(new URIextended("http://www.ncbi.nlm.nih.gov/protein/P05067", false));
                urls.add(new URIextended("http://identifiers.org/uniprot/P05067", false));
            }
            if (Boolean.TRUE == activeFlag) {
                for (Iterator<URIextended> iterator = urls.iterator(); iterator.hasNext();) {
                    URIextended e = iterator.next();
                    if (e.isObsolete()) {
                        iterator.remove();
                    }
                }
            }
            if (Boolean.FALSE == activeFlag) {
                for (Iterator<URIextended> iterator = urls.iterator(); iterator.hasNext();) {
                    URIextended e = iterator.next();
                    if (!e.isObsolete()) {
                        iterator.remove();
                    }
                }
            }
            return urls;
        }
    }

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    private File dataDir;

    @Before
    public void setUp() throws IOException {
        dataDir = folder.newFolder("data.dir");
    }

    @After
    public void tearDown() {

        dataDir.delete();
    }

    String query1 = "PREFIX " + OWL.PREFIX + ": <" + OWL.NAMESPACE
            + ">\n SELECT ?target WHERE {<http://www.ebi.ac.uk/QuickGO/GTerm?id=GO:0006915> owl:sameAs ?target}";

    @Test
    public void testBasicMatch()
            throws IOException, QueryEvaluationException, MalformedQueryException, RepositoryException, SailException {

        IdentifiersOrgStore rep = new IdentifiersOrgStore(new RegistryDaoMock());
        rep.setDataDir(dataDir);
        rep.setValueFactory(SimpleValueFactory.getInstance());
        SailRepository sr = new SailRepository(rep);
        rep.initialize();
        TupleQuery pTQ = sr.getConnection().prepareTupleQuery(QueryLanguage.SPARQL, query1);
        TupleQueryResult eval = pTQ.evaluate();
        for (int i = 0; i < 13; i++) {
            // for (int i = 0; i < 10; i++) {
            assertTrue(eval.hasNext());
            final BindingSet next = eval.next();
            assertNotNull(next);
            assertTrue(next.getBinding("target").getValue().toString().endsWith("0006915"));
        }
        assertFalse(eval.hasNext());
    }

    String query2 = "PREFIX " + OWL.PREFIX + ": <" + OWL.NAMESPACE
            + ">\n SELECT ?target WHERE {<http://www.ebi.ac.uk/QuickGO/GTerm?id=GO:0006915> owl:"
            + OWL.ALLDIFFERENT.getLocalName() + " ?target}";

    @Test
    public void testNoResultsForNotOWLSameAs()
            throws IOException, QueryEvaluationException, MalformedQueryException, RepositoryException, SailException {
        IdentifiersOrgStore rep = new IdentifiersOrgStore(new RegistryDaoMock());
        rep.setDataDir(dataDir);
        rep.setValueFactory(SimpleValueFactory.getInstance());
        SailRepository sr = new SailRepository(rep);
        rep.initialize();
        TupleQuery pTQ = sr.getConnection().prepareTupleQuery(QueryLanguage.SPARQL, query2);
        TupleQueryResult eval = pTQ.evaluate();
        assertFalse(eval.hasNext());
    }

    String query3 = "PREFIX " + OWL.PREFIX + ": <" + OWL.NAMESPACE
            + ">\n SELECT ?target WHERE {<http://www.ebi.uniprot.org/entry/P05067> owl:sameAs ?target}";

    @Test
    public void testBasicUniProt()
            throws IOException, QueryEvaluationException, MalformedQueryException, RepositoryException, SailException {

        IdentifiersOrgStore rep = new IdentifiersOrgStore(new RegistryDaoMock());
        rep.setDataDir(dataDir);
        rep.setValueFactory(SimpleValueFactory.getInstance());
        SailRepository sr = new SailRepository(rep);
        rep.initialize();
        TupleQuery pTQ = sr.getConnection().prepareTupleQuery(QueryLanguage.SPARQL, query3);
        TupleQueryResult eval = pTQ.evaluate();
        for (int i = 0; i < 7; i++) {
            assertTrue(eval.hasNext());
            final BindingSet next = eval.next();
            assertNotNull(next);
            assertTrue(next.getBinding("target").getValue().toString().endsWith("P05067"));
        }
        assertFalse(eval.hasNext());
    }

    String query4 = "PREFIX " + OWL.PREFIX + ": <" + OWL.NAMESPACE
            + ">\n ASK {<http://www.ebi.uniprot.org/entry/P05067> owl:sameAs <http://www.uniprot.org/uniprot/P05067>}";

    @Test
    public void testBasicUniProtSameAs()
            throws IOException, QueryEvaluationException, MalformedQueryException, RepositoryException, SailException {

        IdentifiersOrgStore rep = new IdentifiersOrgStore(new RegistryDaoMock());
        rep.setDataDir(dataDir);
        rep.setValueFactory(SimpleValueFactory.getInstance());
        SailRepository sr = new SailRepository(rep);
        rep.initialize();
        BooleanQuery pTQ = sr.getConnection().prepareBooleanQuery(QueryLanguage.SPARQL, query4);
        assertTrue("Should return true", pTQ.evaluate());
    }

    String query5 = "PREFIX  owl:  <http://www.w3.org/2002/07/owl#>\n"
            + "PREFIX  up:   <http://purl.uniprot.org/core/>\n" + "SELECT  ?target\n" + "WHERE\n"
            + "{ <http://www.ebi.ac.uk/QuickGO/GTerm?id=GO:0006915> owl:sameAs ?target\n"
            + "    BIND(str(?target) as ?goa) .\n" + "FILTER (STRSTARTS(?goa, \"http://purl.uniprot.org\"))\n" + "}\n";

    @Test
    public void testBasicUniProtFilter()
            throws IOException, QueryEvaluationException, MalformedQueryException, RepositoryException, SailException {

        IdentifiersOrgStore rep = new IdentifiersOrgStore(new RegistryDaoMock());
        rep.setDataDir(dataDir);
        rep.setValueFactory(SimpleValueFactory.getInstance());
        SailRepository sr = new SailRepository(rep);
        rep.initialize();
        TupleQuery pTQ = sr.getConnection().prepareTupleQuery(QueryLanguage.SPARQL, query5);
        TupleQueryResult eval = pTQ.evaluate();
        for (int i = 0; i < 1; i++) {
            assertTrue(eval.hasNext());
            final BindingSet next = eval.next();
            assertNotNull(next);
            System.err.println(next.getBinding("target").getValue().toString());
            assertTrue("Expect one more answer", next.getBinding("target").getValue().toString().endsWith("0006915"));
        }
        assertFalse(eval.hasNext());
    }

    String query6 = "PREFIX " + OWL.PREFIX + ": <" + OWL.NAMESPACE + ">\n SELECT ?target \n" + "FROM <id:active> "
            + "WHERE {<http://www.ebi.ac.uk/QuickGO/GTerm?id=GO:0006915> owl:sameAs ?target}";
    String query7 = "PREFIX " + OWL.PREFIX + ": <" + OWL.NAMESPACE + ">\n SELECT ?target \n"
            + "WHERE {GRAPH <id:active> {<http://www.ebi.ac.uk/QuickGO/GTerm?id=GO:0006915> owl:sameAs ?target}}";

    @Test
    public void testActiveGraph()
            throws IOException, QueryEvaluationException, MalformedQueryException, RepositoryException, SailException {

        IdentifiersOrgStore rep = new IdentifiersOrgStore(new RegistryDaoMock());
        rep.setDataDir(dataDir);
        rep.setValueFactory(SimpleValueFactory.getInstance());
        SailRepository sr = new SailRepository(rep);
        rep.initialize();
        TupleQuery pTQ = sr.getConnection().prepareTupleQuery(QueryLanguage.SPARQL, query6);
        TupleQueryResult eval = pTQ.evaluate();
        for (int i = 0; i < 12; i++) {
            // for (int i = 0; i < 10; i++) {
            assertTrue(eval.hasNext());
            final BindingSet next = eval.next();
            assertNotNull(next);
            assertTrue(next.getBinding("target").getValue().toString().endsWith("0006915"));
        }
        pTQ = sr.getConnection().prepareTupleQuery(QueryLanguage.SPARQL, query7);
        eval = pTQ.evaluate();
        for (int i = 0; i < 12; i++) {
            // for (int i = 0; i < 10; i++) {
            assertTrue(eval.hasNext());
            final BindingSet next = eval.next();
            assertNotNull(next);
            assertTrue(next.getBinding("target").getValue().toString().endsWith("0006915"));
        }
        assertFalse(eval.hasNext());
        pTQ = sr.getConnection().prepareTupleQuery(QueryLanguage.SPARQL, query1);
        eval = pTQ.evaluate();
        for (int i = 0; i < 13; i++) {
            // for (int i = 0; i < 10; i++) {
            assertTrue(eval.hasNext());
            final BindingSet next = eval.next();
            assertNotNull(next);
            assertTrue(next.getBinding("target").getValue().toString().endsWith("0006915"));
        }
        assertFalse(eval.hasNext());
    }

    String queryChebi = "PREFIX owl:<http://www.w3.org/2002/07/owl#> SELECT ?target WHERE {<http://purl.bioontology.org/ontology/CHEBI/CHEBI:36927> owl:sameAs ?target}";

    @Test
    public void testChebi()
            throws IOException, QueryEvaluationException, MalformedQueryException, RepositoryException, SailException {

        IdentifiersOrgStore rep = new IdentifiersOrgStore(new ApiDao());
        rep.setDataDir(dataDir);
        rep.setValueFactory(SimpleValueFactory.getInstance());
        SailRepository sr = new SailRepository(rep);
        rep.initialize();
        TupleQuery pTQ = sr.getConnection().prepareTupleQuery(QueryLanguage.SPARQL, queryChebi);
        TupleQueryResult eval = pTQ.evaluate();
        for (int i = 0; i < 3; i++) {
            // for (int i = 0; i < 10; i++) {
            assertTrue(eval.hasNext());
            final BindingSet next = eval.next();
            assertNotNull(next);
            assertTrue(next.getBinding("target").getValue().toString().endsWith("36927"));
        }
    }
}
