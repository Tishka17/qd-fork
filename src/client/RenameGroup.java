/*
 * RenameGroup.java
 *
 * Created on 20.05.2008, 15:26
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
package client;

import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Iq;
import java.util.Enumeration;
import javax.microedition.lcdui.TextField;
import locale.SR;
import midlet.BombusQD;
import ui.controls.form.DefForm;
import ui.controls.form.TextInput;

/**
 *
 * @author ad
 */

public class RenameGroup extends DefForm {
    private Group group;
    private TextInput groupName;

    public RenameGroup(Group group) {
        super(SR.get(SR.MS_RENAME));
        this.group = group;

        groupName = new TextInput(null, group.getName(), "groups", TextField.ANY);
        addControl(groupName);
    }

    public void cmdOk() {
        BombusQD.sd.roster.theStream.send(new IqQueryRenameGroup(group.getName(), groupName.getValue()));
        destroyView();
    }

    private static class IqQueryRenameGroup extends Iq {
        private IqQueryRenameGroup(String sourceGroup, String destGroup) {
            super(null, Iq.TYPE_SET, "addros");

            JabberDataBlock qB = addChildNs("query", "jabber:iq:roster");
            for (Enumeration e = BombusQD.sd.roster.getHContacts().elements(); e.hasMoreElements();) {
                Contact cr = (Contact)e.nextElement();
                if (cr.group.getName().equals(sourceGroup)) {
                    JabberDataBlock item = qB.addChild("item", null);
                    item.setAttribute("jid", cr.bareJid);
                    item.setAttribute("name", cr.getNick());
                    item.setAttribute("subscription", null);
                    if (destGroup != null && destGroup.length() > 0) {//patch by Tishka17
                        item.addChild("group", destGroup);
                    }
                }
            }
        }
    }
}
