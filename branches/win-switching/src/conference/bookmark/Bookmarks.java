/*
 * Bookmarks.java
 *
 * Created on 18.09.2005, 0:03
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

package conference.bookmark;
import conference.ConferenceForm;
import conference.QueryConfigForm;
import conference.affiliation.AffiliationItem;
import conference.affiliation.AffiliationList;
//#ifdef SERVICE_DISCOVERY
import disco.ServiceDiscovery;
//#endif
//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.CommandListener;
//# import javax.microedition.lcdui.Command;
//#else
import menu.MenuListener;
import menu.Command;
//#endif
import javax.microedition.lcdui.Displayable;
import locale.SR;
import ui.*;
import java.util.*;
import ui.MainBar;
import ui.controls.AlertBox;
//#ifdef GRAPHICS_MENU
import ui.GMenu;
//#endif
/**
 *
 * @author EvgS,aqent
 */
public final class Bookmarks extends VirtualList
        implements
//#ifndef MENU_LISTENER
//#         CommandListener
//#else
        MenuListener
//#endif
    {

    private Command cmdCancel;
    private Command cmdJoin;
    private Command cmdAdvJoin;
    private Command cmdDoAutoJoin;
    private Command cmdNew;
    private Command cmdConfigure;
//#ifdef SERVICE_DISCOVERY
    private Command cmdDisco;
//#endif
    private Command cmdUp;
    private Command cmdDwn;
    private Command cmdSort;
    private Command cmdSave;
    private Command cmdRoomOwners;
    private Command cmdRoomAdmins;
    private Command cmdRoomMembers;
    private Command cmdRoomBanned;
    private Command cmdDel;
    private Command cmdMyService;


    private BookmarkItem toAdd;

    public Bookmarks(BookmarkItem toAdd) {
        super ();
        if (getItemCount()==0 && toAdd==null) {
            new ConferenceForm().show();
            return;
        }

        this.toAdd=toAdd;

        if (toAdd!=null)
            addBookmark();

        setMainBarItem(new MainBar(2, null, SR.get(SR.MS_BOOKMARKS)+" ("+getItemCount()+") ", false));//for title updating after "add bookmark"

        initCommands();//fix

        setCommandListener(this);
    }

    public void initCommands() {
        cmdCancel = new Command(SR.get(SR.MS_CANCEL), Command.BACK, 99);

        cmdJoin = new Command(SR.get(SR.MS_SELECT), Command.OK, 1);
        cmdJoin.setImg(0x60);

        cmdAdvJoin = new Command(SR.get(SR.MS_EDIT_JOIN), Command.SCREEN, 2);
        cmdAdvJoin.setImg(0x61);

        cmdDoAutoJoin = new Command(SR.get(SR.MS_DO_AUTOJOIN), Command.SCREEN, 3);
        cmdDoAutoJoin.setImg(0x60);

        cmdNew = new Command(SR.get(SR.MS_NEW_BOOKMARK), Command.SCREEN, 4);
        cmdNew.setImg(0x62);

        cmdConfigure = new Command(SR.get(SR.MS_CONFIG_ROOM), Command.SCREEN, 5);
        cmdConfigure.setImg(0x72);

//#ifdef SERVICE_DISCOVERY
        cmdDisco = new Command(SR.get(SR.MS_DISCO_ROOM), Command.SCREEN, 6);
        cmdDisco.setImg(0x65);
//#endif
        cmdUp = new Command(SR.get(SR.MS_MOVE_UP), Command.SCREEN, 7);
        cmdUp.setImg(0x45);

        cmdDwn = new Command(SR.get(SR.MS_MOVE_DOWN), Command.SCREEN, 8);
        cmdDwn.setImg(0x46);

        cmdSort = new Command(SR.get(SR.MS_SORT), Command.SCREEN, 9);
        cmdSort.setImg(0x64);

        cmdSave = new Command(SR.get(SR.MS_SAVE_LIST), Command.SCREEN, 10);
        cmdSave.setImg(0x44);

        cmdRoomOwners = new Command(SR.get(SR.MS_OWNERS), Command.SCREEN, 11);
        cmdRoomOwners.setImg(0x66);

        cmdRoomAdmins = new Command(SR.get(SR.MS_ADMINS), Command.SCREEN, 12);
        cmdRoomAdmins.setImg(0x67);

        cmdRoomMembers = new Command(SR.get(SR.MS_MEMBERS), Command.SCREEN, 13);
        cmdRoomMembers.setImg(0x70);

        cmdRoomBanned = new Command(SR.get(SR.MS_BANNED), Command.SCREEN, 14);
        cmdRoomBanned.setImg(0x71);

        cmdDel = new Command(SR.get(SR.MS_DELETE), Command.SCREEN, 15);
        cmdDel.setImg(0x41);

        cmdMyService = new Command(SR.get(SR.MS_SERVICE), Command.SCREEN, 31);
        cmdMyService.setImg(0x27);
    }

    public void commandState() {
//#ifdef MENU_LISTENER
        menuCommands.removeAllElements();
        cmdfirstList.removeAllElements();
        cmdsecondList.removeAllElements();
        cmdThirdList.removeAllElements();
//#endif
        boolean notEmpty = (getItemCount() > 0);

        if (notEmpty) {
            addCommand(cmdJoin);
            addCommand(cmdDoAutoJoin);
            addCommand(cmdAdvJoin);
        }

        addCommand(cmdNew);

        if (notEmpty) {
            addCommand(cmdUp);
            addCommand(cmdDwn);
            addCommand(cmdSave);

            addCommand(cmdMyService);
            addInCommand(3, cmdSort);
            addInCommand(3, cmdDel);
            addInCommand(3, cmdRoomOwners);
            addInCommand(3, cmdRoomAdmins);
            addInCommand(3, cmdRoomMembers);
            addInCommand(3, cmdRoomBanned);
            addInCommand(3, cmdConfigure);
    //#ifdef SERVICE_DISCOVERY
            addCommand(cmdDisco);
    //#endif
        }
//#ifndef GRAPHICS_MENU
//#      addCommand(cmdCancel);
//#endif
    }

    protected int getItemCount() {
        Vector bookmarks = midlet.BombusQD.sd.roster.bookmarks;
        return (bookmarks == null) ? 0 : bookmarks.size();
    }

    protected VirtualElement getItemRef(int index) {
        return (VirtualElement) midlet.BombusQD.sd.roster.bookmarks.elementAt(index);
    }

    private void addBookmark() {
        if (toAdd != null) {
            Vector bm=midlet.BombusQD.sd.roster.bookmarks;
            bm.addElement(toAdd);
            saveBookmarks();
        }
    }

    public void eventOk(){
      try{
          Object obj = getFocusedObject();
          if(null != obj){
              BookmarkItem join=(BookmarkItem)getFocusedObject();
              ConferenceForm.join(join.getDesc(), join.getJidNick(), join.getPassword(), midlet.BombusQD.cf.confMessageCount);
          }
       } catch (Exception e) { }
       midlet.BombusQD.sd.roster.show(); //N78 hardfix
    }

    public void commandAction(Command c, Displayable d){
        if (c == cmdCancel) {
            destroyView();
        } else if (c == cmdNew) {
            new ConferenceForm().show();
            return;
        } else if (c == cmdJoin) {
            eventOk();
        }

	if (getItemCount() == 0) {
            return;
        }

        String roomJid = ((BookmarkItem)getFocusedObject()).getJid();

        if (c == cmdAdvJoin) {
            BookmarkItem join = (BookmarkItem)getFocusedObject();
            new ConferenceForm(join).show();
        } else if (c == cmdDel) {
            deleteBookmark();
            setMainBarItem(new MainBar(2, null, SR.get(SR.MS_BOOKMARKS) + " (" + getItemCount() + ") ", false));
            return;
        }
//#ifdef SERVICE_DISCOVERY
        else if (c == cmdDisco) {
            new ServiceDiscovery(roomJid, null, false).show();
        }
//#endif
        else if (c == cmdConfigure) {
            new QueryConfigForm(roomJid);
        } else if (c == cmdRoomOwners) {
            new AffiliationList(roomJid, AffiliationItem.AFFILIATION_OWNER).show();
        } else if (c == cmdRoomAdmins) {
            new AffiliationList(roomJid, AffiliationItem.AFFILIATION_ADMIN).show();
        } else if (c == cmdRoomMembers) {
            new AffiliationList(roomJid, AffiliationItem.AFFILIATION_MEMBER).show();
        } else if (c == cmdRoomBanned) {
            new AffiliationList(roomJid, AffiliationItem.AFFILIATION_OUTCAST).show();
        } else if (c == cmdSort) {
            sort(midlet.BombusQD.sd.roster.bookmarks);
        } else if (c == cmdDoAutoJoin) {
            for (Enumeration e = midlet.BombusQD.sd.roster.bookmarks.elements(); e.hasMoreElements();) {
                BookmarkItem bm = (BookmarkItem)e.nextElement();
                if (bm.isAutoJoin()) {
                    ConferenceForm.join(bm.getDesc(), bm.getJidNick(), bm.getPassword(), midlet.BombusQD.cf.confMessageCount);
                }
            }
            midlet.BombusQD.sd.roster.show();
        } else if (c == cmdSave) {
            saveBookmarks();
        } else if (c == cmdUp) {
            move(-1);
            keyUp();
        } else if (c == cmdDwn) {
            move(+1);
            keyDwn();
        }
        redraw();
    }

    private void deleteBookmark() {
        BookmarkItem del = (BookmarkItem)getFocusedObject();
        if (del == null) {
            return;
        }
        if (del.isURL()) {
            return;
        }

        midlet.BombusQD.sd.roster.bookmarks.removeElementAt(cursor);
        if (getItemCount() <= cursor) {
            moveCursorEnd();
        }
        saveBookmarks();
        redraw();
    }

    private void saveBookmarks() {
        new BookmarkQuery(BookmarkQuery.SAVE);
    }

    public void move(int offset){
        try {
            int index=cursor;
            BookmarkItem p1=(BookmarkItem)getItemRef(index);
            BookmarkItem p2=(BookmarkItem)getItemRef(index+offset);

            midlet.BombusQD.sd.roster.bookmarks.setElementAt(p1, index+offset);
            midlet.BombusQD.sd.roster.bookmarks.setElementAt(p2, index);
        } catch (Exception e) {}
    }

    public void keyPressed(int keyCode) {
//#ifdef SERVICE_DISCOVERY
        if (keyCode == KEY_POUND) {
            new ServiceDiscovery(((BookmarkItem)getFocusedObject()).getJid() , null, false).show();
            return;
        }
//#endif
        super.keyPressed(keyCode);
    }

    protected void keyClear(){
        AlertBox box = new AlertBox(SR.get(SR.MS_DELETE_ASK), ((BookmarkItem)getFocusedObject()).getJid(), false) {
            public void yes() {
                deleteBookmark();
            }
            public void no() {}
        };
        box.show();
    }

//#ifdef MENU_LISTENER
//#ifdef GRAPHICS_MENU
    public int showGraphicsMenu() {
        commandState();
        menuItem = new GMenu(this, null, menuCommands, cmdfirstList, cmdsecondList, cmdThirdList);
        GMenuConfig.getInstance().itemGrMenu=GMenu.BOOKMARKS;
        return GMenu.BOOKMARKS;
    }
//#else
//#     public void showMenu() {
//#         commandState();
//#         new MyMenu(display, parentView, this, SR.get(SR.MS_BOOKMARKS), null, menuCommands);
//#     }
//#endif
//#endif
}
