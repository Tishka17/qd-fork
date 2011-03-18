/*
 * StatusSelect.java
 *
 * Created on 20.05.2008, 15:47
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

package client;
import java.util.*;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextField;
import locale.SR;
import ui.*;
import ui.MainBar;
import ui.controls.form.CheckBox;
import ui.controls.form.DefForm;
import ui.controls.form.NumberInput;
import ui.controls.form.SimpleString;
import ui.controls.form.SpacerItem;
import ui.controls.form.TextInput;
//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.CommandListener;
//# import javax.microedition.lcdui.Command;
//#else
import menu.MenuListener;
import menu.Command;
import menu.MyMenu;
//#endif
//#ifdef GRAPHICS_MENU        
import ui.GMenu;
//#endif   
import account.AccountSelect;
/**
 *
 * @author ad,aqent
 */
public class StatusSelect
        extends VirtualList
        implements
//#ifndef MENU_LISTENER
//#         CommandListener,
//#else
        MenuListener//,
//#endif
        //Runnable
{
    
    private Command cmdOk;
    private Command cmdEdit;
    private Command cmdDef;
    private Command cmdCancel;

    private Vector statusList;
    private int defp;
    private Contact to;

    public StatusSelect(Display d, Displayable pView, Contact to) {
        super();
        
       cmdOk=new Command(SR.get(SR.MS_SELECT),Command.OK,1);
       cmdEdit=new Command(SR.get(SR.MS_EDIT),Command.SCREEN,2);
       cmdDef=new Command(SR.get(SR.MS_SETDEFAULT),Command.OK,3);
       cmdCancel=new Command(SR.get(SR.MS_CANCEL),Command.BACK,99);
       
        statusList=StatusList.getInstance().statusList;
        this.to=to;
        if (to==null) { 
            setMainBarItem(new MainBar(SR.get(SR.MS_STATUS)));
        } else {
            setMainBarItem(new MainBar(to));
        }

        commandState();
        
        setCommandListener(this);
        
        defp=midlet.BombusQD.cf.loginstatus;
        moveCursorTo(defp);
        attachDisplay(d);
        
        this.parentView=pView;
    }
    
    public void commandState() {
//#ifdef MENU_LISTENER
        menuCommands.removeAllElements();
//#endif
        ExtendedStatus ex = (ExtendedStatus)getFocusedObject();
        if(-1 == ex.getName().indexOf("pep")) {
          addCommand(cmdEdit); cmdEdit.setImg(0x40);
          addCommand(cmdDef);  cmdDef.setImg(0x24);
        }
        addCommand(cmdOk); cmdOk.setImg(0x43);
//#ifndef GRAPHICS_MENU        
//#      addCommand(cmdCancel);
//#endif     
    }
    
    public VirtualElement getItemRef(int Index){
        return (VirtualElement)statusList.elementAt(Index);
    }

    private ExtendedStatus getSel(){ return (ExtendedStatus)getFocusedObject();}
    
    private boolean selectAdvancedStatus() {
       ExtendedStatus ex = (ExtendedStatus)getFocusedObject();
//#ifdef PEP
       if(-1 != ex.getName().indexOf("pep")) {
          midlet.BombusQD.sd.roster.selectPEP.show(this, ex.usermood);
          return true;
       }
//#endif
       return false;
    }
    
    public void commandAction(Command c, Displayable d){
        if (c==cmdOk) eventOk(); 
        if (c==cmdEdit) {
            new StatusForm( display, this, getSel() );
        }
        
        if (c==cmdDef) {
            midlet.BombusQD.cf.loginstatus=cursor;
            redraw();
            midlet.BombusQD.cf.saveToStorage();
        }

        if (c==cmdCancel) destroyView();
    }
    
    public void eventLongOk() {
        touchLeftPressed();
    }

    public void eventOk() {
        if(!selectAdvancedStatus()) {
          destroyView();
          send();
        }
    }
    
    public void send(){
        int status=getSel().getImageIndex();
//#ifdef AUTOSTATUS
        midlet.BombusQD.sd.roster.autoAway=false;
        midlet.BombusQD.sd.roster.autoXa=false;
        midlet.BombusQD.sd.roster.messageActivity();
//#endif
        try {
            if (midlet.BombusQD.sd.roster.isLoggedIn()) {
                midlet.BombusQD.sd.roster.sendDirectPresence(status, to, null);
                midlet.BombusQD.sd.roster.setOfflineTransport();
            } else {
		if (midlet.BombusQD.sd.account==null)
		    new AccountSelect(display, this, false,status);
		else
		    midlet.BombusQD.sd.roster.sendPresence(status, null);
                midlet.BombusQD.cf.isStatusFirst=true;
            }
        } catch (Exception e) {}
    }
    
    public int getItemCount(){   return statusList.size(); }
    
    private void save(){
        StatusList.getInstance().saveStatusToStorage();
    }
    
//#ifdef MENU_LISTENER
    
//#ifdef GRAPHICS_MENU        
    public int showGraphicsMenu() {
        commandState();
        menuItem = new GMenu(display, parentView, this, null, menuCommands);
        GMenuConfig.getInstance().itemGrMenu = GMenu.STATUS_SELECT;
        return GMenu.STATUS_SELECT;
    }
//#else
//#     public void showMenu() {
//#         commandState();
//#         new MyMenu(display, parentView, this, SR.get(SR.MS_STATUS), null, menuCommands);
//#     }  
//#endif      
    

//#endif
    
    class StatusForm extends DefForm {

        private NumberInput tfPriority;
        private TextInput tfMessage;
        private TextInput tfAutoRespondMessage;
        
        private ExtendedStatus status;

        private CheckBox autoRespond;
        
        public StatusForm(Display display, Displayable pView, ExtendedStatus status){
            super(display, pView, SR.get(SR.MS_STATUS)+": "+status.getScreenName());
            this.status=status;
            
            tfMessage = new TextInput(display, SR.get(SR.MS_MESSAGE), status.getMessage(), "ex_status_list", TextField.ANY); //, 100, TextField.ANY "ex_status_list"
            itemsList.addElement(tfMessage);

            tfPriority = new NumberInput(display, SR.get(SR.MS_PRIORITY), Integer.toString(status.getPriority()), -128, 128); //, 100, TextField.ANY "ex_status_list"
            itemsList.addElement(tfPriority);

            if (status.getImageIndex()<5) {
                itemsList.addElement(new SpacerItem(10));
               
                tfAutoRespondMessage=new TextInput(display, SR.get(SR.MS_AUTORESPOND), status.getAutoRespondMessage(), "autorespond", TextField.ANY);//, 100, 0
                itemsList.addElement(tfAutoRespondMessage);
                
                autoRespond = new CheckBox(SR.get(SR.MS_ENABLE_AUTORESPOND), status.getAutoRespond()); itemsList.addElement(autoRespond);
            }
            
            itemsList.addElement(new SpacerItem(10));
            
            itemsList.addElement(new SimpleString("%t - time", false));
            itemsList.addElement(new SimpleString("%dt - date time", false));
            itemsList.addElement(new SimpleString("%qd - random phrase", false));
            
            moveCursorTo(getNextSelectableRef(-1));
            attachDisplay(display);
            this.parentView=pView;
        }
        
        public void cmdOk() {
            if (status.getImageIndex()<5) {
                status.setAutoRespondMessage(tfAutoRespondMessage.getValue());
                status.setAutoRespond(autoRespond.getValue());
            }
            status.setMessage(tfMessage.getValue());                    
            status.setPriority(Integer.parseInt(tfPriority.getValue()));

            save();

            destroyView();
        }
    }
}
