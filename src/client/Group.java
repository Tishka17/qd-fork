/*
 * Group.java
 *
 * Created on 8.05.2005, 0:36
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

package client;
import com.alsutton.jabber.datablocks.Presence;
import images.RosterIcons;
import java.util.*;
import colors.ColorTheme;
import javax.microedition.lcdui.Graphics;
import ui.*;


/**
 *
 * @author Evg_S
 */
public class Group extends IconTextElement {
    String name;
    public byte type; // group index
    public boolean visible=true;
    protected int onlines;
    
    public byte imageExpandedIndex = RosterIcons.ICON_EXPANDED_INDEX;
    public byte imageCollapsedIndex = RosterIcons.ICON_COLLAPSED_INDEX;
    
    private Vector contacts = new Vector(0);
    public Vector visibleContacts = new Vector(0);
    
    private boolean hasUnreadMessages = false;
    protected boolean collapsed;

    public Group(String name) {
        super(RosterIcons.getInstance());
        this.name=name;
        sortKey = name.toLowerCase();
    }
    
    public Group(String name, byte type) {
        super(RosterIcons.getInstance());
        this.name = name;
        this.type = type;
        sortKey = name.toLowerCase();
    }
    
    public void destroy() {
        if(contacts.size()==0) return;
        //System.out.println("    :::Group->destroyContacts");
        visibleContacts.removeAllElements();
        visibleContacts = null;
        
        contacts.removeAllElements();
        contacts = null;
       
        if(null != name) name = null;
        if(null != sortKey) sortKey = null;
    }
    
    public boolean addContact(Contact c) {
        if (!contacts.contains(c)) {
            contacts.addElement(c);
            if (isOnline(c)) onlines++;
            return true;
        }
        return false;
    }
    
    private final boolean isOnline(Contact c) {
        return Constants.PRESENCE_OFFLINE > c.status;
    }
    
    public void removeContact(Contact c) {
        contacts.removeElement(c);
        if (isOnline(c)) onlines--;
    }
    public void updateContact(Contact c, boolean nowOnline, boolean prevOnline) {
        if (nowOnline && !prevOnline) onlines++;
        else if (!nowOnline && prevOnline) onlines--;
    }
    
    public Vector getContacts() {
        return contacts;
    }
    
    public int getColor(){ return ColorTheme.getColor(ColorTheme.GROUP_INK); }
    
    public final int getImageIndex() {
        return collapsed ? imageCollapsedIndex : imageExpandedIndex;
    }
 
    public final void drawItem(VirtualList view, Graphics g, int ofs, boolean sel) {
        int w=g.getClipWidth();
        int h=g.getClipHeight();
        int xo=g.getClipX();
        int yo=g.getClipY();

        if (collapsed && hasNewMsgs()) {
            w -= il.getWidth();
            il.drawImage(g, RosterIcons.ICON_MESSAGE_INDEX, w,0);
        }

        g.setFont(getFont());
        final String str = toStringValue;
        int offset = 4;
        final int itemHeight = getVHeight();
        if ((-1 != getImageIndex()) && (null != il)) {
            int imgHeight = il.getHeight();
            int imageYOfs = (itemHeight - imgHeight) >> 2;
            offset += imgHeight;
            il.drawImage(g, getImageIndex(), 2, imageYOfs);
        }
        
        g.setClip(xo, yo, w, h);
        g.clipRect(offset, 0, g.getClipWidth(), itemHeight);
        
        if (str!=null) {
            int fontHeight = getFont().getHeight();
            int fontYOfs = (itemHeight - fontHeight) >> 2;
            g.drawString(str, offset - ofs, fontYOfs, Graphics.TOP|Graphics.LEFT);
        }
    }

    public final String getName() { return name; }
    
    
    protected String mainbar(String mainbarStart) {
        return mainbarStart + " (" + getOnlines() + '/' + getNContacts() + ')';
    }
    
    protected String toStringValue;
    public final String toString() {
        return toStringValue;
    }

    public final void onSelect(VirtualList view){
        collapsed=!collapsed;
        midlet.BombusQD.sd.roster.setUpdateView();
        midlet.BombusQD.sd.roster.reEnumRoster();
    }

    public void updateDinamicInfo() {
        toStringValue = mainbar(name);
    }

    
    public final void updateCounters() {
        hasUnreadMessages = false;
        int nContacts = contacts.size();
        if (!visible) {
            onlines = 0; return;
        }
        visibleContacts = new Vector(0);
        int tonlines = 0;
        for (int index = 0; index < nContacts; ++index) {
            Contact c = (Contact)contacts.elementAt(index);
            boolean isVisible = isOnline(c);
            if (isVisible) tonlines++;
            
            hasUnreadMessages |= c.hasNewMsgs();
            //System.out.println(index + " :: " + c);
            if (isVisible || isVisibleContact(c)) visibleContacts.addElement(c);
        }
        onlines = tonlines;
    }

    private final boolean isVisibleContact(Contact c) {
        // hide offlines whithout new messages
        return midlet.BombusQD.cf.showOfflineContacts || c.hasNewMsgs()
                || isAlwaysVisible() || Constants.ORIGIN_GROUPCHAT == c.origin;
    }

    public final boolean hasNewMsgs() {
        return hasUnreadMessages;
    }

    private final boolean isAlwaysVisible() {
        return type == Groups.TYPE_NOT_IN_LIST || type == Groups.TYPE_TRANSP
                || type == Groups.TYPE_VISIBLE || type == Groups.TYPE_SEARCH_RESULT || type == Groups.TYPE_CONFERENCE;
    }
    
    public int getNContacts() {
        return contacts.size();
    }

    public int getOnlines() {
        return onlines;
    }
    
    private String sortKey = null;
    public int compare(IconTextElement right) {
        if (type<((Group)right).type) return -1;
        if (type>((Group)right).type) return 1;
        return sortKey.compareTo(((Group)right).sortKey);
    }
}
