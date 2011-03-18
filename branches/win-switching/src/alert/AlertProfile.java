/*
 * AlertProfile.java
 *
 * Created on 28.03.2005, 0:05
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

package alert;

import client.*;
import images.RosterIcons;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
import ui.*;
//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.CommandListener;
//# import javax.microedition.lcdui.Command;
//#else
import menu.MenuListener;
import menu.Command;
import menu.MyMenu;
//#endif
import ui.MainBar;
//#ifdef GRAPHICS_MENU        
import ui.GMenu;
//#endif  
/**
 *
 * @author Eugene Stahov,aqent
 */
public class AlertProfile extends VirtualList implements
//#ifndef MENU_LISTENER
//#         CommandListener
//#else
        MenuListener
//#endif
    {
    
    private final static int ALERT_COUNT=4;
    
    public final static int ALL=0;
    public final static int VIBRA=1;
    public final static int SOUND=2;
    public final static int NONE=3;
    
    private Profile profile=new Profile();
    int defp;
    Config cf;
    
    private Command cmdOk;
    private Command cmdDef;
    private Command cmdCancel;

    public AlertProfile() {
        super();
        
        cf=midlet.BombusQD.cf;
        
        cmdOk=new Command(SR.get(SR.MS_SELECT),Command.OK,1);
        cmdOk.setImg(0x43);

        cmdDef=new Command(SR.get(SR.MS_SETDEFAULT),Command.OK,2);
        cmdDef.setImg(0x24);

        cmdCancel=new Command(SR.get(SR.MS_BACK),Command.BACK,99);
        
        setMainBarItem(new MainBar(SR.get(SR.MS_ALERT_PROFILE)));

        setCommandListener(this);
        
        int p=cf.profile;
        defp=cf.def_profile;
        
        moveCursorTo(p);
    }

    public void commandState() {
//#ifdef MENU_LISTENER
        menuCommands.removeAllElements();
//#endif
        addCommand(cmdOk); 
        addCommand(cmdDef);  
//#ifndef GRAPHICS_MENU        
//#      addCommand(cmdCancel);
//#endif     
    }
    
//#ifdef MENU_LISTENER
    
//#ifdef GRAPHICS_MENU        
    public int showGraphicsMenu() {
        commandState();
        menuItem = new GMenu(display, parentView, this,null, menuCommands);        
        return GMenu.ALERT_PROFILE;
    }
//#else
//#     public void showMenu() {
//#         commandState();
//#         new MyMenu(display, parentView, this, SR.get(SR.MS_STATUS), null, menuCommands);
//#    }   
//#endif       
//#endif
    
    int index;
    public VirtualElement getItemRef(int Index){
        if (Index>=ALERT_COUNT) throw new IndexOutOfBoundsException();
        index=Index;
        return profile;
    }
    
    
    private class Profile extends IconTextElement {
        public Profile(){
            super(RosterIcons.getInstance());
        }
        public int getImageIndex(){return index+RosterIcons.ICON_PROFILE_INDEX+1;}
        public String toString(){ 
            StringBuffer s=new StringBuffer();
            switch (index) {
                case ALL: s.append(SR.get(SR.MS_ALERT_PROFILE_ALLSIGNALS)); break;
                case VIBRA: s.append(SR.get(SR.MS_ALERT_PROFILE_VIBRA)); break;
                case SOUND: s.append(SR.get(SR.MS_SOUND)); break;
                case NONE: s.append(SR.get(SR.MS_ALERT_PROFILE_NOSIGNALS)); break;
            }
            if (index==defp) s.append(SR.get(SR.MS_IS_DEFAULT));
            return s.toString();
        }
    }
    
    public void commandAction(Command c, Displayable d){
        if (c==cmdOk) eventOk(); 
        if (c==cmdDef) { 
            cf.def_profile=defp=cursor;
            redraw();
            cf.saveToStorage();
        }
        if (c==cmdCancel) destroyView();
    }
    
    public void eventOk(){
        cf.profile=cursor;
        destroyView();
    }
    
    public int getItemCount(){ return ALERT_COUNT; }
}
