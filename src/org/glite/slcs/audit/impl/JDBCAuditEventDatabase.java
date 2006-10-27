/*
 * $Id: JDBCAuditEventDatabase.java,v 1.1 2006/10/27 12:11:23 vtschopp Exp $
 * 
 * Created on Sep 5, 2006 by Valery Tschopp <tschopp@switch.ch>
 *
 * Copyright (c) 2006 SWITCH - http://www.switch.ch/
 */
package org.glite.slcs.audit.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import org.glite.slcs.audit.event.AuditEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * JDBCAuditEventDatabase is a simple
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision: 1.1 $
 */
public class JDBCAuditEventDatabase {

    /** Logging */
    private static Log LOG= LogFactory.getLog(JDBCAuditEventDatabase.class);

    /** JDBC driver class */
    private String driver_= null;

    /** DB connection URL: jdbc:mysql://localhost/slcs */
    private String connectionUrl_= null;

    /** the DB username */
    private String password_= null;

    /** the DB password */
    private String user_= null;

    /** the DB connection */
    private Connection connection_= null;

    /** prepared statement */
    private PreparedStatement preparedStatement_= null;

    /** Maximum number of reconnection tries */
    private int maxReconnect_= 3;

    /**
     * 
     * @param driver
     * @param connectionUrl
     * @param user
     * @param password
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public JDBCAuditEventDatabase(String driver, String connectionUrl,
            String user, String password) throws ClassNotFoundException {
        driver_= driver;
        connectionUrl_= connectionUrl;
        user_= user;
        password_= password;

        // try to load the JDBC driver
        LOG.debug("load JDBC driver: " + driver_);
        Class.forName(driver_);
    }

    /**
     * Connects to the database and create the prepared statement.
     * 
     * @throws SQLException
     *             If a connection error occurs.
     */
    public void connect() throws SQLException {

        if (LOG.isDebugEnabled()) {
            LOG.debug("create DB connection: " + connectionUrl_ + " user: "
                    + user_ + " password: " + password_);
        }
        connection_= DriverManager.getConnection(connectionUrl_,
                                                 user_,
                                                 password_);

        String sql= "INSERT INTO AuditEvent (eventType,eventLevel,eventDate,eventMessage,remoteAddress,userAgent) VALUES (?,?,NOW(),?,?,?)";
        if (LOG.isDebugEnabled()) {
            LOG.debug("prepared statement: " + sql);
        }
        preparedStatement_= connection_.prepareStatement(sql);

    }

    /**
     * Inserts the AuditEvent in the DB.
     * 
     * @param event
     *            The AuditEvent to insert
     * @throws SQLException
     *             If an error occurs.
     */
    public void insertAuditEvent(AuditEvent event) throws SQLException {
        //TODO insert all user information
        int i= 1;
        preparedStatement_.setInt(i++, event.getType());
        preparedStatement_.setInt(i++, event.getLevel());
        preparedStatement_.setString(i++, event.getMessage());
        Map userInfo= event.getUserInformation();
        preparedStatement_.setString(i++,
                                     (String) userInfo.get("RemoteAddress"));
        preparedStatement_.setString(i++, (String) userInfo.get("UserAgent"));
        preparedStatement_.executeUpdate();

    }

    /**
     * @param maxReconnect
     *            the maxReconnect to set
     */
    public void setMaxReconnect(int maxReconnect) {
        maxReconnect_= maxReconnect;
    }

    /**
     * Try to reconnect to the DB. Default maxReconnect is 3.
     * 
     * @throws SQLException
     *             If reconnection fails after <code>maxReconnect</code>
     * @see #setMaxReconnect(int)
     */
    public void reconnect() throws SQLException {
        int nReconnect= 0;
        SQLException exception= null;
        do {
            close();
            try {
                connect();
                break;
            } catch (SQLException e) {
                LOG.warn("Try to reconnect to database", e);
                exception= e;
            }
        } while (nReconnect++ < maxReconnect_);

        if (nReconnect >= maxReconnect_) {
            throw exception;
        }

    }

    /**
     * Closes the statement and DB connection.
     */
    public void close() {
        if (preparedStatement_ != null) {
            try {
                preparedStatement_.close();
            } catch (SQLException e) {
                LOG.warn(e);
            } finally {
                preparedStatement_= null;
            }
        }
        if (connection_ != null) {
            try {
                connection_.close();
            } catch (SQLException e) {
                LOG.warn(e);
            } finally {
                connection_= null;
            }
        }
    }

}
