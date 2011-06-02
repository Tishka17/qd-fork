/*
 * XMLList.java
 *
 * Created on 7.04.2008, 13:37
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

//#ifdef XML_CONSOLE
//# package console.xml;
//# 
//# import archive.MessageArchive;
//# import client.Config;
//# import client.MsgItem;
//# import message.MessageList;
//# import menu.Command;
//# import locale.SR;
//#ifdef CLIPBOARD
//# import util.ClipBoard;
//#endif
//# import midlet.BombusQD;
//# import midlet.Commands;
//# import ui.MainBar;
//# import ui.VirtualElement;
//# 
//# /**
//#  *
//#  * @author ad,aqent
//#  */
//# 
//# public class XMLConsole extends MessageList {
//#     private XMLList stanzas;
//#     private Command cmdNew;
//#     private Command cmdEnableDisable;
//#     private Command cmdPurge;
//# 
//#     public XMLConsole() {
//#         super();
//# 
//#         cmdNew = new Command(SR.get(SR.MS_NEW), 0x42);
//#         cmdEnableDisable = new Command(SR.get(SR.MS_ENABLE_DISABLE), 0x26);
//#         cmdPurge = new Command(SR.get(SR.MS_CLEAR_LIST), 0x41);
//# 
//#         super.smiles = false;
//# 
//#         stanzas = XMLList.getInstance();
//# 
//#         setMainBarItem(new MainBar(SR.get(SR.MS_XML_CONSOLE)));
//#     }
//# 
//#     public void commandState() {
//#         menuCommands.removeAllElements();
//# 
//#         if (BombusQD.sd.roster.isLoggedIn()) {
//#             addCommand(cmdNew);
//#         }
//# 
//#         if (getItemCount() != 0) {
//#ifdef ARCHIVE
//#             addCommand(Commands.cmdArch);
//#endif
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
//#         if (!XMLList.enabled) {
//#             str.append(SR.get(SR.MS_DISABLED));
//#         }
//# 
//#         getMainBarItem().setElementAt(str.toString(), 1);
//#     }
//#     
//#     public VirtualElement getItemRef(int index) {
//#         return stanzas.getMessage(index);
//#     }
//# 
//#     public int getItemCount() {
//#         return stanzas.size();
//#     }
//# 
//#     public void keyGreen() {
//#         sendStanza(false);
//#     }
//# 
//#     public void commandAction(Command c) {
//#         if (c == cmdNew) {
//#             sendStanza(true);
//#         } else if (c == cmdEnableDisable) {
//#             XMLList.enabled = !XMLList.enabled;
//#             redraw();
//#         } else if (c == cmdPurge) {
//#             clearReadedMessageList();
//#ifdef ARCHIVE
//#         } else if (c == Commands.cmdArch) {
//#             MessageArchive.store(util.StringUtils.replaceNickTags(getMessage(cursor)));
//#endif
//#         } else {
//#             super.commandAction(c);
//#         }
//#     }
//# 
//#     private void sendStanza(boolean isNew) {
//#         if (BombusQD.sd.roster.isLoggedIn()) {
//#             String stanza = "";
//#             if (!isNew) {
//#                 try {
//#                     stanza = getMessage(cursor).body;
//#                 } catch (Exception e) {}
//#             }
//#             new ConsoleTextEdit(stanza).show();
//#         }
//#     }
//# 
//#     private void clearReadedMessageList() {
//#         messages.removeAllElements();
//#         if (cursor + 1 == stanzas.size()) {
//#             stanzas.stanzas.removeAllElements();
//#         } else {
//#             for (int i = 0; i < cursor + 1; i++) {
//#                 stanzas.stanzas.removeElementAt(0);
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
