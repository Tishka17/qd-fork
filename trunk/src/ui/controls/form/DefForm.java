/*
 * DefForm.java
 *
 * Created on 21.05.2008, 9:40
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

package ui.controls.form;

import java.util.Vector;
//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.CommandListener;
//# import javax.microedition.lcdui.Command;
//#else
import menu.MenuListener;
import menu.Command;
//#endif
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
import midlet.Commands;
import ui.MainBar;
import ui.VirtualElement;
import ui.VirtualList;
//#ifdef GRAPHICS_MENU
//# import ui.GMenu;
//#endif
/**
 *
 * @author ad,aqent
 */
public class DefForm
        extends VirtualList
        implements
//#ifndef MENU_LISTENER
//#         CommandListener
//#else
        MenuListener
//#endif
    {

    public Display display;

    public Vector itemsList=new Vector(0);

    public Command cmdCancel;

    public int superWidth;
    /**
     * Creates a new instance of DefForm
     */
    public DefForm() {};

    public DefForm(final Display display, Displayable pView, String caption) {
	this.display=display;

        cmdCancel = new Command(SR.get(SR.MS_BACK), Command.BACK, 99);
        setMainBarItem(new MainBar(caption));

        superWidth=super.getWidth();

//#ifdef GRAPHICS_MENU
//#         //commandState();
//#else
    super.commandState();
//#endif


	setCommandListener(this);

        enableListWrapping(true);

        this.parentView=pView;
    }

    protected int getItemCount() { return itemsList.size(); }

    protected VirtualElement getItemRef(int index) {
        return (VirtualElement)itemsList.elementAt(index);
    }

    public void touchLeftPressed(){ cmdOk(); }

    public void touchRightPressed(){ cmdCancel(); }

    public void commandAction(Command command, Displayable displayable) {
	if (command == cmdCancel) {
	    cmdCancel();
	}
	if (command == Commands.cmdOk) {
            cmdOk();
        }
    }

    public void destroyView()	{
        //System.out.println("DefForm destroyView->" + itemsList.toString());
        int size = itemsList.size();
        Object obj;
        for(int i = 0; i < size; ++i){
            obj = (Object)itemsList.elementAt(i);
            if(obj instanceof DropChoiceBox) ((DropChoiceBox)obj).destroy();
            if(obj instanceof NumberInput) ((NumberInput)obj).destroy();
        }
        itemsList.removeAllElements();
	if (display!=null) display.setCurrent(parentView);
    }

    public final void addControl(Object obj) {
        itemsList.addElement(obj);
    }

    public void cmdCancel() {
        destroyView();
    }

    public void cmdOk() { }

//#ifdef MENU_LISTENER
    public void userKeyPressed(int keyCode){
     switch (keyCode) {
        case KEY_NUM4:
            pageLeft();
            break;
        case KEY_NUM6:
            pageRight();
            break;
     }
    }
//#endif

//#ifdef GRAPHICS_MENU
//# 
//#  //   public void commandState() {
//#ifdef MENU_LISTENER
//#         //menuCommands.removeAllElements();
//#endif
//# 	//addCommand(cmdOk); //cmdOk.setImg(0x43);
//#ifndef GRAPHICS_MENU
//#      addCommand(cmdCancel);
//#endif
//# //    }
//# 
//#else


    public void commandState() {
//#ifdef MENU_LISTENER
        menuCommands.removeAllElements();
//#endif
	addCommand(cmdOk);
    addCommand(cmdCancel);
   }

//#endif



//#ifdef MENU_LISTENER

//#ifdef GRAPHICS_MENU
//# 
//#     public int showGraphicsMenu() {
//#         //System.out.println("1");
//#         return GMenu.DEF_FORM;
//#     }
//# 
//#else
    public void showMenu() {
        commandState();
        if (menuCommands.size()==2) {
            if (menuCommands.elementAt(0).equals(cmdOk) && menuCommands.elementAt(1).equals(cmdCancel)) {
                cmdOk();
                return;
            }
        }
        new MyMenu(display, parentView, this, "", null, menuCommands);
    }
//#endif


    public String touchLeftCommand(){ return SR.get(SR.MS_OK); }
//#endif
}
