/*
 * $Id: ResponseBean.java,v 1.1 2007/03/16 08:59:12 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html 
 */
package org.glite.slcs.struts.view;

public class ResponseBean {
    
    static public String STATUS_SUCCESS= "Success";
    static public String STATUS_FAILURE= "Failure";
    
    String type_= null;
	boolean failure_= false;
    String status_= STATUS_SUCCESS;
	
	public ResponseBean() {
	}

	public boolean isFailure() {
		return failure_;
	}

    public boolean isSuccess() {
        return !failure_;
    }
    
	public void setFailure(boolean failure) {
		failure_ = failure;
        if (failure_) {
            status_= STATUS_FAILURE;
        }
        else {
            status_= STATUS_SUCCESS;
        }
	}

    public String getStatus() {
        return status_;
    }
    
    /**
     * @return the type
     */
    public String getType() {
        return type_;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        type_ = type;
    }
}
