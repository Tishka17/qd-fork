/*
 * PrivacyModifyList.java
 *
 * Created on 11.09.2005, 15:51
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
import javax.microedition.lcdui.Displayable;
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

public class PrivacyModifyList extends VirtualList 
        implements
//#ifndef MENU_LISTENER
//#         CommandListener,
//#else
        MenuListener,
//#endif
        JabberBlockListener
{
//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_PRIVACY");
//#endif
    
    private PrivacyList plist;

    private Command cmdAdd;
    private Command cmdDel;
    private Command cmdEdit;
    private Command cmdUp;
    private Command cmdDwn;
    private Command cmdSave;
    
    JabberStream stream=StaticData.getInstance().roster.theStream;

    public PrivacyModifyList(PrivacyList privacyList) {
        super();

        cmdAdd=new Command (SR.get(SR.MS_ADD_RULE), Command.SCREEN, 10);
        cmdDel=new Command (SR.get(SR.MS_DELETE_RULE), Command.SCREEN, 11);
        cmdEdit=new Command (SR.get(SR.MS_EDIT_RULE), Command.SCREEN, 12);
        cmdUp=new Command (SR.get(SR.MS_MOVE_UP), Command.SCREEN, 13);
        cmdDwn=new Command (SR.get(SR.MS_MOVE_DOWN), Command.SCREEN, 14);
        cmdSave=new Command (SR.get(SR.MS_SAVE_LIST), Command.SCREEN, 16);
        
        setMainBarItem(new MainBar(2, null, SR.get(SR.MS_PRIVACY_LISTS), false));

        plist=privacyList;
        getList();
    }

    public void commandState() {
//#ifdef MENU_LISTENER
        menuCommands.removeAllElements();
//#endif
//#ifndef GRAPHICS_MENU        
//#      addCommand(cmdCancel);
//#endif     
        addCommand(cmdEdit); cmdEdit.setImg(0x40);//EDIT
        addCommand(cmdAdd); cmdAdd.setImg(0x47);
        addCommand(cmdDel); cmdDel.setImg(0x41);//DELETE
        addCommand(cmdUp); cmdUp.setImg(0x45);//UP
        addCommand(cmdDwn); cmdDwn.setImg(0x46);//DOWN 
        addCommand(cmdSave); cmdSave.setImg(0x44);//SAVE
    }
    
//#ifdef MENU_LISTENER
    
//#ifdef GRAPHICS_MENU        
    public int showGraphicsMenu() {
        commandState();
        menuItem = new GMenu(this, menuCommands);
        GMenuConfig.getInstance().itemGrMenu = GMenu.PRIVACY_MODIFY_LIST;        
        return GMenu.PRIVACY_MODIFY_LIST;
    }
//#else
//#     public void showMenu() {
//#         commandState();
//#         new MyMenu(display, parentView, this, SR.get(SR.MS_STATUS), null, menuCommands);
//#     }  
//#endif   

//#endif
    
    private void processIcon(boolean processing){
        getMainBarItem().setElementAt((processing)?(Object)new Integer(RosterIcons.ICON_PROGRESS_INDEX):(Object)null, 0);
        redraw();
    }

    private void getList(){
        processIcon(true);
        stream.addBlockListener(this);
        JabberDataBlock list=new JabberDataBlock("list", null, null);
        list.setAttribute("name", plist.name);
        PrivacyList.privacyListRq(false, list, "getlistitems");
    }
    
    protected int getItemCount() { return plist.rules.size(); }
    protected VirtualElement getItemRef(int index) { return (VirtualElement) plist.rules.elementAt(index); }
    
    public void commandAction(Command c, Displayable d) {
        if (c==cmdAdd) {
            new PrivacyForm(new PrivacyItem(), plist).show();
        }
        if (c==cmdEdit) {
            eventOk();
        }
        if (c==cmdDel) {
            Object del=getFocusedObject();
            if (del!=null) plist.rules.removeElement(del);
        }
        if (c==cmdSave) {
            plist.generateList();
            stream.cancelBlockListener(this);
            PrivacyList.privacyListRq(false, null, "setplists");

            destroyView();
        }
        
        if (c==cmdUp) { move(-1); keyUp(); }
        if (c==cmdDwn) { move(+1); keyDwn(); }
        redraw();
    }
    
    public void move(int offset){
        try {
            int index=cursor;
            PrivacyItem p1=(PrivacyItem)plist.rules.elementAt(index);
            PrivacyItem p2=(PrivacyItem)plist.rules.elementAt(index+offset);
            
            plist.rules.setElementAt(p1, index+offset);
            plist.rules.setElementAt(p2, index);
            
            int tmpOrder=p1.order;
            p1.order=p2.order;
            p2.order=tmpOrder;
            
        } catch (Exception e) {/* IndexOutOfBounds */}
    }

    public void eventOk(){
        PrivacyItem pitem=(PrivacyItem) getFocusedObject();
        if (pitem!=null) {
            new PrivacyForm(pitem, null).show();
        }
    }
    
    public int blockArrived(JabberDataBlock data){
        if (data.getTypeAttribute().equals("result")){
            if (data.getAttribute("id").equals("getlistitems")) {
                data=data.findNamespace("query", "jabber:iq:privacy");
                try {
                    data=data.getChildBlock("list");
                    plist.rules=null;
                    plist.rules=new Vector(0);
                    int size = data.getChildBlocks().size();
                    for(int i=0;i<size;i++){  
                        JabberDataBlock item = (JabberDataBlock)data.getChildBlocks().elementAt(i);
                        plist.addRule(new PrivacyItem(item));
                    }
                } catch (Exception e) {}
                
                processIcon(false);
                return JabberBlockListener.NO_MORE_BLOCKS;
            } //id, result
        } else processIcon(false);
        return JabberBlockListener.BLOCK_REJECTED;
    }

    public void destroyView() {
        stream.cancelBlockListener(this);
        super.destroyView();
    }
}
//#endif