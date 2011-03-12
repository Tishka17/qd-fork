/*
 * BombusQD.java
 *
 * Created on 5.01.2005, 21:46
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

/**
 *
 * @author Eugene Stahov
 */
package midlet;
//#ifdef AUTOTASK
import autotask.AutoTask;
//#endif
import account.Account;
import colors.ColorTheme;
//#ifdef STATS
import stats.Stats;
//#endif
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import locale.*;
import client.Config;
import client.StaticData;
import client.Roster;
import ui.SplashScreen;
import font.*;
//#ifdef DEBUG_CONSOLE
//# import console.debug.DebugList;
//#endif
import client.Contact;
//#ifdef LIGHT_CONTROL
import light.*;
//#endif
import history.*;
//#ifdef CLIENTS_ICONS
import images.ClientsIconsData;
//#endif

import ui.controls.AlertBox;
import io.NvStorage;
import java.io.DataInputStream;
import java.io.DataOutputStream;

/** Entry point class
 *
 * @author  Eugene Stahov
 * @version
 */

public class BombusQD extends MIDlet implements Runnable {

    public Display display;//  = Display.getDisplay(this);
    private boolean isRunning;

    public static StaticData sd;// = StaticData.getInstance();
    public static Config cf;// cf = Config.getInstance();

//#ifdef DEBUG_CONSOLE
//#     public final static DebugList debug = DebugList.get();
//#endif

    public int width = 0;
    public int height = 0;
    public int count = 0;

    public SplashScreen s;
    private static BombusQD instance;

//#ifdef LIGHT_CONTROL
   LightConfig lcf;
//#endif

    public void startApp() {
        if (isRunning) {
	        hideApp(false, null);
            return;
        }
        isRunning = true;
        new Thread(this).start();
    }

    /**
     * Pause is a no-op since there are no background activities or
     * record stores that need to be closed.
     */
    public void pauseApp() {
    }

    public Image[] imageArr = null;
    public int wimg_menu;
    public int himg_menu;
    public int wimg_actions;
    public int himg_actions;

    private void rmsVersion(boolean save, Displayable parentView) {
        String key = "key15628618";
        if(save == false) {
            try {
               DataInputStream is=NvStorage.ReadFileRecord("appver", 0);
               String ver = is.readUTF();
               if(ver.indexOf(key) == -1 ) {
                  //alerbox
                  AlertBox alert = new AlertBox( "WARNING", SR.get(SR.MS_WARNING_MESSAGE_INSTALL) , display, parentView, true) {
                      public void yes() { notifyDestroyed(); }
                      public void no() {}
                  };
               }
               is.close();
               is=null;
            } catch (Exception e) {
               /*
                  AlertBox alert = new AlertBox( "Info", "..." , display, parentView, true) {
                      public void yes() { }
                      public void no() { }
                   };
               */
               rmsVersion(true, null);
            }
            return;
        }
        DataOutputStream os=NvStorage.CreateDataOutputStream();
        try {
            os.writeUTF(key);
            os.close();
            os = null;
        } catch (Exception e) { }
        NvStorage.writeFileRecord(os, "appver", 0, true);
    }

    public void run(){
        instance = this;
        display = Display.getDisplay(this);
        
        sd = StaticData.getInstance();
        cf = Config.getInstance();

        SR.changeLocale();

        ColorTheme.initColors();
        Commands.initCommands();
//#ifdef CLIENTS_ICONS
        ClientsIconsData.initClients();
//#endif

        s = SplashScreen.getInstance(display);
        s.setProgress("Loading", 3);

        if (sd.roster == null) {
            sd.roster = new Roster(display);
        }

        s.getKeys();
        width = s.width;
        height = s.height;

        boolean selAccount=((cf.accountIndex<0));
        if (!selAccount && cf.autoLogin) {
            Account.loadAccount(cf.autoLogin, cf.accountIndex, -1);
        }

        display.setCurrent(sd.roster);
        rmsVersion(false, sd.roster);

//#ifdef DEBUG_CONSOLE
//#         if(cf.debug){
//#             debug.add("::startmem free/total "+
//#                     Long.toString(Runtime.getRuntime().freeMemory()>>10) + "/" +
//#                     Long.toString(Runtime.getRuntime().totalMemory()>>10), 10) ;
//#         }
//#endif

        try {
            imageArr = new Image[2];
            imageArr[0] = Image.createImage("/images/menu.png");
            wimg_menu = imageArr[0].getWidth() / 8;
            himg_menu = imageArr[0].getHeight() / 10;
        } catch (Exception e) {
        }

//#ifdef HISTORY
        HistoryConfig.getInstance().loadFromStorage();
//#endif

//#ifdef LIGHT_CONTROL
        lcf=LightConfig.getInstance();
        CustomLight.switchOn(lcf.light_control);
//#endif

//#ifdef HISTORY
        HistoryStorage hs = new HistoryStorage();
//#endif
//#ifdef PEP
        Activity.loaded();
//#endif
//#ifdef STATS
        Stats.getInstance().loadFromStorage();
        Stats.getInstance().updateRunValue();
//#endif
//#ifdef AUTOTASK
        sd.autoTask=new AutoTask(display);
//#endif
    }

    public void destroyApp(boolean unconditional) {

    }

    public void hideApp(boolean hide, Contact c) {
        if (hide) {
            cf.isMinimized = true;
            display.setCurrent(null);
        } else {
            cf.isMinimized = false;
            if (c != null) {
                display.setCurrent(c.getMessageList());
            } else {
                display.setCurrent(sd.roster);
            }
        }
    }

    public static BombusQD getInstance() {
        return instance;
    }

    public final String getStrProperty(final String key, final String def) {
        String str = getAppProperty(key);
        return (str == null) ? def : str;
    }

    public final int getIntProperty(final String key, final int def) {
        try {
            String str = getAppProperty(key);
            return (str == null) ? def : Integer.parseInt(str);
        } catch (NumberFormatException e) {
        }
        return def;
    }
}
