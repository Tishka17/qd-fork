/*
 * ActiveContacts.java
 *
 * Created on 20.01.2005, 21:20
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

package client;

import java.util.Enumeration;
import java.util.Vector;
import menu.MenuListener;
import menu.Command;
import locale.SR;
import midlet.BombusQD;
import ui.GMenu;
import ui.GMenuConfig;
import ui.MainBar;
import ui.VirtualCanvas;
import ui.VirtualElement;
import ui.VirtualList;

/**
 *
 * @author EvgS,aqent
 */

public final class ActiveContacts extends VirtualList implements MenuListener {
    private static final int SORT_BY_STATUS = 1;
    private static final int SORT_BY_MSGCOUNT = 2;
    
    private static int sortingType = 0;

    private Vector contacts;

    private Command cmdOk;
    private Command cmdCreateMultiMessage;
    private Command cmdSortType;
    private Command cmdSortDefault;
    private Command cmdSortByStatus;
    private Command cmdSortByMsgsCount;
    private Command cmdClearAllMessages;

    public ActiveContacts(Vector contacts, Contact current) {
        cmdOk = new Command(SR.get(SR.MS_SELECT), 0x43);
        cmdCreateMultiMessage = new Command(SR.get(SR.MS_MULTI_MESSAGE), 0x81);
        cmdSortType = new Command(SR.get(SR.MS_SORT_TYPE), 0x64);
        cmdSortDefault = new Command(SR.get(SR.MS_SORT_TYPE_DEF), 0x64);
        cmdSortByStatus = new Command(SR.get(SR.MS_SORT_TYPE_STATUS), 0x64);
        cmdSortByMsgsCount = new Command(SR.get(SR.MS_SORT_TYPE_MSGS), 0x64);
        cmdClearAllMessages = new Command(SR.get(SR.MS_CLEAN_ALL_MESSAGES), 0x41);
        
        this.contacts = contacts;

        if (contacts.contains(current)) {
            focusToContact(current);
        } else {
            setParentView(BombusQD.sd.roster);
        }
        
        sortContacts(sortingType);

        setMainBarItem(new MainBar(SR.get(SR.MS_ACTIVE_CONTACTS) + " (" + getItemCount() + ")"));
    }

    private void sortContacts(int type) {
        sortingType = type;
        switch(type) {
            case SORT_BY_STATUS:
                sort(contacts, 0, 0);
                break;
            case SORT_BY_MSGCOUNT:
                sort(contacts, 0, 1);
                break;
        }
    }

    public void commandState() {
        if (contacts.isEmpty()) {
            return;
        }

        menuCommands.removeAllElements();
        cmdfirstList.removeAllElements();

        addCommand(cmdOk);
        if (contacts.size() > 1) {
            addCommand(cmdCreateMultiMessage);
            addCommand(cmdSortType);
            addInCommand(1, cmdSortDefault);
            addInCommand(1, cmdSortByStatus);
            addInCommand(1, cmdSortByMsgsCount);
            gm.itemCursorIndexIn = sortingType;
        }
        addCommand(cmdClearAllMessages);
    }

    public int showGraphicsMenu() {
        if (contacts.isEmpty()) {
            return 0;
        }
        commandState();

        menuItem = new GMenu(this, null, menuCommands, cmdfirstList, null, null);
        GMenuConfig.getInstance().itemGrMenu = GMenu.ACTIVE_CONTACTS;
        return GMenu.ACTIVE_CONTACTS;
    }

    protected int getItemCount() {
        return contacts.size();
    }

    protected VirtualElement getItemRef(int index) {
        return (VirtualElement) contacts.elementAt(index);
    }

    public void eventOk() {
        Contact contact = (Contact) getFocusedObject();
        BombusQD.sd.roster.focusToContact(contact, false);
//#ifdef CLASSIC_CHAT
//#         if (Config.module_classicchat) {
//#             new SimpleItemChat(contact);
//#         } else {
//#endif
            contact.getMessageList().show();
//#ifdef CLASSIC_CHAT
//#         }
//#endif
    }

    public void commandAction(Command c) {
        if (c == cmdOk) {
            eventOk();
        }
        if (c == cmdCreateMultiMessage) {
            BombusQD.sd.roster.showMultiMsgEditor(contacts);
        }
        if (c == cmdClearAllMessages) {
            BombusQD.sd.roster.cmdCleanAllMessages();
            setParentView(BombusQD.sd.roster);
        }
        if (c == cmdSortByStatus) {
            sortContacts(SORT_BY_STATUS);
        }
        if (c == cmdSortByMsgsCount) {
            sortContacts(SORT_BY_MSGCOUNT);
        }
    }

    public void keyPressed(int keyCode) {
        if (sendEvent(keyCode)) {
            redraw();
            return;
        }
        if (keyCode == VirtualCanvas.KEY_NUM3) {
            destroyView();
        } else if (keyCode == VirtualCanvas.KEY_NUM0) {
            if (getItemCount() < 1) {
                return;
            }

            Contact c = (Contact) getFocusedObject();
            Enumeration i = contacts.elements();

            int pass = 0;
            while (pass < 2) {
                if (!i.hasMoreElements()) {
                    i = contacts.elements();
                }
                Contact p = (Contact) i.nextElement();
                if (pass == 1) {
                    if (p.getNewMessageCount() > 0) {
                        focusToContact(p);
                        setRotator();
                        break;
                    }
                }
                if (p == c) {
                    pass++;
                }
            }
            return;
        } else {
            super.keyPressed(keyCode);
        }
    }

    private void focusToContact(final Contact c) {
        int index = contacts.indexOf(c);
        if (index != -1) {
            moveCursorTo(index);
        }
    }

    protected void keyGreen() {
        eventOk();
    }

    protected void keyClear() {
        Contact contact = (Contact) getFocusedObject();
        if (contact.getMessageList()==this.getParentView()) 
            setParentView(BombusQD.sd.roster);

        contact.purge();
        contacts.removeElementAt(cursor);
        mainbar.setElementAt(Integer.toString(getItemCount()), 0);
    }

    public void destroyView() {
        BombusQD.sd.roster.reEnumRoster();
        super.destroyView();
    }

    public String touchLeftCommand() {
        if (contacts.isEmpty()) {
            return null;
        }
        return SR.get(SR.MS_MENU);
    }

    public String touchRightCommand() {
        return SR.get(SR.MS_BACK);
    }
}
