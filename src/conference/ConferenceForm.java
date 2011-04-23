/*
 * ConferenceForm.java
 *
 * Created on 24.07.2005, 18:32
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

package conference;

import conference.bookmark.BookmarkItem;
import conference.bookmark.BookmarkQuery;
import client.Config;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Presence;
import images.MenuIcons;
import javax.microedition.lcdui.TextField;
import locale.SR;
import ui.controls.form.CheckBox;
import ui.controls.form.DefForm;
import ui.controls.form.NumberInput;
import ui.controls.form.PasswordInput;
import ui.controls.form.TextInput;
//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.CommandListener;
//# import javax.microedition.lcdui.Command;
//#else
import menu.Command;
//#endif
//#ifdef GRAPHICS_MENU
import menu.MenuListener;
import ui.GMenu;
import ui.GMenuConfig;
//#endif
/**
 *
 * @author EvgS,aqent
 */
public final class ConferenceForm extends DefForm implements MenuListener {
//#ifndef MENU
    Command cmdJoin;
    Command cmdAdd;
    Command cmdSave;
//#endif
    private TextInput roomField;
    private TextInput hostField;
    private TextInput nickField;
    private TextInput nameField;
    private PasswordInput passField;
    private NumberInput msgLimitField;
    private CheckBox autoJoin;

    BookmarkItem editConf;

    public ConferenceForm(String name, String confJid, String password, boolean autojoin) {
        super(SR.get(SR.MS_JOIN_CONFERENCE));

        int roomEnd=confJid.indexOf('@');
        String room="";
        if (roomEnd>0) room=confJid.substring(0, roomEnd);
        String server;
        String nick = null;
        int serverEnd=confJid.indexOf('/');
        if (serverEnd>0) {
            server=confJid.substring(roomEnd+1,serverEnd);
            nick=confJid.substring(serverEnd+1);
        } else {
            server=confJid.substring(roomEnd+1);
        }
        createForm(name, room, server, nick, password, autojoin);
    }

    public ConferenceForm(BookmarkItem join) {
        super(SR.get(SR.MS_JOIN_CONFERENCE));
        if (join==null) {
            return;
        }
        if (join.isURL()) {
            return;
        }

        this.editConf=join;

        String confJid=join.getJidNick();
        int roomEnd=confJid.indexOf('@');
        String room="";
        if (roomEnd>0) room=confJid.substring(0, roomEnd);
        String server;
        String nick=null;
        int serverEnd=confJid.indexOf('/');
        if (serverEnd>0) {
            server=confJid.substring(roomEnd+1,serverEnd);
            nick=confJid.substring(serverEnd+1);
        } else {
            server=confJid.substring(roomEnd+1);
        }
        createForm(join.getDesc(), room, server, nick, join.getPassword(), join.isAutoJoin());
    }

    public ConferenceForm() {
        super(SR.get(SR.MS_JOIN_CONFERENCE));

        String room = Config.defConference;
        String server=null;
        // trying to split string like room@server
        int roomE=room.indexOf('@');
        if (roomE>0) {
            server=room.substring(roomE+1);
            room=room.substring(0, roomE);
        }
        // default server
        if (server==null) server="conference."+midlet.BombusQD.sd.account.getServer();
        createForm(null, room, server, null, null, false);
    }

    public ConferenceForm(String name, String room, String server, String nick, String password, boolean autojoin) {
        super(SR.get(SR.MS_JOIN_CONFERENCE));

        createForm(name, room, server, nick, password, autojoin);
    }

    private void createForm(String name, String room, String server, String nick, final String password, boolean autojoin) {
        cmdJoin=new Command(SR.get(SR.MS_JOIN), MenuIcons.ICON_OK);
        cmdAdd=new Command(SR.get(SR.MS_ADD_BOOKMARK), MenuIcons.ICON_ADD);
        cmdSave=new Command(SR.get(SR.MS_SAVE), MenuIcons.ICON_SAVE);

        roomField=new TextInput(SR.get(SR.MS_ROOM), room, TextField.ANY);
        addControl(roomField);

        hostField=new TextInput(SR.get(SR.MS_AT_HOST), server, "muc-host", TextField.ANY);
        addControl(hostField);

        if (nick==null) nick=midlet.BombusQD.sd.account.getNickName();
        nickField=new TextInput(SR.get(SR.MS_NICKNAME), nick, "roomnick", TextField.ANY);
        addControl(nickField);

        msgLimitField=new NumberInput(SR.get(SR.MS_MSG_LIMIT), midlet.BombusQD.cf.confMessageCount, 0, 100);
        addControl(msgLimitField);

        nameField=new TextInput(SR.get(SR.MS_DESCRIPTION), name, TextField.ANY);
        addControl(nameField);

        passField=new PasswordInput(SR.get(SR.MS_PASSWORD), password);
        addControl(passField);

        autoJoin=new CheckBox(SR.get(SR.MS_AUTOLOGIN), autojoin);
        addControl(autoJoin);
    }

    public void commandAction(Command c){
        String nick = nickField.getValue();
        String room = roomField.getValue();
        String host = hostField.getValue();

        if (nick.length()==0 || room.length()==0 || host.length()==0) {
            return;
        }

        String name = nameField.getValue();
        String pass = passField.getValue();

        int msgLimit = msgLimitField.getIntValue();
        boolean autojoin = autoJoin.getValue();

        StringBuffer gchat = new StringBuffer(room.trim()).append('@').append(host.trim());

        if (name.length() ==0) {
            name = gchat.toString();
        }

        saveMsgCount(msgLimit);

        if (c==cmdSave) {
            editConf.setJid(gchat.toString());
            editConf.setDesc(name);
            editConf.setPassword(pass);
            editConf.setNick(nick);
            editConf.setAutoJoin(autojoin);

            new BookmarkQuery(BookmarkQuery.SAVE);
            destroyView();
        } else if (c==cmdAdd) {
            midlet.BombusQD.sd.roster.bookmarks.addElement(new BookmarkItem(name, gchat.toString(), nick, pass, autojoin));

            new BookmarkQuery(BookmarkQuery.SAVE);
            destroyView();
        } else if (c==cmdJoin) {
            try {
                Config.defConference = room + "@" + host;
                 gchat.append('/').append(nick);
                join(name, gchat.toString(),pass, msgLimit);
                midlet.BombusQD.sd.roster.show();
            } catch (Exception e) { }
        }
    }

    public void commandState(){
//#ifdef MENU_LISTENER
        menuCommands.removeAllElements();
//#endif
        addCommand(cmdJoin);
        addCommand(cmdAdd);
        if (editConf != null) {
            addCommand(cmdSave);
        }
    }

//#ifdef MENU_LISTENER
    public String touchLeftCommand(){ return SR.get(SR.MS_MENU); }

//#ifdef GRAPHICS_MENU
    public void touchLeftPressed(){
        showGraphicsMenu();
    }

    public int showGraphicsMenu() {
        commandState();
        menuItem = new GMenu(this, menuCommands);
        GMenuConfig.getInstance().itemGrMenu = GMenu.CONFERENCE_FORM;
        redraw();
        return GMenu.CONFERENCE_FORM;
    }
//#else
//#     public void touchLeftPressed(){
//#         showMenu();
//#     }
//#     public void showMenu() {
//#         commandState();
//#         new MyMenu(display, parentView, this, SR.get(SR.MS_JOIN_CONFERENCE), null, menuCommands);
//#    }
//#endif


//#endif

    private void saveMsgCount(int msgLimit) {
        if (midlet.BombusQD.cf.confMessageCount!=msgLimit) {
            midlet.BombusQD.cf.confMessageCount=msgLimit;
            midlet.BombusQD.cf.saveToStorage();
        }
    }

    public static void join(String name, String jid, String pass, int maxStanzas) {
        ConferenceGroup grp=midlet.BombusQD.sd.roster.initMuc(jid, pass);
        grp.desc=name;
        JabberDataBlock x=new JabberDataBlock("x", null, null);
        x.setNameSpace("http://jabber.org/protocol/muc");
        if (pass.length()!=0) {
            x.addChild("password", pass); // adding password to presence
        }

        JabberDataBlock history=x.addChild("history", null);
        history.setAttribute("maxstanzas", Integer.toString(maxStanzas));
        history.setAttribute("maxchars","32768");
        try {
            long last=grp.confContact.lastMessageTime;
            long delay= ( grp.conferenceJoinTime - last ) /1000 ;
            if (last!=0)
                history.setAttribute("seconds",String.valueOf(delay)); // todo: change to since
        } catch (Exception e) {}

        int status=midlet.BombusQD.sd.roster.myStatus;
        if (status==Presence.PRESENCE_INVISIBLE)
            status=Presence.PRESENCE_ONLINE;
        midlet.BombusQD.sd.roster.sendDirectPresence(status, jid, x);
        grp.inRoom=true;
    }
}
