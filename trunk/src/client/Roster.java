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

import account.Account;
import account.AccountSelect;
import alert.AlertCustomize;
import alert.AlertProfile;
import client.roster.ContactList;
//#ifndef WMUC
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
//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.CommandListener;
//# import javax.microedition.lcdui.Command;
//#else
import menu.MenuListener;
import menu.Command;
//#endif
//#ifdef FILE_IO
//#ifdef FILE_TRANSFER
import io.file.transfer.TransferDispatcher;
//#endif
//#endif
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
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

import conference.QueryConfigForm;
//#ifdef PEP
import xmpp.extensions.PepListener;
//#endif
import javax.microedition.lcdui.Image;
//import javax.microedition.lcdui.Alert;
//import javax.microedition.lcdui.AlertType;

//#ifdef FILE_IO
import io.file.FileIO;
import java.io.IOException;
import java.io.OutputStream;
//#endif

//#ifdef GRAPHICS_MENU
import ui.GMenu;
//#ifdef SERVICE_DISCOVERY
import disco.ServiceDiscovery;
//#endif
import vcard.VCard;
//#ifdef PRIVACY
//#endif
import conference.ConferenceForm;
//#ifdef USER_KEYS
//#endif
//#ifdef STATS
import stats.Stats;
//#endif
//#ifdef XML_CONSOLE
//# import console.xml.XMLConsole;
//#endif
//#ifdef DEBUG_CONSOLE
//# import console.debug.DebugConsole;
//#endif
//#else
//# import Menu.RosterToolsMenu;
//#endif
//#ifdef LIGHT_CONTROL
import light.*;
//#endif
//#ifdef JUICK.COM
import xmpp.extensions.JuickModule;
//#endif
import colors.ColorTheme;
//#ifdef HISTORY
import history.HistoryViewer;
//#endif
import ui.controls.form.MultiLine;

public class Roster
        extends VirtualList
        implements
        JabberListener,
//#ifndef MENU_LISTENER
//#         CommandListener,
//#else
        MenuListener,
//#endif
        Runnable,
        LoginListener
{

    private GMenuConfig gm=GMenuConfig.getInstance();
    //#ifdef JUICK.COM
    private JuickModule juick = JuickModule.jm();
    //#endif


    public Contact activeContact = null;
    private Jid myJid;
    public JabberStream theStream = null;
    public int messageCount;
    int highliteMessageCount;
    public Object transferIcon;

    public ContactList contactList = new ContactList();
    private Vector vContacts = new Vector(0); // just for displaying
    private Vector paintVContacts = new Vector(0);


    public Vector bookmarks;

    private StatusList sl;
    public int myStatus=midlet.BombusQD.cf.loginstatus;
    private static String myMessage;
    public static int oldStatus=0;
    private static int lastOnlineStatus;

    public boolean doReconnect=false;

    public boolean querysign=false;

//#ifdef AUTOSTATUS
    private AutoStatusTask autostatus;
    public static boolean autoAway=false;
    public static boolean autoXa=false;
//#endif

//#if SASL_XGOOGLETOKEN
    private String token;
//#endif
    public int currentReconnect=0;
    public long lastMessageTime=Time.utcTimeMillis();

    public static String startTime=Time.dispLocalTime();



    private int blState=Integer.MAX_VALUE;

//#ifdef PEP
    //EventPublish publishEvent = new EventPublish();
//#endif
    static String hashcheck = StringUtils.calcHash();

    //public ConfigForm pluginsConfig;
    private static MessageEdit messageEdit;
    private static MessageEdit altmessageEdit;

//#ifdef PEP
    public static SelectPEP selectPEP = null;
//#endif

    public final void showActiveContacts(Displayable pView, Contact current){
        new ActiveContacts(display, pView, current);
    }

    public final void showActionsMenu(Displayable pView, Object object) {
       if (isLoggedIn()) {
           if (object instanceof Group) {
               int type = ((Group)object).type;
               if (type == Groups.TYPE_TRANSP || type == Groups.TYPE_SELF) {
                   return;
               }
           }

           new ActionsMenu(display, pView, object);
       }
    }

//#ifdef HISTORY
    public final void showHistory(Displayable pView, Contact c) {
        new HistoryViewer(display, pView, c);
    }
//#endif

    public void createMessageEdit(boolean reCreate){
         if(reCreate)
             messageEdit = altmessageEdit = null;

         if(null == messageEdit && null == altmessageEdit) {
             messageEdit = new MessageEdit(display);
             altmessageEdit = new MessageEdit(display, true);
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



    public Roster(Display display) { //init
        super();
        initCommands();
        this.display=display;
        createMessageEdit(false);

        sl=StatusList.getInstance();

        setLight(midlet.BombusQD.cf.lightState);

        MainBar mainbar=new MainBar(4, null, null, false);
        setMainBarItem(mainbar);
        mainbar.addRAlign();
        mainbar.addElement(null);
        mainbar.addElement(null);
        mainbar.addElement(null); //ft
        updateMainBar();

        setCommandListener(this);

        //message.MessageParser.restart();

//#ifdef PEP
        if(selectPEP == null) selectPEP = new SelectPEP(display);
//#endif

        midlet.BombusQD.getInstance().s.setExit(display, this);
//#ifdef AUTOSTATUS
        if (midlet.BombusQD.cf.autoAwayType==Config.AWAY_IDLE || midlet.BombusQD.cf.autoAwayType==Config.AWAY_MESSAGE)
            autostatus=new AutoStatusTask(false);

        if (myStatus<2)
            messageActivity();
//#endif
    }

    public void showRoster(){
        midlet.BombusQD.getInstance().display.setCurrent(this);
    }

    public void setLight(boolean state) {
        if (phoneManufacturer==Config.SIEMENS || phoneManufacturer==Config.SIEMENS2) {
            try {
                if (state) com.siemens.mp.game.Light.setLightOn();
                else com.siemens.mp.game.Light.setLightOff();
            } catch( Exception e ) { }
            return;
        }
        if (!state) return;
    }

    public void initCommands() {
        createMessageEdit(true);
        StatusList.getInstance().reinit();

        cmdActions = new Command(SR.get(SR.MS_ITEM_ACTIONS), Command.SCREEN, 2);
        cmdActions.setImg(MenuIcons.ICON_ITEM_ACTIONS);

        cmdStatus = new Command(SR.get(SR.MS_STATUS), Command.SCREEN, 4);
        cmdStatus.setImg(MenuIcons.ICON_STATUS);

//#ifdef GRAPHICS_MENU
        cmdOptions = new Command(SR.get(SR.MS_OPTIONS), Command.SCREEN, 16);
        cmdOptions.setImg(0x03);

        cmdMyService = new Command(SR.get(SR.MS_SERVICE), Command.SCREEN, 22);
        cmdMyService.setImg(0x90);

        cmdAlert = new Command(SR.get(SR.MS_ALERT_PROFILE_CMD), Command.SCREEN, 32);
        cmdAlert.setImg(MenuIcons.ICON_NOTIFY);
//#ifdef ARCHIVE
        cmdArchive = new Command(SR.get(SR.MS_ARCHIVE), Command.SCREEN, 33);
        cmdArchive.setImg(MenuIcons.ICON_ARCHIVE);
//#endif
//#endif
//#ifdef XML_CONSOLE
//#         cmdXMLConsole = new Command(SR.get(SR.MS_XML_CONSOLE), Command.SCREEN, 37);
//#         cmdXMLConsole.setImg(MenuIcons.ICON_CONCOLE);
//#endif

//#ifdef DEBUG_CONSOLE
//#         cmdDebugConsole = new Command(SR.get(SR.MS_DEBUG_MENU), Command.SCREEN, 39);
//#         cmdDebugConsole.setImg(MenuIcons.ICON_CONCOLE);
//#endif

        cmdAccount = new Command(SR.get(SR.MS_ACCOUNT_), Command.SCREEN, 15);
        cmdAccount.setImg(MenuIcons.ICON_VCARD);

        cmdInfo = new Command(SR.get(SR.MS_ABOUT), Command.SCREEN, 80);
        cmdInfo.setImg(MenuIcons.ICON_CHECK_UPD);

        cmdMinimize = new Command(SR.get(SR.MS_APP_MINIMIZE), Command.SCREEN, 90);
        cmdMinimize.setImg(MenuIcons.ICON_FILEMAN);

        cmdQuit = new Command(SR.get(SR.MS_APP_QUIT), Command.SCREEN, 99);
        cmdQuit.setImg(MenuIcons.ICON_BUILD_NEW);
    }

    private static Command cmdActions;
    private static Command cmdStatus;
//#ifdef GRAPHICS_MENU
    private static Command cmdOptions;
    private static Command cmdMyService;

     private static Command cmdAlert;
//#ifdef XML_CONSOLE
//#       private static Command cmdXMLConsole;
//#endif
//#ifdef DEBUG_CONSOLE
//#       private static Command cmdDebugConsole;
//#endif

//#endif
//#ifdef ARCHIVE
    private static Command cmdArchive;
//#endif
    private static Command cmdAccount;
    private static Command cmdInfo;
    private static Command cmdMinimize;
    private static Command cmdQuit;

    public void commandState(){
//#ifdef MENU_LISTENER
        menuCommands.removeAllElements();
        cmdfirstList.removeAllElements();
        cmdsecondList.removeAllElements();
        cmdThirdList.removeAllElements();
//#endif

//#ifdef GRAPHICS_MENU
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

//#else
//#        addCommand(cmdTools);
//#
  //#ifndef MENU_LISTENER
  //#         addCommand(cmdActions);
  //#endif
//#
//#
//#
//#         addCommand(cmdStatus);
  //#ifndef WMUC
  //#ifdef MENU_LISTENER
//#         if (isLoggedIn())
  //#endif
//#             addCommand(cmdConference);
  //#endif
//#         addCommand(cmdAlert);
  //#ifdef ARCHIVE
  //#ifdef PLUGINS
  //#          if (midlet.BombusQD.sd.Archive)
  //#endif
//#             addCommand(cmdArchive);
  //#endif
  //#ifdef MENU_LISTENER
//#         if (isLoggedIn())
  //#endif
//#             addCommand(cmdAdd);
//#         addCommand(cmdAccount);
//#         addCommand(cmdInfo);
//#
//#         if (midlet.BombusQD.cf.allowMinimize)
//#             addCommand(cmdMinimize);
//#
//#         addCommand(cmdCleanAllMessages);
//#         if (phoneManufacturer!=Config.NOKIA_9XXX)
//#             addCommand(cmdQuit);
//#
//#
  //#ifdef MENU_LISTENER
//#         cmdActions.setImg(MenuIcons.ICON_ITEM_ACTIONS);
//#         cmdStatus.setImg(MenuIcons.ICON_STATUS);
//#
//#         cmdAlert.setImg(MenuIcons.ICON_NOTIFY);
  //#ifndef WMUC
//#         cmdConference.setImg(MenuIcons.ICON_CONFERENCE);
  //#endif
  //#ifdef ARCHIVE
//#         cmdArchive.setImg(MenuIcons.ICON_ARCHIVE);
  //#endif
//#         cmdAdd.setImg(MenuIcons.ICON_ADD_CONTACT);
//#         cmdTools.setImg(MenuIcons.ICON_SETTINGS);
//#         cmdAccount.setImg(MenuIcons.ICON_VCARD);
//#         cmdInfo.setImg(MenuIcons.ICON_CHECK_UPD);
//#         if (midlet.BombusQD.cf.allowMinimize)
//#             cmdMinimize.setImg(MenuIcons.ICON_FILEMAN);
//#         cmdCleanAllMessages.setImg(MenuIcons.ICON_CLEAN_MESSAGES);
//#         cmdQuit.setImg(MenuIcons.ICON_BUILD_NEW);
  //#endif
//#endif
    }


//#ifndef MENU
    public void commandAction(Command c, Displayable d){
//#ifdef AUTOSTATUS
        userActivity();
//#endif

//#ifdef GRAPHICS_MENU
        if (c == cmdActions) {
            showActionsMenu(this, getFocusedObject());
        }
           else if(c==cmdOptions) {
              display.setCurrent(new ConfigForm(display, this));
//#ifdef SERVICE_DISCOVERY
           } else if(c==cmdMyService) {
               new ServiceDiscovery(display, null, null, false);
//#endif
//#ifdef XML_CONSOLE
//#             } else if(c==cmdXMLConsole){
//#                 new XMLConsole(display,this);
//#endif
//#ifdef DEBUG_CONSOLE
//#           } else if(c==cmdDebugConsole){
//#                 new DebugConsole(display, this);
//#endif
         } else if (c==cmdMinimize) { cmdMinimize();  }

         else if (c==cmdAccount){ cmdAccount(); }
         else if (c==cmdStatus) { cmdStatus(); }
         else if (c==cmdAlert) { cmdAlert(); }
//#ifdef ARCHIVE
 	 else if (c==cmdArchive) { cmdArchive(); }
//#endif
         else if (c==cmdInfo) { cmdInfo(); }
         else if (c==cmdQuit) { cmdQuit(); }
//#else
//#         if (c==cmdActions) { cmdActions(); }
//#         else if (c==cmdMinimize) { cmdMinimize();  }
//#         else if (c==cmdAccount){ cmdAccount(); }
//#         else if (c==cmdStatus) { cmdStatus(); }
//#         else if (c==cmdAlert) { cmdAlert(); }
//#ifdef ARCHIVE
//# 	else if (c==cmdArchive) { cmdArchive(); }
//#endif
//#         else if (c==cmdInfo) { cmdInfo(); }
//#         else if (c==cmdTools) { cmdTools(); }
//#         else if (c==cmdCleanAllMessages) { cmdCleanAllMessages(); }
//#ifndef WMUC
//#         else if (c==cmdConference) { cmdConference(); }
//#endif
//#         else if (c==cmdQuit) { cmdQuit(); }
//#         else if (c==cmdAdd) { cmdAdd(); }
//#endif

    }
//#endif
//menu actions

    public void cmdMinimize() { BombusQD.getInstance().hideApp(true, null);  }
    public void cmdAccount(){ new AccountSelect(display, this, false,-1); }
    public void cmdStatus() { currentReconnect=0; new StatusSelect(display, this, null); }
    public void cmdAlert() { new AlertProfile(display, this); }
//#ifdef ARCHIVE
    public void cmdArchive() { new ArchiveList(display , -1, null, null); }
//#endif
    public void cmdInfo() { new info.InfoWindow(display, this); }

//#ifndef GRAPHICS_MENU
//#     public void cmdTools() { new RosterToolsMenu(display, this); }
//#endif

//#ifdef POPUPS
    public void cmdClearPopups() { VirtualList.getPopUp().clear(); }
//#endif
//#ifndef WMUC
   public void cmdConference() { if (isLoggedIn()) new Bookmarks(display, this, null); }
//#endif

    public void setProgress(String pgs,int percent){
        if (midlet.BombusQD.getInstance().s!=null){
             midlet.BombusQD.getInstance().s.setProgress(pgs, percent);
        }
	if (mainbar!=null)
             mainbar.setElementAt(pgs, 3);
        redraw();
    }

    public void setProgress(int percent){
        if (midlet.BombusQD.getInstance().s!=null)
            midlet.BombusQD.getInstance().s.setProgress(percent);
    }

    private int rscaler;
    private int rpercent;

    public void rosterItemNotify(){
        rscaler++;
        if (rscaler<4) return;
        rscaler=0;
        if (rpercent<100) rpercent++;
        if (midlet.BombusQD.getInstance().s!=null)
            midlet.BombusQD.getInstance().s.setProgress(rpercent);
    }

    // establishing connection process
    public void run(){
//#ifdef POPUPS
        //if (midlet.BombusQD.cf.firstRun) setWobbler(1, (Contact) null, SR.get(SR.MS_ENTER_SETTINGS);
//#endif
        setQuerySign(true);
	if (!doReconnect) {
            setProgress(25);
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
            myStatus=Constants.PRESENCE_OFFLINE;
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
        Msg m=new Msg(Constants.MESSAGE_TYPE_OUT, "local", "Info", s);
        messageStore(selfContact(), m);
        selfContact().getChatInfo().reEnumCounts();
//#ifdef DEBUG_CONSOLE
//#         midlet.BombusQD.debug.add(s,10);
//#endif
    }


    public void beginPaint() {
        reEnumerator.update();
        paintVContacts=vContacts;
    }

    public VirtualElement getItemRef(int Index){
        return (VirtualElement) paintVContacts.elementAt(Index);
    }

    public int getItemCount(){
        return paintVContacts.size();
    }

    public Object getEventIcon() {
        return transferIcon;
    }


    private final static Integer icon_msg = new Integer(RosterIcons.ICON_MESSAGE_INDEX);
    private final static Integer icon_progress = new Integer(RosterIcons.ICON_PROGRESS_INDEX);
    private Integer myStatusIcon = new Integer(myStatus);
    private static int myLatestStatus = 0;

    private void updateMainBar(){
        int profile=midlet.BombusQD.cf.profile;

        if(myStatus != myLatestStatus) myStatusIcon = new Integer(myStatus);

        Object en = (profile>0) ? new Integer(profile + RosterIcons.ICON_PROFILE_INDEX + 1):null;

        if(0 != messageCount) {
            mainbar.setElementAt(icon_msg, 0);
            mainbar.setElementAt(getHeaderString(),1);
        } else {
             mainbar.setElementAt(null, 0);
             mainbar.setElementAt(null, 1);
        }
        mainbar.setElementAt(querysign ? icon_progress : myStatusIcon, 2);
        mainbar.setElementAt(en, 5);

        myLatestStatus = myStatus;
        if (null != en) en = null;
        if (phoneManufacturer==Config.WINDOWS) {
            if (messageCount==0) setTitle("BombusQD");
            else setTitle("BombusQD "+getHeaderString());
        }
    }

    private final static StringBuffer header = new StringBuffer(0);

    public StringBuffer getHeaderString() {
       header.setLength(0);
       header.append(' ');
         if(0 < highliteMessageCount) header.append(highliteMessageCount).append('/');
        header.append(messageCount);
        header.append(' ');
       return header;
    }


    public void setEventIcon(Object icon){
        transferIcon=icon;
        mainbar.setElementAt(icon, 7);
        redraw();
    }


    public void countMsgs(){
      countNewMsgs();
    }

    boolean countNewMsgs() {
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
           new AlertBox(SR.get(SR.MS_UNREAD_MESSAGES)+": "+messageCount, SR.get(SR.MS_SURE_DELETE), display, this, false) {
                public void yes() { cleanAllMessages(); }
                public void no() { }
            };
        } else {
            cleanAllMessages();
        }
    }

     public void cleanAllMessages(){
         contactList.cleanAllMessages();
         highliteMessageCount=0;
         messageCount=0;
         midlet.BombusQD.sd.roster.showRoster();
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
                contact.setStatus(Constants.PRESENCE_OFFLINE);
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
             if (cg.inRoom==false) {
                boolean removeGroup = true;
                hC = hContacts.size() - 1;
//#ifdef DEBUG_CONSOLE
//#                 midlet.BombusQD.debug.add("::cleanupGroup->cg.inRoom->" + cg.inRoom + "(destroyContacts)",10);
//#endif
                for (int index = hC; index >= 0; --index) {
                    Contact contact=(Contact)hContacts.elementAt(index);
                    if (0 == contact.getNewMsgsCount()) {
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
            if ( contact.origin>Constants.ORIGIN_ROSTERRES
                    && (Constants.PRESENCE_OFFLINE <= contact.status)
                    && !contact.hasNewMsgs()
                    && contact.origin!=Constants.ORIGIN_GROUPCHAT) {
                contact.destroy();
                contactList.removeContact(contact);
                setModified();
             }
        }
        if (0 == g.getOnlines() && !(g instanceof ConferenceGroup)) {
            if (g.type == Groups.TYPE_MUC) {
                contactList.removeGroup(g);
                setModified();
             }
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
        int type=0;
        //verifying from attribute as in RFC3921/7.2
        String from=data.getAttribute("from");
        if (from!=null) {
            Jid fromJid=new Jid(from);
            if (fromJid.hasResource())
                if (!myJid.equals(fromJid, true)) return false;
         }
        Vector cont=(null!=q)?q.getChildBlocks():null;
        String group,bareJid;

        try {
          transports = new Vector(0);
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
        if(midlet.BombusQD.cf.networkAnnotation){
           //XEP-0145: Annotations
           JabberDataBlock getNotes = new Iq(null, Iq.TYPE_GET, "getnotes");
           getNotes.addChildNs("query", "jabber:iq:private").addChildNs("storage", "storage:rosternotes");
           theStream.send(getNotes);
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

   private Vector transports = new Vector(0);

   public void connectTransport(){
            if(midlet.BombusQD.cf.isStatusFirst && firstStatus!=-1) {
               if (firstStatus==5) sendPresence(Constants.PRESENCE_INVISIBLE, null);
               else sendPresence(firstStatus, null);

               midlet.BombusQD.cf.isStatusFirst=false;
               firstStatus=-1;
             } else {
                 if (midlet.BombusQD.cf.loginstatus==5) sendPresence(Constants.PRESENCE_INVISIBLE, null);
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
                Contact c = null;
                for (f = 1; f < size; f++) {
                        left=(IconTextElement)sortVector.elementAt(f);
                        right=(IconTextElement)sortVector.elementAt(f-1);
                        if ( left.compare(right) >=0 ) continue;
                        i = f-1;
                        while (i>=0){
                           c = (Contact)sortVector.elementAt(i);
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
        int status = Constants.PRESENCE_OFFLINE;
        if (subscr.equals("none")) status = Constants.PRESENCE_UNKNOWN;
        if (ask) status = Constants.PRESENCE_ASK;
        if (subscr.equals("remove")) status = -1;
        Jid J=new Jid(jid);
        Contact contact = contactList.findContact(contactList.contacts, J, false); // search by bare jid
        if (null == contact) {
            if (status < 0) return;
            contact = new Contact(nick, jid, Constants.PRESENCE_OFFLINE, null);
            addContact(contact);
            Contact c = contact;
            Group group = contactList.getGroup( c.jid.isTransport() ? SR.get(SR.MS_TRANSPORTS) : grpName);

            if (null == group) {
                group = new Group(grpName, Groups.TYPE_COMMON);
                contactList.addGroup(group);
            }

            if (c.origin == Constants.ORIGIN_PRESENCE) c.origin = Constants.ORIGIN_ROSTERRES;
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
                if (c.origin == Constants.ORIGIN_PRESENCE || c.origin == Constants.ORIGIN_ROSTERRES) c.origin = Constants.ORIGIN_CLONE;
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
        if (contact.origin == Constants.ORIGIN_CLONE) contact.origin = Constants.ORIGIN_ROSTERRES;
     }


//#ifndef WMUC

    public final ConferenceGroup initMuc(final String from, String joinPassword){
 //#ifdef AUTOSTATUS
         if (autoAway) {
             ExtendedStatus es=sl.getStatus(oldStatus);
             messageActivity();
             es = null;
         }
 //#endif
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
        if (Constants.PRESENCE_OFFLINE > mucContact.status) return grp;
        mucContact.status = Constants.PRESENCE_ONLINE;
        mucContact.commonPresence = true;
        grp.conferenceJoinTime = Time.utcTimeMillis();
        addContact(grp.getSelfContact(from));
        grp.collapsed = true; //test
        sortRoster(mucContact);
         return grp;
     }

     private final MucContact mucContact(String from){
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


     public final Contact getContact(final String jid, boolean createInNIL) {
        Jid J = new Jid(jid);
        Contact c = findContact(J, true);
        if (null != c) return c;
         c = findContact(J, false);
         Group grp = null;
         if (c==null) {
            if (!createInNIL) {
                J = null;
                return null;
            }
            c = new Contact(null, jid, Constants.PRESENCE_OFFLINE, "none" ); /*"not-in-list"*/
            c.origin=Constants.ORIGIN_PRESENCE;
            c.setGroup(contactList.groups.getGroup(Groups.TYPE_NOT_IN_LIST));
            addContact(c);
         } else {
            if (c.origin == Constants.ORIGIN_ROSTER) {
                c.origin = Constants.ORIGIN_ROSTERRES;
                c.setStatus(Constants.PRESENCE_OFFLINE);

                if(c.group.type == Groups.TYPE_SELF) {
                    c = new Contact(null, jid, Constants.PRESENCE_OFFLINE, "none" );
                    c.setGroup(contactList.groups.getGroup(Groups.TYPE_SELF));
                    addContact(c,true);
                } else {
                 c.jid = J;
                 c.setNick(c.getNick());
                }
            } else {
                c = clone(c, J, Constants.PRESENCE_OFFLINE);
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
         clone.origin=Constants.ORIGIN_CLONE;
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

     public final Contact findContact(final Jid j, final boolean compareResources) {
        return contactList.findContact(contactList.contacts, j, compareResources);
     }

    int firstStatus = -1;

    public void sendPresence(int newStatus, String message) {
        if (newStatus!=Constants.PRESENCE_SAME)
            myStatus=newStatus;
//#ifdef AUTOSTATUS
        messageActivity();
//#endif
	if (message!=null) myMessage=message;

        setQuerySign(false);

        if (myStatus!=Constants.PRESENCE_OFFLINE) {
             lastOnlineStatus=myStatus;
        }

        // reconnect if disconnected
        if (myStatus!=Constants.PRESENCE_OFFLINE && theStream==null ) {
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
            if (myStatus==Constants.PRESENCE_OFFLINE  && !midlet.BombusQD.cf.collapsedGroups)
                contactList.groups.queryGroupState(false);
            // send presence
            ExtendedStatus es = sl.getStatus(myStatus);
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
        if (myStatus==Constants.PRESENCE_OFFLINE) {
            try {
                theStream.close(); // sends </stream:stream> and closes socket
            } catch (Exception e) {
                //e.printStackTrace();
            }
            contactList.setOfflineStatus();
            theStream=null;
            System.gc();
//#ifdef AUTOSTATUS
            autoAway=false;
            autoXa=false;
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
        ExtendedStatus es= sl.getStatus(status);
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
         ConferenceGroup confGroup;
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

//#ifndef WMUC
     public void multicastConferencePresence(int myStatus, String myMessage, int myPriority) {
        //if (!cf.autoJoinConferences) return; //requested to disable
        if (myStatus==Constants.PRESENCE_INVISIBLE) return; //block multicasting presence invisible

        Vector hContacts = contactList.contacts;
        for (int index = hContacts.size() - 1; index >= 0; --index) {
            Contact c = (Contact) hContacts.elementAt(index);
            if (Constants.ORIGIN_GROUPCHAT != c.origin) continue;
            if (!((MucContact)c).commonPresence) continue; // stop if room left manually
            ConferenceGroup confGroup=(ConferenceGroup)c.group;
            if (!confGroup.inRoom) continue; // don`t reenter to leaved rooms

            Contact myself=confGroup.selfContact;
            if (Constants.PRESENCE_OFFLINE <= c.status) {
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

            ExtendedStatus es= sl.getStatus(myStatus);
            switch (myStatus){
                case Constants.PRESENCE_CHAT: presence.addChild("show", Constants.PRS_CHAT);break;
                case Constants.PRESENCE_AWAY: presence.addChild("show", Constants.PRS_AWAY);break;
                case Constants.PRESENCE_XA: presence.addChild("show", Constants.PRS_XA);break;
                case Constants.PRESENCE_DND: presence.addChild("show", Constants.PRS_DND);break;
            }
            if (es.getPriority()!=0)
                presence.addChild("priority",Integer.toString(es.getPriority()));
            if (es.getMessage()!=null)
                presence.addChild("status", StringUtils.toExtendedString(es.getMessage()));
        } else if (conference) {
            ExtendedStatus es= sl.getStatus(Constants.PRESENCE_OFFLINE);
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
        try {
//#ifndef WMUC
            boolean groupchat=to.origin==Constants.ORIGIN_GROUPCHAT;
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
                   while(i<len){ i++;
                      for(int j=0;j<charLen;j++){
                        if(body.charAt(i)==chars.charAt(j)) newLen++;
                      }
                   }
                  if(len==newLen){
                    String postNum = body.substring(1,len);
                    JabberDataBlock request = new Iq("juick@juick.com/Juick", Iq.TYPE_GET, (postNum.length()==0) ? "lastmsgs" :"cmts_"+postNum );
                    JabberDataBlock query = request.addChildNs("query","http://juick.com/query#messages");
                    query.setAttribute("mid", (body=="#+") ? "" : postNum );
                    theStream.send(request);
                    playNotify(SOUND_OUTGOING);
                    return;
                  }
                }
             }
//#endif

//#ifdef AUTOSTATUS
            if (autoAway) {
                    ExtendedStatus es=sl.getStatus(oldStatus);
                    String ms=es.getMessage();
                    sendPresence(oldStatus, ms);
                    autoAway=false;
                    autoXa=false;
                    myStatus=oldStatus;
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
//#            if(body!=null){
//# 
//#                if(midlet.BombusQD.cf.module_classicchat){
//#                  if(!groupchat) {
//#                  //forfix
//#                  Msg mmm = new Msg(Constants.MESSAGE_TYPE_OUT,"Me",null,"Me: " + body);
//#                  to.addMessage(mmm);
//#                  StringUtils.addClassicChatMsg("Me: "+body,midlet.BombusQD.cf.width_classic,to.scroller);
//#                  }else
//#                  {
//#                    Msg mmm;
//#                    if(body.startsWith("/me")){
//#                      mmm = new Msg(Constants.MESSAGE_TYPE_OUT,"Me",null,"***Me " + body.substring(3,body.length()));
//#                      StringUtils.addClassicChatMsg("***Me " + body.substring(3,body.length()),midlet.BombusQD.cf.width_classic,to.scroller);
//#                    }else {
//#                      mmm = new Msg(Constants.MESSAGE_TYPE_OUT,"Me",null,"Me: " + body);
//#                      StringUtils.addClassicChatMsg("Me: " + body,midlet.BombusQD.cf.width_classic,to.scroller);
//#                     }
//#                     mmm=null;
//#                   }
//#                 }
//#          }
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
//#ifdef AUTOSTATUS
        messageActivity();
//#endif
    }

    private void sendDeliveryMessage(Contact c, String id) {
        if (!midlet.BombusQD.cf.eventDelivery) return;
        if (myStatus==Constants.PRESENCE_INVISIBLE) return;
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
        myStatus=Constants.PRESENCE_OFFLINE;
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
        theStream.addBlockListener(new Captcha(display));
//#endif

        playNotify(SOUND_CONNECTED);
        if (doReconnect) {
            querysign=doReconnect=false;
            sendPresence(myStatus, null);
            return;
        }
        //
        rpercent=50;

        if (midlet.BombusQD.sd.account.isMucOnly()) {
            setProgress(SR.get(SR.MS_CONNECTED),100);
            try {
                setModified();
                reEnumRoster();
            } catch (Exception e) { }
            setQuerySign(false);
            doReconnect=false;
            if (midlet.BombusQD.getInstance().s!=null) midlet.BombusQD.getInstance().s.close(); // display.setCurrent(this);

            //query bookmarks
            theStream.addBlockListener(new BookmarkQuery(BookmarkQuery.LOAD));
        } else {
            JabberDataBlock qr=new IqQueryRoster();
            setProgress(SR.get(SR.MS_ROSTER_REQUEST), 49);
            theStream.send( qr );
            qr=null;
        }
    }

    public void bindResource(String myJid) {
        Contact self=selfContact();
        self.jid=this.myJid=new Jid(myJid);
    }

//#if BREDOGENERATOR
//#     public Vector baseWord = new Vector();
//#     private final static String[] badChars= {
//#         "]",           "\"",         "&",
//#         "'",           "/",          ":",
//#         "<",           ">",          "@",
//#         "\\",           "|",          "*",
//#         "^",           "#",          "-",
//#         "{",           "}",          "(",
//#         ")",           "[",          "  "
//#     };
//#     private final static String[] newChars= {
//#         "","","",
//#         "","","",
//#         "","","",
//#         "","","",
//#         "","","",
//#         "","","",
//#         "",""," "
//#     };
//#
//#     public void bredoGEN(String body){
//#         //String body = "hello and welcome";
//#         //System.out.println("BODY "+" ".concat(body).concat(" ")); //gbn dfskj : bdkn
//#         body = " ".concat(body).concat(" ");
//#         char[] bb = body.toCharArray();
//#         StringBuffer add = new StringBuffer();
//#         for (int i=0; i<bb.length;i++) {
//#             if(bb[i]!=' '){
//#               add.append(bb[i]);
//#               //System.out.println(bb[i]);
//#             }
//#             else{
//#              if(add.toString().length()>0){
//#                String txtelement = add.toString();
//#                baseWord.addElement(txtelement);
//#                add = new StringBuffer();
//#              }
//#            }
//#         }
//#         //return "";
//#     }
//#
//#    Random rand = new Random();
//#
//#    public String generateWord(){
//#       int i=0;
//#       int size =  baseWord.size();
//#       int max_words = Math.abs(rand.nextInt()) % 15;
//#       if(max_words==0){
//#           max_words = Math.abs(rand.nextInt()) % 15;
//#       }
//#       StringBuffer sb = new StringBuffer();
//#       for (int k = 0; k<max_words; k++) {
//#          i = Math.abs(rand.nextInt()) % size;
//#          sb.append(baseWord.elementAt(i)+" ");
//#       }
//#
//#       String txtelement = sb.toString();
//#
//#       for (int k=0; k<badChars.length;k++) {
//#           txtelement=StringUtils.stringReplace(txtelement,badChars[k],newChars[k]);
//#       }
//#       //System.out.println(Integer.toString(baseWord.size()) + ": ("+txtelement+")");
//#      return txtelement.toLowerCase();
//#    }
//#endif



//#if FILE_IO
    public void cashePhoto(VCard vcard,Contact c){
       if(vcard.getPhoto()==null) return;
        StringBuffer nickDate=new StringBuffer(0);
        if (c instanceof MucContact){
           nickDate.append("muc_"+c.getNick());
        }else{
           nickDate.append("roster_"+c.bareJid);
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
              int newW=photoImg.getWidth();
              int newH=photoImg.getHeight();
                while(newW > midlet.BombusQD.cf.maxAvatarWidth || newH > midlet.BombusQD.cf.maxAvatarHeight){
                  newW -= (newW*10)/100;
                  newH -= (newH*10)/100;
                }
              c.img_vcard = resizeImage(photoImg,newW,newH);
              c.avatar_width=newW;
              c.avatar_height=newH;
              //errorLog("AVATAR APPLY: " + photoImg + "(" + c.img_vcard + ")");
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


    public void setImageAvatar(Contact c,Image photoImg){
        int newW=photoImg.getWidth();
        int newH=photoImg.getHeight();
        while(newW>midlet.BombusQD.cf.maxAvatarWidth || newH>midlet.BombusQD.cf.maxAvatarHeight){
            newW-=(newW*10)/100;
            newH-=(newH*10)/100;
        }
        c.avatar_width=newW;
        c.avatar_height=newH;
        c.img_vcard=resizeImage(photoImg,newW,newH);
    }

   public int blockArrived( JabberDataBlock data ) { //fix
        try {
            String from = data.getAttribute("from");
            String type = data.getTypeAttribute();
            String id = data.getAttribute("id");

            if( data instanceof Iq ) {
//#ifdef JUICK.COM
                if(from!=null){
                  if(from.indexOf("juick@juick.com")>-1) {
                    Msg m=new Msg(Constants.MESSAGE_TYPE_JUICK, "juick@juick.com/Juick", null, null);
                    m = juick.jm().getMsg(m,data);
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

//#if FILE_IO
                                if(midlet.BombusQD.cf.autoSaveVcard) {//check img in fs?
                                    cashePhoto(vcard,c);
                                }
//#endif
                         if (c != null) {
                             c.vcard = vcard;
                             if (display.getCurrent() instanceof VirtualList) {
                                 if (c.getGroupType() == Groups.TYPE_SELF) {
                                     new VCardEdit(display, this, vcard);
                                 } else {
                                     new VCardView(display, this, c);
                                 }
                             }
                         } else {
                             new VCardView(display, this, vcard);
                         }
                         vcard = null;
                         return JabberBlockListener.BLOCK_PROCESSED;
                    }

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
                                setImageAvatar(c,photoImg);
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
                 }

                if ( type.equals( "result" ) ) {
                    //fix
                    if(id.equals("delacc")) {
                        JabberDataBlock reg=data.findNamespace("query","jabber:iq:register");
                        JabberDataBlock remove=reg.getChildBlock("remove");
                        if(remove!=null){
                          new CommandForm(display,parentView,4,SR.get(SR.MS_ACCOUNT_DELETED),from,null);
                        }
                       redraw();
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
                    }

                    if(id.equals("changemypass")) {
                         JabberDataBlock reg=data.findNamespace("query","jabber:iq:register");
                         redraw();
                         new CommandForm(display,parentView,3,SR.get(SR.MS_CHANGE_PASSWORD),"", reg.getChildBlockText("password"));
                    }

//#ifdef POPUPS
                    if(id.equals("destroyroom"))   {
                         setWobble(1,null,from + " deleted!");
                         redraw();
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
                    }


                    if (id.startsWith("statistic")) {
                                 JabberDataBlock query = data.findNamespace("query","http://jabber.org/protocol/stats");
                                 Vector children = query.getChildBlocks();

                                 CommandForm cmd = new CommandForm(midlet.BombusQD.getInstance().display, this , 5 , "", null, null);
                                 cmd.setParentView(from, this);

                                  try {
                                      for (int i = 0; i < children.size(); ++i){
                                          JabberDataBlock value = (JabberDataBlock)children.elementAt(i);
                                          MultiLine line = new MultiLine(value.getAttribute("name"), value.getAttribute("value"), cmd.superWidth);
                                          line.setSelectable(true);                                          
                                          cmd.addControl(line);
                                      }
                                 } catch (Exception e) {}
                                 query = null;
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

                        if (midlet.BombusQD.getInstance().s!=null){
                            midlet.BombusQD.getInstance().s.close();
                        }
//#ifndef WMUC
                        //query bookmarks
                        if (bookmarks==null) theStream.addBlockListener(new BookmarkQuery(BookmarkQuery.LOAD));
//#endif
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

                Contact c = getContact(from, (midlet.BombusQD.cf.notInListDropLevel != NotInListFilter.DROP_MESSAGES_PRESENCES || groupchat));
                //not-in-list message dropped
                if (c == null) {
                    return JabberBlockListener.BLOCK_REJECTED;
                }

                boolean highlite=false;

                String body=message.getBody().trim();
                String oob=message.getOOB();

//#if BREDOGENERATOR
//#                 if(midlet.BombusQD.cf.bredoGen) { bredoGEN(body); }
//#endif

                if (oob!=null) body+=oob;
                if (body.length()==0) body=null;

                String subj=message.getSubject().trim();
                if (subj.length()==0) subj=null;


                long tStamp=message.getMessageTime();

		byte mType=Constants.MESSAGE_TYPE_IN;

//#if BREDOGENERATOR
//#                 String gen="";
//#                 if(midlet.BombusQD.cf.bredoGen==true) gen = generateWord();
//#endif

                if (groupchat) {
                    if (subj!=null) { // subject
                        if (body==null) body=name+" "+SR.get(SR.MS_HAS_SET_TOPIC_TO)+": "+subj;
                        if (!subj.equals(c.getStatus())) {
                            c.setStatus(subj); // adding secondLine to conference
                            highlite=true;
                        } else {
                            return JabberBlockListener.BLOCK_PROCESSED;
                        }
                        subj=null;
                        start_me=-1;
                        mType=Constants.MESSAGE_TYPE_SUBJ;
                    }
                } else if (type!=null){
                    if (type.equals("error")) {
                        body=SR.get(SR.MS_ERROR_) + XmppError.findInStanza(message).toString();
                    } else if (type.equals("headline")) {
                        mType=Constants.MESSAGE_TYPE_HEADLINE;
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
                                ConferenceGroup invConf=contactList.getConferenceGroup(from);
                                body=XmppError.decodeStanzaError(message).toString(); /*"error: invites are forbidden"*/
                            } else {
                                String room=from+'/'+midlet.BombusQD.sd.account.getNickName();
                                String password=xmlns.getChildBlockText("password");

                                ConferenceGroup invConf=initMuc(room, password);


                                invConf.confContact.commonPresence=false;


                                if (invConf.selfContact.status==Constants.PRESENCE_OFFLINE)
                                    invConf.confContact.status=Constants.PRESENCE_OFFLINE;


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
                        if(midlet.BombusQD.cf.module_classicchat==false) {
                            b.append("<nick>");
                            b.append(name);
                            b.append("</nick>");
                        }else{
                            b.append(name.trim());
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

                if (type.equals("chat") && myStatus!=Constants.PRESENCE_INVISIBLE) {
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
                m.MucChat = groupchat;

//#ifdef JUICK.COM
                if(from.indexOf("juick@juick.com")>-1 || data.findNamespace("juick",juick.NS_MESSAGE)!=null) {
                    m = juick.jm().getMsg(m,data);
                    if(m==null) {
                       m = new Msg(mType, from.trim(), subj, body.toString());
                    } else {
                       c = getContact(m.from, (midlet.BombusQD.cf.notInListDropLevel!=NotInListFilter.DROP_MESSAGES_PRESENCES));
                    }
                }
//#endif
                if (tStamp!=0) m.dateGmt=tStamp;
//#ifndef WMUC
                if (m.body.indexOf(SR.get(SR.MS_IS_INVITING_YOU))>-1) m.dateGmt=0;
                if (groupchat) {
                    ConferenceGroup mucGrp=(ConferenceGroup)c.group;
                    if (mucGrp.selfContact.getJid().equals(message.getFrom())) {
                        m.messageType=Constants.MESSAGE_TYPE_OUT;
                        m.unread=false;
                        m.highlite = false;
                    } else {
//#ifdef LIGHT_CONTROL
                        CustomLight.message();
//#endif
                        if (m.dateGmt<= ((ConferenceGroup)c.group).conferenceJoinTime)
                            m.messageType=Constants.MESSAGE_TYPE_HISTORY;
                        // highliting messages with myNick substring
	                String myNick=mucGrp.selfContact.getNick();
                        String myNick_=myNick+" ";
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
	                myNick=null; myNick_=null; _myNick=null;
                        //TODO: custom highliting dictionary
                        m.highlite=highlite;
                    }
                    m.from=name;
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
//#if BREDOGENERATOR
//#                 if(midlet.BombusQD.cf.bredoGen==true&&groupchat==false){
//#                     sendMessage(c,"bredogen", gen , null , null , false);
//#                     messageStore(c, new Msg(mType, from, null, gen));
//#                 }
//#endif
                  return JabberBlockListener.BLOCK_PROCESSED;
            } else if( data instanceof Presence ) {  // If we've received a presence
                if (myStatus==Constants.PRESENCE_OFFLINE)
                    return JabberBlockListener.BLOCK_REJECTED;

                Presence pr = (Presence) data;

                String Prtext = pr.getPresenceText();
                byte ti = pr.getTypeIndex();
//#ifdef CONSOLE
//#                 //midlet.BombusQD.debug.add("::PRESENCE "+data.toString(),10);
//#endif

                if (ti != Constants.PRESENCE_OFFLINE) {
                    if (midlet.BombusQD.cf.auto_queryPhoto) {
                        Contact c = getContact(from, true);
                        if (c.hasPhoto == false && c.img_vcard == null) {
                            JabberDataBlock req = new Iq(c.bareJid, Iq.TYPE_GET, "avcard_get" + c.getJid());
                            req.addChildNs("vCard", "vcard-temp");
                            theStream.send(req);
                        }
                    }
                }
//#ifndef WMUC
            JabberDataBlock xmuc=pr.findNamespace("x", "http://jabber.org/protocol/muc#user");
            if (xmuc==null) xmuc=pr.findNamespace("x", "http://jabber.org/protocol/muc"); //join errors

            int priority = pr.getPriority();

            if (xmuc!=null) {//MUC only

                    JabberDataBlock status=xmuc.getChildBlock("status");
                    if(status!=null) {
                       int index = from.indexOf('/');
                       int statusCode=Integer.parseInt( status.getAttribute("code") );
                       if(statusCode==201) {
                           new QueryConfigForm(display,from.substring(0,index));
                       }
                    }

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
                            if (pr.getTypeIndex() != Constants.PRESENCE_ERROR) {
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
                                conferenceMessage = new Msg(Constants.MESSAGE_TYPE_ERROR, name, null, chatPres );
                                addPresenceMsg = true;
                         } else conferenceMessage = new Msg(Constants.MESSAGE_TYPE_PRESENCE, name, null, chatPres );

                        if(addPresenceMsg) {
                            conferenceMessage.color = conferenceContact.getMainColor();
                            messageStore(room, conferenceMessage);
                        }

                        conferenceMessage = new Msg( (ti==Constants.PRESENCE_AUTH ||
                              ti==Constants.PRESENCE_AUTH_ASK)?Constants.MESSAGE_TYPE_AUTH : Constants.MESSAGE_TYPE_PRESENCE, from, null, Prtext );

                        if (ti==Constants.PRESENCE_ERROR) {
                           StringBuffer sb = new StringBuffer(0);
                             sb.append(SR.get(SR.MS_ERROR_))
                               .append('-')
                               .append('>')
                               .append(XmppError.findInStanza(pr).toString());
                             conferenceMessage = new Msg(Constants.MESSAGE_TYPE_ERROR, name, null, sb.toString());
                             if(!chatPres.startsWith("remote-server-not-found")) messageStore(room, conferenceMessage);
                           sb = new StringBuffer(0);
                           sb=null;
                        } else {
                           messageStore(conferenceContact, conferenceMessage);
                        }
                        if (ti==Constants.PRESENCE_OFFLINE)  {
                            //автоочистка вышедших из конфы
                            if (!conferenceContact.active() && BombusQD.cf.cleanConfContacts) {
                                conferenceContact.destroy();
                                contactList.removeContact(conferenceContact);
                                setModified();
                            } else {
                                conferenceContact.setIncoming(Constants.INC_NONE);
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

//#ifdef FILE_IO
                        if(midlet.BombusQD.cf.autoload_FSPhoto) {
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
//#ifdef FILE_IO
                    if(midlet.BombusQD.cf.autoload_FSPhoto) {
                        loadAvatar(from, false);
                    }
//#endif

                    Contact c=null;
                    //System.out.println("FROM:"+from);
                    Msg m=new Msg( (ti==Constants.PRESENCE_AUTH ||
                         ti==Constants.PRESENCE_AUTH_ASK)?Constants.MESSAGE_TYPE_AUTH : Constants.MESSAGE_TYPE_PRESENCE, from, null, Prtext );
                     if (ti==Constants.PRESENCE_AUTH_ASK) {
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
                             messageStore(c, new Msg(Constants.MESSAGE_TYPE_AUTH, from, null, SR.get(SR.MS_AUTH_AUTO)));
                         }
                    } else {
                        // processing presences
                        boolean enNIL= midlet.BombusQD.cf.notInListDropLevel > NotInListFilter.DROP_PRESENCES;

                        c=getContact(from, enNIL);
                        if (c==null) return JabberBlockListener.BLOCK_REJECTED; //drop not-in-list presence
                        if (pr.getAttribute("ver")!=null) c.version=pr.getAttribute("ver");  // for bombusmod only
                        if (pr.getTypeIndex()!=Constants.PRESENCE_ERROR) {
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
                    if (c.getNick()==null && c.status<=Constants.PRESENCE_DND) {
                        JabberDataBlock nick = pr.findNamespace("nick", "http://jabber.org/protocol/nick");
                        if (nick!=null) c.setNick(nick.getText()); nick=null;

                    }
                    if ((ti==Constants.PRESENCE_ONLINE || ti==Constants.PRESENCE_CHAT) && notifyReady(-111)) {
//#if USE_ROTATOR
                        if (midlet.BombusQD.cf.notifyBlink) {
                            c.setNewContact();
                        }
//#endif
                        if (midlet.BombusQD.cf.notifyPicture) {
                            if (c.getGroupType()!=Groups.TYPE_TRANSP)
                                c.setIncoming(Constants.INC_APPEARING);
                        }
                    }
                    if (ti==Constants.PRESENCE_OFFLINE)  {
                        c.setIncoming(Constants.INC_NONE);
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
                        if (ti==Constants.PRESENCE_OFFLINE)
                            setTicker(c, SR.getPresence(Constants.PRS_OFFLINE));
                        else if (ti==Constants.PRESENCE_ONLINE)
                            setTicker(c, SR.getPresence(Constants.PRS_ONLINE));
//#endif
                        if ((ti==Constants.PRESENCE_ONLINE || ti==Constants.PRESENCE_CHAT
                                || ti==Constants.PRESENCE_OFFLINE) && (c.getGroupType()!=Groups.TYPE_TRANSP) && (c.getGroupType()!=Groups.TYPE_IGNORE))
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
     public final int getConferenceColor(int status) {
         switch (status) {
             case Constants.PRESENCE_CHAT: return ColorTheme.getColor(ColorTheme.CONTACT_CHAT);
             case Constants.PRESENCE_AWAY: return ColorTheme.getColor(ColorTheme.CONTACT_AWAY);
             case Constants.PRESENCE_XA: return ColorTheme.getColor(ColorTheme.CONTACT_XA);
             case Constants.PRESENCE_DND: return ColorTheme.getColor(ColorTheme.CONTACT_DND);
         }
         return ColorTheme.getColor(ColorTheme.CONTACT_DEFAULT);
     }

     private final static String getAffiliationLocale(int aff) {
         switch (aff) {
             case Constants.AFFILIATION_NONE: return SR.get(SR.MS_AFFILIATION_NONE);
             case Constants.AFFILIATION_MEMBER: return SR.get(SR.MS_AFFILIATION_MEMBER);
             case Constants.AFFILIATION_ADMIN: return SR.get(SR.MS_AFFILIATION_ADMIN);
             case Constants.AFFILIATION_OWNER: return SR.get(SR.MS_AFFILIATION_OWNER);
         }
         return null;
     }

     private final static String getRoleLocale(int rol) {
         switch (rol) {
             case Constants.ROLE_VISITOR: return SR.get(SR.MS_ROLE_VISITOR);
             case Constants.ROLE_PARTICIPANT: return SR.get(SR.MS_ROLE_PARTICIPANT);
             case Constants.ROLE_MODERATOR: return SR.get(SR.MS_ROLE_MODERATOR);
         }
         return null;
     }

     public void testMeOffline(MucContact mc, ConferenceGroup grp, boolean isKick) {
          if ( grp.selfContact == mc ) {
             if(isKick) display.setCurrent(midlet.BombusQD.sd.roster);
             midlet.BombusQD.sd.roster.roomOffline(grp, true);
          }
     }

     private static StringBuffer mucContactBuf = new StringBuffer(0);
     private String processPresence(MucContact mc, JabberDataBlock xmuc, Presence presence, String Prtext) {
//#ifdef DEBUG_CONSOLE
//#           midlet.BombusQD.debug.add("::role: processPresence", 10);
//#           midlet.BombusQD.debug.add("::role: processPresence->affiliation.." +  mc.affiliation, 10);
//#           midlet.BombusQD.debug.add("::role: processPresence->role.." +  mc.role, 10);
//#endif

         String affiliation = mc.affiliation;
         String role = mc.role;
         String from = mc.jid.getJid();
         byte presenceType=presence.getTypeIndex();

         if (presenceType==Constants.PRESENCE_ERROR) return StringUtils.processError(presence, presenceType, (ConferenceGroup)mc.group, mc);

         JabberDataBlock item=xmuc.getChildBlock("item");

         byte roleCode = Constants.ROLE_PARTICIPANT;
         byte affiliationCode = Constants.AFFILIATION_NONE;

         String tempRole = item.getAttribute("role");
         if (tempRole.equals("visitor")) {
             roleCode = Constants.ROLE_VISITOR;
         } else if (tempRole.equals("participant")) {
             roleCode = Constants.ROLE_PARTICIPANT;
         } else if (tempRole.equals("moderator")) {
             roleCode = Constants.ROLE_MODERATOR;
         }

         String tempAffiliation = item.getAttribute("affiliation");
         if (tempAffiliation.equals("owner")) {
             affiliationCode = Constants.AFFILIATION_OWNER;
         } else if (tempAffiliation.equals("admin")) {
             affiliationCode = Constants.AFFILIATION_ADMIN;
         } else if (tempAffiliation.equals("member")) {
             affiliationCode = Constants.AFFILIATION_MEMBER;
         } else if (tempAffiliation.equals("none")) {
             affiliationCode = Constants.AFFILIATION_NONE;
         }

         boolean roleChanged = !tempRole.equals(role);
         boolean affiliationChanged = !tempAffiliation.equals(affiliation);
 //#ifdef DEBUG_CONSOLE
//#          midlet.BombusQD.debug.add("::role: " +  role  + "/"  + tempRole +  "("  + roleChanged +  ")"   +
//#                  "\naff:" +  affiliation +  "/" +  tempAffiliation +  "("  + affiliationChanged  + ")", 10);
//#endif

         mc.affiliation = tempAffiliation;
         mc.role = tempRole;

         mc.roleCode = roleCode;
         mc.affiliationCode = affiliationCode;

         tempRole=null;
         tempAffiliation=null;

         mc.setSortKey(mc.getNick());
         switch (roleCode) {
             case Constants.ROLE_MODERATOR:
                 mc.transport = RosterIcons.ICON_MODERATOR_INDEX;
                 mc.key0 = Constants.GROUP_MODERATOR;
                 break;
             case Constants.ROLE_VISITOR:
                 mc.transport = RosterIcons.getInstance().getTransportIndex("vis");
                 mc.key0 = Constants.GROUP_VISITOR;
                 break;
             default:
                 mc.transport = mc.affiliation.equals("member")? 0 : RosterIcons.getInstance().getTransportIndex("vis");
                 mc.key0 = (mc.affiliation.equals("member")?Constants.GROUP_MEMBER:Constants.GROUP_PARTICIPANT);
         }

         JabberDataBlock statusBlock = xmuc.getChildBlock("status");
         int statusCode = 0;
         //try {
         if(null != statusBlock) {
             String statusCodeStr = statusBlock.getAttribute("code");
             if (null != statusCodeStr ) {
                 statusCode = Integer.parseInt( statusCodeStr );
                 statusBlock = null;
             }
         }
         //} catch (Exception e) { }

         mucContactBuf = new StringBuffer(0);
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
         } else if (presenceType==Constants.PRESENCE_OFFLINE) {
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
                     presenceType=Constants.PRESENCE_ERROR;
                 case 307: //kick
                     mucContactBuf.append((statusCode==301)? SR.get(SR.MS_WAS_BANNED) : SR.get(SR.MS_WAS_KICKED) );
//#ifdef POPUPS
                     if (  ((ConferenceGroup)mc.group).selfContact  == mc ) {
                         setWobble(3, null, ((statusCode==301)? SR.get(SR.MS_WAS_BANNED) :
                             SR.get(SR.MS_WAS_KICKED))+((!reason.equals(""))?"\n"+reason:""));
                     }
//#endif
                     if (!reason.equals(""))
                         mucContactBuf.append('(').append(reason).append(')');

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
             if (mc.status==Constants.PRESENCE_OFFLINE) {
                 if (tempRealJid!=null) {
                     mc.realJid=tempRealJid;  //for moderating purposes
                     mucContactBuf.append(" (").append(tempRealJid).append(')');
                 }
                 mucContactBuf.append(SR.get(SR.MS_HAS_JOINED_THE_CHANNEL_AS));
                 if (affiliationCode!=Constants.AFFILIATION_MEMBER) mucContactBuf.append(getRoleLocale(roleCode));
                 //
                 if (!mc.affiliation.equals("none")) {
                     if (roleCode!=Constants.ROLE_PARTICIPANT) mucContactBuf.append(SR.get(SR.MS_AND));
                     mucContactBuf.append(getAffiliationLocale(affiliationCode));
                 }

                 if (statusText.length()>0) mucContactBuf.append(" (").append(statusText).append(')');
             } else {
                 mucContactBuf.append(SR.get(SR.MS_IS_NOW));
                 if (roleChanged) mucContactBuf.append(getRoleLocale(roleCode));
                 if(mc.role.equals("visitor")) {
                       if(null != item.getChildBlockText("reason")) mucContactBuf.append('('+item.getChildBlockText("reason")+')');
                  }
                  if (affiliationChanged) {
                     if (roleChanged) mucContactBuf.append(SR.get(SR.MS_AND));
                     mucContactBuf.append(getAffiliationLocale(affiliationCode));
                 }
                 if (!roleChanged && !affiliationChanged) mucContactBuf.append(Prtext);
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
    public void addFileQuery(String from, String message) {
        Contact c=null;
        if(c==null) c=getContact(from, true);
        c.fileQuery=true;
        messageStore(c, new Msg(Constants.MESSAGE_TYPE_SYSTEM, from, " "+SR.get(SR.MS_FILE), message));
    }
//#endif

    public void messageStore(Contact c, Msg message) {
        if (c==null) return;

        boolean active=true;
        if(message.messageType==Constants.MESSAGE_TYPE_PRESENCE){
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
        if(active==false) return;

        c.addMessage(message);

        boolean autorespond = false;
//#ifdef RUNNING_MESSAGE
        if (message.messageType==Constants.MESSAGE_TYPE_IN)
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
            if (message.messageType==Constants.MESSAGE_TYPE_AUTH && showWobbler(c))
                setWobbler(2, c, message.from+"\n"+message.body,null);
//#endif

	if (midlet.BombusQD.cf.popupFromMinimized && midlet.BombusQD.cf.getInstance().isMinimized==true)
	    BombusQD.getInstance().hideApp(false,c);

        if (midlet.BombusQD.cf.autoFocus && message.messageType!=Constants.MESSAGE_TYPE_PRESENCE && message.messageType!=Constants.MESSAGE_TYPE_OUT)
            focusToContact(c, false);

        if (message.highlite) {
            playNotify(SOUND_FOR_ME);
//#ifdef POPUPS
            if (showWobbler(c))
                setWobbler(2, c, message.body,null);
//#endif
            autorespond = true;

        }else {
	    //#ifdef JUICK.COM
            boolean incomingMsg = (message.messageType==Constants.MESSAGE_TYPE_IN || message.messageType==Constants.MESSAGE_TYPE_JUICK);
	    //#else
//#             boolean incomingMsg = (message.messageType==Constants.MESSAGE_TYPE_IN);
	    //#endif
            boolean groupchat = (c.origin==Constants.ORIGIN_GROUPCHAT);
            if(!incomingMsg) return;

            if (groupchat==false) {
//#ifdef POPUPS
//#ifndef WMUC
                if (!(c instanceof MucContact)) {
//#endif
                    if (showWobbler(c)) {
                        setWobbler(2, c, c.toString()+": "+message.body,null);
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

        if (c.origin==Constants.ORIGIN_GROUPCHAT || c.jid.isTransport()
              || c.getGroupType()==Groups.TYPE_TRANSP || c.getGroupType()==Groups.TYPE_SEARCH_RESULT
                || c.getGroupType()==Groups.TYPE_SELF)
            autorespond=false;

        if (message.messageType!=Constants.MESSAGE_TYPE_IN)
            autorespond=false;

        if (!c.autoresponded && autorespond) {
            ExtendedStatus es=sl.getStatus(myStatus);
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

          c.addMessage(new Msg(Constants.MESSAGE_TYPE_SYSTEM, "local", SR.get(SR.MS_AUTORESPOND), ""));


            }
        }
//#ifdef CLASSIC_CHAT
//#         if(midlet.BombusQD.cf.module_classicchat) {
//#             StringUtils.addClassicChatMsg(message.toString(),midlet.BombusQD.cf.width_classic,c.scroller);
//#         }
//#endif
    }

    private static long notifyReadyTime=System.currentTimeMillis();
    private static int blockNotifyEvent=-111;


    private final static int SOUND_FOR_ME=500;
    private final static int SOUND_FOR_CONFERENCE=800;
    private final static int SOUND_MESSAGE=1000;
    private final static int SOUND_CONNECTED=777;
    private final static int SOUND_FOR_VIP=100;
    private final static int SOUND_COMPOSING=888;
    private final static int SOUND_OUTGOING=999;
    private final static int SOUND_ATTENTION=666;


    public void blockNotify(int event, long ms) {
        if (!notifyReady(-111)) return;
        blockNotifyEvent=event;
        notifyReadyTime=System.currentTimeMillis()+ms;
    }

    public boolean notifyReady(int event) {
        if ((blockNotifyEvent==event || (blockNotifyEvent==-111 && event<=7)) && System.currentTimeMillis()<notifyReadyTime) return false;
        else return true;
    }

    public void playNotify(int event) {
        if(midlet.BombusQD.cf.profile==AlertProfile.NONE) return;
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

        int profile=midlet.BombusQD.cf.profile;
        EventNotify notify=null;

        switch (profile) {                                //display   fileType   soundName   volume      vibrate
            case AlertProfile.ALL:   notify=new EventNotify(display,    type,   message,    volume,     vibraLen); break;
            case AlertProfile.VIBRA: notify=new EventNotify(display,    null,   null,       volume,     vibraLen); break;
            case AlertProfile.SOUND: notify=new EventNotify(display,    type,   message,    volume,     0); break;
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
        paintVContacts = vContacts;
        int index = paintVContacts.indexOf(c);
        if (index >= 0) moveCursorTo(index);
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
            setProgress(SR.get(SR.MS_DISCONNECTED), 0);
            try {
                sendPresence(Constants.PRESENCE_OFFLINE, null);
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
        if (currentReconnect>=midlet.BombusQD.cf.reconnectCount) { errorLog(error.toString()); return; }
        currentReconnect++;
        String topBar="("+currentReconnect+"/"+midlet.BombusQD.cf.reconnectCount+") Reconnecting";
        errorLog(topBar+"\n"+error.toString());

        //reconnectWindow.getInstance().startReconnect();
        //doReconnect();
        new Reconnect(topBar, error.toString(), display);
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
                if (mess==null) mess=sl.getStatus(Constants.PRESENCE_OFFLINE).getMessage();
                sendPresence(Constants.PRESENCE_OFFLINE, mess);
            } catch (Exception e) { }
        }
//#ifdef STATS
//#ifdef PLUGINS
//#         if (midlet.BombusQD.sd.Stats)
//#endif
            Stats.getInstance().saveToStorage(false,false);
//#endif
      }


    public void eventLongOk(){
        super.eventLongOk();
        showActionsMenu(this, getFocusedObject());
    }


    private Displayable createMsgList(){
        Object e=getFocusedObject();
        if (e instanceof Contact) {
           Contact c = (Contact)e;
           if(c.getChatInfo().getMessageCount()==0){
               createMessageEdit(c, c.msgSuspended, this, true);
               return null;
           }
           return ((Contact)e).getMessageList();
        }
        return null;
    }

    public void replaceMessageEditText(Contact to, String bodyNew, Displayable pview){
         switch(midlet.BombusQD.cf.msgEditType){
            case 0:
                messageEdit.replaceText(to, bodyNew, pview);
                break;
            case 1:
                altmessageEdit.replaceText(to, bodyNew, pview);
                break;
         }
    }

    public void createMultiMessage(Displayable pview,Vector contacts){
         switch(midlet.BombusQD.cf.msgEditType){
            case 0: messageEdit.setText(pview, contacts, true); break;
            case 1:  altmessageEdit.setText(pview, contacts, true); break;
         }
    }

    public void createMessageEdit(Contact c, String body, Displayable pview, boolean emptyChat){
         switch(midlet.BombusQD.cf.msgEditType){
            case 0:
                if(messageEdit.ticker!=null) messageEdit.ticker.setString("BombusQD");
                messageEdit.setText(body, c, pview, emptyChat);
                break;
            case 1:
                if(altmessageEdit.ticker!=null) altmessageEdit.ticker.setString("BombusQD");
                altmessageEdit.setText(body, c, pview, emptyChat);
                break;
         }
    }

    protected void keyGreen() {
        if (!isLoggedIn()) {
            return;
        }
        Displayable pview = createMsgList();
        if (pview != null) {
            Contact c = (Contact)getFocusedObject();

//#ifdef CLASSIC_CHAT
//#             if (midlet.BombusQD.cf.module_classicchat) {
//#                 new SimpleItemChat(display, this, c);
//#             } else {
//#endif
                createMessageEdit(c, c.msgSuspended, pview, (c.getChatInfo().getMessageCount() == 0));
//#ifdef CLASSIC_CHAT
//#             }
//#endif
            c.msgSuspended = null;
        }
    }

    protected void keyClear(){
        if (isLoggedIn()) {
            Contact c=(Contact) getFocusedObject();
            try {
                boolean isContact=( getFocusedObject() instanceof Contact );
//#ifndef WMUC
                boolean isMucContact=( getFocusedObject() instanceof MucContact );
//#else
//#             boolean isMucContact=false;
//#endif
                if (isContact && !isMucContact) {
                   new AlertBox(SR.get(SR.MS_DELETE_ASK), c.getNickJid(), display, this, false) {
                        public void yes() {
                            deleteContact((Contact)getFocusedObject());
                        }
                        public void no() {}
                    };
                }
//#ifndef WMUC
                else if (isContact && isMucContact && c.origin!=Constants.ORIGIN_GROUPCHAT) {
                    ConferenceGroup mucGrp=(ConferenceGroup)c.group;
                    if (mucGrp.selfContact.roleCode==Constants.ROLE_MODERATOR) {
                        String myNick=mucGrp.selfContact.getName();
                        MucContact mc=(MucContact) c;
                        new QuickPrivelegyEditForm(display, this, mc, QuickPrivelegyEditForm.KICK,myNick);
                    }
                }
//#endif
            }
            catch(OutOfMemoryError eom) {
                 errorLog("error Roster::4");
            } catch (Exception e) {}
        }
    }

    public void eventOk() {
        super.eventOk();
        Object e = getFocusedObject();
        if (e instanceof Contact) {
            Contact c = (Contact)e;

            if (c.getChatInfo().getMessageCount() == 0) {
                createMessageEdit(c, c.msgSuspended, this, true);
                return;
            }
//#ifdef CLASSIC_CHAT
//#             if (midlet.BombusQD.cf.module_classicchat) {
//#                 new SimpleItemChat(display, this, c);
//#             } else {
//#endif
                display.setCurrent(c.getMessageList());
//#ifdef CLASSIC_CHAT
//#             }
//#endif
            c = null;
        } else {
            cleanupGroup();
            reEnumRoster();
            redraw();//???
        }
    }

    public void keyPressed(int keyCode){
        super.keyPressed(keyCode);
        if(gm.itemGrMenu>0 && midlet.BombusQD.cf.graphicsMenu){
            return;
        }
        switch (keyCode) {
//#ifdef POPUPS
            case KEY_POUND:
                if (getItemCount()==0)
                    return;
                showInfo();
                return;
//#endif
            case KEY_NUM1:
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
            case KEY_NUM4:
                super.pageLeft();
                return;
            case KEY_NUM6:
                super.pageRight();
                return;
//#ifdef AUTOSTATUS
            case SE_FLIPCLOSE_JP6:
            case SIEMENS_FLIPCLOSE:
            case MOTOROLA_FLIP:
                if (phoneManufacturer!=Config.SONYE) { //workaround for SE JP6 - enabling vibra in closed state
                    display.setCurrent(null);
                    try {
                        Thread.sleep(300);
                    } catch (Exception ex) {}
                    display.setCurrent(this);
                }
                if (midlet.BombusQD.cf.autoAwayType==Config.AWAY_LOCK)
                    if (!autoAway)
                        autostatus.setTimeEvent(midlet.BombusQD.cf.autoAwayDelay* 60*1000);
                break;
//#endif


            case KEY_NUM0:
                if (getItemCount()==0)
                     return;
                Vector contacts = contactList.contacts;
                int size = contacts.size() - 1;
                for (int index = size; 0 <= index; --index) {
                    Contact contact = (Contact) contacts.elementAt(index);
                    contact.setIncoming(Constants.INC_NONE);
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

            case KEY_NUM3:
                showActiveContacts(this, null);
                break;
            case KEY_NUM9:
                if (getItemCount()==0)
                    return;
                int newpos2=searchGroup(1);
                if (newpos2>-1) {
                    moveCursorTo(newpos2);
                    setRotator();
                }
                break;
            case KEY_STAR:
                if (midlet.BombusQD.cf.ghostMotor) {
                    // backlight management
                    blState=(blState==1)? Integer.MAX_VALUE : 1;
                    display.flashBacklight(blState);
                }
                break;
        }
//#ifdef AUTOSTATUS
        userActivity();
//#endif
     }

    protected void keyRepeated(int keyCode) {
        super.keyRepeated(keyCode);
        if (kHold==keyCode) return;
        kHold=keyCode;

        if (keyCode==midlet.BombusQD.cf.keyLock) {
//#ifdef AUTOSTATUS
            if (midlet.BombusQD.cf.autoAwayType==Config.AWAY_LOCK) {
                if (!autoAway) {
                    autoAway=true;
                    if (!midlet.BombusQD.cf.setAutoStatusMessage) {
                        sendPresence(Constants.PRESENCE_AWAY, "Auto Status on KeyLock since %t");
                    } else {
                        sendPresence(Constants.PRESENCE_AWAY, null);
                    }
                }
            }
//#endif
            midlet.BombusQD.getInstance().s = new SplashScreen(display, getMainBarItem(), midlet.BombusQD.cf.keyLock);
            //midlet.BombusQD.getInstance().s.createSnow();
            return;
        } else if (keyCode==midlet.BombusQD.cf.keyVibra || keyCode==MOTOE680_FMRADIO /* TODO: redefine keyVibra*/) {
            // swap profiles
            int profile=midlet.BombusQD.cf.profile;
            midlet.BombusQD.cf.profile=(profile==AlertProfile.VIBRA)?midlet.BombusQD.cf.lastProfile : AlertProfile.VIBRA;
            midlet.BombusQD.cf.lastProfile=profile;

            updateMainBar();
            redraw();
            return;
        } else if (keyCode==KEY_NUM0) {
            midlet.BombusQD.cf.showOfflineContacts=!midlet.BombusQD.cf.showOfflineContacts;
            sortRoster(null);
            setUpdateView();
            reEnumRoster();
            return;
        }
//#ifndef WMUC
        else if (keyCode==KEY_NUM1 && isLoggedIn()) new Bookmarks(display, this, null);
//#endif

       	else if (keyCode==KEY_NUM4) {
              display.setCurrent(new ConfigForm(display, this));
        }
        else if (keyCode==KEY_NUM6) {
            Config.fullscreen =! Config.fullscreen;
            setFullScreenMode(Config.fullscreen);
            midlet.BombusQD.cf.saveToStorage();
        }
//#ifdef SERVICE_DISCOVERY
        else if (keyCode==KEY_NUM7 && isLoggedIn())
            new ServiceDiscovery(display, null, null, false);
//#endif
        else if (keyCode==KEY_NUM9) {
            if (midlet.BombusQD.cf.allowMinimize)
                BombusQD.getInstance().hideApp(true,null);
            else if (phoneManufacturer==Config.SIEMENS2)//SIEMENS: MYMENU call. Possible Main Menu for capable phones
                 try {
                      BombusQD.getInstance().platformRequest("native:ELSE_STR_MYMENU");
                 } catch (Exception e) { }
            else if (phoneManufacturer==Config.SIEMENS)//SIEMENS-NSG: MYMENU call. Possible Native Menu for capable phones
                 try {
                    BombusQD.getInstance().platformRequest("native:NAT_MAIN_MENU");
                 } catch (Exception e) { }
        }
    }

//#ifdef AUTOSTATUS
    private void userActivity() {
        if (autostatus==null) return;

        if (midlet.BombusQD.cf.autoAwayType==Config.AWAY_IDLE) {
            if (!autoAway) {
                autostatus.setTimeEvent(midlet.BombusQD.cf.autoAwayDelay* 60*1000);
                return;
            }
        } else {
            return;
        }
        autostatus.setTimeEvent(0);
        setAutoStatus(Constants.PRESENCE_ONLINE);
    }

    public void messageActivity() {
        if (autostatus==null) return;

        if (midlet.BombusQD.cf.autoAwayType==Config.AWAY_MESSAGE) {
             if (myStatus<2)
                autostatus.setTimeEvent(midlet.BombusQD.cf.autoAwayDelay* 60*1000);
             else if (!autoAway)
                autostatus.setTimeEvent(0);
        }
    }
//#endif

//#ifdef POPUPS
    public void showInfo() {
        Object focused = getFocusedObject();
        if (focused==null) return;
        try {
            VirtualList.getPopUp().next();
            if (focused instanceof Group) return; //|| focused instanceof ConferenceGroup // ???
            setWobbler(1, (Contact)focused, null, focused);
        } catch(OutOfMemoryError eom) {
            errorLog("error Roster::5 OutOfMemoryError(" + focused + "->Class::" + focused.getClass().toString() + ")");
        } catch (Exception e) {
            errorLog("error Roster::5 Exception->" + focused);
        }
    }

    private static StringBuffer mess=new StringBuffer(0);

    public void setWobbler(int type, Contact contact, String info,Object focused) {
        if (info==null) {
            if(focused==null) return;
            Contact cntact=(Contact)focused;
//#ifndef WMUC
            boolean isMucContact = (focused instanceof MucContact);
            if (isMucContact) {
                MucContact mucContact=(MucContact)focused;

                if (mucContact.origin!=Constants.ORIGIN_GROUPCHAT){
                    mess.append((mucContact.realJid==null)?"":"jid: "+mucContact.realJid+'\n');

                    if (mucContact.affiliationCode>Constants.AFFILIATION_NONE)
                        mess.append(getAffiliationLocale(mucContact.affiliationCode));

                    if (!(mucContact.roleCode==Constants.ROLE_PARTICIPANT && mucContact.affiliationCode==Constants.AFFILIATION_MEMBER)) {
                        if (mucContact.affiliationCode>Constants.AFFILIATION_NONE)
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
                    if (cntact.pepTuneText!="") {
                        mess.append(": ").append(cntact.pepTuneText);
                    }
                }
//#endif
//#ifndef WMUC
            }
//#endif
            if (cntact.origin!=Constants.ORIGIN_GROUPCHAT){
                if (cntact.j2j != null) {
                    mess.append("\nJ2J: ").append(cntact.j2j);
                }
//#ifdef CLIENTS_ICONS
                if (midlet.BombusQD.cf.showClientIcon) {
                    if (cntact.client != -1) {
                        mess.append("\nUse: ").append(cntact.clientName);
                        if (cntact.version != null) {
                            mess.append(" ").append(cntact.version);
                        }
                    }
                }
//#endif
                if (cntact.lang != null) {
                    mess.append("\nLang: " + cntact.lang);
                }
            }

            if (cntact.getStatus()!=null) {
                if (cntact.origin!=Constants.ORIGIN_GROUPCHAT){
                    mess.append('\n')
                        .append(SR.get(SR.MS_STATUS))
                        .append(": ");
                }
                mess.append(cntact.getStatus());
            }
            if(cntact.annotations!=null) mess.append('\n'+SR.get(SR.MS_ANNOTATION)+": "+cntact.annotations);

            VirtualList.setWobble(1, null, mess.toString());
            mess = new StringBuffer(0);
        } else {
            VirtualList.setWobble(type, contact.getJid(), info);
        }

        redraw();
    }
//#endif


    public void quit() {
//#ifdef AUTOSTATUS
        if (midlet.BombusQD.cf.autoAwayType!=Config.AWAY_OFF) {
            try {
                autostatus.destroyTask();
            } catch (Exception ex) {}
        }
//#endif
        destroyView();
        logoff(null);

        BombusQD.getInstance().notifyDestroyed();
    }

    public void cmdQuit() {
        if (midlet.BombusQD.cf.queryExit) {
            new AlertBox(SR.get(SR.MS_QUIT_ASK), SR.get(SR.MS_SURE_QUIT), display, null, false) {
                public void yes() {quit(); }
                public void no() { }
            };
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
            new ContactEdit(display, this, cn);
       }
   }

    protected void showNotify() {
        super.showNotify();
        countNewMsgs();
//#ifdef AUTOSTATUS
        if (midlet.BombusQD.cf.autoAwayType==Config.AWAY_IDLE){
            if(null == autostatus) autostatus = new AutoStatusTask(true);
            if (!autostatus.isAwayTimerSet())
                if (!autoAway)
                    autostatus.setTimeEvent(midlet.BombusQD.cf.autoAwayDelay* 60*1000);
        }
//#endif
    }

    protected void hideNotify() {
        super.hideNotify();
//#ifdef AUTOSTATUS
        if (midlet.BombusQD.cf.autoAwayType==Config.AWAY_IDLE)
            if (kHold==0)
                autostatus.setTimeEvent(0);
//#endif
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

    private Vector aContacts = new Vector(0);
    public void searchActiveContact(Contact first, boolean right) {
        aContacts = new Vector(0);
        Vector search = contactList.contacts;
        int size = search.size();
        short activePos = 0;
        Contact activeContact;
        Contact showNext;
        for(int i = 0; i < size; ++i) {
             activeContact = (Contact)search.elementAt(i);
             if(activeContact.active()) aContacts.addElement(activeContact);
        }
        if (aContacts.isEmpty()) return;
        if (aContacts.size()<2 && first == aContacts.firstElement()) return;
        first.getChatInfo().opened = false;
        int pos = aContacts.indexOf(first);
        if (pos<0) showNext = (Contact)aContacts.firstElement();
        else if (right) {
           showNext = (aContacts.size() - 1 == pos) ? (Contact)aContacts.firstElement() : (Contact)aContacts.elementAt(pos + 1);
        } else {
          showNext = (0 == pos) ? (Contact)aContacts.lastElement() : (Contact)aContacts.elementAt(pos - 1);
        }
        display.setCurrent(showNext.getMessageList());
    }

    public void deleteContact(Contact c) {
        Vector hContacts = contactList.contacts;
        for (Enumeration e=hContacts.elements();e.hasMoreElements();) {
            Contact c2=(Contact)e. nextElement();
            if (c.jid.equals(c2. jid,false)) {
                c2.setStatus(Constants.PRESENCE_TRASH);
                c2.offline_type = Constants.PRESENCE_TRASH;
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

//#ifdef AUTOSTATUS
    public void setAutoAway() {
        if(!isLoggedIn() || !midlet.BombusQD.cf.module_autostatus) return;
        if (!autoAway) {
            oldStatus=myStatus;
            if (myStatus==0 || myStatus==1) {
                autoAway=true;
                if (!midlet.BombusQD.cf.setAutoStatusMessage) {
                    sendPresence(Constants.PRESENCE_AWAY, SR.get(SR.MS_AUTO_AWAY));
                } else {
                    sendPresence(Constants.PRESENCE_AWAY, null);
                }
            }
        }
    }

    public void setAutoXa() {
        if(!isLoggedIn() || !midlet.BombusQD.cf.module_autostatus) return;
        if (autoAway && !autoXa) {
            autoXa=true;
            if (!midlet.BombusQD.cf.setAutoStatusMessage) {
                sendPresence(Constants.PRESENCE_XA, SR.get(SR.MS_AUTO_XA));
            } else {
                sendPresence(Constants.PRESENCE_XA, null);
            }
        }
    }

    public void setAutoStatus(int status) {
        if (!isLoggedIn())
            return;
        if (status==Constants.PRESENCE_ONLINE && autoAway) {
            autoAway=false;
            autoXa=false;
            sendPresence(Constants.PRESENCE_ONLINE, null);
            return;
        }
        if (status!=Constants.PRESENCE_ONLINE && myStatus==Constants.PRESENCE_ONLINE && !autoAway) {
            autoAway=true;
            if (!midlet.BombusQD.cf.setAutoStatusMessage) {
                sendPresence(Constants.PRESENCE_AWAY, "Auto Status on KeyLock since %t");
            } else {
                sendPresence(Constants.PRESENCE_AWAY, null);
            }
        }
    }
//#endif

    public void deleteGroup(Group deleteGroup) {
        Vector contacts = contactList.contacts;
        for (int index = contacts.size() - 1; 0 <= index; --index) {
            Contact contact = (Contact) contacts.elementAt(index);
            if (contact.group==deleteGroup) deleteContact(contact);
        }
    }


//#ifdef MENU_LISTENER

//#ifdef GRAPHICS_MENU
    public int showGraphicsMenu() {
         GMenuConfig.getInstance().itemGrMenu = GMenu.MAIN_MENU_ROSTER;
         commandState();
         menuItem = new GMenu(display, parentView, this, MenuIcons.getInstance(), menuCommands, cmdfirstList, cmdsecondList, cmdThirdList);
         redraw();
        return GMenu.MAIN_MENU_ROSTER;
    }

    public String touchLeftCommand(){ return (midlet.BombusQD.cf.oldSE)?SR.get(SR.MS_ITEM_ACTIONS):SR.get(SR.MS_MENU); }
    public String touchRightCommand(){ return (midlet.BombusQD.cf.oldSE)?SR.get(SR.MS_MENU):SR.get(SR.MS_ITEM_ACTIONS); }

//#else
//#       public void showMenu() {
//#        commandState();
//#        new MyMenu(display, parentView, this, SR.get(SR.MS_MAIN_MENU), MenuIcons.getInstance(), menuCommands);
//#      }
//#endif

//#ifdef TOUCH
    protected void touchMainPanelPressed(int x, int y) {
        if (x> width-50) {
            cmdAlert();
        } else if (x < 50){
            cmdStatus();
        } else {
            showActiveContacts(this, null);
        }
    }

    public void touchMiddlePressed(){
        if (getItemCount()==0)
             return;
        Vector contacts = contactList.contacts;
        int size = contacts.size() - 1;
        for (int index = size; 0 <= index; --index) {
            Contact contact = (Contact) contacts.elementAt(index);
            contact.setIncoming(Constants.INC_NONE);
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
//#ifdef GRAPHICS_MENU

    public void touchRightPressed() {
        if (midlet.BombusQD.cf.oldSE) {
            showGraphicsMenu();
        } else {
            showActionsMenu(this, getFocusedObject());
        }
    }

    public void touchLeftPressed() {
        if (midlet.BombusQD.cf.oldSE) {
            showActionsMenu(this, getFocusedObject());
        } else {
            showGraphicsMenu();
        }
    }
//#else
//#     public void touchRightPressed(){ if (midlet.BombusQD.cf.oldSE) showMenu(); else cmdActions(); }
//#     public void touchLeftPressed(){ if (midlet.BombusQD.cf.oldSE) cmdActions(); else showMenu(); }
//#
//#endif

//#endif


//#ifdef RUNNING_MESSAGE
    void setTicker(Contact c, String message) {
       if (midlet.BombusQD.cf.runningMessage) {
            switch(midlet.BombusQD.cf.msgEditType){
               case 0:
                   if (messageEdit==null) return;
                   if (messageEdit.to==c) {
                        message = StringUtils.replaceNickTags(message);
                        if(messageEdit.ticker==null) return;
                        messageEdit.ticker.setString(message);
                   }
                   break;
               case 1:
                   if (altmessageEdit==null) return;
                     if (altmessageEdit.to==c) {
                        message = StringUtils.replaceNickTags(message);
                        if(altmessageEdit.ticker==null) return;
                        altmessageEdit.ticker.setString(message);
                     }
                   break;
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
        private StringBuffer counts = new StringBuffer(0);

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
                //e.printStackTrace();
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

                  counts = new StringBuffer(0);
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
