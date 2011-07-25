/*
 * CameraImage.java
 *
 * Created on 25.10.2006, 22:35
 *
 * Copyright (c) 2005-2008, Eugene Stahov (evgs), http://bombus-im.org
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
package images.camera;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.media.control.GUIControl;
import javax.microedition.media.control.VideoControl;
import locale.SR;
import midlet.BombusQD;
import ui.CanvasEx;
import ui.controls.AlertBox;

/**
 *
 * @author Evg_S
 */
public class CameraImage implements CommandListener {
    private Command cmdShot;
    private Command cmdCancel;

    private CanvasEx parentView;

    private Player player;
    private VideoControl videoControl;

    CameraImageListener imgListener;

    public CameraImage(CameraImageListener imgListener) {
        cmdShot = new Command(SR.get(SR.MS_CAMERASHOT), Command.OK, 1);
        cmdCancel = new Command(SR.get(SR.MS_CANCEL), Command.BACK, 99);

        parentView = BombusQD.sd.canvas.getCanvas();
        this.imgListener = imgListener;

        try {
            String uri = "capture://video";

            String device = System.getProperty("microedition.platform").toLowerCase();
            int ind = device.indexOf('.');
            if (ind != -1) ind = device.indexOf(".", ind +1);
            if( (device.indexOf("nokia") != -1) && (ind == -1))
            uri = "capture://image";

            player = Manager.createPlayer(uri);
            player.realize();

            videoControl = (VideoControl)player.getControl("VideoControl");

            Form form = new Form("Camera");
            Item item = (Item)videoControl.initDisplayMode(
                    GUIControl.USE_GUI_PRIMITIVE, null);
            form.append(item);
            form.addCommand(cmdShot);
            form.addCommand(cmdCancel);
            form.setCommandListener(this);

            BombusQD.setCurrentView(form);

            player.start();
        } catch (Exception e) {
            AlertBox box = new AlertBox("Error", e.toString(), AlertBox.BUTTONS_OK);
            box.setParentView(parentView);
            box.show();
        }
    }

    public void commandAction(Command command, Displayable displayable) {
        if (command == cmdShot) {
            try {
                byte photo[] = videoControl.getSnapshot(null);
                imgListener.cameraImageNotify(photo);
                photo=null;
            } catch (Exception e) {
//#ifdef DEBUG
//#                 e.printStackTrace();
//#endif
            }
        }

        player.close();
        BombusQD.sd.canvas.show(parentView);
    }
}
