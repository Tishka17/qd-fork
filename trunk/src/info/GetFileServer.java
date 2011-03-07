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

import client.Msg;
import client.Constants;
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
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
import ui.MainBar;
import client.DiscoSearchForm;
import ui.controls.form.DefForm;
//#ifdef GRAPHICS_MENU
//# import ui.GMenu;
//# import ui.GMenuConfig;
//# import ui.controls.form.MultiLine;
//# import ui.controls.form.CollapsibleItem;
//#endif

/**
 *
 * @author evgs
 */

public class GetFileServer extends DefForm implements Runnable {
//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_VERSION_UPGRADE");
//#endif

    private final static String update_url = "http://bombusmod-qd.wen.ru/midp/update.txt";

    private Command cmdICQ = new Command("QD: ICQ Transports list", Command.SCREEN, 3);
    private Command cmdMrim = new Command("QD: Mrim Transports list", Command.SCREEN, 4);
    private Command cmdIrc = new Command("QD: IRC Transports list", Command.SCREEN, 5);
    private Command cmdVk = new Command("QD: j2j Transports list", Command.SCREEN, 6);

    private Vector icq = new Vector();
    private Vector mrim = new Vector();
    private Vector irc = new Vector();
    private Vector vk = new Vector();
    private Vector news;

    private boolean wait = true;
    private boolean error = false;

    public GetFileServer(Display display, Displayable pView) {
        super(display, pView, "Update");

        news = new Vector();

        try {
            focusedItem(0);
        } catch (Exception e) {
        }

        MainBar mainbar = new MainBar(SR.get(SR.MS_CHECK_UPDATE));
        setMainBarItem(mainbar);
        mainbar.addElement(null);
        mainbar.addRAlign();
        mainbar.addElement(null);
        commandState();

        attachDisplay(display);
        this.parentView = pView;

        new Thread(this).start();
    }

    public void run() {
        wait = true;
        rePaint();

        HttpConnection c;
        InputStream is;

        try {
            c = (HttpConnection)Connector.open(update_url);
            is = c.openInputStream();
            Vector versions[] = new util.StringLoader().stringLoader(is, 1);
            int size = versions[0].size();
            for (int i = 0; i < size; i++) {
                if (versions[0].elementAt(i) == null) {
                    continue;
                }
                String name = (String)versions[0].elementAt(i);
                if (i == 0) {
                    CollapsibleItem item = new CollapsibleItem(name + Version.getNameVersion(), true);
                    addControl(item);
                } else if (i < 2) {
                    CollapsibleItem item = new CollapsibleItem(name, true);
                    addControl(item);
                } else {
                    if (name.startsWith("*")) {
                        MultiLine line = new MultiLine(null, name, super.superWidth);
                        line.setSelectable(true);

                        addControl(line);
                    } else if (name.startsWith("#")) {
                        icq.addElement(name.substring(1, name.length()));
                    } else if (name.startsWith("@")) {
                        mrim.addElement(name.substring(1, name.length()));
                    } else if (name.startsWith("%")) {
                        irc.addElement(name.substring(1, name.length()));
                    } else if (name.startsWith("$")) {
                        vk.addElement(name.substring(1, name.length()));
                    }
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
            news.addElement(new Msg(Constants.MESSAGE_TYPE_IN, null, null, SR.get(SR.MS_ERROR)));
        }
        wait = false;
        rePaint();
        redraw();
    }

    public void commandAction(Command c, Displayable d) {
        if (c == cmdICQ) {
            new DiscoSearchForm(display, this, icq, 0);
        }
        if (c == cmdMrim) {
            new DiscoSearchForm(display, this, mrim, 1);
        }
        if (c == cmdIrc) {
            new DiscoSearchForm(display, this, irc, 2);
        }
        if (c == cmdVk) {
            new DiscoSearchForm(display, this, vk, 3);
        }
        super.commandAction(c, d);
    }

    protected void rePaint() {
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
//#ifdef GRAPHICS_MENU
//#         //super.commandState();
//#else
    super.commandState();
//#endif
        addCommand(cmdICQ);
        cmdICQ.setImg(0x04);
        addCommand(cmdMrim);
        cmdMrim.setImg(0x04);
        addCommand(cmdIrc);
        cmdIrc.setImg(0x04);
        addCommand(cmdVk);
        cmdVk.setImg(0x04);
//#ifndef GRAPHICS_MENU
     addCommand(cmdCancel);
//#endif
    }

//#ifdef MENU_LISTENER
    public String touchLeftCommand() {
        return SR.get(SR.MS_MENU);
    }

//#ifdef GRAPHICS_MENU
//#     public void touchLeftPressed() {
//#         showGraphicsMenu();
//#     }
//# 
//#     public int showGraphicsMenu() {
//#         commandState();
//#         menuItem = new GMenu(display, parentView, this, null, menuCommands);
//#         GMenuConfig.getInstance().itemGrMenu = 123;
//#         redraw();
//#         return 123;
//#     }
//# 
//#else
    public void touchLeftPressed(){
        showMenu();
    }

    public void showMenu() {
        commandState();
        new MyMenu(display, parentView, this, SR.get(SR.MS_HISTORY_OPTIONS), null, menuCommands);
   }
//#endif
//#endif
}
