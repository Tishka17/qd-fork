/*
 * LightControlItem.java
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

//#ifdef LIGHT_CONTROL
package ui.controls.form;

import client.Config;
import colors.ColorTheme;
import javax.microedition.lcdui.Graphics;
import midlet.BombusQD;
import ui.VirtualCanvas;
import ui.VirtualList;

public final class LightControlItem extends TrackItem {
    private int lightValues[] = {0, 1, 2, 3, 5, 7, 10, 15, 20, 25, 30, 35, 40, 45, 50, 60, 70, 80, 90, 100};
    private int index = 0;

    public LightControlItem(int value) {
        super(value, 19);

        calculateIndex();
    }

    public void onSelect(VirtualList view) {
        setIndex((index + 1) % steps);
    }

    public void drawItem(VirtualList view, Graphics g, int ofs, boolean sel) {
        int width = g.getClipWidth();
        int height = g.getClipHeight();

        int scrollWidth = getFont().stringWidth(String.valueOf(value));

        int xOffset = getOffset();
        int pos = ((width - scrollWidth - xOffset * 2) * index) / (steps - 1);

        g.clipRect(xOffset, 0, g.getClipWidth(), itemHeight);

        g.setColor(ColorTheme.getColor(ColorTheme.CONTROL_ITEM));
        g.drawLine(xOffset, height / 2, width - xOffset, height / 2);
        g.fillRect(xOffset + pos, 2, scrollWidth, height - 4);

        g.setFont(getFont());
        g.setColor(0xFFFFFF - g.getColor());
        g.drawString(Integer.toString(value), xOffset + pos, 1, Graphics.TOP|Graphics.LEFT);
    }

    public boolean handleEvent(int keyCode) {
        switch (keyCode) {
            case VirtualCanvas.LEFT:
            case VirtualCanvas.KEY_NUM4:
                setIndex((index > 0) ? index - 1 : steps - 1);
                return true;
            case VirtualCanvas.RIGHT:
            case VirtualCanvas.KEY_NUM6:
                setIndex((index + 1) % steps);
                return true;
        }
        return false;
    }
    
    public boolean handleEvent(int x, int y) {
        final int screenW = BombusQD.sd.canvas.getWidth() - Config.scrollWidth - getOffset();        
        setIndex((steps - 1) * x / screenW);
        return true;
    }

    private void calculateIndex() {
        for (int i = 0; i < lightValues.length; ++i) {
            int tmp = lightValues[i];

            if (value <= tmp) {
                setIndex(Math.max(0, i));
                return;
            }
        }
        setIndex(0);
    }

    private void setIndex(int index) {
        this.index = index;
        value = lightValues[index];
    }
}
//#endif
