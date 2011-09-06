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

import javax.microedition.lcdui.Graphics;
import midlet.BombusQD;

public abstract class CanvasEx {
    private Object parentView;

    protected int width;
    protected int height;

    public CanvasEx() {
        this.width = BombusQD.sd.canvas.getWidth();
        this.height = BombusQD.sd.canvas.getHeight();
    }

    public void show() {
        if (parentView == null) {
            parentView = BombusQD.getCurrentView();
        }
        BombusQD.sd.canvas.show(this);
    }

    public void destroyView() {
        if (parentView != null) {
            BombusQD.setCurrentView(parentView);
        }
    }

    public final Object getParentView() {
        return parentView;
    }

    public final void setParentView(Object d) {
        parentView = d;
    }

    public final boolean isShown() {
        return BombusQD.sd.canvas.getCanvas() == this;
    }

    public final void redraw() {
        if (!VirtualCanvas.isPainting || BombusQD.sd.canvas.getCanvas()!=this)
            BombusQD.sd.canvas.repaint();
    }

    public final void setTitle(String title) {
        BombusQD.sd.canvas.setTitle(title);
    }

    public final int getHeight() {
        return height;
    }

    public final int getWidth() {
        return width;
    }

    protected abstract void paint(Graphics g);

    protected void keyPressed(int code) {}
    protected boolean keyLong(int keyCode){return false;}
    

    protected void pointerPressed(int x, int y) {}
    protected void pointerDragged(int x, int y) {}
    protected void pointerReleased(int x, int y) {}

    protected void showNotify() {}

    protected void sizeChanged(int w, int h) {}
}
