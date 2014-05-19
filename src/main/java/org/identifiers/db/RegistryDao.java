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
public class RegistryDao
{
	private Connection connection = null;
	
	
	public List<URIextended> getSameAsURIsTest(String uri)
	{
		List<URIextended> urls = null;
		urls = new ArrayList<URIextended>();
        
		urls.add(new URIextended("http://www.ebi.ac.uk/QuickGO/GTerm?id=GO:0006915", 0));
		urls.add(new URIextended("http://amigo.geneontology.org/cgi-bin/amigo/term_details?term=GO:0006915", 0));
		urls.add(new URIextended("http://www.bioinf.ebc.ee/EP/EP/GO/?Q=GO:0006915", 1));
		urls.add(new URIextended("http://www.informatics.jax.org/searches/GO.cgi?id=GO:0006915", 0));
		urls.add(new URIextended("http://www.pantherdb.org/panther/category.do?categoryAcc=GO:0006915", 0));
		urls.add(new URIextended("http://amigo2.berkeleybop.org/cgi-bin/amigo2/amigo/term/GO:0006915", 0));
		urls.add(new URIextended("http://purl.obolibrary.org/obo/GO_0006915", 0));
		urls.add(new URIextended("http://bio2rdf.org/GO:0006915", 0));
		urls.add(new URIextended("http://identifiers.org/go/GO:0006915", 0));
		urls.add(new URIextended("urn:miriam:go:GO:0006915", 0));
		urls.add(new URIextended("http://www.geneontology.org/GO:0006915", 0));
		urls.add(new URIextended("urn:miriam:obo.go:GO:0006915", 0));
		urls.add(new URIextended("http://identifiers.org/obo.go/GO:0006915", 0));
        
        return urls;
	}
	
	/**
	 * Returns all URIs sameAs the provided one.
	 * @param uri
	 * @return
	 */
	public List<URIextended> getSameAsURIs(String uri)
	{
        Boolean error = false;   // if an error happens
        PreparedStatement stmt = null;
        ResultSet rs;
        List<URIextended> urls = null;
        
        // initialisation of the database connection
	    connection = DbUtilities.initDbConnection();
        
        try
        {

            String query = "SELECT convertPrefix, ptr_datatype FROM mir_resource WHERE `convertPrefix` LIKE '"+uri.substring(0,uri.indexOf("/", 10))+"%'";
            
            try
            {
                stmt = connection.prepareStatement(query);
            }
            catch (SQLException e)
            {
                System.err.println("Error while creating the prepared statement!");
                System.err.println("SQL Exception raised: " + e.getMessage());
            }
            
            //logger.debug("SQL prepared query: " + stmt.toString());
            rs = stmt.executeQuery();

            String dataTypeId = null;
            String identifier = null;

            while (rs.next()) {
                String convertPrefix = rs.getString("convertPrefix");
                if(uri.contains(convertPrefix)){
                    dataTypeId = rs.getString("ptr_datatype");
                    identifier = uri.substring(convertPrefix.length());
                }

            }

            query = "SELECT convertPrefix, obsolete FROM mir_resource WHERE ptr_datatype=\""+dataTypeId+"\" and urischeme=1";

            try
            {
                stmt = connection.prepareStatement(query);
            }
            catch (SQLException e)
            {
                System.err.println("Error while creating the prepared statement!");
                System.err.println("SQL Exception raised: " + e.getMessage());
            }
            //logger.debug("SQL prepared query: " + stmt.toString());
            rs = stmt.executeQuery();

            urls = new ArrayList<URIextended>();
            while (rs.next())
            {
                urls.add(new URIextended(rs.getString("convertPrefix") + identifier, rs.getInt("obsolete")));
            }
            rs.close();
        }
        catch (SQLException e)
        {
            //logger.error("Error during the processing of the result of a query.");
            //logger.error("SQL Exception raised: " + e.getMessage());
            error = true;
        }
        finally
        {
        	// closes the database connection and statement
            DbUtilities.closeDbConnection(connection, stmt);
        }


        // exception handling
        if (error)
        {
            throw new RuntimeException("Sorry, an error occurred while dealing with your request.");
        }
        return urls;
	}
}
