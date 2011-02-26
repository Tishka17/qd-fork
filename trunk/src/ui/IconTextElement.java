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
import javax.microedition.lcdui.*;
import colors.ColorTheme;
import font.*;

abstract public class IconTextElement implements VirtualElement {
    protected int itemHeight;
    protected int imageYOfs;
    protected int fontYOfs;
    protected ImageList il;
    protected int ilImageSize=0;
    
    public IconTextElement(ImageList il) {
        this.il = il;
        if (il != null) {
            ilImageSize = il.getHeight();
        }
    }

    public boolean isSelectable() {
        return true;
    }

    public boolean handleEvent(int keyCode) {
        return false;
    }

    public int getImageIndex() {
        return -1;
    }

    ;
    public boolean getFontIndex() {
        return false;
    }
    
    public Font getFont() {
        return FontCache.getFont(getFontIndex(),FontCache.roster);
    }

    public void drawItem(VirtualList view, Graphics g, int ofs, boolean sel, boolean inCommand) {      
        drawItem(view, g, 4 + (inCommand ? ilImageSize : 0), sel);
    }
    
    public void drawItem(VirtualList view, Graphics g, int ofs, boolean sel){
       g.setFont(getFont());

       String str = toString();
       int xOffset = getOffset();
       if (null != il) {
            if (getImageIndex() != -1) {
                int yOffset = il.getHeight();
                il.drawImage(g, getImageIndex(), xOffset , (itemHeight - yOffset) / 2);
                xOffset += ilImageSize;
            }
       }
       g.clipRect(xOffset, 0, g.getClipWidth(), itemHeight);
       if (null != str) {
           int yOffset = getFont().getHeight();
           g.drawString(str, xOffset - ofs, (itemHeight - yOffset) / 2, Graphics.TOP | Graphics.LEFT);
       }
    }
    
    public int getOffset() {
        return 4;
    }

    public int getVWidth(){ 
        return getFont().stringWidth(toString())+ilImageSize+4;
    }

    public int getVHeight() {
        if (0 == itemHeight) {
            itemHeight = getFont().getHeight();
            if (null != il) itemHeight = Math.max(itemHeight, il.getHeight());
        }
		if (itemHeight < midlet.BombusQD.cf.minItemHeight)
			itemHeight = midlet.BombusQD.cf.minItemHeight;
        return itemHeight;
    }

    public int getItemHeight(){ 
        return itemHeight;
    }
    
    public int getColorBGnd() {
        return ColorTheme.getColor(ColorTheme.LIST_BGND);
    }

    public int getColor() {
        return ColorTheme.getColor(ColorTheme.LIST_INK);
    }

    public void onSelect(VirtualList view) {
    }

    ;
    public String getTipString() {
        return null;
    }

    public int compare(IconTextElement right) {
        return 0;
    }
}
