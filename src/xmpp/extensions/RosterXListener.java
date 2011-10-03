/*
 * RosterXListener.java
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

//#if ROSTERX

package xmpp.extensions;

import client.Contact;
import client.Group;
import client.Groups;
import client.Msg;
import client.StaticData;
import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Iq;
import com.alsutton.jabber.datablocks.Message;
import com.alsutton.jabber.datablocks.Presence;
import java.util.Enumeration;
import java.util.Vector;
import ui.controls.form.DefForm;
import ui.controls.form.MultiLine;
import ui.controls.form.SpacerItem;
/**
 *
 * @author Vitaly
 */
public class RosterXListener implements JabberBlockListener {

    public RosterXListener() {};
    public void destroy() {
    }

    public int blockArrived(JabberDataBlock data) {
        if (data instanceof Presence)
            return BLOCK_REJECTED;
        JabberDataBlock x=data.findNamespace("x", "http://jabber.org/protocol/rosterx");
        if (x == null) return BLOCK_REJECTED;
        if (data instanceof Iq) {
            Iq reply=new Iq(data.getAttribute("from"), Iq.TYPE_RESULT, data.getAttribute("id"));
            StaticData.getInstance().roster.theStream.send(reply);
        }
        Vector addcontacts = new Vector();
        Vector delcontacts = new Vector();
        Vector modcontacts = new Vector();
        
        String sender = data.getAttribute("from");
        JabberDataBlock item;
        String action;
        Contact newcontact;
        for (Enumeration e=x.getChildBlocks().elements(); e.hasMoreElements();)  {
            item = null;
            item = (JabberDataBlock)e.nextElement();
            if (item != null) {
                action = null;
                action = item.getAttribute("action");
                if (action != null) {
                    newcontact = null;
                    newcontact = new Contact(item.getAttribute("name"), item.getAttribute("jid"), Presence.PRESENCE_OFFLINE, null);
                    newcontact.group = new Group(item.getChildBlockText("group"), Groups.TYPE_COMMON);
                    if (action.equals("add")) {
                        addcontacts.addElement(newcontact);
                    } else if (action.equals("delete")) {
                        delcontacts.addElement(newcontact);
                    } else if (action.equals("modify")) {
                        modcontacts.addElement(newcontact);
                    }
                }
            }
        }
        String body=null;
        if (data instanceof Message) {
            body=((Message)data).getBody();
        }
        if (!addcontacts.isEmpty() || !modcontacts.isEmpty() || !delcontacts.isEmpty())
            new RosterAddForm(sender, body, addcontacts, delcontacts, modcontacts).show();
        return BLOCK_PROCESSED;
    }
}


class RosterAddForm extends DefForm {
    Vector add;
    Vector del;
    Vector mod;
    public RosterAddForm (String sender, String body, Vector additems, Vector delitems, Vector moditems) {
        super(sender);
        add = additems;
        del = delitems;
        mod = moditems;
        Contact c;
        if (body!=null) 
            itemsList.addElement(new Msg(Msg.INCOMING, sender, body));
        itemsList.addElement(new SpacerItem(5));
        if (!add.isEmpty()) {
            itemsList.addElement(new MultiLine("Add contacts", "Add " + add.size() + " contacts to your roster?"));
            for (Enumeration e = add.elements(); e.hasMoreElements();) {
                c = (Contact)e.nextElement();
                itemsList.addElement(c);
            }
        }
        if (!del.isEmpty()) {
            itemsList.addElement(new MultiLine("Delete contacts", "Delete " + del.size() + " contacts from your roster?"));
            for (Enumeration e = del.elements(); e.hasMoreElements();) {
                c = (Contact)e.nextElement();
                itemsList.addElement(c);
            }
        }
        if (!mod.isEmpty()) {
            itemsList.addElement(new MultiLine("Modify contacts", "Modify " + mod.size() + " contacts in your roster?"));
            for (Enumeration e = mod.elements(); e.hasMoreElements();) {
                c = (Contact)e.nextElement();
                itemsList.addElement(c);
            }
        }
    }
    
    public void cmdOk() {
        Contact c;
        for (Enumeration e = add.elements(); e.hasMoreElements();) {
            c = (Contact)e.nextElement();
            StaticData.getInstance().roster.storeContact(c.bareJid.toString(), c.getNick(), c.group.getName(), true);
        }
        for (Enumeration e = del.elements(); e.hasMoreElements();) {
            c = (Contact)e.nextElement();
            StaticData.getInstance().roster.deleteContact(c);
        }
        for (Enumeration e = mod.elements(); e.hasMoreElements();) {
            c = (Contact)e.nextElement();
            StaticData.getInstance().roster.storeContact(c.bareJid.toString(), c.getNick(), c.group.getName(), true);
        }
        destroyView();
    }
}

//#endif