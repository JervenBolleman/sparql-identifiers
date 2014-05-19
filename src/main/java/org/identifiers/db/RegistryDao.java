package org.identifiers.db;

import org.identifiers.data.URIextended;
import org.identifiers.db.DbUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple dao for SPARQL testing.
 * 
 * @author Camille
 * @version 20140519
 */
public class RegistryDao {

	private static final String QUERY = "SELECT convertPrefix, obsolete "
			+ ", ( SELECT convertPrefix FROM mir_resource WHERE convertPrefix LIKE ? LIMIT 1 ORDER BY size(convertPrefix)) AS original_prefix "
			+ "FROM mir_resource WHERE urischeme =1 "
			+ "AND ptr_datatype = ( SELECT ptr_datatype FROM mir_resource WHERE convertPrefix LIKE ? )";

	/**
	 * Returns all URIs sameAs the provided one.
	 * 
	 * @param uri
	 * @return
	 */
	public List<URIextended> getSameAsURIs(String uri) {

		// initialisation of the database connection
		try (Connection connection = DbUtilities.initDbConnection()) {
			return fetchUrisFromConnection(connection, uri);
		} catch (SQLException e1) {
			throw new RuntimeException(
					"Sorry, an error occurred while dealing with your request.",
					e1);
		}
	}

	private List<URIextended> fetchUrisFromConnection(Connection connection,
			String uri) throws SQLException {
		List<URIextended> urls = new ArrayList<URIextended>();
		final String uriTobe = uri.substring(0, uri.lastIndexOf("/") + 1) + '%';

		try (PreparedStatement stmt = connection.prepareStatement(QUERY)) {
			stmt.setString(1, uriTobe);
			stmt.setString(2, uriTobe);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					String identifier = uri.substring(rs.getString(
							"original_prefix").length());
					final String uri2 = rs.getString("convertPrefix")
							+ identifier;
					urls.add(new URIextended(uri2, rs.getInt("obsolete")));
				}
			}
		}
		return urls;
	}
}
