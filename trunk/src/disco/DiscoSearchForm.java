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

//#ifdef SERVICE_DISCOVERY
package disco;

import io.NvStorage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.lcdui.TextField;
import locale.SR;
import ui.MainBar;
import ui.VirtualElement;
import ui.VirtualList;
import ui.controls.form.ListItem;
import util.StringLoader;
import images.MenuIcons;
import menu.MenuListener;
import menu.Command;
import ui.GMenu;
import ui.GMenuConfig;
import ui.input.InputTextBox;
import ui.input.InputTextBoxNotify;

/**
 *
 * @author ad,aqent
 */

public final class DiscoSearchForm extends VirtualList implements MenuListener, InputTextBoxNotify {
    private static final String SRCH_SERVERS_DB = "search_servers";
            
    private Command cmdSearch;
    private Command cmdAddServer;
    private Command cmdDel;

    private Vector servers = new Vector(0);
    private Vector list = new Vector(0);
    int type = 0;

    public DiscoSearchForm(Vector list, int type) {
        super();

        this.list=list;
        this.type=type;

        cmdSearch = new Command(SR.get(SR.MS_SEARCH), MenuIcons.ICON_SEARCH);
        cmdAddServer = new Command(SR.get(SR.MS_ADD), MenuIcons.ICON_USER_SEARCH);
        cmdDel = new Command (SR.get(SR.MS_DELETE), 0x41);

        if(list == null){
            loadRecentList();
            if (getItemCount() == 0) {
                loadDefaults();
            }
        }
        updateMainBar();
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
              case 0: return new ListItem((String)list.elementAt(index),"icq");
              case 1: return new ListItem((String) list.elementAt(index),"mrim");
              case 2: return new ListItem((String) list.elementAt(index),"irc");
              case 3: return new ListItem((String) list.elementAt(index),"j2j");
              case 4: return new ListItem((String) list.elementAt(index),"vk");
              default: return new ListItem((String)list.elementAt(index));
          }
       }else {
          return new ListItem((String)servers.elementAt(index));
       }
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

    public void commandAction(Command c) {
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
            DataInputStream is=NvStorage.ReadFileRecord(SRCH_SERVERS_DB, 0);
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
        NvStorage.writeFileRecord(os, SRCH_SERVERS_DB, 0, true);
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
        if (getItemCount()==0)
            return;
        ListItem join = (ListItem)getFocusedObject();
        new ServiceDiscovery(join.toString(), null, (list!=null)?false:true).show();
    }

    public int showGraphicsMenu() {
        commandState();
        menuItem = new GMenu(this, menuCommands);
        GMenuConfig.getInstance().itemGrMenu=GMenu.SEARCH_FORM;
        return GMenu.SEARCH_FORM;
    }
}
//#endif
