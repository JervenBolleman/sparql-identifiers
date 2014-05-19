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


import javax.xml.bind.annotation.XmlAttribute;


/**
 * Contains all the information about an extended URI (a <code>URI</code> with additional info: is the URI obsolete or not).
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
public class URIextended extends URI
{
    @XmlAttribute(name="deprecated", required=false)
    private Boolean obsolete;
    
    
    /**
     * Default constructor.
     */
    public URIextended()
    {
        super();
        this.obsolete = false;
    }
    
    
    /**
     * Constructor with parameters (URI).
     * @param obsolete
     */
    public URIextended(URI uri, Boolean obsolete)
    {
        super(uri);
        this.obsolete = obsolete;
    }
    
    
    /**
     * COnstructor with parameters (String).
     * @param uri
     * @param obsolete
     */
    public URIextended(String uri, Boolean obsolete)
    {
        super(uri);
        this.obsolete = obsolete;
    }
    
    
    /**
     * COnstructor with parameters (String).
     * @param uri
     * @param obsolete
     */
    public URIextended(String uri, int obsolete)
    {
        super(uri);
        if (obsolete == 0)
        {
            this.obsolete = false;
        }
        else
        {
            this.obsolete = true;
        }
    }
    
    
    /**
     * Getter
     * @return the obsolete
     */
    public Boolean isObsolete()
    {
        return this.obsolete;
    }
    
    
    /**
     * Setter
     * @param obsolete the obsolete to set
     */
    public void setObsolete(Boolean obsolete)
    {
        this.obsolete = obsolete;
    }
}
