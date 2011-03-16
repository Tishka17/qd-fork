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
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Vector;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;
import locale.SR;
import menu.Command;
import message.MessageList;
import midlet.Commands;
import ui.MainBar;
//#ifdef CLIPBOARD
import util.ClipBoard;
//#endif

public class HistoryViewer extends MessageList implements Runnable {
    private Vector elements;
    private RecordStore store;
    private String storeName;

    private Thread thread;

    public HistoryViewer(Display display, Displayable pView, Contact contact) {
        super();

        elements = new Vector();

        setCommandListener(this);

        moveCursorHome();

        setMainBarItem(new MainBar(SR.get(SR.MS_historyStr)));

        attachDisplay(display);
        this.parentView = pView;

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
        } catch (RecordStoreNotOpenException e) {
            // empty;
        }
        setMainBarItem(new MainBar(SR.get(SR.MS_historyStr) + " [" + storeSize + "]"));
        redraw();
    }

    private void closeRecordStore() {
        try {
            store.closeRecordStore();
        } catch (RecordStoreException e) {
            // empty;
        }
    }

    public void commandState() {
//#ifdef MENU_LISTENER
        menuCommands.removeAllElements();
//#endif

//#ifndef GRAPHICS_MENU
//#      addCommand(cmdBack);
//#endif
//#ifdef CLIPBOARD
        if (Config.getInstance().useClipBoard) {
            addCommand(Commands.cmdCopy);
            if (!ClipBoard.isEmpty()) {
                addCommand(Commands.cmdCopyPlus);
            }
        }
//#endif
    }

    public int getItemCount() {
        return elements.size();
    }

    public Msg getMessage(int index) {
        return (Msg)elements.elementAt(index);
    }

    public void commandAction(Command c, Displayable d) {
        super.commandAction(c, d);
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
