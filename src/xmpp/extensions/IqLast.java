/*
 * IqLast.java
 *
 * Created on 25.07.2006, 19:14
 *
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
 */

package xmpp.extensions;

import client.Contact;
import client.Msg;
import client.Roster;
import client.StaticData;
import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.*;
import locale.SR;
import util.Time;

/**
 *
 * @author EvgS
 */

public class IqLast implements JabberBlockListener {
    
    public IqLast(){}
    public void destroy() {
    }
    public static JabberDataBlock query(String to, String type){
        JabberDataBlock result=new Iq(to, Iq.TYPE_GET, "last_"+type);
        result.addChildNs("query", "jabber:iq:last");
        return result;
    }

    public int blockArrived(JabberDataBlock data) {
        if (!(data instanceof Iq)) return BLOCK_REJECTED;
        String type=data.getTypeAttribute();
        if (type.equals("get")) {
            JabberDataBlock query=data.findNamespace("query", "jabber:iq:last");
            if (query==null) return BLOCK_REJECTED;
            
            Contact c=StaticData.getInstance().roster.getContact( data.getAttribute("from"), false);
            c.setIncoming(Roster.INC_VIEWING);
            
            long last=(Time.utcTimeMillis() - StaticData.getInstance().roster.lastMessageTime)/1000;

            Iq reply=new Iq(data.getAttribute("from"), Iq.TYPE_RESULT, data.getAttribute("id"));
            reply.addChild(query);
            query.setAttribute("seconds", String.valueOf(last));

            StaticData.getInstance().roster.theStream.send(reply);
            reply=null;

            return BLOCK_PROCESSED;
        }
        String id=data.getAttribute("id");
        if (id.startsWith("last_")) {
            Roster roster=StaticData.getInstance().roster;
            Contact c=roster.getContact( data.getAttribute("from"), false);
            //String from=data.getAttribute("from");
            String body=null;

            if (type.equals("error")) {
                body="error";
                roster.querysign=false;
            } else if (type.equals("result")) {
                JabberDataBlock tm=data.getChildBlock("query");
                if (tm!=null) {
                    body=dispatchLast(tm);
                }
                roster.querysign=false;
            }
            if (body!=null) {
                String lastType=SR.get(SR.MS_IDLE);
                String resource="";
                if (id.startsWith("last_seen")) {
                    lastType=SR.get(SR.MS_SEEN);
                } else if (id.startsWith("last_online")) {
                    resource=id.substring(id.indexOf("last_online_")+12);
                    c=roster.getContact( data.getAttribute("from")+resource, false);
                    lastType=SR.get(SR.MS_ONLINE_TIME);
                }
                String status=(data.getChildBlockText("query").length()!=0)?" ("+data.getChildBlockText("query")+")":"";
                Msg m=new Msg(Msg.SYSTEM, "last", " "+lastType, body+status);
                roster.messageStore(c, m);
                roster.redraw();
                return BLOCK_PROCESSED;
            }
        }
        return BLOCK_REJECTED;
    }
    
    public static String dispatchLast(JabberDataBlock data) {
        if (!data.isJabberNameSpace("jabber:iq:last")) return "unknown last namespace";
        StringBuffer tm=new StringBuffer();
        String field=data.getAttribute("seconds");
        
        if (field!=null) {
            tm.append(Time.secDiffToDate(Integer.parseInt(field)));
        }
        return tm.toString();
    }

}
