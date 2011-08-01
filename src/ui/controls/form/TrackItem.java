/*
 * TrackItem.java
 *
 * Created on 26.05.2008, 11:16
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
import colors.ColorTheme;
import font.FontCache;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import midlet.BombusQD;
import ui.IconTextElement;
import ui.VirtualCanvas;
import ui.VirtualList;

/**
 *
 * @author ad,aqent
 */

public class TrackItem extends IconTextElement {
    protected static final int ITEM_HEIGHT = 16;

    protected int value;
    protected int steps;

    public TrackItem(int value, int maxValue) {
        super(null);
        this.value = value;
        this.steps = maxValue + 1;
    }

    public int getValue() {
        return value;
    }

    public int getVWidth() {
        return -1;
    }

    public void drawItem(VirtualList view, Graphics g, int ofs, boolean sel) {
        int width = g.getClipWidth();
        int height = g.getClipHeight();

        int scrollWidth = getFont().stringWidth(String.valueOf(value));

        int xOffset = getOffset();
        int pos = ((width - scrollWidth - xOffset * 2) * value) / (steps - 1);

        g.clipRect(xOffset, 0, g.getClipWidth(), itemHeight);

        g.setColor(ColorTheme.getColor(ColorTheme.CONTROL_ITEM));
        g.drawLine(xOffset, height / 2, width - xOffset, height / 2);
        g.fillRect(xOffset + pos, 2, scrollWidth, height - 4);

        g.setFont(getFont());
        g.setColor(0xFFFFFF - g.getColor());
        g.drawString(
                Integer.toString(value), 
                xOffset + pos, 
                (itemHeight - getFont().getHeight()) / 2, 
                Graphics.TOP|Graphics.LEFT);
    }

    public int getVHeight() {
        if (0 == itemHeight) {
            itemHeight = Math.max(Config.getInstance().minItemHeight,
                    Math.max(getFont().getHeight(), ITEM_HEIGHT));
        }
        return itemHeight;
    }

    public void onSelect(VirtualList view) {
        value = (value + 1) % steps;
    }

    public boolean handleEvent(int keyCode) {
        switch (keyCode) {
            case VirtualCanvas.NAVIKEY_LEFT:
            case VirtualCanvas.KEY_NUM4:
                value = (value > 0) ? value - 1 : steps - 1;
                return true;
            case VirtualCanvas.NAVIKEY_RIGHT:
            case VirtualCanvas.KEY_NUM6:
                value = (value + 1) % steps;
                return true;
        }
        return false;
    }
    
    public boolean handleEvent(int x, int y) {
        final int screenW = BombusQD.sd.canvas.getWidth() - Config.scrollWidth - getOffset();        
        value = (steps - 1) * x / screenW;
        return true;
    }

    public final Font getFont() {
        return FontCache.getFont(false, Config.getInstance().rosterFont);
    }
}
