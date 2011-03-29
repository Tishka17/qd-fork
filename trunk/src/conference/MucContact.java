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
 
import client.*;
import client.Constants;
import ui.IconTextElement;
 
 /**
  *
  * @author root(linux detected!),aqent
  */
 public class MucContact extends Contact {
     public String realJid = null;
     public byte roleCode;
     public byte affiliationCode;
     
     public String affiliation = "";
     public String role = "";
     public boolean commonPresence=true;
     public long lastMessageTime;

     /** Creates a new instance of MucContact */
     public MucContact(String nick, String jid) {
        super(nick, jid, Constants.PRESENCE_OFFLINE, "muc");
        offline_type=Constants.PRESENCE_OFFLINE;
     }
     
     public void destroy(){
         if(null != realJid) realJid = null;
         if(null != affiliation) affiliation = null;
         if(null != role) role = null;
     }

     public void addMessage(Msg m) {
         super.addMessage(m);
         switch (m.messageType) {
            case Constants.MESSAGE_TYPE_IN:
            case Constants.MESSAGE_TYPE_OUT:
            case Constants.MESSAGE_TYPE_HISTORY: break;
            default: return;
         }
         lastMessageTime=m.dateGmt;
    }
    public int compare(IconTextElement right){
        if (right instanceof MucContact) {
            MucContact c = (MucContact) right;
            if (c.origin==Constants.ORIGIN_GROUPCHAT && origin!=Constants.ORIGIN_GROUPCHAT)
                return 1;
            if (origin==Constants.ORIGIN_GROUPCHAT && c.origin!=Constants.ORIGIN_GROUPCHAT)
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
