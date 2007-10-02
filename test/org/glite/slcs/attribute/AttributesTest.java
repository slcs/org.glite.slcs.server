/**
 * 
 */
package org.glite.slcs.attribute;

import java.util.List;
import java.util.Vector;

import junit.framework.TestCase;

/**
 * $Id: AttributesTest.java,v 1.1 2007/10/02 12:55:21 vtschopp Exp $
 * 
 * @author tschopp
 */
public class AttributesTest extends TestCase {

    public void testCaseSensitive() {
        List userAttributes= new Vector();
        userAttributes.add(new Attribute("A", "aaaa"));
        userAttributes.add(new Attribute("B", "bbbb"));
        userAttributes.add(new Attribute("C", "cccc"));

        System.out.println("user: " + userAttributes);
        
        List ruleAttributes= new Vector();
        Attribute a = new Attribute("A", "AAAA");
        a.setCaseSensitive(false);
        Attribute b = new Attribute("B", "BBBB");
        b.setCaseSensitive(false);
        ruleAttributes.add(a);
        ruleAttributes.add(b);
        
        System.out.println("rule: " + ruleAttributes);

        assertTrue(userAttributes.containsAll(ruleAttributes));
    
    }
    
}
