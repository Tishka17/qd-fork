/*
 * GetFileServer.java
 *
 * Created on 17.07.2007, 0:57
 *
 * Copyright (c) 2005-2008, Eugene Stahov (evgs), http://bombus-im.org
 * Copyright (c) 2009, Alexej Kotov (aqent), http://bombusmod-qd.wen.ru
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
package info;

import images.RosterIcons;
import java.io.InputStream;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.CommandListener;
//# import javax.microedition.lcdui.Command;
//#else
import menu.Command;
//#endif
import locale.SR;
import ui.MainBar;
import disco.DiscoSearchForm;
import javax.microedition.io.ConnectionNotFoundException;
import menu.MenuListener;
import midlet.BombusQD;
import ui.controls.form.DefForm;
//#ifdef GRAPHICS_MENU
import ui.GMenu;
import ui.GMenuConfig;
import ui.controls.form.LinkString;
import ui.controls.form.MultiLine;
import ui.controls.form.SpacerItem;
//#endif

/**
 *
 * @author evgs
 */

public class GetFileServer extends DefForm implements MenuListener, Runnable {
    private final static String NEWS_URL = "http://qd-fork.googlecode.com/svn/trunk/changelog.txt";
    
    private static final String ICQ_PREFIX = "#";
    private static final String MRIM_PREFIX = "@";
    private static final String IRC_PREFIX = "&";
    private static final String J2J_PREFIX = "$";
    private static final String VK_PREFIX = "%";

    private Command cmdICQ = new Command("ICQ Transports", 0x04);
    private Command cmdMrim = new Command("Mrim Transports", 0x04);
    private Command cmdIrc = new Command("IRC Transports", 0x04);
    private Command cmdVk = new Command("VK Transports", 0x04);
    private Command cmdJ2J = new Command("J2J Transports", 0x04);

    private Vector icq = new Vector();
    private Vector mrim = new Vector();
    private Vector irc = new Vector();
    private Vector vk = new Vector();
    private Vector j2j = new Vector();
    private boolean wait = true;
    private boolean error = false;

    public GetFileServer() {
        super("Update");

        MainBar bar = new MainBar(SR.get(SR.MS_CHECK_UPDATE));
        setMainBarItem(bar);
        bar.addElement(null);
        bar.addRAlign();
        bar.addElement(null);
    }

    public void show() {
        super.show();
        new Thread(this).start();
    }

    public void run() {
        wait = true;
        updateCaption();

        HttpConnection c;
        InputStream is;

        try {
            c = (HttpConnection)Connector.open(NEWS_URL);
            is = c.openInputStream();
            Vector versions[] = new util.StringLoader().stringLoader(is, 1);
            
            addControl(new MultiLine("Current version", Version.getVersionString(false)));
            addControl(new MultiLine("Last version", (String)versions[0].elementAt(0)));

            int size = versions[0].size();
            for (int i = 1; i < size; i++) {
                String name = (String)versions[0].elementAt(i);
                if (name != null) {
                    if (name.startsWith("http://")) {
                        addControl(new LinkString(name) {
                            public void doAction() {
                                try {
                                    BombusQD.getInstance().platformRequest(text);
                                } catch (ConnectionNotFoundException ex) {
                                    //ex.printStackTrace();
                                }
                            }
                        });
                    } else if (name.startsWith(ICQ_PREFIX)) {
                        icq.addElement(name.substring(1));
                    } else if (name.startsWith(MRIM_PREFIX)) {
                        mrim.addElement(name.substring(1));
                    } else if (name.startsWith(IRC_PREFIX)) {
                        irc.addElement(name.substring(1));
                    } else if (name.startsWith(VK_PREFIX)) {
                        vk.addElement(name.substring(1));
                    } else if (name.startsWith(J2J_PREFIX)) {
                        j2j.addElement(name.substring(1));
                    } else {
                        addControl(new MultiLine(null, name));
                    }
                } else {
                    addControl(new SpacerItem(5));
                } 
            }
            if (is != null) {
                is.close();
            }
            is = null;
            if (c != null) {
                c.close();
            }
            c = null;
        } catch (Exception e) {
            addControl(new MultiLine("Error", "Can't get news!"));
        }
        wait = false;
        updateCaption();
        redraw();
    }

    public void commandAction(Command c) {
        if (c == cmdICQ) {
            new DiscoSearchForm(icq, 0).show();
        } else if (c == cmdMrim) {
            new DiscoSearchForm(mrim, 1).show();
        } else if (c == cmdIrc) {
            new DiscoSearchForm(irc, 2).show();
        } else if (c == cmdVk) {
            new DiscoSearchForm(vk, 4).show();
        } else if (c == cmdJ2J) {
            new DiscoSearchForm(j2j, 3).show();
        }
    }

    protected void updateCaption() {
        StringBuffer str = new StringBuffer();
        Object pic = null;
        if (wait) {
            str.append(" loading.. ");
            pic = new Integer(RosterIcons.ICON_PROGRESS_INDEX);
        } else if (error) {
            pic = new Integer(RosterIcons.ICON_PRIVACY_BLOCK);
        } else {
            pic = new Integer(RosterIcons.ICON_PRIVACY_ALLOW);
        }
        getMainBarItem().setElementAt(str.toString(), 1);
        getMainBarItem().setElementAt(pic, 3);
    }

    public void commandState() {
//#ifdef MENU_LISTENER
        menuCommands.removeAllElements();
        cmdfirstList.removeAllElements();
        cmdsecondList.removeAllElements();
        cmdThirdList.removeAllElements();
//#endif
        addCommand(cmdICQ);
        addCommand(cmdMrim);
        addCommand(cmdIrc);
        addCommand(cmdVk);
        addCommand(cmdJ2J);
    }

//#ifdef MENU_LISTENER
    public String touchLeftCommand() {
        return SR.get(SR.MS_MENU);
    }

//#ifdef GRAPHICS_MENU
    public void touchLeftPressed() {
        showGraphicsMenu();
    }

    public int showGraphicsMenu() {
        commandState();
        menuItem = new GMenu(this, menuCommands);
        GMenuConfig.getInstance().itemGrMenu = 123;
        redraw();
        return 123;
    }

//#else
//#     public void touchLeftPressed(){
//#         showMenu();
//#     }
//#
//#     public void showMenu() {
//#         commandState();
//#         new MyMenu(display, parentView, this, SR.get(SR.MS_HISTORY_OPTIONS), null, menuCommands);
//#    }
//#endif
//#endif
}
