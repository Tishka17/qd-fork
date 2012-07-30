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

import locale.SR;
import menu.MenuListener;
import menu.Command;
import ui.MainBar;
import io.NvStorage;
import java.io.DataOutputStream;
import java.util.Vector;
import javax.microedition.lcdui.TextField;
import ui.controls.AlertBox;
import ui.GMenu;
import ui.GMenuConfig;
import ui.VirtualElement;
import ui.VirtualList;
import ui.input.InputTextBox;
import ui.input.InputTextBoxNotify;

public class AccountSelect extends VirtualList implements MenuListener, InputTextBoxNotify {

    private final Vector accountList;
    private final int activeAccount;
    private final Command cmdLogin;
    private final Command cmdRegister;
    private final Command cmdServ1_reg = new Command("Jabber.ru", 0x86);
    private final Command cmdServ2_reg = new Command("Silper.cz", 0x86);
    private final Command cmdServ3_reg = new Command("Jabbus.org", 0x86);
    private final Command cmdServ4_reg = new Command("Mytlt.ru", 0x86);
    private final Command cmdServ5_reg = new Command("Jabbim.com", 0x86);
    private final Command cmdServ6_reg = new Command("Other", 0x86);
    private final Command cmdAdd;
    private final Command cmdJabber = new Command("Jabber", 0x90);
    private final Command cmdYaru = new Command("Yandex.ru", 0x91);
    private final Command cmdGTalk_SSL = new Command("Gtalk SSL", 0x92);
    private final Command cmdGTalk_HTTPS = new Command("Gtalk HTTPS", 0x92);
    private final Command cmdLj = new Command("LiveJournal", 0x93);
    private final Command cmdQip = new Command("QIP", 0x94);
    private final Command cmdVk = new Command("Вконтакте", 0x95);
    private final Command cmdOk = new Command("Одноклассники", 0x90);
    private final Command cmdEdit;
    private final Command cmdDel;
    private final Command cmdRemoveAcc;
    private final Command cmdChangePass;
    int status;

    public AccountSelect(int status) {
        super();
        cmdLogin = new Command(SR.get(SR.MS_SELLOGIN), 0x50);
        cmdRegister = new Command(SR.get(SR.MS_REGISTERING), 0x42);
        cmdAdd = new Command(SR.get(SR.MS_NEW_ACCOUNT), 0x42);
        cmdEdit = new Command(SR.get(SR.MS_EDIT), 0x40);
        cmdDel = new Command(SR.get(SR.MS_DELETE), 0x41);
        cmdRemoveAcc = new Command(SR.get(SR.MS_REMOVE_ACCOUNT), 0x51);
        cmdChangePass = new Command(SR.get(SR.MS_CHANGE_PASSWORD), 0x52);

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

        activeAccount = midlet.BombusQD.cf.accountIndex;
        accountList = Account.createListFromStorage();
        try {
            Account a;
            a = (Account) accountList.elementAt(activeAccount);
            a.setActive(true);
            moveCursorTo(activeAccount);
        } catch (Exception e) {
        }
    }

    public void commandState() {
        menuCommands.removeAllElements();
        cmdfirstList.removeAllElements();
        cmdsecondList.removeAllElements();
        cmdThirdList.removeAllElements();

        addCommand(cmdAdd);
        addInCommand(1, cmdJabber);
        addInCommand(1, cmdYaru);
        addInCommand(1, cmdGTalk_SSL);
        addInCommand(1, cmdGTalk_HTTPS);
        addInCommand(1, cmdLj);
        addInCommand(1, cmdQip);
        addInCommand(1, cmdVk);
        addInCommand(1, cmdOk);

        addCommand(cmdRegister);
        addInCommand(2, cmdServ1_reg);
        addInCommand(2, cmdServ4_reg);
        addInCommand(2, cmdServ5_reg);
        addInCommand(2, cmdServ6_reg);

        if (!accountList.isEmpty()) {
            addCommand(cmdEdit);
            addCommand(cmdLogin);
            addCommand(cmdDel);
            if (midlet.BombusQD.sd.roster.isLoggedIn()) {
                Object cursor_acc = getFocusedObject();
                Object active_acc = accountList.elementAt(activeAccount);
                if (active_acc.equals(cursor_acc)) {
                    addCommand(cmdRemoveAcc);
                    addCommand(cmdChangePass);
                }
            }
        }
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

    public void okNotify(String text) {
        Account acc = new Account();
        acc.setServer(text);
        new AccountRegister(acc, this);
    }

    public void commandAction(Command c) {
        if (c == cmdServ1_reg) {
            Account acc = new Account();
            acc.setServer("jabber.ru");
            new AccountRegister(acc, this);
        } else if (c == cmdServ2_reg) {
            Account acc = new Account();
            acc.setServer("silper.cz");
            new AccountRegister(acc, this);
        } else if (c == cmdServ3_reg) {
            Account acc = new Account();
            acc.setServer("jabbus.org");
            new AccountRegister(acc, this);
        } else if (c == cmdServ4_reg) {
            Account acc = new Account();
            acc.setServer("mytlt.ru");
            new AccountRegister(acc, this);
        } else if (c == cmdServ5_reg) {
            Account acc = new Account();
            acc.setServer("jabbim.com");
            new AccountRegister(acc, this);
        } else if (c == cmdServ6_reg) {
            InputTextBox input = new InputTextBox(SR.get(SR.MS_SERVER), null, "server", 1000, TextField.ANY);
            input.setNotifyListener(this);
            input.show();
        } else if (c == cmdJabber) {
            new AccountForm(null, AccountForm.PROFILE_JABBER).show();
        } else if (c == cmdYaru) {
            new AccountForm(null, AccountForm.PROFILE_YANDEX).show();
        } else if (c == cmdGTalk_SSL) {
            new AccountForm(null, AccountForm.PROFILE_GTALK_SSL).show();
        } else if (c == cmdGTalk_HTTPS) {
            new AccountForm(null, AccountForm.PROFILE_GTALK_HTTPS).show();
        } else if (c == cmdLj) {
            new AccountForm(null, AccountForm.PROFILE_LIVEJOURNAL).show();
        } else if (c == cmdQip) {
            new AccountForm(null, AccountForm.PROFILE_QIP).show();
        } else if (c == cmdVk) {
            new AccountForm(null, AccountForm.PROFILE_VKONTAKTE).show();
        } else if (c == cmdOk) {
            new AccountForm(null, AccountForm.PROFILE_ODNOKLASSNIKI).show();
        } else if (c == cmdLogin) {
            switchAccount(true);
        } else if (c == cmdEdit) {
            new AccountForm((Account) getFocusedObject(), -1).show();
        } else if (c == cmdChangePass) {
            new ChangePasswordForm((Account) getFocusedObject()).show();
        } else if (c == cmdRemoveAcc) {
            new AccountRemoveForm((Account) getFocusedObject()).show();
        } else if (c == cmdDel) {
            if (midlet.BombusQD.sd.roster != null) {
                if (cursor == midlet.BombusQD.cf.accountIndex && midlet.BombusQD.sd.roster.isLoggedIn()) {
                    return;
                }
                AlertBox box = new AlertBox(SR.get(SR.MS_DELETE), getFocusedObject().toString(), AlertBox.BUTTONS_YESNO) {
                    public void yes() {
                        delAccount();
                    }
                };
                box.show();
            }
        }
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

    public void eventLongOk() {
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

    public int showGraphicsMenu() {
        commandState();
        menuItem = new GMenu(this, null, menuCommands, cmdfirstList, cmdsecondList, cmdThirdList);
        GMenuConfig.getInstance().itemGrMenu = GMenu.ACCOUNT_SELECT_MENU;
        return GMenu.ACCOUNT_SELECT_MENU;
    }
}
