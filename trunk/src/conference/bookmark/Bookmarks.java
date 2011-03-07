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
import conference.bookmark.BookmarkItem;
import conference.bookmark.BookmarkQuery;
import conference.affiliation.Affiliations;
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
import javax.microedition.lcdui.Display;
import locale.SR;
import ui.*;
import java.util.*;
import ui.MainBar;
import ui.controls.AlertBox;
//#ifdef GRAPHICS_MENU
//# import ui.GMenu;
//#endif
/**
 *
 * @author EvgS,aqent
 */
public final class Bookmarks
        extends VirtualList
        implements
//#ifndef MENU_LISTENER
//#         CommandListener
//#else
        MenuListener
//#endif
    {


    public void initCommands() {
          cmdCancel=new Command (SR.get(SR.MS_CANCEL), Command.BACK, 99);
          cmdJoin=new Command (SR.get(SR.MS_SELECT), Command.OK, 1);
          cmdAdvJoin=new Command (SR.get(SR.MS_EDIT_JOIN), Command.SCREEN, 2);
          cmdDoAutoJoin=new Command(SR.get(SR.MS_DO_AUTOJOIN), Command.SCREEN, 3);
          cmdNew=new Command (SR.get(SR.MS_NEW_BOOKMARK), Command.SCREEN, 4);
          cmdConfigure=new Command (SR.get(SR.MS_CONFIG_ROOM), Command.SCREEN, 5);
//#ifdef SERVICE_DISCOVERY
          cmdDisco=new Command (SR.get(SR.MS_DISCO_ROOM), Command.SCREEN, 6);
//#endif
          cmdUp=new Command (SR.get(SR.MS_MOVE_UP), Command.SCREEN, 7);
          cmdDwn=new Command (SR.get(SR.MS_MOVE_DOWN), Command.SCREEN, 8);
          cmdSort=new Command (SR.get(SR.MS_SORT), Command.SCREEN, 9);
          cmdSave=new Command (SR.get(SR.MS_SAVE_LIST), Command.SCREEN, 10);
          cmdRoomOwners=new Command (SR.get(SR.MS_OWNERS), Command.SCREEN, 11);
          cmdRoomAdmins=new Command (SR.get(SR.MS_ADMINS), Command.SCREEN, 12);
          cmdRoomMembers=new Command (SR.get(SR.MS_MEMBERS), Command.SCREEN, 13);
          cmdRoomBanned=new Command (SR.get(SR.MS_BANNED), Command.SCREEN, 14);
          cmdDel=new Command (SR.get(SR.MS_DELETE), Command.SCREEN, 15);
          cmdMyService=new Command(SR.get(SR.MS_SERVICE), Command.SCREEN, 31);
          commandState();
    }


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

    //JabberStream stream=sd.roster.theStream;
    /** Creates a new instance of Bookmarks */
    public Bookmarks(Display display, Displayable pView, BookmarkItem toAdd) {
        super ();
        if (getItemCount()==0 && toAdd==null) {
            new ConferenceForm(display, pView);
            return;
        }

        this.toAdd=toAdd;

        if (toAdd!=null)
            addBookmark();

        setMainBarItem(new MainBar(2, null, SR.get(SR.MS_BOOKMARKS)+" ("+getItemCount()+") ", false));//for title updating after "add bookmark"

        initCommands();//fix

        setCommandListener(this);
	attachDisplay(display);
        this.parentView=pView;
    }

    public void commandState() {
//#ifdef MENU_LISTENER
        menuCommands.removeAllElements();
        cmdfirstList.removeAllElements();
        cmdsecondList.removeAllElements();
        cmdThirdList.removeAllElements();
//#endif
        addCommand(cmdJoin); cmdJoin.setImg(0x60);
        addCommand(cmdDoAutoJoin); cmdDoAutoJoin.setImg(0x60);
        addCommand(cmdAdvJoin); cmdAdvJoin.setImg(0x61);
	addCommand(cmdNew); cmdNew.setImg(0x62);
        addCommand(cmdUp); cmdUp.setImg(0x45);
        addCommand(cmdDwn); cmdDwn.setImg(0x46);
        addCommand(cmdSave); cmdSave.setImg(0x44);

       addCommand(cmdMyService); cmdMyService.setImg(0x27);
        addInCommand(3,cmdSort); cmdSort.setImg(0x64);
        addInCommand(3,cmdDel); cmdDel.setImg(0x41);
        addInCommand(3,cmdRoomOwners); cmdRoomOwners.setImg(0x66);
        addInCommand(3,cmdRoomAdmins); cmdRoomAdmins.setImg(0x67);
        addInCommand(3,cmdRoomMembers); cmdRoomMembers.setImg(0x70);
        addInCommand(3,cmdRoomBanned); cmdRoomBanned.setImg(0x71);
        addInCommand(3,cmdConfigure); cmdConfigure.setImg(0x72);
//#ifdef SERVICE_DISCOVERY
        addCommand(cmdDisco); cmdDisco.setImg(0x65);
//#endif
//#ifndef GRAPHICS_MENU
     addCommand(cmdCancel);
//#endif
    }

    protected int getItemCount() {
        Vector bookmarks=midlet.BombusQD.sd.roster.bookmarks;
        return (bookmarks==null)?0: bookmarks.size();
    }

    protected VirtualElement getItemRef(int index) {
        return (VirtualElement) midlet.BombusQD.sd.roster.bookmarks.elementAt(index);
    }

    public void loadBookmarks() {
    }

    private void addBookmark() {
        if (toAdd!=null) {
            Vector bm=midlet.BombusQD.sd.roster.bookmarks;
            bm.addElement(toAdd);
            //sort(bm);
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
       midlet.BombusQD.sd.roster.showRoster(); //N78 hardfix
    }

    public void commandAction(Command c, Displayable d){
        if (c==cmdCancel) exitBookmarks();
        if (c==cmdNew) {
            new ConferenceForm(display, this);
            return;
        }
        if (c==cmdJoin) eventOk();

	if (getItemCount()==0) return;
        String roomJid=((BookmarkItem)getFocusedObject()).getJid();

        if (c==cmdAdvJoin) {
            BookmarkItem join=(BookmarkItem)getFocusedObject();
            new ConferenceForm(display, this, join, cursor);
        } else if (c==cmdDel) {
            deleteBookmark();
            setMainBarItem(new MainBar(2, null, SR.get(SR.MS_BOOKMARKS)+" ("+getItemCount()+") ", false));
            return;
        }
//#ifdef SERVICE_DISCOVERY
        else if (c==cmdDisco) new ServiceDiscovery(display, roomJid, null, false);
//#endif
        else if (c==cmdConfigure) new QueryConfigForm(display,roomJid);
        else if (c==cmdRoomOwners) new Affiliations(display, this, roomJid, (short)1);
        else if (c==cmdRoomAdmins) new Affiliations(display, this, roomJid, (short)2);
        else if (c==cmdRoomMembers) new Affiliations(display, this, roomJid, (short)3);
        else if (c==cmdRoomBanned) new Affiliations(display, this, roomJid, (short)4);
        else if (c==cmdSort) sort(midlet.BombusQD.sd.roster.bookmarks);
        else if (c==cmdDoAutoJoin) {
            for (Enumeration e=midlet.BombusQD.sd.roster.bookmarks.elements(); e.hasMoreElements();) {
                BookmarkItem bm=(BookmarkItem) e.nextElement();
                if (bm.isAutoJoin()) {
                    ConferenceForm.join(bm.getDesc(), bm.getJidNick(), bm.getPassword(), midlet.BombusQD.cf.confMessageCount);
                }
            }
            midlet.BombusQD.sd.roster.showRoster();
        }

        else if (c==cmdSave) saveBookmarks();
        else if (c==cmdUp) { move(-1); keyUp(); }
        else if (c==cmdDwn) { move(+1); keyDwn(); }
        redraw();
    }

    private void deleteBookmark(){
        BookmarkItem del=(BookmarkItem)getFocusedObject();
        if (del==null)
            return;
        if (del.isURL())
            return;

        midlet.BombusQD.sd.roster.bookmarks.removeElementAt(cursor);
        if (getItemCount()<=cursor)
            moveCursorEnd();
        saveBookmarks();
        redraw();
    }

    private void saveBookmarks() {
        new BookmarkQuery(BookmarkQuery.SAVE);
    }

    private void exitBookmarks(){
        midlet.BombusQD.sd.roster.showRoster();
    }

    public void move(int offset){
        try {
            int index=cursor;
            BookmarkItem p1=(BookmarkItem)getItemRef(index);
            BookmarkItem p2=(BookmarkItem)getItemRef(index+offset);

            midlet.BombusQD.sd.roster.bookmarks.setElementAt(p1, index+offset);
            midlet.BombusQD.sd.roster.bookmarks.setElementAt(p2, index);
        } catch (Exception e) {/* IndexOutOfBounds */}
    }

    public void keyPressed(int keyCode) {
        if (keyCode==KEY_NUM4){
            pageLeft();
        }
        if (keyCode==KEY_NUM6){
            pageRight();
        }
//#ifdef SERVICE_DISCOVERY
        if (keyCode==KEY_POUND) {
            new ServiceDiscovery(display, ((BookmarkItem)getFocusedObject()).getJid() , null, false);
            return;
        }
//#endif
        super.keyPressed(keyCode);
    }

    protected void keyClear(){
        new AlertBox(SR.get(SR.MS_DELETE_ASK), ((BookmarkItem)getFocusedObject()).getJid(), display, this, false) {
            public void yes() {
                deleteBookmark();
            }
            public void no() {}
        };
    }

//#ifdef MENU_LISTENER

//#ifdef GRAPHICS_MENU
//#     public int showGraphicsMenu() {
//#         commandState();
//#         menuItem = new GMenu(display, parentView, this, null, menuCommands, cmdfirstList, cmdsecondList, cmdThirdList);
//#         GMenuConfig.getInstance().itemGrMenu=GMenu.BOOKMARKS;
//#         return GMenu.BOOKMARKS;
//#     }
//#else
    public void showMenu() {
        commandState();
        new MyMenu(display, parentView, this, SR.get(SR.MS_BOOKMARKS), null, menuCommands);
    }
//#endif

//#endif
}
