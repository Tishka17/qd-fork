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

import account.AccountSelect;
import java.util.Vector;
import locale.SR;
import menu.Command;
import menu.MenuListener;
import midlet.BombusQD;
import ui.GMenu;
import ui.GMenuConfig;
import ui.MainBar;
import ui.VirtualElement;
import ui.VirtualList;

/**
 *
 * @author ad,aqent
 */

public class StatusSelect extends VirtualList implements MenuListener {
    private Command cmdOk;
    private Command cmdEdit;
    private Command cmdDef;

    private Vector statusList;
    private int defp;
    private Contact to;

    public StatusSelect(Contact to) {
        super();

       cmdOk=new Command(SR.get(SR.MS_SELECT), 0x43);
       cmdEdit=new Command(SR.get(SR.MS_EDIT), 0x40);
       cmdDef=new Command(SR.get(SR.MS_SETDEFAULT), 0x24);

        statusList=StatusList.getInstance().statusList;
        this.to=to;
        if (to==null) {
            setMainBarItem(new MainBar(SR.get(SR.MS_STATUS)));
        } else {
            setMainBarItem(new MainBar(to));
        }

        defp = BombusQD.cf.loginstatus;
        moveCursorTo(defp);
    }

    public void commandState() {
        menuCommands.removeAllElements();
        ExtendedStatus ex = (ExtendedStatus)getFocusedObject();
        if(-1 == ex.getName().indexOf("pep")) {
          addCommand(cmdEdit);
          addCommand(cmdDef);
        }
        addCommand(cmdOk);
    }

    public VirtualElement getItemRef(int Index){
        return (VirtualElement)statusList.elementAt(Index);
    }

    private ExtendedStatus getSel(){ return (ExtendedStatus)getFocusedObject();}

    private boolean selectAdvancedStatus() {
       ExtendedStatus ex = (ExtendedStatus)getFocusedObject();
//#ifdef PEP
       if(-1 != ex.getName().indexOf("pep")) {
          new SelectPEP().show(ex.usermood);
          return true;
       }
//#endif
       return false;
    }

    public void commandAction(Command c){
        if (c==cmdOk) eventOk();
        if (c==cmdEdit) {
            new StatusEditForm(getSel()).show();
        }

        if (c==cmdDef) {
            BombusQD.cf.loginstatus=cursor;
            redraw();
            BombusQD.cf.saveToStorage();
        }
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
            BombusQD.sd.roster.sendDirectPresence(status, to, null);
            destroyView();
        } else {
            BombusQD.cf.isStatusFirst = true;
            if (!BombusQD.sd.roster.isLoggedIn()) {
                AccountSelect select = new AccountSelect(status);
                select.setParentView(BombusQD.sd.roster);
                select.show();
            } else {
                BombusQD.sd.roster.sendPresence(status, null);
                destroyView();
            }
        }
    }

    public int getItemCount() {
        if (BombusQD.sd.roster.isLoggedIn()) {
            if (to == null) {
                return statusList.size();
            }
        }
        return StatusList.STATUS_COUNT;
    }

    public int showGraphicsMenu() {
        commandState();
        menuItem = new GMenu(this, menuCommands);
        GMenuConfig.getInstance().itemGrMenu = GMenu.STATUS_SELECT;
        return GMenu.STATUS_SELECT;
    }
}
