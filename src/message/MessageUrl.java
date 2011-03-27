/*
 * MessageUrl.java
 *
 * Created on 22.12.2005, 3:01
 * Copyright (c) 2005-2008, Eugene Stahov (evgs), http://bombus-im.org
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
import java.util.Vector;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextField;
import locale.SR;
import menu.Command;
import menu.Menu;
import menu.MenuListener;
import midlet.Commands;
import ui.GMenu;
import ui.GMenuConfig;
import ui.InputTextBox;
import util.ClipBoard;

/**
 *
 * @author EvgS
 */

public class MessageUrl extends Menu implements MenuListener {
    private Vector urlList;

    private Command cmdEdit;

    public MessageUrl(Display display, Vector urlList) {
	super("URLs", null, null);
	this.urlList = urlList;
	
	for (int i = 0; i < urlList.size(); ++i) {
	    addItem((String)urlList.elementAt(i), i);
	}

        cmdEdit = new Command(SR.get(SR.MS_EDIT), Command.ITEM, 0);

	attachDisplay(display);
    }

    private void commandState() {
        menuCommands.removeAllElements();

        addCommand(cmdEdit);
//#ifdef CLIPBOARD
        if (Config.useClipBoard) {
            addCommand(Commands.cmdCopy);
            if (!ClipBoard.isEmpty()) {
                addCommand(Commands.cmdCopyPlus);
            }
        }
//#endif
    }

    public void commandAction(Command c, Displayable d) {
        String url = (String)urlList.elementAt(cursor);

        if (c == cmdEdit) {
            InputTextBox input = new InputTextBox("Edit URL", url, 1024, TextField.URL);
            input.show();
//#ifdef CLIPBOARD
        } else if (c == Commands.cmdCopy) {
            ClipBoard.setClipBoard(url);
        } else if (c == Commands.cmdCopyPlus) {
            ClipBoard.addToClipBoard(url);
//#endif
        }
    }

    public String touchLeftCommand() {
        return SR.get(SR.MS_MENU);
    }

    public void touchLeftPressed(){
        showGraphicsMenu();
    }

    public int showGraphicsMenu() {
        commandState();
        menuItem = new GMenu(display, parentView, this, null, menuCommands);
        GMenuConfig.getInstance().itemGrMenu = GMenu.STATS_WINDOW;
        redraw();
        return GMenu.STATS_WINDOW;
    }
    
    public void eventOk() {
        try {
            midlet.BombusQD.getInstance().platformRequest((String)urlList.elementAt(cursor));
        } catch (ConnectionNotFoundException ex) {
            ex.printStackTrace();
        }
	destroyView();
    }
}
