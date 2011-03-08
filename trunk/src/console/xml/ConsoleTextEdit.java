/*
 * ConsoleTextEdit.java
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

//#if XML_CONSOLE || DEBUG_CONSOLE
//# package console.xml;
//# 
//# import archive.ArchiveList;
//# import client.StaticData;
//# import java.io.IOException;
//# import javax.microedition.lcdui.*;
//# import locale.SR;
//#ifdef CLIPBOARD
//# import util.ClipBoard;
//#endif
//# 
//# /**
//#  *
//#  * @author ad
//#  */
//# public class ConsoleTextEdit implements CommandListener {
//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_CONSOLE");
//#endif
//#     private Display display;
//#     private Displayable parentView;
//#     private String body;
//#     private Command cmdCancel;
//#     private Command cmdSend;
//#     private Command cmdPasteIQDisco;
//#     private Command cmdPasteIQVersion;
//#     private Command cmdPastePresence;
//#     private Command cmdPasteMessage;
//#ifdef CLIPBOARD
//#     private Command cmdPaste;
//#endif
//#ifdef ARCHIVE
//#     private Command cmdArchive;
//#endif
//# 
//#     private static final String TEMPLATE_IQ_DISCO = "<iq to='???' type='get'>\n<query xmlns='http://jabber.org/protocol/disco#info'/>\n</iq>";
//#     private static final String TEMPLATE_IQ_VERSION = "<iq to='???' type='get'>\n<query xmlns='jabber:iq:version'/>\n</iq>";
//#     private static final String TEMPLATE_PRESENCE = "<presence to='???'>\n<show>???</show>\n<status>???</status>\n</presence>";
//#     private static final String TEMPLATE_MESSAGE = "<message to='???' type='???'>\n<body>???</body>\n</message>";
//#     private TextBox textBox;
//# 
//#     public ConsoleTextEdit(Display display, Displayable pView, String body) {
//#         cmdCancel = new Command(SR.get(SR.MS_CANCEL), Command.BACK, 99);
//#         cmdSend = new Command(SR.get(SR.MS_SEND), Command.OK, 1);
//#ifdef ARCHIVE
//#         cmdArchive = new Command(SR.get(SR.MS_ARCHIVE), Command.SCREEN, 9);
//#endif
//#ifdef CLIPBOARD
//#         cmdPaste = new Command(SR.get(SR.MS_PASTE), Command.SCREEN, 10);
//#endif
//#         cmdPasteIQDisco = new Command("disco#info", Command.SCREEN, 11);
//#         cmdPasteIQVersion = new Command("jabber:iq:version", Command.SCREEN, 12);
//#         cmdPastePresence = new Command("presence", Command.SCREEN, 13);
//#         cmdPasteMessage = new Command("message", Command.SCREEN, 14);
//# 
//#         textBox = new TextBox(SR.get(SR.MS_XML_CONSOLE), body, 4096, TextField.ANY);
//#         this.display = display;
//# 
//#         textBox.addCommand(cmdSend);
//#ifdef CLIPBOARD
//#         if (!ClipBoard.isEmpty()) {
//#             textBox.addCommand(cmdPaste);
//#         }
//#endif
//#ifdef ARCHIVE
//#         textBox.addCommand(cmdArchive);
//#endif
//#         textBox.addCommand(cmdPasteIQDisco);
//#         textBox.addCommand(cmdPasteIQVersion);
//#         textBox.addCommand(cmdPastePresence);
//#         textBox.addCommand(cmdPasteMessage);
//#         textBox.addCommand(cmdCancel);
//# 
//#         textBox.setCommandListener(this);
//# 
//#         display.setCurrent(textBox);
//#         parentView = pView;
//#     }
//# 
//#     public void commandAction(Command c, Displayable d) {
//#         if (c == cmdCancel) {
//#             destroyView();
//#         } else {
//#             body = textBox.getString();
//#             if (body.length() == 0) {
//#                 body = null;
//#             }
//#             if (c == cmdSend && body != null) {
//#                 try {
//#                     StaticData.getInstance().roster.theStream.send(body.trim());
//#                 } catch (IOException e) {
//#                     // empty;
//#                 }
//#                 destroyView();
//#             } else {
//#                 int caretPos = textBox.getCaretPosition();
//#                 if (c == cmdPasteIQDisco) {
//#                     textBox.insert(TEMPLATE_IQ_DISCO, caretPos);
//#                 } else if (c == cmdPasteIQVersion) {
//#                     textBox.insert(TEMPLATE_IQ_VERSION, caretPos);
//#ifdef ARCHIVE
//#                 } else if (c == cmdArchive) {
//#                     new ArchiveList(display, caretPos, null, textBox);
//#endif
//#                 } else if (c == cmdPastePresence) {
//#                     textBox.insert(TEMPLATE_PRESENCE, caretPos);
//#                 } else if (c == cmdPasteMessage) {
//#                     textBox.insert(TEMPLATE_MESSAGE, caretPos);
//#                 }
//#ifdef CLIPBOARD
//#                 else if (c == cmdPaste) {
//#                     textBox.insert(ClipBoard.getClipBoard(), caretPos);
//#                 }
//#endif
//#             }
//#         }
//#     }
//# 
//#     private void destroyView() {
//#         textBox.setCommandListener(null);
//#         display.setCurrent(parentView);
//#     }
//# }
//#endif