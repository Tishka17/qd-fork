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

import client.Config;
import images.RosterIcons;
import locale.SR;
import menu.MenuListener;
import menu.Command;
import ui.MainBar;    
import ui.GMenu;
import ui.IconTextElement;
import ui.VirtualElement;
import ui.VirtualList;

/**
 *
 * @author Eugene Stahov,aqent
 */

public class AlertProfile extends VirtualList implements MenuListener {    
    private final static int ALERT_COUNT=4;
    
    public final static int ALL=0;
    public final static int VIBRA=1;
    public final static int SOUND=2;
    public final static int NONE=3;
    
    private Profile profile=new Profile();
    Config cf;
    
    private Command cmdOk;
    private Command cmdDef;

    public AlertProfile() {
        super();
        
        cf=midlet.BombusQD.cf;
        
        cmdOk=new Command(SR.get(SR.MS_SELECT), 0x43);
        cmdDef=new Command(SR.get(SR.MS_SETDEFAULT), 0x24);
        
        setMainBarItem(new MainBar(SR.get(SR.MS_ALERT_PROFILE)));
        
        moveCursorTo(cf.currentAlertProfile);
    }

    public void commandState() {
        menuCommands.removeAllElements();

        addCommand(cmdOk); 
        addCommand(cmdDef);  
    }
      
    public int showGraphicsMenu() {
        commandState();
        menuItem = new GMenu(this, menuCommands);        
        return GMenu.ALERT_PROFILE;
    }
    
    int index;
    public VirtualElement getItemRef(int Index){
        if (Index>=ALERT_COUNT) throw new IndexOutOfBoundsException();
        index=Index;
        return profile;
    }
    
    
    private class Profile extends IconTextElement {
        private Profile(){
            super(RosterIcons.getInstance());
        }
        
        public int getImageIndex() {
            return 49 + index;
        }

        public String toString(){ 
            StringBuffer s=new StringBuffer();
            switch (index) {
                case ALL: s.append(SR.get(SR.MS_ALERT_PROFILE_ALLSIGNALS)); break;
                case VIBRA: s.append(SR.get(SR.MS_ALERT_PROFILE_VIBRA)); break;
                case SOUND: s.append(SR.get(SR.MS_SOUND)); break;
                case NONE: s.append(SR.get(SR.MS_ALERT_PROFILE_NOSIGNALS)); break;
            }
            if (index == cf.defaultAlertProfile) s.append(SR.get(SR.MS_IS_DEFAULT));
            return s.toString();
        }
    }
    
    public void commandAction(Command c){
        if (c==cmdOk) eventOk(); 
        if (c==cmdDef) { 
            cf.defaultAlertProfile = cursor;
            cf.saveToStorage();
        }
    }
    
    public void eventOk(){
        cf.currentAlertProfile = cursor;
        destroyView();
    }

    public void eventLongOk() {
        cf.defaultAlertProfile = cursor;
        cf.saveToStorage();
    }
    
    public int getItemCount(){ return ALERT_COUNT; }
}
