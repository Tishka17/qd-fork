/*
 * CheckBox.java
 *
 * Created on 19.05.2008, 22:16
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

package ui.controls.form;

import client.Config;
import images.RosterIcons;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import midlet.BombusQD;
import ui.IconTextElement;
import ui.VirtualList;
import util.StringUtils;

/**
 *
 * @author ad,aqent
 */

public final class CheckBox extends IconTextElement {
    private boolean isChecked = false;

    private String tip;
    private Vector tipLines;

    public CheckBox(String text, boolean isChecked) {
        super(RosterIcons.getInstance());

        int sep = text.indexOf('%');
        if (sep > -1) {
            this.tip = text.substring(sep + 1);
            this.text = text.substring(0, sep);

            tipLines = StringUtils.parseMessage(
                    this.tip,
                    BombusQD.sd.canvas.getWidth() - Config.scrollWidth - getOffset() - 2 - imgWidth,
                    getFont());
        } else {
            this.text = text;
            this.tip = null;
        }
        this.isChecked = isChecked;
    }


    public void onSelect(VirtualList view) {
        isChecked = !isChecked;
    }

    public int getImageIndex() {
        return isChecked ?
            RosterIcons.ICON_CHOICEBOX_CHECKED :
            RosterIcons.ICON_CHOICEBOX_UNCHECKED;
    }

    public void drawItem(VirtualList view, Graphics g, int ofs, boolean sel) {
        g.setFont(getFont());

        int xOffset = getOffset();
        if (null != il) {
            il.drawImage(g, getImageIndex(), xOffset, (itemHeight - imgHeight) / 2);
            xOffset += imgHeight + 5;
         }

        if ((tip != null && !isChecked) || (tip == null)) {
            g.clipRect(xOffset, 0, g.getClipWidth(), itemHeight);

            String str = toString();
            if (null != str) {
                int yOffset = getFont().getHeight();
                g.drawString(str, xOffset - ofs, (itemHeight - yOffset) / 2, Graphics.TOP | Graphics.LEFT);
            }
        } else {
            int fontHeight = getFont().getHeight();

            int size = tipLines.size();

            g.clipRect(xOffset, 0, g.getClipWidth(), getVHeight());

            int height = fontHeight * (size);
            int width = g.getClipWidth() - getOffset();

            g.drawString(text, xOffset - ofs, 0, Graphics.TOP | Graphics.LEFT);

            g.setColor(0xFFFFFF);
            g.fillRoundRect(xOffset, fontHeight + 2, width, height, 9, 9);
            g.setColor(0x000000);
            g.drawRoundRect(xOffset, fontHeight + 2, width, height, 9, 9);

            int y = fontHeight;
            for (int i = 0; i < size; ++i) {
                g.drawString((String)tipLines.elementAt(i), xOffset + 2, y + 2, Graphics.TOP | Graphics.LEFT);
                y += fontHeight;
            }
        }
    }

    public int getVHeight() {
        int fontHeight = getFont().getHeight();
        if (isChecked && tip != null) {
            itemHeight = fontHeight + fontHeight * (tipLines.size()) + 5;
        } else {
            itemHeight = fontHeight;
        }
        if (itemHeight < imgHeight) {
            itemHeight = imgHeight;
        }
        if (itemHeight < Config.getInstance().minItemHeight) {
            itemHeight = Config.getInstance().minItemHeight;
        }
        return itemHeight;
    }

    public boolean getValue() {
        return isChecked;
    }

    public void setValue( boolean value){
        isChecked= value;
    }
}
