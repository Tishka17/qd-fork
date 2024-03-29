/*
 * Jid.java
 *
 * Created on 4.03.2005, 1:25
 * Copyright (c) 2005-2008, Eugene Stahov (evgs), http://bombus-im.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * You can also redistribute and/or modify this program under the
 * terms of the Psi License, specified in the accompanied COPYING
 * file, as published by the Psi Project; either dated January 1st,
 * 2005, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package client;

import util.Strconv;

/**
 *
 * @author Eugene Stahov
 */
public class Jid {
    
    private String bareJid;
    private String resource;
    
    /** Creates a new instance of Jid */
    public Jid(String s) {
        setJid(s);
    }
    
    public void setJid(String s){
        int resourcePos=s.indexOf('/');
        if (resourcePos<0) resourcePos=s.length();
        resource=s.substring(resourcePos);
        bareJid=Strconv.toLowerCase(s.substring(0,resourcePos));
    }
    
    /** Compares two Jids */
    public boolean equals(Jid j, boolean compareResource) {
        if (j==null) return false;
        
        if (!bareJid.equals(j.bareJid)) return false;

        if (!compareResource) return true;
        
        return (resource.equals(j.resource));
    }

    public boolean isTransport(){
        return bareJid.indexOf('@')==-1;
    }

    public boolean hasResource(){
        return (resource.length()!=0) ;
    }
    
    public String getTransport(){
        try {
            int beginIndex=bareJid.indexOf('@')+1;
            int endIndex=bareJid.indexOf('.',beginIndex);
            return bareJid.substring(beginIndex, endIndex);
        } catch (Exception e) {
            return "-";
        }
    }

    public String getResource(){ return resource; }

    public String getBareJid(){ return bareJid; }

    public String getJid(){
        if (resource.length()==0) return bareJid;
        return bareJid+resource;
    }
    
    public static String toBareJid(String jid) { return new Jid(jid).getBareJid(); } 
    
     /** returns server of the JID */
     public String getServer(){
         try {
             int beginIndex=bareJid.indexOf('@')+1;
             return bareJid.substring(beginIndex);
         } catch (Exception e) {
             return "-";
         }
     }
}
