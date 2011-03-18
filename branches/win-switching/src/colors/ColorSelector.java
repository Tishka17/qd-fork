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
import javax.microedition.lcdui.*;
import ui.controls.form.DefForm;

//#ifdef LIGHT_CONTROL
import light.CustomLight;
//#endif

public class ColorSelector extends DefForm implements Runnable {
    private Font font;
    private int fontH;

    private int w, h;

    int cpos;
    int alpha,red,green,blue;

    int dy;
    int timer;
    boolean exit;

    private int value;
    private int paramName;

    private int color;

    private int py;
    private int ph;

    private int boxY, boxW;

    public ColorSelector(Display display, ColorsList list, String caption, int param) {
        super(display, list, caption);

        this.paramName=param;

        this.color=ColorTheme.getColor(param);

        this.font = FontCache.getFont(false, FontCache.SMALL);
        this.fontH= font.getHeight();

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

        if (param == 49) {
            alpha = midlet.BombusQD.cf.argb_bgnd;
        } else if (param == 50) {
            alpha = midlet.BombusQD.cf.gmenu_bgnd;
        } else if (param == 40) {
            alpha = midlet.BombusQD.cf.popup_bgnd;
        } else if (param == 42) {
            alpha = midlet.BombusQD.cf.popup_bgnd;
        } else if (param == 34) {
            alpha = midlet.BombusQD.cf.cursor_bgnd;
        }

        red=ColorTheme.getRed(color);
        green=ColorTheme.getGreen(color);
        blue=ColorTheme.getBlue(color);

        cpos = 0;

        exit = false;
        (new Thread(this)).start();

        attachDisplay(display);
        this.parentView = list;
    }

    public void paint(Graphics g) {
        super.paint(g);

        g.setFont(font);
        g.setColor(ColorTheme.getColor(ColorTheme.LIST_INK));

        String s = ColorTheme.ColorToString(red, green, blue);
        g.drawString(s, 5, boxY - fontH, Graphics.TOP | Graphics.LEFT);

        g.setStrokeStyle(Graphics.SOLID);

        g.setColor(0x000000);
        g.drawRect(4, boxY - 1, boxW + 1, 81);
        g.setColor(red, green, blue);
        g.fillRect(5, boxY, boxW, 80);
        //g.setColor(0x80000300);

        //draw red
        int pxred = (w * 3 / 7);
        int psred = (ph * red) / 255;
        g.setColor(0);
        //g.setStrokeStyle(Graphics.SOLID);
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
        //g.setStrokeStyle(Graphics.SOLID);
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
        //g.setStrokeStyle(Graphics.SOLID);
        g.fillRect(pxblue, py - ph, w / 10, ph);
        g.setColor(0x3333ff);
        g.fillRect(pxblue, py - psblue, w / 10, psblue);
        if (cpos != 2) {
            g.setColor(0xbbbbff);
        }
        g.fillArc(pxblue, py - ph - h * 7 / 100, w / 10 - 1, h / 10 - 1, 0, 180);
        g.fillArc(pxblue, py - h * 3 / 100, w / 10 - 1, h / 10 - 1, 180, 180);

        if (paramName == 49 || paramName == 50 || paramName == 40 || paramName == 42 || paramName == 34) {
            int pxalpha = (w * 6 / 7);
            int pspxalpha = (ph * alpha) / 255;
            g.setColor(0);
            //g.setStrokeStyle(Graphics.SOLID);
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
        } else if ((paramName == 49 || paramName == 50 || paramName == 40 || paramName == 42 || paramName == 34)
                && (x > (6 * w / 7) && x < (6 * w / 7 + w / 10))) {
            cpos = 3;
        } else {
            return;
        }

        if ((y < py - ph) || (y > py)) {
            if (y < py - ph) {
                dy = 1;
            } else {
                dy = -1;
            }
            movePoint();
            dy = 0;
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
                    alpha = (py - y) * 255 / ph;
                    break;

            }

            repaint();
        }
     }
//#endif

    protected void keyPressed(int key) {
//#ifdef LIGHT_CONTROL
        CustomLight.keyPressed();
//#endif
        switch (key) {
            case KEY_NUM2:
                timer = 7;
                dy = 1;
                movePoint();
                return;
            case KEY_NUM8:
                timer = 7;
                dy = -1;
                movePoint();
                return;
            case KEY_NUM4:
                moveCursorLeft();
                return;
            case KEY_NUM6:
                moveCursorRight();
                return;
            case KEY_NUM0:
                exit = true;
                destroyView();
                break;
            default:
                switch (getGameAction(key)) {
                    case UP:
                        timer = 7;
                        dy = 1;
                        movePoint();
                        return;
                    case DOWN:
                        timer = 7;
                        dy = -1;
                        movePoint();
                        return;
                    case LEFT:
                        moveCursorLeft();
                        return;
                    case RIGHT:
                        moveCursorRight();
                        return;
                }
                repaint();
        }
        super.keyPressed(key);
    }

    private void moveCursorLeft() {
        if (paramName == 49
                || paramName == 50 || paramName == 40 || paramName == 42 || paramName == 34) {
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
        repaint();
    }

    private void moveCursorRight() {
        if (paramName == 49
                || paramName == 50 || paramName == 40 || paramName == 42 || paramName == 34) {
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
        repaint();
    }

    protected void keyReleased(int key) {
            dy = 0;
            super.keyReleased(key);
    }

    public void run() {
        while (! exit) {
            try { Thread.sleep(35); } catch (Exception e) { }
            if (--timer > 0) continue;
            movePoint();
            movePoint();
        }
    }

    public void setValue(int vall) {
        this.value=vall;
        ColorTheme.setColor(paramName, value);
        ColorTheme.saveToStorage();

        ((ColorsList)parentView).setColor(paramName, value);
    }

    private void movePoint() {
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
                alpha=dy+alpha;
                if (alpha>255) alpha=0;
                if (alpha<0) alpha=255;
                break;
        }
        repaint();
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
        if (paramName == 49) {
            midlet.BombusQD.cf.argb_bgnd = alpha;
        } else if (paramName == 50) {
            midlet.BombusQD.cf.gmenu_bgnd = alpha;
        } else if (paramName == 40 || paramName == 42) {
            midlet.BombusQD.cf.popup_bgnd = alpha;
        } else if (paramName == 34) {
            midlet.BombusQD.cf.cursor_bgnd = alpha;
        }
        String val = ColorTheme.ColorToString(red, green, blue);
        int finalColor = ColorTheme.getColorInt(val);
        setValue(finalColor);
        exit = true;
    }
}
//#endif
