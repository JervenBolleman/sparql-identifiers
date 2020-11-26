package org.identifiers.api;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;
import java.util.List;

import org.identifiers.data.URIextended;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class ApiDaoTest {

    private static final ApiDao apiDao = new ApiDao();
 
    @BeforeClass
    public static void before() {
	apiDao.update();
    }

    @Test
    public void testUniProt() throws URISyntaxException {
	final String in = "http://purl.uniprot.org/uniprot/P05067";
	List<URIextended> sameAsURIs = apiDao.getSameAsURIs(in, true);
	assertNotNull(sameAsURIs);
	assertTrue(sameAsURIs.contains(new URIextended("https://identifiers.org/uniprot:P05067", false)));
	sameAsURIs = apiDao.getSameAsURIs(in, false);
	assertTrue(sameAsURIs.contains(new URIextended("https://identifiers.org/uniprot/P05067", true)));
    }

    @Test
    public void testChebi() throws URISyntaxException {

	final String in = "http://purl.bioontology.org/ontology/CHEBI/CHEBI:36927";
	List<URIextended> sameAsURIs = apiDao.getSameAsURIs(in, true);
	assertNotNull(sameAsURIs);
	assertTrue(sameAsURIs
		.contains(new URIextended("https://www.ebi.ac.uk/ols/ontologies/chebi/terms?obo_id=CHEBI:36927", false)));
    }
}
