/*
 * MessageList.java
 *
 * Created on 11.12.2005, 3:02
 * Copyright (c) 2005-2008, Eugene Stahov (evgs), http://bombus-im.org
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

package message;

import client.Config;
import client.Msg;
import colors.ColorTheme;
import java.util.Vector;
//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.CommandListener;
//# import javax.microedition.lcdui.Command;
//#else
import menu.MenuListener;
import menu.Command;
//#endif
import javax.microedition.lcdui.Displayable;
import midlet.Commands;
import ui.VirtualElement;
import ui.VirtualList;
//import ui.reconnectWindow;
//#ifdef GRAPHICS_MENU
import ui.GMenu;
import ui.GMenuConfig;
//#ifdef CLIPBOARD
import util.ClipBoard;
//#endif
//#endif

public abstract class MessageList extends VirtualList
    implements
//#ifndef MENU_LISTENER
//#         CommandListener
//#else
        MenuListener
//#endif
    {

    protected final Vector messages = new Vector(0);

    /** Creates a new instance of MessageList */

    public void destroy() {
        super.destroy();
        //System.out.println("    :::MessageList msgList->removeAllMessages");
        messages.removeAllElements();
    }

    public MessageList() {
        super();
//#ifdef MENU_LISTENER
        menuCommands.removeAllElements();
//#endif

//#ifdef SMILES
        smiles=midlet.BombusQD.cf.smiles;
//#else
//#         smiles=false;
//#endif
        enableListWrapping(false);


        cursor=0;//activate
    }

    protected abstract int getItemCount();

    public VirtualElement getItemRef(int index) {
        if (messages.size()<getItemCount()) messages.setSize(getItemCount());
        MessageItem mi = (MessageItem)messages.elementAt(index);
        if (mi==null) {
            mi = new MessageItem(getMessage(index), smiles);
            mi.setEven( (index & 1) == 0);
            mi.parse(this);
            messages.setElementAt(mi, index);
        }
        return mi;
    }

    protected final void initItem(Msg msg, int index) {
        //System.out.println(msg);
        if (messages.size()<getItemCount()) {
            messages.setSize(getItemCount());//?
        }
        MessageItem mi = new MessageItem(msg, smiles);
        mi.setEven( (index & 1) == 0);
        mi.parse(this);
        //mi.getColor();
        messages.setElementAt(mi, index);
    }

    protected abstract Msg getMessage(int index);

    public Msg replaceNickTags(Msg msg){
         return util.StringUtils.replaceNickTags(msg);
    }

    protected boolean smiles;

    public void addDefaultCommands() {
//#ifdef CLIPBOARD
        if (getItemCount() != 0) {
            if (Config.useClipBoard) {
                addCommand(Commands.cmdCopy);
                if (!ClipBoard.isEmpty()) {
                    addCommand(Commands.cmdCopyPlus);
                }
            }
        }
//#endif
        if (getItemCount() != 0) {
            if (hasScheme()) {
                addCommand(Commands.cmdxmlSkin);
            }
            if (hasUrl()) {
                addCommand(Commands.cmdUrl);
            }          
        }
    }

    public void commandAction(Command c, Displayable d) {
        MessageItem item = (MessageItem)getFocusedObject();
        if (c == Commands.cmdUrl) {
            Vector urls = (item).getUrlList();
            if (urls != null) {
                new MessageUrl(urls).show();
            }
        } else if (c == Commands.cmdxmlSkin) {
             ColorTheme.loadSkin(((MessageItem)getFocusedObject()).msg.body, 2, true);
        }
//#ifdef CLIPBOARD
        else if(c == Commands.cmdCopy) {
            ClipBoard.add(replaceNickTags(item.msg));
        } else  if (c == Commands.cmdCopyPlus) {
            ClipBoard.append(replaceNickTags(item.msg));
        }
//#endif
    }

    protected void keyPressed(int keyCode) { // overriding this method to avoid autorepeat
//#ifdef SMILES
        if (keyCode=='*') {
            try {
                ((MessageItem)getFocusedObject()).toggleSmiles(this);
            } catch (Exception e){}
            return;
        }
//#endif
       super.keyPressed(keyCode);
    }

//#ifdef MENU_LISTENER

//#ifdef GRAPHICS_MENU
    public int showGraphicsMenu() {
        commandState();
        menuItem = new GMenu(this, menuCommands);
        GMenuConfig.getInstance().itemGrMenu = GMenu.MESSAGE_LIST;
        return GMenu.MESSAGE_LIST;
    }
//#else
//#     public void showMenu() {
//#         commandState();
//#         String capt="";
//#         try {
//#             capt=getMainBarItem().elementAt(0).toString();
//#         } catch (Exception ex){ }
//#         new MyMenu(display, parentView, this, capt, null, menuCommands);
//#    }
//#endif
//#endif

    protected boolean hasScheme() {
        if (0 == getItemCount()) {
            return false;
        }
        String body = getMessage(cursor).body;
        if (body.indexOf("xmlSkin") > -1) {
            return true;
        }
        return false;
    }

    protected boolean hasUrl() {
        if (0 == getItemCount()) {
            return false;
        }
        String body = getMessage(cursor).body;
        if (-1 != body.indexOf("http://")) {
            return true;
        }
        if (-1 != body.indexOf("https://")) {
            return true;
        }
        if (-1 != body.indexOf("ftp://")) {
            return true;
        }
        if (-1 != body.indexOf("tel:")) {
            return true;
        }
        if (-1 != body.indexOf("native:")) {
            return true;
        }
        return false;
    }

    public void commandState() {}
}
