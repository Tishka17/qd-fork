/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import client.Config;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import midlet.BombusQD;

/**
 *
 * @author esprit
 */

public class VirtualCanvas extends Canvas {
    private CanvasEx canvas;

    private Image offscreen;

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
            repaint();
        } else {
            BombusQD.setCurrentView(this);
        }
    }

    public final void setFullScreenMode(boolean flag) {
        super.setFullScreenMode(flag);

        if(!isDoubleBuffered()) {
            offscreen = Image.createImage(getWidth(), getHeight());
        }
    }

    protected void sizeChanged(int w, int h) {
        canvas.sizeChanged(w, h);

        if(!isDoubleBuffered()) {
            offscreen = Image.createImage(w, h);
        }
    }

    public CanvasEx getCanvas() {
        return canvas;
    }

    protected void paint(Graphics g) {
        if(!isDoubleBuffered()) {
            Graphics graphics = offscreen.getGraphics();
            canvas.paint(graphics);
            g.drawImage(offscreen, 0, 0, Graphics.TOP | Graphics.LEFT);
        } else {
            canvas.paint(g);
        }
    }

    protected void keyPressed(int code) {
        canvas.keyPressed(code);
    }

    protected void keyRepeated(int code) {
        canvas.keyRepeated(code);
    }

    protected void keyReleased(int code) {
        canvas.keyReleased(code);
    }

    protected void pointerPressed(int x, int y) {
        canvas.pointerPressed(x, y);
    }

    protected void pointerDragged(int x, int y) {
        canvas.pointerDragged(x, y);
    }

    protected void pointerReleased(int x, int y) {
        canvas.pointerReleased(x, y);
    }
}
