/*
 * ConferenceQuickPrivelegeModify.java
 *
 * Created on 12.11.2006, 19:02
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
import com.alsutton.jabber.datablocks.Iq;
import conference.MucContact;
import javax.microedition.lcdui.TextField;
import locale.SR;
import ui.controls.form.DefForm;
import ui.controls.form.MultiLine;
import ui.controls.form.TextInput;

/**
 *
 * @author Evg_S
 */

public final class QuickPrivelegyEditForm extends DefForm {
    private static final String REASON_RECENT_ID = "reason";

    public final static int KICK=1;
    public final static int VISITOR=2;
    public final static int PARTICIPANT=3;
    public final static int MODERATOR=4;

    public final static int OUTCAST=5;
    public final static int NONE=6;
    public final static int MEMBER=7;
    public final static int ADMIN=8;
    public final static int OWNER=9;

    private TextInput reason;
    private MucContact victim;

    private int action;

    private String myNick;

    public QuickPrivelegyEditForm(MucContact victim, int action, String myNick) {
        super("");

        this.victim=victim;
        this.action=action;
        this.myNick=myNick;

        StringBuffer user=new StringBuffer(victim.getNick());
        if (victim.jid!=null) {
            user.append(" (")
            .append(victim.realJid)
            .append(")");
        }
        addControl(new MultiLine(SR.get(SR.MS_USER), user.toString(), getWidth()));

        reason=new TextInput(SR.get(SR.MS_REASON), null, REASON_RECENT_ID, TextField.ANY);
        addControl(reason);

        moveCursorTo(1);
    }

    public void cmdOk() {
        setMucMod();
        destroyView();
    }

    private void setMucMod(){
        JabberDataBlock iq=new Iq(victim.jid.getBareJid(), Iq.TYPE_SET, "itemmuc");
        JabberDataBlock query=iq.addChildNs("query", "http://jabber.org/protocol/muc#admin");
        //TODO: separate usecases to muc#owner, muc#admin and muc#moderator
        JabberDataBlock item=new JabberDataBlock("item", null, null);
        query.addChild(item);

        try {
            String rzn=reason.getValue();
            String Nick="";
            if (rzn.startsWith("!")) {
                rzn=rzn.substring(1);
            } else {
                Nick=(myNick==null)?myNick:myNick+": ";
            }
            if (rzn.length()!=0 && myNick!=null) {
               item.addChild("reason",Nick+rzn);
            } else {
                item.addChild("reason", Nick);
            }
        } catch (Exception e) {}

        switch (action) {
            case KICK:
                item.setAttribute("role", "none");
                item.setAttribute("nick", victim.getNick());
                break;

            case OUTCAST:
                item.setAttribute("affiliation", "outcast");
                item.setAttribute("jid", victim.realJid);
                break;

            case PARTICIPANT:
                item.setAttribute("role", "participant");
                item.setAttribute("nick", victim.getNick());
                break;

            case VISITOR:
                item.setAttribute("role", "visitor");
                item.setAttribute("nick", victim.getNick());
                break;

            case MODERATOR:
                item.setAttribute("role", "moderator");
                item.setAttribute("nick", victim.getNick());
                break;

            case MEMBER:
                item.setAttribute("affiliation", "member");
                item.setAttribute("jid", victim.realJid);
                break;

            case NONE:
                item.setAttribute("affiliation", "none");
                item.setAttribute("jid", victim.realJid);
                break;

            case ADMIN:
                item.setAttribute("affiliation", "admin");
                item.setAttribute("jid", victim.realJid);
                break;

            case OWNER:
                item.setAttribute("affiliation", "owner");
                item.setAttribute("jid", victim.realJid);

        }

        StaticData.getInstance().roster.theStream.send(iq);
    }
}
