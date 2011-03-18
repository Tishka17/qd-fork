/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import client.Config;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Displayable;
import midlet.BombusQD;

/**
 *
 * @author esprit
 */

public abstract class CanvasEx extends Canvas {
    protected Displayable parentView;

    public CanvasEx() {
        setFullScreenMode(Config.fullscreen);
    }

    public void show() {
        if (parentView == null) {
            parentView = BombusQD.getCurrentView();
        }        
        BombusQD.setCurrentView(this);
    }

    public void destroyView() {
        if (parentView != null) {
            BombusQD.setCurrentView(parentView);
        }
    }

    public final Displayable getParentView() {
        return parentView;
    }

    public final void setParentView(Displayable d) {
        parentView = d;
    }
}
