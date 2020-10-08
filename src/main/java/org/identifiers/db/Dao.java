package org.identifiers.db;

import java.util.List;

import org.identifiers.data.URIextended;


public interface Dao
{

	/**
	 * Returns all URIs sameAs the provided one.
	 * 
	 * @param uri
	 * @param activeflag 
	 * @return
	 */
	List<URIextended> getSameAsURIs(String uri, Boolean activeflag);

}