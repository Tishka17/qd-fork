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
//# import console.xml.StanzaEdit;
//# import client.Constants;
//# import client.Msg;
//# import client.StaticData;
//# import message.MessageList;
//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.Command;
//#else
//# import menu.Command;
//#endif
//# import javax.microedition.lcdui.Display;
//# import javax.microedition.lcdui.Displayable;
//# import locale.SR;
//# import ui.MainBar;
//# import message.MessageItem;
//# import midlet.Commands;
//#ifdef CLIPBOARD
//# import util.ClipBoard;
//#endif
//# 
//# /**
//#  *
//#  * @author ad,aqent
//#  */
//# public final class DebugXMLList
//#     extends MessageList {
//# 
//#     private StaticData sd=StaticData.getInstance();
//# 
//#     private Command cmdEnableDisable;
//#     private Command cmdPurge;
//#ifdef CLIPBOARD
//#     private Command copyReport;
//#endif
//# 
//#     /** Creates a new instance of XMLList */
//#     public DebugXMLList(Display display, Displayable pView) {
//#         super ();
//# 
//#         cmdEnableDisable=new Command(SR.get(SR.MS_ENABLE_DISABLE), Command.SCREEN, 1);
//#         cmdPurge=new Command(SR.get(SR.MS_CLEAR_LIST), Command.SCREEN, 3);
//#ifdef CLIPBOARD
//#         copyReport=new Command("Bugreport to clipboard", Command.SCREEN, 2);
//#endif
//# 
//#         super.smiles=false;
//# 
//#         commandState();
//#         addCommands();
//#         setCommandListener(this);
//# 
//#         moveCursorHome();
//# 
//# 
//#  	MainBar mainbar=new MainBar("Debug console");
//#          setMainBarItem(mainbar);
//# 
//#         attachDisplay(display);
//#         this.parentView=pView;
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
//#ifdef CLIPBOARD
//#         addCommand(copyReport); copyReport.setImg(0x44);
//#              if (midlet.BombusQD.cf.useClipBoard) {
//#                 addCommand(Commands.cmdCopy);
//#                 if (!ClipBoard.isEmpty()) {
//#                     addCommand(Commands.cmdCopyPlus);
//#                 }
//#             }
//#endif
//#         addCommand(cmdEnableDisable);
//#         cmdEnableDisable.setImg(0x26);
//# 
//#         addCommand(cmdPurge);
//#         cmdPurge.setImg(0x41);//DELETE
//# 
//#     }
//# 
//#     private StringBuffer str;
//# 
//#     protected void beginPaint() {
//#         str = new StringBuffer(0);
//#         str.append(" (")
//#         .append(getItemCount())
//#         .append(")");
//# 
//#         if (!midlet.BombusQD.cf.debug)
//#             str.append(" - Disabled");
//# 
//#         getMainBarItem().setElementAt(str.toString(),1);
//#     }
//# 
//# 
//#     public int getItemCount() {
//#         return midlet.BombusQD.debug.stanzas.size();
//#     }
//# 
//#     public void eventOk(){
//#        MessageItem mi = (MessageItem)messages.elementAt(cursor);
//#        mi.onSelect(this);
//#     }
//# 
//#     protected Msg getMessage(int index) {
//#         Msg msg=new Msg(Constants.MESSAGE_TYPE_OUT, "local", null, null);
//#         try {
//#             msg=midlet.BombusQD.debug.msg(index);
//#         } catch (Exception e) {}
//# 	return msg;
//#     }
//# 
//#     public void keyGreen(){
//# 	Msg m=getMessage(cursor);
//#         String stanza = "";
//#         try {
//#             stanza =  m.toString();
//#         } catch (Exception e) {}
//#         new StanzaEdit(display, this, stanza);
//#     }
//# 
//#     public void commandAction(Command c, Displayable d) {
//#         super.commandAction(c,d);
//# 
//# 	Msg m=getMessage(cursor);
//#         if (c==cmdEnableDisable) {
//#             midlet.BombusQD.cf.debug=!midlet.BombusQD.cf.debug;
//#             redraw();
//#         }
//# 	if (m==null) return;
//# 
//#         if (c==cmdPurge) {
//#             clearReadedMessageList();
//#         }
//#ifdef CLIPBOARD
//#         if(c==copyReport){
//# 
//#         }
//#endif
//#     }
//# 
//#     private void clearReadedMessageList() {
//#         try {
//#             if (cursor+1==midlet.BombusQD.debug.stanzas.size()) {
//#                 midlet.BombusQD.debug.stanzas.removeAllElements();
//#             }
//#             else {
//#                 for (int i=0; i<cursor+1; i++)
//#                     midlet.BombusQD.debug.stanzas.removeElementAt(0);
//#             }
//#             messages.removeAllElements();
//#         } catch (Exception e) { }
//#         moveCursorHome();
//#         redraw();
//#     }
//# 
//#     public void keyClear() {
//#         clearReadedMessageList();
//#     }
//# 
//#     public void userKeyPressed(int keyCode) {
//#         if (keyCode=='0')
//#             clearReadedMessageList();
//#     }
//# 
//#     public void destroyView(){
//# 	super.destroyView();
//#     }
//# }
//#endif
