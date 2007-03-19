/*
 * $Id: XMLFileProcessor.java,v 1.4 2007/03/19 14:05:50 vtschopp Exp $
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html 
 */
package org.glite.slcs.acl.impl;

import java.io.File;
import java.util.LinkedList;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glite.slcs.SLCSConfigurationException;
import org.glite.slcs.SLCSException;

/**
 * XML file processor to process XMLOperation to be applied on the XML file.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.4 $
 */
public class XMLFileProcessor {

    /** Logging */
    static private Log LOG = LogFactory.getLog(XMLFileProcessor.class);

    /** XML file to process */
    private XMLConfiguration config_ = null;
    
    /** Absolute ACL filename */
    private String filename_= null;

    /** Queue of processing operations */
    private LinkedList operationsQueue_ = null;

    /** Operations processing thread */
    private ProcessingThread processingThread_ = null;

    /**
     * Constructor.
     * 
     * @param filename
     *            The XML file
     * @throws SLCSException
     *             If an error occurs.
     */
    public XMLFileProcessor(String filename) throws SLCSException {
        // create the xml file
        try {
            config_ = new XMLConfiguration(filename);
            File configFile = config_.getFile();
            filename_= configFile.getAbsolutePath();
            if (LOG.isDebugEnabled()) {
                LOG.debug("XMLConfiguration file="
                        + filename_);
            }
        } catch (ConfigurationException e) {
            LOG.error("Failed to create XMLConfiguration: " + filename, e);
            throw new SLCSConfigurationException(
                    "Failed to create XMLConfiguration: " + filename, e);
        }
        // create the operation queue
        operationsQueue_ = new LinkedList();
        // create the process thread and start it
        processingThread_ = new ProcessingThread();
        processingThread_.start();
    }

    /**
     * Enqueues the {@link XMLOperation} and wait for its completion.
     * 
     * @param operation The {@link XMLOperation} to process
     */
    public void process(XMLOperation operation) {
        queueOperation(operation);
        waitForOperation(operation);        
    }
    
    /**
     * Enqueues the operation in the queue.
     * 
     * @param operation
     */
    protected void queueOperation(XMLOperation operation) {
        synchronized (operationsQueue_) {
            operationsQueue_.addFirst(operation);
            operationsQueue_.notifyAll();
        }
    }

    /**
     * Waits for the operation to be finished.
     * 
     * @param operation
     */
    protected void waitForOperation(XMLOperation operation) {
        synchronized (operation) {
            while (!operation.isDone()) {
                try {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("operation not finished, waiting...");
                    }
                    operation.wait();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

    /**
     * Shutdowns the processing thread
     */
    public void shutdown() {
        if (processingThread_ != null) {
            LOG.info("Shutdown XML operations processing thread...");
            processingThread_.shutdown();
            processingThread_ = null;
        }
    }

    /**
     * @return The absolute ACL filename.
     */
    public String getFilename() {
        return filename_;
    }
    
    /**
     * Operations processing thread.
     * 
     */
    class ProcessingThread extends Thread {
        /** running flag */
        private volatile boolean running_ = true;

        public ProcessingThread() {
            super("XMLProcessingThread");
            setDaemon(true);
        }

        public void shutdown() {
            running_ = false;
            interrupt();
        }

        public void run() {
            while (running_) {
                XMLOperation operation = null;
                // consume next operation and process
                synchronized (operationsQueue_) {
                    while (operationsQueue_.isEmpty()) {
                        try {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("OperationsQueue empty, waiting...");
                            }
                            operationsQueue_.wait();
                        } catch (InterruptedException e) {
                            LOG.debug("ProcessingThread interrupted with "
                                    + operationsQueue_.size()
                                    + " operation(s) in queue...");
                            break;
                        }
                    }
                    if (!operationsQueue_.isEmpty()) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("get operation from queue");
                        }
                        operation = (XMLOperation) operationsQueue_
                                .removeLast();
                    }
                    operationsQueue_.notifyAll();
                }

                if (operation != null) {
                    // process and notify
                    synchronized (operation) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("process operation...");
                        }
                        operation.process(config_);
                        operation.notifyAll();
                    }
                }
                // yield();
            }
        }
    }

}
