/*
 * SimpleItemChat.java
 *
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
 *
 */

package client;
import javax.microedition.lcdui.*;
//#ifdef CONSOLE
//#endif
import util.StringUtils;
import conference.AppendNick;
//#ifndef GRAPHICS_MENU
import Menu.RosterToolsMenu;
//#endif

public class SimpleItemChat implements CommandListener {

  private Display display;
  Displayable parentView;

  private Form form;
  private Command exit;
  public TextField txtField;

  private Command cmdSend;
  private Command cmdInsNick;
  private Command cmdInsMe; // /me
  private Command cmdCancel;
  Contact contact;

  public TextField msgTF;
  public ClassicChat scroller;



  public SimpleItemChat(Display display, Displayable pView,Contact contact) {
    this.display=display;
    this.contact=contact;

    StaticData.getInstance().roster.activeContact=contact;
    contact.setIncoming(0);

    cmdSend=new Command(locale.SR.get(locale.SR.MS_SEND), Command.SCREEN, 1);
    cmdInsNick=new Command(locale.SR.get(locale.SR.MS_NICKNAMES),Command.SCREEN,6);
    cmdInsMe=new Command(locale.SR.get(locale.SR.MS_SLASHME), Command.SCREEN, 5); ; // /me
    cmdCancel=new Command(locale.SR.get(locale.SR.MS_BACK), Command.BACK, 2);

    form = new Form(contact.getJid());
    int width = form.getWidth();
    int height = form.getHeight();

    msgTF = new TextField(null, null, 1024, 0);
    scroller = new ClassicChat(null, width, Config.getInstance().classicChatHeight , contact);

    Config.getInstance().width_classic=width;

    form.append(scroller);
    form.append(msgTF);
    form.addCommand(cmdCancel);
    form.addCommand(cmdSend);
    form.addCommand(cmdInsNick);
    form.addCommand(cmdInsMe);
    form.setCommandListener(this);

    contact.scroller=scroller;

    int size = contact.getChatInfo().msgs.size();
      for (int i = 0; i<size; i++) {
         String msg = contact.getChatInfo().msgs.elementAt(i).toString();
            if (((Msg)contact.getChatInfo().msgs.elementAt(i)).unread==true){
              ((Msg)contact.getChatInfo().msgs.elementAt(i)).unread=false;
            }
         StringUtils.addClassicChatMsg(msg,width,scroller);
      }
    contact.getChatInfo().reEnumCounts();

    display.setCurrent(form);
    this.parentView=pView;
    scroller.setDisplay(display,parentView);
  }

  public void commandAction(Command c, Displayable s) {
    if (c == cmdSend){
        String msg = msgTF.getString().trim();
      if ((msg != null) && (!msg.equals("")) && msg.length()>=1){
        StaticData.getInstance().roster.sendMessage(contact, null, msg , null, null);
        msgTF.delete(0,msgTF.size());
      }
    }
    if (c == cmdInsNick){
         new AppendNick(display, display.getCurrent(), contact, msgTF.getCaretPosition() , msgTF, null, true); return;
    }
    if (c == cmdInsMe){
        msgTF.setString("/me ");
    }
    if (c == cmdCancel){
        display.setCurrent(parentView);
    }
  }
}

