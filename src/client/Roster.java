/*
 * Roster.java
 *
 * Created on 6.01.2005, 19:16
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

import client.msgedit.AltMessageEdit;
import client.msgedit.BaseMessageEdit;
import client.msgedit.MessageEdit;
import util.Time;
import account.Account;
import account.AccountRemoveForm;
import account.AccountSelect;
import account.ChangePasswordForm;
import alert.AlertCustomize;
import alert.AlertProfile;
import client.roster.ContactList;
//#ifndef WMUC
import conference.bookmark.BookmarkItem;
import conference.bookmark.BookmarkQuery;
import conference.bookmark.Bookmarks;
import conference.ConferenceGroup;
import conference.MucContact;
import conference.affiliation.QuickPrivelegyEditForm;
//#endif
import images.MenuIcons;
//#ifdef ARCHIVE
import archive.ArchiveList;
//#endif
//#ifdef CLIENTS_ICONS
import images.ClientsIconsData;
//#endif
import images.RosterIcons;
import menu.MenuListener;
import menu.Command;
//#if FILE_IO && FILE_TRANSFER
import io.file.transfer.TransferDispatcher;
import io.file.transfer.TransferTask;
//#endif
import locale.SR;
import login.LoginListener;
//#ifdef NON_SASL_AUTH
//# import login.NonSASLAuth;
//#endif
//#if SASL_XGOOGLETOKEN
import login.GoogleTokenAuth;
//#endif
import login.SASLAuth;
import midlet.BombusQD;
import ui.controls.AlertBox;
import util.StringUtils;
import vcard.*;
import com.alsutton.jabber.*;
import com.alsutton.jabber.datablocks.*;
import java.util.*;
import ui.*;
import xmpp.EntityCaps;
import xmpp.XmppError;
//#ifdef CAPTCHA
import xmpp.extensions.Captcha;
//#endif
import xmpp.extensions.IqQueryRoster;
//#if SASL_XGOOGLETOKEN
import xmpp.extensions.IqGmail;
//#endif
import xmpp.extensions.IqLast;
import xmpp.extensions.IqPing;
import xmpp.extensions.IqVersionReply;
import xmpp.extensions.IqTimeReply;
//#if SERVICE_DISCOVERY && ADHOC
import xmpp.extensions.IQCommands;
//#endif
//#ifdef SERVICE_DISCOVERY
import conference.QueryConfigForm;
//#endif
//#ifdef PEP
import xmpp.extensions.PepListener;
//#endif
import javax.microedition.lcdui.Image;
//#ifdef FILE_IO
import io.file.FileIO;
import java.io.IOException;
import java.io.OutputStream;
//#endif
//#ifdef SERVICE_DISCOVERY
import disco.ServiceDiscovery;
//#endif
import conference.ConferenceForm;
//#ifdef STATS
import stats.Stats;
//#endif
//#ifdef XML_CONSOLE
//# import console.xml.XMLConsole;
//#endif
//#ifdef DEBUG_CONSOLE
//# import console.debug.DebugConsole;
//#endif
//#ifdef LIGHT_CONTROL
import light.CustomLight;
//#endif
//#ifdef JUICK.COM
import xmpp.extensions.JuickModule;
//#endif
import disco.ServerStatsForm;
//#ifdef HISTORY
import history.HistoryViewer;
//#endif
//#if ROSTERX
import xmpp.extensions.RosterXListener;
//#endif
public final class Roster extends VirtualList
            implements JabberListener, MenuListener, Runnable, LoginListener {
    public final static int DROP_MESSAGES_PRESENCES = 0;
    public final static int DROP_PRESENCES = 1;
    public final static int ALLOW_ALL = 2;

    public final static byte INC_NONE=0;
    public final static byte INC_APPEARING=1;
    public final static byte INC_VIEWING=2;

    //#ifdef JUICK.COM
    private final JuickModule juick = JuickModule.jm();
    //#endif
//#if ROSTERX
    private final RosterXListener rosterx = new RosterXListener();
//#endif

    public Contact activeContact = null;
    private Jid myJid;
    public JabberStream theStream = null;
    public int messageCount;
    int highliteMessageCount;
    public Object eventIcon;

    public ContactList contactList = new ContactList();
    private Vector vContacts = new Vector(0);

    public Vector bookmarks;

    public int myStatus=midlet.BombusQD.cf.loginstatus;
    private static String myMessage;
    private static int lastOnlineStatus;

    public boolean doReconnect=false;

    public boolean querysign=false;

//#if SASL_XGOOGLETOKEN
    private String token;
//#endif
    public int currentReconnect=0;
    public long lastMessageTime=Time.utcTimeMillis();

    public static String startTime=Time.dispLocalTime();

    private int blState=Integer.MAX_VALUE;

    private BaseMessageEdit msgEditor;
    public String rosterVersion=null;

    public Roster() { //init
        super();
        canBack = false;

        initCommands();

        setLight(midlet.BombusQD.cf.lightState);

        MainBar mainbar=new MainBar(4, null, null, false);
        setMainBarItem(mainbar);
        mainbar.addRAlign();
        mainbar.addElement(null);
        mainbar.addElement(null);
        mainbar.addElement(null); //ft
        updateMainBar();
    }

    public void showActiveContacts(Contact current) {
        Vector contacts = new Vector();
        Vector hContacts = getHContacts();
        
        Contact contact;
        int size = hContacts.size();
        for (int i = 0; i < size; ++i) {
           contact = (Contact)hContacts.elementAt(i);
           if (contact.active()) {
               contacts.addElement(contact);
           }
        }
        if (!contacts.isEmpty()) {
            ActiveContacts form = new ActiveContacts(contacts, current);
            form.setParentView(this);
            form.show();
            if (current!=null && current.getMessageList()!=null) 
                current.getMessageList().updateSeparator();
        }
    }

    public void showActionsMenu(Object object) {
       if (isLoggedIn()) {
           if (object instanceof Group) {
               int type = ((Group)object).type;
               if (type == Groups.TYPE_TRANSP || 
                       type == Groups.TYPE_SELF || 
                       type == Groups.TYPE_NOT_IN_LIST) {
                   return;
               }
           }

           new ActionsMenu(object).show();
       }
    }

    public void createMessageEdit(){
        if (Config.getInstance().msgEditType == 0) {
            msgEditor = new MessageEdit();
        } else {
            msgEditor = new AltMessageEdit();
        }
    }

    public void updateBarsFont() {
        mainbar=new MainBar(4, null, null, false);
        MainBar secondBar=new MainBar("", true);
        setMainBarItem(mainbar);
        setInfoBarItem(secondBar);

        mainbar.addRAlign();
        mainbar.addElement(null);
        mainbar.addElement(null);
        mainbar.addElement(null);

        secondBar.addElement(null);
        secondBar.addRAlign();
        secondBar.addElement(null);

        updateMainBar();
    }

    public void setLight(boolean state) {
//#ifndef Android
        if (phoneManufacturer==Config.SIEMENS || phoneManufacturer==Config.SIEMENS2) {
            try {
                if (state) com.siemens.mp.game.Light.setLightOn();
                else com.siemens.mp.game.Light.setLightOff();
            } catch( Exception e ) { }
            return;
        }
        if (!state) return;
//#endif
    }

    public void initCommands() {
//#ifndef ANDROID
        createMessageEdit();
//#endif
        StatusList.getInstance().reinit();

        cmdStatus = new Command(SR.get(SR.MS_STATUS), MenuIcons.ICON_STATUS);

        cmdOptions = new Command(SR.get(SR.MS_OPTIONS), 0x03);
        cmdMyService = new Command(SR.get(SR.MS_SERVICE), 0x90);
        cmdAlert = new Command(SR.get(SR.MS_ALERT_PROFILE_CMD), MenuIcons.ICON_NOTIFY);
//#ifdef ARCHIVE
        cmdArchive = new Command(SR.get(SR.MS_ARCHIVE), MenuIcons.ICON_ARCHIVE);
//#endif
//#ifdef XML_CONSOLE
//#         cmdXMLConsole = new Command(SR.get(SR.MS_XML_CONSOLE), MenuIcons.ICON_CONCOLE);
//#endif

//#ifdef DEBUG_CONSOLE
//#         cmdDebugConsole = new Command(SR.get(SR.MS_DEBUG_MENU), MenuIcons.ICON_CONCOLE);
//#endif

        cmdAccount = new Command(SR.get(SR.MS_ACCOUNT_), MenuIcons.ICON_VCARD);
        cmdInfo = new Command(SR.get(SR.MS_ABOUT), MenuIcons.ICON_CHECK_UPD);
        cmdMinimize = new Command(SR.get(SR.MS_APP_MINIMIZE), MenuIcons.ICON_FILEMAN);
        cmdQuit = new Command(SR.get(SR.MS_APP_QUIT), MenuIcons.ICON_QUIT);
    }

    private static Command cmdStatus;
    private static Command cmdOptions;
    private static Command cmdMyService;

     private static Command cmdAlert;
//#ifdef XML_CONSOLE
//#       private static Command cmdXMLConsole;
//#endif
//#ifdef DEBUG_CONSOLE
//#       private static Command cmdDebugConsole;
//#endif

//#ifdef ARCHIVE
    private static Command cmdArchive;
//#endif
    private static Command cmdAccount;
    private static Command cmdInfo;
    private static Command cmdMinimize;
    private static Command cmdQuit;

    public void commandState(){
        menuCommands.removeAllElements();

        addCommand(cmdStatus);
        if (isLoggedIn()) {
            addCommand(cmdMyService);
        }
        addCommand(cmdAlert);
        addCommand(cmdAccount);
//#ifdef ARCHIVE
        addCommand(cmdArchive);
//#endif
        addCommand(cmdOptions);

//#ifdef XML_CONSOLE
//#         if(midlet.BombusQD.cf.userAppLevel==1) {
//#             addCommand(cmdXMLConsole);
//#         }
//#endif

//#ifdef DEBUG_CONSOLE
//#         if(midlet.BombusQD.cf.userAppLevel==1) {
//#             if (midlet.BombusQD.cf.debug) {
//#                 addCommand(cmdDebugConsole);
//#             }
//#         }
//#endif

        addCommand(cmdInfo);
        if (midlet.BombusQD.cf.allowMinimize)
            addCommand(cmdMinimize);
        if (phoneManufacturer != Config.NOKIA_9XXX) {
            addCommand(cmdQuit);
        }
    }

    public void commandAction(Command c){
        if(c==cmdOptions) {
            new ConfigForm().show();
//#ifdef SERVICE_DISCOVERY
        } else if(c==cmdMyService) {
            new ServiceDiscovery(null, null, false).show();
//#endif
//#ifdef XML_CONSOLE
//#             } else if(c==cmdXMLConsole){
//#                 new XMLConsole().show();
//#endif
//#ifdef DEBUG_CONSOLE
//#           } else if(c==cmdDebugConsole){
//#                 new DebugConsole().show();
//#endif
         } else if (c==cmdMinimize) { 
             cmdMinimize();  
         } else if (c==cmdAccount) { 
             cmdAccount(); 
         } else if (c==cmdStatus) { 
             cmdStatus(); 
         } else if (c==cmdAlert) { 
             cmdAlert();
//#ifdef ARCHIVE
         } else if (c==cmdArchive) { 
             cmdArchive();
//#endif
         } else if (c==cmdInfo) {
             cmdInfo(); 
         } else if (c==cmdQuit) { 
             cmdQuit(); 
         }

    }

    public void cmdMinimize() {
        BombusQD.hideApp();
    }

    public void cmdAccount() {
        new AccountSelect(-1).show();
    }

    public void cmdStatus() {
        currentReconnect = 0;
        new StatusSelect(null).show();
    }

    public void cmdAlert() { new AlertProfile().show(); }

//#ifdef ARCHIVE
    public void cmdArchive() {
        new ArchiveList().show();
    }
//#endif

    public void cmdInfo() {
        new info.InfoWindow().show();
    }

    public void setProgress(String pgs,int percent){
  if (mainbar!=null)
             mainbar.setElementAt(pgs, 3);
        redraw();
    }

    // establishing connection process
    public void run(){
        setQuerySign(true);
        if (!doReconnect) {
            resetRoster();
        }
        try {
            Account a=midlet.BombusQD.sd.account;
//#if SASL_XGOOGLETOKEN
            if (a.useGoogleToken()) {
                setProgress(SR.get(SR.MS_TOKEN), 30);
                token=new GoogleTokenAuth(a).responseXGoogleToken();
                if (token==null) throw new SecurityException("Can't get Google token");
            }
//#endif
            setProgress(SR.get(SR.MS_CONNECT_TO_)+a.getServer(), 30);
            theStream = a.openJabberStream();
            setProgress(SR.get(SR.MS_OPENING_STREAM), 40);
            theStream.setJabberListener( this );
            theStream.initiateStream();
        } catch( Exception e ) {
//#ifdef DEBUG
//#             e.printStackTrace();
//#endif
            setProgress(SR.get(SR.MS_FAILED), 100);
            doReconnect=false;
            myStatus=Presence.PRESENCE_OFFLINE;
            setQuerySign(false);
            redraw();
            askReconnect(e);
        }
    }

    public void resetRoster() {
//#ifdef DEBUG_CONSOLE
//#         if(midlet.BombusQD.cf.debug) midlet.BombusQD.debug.add("::resetRoster",10);
//#endif
        contactList.resetRoster();
        vContacts = new Vector(0); // just for displaying
        rosterVersion = "";
        bookmarks = null;
        Jid myJid = new Jid(midlet.BombusQD.sd.account.getJid());
        setMyJid(myJid);
        Contact selfContact = contactList.getSelfContact(myJid);
        if (null != selfContact) {
            setMyJid(myJid);
        }
    }

    public void systemGC() {
        Runtime.getRuntime().gc();
        try {
            Thread.sleep(100);
        } catch (Exception e){ }
    }

    public void errorLog(String s){
        if (s==null) return;
        Msg m=new Msg(Msg.OUTGOING, "local", "Info", s);
        messageStore(selfContact(), m);
        selfContact().getMessageList().reEnumCounts();
//#ifdef DEBUG_CONSOLE
//#         midlet.BombusQD.debug.add(s,10);
//#endif
    }


    public void beginPaint() {
        reEnumerator.update();
        //paintVContacts=vContacts;
    }

    public VirtualElement getItemRef(int Index){
        return (VirtualElement) vContacts.elementAt(Index);
    }

    public int getItemCount(){
        return vContacts.size();
    }

    public Object getEventIcon() {
        return eventIcon;
    }

    private void updateMainBar(){
        int profile = midlet.BombusQD.cf.currentAlertProfile;

        if(0 != messageCount) {
            mainbar.setElementAt(new Integer(RosterIcons.ICON_MESSAGE_INDEX), 0);
            mainbar.setElementAt(getHeaderString(), 1);
        } else {
             mainbar.setElementAt(null, 0);
             mainbar.setElementAt(null, 1);
        }
        mainbar.setElementAt(querysign ? new Integer(RosterIcons.ICON_PROGRESS_INDEX) : new Integer(myStatus), 2);
        if (profile > 0) {
            mainbar.setElementAt(new Integer(profile + RosterIcons.ICON_PROFILE_INDEX + 1), 5);
        } else {
            mainbar.setElementAt(null, 5);
        }

        if (phoneManufacturer == Config.WINDOWS) {
            if (messageCount == 0) {
                setTitle("BombusQD");
            } else {
                setTitle("BombusQD " + getHeaderString());
            }
        }
    }

    public String getHeaderString() {
        StringBuffer buf = new StringBuffer();
        buf.append(' ');
        if(0 < highliteMessageCount) {
            buf.append(highliteMessageCount).append('/');
        }
        buf.append(messageCount);
        buf.append(' ');
       return buf.toString();
    }


    public void setEventIcon(Object icon){
        eventIcon=icon;
        mainbar.setElementAt(icon, 7);
        redraw();
    }

    public boolean countNewMsgs() {
        highliteMessageCount = contactList.getHighliteNewMessageCount();
        messageCount = contactList.getNewMessageCount();
        updateMainBar();
        return (messageCount > 0);
    }

    public void cleanupSearch() {
        contactList.cleanupSearch();
        setModified();
        reEnumRoster();
     }


    public void cmdCleanAllMessages(){
        if (messageCount>0) {
            AlertBox box = new AlertBox(SR.get(SR.MS_UNREAD_MESSAGES)+": "+messageCount, SR.get(SR.MS_SURE_DELETE), AlertBox.BUTTONS_YESNO) {
                public void yes() { cleanAllMessages(); }
            };
            box.show();
        } else {
            cleanAllMessages();
        }
    }

     public void cleanAllMessages(){
         contactList.cleanAllMessages();
         highliteMessageCount=0;
         messageCount=0;

         show();
         setModified();
         redraw();
     }

//#ifndef WMUC
    public void roomOffline(final Group group, boolean leaveRoom) {
        int size = contactList.contacts.size() - 1;
        //System.out.println("\nstart");
        if(!leaveRoom) contactList.removeGroup(group);
        Contact contact;
        for (int index = size; 0 <= index; --index) {
            contact = (Contact) contactList.contacts.elementAt(index);
            if (contact.group == group) {
                //System.out.println("==>" + contact);
                contact.setStatus(Presence.PRESENCE_OFFLINE);
                if(!leaveRoom){
                  contact.destroy();
                  contact.clearVCard();//..check contact active status
                  contactList.contacts.removeElementAt(index);
                  if(null != contact.group) contact.group = null;
                }
            }
        }
        contact = null;
        //System.out.println("end ");
        //systemGC();
        reEnumRoster();
    }
//#endif

    public void cleanupGroup() {
         Group g=(Group)getFocusedObject();
         if (g==null) return;
         if (!g.collapsed) return;
         Vector hContacts = g.getContacts();
 //#ifndef WMUC
         int hC;
         if (g instanceof ConferenceGroup) {
             ConferenceGroup cg = (ConferenceGroup) g;
             if (!cg.inRoom) {
                boolean removeGroup = true;
                hC = hContacts.size() - 1;
//#ifdef DEBUG_CONSOLE
//#                 midlet.BombusQD.debug.add("::cleanupGroup->cg.inRoom->" + cg.inRoom + "(destroyContacts)",10);
//#endif
                for (int index = hC; index >= 0; --index) {
                    Contact contact=(Contact)hContacts.elementAt(index);
                    if (0 == contact.getNewMessageCount()) {
                        contact.destroy();
                        contactList.removeContact(contact);
                        setModified();
                     } else {
                        removeGroup = false;
                     }
                 }
                if (removeGroup) {
                    //System.out.println("DestroyGrp: " + g.name);
                    contactList.removeGroup(g);
                    setModified();
                }
                return;
             }// else return;
         }
 //#endif
        hC = hContacts.size() - 1;
        for (int index = hC; index >= 0; --index) {
            Contact contact=(Contact)hContacts.elementAt(index);
            if ( contact.origin>Contact.ORIGIN_ROSTERRES
                    && (Presence.PRESENCE_OFFLINE <= contact.status)
                    && !contact.hasNewMsgs()
                    && contact.origin!=Contact.ORIGIN_GROUPCHAT) {
                contact.destroy();
                contactList.removeContact(contact);
                setModified();
             }
        }
        if (0 == g.getOnlines() 
                && !(g instanceof ConferenceGroup) 
                && g.type == Groups.TYPE_MUC) {
            contactList.removeGroup(g);
            setModified();
         }
         hContacts = null;
         g = null;
    }

    private ReEnumerator reEnumerator = new ReEnumerator();
    public void setModified() {
        reEnumerator.setModified();
    }
    public void reEnumRoster() {
         reEnumerator.queueEnum();
     }
    public Vector getHContacts() {return contactList.contacts;}



    boolean  processRoster(JabberDataBlock data, boolean getRoster){
        JabberDataBlock q=data.findNamespace("query", "jabber:iq:roster");
        if (q==null) return false;
        //verifying from attribute as in RFC3921/7.2
        String from=data.getAttribute("from");
        if (from!=null) {
            Jid fromJid=new Jid(from);
            if (fromJid.hasResource() && !myJid.equals(fromJid, true)) return false;
        }
        rosterVersion = q.getAttribute("ver");
        Vector cont=q.getChildBlocks();
        String group,bareJid;
        Vector transports = new Vector(0);
        try {
          if (cont!=null){
            int size=cont.size();
            int k;
            for(k=0; k<size; ++k){
               JabberDataBlock i=(JabberDataBlock)cont.elementAt(k);
                if (i.getTagName().equals("item")) {
                   group=i.getChildBlockText("group");
                   bareJid=i.getAttribute("jid");

                   if (group.length()==0) group=Groups.COMMON_GROUP;
                   updateContact(i.getAttribute("name"), bareJid, group, i.getAttribute("subscription"), (null != i.getAttribute("ask")));

                   if(-1 == bareJid.indexOf('@')) transports.addElement(bareJid);
                }
               i=null;
            }
          }
        } catch (Exception e) {
            errorLog("roster load error");
        }
        if(cont!=null){
          cont = new Vector(0);
          cont = null;
        }
        q=null;
        sortRoster(null);
        reEnumRoster();

        if(getRoster) connectTransport();
        return true;
    }

   public void connectTransport(){
        if(midlet.BombusQD.cf.isStatusFirst && firstStatus!=-1) {
           if (firstStatus==5) sendPresence(Presence.PRESENCE_INVISIBLE, null);
           else sendPresence(firstStatus, null);

           midlet.BombusQD.cf.isStatusFirst=false;
           firstStatus=-1;
         } else {
             if (midlet.BombusQD.cf.loginstatus==5) sendPresence(Presence.PRESENCE_INVISIBLE, null);
             else {
                sendPresence(midlet.BombusQD.cf.loginstatus, null);
             }
         }
   }


   private void sortContacts(Vector sortVector){
        try {
                if(sortVector == null) return;
                int f, i;
                IconTextElement left, right;
                int size = sortVector.size();
                for (f = 1; f < size; f++) {
                        left=(IconTextElement)sortVector.elementAt(f);
                        right=(IconTextElement)sortVector.elementAt(f-1);
                        if ( left.compare(right) >=0 ) continue;
                        i = f-1;
                        while (i>=0){
                           right=(IconTextElement)sortVector.elementAt(i);
                           if (right.compare(left) <0) break;
                           sortVector.setElementAt(right,i+1);
                           i--;
                        }
                        sortVector.setElementAt(left,i+1);
                }
            reEnumRoster();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private void sortGroup(Group g) {
        sortContacts(g.getContacts());
    }

    private void sortRoster(Contact c) {
        //System.out.println("           (" + contactList.contacts.size() + ") " + c + "SORT: " + contactList.contacts);
        if ((null != c) && (null != c.group)) {
            sortGroup(c.group);
            return;
        }
        Vector groups = contactList.groups.groups;
        int grpSize = groups.size();
        for (int i = 0; i < grpSize; ++i) {
           sortGroup((Group)groups.elementAt(i));
        }
    }

    private void updateContact(String nick, String jid, String grpName, String subscr, boolean ask) {
         // called only on roster read
        int status = Presence.PRESENCE_OFFLINE;
        if (subscr.equals("none")) status = Presence.PRESENCE_UNKNOWN;
        if (ask) status = Presence.PRESENCE_ASK;
        if (subscr.equals("remove")) status = -1;
        Jid J=new Jid(jid);
        Contact contact = contactList.findContact(contactList.contacts, J, false); // search by bare jid
        if (null == contact) {
            if (status < 0) return;
            contact = new Contact(nick, jid, Presence.PRESENCE_OFFLINE, null);
            addContact(contact);
            Contact c = contact;
            Group group = contactList.getGroup( c.jid.isTransport() ? SR.get(SR.MS_TRANSPORTS) : grpName);

            if (null == group) {
                group = new Group(grpName, Groups.TYPE_COMMON);
                contactList.addGroup(group);
            }

            if (c.origin == Contact.ORIGIN_PRESENCE) c.origin = Contact.ORIGIN_ROSTERRES;
            c.setNick(nick);
            c.setGroup(group);
            c.subscr=subscr;
            c.offline_type = status;
            c.ask_subscribe=ask;
            if (querysign && midlet.BombusQD.cf.collapsedGroups) contact.group.collapsed = true;
            return;
         }
        Vector hContacts = contactList.contacts;
        int size = hContacts.size() - 1;
        Contact c;
        for (int index = size; index >= 0; --index) {
            c = (Contact)hContacts.elementAt(index);
            if (c.jid.equals(J, false)) {
                if (status < 0) {
                    contactList.removeContact(c);
                    continue;
                }
                Group group = contactList.getGroup( c.jid.isTransport() ? SR.get(SR.MS_TRANSPORTS) : grpName);
                if (null == group) {
                    group = new Group(grpName, Groups.TYPE_COMMON);
                    contactList.addGroup(group);
                }
                if (c.origin == Contact.ORIGIN_PRESENCE || c.origin == Contact.ORIGIN_ROSTERRES) c.origin = Contact.ORIGIN_CLONE;
                c.setNick(nick);
                c.setGroup(group);
                c.subscr=subscr;
                c.offline_type = status;
                c.ask_subscribe=ask;
                if (querysign && midlet.BombusQD.cf.collapsedGroups) contact.group.collapsed = true;
                group = null;
             }
         }
        J = null;
        if (status < 0) return;
        if (contact.origin == Contact.ORIGIN_CLONE) contact.origin = Contact.ORIGIN_ROSTERRES;
     }


//#ifndef WMUC

    public ConferenceGroup initMuc(final String from, String joinPassword){
         // muc message
         int ri=from.indexOf('@');
         int rp=from.indexOf('/');
         String room=from.substring(0,ri);
         String roomJid=from.substring(0,rp).toLowerCase();
        ConferenceGroup grp = contactList.getConferenceGroup(roomJid);
        // creating room
        if (grp==null) {// we hasn't joined this room yet
            grp = new ConferenceGroup(roomJid, room);
            contactList.addGroup(grp);
         }
        grp.password = joinPassword;
        grp.inRoom = true;
        room = null;
        roomJid = null;
        MucContact mucContact = grp.getConfContact();
        addContact(mucContact);
        //mucContact.setBareJid(from);

         // change nick if already in room
        if (Presence.PRESENCE_OFFLINE > mucContact.status) return grp;
        mucContact.status = Presence.PRESENCE_ONLINE;
        mucContact.commonPresence = true;
        grp.conferenceJoinTime = Time.utcTimeMillis();
        addContact(grp.getSelfContact(from));
        grp.collapsed = true; //test
        sortRoster(mucContact);
         return grp;
     }

     private MucContact mucContact(String from){
        String roomJid = from.substring(0, from.indexOf('/')).toLowerCase();
        ConferenceGroup grp = contactList.getConferenceGroup(roomJid);
        if (null == grp) return null; // we are not joined this room

        MucContact c = grp.getContact(from);
        addContact(c);
        sortRoster(c);
        //MucContact c = grp.getContact(from);
        //sortRoster(c);
        grp = null;
        roomJid = null;
        return c;
     }
//#endif


     public Contact getContact(final String jid, boolean createInNIL) {
        Jid J = new Jid(jid);
        Contact c = findContact(J, true);
        if (null != c) return c;
         c = findContact(J, false);
         if (c==null) {
            if (!createInNIL) {
                J = null;
                return null;
            }

            c = new Contact(null, jid, Presence.PRESENCE_OFFLINE, "none" ); /*"not-in-list"*/
            c.origin=Contact.ORIGIN_PRESENCE;
            c.setGroup(contactList.groups.getGroup(Groups.TYPE_NOT_IN_LIST));
            addContact(c);
         } else {
            if (c.origin == Contact.ORIGIN_ROSTER) {
                c.origin = Contact.ORIGIN_ROSTERRES;
                c.setStatus(Presence.PRESENCE_OFFLINE);

                if(c.group.type == Groups.TYPE_SELF) {
                    c = new Contact(null, jid, Presence.PRESENCE_OFFLINE, null );
                    c.setGroup(contactList.groups.getGroup(Groups.TYPE_SELF));
                    c.origin = Contact.ORIGIN_CLONE;
                    addContact(c,true);
                } else {
                 c.jid = J;
                 c.setNick(c.getNick());
                }
            } else {
                c = clone(c, J, Presence.PRESENCE_OFFLINE);
                addContact(c);
            }
         }
         sortRoster(c);
         J = null;
         return c;
     }

     public Contact clone(Contact c, Jid newjid, final int status) {
         Contact clone = new Contact();
         clone.setGroup(c.group);
         clone.jid=newjid;
         clone.setNick(c.getNick());
         clone.key1=c.key1;
         clone.subscr=c.subscr;
         clone.offline_type=c.offline_type;
         clone.origin=Contact.ORIGIN_CLONE;
         clone.status=status;
         clone.transport=RosterIcons.getInstance().getTransportIndex(newjid.getTransport()); //<<<<
//#ifdef PEP
         clone.pepMood=c.pepMood;
         clone.pepMoodName=c.pepMoodName;
         clone.pepMoodText=c.pepMoodText;
//#ifdef PEP
         clone.pepTune=c.pepTune;
         clone.pepTuneText=c.pepTuneText;
         clone.activity=c.activity;
//#endif
//#endif
         clone.bareJid=c.bareJid;
         return clone;
      }

     public void addContact(Contact c, boolean ttt) {
        contactList.addContact(c, true);
        setModified();
     }
     public void addContact(Contact c) {
        contactList.addContact(c);
        setModified();
     }

     public Contact findContact(final Jid j, final boolean compareResources) {
        return contactList.findContact(contactList.contacts, j, compareResources);
     }

    int firstStatus = -1;

    public void sendPresence(int newStatus, String message) {
        myStatus=newStatus;
  if (message!=null) {
            myMessage=message;
        }

        setQuerySign(false);

        if (myStatus != Presence.PRESENCE_OFFLINE) {
             lastOnlineStatus=myStatus;
        }

        // reconnect if disconnected
        if (myStatus!=Presence.PRESENCE_OFFLINE && theStream==null ) {
            doReconnect=(contactList.contacts.size() > 1);
//#ifdef DEBUG_CONSOLE
//#             midlet.BombusQD.debug.add(" ::OFFLINE ==> doReconnect ==> " + doReconnect ,10);
//#endif
            firstStatus=newStatus;
            redraw();
            new Thread(this).start();
            return;
        }

        blockNotify(-111,13000);

        if (isLoggedIn()) {
//#ifdef DEBUG_CONSOLE
//#         if(midlet.BombusQD.cf.debug) midlet.BombusQD.debug.add("::sendPresence",10);
//#endif
            if (myStatus==Presence.PRESENCE_OFFLINE  && !midlet.BombusQD.cf.collapsedGroups)
                contactList.groups.queryGroupState(false);
            // send presence
            ExtendedStatus es = StatusList.getInstance().getStatus(myStatus);
            if (message==null){
                myMessage=StringUtils.toExtendedString(es.getMessage());
            }

            myMessage=StringUtils.toExtendedString(myMessage);
            int myPriority=es.getPriority();
            Presence presence = new Presence(myStatus, myPriority, myMessage, midlet.BombusQD.sd.account.getNick());
            if (!midlet.BombusQD.sd.account.isMucOnly() )
    theStream.send(presence);
                presence=null;
//#ifdef DEBUG_CONSOLE
//#             if(midlet.BombusQD.cf.debug) midlet.BombusQD.debug.add("::sendMultiPresence",10);
//#endif
//#ifndef WMUC
            multicastConferencePresence(myStatus, myMessage, myPriority);
//#endif
        }

        // disconnect
        if (myStatus==Presence.PRESENCE_OFFLINE) {
            try {
                theStream.close(); // sends </stream:stream> and closes socket
            } catch (Exception e) {
                //e.printStackTrace();
            }
            contactList.setOfflineStatus();
            theStream=null;
            System.gc();
//#ifdef AUTOSTATUS
            AutoStatus.getInstance().stop();
//#endif

//#ifdef DEBUG_CONSOLE
//#             midlet.BombusQD.debug.add(" ::OFFLINE ==> " + theStream ,10);
//#endif
        }
        Contact c=selfContact();
        if (c!=null) {
            c.setStatus(myStatus);
            sortRoster(c);
        }
        setModified();
        reEnumRoster();
    }

    public void sendDirectPresence(int status, String to, JabberDataBlock x) {
        if (to==null) {
            sendPresence(status, null);
            return;
        }
        ExtendedStatus es= StatusList.getInstance().getStatus(status);
        myMessage=es.getMessage();
        myMessage=StringUtils.toExtendedString(myMessage);
        Presence presence = new Presence(status, es.getPriority(), myMessage, midlet.BombusQD.sd.account.getNick());
        presence.setTo(to);
        if (x!=null) presence.addChild(x);
        if (theStream!=null) {
            theStream.send( presence );
            presence=null;
        }
    }

    public void sendDirectPresence(int status, Contact to, JabberDataBlock x) {
        sendDirectPresence(status, (to==null)? null: to.getJid(), x);
        if (to == null) return;
        if (to.jid.isTransport()) blockNotify(-111,10000);
//#ifndef WMUC
        if (to instanceof MucContact) ((MucContact)to).commonPresence=false;
//#endif
    }

    public boolean isLoggedIn() {
        if (theStream==null) return false;
        return theStream.loggedIn;
    }

    public Contact selfContact() {
  return contactList.getSelfContact();
    }


    public void leaveAllMUCs() {//Tishka17
         Contact c;
         Group grp;
         int size = contactList.contacts.size();
             for(int i = 0; i < size; ++i){
                c = (Contact) contactList.contacts.elementAt(i);
                if(c.group != null) {
                  grp = c.group;
                  if(grp instanceof ConferenceGroup){
                    ((ConferenceGroup)grp).leaveRoom();
                    i = 0;
                    size = contactList.contacts.size();
                  }
                }
           }
    }

    public void MUCsAutoJoin(String mess) {//Mars
        for (Enumeration e = midlet.BombusQD.sd.roster.bookmarks.elements(); e.hasMoreElements();) {
            BookmarkItem bm = (BookmarkItem)e.nextElement();
                if (bm.isAutoJoin()) {
                    ConferenceForm.join(bm.getDesc(), bm.getJidNick(), bm.getPassword(), midlet.BombusQD.cf.confMessageCount);
                }
        }
    }

//#ifndef WMUC
     public void multicastConferencePresence(int myStatus, String myMessage, int myPriority) {
        //if (!cf.autoJoinConferences) return; //requested to disable
        if (myStatus==Presence.PRESENCE_INVISIBLE) return; //block multicasting presence invisible

        Vector hContacts = contactList.contacts;
        for (int index = hContacts.size() - 1; index >= 0; --index) {
            Contact c = (Contact) hContacts.elementAt(index);
            if (Contact.ORIGIN_GROUPCHAT != c.origin) continue;
            if (!((MucContact)c).commonPresence) continue; // stop if room left manually
            ConferenceGroup confGroup=(ConferenceGroup)c.group;
            if (!confGroup.inRoom) continue; // don`t reenter to leaved rooms

            Contact myself=confGroup.selfContact;
            if (Presence.PRESENCE_OFFLINE <= c.status) {
                ConferenceForm.join(confGroup.desc, myself.getJid(), confGroup.password, 20);
                continue;
            }
            Presence presence = new Presence(myStatus, myPriority, myMessage, null);
            presence.setTo(myself.bareJid);
            theStream.send(presence);
        }
     }
 //#endif
    public void sendPresence(String to, String type, JabberDataBlock child, boolean conference) {
        JabberDataBlock presence=new Presence(to, type);

        if (child!=null) {
            presence.addChild(child);

            ExtendedStatus es= StatusList.getInstance().getStatus(myStatus);
            switch (myStatus){
                case Presence.PRESENCE_CHAT: presence.addChild("show", Presence.PRS_CHAT);break;
                case Presence.PRESENCE_AWAY: presence.addChild("show", Presence.PRS_AWAY);break;
                case Presence.PRESENCE_XA: presence.addChild("show", Presence.PRS_XA);break;
                case Presence.PRESENCE_DND: presence.addChild("show", Presence.PRS_DND);break;
            }
            if (es.getPriority()!=0)
                presence.addChild("priority",Integer.toString(es.getPriority()));
            if (es.getMessage()!=null)
                presence.addChild("status", StringUtils.toExtendedString(es.getMessage()));
        } else if (conference) {
            ExtendedStatus es= StatusList.getInstance().getStatus(Presence.PRESENCE_OFFLINE);
            if (es.getMessage()!=null)
                presence.addChild("status", StringUtils.toExtendedString(es.getMessage()));
                es=null;
        }

        theStream.send(presence);
        presence=null;
    }

    public void doSubscribe(Contact c) {
        if (c.subscr==null) return;
        boolean subscribe =
                c.subscr.startsWith("none") ||
                c.subscr.startsWith("from");
        if (c.ask_subscribe) subscribe=false;

        boolean subscribed =
                c.subscr.startsWith("none") ||
                c.subscr.startsWith("to");

        String to=(c.jid.isTransport())?c.getJid():c.bareJid;

        if (subscribed) sendPresence(to,"subscribed", null, false);
        if (subscribe) sendPresence(to,"subscribe", null, false);
    }

    public void sendMessage(Contact to, String id,String body,String subject, String composingState) {
//#ifdef AUTOSTATUS
        AutoStatus.getInstance().userActivity(Config.AWAY_MESSAGE);
//#endif

        try {
//#ifndef WMUC
            boolean groupchat=to.origin==Contact.ORIGIN_GROUPCHAT;
//#else
//#           boolean groupchat=false;
//#endif

//#ifdef JUICK.COM
             if(to.getJid().indexOf("juick@juick.com")>-1) { //Need fixes
                String chars = "0123456789";
                int charLen = chars.length();
                if(body.startsWith("#") && body.endsWith("+")){//include #+
                   int len = body.length()-1;
                   int newLen = 1;
                   int i = 0;
                   //проверка, что введет только номер +
                   while(i<len){ i++;
                      for(int j=0;j<charLen;j++){
                        if(body.charAt(i)==chars.charAt(j)) newLen++;
                      }
                   }
                   if(len==newLen){
                     String postNum = body.substring(1,len);
                     JabberDataBlock request = new Iq("juick@juick.com/Juick", Iq.TYPE_GET, (postNum.length()==0) ? "lastmsgs" :"cmts_"+postNum );
                     JabberDataBlock query = request.addChildNs("query","http://juick.com/query#messages");
                     query.setAttribute("mid", (body.equals("#+")) ? "" : postNum );
                     theStream.send(request);
                     playNotify(SOUND_OUTGOING);
                     return;
                   }
                }
             }
//#endif
            Message message = new Message(
                    to.getJid(),
                    body,
                    subject,
                    groupchat
            );

            message.setAttribute("id", id);

            if (groupchat && body==null && subject==null) return;

            if (composingState!=null)
                message.addChildNs(composingState, "http://jabber.org/protocol/chatstates");


            if (!groupchat)
                if (body!=null) if (midlet.BombusQD.cf.eventDelivery)
                    message.addChildNs("request", "urn:xmpp:receipts");

           theStream.send(message);

//#ifdef CLASSIC_CHAT
//#             if (body != null) {
//#                 if (Config.module_classicchat) {
//#                     if (!groupchat) {
//#                         //forfix
//#                         Msg mmm = new Msg(Msg.OUTGOING, "Me", null, body);
//#                         to.addMessage(mmm);
//#                         StringUtils.addClassicChatMsg(mmm.toString(), SimpleItemChat.getWidth(), to.scroller);
//#                     } else {
//#                         if (body.startsWith("/me")) {
//#                             StringUtils.addClassicChatMsg("***Me " + body.substring(3, body.length()), SimpleItemChat.getWidth(), to.scroller);
//#                         } else {
//#                             StringUtils.addClassicChatMsg("Me: " + body, SimpleItemChat.getWidth(), to.scroller);
//#                         }
//#                     }
//#                 }
//#             }
//#endif
        if (body!=null || subject!=null)
            playNotify(SOUND_OUTGOING);
        message=null;
        to=null;
        body=null;
        subject=null;

            lastMessageTime=Time.utcTimeMillis();

        }
        catch(OutOfMemoryError eom) { errorLog("error Roster::1");
        } catch (Exception e) {
            //e.printStackTrace()
        }
    }

    private void sendDeliveryMessage(Contact c, String id) {
        if (!midlet.BombusQD.cf.eventDelivery) return;
        if (myStatus==Presence.PRESENCE_INVISIBLE) return;
        Message message=new Message(c.jid.getJid());
        //xep-0184
        message.setAttribute("id", id);
        message.addChildNs("received", "urn:xmpp:receipts");
        theStream.send( message );
        message=null;
        c=null;
    }

    private Vector vCardQueue = new Vector(0);
    public void resolveNicknames(String transport){
        Vector contacts = contactList.contacts;
        for (int index = contacts.size() - 1; 0 <= index; --index) {
            Contact contact = (Contact) contacts.elementAt(index);
            if (contact.jid.isTransport()) continue;
            int grpType = contact.getGroupType();
            if (contact.jid.getServer().equals(transport) && contact.getNick()==null
                    && (grpType==Groups.TYPE_COMMON || grpType==Groups.TYPE_NO_GROUP)) {
                vCardQueue.addElement(VCard.getQueryVCard(contact.getJid(), "nickvc" + contact.bareJid));
            }
        }
        setQuerySign(true);
        sendVCardReq();
    }


    private void sendVCardReq() {
        if ((null != vCardQueue) && !vCardQueue.isEmpty()){
            JabberDataBlock req=(JabberDataBlock) vCardQueue.lastElement();
            vCardQueue.removeElement(req);
            //System.out.println(k.nick);
            theStream.send(req);
            querysign=true;
            req = null;
         }
         updateMainBar();
     }

    public void loginFailed(String error){
        myStatus=Presence.PRESENCE_OFFLINE;
        setProgress(SR.get(SR.MS_LOGIN_FAILED), 100);

        errorLog(error);

        try {
            theStream.close();
        } catch (Exception e) {
            //e.printStackTrace();
        }
        contactList.setOfflineStatus();//?
        theStream=null;

        systemGC();

        doReconnect=false;
        setQuerySign(false);
        redraw();
    }

//#if CHANGE_TRANSPORT
    public void contactChangeTransport(String srcTransport, String dstTransport){ //<voffk>
  setQuerySign(true);
        int size = contactList.contacts.size();
        Contact k;
        for(int i=0;i<size;++i){
            k =(Contact)contactList.contacts.elementAt(i);
      if (k.jid.isTransport()) continue;
            int grpType=k.getGroupType();
            if (k.jid.getServer().equals(srcTransport) &&
                    (grpType==Groups.TYPE_COMMON || grpType==Groups.TYPE_NO_GROUP ||
                    grpType==Groups.TYPE_VISIBLE || grpType==Groups.TYPE_IGNORE)) {
                String jid=k.getJid();
                jid=StringUtils.stringReplace(jid, srcTransport, dstTransport);
                storeContact(jid, k.getNick(), (!k.group.getName().equals(SR.get(SR.MS_GENERAL)))?(k.group.getName()):"", true); //new contact addition
                try {
                    Thread.sleep(300);
                } catch (Exception ex) { }
                deleteContact(k); //old contact deletion
      }
  }
  setQuerySign(false);
    }
//#endif

    public void loginSuccess() {
        theStream.resetBlockListners();

        theStream.addBlockListener(new IqVersionReply());
        theStream.addBlockListener(new IqVersionReply());
        theStream.addBlockListener(new IqPing());
        theStream.addBlockListener(new EntityCaps());
//#if ROSTERX        
        theStream.addBlockListener(rosterx);
//#endif
        theStream.startKeepAliveTask();

  theStream.loggedIn=true;
  currentReconnect=0;

        theStream.addBlockListener(new IqLast());
        theStream.addBlockListener(new IqTimeReply());
//#if SERVICE_DISCOVERY && ADHOC
        if (midlet.BombusQD.cf.adhoc) {
            theStream.addBlockListener(new IQCommands());
        }
//#endif

//#ifdef PEP
        if (midlet.BombusQD.cf.sndrcvmood || midlet.BombusQD.cf.rcvtune || midlet.BombusQD.cf.rcvactivity) {
            theStream.addBlockListener(new PepListener());
        }
//#endif
//#if SASL_XGOOGLETOKEN
        if (midlet.BombusQD.sd.account.isGmail()) {
            theStream.addBlockListener(new IqGmail());
        }
//#endif

//#ifdef FILE_IO
//#ifdef FILE_TRANSFER
        if (midlet.BombusQD.cf.fileTransfer) {
            TransferDispatcher.getInstance().addBlockListener();
        }
//#endif
//#endif

//#ifdef CAPTCHA
        theStream.addBlockListener(new Captcha());
//#endif

        playNotify(SOUND_CONNECTED);

        if (midlet.BombusQD.sd.account.isMucOnly()) {
            setProgress(SR.get(SR.MS_CONNECTED),100);
            try {
                setModified();
                reEnumRoster();
            } catch (Exception e) { }
            setQuerySign(false);
            doReconnect=false;

            //query bookmarks
            theStream.addBlockListener(new BookmarkQuery(BookmarkQuery.LOAD));
        } else if (!doReconnect || rosterVersion!=null) { //запрашваем ростер при первом коннекте или если есть поддержка версий
            JabberDataBlock qr=new IqQueryRoster(rosterVersion);
            setProgress(SR.get(SR.MS_ROSTER_REQUEST), 49);
            theStream.send( qr );
            qr=null;
        }
                
        if (doReconnect) {
            querysign=doReconnect=false;
            sendPresence(myStatus, null);
            return;
        }
//#ifdef AUTOSTATUS
        if (Config.module_autostatus && Config.autoAwayType != Config.AWAY_LOCK) {
            AutoStatus.getInstance().start();
        }
//#endif
    }

    public void bindResource(String myJid) {
        Contact self=selfContact();
        self.jid=this.myJid=new Jid(myJid);
    }

//#if FILE_IO && AVATARS
    public void cashePhoto(VCard vcard,Contact c){
       if(vcard.getPhoto()==null) return;
        StringBuffer nickDate=new StringBuffer(0);
        if (c instanceof MucContact){
            nickDate.append("muc_").append(c.getNick());
        }else{
            nickDate.append("roster_").append(c.bareJid);
        }
       String filename = StringUtils.replaceBadChars(nickDate.toString());
       nickDate=null;
       OutputStream os;
        try {
            FileIO file=FileIO.createConnection(midlet.BombusQD.cf.msgAvatarPath+filename+vcard.getFileType());
            os = file.openOutputStream(0);
            os.write(vcard.getPhoto());
            os.close();
            os.flush();
            file.close();
            file=null;
        } catch (IOException ex) { }
    }

    private synchronized void loadAvatar(String from, boolean mucContactItem) {
        /*
         1.check avaliable image formats
         2.resize loaded image
         3.apply image to selected contact
         */
        Contact c = getContact(from, true);
        if(c.hasPhoto) return;

        Image photoImg;
        FileIO f;
        String filename;
        StringBuffer buffer;
        byte[] b;
        int len = -1;


        buffer = new StringBuffer(0);
        if (mucContactItem) {
            buffer.append("muc_");
            buffer.append(c.getNick());
        } else {
            buffer.append("roster_");
            buffer.append(c.bareJid);
        }
        filename = StringUtils.replaceBadChars(buffer.toString());
        try {
            f = FileIO.createConnection(midlet.BombusQD.cf.msgAvatarPath + filename + ".jpg");
            b = f.fileRead();
            len = b.length;
            //errorLog("AVATAR " + filename + ".jpg MUC:" + mucContactItem); //send to self-contact
        } catch (Exception ex) {
            try {
                 f = FileIO.createConnection(midlet.BombusQD.cf.msgAvatarPath + filename + ".png");
                 b = f.fileRead();
                 len = b.length;
                 //errorLog("AVATAR " + filename + ".png MUC:" + mucContactItem);
            } catch (Exception expng) {
               try {
                 f = FileIO.createConnection(midlet.BombusQD.cf.msgAvatarPath + filename + ".gif");
                 b = f.fileRead();
                 len = b.length;
                 //errorLog("AVATAR " + filename + ".gif MUC:" + mucContactItem);
               } catch (Exception exgif) { f = null; }
            }
        }

        if(null == f) return;
          try {
              b = f.fileRead();
              len = b.length;
              photoImg=Image.createImage(b, 0, len);
              c.setImageAvatar(photoImg);
              f.close();
              f = null;
          } catch(OutOfMemoryError eom) {
              errorLog("AVATAR OutOfMemoryError " + filename);
          } catch (Exception e) {
              errorLog("AVATAR Exception " + filename);
          }

        photoImg = null;
        buffer = new StringBuffer(0);
    }
//#endif

   public int blockArrived( JabberDataBlock data ) { //fix
        try {
            String from = data.getAttribute("from");
            String type = data.getTypeAttribute();
            String id = data.getAttribute("id");

            if( data instanceof Iq ) {
//#ifdef JUICK.COM
                if(from!=null){
                  if(from.indexOf("juick@juick.com")>-1) {
                    Msg m=new Msg(Msg.JUICK, "juick@juick.com/Juick", null, null);
                    m = juick.getMsg(m,data);
                  }
                }
//#endif
                if (id!=null) {
                    if (id.startsWith("nickvc")) {
                        if (type.equals("get") || type.equals("set")) return JabberBlockListener.BLOCK_REJECTED;
                        String matchedjid = id.substring(6, id.length());
                        if (!(from.equals(matchedjid) || from.equals(new Jid(matchedjid).getBareJid())))
                            return JabberBlockListener.BLOCK_REJECTED;

                        VCard vc=new VCard(data);//.getNickName();
                        String nick=vc.getNickName();

                        Contact c=findContact(new Jid(from), false);

                        String group=(c.getGroupType()==Groups.TYPE_NO_GROUP)? null: c.group.name;
                        if (nick!=null)  storeContact(from,nick,group, false);
                        sendVCardReq();
                        vc=null;
                        return JabberBlockListener.BLOCK_PROCESSED;
                    }

                     if (id.startsWith("getvc")) {
                        if (type.equals("get") || type.equals("set") || type.equals("error") ) return JabberBlockListener.BLOCK_REJECTED;

                        setQuerySign(false);
                        VCard vcard=new VCard(data);
                        String jid=id.substring(5);
                        Contact c=null;

                        if(c==null) c=getContact(jid, false); // drop unwanted vcards
      if (c instanceof MucContact) {
        MucContact mucContact=(MucContact)c;
        String realjid=mucContact.realJid;
        mucContact=null;
        if (realjid==null)
          if (!(from.equals(jid) || from.equals(new Jid(jid).getBareJid())))
            return JabberBlockListener.BLOCK_REJECTED;
        else
          if (!(from.equals(jid)
               || from.equals(new Jid(jid).getBareJid())
               || from.equals(realjid)))
            return JabberBlockListener.BLOCK_REJECTED;
        realjid=null;
      } else {
        if (!(from.equals(jid) || from.equals(new Jid(jid).getBareJid())) )
          return JabberBlockListener.BLOCK_REJECTED;
      }

//#if FILE_IO && AVATARS
                                if(midlet.BombusQD.cf.autoSaveVcard) {//check img in fs?
                                    cashePhoto(vcard,c);
                                }
//#endif
                         if (c != null) {
                             c.vcard = vcard;
                             if (BombusQD.sd.canvas.isShown()) {
                                 if (c.getGroupType() == Groups.TYPE_SELF) {
                                     new VCardEdit(vcard).show();
                                 } else {
                                     new VCardView(c).show();
                                 }
                             }
                         } else {
                             new VCardView(vcard).show();
                         }
                         vcard = null;
                         return JabberBlockListener.BLOCK_PROCESSED;
                    }

//#ifdef AVATARS
                    if (id.startsWith("avcard_get")) {
                        Thread.sleep(100);
      String matchedjid = id.substring(10, id.length());

      if (!from.equals(new Jid(matchedjid).getBareJid())) {
                            return JabberBlockListener.BLOCK_REJECTED;
                        }
                        VCard vc=new VCard(data);
                        try {
                            int length=vc.getPhoto().length;
                            if (length==1) {
                                vc.setPhoto(null);
                            } else {
                                Contact c = getContact(matchedjid, true);
                                Image photoImg = Image.createImage(vc.getPhoto(), 0, length);
//#if FILE_IO
                                if(midlet.BombusQD.cf.autoSaveVcard) {
                                    cashePhoto(vc, c);
                                }
//#endif
                                c.hasPhoto = true;
                                c.setImageAvatar(photoImg);
                                vc.hasPhoto = true;
                                c.vcard = vc;
                            }
                        } catch(OutOfMemoryError eom){
//#ifdef DEBUG
//#                               System.out.println("OutOfMemoryError onload " + vc.getJid());
//#endif
                        }  catch (Exception e) {
                            //e.printStackTrace();
                        }

                        return JabberBlockListener.BLOCK_PROCESSED;
                    }
//#endif
                 }

                if ( type.equals( "result" ) ) {
                    //fix
                    if(id.equals("delacc")) {
                        JabberDataBlock reg=data.findNamespace("query","jabber:iq:register");
                        JabberDataBlock remove=reg.getChildBlock("remove");
                        if(remove!=null){
                            new AccountRemoveForm().show();
                            return JabberBlockListener.BLOCK_PROCESSED;
                        }
                    }

                    if (id.startsWith("getnotes")) {
                        //XEP-0145: Annotations
                        if (midlet.BombusQD.cf.networkAnnotation) {
                            JabberDataBlock notes = data.findNamespace("query", "jabber:iq:private").findNamespace("storage", "storage:rosternotes");
                            Vector childBlocks = notes.getChildBlocks();
                            if (null != childBlocks) {
                                int size = notes.getChildBlocks().size();
                                int hCsize = contactList.contacts.size();
                                Contact find;
                                for (int i = 0; i < size; i++) {
                                    JabberDataBlock note = (JabberDataBlock)childBlocks.elementAt(i);
                                    for (int j = 0; j < hCsize; j++) {
                                        find = (Contact)contactList.contacts.elementAt(j);
                                        if (find.bareJid.indexOf(note.getAttribute("jid")) > -1) {
                                            find.annotations = note.getText();
                                        }
                                    }
                                }
                            }
                        }
                        return JabberBlockListener.BLOCK_PROCESSED;
                    }

                    if(id.equals("changemypass")) {
                         JabberDataBlock reg=data.findNamespace("query","jabber:iq:register");
                         ChangePasswordForm form = new ChangePasswordForm(reg.getChildBlockText("password"));
                         form.setParentView(this);
                         form.show();
                         return JabberBlockListener.BLOCK_PROCESSED;
                    }

//#ifdef POPUPS
                    if(id.equals("destroyroom"))   {
                         setWobble(1,null,from + " deleted!");
                         redraw();
                         return JabberBlockListener.BLOCK_PROCESSED;
                    }
//#endif


                    if (id.startsWith("getst")) {
                           JabberDataBlock query = data.findNamespace("query","http://jabber.org/protocol/stats");
                           JabberDataBlock req=new Iq(from, Iq.TYPE_GET,"statistic");
                           JabberDataBlock qry = req.addChildNs("query","http://jabber.org/protocol/stats");
                            try {
                                  for (Enumeration e=query.getChildBlocks().elements(); e.hasMoreElements(); ){
                                    JabberDataBlock stat = (JabberDataBlock)e.nextElement();
                                    if(stat.getAttribute("name")!=null){
                                       JabberDataBlock stats=qry.addChild("stat",null);
                                       stats.setAttribute("name",stat.getAttribute("name"));
                                   }
                                  }
                           } catch (Exception e) {  }
                           theStream.send(req);
                           query=null;
                           qry=null;
                           req=null;
                           return JabberBlockListener.BLOCK_PROCESSED;
                    }


                    if (id.startsWith("statistic")) {
                        JabberDataBlock query = data.findNamespace("query", "http://jabber.org/protocol/stats");
                        if (query != null) {
                            Vector stats = new Vector();
                            Vector children = query.getChildBlocks();
                            if (children != null) {
                                for (int i = 0; i < children.size(); ++i) {
                                    JabberDataBlock block = (JabberDataBlock)children.elementAt(i);
                                    stats.addElement(new String[]{block.getAttribute("name"), block.getAttribute("value")});
                                }
                                new ServerStatsForm(from, stats).show();
                                children = null;
                            }
                            query = null;
                            return JabberBlockListener.BLOCK_PROCESSED;
                        }
                    }

                    if (id.equals("getros")){
      if (from != null) {
        if (!from.equals(midlet.BombusQD.sd.account.getBareJid()))
          return JabberBlockListener.BLOCK_REJECTED;
      }
                        processRoster(data, true);

                        if(!midlet.BombusQD.cf.collapsedGroups)
                            contactList.groups.queryGroupState(true);

                        setProgress(SR.get(SR.MS_CONNECTED),100);
                        setModified();
                        reEnumRoster();
                        querysign=doReconnect=false;

//#ifndef WMUC
                        //query bookmarks
                        if (bookmarks==null) {
                            theStream.addBlockListener(new BookmarkQuery(BookmarkQuery.LOAD));
                        }
//#endif
                        if (midlet.BombusQD.cf.networkAnnotation) {
                            JabberDataBlock query = new Iq(null, Iq.TYPE_GET, "getnotes");
                            query.addChildNs("query", "jabber:iq:private").addChildNs("storage", "storage:rosternotes");
                            theStream.send(query);
                        }

                        return JabberBlockListener.BLOCK_PROCESSED;
                    }
                } else if (type.equals("set")) {
                    if (processRoster(data, false)) {
                        theStream.send(new Iq(from, Iq.TYPE_RESULT, id));
                        setModified();
                        reEnumRoster();
                        return JabberBlockListener.BLOCK_PROCESSED;
                    }
                }
            } else if( data instanceof Message ) { // If we've received a message
                querysign=false;
                Message message = (Message) data;

//#ifdef CONSOLE
//#                 //midlet.BombusQD.debug.add("::MESSAGE "+data.toString(),10);
//#endif

                if (myJid.equals(new Jid(from), false)) //Enable forwarding only from self-jids
                    from=message.getXFrom();

                int start_me=-1;
                String name=null;
                boolean groupchat=false;
                if (type != null) groupchat = type.equals("groupchat");

                if (groupchat) {
                    start_me=0;
                    int rp=from.indexOf('/');
                    name=from.substring(rp+1);
                    if (rp>0) from=from.substring(0, rp);
                }

                Contact c = getContact(from, (midlet.BombusQD.cf.notInListDropLevel != DROP_MESSAGES_PRESENCES || groupchat));
                //not-in-list message dropped
                if (c == null) {
                    return JabberBlockListener.BLOCK_REJECTED;
                }

                boolean highlite=false;

                String body=message.getBody().trim();
                String oob=message.getOOB();

                if (oob!=null) body+=oob;
                if (body.length()==0) body=null;

                String subj=message.getSubject().trim();
                if (subj.length()==0) subj=null;


                long tStamp=message.getMessageTime();

    byte mType=Msg.INCOMING;

                if (groupchat) {
                    if (subj!=null) { // subject
                        if (body==null) body=name+" "+SR.get(SR.MS_HAS_SET_TOPIC_TO)+": "+subj;
                        if (!subj.equals(c.getStatus())) {
                            c.setStatus(subj); // adding secondLine to conference
                            //highlite=true;
                        } else {
                            return JabberBlockListener.BLOCK_PROCESSED;
                        }
                        subj=null;
                        start_me=-1;
                        mType=Msg.SUBJECT;
                    }
                } else if (type!=null){
                    if (type.equals("error")) {
                        body=SR.get(SR.MS_ERROR_) + XmppError.findInStanza(message).toString();
                    } else if (type.equals("headline")) {
                        mType=Msg.HEADLINE;
                    }
                } else {
                    type="chat";
                }
//#ifndef WMUC
                try {
                    JabberDataBlock xmlns=message.findNamespace("x", "http://jabber.org/protocol/muc#user");
                    if (xmlns!=null) {
                        JabberDataBlock invite=xmlns.getChildBlock("invite");
                        if (invite !=null) {
                            if (message.getTypeAttribute().equals("error")) {
                                body=XmppError.decodeStanzaError(message).toString(); /*"error: invites are forbidden"*/
                            } else {
                                String room=from+'/'+midlet.BombusQD.sd.account.getNickName();
                                String password=xmlns.getChildBlockText("password");

                                ConferenceGroup invConf=initMuc(room, password);


                                invConf.confContact.commonPresence=false;


                                if (invConf.selfContact.status==Presence.PRESENCE_OFFLINE)
                                    invConf.confContact.status=Presence.PRESENCE_OFFLINE;


                                String inviteReason=invite.getChildBlockText("reason");
                                if (inviteReason!=null)
                                    inviteReason=(inviteReason.length()>0)?" ("+inviteReason+")":"";

                                body=invite.getAttribute("from")+SR.get(SR.MS_IS_INVITING_YOU)+from+inviteReason;

                                reEnumRoster();
                                xmlns=null;
                            }
                         }
                    }
                } catch (OutOfMemoryError eom) {
                    errorLog("error Roster::2");
                } catch (Exception e) {
                    //e.printStackTrace();
                }
//#endif
                if (name==null) name=c.getName();
                // /me
                if (body!=null) {
                    if (body.startsWith("/me ")) start_me=3;
                    if (start_me>=0 && groupchat) {
                        StringBuffer b=new StringBuffer(0);
//#if NICK_COLORS
//#ifdef CLASSIC_CHAT 
//#                         if(Config.module_classicchat) {
//#                             b.append(name.trim());
//#                         } else
//#endif
                        {
                            b.append("<nick>");
                            b.append(name);
                            b.append("</nick>");
                        }
//#endif
                        if (start_me==0){
                            b.append(": ");
                        } else b.insert(0,'*');
                        b.append(body.substring(start_me));
                        body=b.toString();
                        b=null;
                    }
//#ifdef LIGHT_CONTROL
                     if (type.equals("chat")) CustomLight.message();
//#endif
                }
    if (message.findNamespace("attention", "urn:xmpp:attention:0")!=null && AlertCustomize.getInstance().enableAttention) {
      //#ifdef LIGHT_CONTROL
      CustomLight.startBlinking();
      //#endif
      if (body==null || body.length()==0)
                body=SR.get(SR.LA_ATTENTION)+SR.get(SR.LA_WAKEUP);
//#ifdef POPUPS
      setWobbler(3, c, c.getName() + "\n" +body, null);
//#endif
      playNotify(SOUND_ATTENTION);
    }

                if (type.equals("chat") && myStatus!=Presence.PRESENCE_INVISIBLE) {
                    if (message.findNamespace("request", "urn:xmpp:receipts")!=null) {
                        sendDeliveryMessage(c, id);
                    }
                    if (message.findNamespace("received", "urn:xmpp:receipts")!=null) {
                         c.markDelivered(id);
                    }
                    if (message.findNamespace("active", "http://jabber.org/protocol/chatstates")!=null) {
                        c.acceptComposing=true;
                        c.showComposing=false;
//#ifdef RUNNING_MESSAGE
                        setTicker(c, "");
//#endif
                    }
                    if (message.findNamespace("paused", "http://jabber.org/protocol/chatstates")!=null) {
                        c.acceptComposing=true;
                        c.showComposing=false;
//#ifdef RUNNING_MESSAGE
                        setTicker(c, "");
//#endif
                    }
                    if (message.findNamespace("composing", "http://jabber.org/protocol/chatstates")!=null) {
                        playNotify(SOUND_COMPOSING);
                        c.acceptComposing=true;
                        c.showComposing=true;
//#ifdef RUNNING_MESSAGE
                        setTicker(c, SR.get(SR.MS_COMPOSING_NOTIFY));
//#endif
                    }
                }
                redraw();

                if (body==null) return JabberBlockListener.BLOCK_REJECTED;

                Msg m=new Msg(mType, from, subj, body);
                m.setMucChat(groupchat);

//#ifdef JUICK.COM
                if(from.indexOf("juick@juick.com")>-1 || data.findNamespace("juick", JuickModule.NS_MESSAGE)!=null) {
                    m = juick.getMsg(m,data);
                    if(m==null) {
                       m = new Msg(mType, from.trim(), subj, body);
                    } else {
                       c = getContact(m.getFrom(), (midlet.BombusQD.cf.notInListDropLevel!=DROP_MESSAGES_PRESENCES));
                    }
                }
//#endif
                if (tStamp!=0) m.dateGmt=tStamp;
//#ifndef WMUC
                if (m.getBody().indexOf(SR.get(SR.MS_IS_INVITING_YOU))>-1) m.dateGmt=0;
                if (groupchat) {
                    ConferenceGroup mucGrp=(ConferenceGroup)c.group;
                    if (mucGrp.selfContact.getJid().equals(message.getFrom())) {
                        m.setType(Msg.OUTGOING);
                        m.read();
                    } else {
//#ifdef LIGHT_CONTROL
                        CustomLight.message();
//#endif
                        if (m.dateGmt<= ((ConferenceGroup)c.group).conferenceJoinTime) {
                            if (m.getType() != Msg.SUBJECT) {
                                m.setType(Msg.HISTORY);
                            }
                        }
                        // highliting messages with myNick substring
                        String myNick=mucGrp.selfContact.getNick();
                        String _myNick=" "+myNick;
                        if (body.indexOf(myNick)>-1) {
                            if (body.indexOf(": all:")>-1)
                                highlite=true;
                            else if (body.indexOf("> "+myNick+": ")>-1)
                                highlite=true;
                            else if (body.indexOf(_myNick+",")>-1)
                                highlite=true;
                            else if (body.indexOf(": "+myNick+": ")>-1)
                                highlite=true;
                            else if (body.indexOf(_myNick+" ")>-1)
                                highlite=true;
                            else if (body.indexOf(", "+myNick)>-1)
                                highlite=true;
                            else if (body.endsWith(_myNick))
                                highlite=true;
                            else if (body.indexOf(_myNick+"?")>-1)
                                highlite=true;
                            else if (body.indexOf(_myNick+"!")>-1)
                                highlite=true;
                            else if (body.indexOf(_myNick+".")>-1)
                                highlite=true;
                        }
                        if (highlite) {
                            m.highlite();
                        }
                    }
                    m.setFrom(name);
                    mucGrp=null;
                }
//#endif
                messageStore(c, m);
                from=null;type=null;
                c=null;
                m=null;
                message=null;
                name=null;
                c=null;
                body=null;oob=null;
                m=null;
                  return JabberBlockListener.BLOCK_PROCESSED;
            } else if( data instanceof Presence ) {  // If we've received a presence
                if (myStatus==Presence.PRESENCE_OFFLINE)
                    return JabberBlockListener.BLOCK_REJECTED;

                Presence pr = (Presence) data;

                String Prtext = pr.getPresenceText();
                byte ti = pr.getTypeIndex();
//#ifdef CONSOLE
//#                 //midlet.BombusQD.debug.add("::PRESENCE "+data.toString(),10);
//#endif

//#ifdef AVATARS
                if (ti != Presence.PRESENCE_OFFLINE) {
                    if (Config.module_avatars && Config.auto_queryPhoto) {
                        Contact c = getContact(from, true);
                        if (c.hasPhoto == false && c.img_vcard == null) {
                            JabberDataBlock req = new Iq(c.bareJid, Iq.TYPE_GET, "avcard_get" + c.getJid());
                            req.addChildNs("vCard", "vcard-temp");
                            theStream.send(req);
                        }
                    }
                }
//#endif
//#ifndef WMUC
            JabberDataBlock xmuc=pr.findNamespace("x", "http://jabber.org/protocol/muc#user");
            if (xmuc==null) xmuc=pr.findNamespace("x", "http://jabber.org/protocol/muc"); //join errors

            int priority = pr.getPriority();

            if (xmuc!=null) {//MUC only
//#ifdef SERVICE_DISCOVERY
                    JabberDataBlock status=xmuc.getChildBlock("status");
                    if(status!=null) {
                       int index = from.indexOf('/');
                       int statusCode=Integer.parseInt( status.getAttribute("code") );
                       if(statusCode==201) {
                           new QueryConfigForm(from.substring(0,index));
                       }
                    }
//#endif

                    MucContact conferenceContact = null;
                    Contact room = null;

                    int rp=from.indexOf('/');
                    if(rp!=-1) room = getContact(from.substring(0, rp), false);

                    try {
                        conferenceContact = mucContact(from);
                        ConferenceGroup cGroup = (ConferenceGroup)conferenceContact.group;
                        if(conferenceContact == cGroup.selfContact){
                           String statusMsg = data.getChildBlockText("status");
                           if(null != statusMsg) {
                              if(statusMsg.indexOf("Replaced by new connection")>-1){ //"Replaced by new connection"
                                return JabberBlockListener.BLOCK_REJECTED;
                              }
                           }
                           statusMsg = null;
                        }
                        cGroup = null;

///* crashes after roster get
                        //if (room==null) return JabberBlockListener.BLOCK_REJECTED;
                        if (pr.getAttribute("ver")!=null) conferenceContact.version=pr.getAttribute("ver"); // for bombusmod only
//#ifdef CLIENTS_ICONS
                        if (pr.hasEntityCaps()) {
                            if (pr.getTypeIndex() != Presence.PRESENCE_ERROR) {
                                if (pr.getEntityNode() != null) {
                                    ClientsIconsData.processData(conferenceContact, pr.getEntityNode());
                                }
                                if (pr.getEntityVer() != null) {
                                    conferenceContact.version = pr.getEntityVer();
                                }
                            }
                        }
//#endif
                        String lang=pr.getAttribute("xml:lang");
                        if (lang!=null) conferenceContact.lang=lang;
                        lang=null;
                        conferenceContact.setStatus(pr.getStatus());
                        String chatPres = processPresence(conferenceContact,  xmuc, pr , Prtext);

                        String name=from.substring(rp+1);
                        boolean addPresenceMsg = midlet.BombusQD.cf.storeConfPresence;
                        Msg conferenceMessage;
                        if(chatPres.indexOf(SR.get(SR.MS_WAS_BANNED))>-1 || chatPres.indexOf(SR.get(SR.MS_WAS_KICKED))>-1
                                || chatPres.indexOf(SR.get(SR.MS_NEW_ROOM_CREATED))>-1 ) {
                                conferenceMessage = new Msg(Msg.ERROR, name, null, chatPres );
                                addPresenceMsg = true;
                         } else conferenceMessage = new Msg(Msg.PRESENCE, name, null, chatPres );

                        if(addPresenceMsg) {
                            conferenceMessage.setColor(conferenceContact.getMainColor());
                            messageStore(room, conferenceMessage);
                        }

                        conferenceMessage = new Msg( (ti==Presence.PRESENCE_AUTH ||
                              ti==Presence.PRESENCE_AUTH_ASK)?Msg.AUTH : Msg.PRESENCE, from, null, Prtext );

                        if (ti==Presence.PRESENCE_ERROR) {
                           StringBuffer sb = new StringBuffer(0);
                             sb.append(SR.get(SR.MS_ERROR_))
                               .append("->")
                               .append(XmppError.findInStanza(pr).toString());
                             conferenceMessage = new Msg(Msg.ERROR, name, null, sb.toString());
                             if(!chatPres.startsWith("remote-server-not-found")) messageStore(room, conferenceMessage);
                           sb = new StringBuffer(0);
                           sb=null;
                        } else {
                           messageStore(conferenceContact, conferenceMessage);
                        }
                        if (ti==Presence.PRESENCE_OFFLINE)  {
                            //автоочистка вышедших из конфы
                            if (!conferenceContact.active() && Config.cleanConfContacts) {
                                conferenceContact.destroy();
                                contactList.removeContact(conferenceContact);
                                setModified();
                            } else {
                                conferenceContact.setIncoming(INC_NONE);
                                conferenceContact.showComposing=false;
//#ifdef CLIENTS_ICONS
                                conferenceContact.client=-1;
                                conferenceContact.clientName="-";
//#endif
                                conferenceContact.version="";
                           }
                        }
                        name=null;
                        lang=null;
                        conferenceContact.priority=priority;

                        sortRoster(conferenceContact);
                        conferenceMessage=null;
                        conferenceContact=null;
                        chatPres=null;

//#if FILE_IO && AVATARS
                        if(Config.module_avatars && Config.autoload_FSPhoto) {
                            loadAvatar(from, true);
                        }
//#endif
                    }
                    catch(OutOfMemoryError eom){ errorLog("error Roster::3"); } catch (Exception e) {
                        if(null != conferenceContact) {
                            contactList.removeContact(conferenceContact);
                            conferenceContact.destroy();
                        }
                    }
                    if(null != conferenceContact) conferenceContact = null;
                    //if(null != room) room = null;
                } else {
//#endif
//#if FILE_IO && AVATARS
                    if(Config.module_avatars && Config.autoload_FSPhoto) {
                        loadAvatar(from, false);
                    }
//#endif

                    Contact c=null;
                    //System.out.println("FROM:"+from);
                    Msg m=new Msg( (ti==Presence.PRESENCE_AUTH ||
                         ti==Presence.PRESENCE_AUTH_ASK)?Msg.AUTH : Msg.PRESENCE, from, null, Prtext );
                     if (ti==Presence.PRESENCE_AUTH_ASK) {
                        //processing subscriptions
                        if (midlet.BombusQD.cf.autoSubscribe==Config.SUBSCR_DROP)
                            return JabberBlockListener.BLOCK_REJECTED;
                        if (midlet.BombusQD.cf.autoSubscribe==Config.SUBSCR_REJECT) {
                            sendPresence(from, "unsubscribed", null, false);
                            return JabberBlockListener.BLOCK_PROCESSED;
                        }

                        c=getContact(from, true);
                        messageStore(c, m);
                        sortRoster(c);
                        m=null;
                        if (midlet.BombusQD.cf.autoSubscribe==Config.SUBSCR_AUTO) {
                             doSubscribe(c);
                             messageStore(c, new Msg(Msg.AUTH, from, null, SR.get(SR.MS_AUTH_AUTO)));
                         }
                    } else {
                        // processing presences
                        boolean enNIL= midlet.BombusQD.cf.notInListDropLevel > DROP_PRESENCES;

                        c=getContact(from, enNIL);
                        if (c==null) return JabberBlockListener.BLOCK_REJECTED; //drop not-in-list presence
                        if (pr.getAttribute("ver")!=null) c.version=pr.getAttribute("ver");  // for bombusmod only
                        if (pr.getTypeIndex()!=Presence.PRESENCE_ERROR) {
//#ifdef CLIENTS_ICONS
//#ifdef PLUGINS
//#                             if (midlet.BombusQD.cf.showClientIcon)
//#endif
                                if (pr.hasEntityCaps()) {
                                    if (pr.getEntityNode() != null) {
                                        ClientsIconsData.processData(c, pr.getEntityNode());
                                        if (pr.getEntityVer() != null) {
                                            c.version = pr.getEntityVer();
                                        }
                                    }
                                } else if (c.jid.hasResource()) {
                                    ClientsIconsData.processData(c, c.getResource().substring(1));
                                }
//#endif
                            JabberDataBlock j2j=pr.findNamespace("x", "j2j:history");
                            if (j2j!=null) {
                                if (j2j.getChildBlock("jid")!=null)
                                    c.j2j=j2j.getChildBlock("jid").getAttribute("gateway");
                            }
                            j2j=null;
                            String lang=pr.getAttribute("xml:lang");
                            c.lang=lang; lang=null;
                            c.setStatus(pr.getStatus());
                        }
                        if (ti>=0) c.setStatus(ti);
                        messageStore(c, m);
                        //sortContacts(c);
                        sortRoster(c);
                        m=null;
                     }
                    c.priority=priority;
                    if (ti>=0) c.setStatus(ti);
                    if (c.getNick()==null && c.status<=Presence.PRESENCE_DND) {
                        JabberDataBlock nick = pr.findNamespace("nick", "http://jabber.org/protocol/nick");
                        if (nick!=null) c.setNick(nick.getText()); nick=null;

                    }
                    if ((ti==Presence.PRESENCE_ONLINE || ti==Presence.PRESENCE_CHAT) && notifyReady(-111)) {
//#if USE_ROTATOR
                        if (midlet.BombusQD.cf.notifyBlink) {
                            c.setNewContact();
                        }
//#endif
                        if (midlet.BombusQD.cf.notifyPicture) {
                            if (c.getGroupType()!=Groups.TYPE_TRANSP)
                                c.setIncoming(INC_APPEARING);
                        }
                    }
                    if (ti==Presence.PRESENCE_OFFLINE)  {
                        c.setIncoming(INC_NONE);
                        c.showComposing=false;
//#ifdef PEP
                        //c.pepTune=false;
                        //c.pepMood=-1;
                        //c.pepTuneText="-";
                        //c.pepMoodName="-";
                        //c.pepMoodText="-";
                        //c.activity="";
//#endif
//#ifdef CLIENTS_ICONS
                        c.client=-1;
                        c.clientName="-";
//#endif
                        c.version="";
                    }
                    if (ti>=0) {
//#ifdef RUNNING_MESSAGE
                        if (ti==Presence.PRESENCE_OFFLINE)
                            setTicker(c, SR.getPresence(Presence.PRS_OFFLINE));
                        else if (ti==Presence.PRESENCE_ONLINE)
                            setTicker(c, SR.getPresence(Presence.PRS_ONLINE));
//#endif
                        if ((ti==Presence.PRESENCE_ONLINE || ti==Presence.PRESENCE_CHAT
                                || ti==Presence.PRESENCE_OFFLINE) && (c.getGroupType()!=Groups.TYPE_TRANSP) && (c.getGroupType()!=Groups.TYPE_IGNORE))
                            playNotify(ti);
                    }
                    c=null;
//#ifndef WMUC
                }
//#endif
                pr=null;
                from=null;
                 //sortRoster(c);
                reEnumRoster();
                return JabberBlockListener.BLOCK_PROCESSED;
            }
        } catch( Exception e ) {
            //e.printStackTrace();
        }
        return JabberBlockListener.BLOCK_REJECTED;
    }

//#ifndef WMUC
     private static String getAffiliationLocale(int aff) {
         switch (aff) {
             case MucContact.AFFILIATION_NONE: return SR.get(SR.MS_AFFILIATION_NONE);
             case MucContact.AFFILIATION_MEMBER: return SR.get(SR.MS_AFFILIATION_MEMBER);
             case MucContact.AFFILIATION_ADMIN: return SR.get(SR.MS_AFFILIATION_ADMIN);
             case MucContact.AFFILIATION_OWNER: return SR.get(SR.MS_AFFILIATION_OWNER);
         }
         return null;
     }

     private static String getRoleLocale(int rol) {
         switch (rol) {
             case MucContact.ROLE_VISITOR: return SR.get(SR.MS_ROLE_VISITOR);
             case MucContact.ROLE_PARTICIPANT: return SR.get(SR.MS_ROLE_PARTICIPANT);
             case MucContact.ROLE_MODERATOR: return SR.get(SR.MS_ROLE_MODERATOR);
         }
         return null;
     }

     public void testMeOffline(MucContact mc, ConferenceGroup grp, boolean isKick) {
          if ( grp.selfContact == mc ) {
             if(isKick) {
                 show();
             }
             roomOffline(grp, true);
          }
     }

     private String processPresence(MucContact mc, JabberDataBlock xmuc, Presence presence, String Prtext) {
         String affiliation = mc.affiliation;
         String role = mc.role;
         String from = mc.jid.getJid();
         byte presenceType=presence.getTypeIndex();

         if (presenceType==Presence.PRESENCE_ERROR) {
             return StringUtils.processError(presence, presenceType, (ConferenceGroup)mc.group, mc);
         }

         JabberDataBlock item=xmuc.getChildBlock("item");

         byte roleCode = MucContact.ROLE_PARTICIPANT;
         byte affiliationCode = MucContact.AFFILIATION_NONE;

         String tempRole = item.getAttribute("role");
         if (tempRole.equals("visitor")) {
             roleCode = MucContact.ROLE_VISITOR;
         } else if (tempRole.equals("participant")) {
             roleCode = MucContact.ROLE_PARTICIPANT;
         } else if (tempRole.equals("moderator")) {
             roleCode = MucContact.ROLE_MODERATOR;
         }

         String tempAffiliation = item.getAttribute("affiliation");
         if (tempAffiliation.equals("owner")) {
             affiliationCode = MucContact.AFFILIATION_OWNER;
         } else if (tempAffiliation.equals("admin")) {
             affiliationCode = MucContact.AFFILIATION_ADMIN;
         } else if (tempAffiliation.equals("member")) {
             affiliationCode = MucContact.AFFILIATION_MEMBER;
         } else if (tempAffiliation.equals("none")) {
             affiliationCode = MucContact.AFFILIATION_NONE;
         }

         boolean roleChanged = !tempRole.equals(role);
         boolean affiliationChanged = !tempAffiliation.equals(affiliation);

         mc.affiliation = tempAffiliation;
         mc.role = tempRole;

         mc.roleCode = roleCode;
         mc.affiliationCode = affiliationCode;

         tempRole=null;
         tempAffiliation=null;

         mc.setSortKey(mc.getNick());
         switch (roleCode) {
             case MucContact.ROLE_MODERATOR:
                 mc.transport = RosterIcons.ICON_MODERATOR_INDEX;
                 mc.key0 = MucContact.GROUP_MODERATOR;
                 break;
             case MucContact.ROLE_VISITOR:
                 mc.transport = RosterIcons.getInstance().getTransportIndex("vis");
                 mc.key0 = MucContact.GROUP_VISITOR;
                 break;
             default:
                 mc.transport = mc.affiliation.equals("member")? 0 : RosterIcons.getInstance().getTransportIndex("vis");
                 mc.key0 = (mc.affiliation.equals("member")?MucContact.GROUP_MEMBER:MucContact.GROUP_PARTICIPANT);
         }

         JabberDataBlock statusBlock = xmuc.getChildBlock("status");
         int statusCode = 0;

         if(null != statusBlock) {
             String statusCodeStr = statusBlock.getAttribute("code");
             if (null != statusCodeStr ) {
                 statusCode = Integer.parseInt( statusCodeStr );
                 statusBlock = null;
             }
         }

         StringBuffer mucContactBuf = new StringBuffer(0);
         mucContactBuf.append(mc.getNick().trim());

         String statusText = presence.getChildBlockText("status");
         String tempRealJid = item.getAttribute("jid");
         if (tempRealJid != null && mc.realJid == null) mc.realJid=tempRealJid;  //for moderating purposes

//#ifdef POPUPS
           JabberDataBlock destroy = xmuc.getChildBlock("destroy");
           if(null != destroy){
               if(null != destroy.getChildBlockText("reason")) {
                    setWobble(1,null,"Groupchat " +  destroy.getAttribute("jid") +
                      " was destroyed!(reason: " + destroy.getChildBlockText("reason") + ")");
               }
           }
//#endif

         if (statusCode==201) { //todo: fix this nasty hack, it will not work if multiple status codes are nested in presence)
             mucContactBuf = new StringBuffer(0);
             mucContactBuf.append(SR.get(SR.MS_NEW_ROOM_CREATED));
         } else if (presenceType==Presence.PRESENCE_OFFLINE) {
             mc.key0=3;
             String reason=item.getChildBlockText("reason");
             switch (statusCode) {
                 case 303:
                     mucContactBuf.append(SR.get(SR.MS_IS_NOW_KNOWN_AS));
                     String chNick=item.getAttribute("nick");
                     mucContactBuf.append(chNick.trim());
                     String newJid=from.substring(0, from.indexOf('/')+1)+chNick;
                     mc.jid.setJid(newJid);
                     mc.bareJid=newJid;
                     from=newJid;
                     mc.setNick(chNick);
                     break;
                 case 301: //ban
                 case 307: //kick
                     mucContactBuf.append((statusCode==301) ? SR.get(SR.MS_WAS_BANNED) : SR.get(SR.MS_WAS_KICKED) );
//#ifdef POPUPS
                     if (((ConferenceGroup) mc.group).selfContact == mc) {
                         setWobble(3, null, ((statusCode == 301) ? SR.get(SR.MS_WAS_BANNED)
                                 : SR.get(SR.MS_WAS_KICKED)) + ((reason.length() != 0) ? "\n" + reason : ""));
                     }
//#endif
                     if (reason.length() != 0) {
                         mucContactBuf.append(" (").append(reason).append(')');
                     }

                     testMeOffline(mc, (ConferenceGroup)mc.group , true);
                     break;
                 case 321:
                 case 322:
                     mucContactBuf.append((statusCode==321)?SR.get(SR.MS_HAS_BEEN_UNAFFILIATED_AND_KICKED_FROM_MEMBERS_ONLY_ROOM):
                         SR.get(SR.MS_HAS_BEEN_KICKED_BECAUSE_ROOM_BECAME_MEMBERS_ONLY));
                     testMeOffline(mc, (ConferenceGroup)mc.group , true);
                     break;
                 default:
                     if (tempRealJid!=null)
                         mucContactBuf.append(" (").append(tempRealJid).append(')');
                     mucContactBuf.append(SR.get(SR.MS_HAS_LEFT_CHANNEL));

                     mc.affiliation = "none";
                     mc.role = "none";
                     mc.roleCode = 0;
                     mc.affiliationCode = 0;

                     if (statusText.length()>0)
                         mucContactBuf.append(" (").append(statusText).append(')');
                     testMeOffline(mc, (ConferenceGroup)mc.group, false);
             }
         } else {
             if (mc.status==Presence.PRESENCE_OFFLINE) {
                 if (tempRealJid!=null) {
                     mc.realJid=tempRealJid;  //for moderating purposes
                     mucContactBuf.append(" (").append(tempRealJid).append(')');
                 }
                 mucContactBuf.append(SR.get(SR.MS_HAS_JOINED_THE_CHANNEL_AS));
                 if (affiliationCode!=MucContact.AFFILIATION_MEMBER) {
                     mucContactBuf.append(getRoleLocale(roleCode));
                 }
                 //
                 if (!mc.affiliation.equals("none")) {
                     if (roleCode!=MucContact.ROLE_PARTICIPANT) {
                         mucContactBuf.append(SR.get(SR.MS_AND));
                     }
                     mucContactBuf.append(getAffiliationLocale(affiliationCode));
                 }
                 if (statusText.length()>0) {
                     mucContactBuf.append(" (").append(statusText).append(')');
                 }
             } else {
                 mucContactBuf.append(SR.get(SR.MS_IS_NOW));
                 if (roleChanged) {
                     mucContactBuf.append(getRoleLocale(roleCode));
                 }
                 if(mc.role.equals("visitor")) {
                       if(null != item.getChildBlockText("reason")) {
                           mucContactBuf.append('(').append(item.getChildBlockText("reason")).append(')');
                       }
                  }
                  if (affiliationChanged) {
                     if (roleChanged) mucContactBuf.append(SR.get(SR.MS_AND));
                     mucContactBuf.append(getAffiliationLocale(affiliationCode));
                 }
                 if (!roleChanged && !affiliationChanged) {
                     mucContactBuf.append(Prtext);
                 }
             }
         }

         from=null;
         item=null;

         statusText=null;
         tempRealJid=null;

         mc.setStatus(presenceType);
         return mucContactBuf.toString();
     }
//#endif

//#ifdef POPUPS
    boolean showWobbler(Contact c) {
        if (midlet.BombusQD.cf.popUps==false)
            return false;
        if (activeContact==null)
            return true;
        return(!c.equals(activeContact));
    }
//#endif

//#ifdef FILE_TRANSFER
    public void addFileQuery(TransferTask task) {
        Contact c=null;
        if(c==null) c=getContact(task.jid, true);
        c.fileQuery=true;
        Msg m = new Msg(Msg.SYSTEM, task.jid, SR.get(SR.MS_FILE), "");
        m.attachment = task;
        messageStore(c, m);
    }
//#endif

    public void messageStore(Contact c, Msg message) {
        if (c==null) return;

        boolean active=true;
        if(message.getType()==Msg.PRESENCE){
           //reEnumRoster();//<<<
           Vector hContacts = contactList.contacts;
           int size = hContacts.size();
           Contact search;
           for(int i=0;i<size;i++) {
             search = (Contact)contactList.contacts.elementAt(i);
             if(c==search) active = search.active();
           }
          search=null;
          hContacts=null;
        }
        //В конфе всегда показывать презенсы если включены
        if(active==false && c.origin!=Contact.ORIGIN_GROUPCHAT) return;

        c.addMessage(message);

        boolean autorespond = false;
//#ifdef RUNNING_MESSAGE
        if (message.getType()==Msg.INCOMING)
            setTicker(c, message.toString());
//#endif

        if (countNewMsgs()) {
            reEnumRoster();
        }

        //if (!message.unread) return;
        //TODO: clear unread flag if not-in-list IS HIDDEN

        if (c.getGroupType()==Groups.TYPE_IGNORE)
            return;    // no signalling/focus on ignore

//#ifdef POPUPS
        if (midlet.BombusQD.cf.popUps)
            if (message.getType()==Msg.AUTH && showWobbler(c))
                setWobbler(2, c, message.getFrom()+"\n"+message.getBody(),null);
//#endif

  if (midlet.BombusQD.cf.popupFromMinimized && BombusQD.isMinimized())
      c.getMessageList().show();

        if (midlet.BombusQD.cf.autoFocus && message.getType()!=Msg.PRESENCE && message.getType()!=Msg.OUTGOING)
            focusToContact(c, false);

        if (message.isHighlite()) {
            playNotify(SOUND_FOR_ME);
//#ifdef POPUPS
            if (showWobbler(c))
                setWobbler(2, c, message.getBody(),null);
//#endif
            autorespond = true;

        }else {
      //#ifdef JUICK.COM
            boolean incomingMsg = (message.getType()==Msg.INCOMING || message.getType()==Msg.JUICK);
      //#else
//#             boolean incomingMsg = (message.getType()==Msg.INCOMING);
      //#endif
            boolean groupchat = (c.origin==Contact.ORIGIN_GROUPCHAT);
            if(!incomingMsg) return;

            if (groupchat==false) {
//#ifdef POPUPS
//#ifndef WMUC
                if (!(c instanceof MucContact)) {
//#endif
                    if (showWobbler(c)) {
                        setWobbler(2, c, c.toString()+": "+message.getBody(),null);
                        autorespond = true;
                    }
                }
//#endif
                if (c.group.type==Groups.TYPE_VIP) {
                    playNotify(SOUND_FOR_VIP);
                    autorespond = true;
                } else {
                    playNotify(SOUND_MESSAGE);
                    autorespond = true;
                }
            }
//#ifndef WMUC
            else playNotify(SOUND_FOR_CONFERENCE);
//#endif
        }

        if (c.origin==Contact.ORIGIN_GROUPCHAT || c.jid.isTransport()
              || c.getGroupType()==Groups.TYPE_TRANSP || c.getGroupType()==Groups.TYPE_SEARCH_RESULT
                || c.getGroupType()==Groups.TYPE_SELF)
            autorespond=false;

        if (message.getType()!=Msg.INCOMING)
            autorespond=false;

        if (!c.autoresponded && autorespond) {
            ExtendedStatus es = StatusList.getInstance().getStatus(myStatus);
            if (es.getAutoRespond()) {
                Message autoMessage = new Message(
                        c.getJid(),
                        es.getAutoRespondMessage(),
                        SR.get(SR.MS_AUTORESPOND),
                        false
                );
                theStream.send(autoMessage);
                autoMessage=null;
                es=null;
                c.autoresponded=true;

          c.addMessage(new Msg(Msg.SYSTEM, "local", SR.get(SR.MS_AUTORESPOND), ""));


            }
        }
//#ifdef CLASSIC_CHAT
//#         if(Config.module_classicchat) {
//#             StringUtils.addClassicChatMsg(message.toString(), SimpleItemChat.getWidth() ,c.scroller);
//#         }
//#endif
    }

    private static long notifyReadyTime=System.currentTimeMillis();
    private static int blockNotifyEvent=-111;


    public final static int SOUND_FOR_ME=500;
    public final static int SOUND_FOR_CONFERENCE=800;
    public final static int SOUND_MESSAGE=1000;
    public final static int SOUND_CONNECTED=777;
    public final static int SOUND_FOR_VIP=100;
    public final static int SOUND_COMPOSING=888;
    public final static int SOUND_OUTGOING=999;
    public final static int SOUND_ATTENTION=666;


    public static void blockNotify(int event, long ms) {
        if (!notifyReady(-111)) return;
        blockNotifyEvent=event;
        notifyReadyTime=System.currentTimeMillis()+ms;
    }

    public static boolean notifyReady(int event) {
        if ((blockNotifyEvent==event || (blockNotifyEvent==-111 && event<=7)) && System.currentTimeMillis()<notifyReadyTime) return false;
        else return true;
    }

    public static void playNotify(int event) {
        if(midlet.BombusQD.cf.currentAlertProfile==AlertProfile.NONE) return;
        if (notifyReady(event)==false) return;
        AlertCustomize ac=AlertCustomize.getInstance();

        int volume=ac.soundVol;
        int vibraLen=ac.vibraLen;
        String type, message;
        //boolean flashBackLight=ac.flashBackLight;

        switch (event) {
            case 0: //online
            case 1: //chat
                message=ac.soundOnline;
                type=ac.soundOnlineType;
                vibraLen=0;
                //flashBackLight=false;
                break;
            case 5: //offline
                message=ac.soundOffline;
                type=ac.soundOfflineType;
                vibraLen=0;
                //flashBackLight=false;
                break;
            case SOUND_FOR_VIP: //VIP
                message=ac.soundVIP;
                type=ac.soundVIPType;
                break;
            case SOUND_MESSAGE: //message
                message=ac.messagesnd;
                type=ac.messageSndType;
                break;
            case SOUND_FOR_CONFERENCE: //conference
                message=ac.soundConference;
                type=ac.soundConferenceType;
                if (ac.vibrateOnlyHighlited) vibraLen=0;
                break;
            case SOUND_FOR_ME: //message for you
                message=ac.soundForYou;
                type=ac.soundForYouType;
                break;
            case SOUND_CONNECTED: //startup
                message=ac.soundStartUp;
                type=ac.soundStartUpType;
                vibraLen=0;
                //flashBackLight=false;
                break;
            case SOUND_COMPOSING: //composing
                message=ac.soundComposing;
                type=ac.soundComposingType;
                vibraLen=0;
                //flashBackLight=false;
                break;
            case SOUND_OUTGOING: //Outgoing
                message=ac.soundOutgoing;
                type=ac.soundOutgoingType;
                vibraLen=0;
                //flashBackLight=false;
                break;
            case SOUND_ATTENTION://Attention Request
                message=ac.soundAttention;
                type=ac.soundAttentionType;
                vibraLen=vibraLen*5;
                break;
            default:
                message="";
                type="none";
                vibraLen=0;
                //flashBackLight=false;
                break;
        }

        int profile=midlet.BombusQD.cf.currentAlertProfile;
        EventNotify notify=null;

        switch (profile) {                                //display   fileType   soundName   volume      vibrate
            case AlertProfile.ALL:   notify=new EventNotify(type,   message,    volume,     vibraLen); break;
            case AlertProfile.VIBRA: notify=new EventNotify(null,   null,       volume,     vibraLen); break;
            case AlertProfile.SOUND: notify=new EventNotify(type,   message,    volume,     0); break;
        }
        if (notify!=null) notify.startNotify();
        type=null;
        message=null;
        blockNotify(event, 2000);
        notify = null;
    }

     private void focusToContact(final Contact c, boolean force) {
        Group g = c.group;
         if (g.collapsed) {
            g.collapsed = false;
            reEnumerator.queueEnum(c, force);
            reEnumerator.update();
         }
        //paintVContacts = vContacts;
        int index = vContacts.indexOf(c);
        if (index >= 0) {
            moveCursorTo(index);
        }
     }


    public void beginConversation() { //todo: verify xmpp version
        if (theStream.isXmppV1())
            new SASLAuth(midlet.BombusQD.sd.account, this)
//#if SASL_XGOOGLETOKEN
             .setToken(token)
//#endif
             ;
//#if NON_SASL_AUTH
//#         else new NonSASLAuth(midlet.BombusQD.sd.account, this, theStream);
//#endif
    }


    /**
     * If the connection is terminated then print a message
     *
     * @e The exception that caused the connection to be terminated, Note that
     *  receiving a SocketException is normal when the client closes the stream.
     */

    public void connectionTerminated( Exception e ) {
//#ifdef DEBUG_CONSOLE
//#         midlet.BombusQD.debug.add("::connectionTerminated " + e ,10);
//#endif
         if( e!=null ) {
            askReconnect(e);
        } else {
//#ifdef AUTOSTATUS
             AutoStatus.getInstance().stop();
//#endif
            setProgress(SR.get(SR.MS_DISCONNECTED), 0);
            try {
                sendPresence(Presence.PRESENCE_OFFLINE, null);
            } catch (Exception e2) {
//#if DEBUG
//#                 e2.printStackTrace();
//#endif
            }
         }
        redraw();
    }

    private void askReconnect(final Exception e) {
        StringBuffer error=new StringBuffer(0);
        if (e.getClass().getName().indexOf("java.lang.Exception")<0) {
            error.append(e.getClass().getName());
            error.append('\n');
        }
        if (e.getMessage()!=null)
            error.append(e.getMessage());

        if (e instanceof SecurityException) { errorLog(error.toString()); return; }
        if (currentReconnect>=Config.reconnectCount) { errorLog(error.toString()); return; }
        currentReconnect++;
        String topBar="("+currentReconnect + "/" + Config.reconnectCount+") Reconnecting";
        errorLog(topBar+"\n"+error.toString());

  AlertBox box = new AlertBox(topBar, error.toString(), AlertBox.BUTTONS_YESNO, Config.reconnectTime){
      public void yes(){doReconnect();};
  };
        box.setParentView(BombusQD.sd.roster);
        box.show();
     }

     public void doReconnect() {
//#ifdef DEBUG_CONSOLE
//#         midlet.BombusQD.debug.add("::doReconnect()" ,10);
//#endif

  setProgress(SR.get(SR.MS_DISCONNECTED), 0);
        logoff(null);
        try {
             sendPresence(lastOnlineStatus, null);
        } catch (Exception e2) { }

     }


     public void logoff(String mess){
        if (isLoggedIn()) {
            try {
                if (mess==null) mess = StatusList.getInstance().getStatus(Presence.PRESENCE_OFFLINE).getMessage();
                sendPresence(Presence.PRESENCE_OFFLINE, mess);
            } catch (Exception e) { }
        }
//#ifdef STATS
//#ifdef PLUGINS
//#         if (midlet.BombusQD.sd.Stats)
//#endif
            Stats.getInstance().saveToStorage(false,false);
//#endif
      }

     public void logon(String mess){
        if( midlet.BombusQD.cf.accountIndex <0) return;
        if (!isLoggedIn()) {
            try {
                Account.loadAccount(true, midlet.BombusQD.cf.accountIndex, -1);
            } catch (Exception e) { }
        }
            //Stats.getInstance().saveToStorage(false,false);
      }


    public void eventLongOk(){
        super.eventLongOk();
        showActionsMenu(getFocusedObject());
    }

    public void setMsgEditText(Contact contact, String text){
         showMsgEditor(contact, text);
    }

    public void showMultiMsgEditor(Vector contacts) {
//#ifdef ANDROID
//#         createMessageEdit();
//#endif
//#ifdef RUNNING_MESSAGE
        msgEditor.setTicker("");
//#endif
        msgEditor.show(contacts);
    }

    public void showMsgEditor(Contact c, String body) {
//#ifdef ANDROID
//#         createMessageEdit();
//#endif
        
//#ifdef RUNNING_MESSAGE
        msgEditor.setTicker(c.getNickJid());
//#endif
        msgEditor.show(c, body);
//#ifdef ANDROID
//# msgEditor = null; 
//#endif     
    }

    protected void keyGreen() {
        if (!isLoggedIn()) {
            return;
        }
        Object e = getFocusedObject();
        if (e instanceof Contact) {
            Contact c = (Contact)e;

//#ifdef CLASSIC_CHAT
//#             if (!Config.module_classicchat) {
//#endif
                if (c.getMessageCount() == 0) {
//#ifdef HISTORY
                    if (!Config.module_history || Config.getInstance().loadLastMsgCount == 0)
//#endif
                    {
                        showMsgEditor(c, c.msgSuspended);
                        return;
                    }
                }
                c.getMessageList().show();
//#ifdef CLASSIC_CHAT
//#             } else {
//#                 new SimpleItemChat(c);
//#             }
//#endif
        } else {
            cleanupGroup();
            reEnumRoster();
            redraw();//???
        }
    }

    protected void keyClear() {
        if (isLoggedIn()) {
            VirtualElement item = (VirtualElement)getFocusedObject();
            try {
                if (item instanceof Contact) {
                    final Contact c = (Contact)item;
//#ifndef WMUC
                    boolean isMucContact = (item instanceof MucContact);
                    if (!isMucContact) {
//#endif
                        AlertBox box = new AlertBox(SR.get(SR.MS_DELETE_ASK), c.getNickJid(), AlertBox.BUTTONS_YESNO) {
                            public void yes() {
                                deleteContact(c);
                            }
                        };
                        box.show();
//#ifndef WMUC
                    } else if (c.origin!=Contact.ORIGIN_GROUPCHAT) {
                        ConferenceGroup mucGrp=(ConferenceGroup)c.group;
                        if (mucGrp.selfContact.roleCode==MucContact.ROLE_MODERATOR) {
                            String myNick=mucGrp.selfContact.getName();
                            MucContact mc=(MucContact)item;
                            new QuickPrivelegyEditForm(mc, QuickPrivelegyEditForm.KICK,myNick).show();
                        }
                    }
//#endif
                }
            } catch(OutOfMemoryError eom) {
                errorLog("error Roster::4");
            } catch (Exception e) {}
        }
    }

    public void eventOk() {
        super.eventOk();
        keyGreen();
    }

    public void keyPressed(int keyCode){
        if (gm.itemGrMenu > 0) {
            super.keyPressed(keyCode);
            return;
        } else {
            super.keyPressed(keyCode);
        }
        switch (keyCode) {
//#ifdef POPUPS
            case VirtualCanvas.KEY_POUND:
                if (getItemCount()==0)
                    return;
                Object focused = getFocusedObject();
                if (focused instanceof Contact) {
                    showInfo((Contact)focused);
                }
                return;
//#endif
            case VirtualCanvas.KEY_NUM1:
                if (midlet.BombusQD.cf.collapsedGroups) { //collapse all groups
                     int size = contactList.groups.groups.size();
                     for (int i=0; i<size; ++i) {
                         Group grp=(Group)contactList.groups.groups.elementAt(i);
                         grp.collapsed=true;
                     }
                    setModified();
                    reEnumRoster();
                }
                break;
            case VirtualCanvas.KEY_NUM4:
                super.pageLeft();
                return;
            case VirtualCanvas.KEY_NUM6:
                super.pageRight();
                return;
// что это?
/*
//#ifdef AUTOSTATUS
            case SE_FLIPCLOSE_JP6:
            case SIEMENS_FLIPCLOSE:
            case MOTOROLA_FLIP:
                if (phoneManufacturer!=Config.SONYE) { //workaround for SE JP6 - enabling vibra in closed state
                    BombusQD.setCurrentView(null);
                    try {
                        Thread.sleep(300);
                    } catch (Exception ex) {}
                    BombusQD.setCurrentView(this);
                }
                if (midlet.BombusQD.cf.autoAwayType==Config.AWAY_LOCK)
                    if (!autoAway)
                        autostatus.setTimeEvent(Config.autoAwayDelay * 60 * 1000L);
                break;
//#endif
*/

            case VirtualCanvas.KEY_NUM0:
                if (getItemCount()==0)
                     return;
                Vector contacts = contactList.contacts;
                int size = contacts.size() - 1;
                for (int index = size; 0 <= index; --index) {
                    Contact contact = (Contact) contacts.elementAt(index);
                    contact.setIncoming(INC_NONE);
                 }
                if (messageCount==0) return;
                Contact c = null;
                Object atcursor = getFocusedObject();
                if (atcursor instanceof Contact) c = (Contact)atcursor;
                c = contactList.getFirstContactWithNewMessage(c);
                if (null != c) {
                    focusToContact(c, true);
                    setRotator();
                }
                redraw();
                break;

            case VirtualCanvas.KEY_NUM3:
                showActiveContacts(null);
                break;
            case VirtualCanvas.KEY_NUM9:
                if (getItemCount()==0)
                    return;
                int newpos2=searchGroup(1);
                if (newpos2>-1) {
                    moveCursorTo(newpos2);
                    setRotator();
                }
                break;
            case VirtualCanvas.KEY_STAR:
                if (midlet.BombusQD.cf.ghostMotor) {
                    // backlight management
                    blState=(blState==1)? Integer.MAX_VALUE : 1;
                    BombusQD.display.flashBacklight(blState);
                }
                break;
        }
     }

    protected boolean keyLong(int keyCode) {
        if (keyCode==VirtualCanvas.KEY_STAR) {
//#ifdef AUTOSTATUS
            AutoStatus.getInstance().appLocked();
//#endif
            new SplashScreen(getMainBarItem()).show();
            return true;
        } else if (keyCode==VirtualCanvas.KEY_POUND) {//WTF  keyCode==MOTOE680_FMRADIO?
            // swap profiles
            int profile=midlet.BombusQD.cf.currentAlertProfile;
            midlet.BombusQD.cf.currentAlertProfile=(profile==AlertProfile.VIBRA)?midlet.BombusQD.cf.lastProfile : AlertProfile.VIBRA;
            midlet.BombusQD.cf.lastProfile=profile;
            updateMainBar();
            redraw();
            return true;
        } else if (keyCode==VirtualCanvas.KEY_NUM0) {
            midlet.BombusQD.cf.showOfflineContacts=!midlet.BombusQD.cf.showOfflineContacts;
            sortRoster(null);
            setUpdateView();
            reEnumRoster();
            return true;
        }
//#ifndef WMUC
        else if (keyCode==VirtualCanvas.KEY_NUM1 && isLoggedIn()) {
            new Bookmarks().show();
            return true;
        }
//#endif

        else if (keyCode==VirtualCanvas.KEY_NUM4) {
            new ConfigForm().show();
            return true;
        }
        else if (keyCode==VirtualCanvas.KEY_NUM6) {
            Config.fullscreen =! Config.fullscreen;
            BombusQD.sd.canvas.setFullScreenMode(Config.fullscreen);
            midlet.BombusQD.cf.saveToStorage();
            return true;
        }
//#ifdef SERVICE_DISCOVERY
        else if (keyCode==VirtualCanvas.KEY_NUM7 && isLoggedIn()) {
            new ServiceDiscovery(null, null, false).show();
            return true;
        }
//#endif
        else if (keyCode==VirtualCanvas.KEY_NUM9) {
            if (midlet.BombusQD.cf.allowMinimize)
                BombusQD.hideApp();
            else if (phoneManufacturer==Config.SIEMENS2)//SIEMENS: MYMENU call. Possible Main Menu for capable phones
                 try {
                      BombusQD.getInstance().platformRequest("native:ELSE_STR_MYMENU");
                 } catch (Exception e) { }
            else if (phoneManufacturer==Config.SIEMENS)//SIEMENS-NSG: MYMENU call. Possible Native Menu for capable phones
                 try {
                    BombusQD.getInstance().platformRequest("native:NAT_MAIN_MENU");
                 } catch (Exception e) { }
            
            return true;
        }
        return super.keyLong(keyCode);
    }

    public void ShowHideOffline( ){
        midlet.BombusQD.cf.showOfflineContacts=!midlet.BombusQD.cf.showOfflineContacts;
        sortRoster(null);
        setUpdateView();
        reEnumRoster();
    }

//#ifdef POPUPS
    public void showInfo(Contact contact) {
        try {
            VirtualList.getPopUp().next();
            setWobbler(1, contact, null, contact);
        } catch(OutOfMemoryError eom) {
            errorLog("error Roster::5 OutOfMemoryError(" + contact + "->Class::" + contact.getClass().toString() + ")");
        } catch (Exception e) {
            errorLog("error Roster::5 Exception->" + contact);
        }
    }

    public void setWobbler(int type, Contact contact, String info,Object focused) {
        if (info==null) {
            if(focused==null) {
                return;
            }
            Contact cntact=(Contact)focused;
            StringBuffer mess = new StringBuffer();
//#ifndef WMUC
            boolean isMucContact = (focused instanceof MucContact);
            if (isMucContact) {
                MucContact mucContact=(MucContact)focused;

                if (mucContact.origin!=Contact.ORIGIN_GROUPCHAT){
                    mess.append((mucContact.realJid==null)?"":"jid: "+mucContact.realJid+'\n');

                    if (mucContact.affiliationCode>MucContact.AFFILIATION_NONE)
                        mess.append(getAffiliationLocale(mucContact.affiliationCode));

                    if (!(mucContact.roleCode==MucContact.ROLE_PARTICIPANT && mucContact.affiliationCode==MucContact.AFFILIATION_MEMBER)) {
                        if (mucContact.affiliationCode>MucContact.AFFILIATION_NONE)
                            mess.append(SR.get(SR.MS_AND));
                        mess.append(getRoleLocale(mucContact.roleCode));
                    }
//#ifdef PEP
                    if (cntact.hasMood()) {
                         mess.append('\n')
                        .append(SR.get(SR.MS_USERMOOD))
                        .append(": ")
                        .append(cntact.getMoodString());
                    }
//#endif
                }
            } else {
//#endif
                mess.append("Jid: ").append(cntact.getJid()).append('\n');
                mess.append(SR.get(SR.MS_SUBSCRIPTION)).append(": ");

                if (cntact.subscr != null) {
                    if (cntact.subscr.indexOf("both") > -1) {
                        mess.append(SR.get(SR.MS_SUBSCR_BOTH));
                    } else if (cntact.subscr.indexOf("from") > -1) {
                        mess.append(SR.get(SR.MS_SUBSCR_FROM));
                    } else if (cntact.subscr.indexOf("to") > -1) {
                        mess.append(SR.get(SR.MS_SUBSCR_TO));
                    } else if (cntact.subscr.indexOf("none") > -1) {
                        mess.append(SR.get(SR.MS_SUBSCR_NONE));
                    } else {
                        mess.append(cntact.subscr);
                    }
                } else {
                    mess.append("self");
                }
//#ifdef PEP
                if (cntact.hasMood()) {
                    mess.append('\n')
                        .append(SR.get(SR.MS_USERMOOD))
                        .append(": ")
                        .append(cntact.getMoodString());
                }
                if (cntact.hasActivity()) {
                    mess.append('\n').append(SR.get(SR.MS_USERACTIVITY)).append(": ").append(cntact.activity);
                }
                if (cntact.pepTune) {
                    mess.append('\n').append(SR.get(SR.MS_USERTUNE));
                    if (cntact.pepTuneText.length() != 0) {
                        mess.append(": ").append(cntact.pepTuneText);
                    }
                }
//#endif
//#ifndef WMUC
            }
//#endif
            if (cntact.origin!=Contact.ORIGIN_GROUPCHAT){
                if (cntact.j2j != null) {
                    mess.append("\nJ2J: ").append(cntact.j2j);
                }
//#ifdef CLIENTS_ICONS
                if (midlet.BombusQD.cf.showClientIcon) {
                    if (cntact.client != -1) {
                        mess.append('\n')
                            .append(SR.get(SR.MS_USE))
                            .append(": ")
                            .append(cntact.clientName);
                        if (cntact.version != null) {
                            mess.append(' ').append(cntact.version);
                        }
                    }
                }
//#endif
                if (cntact.lang != null) {
                    mess.append('\n')
                        .append(SR.get(SR.MS_LANGUAGE))
                        .append(": ")
                        .append(cntact.lang);
                }
            }

            if (cntact.getStatus()!=null) {
                if (cntact.origin!=Contact.ORIGIN_GROUPCHAT){
                    mess.append('\n')
                        .append(SR.get(SR.MS_STATUS))
                        .append(": ");
                }
                mess.append(cntact.getStatus());
            }
            if(cntact.annotations!=null) {
                mess.append('\n').append(SR.get(SR.MS_ANNOTATION)).append(": ").append(cntact.annotations);
            }

            VirtualList.setWobble(1, null, mess.toString());
        } else {
            VirtualList.setWobble(type, contact.getJid(), info);
        }

        redraw();
    }
//#endif

    public void quit() {
//#ifdef AUTOSTATUS
        AutoStatus.getInstance().stop();
//#endif
        logoff(null);

        BombusQD.getInstance().notifyDestroyed();
    }

    public void cmdQuit() {
        if (midlet.BombusQD.cf.queryExit) {
            AlertBox box = new AlertBox(SR.get(SR.MS_QUIT_ASK), SR.get(SR.MS_SURE_QUIT), AlertBox.BUTTONS_YESNO) {
                public void yes() {quit(); }
            };
            box.show();
        } else {
            quit();
        }
    }

   public void cmdAdd() {
       if (isLoggedIn()) {
            Object o=getFocusedObject();
            Contact cn=null;
            if (o instanceof Contact) {
                cn=(Contact)o;
                if (cn.getGroupType()!=Groups.TYPE_NOT_IN_LIST && cn.getGroupType()!=Groups.TYPE_SEARCH_RESULT)
                    cn=null;
            }
//#ifndef WMUC
            if (o instanceof MucContact)
                cn=(Contact)o;
//#endif
            new ContactEdit(cn).show();
       }
   }

    protected void showNotify() {
        super.showNotify();
        countNewMsgs();
    }

    private int searchGroup(int direction) {
        Vector contacts = vContacts;
        int size = contacts.size();
        int pos = cursor;
        int count = size;
        try {
            while (count > 0) {
                pos += direction;
                if (pos<0) pos = size - 1;
                if (pos>=size) pos=0;
                if (contacts.elementAt(pos) instanceof Group) break;
            }
        } catch (Exception e) {
            return cursor;
        }
        return pos;
     }

    public void searchActiveContact(Contact first, boolean right) {
        Vector aContacts = new Vector(0);
        Vector search = contactList.contacts;
        int size = search.size();
        Contact activeContact;
        Contact showNext;
        for(int i = 0; i < size; ++i) {
             activeContact = (Contact)search.elementAt(i);
             if(activeContact.active()) aContacts.addElement(activeContact);
        }
        if (aContacts.isEmpty()) return;
        if (aContacts.size()<2 && first == aContacts.firstElement()) return;
        int pos = aContacts.indexOf(first);
        if (pos<0) showNext = (Contact)aContacts.firstElement();
        else if (right) {
           showNext = (aContacts.size() - 1 == pos) ? (Contact)aContacts.firstElement() : (Contact)aContacts.elementAt(pos + 1);
        } else {
          showNext = (0 == pos) ? (Contact)aContacts.lastElement() : (Contact)aContacts.elementAt(pos - 1);
        }
        
        if (first!=showNext && first!=null && first.getMessageList()!=null)
            first.getMessageList().updateSeparator();
        showNext.getMessageList().show();
    }

    public void deleteContact(Contact c) {
        Vector hContacts = contactList.contacts;
        for (Enumeration e=hContacts.elements();e.hasMoreElements();) {
            Contact c2=(Contact)e. nextElement();
            if (c.jid.equals(c2. jid,false)) {
                c2.setStatus(Presence.PRESENCE_TRASH);
                c2.offline_type = Presence.PRESENCE_TRASH;
            }
        }

        if (c.getGroupType() == Groups.TYPE_NOT_IN_LIST) {
            contactList.removeContact(c);
            countNewMsgs();
            setModified();
            reEnumRoster();

        } else {
            theStream.send(new IqQueryRoster(c.bareJid,null,null,"remove"));

            sendPresence(c.bareJid, "unsubscribe", null, false);
            sendPresence(c.bareJid, "unsubscribed", null, false);
        }
    }

    public void setQuerySign(boolean requestState) {
        querysign=requestState;
        updateMainBar();
    }

    public void storeContact(String jid, String name, String group, boolean askSubscribe){
        theStream.send(new IqQueryRoster(jid, name, group, null));
        if (askSubscribe) theStream.send(new Presence(jid,"subscribe"));
    }

    public void loginMessage(String msg, int pos) {
        setProgress(msg, pos);
    }

    public void setMyJid(Jid myJid) {
        this.myJid = myJid;
    }

    public void deleteGroup(Group deleteGroup) {
        Vector contacts = contactList.contacts;
        for (int index = contacts.size() - 1; 0 <= index; --index) {
            Contact contact = (Contact) contacts.elementAt(index);
            if (contact.group==deleteGroup) deleteContact(contact);
        }
    }

    public int showGraphicsMenu() {
         GMenuConfig.getInstance().itemGrMenu = GMenu.MAIN_MENU_ROSTER;
         commandState();
         menuItem = new GMenu(this, MenuIcons.getInstance(), menuCommands, cmdfirstList, cmdsecondList, cmdThirdList);
         redraw();
        return GMenu.MAIN_MENU_ROSTER;
    }

    public String touchLeftCommand(){ return (midlet.BombusQD.cf.oldSE)?SR.get(SR.MS_ITEM_ACTIONS):SR.get(SR.MS_MENU); }
    public String touchRightCommand(){ return (midlet.BombusQD.cf.oldSE)?SR.get(SR.MS_MENU):SR.get(SR.MS_ITEM_ACTIONS); }

//#ifdef TOUCH
    protected void touchMainPanelPressed(int x, int y) {
        int zoneWidth = width / 4;

        if (x > width - zoneWidth) {
            cmdAlert();
        } else if (x < zoneWidth){
            cmdStatus();
        } else {
            showActiveContacts(null);
        }
    }

    public void touchMiddlePressed(){
        if (getItemCount()==0)
             return;
        Vector contacts = contactList.contacts;
        int size = contacts.size() - 1;
        for (int index = size; 0 <= index; --index) {
            Contact contact = (Contact) contacts.elementAt(index);
            contact.setIncoming(INC_NONE);
         }
        if (messageCount==0) return;
        Contact c = null;
        Object atcursor = getFocusedObject();
        if (atcursor instanceof Contact) c = (Contact)atcursor;
        c = contactList.getFirstContactWithNewMessage(c);
        if (null != c) {
            focusToContact(c, true);
            setRotator();
           fitCursorByBottom();
        }
        redraw();
    }
//#endif
    public void touchRightPressed() {
        if (midlet.BombusQD.cf.oldSE) {
            showGraphicsMenu();
        } else {
            showActionsMenu(getFocusedObject());
        }
    }

    public void touchLeftPressed() {
        if (midlet.BombusQD.cf.oldSE) {
            showActionsMenu(getFocusedObject());
        } else {
            showGraphicsMenu();
        }
    }

//#ifdef RUNNING_MESSAGE
    void setTicker(Contact c, String message) {
       if (midlet.BombusQD.cf.runningMessage) {
           if (msgEditor == null) {
               return;
           }
           if (msgEditor.getContact() == c) {
               message = StringUtils.replaceNickTags(message);
               msgEditor.setTicker(message);
           }
        }
    }
//#endif

    public void setUpdateView() {
        reEnumerator.setUpdateView();
    }
    private class ReEnumerator {
        Object desiredFocus;

        public void queueEnum(Object focusTo, boolean force) {
            desiredFocus=focusTo;
            doId();
        }

        public void queueEnum() {
            doId();
        }
        private boolean isModified = false;
        private boolean isUpdateView = false;

        private void setModified() {
            isModified = true;
        }
        public void setUpdateView() {
            isUpdateView = true;
        }
        private void doId() {
            setModified();
            redraw();
        }

        public void update() {
            if(theStream == null) return;
            try {
                Object focused = (desiredFocus == null) ? getFocusedObject() : desiredFocus;
                reinit(focused);
                if(desiredFocus!=null) desiredFocus = null;
                focused = null;
            } catch (Exception e) {
//#ifdef DEBUG
//#                 e.printStackTrace();
//#endif
            }
        }
        private void reinit(Object focused) {
            if (isModified) {
                contactList.updateUI();
                isModified = false;
                isUpdateView = true;
            }

            if (isUpdateView) {
                isUpdateView = false;
                int locCursor=cursor;

                //int sV = vContacts.size();
                  vContacts = null;
                  vContacts = new Vector(0);
                  vContacts = contactList.getVisibleTree(vContacts);

                  StringBuffer counts = new StringBuffer(0);
                  counts.append('(')
                        .append(contactList.getOnlineCount())
                        .append('/')
                        .append(contactList.getContactCount())
                        .append(')');
                  mainbar.setElementAt( counts , 3);
                  selfContact().setStatus(myStatus);

                if (cursor<0) cursor=0;

                if ( locCursor==cursor && focused!=null ) {
                    int c=vContacts.indexOf(focused);
                    if (c>=0) moveCursorTo(c);
                }
                focusedItem(cursor);
            }
        }
    }
}
