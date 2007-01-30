/*
 * $Id: XMLOperation.java,v 1.1 2007/01/30 13:40:06 vtschopp Exp $
 * 
 * Created on Aug 18, 2006 by Valery Tschopp <tschopp@switch.ch>
 *
 * Copyright (c) 2006 SWITCH - http://www.switch.ch/
 */
package org.glite.slcs.acl.impl;

import java.io.File;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.1 $
 */
public abstract class XMLOperation {
    
    /** Logging */
    private static Log LOG= LogFactory.getLog(XMLOperation.class);
    
    private String group_= null;
    private volatile boolean done_= false;
    
    public XMLOperation(String group) {
        group_= group;
    }

    public String getGroup() {
        return group_;
    }

    protected void setGroup(String group) {
        group_ = group;
    }

    public boolean isDone() {
        return done_;
    }

    protected void setDone(boolean done) {
        done_ = done;
    }
    
    abstract public void process(XMLConfiguration config);
    
    protected void save(XMLConfiguration config) {
        // save
        try {
            File file = config.getFile();
            LOG.info("saving file=" + file.getAbsolutePath());
            config.save(file);
        } catch (ConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    
}
