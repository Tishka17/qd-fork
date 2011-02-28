/*
 * ActionsMenu.java
 *
 * Created on 11.12.2005, 19:05
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
package client;

import alert.AlertCustomize;
import colors.ColorTheme;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Iq;
import com.alsutton.jabber.datablocks.Message;
import com.alsutton.jabber.datablocks.Presence;
import conference.ConferenceGroup;
import conference.InviteForm;
import conference.MucContact;
import conference.QueryConfigForm;
import conference.affiliation.AffiliationItem;
import conference.affiliation.Affiliations;
import conference.affiliation.ConferenceQuickPrivelegeModify;
import disco.ServiceDiscovery;
import images.ActionsIcons;
import io.file.transfer.TransferImage;
import io.file.transfer.TransferSendFile;
import java.util.Enumeration;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextField;
import locale.SR;
import menu.Menu;
import menu.MenuItem;
import midlet.BombusQD;
import ui.controls.AlertBox;
import ui.MIDPTextBox;
import ui.MainBar;
import util.ClipBoard;
import vcard.VCard;
import vcard.VCardEdit;
import vcard.VCardView;
import xmpp.extensions.IqLast;
import xmpp.extensions.IqPing;
import xmpp.extensions.IqTimeReply;
import xmpp.extensions.IqVersionReply;
import xmpp.extensions.SoftwareInfo;

public class ActionsMenu extends Menu implements MIDPTextBox.TextBoxNotify {
    private static final int MI_LOGIN = 0;
    private static final int MI_LOGOUT = 1;
    private static final int MI_CHTRANSPORT = 2;
    private static final int MI_VCARD = 3;
    private static final int MI_DELVCARD = 4;
    private static final int MI_DELALLVCARD = 5;
    private static final int MI_DELAVATAR = 6;
    private static final int MI_DELALLAVATAR = 7;
    private static final int MI_FEATURES = 8;
    private static final int MI_INFO = 9;
    private static final int MI_ANNOTATION = 10;
    private static final int MI_DEL_ANNOTATION = 11;
    private static final int MI_HISTORY = 12;
    private static final int MI_VERSION = 13;
    private static final int MI_COMMANDS = 14;
    private static final int MI_SEND_BUFFER = 15;
    private static final int MI_COPY_JID = 16;
    private static final int MI_SEND_SCHEME = 17;
    private static final int MI_PING = 18;
    private static final int MI_TIME = 19;
    private static final int MI_IDLE = 20;
    private static final int MI_ATTENTION = 21;
    private static final int MI_ONLINE = 22;
    private static final int MI_SEEN = 23;
    private static final int MI_EDIT = 24;
    private static final int MI_DELETE = 25;
    private static final int MI_SUBSCRIBTION = 26;
    private static final int MI_SEND_PRESENCE = 27;
    private static final int MI_INVITE = 28;
    private static final int MI_KICK = 29;
    private static final int MI_BAN = 30;
    private static final int MI_VOICE = 31;
    private static final int MI_DEVOICE = 32;
    private static final int MI_ADD_MODER = 33;
    private static final int MI_DEL_MODER = 34;
    private static final int MI_MEMBER = 35;
    private static final int MI_UNMEMBER = 36;
    private static final int MI_ADMIN = 37;
    private static final int MI_OWNER = 38;
    private static final int MI_BANLIST = 39;
    private static final int MI_MEMBERLIST = 40;
    private static final int MI_ADMINLIST = 41;
    private static final int MI_OWNERLIST = 42;
    private static final int MI_SEND_FILE = 43;
    private static final int MI_SEND_PHOTO = 44;
    private static final int MI_LEAVE = 45;
    private static final int MI_LEAVE_ALL = 46;
    private static final int MI_CONFIG = 47;
    private static final int MI_CHANGE_NICK = 48;
    private static final int MI_REJOIN = 49;
    private static final int MI_RENAME = 50;
    private Object item;

    public ActionsMenu(Display display, Displayable pView, Object item) {
        super(null, ActionsIcons.getInstance(), null);

        this.item = item;
        if (item instanceof Contact) {
            setMainBarItem(new MainBar(((Contact) item).getJid()));
        } else if (item instanceof Group) {
            setMainBarItem(new MainBar(((Group) item).getName()));
        }

        if (item instanceof Contact) {
            Contact contact = (Contact) item;
            int groupType = contact.getGroupType();

            if (groupType == Groups.TYPE_TRANSP) {
                addItem(SR.get(SR.MS_LOGIN), MI_LOGIN, ActionsIcons.ICON_ON);
                addItem(SR.get(SR.MS_LOGOFF), MI_LOGOUT, ActionsIcons.ICON_OFF);
//#ifdef CHANGE_TRANSPORT
//#ifdef PLUGINS
//#                if (sd.ChangeTransport);
//#endif
//#                 addItem(SR.get(SR.MS_CHANGE_TRANSPORT), MI_CHTRANSPORT, ActionsIcons.ICON_NICK_RESOLVE);
//#endif
            }

            addItem(SR.get(SR.MS_VCARD), MI_VCARD, ActionsIcons.ICON_VCARD);
            if (contact.vcard != null) {
                addItem(SR.get(SR.MS_DELETE_VCARD), MI_DELVCARD, ActionsIcons.ICON_VCARD, true);
                addItem(SR.get(SR.MS_DELETE_ALL_VCARD), MI_DELALLVCARD, ActionsIcons.ICON_VCARD, true);
            }
            if (contact.img_vcard != null) {
                addItem(SR.get(SR.MS_DELETE_AVATAR_VCARD), MI_DELAVATAR, ActionsIcons.ICON_VCARD, true);
                addItem(SR.get(SR.MS_DELETE_ALL_AVATAR_VCARD), MI_DELALLAVATAR, ActionsIcons.ICON_VCARD, true);
            }
            if (midlet.BombusQD.cf.userAppLevel == 1) {
                addItem(SR.get(SR.MS_FEATURES), MI_FEATURES, ActionsIcons.ICON_INFO);
            }
//#ifdef POPUPS
            addItem(SR.get(SR.MS_INFO), MI_INFO, ActionsIcons.ICON_INFO);
//#endif
            if (!(contact instanceof MucContact) && BombusQD.cf.networkAnnotation) {
                // XEP-0145: Annotations
                if (groupType != Groups.TYPE_TRANSP && groupType != Groups.TYPE_SELF) {
                    addItem(SR.get(SR.MS_CREATE_ANNOTATION), MI_ANNOTATION, ActionsIcons.ICON_VOICE);
                    if (contact.annotations != null) {
                        addItem(SR.get(SR.MS_REMOVE_ANNOTATION), MI_DEL_ANNOTATION, ActionsIcons.ICON_VOICE);
                    }
                }
            }
//#ifdef HISTORY
//#             if (groupType != Groups.TYPE_TRANSP && !(contact instanceof MucContact)) {
//#                 if (midlet.BombusQD.cf.module_history) {
//#                     addItem(SR.get(SR.MS_HISTORY_SHOW), MI_HISTORY, ActionsIcons.ICON_VERSION);
//#                 }
//#             }
//#endif
            addItem(SR.get(SR.MS_CLIENT_INFO), MI_VERSION, ActionsIcons.ICON_VERSION);
//#ifdef SERVICE_DISCOVERY
            addItem(SR.get(SR.MS_COMMANDS), MI_COMMANDS, ActionsIcons.ICON_COMMAND);
//#endif
//#ifdef CLIPBOARD
//#             if (midlet.BombusQD.cf.useClipBoard) {
//#                 addItem(SR.get(SR.MS_COPY_JID), MI_COPY_JID, ActionsIcons.ICON_COPY_JID);
//#                 if (!ClipBoard.isEmpty()) {
//#                     addItem(SR.get(SR.MS_SEND_BUFFER), MI_SEND_BUFFER, ActionsIcons.ICON_SEND_BUFFER);
//#                 }
//#             }
//#endif
            if (groupType != Groups.TYPE_SELF) {
                addItem(SR.get(SR.MS_SEND_COLOR_SCHEME), MI_SEND_SCHEME, ActionsIcons.ICON_SEND_COLORS);
            }
            if (contact.status < Constants.PRESENCE_OFFLINE) {
                addItem(SR.get(SR.MS_TIME), MI_TIME, ActionsIcons.ICON_TIME);
                addItem(SR.get(SR.MS_IDLE), MI_IDLE, ActionsIcons.ICON_IDLE);
                addItem(SR.get(SR.MS_PING), MI_PING, ActionsIcons.ICON_PING);
                if (AlertCustomize.getInstance().enableAttention) {
                    addItem(SR.get(SR.LA_REQUEST), MI_ATTENTION, ActionsIcons.ICON_TIME);
                }
            }
            if (groupType != Groups.TYPE_SELF
                    && groupType != Groups.TYPE_SEARCH_RESULT) {

                if (contact.status < Constants.PRESENCE_OFFLINE) {
                    addItem(SR.get(SR.MS_ONLINE_TIME), MI_ONLINE, ActionsIcons.ICON_ONLINE);
                } else {
                    addItem(SR.get(SR.MS_SEEN), MI_SEEN, ActionsIcons.ICON_ONLINE);
                }
                addItem(SR.get(SR.MS_DIRECT_PRESENCE), MI_SEND_PRESENCE, ActionsIcons.ICON_SET_STATUS);
                if (groupType != Groups.TYPE_TRANSP) {
                    addItem(SR.get(SR.MS_EDIT), MI_EDIT, ActionsIcons.ICON_RENAME);
                }
                addItem(SR.get(SR.MS_SUBSCRIPTION), MI_SUBSCRIBTION, ActionsIcons.ICON_SUBSCR);
                addItem(SR.get(SR.MS_DELETE), MI_DELETE, ActionsIcons.ICON_DELETE);
            }

//#ifndef WMUC
            if (contact instanceof MucContact) {
                MucContact self = ((ConferenceGroup) contact.group).selfContact;
                MucContact mcontact = (MucContact) contact;

                int myRole = self.roleCode;
                int myAff = self.affiliationCode;

                if (mcontact.realJid != null) {
                    if (myAff == Constants.AFFILIATION_MEMBER) {
                        addItem(SR.get(SR.MS_INVITE), MI_INVITE, ActionsIcons.ICON_INVITE);
                    }
                }
                if (myRole == Constants.ROLE_MODERATOR) {
                    if (mcontact.roleCode < Constants.ROLE_MODERATOR) {
                        addItem(SR.get(SR.MS_KICK), MI_KICK, ActionsIcons.ICON_KICK);
                    }
                    if (myAff >= Constants.AFFILIATION_ADMIN && mcontact.affiliationCode < myAff) {
                        addItem(SR.get(SR.MS_BAN), MI_BAN, ActionsIcons.ICON_BAN);
                    }

                    if (mcontact.affiliationCode < Constants.AFFILIATION_ADMIN) {
                        if (mcontact.roleCode == Constants.ROLE_VISITOR) {
                            addItem(SR.get(SR.MS_GRANT_VOICE), MI_VOICE, ActionsIcons.ICON_VOICE);
                        } else {
                            addItem(SR.get(SR.MS_REVOKE_VOICE), MI_DEVOICE, ActionsIcons.ICON_DEVOICE);
                        }
                    }
                }
                if (myAff >= Constants.AFFILIATION_ADMIN) {
                    if (mcontact.affiliationCode < Constants.AFFILIATION_ADMIN) {
                        if (mcontact.roleCode == Constants.ROLE_MODERATOR) {
                            addItem(SR.get(SR.MS_REVOKE_MODERATOR), MI_ADD_MODER, ActionsIcons.ICON_MEMBER);
                        } else {
                            addItem(SR.get(SR.MS_GRANT_MODERATOR), MI_DEL_MODER, ActionsIcons.ICON_ADMIN);
                        }
                    }
                    if (mcontact.affiliationCode < myAff) {
                        if (mcontact.affiliationCode != Constants.AFFILIATION_NONE) {
                            addItem(SR.get(SR.MS_UNAFFILIATE), MI_UNMEMBER, ActionsIcons.ICON_DEMEMBER);
                        }
                        if (mcontact.affiliationCode != Constants.AFFILIATION_MEMBER) {
                            addItem(SR.get(SR.MS_GRANT_MEMBERSHIP), MI_MEMBER, ActionsIcons.ICON_MEMBER);
                        }
                    }
                }
                if (myAff == Constants.AFFILIATION_OWNER) {
                    if (mcontact.affiliationCode != Constants.AFFILIATION_ADMIN) {
                        addItem(SR.get(SR.MS_GRANT_ADMIN), MI_ADMIN, ActionsIcons.ICON_ADMIN);
                    }

                    if (mcontact.affiliationCode != Constants.AFFILIATION_OWNER) {
                        addItem(SR.get(SR.MS_GRANT_OWNERSHIP), MI_OWNER, ActionsIcons.ICON_OWNER);
                    }
                }
            } else if (groupType != Groups.TYPE_TRANSP && groupType != Groups.TYPE_SEARCH_RESULT && groupType != Groups.TYPE_SELF) {
                addItem(SR.get(SR.MS_INVITE), MI_INVITE, ActionsIcons.ICON_INVITE);
            }
//#endif

//#ifdef FILE_TRANSFER
            if (groupType != Groups.TYPE_TRANSP && midlet.BombusQD.cf.fileTransfer) {
                if (groupType != Groups.TYPE_SELF) {
//#ifdef FILE_IO
//#ifdef PLUGINS
//#                     if (sd.FileTransfer)
//#endif
                    addItem(SR.get(SR.MS_SEND_FILE), MI_SEND_FILE, ActionsIcons.ICON_SEND_FILE);
                    String cameraAvailable = System.getProperty("supports.video.capture");
                    if (cameraAvailable != null) {
                        if (cameraAvailable.startsWith("true")) {
//#ifdef PLUGINS
//#                         if (sd.ImageTransfer)
//#endif
                            addItem(SR.get(SR.MS_SEND_PHOTO), MI_SEND_PHOTO, ActionsIcons.ICON_SEND_FILE);
                        }
                    }
//#endif
                }
            }
//#endif
        } else {
            Group group = (Group) item;
            if (group.type == Groups.TYPE_SEARCH_RESULT) {
                addItem(SR.get(SR.MS_DISCARD), 21, ActionsIcons.ICON_BAN);
            }
//#ifndef WMUC
            if (group instanceof ConferenceGroup) {
                MucContact self = ((ConferenceGroup) group).selfContact;

                addItem(SR.get(SR.MS_LEAVE_ROOM), MI_LEAVE, ActionsIcons.ICON_LEAVE);
                addItem(SR.get(SR.MS_CLOSE_ALL_ROOMS), MI_LEAVE_ALL, ActionsIcons.ICON_LEAVE);

                if (self.status >= Constants.PRESENCE_OFFLINE) {
                    addItem(SR.get(SR.MS_REENTER), MI_REJOIN, ActionsIcons.ICON_CHANGE_NICK);
                } else {
                    addItem(SR.get(SR.MS_DIRECT_PRESENCE), MI_SEND_PRESENCE, ActionsIcons.ICON_SET_STATUS);
                    addItem(SR.get(SR.MS_CHANGE_NICKNAME), MI_CHANGE_NICK, ActionsIcons.ICON_CHANGE_NICK);
                    if (self.affiliationCode >= Constants.AFFILIATION_OWNER) {
                        addItem(SR.get(SR.MS_CONFIG_ROOM), MI_CONFIG, ActionsIcons.ICON_CONFIGURE);
                        addItem(SR.get(SR.MS_DESTROY_ROOM), MI_DELETE, ActionsIcons.ICON_OUTCASTS);
                    }
                    if (self.affiliationCode >= Constants.AFFILIATION_ADMIN) {
                        addItem(SR.get(SR.MS_OWNERS), MI_OWNERLIST, ActionsIcons.ICON_OWNERS);
                        addItem(SR.get(SR.MS_ADMINS), MI_ADMINLIST, ActionsIcons.ICON_ADMINS);
                        addItem(SR.get(SR.MS_MEMBERS), MI_MEMBERLIST, ActionsIcons.ICON_MEMBERS);
                        addItem(SR.get(SR.MS_BANNED), MI_BANLIST, ActionsIcons.ICON_OUTCASTS);
                    }
                }
            } else {
//#endif
                if (group.type != Groups.TYPE_IGNORE
                        && group.type != Groups.TYPE_NOT_IN_LIST
                        && group.type != Groups.TYPE_SEARCH_RESULT
                        && group.type != Groups.TYPE_SELF
                        && group.type != Groups.TYPE_TRANSP) {
                    addItem(SR.get(SR.MS_RENAME), MI_RENAME, ActionsIcons.ICON_RENAME);
                    addItem(SR.get(SR.MS_DELETE), MI_DELETE, ActionsIcons.ICON_DELETE);
                }
//#ifndef WMUC
            }
//#endif
        }
        attachDisplay(display);
        super.parentView = pView;
    }

    public void OkNotify(String annotationText) {
        Contact find;
        Contact current = (Contact) item;
        current.annotations = annotationText;

        JabberDataBlock query = new Iq(null, Iq.TYPE_SET, "notes" + current.bareJid);
        JabberDataBlock query_private = query.addChildNs("query", "jabber:iq:private");

        JabberDataBlock storage = query_private.addChildNs("storage", "storage:rosternotes");
        int size = midlet.BombusQD.sd.roster.contactList.contacts.size();
        synchronized (midlet.BombusQD.sd.roster.contactList.contacts) {
            for (int i = 0; i < size; i++) {
                find = (Contact) midlet.BombusQD.sd.roster.contactList.contacts.elementAt(i);
                if (find.annotations != null) {
                    JabberDataBlock note = storage.addChild("note", find.annotations);
                    note.setAttribute("jid", find.bareJid);
                }
            }
        }
        midlet.BombusQD.sd.roster.theStream.send(query);
        destroyView();
    }

    public void eventOk() {
        MenuItem mItem = (MenuItem) getFocusedObject();

        // return - don't close Actions window;
        // break - vice versa

        if (item instanceof Contact) {
            Contact contact = (Contact) item;
            switch (mItem.index) {
                case MI_VERSION:
                    BombusQD.sd.roster.setQuerySign(true);
                    BombusQD.cf.flagQuerySign = true;
                    BombusQD.sd.roster.theStream.send(IqVersionReply.query(contact.getJid()));
                    break;
//#ifdef POPUPS
                case MI_INFO:
                    midlet.BombusQD.sd.roster.showInfo();
                    break;
//#endif
                case MI_FEATURES:
                    BombusQD.sd.roster.setQuerySign(true);
                    BombusQD.sd.roster.theStream.addBlockListener(new SoftwareInfo());
                    BombusQD.sd.roster.theStream.send(SoftwareInfo.querySend(contact.getJid()));
                    break;
                case MI_SEND_SCHEME: {
                    String from = midlet.BombusQD.sd.account.toString();
                    String body = ColorTheme.getSkin();
                    String id = String.valueOf((int) System.currentTimeMillis());

                    BombusQD.sd.roster.sendMessage(contact, id, body, null, null, false);
                    contact.addMessage(new Msg(Constants.MESSAGE_TYPE_OUT, from, null, "Scheme sended"));
                    break;
                }
                case MI_VCARD:
                    if (contact.vcard != null) {
                        if (contact.getGroupType() == Groups.TYPE_SELF) {
                            new VCardEdit(display, this, contact.vcard);
                        } else {
                            new VCardView(display, this, contact);
                        }
                    } else {
                        if (contact instanceof MucContact) {
                            MucContact mcontact = (MucContact) contact;
                            String realjid = mcontact.realJid;
                            if (realjid == null) {
                                VCard.request(contact.bareJid, contact.getJid());
                            } else {
                                VCard.request(realjid.substring(0, realjid.indexOf("/", realjid.indexOf("@"))), contact.getJid());
                            }
                        } else {
                            VCard.request(contact.bareJid, contact.getJid());
                        }
                    }
                    break;
                case MI_DELVCARD:
                    contact.clearVCard();
                    break;
                case MI_DELALLVCARD:
                    int size = BombusQD.sd.roster.contactList.contacts.size();
                    for (int i = 0; i < size; ++i) {
                        Contact c = (Contact) BombusQD.sd.roster.contactList.contacts.elementAt(i);
                        if (c.vcard != null) {
                            c.clearVCard();
                        }
                    }
                    break;
                case MI_DELAVATAR:
                    contact.img_vcard = null;
                    break;
                case MI_DELALLAVATAR:
                    size = BombusQD.sd.roster.contactList.contacts.size();
                    for (int i = 0; i < size; ++i) {
                        Contact c = (Contact) BombusQD.sd.roster.contactList.contacts.elementAt(i);
                        if (c.img_vcard != null) {
                            c.img_vcard = null;
                        }
                    }
                    break;
                case MI_EDIT:
                    new ContactEdit(display, this, contact);
                    return;
                case MI_SUBSCRIBTION:
                    new SubscriptionEdit(display, this, contact);
                    return;
                case MI_DELETE:
                    new AlertBox(SR.get(SR.MS_DELETE_ASK), contact.getName(), display, BombusQD.sd.roster, false) {
                        public void yes() {
                            BombusQD.sd.roster.deleteContact((Contact) item);
                        }

                        public void no() {
                        }
                    };
                    return;
                case MI_LOGOUT: {
                    midlet.BombusQD.sd.roster.blockNotify(-111, 10000);
                    Presence presence = new Presence(
                            Constants.PRESENCE_OFFLINE, -1, "", null);
                    presence.setTo(contact.getJid());
                    BombusQD.sd.roster.theStream.send(presence);
                    break;
                }
                case MI_LOGIN: {
                    midlet.BombusQD.sd.roster.blockNotify(-111, 10000);
                    Presence presence = new Presence(midlet.BombusQD.sd.roster.myStatus, 0, "", null);
                    presence.setTo(contact.getJid());
                    BombusQD.sd.roster.theStream.send(presence);
                    break;
                }
//#ifdef CHANGE_TRANSPORT
//#                 case MI_CHTRANSPORT: {
//#                     new ChangeTransport(display, contact.bareJid);
//#                     return;
//#                 }
//#endif
//#ifdef HISTORY
//#                 case MI_HISTORY: {
//#                     contact.getMessageList().getRmsData(3, null);
//#                 }
//#endif
//#ifdef SERVICE_DISCOVERY
                case MI_COMMANDS:
                    new ServiceDiscovery(display, contact.getJid(), "http://jabber.org/protocol/commands", false);
                    return;
//#endif
                case MI_ATTENTION:
                    Message message = new Message(contact.getJid(), SR.get(SR.LA_WAKEUP), SR.get(SR.LA_ATTENTION), false);
                    message.setType("headline");
                    message.addChildNs("attention", "urn:xmpp:attention:0");
                    BombusQD.sd.roster.theStream.send(message);
                    break;
                case MI_IDLE:
                    BombusQD.sd.roster.setQuerySign(true);
                    BombusQD.sd.roster.theStream.send(IqLast.query(contact.getJid(), "idle"));
                    break;
                case MI_ONLINE:
                    BombusQD.sd.roster.setQuerySign(true);
                    BombusQD.sd.roster.theStream.send(IqLast.query(contact.bareJid, "online_" + contact.getResource()));
                    break;
                case MI_SEEN:
                    BombusQD.sd.roster.setQuerySign(true);
                    BombusQD.sd.roster.theStream.send(IqLast.query(contact.bareJid, "seen"));
                    break;
                case MI_PING:
                    BombusQD.sd.roster.setQuerySign(true);
                    BombusQD.sd.roster.theStream.send(IqPing.query(contact.getJid(), null));
                    break;
                case MI_TIME:
                    BombusQD.sd.roster.setQuerySign(true);
                    BombusQD.sd.roster.theStream.send(IqTimeReply.query(contact.getJid()));
                    break;
                case MI_ANNOTATION:
                    new MIDPTextBox(display, SR.get(SR.MS_NEW), contact.annotations, this, TextField.ANY, 200);
                    return;
                case MI_DEL_ANNOTATION:
                    OkNotify(null);
                    break;
//#ifdef CLIPBOARD
//#                 case MI_COPY_JID:
//#                     if (contact.bareJid != null) {
//#                         Msg msg = new Msg(Constants.MESSAGE_TYPE_SYSTEM, "JID", null, contact.bareJid);
//#                         ClipBoard.add(msg);
//#                     }
//#                     break;
//#                 case MI_SEND_BUFFER: {
//#                     String body = ClipBoard.getClipBoard();
//#                     if (body == null && body.length() == 0) {
//#                         return;
//#                     }
//# 
//#                     String from = midlet.BombusQD.sd.account.toString();
//#                     String id = String.valueOf((int) System.currentTimeMillis());
//# 
//#                     BombusQD.sd.roster.sendMessage(contact, id, body, null, null, false);
//# 
//#                     Msg msg = new Msg(Constants.MESSAGE_TYPE_OUT, from, null, body);
//#                     msg.id = id;
//#                     msg.itemCollapsed = true;
//# 
//#                     contact.addMessage(msg);
//#                     break;
//#                 }
//#endif
//#ifndef WMUC
                case MI_INVITE:
                    if (contact.jid != null) {
                        new InviteForm(display, midlet.BombusQD.sd.roster, contact);
                    } else {
                        MucContact mcontact = (MucContact) contact;

                        if (mcontact.realJid != null) {
                            boolean onlineConferences = false;
                            for (Enumeration cJ = midlet.BombusQD.sd.roster.getHContacts().elements(); cJ.hasMoreElements();) {
                                try {
                                    MucContact mcN = (MucContact) cJ.nextElement();
                                    if (mcN.origin == Constants.ORIGIN_GROUPCHAT && mcN.status == Constants.PRESENCE_ONLINE) {
                                        onlineConferences = true;
                                    }
                                } catch (Exception e) {
                                }
                            }
                            if (onlineConferences) {
                                new InviteForm(display, this, mcontact);
                            }
                        }
                    }
                    return;
//#endif
                case MI_SEND_PRESENCE:
                    new StatusSelect(display, this, contact);
                    return;
//#ifdef FILE_TRANSFER
//#ifdef FILE_IO
                case MI_SEND_FILE:
                    new TransferSendFile(display, this, contact.getJid());
                    return;
//#endif
                case MI_SEND_PHOTO:
                    new TransferImage(display, this, contact.getJid());
                    return;
//#endif
            }
            if (contact instanceof MucContact) {
                MucContact mcontact = (MucContact) contact;
                String myNick = ((ConferenceGroup) contact.group).selfContact.getName();

                switch (mItem.index) {
                    case MI_KICK:
                        new ConferenceQuickPrivelegeModify(display, this, mcontact, ConferenceQuickPrivelegeModify.KICK, myNick);
                        return;
                    case MI_BAN:
                        new ConferenceQuickPrivelegeModify(display, this, mcontact, ConferenceQuickPrivelegeModify.OUTCAST, myNick);
                        return;
                    case MI_VOICE:
                        new ConferenceQuickPrivelegeModify(display, this, mcontact, ConferenceQuickPrivelegeModify.PARTICIPANT, myNick);
                        return;
                    case MI_DEVOICE:
                        new ConferenceQuickPrivelegeModify(display, this, mcontact, ConferenceQuickPrivelegeModify.VISITOR, myNick);
                        return;
                    case MI_ADD_MODER:
                        new ConferenceQuickPrivelegeModify(display, this, mcontact, ConferenceQuickPrivelegeModify.MODERATOR, myNick);
                        return;
                    case MI_MEMBER:
                        new ConferenceQuickPrivelegeModify(display, this, mcontact, ConferenceQuickPrivelegeModify.MEMBER, myNick);
                        break;
                    case MI_UNMEMBER:
                        new ConferenceQuickPrivelegeModify(display, this, mcontact, ConferenceQuickPrivelegeModify.NONE, myNick);
                        break;
                    case MI_ADMIN:
                        new ConferenceQuickPrivelegeModify(display, this, mcontact, ConferenceQuickPrivelegeModify.ADMIN, myNick);
                        break;
                    case MI_OWNER:
                        new ConferenceQuickPrivelegeModify(display, this, mcontact, ConferenceQuickPrivelegeModify.OWNER, myNick);
                        break;
                }
            }
        } else if (item instanceof Group) {
            final Group group = (Group) item;
            if (group instanceof ConferenceGroup) {
                String roomjid = ((ConferenceGroup) group).confContact.getJid();
                switch (mItem.index) {
                    case MI_CONFIG: // room config
                        new QueryConfigForm(display, roomjid);
                        return;
                    case MI_BANLIST:
                        new Affiliations(display, this, roomjid, AffiliationItem.AFFILIATION_OUTCAST);
                        return;
                    case MI_MEMBERLIST:
                        new Affiliations(display, this, roomjid, AffiliationItem.AFFILIATION_MEMBER);
                        return;
                    case MI_ADMINLIST:
                        new Affiliations(display, this, roomjid, AffiliationItem.AFFILIATION_ADMIN);
                        return;
                    case MI_OWNERLIST:
                        new Affiliations(display, this, roomjid, AffiliationItem.AFFILIATION_OWNER);
                        return;
                    case MI_LEAVE:
                        ((ConferenceGroup) group).leaveRoom();
                        break;
                    case MI_LEAVE_ALL:
                        BombusQD.sd.roster.leaveAllMUCs();
                        break;
                    case MI_CHANGE_NICK:
                    case MI_REJOIN:
                        ((ConferenceGroup) group).reEnterRoom();
                        return;
                    case MI_SEND_PRESENCE:
                        new StatusSelect(display, this, ((ConferenceGroup) group).confContact);
                        return;
                    case MI_DELETE:
                        new CommandForm(display, parentView, 0, "Form", item, null);
                        return;
                }
            } else {
                switch (mItem.index) {
                    case MI_RENAME:
                        new RenameGroup(display, this, group);
                        return;
                    case MI_DELETE:
                        new AlertBox(SR.get(SR.MS_DELETE_GROUP_ASK), group.getName(), display, BombusQD.sd.roster, false)       {
                            public void yes() {
                                BombusQD.sd.roster.deleteGroup(group);
                            }

                            public void no() {
                            }
                        };
                        return;
                }
            }
        }
        destroyView();
    }
}
