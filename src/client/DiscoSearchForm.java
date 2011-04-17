/*
 * DiscoSearchForm.java
 *
 * Created on 03.10.2008, 19:34
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
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

import io.NvStorage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextField;
import locale.SR;
import ui.MainBar;
import ui.VirtualElement;
import ui.VirtualList;
import ui.controls.form.ListItem;
import util.StringLoader;

//#ifdef SERVICE_DISCOVERY
import disco.ServiceDiscovery;
//#endif
//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.CommandListener;
//# import javax.microedition.lcdui.Command;
//#else
import images.MenuIcons;
import menu.MenuListener;
import menu.Command;
//#endif
//#ifdef GRAPHICS_MENU
import midlet.BombusQD;
//#endif
//#ifdef GRAPHICS_MENU        
import ui.GMenu;
import ui.GMenuConfig;
//#endif
import ui.input.InputTextBox;
import ui.input.InputTextBoxNotify;

/**
 *
 * @author ad,aqent
 */

public class DiscoSearchForm
        extends VirtualList 
        implements
//#ifndef MENU_LISTENER
//#         CommandListener
//#else
        MenuListener
//#endif
        , InputTextBoxNotify
    { 
    
    private Command cmdSearch;
    private Command cmdAddServer;
    private Command cmdDel;
    
    Vector servers = new Vector(0);
    Vector list = new Vector(0);
    int type = 0;
    
    /**
     * Creates a new instance of DiscoSearchForm
     */
    
    public DiscoSearchForm(Vector list, int type) {
        super();

        this.list=list;
        this.type=type;
        
        cmdSearch = new Command(SR.get(SR.MS_SEARCH), Command.OK, 1);
        cmdSearch.setImg(MenuIcons.ICON_SEARCH);

        cmdAddServer = new Command(SR.get(SR.MS_ADD), Command.SCREEN, 2);
        cmdAddServer.setImg(0x42);

        cmdDel=new Command (SR.get(SR.MS_DELETE), Command.SCREEN, 3);
        cmdDel.setImg(0x41);
    
        if(list==null){
          loadRecentList();
          if (getItemCount()<1) loadDefaults();
        }
        updateMainBar();
        commandState();
    }
   
    protected int getItemCount() {
       if(list!=null){
          return list.size();       
       }else {
          return (servers==null)?0:servers.size();
      }
    }

    protected VirtualElement getItemRef(int index) {
       if(list!=null){
          switch(type){
              case 0: return new ListItem((String)list.elementAt(index),0x00,"icq");
              case 1: return new ListItem((String) list.elementAt(index),0x00,"mrim");
              case 2: return new ListItem((String) list.elementAt(index),0x00,"irc");
              case 3: return new ListItem((String) list.elementAt(index),0x00,"j2j");
          }           
       }else {
          return new ListItem((String) servers.elementAt(index));
       }
      return null;
    }

    private void updateMainBar() {
        String str="";
          switch(type){
              case 0: str = SR.get(SR.MS_TRANSPORTS).concat(" ICQ"); break;
              case 1: str = SR.get(SR.MS_TRANSPORTS).concat(" MRIM"); break;
              case 2: str = SR.get(SR.MS_TRANSPORTS).concat(" IRC"); break;
              case 3: str = SR.get(SR.MS_TRANSPORTS).concat(" J2J"); break;
          }
        setMainBarItem(new MainBar(2, null, ((list!=null)?str:SR.get(SR.MS_USERS_SEARCH))
                +" ("+getItemCount()+") ", false));
    }
    
    public void commandState() {
//#ifdef MENU_LISTENER
        menuCommands.removeAllElements();
        cmdfirstList.removeAllElements();
        cmdsecondList.removeAllElements();
        cmdThirdList.removeAllElements();
//#endif
        if(list==null){
          addCommand(cmdSearch);
          addCommand(cmdAddServer); 
          addCommand(cmdDel); 
        }
//#ifndef GRAPHICS_MENU        
//#      addCommand(cmdCancel);
//#endif     
    }


//#ifdef MENU_LISTENER
    public String touchLeftCommand(){ return SR.get(SR.MS_MENU); }
    
//#ifdef GRAPHICS_MENU        
   public void cmdOk(){ showGraphicsMenu(); }
//#else
//#    public void cmdOk(){ showMenu(); }  
//#endif    

    
//#endif
    
    public void commandAction(Command c, Displayable displayable) {
        if (c == cmdSearch) {
            eventOk();
        } else if (c==cmdAddServer) {
            InputTextBox box = new InputTextBox(SR.get(SR.MS_SERVER), null, 50, TextField.ANY);
            box.setNotifyListener(this);
            box.show();
	} else if (c==cmdDel) {
            delServer();
        }
    }
    
    private void exitSearchForm(){
        BombusQD.sd.roster.show();
    }
    
    
    public void okNotify(String server) {
        addServer(server);
    }    
    private void loadDefaults() {
	Vector defs[]=new StringLoader().stringLoader("/def_search.txt", 1);
        for (int i=0; i<defs[0].size(); i++) {
            String server   =(String) defs[0].elementAt(i);
            servers.addElement(server);
        }
        defs=null;
    }
    private void loadRecentList() {
        servers=new Vector(0);
        try {
            DataInputStream is=NvStorage.ReadFileRecord("search_servers", 0);
            try { 
                while (true) servers.addElement(is.readUTF());
            } catch (EOFException e) { is.close(); is=null; }
        } catch (Exception e) { }
    }
    public void saveRecentList() {
        DataOutputStream os=NvStorage.CreateDataOutputStream();
        try {
            for (Enumeration e=servers.elements(); e.hasMoreElements(); ) {
                String s=(String)e.nextElement();
                os.writeUTF(s);
            }
        } catch (Exception e) { }
        NvStorage.writeFileRecord(os, "search_servers", 0, true);
    }
    private void addServer(String server) {
        servers.addElement(server);
        saveRecentList();
        updateMainBar();
    }
    private void delServer(){
        servers.removeElementAt(cursor);
        if (getItemCount()<=cursor) moveCursorEnd();
        saveRecentList();
        updateMainBar();
        redraw();
    }

    public void eventOk(){
//#ifdef SERVICE_DISCOVERY
        if (getItemCount()==0) 
            return;
        ListItem join=(ListItem)getFocusedObject();
        new ServiceDiscovery(join.toString(), null, (list!=null)?false:true).show();
//#endif
    }
    

//#ifdef MENU_LISTENER
    
//#ifdef GRAPHICS_MENU        
    public int showGraphicsMenu() {
        commandState();
        menuItem = new GMenu(this,null, menuCommands);        
        GMenuConfig.getInstance().itemGrMenu=GMenu.SEARCH_FORM;
        return GMenu.SEARCH_FORM;
    }
//#else
//#     public void showMenu() {
//#         commandState();
//#         new MyMenu(display, parentView, this, SR.get(SR.MS_BOOKMARKS, null, menuCommands));
//#     }   
//#endif      

//#endif
}

