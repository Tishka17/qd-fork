/*
 * ConfigFonts.java
 *
 * Created on 20.05.2008, 15:37
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

package font;

import client.Config;
import locale.SR;
import midlet.BombusQD;
import ui.controls.form.CheckBox;
import ui.controls.form.DefForm;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.SimpleString;

public class FontConfigForm extends DefForm {
    private DropChoiceBox rosterFont;
    private DropChoiceBox msgFont;
    private DropChoiceBox barFont;
    private DropChoiceBox ballonFont;
    private DropChoiceBox menuFont;

    private CheckBox useItalic;

    public FontConfigForm() {
        super(SR.get(SR.MS_FONTS_OPTIONS));

        rosterFont = new DropChoiceBox(SR.get(SR.MS_ROSTER_FONT));
        rosterFont.append(SR.get(SR.MS_FONTSIZE_NORMAL));
        rosterFont.append(SR.get(SR.MS_FONTSIZE_SMALL));
        rosterFont.append(SR.get(SR.MS_FONTSIZE_LARGE));
        rosterFont.setSelectedIndex(Config.rosterFont / 8);
        addControl(rosterFont);

        msgFont = new DropChoiceBox(SR.get(SR.MS_MESSAGE_FONT));
        msgFont.append(SR.get(SR.MS_FONTSIZE_NORMAL));
        msgFont.append(SR.get(SR.MS_FONTSIZE_SMALL));
        msgFont.append(SR.get(SR.MS_FONTSIZE_LARGE));
        msgFont.setSelectedIndex(Config.msgFont / 8);
        addControl(msgFont);
 
        barFont = new DropChoiceBox(SR.get(SR.MS_BAR_FONT));
        barFont.append(SR.get(SR.MS_FONTSIZE_NORMAL));
        barFont.append(SR.get(SR.MS_FONTSIZE_SMALL));
        barFont.append(SR.get(SR.MS_FONTSIZE_LARGE));
        barFont.setSelectedIndex(Config.barFont / 8);
        addControl(barFont);
        
        ballonFont = new DropChoiceBox(SR.get(SR.MS_POPUP_FONT));
        ballonFont.append(SR.get(SR.MS_FONTSIZE_NORMAL));
        ballonFont.append(SR.get(SR.MS_FONTSIZE_SMALL));
        ballonFont.append(SR.get(SR.MS_FONTSIZE_LARGE));
        ballonFont.setSelectedIndex(Config.baloonFont / 8);
        addControl(ballonFont);

        menuFont = new DropChoiceBox(SR.get(SR.MS_MENU_FONT));
        menuFont.append(SR.get(SR.MS_FONTSIZE_NORMAL));
        menuFont.append(SR.get(SR.MS_FONTSIZE_SMALL));
        menuFont.append(SR.get(SR.MS_FONTSIZE_LARGE));
        menuFont.setSelectedIndex(Config.menuFont / 8);
        addControl(menuFont);
        
        useItalic = new CheckBox('*' + SR.get(SR.MS_Italic), Config.useItalic);
        addControl(useItalic);

        // first control is unselectable
        moveCursorTo(1);
    }
    
    public void cmdOk() {
        Config.rosterFont = rosterFont.getValue() * 8;
        Config.baloonFont = ballonFont.getValue() * 8;
        Config.menuFont = menuFont.getValue() * 8;
        Config.msgFont = msgFont.getValue() * 8;
        Config.barFont = barFont.getValue() * 8;

        Config.useItalic = useItalic.getValue();

        BombusQD.sd.roster.updateBarsFont();
        //BombusQD.sd.roster.reEnumRoster();

        destroyView();
    }
}
