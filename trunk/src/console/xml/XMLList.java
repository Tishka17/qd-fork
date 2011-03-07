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
//# import console.xml.StanzasList;
//# import archive.MessageArchive;
//# import client.Constants;
//# import client.Config;
//# import client.Msg;
//# import message.MessageList;
//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.Command;
//#else
//# import menu.Command;
//#endif
//# import javax.microedition.lcdui.Display;
//# import javax.microedition.lcdui.Displayable;
//# import locale.SR;
//#ifdef CLIPBOARD
//# import util.ClipBoard;
//#endif
//#ifdef CONSOLE
//# import ui.MainBar;
//#endif
//# import message.MessageItem;
//# import midlet.Commands;
//# 
//# /**
//#  *
//#  * @author ad,aqent
//#  */
//# public class XMLList
//#         extends MessageList {
//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_CONSOLE");
//#endif
//#     private StanzasList stanzas;
//#     private Command cmdNew;
//#     private Command cmdEnableDisable;
//#     private Command cmdPurge;
//#     private Command cmdDebugLog;
//# 
//#     /** Creates a new instance of XMLList */
//#     public XMLList(Display display, Displayable pView) {
//#         super();
//# 
//#         cmdNew = new Command(SR.get(SR.MS_NEW), Command.SCREEN, 5);
//#         cmdNew.setImg(0x42);
//# 
//#         cmdEnableDisable = new Command(SR.get(SR.MS_ENABLE_DISABLE), Command.SCREEN, 6);
//#         cmdEnableDisable.setImg(0x26);
//# 
//#         cmdPurge = new Command(SR.get(SR.MS_CLEAR_LIST), Command.SCREEN, 10);
//#         cmdPurge.setImg(0x41);
//# 
//#         cmdDebugLog = new Command("CREATE DEBUG LOG", Command.SCREEN, 13);
//#         cmdDebugLog.setImg(0x44);
//# 
//#         super.smiles = false;
//# 
//#         stanzas = StanzasList.getInstance();
//# 
//#         commandState();
//#         addCommands();
//#         setCommandListener(this);
//# 
//#         moveCursorHome();
//# 
//#         setMainBarItem(new MainBar(SR.get(SR.MS_XML_CONSOLE)));
//# 
//#         attachDisplay(display);
//#         this.parentView = pView;
//#     }
//# 
//#     public void commandState() {
//#ifdef MENU_LISTENER
//#         menuCommands.removeAllElements();
//#endif
//# 
//#ifndef GRAPHICS_MENU
//#      addCommand(cmdBack);
//#endif
//#         addCommand(cmdNew);
//#         addCommand(cmdDebugLog);
//#ifdef ARCHIVE
//#         addCommand(Commands.cmdArch);
//#endif
//#ifdef CLIPBOARD
//#         if (Config.getInstance().useClipBoard) {
//#             addCommand(Commands.cmdCopy);
//#             if (!ClipBoard.isEmpty()) {
//#                 addCommand(Commands.cmdCopyPlus);
//#             }
//#         }
//#endif
//#         addCommand(cmdEnableDisable);
//#         addCommand(cmdPurge);
//#     }
//# 
//#     protected void beginPaint() {
//#         StringBuffer str = new StringBuffer(" (").append(getItemCount()).append(")");
//# 
//#         if (!stanzas.enabled) {
//#             str.append(" - Disabled");
//#         }
//# 
//#         getMainBarItem().setElementAt(str.toString(), 1);
//#     }
//# 
//#     public void eventOk() {
//#         MessageItem mi = (MessageItem)messages.elementAt(cursor);
//#         mi.onSelect(this);
//#     }
//# 
//#     public int getItemCount() {
//#         return stanzas.size();
//#     }
//# 
//#     public Msg getMessage(int index) {
//#         try {
//#             return stanzas.msg(index);
//#         } catch (Exception e) {
//#         }
//#         return new Msg(Constants.MESSAGE_TYPE_OUT, "local", null, null);
//#     }
//# 
//#     public void keyGreen() {
//#         Msg m = getMessage(cursor);
//#         String stanza = "";
//#         try {
//#             stanza = m.toString();
//#         } catch (Exception e) {
//#         }
//#         new StanzaEdit(display, this, stanza);
//#     }
//# 
//#     public void commandAction(Command c, Displayable d) {
//#         Msg msg = getMessage(cursor);
//#         if (c == cmdNew) {
//#             keyGreen();
//#         } else if (c == cmdEnableDisable) {
//#             StanzasList.enabled = !StanzasList.enabled;
//#             redraw();
//#         }
//#         if (msg == null) {
//#             return;
//#         }
//# 
//#         if (c == cmdPurge) {
//#             clearReadedMessageList();
//#ifdef ARCHIVE
//#         } else if (c == Commands.cmdArch) {
//#             MessageArchive.store(util.StringUtils.replaceNickTags(msg));
//#endif
//#         }
//# 
//#         super.commandAction(c, d);
//#     }
//# 
//#     private void clearReadedMessageList() {
//#         try {
//#             if (cursor + 1 == stanzas.size()) {
//#                 stanzas.stanzas.removeAllElements();
//#             } else {
//#                 for (int i = 0; i < cursor + 1; i++) {
//#                     stanzas.stanzas.removeElementAt(0);
//#                 }
//#             }
//#             messages.removeAllElements();
//#         } catch (Exception e) {
//#         }
//#         moveCursorHome();
//#         redraw();
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
//# 
//#     public void destroyView() {
//#         super.destroyView();
//#     }
//# }
//#endif
