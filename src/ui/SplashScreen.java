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

import util.Time;
import client.Config;
import font.FontCache;
import images.RosterIcons;
import java.util.Timer;
import java.util.TimerTask;
import javax.microedition.lcdui.*;
import colors.ColorTheme;
import java.io.IOException;
import midlet.BombusQD;
import ui.controls.Progress;

/**
 *
 * @author Eugene Stahov
 */
public class SplashScreen extends CanvasEx implements CommandListener {
    private String capt;
    private int pos=-1;

    private static Image img;

    private ComplexString status;

    private TimerTaskClock tc;

    private static SplashScreen instance;

    public int keypressed=0;

    private Font clockFont=FontCache.getFont(true,FontCache.LARGE);

    private Progress pb;

    public static SplashScreen getInstance(){
        if (instance==null) {
            instance=new SplashScreen();
        }
        return instance;
    }

    /** Creates a new instance of SplashScreen */
    public SplashScreen() {
        super();

        try {
            if (img==null) {
                img=Image.createImage("/images/splash.png");
                //this.img=img;
            }
        } catch (IOException e) {
//#ifdef DEBUG
//#            System.out.println("splash NOT created ");
//#endif
        }
    }

   public SplashScreen(ComplexString status) {
        super();

        this.status = status;

        status.setElementAt(new Integer(RosterIcons.ICON_KEYBLOCK_INDEX),6);
        redraw();

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

    public void setProgress(int progress) {
        pos=progress;
        redraw();
    }

    public void setFailed(){
        setProgress("Failed", 100);
    }

    public void setProgress(String caption, int progress){
        capt = caption;
	setProgress(progress);
    }

    public int getProgress(){
        return pos;
    }

    // close splash
    private Command cmdExit=new Command("Hide", Command.BACK, 99);

    public void commandAction(Command c, Displayable d) {
        if (c==cmdExit)
            close();
    }

    public void close(){
        if (getParentView() == null) {
            setParentView(midlet.BombusQD.sd.roster);
        }
        super.destroyView();

        instance = null;
        System.gc();
    }

    private class TimerTaskClock extends TimerTask {
        private Timer t;
        public TimerTaskClock(){
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

    protected void keyRepeated(int keyCode) {
        if (keyCode == BombusQD.cf.keyLock) {
            destroyView();
        }
    }

    public void destroyView() {
        if (status != null) {
            status.setElementAt(null,6);
            tc.stop();
        }
//#ifdef AUTOSTATUS
        if (BombusQD.sd.roster.autoAway && Config.autoAwayType == Config.AWAY_LOCK) {
            BombusQD.sd.roster.restoreStatus();
        }
//#endif
        System.gc();
        super.destroyView();
    }

    public void getKeys() {
        int pm=BombusQD.cf.phoneManufacturer;
        if (pm==Config.SIEMENS || pm==Config.SIEMENS2) {
             Config.SOFT_LEFT=-1;
             Config.SOFT_RIGHT=-4;
             Config.KEY_BACK=-12;
             return;
        }
        if (pm==Config.WINDOWS) {
             Config.SOFT_LEFT=40;
             Config.SOFT_RIGHT=41;
             return;
        }
        if (pm==Config.NOKIA || pm==Config.SONYE || pm==Config.SAMSUNG) {
            Config.SOFT_LEFT=-6;
            Config.SOFT_RIGHT=-7;
            return;
        }
        if (pm==Config.MOTOEZX) {
            Config.SOFT_LEFT=-21;
            Config.SOFT_RIGHT=-22;
            return;
        }
        try {
            //Set Motorola specific keycodes
            Class.forName("com.motorola.phonebook.PhoneBookRecord");
            if (getKeyName(-21).toUpperCase().indexOf("SOFT")>=0) {
                Config.SOFT_LEFT=-21;
                Config.SOFT_RIGHT=-22;
            } else {
                Config.SOFT_LEFT=21;
                Config.SOFT_RIGHT=22;
            }
        } catch (ClassNotFoundException ignore2) {
            try {
                if (getKeyName(21).toUpperCase().indexOf("SOFT")>=0) {
                    Config.SOFT_LEFT=21;
                    Config.SOFT_RIGHT=22;
                }
                if (getKeyName(-6).toUpperCase().indexOf("SOFT")>=0) {
                    Config.SOFT_LEFT=-6;
                    Config.SOFT_RIGHT=-7;
                }
            } catch(Exception e) {}

            for (int i=-210;i<127;i++) {//LG-?
            //run thru all the keys
                try {
                   if (getKeyName(i).toUpperCase().indexOf("SOFT")>=0) {// Check for "SOFT" in name description
                      if (getKeyName(i).indexOf("1")>=0) Config.SOFT_LEFT=i;// check for the 1st softkey
                      if (getKeyName(i).indexOf("2")>=0) Config.SOFT_RIGHT=i;// check for 2nd softkey
                   }
                } catch(Exception e){ }
            }
        }
    }
}

