/**
 * $Id: CMPRequest.java,v 1.2 2007/11/16 15:03:15 mikkonen Exp $
 *
 * Created on 11/07/2007 by Henri Mikkonen <henri.mikkonen@hip.fi>
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004.
 * See http://eu-egee.org/partners/ for details on the copyright holders.
 * For license conditions see the license file or http://eu-egee.org/license.html
 */
package org.glite.slcs.caclient.impl;

import org.glite.slcs.caclient.CARequest;
import org.glite.slcs.pki.CertificateRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERInteger;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.asn1.pkcs.CertificationRequestInfo;

import org.bouncycastle.jce.PKCS10CertificationRequest;

import com.novosec.pkix.asn1.cmp.PKIBody;
import com.novosec.pkix.asn1.cmp.PKIHeader;
import com.novosec.pkix.asn1.cmp.PKIMessage;

import com.novosec.pkix.asn1.crmf.CertReqMessages;
import com.novosec.pkix.asn1.crmf.CertReqMsg;
import com.novosec.pkix.asn1.crmf.CertRequest;
import com.novosec.pkix.asn1.crmf.CertTemplate;
import com.novosec.pkix.asn1.crmf.OptionalValidity;
import com.novosec.pkix.asn1.crmf.PBMParameter;
import com.novosec.pkix.asn1.crmf.ProofOfPossession;

/**
 * CMPRequest is an implementation for RFC 4210 request.
 * 
 * @author Henri Mikkonen <henri.mikkonen@hip.fi>
 */
public class CMPRequest implements CARequest {

    /** Maximum (random) id number */
    private static final int REQUEST_ID_MAXIMUM = 640000000;
	
    /** Logging */
    private static Log log = LogFactory.getLog(CMPRequest.class);

    /** PKIMessage including the request details */
    private PKIMessage pkiMessage;
	
    /** CMP configuration variables */
    private Properties cmpProperties;
	
    /*
     * Constructs a <code>CMPRequest</code>
     * 
     * @param certRequest The certificate request
     * @param cmpProps CMP configuration variables 
     */
    protected CMPRequest(CertificateRequest certRequest, Properties cmpProps) {
        this.cmpProperties = cmpProps;
        this.addCertificateRequest(certRequest);
    }

    /* (non-Javadoc)
     * @see org.glite.slcs.caclient.CARequest#addCertificateRequest(org.glite.slcs.pki.CertificateRequest)
     */
    public void addCertificateRequest(CertificateRequest certRequest) {

        String issuerDN = this.cmpProperties.getProperty(CMPClient.CA_DN_IDENTIFIER);
        CertTemplate certTemplate = makeCertTemplate(certRequest, issuerDN);

        int requestId = makeRandomInt(REQUEST_ID_MAXIMUM);
        log.debug("Constructing CMP CertRequest with id=" + requestId); // TODO: The ID is not (yet) actually verified later on
        CertRequest cmpCertRequest = new CertRequest(new DERInteger(requestId), certTemplate);
        
        CertReqMsg cmpCertReqMsg = new CertReqMsg(cmpCertRequest);
        ProofOfPossession pop = new ProofOfPossession(new DERNull(), 0); // TODO: No POP supported at the moment
        cmpCertReqMsg.setPop(pop);
	    
        CertReqMessages cmpCertReqMessages = new CertReqMessages(cmpCertReqMsg);

        PKIBody pkiBody = new PKIBody(cmpCertReqMessages, 2); // 2 for CMP CertReqMessages

        String saltStr = this.cmpProperties.getProperty(CMPClient.SALT_STRING_IDENTIFIER);
        String owfAlgIdStr = this.cmpProperties.getProperty(CMPClient.OWF_ALG_ID_IDENTIFIER);
        String macAlgIdStr = this.cmpProperties.getProperty(CMPClient.MAC_ALG_ID_IDENTIFIER);
        String iterCountStr = this.cmpProperties.getProperty(CMPClient.ITERATION_COUNT_IDENTIFIER);
        String senderDN = this.cmpProperties.getProperty(CMPClient.SENDER_DN_IDENTIFIER);
        String recipientDN = this.cmpProperties.getProperty(CMPClient.RECIPIENT_DN_IDENTIFIER);
        String sharedSecret = this.cmpProperties.getProperty(CMPClient.SHARED_SECRET_IDENTIFIER);
        String senderKID = this.cmpProperties.getProperty(CMPClient.SENDER_KID_IDENTIFIER);
        String protectionAlgIdStr = this.cmpProperties.getProperty(CMPClient.PROTECTION_ALG_ID_IDENTIFIER);

        int iterCountInt = new Integer(iterCountStr).intValue();

        DEROctetString salt = new DEROctetString(saltStr.getBytes());		

        PKIHeader pkiHeader = makePKIHeader(senderDN, recipientDN, senderKID, salt, 
                owfAlgIdStr, macAlgIdStr, iterCountInt, protectionAlgIdStr);
		
        this.pkiMessage = new PKIMessage(pkiHeader, pkiBody);
		
        byte[] protectionBytes = makeProtection(sharedSecret, iterCountInt, 
                owfAlgIdStr, macAlgIdStr, salt, this.pkiMessage);

        this.pkiMessage.setProtection(new DERBitString(protectionBytes));

        log.debug("pkiMessage = " + this.pkiMessage.toString());
    }

    /* (non-Javadoc)
     * @see org.glite.slcs.caclient.CARequest#getDEREncoded()
     */
    public byte[] getDEREncoded() {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        DEROutputStream out = new DEROutputStream(bao);
        try {
            out.writeObject(this.pkiMessage);
        } catch (IOException e) {
            log.error("IOException caught while DER-encoding PKIMessage! " + e.getMessage());
        }
        return bao.toByteArray();
    }	  

    private static byte[] makeProtection(String secret, int iterCount, 
            String owfAlgId, String macAlgId, DEROctetString salt, PKIMessage message) {
        byte[] saltBytes = salt.getOctets();
        byte[] sharedSecret = secret.getBytes();
        byte[] firstKey = new byte[sharedSecret.length + saltBytes.length];
        for (int i = 0; i < sharedSecret.length; i++) {
            firstKey[i] = sharedSecret[i];
        }
        for (int i = 0; i < saltBytes.length; i++) {
            firstKey[sharedSecret.length + i] = saltBytes[i];
        }
        // Construct the base key according to rfc4210, section 5.1.3.1
        MessageDigest dig = null;
        Mac mac = null;
        try {
            dig = MessageDigest.getInstance(owfAlgId, "BC");
            for (int i = 0; i < iterCount; i++) {
                firstKey = dig.digest(firstKey);
                dig.reset();
            }
            mac = Mac.getInstance(macAlgId, "BC");
            SecretKey key = new SecretKeySpec(firstKey, macAlgId);
            mac.init(key);
        } catch (Exception e) {
            log.error("Error while calculating PKIMessage protection", e);
        }
        mac.reset();
        byte[] protectedBytes = message.getProtectedBytes();
        mac.update(protectedBytes, 0, protectedBytes.length);
        return mac.doFinal();
    }
	 
    private static CertTemplate makeCertTemplate(CertificateRequest certRequest, String issuerDN) {
        PKCS10CertificationRequest pkcs10 = new PKCS10CertificationRequest(certRequest.getDEREncoded());
        CertificationRequestInfo pkcs10info = pkcs10.getCertificationRequestInfo();
        
        log.debug("Constructing CMP CertTemplate...");
        CertTemplate certTemplate = new CertTemplate();
        certTemplate.setPublicKey(pkcs10info.getSubjectPublicKeyInfo());
        certTemplate.setSubject(pkcs10info.getSubject());		
        certTemplate.setIssuer(new X509Name(issuerDN));

        // validity
        OptionalValidity validity = new OptionalValidity();
        GregorianCalendar date = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        // five minutes extra to before/after
        date.add(Calendar.MINUTE, -5);
        Time notBefore = new Time(date.getTime());
        date.add(Calendar.MINUTE, 5);
        // TODO: lifetime fixed to 1 mio seconds, should be possible to configure by user
        date.add(Calendar.SECOND, 1000000);
        Time notAfter = new Time(date.getTime());
        validity.setNotBefore(notBefore);
        validity.setNotAfter(notAfter);
        certTemplate.setValidity(validity);

        log.debug("Constructed " + certTemplate.toString());

        return certTemplate;
    }

    private static PKIHeader makePKIHeader(String senderDN, String recipientDN, String senderKID, 
            DEROctetString salt, String owfAlgIdStr, String macAlgIdStr, int iterCountInt, String protectionAlgIdStr) {

        AlgorithmIdentifier owfAlgId = new AlgorithmIdentifier(new DERObjectIdentifier(owfAlgIdStr));
        AlgorithmIdentifier macAlgId = new AlgorithmIdentifier(new DERObjectIdentifier(macAlgIdStr));		
        DERInteger iterCount = new DERInteger(iterCountInt);

        PBMParameter params = new PBMParameter(salt, owfAlgId, iterCount, macAlgId);

        AlgorithmIdentifier algId = new AlgorithmIdentifier(new DERObjectIdentifier(protectionAlgIdStr), params); 		  

        PKIHeader pkiHeader = new PKIHeader(new DERInteger(2), // fixed to 2, RFC 4210
                new GeneralName(new X509Name(senderDN)),
                new GeneralName(new X509Name(recipientDN)));
        pkiHeader.setSenderKID(new DEROctetString(senderKID.getBytes()));
        pkiHeader.setProtectionAlg(algId);

        return pkiHeader;
    }
    
    private static int makeRandomInt(int digits) {
        SecureRandom random = new SecureRandom();
        return random.nextInt(digits);
    }
}