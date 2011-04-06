/*
 * AccountSelect.java
 *
 * Created on 19.03.2005, 23:26
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
 *
 */
package account;

import client.*;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
import ui.*;
import java.io.*;
import java.util.*;
//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.CommandListener;
//# import javax.microedition.lcdui.Command;
//#else
import menu.MenuListener;
import menu.Command;
//#endif
import ui.MainBar;
import io.NvStorage;
import ui.controls.AlertBox;
//#ifdef GRAPHICS_MENU
import ui.GMenu;
//#endif

public class AccountSelect extends VirtualList implements
//#ifndef MENU_LISTENER
//#         CommandListener
//#else
        MenuListener
//#endif
{
    private Vector accountList;

    private int activeAccount;

    private Command cmdLogin;
    private Command cmdConfigurationMaster;
    private Command cmdRegister;
    private Command cmdServ1_reg = new Command("Jabber.ru", Command.SCREEN, 18);
    private Command cmdServ2_reg = new Command("Silper.cz", Command.SCREEN, 19);
    private Command cmdServ3_reg = new Command("Jabbus.org", Command.SCREEN, 20);
    private Command cmdServ4_reg = new Command("Mytlt.ru", Command.SCREEN, 21);
    private Command cmdServ5_reg = new Command("Jabbim.com", Command.SCREEN, 22);
    private Command cmdServ6_reg = new Command("Other", Command.SCREEN, 23);
    private Command cmdAdd;
    private Command cmdJabber = new Command("Jabber", Command.SCREEN, 13);
    private Command cmdYaru = new Command("Yandex.ru", Command.SCREEN, 14);
    private Command cmdGTalk_SSL = new Command("Gtalk SSL", Command.SCREEN, 15);
    private Command cmdGTalk_HTTPS = new Command("Gtalk HTTPS", Command.SCREEN, 16);
    private Command cmdLj = new Command("LiveJournal", Command.SCREEN, 17);
    private Command cmdQip = new Command("QIP", Command.SCREEN, 18);
    private Command cmdVk = new Command("Вконтакте", Command.SCREEN, 19);
    private Command cmdEdit;
    private Command cmdDel;
    private Command cmdCancel;
    private Command cmdRemoveAcc;
    private Command cmdChangePass;

    int status;

    public AccountSelect(Display display, Displayable pView, boolean enableQuit, int status) {
        super();

        cmdConfigurationMaster = new Command(SR.get(SR.MS_CONFIGURATION_MASTER), Command.OK, 1);
        cmdConfigurationMaster.setImg(0x42);

        cmdLogin = new Command(SR.get(SR.MS_SELLOGIN), Command.OK, 1);
        cmdLogin.setImg(0x50);

        cmdRegister = new Command(SR.get(SR.MS_REGISTERING), Command.ITEM, 11);
        cmdRegister.setImg(0x42);

        cmdAdd = new Command(SR.get(SR.MS_NEW_ACCOUNT), Command.SCREEN, 12);
        cmdAdd.setImg(0x42);

        cmdEdit = new Command(SR.get(SR.MS_EDIT), Command.ITEM, 3);
        cmdEdit.setImg(0x40);

        cmdDel = new Command(SR.get(SR.MS_DELETE), Command.ITEM, 4);
        cmdDel.setImg(0x41);

        cmdCancel = new Command(SR.get(SR.MS_BACK), Command.BACK, 99);

        cmdRemoveAcc = new Command(SR.get(SR.MS_REMOVE_ACCOUNT), Command.ITEM, 90);
        cmdRemoveAcc.setImg(0x51);

        cmdChangePass = new Command(SR.get(SR.MS_CHANGE_PASSWORD), Command.ITEM, 91);
        cmdChangePass.setImg(0x52);

        cmdServ1_reg.setImg(0x86);
        cmdServ4_reg.setImg(0x86);
        cmdServ5_reg.setImg(0x86);
        cmdServ6_reg.setImg(0x86);

        cmdJabber.setImg(0x90);
        cmdYaru.setImg(0x91);
        cmdGTalk_SSL.setImg(0x92);
        cmdGTalk_HTTPS.setImg(0x92);
        cmdLj.setImg(0x93);
        cmdQip.setImg(0x94);
        cmdVk.setImg(0x95);

        this.display = display;
        this.status = status;
        String str = "";
        switch (status) {
            case 0:
                str = "(online)";
                break;
            case 1:
                str = "(chat)";
                break;
            case 2:
                str = "(away)";
                break;
            case 3:
                str = "(xa)";
                break;
            case 4:
                str = "(dnd)";
                break;
            case 5:
                str = "(offline)";
                break;
            case 6:
                str = "";
                break;
        }
        setMainBarItem(new MainBar(status == -1 ? SR.get(SR.MS_ACCOUNTS) : SR.get(SR.MS_CONNECT_TO) + str));

        accountList = null;
        accountList = new Vector(0);

        Account a;

        int index = 0;
        activeAccount = midlet.BombusQD.cf.accountIndex;
        do {
            a = Account.createFromStorage(index);
            if (a != null) {
                a.setActive(activeAccount == index);
                accountList.addElement(a);
                index++;
            }
        } while (a != null);

        if (!accountList.isEmpty()) {
            moveCursorTo(activeAccount);
        }

        setCommandListener(this);
        display.setCurrent(this);
        this.parentView = pView;
    }

    public void commandState() {
//#ifdef MENU_LISTENER
        menuCommands.removeAllElements();
        cmdfirstList.removeAllElements();
        cmdsecondList.removeAllElements();
        cmdThirdList.removeAllElements();
//#endif
        addCommand(cmdConfigurationMaster);

        if (!accountList.isEmpty()) {
            addCommand(cmdLogin);
            addCommand(cmdEdit);
            addCommand(cmdDel);
        }

        if (midlet.BombusQD.sd.roster != null) {
            if (midlet.BombusQD.sd.roster.isLoggedIn()) {
                addCommand(cmdRemoveAcc);
                addCommand(cmdChangePass);
            }
        }
//#ifdef GRAPHICS_MENU
        addCommand(cmdRegister);
        addInCommand(2, cmdServ1_reg);
        addInCommand(2, cmdServ4_reg);
        addInCommand(2, cmdServ5_reg);
        addInCommand(2, cmdServ6_reg);

        addCommand(cmdAdd);

        addInCommand(1, cmdJabber);
        addInCommand(1, cmdYaru);

        addInCommand(1, cmdGTalk_SSL);
        addInCommand(1, cmdGTalk_HTTPS);
        addInCommand(1, cmdLj);
        addInCommand(1, cmdQip);
        addInCommand(1, cmdVk);
//#endif

//#ifndef GRAPHICS_MENU
//#     if (activeAccount>=0 && !enableQuit)
//#      addCommand(cmdCancel);
//#endif
    }

    public void addAccount(Account a) {
        accountList.addElement(a);
    }

    public VirtualElement getItemRef(int Index) {
        return (VirtualElement) accountList.elementAt(Index);
    }

    protected int getItemCount() {
        return accountList.size();
    }

    public void commandAction(Command c, Displayable d) {
        if (c == cmdCancel) {
            destroyView();
        }
//#ifdef GRAPHICS_MENU
        if (c == cmdServ1_reg) {
            new AccountForm(display, this, "jabber.ru");
        }
        if (c == cmdServ2_reg) {
            new AccountForm(display, this, "silper.cz");
        }
        if (c == cmdServ3_reg) {
            new AccountForm(display, this, "jabbus.org");
        }
        if (c == cmdServ4_reg) {
            new AccountForm(display, this, "mytlt.ru");
        }
        if (c == cmdServ5_reg) {
            new AccountForm(display, this, "jabbim.com");
        }
        if (c == cmdServ6_reg) {
            new AccountForm(display, this, "");
        }

        if (c == cmdJabber) {
            new AccountForm(display, this, null, AccountForm.PROFILE_JABBER);
        }
        if (c == cmdYaru) {
            new AccountForm(display, this, null, AccountForm.PROFILE_YANDEX);
        }

        if (c == cmdGTalk_SSL) {
            new AccountForm(display, this, null, AccountForm.PROFILE_GTALK_SSL);
        }
        if (c == cmdGTalk_HTTPS) {
            new AccountForm(display, this, null, AccountForm.PROFILE_GTALK_HTTPS);
        }

        if (c == cmdLj) {
            new AccountForm(display, this, null, AccountForm.PROFILE_LIVEJOURNAL);
        }
        if (c == cmdQip) {
            new AccountForm(display, this, null, AccountForm.PROFILE_QIP);
        }
        if (c == cmdVk) {
            new AccountForm(display, this, null, AccountForm.PROFILE_VKONTAKTE);
        }
//#else
//#         if (c==cmdAdd) {
//#             new AccountForm(display, this, this, null);
//#         }
//#endif
        if (c == cmdConfigurationMaster) {
            new ConfigurationMaster(display, this);
        }
        if (c == cmdLogin) {
            switchAccount(true);
        }
        if (c == cmdEdit) {
            new AccountForm(display, this, (Account)getFocusedObject(), -1);
        }
        if (c == cmdChangePass) {
            Object cursor_acc = getFocusedObject();
            Object active_acc = accountList.elementAt(activeAccount);
            if (active_acc.equals(cursor_acc)) {
                Account acc = (Account) getFocusedObject();
                new CommandForm(display, parentView, 2, "Form", acc, this);
            }

        }
        if (c == cmdRemoveAcc) {
            Object cursor_acc = getFocusedObject();
            Object active_acc = accountList.elementAt(activeAccount);
            if (active_acc.equals(cursor_acc)) {
                Account acc = (Account) getFocusedObject();
                new CommandForm(display, parentView, 1, "Form", acc, accountList);
            }
        }
        if (c == cmdDel) {
            if (midlet.BombusQD.sd.roster != null) {
                if (cursor == midlet.BombusQD.cf.accountIndex && midlet.BombusQD.sd.roster.isLoggedIn()) {
                    return;
                }
                new AlertBox(SR.get(SR.MS_DELETE), getFocusedObject().toString(), display, this, false)  {
                    public void yes() {
                        delAccount();
                    }

                    public void no() {
                    }
                };
            }
        }

    }

    public void touchRightPressed() {
        destroyView();
    }

    public void destroyView() {
        midlet.BombusQD.sd.roster.showRoster();
    }

    private void delAccount() {
        if (accountList.size() == 1) {
            midlet.BombusQD.cf.accountIndex = -1;
        } else if (midlet.BombusQD.cf.accountIndex > cursor) {
            midlet.BombusQD.cf.accountIndex--;
        }

        midlet.BombusQD.cf.saveToStorage();

        accountList.removeElement(getFocusedObject());
        rmsUpdate();
        moveCursorHome();

        redraw();
    }

    private void switchAccount(boolean login) {
        midlet.BombusQD.cf.accountIndex = cursor;
        midlet.BombusQD.cf.saveInt();
        Account.loadAccount(login, cursor, status);
        destroyView();
    }

    public void eventOk() {
        if (getItemCount() > 0) {
            switchAccount(true);
        }
    }
    public void eventLongOk(){
	showGraphicsMenu();
    }

    public void rmsUpdate() {
        DataOutputStream outputStream = NvStorage.CreateDataOutputStream();
        Account acc;
        for (int i = 0; i < accountList.size(); i++) {
            acc = (Account) accountList.elementAt(i);
            acc.saveToDataOutputStream(outputStream);
            acc.setIconElement();
        }
        NvStorage.writeFileRecord(outputStream, "accnt_db", 0, true); //Account.storage
    }

//#ifdef MENU_LISTENER
//#ifdef GRAPHICS_MENU
    public int showGraphicsMenu() {
        commandState();
        menuItem = new GMenu(display, parentView, this, null, menuCommands, cmdfirstList, cmdsecondList, cmdThirdList);
        GMenuConfig.getInstance().itemGrMenu = GMenu.ACCOUNT_SELECT_MENU;
        return GMenu.ACCOUNT_SELECT_MENU;
    }
//#else
//#     public void showMenu() {
//#         commandState();
//#         new MyMenu(display, parentView, this, SR.get(SR.MS_DISCO, null, menuCommands));
//#    }
//#endif

//#endif
}
