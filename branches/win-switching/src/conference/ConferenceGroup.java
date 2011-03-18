/*
 * ConferenceGroup.java
 *
 * Created on 29.11.2005, 23:11
 * Copyright (c) 2005-2008, Eugene Stahov (evgs), http://bombus-im.org
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

package conference;

import client.Contact;
import client.Group;
import client.Groups;
import client.Jid;
import images.RosterIcons;
import client.Constants;

/**
 *
 * @author EvgS
 */

public final class ConferenceGroup extends Group{
    public boolean inRoom=true;
    
    public MucContact selfContact;
    public MucContact confContact;
    
    public String password;
    public String label;
    public String desc;
    
    public ConferenceGroup(String name, String label) {
        super(name, Groups.TYPE_MUC);
        this.label = label;
    }
    
    public int getOnlines() {
        return Math.max(onlines - 1, 0);
    }

    public int getNContacts() {
        return Math.max(super.getNContacts() - 1, 0);
    }
    
    public void leaveRoom() {
        confContact.commonPresence = false; //disable reenter after reconnect
        midlet.BombusQD.sd.roster.sendPresence(selfContact.getJid(), "unavailable", null, true);
        inRoom = false;
        midlet.BombusQD.sd.roster.roomOffline(this, false);
    }

    public void reEnterRoom() {
        String confJid = selfContact.getJid();
        String name = desc;
        new ConferenceForm(midlet.BombusQD.getInstance().display ,midlet.BombusQD.sd.roster, name, confJid, password, false);
    }

    public long conferenceJoinTime;
    
    public void updateDynamicInfo() {
        toStringValue = mainbar((null == desc) ? label : desc);
    }

    public MucContact findMucContact(Jid jid) {
        Contact contact = midlet.BombusQD.sd.roster.contactList.findContact(getContacts(), jid, true);
        try {
            return (MucContact) contact;
        } catch (Exception e) {
            // drop buggy bookmark in roster
            midlet.BombusQD.sd.roster.contactList.removeContact(contact);
            contact.destroy();
            return null;
        }
    }
    
    public MucContact getSelfContact(String jid) {
        // check for existing entry - it may be our old self-contact
        // or another contact whose nick we pretend
        MucContact selfContact = findMucContact( new Jid(jid) );
        if (null == selfContact) {
            // old self-contact
            selfContact = this.selfContact;
        }
        String nick = jid.substring(jid.indexOf('/') + 1);
        // create self-contact if no any candidates found
        if (null == selfContact) {
            selfContact = new MucContact(nick, jid);
        }

        // rename contact
        if (Constants.PRESENCE_OFFLINE <= selfContact.status) {
            selfContact.setNick(nick);
            selfContact.jid.setJid(jid);
            //selfContact.setBareJid(jid);
        }

        selfContact.setGroup(this);
        selfContact.origin = Constants.ORIGIN_GC_MYSELF;
        selfContact.setNick(selfContact.getNick());
        
        this.selfContact = selfContact;
        return selfContact;
    }
    public MucContact getConfContact() {
        if (null == confContact) {
            MucContact mucContact = findMucContact(new Jid(getName()));

            if (null == mucContact) {
                mucContact = new MucContact(label, getName());
                midlet.BombusQD.sd.roster.addContact(mucContact);
            }
            mucContact.setGroup(this);
            mucContact.transport = RosterIcons.ICON_GROUPCHAT_INDEX; //FIXME: убрать хардкод
            mucContact.origin = Constants.ORIGIN_GROUPCHAT;
            mucContact.setNick(mucContact.getNick());
            confContact = mucContact;
        }
        return confContact;
    }
    
    public MucContact getContact(String jid) {
        MucContact c = findMucContact(new Jid(jid));
        if (null == c) {
            String nick = jid.substring(jid.indexOf('/') + 1);
            c = new MucContact(nick, jid);
            c.origin = Constants.ORIGIN_GC_MEMBER;
            c.setNick(c.getNick());
            midlet.BombusQD.sd.roster.addContact(c);
        }
        c.setGroup(this);
        return c;
    }
    
    public int getImageIndex() {
        return collapsed ? RosterIcons.ICON_GCCOLLAPSED_INDEX : RosterIcons.ICON_GCJOIN_INDEX;
    }
}
