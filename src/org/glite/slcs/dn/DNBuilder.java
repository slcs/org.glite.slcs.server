/*
 * $Id: DNBuilder.java,v 1.3 2007/03/19 15:39:30 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html 
 */
package org.glite.slcs.dn;

import java.util.List;
import java.util.Map;

import org.glite.slcs.SLCSException;
import org.glite.slcs.SLCSServerComponent;

/**
 * DNBuilder is a distinguished name creator. It uses the Shibboleth attribute
 * name-value to construct a unique personal certificate DN.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.3 $
 */
public interface DNBuilder extends SLCSServerComponent {

    /**
     * Returns the DN based on the attributes stored in the Map.
     * 
     * @param attributes
     *            The map of attributes name-value.
     * @return The constructed DN.
     * @throws SLCSException
     *             if it is not possible to create a DN based on the attributes
     *             map.
     */
    public String createDN(Map attributes) throws SLCSException;

    /**
     * Returns the DN based on the List of Attributes.
     * 
     * @param attributes
     *            The List of Attributes.
     * @return The constructed DN.
     * @throws SLCSException
     *             if it is not possible to create a DN based on the attributes.
     * @see org.glite.slcs.attribute.Attribute
     */
    public String createDN(List attributes) throws SLCSException;

}