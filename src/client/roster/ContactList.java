/*
 * ContactList.java
 *
 * Created on 18 Август 2009 г., 13:49
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package client.roster;

import client.Contact;
import client.Group;
import client.Groups;
import client.Jid;
import conference.ConferenceGroup;
import com.alsutton.jabber.datablocks.Presence;
import java.util.Vector;
import locale.SR;

/**
 *
 * @author Vladimir Krukov
 */

public final class ContactList {

    /** Creates a new instance of ContactList */
    public ContactList() {
    }
    public Vector contacts = new Vector(0);
    public Groups groups = new Groups();
    public void resetRoster() {
        for (int i = 0; i < contacts.size(); ++i) {
            ((Contact)contacts.elementAt(i)).destroy();
        }
        contacts = new Vector(0);
        groups = new Groups();
    }
    public int getNewMessageCount() {
        int m=0;
        for (int i = contacts.size() - 1; 0 <= i; --i) {
            Contact c=(Contact)contacts.elementAt(i);
            m += c.getNewMessageCount();
        }
        return m;
    }
    public int getHighliteNewMessageCount() {
        int h=0;
        for (int i = contacts.size() - 1; 0 <= i; --i) {
            Contact c=(Contact)contacts.elementAt(i);
            h += c.getNewHighliteMsgsCount();
        }
        return h;
    }
    public void cleanupSearch() {
        for (int index = contacts.size() - 1; 0 <= index; --index) {
            Contact c = (Contact) contacts.elementAt(index);
            if (c.getGroupType() == Groups.TYPE_SEARCH_RESULT ) removeContact(c);
        }
    }
    public void cleanAllMessages() {
        for (int index = contacts.size() - 1; 0 <= index; --index) {
            Contact c = (Contact) contacts.elementAt(index);
            try {
                c.purge();
            } catch (Exception ex) { }
        }
    }

    public void removeContact(Contact c) {
        c.destroy();
        contacts.removeElement(c);
        if (null != c.group) c.group.removeContact(c);
    }

    public void addContact(Contact c, boolean self) {
        if(c.group == null) return;
        if (self) contacts.insertElementAt(c,0);
    }

    public void addContact(Contact c) {
        if (!contacts.contains(c)) contacts.addElement(c);
    }

    private Contact selfContact = null;
    public Contact getSelfContact() {
        return selfContact;
    }

    public Contact getSelfContact(Jid selfJid) {
        Contact selfContact = findContact(contacts, selfJid, false);
        if (null == selfContact) {
            selfContact = new Contact(midlet.BombusQD.sd.account.getNick(), selfJid.getBareJid(), Presence.PRESENCE_OFFLINE, null);
            Group group = getGroup(SR.get(SR.MS_SELF_CONTACT));
            if (null == group) {
                group = new Group(SR.get(SR.MS_SELF_CONTACT), Groups.TYPE_COMMON);
                addGroup(group);
            }
            addContact(selfContact);
            selfContact.setGroup(group);
            selfContact.setNick(midlet.BombusQD.sd.account.getNick());
            //System.out.println("selfContact set;");
            this.selfContact = selfContact;
        }
        return selfContact;
    }

    public Contact findContact(Vector contacts, final Jid j, final boolean compareResources) {
        int size = contacts.size() - 1;
        for (int index = size; 0 <= index; --index) {
            Contact c = (Contact) contacts.elementAt(index);
            if (c.jid.equals(j, compareResources)) return c;
        }
        return null;
    }

    public Contact getFirstContactWithNewMessage(Contact contact) {
        if (contacts.isEmpty()) {
            return null;
        }
        if (null == contact) {
            contact = (Contact)contacts.firstElement();
        }
        for (int index = contacts.indexOf(contact) + 1; index < contacts.size(); ++index) {
            Contact c = (Contact) contacts.elementAt(index);
            if (c.hasNewMsgs()) {
                return c;
            }
        }
        for (int index = 0; index < contacts.size(); ++index) {
            Contact c = (Contact) contacts.elementAt(index);
            if (c.hasNewMsgs()) {
                return c;
            }
        }
        return contact;
    }


    public void addGroup(Group group) {
        groups.addGroup(group);
    }

    public void removeGroup(Group group) {
        groups.removeGroup(group);
    }

    public Group getGroup(String name) {
        return groups.getGroup(name);
    }

    public ConferenceGroup getConferenceGroup(String name) {
        return (ConferenceGroup)groups.getGroup(name);
    }

    public void setOfflineStatus() {
        for (int index = contacts.size() - 1; 0 <= index; --index) {
            Contact c = (Contact) contacts.elementAt(index);
            c.setStatus(Presence.PRESENCE_OFFLINE); // keep error & unknown
        }
    }

    public int getOnlineCount() {
        return groups.getRosterOnline();
    }

    public int getContactCount() {
        return groups.getRosterContacts();
    }
    public void updateUI() {
        groups.update();
    }
    public Vector getVisibleTree(Vector vContacts) {
        return groups.getVisibleTree(vContacts);
    }
}
