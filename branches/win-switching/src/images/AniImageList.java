/*******************************************************************************
 Jimm - Mobile Messaging - J2ME ICQ clone
 Copyright (C) 2003-05  Jimm Project

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 ********************************************************************************
 File: src/DrawControls/AniImageList.java
 Version: ###VERSION###  Date: ###DATE###
 Author(s): Vladimir Kryukov
 *******************************************************************************/
/*
 * GifImageList.java
 *
 * Created on 4 Апрель 2008 г., 18:55
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

//#ifdef SMILES
package images;

import java.io.IOException;
import java.io.InputStream;
import javax.microedition.lcdui.*;

/**
 *
 * @author vladimir
 */
public class AniImageList extends ImageList implements Runnable {

    private AniIcon[] icons;
    private Thread thread;

    //! Return image by index
    public AniIcon iconAt(int index) { //!< Index of requested image in the list
        if (index < size() && index >= 0) {
            return icons[index];
        }
        return null;
    }
    private int size() {
        return icons != null ? icons.length : 0;
    }

    public AniImageList() { }

    public void drawImage(Graphics g, int index, int x, int y) {
       if (0 <= index && index < icons.length) icons[index].drawImage(g, x, y);
    }

    private String getAnimationFile(String resName, int i) {
        return resName + "/" + (i + 1) + ".png";
    }

    public boolean load(String resName) {
        try {
            InputStream is = getClass().getResourceAsStream(resName + "/animate.bin");
            if (is == null) {
                return false;
            }
            int smileCount = is.read();
            icons = new AniIcon[smileCount];
            aniWidth = new int[smileCount + 1];
            aniHeight = new int[smileCount + 1];
            for (int smileNum = 0; smileNum < smileCount; smileNum++) {
                int imageCount = is.read();
                int frameCount = is.read();
                AniIcon icon = new AniIcon(getAnimationFile(resName, smileNum), imageCount, frameCount);
                boolean loaded = (0 < icon.getWidth());
                if (!loaded) {
                    width = height = 0;
                    return false;
                }
                for (int frameNum = 0; frameNum < frameCount; frameNum++) {
                    int iconIndex = is.read();
                    int delay = is.read() * WAIT_TIME;
                    icon.addFrame(frameNum, iconIndex, delay);
                }
                icons[smileNum] = icon;
                width = Math.max(width, icon.getWidth());
                height = Math.max(height, icon.getHeight());
                aniWidth[smileNum] = icon.getWidth();
                aniHeight[smileNum] = icon.getHeight();
            }
        }
        catch(OutOfMemoryError eom) {
            System.out.println("Load Ani OutofMem");
            return false;
        }
        catch (IOException e) {
            System.out.println("Error Loading animated.bin");
            return false;
        }
        if (size() > 0) {
            thread = new Thread(this);
            thread.start();
        }
       return true;
    }

    private static final int WAIT_TIME = 100;
    public void run() {
        long time = System.currentTimeMillis();
        while (true) {
            try {
                Thread.sleep(WAIT_TIME);
            } catch (Exception e) {
            }
            long newTime = System.currentTimeMillis();
            boolean animationWorked = true;
            if (animationWorked) {
                boolean update = false;
                int size = size();
                for (int i = 0; i < size; i++) {
                    if (null != icons[i]) {
                        update |= icons[i].nextFrame(newTime - time);
                    }
                }
                if (update) {
                   Displayable displayable = midlet.BombusQD.getInstance().display.getCurrent();
                   if (displayable instanceof Canvas) ((Canvas)displayable).repaint();
                }
            }
            time = newTime;
        }
    }
}
//#endif
