/*
 * ColorsList.java
 *
 * Created on 23.05.2008, 13:10
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

//#ifdef COLOR_TUNE
//# package colors;
//# 
//# import java.util.Enumeration;
//# import java.util.Vector;
//# import javax.microedition.lcdui.*;
//# import locale.SR;
//# import ui.MainBar;
//# import ui.VirtualElement;
//# import ui.VirtualList;
//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.CommandListener;
//# import javax.microedition.lcdui.Command;
//#else
//# import menu.MenuListener;
//# import menu.Command;
//# import menu.MyMenu;
//#endif
//# 
//#ifdef GRAPHICS_MENU
//# import ui.GMenu;
//# import ui.GMenuConfig;
//#endif
//# /**
//#  *
//#  * @author ad,aqent
//#  */
//# public class ColorsList
//#         extends VirtualList implements
//#ifndef MENU_LISTENER
//#         CommandListener,
//#else
//#         MenuListener
//#endif
//#     {
//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_COLORS");
//#endif
//# 
//# 
//#     static void setColor(int paramName, int value) {
//#         ((ColorVisualItem)itemsList.elementAt(paramName)).setColor(value);
//#     }
//# 
//# 
//#     static Vector itemsList;
//# 
//#     public Command cmdOk;
//#     Command cmdBack;
//# 
//#     /**
//#      * Creates a new instance of ColorsList
//#      */
//#     public ColorsList(Display display) {
//#         super(display);
//# 
//#         cmdOk = new Command(SR.get(SR.MS_EDIT), Command.OK, 1);
//#         cmdBack=new Command(SR.get(SR.MS_BACK),Command.BACK,99);
//# 
//#         setMainBarItem(new MainBar(SR.get(SR.MS_COLOR_TUNE)));
//#         addCommand(cmdBack);
//#         addCommand(cmdOk);
//#         setCommandListener(this);
//# 
//#         itemsList=new Vector(0);
//#         int cnt=0;
//#         int size = ColorTheme.colorsContainer.size();
//#         ColorVisualItem item;
//#         int[] colors = ColorTheme.getColors();
//#         for (int i=0;i<=size;++i) {
//#             ColorItem c=(ColorItem)ColorTheme.colorsContainer.elementAt(i);
//#ifdef COLOR_TUNE
//#             item = new ColorVisualItem(c.name, ColorTheme.NAMES[cnt], colors[i]);
//#             itemsList.addElement(item);
//#endif
//#             ++cnt;
//#         }
//#         item = null;
//#         commandState();
//#         attachDisplay(display);
//#         repaint();
//#     }
//# 
//#     protected int getItemCount() { return itemsList.size(); }
//# 
//#     protected VirtualElement getItemRef(int index) {
//#         return (VirtualElement)itemsList.elementAt(index);
//#     }
//# 
//#     public void commandState(){
//#ifdef MENU_LISTENER
//#         menuCommands.removeAllElements();
//#endif
//#     }
//# 
//#ifdef MENU_LISTENER
//#     public String touchLeftCommand(){ return SR.get(SR.MS_SELECT); }
//# 
//#ifdef GRAPHICS_MENU
//#     public int showGraphicsMenu() {
//#          commandState();
//#          menuItem = new GMenu(display, parentView, this, null, menuCommands);
//#          GMenuConfig.getInstance().itemGrMenu = -1;
//#          eventOk();
//#          return -1;
//#     }
//#else
//#     public void showMenu() {
//#         commandState();
//#         new MyMenu(display, parentView, this, SR.get(SR.MS_DISCO), null, menuCommands);
//#     }
//#endif
//# 
//#endif
//# 
//#    public void commandAction(Command command, Displayable displayable) {
//#         if (command==cmdBack) destroyView();
//#         else eventOk();
//#    }
//# 
//# 
//#     public void eventOk() {
//#ifdef COLOR_TUNE
//#          new ColorSelector(display, cursor);
//#endif
//#     }
//# }
//#endif
