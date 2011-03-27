/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import client.Config;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import midlet.BombusQD;

/**
 *
 * @author esprit
 */

public abstract class CanvasEx extends Canvas {
    private Displayable parentView;

    public CanvasEx() {
        setFullScreenMode(Config.fullscreen);
    }

    public void show() {
        if (parentView == null) {
            parentView = BombusQD.sd.canvas.getCanvas();
        }        
        BombusQD.sd.canvas.show(this);

        System.out.println("show " + this + " [" + parentView + "]");
    }

    public void destroyView() {
        if (parentView != null) {
            if (parentView instanceof CanvasEx) {
                BombusQD.sd.canvas.show((CanvasEx)parentView);
            } else {
                BombusQD.setCurrentView(parentView);
            }
            parentView = null;

            System.out.println("destroy " + this);
        }
    }

    public final Displayable getParentView() {
        return parentView;
    }

    public final void setParentView(Displayable d) {
        parentView = d;
    }

    public void redraw() {
        BombusQD.sd.canvas.repaint();
    }

    protected abstract void paint(Graphics g);

    protected void keyPressed(int code) {};
    protected void keyRepeated(int code) {};
    protected void keyReleased(int code) {};

    protected void pointerPressed(int x, int y) {};
    protected void pointerDragged(int x, int y) {};
    protected void pointerReleased(int x, int y) {};

    protected void sizeChanged(int w, int h) {};
}
