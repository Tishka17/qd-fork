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
//#ifdef FILE_IO
import io.file.browse.Browser;
import io.file.browse.BrowserListener;
//#endif
import javax.microedition.lcdui.TextField;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;
import locale.SR;
import menu.Command;
import message.MessageList;
import ui.GMenu;
import ui.GMenuConfig;
import ui.MainBar;
//#ifdef POPUPS
import ui.controls.AlertBox;
import ui.controls.PopUp;
//#endif
import ui.input.InputTextBox;
import ui.input.InputTextBoxNotify;
//#ifdef FILE_IO
import util.StringUtils;
import ui.VirtualCanvas;
import io.VirtualStore;
//#endif

public class HistoryViewer extends MessageList
        implements Runnable, InputTextBoxNotify
//#ifdef FILE_IO
        , BrowserListener
//#endif
{

    private static final String RECENT_LIST_ID = "history-srch";

    private RecordStore store;
    private String storeName;
    private VirtualStore vstore;

    private Thread thread;

    private static int HistBlkPos; // current history block (first message, from 1)
    private static int HistBlkSize;
    private int HistSize; 

    private Command cmdPrevBlk;
    private Command cmdNextBlk;

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

        cmdPrevBlk= new Command("View prev block", MenuIcons.ICON_SEARCH);
        cmdNextBlk = new Command("View next block", MenuIcons.ICON_CLEAR);

        cmdFind = new Command(SR.get(SR.MS_SEARCH), MenuIcons.ICON_SEARCH);
        cmdClear = new Command(SR.get(SR.MS_CLEAR), MenuIcons.ICON_CLEAR);
//#ifdef FILE_IO
        cmdExport = new Command(SR.get(SR.MS_SAVE_TO_FILE), MenuIcons.ICON_SAVE);
//#endif

        setMainBarItem(new MainBar(SR.get(SR.MS_HISTORY)));
        HistBlkPos = -1;
        HistBlkSize = 20;
    }

    public void show() {
        super.show();
        loadHistory();
    }

    private void loadHistory() {
        if( Config.historyTypeIndex ==Config.HISTORY_RMS){
            try {
                this.store = RecordStore.openRecordStore(storeName, true);
            } catch (RecordStoreException e) {
                closeRecordStore();
            }
        } else{
//#ifdef FILE_IO
            try {
                 this.vstore= new VirtualStore();
                 this.vstore.openVirtualStore( storeName, true);
            } catch (RecordStoreException o) { }
//#endif
        }// if

        thread = new Thread(this);
        thread.start();
    }

    public void run() {
        ShowMessages();
    }

    private void ShowMessages(){
        if( Config.historyTypeIndex ==Config.HISTORY_RMS){
            try {
                HistSize = store.getNumRecords();
                if (HistBlkPos<0) {HistBlkPos=lastPage();}
                Msg msg = null;
                messages.removeAllElements();
                for (int i = Math.max(1, HistBlkPos); i <= Math.min(HistSize, HistBlkPos +HistBlkSize); i++) {
                        msg = HistoryStorage.readRMSMessage(store, i);
                        if (msg != null) {
                                messages.addElement(msg);
                        }
                }
            } catch (RecordStoreNotOpenException e) {}
        }else{
            try {
                HistSize = vstore.getNumRecords();
                if (HistBlkPos<0) {HistBlkPos=lastPage();}
//#if DEBUG
//#                 System.out.println(HistSize + " total records from VirtualStore");
//#endif
                Msg msg = null;
                messages.removeAllElements();
                for (int i = Math.max(1, HistBlkPos); i <= Math.min(HistSize, HistBlkPos +HistBlkSize); i++) {
//#if DEBUG
//#                         System.out.println("Reading record " +i +" from VirtualStore");
//#endif
                        msg = HistoryStorage.readVSMessage(vstore, i);
                        if (msg != null) {
                                messages.addElement(msg);
                        }
                }
            } catch (RecordStoreNotOpenException e) {}
        }// ifel
        int to=Math.min(HistBlkPos +HistBlkSize, HistSize);
        setMainBarItem(new MainBar(SR.get(SR.MS_HISTORY) +" " +HistBlkPos + " - " + to +" (" +HistSize+")"));
        redraw();
    }

    private int findString(String query) {
        for (int i = (cursor + 1); i < getItemCount(); ++i) {
            Msg msg = getMessage(i);
            if (msg.getBody().indexOf(query) >= 0) {
                return i;
            }
        }
        return -1;
    }

    private boolean deleteHistory() {
        try {
            if( Config.historyTypeIndex ==Config.HISTORY_RMS)
                RecordStore.deleteRecordStore(storeName);
            else
                VirtualStore.deleteVirtualStore(storeName);
            return true;
        } catch (RecordStoreException e) {}
        return false;
    }

    private void closeRecordStore() {
        try {
            if( Config.historyTypeIndex ==Config.HISTORY_RMS)
                store.closeRecordStore();
            else
                vstore.closeVirtualStore();
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
        addCommand(cmdPrevBlk);
        addCommand(cmdNextBlk);
    }

    public int getItemCount() {
        return messages.size();
    }

    public void commandAction(Command c) {
        if (c == cmdFind) {
            InputTextBox input = new InputTextBox(SR.get(SR.MS_SEARCH), null, RECENT_LIST_ID, 200, TextField.ANY);
            input.setNotifyListener(this);
            input.show();
        } else if (c == cmdClear) {
            final HistoryViewer view = this;
            AlertBox box = new AlertBox("", SR.get(SR.MS_DELETE_HISTORY), AlertBox.BUTTONS_YESNO) {
                public void yes() {
                    if (deleteHistory()) {
                        view.destroyView();
                    } else {
//#ifdef POPUPS
                        setWobble(PopUp.TYPE_SYSTEM, null, SR.get(SR.MS_ERROR));
//#endif
                    }
                }
            };
            box.show();
//#ifdef FILE_IO
        } else if (c == cmdExport) {
            new Browser(null, this, true).show();
//#endif
        } else if (c == cmdPrevBlk) {
            if( HistBlkPos >HistBlkSize)
                HistBlkPos-= HistBlkSize;
            else
                HistBlkPos= 1;
            cursor = 0;
            ShowMessages();
        } else if (c == cmdNextBlk) {
            if(HistBlkPos+HistBlkSize < HistSize)
                HistBlkPos += HistBlkSize;
            else 
                HistBlkPos = lastPage();
            cursor = 0;
            ShowMessages();
        } else {
            super.commandAction(c);
        }
    }

    private int lastPage() {
        int mod = HistSize%HistBlkSize;
        if (mod==0) {
            return HistSize-HistBlkPos+1;
        } else 
            return HistSize-mod+1;
    }
    public void okNotify(String text) {
        if (text.length() > 0) {
            int index = findString(text);
            if (index >= 0) {
                moveCursorTo(index);
            } else {
//#ifdef POPUPS
                setWobble(PopUp.TYPE_SYSTEM, null, SR.get(SR.MS_NOT_FOUND));
//#endif
            }
        }
    }

//#ifdef FILE_IO
    public void BrowserFilePathNotify(String path) {
        String fname = StringUtils.getFileName(path);
        HistoryExportTask task = new HistoryExportTask(messages, fname);
        task.start();
    }
//#endif

    public void destroyView() {
        closeRecordStore();
        super.destroyView();
    }

    public int showGraphicsMenu() {
        commandState();
        menuItem = new GMenu(this, menuCommands);
        GMenuConfig.getInstance().itemGrMenu = GMenu.MESSAGE_LIST;
        return GMenu.MESSAGE_LIST;
    }

    public void keyPressed(int keyCode){    
        if (sendEvent(keyCode)) {
            redraw();
            return;
        }
        switch (keyCode) {
            case VirtualCanvas.NAVIKEY_LEFT:
            case VirtualCanvas.KEY_NUM4:
                commandAction(cmdPrevBlk);
                break;
            case VirtualCanvas.NAVIKEY_RIGHT:
            case VirtualCanvas.KEY_NUM6:
                commandAction(cmdNextBlk);
                break;
            default: 
                super.keyPressed(keyCode);
        }// switch
    }// keyPressed
}
//#endif
