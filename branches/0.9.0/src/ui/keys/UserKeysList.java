/*
 * userKeysList.java
 *
 * Created on 14.09.2007, 10:11
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
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
 */
//#ifdef USER_KEYS
package ui.keys;

import io.NvStorage;
import java.io.DataOutputStream;
import java.util.Vector;
import locale.SR;
import ui.MainBar;
import ui.VirtualElement;
import ui.VirtualList;
//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.CommandListener;
//# import javax.microedition.lcdui.Command;
//#else
import menu.MenuListener;
import menu.Command;
//#endif
//#ifdef GRAPHICS_MENU        
import ui.GMenu;
import ui.GMenuConfig;
//#endif

public class UserKeysList
        extends VirtualList 
        implements
//#ifndef MENU_LISTENER
//#         CommandListener
//#else
        MenuListener
//#endif
    {
//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_USER_KEYS");
//#endif

    Vector commandsList;
    
    Command cmdSave;
    Command cmdAdd;
    Command cmdEdit;
    Command cmdDel;

    /** Creates a new instance of AccountPicker */
    public UserKeysList() {
        super();
        
        cmdSave=new Command(SR.get(SR.MS_SAVE), 0x44);
        cmdAdd=new Command(SR.get(SR.MS_ADD_CUSTOM_KEY), 0x47);
        cmdEdit=new Command(SR.get(SR.MS_EDIT),0x40);
        cmdDel=new Command(SR.get(SR.MS_DELETE),0x41);
    
        setMainBarItem(new MainBar(SR.get(SR.MS_CUSTOM_KEYS)));
        
        commandsList=UserKeyExec.getInstance().commandsList;
    }

    void commandState(){
//#ifdef MENU_LISTENER
        menuCommands.removeAllElements();
//#endif
        addCommand(cmdSave);
        addCommand(cmdAdd);
        if (getItemCount() > 0) {
            addCommand(cmdEdit); 
            addCommand(cmdDel); 
        }
//#ifndef GRAPHICS_MENU        
//#      addCommand(cmdCancel);
//#endif     
    }

    public VirtualElement getItemRef(int Index) { 
        return (VirtualElement)commandsList.elementAt(Index); 
    }
    
    protected int getItemCount() {
        return commandsList.size();
    }
    
    public void commandAction(Command c){
        if (c==cmdSave) {
            rmsUpdate();
            destroyView();    
        }
        if (c==cmdEdit) 
            new UserKeyEdit(this, (UserKey)getFocusedObject()).show();
        if (c==cmdAdd)
            new UserKeyEdit(this, null).show();
        if (c==cmdDel) {
            UserKeyExec.getInstance().commandsList.removeElement(getFocusedObject());
            
            rmsUpdate();
            moveCursorHome();
            redraw();
        }
    }
    
    public void eventOk(){
        new UserKeyEdit(this, (UserKey)getFocusedObject()).show();
    }
    
    void rmsUpdate(){
        DataOutputStream outputStream=NvStorage.CreateDataOutputStream();
        
        for (int i=0;i<commandsList.size();i++) {
            ((UserKey)commandsList.elementAt(i)).saveToDataOutputStream(outputStream);
        }
        
        NvStorage.writeFileRecord(outputStream, UserKey.storage, 0, true);
    }
   
    public int showGraphicsMenu() {
        commandState();
        menuItem = new GMenu(this, menuCommands);
        GMenuConfig.getInstance().itemGrMenu = GMenu.USERKEYSLIST;            
        return GMenu.USERKEYSLIST;
    }
}
//#endif
