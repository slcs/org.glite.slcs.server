/*
 * Copyright (c) 2007-2009. Members of the EGEE Collaboration.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * $Id: SLCSServerVersion.java,v 1.24 2009/02/02 15:21:10 vtschopp Exp $
 */
package org.glite.slcs;

/**
 * SLCSServerVersion and Copyright constants.
 *
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision: 1.24 $
 */
public class SLCSServerVersion {

    /** Major version number */
    static public final int MAJOR= 1;
    /** Minor version number */
    static public final int MINOR= 4;
    /** Revision version number */
    static public final int REVISION= 2;    
    /** Build number */
    static public final int BUILD= 2;
    
    /** Copyright */
    static public final String COPYRIGHT= "Copyright (c) 2007-2009 Members of the EGEE Collaboration";
    
    private SLCSServerVersion() {}
    
    /**
     * @return The version of the server in format MAJOR.MINOR.REVISION-BUILD
     */
    static public String getVersion() {
        StringBuffer sb= new StringBuffer();
        sb.append(MAJOR).append('.');
        sb.append(MINOR).append('.');
        sb.append(REVISION).append('-');
        sb.append(BUILD);
        return sb.toString();
    }
    
    /**
     * @return The copyright
     */
    static public String getCopyright() {
        return COPYRIGHT;
    }


}
