/*
 * $Id: CMCException.java,v 1.1 2006/10/27 12:11:23 vtschopp Exp $
 * 
 * Created on Jun 15, 2006 by tschopp
 *
 * Copyright (c) 2006 SWITCH - http://www.switch.ch/
 */
package org.glite.slcs.caclient.impl;

import org.glite.slcs.SLCSException;

/**
 * CMCException is the exception type used by the CMC (RFC2797) implementation.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.1 $
 */
public class CMCException extends SLCSException {

    private static final long serialVersionUID= -1165358680515865162L;

    public CMCException() {
        super();
    }

    public CMCException(String arg0) {
        super(arg0);
    }

    public CMCException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public CMCException(Throwable arg0) {
        super(arg0);
    }

}
