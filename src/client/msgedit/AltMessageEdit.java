/*
 * AltMessageEdit.java

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
import conference.MucContact;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;
import javax.microedition.lcdui.Ticker;
import midlet.BombusQD;
import util.ClipBoard;

public final class AltMessageEdit extends BaseMessageEdit {
    private Form form;
    private TextField input;

    public AltMessageEdit() {
        form = new Form("");

        int maxSize = MAX_EDITOR_SIZE;
        input = new TextField("Message Edit", null, maxSize, TextField.ANY);
        try {
            maxSize = input.setMaxSize(MAX_EDITOR_SIZE);
            if (body != null) {
                if (body.length() > maxSize) {
                    body = body.substring(0, maxSize - 1);
                }
                input.setString(body);
            }
        } catch (Exception e) {}

        if (Config.getInstance().capsState) {
            input.setConstraints(TextField.INITIAL_CAPS_SENTENCE);
        }
        form.append(input);

//#ifdef RUNNING_MESSAGE
        if (midlet.BombusQD.cf.runningMessage) {
            ticker = new Ticker("BombusQQ");
            form.setTicker(ticker);
        }
//#endif

        if (midlet.BombusQD.cf.capsState) {
            input.setConstraints(TextField.INITIAL_CAPS_SENTENCE);
        }

        form.addCommand(cmdSend);
        form.addCommand(cmdInsMe);
//#ifdef SMILES
        form.addCommand(cmdSmile);
//#endif
        form.addCommand(cmdTranslate);

//#ifdef DETRANSLIT
//#         form.addCommand(cmdSendInTranslit);
//#         form.addCommand(cmdSendInDeTranslit);
//#endif
        form.addCommand(cmdSuspend);
        form.addCommand(cmdCancel);
//#ifdef ARCHIVE
//#ifdef PLUGINS
//#         if (StaticData.getInstance().Archive)
//#endif
        form.addCommand(cmdPaste);
//#endif
        form.setCommandListener(this);
    }

    public String getString() {
        return input.getString();
    }

    public Object getInput() {
        return input;
    }

    public int getCaretPosition() {
        return input.getCaretPosition();
    }

    public void insert(int pos, String text) {
        input.insert(text, pos);
    }

    public void setString(String text) {
        input.setString(text);
     }

    public void show(Contact to, String body) {
        this.body = body;
        this.to = to;

        boolean phoneSONYE = (midlet.BombusQD.cf.phoneManufacturer == Config.SONYE);
        System.gc();

        if (!phoneSONYE) {
            form.setTitle(null == to ? "Multi-Message" : to.toString());
        }
        //textField.setConstraints(2);//just magic for clearing attributes
        //textField.setConstraints(0);
        input.setString("");
        if (body != null) {
            input.insert(body, 0);
        }
//#ifdef CLIPBOARD
        if (Config.useClipBoard && ClipBoard.isEmpty()) {
            form.addCommand(cmdPasteText);
        }
//#endif
        if (null != to) {
            if (to instanceof MucContact) {
                form.addCommand(cmdInsNick);
            } else {
                form.removeCommand(cmdInsNick);
            }
            if (to.origin == Contact.ORIGIN_GROUPCHAT) {
                form.addCommand(cmdSubj);
            } else {
                form.removeCommand(cmdSubj);
            }
            if (to.lastSendedMessage != null) {
                form.addCommand(cmdLastMessage);
            }
        }
        BombusQD.setCurrentView(form);
        if (!multiMessage) {
            composing = true;
            send(null, null);
        }
    }

}
