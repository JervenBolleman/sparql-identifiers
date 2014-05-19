/*
 * MIRIAM Resources
 * MIRIAM is an online resource created to catalogue biological data types,
 * their URIs and the corresponding physical URLs,
 * whether these are controlled vocabularies or databases.
 * Ref. http://www.ebi.ac.uk/miriam/
 *
 * Copyright (C) 2006-2010  Camille Laibe (EMBL - European Bioinformatics Institute, Computational Neurobiology Group)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */


package org.identifiers.data;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;


/**
 * Contains all the information about a URI (which can be a URN or a URL).
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>
 * Copyright (C) 2006-2010 Camille Laibe (EMBL - European Bioinformatics Institute, Computational Neurobiology Group)
 * <br />
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * <br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <br />
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * </dd>
 * </dl>
 * </p>
 * 
 * @author Camille Laibe <camille.laibe@ebi.ac.uk>
 * @version 20100307
 */
@XmlAccessorType(XmlAccessType.NONE)   // only annotated fields and properties will be serialised
@XmlRootElement(name="uri")
public class URI implements Comparable<Object>
{
    /* type of URIs: URLs can be directly used in a Web browser, URNs need to be dereferenced for a usage on the Web */
    private static enum typeURI {URN, URL};
    /* reference stored as a URI (can be a URN or a URL) */
    @XmlValue
    private String uri;
    /* type of URI (cf. typeURI) */
    @XmlAttribute(name="type")
    private typeURI type;
    
    
    /**
     * Default constructor.
     */
    public URI()
    {
        this.uri = new String();
        this.type = typeURI.URL;   // default value: URL
    }
    
    
    /**
     * Constructor allowing to set a type of URI.
     */
    public URI(String uri)
    {
        this.uri = new String();
        this.uri = uri.trim();
        this.type = getURIType(uri);
    }
    
    
    /**
     * Constructor per copy.
     * @param uri
     */
    public URI(URI uri)
    {
        this(uri.getURI());
    }
    
    
    /**
     * Constructor allowing to set a type of URI.
     * Warning: if the uri is updated and not of the given type, its type will be updated too!
     */
    public URI(typeURI type)
    {
        this.uri = new String();
        this.type = type;
    }
    
    
    /**
     * Compares to objects and determine whether they are equivalent or not.
     * Mandatory method for the class to be able to implement 'Comparable'
     * 
     * @param obj the reference object with which to compare
     * @return 0 if the two objects are the same, -1 otherwise
     */
    public int compareTo(Object obj)
    {
        if (this.equals(obj))
        {
            return 0;
        }
        else
        {
            return -1;
        }
    }
    
    
    /**
     * Indicates whether some other object is "equal to" this one.
     * 
     * @param obj the reference object with which to compare
     * @return <code>true</code> if this object is the same as the obj argument; <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object obj)
    {
        URI ref = (URI) obj;
        
        if (null != ref)
        {
            return (((this.getURI()).compareToIgnoreCase(ref.getURI()) == 0) && (this.getType().equals(ref.getType())));
        }
        else   // the object is null
        {
            return false;
        }
    }
    
    
    /**
     * Overrides the 'toString()' method for the 'Reference' object
     * @return a string which contains all the information about the reference
     */
    public String toString()
    {
        return this.getURI();
    }
    
    
    /**
     * Getter
     * @return the uri
     */
    public String getURI()
    {
        return this.uri;
    }
    
    
    /**
     * Setter (also updates the type of URI)
     * @param uri the uri to set
     */
    public void setURI(String uri)
    {
        this.uri = uri.trim();
        this.type = getURIType(uri);
    }
    
    
    /**
     * Getter
     * @return the type of URI
     */
    public String getType()
    {
        return this.type.name();
    }
    
    
    /**
     * Getter of the URN type of URI
     * @return URN type
     */
    public static typeURI getTypeURN()
    {
        return typeURI.URN;
    }
    
    
    /**
     * Getter of the URL type of URI
     * @return URL type
     */
    public static typeURI getTypeURL()
    {
        return typeURI.URL;
    }
    
    
    /*
     * Returns the type of a URI: URL or URN?
     * @param uri
     * @return
     */
    private typeURI getURIType(String uri)
    {
        uri = uri.trim();
        
        if (uri.startsWith("urn:"))
        {
            return typeURI.URN;
        }
        else
        {
            return typeURI.URL;
        }
    }
}
