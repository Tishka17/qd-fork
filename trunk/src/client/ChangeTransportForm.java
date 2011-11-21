/*
 * ChangeTransport.java
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
 *
 */

//#if CHANGE_TRANSPORT
package client;

import com.alsutton.jabber.datablocks.Presence;
import java.util.Enumeration;
import locale.SR;
import midlet.BombusQD;
import ui.controls.form.DefForm;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.MultiLine;

public class ChangeTransportForm extends DefForm {
    private DropChoiceBox selectJid;
    private String jid;

    public ChangeTransportForm(String jid) {
        super(SR.get(SR.MS_TRANSPORT));

        this.jid = jid;

        addControl(new MultiLine("Warning!", "Gateway "+jid+" will be replaced by one from the list of online gateways for all JIDs in your roster (please logoff old gateway to avoid damaging contact list of your guest IM account)", false));

        selectJid = new DropChoiceBox(SR.get(SR.MS_TRANSPORT));
        for (Enumeration e=BombusQD.sd.roster.getHContacts().elements(); e.hasMoreElements(); ){
            Contact ct=(Contact)e.nextElement();
            if (ct.jid.isTransport() && ct.status < Presence.PRESENCE_OFFLINE) {
                selectJid.append(ct.bareJid);
            }
        }
        if (selectJid.size() == 0) {
            //for avoiding exceptions and for resubscribing to all users of the transport ;)
            selectJid.append(jid);
        }
        addControl(selectJid);

        // first control is unselectable
        moveCursorTo(1);
    }

    public void cmdOk() {
        BombusQD.sd.roster.contactChangeTransport(jid, selectJid.getTextValue());
        destroyView();
    }
}
//#endif