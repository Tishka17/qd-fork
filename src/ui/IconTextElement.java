/*
 * IconTextList.java
 *
 * Created on 30.01.2005, 18:19
 *
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
 */

package ui;

import client.Config;
import colors.ColorTheme;
import font.FontCache;
import images.ImageList;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

public class IconTextElement implements VirtualElement {
    protected int itemHeight;

    protected ImageList il;
    protected int imgHeight = 0;
    protected int imgWidth = 0;
    protected String text=null;
    protected int imageindex=-1;

    public IconTextElement(ImageList il) {
        super();
        setImageList(il);
    }
    public IconTextElement(String text, ImageList il, int index) {
        super();
        setImageList(il);
        this.text = text;
        this.imageindex = index;
    }
    public void setImageList(ImageList il) {
        this.il = il;
        if (il != null) {
            imgHeight = il.getHeight();
            imgWidth = il.getWidth();
        }
    }
    public boolean isSelectable() {
        return true;
    }

    public boolean handleEvent(int keyCode) {
        return false;
    }
    
    public boolean handleEvent(int x, int y) {
        return false;
    } 

    public String toString() {
        return text;
    }
    public int getImageIndex() {
        return imageindex;
    }
    public void setImageIndex(int imageindex) {
        this.imageindex = imageindex;
    }

    public boolean getFontIndex() {
        return false;
    }

    public Font getFont() {
        return FontCache.getFont(getFontIndex(), Config.rosterFont);
    }

    public void drawItem(VirtualList view, Graphics g, int ofs, boolean sel) {
        g.setFont(getFont());

        int xOffset = getOffset();
        if (null != il) {
            if (getImageIndex() != -1) {
                il.drawImage(g, getImageIndex(), xOffset , (itemHeight - imgHeight) / 2);
                xOffset += imgHeight;
            }
        }
        g.clipRect(xOffset, 0, g.getClipWidth(), itemHeight);

        String str = toString();
        if (null != str) {
            int yOffset = getFont().getHeight();
            g.drawString(str, xOffset - ofs, (itemHeight - yOffset) / 2, Graphics.TOP | Graphics.LEFT);
        }
    }

    public int getOffset() {
        return 4;
    }

    public int getVWidth() {
        return getFont().stringWidth(toString()) + imgHeight + 4;
    }

    public int getVHeight() {
        if (0 == itemHeight) {
            itemHeight = getFont().getHeight();
            if (null != il) {
                itemHeight = Math.max(itemHeight, il.getHeight());
            }
        }
        if (itemHeight < midlet.BombusQD.cf.minItemHeight) {
            itemHeight = midlet.BombusQD.cf.minItemHeight;
        }
        return itemHeight;
    }

    public void onSelect(VirtualList view) {

    }

    public int getColorBGnd() {
        return ColorTheme.getColor(ColorTheme.LIST_BGND);
    }

    public int getColor() {
        return ColorTheme.getColor(ColorTheme.LIST_INK);
    }

    public String getTipString() {
        return null;
    }

    public int compare(IconTextElement right) {
        return 0;
    }
}
