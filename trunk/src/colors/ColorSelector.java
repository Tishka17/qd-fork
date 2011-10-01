/*
 * ColorSelector.java
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

//#ifdef COLOR_TUNE
package colors;

import font.FontCache;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import ui.VirtualCanvas;
import ui.controls.form.DefForm;
//#ifdef GRADIENT
import ui.Gradient;
//#endif

public class ColorSelector extends DefForm {
    private Font font;
    private int fontH;

    private int w, h;

    int cpos;
    int alpha,red,green,blue;
    private boolean hasAlphaChannel = true;
//#ifdef GRADIENT
    private Gradient example = new Gradient();
//#endif

    boolean exit;

    private int py;
    private int ph;

    private int boxY, boxW;

    private ColorVisualItem item;

    public ColorSelector(ColorVisualItem item) {
        super(item.toString());

        this.item = item;

        this.font = FontCache.getFont(false, FontCache.SMALL);
        this.fontH = font.getHeight();

        w = getWidth() - scrollbar.getScrollWidth();
        h = getHeight();

        if (infobar != null) {
            h -= infobar.getVHeight();
        }
        if (mainbar != null) {
            h -= mainbar.getVHeight();
        }

        boxY = h / 2 - 40;
        boxW = w * 2 / 7;

        py = h - h / 10;
        ph = h - h * 3 / 10;

        switch (item.getIndex()) {
            case ColorTheme.GRAPHICS_MENU_BGNG_ARGB:
            case ColorTheme.TRANSPARENCY_ARGB:
            case ColorTheme.POPUP_MESSAGE_BGND:
            case ColorTheme.POPUP_SYSTEM_BGND:
            case ColorTheme.CURSOR_BGND:
            case ColorTheme.GRADIENT_CURSOR_1:
            case ColorTheme.GRADIENT_CURSOR_2:
            case ColorTheme.HEAP_FREE:
            case ColorTheme.HEAP_TOTAL:
            case ColorTheme.BAR_BGND:
            case ColorTheme.BAR_BGND_BOTTOM:
            case ColorTheme.BALLOON_BGND:
            case ColorTheme.SCROLL_BGND:
            case ColorTheme.SCROLL_BAR:
                break;
            default:
                hasAlphaChannel = false;
                break;
        }

        int color = ColorTheme.getColor(item.getIndex());
        red=ColorTheme.getRed(color);
        green=ColorTheme.getGreen(color);
        blue=ColorTheme.getBlue(color);
        alpha=ColorTheme.getAlpha(color);

        cpos = 0;

        exit = false;
    }

    public void paint(Graphics g) {
        super.paint(g);

        g.setFont(font);
        g.setColor(ColorTheme.getColor(ColorTheme.LIST_INK));

        String s;
        if (hasAlphaChannel) {
            s = ColorTheme.colorToString(red, green, blue, alpha);
        } else {
            s = ColorTheme.colorToString(red, green, blue);
        }
        g.drawString(s, 5, boxY - fontH, Graphics.TOP | Graphics.LEFT);

        g.setStrokeStyle(Graphics.SOLID);

        g.setColor(0x000000);
        g.drawRect(4, boxY - 1, boxW + 1, 81);
//#ifdef GRADIENT
        example.update(4, boxY, 5+boxW, boxY+81, ColorTheme.getColor(alpha, red, green, blue), ColorTheme.getColor(alpha, red, green, blue), Gradient.CACHED_HORIZONTAL);
        example.paint(g);
//#else
//#         g.setColor(red, green, blue);
//#         g.fillRect(5, boxY, boxW, 80);
//#endif        

        //draw red
        int pxred = (w * 3 / 7);
        int psred = (ph * red) / 255;
        g.setColor(0);
        g.fillRect(pxred, py - ph, w / 10, ph);
        g.setColor(0xff1111);
        g.fillRect(pxred, py - psred, w / 10, psred);
        if (cpos != 0) {
            g.setColor(0xffbbbb);
        }
        g.fillArc(pxred, py - ph - h * 7 / 100, w / 10 - 1, h / 10 - 1, 0, 180);
        g.fillArc(pxred, py - h * 3 / 100, w / 10 - 1, h / 10 - 1, 180, 180);

        //draw green
        int pxgreen = (w * 4 / 7);
        int psgreen = (ph * green) / 255;
        g.setColor(0);
        g.fillRect(pxgreen, py - ph, w / 10, ph);
        g.setColor(0x00ee00);
        g.fillRect(pxgreen, py - psgreen, w / 10, psgreen);
        if (cpos != 1) {
            g.setColor(0xbbffbb);
        }
        g.fillArc(pxgreen, py - ph - h * 7 / 100, w / 10 - 1, h / 10 - 1, 0, 180);
        g.fillArc(pxgreen, py - h * 3 / 100, w / 10 - 1, h / 10 - 1, 180, 180);

        //draw blue
        int pxblue = (w * 5 / 7);
        int psblue = (ph * blue) / 255;
        g.setColor(0);
        g.fillRect(pxblue, py - ph, w / 10, ph);
        g.setColor(0x3333ff);
        g.fillRect(pxblue, py - psblue, w / 10, psblue);
        if (cpos != 2) {
            g.setColor(0xbbbbff);
        }
        g.fillArc(pxblue, py - ph - h * 7 / 100, w / 10 - 1, h / 10 - 1, 0, 180);
        g.fillArc(pxblue, py - h * 3 / 100, w / 10 - 1, h / 10 - 1, 180, 180);

        if (hasAlphaChannel) {
            int pxalpha = (w * 6 / 7);
            int pspxalpha = (ph * alpha) / 255;
            g.setColor(0);
            g.fillRect(pxalpha, py - ph, w / 10, ph);
            g.setColor(0x666666);
            g.fillRect(pxalpha, py - pspxalpha, w / 10, pspxalpha);
            if (cpos != 3) {
                g.setColor(0xaaaaaa);
            }
            g.fillArc(pxalpha, py - ph - h * 7 / 100, w / 10 - 1, h / 10 - 1, 0, 180);
            g.fillArc(pxalpha, py - h * 3 / 100, w / 10 - 1, h / 10 - 1, 180, 180);
        }
    }

//#ifdef TOUCH
    protected void pointerPressed(int x, int y) {
        if ((y < py - ph - h * 7 / 100) || (y > py + h * 7 / 100)) {
            super.pointerPressed(x, y);
        }

        if (x > 3 * w / 7 && x < (3 * w / 7 + w / 10)) {
            cpos = 0;
        } else if (x > (4 * w / 7) && x < (4 * w / 7 + w / 10)) {
            cpos = 1;
        } else if (x > (5 * w / 7) && x < (5 * w / 7 + w / 10)) {
            cpos = 2;
        } else if (hasAlphaChannel && (x > (6 * w / 7) && x < (6 * w / 7 + w / 10))) {
            cpos = 3;
        } else {
            return;
        }

        if ((y < py - ph) || (y > py)) {
            if (y < py - ph) {
                movePoint(1);
            } else {
                movePoint(-1);
            }
        } else {
            switch (cpos) {
                case 0:
                    red = (py - y) * 255 / ph;
                    break;
                case 1:
                    green = (py - y) * 255 / ph;
                    break;
                case 2:
                    blue = (py - y) * 255 / ph;
                    break;
                case 3:
                    if (hasAlphaChannel) alpha = (py - y) * 255 / ph;
                    break;

            }

            redraw();
        }
     }
//#endif

    public void keyPressed(int key) {
        switch (key) {
            case VirtualCanvas.NAVIKEY_UP:
            case VirtualCanvas.KEY_NUM2:
                movePoint(1);
                return;
            case VirtualCanvas.NAVIKEY_DOWN:
            case VirtualCanvas.KEY_NUM8:
                movePoint(-1);
                return;
            case VirtualCanvas.NAVIKEY_LEFT:
            case VirtualCanvas.KEY_NUM4:
                moveCursorLeft();
                return;
            case VirtualCanvas.NAVIKEY_RIGHT:
            case VirtualCanvas.KEY_NUM6:
                moveCursorRight();
                return;
            case VirtualCanvas.KEY_NUM3:
                movePoint(+16);
                return;
            case VirtualCanvas.KEY_NUM9:
            case VirtualCanvas.KEY_NUM1:
                movePoint(-16);
                return;
            case VirtualCanvas.KEY_NUM0:
                destroyView();
                break;
        }
        super.keyPressed(key);
    }

    private void moveCursorLeft() {
        if (hasAlphaChannel) {
            cpos -= 1;
            if (cpos < 0) {
                cpos = 3;
            }
        } else {
            cpos -= 1;
            if (cpos < 0) {
                cpos = 2;
            }
        }
        redraw();
    }

    private void moveCursorRight() {
        if (hasAlphaChannel) {
            cpos += 1;
            if (cpos > 3) {
                cpos = 0;
            }
        } else {
            cpos += 1;
            if (cpos > 2) {
                cpos = 0;
            }
        }
        redraw();
    }

    private void movePoint(int dy) {
        if (dy == 0) return;
        switch (cpos) {
            case 0:
                red=dy+red;
                if (red>255) red=0;
                if (red<0) red=255;
                break;
            case 1:
                green=dy+green;
                if (green>255) green=0;
                if (green<0) green=255;
                break;
            case 2:
                blue=dy+blue;
                if (blue>255) blue=0;
                if (blue<0) blue=255;
                break;
            case 3:
                if (hasAlphaChannel) {
                    alpha=dy+alpha;
                    if (alpha>255) alpha=0;
                    if (alpha<0) alpha=255;
                }
                break;
        }
        redraw();
    }

    public void cmdOk() {
        applyChanges();
        destroyView();
    }

    public void eventOk () {
        applyChanges();
        destroyView();
    }

    private void applyChanges() {
        int color=ColorTheme.getColor(alpha, red, green, blue);
        item.setColor(color);
        ColorTheme.setColor(item.getIndex(), color);
        ColorTheme.saveToStorage();
    }

    public void destroyView() {
        exit = true;
        super.destroyView();
    }
}
//#endif
