/*
 * StatsWindow.java
 *
 * Created on 03.10.2008, 19:42
 *
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
 */
//#ifdef STATS
package stats;

import client.Config;
import client.StaticData;
import info.Version;
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
import ui.controls.form.DefForm;
import ui.controls.form.MultiLine;
import util.StringUtils;
//#ifdef GRAPHICS_MENU        
import ui.GMenu;
import ui.GMenuConfig;
import util.ClipBoard;
//#endif
//#ifdef CLIPBOARD
//# import util.ClipBoard;
//#endif
/**
 *
 * @author ad,aqent
 */
public class StatsWindow
        extends DefForm {

//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_STATS");
//#endif
    
    Stats st=Stats.getInstance();
    
    public Command cmdClear;
    public Command cmdSave;   
    
    /**
     * Creates a new instance of StatsWindow
     */
    public StatsWindow(Display display) {
        super(display, midlet.BombusQD.sd.roster , SR.get(SR.MS_STATS));
        StringBuffer sb = new StringBuffer(0);
        cmdClear = new Command(SR.get(SR.MS_CLEAR), Command.SCREEN, 2);
        cmdSave = new Command(SR.get(SR.MS_SAVE), Command.OK, 3);  
        
        this.display=display;

        sb.append(SR.get(SR.MS_ALL))
          .append(StringUtils.getSizeString(st.getAllTraffic()))
          .append('\n');
        sb.append(SR.get(SR.MS_PREVIOUS_))
          .append(StringUtils.getSizeString(st.getLatest()))
          .append('\n');
        sb.append(SR.get(SR.MS_CURRENT))
          .append(StringUtils.getSizeString(st.getCurrentTraffic()))
          .append('\n');
        
        if (midlet.BombusQD.sd.roster.isLoggedIn() && midlet.BombusQD.cf.userAppLevel == 1) {
           sb.append(SR.get(SR.MS_COMPRESSION))
             .append(midlet.BombusQD.sd.roster.theStream.getStreamStats())
             .append('\n');
        }
        
        if (midlet.BombusQD.sd.roster.isLoggedIn()) {
            sb.append(SR.get(SR.MS_CONNECTED))
              .append(midlet.BombusQD.sd.roster.theStream.getConnectionData())
              .append('\n');
        }
        sb.append(SR.get(SR.MS_CONN))
          .append(st.getSessionsCount())
          .append('\n');
        sb.append(SR.get(SR.MS_STARTED))
          .append(midlet.BombusQD.sd.roster.startTime)
          .append('\n');
        sb.append(SR.get(SR.MS_APPRUN_COUNT))
          .append(st.appRunCount);

        MultiLine item = new MultiLine( null, sb.toString(), super.superWidth);
        item.selectable=true;
        itemsList.addElement(item);
        
        sb = new StringBuffer(0);
        commandState();

        attachDisplay(display);
        this.parentView=midlet.BombusQD.sd.roster;
    }

    
//#ifdef CLIPBOARD
    public void cmdCopy(){
        StringBuffer copy=new StringBuffer();
        for (int i=0;i<itemsList.size();i++) {
            copy.append(((MultiLine)itemsList.elementAt(i)).toString()+"\n");
        }
        ClipBoard.setClipBoard(copy.toString());
        destroyView();
    }
//#endif

 
    public void commandAction(Command command, Displayable displayable) {
//#ifdef CLIPBOARD
	if (command==midlet.BombusQD.commands.cmdCopy) {
	    cmdCopy();
	} else
//#endif
        if(command==cmdSave){
            Stats.getInstance().saveToStorage(false,false);
        }
        if (command==cmdClear) {
            st.saveToStorage(true,false);
            cmdCancel();
        } else super.commandAction(command, displayable);
    }
    
    
    public void commandState(){
//#ifdef MENU_LISTENER        
        menuCommands.removeAllElements();
//#endif    
        
//#ifdef GRAPHICS_MENU               
        //super.commandState();
//#else
//#     super.commandState(); 
//#endif
        removeCommand(cmdCancel);
        removeCommand(midlet.BombusQD.commands.cmdOk);
//#ifdef CLIPBOARD
        if (Config.getInstance().useClipBoard) {
            addCommand(midlet.BombusQD.commands.cmdCopy);
        }
//#endif
        addCommand(cmdClear); cmdClear.setImg(0x13);
        addCommand(cmdSave); cmdSave.setImg(0x44);//SAVE
//#ifndef GRAPHICS_MENU        
//#      addCommand(cmdCancel);
//#endif     
    }
    
    
//#ifdef MENU_LISTENER
    public String touchLeftCommand(){ return SR.get(SR.MS_MENU); }
    
    
//#ifdef GRAPHICS_MENU       
    public void touchLeftPressed(){
        showGraphicsMenu();
    }
    public int showGraphicsMenu() {
        commandState();
        menuItem = new GMenu(display, parentView, this, null, menuCommands);
        GMenuConfig.getInstance().itemGrMenu = GMenu.STATS_WINDOW;
        redraw();
        return GMenu.STATS_WINDOW;
    }
//#else
//#     public void touchLeftPressed(){
//#         showMenu();
//#     }
//#     public void showMenu() {
//#         commandState();
//#         new MyMenu(display, parentView, this, SR.get(SR.MS_STATS), null, menuCommands);
//#    }  
//#endif    

//#endif         
    
    
}
//#endif         