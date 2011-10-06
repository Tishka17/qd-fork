/*
 * ProgressItem.java
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
//#ifdef GRADIENT
import ui.Gradient;
//#endif
import ui.VirtualElement;
import ui.VirtualList;

public final class ProgressItem implements VirtualElement {
    private static final int ITEM_HEIGHT = 16;
    private static final int X_OFFSET = 3;
    private static final int Y_OFFSET = 2;

    private int progress;

    private int topColor;
//#ifdef GRADIENT
    private Gradient gr=new Gradient();
    private int bottomColor;
//#endif

    public ProgressItem() {
        this.topColor = ColorTheme.getColor(ColorTheme.PGS_COMPLETE_TOP);
//#ifdef GRADIENT
        this.bottomColor = ColorTheme.getColor(ColorTheme.PGS_COMPLETE_BOTTOM);
//#endif
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void drawItem(VirtualList view, Graphics g, int ofs, boolean sel) {
        int height = getVHeight() - Y_OFFSET * 2 - 1;
        int width = g.getClipWidth() - X_OFFSET * 2 - 1;
        int filledWidth = progress * width / 100;

        g.setColor(ColorTheme.getColor(ColorTheme.PGS_REMAINED));
        g.fillRect(X_OFFSET, Y_OFFSET, width, height);
        if (filledWidth > 0) {
//#ifdef GRADIENT
            if (topColor != bottomColor) {
                gr.update(X_OFFSET, Y_OFFSET,
                        X_OFFSET + filledWidth,
                        Y_OFFSET + height,
                        topColor, bottomColor, Gradient.CACHED_HORIZONTAL, 0);
                gr.paint(g);
            } else {
//#endif
                g.setColor(topColor);
                g.fillRect(X_OFFSET, Y_OFFSET, filledWidth, height);
//#ifdef GRADIENT
            }
//#endif
        }

        g.setColor(ColorTheme.getColor(ColorTheme.PGS_INK));
        g.drawRect(X_OFFSET, Y_OFFSET, width, height);
        g.drawLine(filledWidth + X_OFFSET, Y_OFFSET, filledWidth + X_OFFSET, height+ Y_OFFSET);
    }

    public int getColorBGnd() {
        return ColorTheme.getColor(ColorTheme.LIST_BGND);
    }

    public int getColor() {
        return ColorTheme.getColor(ColorTheme.LIST_INK);
    }

    public int getVWidth() {
        return -1;
    }

    public int getVHeight() {
        return Math.max(ITEM_HEIGHT, Config.getInstance().minItemHeight);
    }

    public void onSelect(VirtualList list) {}

    public boolean isSelectable() {
        return true;
    }

    public boolean eventKeyPressed(int keyCode) {
        return false;
    }
     
    public boolean eventKeyLong(int keyCode) {
        return false;
    }
//#ifdef TOUCH       
    public boolean eventPointerPressed(int x, int y) {
        return false;
    }
//#endif
    public String getTipString() {
        return null;
    }
}
