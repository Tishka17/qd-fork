/*
 * BaseMessageEdit.java

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

import archive.ArchiveList;
import client.Config;
import client.Contact;
import client.Msg;
import client.SmilePicker;
import conference.AppendNickForm;
import io.TranslateSelect;
import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import javax.microedition.lcdui.Ticker;
import locale.SR;
import midlet.BombusQD;
import util.ClipBoard;
//#ifdef DETRANSLIT
//# import util.DeTranslit;
//#endif

public abstract class BaseMessageEdit implements CommandListener {
    protected static final int MAX_EDITOR_SIZE = 4096;

    protected String body;
    protected String subj;

    protected boolean multiMessage = false;
    protected boolean composing = true;

    protected Vector activeContacts;
//#ifdef DETRANSLIT
//#     protected Command cmdSendInTranslit;
//#     protected Command cmdSendInDeTranslit;
//#endif
//#ifdef DETRANSLIT
//#     private boolean sendInTranslit = false;
//#     private boolean sendInDeTranslit = false;
//#endif
    protected Command cmdCancel;
    protected Command cmdInsMe;
    protected Command cmdInsNick;
    protected Command cmdLastMessage;
    protected Command cmdPaste;
    protected Command cmdPasteText;
    protected Command cmdSend;
    protected Command cmdSmile;
    protected Command cmdSubj;
    protected Command cmdSuspend;
    protected Command cmdTranslate;
    protected Ticker ticker = null;

    protected Contact to;

    public BaseMessageEdit() {
        initCommands();
    }

    public final void initCommands() {
        if (Config.swapSendAndSuspend) {
            cmdSend = new Command(SR.get(SR.MS_SEND), Command.BACK, 1);
            cmdSuspend = new Command(SR.get(SR.MS_SUSPEND), Command.SCREEN, 90);
        } else {
            cmdSend = new Command(SR.get(SR.MS_SEND), Command.OK, 1);
            cmdSuspend = new Command(SR.get(SR.MS_SUSPEND), Command.BACK, 90);
        }
//#ifdef SMILES
        cmdSmile = new Command(SR.get(SR.MS_ADD_SMILE), Command.SCREEN, 2);
//#endif
        cmdInsNick = new Command(SR.get(SR.MS_NICKNAMES), Command.SCREEN, 3);
        cmdInsMe = new Command(SR.get(SR.MS_SLASHME), Command.SCREEN, 4);
//#ifdef DETRANSLIT
//#         cmdSendInTranslit = new Command(SR.get(SR.MS_TRANSLIT), Command.SCREEN, 5);
//#         cmdSendInDeTranslit = new Command(SR.get(SR.MS_DETRANSLIT), Command.SCREEN, 5);
//#endif
        cmdLastMessage = new Command(SR.get(SR.MS_PREVIOUS), Command.SCREEN, 9);
        cmdSubj = new Command(SR.get(SR.MS_SET_SUBJECT), Command.SCREEN, 10);
        cmdCancel = new Command(SR.get(SR.MS_CANCEL), Command.SCREEN, 99);
        cmdTranslate = new Command(SR.get(SR.MS_TRANSLATE), Command.SCREEN, 337);
//#ifdef ARCHIVE
        cmdPaste = new Command(SR.get(SR.MS_ARCHIVE), Command.SCREEN, 6);
//#endif
//#ifdef CLIPBOARD
        cmdPasteText = new Command(SR.get(SR.MS_PASTE), Command.SCREEN, 8);
//#endif
    }

    public Contact getContact() {
        return to;
    }

    public abstract String getString();

    public abstract int getCaretPosition();

    public abstract void insert(int pos, String text);

    public abstract Object getInput();

    public abstract void setString(String bodyNew);

    public final void setTicker(String caption) {
        if (ticker != null) {
            ticker.setString(caption);
        }
    }

    public final void show(Vector contacts) {
        this.multiMessage = true;
        activeContacts = contacts;

        show(null, null);
    }

    public abstract void show(Contact to, String body);

    protected final void send(String body, String subj) {
        String comp = null;
        String id = String.valueOf((int)System.currentTimeMillis());
        Msg msg = new Msg(Msg.MESSAGE_TYPE_OUT, midlet.BombusQD.sd.account.toString(), subj, body);

        if (body != null) {
            body = body.trim();
        }
//#ifdef DETRANSLIT
//#         if (sendInTranslit) {
//#             if (body != null) {
//#                 body = DeTranslit.translit(body);
//#             }
//#             if (subj != null) {
//#                 subj = DeTranslit.translit(subj);
//#             }
//#         }
//#         if (sendInDeTranslit || midlet.BombusQD.cf.autoDeTranslit) {
//#             if (body != null) {
//#                 body = DeTranslit.deTranslit(body);
//#             }
//#             if (subj != null) {
//#                 subj = DeTranslit.deTranslit(subj);
//#             }
//#         }
//#endif
        if (body != null || subj != null) {
            msg.subject = subj;
            msg.body = body;

            msg.id = id;

            if (to.origin != Contact.ORIGIN_GROUPCHAT) {
                to.addMessage(msg);
                comp = "active";
            }
        } else if (to.acceptComposing) {
            comp = (composing) ? "composing" : "paused";
        }
        if (!midlet.BombusQD.cf.eventComposing) {
            comp = null;
        }
        try { //??
            if (body != null || subj != null || comp != null) {
                if (body != null) {
                    to.lastSendedMessage = body;
                }
                midlet.BombusQD.sd.roster.sendMessage(to,id, body, subj, comp);
                msg = null;
                if (subj != null) {
                    this.subj = null;
                }
                id = to.msgSuspended = null;
            }
        } catch (Exception e) {
            msg.body = "::MessageEdit Exception->Error send message(" + e.getMessage() + ")";
            to.addMessage(msg);
        }
    }

    protected final void send() {
        send(body, subj);
        destroyView();
    }

    protected final void destroyView() {
        if (!multiMessage && null != to) {
            if (to.getChatInfo().getMessageCount() > 0) {
                to.getMessageList().show();
                return;
            }
        }
        BombusQD.sd.roster.show();
    }

    public void commandAction(Command c, Displayable d) {
        if (to == null && !multiMessage) {
            return;
        }
        body = getString();
        if (body.length() == 0) {
            body = null;
        }

//#ifdef ARCHIVE
        if (c == cmdPaste) {
            if (null != to) {
                to.msgSuspended = body;
            }
            if (midlet.BombusQD.cf.msgEditType > 0) {
                new ArchiveList(getCaretPosition(), (TextField)getInput(), null).show();
            } else {
                new ArchiveList(getCaretPosition(), null, (TextBox)getInput()).show();
            }
            return;
        }
//#endif
//#ifdef CLIPBOARD
        if (c == cmdPasteText) {
            insert(getCaretPosition(), ClipBoard.getClipBoard());
            return;
        }
//#endif
        if (c == cmdInsMe) {
            insert(0, "/me ");
            return;
        } else if (c == cmdLastMessage) {
            if (null == to) {
                return;
            }
            if (null == to.lastSendedMessage) {
                return;
            }
            insert(getCaretPosition(), to.lastSendedMessage);
            return;
        }
//#ifdef SMILES
        if (c == cmdSmile) {
            if (midlet.BombusQD.cf.msgEditType > 0) {
                new SmilePicker(getCaretPosition(), (TextField)getInput(), null).show();
            } else {
                new SmilePicker(getCaretPosition(), null, (TextBox)getInput()).show();
            }
            return;
        }
//#endif
//#ifndef WMUC
        if (c == cmdInsNick) {
            if (midlet.BombusQD.cf.msgEditType > 0) {
                new AppendNickForm( to, getCaretPosition(), (TextField)getInput(), null).show();
            } else {
                new AppendNickForm( to, getCaretPosition(), null, (TextBox)getInput()).show();
            }
            return;
        }
//#endif

        // придумать что-то с общим кодом
        if (c == cmdCancel) {
            composing = false;
            if (!multiMessage) {
                send(null, null);
            }
            body = null;
            multiMessage = false;
            if (null != to) {
                to.msgSuspended = body;
            }
            destroyView();
            return;
        } else if (c == cmdSuspend) {
            composing = false;
            if (!multiMessage) {
                send(null, null);
            }
            multiMessage = false;
            if (null != to) {
                to.msgSuspended = body;
            }
            body = null;
            destroyView();
            return;
        } else if (c == cmdTranslate) {
            new TranslateSelect(to,body, "none", false, -1).show();
            body = null;
            return;
        } else if (c == cmdSend) {
            if (body == null) {
                composing = false;
                if (!multiMessage) {
                    send(null, null);
                }
                multiMessage = false;
                if (null != to && to.msgSuspended != null) {
                    to.msgSuspended = null;
                }
                destroyView();
                return;
            } else {
                if (null != to) {
                    to.msgSuspended = null;
                }
            }
        }
//#ifdef DETRANSLIT
//#         if (c == cmdSendInTranslit) {
//#             sendInTranslit = true;
//#         }
//#
//#         if (c == cmdSendInDeTranslit) {
//#             sendInDeTranslit = true;
//#         }
//#endif
        if (c == cmdSubj) {
            if (body == null) {
                return;
            }
            subj = body;
            body = null;
        }

//#ifdef RUNNING_MESSAGE
        if (null == to || multiMessage) {
            composing = false;
            if (activeContacts != null) {
                int size = activeContacts.size();
                for (int i = 0; i < size; ++i) {
                    to = (Contact)activeContacts.elementAt(i);
                    send();
                }
            }
            multiMessage = false;
        } else {
            if (to.msgSuspended == null) {
                composing = false;
                //send(null,null);//check it on Sony Ericsson W595
                send();
            }
        }
//#if DETRANSLIT
//#             sendInTranslit = false;
//#             sendInDeTranslit = false;
//#endif
//#endif
    }
}
