/*
 * MucContact.java
 *
 * Created on 2.05.2006, 14:05
 * Copyright (c) 2005-2008, Eugene Stahov (evgs), http://bombus-im.org
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

package conference;

import client.Contact;
import client.Msg;
import com.alsutton.jabber.datablocks.Presence;
import ui.IconTextElement;

 /**
  *
  * @author root(linux detected!),aqent
  */
 public class MucContact extends Contact {
    public final static byte AFFILIATION_MEMBER=1;
    public final static byte AFFILIATION_NONE=0;
    public final static byte ROLE_VISITOR=-1;
    public final static byte ROLE_PARTICIPANT=0;
    public final static byte ROLE_MODERATOR=1;
    public final static byte AFFILIATION_OUTCAST=-1;
    public final static byte AFFILIATION_ADMIN=2;
    public final static byte AFFILIATION_OWNER=3;

    public final static byte GROUP_VISITOR=4;
    public final static byte GROUP_MEMBER=3;
    public final static byte GROUP_PARTICIPANT=2;
    public final static byte GROUP_MODERATOR=1;

     public String realJid = null;
     public byte roleCode;
     public byte affiliationCode;
     
     public String affiliation = "";
     public String role = "";
     public boolean commonPresence=true;
     public long lastMessageTime;

     /** Creates a new instance of MucContact */
     public MucContact(String nick, String jid) {
        super(nick, jid, Presence.PRESENCE_OFFLINE, "muc");
        offline_type=Presence.PRESENCE_OFFLINE;
     }
     
     public void destroy(){
         if(null != realJid) realJid = null;
         if(null != affiliation) affiliation = null;
         if(null != role) role = null;
     }

     public void addMessage(Msg m) {
         super.addMessage(m);
         switch (m.messageType) {
            case Msg.MESSAGE_TYPE_IN:
            case Msg.MESSAGE_TYPE_OUT:
            case Msg.MESSAGE_TYPE_HISTORY: break;
            default: return;
         }
         lastMessageTime=m.dateGmt;
    }
    public int compare(IconTextElement right){
        if (right instanceof MucContact) {
            MucContact c = (MucContact) right;
            if (c.origin==Contact.ORIGIN_GROUPCHAT && origin!=Contact.ORIGIN_GROUPCHAT)
                return 1;
            if (origin==Contact.ORIGIN_GROUPCHAT && c.origin!=Contact.ORIGIN_GROUPCHAT)
                return -1;
            if (c.affiliationCode!=affiliationCode)
                return c.affiliationCode - affiliationCode;
        }
        return super.compare(right);
    }
    public String getTipString() {
        int nm=getNewMsgsCount();
        if (nm!=0)
            return String.valueOf(nm);
        if (realJid!=null)
            return realJid;
        return getJid();
    }
}
