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

import client.Contact;
import client.Msg;
import images.MenuIcons;
//#ifdef FILE_IO
import io.file.browse.Browser;
import io.file.browse.BrowserListener;
//#endif
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Vector;
import javax.microedition.lcdui.TextField;
import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;
import locale.SR;
import menu.Command;
import message.MessageList;
import ui.GMenu;
import ui.GMenuConfig;
import ui.MainBar;
import ui.controls.PopUp;
import ui.input.InputTextBox;
import ui.input.InputTextBoxNotify;
import util.StringUtils;
import util.Time;

public class HistoryViewer extends MessageList
        implements Runnable, InputTextBoxNotify
//#ifdef FILE_IO
        , BrowserListener
//#endif
{

    private static final String RECENT_LIST_ID = "history-srch";

    private Vector elements;
    private RecordStore store;
    private String storeName;

    private Thread thread;

    private Command cmdFind;
    private Command cmdClear;
//#ifdef FILE_IO
    private Command cmdExport;
//#endif

    public HistoryViewer(Contact contact) {
        this(HistoryStorage.getRSName(contact.bareJid));
    }

    public HistoryViewer(String storeName) {
        super();

        this.storeName = storeName;

        elements = new Vector();

        cmdFind = new Command(SR.get(SR.MS_SEARCH), MenuIcons.ICON_SEARCH);
        cmdClear = new Command(SR.get(SR.MS_CLEAR), MenuIcons.ICON_CLEAR);
//#ifdef FILE_IO
        cmdExport = new Command(SR.get(SR.MS_SAVE_TO_FILE), MenuIcons.ICON_SAVE);
//#endif

        setMainBarItem(new MainBar(SR.get(SR.MS_HISTORY)));
    }

    public void show() {
        super.show();
        loadHistory();
    }

    private void loadHistory() {
        try {
            this.store = RecordStore.openRecordStore(storeName, true);

            thread = new Thread(this);
            thread.start();
        } catch (RecordStoreException e) {
            closeRecordStore();
        }
    }

    public void run() {
        try {
            int size = store.getNumRecords();
            for (int i = 1; i <= size; ++i) {
                try {
                    byte buf[];

                    try {
                        buf = store.getRecord(i);
                    } catch (InvalidRecordIDException e) {
//#ifdef DEBUG
//#                         System.out.println(i + " record doesn't exist, skipping...");
//#endif
                        continue;
                    }

                    ByteArrayInputStream bais = new ByteArrayInputStream(buf);
                    DataInputStream dis = new DataInputStream(bais);
                    byte msgtype = dis.readByte();
                    String from = dis.readUTF();
                    long date = dis.readLong();
                    String text = dis.readUTF();

                    Msg msg = new Msg(msgtype, from, null, text);
                    msg.setDayTime(date);
                    elements.addElement(msg);
                } catch (RecordStoreException ex) {
                } catch (IOException ioe) {}
            }
            closeRecordStore();
        } catch (RecordStoreNotOpenException e) {}
        setMainBarItem(new MainBar(SR.get(SR.MS_HISTORY) + " [" + elements.size() + "]"));
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
        addDefaultCommands();
//#ifdef FILE_IO
        addCommand(cmdExport);
//#endif
        addCommand(cmdClear);
    }

    public int getItemCount() {
        return elements.size();
    }

    public Msg getMessage(int index) {
        return (Msg)elements.elementAt(index);
    }

    public void commandAction(Command c) {
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
//#ifdef FILE_IO
        } else if (c == cmdExport) {
            new Browser(null, this, true).show();
//#endif
        } else {
            super.commandAction(c);
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

//#ifdef FILE_IO
    public void BrowserFilePathNotify(String path) {
        StringBuffer buf = new StringBuffer(path);

        buf.append(StringUtils.replaceBadChars(storeName));
        buf.append("_");
        buf.append(Time.localDate());
        buf.append("_");
        buf.append(Time.localTime());
        buf.append(".txt");

        HistoryExportTask task = new HistoryExportTask(elements, buf.toString());
        task.start();
    }
//#endif

    public void destroyView() {
        if (thread.isAlive()) {
            thread.interrupt();
        }
        closeRecordStore();
        super.destroyView();
    }

    public int showGraphicsMenu() {
        commandState();
        menuItem = new GMenu(this, menuCommands);
        GMenuConfig.getInstance().itemGrMenu = GMenu.MESSAGE_LIST;
        return GMenu.MESSAGE_LIST;
    }
}
//#endif
