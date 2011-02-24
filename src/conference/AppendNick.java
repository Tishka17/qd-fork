/*
 * AppendNick.java
 *
 * Created on 14.09.2005, 23:32
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

package conference;

import client.Contact;
import client.StaticData;
import locale.SR;
import client.Config;
import ui.MainBar;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import com.alsutton.jabber.datablocks.Presence;
import java.util.Enumeration;
import java.util.Vector;
import ui.VirtualElement;
import ui.VirtualList;
import client.Constants;
//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.CommandListener;
//# import javax.microedition.lcdui.Command;
//#else
import menu.MenuListener;
import menu.Command;
//#endif
//#ifdef GRAPHICS_MENU        
//# import ui.GMenu;
//# import ui.GMenuConfig;
//#endif 
/**
 *
 * @author EvgS,aqent
 */
public class AppendNick         
        extends VirtualList 
        implements
//#ifndef MENU_LISTENER
//#         CommandListener
//#else
        MenuListener
//#endif
{

    Vector nicknames;
    int caretPos; 
    
    Command cmdOk;
    Command cmdCancel;

    private TextField tf;
    private TextBox tb;
    
    private boolean classic_chat=false;        
    public AppendNick(Display display, Displayable pView, Contact to, int caretPos, TextField tf,TextBox tb,boolean classic_chat)
    {
        super(display);
        
        cmdOk=new Command(SR.get(SR.MS_APPEND), Command.OK, 1);
        cmdCancel=new Command(SR.get(SR.MS_CANCEL), Command.BACK, 99);
        
        this.caretPos=caretPos;
        if(midlet.BombusQD.cf.msgEditType>0){
           this.tf=tf;
        }else{
           this.tb=tb;
        };
        this.classic_chat=classic_chat;
        setMainBarItem(new MainBar(SR.get(SR.MS_SELECT_NICKNAME)));
        nicknames=null;
        nicknames=new Vector(0);
        int size=StaticData.getInstance().roster.getHContacts().size();        
            for(int i=0;i<size;i++){    
              Contact c=(Contact)StaticData.getInstance().roster.getHContacts().elementAt(i);
              if (c.group==to.group && c.origin>Constants.ORIGIN_GROUPCHAT && c.status<Constants.PRESENCE_OFFLINE)
                nicknames.addElement(c);             
            }
        commandState();
        this.parentView=pView;        
    }
    
    public AppendNick(Display display, Displayable pView, Contact to, int caretPos, TextField tf,TextBox tb) {
        super(display);
        this.caretPos=caretPos;
        
        cmdOk=new Command(SR.get(SR.MS_APPEND), Command.OK, 1);
        cmdCancel=new Command(SR.get(SR.MS_CANCEL), Command.BACK, 99);
        
        if(midlet.BombusQD.cf.msgEditType>0){
           this.tf=tf;
        }else{
           this.tb=tb;
        };
        
        setMainBarItem(new MainBar(SR.get(SR.MS_SELECT_NICKNAME)));
        
        nicknames=null;
        nicknames=new Vector(0);
        int size=StaticData.getInstance().roster.getHContacts().size();        
            for(int i=0;i<size;i++){    
              Contact c=(Contact)StaticData.getInstance().roster.getHContacts().elementAt(i);
              if (c.group==to.group && c.origin>Constants.ORIGIN_GROUPCHAT && c.status<Constants.PRESENCE_OFFLINE)
                nicknames.addElement(c);             
            }
        sort(nicknames);
        commandState();
        this.parentView=pView;
    }
    
    public void commandState() {
//#ifdef MENU_LISTENER
        menuCommands.removeAllElements();
//#endif
        addCommand(cmdOk); cmdOk.setImg(0x43);
//#ifndef GRAPHICS_MENU        
     addCommand(cmdCancel);
//#endif     
        
        setCommandListener(this);
    }
    
    public VirtualElement getItemRef(int Index) { return (VirtualElement)nicknames.elementAt(Index); }
    protected int getItemCount() { return nicknames.size();  }

    public void commandAction(Command c, Displayable d){
        if (c==cmdOk)
            eventOk();
        else
            destroyView();
    }
    
     public void eventOk(){
         try {
             String nick=((Contact)getFocusedObject()).getJid();
             int rp=nick.indexOf('/');
             StringBuffer b=new StringBuffer(nick.substring(rp+1));
             
            if (caretPos==0) b.append(": ");
//#ifdef RUNNING_MESSAGE
//#              if(classic_chat==true){
//#                if(midlet.BombusQD.cf.msgEditType>0){ tf.setString(b.toString()); }else { tb.setString(b.toString()); };
//#              }else{
//#                if(midlet.BombusQD.cf.msgEditType>0){ tf.insert(b.toString(), caretPos); }else { tb.insert(b.toString(), caretPos); };
//#              }
//#else
            t.insert(b.toString(), caretPos);
//#endif
            b=null;
            nick=null;
         } catch (Exception e) {}
         destroyView();
    }
     
//#ifdef MENU_LISTENER
     
//#ifdef GRAPHICS_MENU        
//#     public int showGraphicsMenu() {
//#         commandState();
//#         menuItem = new GMenu(display, parentView, this, null, menuCommands);
//#         GMenuConfig.getInstance().itemGrMenu = GMenu.APPEND_NICK;          
//#         return GMenu.APPEND_NICK;
//#     }
//#else
    public void showMenu(){ eventOk(); } 
//#endif       
   
     
    public String touchLeftCommand(){ return SR.get(SR.MS_SELECT); }
    public String touchRightCommand(){ return SR.get(SR.MS_BACK); }
//#endif
}
