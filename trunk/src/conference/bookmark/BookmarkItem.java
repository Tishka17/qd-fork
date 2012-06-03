/*
 * BookmarkItem.java
 *
 * Created on 17.09.2005, 23:21
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

package conference.bookmark;

import com.alsutton.jabber.JabberDataBlock;
import images.RosterIcons;
import ui.IconTextElement;

/**
 *
 * @author EvgS
 */

public final class BookmarkItem extends IconTextElement {
    private String desc;
    private String jid;
    private String nick;
    private String password;
    private boolean autojoin = false;
    private boolean isUrl;

    public BookmarkItem() {
        super(RosterIcons.getInstance());
    }

    public BookmarkItem(JabberDataBlock data) {
        this();
        isUrl = !data.getTagName().equals("conference");
        desc = data.getAttribute("name");
        try {
            String ajoin = data.getAttribute("autojoin").trim();
            autojoin = ajoin.equals("true") || ajoin.equals("1");
        } catch (Exception e) {
        }
        jid = data.getAttribute((isUrl) ? "url" : "jid");
        nick = data.getChildBlockText("nick");
        password = data.getChildBlockText("password");
    }

    public BookmarkItem(String desc, String jid, String nick, String password, boolean autojoin) {
        this();
        this.desc = desc;
        this.jid = jid;
        this.nick = nick;
        this.password = password;
        this.autojoin = autojoin;
    }

    public int getImageIndex() {
        if (isUrl) {
            return RosterIcons.ICON_PRIVACY_ACTIVE;
        }
        return (autojoin) ? RosterIcons.ICON_GCJOIN_INDEX : RosterIcons.ICON_GROUPCHAT_INDEX;
    }

    public String toString() {
        if (desc.length() > 0) {
            return desc;
        }

        return (nick == null) ? jid : jid + '/' + nick;
    }

    public JabberDataBlock constructBlock() {
        JabberDataBlock data = new JabberDataBlock((isUrl) ? "url" : "conference", null, null);
        data.setAttribute("name", (desc.equals("")) ? jid : desc);
        data.setAttribute((isUrl) ? "url" : "jid", jid);
        data.setAttribute("autojoin", (autojoin) ? "true" : "false");
        if (nick != null) {
            if (nick.length() > 0) {
                data.addChild("nick", nick);
            }
        }
        if (password.length() > 0) {
            data.addChild("password", password);
        }

        return data;
    }

    public int compare(IconTextElement right) {
        String th = (nick == null) ? jid : jid + '/' + nick;
        return th.compareTo(right.toString());
    }

    public String getJidNick() { //patch by Tishka17
        if (nick == null || nick.length() == 0) {
            return jid + '/' + midlet.BombusQD.sd.account.getNickName();
        }
        return jid + '/' + nick;
    }

    public String getJid() {
        return jid;
    }

    public String getMyNick() {
        return nick;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public boolean isURL() {
        return isUrl;
    }

    public boolean isAutoJoin() {
        return autojoin;
    }

    public void setAutoJoin(boolean autojoin) {
        this.autojoin = autojoin;
    }
}
