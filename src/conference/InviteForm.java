/*
 * InviteForm.java
 *
 * Created on 26.05.2008, 09:37
 *
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
 */

package conference;

import client.Contact;
import client.StaticData;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Message;
import java.util.Enumeration;
import javax.microedition.lcdui.TextField;
import locale.SR;
import ui.controls.form.SimpleString;
import ui.controls.form.DefForm;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.TextInput;
import com.alsutton.jabber.datablocks.Presence;
import ui.controls.form.MultiLine;

public class InviteForm extends DefForm {
    private TextInput reason;
    private DropChoiceBox conferenceList;
    private Contact contact;

    public InviteForm(Contact contact) {
        super(SR.get(SR.MS_INVITE));
        this.contact = contact;

        addControl(new SimpleString(contact.getNickJid(), true));
        conferenceList = new DropChoiceBox(SR.get(SR.MS_CONFERENCE));
        for (Enumeration c = StaticData.getInstance().roster.getHContacts().elements(); c.hasMoreElements();) {
            try {
                MucContact mc = (MucContact)c.nextElement();
                if (mc.origin == Contact.ORIGIN_GROUPCHAT && mc.status == Presence.PRESENCE_ONLINE) {
                    conferenceList.append(mc.getJid());
                }
            } catch (Exception e) {
            }
        }
        if (conferenceList.size() > 0) {
            addControl(conferenceList);

            reason = new TextInput(SR.get(SR.MS_REASON), null, TextField.ANY);
            addControl(reason);
        } else {
            addControl(new MultiLine(null, SR.get(SR.MS_NO_ACTIVE_ROOMS)));
        }

        // first control is unselectable
        moveCursorTo(1);
    }

    public void cmdOk() {
        if (conferenceList.size() != 0) {
            String room = conferenceList.getTextValue();
            String rs = reason.getValue();

            Message inviteMsg = new Message(room);
            JabberDataBlock x = inviteMsg.addChildNs("x", "http://jabber.org/protocol/muc#user");
            JabberDataBlock invite = x.addChild("invite", null);
            String invited = (contact instanceof MucContact) ? ((MucContact)contact).realJid : contact.bareJid;

            invite.setAttribute("to", invited);

            invite.addChild("reason", rs);
            StaticData.getInstance().roster.theStream.send(inviteMsg);
        }
        destroyView();
    }
}
