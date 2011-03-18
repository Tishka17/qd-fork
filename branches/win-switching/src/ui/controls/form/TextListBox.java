/*
 * TextListBox.java
 *
 * Created on 25 ??? 2008 ?., 16:58
 *
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
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

package ui.controls.form;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import ui.MainBar;
import ui.VirtualElement;
import ui.VirtualList;
import client.Config;

import java.util.Vector;
import locale.SR;

//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.CommandListener;
//# import javax.microedition.lcdui.Command;
//#else
import menu.MenuListener;
import menu.Command;
//#endif
//#ifdef GRAPHICS_MENU
import midlet.BombusQD;
import ui.GMenu;
import ui.GMenuConfig;
//#endif
/**
 *
 * @author ad,aqent
 */
public class TextListBox extends VirtualList implements
//#ifndef MENU_LISTENER
//#         CommandListener
//#else
        MenuListener
//#endif
    {

    private Command cmdOk;
    private Command cmdClear;

    private Vector recentList;

    private EditBox ti;

    public TextListBox(EditBox ti) {
        super();

        cmdOk=new Command(SR.get(SR.MS_OK), Command.OK,1);
        cmdOk.setImg(0x43);

        cmdClear=new Command(SR.get(SR.MS_CLEAR), Command.SCREEN, 2);
        cmdClear.setImg(0x13);

        this.ti=ti;
        this.recentList=ti.recentList;
        setMainBarItem(new MainBar(SR.get(SR.MS_SELECT)));

        setCommandListener(this);
    }

    public void commandState() {
//#ifdef MENU_LISTENER
        menuCommands.removeAllElements();
//#endif
        addCommand(cmdOk); 
        addCommand(cmdClear); 
//#ifndef GRAPHICS_MENU
//#      addCommand(cmdCancel);
//#endif        
    }

    public void eventOk() {
        if (recentList.size()>0)
            ti.setValue((String) recentList.elementAt(cursor));

        destroyView();
    }

    public void commandAction(Command c, Displayable d){
        if (c==cmdClear) {
            ti.recentList.removeAllElements();
            ti.saveRecentList();
        }
        if (c==cmdOk) {
            eventOk();
            return;
        }

        destroyView();
    }

    public VirtualElement getItemRef(int index){
        return new ListItem((String) recentList.elementAt(index));
    }
    public int getItemCount() { return recentList.size(); }

//#ifdef MENU_LISTENER

//#ifdef GRAPHICS_MENU
    public int showGraphicsMenu() {
        commandState();

        menuItem = new GMenu(this,null, menuCommands);
        GMenuConfig.getInstance().itemGrMenu = GMenu.TEXTLISTBOX;
        return GMenu.TEXTLISTBOX;
    }
//#else
//#     public void showMenu() {
//#         commandState();
//#         String capt="";
//#         try {
//#             capt=getMainBarItem().elementAt(0).toString();
//#         } catch (Exception ex){ }
//#         new MyMenu(display, parentView, this, capt, null, menuCommands);
//#    }
//#endif

//#endif
}
