/*
 * SplashScreen.java
 *
 * Created on 16.02.2007, 14:23
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

package ui;

//#ifdef AUTOSTATUS
import client.AutoStatus;
//#endif
import colors.ColorTheme;
import font.FontCache;
import images.RosterIcons;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import ui.controls.Progress;
import util.Time;

/**
 *
 * @author Eugene Stahov
 */
public class SplashScreen extends CanvasEx {
    private String capt;
    private int pos=-1;

    private Image img;

    private ComplexString status;

    private TimerTaskClock tc;

    private Font clockFont=FontCache.getFont(true,FontCache.LARGE);

    private Progress pb;

    public SplashScreen() {
        super();

        try {
            img=Image.createImage("/images/splash.png");
        } catch (IOException e) {
//#ifdef DEBUG
//#            System.out.println("splash NOT created ");
//#endif
        }
    }

   public SplashScreen(ComplexString status) {
        this();

        this.status = status;

        status.setElementAt(new Integer(RosterIcons.ICON_KEYBLOCK_INDEX),6);

        tc=new TimerTaskClock();

        System.gc();
        try { Thread.sleep(50); } catch (InterruptedException ex) { }
    }

   /*
    private Snow snow;
    private int speed = 3;
    public void createSnow(){
        if(snow==null) {
            snow = new Snow(display);
            snow.changeSnowProcess(speed);
        }
    }
    */

    /*long s1;
    long s2;*/

    public void paint(Graphics g) {
        /*
        if(snow != null) {
            //s1 = System.currentTimeMillis();
            snow.paint(g, width, height);
        } else {
         */
           g.setColor(ColorTheme.getColor(ColorTheme.BLK_BGND));
           g.fillRect(0,0, width, height);
        //}

        if(img!=null) g.drawImage(img, width/2, height/2, Graphics.VCENTER|Graphics.HCENTER);

        //todo: fix memory leak in getTimeWeekDay
        if (pos==-1) {
            if (status != null) {
                g.setColor(ColorTheme.getColor(ColorTheme.BLK_INK));
                status.drawItem(null, g, 0, false);
            }

            g.setFont(clockFont);
             String time = Time.getTimeWeekDay();
             g.drawString(time, width/2, height - 5, Graphics.BOTTOM | Graphics.HCENTER);

        } else {
            int filled=pos*width/100;
            if (pb==null) pb=new Progress(0, height, width);
            pb.draw(g, filled, capt);
        }
        /*
        if(snow != null) {
          s2 = System.currentTimeMillis();
          g.drawString("FPS: " + Long.toString( 1000/(s2-s1) ) + " (" + speed + ")", width/2, height - 20, Graphics.BOTTOM | Graphics.HCENTER);
        }
        */
    }

    public void setFailed(){
        setProgress("Failed", 100);
    }

    public void setProgress(String caption, int progress){
        capt = caption;
        pos=progress;
        redraw();
    }

    public int getProgress(){
        return pos;
    }

    protected boolean keyLong(int keyCode) {
        if (keyCode == VirtualCanvas.KEY_STAR) {
            destroyView();
            return true;
        }
        return super.keyLong(keyCode);
    }

    public void destroyView() {
        if (status != null) {
            status.setElementAt(null,6);
            tc.stop();
        }
//#ifdef AUTOSTATUS
        AutoStatus.getInstance().appUnlocked();
//#endif
        img = null;
        super.destroyView();
    }

    private class TimerTaskClock extends TimerTask {
        private Timer t;
        private TimerTaskClock(){
            t=new Timer();
            t.schedule(this, 10, 20000);
        }
        public void run() {
            redraw();
        }
        public void stop(){
            cancel();
            t.cancel();
        }
    }
}

