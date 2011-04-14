/*
 * AffiliationModify.java
 *
 * Created on 30.10.2005, 15:32
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
 *
 */

package conference.affiliation;

import client.StaticData;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.JabberStream;
import com.alsutton.jabber.datablocks.Iq;
import javax.microedition.lcdui.*;
import locale.SR;
import ui.controls.AlertBox;
import ui.controls.form.DefForm;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.TextInput;

/**
 *
 * @author EvgS
 */

public final class AffiliationEditForm extends DefForm {
    private static final String REASON_RECENT_ID = "reason";

    private TextInput jidItem;
    private DropChoiceBox affiliationItem;
    private TextInput reasonItem;

    private String room;

    public AffiliationEditForm(String room, String jid, String affiliation, String reason) {
        super(SR.get(SR.MS_AFFILIATION));

        this.room = room;

        jidItem = new TextInput("JID", jid, TextField.ANY);
        addControl(jidItem);

        affiliationItem = new DropChoiceBox(SR.get(SR.MS_SET_AFFILIATION));
        affiliationItem.append("owner");
        affiliationItem.append("admin");
        affiliationItem.append("member");
        affiliationItem.append("none");
        affiliationItem.append("outcast");
        affiliationItem.setSelectedIndex(AffiliationItem.getIndexByName(affiliation));
        addControl(affiliationItem);

        reasonItem = new TextInput(SR.get(SR.MS_REASON), reason, REASON_RECENT_ID, TextField.ANY);
        addControl(reasonItem);
    }

    public void cmdOk() {
        if (jidItem.getValue().length() == 0) {
            return;
        }

        JabberStream stream=StaticData.getInstance().roster.theStream;

        JabberDataBlock request=new Iq(room, Iq.TYPE_SET, "admin_modify");
        JabberDataBlock query=request.addChildNs("query", "http://jabber.org/protocol/muc#admin");
        JabberDataBlock child=query.addChild("item", null);
        child.setAttribute("jid", jidItem.getValue());
        child.setAttribute("affiliation", AffiliationItem.getNameByIndex(affiliationItem.getSelectedIndex()));

        String rs=reasonItem.getValue();
        if (rs.length() != 0) {
            child.addChild("reason", rs);
        }

        stream.send(request);
        try {
            Thread.sleep(300);
        } catch (Exception ex) {}

        try {
            AffiliationList a = (AffiliationList)getParentView();
            a.getList();
        } catch (Exception e) {}

        destroyView();
    }
}
