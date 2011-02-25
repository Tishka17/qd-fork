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
    private Vector items;
    private int maxValue;

    public TrackItem(int value, int maxValue) {
        super(null);
        this.value = value;
        this.items = null;
        this.maxValue = maxValue;
        this.steps = maxValue + 1;
    }

    public TrackItem(int value, int maxValue, Vector items) {
        this(value, maxValue);

        this.items = items;
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
        int pos = ((width - itemWidth) * value) / (steps - 1);

        int oldColor = g.getColor();

        g.setColor(ColorTheme.getColor(ColorTheme.CONTROL_ITEM));
        g.drawLine(4, height / 2, width - 4, height / 2);

        g.fillRect(pos, 2, itemWidth, height - 4);
        if (items != null) {
            g.setColor(0xFFFFFF - g.getColor());
            g.drawString(Integer.toString(value), pos + 1, height / 4, Graphics.TOP | Graphics.LEFT);
        }
        g.setColor(oldColor);

    }

    public int getVHeight() { //fix it
        if (items != null) {
            return 30;
        } else if (maxValue == 2) {
            return 18;
        }
        if ((midlet.BombusQD.cf.isTouchPhone) && (maxValue == 25)) {
            return 30;
        }
        return maxValue == 25 ? 8 : 30;
    }

    private void loadSkin() {
        if (items != null) {
            ColorTheme.loadSkin("/themes/" + (String)items.elementAt(value) + ".txt", 1, false);
            Config.getInstance().path_skin = "/themes/" + (String) items.elementAt(value) + ".txt";
        }
    }

    public void onSelect(VirtualList view) {
        value = (value + 1) % steps;
        if (items != null) {
            loadSkin();
        }
    }

    public boolean handleEvent(int keyCode) {
        switch (keyCode) {
            case 4:
                value = (value > 0) ? value - 1 : steps - 1;
                if (items != null) {
                    loadSkin();
                }
                return true;
            case 6:
                value = (value + 1) % steps;
                if (items != null) {
                    loadSkin();
                }
                return true;
        }
        return false;
    }
}
