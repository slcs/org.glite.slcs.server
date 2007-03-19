/*
 * $Id: XMLOperation.java,v 1.3 2007/03/19 14:05:50 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html 
 */
package org.glite.slcs.acl.impl;

import java.io.File;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Abstract XMLOperation processed by the XMLFileProcessor.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.3 $
 */
public abstract class XMLOperation {

    /** Logging */
    private static Log LOG = LogFactory.getLog(XMLOperation.class);

    /** Status of the operation */
    private boolean status_= false;
    
    /** Signal that the operation have been processed */
    private volatile boolean done_ = false;

    /**
     * Constructor.
     */
    public XMLOperation() {
    }

    /**
     * @return <code>true</code> if the operation have been processed.
     */
    public boolean isDone() {
        return done_;
    }

    /**
     * Sets the done signal
     * 
     * @param done
     */
    private void setDone(boolean done) {
        done_ = done;
    }

    
    
    /**
     * @return the status
     */
    public boolean getStatus() {
        return status_;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(boolean status) {
        status_ = status;
    }

    /**
     * The XMLFileProcessor will call process(config) on each XMLOpertion read
     * from the queue.
     * 
     * @param config
     */
    public void process(XMLConfiguration config) {
        // call doProcessing
        doProcessing(config);
        // and signal done
        setDone(true);
    }

    /**
     * An operation implements the doProcessing(config) method to process the
     * content of the {@link XMLConfiguration} file.
     * 
     * @param config
     *            The {@link XMLConfiguration} to be processed
     */
    abstract protected void doProcessing(XMLConfiguration config);

    /**
     * Returns a List of results if the operation produce some.
     * 
     * @return a List of results or <code>null</code> if no results are
     *         available.
     */
    public List getResults() {
        return null;
    }

    /**
     * Returns the single result of the operation.
     * 
     * @return the resulting object or <code>null</code> if no produced or
     *         found.
     */
    public Object getResult() {
        return null;
    }

    /**
     * Saves the given XML file.
     * 
     * @param config
     *            The {@link XMLConfiguration} to save
     */
    protected void save(XMLConfiguration config) {
        // save
        try {
            File file = config.getFile();
            LOG.info("saving file=" + file.getAbsolutePath());
            config.save(file);
        } catch (ConfigurationException e) {
            LOG.error(e);
        }

    }

}
