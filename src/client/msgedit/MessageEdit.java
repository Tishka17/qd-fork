/*
 * MessageEdit.java

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

package client.msgedit;

import client.Config;
import client.Contact;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import javax.microedition.lcdui.Ticker;
import midlet.BombusQD;
import util.ClipBoard;

public final class MessageEdit extends BaseMessageEdit {
    private TextBox input;

    public MessageEdit() {
        super();

        if (midlet.BombusQD.cf.runningMessage) {
            input = new TextBox("", body, MAX_EDITOR_SIZE, TextField.ANY);
            ticker = new Ticker("BombusQD");
            input.setTicker(ticker);
        } else {
            int maxSize = MAX_EDITOR_SIZE;
            input = new TextBox("", null, maxSize, TextField.ANY);
            try {
                maxSize = input.setMaxSize(MAX_EDITOR_SIZE);
                if (body != null) {
                    if (body.length() > maxSize) {
                        body = body.substring(0, maxSize - 1);
                    }
                    input.setString(body);
                }
            } catch (Exception e) {}
        }

        input.addCommand(cmdSend);
        input.addCommand(cmdInsMe);
//#ifdef SMILES
        input.addCommand(cmdSmile);
//#endif
        input.addCommand(cmdTranslate);

//#ifdef DETRANSLIT
//#         input.addCommand(cmdSendInTranslit);
//#         input.addCommand(cmdSendInDeTranslit);
//#endif
        input.addCommand(cmdSuspend);
        input.addCommand(cmdCancel);
//#ifdef ARCHIVE
//#ifdef PLUGINS
//#         if (StaticData.getInstance().Archive)
//#endif
        input.addCommand(cmdPaste);
//#endif
        input.setCommandListener(this);
    }

    public String getString() {
        return input.getString();
    }

    public int getCaretPosition() {
        return input.getCaretPosition();
    }

    public void insert(int pos, String text) {
        input.insert(text, pos);
    }

    public Object getInput() {
        return input;
    }

    public void setString(String text) {
        input.setString(text);
    }

    public void show(Contact to, String body) {
        this.to = to;
        this.body = body;

        boolean phoneSONYE = (midlet.BombusQD.cf.phoneManufacturer == Config.SONYE);
        System.gc();

        if (!phoneSONYE) {
            input.setTitle(null == to ? "Multi-Message" : to.toString());
        }
        //t.setConstraints(2);//just magic for clearing attributes
        //t.setConstraints(0);
        input.setString("");
        if (body != null) {
            input.insert(body, 0);
        }
        if (midlet.BombusQD.cf.capsState) {
            input.setConstraints(TextField.INITIAL_CAPS_SENTENCE);
        }
//#ifdef CLIPBOARD
        if (midlet.BombusQD.cf.useClipBoard && !ClipBoard.isEmpty()) {
            input.addCommand(cmdPasteText);
        }
//#endif
        if (null != to) {
            if (to.origin >= 4) {
                input.addCommand(cmdInsNick);
            } else {
                input.removeCommand(cmdInsNick);
            }
            if (to.origin == 4) {
                input.addCommand(cmdSubj);
            } else {
                input.removeCommand(cmdSubj);
            }
            if (to.lastSendedMessage != null) {
                input.addCommand(cmdLastMessage);
            }
        }
        BombusQD.setCurrentView(input);

        if (!multiMessage) {
            composing = true;
            send(null, null);
        }
    }
}
