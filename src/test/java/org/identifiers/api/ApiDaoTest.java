package org.identifiers.api;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;
import java.util.List;

import org.identifiers.data.URIextended;
import org.junit.Test;


public class ApiDaoTest
{

	@Test
	public void test()
	    throws URISyntaxException
	{
		final ApiDao apiDao = new ApiDao();
		apiDao.update();
		final String in = "http://purl.uniprot.org/uniprot/P05067";
		List<URIextended> sameAsURIs = apiDao.getSameAsURIs(in, true);
		assertNotNull(sameAsURIs);
		assertTrue(sameAsURIs.contains(new URIextended("https://identifiers.org/uniprot:P05067", false)));
		sameAsURIs = apiDao.getSameAsURIs(in, false);
		assertTrue(sameAsURIs.contains(new URIextended("https://identifiers.org/uniprot/P05067", true)));
	}

}
