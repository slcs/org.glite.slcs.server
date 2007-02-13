/*
 * $Id: MemorySessions.java,v 1.3 2007/02/13 15:58:07 vtschopp Exp $
 * 
 * Created on Aug 3, 2006 by tschopp
 *
 * Copyright (c) 2006 SWITCH - http://www.switch.ch/
 */
package org.glite.slcs.session.impl;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glite.slcs.config.SLCSServerConfiguration;
import org.glite.slcs.session.SLCSSessions;
import org.glite.slcs.util.Utils;

/**
 * MemorySessions is the memory implementation of the SLCSSessions. Uses a
 * cleaning thread to delete expired sessions.
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.3 $
 */
public class MemorySessions implements SLCSSessions {

    /** logging */
    public static Log LOG= LogFactory.getLog(MemorySessions.class);

    /** hashtable to store the (token,SessionEntry) pair */
    private Hashtable sessions_= null;
    private Object sessionsMutex_= new Object();

    /** random bytes generator */
    private SecureRandom random_= null;

    /** memory sessions cleaning thread */
    private MemorySessionsCleaner memorySessionsCleaner_= null;

    /** default TTL: 300 seconds */
    private long sessionTTL_= 300;

    /**
     * Constructor accessed only by factory
     */
    public MemorySessions() {
        super();
        sessions_= new Hashtable();
        try {
            random_= SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            LOG.error(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.session.SLCSSessions#init()
     */
    public void init(SLCSServerConfiguration config) {
        // read SessionTTL from config
        if (config.contains(SLCSServerConfiguration.COMPONENTSCONFIGURATION_PREFIX + ".SLCSSessions.SessionTTL")) {
            int sessionTTL= config.getInt(SLCSServerConfiguration.COMPONENTSCONFIGURATION_PREFIX + ".SLCSSessions.SessionTTL");
            this.sessionTTL_= sessionTTL;
        }
        LOG.info("SLCSSessions.SessionTTL=" + sessionTTL_);

        // read CleaningInterval (in seconds) for the memory cleaner thread
        int cleaningInterval= 60; // default 1 minute
        if (config.contains(SLCSServerConfiguration.COMPONENTSCONFIGURATION_PREFIX + ".SLCSSessions.CleaningInterval")) {
            cleaningInterval= config.getInt(SLCSServerConfiguration.COMPONENTSCONFIGURATION_PREFIX + ".SLCSSessions.CleaningInterval");
        }
        LOG.info("SLCSSessions.CleaningInterval=" + cleaningInterval);
        // and create/start the cleaning thread
        memorySessionsCleaner_= new MemorySessionsCleaner(cleaningInterval);
        memorySessionsCleaner_.start();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.SLCSServerComponent#shutdown()
     */
    public void shutdown() {
        // stop the cleaning thread
        if (memorySessionsCleaner_ != null ) {
            LOG.info("stop cleaning thread");
            memorySessionsCleaner_.shutdown();
            memorySessionsCleaner_= null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.session.SLCSSessions#createSession(java.lang.String)
     */
    public String createSession(String dn) {
        // create a random token
        String token= createRandomToken();
        // store the pair
        SessionEntry session= new SessionEntry(token, dn, sessionTTL_);
        if (LOG.isDebugEnabled()) {
            LOG.debug("add: " + session);
        }
        synchronized (sessionsMutex_) {
            sessions_.put(token, session);            
        }
        // return the token
        return token;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.session.SLCSSessions#deleteToken(java.lang.String)
     */
    public void removeSession(String token) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("remove: " + token);
        }
        synchronized (sessionsMutex_) {
            sessions_.remove(token);            
        }
    }

    /**
     * Sets the attributes associated with an existing session.
     * 
     * @param token
     *            The authorization token
     * @param attributes
     *            The attributes Map to associated with the session.
     */
    public void setAttributes(String token, Map attributes) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("session: " + token + " attributes: " + attributes);
        }
        SessionEntry session= getSessionEntry(token);
        if (session != null) {
            session.setAttributes(attributes);
        }
    }

    /**
     * Return the attributes Map associated with the session.
     * 
     * @param token
     *            The authorization token.
     * @return The attributes Map or <code>null</code> if the session doesn't
     *         exists.
     */
    public Map getAttributes(String token) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("session: " + token);
        }
        Map attributes= null;
        SessionEntry session= getSessionEntry(token);
        if (session != null) {
            attributes= session.getAttributes();
        }
        return attributes;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.session.SLCSSessions#sessionExists(java.lang.String,
     *      java.lang.String)
     */
    public boolean sessionExists(String token, String dn) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("token: " + token + " dn: " + dn);
        }
        SessionEntry session= getSessionEntry(token);
        if (session != null) {
            String storedDN= session.getDN();
            if (storedDN.equals(dn)) {
                return true;
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.slcs.session.SLCSSessions#isValidSession(java.lang.String,
     *      java.lang.String)
     */
    public boolean isSessionValid(String token, String dn) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("token: " + token + " dn: " + dn);
        }
        SessionEntry session= getSessionEntry(token, dn);
        if (session != null) {
            return session.isValid();
        }
        return false;
    }

    /**
     * Returns the SessionEntry for this token.
     * 
     * @param token
     *            The session token (id)
     * @return The session or <code>null</code> if the session doesn't exists.
     */
    public SessionEntry getSessionEntry(String token) {
        if (sessions_.containsKey(token)) {
            SessionEntry session= (SessionEntry) sessions_.get(token);
            return session;
        }
        return null;
    }

    /**
     * Returns the SessionEntry if and only if the token-dn pair exists in the
     * sessions.
     * 
     * @param token
     *            The session token.
     * @param dn
     *            The subject DN.
     * @return The session or <code>null</code> if the session doesn't exists.
     */
    private SessionEntry getSessionEntry(String token, String dn) {
        SessionEntry session= getSessionEntry(token);
        if (session != null) {
            String storedDN= session.getDN();
            if (storedDN.equals(dn)) {
                return session;
            }
        }
        return session;
    }

    /**
     * @return A 64 chars random token
     */
    private String createRandomToken() {
        byte[] randomBytes= null;
        randomBytes= new byte[32];
        random_.nextBytes(randomBytes);
        String token= Utils.toHexString(randomBytes);
        return token;
    }

    /**
     * SessionEntry
     * 
     * @author Valery Tschopp <tschopp@switch.ch>
     * @version $Revision: 1.3 $
     */
    public class SessionEntry {
        private String dn_= null;

        private String token_= null;

        private long time_= 0;

        private Map attributes_= null;

        private SimpleDateFormat dateFormatter_= new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

        /** millis */
        private long ttl_= 0;

        /**
         * Constructor.
         * 
         * @param token
         *            The session token.
         * @param dn
         *            The session DN.
         * @param ttl
         *            The time to live in seconds
         */
        public SessionEntry(String token, String dn, long ttl) {
            this.dn_= dn;
            this.token_= token;
            this.time_= System.currentTimeMillis();
            // convert seconds to millis
            this.ttl_= ttl * 1000;
            // attributes store
            this.attributes_= new HashMap();
        }

        public String getToken() {
            return this.token_;
        }

        public String getDN() {
            return this.dn_;
        }

        public void setAttributes(Map attributes) {
            attributes_= attributes;
        }

        public Map getAttributes() {
            return attributes_;
        }

        public void addAttribute(String name, String value) {
            attributes_.put(name, value);
        }

        public String getAttribute(String name) {
            return (String) attributes_.get(name);
        }

        public boolean isValid() {
            long currentTime= System.currentTimeMillis();
            long deathTime= time_ + ttl_;
            boolean valid= currentTime <= deathTime;
            return valid;
        }

        public boolean isExpired() {
            return !isValid();
        }

        public String toString() {
            StringBuffer sb= new StringBuffer();
            sb.append("Session[");
            sb.append(token_).append(':');
            sb.append(dn_).append(':');
            if (!attributes_.isEmpty()) {
                sb.append(attributes_).append(':');
            }
            long deathTime= time_ + ttl_;
            Date date= new Date(deathTime);
            sb.append(dateFormatter_.format(date));
            sb.append(']');
            return sb.toString();
        }
    }

    /**
     * MemorySessionsCleaner is a cleaning thread which delete all exipried SLCS
     * sessions.
     * 
     * TODO: parametrize the sleep interval
     * 
     * @author Valery Tschopp <tschopp@switch.ch>
     * @version $Revision: 1.3 $
     */
    private class MemorySessionsCleaner extends Thread {
        private volatile boolean running_= false;
        private long cleaningInterval_= 60 * 1000; // one minute

        public MemorySessionsCleaner() {
            this(60);
        }
        
        public MemorySessionsCleaner(int seconds) {
            super("MemorySessionsCleaner");
            this.cleaningInterval_= seconds * 1000;
            this.running_= true;
            setDaemon(true);
            
        }

        public void shutdown() {
            this.running_= false;
            interrupt();
        }

        public void run() {
            LOG.info("MemorySessionsCleaner started");
            while (running_) {
                synchronized (sessionsMutex_) {
                    if (!sessions_.isEmpty()) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("MemorySessionsCleaner cleaning sessions: "
                                    + sessions_.size());
                        }
                        Enumeration sessionEntries= sessions_.elements();
                        while (sessionEntries.hasMoreElements()) {
                            SessionEntry session= (SessionEntry) sessionEntries.nextElement();
                            if (session.isExpired()) {
                                LOG.info("Removing expired session: " + session);
                                sessions_.remove(session.getToken());
                            }
                        }
                    }
                }
                try {
                    // the sleep interval
                    sleep(this.cleaningInterval_);
                } catch (InterruptedException e) {
                    // LOG.debug("MemorySessionsCleaner interrupted");
                    running_= false;
                }
            }
            LOG.info("MemorySessionsCleaner terminated.");
        }

    }
}
