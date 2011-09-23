/*
 * ShowFile.java
 *
 * Created on 9.10.2006, 14:00
 *
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
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

//#ifdef FILE_IO
package io.file.browse;

import io.file.FileIO;
//#ifndef NOMMEDIA
import java.io.IOException;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
//#endif
import javax.microedition.lcdui.Image;
import ui.controls.form.DefForm;
import ui.controls.form.ImageItem;
import ui.controls.form.MultiLine;

/**
 *
 * @author User
 */

public class ShowFile extends DefForm {   
    private int len;
    private byte[] b;
//#ifndef NOMMEDIA
    private Player pl;
//#endif
    public ShowFile(final String fileName, int type) {
        super(fileName);

        if (loadFile(fileName)) {
            switch (type) {
                case Browser.TEXT_FILE:
                    showText();
                    break;
                case Browser.IMAGE_FILE:
                    showImage();
                    break;
//#ifndef NOMMEDIA
                case Browser.SOUND_FILE:
                    playFile(fileName);
                    break;
//#endif
            }
        }
    }

    private boolean loadFile(String fileName) {
        FileIO f = FileIO.createConnection(fileName);
        if (f == null) {
            return false;
        }        
        try {            
            b = f.fileRead();
            len = b.length;
            f.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private void showImage() {
        Image photoImg = null;
        try {
            photoImg = Image.createImage(b, 0, len);
        } catch (OutOfMemoryError eom) {
        } catch (Exception e) {}

        if (null == photoImg) {
            return;
        }
        addControl(new ImageItem(photoImg));
    }

    private void showText() {
        if (len > 0) {
            addControl(new MultiLine(null, new String(b, 0, len)));
        }        
    }

//#ifndef NOMMEDIA
    private void playFile(String file) {
        try {
            pl = Manager.createPlayer("file://" + file);
            pl.realize();
            pl.start();
        } catch (IOException ex) {
//#ifdef DEBUG
//#             ex.printStackTrace();
//#endif
        } catch (MediaException ex) {
//#ifdef DEBUG
//#             ex.printStackTrace();
//#endif
        }

        addControl("Playing...");
    }

    public void destroyView() {
        if (pl != null) {
            try {
                pl.stop();
            } catch (MediaException e) {}
            pl.close();
        }
        super.destroyView();
    }
//#endif
} 
//#endif
