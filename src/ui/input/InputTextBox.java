/*
 * InputTextBox.java
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

package ui.input;

import client.Config;
import io.NvStorage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import locale.SR;
import midlet.BombusQD;
import ui.CanvasEx;
//#ifdef CLIPBOARD
import util.ClipBoard;
//#endif

public class InputTextBox extends TextBox implements CommandListener {
    private static final int MAX_RECENT_ELEMENTS = 10;

    protected Object parentView;

    protected Command cmdOk;
    protected Command cmdCancel;

    protected Command cmdRecent;

//#ifdef CLIPBOARD
    protected Command cmdCopy;
    protected Command cmdCopyPlus;
    protected Command cmdPasteText;
//#endif

    protected InputTextBoxNotify notify;

    private String id;
    private Vector recentList;

    public InputTextBox(String caption, String text, String id, int len, int mode) {
        super(caption, text, len, mode);

        this.id = id;
        if (id != null) {
            loadRecentList();
        }
 
        if (Config.getInstance().capsState) {
            setConstraints(TextField.INITIAL_CAPS_SENTENCE);
        }

        if (Config.swapSendAndSuspend) {
            cmdOk = new Command(SR.get(SR.MS_OK), Command.BACK, 1);
            cmdCancel = new Command(SR.get(SR.MS_CANCEL), Command.SCREEN, 99);
        } else {
            cmdOk = new Command(SR.get(SR.MS_OK), Command.OK,1);
            cmdCancel = new Command(SR.get(SR.MS_CANCEL), Command.BACK, 99);
        }

//#ifdef CLIPBOARD
        cmdCopy = new Command(SR.get(SR.MS_COPY), Command.SCREEN, 3);
        cmdCopyPlus = new Command("+ " + SR.get(SR.MS_COPY), Command.SCREEN, 4);
        cmdPasteText = new Command(SR.get(SR.MS_PASTE), Command.SCREEN, 5);
//#endif

        cmdRecent = new Command(SR.get(SR.MS_RECENT), Command.SCREEN, 2);

        addCommand(cmdOk);
        addCommand(cmdCancel);

        if (id != null && !recentList.isEmpty()) {
            addCommand(cmdRecent);
        }

//#ifdef CLIPBOARD
        if (Config.useClipBoard) {
            addCommand(cmdCopy);
            if (!ClipBoard.isEmpty()) {
                addCommand(cmdCopyPlus);
                addCommand(cmdPasteText);
            }
        }
//#endif
    }

    public InputTextBox(String caption, String text, int len, int mode) {
        this(caption, text, null, len, mode);
    }

    public void setNotifyListener(InputTextBoxNotify notify) {
        this.notify = notify;
    }

    public void setParentView(Displayable parentView) {
        this.parentView = parentView;
    }

    public void show() {
        setCommandListener(this);

        if (parentView == null) {
            parentView = BombusQD.getCurrentView();
        }
        BombusQD.setCurrentView(this);
    }

    public void destroyView() {
        BombusQD.setCurrentView(parentView);
    }

    public void commandAction(Command c, Displayable d) {
        if (c == cmdOk) {
            destroyView();
            if (notify != null) {
                notify.okNotify(getString());
            }

            if (id != null && getString().length() > 0) {
                addToRecentList(getString());
                saveRecentList();
            }
        } else if (c == cmdCancel) {
            destroyView();
//#ifdef CLIPBOARD
        } else if (c == cmdCopy) {
            ClipBoard.setClipBoard(getString());

            addCommand(cmdCopyPlus);
            addCommand(cmdPasteText);
        } else if (c == cmdCopyPlus) {
            ClipBoard.addToClipBoard(getString());
        } else  if (c == cmdPasteText) {
            insert(ClipBoard.getClipBoard(), getCaretPosition());
//#endif
        } else if (c == cmdRecent) {
            new RecentInputList(this).show();
        }
    }
    
//#ifdef CLIPBOARD
    public int getCaretPosition() {
        int caretPos = super.getCaretPosition();
        // +MOTOROLA STUB
        if (Config.getInstance().phoneManufacturer == Config.MOTO) {
            caretPos = -1;
        }
        if (caretPos < 0) {
            caretPos = getString().length();
        }
        return caretPos;
    }
//#endif

    private void loadRecentList() {
        recentList = new Vector(0);
        try {
            DataInputStream is = NvStorage.ReadFileRecord(id, 0);
            if (is == null) {
                return;
            }

            try {
                while (true) {
                    recentList.addElement(is.readUTF());
                }
            } catch (EOFException e) {
                is.close();
                is = null;
            }
        } catch (IOException e) {
//#if DEBUG
//#             e.printStackTrace();
//#endif
        }
    }

    private void addToRecentList(String text) {
        if (!recentList.isEmpty()) {
            String prev = (String)recentList.firstElement();
            if (prev.equals(text)) {
                return;
            }
        }       

        recentList.insertElementAt(text, 0);
        if (recentList.size() > MAX_RECENT_ELEMENTS) {
            recentList.removeElementAt(MAX_RECENT_ELEMENTS);
        }        
    }

    public final Vector getRecentList() {
        return recentList;
    }

    public final void clearRecentList() {
        recentList = null;
        recentList = new Vector(0);
        removeCommand(cmdRecent);

        saveRecentList();
    }

    private void saveRecentList() {
        DataOutputStream os = NvStorage.CreateDataOutputStream();
        try {
            for (int i = 0; i < recentList.size(); ++i) {
                os.writeUTF((String)recentList.elementAt(i));
            }
        } catch (Exception e) {}

        NvStorage.writeFileRecord(os, id, 0, true);
    }
}
