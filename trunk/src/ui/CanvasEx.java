/*
 * CanvasEx.java
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

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import midlet.BombusQD;

public abstract class CanvasEx extends Canvas {
    private Displayable parentView;

    protected int width;
    protected int height;

    public CanvasEx() {
        this.width = BombusQD.sd.canvas.getWidth();
        this.height = BombusQD.sd.canvas.getHeight();
    }

    public void show() {
        if (parentView == null) {
            if (BombusQD.sd.canvas.isShown()) {
                parentView = BombusQD.sd.canvas.getCanvas();
            } else {
                parentView = BombusQD.getCurrentView();
            }            
        }        
        BombusQD.sd.canvas.show(this);

        //System.out.println("show " + this + " [" + parentView + "]");
    }

    public void destroyView() {
        if (parentView != null) {
            if (parentView instanceof CanvasEx) {
                BombusQD.sd.canvas.show((CanvasEx)parentView);
            } else {
                BombusQD.setCurrentView(parentView);
            }
            parentView = null;

            //System.out.println("destroy " + this);
        }
    }

    public final Displayable getParentView() {
        return parentView;
    }

    public final void setParentView(Displayable d) {
        parentView = d;
    }

    public boolean isShown() {
        return BombusQD.sd.canvas.getCanvas() == this;
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

    protected void showNotify() {};

    protected void sizeChanged(int w, int h) {};
}
