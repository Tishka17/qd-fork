/*
 * StatusSelect.java
 *
 * Created on 20.05.2008, 15:47
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

package client;

import java.util.*;
import javax.microedition.lcdui.Displayable;
import locale.SR;
import ui.*;
import ui.MainBar;
//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.CommandListener;
//# import javax.microedition.lcdui.Command;
//#else
import menu.MenuListener;
import menu.Command;
//#endif
//#ifdef GRAPHICS_MENU
import ui.GMenu;
//#endif
import account.AccountSelect;
import midlet.BombusQD;

/**
 *
 * @author ad,aqent
 */

public class StatusSelect extends VirtualList implements
//#ifndef MENU_LISTENER
//#         CommandListener,
//#else
        MenuListener//,
//#endif
{

    private Command cmdOk;
    private Command cmdEdit;
    private Command cmdDef;
    private Command cmdCancel;

    private Vector statusList;
    private int defp;
    private Contact to;

    public StatusSelect(Contact to) {
        super();

       cmdOk=new Command(SR.get(SR.MS_SELECT),Command.OK,1);
       cmdOk.setImg(0x43);

       cmdEdit=new Command(SR.get(SR.MS_EDIT),Command.SCREEN,2);
       cmdEdit.setImg(0x40);

       cmdDef=new Command(SR.get(SR.MS_SETDEFAULT),Command.OK,3);
       cmdDef.setImg(0x24);

       cmdCancel=new Command(SR.get(SR.MS_CANCEL),Command.BACK,99);

        statusList=StatusList.getInstance().statusList;
        this.to=to;
        if (to==null) {
            setMainBarItem(new MainBar(SR.get(SR.MS_STATUS)));
        } else {
            setMainBarItem(new MainBar(to));
        }

        defp=midlet.BombusQD.cf.loginstatus;
        moveCursorTo(defp);
    }

    public void show() {
        setCommandListener(this);
        super.show();
    }

    public void commandState() {
//#ifdef MENU_LISTENER
        menuCommands.removeAllElements();
//#endif
        ExtendedStatus ex = (ExtendedStatus)getFocusedObject();
        if(-1 == ex.getName().indexOf("pep")) {
          addCommand(cmdEdit);
          addCommand(cmdDef);
        }
        addCommand(cmdOk);
//#ifndef GRAPHICS_MENU
//#      addCommand(cmdCancel);
//#endif
    }

    public VirtualElement getItemRef(int Index){
        return (VirtualElement)statusList.elementAt(Index);
    }

    private ExtendedStatus getSel(){ return (ExtendedStatus)getFocusedObject();}

    private boolean selectAdvancedStatus() {
       ExtendedStatus ex = (ExtendedStatus)getFocusedObject();
//#ifdef PEP
       if(-1 != ex.getName().indexOf("pep")) {
          midlet.BombusQD.sd.roster.selectPEP.show(ex.usermood);
          return true;
       }
//#endif
       return false;
    }

    public void commandAction(Command c, Displayable d){
        if (c==cmdOk) eventOk();
        if (c==cmdEdit) {
            new StatusEditForm(getSel()).show();
        }

        if (c==cmdDef) {
            midlet.BombusQD.cf.loginstatus=cursor;
            redraw();
            midlet.BombusQD.cf.saveToStorage();
        }

        if (c==cmdCancel) destroyView();
    }

    public void eventLongOk() {
        touchLeftPressed();
    }

    public void eventOk() {
        if(!selectAdvancedStatus()) {
          send();
        }
    }

    public void send() {
        int status = getSel().getImageIndex();
        if (to != null) {
            if (midlet.BombusQD.sd.roster.isLoggedIn()) {
                midlet.BombusQD.sd.roster.sendDirectPresence(status, to, null);
            }
        } else {
            midlet.BombusQD.cf.isStatusFirst = true;
            if (!midlet.BombusQD.sd.roster.isLoggedIn()) {
                AccountSelect select = new AccountSelect(false, status);
                select.setParentView(BombusQD.sd.roster);
                select.show();
            } else {
                midlet.BombusQD.sd.roster.sendPresence(status, null);
                destroyView();
            }
        }
    }

    public int getItemCount(){   return statusList.size(); }

    public static void save(){
        StatusList.getInstance().saveStatusToStorage();
    }

//#ifdef MENU_LISTENER
//#ifdef GRAPHICS_MENU
    public int showGraphicsMenu() {
        commandState();
        menuItem = new GMenu(this, null, menuCommands);
        GMenuConfig.getInstance().itemGrMenu = GMenu.STATUS_SELECT;
        return GMenu.STATUS_SELECT;
    }
//#else
//#     public void showMenu() {
//#         commandState();
//#         new MyMenu(display, parentView, this, SR.get(SR.MS_STATUS), null, menuCommands);
//#     }
//#endif
//#endif
}
