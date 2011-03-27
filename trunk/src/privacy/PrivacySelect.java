/*
 * PrivacySelect.java
 *
 * Created on 26.08.2005, 23:04
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
//#ifdef PRIVACY
package privacy;

import client.StaticData;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextField;
import images.RosterIcons;
//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.CommandListener;
//# import javax.microedition.lcdui.Command;
//#else
import menu.MenuListener;
import menu.Command;
//#endif
import locale.SR;
import ui.*;
import java.util.*;
import com.alsutton.jabber.*;
//#ifdef GRAPHICS_MENU        
import ui.GMenu;
//#endif 
/**
 *
 * @author EvgS,aqent
 */
public class PrivacySelect 
        extends VirtualList 
        implements
//#ifndef MENU_LISTENER
//#         CommandListener,
//#else
        MenuListener,
//#endif
        JabberBlockListener,
        InputTextBoxNotify
{
//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_PRIVACY");
//#endif
    
    private Vector list=new Vector(0);
    
    private Command cmdCancel;
    private Command cmdActivate;
    private Command cmdDefault;
    private Command cmdNewList;
    private Command cmdDelete;
    private Command cmdIL;
    
    JabberStream stream=StaticData.getInstance().roster.theStream;
    
    /** Creates a new instance of PrivacySelect */
    public PrivacySelect(Display display, Displayable pView) {
        super(display);
        
       cmdCancel=new Command (SR.get(SR.MS_BACK), Command.BACK, 99);
       cmdActivate=new Command (SR.get(SR.MS_ACTIVATE), Command.SCREEN, 10);
       cmdDefault=new Command (SR.get(SR.MS_SETDEFAULT), Command.SCREEN, 11);
       cmdNewList=new Command (SR.get(SR.MS_NEW_LIST), Command.SCREEN, 12);
       cmdDelete=new Command (SR.get(SR.MS_DELETE_LIST), Command.SCREEN, 13);
       cmdIL=new Command (SR.get(SR.MS_MK_ILIST), Command.SCREEN, 16);
    
        this.parentView=pView;

        setMainBarItem(new MainBar(2, null, SR.get(SR.MS_PRIVACY_LISTS), false));

        list.addElement(new PrivacyList(null));//none

        setCommandListener(this);
        
        getLists();
        
        //attachDisplay(display);        
    }
    
    public void commandState() {
//#ifdef MENU_LISTENER
        menuCommands.removeAllElements();
//#endif
        addCommand(cmdActivate); cmdActivate.setImg(0x16);
        addCommand(cmdDefault); cmdDefault.setImg(0x24);
//#ifndef GRAPHICS_MENU        
//#      addCommand(cmdCancel);
//#endif     
        addCommand(cmdNewList); cmdNewList.setImg(0x13);
        addCommand(cmdDelete); cmdDelete.setImg(0x41);
        addCommand(cmdIL); cmdIL.setImg(0x23);
    }

    private void processIcon(boolean processing){
        getMainBarItem().setElementAt((processing)?(Object)new Integer(RosterIcons.ICON_PROGRESS_INDEX):(Object)null, 0);
        redraw();
    }
   
    private void getLists(){
        stream.addBlockListener(this);
        processIcon(true);
        PrivacyList.privacyListRq(false, null, "getplists");
    }
    
    protected int getItemCount() { return list.size(); }
    protected VirtualElement getItemRef(int index) { return (VirtualElement) list.elementAt(index); }
    
    public void commandAction(Command c, Displayable d) {
        if (c==cmdCancel) {
            destroyView();
            stream.cancelBlockListener(this);
            return;
        }
        if (c==cmdActivate || c==cmdDefault) {
            PrivacyList active=((PrivacyList)getFocusedObject());
            for (Enumeration e=list.elements(); e.hasMoreElements(); ) {
                PrivacyList pl=(PrivacyList)e.nextElement();
                boolean state=(pl==active);
                if (c==cmdActivate)
                    pl.isActive=state;
                else
                    pl.isDefault=state;
            }
            ((PrivacyList)getFocusedObject()).activate( (c==cmdActivate)? "active":"default" ); 
            getLists();
        }
        if (c==cmdIL) {
            generateIgnoreList();
            getLists();
            destroyView();
        }
        if (c==cmdDelete) {
            PrivacyList pl=(PrivacyList) getFocusedObject();
            if (pl!=null) {
                if (pl.name!=null)
                        pl.deleteList();
                getLists();
            }
        }
        if (c==cmdNewList) {
            InputTextBox input = new InputTextBox(SR.get(SR.MS_NEW), null, 50, TextField.ANY);
            input.setNotifyListener(this);
            input.show();
        }
    }
    
//#ifdef MENU_LISTENER
    
//#ifdef GRAPHICS_MENU        
    public int showGraphicsMenu() {
        commandState();
        menuItem = new GMenu(display, parentView, this, null, menuCommands);
        GMenuConfig.getInstance().itemGrMenu = GMenu.PRIVACY_SELECT;         
        return GMenu.PRIVACY_SELECT;
    }
//#else
//#     public void showMenu() {
//#         commandState();
//#         new MyMenu(display, parentView, this, SR.get(SR.MS_STATUS), null, menuCommands);
//#     }   
//#endif    

//#endif

    public void okNotify(String listName) {
        if (listName.length()>0)
            new PrivacyModifyList(display, this, new PrivacyList(listName));
    }
    
    public int blockArrived(JabberDataBlock data){
        try {
            if (data.getTypeAttribute().equals("result"))
                if (data.getAttribute("id").equals("getplists")) {
                data=data.findNamespace("query", "jabber:iq:privacy");
                if (data!=null) {
                    list=null;
                    list=new Vector(0);
                    String activeList="";
                    String defaultList="";
                    try {
                        int size = data.getChildBlocks().size();
                        for(int i=0;i<size;i++){  
                           JabberDataBlock pe = (JabberDataBlock)data.getChildBlocks().elementAt(i);
                            String tag=pe.getTagName();
                            String name=pe.getAttribute("name");
                            if (tag.equals("active")) activeList=name;
                            if (tag.equals("default")) defaultList=name;
                            if (tag.equals("list")) {
                                PrivacyList pl=new PrivacyList(name);
                                pl.isActive=(name.equals(activeList));
                                pl.isDefault=(name.equals(defaultList));
                                list.addElement(pl);
                            }
                        }
                    } catch (Exception e) {}
                    PrivacyList nullList=new PrivacyList(null);
                    nullList.isActive=activeList.length()==0;
                    nullList.isDefault=defaultList.length()==0;
                    list.addElement(nullList);//none
                }
                
                processIcon(false);
                
                return JabberBlockListener.NO_MORE_BLOCKS;
                }
        } catch (Exception e) { }
        return JabberBlockListener.BLOCK_REJECTED;
    }

    public void eventOk(){
        PrivacyList pl=(PrivacyList) getFocusedObject();
        if (pl!=null) {
            if (pl.name!=null) new PrivacyModifyList(display, this, pl);
        }
    }
    private void generateIgnoreList(){
        JabberDataBlock ignoreList=new JabberDataBlock("list", null, null);
        ignoreList.setAttribute("name", SR.get(SR.MS_IGNORE_LIST));
        JabberDataBlock item=PrivacyItem.itemIgnoreList().constructBlock();
        ignoreList.addChild(item);
        PrivacyList.privacyListRq(true, ignoreList, "ignlst");
    }
}
//#endif
