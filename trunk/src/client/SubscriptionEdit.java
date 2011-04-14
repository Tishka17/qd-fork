/*
 * SubscriptionEdit.java
 *
 * Created on 10.05.2005, 19:09
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
package client;

import locale.SR;
import midlet.BombusQD;
import ui.controls.form.DefForm;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.SimpleString;
import ui.controls.form.SpacerItem;

/**
 *
 * @author Evg_S
 */

public class SubscriptionEdit extends DefForm {
    private DropChoiceBox action;
    private String to;

    public SubscriptionEdit(Contact c) {
        super(SR.get(SR.MS_SUBSCRIPTION));

        to = c.bareJid;

		addControl(new SimpleString(c.getNickJid(), true));
        addControl(new SpacerItem(10));

        action = new DropChoiceBox(SR.get(SR.MS_ACTION));
        action.append(SR.get(SR.MS_NO));
        action.append(SR.get(SR.MS_ASK_SUBSCRIPTION));
        action.append(SR.get(SR.MS_GRANT_SUBSCRIPTION));
        action.append(SR.get(SR.MS_SUBSCR_REMOVE));
        addControl(action);

        // first control is unselectable
        moveCursorTo(1);
    }

    public void cmdOk() {
        int actionType = action.getSelectedIndex();

        if (actionType > 0) {
            String type = null;
            switch (actionType) {
                case 1:
                    type = "subscribe";
                    break;
                case 2:
                    type = "subscribed";
                    break;
                case 3:
                    type = "unsubscribed";
                    break;
            }

            if (type != null) {
                BombusQD.sd.roster.sendPresence(to, type, null, false);
            }
        }
        destroyView();
    }
}
