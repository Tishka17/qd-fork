/*
 * ColorsList.java
 *
 * Created on 23.05.2008, 13:10
 *
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
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
 */

//#ifdef COLOR_TUNE
package colors;

import locale.SR;
import ui.controls.form.DefForm;

/**
 *
 * @author ad,aqent
 */

public class ColorList extends DefForm {
    public ColorList() {
        super(SR.get(SR.MS_THEMES));

        addColorControl(SR.get(SR.MS_BALLOON_INK), ColorTheme.BALLOON_INK);
        addColorControl(SR.get(SR.MS_BALLOON_BGND), ColorTheme.BALLOON_BGND);
        addColorControl(SR.get(SR.MS_LIST_BGND), ColorTheme.LIST_BGND);
        addColorControl(SR.get(SR.MS_LIST_BGND_EVEN), ColorTheme.LIST_BGND_EVEN);
        addColorControl(SR.get(SR.MS_LIST_INK), ColorTheme.LIST_INK);
        addColorControl(SR.get(SR.MS_MSG_SUBJ), ColorTheme.MSG_SUBJ);
        addColorControl(SR.get(SR.MS_MSG_HIGHLIGHT), ColorTheme.MSG_HIGHLIGHT);
        addColorControl(SR.get(SR.MS_DISCO_CMD), ColorTheme.DISCO_CMD);
        addColorControl(SR.get(SR.MS_BAR_BGND), ColorTheme.BAR_BGND);
        addColorControl(SR.get(SR.MS_BAR_BGND) + " 2", ColorTheme.BAR_BGND_BOTTOM);
        addColorControl(SR.get(SR.MS_BAR_INK), ColorTheme.BAR_INK);
        addColorControl(SR.get(SR.MS_CONTACT_DEFAULT), ColorTheme.CONTACT_DEFAULT);
        addColorControl(SR.get(SR.MS_CONTACT_CHAT), ColorTheme.CONTACT_CHAT);
        addColorControl(SR.get(SR.MS_CONTACT_AWAY), ColorTheme.CONTACT_AWAY);
        addColorControl(SR.get(SR.MS_CONTACT_XA), ColorTheme.CONTACT_XA);
        addColorControl(SR.get(SR.MS_CONTACT_DND), ColorTheme.CONTACT_DND);
        addColorControl(SR.get(SR.MS_CONTACT) + " J2J", ColorTheme.CONTACT_J2J);

        addColorControl(SR.get(SR.MS_GROUP_INK), ColorTheme.GROUP_INK);
        addColorControl(SR.get(SR.MS_HEAP_TOTAL), ColorTheme.HEAP_TOTAL);
        addColorControl(SR.get(SR.MS_HEAP_FREE), ColorTheme.HEAP_FREE);

        addColorControl(SR.get(SR.MS_BLK_INK), ColorTheme.BLK_INK);
        addColorControl(SR.get(SR.MS_BLK_BGND), ColorTheme.BLK_BGND);
        addColorControl(SR.get(SR.MS_MESSAGE_IN), ColorTheme.MESSAGE_IN);
        addColorControl(SR.get(SR.MS_MESSAGE_OUT), ColorTheme.MESSAGE_OUT);
        addColorControl(SR.get(SR.MS_MESSAGE_PRESENCE), ColorTheme.MESSAGE_PRESENCE);
        addColorControl(SR.get(SR.MS_MESSAGE_AUTH), ColorTheme.MESSAGE_AUTH);
        addColorControl(SR.get(SR.MS_MESSAGE_HISTORY), ColorTheme.MESSAGE_HISTORY);
        addColorControl(SR.get(SR.MS_MESSAGE_IN_S), ColorTheme.MESSAGE_IN_S);
        addColorControl(SR.get(SR.MS_MESSAGE_OUT_S), ColorTheme.MESSAGE_OUT_S);
        addColorControl(SR.get(SR.MS_MESSAGE_PRESENCE_S), ColorTheme.MESSAGE_PRESENCE_S);
        addColorControl(SR.get(SR.MS_PGS_REMAINED), ColorTheme.PGS_REMAINED);
        addColorControl(SR.get(SR.MS_PGS_COMPLETE), ColorTheme.PGS_COMPLETE_TOP);
        addColorControl(SR.get(SR.MS_PGS_COMPLETE) + " 2", ColorTheme.PGS_COMPLETE_BOTTOM);
        addColorControl(SR.get(SR.MS_PGS_INK), ColorTheme.PGS_INK);

        addColorControl(SR.get(SR.MS_CURSOR_BGND), ColorTheme.CURSOR_BGND);
        addColorControl(SR.get(SR.MS_CURSOR_OUTLINE), ColorTheme.CURSOR_OUTLINE);
        addColorControl(SR.get(SR.MS_SCROLL_BRD), ColorTheme.SCROLL_BRD);
        addColorControl(SR.get(SR.MS_SCROLL_BAR), ColorTheme.SCROLL_BAR);
        addColorControl(SR.get(SR.MS_SCROLL_BGND), ColorTheme.SCROLL_BGND);

        addColorControl(SR.get(SR.MS_POPUP_MESSAGE), ColorTheme.POPUP_MESSAGE_INK);
        addColorControl(SR.get(SR.MS_POPUP_MESSAGE_BGND), ColorTheme.POPUP_MESSAGE_BGND);
        addColorControl(SR.get(SR.MS_POPUP_SYSTEM), ColorTheme.POPUP_SYSTEM_INK);
        addColorControl(SR.get(SR.MS_POPUP_SYSTEM_BGND), ColorTheme.POPUP_SYSTEM_BGND);
        addColorControl(SR.get(SR.MS_CONTACT_STATUS), ColorTheme.SECOND_LINE);
        addColorControl(SR.get(SR.MS_CONTROL_ITEM), ColorTheme.CONTROL_ITEM);
        addColorControl(SR.get(SR.MS_GRADIENT_BGND_LEFT), ColorTheme.GRADIENT_BGND_LEFT);
        addColorControl(SR.get(SR.MS_GRADIENT_BGND_RIGHT), ColorTheme.GRADIENT_BGND_RIGHT);
        addColorControl(SR.get(SR.MS_GRADIENT_CURSOR_1), ColorTheme.GRADIENT_CURSOR_1);
        addColorControl(SR.get(SR.MS_GRADIENT_CURSOR_2), ColorTheme.GRADIENT_CURSOR_2);

        //addColorControl(SR.get(SR.MS_TRANSPARENCY_ARGB), ColorTheme.TRANSPARENCY_ARGB));
        addColorControl(SR.get(SR.MS_GRAPHICS_MENU_BGNG_ARGB), ColorTheme.GRAPHICS_MENU_BGNG_ARGB);
        addColorControl(SR.get(SR.MS_GRAPHICS_MENU_FONT), ColorTheme.GRAPHICS_MENU_FONT);
    }

    private void addColorControl(String text, int colorIndex) {
        addControl(new ColorVisualItem(text, colorIndex));
    }

    public String touchLeftCommand() {
        return SR.get(SR.MS_SELECT);
    }

    public void cmdOk() {
        showColorSelector();
    }

    public void eventOk() {
         showColorSelector();
    }

    private void showColorSelector() {
        ColorVisualItem item = (ColorVisualItem)getFocusedObject();

        new ColorSelector(item).show();
    }
}
//#endif
