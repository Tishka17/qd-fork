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
import menu.MyMenu;
//#endif
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
import ui.MainBar;
import ui.VirtualElement;
import ui.VirtualList;
//#ifdef GRAPHICS_MENU        
//# import ui.GMenu;
//# import ui.GMenuConfig;
//#endif   
import conference.ConferenceGroup;
/**
 *
 * @author EvgS,aqent
 */
public final class ActiveContacts 
    extends VirtualList
    implements
//#ifndef MENU_LISTENER
//#         CommandListener
//#else
        MenuListener
//#endif
{
    
    Vector activeContacts = new Vector(0);
    private Display display;
    private MainBar mainbar;
    private Displayable parentView;
    
    private int sortType = -1;
    public static boolean isActive = false;
    
    public void initCommands(){
        cmdCancel=new Command(SR.get(SR.MS_BACK), Command.BACK, 99);
        cmdOk=new Command(SR.get(SR.MS_SELECT), Command.SCREEN, 1);
        cmdCreateMultiMessage=new Command(SR.get(SR.MS_MULTI_MESSAGE), Command.SCREEN, 3);
        cmdSortType=new Command(SR.get(SR.MS_SORT_TYPE), Command.SCREEN, 4);
        cmdSortDefault=new Command(SR.get(SR.MS_SORT_TYPE_DEF), Command.SCREEN, 5);
        cmdSortByStatus=new Command(SR.get(SR.MS_SORT_TYPE_STATUS), Command.SCREEN, 6);
        cmdSortByMsgsCount=new Command(SR.get(SR.MS_SORT_TYPE_MSGS), Command.SCREEN, 7);
        cmdClearAllMessages=new Command(SR.get(SR.MS_CLEAN_ALL_MESSAGES), Command.SCREEN, 35);
    }

    private Command cmdCancel;
    private Command cmdOk;
    private Command cmdCreateMultiMessage;
    private Command cmdSortType;
    private Command cmdSortDefault;
    private Command cmdSortByStatus;
    private Command cmdSortByMsgsCount;
    private Command cmdClearAllMessages;
    
    private long lasttime = 0;
    private long current = 0;
    private Contact opened;
    
    public void sort(){
        current = System.currentTimeMillis();
        if(lasttime==0) lasttime = System.currentTimeMillis() + 10000;
        //System.out.println( (current - lasttime) );
        if( (current - lasttime) < 500 ) { //0.5sec
          lasttime = current;
          return;
        }        
        switch(sortType) {
            case -1: break;
            case 0: sort(activeContacts, 0, 0); break;//byStatus
            case 1: sort(activeContacts, 0, 1); break;//byMsgsCount
        }
        lasttime = current;
    }
    
    public boolean setActiveContacts(Displayable pView, Contact current){
        this.parentView=pView;
        this.opened = current;
        
        commandState();
        activeContacts = new Vector(0);
        Contact c = null;
        
        Vector hContacts = midlet.BombusQD.sd.roster.getHContacts();
        int size=hContacts.size();   
        for(int i=0;i<size;i++){    
           c=(Contact)hContacts.elementAt(i);
           if (c.active()) activeContacts.addElement(c);
        }
        if (getItemCount()==0) return false;
        //System.out.println(activeContacts.toString());

        isActive = true;
        sort();
        mainbar.setElementAt( Integer.toString(getItemCount()) , 0 );
	try {
           focusToContact(current);
           hContacts = null;
        } catch (Exception e) { }
        return true;
    }
    
    
    /** Creates a new instance of ActiveContacts */
    public ActiveContacts(Display display, Displayable pView) {
	//super();
        this.display = display;

        mainbar=new MainBar(2, String.valueOf(getItemCount()), " ", false);
        mainbar.addElement(SR.get(SR.MS_ACTIVE_CONTACTS));
        setMainBarItem(mainbar);
    }
    
    public void commandState() {
//#ifdef MENU_LISTENER
        menuCommands.removeAllElements();
        cmdfirstList.removeAllElements();
//#endif
        if(cmdOk == null) initCommands();
        addCommand(cmdOk); cmdOk.setImg(0x43);
        addCommand(cmdCreateMultiMessage); cmdCreateMultiMessage.setImg(0x81);
        addCommand(cmdSortType); cmdSortType.setImg(0x64);
          addInCommand(1,cmdSortDefault); cmdSortDefault.setImg(0x64);
          addInCommand(1,cmdSortByStatus); cmdSortByStatus.setImg(0x64);
          addInCommand(1,cmdSortByMsgsCount); cmdSortByMsgsCount.setImg(0x64);
        addCommand(cmdClearAllMessages); cmdClearAllMessages.setImg(0x41);
    }
    
//#ifdef MENU_LISTENER
    
//#ifdef GRAPHICS_MENU        
//#     public int showGraphicsMenu() {
//#         menuItem = new GMenu(display, parentView, this, null, menuCommands, cmdfirstList, null, null);  
//#         GMenuConfig.getInstance().itemGrMenu = GMenu.ACTIVE_CONTACTS;        
//#         return GMenu.ACTIVE_CONTACTS;
//#     }
//#else
    public void showMenu() { eventOk();}   
//#endif      

//#endif

    protected int getItemCount() { 
        if(activeContacts==null) return 0;
        return activeContacts.size();
    }
    protected VirtualElement getItemRef(int index) { 
	return (VirtualElement) activeContacts.elementAt(index);
    }

    public void eventOk() {
	Contact c=(Contact)getFocusedObject();
        isActive = false;
         if(Config.getInstance().module_classicchat) 
             new SimpleItemChat(display,midlet.BombusQD.sd.roster,(Contact)c);            
         else
             display.setCurrent(c.getMessageList());
    }

    public void commandAction(Command c, Displayable d) {
	if (c==cmdCancel) destroyView();
	if (c==cmdOk) eventOk();
        if (c==cmdCreateMultiMessage) {
            isActive = false;
            midlet.BombusQD.sd.roster.createMultiMessage(this,activeContacts);
        }
        if (c==cmdClearAllMessages) {
            midlet.BombusQD.sd.roster.cmdCleanAllMessages();
        }
        if (c==cmdSortDefault) {
           sortType = -1;
           //sort(activeContacts);
        }
        if (c==cmdSortByStatus) {
           sortType = 0;
           sort();
        }
        if (c==cmdSortByMsgsCount) {
           sortType = 1;
           sort();
        }
    }


    public void keyPressed(int keyCode) {
        kHold=0;
//#ifdef POPUPS
        VirtualList.popup.next();
//#endif
        sort();
	if (keyCode==KEY_NUM3) {
            destroyView();
        } else if (keyCode==KEY_NUM0) {
            if (getItemCount()<1)
                return;

            Contact c=(Contact)getFocusedObject();

            Enumeration i=activeContacts.elements();
            
            int pass=0;
            while (pass<2) {
                if (!i.hasMoreElements()) i=activeContacts.elements();
                Contact p=(Contact)i.nextElement();
                if (pass==1) 
                    if (p.getNewMsgsCount()>0) { 
                        focusToContact(p);
                        setRotator();
                        break; 
                    }
                if (p==c) pass++;
            }
            return;
        } else super.keyPressed(keyCode);
    }
    
    private void focusToContact(final Contact c) {
        int index=activeContacts.indexOf(c);
        if (index>=0) 
            moveCursorTo(index);
    }
    
    protected void keyGreen(){
        eventOk();
    }
    
    protected void keyClear () {
       Contact c = (Contact)getFocusedObject();
       try{
          c.purge();
           activeContacts.removeElementAt(cursor);
           mainbar.setElementAt(Integer.toString(getItemCount()), 0);
          c = null;
       } catch (Exception e){
//#ifdef CONSOLE 
//#           if(midlet.BombusQD.cf.debug) midlet.BombusQD.debug.add("::ActiveContacts->Exception->"+c,10);
//#endif
       }
    }
    
    public void destroyView() {
        isActive = false;
        midlet.BombusQD.sd.roster.reEnumRoster();
        if(null == parentView || !activeContacts.contains(opened) ) midlet.BombusQD.sd.roster.showRoster();
        else display.setCurrent(parentView);
    }
//#ifdef MENU_LISTENER
    public String touchLeftCommand(){ return SR.get(SR.MS_SELECT); }
    public String touchRightCommand(){ return SR.get(SR.MS_BACK); }
//#endif
}
