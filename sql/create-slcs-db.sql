##
# Creates the SLCS audit database
##
# $Id: create-slcs-db.sql,v 1.2 2007/11/12 15:12:06 vtschopp Exp $
##

CREATE DATABASE slcs;

USE slcs;
DROP TABLE IF EXISTS AuditEvent;
CREATE TABLE AuditEvent (
	eventId INTEGER NOT NULL AUTO_INCREMENT,
	# event 
	eventType INTEGER NOT NULL,
	eventLevel INTEGER NOT NULL,
	eventDate TIMESTAMP NOT NULL CURRENT_TIMESTAMP,
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

