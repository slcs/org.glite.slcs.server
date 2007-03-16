/*
 * $Id: LoginResponseBean.java,v 1.1 2007/03/16 08:59:12 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html 
 */
package org.glite.slcs.struts.view;

import java.util.List;

public class LoginResponseBean extends ResponseBean {

	private String authorizationToken_= null;
	private String subject_= null;
	private String requestURL_= null;
    private List certificateExtensions_= null;
	
	public LoginResponseBean() {
        super();
        setType("SLCSLoginResponse");
	}

	public String getAuthorizationToken() {
		return authorizationToken_;
	}

	public void setAuthorizationToken(String authorizationToken) {
		this.authorizationToken_ = authorizationToken;
	}

	public String getRequestURL() {
		return requestURL_;
	}

	public void setRequestURL(String requestURL) {
		this.requestURL_ = requestURL;
	}

	public String getSubject() {
		return subject_;
	}

	public void setSubject(String subject) {
		this.subject_ = subject;
	}

    /**
     * @return the certificateExtensions
     */
    public List getCertificateExtensions() {
        return certificateExtensions_;
    }

    /**
     * @param certificateExtensions the certificateExtensions to set
     */
    public void setCertificateExtensions(List certificateExtensions) {
        certificateExtensions_ = certificateExtensions;
    }

    
}
