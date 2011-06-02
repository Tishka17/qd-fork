/*
 * DebugXMLList.java
 *
 * Created on 7.04.2008, 16:05
 *
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
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

//#ifdef DEBUG_CONSOLE
//# package console.debug;
//# 
//# import client.Config;
//# import client.MsgItem;
//# import message.MessageList;
//# import menu.Command;
//# import locale.SR;
//# import midlet.BombusQD;
//# import ui.MainBar;
//# import midlet.Commands;
//#ifdef CLIPBOARD
//# import ui.VirtualElement;
//# import util.ClipBoard;
//#endif
//# 
//# /**
//#  *
//#  * @author ad,aqent
//#  */
//# 
//# public final class DebugConsole extends MessageList {
//#     private Command cmdEnableDisable;
//#     private Command cmdPurge;
//# 
//#     public DebugConsole() {
//#         super();
//# 
//#         cmdEnableDisable = new Command(SR.get(SR.MS_ENABLE_DISABLE), 0x26);
//#         cmdPurge = new Command(SR.get(SR.MS_CLEAR_LIST), 0x41);
//# 
//#         super.smiles = false;
//# 
//#         setMainBarItem(new MainBar(SR.get(SR.MS_DEBUG_MENU)));
//#     }
//# 
//#     public void commandState() {
//#         menuCommands.removeAllElements();
//#         if (getItemCount() != 0) {
//#ifdef CLIPBOARD
//#             if (Config.useClipBoard) {
//#                 addCommand(Commands.cmdCopy);
//#                 if (!ClipBoard.isEmpty()) {
//#                     addCommand(Commands.cmdCopyPlus);
//#                 }
//#             }
//#endif
//#             addCommand(cmdPurge);
//#         }
//#         addCommand(cmdEnableDisable);
//#     }
//# 
//#     protected void beginPaint() {
//#         StringBuffer str = new StringBuffer(" (").append(getItemCount()).append(") ");
//# 
//#         if (!Config.debug) {
//#             str.append(SR.get(SR.MS_DISABLED));
//#         }
//# 
//#         getMainBarItem().setElementAt(str.toString(), 1);
//#     }
//# 
//#     public VirtualElement getItemRef(int index) {
//#         return (VirtualElement)BombusQD.debug.getMessage(index);
//#     }
//# 
//#     public int getItemCount() {
//#         return BombusQD.debug.size();
//#     }
//# 
//#     public void commandAction(Command c) {
//#         if (c == cmdEnableDisable) {
//#             Config.debug = !Config.debug;
//#             redraw();
//#         } else if (c == cmdPurge) {
//#             clearReadedMessageList();
//#         } else {
//#              super.commandAction(c);
//#         }
//#     }
//# 
//#     private void clearReadedMessageList() {
//#         messages.removeAllElements();
//#         if (cursor + 1 == midlet.BombusQD.debug.stanzas.size()) {
//#             midlet.BombusQD.debug.stanzas.removeAllElements();
//#         } else {
//#             for (int i = 0; i < cursor + 1; i++) {
//#                 midlet.BombusQD.debug.stanzas.removeElementAt(0);
//#             }
//#             moveCursorHome();
//#         }
//#     }
//# 
//#     public void keyClear() {
//#         clearReadedMessageList();
//#     }
//# 
//#     public void userKeyPressed(int keyCode) {
//#         if (keyCode == '0') {
//#             clearReadedMessageList();
//#         }
//#     }
//# }
//#endif
