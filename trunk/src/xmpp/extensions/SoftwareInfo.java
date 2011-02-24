/*
 * UserTuneResult.java
 *
 *
 * Copyright (c) 2009, Alexej Kotov (aqent), http://bombusmod-qd.wen.ru
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

package xmpp.extensions;

import info.Version;
import com.alsutton.jabber.*;
import com.alsutton.jabber.datablocks.*;
import java.util.*;
import client.*;

public class SoftwareInfo implements JabberBlockListener {
    
    public SoftwareInfo(){ };
    public void destroy() {
    }
    public int blockArrived(JabberDataBlock data) {
        if (!(data instanceof Iq)) return BLOCK_REJECTED;
        String id=/*(String)*/ data.getAttribute("id"); 
        if(id!=null)
        {
         if (data.getAttribute("type").equals("result")) {
            if(id.equals("discof")){
            JabberDataBlock query=data.getChildBlock("query");
            JabberDataBlock identity=query.getChildBlock("identity");
              Vector childs=query.getChildBlocks();
               StringBuffer softinfo = new StringBuffer();
               softinfo.append("Information:\n");                   
               softinfo.append("Category: "+identity.getAttribute("category")+"\n");
               softinfo.append("Type: "+identity.getAttribute("type")+"\n");
               softinfo.append("Name: "+identity.getAttribute("name")+"\n\n");
               int k=0;
               if (childs!=null) {
                 for (Enumeration e=childs.elements(); e.hasMoreElements();) {
                    JabberDataBlock i=(JabberDataBlock)e.nextElement();
                    if (i.getTagName().equals("feature")) {
                        k+=1;
                        String var=i.getAttribute("var");
                          softinfo.append(Integer.toString(k) + ")." + var+"\n"); 
                     }
                  }   
                }
                Msg m=new Msg(Constants.MESSAGE_TYPE_IN, "softinfo", locale.SR.get(locale.SR.MS_FEATURES), softinfo.toString());
                m.highlite=true;
                m.itemCollapsed=true;
                StaticData.getInstance().roster.setQuerySign(false);                
                StaticData.getInstance().roster.messageStore(StaticData.getInstance().roster.getContact(data.getAttribute("from"),false),m);
                StaticData.getInstance().roster.redraw();
              return JabberBlockListener.BLOCK_PROCESSED;                
            }
            if(id.equals("agents1")){
                Msg m=new Msg(Constants.MESSAGE_TYPE_IN, "agents1", "jabber:iq:agents", data.toString());                
                m.highlite=true;
                m.itemCollapsed=true;
                StaticData.getInstance().roster.setQuerySign(false);                
                StaticData.getInstance().roster.messageStore(StaticData.getInstance().roster.getContact(data.getAttribute("from"),false),m);
                StaticData.getInstance().roster.redraw();
            }
         }
       }
       return BLOCK_REJECTED;
    }

    public static JabberDataBlock agentsSend(String to) {
        JabberDataBlock result=new Iq(to, Iq.TYPE_GET, "agents1");
        result.addChildNs("query", "jabber:iq:agents");
        return result;
    }
    
    public static JabberDataBlock querySend(String to) {
        JabberDataBlock result=new Iq(to, Iq.TYPE_GET, "discof");
        result.addChildNs("query", "http://jabber.org/protocol/disco#info");
        return result;
    }

}
