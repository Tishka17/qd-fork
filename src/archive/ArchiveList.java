/*
 * ArchiveList.java
 *
 * Created on 11.12.2005, 5:24
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

//#ifdef ARCHIVE
package archive;

import client.Msg;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import ui.MainBar;
import message.MessageList;
import menu.Command;
import locale.SR;
import ui.controls.AlertBox;
//#if IMPORT_EXPORT && FILE_IO
import impexp.ImportExportForm;
//#endif
import ui.GMenu;
import ui.GMenuConfig;

/**
 *
 * @author EvgS,aqent
 */

public class ArchiveList extends MessageList {
    private static final int BODY = 0;
    private static final int SUBJECT = 1;
    private static final int JID = 2;

    Command cmdPaste;
    Command cmdJid;
    Command cmdSubj;
    Command cmdEdit;
    Command cmdNew;
    Command cmdDelete;
    Command cmdDeleteAll;
//#ifdef IMPORT_EXPORT
    Command cmdExport;
//#endif
    MessageArchive archive;
    private int caretPos;

    private Object input;

    public ArchiveList() {
        this(-1, null);
    }

    public ArchiveList(int caretPos, Object input) {
        super();

        this.caretPos = caretPos;
        this.input = input;

        cmdPaste = new Command(SR.get(SR.MS_PASTE_BODY), 0x60);
        cmdJid = new Command(SR.get(SR.MS_PASTE_JID), 0x60);
        cmdSubj = new Command(SR.get(SR.MS_PASTE_SUBJECT), 0x81);
        cmdEdit = new Command(SR.get(SR.MS_EDIT), 0x40);
        cmdNew = new Command(SR.get(SR.MS_NEW), 0x47);
        cmdDelete = new Command(SR.get(SR.MS_DELETE), 0x76);
        cmdDeleteAll = new Command(SR.get(SR.MS_DELETE_ALL), 0x41);

//#ifdef IMPORT_EXPORT
        cmdExport = new Command(SR.get(SR.MS_IMPORT_EXPORT), 0x60);
//#endif

        // rewrite this
        archive = new MessageArchive();
        for (int i = 0; i < archive.size(); ++i) {
            messages.addElement(archive.msg(i));
        }

        MainBar bar = new MainBar(SR.get(SR.MS_ARCHIVE));
        bar.addElement(null);
        bar.addRAlign();
        bar.addElement(null);
        bar.addElement(SR.get(SR.MS_FREE));
        setMainBarItem(bar);
    }

    public final void commandState() {
        menuCommands.removeAllElements();

        addCommand(cmdNew);
        if (getItemCount() > 0) {
            addCommand(cmdEdit);

            if (input != null) {
                addCommand(cmdPaste);
                addCommand(cmdJid);
                addCommand(cmdSubj);
            }

            addCommand(cmdDelete);
            addCommand(cmdDeleteAll);
        }

//#ifdef IMPORT_EXPORT
        addCommand(cmdExport);
//#endif

        super.addDefaultCommands();
    }

    protected void beginPaint() {
        getMainBarItem().setElementAt(" (" + getItemCount() + ")", 1);
        getMainBarItem().setElementAt(String.valueOf(getFreeSpace()), 3);
    }

    public void commandAction(Command c) {
        super.commandAction(c);

        if (c == cmdNew) {
            new ArchiveEdit(-1, this).show();
        } else if (c == cmdDelete) {
            keyClear();
        } else if (c == cmdDeleteAll) {
            deleteAllMessages();
            redraw();
        } else if (c == cmdPaste) {
            pasteData(BODY);
        } else if (c == cmdSubj) {
            pasteData(SUBJECT);
        } else if (c == cmdJid) {
            pasteData(SUBJECT);
        } else if (c == cmdEdit) {
            new ArchiveEdit(cursor, this).show();
//#if FILE_IO && IMPORT_EXPORT
        } else if (c == cmdExport) {
            new ImportExportForm().show();
//#endif
        }
    }

    public void reFresh() {
        archive = new MessageArchive();
        messages.removeAllElements();
    }

    private void deleteMessage() {
        archive.delete(cursor);
        messages.removeAllElements();
    }

    private void deleteAllMessages() {
        AlertBox box = new AlertBox(SR.get(SR.MS_ACTION), SR.get(SR.MS_DELETE_ALL) + "?", AlertBox.BUTTONS_YESNO) {
            public void yes() {
                archive.deleteAll();
                messages.removeAllElements();
            }
        };
        box.show();
    }

    private void pasteData(int field) {
        if (input == null) {
            return;
        }
        Msg m = getMessage(cursor);
        if (m == null) {
            return;
        }
        String data;
        switch (field) {
            case SUBJECT:
                data = m.getSubject();
                break;
            case JID:
                data = m.getFrom();
                break;
            default:
                data = m.getBody();
                break;
        }
        if (input instanceof TextBox) {
            ((TextBox)input).insert(data, caretPos);
        } else if (input instanceof TextField) {
            ((TextField)input).insert(data, caretPos);
        }
        destroyView();
    }

    public void keyGreen() {
        pasteData(BODY);
    }

    public void keyClear() {
        if (getItemCount() > 0) {
            AlertBox box = new AlertBox(SR.get(SR.MS_DELETE), SR.get(SR.MS_SURE_DELETE), AlertBox.BUTTONS_YESNO) {
                public void yes() {
                    deleteMessage();
                }
            };
            box.show();
            redraw();
        }
    }

    public void destroyView() {
        super.destroyView();
        archive.close();
    }

    private int getFreeSpace() {
        return archive.freeSpace();
    }

    public int showGraphicsMenu() {
        commandState();
        menuItem = new GMenu(this, menuCommands);
        GMenuConfig.getInstance().itemGrMenu = GMenu.MESSAGE_LIST;
        return GMenu.MESSAGE_LIST;
    }
}
//#endif
