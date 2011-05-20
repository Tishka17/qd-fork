/*
 * Groups.java
 *
 * Created on 8.05.2005, 0:36
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

import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Iq;
import java.util.Enumeration;
import java.util.Vector;
import locale.SR;
import ui.VirtualList;

/**
 *
 * @author Evg_S,aqent
 */

public class Groups implements JabberBlockListener{

    Vector groups;

    public final static byte TYPE_SELF=0;
    public final static byte TYPE_NO_GROUP=1;
    public final static byte TYPE_COMMON=2;
    public final static byte TYPE_VISIBLE=3;
    public final static byte TYPE_VIP=4;
    public final static byte TYPE_IGNORE=5;
    public final static byte TYPE_MUC=6;
    public final static byte TYPE_SEARCH_RESULT=7;
    public final static byte TYPE_NOT_IN_LIST=8;
    public final static byte TYPE_TRANSP=9;
    public final static byte TYPE_CONFERENCE=10;

    public final static String COMMON_GROUP=SR.get(SR.MS_GENERAL);

    private final static String GROUPSTATE_NS="http://bombusmod-qd.wen.ru/groups";

    private Group[] spetialGroup = new Group[11];
    public Groups(){
        groups=new Vector(0);
        addGroup(SR.get(SR.MS_TRANSPORTS), TYPE_TRANSP);
        addGroup(SR.get(SR.MS_SELF_CONTACT), TYPE_SELF);
        addGroup(SR.get(SR.MS_SEARCH_RESULTS), TYPE_SEARCH_RESULT);
        addGroup("Conferences", TYPE_CONFERENCE);
        addGroup(SR.get(SR.MS_NOT_IN_LIST), TYPE_NOT_IN_LIST);
        addGroup(SR.get(SR.MS_IGNORE_LIST), TYPE_IGNORE);
        addGroup(SR.get(SR.MS_VISIBLE_GROUP), TYPE_VISIBLE);
        addGroup(SR.get(SR.MS_VIP_GROUP), TYPE_VIP);
        addGroup(Groups.COMMON_GROUP, TYPE_NO_GROUP);
    }

    public void destroy() {

    }

    public Group getGroup(int type) {
        return spetialGroup[type];
    }

    public Group getGroup(String name) {
        int size = groups.size();
        for (int i = 0; i < size; ++i) {
            Group grp=(Group)groups.elementAt(i);
            if (name.equals(grp.name)) return grp;
        }
        return null;
    }

    public Group addGroup(String name, byte type) {
        Group ng=new Group(name, type);
        spetialGroup[type] = ng;
        return addGroup(ng);
    }

    public Group addGroup(Group ng) {
        groups.addElement(ng);
        VirtualList.sort(groups);
        return ng;
    }

    public Vector getRosterGroupNames(){
        Vector s=new Vector(0);
        for (int i=0; i<groups.size(); i++) {
	    Group grp=(Group) groups.elementAt(i);
            if (grp.type<TYPE_NO_GROUP) continue;
            if (grp.type>TYPE_IGNORE) continue;
            s.addElement(grp.name);
        }
        return s;
    }

    public int getCount() {return groups.size();}

    public int getRosterContacts() { return rosterContacts; }
    public int getRosterOnline() { return rosterOnline; }

    public void removeGroup(Group g) {
        groups.removeElement(g);
    }

    public int blockArrived(JabberDataBlock data) {
        if (data instanceof Iq) {
            if (data.getTypeAttribute().equals("result")) {
                JabberDataBlock query=data.findNamespace("query", "jabber:iq:private");
                if (query==null)
                    return BLOCK_REJECTED;
                JabberDataBlock gs=query.findNamespace("gs", GROUPSTATE_NS);
                if (gs==null || gs.getChildBlocks()==null)
                    return BLOCK_REJECTED;
                for (Enumeration e=gs.getChildBlocks().elements(); e.hasMoreElements();) {
                    JabberDataBlock item=(JabberDataBlock)e.nextElement();
                    String groupName=item.getText();
                    boolean collapsed=item.getAttribute("state").equals("collapsed");
                    Group grp=getGroup(groupName);
                    if (grp==null)
                        continue;
                    grp.collapsed=collapsed;
                }
                midlet.BombusQD.sd.roster.reEnumRoster();
                return NO_MORE_BLOCKS;
            }
        }
        return BLOCK_REJECTED;
    }

    public void queryGroupState(boolean get) {
        if (!midlet.BombusQD.sd.roster.isLoggedIn())
            return;

        JabberDataBlock iq=new Iq(null, (get)? Iq.TYPE_GET : Iq.TYPE_SET, (get)? "queryGS" : "setGS");
        JabberDataBlock query=iq.addChildNs("query", "jabber:iq:private");
        JabberDataBlock gs=query.addChildNs("gs", GROUPSTATE_NS);

        if (get) {
            midlet.BombusQD.sd.roster.theStream.addBlockListener(this);
        } else {
            for (Enumeration e=groups.elements(); e.hasMoreElements();) {
                Group grp=(Group)e.nextElement();
                if (grp.collapsed) {
                    gs.addChild("item", grp.getName()).setAttribute("state", "collapsed");
                }
            }
        }
        midlet.BombusQD.sd.roster.theStream.send(iq);
    }

    private int rosterContacts;
    private int rosterOnline;

    public final void update() {
        // self-contact group
        Group selfContactGroup = getGroup(TYPE_SELF);
        if (midlet.BombusQD.cf.selfContact) {
            selfContactGroup.visible = true;
        } else {
           selfContactGroup.visible = selfContactGroup.hasNewMsgs();
        }
        //if (!selfContactGroup.visible) selfContactGroup.visible |= selfContactGroup.hasNewMsgs(); //?? Stupid code

        // hiddens
        getGroup(TYPE_IGNORE).visible = midlet.BombusQD.cf.ignore;
        //getGroup(TYPE_CONFERENCE).visible = true;
        //getGroup(TYPE_SEARCH_RESULT).visible = true;
        //getGroup(TYPE_VISIBLE).visible = true;
        // transports
        Group transpGroup = getGroup(TYPE_TRANSP);
        transpGroup.visible = (midlet.BombusQD.cf.showTransports || transpGroup.hasNewMsgs());

        rosterContacts = 0;
        rosterOnline = 0;
        for (int i = 0; i < getCount(); i++) {
            Group g = (Group)groups.elementAt(i);
            g.updateCounters();
            if (g.type >= TYPE_MUC) continue;//dont count muc-contacts
            rosterContacts += g.getNContacts();
            rosterOnline += g.getOnlines();
        }
    }

    public void addToVector(Vector d, Group gr) {
        if (!gr.visible) return;
        if (0 == gr.getNContacts()) return;

        int groupType = gr.type;
        if( groupType == TYPE_NO_GROUP || groupType == TYPE_COMMON) {
            if(0 == gr.onlines && !midlet.BombusQD.cf.showOfflineContacts) return;
        }

        d.addElement(gr);
        Vector contacts = gr.visibleContacts;
         if (!gr.collapsed) {
           int size = contacts.size();
           for(int i=0; i<size; ++i) d.addElement(contacts.elementAt(i));
        }
        gr.updateDynamicInfo();
        //if (gr.type>Groups.TYPE_MUC) return; //don't count this contacts
    }


    public final Vector getVisibleTree(Vector vContacts) {//reEnum
        //Vector vContacts = new Vector(0);
        for (int i = 0; i < getCount(); i++) {
            Group g = (Group)groups.elementAt(i);
            addToVector(vContacts, g);
        }
        return vContacts;
    }
 }

