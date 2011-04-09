/*
 * LinkString.java
 *
 * Created on 25.05.2008, 13:24
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

package ui.controls.form;

import client.Config;
import colors.ColorTheme;
import font.FontCache;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import ui.IconTextElement;
import ui.VirtualList;

/**
 *
 * @author ad
 */

public abstract class LinkString extends IconTextElement {
    private String text;

    public LinkString(String text) {
        super(null);
        this.text = text;
    }

    public Font getFont() {
        return FontCache.getFont(true, Config.msgFont);
    }

    public int getColor() {
        return ColorTheme.getColor(ColorTheme.MSG_HIGHLIGHT);
    }

    public void drawItem(VirtualList view, Graphics g, int ofs, boolean sel) {
        final int stringWidth = getFont().stringWidth(toString());
        final int fontHeight = getFont().getHeight();

        final int yOffset = (itemHeight - fontHeight) / 2;

        g.setFont(getFont());
        g.setColor(getColor());

        final int lineOffset = yOffset + fontHeight - 1;
        g.drawLine(4, lineOffset, stringWidth + 3, lineOffset);

        g.drawString(text, getOffset() - ofs, yOffset, Graphics.TOP | Graphics.LEFT);
    }

    public String toString() {
        return text;
    }

    public boolean isSelectable() {
        return true;
    }

    public void onSelect(VirtualList view) {
        doAction();
    }

    public abstract void doAction();
}