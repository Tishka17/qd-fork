/*
 * ChoiceBox.java
 *
 * Created on 20.05.2008, 9:06
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
import images.RosterIcons;
import java.util.Vector;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import ui.IconTextElement;
import ui.VirtualCanvas;
import ui.VirtualList;

/**
 *
 * @author ad
 */

public  class DropChoiceBox extends IconTextElement {
    public int index = 0;
    public Vector items = new Vector(0);

    private String caption;
    private Font font;
    private int fontHeight;
    private Font captionFont;
    private int captionFontHeight;

    public DropChoiceBox(String caption) {
        super(RosterIcons.getInstance());
        this.caption = caption;
        font = FontCache.getFont(false, Config.rosterFont);
        fontHeight = font.getHeight();
        itemHeight = fontHeight;

        if (caption != null) {
            captionFont = FontCache.getFont(true, Config.rosterFont);
            captionFontHeight = captionFont.getHeight();
            itemHeight += captionFontHeight;
        }
    }

    public String getCaption() {
        return caption;
    }

    public int getCaptionLength() {
        if (caption == null) {
            return 0;
        }
        return captionFont.stringWidth(caption);
    }

    public int getTextLength() {
        String text = getTextValue();
        if (text == null) {
            return 0;
        }
        return font.stringWidth(text);
    }

    public String getTextValue() {
        if (items.size() < 1) {
            return null;
        }
        return items.elementAt(index).toString();
    }

    public String toString() {
        return (getCaptionLength() > getTextLength()) ? caption : getTextValue();
    }

    public void onSelect(VirtualList view) {
        if (items.size() > 1) {
            new DropListBox(items, this).show();
        }
    }

    public int getValue() {
        return index;
    }

    public void append(Object value) {
        items.addElement(value);
        if (value instanceof IconTextElement) {
            int h = captionFontHeight + ((IconTextElement)value).getVHeight();
            if (h>itemHeight) itemHeight = h;
        }
    }

    public void removeAt( int index) { // Mars
        items.removeElementAt( index);
    }

    public void insertAt( Object value, int index) { // Mars
        items.insertElementAt( value, index);
    }

    public void setSelectedIndex(int index) {
        if (index > items.size() - 1) {
            index = 0;
        }
        this.index = index;
    }

    public int size() {
        return items.size();
    }

    public int getSelectedIndex() {
        return index;
    }

    public void drawItem(VirtualList view, Graphics g, int ofs, boolean sel) {
        int width = g.getClipWidth();
        int height = fontHeight;

        int xOffset = 0;
        int baseOffset = getOffset();

        int y = 0;
        if (caption != null) {
            xOffset = (getCaptionLength() > width) ? -ofs + baseOffset: baseOffset;
            g.setFont(captionFont);
            g.drawString(caption, xOffset, y, Graphics.TOP | Graphics.LEFT);
            y = captionFontHeight;
        }
        if (size() > 1) {
            il.drawImage(g, RosterIcons.ICON_COLLAPSED_INDEX, (width - imgHeight) - 1, (y + (height >> 1)) - (imgHeight >> 1));
        }
        Object c = items.elementAt(index);
        if (c instanceof IconTextElement) {
            g.translate(0, y);
            ((IconTextElement)c).drawItem(view, g, ofs, sel);
        } else if (getTextLength() > 0) {
            xOffset = (getTextLength() > width) ? -ofs + baseOffset: baseOffset;
            g.setFont(font);
            g.drawString(getTextValue(), xOffset, y, Graphics.TOP | Graphics.LEFT);
        }
    }

    public int getVHeight() {
        return itemHeight;
    }

    public boolean handleEvent(int keyCode) {
        if (items.size() < 1) {
            return false;
        }

        switch (keyCode) {
            case VirtualCanvas.NAVIKEY_LEFT:
            case VirtualCanvas.KEY_NUM4:
                if (--index < 0) {
                    index = 0;
                }
                return true;
            case VirtualCanvas.NAVIKEY_RIGHT:
            case VirtualCanvas.KEY_NUM6:
                if (++index > items.size() - 1) {
                    index = items.size() - 1;
                }
                return true;
        }
        return false;
    }
}
