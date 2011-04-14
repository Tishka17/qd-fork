/*
 * AppendNick.java
 *
 * Created on 14.09.2005, 23:32
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
import client.StaticData;
import locale.SR;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import java.util.Vector;
import com.alsutton.jabber.datablocks.Presence;
import ui.controls.form.DefForm;

/**
 *
 * @author EvgS,aqent
 */

public class AppendNickForm extends DefForm {
    private int caretPos;

    private TextField tf;
    private TextBox tb;

    private boolean classic_chat=false;

    public AppendNickForm(Contact to, int caretPos, TextField tf,TextBox tb) {
        this(to, caretPos, tf, tb, false);
    }

    public AppendNickForm(Contact to, int caretPos, TextField tf,TextBox tb,boolean classic_chat) {
        super(SR.get(SR.MS_SELECT_NICKNAME));
        this.caretPos = caretPos;

        if (midlet.BombusQD.cf.msgEditType > 0) {
            this.tf = tf;
        } else {
            this.tb = tb;
        }

        Vector nicknames = new Vector(0);
        Vector contacts = StaticData.getInstance().roster.getHContacts();

        for (int i = 0; i < contacts.size(); ++i) {
            Contact c = (Contact)contacts.elementAt(i);
            if (c.group == to.group && c.origin > Contact.ORIGIN_GROUPCHAT && c.status < Presence.PRESENCE_OFFLINE) {
                nicknames.addElement(c);
            }
        }
        sort(nicknames);
        for (int i = 0; i < nicknames.size(); ++i) {
            addControl(nicknames.elementAt(i));
        }
    }

    public void cmdOk() {
        appendNick();
    }

    public void eventOk() {
        appendNick();
    }

    private void appendNick() {
        if (getItemCount() != 0) {
            String nick = ((Contact)getFocusedObject()).getJid();
            int rp = nick.indexOf('/');
            StringBuffer b = new StringBuffer(nick.substring(rp + 1));

            if (caretPos == 0) {
                b.append(": ");
            }

            if (classic_chat == true) {
                if (midlet.BombusQD.cf.msgEditType > 0) {
                    tf.setString(b.toString());
                } else {
                    tb.setString(b.toString());
                }
            } else {
                if (midlet.BombusQD.cf.msgEditType > 0) {
                    tf.insert(b.toString(), caretPos);
                } else {
                    tb.insert(b.toString(), caretPos);
                }
            }
        }
        destroyView();
    }
}
