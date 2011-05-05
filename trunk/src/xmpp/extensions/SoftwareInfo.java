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

import client.Msg;
import client.StaticData;
import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Iq;
import java.util.Enumeration;
import java.util.Vector;
import locale.SR;

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
               softinfo.append(SR.get(SR.MS_NAME)).append(": ").append(identity.getAttribute("name")).append("\n");
               softinfo.append(SR.get(SR.MS_CATEGORY)).append(": ").append(identity.getAttribute("category")).append("\n");
               softinfo.append(SR.get(SR.MS_TYPE)).append(": ").append(identity.getAttribute("type")).append("\n");
               int k=0;
               if (childs!=null) {
                 for (Enumeration e=childs.elements(); e.hasMoreElements();) {
                    JabberDataBlock i=(JabberDataBlock)e.nextElement();
                    if (i.getTagName().equals("feature")) {
                        k+=1;
                        String var=i.getAttribute("var");
                        softinfo.append(k).append(") ").append(var).append("\n");
                     }
                  }
                }
                Msg m=new Msg(Msg.MESSAGE_TYPE_IN, "softinfo", SR.get(SR.MS_FEATURES), softinfo.toString());
                //m.highlite=true;
                m.itemCollapsed=true;
                StaticData.getInstance().roster.setQuerySign(false);
                StaticData.getInstance().roster.messageStore(StaticData.getInstance().roster.getContact(data.getAttribute("from"),false),m);
                return NO_MORE_BLOCKS;
            }
         }
       }
       return BLOCK_REJECTED;
    }

    public static JabberDataBlock querySend(String to) {
        JabberDataBlock result=new Iq(to, Iq.TYPE_GET, "discof");
        result.addChildNs("query", "http://jabber.org/protocol/disco#info");
        return result;
    }
}
