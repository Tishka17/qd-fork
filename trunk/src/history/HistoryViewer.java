/*
 * HistoryViewer.java
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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA *
 */

//#ifdef HISTORY
package history;

import client.Config;
import client.Contact;
import client.Msg;
import images.MenuIcons;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Vector;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextField;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;
import locale.SR;
import menu.Command;
import message.MessageList;
import midlet.Commands;
import ui.MainBar;
import ui.controls.PopUp;
import ui.input.InputTextBox;
import ui.input.InputTextBoxNotify;
//#ifdef CLIPBOARD
import util.ClipBoard;
//#endif

public class HistoryViewer extends MessageList implements Runnable, InputTextBoxNotify {
    private static final String RECENT_LIST_ID = "history-srch";

    private Vector elements;
    private RecordStore store;
    private String storeName;

    private Thread thread;

    private Command cmdFind;
    private Command cmdClear;

    public HistoryViewer(Contact contact) {
        super();

        elements = new Vector();

        cmdFind = new Command(SR.get(SR.MS_SEARCH), Command.SCREEN, 0);
        cmdFind.setImg(MenuIcons.ICON_SEARCH);

        cmdClear = new Command(SR.get(SR.MS_CLEAR), Command.SCREEN, 0);
        cmdClear.setImg(MenuIcons.ICON_CLEAR);

        setCommandListener(this);

        setMainBarItem(new MainBar(SR.get(SR.MS_HISTORY)));

        loadHistory(contact);
    }

    private void loadHistory(Contact contact) {
        this.storeName = HistoryStorage.getRSName(contact.bareJid);

        try {
            this.store = RecordStore.openRecordStore(storeName, true);

            thread = new Thread(this);
            thread.start();
        } catch (RecordStoreException e) {
            closeRecordStore();
        }
    }

    public void run() {
        int storeSize = 0;

        try {
            for (RecordEnumeration e = store.enumerateRecords(null, null, false); e.hasNextElement();) {
                try {
                    byte buf[] = e.nextRecord();
                    ByteArrayInputStream bais = new ByteArrayInputStream(buf);
                    DataInputStream dis = new DataInputStream(bais);

                    byte msgtype = dis.readByte();
                    String from = dis.readUTF();
                    String date = dis.readUTF();
                    String text = dis.readUTF();

                    Msg msg = new Msg(msgtype, from, null, text);
                    msg.setDayTime(date);
                    elements.insertElementAt(msg, 0);

                    ++storeSize;
                } catch (RecordStoreException rse) {
                } catch (IOException ioe) {
                }
            }
            closeRecordStore();
        } catch (RecordStoreNotOpenException e) {}
        setMainBarItem(new MainBar(SR.get(SR.MS_HISTORY) + " [" + storeSize + "]"));
        redraw();
    }

    private int findString(String query) {
        for (int i = (cursor + 1); i < getItemCount(); ++i) {
            Msg msg = getMessage(i);
            if (msg.body.indexOf(query) >= 0) {
                return i;
            }
        }
        return -1;
    }

    private boolean deleteHistory() {
        try {
            RecordStore.deleteRecordStore(storeName);
            return true;
        } catch (RecordStoreException e) {}
        return false;
    }

    private void closeRecordStore() {
        try {
            store.closeRecordStore();
        } catch (RecordStoreException e) {}
    }

    public void commandState() {
        menuCommands.removeAllElements();
        addCommand(cmdFind);
//#ifdef CLIPBOARD
        if (Config.useClipBoard) {
            addCommand(Commands.cmdCopy);
            if (!ClipBoard.isEmpty()) {
                addCommand(Commands.cmdCopyPlus);
            }
        }
//#endif
        addCommand(cmdClear);
    }

    public int getItemCount() {
        return elements.size();
    }

    public Msg getMessage(int index) {
        return (Msg)elements.elementAt(index);
    }

    public void commandAction(Command c, Displayable d) {
        if (c == cmdFind) {
            InputTextBox input = new InputTextBox(SR.get(SR.MS_SEARCH), null, RECENT_LIST_ID, 200, TextField.ANY);
            input.setNotifyListener(this);
            input.show();
        } else if (c == cmdClear) {
            if (deleteHistory()) {
                destroyView();
            } else {
                setWobble(PopUp.TYPE_SYSTEM, null, SR.get(SR.MS_ERROR));
            }
        } else {
            super.commandAction(c, d);
        }
    }

    public void okNotify(String text) {
        if (text.length() > 0) {
            int index = findString(text);
            if (index >= 0) {
                moveCursorTo(index);
            } else {
                setWobble(PopUp.TYPE_SYSTEM, null, SR.get(SR.MS_NOT_FOUND));
            }
        }
    }

    public void destroyView() {
        if (thread.isAlive()) {
            thread.interrupt();
        }
        closeRecordStore();
        super.destroyView();
    }
}
//#endif
