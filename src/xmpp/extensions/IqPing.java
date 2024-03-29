/*
 * IqPing.java
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
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

import client.Contact;
import client.Msg;
import client.Roster;
import client.StaticData;
import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.*;
import locale.SR;
import util.Time;

public class IqPing implements JabberBlockListener {

    public IqPing(){};
    public void destroy() {
    }
    private final static String PING="ping";
    private final static String _PING_="_ping_";
    
    public static JabberDataBlock query(String to, String id) {
        String newId=(id==null)?_PING_+Time.utcTimeMillis():id;
        JabberDataBlock result=new Iq(to, Iq.TYPE_GET, newId);
        result.addChildNs(PING, "urn:xmpp:ping");
        return result;
    }

    public int blockArrived(JabberDataBlock data) {
        if (!(data instanceof Iq)) 
            return BLOCK_REJECTED;
        
        String type=data.getTypeAttribute();
        String from=data.getAttribute("from");
        String id=data.getAttribute("id");
        
        if (type.equals("result") || type.equals("error")) {
            if (id.equals(PING)) {
                StaticData.getInstance().roster.theStream.pingSent=false;
                return BLOCK_PROCESSED;
            }
        }
        if (type.equals("get")) {
            JabberDataBlock ping=data.getChildBlock(PING);
            if (ping!=null) {
                if (ping.getAttribute("xmlns").equals("urn:xmpp:ping")) {
                    Iq reply=new Iq(from, Iq.TYPE_RESULT, id);
                    StaticData.getInstance().roster.theStream.send(reply);
                    return BLOCK_PROCESSED;
                }
            }
        }
        if (type.equals("result")) {
            if (id.startsWith(_PING_)) {
                Roster roster=StaticData.getInstance().roster;
                Contact c=roster.getContact(from, false);

                String pong = pingToString(id.substring(6));

                roster.querysign=false;

                if (pong!=null) {
                    Msg m=new Msg(Msg.SYSTEM, "pong", " "+SR.get(SR.MS_PING), pong);
                    roster.messageStore(c, m);
                    roster.redraw();
                }
                return BLOCK_PROCESSED;
            }
        }
        return BLOCK_REJECTED;
    }

    private String pingToString(String time) {
        String timePing=Long.toString((Time.utcTimeMillis()-Long.parseLong(time))/10);
        int dotpos=timePing.length()-2;
        String first = (dotpos==0)? "0":timePing.substring(0, dotpos);
        String second = timePing.substring(dotpos);

        StringBuffer s=new StringBuffer(first)
        .append('.')
        .append(second)
        .append(' ')
        .append(Time.goodWordForm (Integer.parseInt(second), 0));

        return s.toString();
    }
}
