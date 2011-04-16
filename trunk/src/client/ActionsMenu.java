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
import conference.ConferenceRemoveForm;
import conference.InviteForm;
import conference.MucContact;
//#ifdef SERVICE_DISCOVERY
import conference.QueryConfigForm;
//#endif
import conference.affiliation.AffiliationItem;
import conference.affiliation.AffiliationList;
import conference.affiliation.QuickPrivelegyEditForm;
//#if SERVICE_DISCOVERY && ADHOC
import disco.ServiceDiscovery;
//#endif
import images.ActionsIcons;
//#if FILE_IO && FILE_TRANSFER
import io.file.transfer.TransferImage;
import io.file.transfer.TransferSendFile;
//#endif
import javax.microedition.lcdui.TextField;
import locale.SR;
import menu.Menu;
import menu.MenuItem;
import midlet.BombusQD;
import ui.CanvasEx;
import ui.controls.AlertBox;
import ui.MainBar;
//#ifdef CLIPBOARD
import util.ClipBoard;
//#endif
import ui.input.InputTextBox;
import ui.input.InputTextBoxNotify;
import vcard.VCard;
import vcard.VCardEdit;
import vcard.VCardView;
import xmpp.extensions.IqLast;
import xmpp.extensions.IqPing;
import xmpp.extensions.IqTimeReply;
import xmpp.extensions.IqVersionReply;
import xmpp.extensions.SoftwareInfo;

public class ActionsMenu extends Menu implements InputTextBoxNotify {
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
    private static final int MI_COPY_TOPIC = 51;
    private static final int MI_RESOLVE_NICKS = 52;

    private static int lastCursorPos = 0;

    private Object item;

    public ActionsMenu(Object item) {
        super(null, ActionsIcons.getInstance());

        this.item = item;
        if (item instanceof Contact) {
            setMainBarItem(new MainBar(((Contact) item).getNickJid()));
        } else if (item instanceof Group) {
            setMainBarItem(new MainBar(((Group) item).getName()));
        }

        if (item instanceof Contact) {
            Contact contact = (Contact) item;
            int groupType = contact.getGroupType();

            boolean isMucContact = (contact instanceof MucContact);
            boolean isConference = (contact.origin == Contact.ORIGIN_GROUPCHAT);

            if (!isConference) {
                if (groupType == Groups.TYPE_TRANSP) {
                    addItem(SR.get(SR.MS_LOGIN), MI_LOGIN, ActionsIcons.ICON_ON);
                    addItem(SR.get(SR.MS_LOGOFF), MI_LOGOUT, ActionsIcons.ICON_OFF);
                    addItem(SR.get(SR.MS_RESOLVE_NICKNAMES), MI_RESOLVE_NICKS, ActionsIcons.ICON_NICK_RESOLVE);
//#ifdef CHANGE_TRANSPORT
                    addItem(SR.get(SR.MS_CHANGE_TRANSPORT), MI_CHTRANSPORT, ActionsIcons.ICON_NICK_RESOLVE);
//#endif
                }

                addItem(SR.get(SR.MS_VCARD), MI_VCARD, ActionsIcons.ICON_VCARD);
                if (contact.vcard != null) {
                    addItem(SR.get(SR.MS_DELETE_VCARD), MI_DELVCARD, ActionsIcons.ICON_VCARD);
                    addItem(SR.get(SR.MS_DELETE_ALL_VCARD), MI_DELALLVCARD, ActionsIcons.ICON_VCARD);
                }
                if (contact.img_vcard != null) {
                    addItem(SR.get(SR.MS_DELETE_AVATAR_VCARD), MI_DELAVATAR, ActionsIcons.ICON_VCARD);
                    addItem(SR.get(SR.MS_DELETE_ALL_AVATAR_VCARD), MI_DELALLAVATAR, ActionsIcons.ICON_VCARD);
                }
            }
            if (midlet.BombusQD.cf.userAppLevel == 1) {
                addItem(SR.get(SR.MS_FEATURES), MI_FEATURES, ActionsIcons.ICON_INFO);
            }
//#ifdef POPUPS
            addItem(SR.get(SR.MS_INFO), MI_INFO, ActionsIcons.ICON_INFO);
//#endif
            if (!isConference) {
                if (!isMucContact && BombusQD.cf.networkAnnotation) {
                    // XEP-0145: Annotations
                    if (groupType != Groups.TYPE_TRANSP && groupType != Groups.TYPE_SELF) {
                        addItem(SR.get(SR.MS_CREATE_ANNOTATION), MI_ANNOTATION, ActionsIcons.ICON_VOICE);
                        if (contact.annotations != null) {
                            addItem(SR.get(SR.MS_REMOVE_ANNOTATION), MI_DEL_ANNOTATION, ActionsIcons.ICON_DEVOICE);
                        }
                    }
                }
//#ifdef HISTORY
                if (groupType != Groups.TYPE_TRANSP && !(contact instanceof MucContact)) {
                    if (Config.module_history) {
                        if (Config.historyTypeIndex == Config.HISTORY_RMS) {
                            addItem(SR.get(SR.MS_HISTORY_SHOW), MI_HISTORY, ActionsIcons.ICON_VERSION);
                        }
                    }
                }
//#endif
                addItem(SR.get(SR.MS_CLIENT_INFO), MI_VERSION, ActionsIcons.ICON_VERSION);
//#if SERVICE_DISCOVERY && ADHOC
                addItem(SR.get(SR.MS_COMMANDS), MI_COMMANDS, ActionsIcons.ICON_COMMAND);
//#endif
            }

//#ifdef CLIPBOARD
            if (midlet.BombusQD.cf.useClipBoard) {
                addItem(SR.get(SR.MS_COPY_JID), MI_COPY_JID, ActionsIcons.ICON_COPY_JID);
                if (isConference) {
                    addItem(SR.get(SR.MS_COPY_TOPIC), MI_COPY_TOPIC, ActionsIcons.ICON_COPY_JID);
                }
                if (!ClipBoard.isEmpty()) {
                    addItem(SR.get(SR.MS_SEND_BUFFER), MI_SEND_BUFFER, ActionsIcons.ICON_SEND_BUFFER);
                }
            }
//#endif
            if (!isConference) {
                if (groupType != Groups.TYPE_SELF) {
                    addItem(SR.get(SR.MS_SEND_COLOR_SCHEME), MI_SEND_SCHEME, ActionsIcons.ICON_SEND_COLORS);
                }
                if (contact.status < Presence.PRESENCE_OFFLINE) {
                    addItem(SR.get(SR.MS_TIME), MI_TIME, ActionsIcons.ICON_TIME);
                    addItem(SR.get(SR.MS_IDLE), MI_IDLE, ActionsIcons.ICON_IDLE);
                    addItem(SR.get(SR.MS_PING), MI_PING, ActionsIcons.ICON_PING);
                    if (AlertCustomize.getInstance().enableAttention) {
                        addItem(SR.get(SR.LA_REQUEST), MI_ATTENTION, ActionsIcons.ICON_TIME);
                    }
                }
                if (groupType != Groups.TYPE_SELF
                        && groupType != Groups.TYPE_SEARCH_RESULT) {
                        if (!isMucContact) {
                            if (contact.status < Presence.PRESENCE_OFFLINE) {
                                addItem(SR.get(SR.MS_ONLINE_TIME), MI_ONLINE, ActionsIcons.ICON_ONLINE);
                            } else {
                                addItem(SR.get(SR.MS_SEEN), MI_SEEN, ActionsIcons.ICON_ONLINE);
                            }
                        }
                    addItem(SR.get(SR.MS_DIRECT_PRESENCE), MI_SEND_PRESENCE, ActionsIcons.ICON_SET_STATUS);
                }

//#ifndef WMUC
                if (isMucContact) {
                    MucContact self = ((ConferenceGroup) contact.group).selfContact;
                    MucContact mcontact = (MucContact) contact;

                    int myRole = self.roleCode;
                    int myAff = self.affiliationCode;

                    if (mcontact.realJid != null) {
                        if (myAff >= MucContact.AFFILIATION_MEMBER) {
                            addItem(SR.get(SR.MS_INVITE), MI_INVITE, ActionsIcons.ICON_INVITE);
                        }
                    }
                    if (myRole == MucContact.ROLE_MODERATOR) {
                        if (mcontact.roleCode < MucContact.ROLE_MODERATOR) {
                            addItem(SR.get(SR.MS_KICK), MI_KICK, ActionsIcons.ICON_KICK);
                        }
                        if (myAff >= MucContact.AFFILIATION_ADMIN && mcontact.affiliationCode < myAff) {
                            addItem(SR.get(SR.MS_BAN), MI_BAN, ActionsIcons.ICON_BAN);
                        }

                        if (mcontact.affiliationCode < MucContact.AFFILIATION_ADMIN) {
                            if (mcontact.roleCode == MucContact.ROLE_VISITOR) {
                                addItem(SR.get(SR.MS_GRANT_VOICE), MI_VOICE, ActionsIcons.ICON_VOICE);
                            } else {
                                addItem(SR.get(SR.MS_REVOKE_VOICE), MI_DEVOICE, ActionsIcons.ICON_DEVOICE);
                            }
                        }
                    }
                    if (myAff >= MucContact.AFFILIATION_ADMIN) {
                        if (mcontact.affiliationCode < MucContact.AFFILIATION_ADMIN) {
                            if (mcontact.roleCode == MucContact.ROLE_MODERATOR) {
                                addItem(SR.get(SR.MS_REVOKE_MODERATOR), MI_DEL_MODER, ActionsIcons.ICON_MEMBER);
                            } else {
                                addItem(SR.get(SR.MS_GRANT_MODERATOR), MI_ADD_MODER, ActionsIcons.ICON_ADMIN);
                            }
                        }
                        if (mcontact.affiliationCode < myAff) {
                            if (mcontact.affiliationCode != MucContact.AFFILIATION_NONE) {
                                addItem(SR.get(SR.MS_UNAFFILIATE), MI_UNMEMBER, ActionsIcons.ICON_DEMEMBER);
                            }
                            if (mcontact.affiliationCode != MucContact.AFFILIATION_MEMBER) {
                                addItem(SR.get(SR.MS_GRANT_MEMBERSHIP), MI_MEMBER, ActionsIcons.ICON_MEMBER);
                            }
                        }
                    }
                    if (myAff == MucContact.AFFILIATION_OWNER) {
                        if (mcontact.affiliationCode != MucContact.AFFILIATION_ADMIN) {
                            addItem(SR.get(SR.MS_GRANT_ADMIN), MI_ADMIN, ActionsIcons.ICON_ADMIN);
                        }

                        if (mcontact.affiliationCode != MucContact.AFFILIATION_OWNER) {
                            addItem(SR.get(SR.MS_GRANT_OWNERSHIP), MI_OWNER, ActionsIcons.ICON_OWNER);
                        }
                    }
                } else if (groupType != Groups.TYPE_SEARCH_RESULT && groupType != Groups.TYPE_SELF) {
                    if (groupType != Groups.TYPE_TRANSP) {
                        addItem(SR.get(SR.MS_INVITE), MI_INVITE, ActionsIcons.ICON_INVITE);
                    }
                    addItem(SR.get(SR.MS_EDIT), MI_EDIT, ActionsIcons.ICON_RENAME);
                    addItem(SR.get(SR.MS_SUBSCRIPTION), MI_SUBSCRIBTION, ActionsIcons.ICON_SUBSCR);
                    addItem(SR.get(SR.MS_DELETE), MI_DELETE, ActionsIcons.ICON_DELETE);
                }
//#endif

//#if FILE_IO && FILE_TRANSFER
                if (groupType != Groups.TYPE_TRANSP && midlet.BombusQD.cf.fileTransfer) {
                    if (groupType != Groups.TYPE_SELF) {
                        addItem(SR.get(SR.MS_SEND_FILE), MI_SEND_FILE, ActionsIcons.ICON_SEND_FILE);
                        String cameraAvailable = System.getProperty("supports.video.capture");
                        if (cameraAvailable != null) {
                            if (cameraAvailable.startsWith("true")) {
                                addItem(SR.get(SR.MS_SEND_PHOTO), MI_SEND_PHOTO, ActionsIcons.ICON_SEND_FILE);
                            }
                        }
                    }
                }
//#endif
            }
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

                if (self.status == Presence.PRESENCE_OFFLINE) {
                    addItem(SR.get(SR.MS_REENTER), MI_REJOIN, ActionsIcons.ICON_CHANGE_NICK);
                } else {
                    addItem(SR.get(SR.MS_DIRECT_PRESENCE), MI_SEND_PRESENCE, ActionsIcons.ICON_SET_STATUS);
                    addItem(SR.get(SR.MS_CHANGE_NICKNAME), MI_CHANGE_NICK, ActionsIcons.ICON_CHANGE_NICK);
                    if (self.affiliationCode == MucContact.AFFILIATION_OWNER) {
//#ifdef SERVICE_DISCOVERY
                        addItem(SR.get(SR.MS_CONFIG_ROOM), MI_CONFIG, ActionsIcons.ICON_CONFIGURE);
//#endif
                        addItem(SR.get(SR.MS_DESTROY_ROOM), MI_DELETE, ActionsIcons.ICON_DESTROY);
                    }
                    if (self.affiliationCode >= MucContact.AFFILIATION_ADMIN) {
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

        moveCursorTo(lastCursorPos);
    }

    public void okNotify(String annotationText) {
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

        lastCursorPos = cursor;
        if (item instanceof Contact) {
            Contact contact = (Contact) item;

            boolean isMucContact = (contact instanceof MucContact);
            boolean isConference = (contact.origin == Contact.ORIGIN_GROUPCHAT);

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
                    String body = ColorTheme.getSkin();
                    String id = String.valueOf((int) System.currentTimeMillis());
                    BombusQD.sd.roster.sendMessage(contact, id, body, null, null);

                    String from2 = midlet.BombusQD.sd.account.toString();
                    contact.addMessage(new Msg(Msg.MESSAGE_TYPE_OUT, from2, null, SR.get(SR.MS_SCHEME_SENT)));
                } break;
                case MI_VCARD: {
                    if (contact.vcard != null) {
                        if (contact.getGroupType() == Groups.TYPE_SELF) {
                            showForm(new VCardEdit(contact.vcard));

                        } else {
                            showForm(new VCardView(contact));
                        }
                        return;
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
                }
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
                case MI_EDIT: {
                    showForm(new ContactEdit(contact));
                    return;
                }
                case MI_SUBSCRIBTION: {
                    showForm(new SubscriptionEdit(contact));
                    return;
                }
                case MI_DELETE: {
                    AlertBox box = new AlertBox(SR.get(SR.MS_DELETE_ASK), contact.getName(), false) {
                        public void yes() {
                            BombusQD.sd.roster.deleteContact((Contact) item);
                        }

                        public void no() {
                        }
                    };
                    showForm(box);
                    return;
                }
                case MI_LOGOUT: {
                    midlet.BombusQD.sd.roster.blockNotify(-111, 10000);
                    Presence presence = new Presence(
                            Presence.PRESENCE_OFFLINE, -1, "", null);
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
                case MI_RESOLVE_NICKS: {
                    BombusQD.sd.roster.resolveNicknames(contact.bareJid);
                    break;
                }
//#ifdef CHANGE_TRANSPORT
                case MI_CHTRANSPORT: {
                    new ChangeTransportForm(contact.bareJid).show();
                    return;
                }
//#endif
//#ifdef HISTORY
                case MI_HISTORY: {
                    BombusQD.sd.roster.showHistory(getParentView(), contact);
                    return;
                }
//#endif
//#if SERVICE_DISCOVERY && ADHOC
                case MI_COMMANDS: {
                    showForm(new ServiceDiscovery(contact.getJid(), "http://jabber.org/protocol/commands", false));
                    return;
                }                 
//#endif
                case MI_ATTENTION: {
                    Message message = new Message(contact.getJid(), SR.get(SR.LA_WAKEUP), SR.get(SR.LA_ATTENTION), false);
                    message.setType("headline");
                    message.addChildNs("attention", "urn:xmpp:attention:0");
                    BombusQD.sd.roster.theStream.send(message);

                    String from = midlet.BombusQD.sd.account.toString();
                    contact.addMessage(
                            new Msg(Msg.MESSAGE_TYPE_OUT, from, null, SR.get(SR.MS_YOU_WOKE_UP) + " " + contact.getName()));
                    break;
                }
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
                case MI_ANNOTATION: {
                    InputTextBox input = new InputTextBox(SR.get(SR.MS_NEW), contact.annotations, 200, TextField.ANY);
                    input.setNotifyListener(this);
                    input.show();
                    return;
                }
                case MI_DEL_ANNOTATION:
                    okNotify(null);
                    break;
//#ifdef CLIPBOARD
                case MI_COPY_JID:
                    if (!isConference) {
                        if (contact instanceof MucContact) {
                            MucContact c = (MucContact)contact;
                            if (c.realJid != null) {
                                ClipBoard.setClipBoard(c.realJid);
                            } else {
                                ClipBoard.setClipBoard(c.getJid());
                            }
                        } else {
                            ClipBoard.setClipBoard(contact.getJid());
                        }
                    } else {
                        ClipBoard.setClipBoard(contact.bareJid);
                    }
                    break;
                case MI_COPY_TOPIC:
                    String topic = contact.getStatus();
                    if (topic != null) {
                        ClipBoard.setClipBoard(topic);
                    }
                    break;
                case MI_SEND_BUFFER: {
                    String body = ClipBoard.getClipBoard();
                    if (body == null && body.length() == 0) {
                        return;
                    }

                    String from = midlet.BombusQD.sd.account.toString();
                    String id = String.valueOf((int) System.currentTimeMillis());

                    BombusQD.sd.roster.sendMessage(contact, id, body, null, null);

                    Msg msg = new Msg(Msg.MESSAGE_TYPE_OUT, from, null, body);
                    msg.id = id;
                    msg.itemCollapsed = true;

                    contact.addMessage(msg);
                    break;
                }
//#endif
//#ifndef WMUC
                case MI_INVITE:
                    showForm(new InviteForm(contact));
                    return;
//#endif
                case MI_SEND_PRESENCE: {
                    showForm(new StatusSelect(contact));
                    return;
                }
//#if FILE_IO && FILE_TRANSFER
                case MI_SEND_FILE:
                    showForm(new TransferSendFile(contact.getJid()));
                    return;
                case MI_SEND_PHOTO:
                    showForm(new TransferImage(contact.getJid()));
                    return;
//#endif
            }
            if (isMucContact) {
                MucContact mcontact = (MucContact) contact;
                String myNick = ((ConferenceGroup) contact.group).selfContact.getName();

                int action = -1;
                switch (mItem.index) {
                    case MI_KICK:
                        action = QuickPrivelegyEditForm.KICK;
                        break;
                    case MI_BAN:
                        action = QuickPrivelegyEditForm.OUTCAST;
                        break;
                    case MI_DEL_MODER:
                    case MI_VOICE:
                        action = QuickPrivelegyEditForm.PARTICIPANT;
                        break;
                    case MI_DEVOICE:
                        action = QuickPrivelegyEditForm.VISITOR;
                        break;
                    case MI_ADD_MODER:
                        action = QuickPrivelegyEditForm.MODERATOR;
                        break;
                    case MI_MEMBER:
                        action = QuickPrivelegyEditForm.MEMBER;
                        break;
                    case MI_UNMEMBER:
                        action = QuickPrivelegyEditForm.NONE;
                        break;
                    case MI_ADMIN:
                        action = QuickPrivelegyEditForm.ADMIN;
                        break;
                    case MI_OWNER:
                        action = QuickPrivelegyEditForm.OWNER;
                        break;
                    default:
                        break;
                }
                if (action != -1) {
                    showForm(new QuickPrivelegyEditForm(mcontact, action, myNick));
                    return;
                }
            }
        } else if (item instanceof Group) {
            final Group group = (Group) item;
            if (group instanceof ConferenceGroup) {
                ConferenceGroup cgroup = (ConferenceGroup) group;
                String roomjid = cgroup.confContact.getJid();

                switch (mItem.index) {
//#ifdef SERVICE_DISCOVERY
                    case MI_CONFIG:
                        new QueryConfigForm(roomjid);
                        return;
//#endif
                    case MI_BANLIST:
                        showForm(new AffiliationList(roomjid, AffiliationItem.AFFILIATION_OUTCAST));
                        return;
                    case MI_MEMBERLIST:
                        showForm(new AffiliationList(roomjid, AffiliationItem.AFFILIATION_MEMBER));
                        return;
                    case MI_ADMINLIST:
                        showForm(new AffiliationList(roomjid, AffiliationItem.AFFILIATION_ADMIN));
                        return;
                    case MI_OWNERLIST:
                        showForm(new AffiliationList(roomjid, AffiliationItem.AFFILIATION_OWNER));
                        return;
                    case MI_LEAVE:
                        cgroup.leaveRoom();
                        break;
                    case MI_LEAVE_ALL:
                        BombusQD.sd.roster.leaveAllMUCs();
                        break;
                    case MI_CHANGE_NICK:
                    case MI_REJOIN:
                        cgroup.reEnterRoom();
                        return;
                    case MI_SEND_PRESENCE: {
                        showForm(new StatusSelect(((ConferenceGroup) group).confContact));
                        return;
                    }
                    case MI_DELETE:
                        showForm(new ConferenceRemoveForm(roomjid));
                        return;
                }
            } else {
                switch (mItem.index) {
                    case MI_RENAME:
                        showForm(new RenameGroup(group));
                        return;
                    case MI_DELETE:
                        AlertBox box = new AlertBox(SR.get(SR.MS_DELETE_GROUP_ASK), group.getName(), false)       {
                            public void yes() {
                                BombusQD.sd.roster.deleteGroup(group);
                            }

                            public void no() {
                            }
                        };
                        showForm(box);
                        return;
                }
            }
        }
        destroyView();
    }

    private void showForm(CanvasEx list) {
        list.setParentView(getParentView());
        list.show();
    }
}
