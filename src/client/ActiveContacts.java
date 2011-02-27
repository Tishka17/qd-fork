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
//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.CommandListener;
//# import javax.microedition.lcdui.Command;
//#else
import menu.MenuListener;
import menu.Command;
//#endif
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
import midlet.BombusQD;
import ui.MainBar;
import ui.VirtualElement;
import ui.VirtualList;
//#ifdef GRAPHICS_MENU        
//# import ui.GMenu;
//# import ui.GMenuConfig;
//#endif

/**
 *
 * @author EvgS,aqent
 */

public final class ActiveContacts extends VirtualList implements
//#ifndef MENU_LISTENER
//#         CommandListener
//#else
        MenuListener
//#endif
{

    private static final int SORT_BY_STATUS = 0;
    private static final int SORT_BY_MSGCOUNT = 1;  
    
    private Vector contacts = new Vector();

    public static boolean isActive = false;
     
    private Command cmdCancel;
    private Command cmdOk;
    private Command cmdCreateMultiMessage;
    private Command cmdSortType;
    private Command cmdSortDefault;
    private Command cmdSortByStatus;
    private Command cmdSortByMsgsCount;
    private Command cmdClearAllMessages;

    public ActiveContacts(Display display, Displayable pView, Contact current) {
        cmdCancel = new Command(SR.get(SR.MS_BACK), Command.BACK, 99);
        cmdOk = new Command(SR.get(SR.MS_SELECT), Command.SCREEN, 1);
        cmdCreateMultiMessage = new Command(SR.get(SR.MS_MULTI_MESSAGE), Command.SCREEN, 3);
        cmdSortType = new Command(SR.get(SR.MS_SORT_TYPE), Command.SCREEN, 4);
        cmdSortDefault = new Command(SR.get(SR.MS_SORT_TYPE_DEF), Command.SCREEN, 5);
        cmdSortByStatus = new Command(SR.get(SR.MS_SORT_TYPE_STATUS), Command.SCREEN, 6);
        cmdSortByMsgsCount = new Command(SR.get(SR.MS_SORT_TYPE_MSGS), Command.SCREEN, 7);
        cmdClearAllMessages = new Command(SR.get(SR.MS_CLEAN_ALL_MESSAGES), Command.SCREEN, 35);
        
        Vector hContacts = BombusQD.sd.roster.getHContacts();
        int size = hContacts.size();
        for (int i = 0; i < size; ++i) {
           Contact c = (Contact)hContacts.elementAt(i);
           if (c.active()) {
               contacts.addElement(c);
           }
        }
        
        if (contacts.contains(current)) {
            focusToContact(current);
        }
        
        setMainBarItem(new MainBar(SR.get(SR.MS_ACTIVE_CONTACTS) + " (" + getItemCount() + ")"));

        commandState();

        attachDisplay(display);
        super.parentView = pView;
    }
    
    private void sortContacts(int type) {
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
//#ifdef MENU_LISTENER
        menuCommands.removeAllElements();
        cmdfirstList.removeAllElements();
//#endif
        addCommand(cmdOk);
        cmdOk.setImg(0x43);

        addCommand(cmdCreateMultiMessage);
        cmdCreateMultiMessage.setImg(0x81);

        addCommand(cmdSortType);
        cmdSortType.setImg(0x64);

        addInCommand(1, cmdSortDefault);
        cmdSortDefault.setImg(0x64);

        addInCommand(1, cmdSortByStatus);
        cmdSortByStatus.setImg(0x64);

        addInCommand(1, cmdSortByMsgsCount);
        cmdSortByMsgsCount.setImg(0x64);

        addCommand(cmdClearAllMessages);
        cmdClearAllMessages.setImg(0x41);
    }
    
//#ifdef MENU_LISTENER
    
//#ifdef GRAPHICS_MENU        
//#     public int showGraphicsMenu() {
//#         if (contacts.isEmpty()) {
//#             return 0;
//#         }
//#         menuItem = new GMenu(display, parentView, this, null, menuCommands, cmdfirstList, null, null);  
//#         GMenuConfig.getInstance().itemGrMenu = GMenu.ACTIVE_CONTACTS;        
//#         return GMenu.ACTIVE_CONTACTS;
//#     }
//#else
    public void showMenu() { eventOk();}   
//#endif      

//#endif

    protected int getItemCount() {
        return contacts.size();
    }

    protected VirtualElement getItemRef(int index) {
        return (VirtualElement) contacts.elementAt(index);
    }

    public void eventOk() {
        Contact contact = (Contact) getFocusedObject();
        isActive = false;
        if (Config.getInstance().module_classicchat) {
            new SimpleItemChat(display, BombusQD.sd.roster, contact);
        } else {
            display.setCurrent(contact.getMessageList());
        }
    }

    public void commandAction(Command c, Displayable d) {
        if (c == cmdCancel) {
            destroyView();
        }
        if (c == cmdOk) {
            eventOk();
        }
        if (c == cmdCreateMultiMessage) {
            BombusQD.sd.roster.createMultiMessage(this, contacts);
        }
        if (c == cmdClearAllMessages) {
            BombusQD.sd.roster.cmdCleanAllMessages();
        }
        if (c == cmdSortByStatus) {
            sortContacts(SORT_BY_STATUS);
        }
        if (c == cmdSortByMsgsCount) {
            sortContacts(SORT_BY_STATUS);
        }
    }

    public void keyPressed(int keyCode) {
        kHold = 0;
//#ifdef POPUPS
        VirtualList.popup.next();
//#endif
        if (keyCode == KEY_NUM3) {
            destroyView();
        } else if (keyCode == KEY_NUM0) {
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
                    if (p.getNewMsgsCount() > 0) {
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
    
    protected void keyGreen(){
        eventOk();
    }
    
    protected void keyClear () {
        Contact contact = (Contact) getFocusedObject();

        contact.purge();
        contacts.removeElementAt(cursor);
        mainbar.setElementAt(Integer.toString(getItemCount()), 0);
    }
    
    public void destroyView() {
        BombusQD.sd.roster.reEnumRoster();
        display.setCurrent(parentView);
    }

//#ifdef MENU_LISTENER
    public String touchLeftCommand() {
        if (contacts.isEmpty()) {
            return null;
        }
        return SR.get(SR.MS_MENU);
    }

    public String touchRightCommand() {
        return SR.get(SR.MS_BACK);
    }
//#endif
}
