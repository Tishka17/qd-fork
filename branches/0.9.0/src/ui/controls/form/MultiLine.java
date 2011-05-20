/*
 * MultiLine.java
 *
 * Created on 25.05.2008, 18:37
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
import font.FontCache;
import java.util.Vector;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import midlet.BombusQD;
import ui.IconTextElement;
import util.StringUtils;
import ui.VirtualList;

/**
 *
 * @author ad
 */

public final class MultiLine extends IconTextElement {
    private Vector lines = null;
    private String text;
    private String caption;
    public boolean selectable = true;
    private Font font;
    private int fontHeight;
    private Font captionFont;
    private int captionFontHeight;
    private boolean parsed;

    private int width;

    public MultiLine(String caption, String text) {
        this(caption, text, true);
    }

    public MultiLine(String caption, String text, boolean selectable) {
        super(null);
        this.text = text;
        this.caption = caption;
        this.selectable = selectable;

        font = FontCache.getFont(false, Config.msgFont);
        fontHeight = font.getHeight();
        itemHeight = fontHeight;

        width = BombusQD.sd.canvas.getWidth() - getOffset() - Config.scrollWidth;

        if (caption != null) {
            captionFont = FontCache.getFont(true, Config.msgFont);
            captionFontHeight = captionFont.getHeight();
            itemHeight = captionFontHeight;
        }
    }

    public String getValue() {
        return text;
    }

    public String toString() {
        if (caption == null) {
            return text;
        }

        return caption + "\n" + text;
    }

    public int getVWidth() {
        return -1;
    }

    public int getVHeight() {
        if (lines == null && width > 0) {
            lines = StringUtils.parseMessage(text, width, font);
            itemHeight = (fontHeight * lines.size());
            if (caption != null) {
                itemHeight += captionFontHeight;
            }
            parsed = true;
        }
        return itemHeight;
    }

    public void drawItem(VirtualList view, Graphics g, int ofs, boolean sel) {
        if (!parsed) {
            return;
        }

        int xOffset = getOffset();
        int y = 0;
        if (caption != null) {
            g.setFont(captionFont);
            g.drawString(caption, xOffset, y, Graphics.TOP | Graphics.LEFT);
            y = captionFontHeight;
        }

        g.setFont(font);
        String str;
        for (int line = 0; line < lines.size(); line++) {
            str = (String)lines.elementAt(line);
            if (str != null && str.length() > 0) {
                g.drawString(str, xOffset, y, Graphics.TOP | Graphics.LEFT);
            }
            y += fontHeight;
        }
    }

    public int getOffset() {
        return 2;
    }

    public boolean isSelectable() {
        return selectable;
    }
}