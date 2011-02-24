/*
 * StanzaEdit.java
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
//#ifdef CONSOLE
//# package console; 
//# 
//# import client.StaticData;
//# import java.io.IOException;
//# import javax.microedition.lcdui.*;
//# import locale.SR;
//# //import ui.controls.ExTextBox;
//# 
//# /**
//#  *
//#  * @author ad
//#  */
//# public class StanzaEdit 
//#         //extends ExTextBox
//#         implements CommandListener {
//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_CONSOLE");
//#endif
//# 
//#     private Display display;
//#     private Displayable parentView;
//# 
//#     private String body;
//# 
//#     private Command cmdCancel;
//#     private Command cmdSend;
//#     private Command cmdPasteIQDisco;
//#     private Command cmdPasteIQVersion;
//#     private Command cmdPastePresence;
//#     private Command cmdPasteMessage;
//#     
//#     private static final String TEMPLATE_IQ_DISCO="<iq to='???' type='get'>\n<query xmlns='http://jabber.org/protocol/disco#info'/>\n</iq>";
//#     private static final String TEMPLATE_IQ_VERSION="<iq to='???' type='get'>\n<query xmlns='jabber:iq:version'/>\n</iq>";
//#     private static final String TEMPLATE_PRESENCE="<presence to='???'>\n<show>???</show>\n<status>???</status>\n</presence>";
//#     private static final String TEMPLATE_MESSAGE="<message to='???' type='???'>\n<body>???</body>\n</message>";
//# 
//#     public TextBox t;      
//#     
//#     public StanzaEdit(Display display, Displayable pView, String body) {
//#         //super(display, pView, body, SR.get(SR.MS_XML_CONSOLE), TextField.ANY);
//#         
//#         cmdCancel=new Command(SR.get(SR.MS_CANCEL), Command.BACK,99);
//#         cmdSend=new Command(SR.get(SR.MS_SEND), Command.OK,1);
//#         cmdPasteIQDisco=new Command("disco#info", Command.SCREEN,11);
//#         cmdPasteIQVersion=new Command("jabber:iq:version", Command.SCREEN,12);
//#         cmdPastePresence=new Command("presence", Command.SCREEN,13);
//#         cmdPasteMessage=new Command("message", Command.SCREEN,14);
//#         
//#         t=new TextBox(SR.get(SR.MS_XML_CONSOLE),body, 4096, TextField.ANY);
//#         this.display=display;
//#         
//#         t.addCommand(cmdSend);
//# 
//#         t.addCommand(cmdPasteIQDisco);
//#         t.addCommand(cmdPasteIQVersion);
//#         t.addCommand(cmdPastePresence);
//#         t.addCommand(cmdPasteMessage);
//#         
//#         t.addCommand(cmdCancel);
//#         t.setCommandListener(this);
//#         
//#         display.setCurrent(t);
//#         parentView=pView;
//#     }
//#     
//#     public void setParentView(Displayable parentView){
//#         this.parentView=parentView;
//#     }
//#     
//#     public void commandAction(Command c, Displayable d){
//#         //if (executeCommand(c, d)) return;
//#         
//#         body=t.getString();
//#         if (body.length()==0) body=null;
//# 
//#         int caretPos=t.getCaretPosition();
//# 
//#         if (c==cmdPasteIQDisco) { t.insert(TEMPLATE_IQ_DISCO, caretPos); return; }
//#         if (c==cmdPasteIQVersion) { t.insert(TEMPLATE_IQ_VERSION, caretPos); return; }
//#         if (c==cmdPastePresence) { t.insert(TEMPLATE_PRESENCE, caretPos); return; }
//#         if (c==cmdPasteMessage) { t.insert(TEMPLATE_MESSAGE, caretPos); return; }
//# 
//#         if (c==cmdCancel) { 
//#             body=null;
//#         }
//# 
//#         if (c==cmdSend && body!=null) {
//#             try {
//#                 StaticData.getInstance().roster.theStream.send(body.trim());
//#             } catch (IOException ex) { }
//#         }
//# 
//#         display.setCurrent(parentView);
//#     }
//# }
//#endif