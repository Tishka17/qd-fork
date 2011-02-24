/*
 * SearchResult.java
 *
 * Created on 10.07.2005, 21:40
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
//#ifdef SERVICE_DISCOVERY
package disco; 
import java.util.*;
import javax.microedition.lcdui.*;
//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.CommandListener;
//# import javax.microedition.lcdui.Command;
//#else
import menu.MenuListener;
import menu.Command;
import menu.MyMenu;
//#endif
import locale.SR;
import ui.*;
import com.alsutton.jabber.*;
import com.alsutton.jabber.datablocks.*;
import client.*;
import ui.MainBar;
//#ifdef GRAPHICS_MENU        
//# import ui.GMenu;
//# import ui.GMenuConfig;
//#endif
/**
 *
 * @author EvgS,aqent
 */
public class SearchResult
        extends VirtualList
        implements 
//#ifndef MENU_LISTENER
//#         CommandListener,
//#else
        MenuListener
//#endif  
{
    
    StaticData sd=StaticData.getInstance();
    private Command cmdAdd;
    
    private Vector items;
    boolean xData;
    
    /** Creates a new instance of SearchResult */
    public SearchResult(Display display, JabberDataBlock result) {
        super(display);
        
        cmdAdd=new Command(SR.get(SR.MS_ADD), Command.SCREEN, 1);
        String service=result.getAttribute("from");
        
        setMainBarItem(new MainBar(2, null, service, false));
        
        setCommandListener(this);

        items=new Vector(0);
        
        JabberDataBlock query=result.getChildBlock("query");
        if (query==null) return;
        
        addCommand(cmdAdd);

        JabberDataBlock x=query.getChildBlock("x");
        if (x!=null) { query=x; xData=true; }
        
        sd.roster.cleanupSearch();
        
        StringBuffer vcard;
        Contact serv;
        Msg m;
        Vector queryElements = query.getChildBlocks();
        Vector childBlocks = new Vector(0);
        
        int size = queryElements.size();
        for(int i = 0; i<size; ++i){
            JabberDataBlock child = (JabberDataBlock) queryElements.elementAt(i);
	    
            if (child.getTagName().equals("item")) {
                vcard = new StringBuffer(0);
                String jid="";
		
	        byte status=Constants.PRESENCE_OFFLINE;
                if (!xData) { jid=child.getAttribute("jid"); }

                childBlocks = child.getChildBlocks();
                int chSize = childBlocks.size();
                for(int k = 0; k<chSize; ++k){
                    JabberDataBlock field=(JabberDataBlock) childBlocks.elementAt(k);
                    String name;
                    String value;
                    if (xData) {
                        name=field.getAttribute("var");
                        value=field.getChildBlockText("value");
                    } else {
                        name=field.getTagName();
                        value=field.getText();
                    }
                    if (name.equals("jid")) jid=value;
                    if (value.length()>0)
                    {
                        //vcard.append(new StringItem(name,value+"\n"));
                        vcard.append(name)
                             .append((char)0xa0)
                             .append(value)
                             .append((char)'\n');
                    }
		    // status returned by jit
		    if (name.equals("status")) if (!value.equals("offline")) status=Constants.PRESENCE_ONLINE;
                }
                serv = new Contact(null, jid, status,"search");
                serv.setGroup(sd.roster.contactList.groups.getGroup(Groups.TYPE_SEARCH_RESULT));
                m=new Msg(Constants.MESSAGE_TYPE_PRESENCE, jid, "Short info", vcard.toString());
                m.unread=false;
                m.itemCollapsed = false;
                serv.addMessage(m);
                
                items.addElement(serv);
                sd.roster.addContact(serv);
            }
        }
        vcard = new StringBuffer(0);
        vcard = null;
        
        queryElements = new Vector(0);
        childBlocks = new Vector(0);
        sd.roster.reEnumRoster();
        commandState();
        attachDisplay(display);
    }
    
    public void commandState(){
//#ifdef MENU_LISTENER
        menuCommands.removeAllElements();
//#endif
    }
    
    
//#ifdef MENU_LISTENER
    public String touchLeftCommand(){ return SR.get(SR.MS_ADD); }
    public void touchLeftPressed(){ new ContactEdit(display, sd.roster, (Contact)getFocusedObject()); }
    public void touchRigthPressed(){ destroyView(); }
    
    public void destroyView(){
       if (display!=null) midlet.BombusQD.sd.roster.showRoster();
    }
    
//#ifdef GRAPHICS_MENU        
//#     public int showGraphicsMenu() {
//#          commandState();
//#          menuItem = new GMenu(display, parentView, this, null, menuCommands);
//#          GMenuConfig.getInstance().itemGrMenu = -1;        
//#          eventOk();
//#          return -1;
//#     }
//#else
    public void showMenu() {
        commandState();
        new MyMenu(display, parentView, this, SR.get(SR.MS_DISCO), null, menuCommands);
    } 
//#endif

//#endif        
    
    public int getItemCount(){ return items.size();}
    public VirtualElement getItemRef(int index) { return (VirtualElement) items.elementAt(index);}

    public void commandAction(Command c, Displayable d){
        if (c==cmdAdd){
            //destroyView();
            new ContactEdit(display, sd.roster, (Contact)getFocusedObject());
            //return;
        }
    }
    
    public void eventOk(){
        try {
            Contact c=(Contact)getFocusedObject();
            if (c==null) return;
            display.setCurrent(c.getMessageList());
        } catch (Exception e) {}
    }
}
//#endif
