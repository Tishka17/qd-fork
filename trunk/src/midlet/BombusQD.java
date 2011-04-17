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
import account.AccountSelect;
import colors.ColorTheme;
//#ifdef STATS
import stats.Stats;
//#endif
import javax.microedition.midlet.MIDlet;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
//#ifdef PEP
import locale.Activity;
//#endif
import locale.SR;
import client.Config;
import client.StaticData;
import client.Roster;
import ui.SplashScreen;
//#ifdef DEBUG_CONSOLE
//# import console.debug.DebugList;
//#endif
import client.Contact;
//#ifdef LIGHT_CONTROL
import light.CustomLight;
//#endif
//#ifdef CLIENTS_ICONS
import images.ClientsIconsData;
//#endif

import ui.controls.AlertBox;
import io.NvStorage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import ui.VirtualCanvas;

/** Entry point class
 *
 * @author  Eugene Stahov
 * @version
 */

public class BombusQD extends MIDlet implements Runnable {
    public static Display display;
    private boolean isRunning = false;

    public static StaticData sd;
    public static Config cf;

//#ifdef DEBUG_CONSOLE
//#     public final static DebugList debug = DebugList.get();
//#endif

    public SplashScreen s;
    private static BombusQD instance;

    private static boolean minimized = false;

    public void startApp() {
        if (isRunning) {
            showApp();
        } else {
            isRunning = true;

            instance = this;
            display = Display.getDisplay(this);

            sd = StaticData.getInstance();
            cf = Config.getInstance();

            ColorTheme.initColors();
            ColorTheme.loadFromStorage();

            sd.canvas = new VirtualCanvas();

            s = SplashScreen.getInstance();
            s.show();

            s.setProgress("Loading", 3);

            new Thread(this).start();
        }
    }

    public void pauseApp() {
    }

    private void rmsVersion(boolean save, Displayable parentView) {
        String key = "key15628618";
        if(save == false) {
            try {
               DataInputStream is=NvStorage.ReadFileRecord("appver", 0);
               String ver = is.readUTF();
               if(ver.indexOf(key) == -1 ) {
                  //alerbox
                  AlertBox box = new AlertBox( "WARNING", SR.get(SR.MS_WARNING_MESSAGE_INSTALL), true) {
                      public void yes() { notifyDestroyed(); }
                      public void no() {}
                  };
                  box.show();
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

    public void run() {
        SR.changeLocale();

        Commands.initCommands();
//#ifdef CLIENTS_ICONS
        ClientsIconsData.initClients();
//#endif

        if (sd.roster == null) {
            sd.roster = new Roster();
        }

        s.getKeys();

        boolean selAccount=((cf.accountIndex<0));
        if (!selAccount && cf.autoLogin) {
            Account.loadAccount(cf.autoLogin, cf.accountIndex, -1);
	    sd.roster.show();
	} else {
            AccountSelect select = new AccountSelect(false, 0);
            select.setParentView(sd.roster);
	    select.show();
	}

        rmsVersion(false, sd.roster);

//#ifdef DEBUG_CONSOLE
//#         if(cf.debug){
//#             debug.add("::startmem free/total "+
//#                     Long.toString(Runtime.getRuntime().freeMemory()>>10) + "/" +
//#                     Long.toString(Runtime.getRuntime().totalMemory()>>10), 10) ;
//#         }
//#endif

//#ifdef LIGHT_CONTROL
        CustomLight.switchOn(Config.lightControl);
//#endif

//#ifdef PEP
        Activity.loaded();
//#endif
//#ifdef STATS
        Stats.getInstance().loadFromStorage();
        Stats.getInstance().updateRunValue();
//#endif
//#ifdef AUTOTASK
        sd.autoTask=new AutoTask();
//#endif
    }

    public void destroyApp(boolean unconditional) {

    }

    public static void hideApp() {
        minimized = true;
        display.setCurrent(null);
    }

    public static void showApp() {
        minimized = false;
        display.setCurrent(sd.canvas);
    }

    public static boolean isMinimized() {
        return minimized;
    }

    public static BombusQD getInstance() {
        return instance;
    }

    public static String getStrProperty(final String key, final String def) {
        String str = instance.getAppProperty(key);
        return (str == null) ? def : str;
    }

    public static int getIntProperty(final String key, final int def) {
        try {
            String str = instance.getAppProperty(key);
            return (str == null) ? def : Integer.parseInt(str);
        } catch (NumberFormatException e) {
        }
        return def;
    }

    public static Displayable getCurrentView() {
        return display.getCurrent();
    }

    public static void setCurrentView(Displayable d) {
        display.setCurrent(d);
    }
}
