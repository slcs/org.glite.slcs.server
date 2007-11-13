##
# Creates the SLCS audit database (MySQL 4.1 or better)
#
# root# mysql < create-slcs-db.sql
##
# $Id: create-slcs-db.sql,v 1.3 2007/11/13 09:39:22 vtschopp Exp $
##

#
# create the DB
#
CREATE DATABASE slcs;

#
# create the user
#
GRANT INSERT ON slcs.* TO slcs@localhost IDENTIFIED BY 'slcs';
FLUSH PRIVILEGES;

#
# create the tables
#
USE slcs;
DROP TABLE IF EXISTS AuditEvent;
CREATE TABLE AuditEvent (
	eventId INTEGER NOT NULL AUTO_INCREMENT,
	# event 
	eventType INTEGER NOT NULL,
	eventLevel INTEGER NOT NULL,
	eventDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	eventMessage VARCHAR(255) NOT NULL,
	# user attributes
	attributeUniqueId VARCHAR(255),
	attributeSurname VARCHAR(255),
	attributeGivenName VARCHAR(255),
	attributeEmail VARCHAR(255),
	attributeHomeOrganization VARCHAR(255),
	attributeHomeOrganizationType VARCHAR(255),
	attributeAffiliation VARCHAR(255),
	# client
	remoteAddress VARCHAR(64),
	userAgent VARCHAR(255),
	PRIMARY KEY (eventId)
);

