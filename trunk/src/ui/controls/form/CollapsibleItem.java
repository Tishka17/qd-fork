/*
 * CollapsibleItem.java
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

import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import midlet.BombusQD;
import ui.GMenuConfig;
import ui.IconTextElement;
import ui.VirtualList;
import util.StringUtils;

/**
 *
 * @author ad,aqent
 */

public final class CollapsibleItem extends IconTextElement {
    private boolean isExpanded;

    private String tip;
    private String text;

    private Vector tipLines;


    public CollapsibleItem(String text, boolean isExpanded) {
        super(null);

        int sep = text.indexOf("%");
        if (sep > -1) {
            this.tip = text.substring(sep);
            this.text = text.substring(0, sep);

            tipLines = StringUtils.parseBoxString(this.tip, BombusQD.sd.canvas.getWidth() - 30, getFont());
        } else {
            this.text = text;
            this.tip = null;
        }
        this.isExpanded = isExpanded;
    }

    public String toString() {
        return text;
    }

    public void onSelect(VirtualList view) {
        isExpanded = !isExpanded;
    }

    public void drawItem(VirtualList view, Graphics g, int ofs, boolean sel) {
        g.setFont(getFont());

        int xOffset = getOffset();

        if ((tip != null && !isExpanded) || (tip == null)) {
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

            int height = fontHeight * (size - 1);
            int width = g.getClipWidth() - getOffset();

            g.drawString(text, xOffset - ofs, 0, Graphics.TOP | Graphics.LEFT);

            g.setColor(0xFFFFFF);
            g.fillRoundRect(xOffset, fontHeight + 2, width, height, 9, 9);
            g.setColor(0x000000);
            g.drawRoundRect(xOffset, fontHeight + 2, width, height, 9, 9);

            int y = 0;
            for (int i = 0; i < size; i++) {
                g.drawString((String)tipLines.elementAt(i), xOffset + 3, y + 2, Graphics.TOP | Graphics.LEFT);
                y += fontHeight;
            }
        }
    }

    public int getVWidth(){
        return -1;
    }

    public int getVHeight() {
        int fontHeight = getFont().getHeight();
        if (isExpanded && tip != null) {
            itemHeight = fontHeight * (tipLines.size()) + 5;
        } else {
            itemHeight = fontHeight;
        }
        if (itemHeight < midlet.BombusQD.cf.minItemHeight) {
            itemHeight = midlet.BombusQD.cf.minItemHeight;
        }
        return itemHeight;
    }

    public boolean isSelectable() {
        return true;
    }

    public boolean handleEvent(int keyCode) {
        switch (keyCode) {
            case 12:
            case 5:
                isExpanded = !isExpanded;
                return true;
        }
        return false;
    }
}
