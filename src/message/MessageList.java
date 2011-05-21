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
import menu.MenuListener;
import menu.Command;
import midlet.Commands;
import ui.VirtualElement;
import ui.VirtualList;
import ui.GMenu;
import ui.GMenuConfig;
//#ifdef CLIPBOARD
import util.ClipBoard;
//#endif
import util.StringUtils;

public abstract class MessageList extends VirtualList implements MenuListener {
    protected boolean smiles;

    protected final Vector messages = new Vector(0);

    public MessageList() {
        super();

//#ifdef SMILES
        smiles=midlet.BombusQD.cf.smiles;
//#else
//#         smiles=false;
//#endif
        enableListWrapping(false);
    }

    public void destroy() {
        messages.removeAllElements();
    }

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

    protected abstract void commandState();
    protected abstract int getItemCount();
    protected abstract Msg getMessage(int index);

    protected void addDefaultCommands() {
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

    public void commandAction(Command c) {
        MessageItem item = (MessageItem)getFocusedObject();
        if (c == Commands.cmdUrl) {
            Vector urls = (item).getUrlList();
            if (urls != null) {
                new MessageUrl(urls).show();
            }
        } else if (c == Commands.cmdxmlSkin) {
             ColorTheme.loadSkin(item.msg.body, 2, true);
        }
//#ifdef CLIPBOARD
        else if(c == Commands.cmdCopy) {
            ClipBoard.setClipBoard(msg2str(item.msg));
        } else  if (c == Commands.cmdCopyPlus) {
            ClipBoard.addToClipBoard(msg2str(item.msg));
        }
//#endif
    }

//#ifdef CLIPBOARD
    private String msg2str(Msg msg) {
        StringBuffer buf = new StringBuffer();

        if (msg.subject != null) {
            buf.append(StringUtils.replaceNickTags(msg.subject));
            buf.append("\n");
        }
        buf.append(StringUtils.replaceNickTags(msg.toString()));
        buf.append("\n");

        return buf.toString();
    }
//#endif

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

    public int showGraphicsMenu() {
        commandState();
        menuItem = new GMenu(this, menuCommands);
        GMenuConfig.getInstance().itemGrMenu = GMenu.MESSAGE_LIST;
        return GMenu.MESSAGE_LIST;
    }

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
}
