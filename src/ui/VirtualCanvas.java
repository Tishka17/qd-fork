/*
 * VirtualCanvas.java
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

//#ifdef AUTOSTATUS
import client.AutoStatus;
//#endif
import client.Config;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
//#ifdef LIGHT_CONTROL
import light.CustomLight;
//#endif
import midlet.BombusQD;
//#ifdef USER_KEYS
import ui.keys.UserKeyExec;
//#endif

public class VirtualCanvas extends Canvas {
    private CanvasEx canvas;

    private Image offscreen;
    public static boolean isPainting;

    public VirtualCanvas() {
        super();

//#ifdef TOUCH
        Config.isTouchPhone = hasPointerEvents();
//#endif

        setFullScreenMode(Config.fullscreen);

        if(!isDoubleBuffered()) {
            offscreen = Image.createImage(getWidth(), getHeight());
        }
    }

    public void show(CanvasEx canvas) {
        this.canvas = canvas;
        if (isShown()) {
            this.canvas.showNotify();
            repaint();
        } else {
            BombusQD.setCurrentView(this);
        }
    }

    public void showNotify() {
        setFullScreenMode(Config.fullscreen);
        
        canvas.showNotify();
    }

    protected void sizeChanged(int w, int h) {
        canvas.sizeChanged(w, h);

        canvas.width = w;
        canvas.height = h;

        if(!isDoubleBuffered()) {
            offscreen = Image.createImage(w, h);
        }
    }
    
    public int getAvailWidth() {
        return getWidth() - Config.getInstance().scrollWidth - 2;
    }

    public CanvasEx getCanvas() {
        return canvas;
    }

    protected void paint(Graphics g) {
        isPainting = true;
        // from bm2
        canvas.width = getWidth();
        canvas.height = getHeight();
        if(!isDoubleBuffered()) {
            Graphics graphics = offscreen.getGraphics();
            canvas.paint(graphics);
            g.drawImage(offscreen, 0, 0, Graphics.TOP | Graphics.LEFT);
        } else {
            canvas.paint(g);
        }
        isPainting = false;
    }

    
    static long star_pressed_time = 0;
    protected void keyPressed(int code) {
//#ifdef USER_KEYS
        long time = System.currentTimeMillis();
        if (code==KEY_STAR) {
            star_pressed_time = time;
        } else if (star_pressed_time!=0 && time-star_pressed_time<2000) {
            star_pressed_time = 0;
            if (UserKeyExec.getInstance().commandExecute(code))
                return;
        }
//#endif
//#ifdef LIGHT_CONTROL
        CustomLight.keyPressed();
//#endif
//#ifdef AUTOSTATUS
        AutoStatus.getInstance().userActivity(Config.AWAY_IDLE);
//#endif
        canvas.keyPressed(code);
    }

    protected void keyRepeated(int code) {
        canvas.keyRepeated(code);
    }

    protected void keyReleased(int code) {
        canvas.keyReleased(code);
    }

    protected void pointerPressed(int x, int y) {
//#ifdef LIGHT_CONTROL
        CustomLight.keyPressed();
//#endif
//#ifdef AUTOSTATUS
        AutoStatus.getInstance().userActivity(Config.AWAY_IDLE);
//#endif
        canvas.pointerPressed(x, y);
    }

    protected void pointerDragged(int x, int y) {
        canvas.pointerDragged(x, y);
    }

    protected void pointerReleased(int x, int y) {
        canvas.pointerReleased(x, y);
    }
}
