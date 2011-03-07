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
import midlet.BombusQD;
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
//# import ui.GMenu;
//#endif

public class AccountSelect
        extends VirtualList
        implements
//#ifndef MENU_LISTENER
//#         CommandListener
//#else
        MenuListener
//#endif
{
    public Vector accountList;
    int activeAccount;
    Command cmdLogin;
    Command cmdConfigurationMaster;
    Command cmdRegister;
    Command cmdServ1_reg = new Command("Jabber.ru", Command.SCREEN, 18);
    Command cmdServ2_reg = new Command("Silper.cz", Command.SCREEN, 19);
    Command cmdServ3_reg = new Command("Jabbus.org", Command.SCREEN, 20);
    Command cmdServ4_reg = new Command("Mytlt.ru", Command.SCREEN, 21);
    Command cmdServ5_reg = new Command("Jabbim.com", Command.SCREEN, 22);
    Command cmdServ6_reg = new Command("Other", Command.SCREEN, 23);
    Command cmdAdd;
    Command cmdJabber = new Command("Jabber", Command.SCREEN, 13);
    Command cmdYaru = new Command("Yandex.ru", Command.SCREEN, 14);
    Command cmdGTalk_SSL = new Command("Gtalk SSL", Command.SCREEN, 15);
    Command cmdGTalk_HTTPS = new Command("Gtalk HTTPS", Command.SCREEN, 16);
    Command cmdLj = new Command("LiveJournal", Command.SCREEN, 17);
    Command cmdQip = new Command("QIP", Command.SCREEN, 18);
    Command cmdVk = new Command("Вконтакте", Command.SCREEN, 19);
    Command cmdEdit;
    Command cmdDel;
    Command cmdCancel;
    Command cmdQuit;
    Command cmdRemoveAcc;
    Command cmdChangePass;
    int status;

    /** Creates a new instance of AccountPicker */
    public AccountSelect(Display display, Displayable pView, boolean enableQuit, int status) {
        super();

        cmdConfigurationMaster = new Command(SR.get(SR.MS_CONFIGURATION_MASTER), Command.OK, 1);
        cmdLogin = new Command(SR.get(SR.MS_SELLOGIN), Command.OK, 1);
        cmdRegister = new Command(SR.get(SR.MS_REGISTERING), Command.ITEM, 11);
        cmdAdd = new Command(SR.get(SR.MS_NEW_ACCOUNT), Command.SCREEN, 12);
        cmdEdit = new Command(SR.get(SR.MS_EDIT), Command.ITEM, 3);
        cmdDel = new Command(SR.get(SR.MS_DELETE), Command.ITEM, 4);
        cmdCancel = new Command(SR.get(SR.MS_BACK), Command.BACK, 99);
        cmdQuit = new Command(SR.get(SR.MS_APP_QUIT), Command.SCREEN, 10);
        cmdRemoveAcc = new Command(SR.get(SR.MS_REMOVE_ACCOUNT), Command.ITEM, 90);
        cmdChangePass = new Command(SR.get(SR.MS_CHANGE_PASSWORD), Command.ITEM, 91);

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
        commandState();
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
        cmdConfigurationMaster.setImg(0x42);
        if (!accountList.isEmpty()) {
            addCommand(cmdLogin);
            cmdLogin.setImg(0x50);
            addCommand(cmdEdit);
            cmdEdit.setImg(0x40);
            addCommand(cmdDel);
            cmdDel.setImg(0x41);
        }

        if (midlet.BombusQD.sd.roster != null) {
            if (midlet.BombusQD.sd.roster.isLoggedIn()) {
                //if(cf.difficulty_level==2) {
                addCommand(cmdRemoveAcc);
                cmdRemoveAcc.setImg(0x51);
                addCommand(cmdChangePass);
                cmdChangePass.setImg(0x52);
            }
        }
//#ifdef GRAPHICS_MENU
//#         addCommand(cmdRegister);
//#         cmdRegister.setImg(0x42);
//#         addInCommand(2, cmdServ1_reg);
//#         cmdServ1_reg.setImg(0x86);
//#         //addInCommand(2,cmdServ2_reg); cmdServ2_reg.setImg(0x85);
//#         //addInCommand(2,cmdServ3_reg); cmdServ3_reg.setImg(0x86);
//#         addInCommand(2, cmdServ4_reg);
//#         cmdServ4_reg.setImg(0x86);
//#         addInCommand(2, cmdServ5_reg);
//#         cmdServ5_reg.setImg(0x85);
//#         addInCommand(2, cmdServ6_reg);
//#         cmdServ6_reg.setImg(0x72);
//# 
//#         addCommand(cmdAdd);
//#         cmdAdd.setImg(0x42);
//#         addInCommand(1, cmdJabber);
//#         cmdJabber.setImg(0x90);
//#         addInCommand(1, cmdYaru);
//#         cmdYaru.setImg(0x91);
//#         addInCommand(1, cmdGTalk_SSL);
//#         cmdGTalk_SSL.setImg(0x92);
//#         addInCommand(1, cmdGTalk_HTTPS);
//#         cmdGTalk_HTTPS.setImg(0x92);
//#         addInCommand(1, cmdLj);
//#         cmdLj.setImg(0x93);
//#         addInCommand(1, cmdQip);
//#         cmdQip.setImg(0x94);
//#         addInCommand(1, cmdVk);
//#         cmdVk.setImg(0x95);
//#endif

        if (accountList.isEmpty() == true) {
            addCommand(cmdQuit);
            cmdQuit.setImg(0x33);
            //addCommand(cmdConfig); cmdConfig.setImg(0x03);
        }

//#ifndef GRAPHICS_MENU
    if (activeAccount>=0 && !enableQuit)
     addCommand(cmdCancel);
//#endif
    }

    public VirtualElement getItemRef(int Index) {
        return (VirtualElement) accountList.elementAt(Index);
    }

    protected int getItemCount() {
        return accountList.size();
    }

    public void commandAction(Command c, Displayable d) {
        if (c == cmdQuit) {
            destroyView();
            BombusQD.getInstance().notifyDestroyed();
            return;
        }
        if (c == cmdCancel) {
            destroyView();
        }
//#ifdef GRAPHICS_MENU
//#         if (c == cmdServ1_reg) {
//#             new AccountForm(display, this, this, null, 1, true, "jabber.ru");
//#         }
//#         if (c == cmdServ2_reg) {
//#             new AccountForm(display, this, this, null, 1, true, "silper.cz");
//#         }
//#         if (c == cmdServ3_reg) {
//#             new AccountForm(display, this, this, null, 1, true, "jabbus.org");
//#         }
//#         if (c == cmdServ4_reg) {
//#             new AccountForm(display, this, this, null, 1, true, "mytlt.ru");
//#         }
//#         if (c == cmdServ5_reg) {
//#             new AccountForm(display, this, this, null, 1, true, "jabbim.com");
//#         }
//#         if (c == cmdServ6_reg) {
//#             new AccountForm(display, this, this, null, 1, true, "");
//#         }
//# 
//#         if (c == cmdJabber) {
//#ifdef DEBUG_CONSOLE
//#             midlet.BombusQD.debug.add("::add profile", 10);
//#endif
//#             new AccountForm(display, this, this, null, 1, false, null);
//#         }
//#         if (c == cmdYaru) {
//#             new AccountForm(display, this, this, null, 2, false, null);
//#         }
//# 
//#         if (c == cmdGTalk_SSL) {
//#             new AccountForm(display, this, this, null, 3, false, null);
//#         }
//#         if (c == cmdGTalk_HTTPS) {
//#             new AccountForm(display, this, this, null, 6, false, null);
//#         }
//# 
//# 
//#         if (c == cmdLj) {
//#             new AccountForm(display, this, this, null, 4, false, null);
//#         }
//#         if (c == cmdQip) {
//#             new AccountForm(display, this, this, null, 5, false, null);
//#         }
//#         if (c == cmdVk) {
//#             new AccountForm(display, this, this, null, 7, false, null);
//#         }
//#else
        if (c==cmdAdd) {
            new AccountForm(display, this, this, null);
        }
//#endif
        //if (c==cmdConfig) new ConfigForm(display, this);
        if (c == cmdConfigurationMaster) {
            new ConfigurationMaster(display, this);
        }
        if (c == cmdLogin) {
            switchAccount(true);
        }
        if (c == cmdEdit) {
            new AccountForm(display, this, this, (Account) getFocusedObject(), -1, false, null);
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
        commandState();
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
//#     public int showGraphicsMenu() {
//#         commandState();
//#         menuItem = new GMenu(display, parentView, this, null, menuCommands, cmdfirstList, cmdsecondList, cmdThirdList);
//#         GMenuConfig.getInstance().itemGrMenu = GMenu.ACCOUNT_SELECT_MENU;
//#         return GMenu.ACCOUNT_SELECT_MENU;
//#     }
//#else
    public void showMenu() {
        commandState();
        new MyMenu(display, parentView, this, SR.get(SR.MS_DISCO, null, menuCommands));
   }
//#endif

//#endif
    protected void keyRepeated(int keyCode) {
        super.keyRepeated(keyCode);
        if (kHold == keyCode) {
            return;
        }
        kHold = keyCode;
        if (keyCode == KEY_NUM6) {
            midlet.BombusQD.cf.fullscreen = !midlet.BombusQD.cf.fullscreen;
            midlet.BombusQD.cf.saveToStorage();
            VirtualList.fullscreen = midlet.BombusQD.cf.fullscreen;
            if (midlet.BombusQD.sd.roster != null) {
                StaticData.getInstance().roster.setFullScreenMode(midlet.BombusQD.cf.fullscreen);
            }
        }
    }
}
