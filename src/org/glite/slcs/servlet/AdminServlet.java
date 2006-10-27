package org.glite.slcs.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Servlet implementation class for Servlet: AdminServlet
 * 
 * @web.servlet name="AdminServlet" display-name="AdminServlet"
 * 
 * @web.servlet-mapping url-pattern="/admin"
 * 
 */
public class AdminServlet extends AbstractSLCSServlet {

    /** serial version */
    private static final long serialVersionUID= -2149046262077913860L;

    /** Logging */
    private static Log LOG= LogFactory.getLog(AdminServlet.class);

    /**
     * Default constructor.
     */
    public AdminServlet() {
        super();
    }

    protected void doProcess(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub

    }

}