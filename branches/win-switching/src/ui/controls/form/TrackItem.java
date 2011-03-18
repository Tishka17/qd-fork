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
import javax.microedition.lcdui.Graphics;
import ui.IconTextElement;
import java.util.Vector;
import ui.VirtualList;

/**
 *
 * @author ad,aqent
 */

public class TrackItem extends IconTextElement {
    private int value;
    private int steps;

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

        int itemWidth = 16;

        int xOffset = getOffset();
        int pos = ((width - itemWidth - xOffset * 2) * value) / (steps - 1);

        g.clipRect(getOffset(), 0, g.getClipWidth(), itemHeight);

        g.setColor(ColorTheme.getColor(ColorTheme.CONTROL_ITEM));
        g.drawLine(xOffset, height / 2, width - xOffset, height / 2);
        g.fillRect(xOffset + pos, 2, itemWidth, height - 4);
    }

    public int getVHeight() { //fix it
        if (0 == itemHeight) {
            itemHeight = 16;
        }
        if (itemHeight < midlet.BombusQD.cf.minItemHeight) {
            itemHeight = midlet.BombusQD.cf.minItemHeight;
        }
        return itemHeight;
    }

    public void onSelect(VirtualList view) {
        value = (value + 1) % steps;
    }

    public boolean handleEvent(int keyCode) {
        switch (keyCode) {
            case 4:
                value = (value > 0) ? value - 1 : steps - 1;
                return true;
            case 6:
                value = (value + 1) % steps;
                return true;
        }
        return false;
    }
}
