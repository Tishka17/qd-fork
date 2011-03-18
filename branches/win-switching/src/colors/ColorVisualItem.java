/*
 * ColorVisualItem.java
 *
 * Created on 23.08.2008, 22:49
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

import javax.microedition.lcdui.Graphics;
import midlet.BombusQD;
import ui.IconTextElement;
import ui.VirtualList;

/**
 *
 * @author ad,aqent
 */

public class ColorVisualItem extends IconTextElement {
    private int index;
    private int color;

    private String locale;

    public ColorVisualItem(String locale, int index) {
        super(null);

        this.index = index;
        this.color = ColorTheme.getColor(index);

        this.locale = locale;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getIndex() {
        return index;
    }

    public String toString() {
        return locale;
    }

    public void drawItem(VirtualList view, Graphics g, int ofs, boolean sel) {
        g.setFont(getFont());

        g.clipRect(4, 0, g.getClipWidth(), itemHeight);

        int yPos = (itemHeight - getFont().getHeight()) / 2;
        g.drawString(locale, 4 - ofs, yPos, Graphics.TOP | Graphics.LEFT);

        int width = g.getClipWidth();
        g.setColor(color);

        g.fillRect(width - itemHeight + 4, 2, itemHeight - 4, itemHeight - 4);
    }

    public int getVHeight() {
        if (0 == itemHeight) {
            int fontHeight = getFont().getHeight();
            if (fontHeight > 16) {
                itemHeight = fontHeight;
            } else {
                itemHeight = 16;
            }
            if (itemHeight < BombusQD.cf.minItemHeight) {
                itemHeight = BombusQD.cf.minItemHeight;
            }
        }
        return itemHeight;
    }

    public int getVWidth() {
        return getFont().stringWidth(toString()) + 4 + itemHeight;
    }

    public void onSelect(VirtualList view) {

    }

    public boolean isSelectable() {
        return true;
    }

    public String getTipString() {
        return ColorTheme.getColorString(color);
    }
}
//#endif
