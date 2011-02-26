/*
 * RosterItemActions.java
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

package menu;
import client.*;
import colors.ColorTheme;
import alert.AlertCustomize;
//#ifndef WMUC
import conference.ConferenceGroup;
import conference.InviteForm;
import conference.MucContact;
import conference.QueryConfigForm;
import conference.affiliation.ConferenceQuickPrivelegeModify;
//#ifdef REQUEST_VOICE
//# import Conference.QueryRequestVoice;
//#endif
import conference.affiliation.Affiliations;
//#endif
//#ifdef SERVICE_DISCOVERY
import disco.ServiceDiscovery;
//#endif

import images.ActionsIcons;

//#if FILE_TRANSFER
import io.file.transfer.TransferImage;
import io.file.transfer.TransferSendFile;
//#endif

import ui.controls.AlertBox;

import xmpp.extensions.IqLast;
import xmpp.extensions.IqPing;
import xmpp.extensions.IqTimeReply;
import xmpp.extensions.IqVersionReply;
import xmpp.extensions.SoftwareInfo;

import com.alsutton.jabber.datablocks.Presence;

import java.util.Enumeration;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
//#ifdef CLIPBOARD
//# import util.ClipBoard;
//#endif

import vcard.VCard;
import vcard.VCardEdit;
import vcard.VCardView;
import ui.ImageList;
//#ifdef PEP
//# import mood.EventPublish;
//#endif
import ui.MainBar;
import com.alsutton.jabber.datablocks.*;
import com.alsutton.jabber.*;
//#ifdef CHECKERS
//# import xmpp.extensions.games.*;
//#endif
import ui.MIDPTextBox;
import java.util.Vector;
import javax.microedition.lcdui.TextField;

/**
 *
 * @author EvgS,aqent
 */
public final class RosterItemActions extends Menu implements MIDPTextBox.TextBoxNotify {

    Object item=null;

    private int action;

    ActionsIcons menuIcons=ActionsIcons.getInstance();

    private MainBar mainbar;

    /** Creates a new instance of RosterItemActions */
    public RosterItemActions(Display display, Displayable pView) {
        super("init", ActionsIcons.getInstance() ,null);
        mainbar = new MainBar("init");
        setMainBarItem(mainbar);
        this.display = display;
    }
    
    private void updateMainBar(Object item){
        mainbar = new MainBar(item);
        setMainBarItem(mainbar);
        mainbar = null;
    }
    
    public void showActions(Displayable pView,Object item, int action){
        updateMainBar(item);
        setItems(pView,item,action);
	if (getItemCount()>0) {
            if (action<0) {
                attachDisplay(display);
                this.parentView=pView;
            } else try {
                this.display=display;
                doAction(action);
            } catch (Exception e) { }
        }
    }
    
    private void setItems(Displayable pView,Object item, int action) {
        this.item=item;
        this.action=action;
        if (!midlet.BombusQD.sd.roster.isLoggedIn()) return;

        if (item==null) return;
        boolean isContact=( item instanceof Contact );
        
	if (isContact) {
	    Contact contact=(Contact)item;   
            int grType = contact.getGroupType();
            boolean originGroupchat = (contact.origin==Constants.ORIGIN_GROUPCHAT);

        // if(contact.bareJid.equals(StaticData.HELPER_CONTACT)==false){            
	    if (grType==Groups.TYPE_TRANSP) {
		addItem(SR.get(SR.MS_LOGIN),5, menuIcons.ICON_ON);
		addItem(SR.get(SR.MS_LOGOFF),6, menuIcons.ICON_OFF);
                addItem(SR.get(SR.MS_RESOLVE_NICKNAMES), 7, menuIcons.ICON_NICK_RESOLVE);
//#if CHANGE_TRANSPORT
//#ifdef PLUGINS
//#                 if (sd.ChangeTransport);
//#endif
//#                     addItem(SR.get(SR.MS_CHANGE_TRANSPORT),915, menuIcons.ICON_NICK_RESOLVE);
//#endif
	    }

	    if(!originGroupchat) addItem(SR.get(SR.MS_VCARD),1, menuIcons.ICON_VCARD);
            if(contact.vcard!=null){
             addItem(SR.get(SR.MS_DELETE_VCARD),88, menuIcons.ICON_VCARD, true);
             addItem(SR.get(SR.MS_DELETE_ALL_VCARD),89, menuIcons.ICON_VCARD, true);
            }
              if(contact.img_vcard!=null){
                addItem(SR.get(SR.MS_DELETE_AVATAR_VCARD),90, menuIcons.ICON_VCARD, true);
                addItem(SR.get(SR.MS_DELETE_ALL_AVATAR_VCARD),91, menuIcons.ICON_VCARD, true);
              }
            
            if(midlet.BombusQD.cf.userAppLevel==1) addItem(SR.get(SR.MS_FEATURES),250,menuIcons.ICON_INFO);

//#ifdef POPUPS
            addItem(SR.get(SR.MS_INFO),86, menuIcons.ICON_INFO);
//#endif
            
           if (!(contact instanceof MucContact) && midlet.BombusQD.cf.networkAnnotation) { 
               //XEP-0145: Annotations
               if (grType!=Groups.TYPE_TRANSP && grType!=Groups.TYPE_SELF){
                   addItem(SR.get(SR.MS_CREATE_ANNOTATION),888, menuIcons.ICON_VOICE);
                   if(contact.annotations!=null) addItem(SR.get(SR.MS_REMOVE_ANNOTATION),887, menuIcons.ICON_VOICE);
               }
           }   
           //History
           if (grType!=Groups.TYPE_TRANSP) {
	        if(midlet.BombusQD.cf.module_history) addItem(SR.get(SR.MS_HISTORY_SHOW), 1110, menuIcons.ICON_VERSION);
           }

            if(!originGroupchat) addItem(SR.get(SR.MS_CLIENT_INFO),0, menuIcons.ICON_VERSION);

//#ifdef SERVICE_DISCOVERY
	    if(!originGroupchat) addItem(SR.get(SR.MS_COMMANDS),30, menuIcons.ICON_COMMAND);
//#endif
//#ifdef CLIPBOARD
//#             if (midlet.BombusQD.cf.useClipBoard) {
//#                 if (!midlet.BombusQD.clipboard.isEmpty())
//#                     addItem(SR.get(SR.MS_SEND_BUFFER),914, menuIcons.ICON_SEND_BUFFER);
//#                 if (grType!=Groups.TYPE_SELF)
//#                     addItem(SR.get(SR.MS_COPY_JID),892, menuIcons.ICON_COPY_JID);
//#             }
//#endif
            if(!originGroupchat) addItem(SR.get(SR.MS_SEND_COLOR_SCHEME), 912, menuIcons.ICON_SEND_COLORS);
            if (contact.status<Constants.PRESENCE_OFFLINE && !originGroupchat) {
                addItem(SR.get(SR.MS_TIME),891, menuIcons.ICON_TIME);
                addItem(SR.get(SR.MS_IDLE),889, menuIcons.ICON_IDLE);
                addItem(SR.get(SR.MS_PING),893, menuIcons.ICON_PING);
		if (AlertCustomize.getInstance().enableAttention)
			addItem(SR.get(SR.LA_REQUEST),666,menuIcons.ICON_TIME);//Attention
            }
	    
	    if (grType!=Groups.TYPE_SELF && grType!=Groups.TYPE_SEARCH_RESULT && contact.origin<Constants.ORIGIN_GROUPCHAT) {
		if (contact.status<Constants.PRESENCE_OFFLINE) {
                    addItem(SR.get(SR.MS_ONLINE_TIME),890, menuIcons.ICON_ONLINE);    
                } else {
                    addItem(SR.get(SR.MS_SEEN),894, menuIcons.ICON_ONLINE); 
                }
                if (grType!=Groups.TYPE_TRANSP) {
                    addItem(SR.get(SR.MS_EDIT),2, menuIcons.ICON_RENAME);
                }
		addItem(SR.get(SR.MS_SUBSCRIPTION),3, menuIcons.ICON_SUBSCR);
//		addItem(SR.get(SR.MS_MOVE),1003, menuIcons.ICON_MOVE);
		addItem(SR.get(SR.MS_DELETE), 4, menuIcons.ICON_DELETE);
		addItem(SR.get(SR.MS_DIRECT_PRESENCE),45, menuIcons.ICON_SET_STATUS);

	    }
         //}
	    if (originGroupchat) return;
//#ifndef WMUC
            boolean onlineConferences=true;

            if (contact instanceof MucContact) {
                MucContact selfContact= ((ConferenceGroup) contact.group).selfContact;
                MucContact mc=(MucContact) contact;
                
                
                int myAffiliation=selfContact.affiliationCode;
                if (myAffiliation==Constants.AFFILIATION_OWNER) 
                    myAffiliation++; // allow owner to change owner's affiliation

            
                //addItem(SR.get(SR.MS_TIME),891); 
                
                //invite
                if (mc.realJid!=null) {
                    //if (onlineConferences)
                    if (selfContact.roleCode==Constants.ROLE_MODERATOR || myAffiliation==Constants.AFFILIATION_MEMBER){
                        addItem(SR.get(SR.MS_INVITE),40, menuIcons.ICON_INVITE);
                    }
                }
                //invite                
                
                if (selfContact.roleCode==Constants.ROLE_MODERATOR) {
                    if(mc.roleCode<Constants.ROLE_MODERATOR)
                        addItem(SR.get(SR.MS_KICK),8, menuIcons.ICON_KICK);
                    
                    if (myAffiliation>=Constants.AFFILIATION_ADMIN && mc.affiliationCode<myAffiliation)
                        addItem(SR.get(SR.MS_BAN),9, menuIcons.ICON_BAN);
                    
                    if (mc.affiliationCode<Constants.AFFILIATION_ADMIN) 
                        /* 5.1.1 *** A moderator MUST NOT be able to revoke voice privileges from an admin or owner. */ 
                    if (mc.roleCode==Constants.ROLE_VISITOR) addItem(SR.get(SR.MS_GRANT_VOICE),31, menuIcons.ICON_VOICE);
                    else addItem(SR.get(SR.MS_REVOKE_VOICE), 32, menuIcons.ICON_DEVOICE);
                }
//#ifdef REQUEST_VOICE
//# 		if (selfContact.roleCode==MucContact.ROLE_VISITOR) {
//#                     //System.out.println("im visitor");
//#                     if (mc.roleCode==MucContact.ROLE_MODERATOR) {
//#                         //System.out.println(mc.getJid()+" is a moderator");
//#                         addItem(SR.get(SR.MS_REQUEST_PARTICIPANT_ROLE),39);
//#                     }
//#  		}
//#endif
                if (myAffiliation>=Constants.AFFILIATION_ADMIN) {
                    // admin use cases
                    
                    //roles
                    if (mc.affiliationCode<Constants.AFFILIATION_ADMIN) 
                        /* 5.2.1 ** An admin or owner MUST NOT be able to revoke moderation privileges from another admin or owner. */ 
                    if (mc.roleCode==Constants.ROLE_MODERATOR) 
                        addItem(SR.get(SR.MS_REVOKE_MODERATOR),31, menuIcons.ICON_MEMBER);
                    else 
                        addItem(SR.get(SR.MS_GRANT_MODERATOR),33, menuIcons.ICON_ADMIN);
                    
                    //affiliations
                    if (mc.affiliationCode<myAffiliation) {
                        if (mc.affiliationCode!=Constants.AFFILIATION_NONE) 
                            addItem(SR.get(SR.MS_UNAFFILIATE),36, menuIcons.ICON_DEMEMBER);
                        /* 5.2.2 */
                        if (mc.affiliationCode!=Constants.AFFILIATION_MEMBER) 
                            addItem(SR.get(SR.MS_GRANT_MEMBERSHIP),35, menuIcons.ICON_MEMBER);
                    }
                    
                    
               //m.addItem(new MenuItem("Set Affiliation",15));
                }
                if (myAffiliation>=Constants.AFFILIATION_OWNER) {
                    // owner use cases
                    if (mc.affiliationCode!=Constants.AFFILIATION_ADMIN) 
                        addItem(SR.get(SR.MS_GRANT_ADMIN),37, menuIcons.ICON_ADMIN);
                    
                    if (mc.affiliationCode!=Constants.AFFILIATION_OWNER) 
                        addItem(SR.get(SR.MS_GRANT_OWNERSHIP),38, menuIcons.ICON_OWNER);
                }
                if (mc.realJid!=null && mc.status<Constants.PRESENCE_OFFLINE) {
                    
                }
            } else if (grType!=Groups.TYPE_TRANSP && grType!=Groups.TYPE_SEARCH_RESULT) {
                // usual contact - invite item check
                 if (onlineConferences) 
                     addItem(SR.get(SR.MS_INVITE),40, menuIcons.ICON_INVITE);
            }
//#endif
//#if (FILE_IO && FILE_TRANSFER)
            if (grType!=Groups.TYPE_TRANSP && midlet.BombusQD.cf.fileTransfer) 
                if (contact!=midlet.BombusQD.sd.roster.selfContact()) {
//#ifdef PLUGINS
//#                     if (sd.FileTransfer)
//#endif
                        addItem(SR.get(SR.MS_SEND_FILE), 50, menuIcons.ICON_SEND_FILE);
                }
            
//#endif
//#if FILE_TRANSFER
            if (grType!=Groups.TYPE_TRANSP && midlet.BombusQD.cf.fileTransfer) {
                if (contact!=midlet.BombusQD.sd.roster.selfContact()) {
                    String cameraAvailable=System.getProperty("supports.video.capture");
                    if (cameraAvailable!=null) if (cameraAvailable.startsWith("true")) {
//#ifdef PLUGINS
//#                         if (sd.ImageTransfer)
//#endif
                            addItem(SR.get(SR.MS_SEND_PHOTO), 51, menuIcons.ICON_SEND_FILE);
                    }
                }
            }
//#endif
        } else {
	    Group group=(Group)item;
	    if (group.type==Groups.TYPE_SEARCH_RESULT)
		addItem(SR.get(SR.MS_DISCARD),21, menuIcons.ICON_BAN);
//#ifndef WMUC
	    if (group instanceof ConferenceGroup) {
		MucContact self=((ConferenceGroup)group).selfContact;
                
		addItem(SR.get(SR.MS_LEAVE_ROOM),22, menuIcons.ICON_LEAVE);
                addItem(SR.get(SR.MS_CLOSE_ALL_ROOMS),900, menuIcons.ICON_LEAVE);

                if (self.status>=Constants.PRESENCE_OFFLINE) {// offline or error
		    addItem(SR.get(SR.MS_REENTER),23, menuIcons.ICON_CHANGE_NICK);
                } else {
                    addItem(SR.get(SR.MS_DIRECT_PRESENCE),46, menuIcons.ICON_SET_STATUS);
                    addItem(SR.get(SR.MS_CHANGE_NICKNAME),23, menuIcons.ICON_CHANGE_NICK);
		    if (self.affiliationCode>=Constants.AFFILIATION_OWNER) {
			addItem(SR.get(SR.MS_CONFIG_ROOM),10, menuIcons.ICON_CONFIGURE);
                        addItem(SR.get(SR.MS_DESTROY_ROOM),49,menuIcons.ICON_OUTCASTS);
                    }
		    if (self.affiliationCode>=Constants.AFFILIATION_ADMIN) {
			addItem(SR.get(SR.MS_OWNERS),11, menuIcons.ICON_OWNERS);
			addItem(SR.get(SR.MS_ADMINS),12, menuIcons.ICON_ADMINS);
			addItem(SR.get(SR.MS_MEMBERS),13, menuIcons.ICON_MEMBERS);
			addItem(SR.get(SR.MS_BANNED),14, menuIcons.ICON_OUTCASTS);
 		    }
 		}
	    } else {
//#endif
             //  if (group.type==Groups.TYPE_CONFERENCE){
             //     addItem("CLOSE ALL ROOMS!",900, menuIcons.ICON_LEAVE);
             //  }
             //  else{
                 if (    group.type!=Groups.TYPE_IGNORE
                        && group.type!=Groups.TYPE_NOT_IN_LIST
                        && group.type!=Groups.TYPE_SEARCH_RESULT
                        && group.type!=Groups.TYPE_SELF
                        && group.type!=Groups.TYPE_TRANSP)
                 {
                    addItem(SR.get(SR.MS_RENAME),1001, menuIcons.ICON_RENAME);
                    addItem(SR.get(SR.MS_DELETE), 1004, menuIcons.ICON_DELETE);
                 }
             //  }
//#ifndef WMUC
            }
//#endif
 	}
        moveCursorTo(Config.getInstance().cursorPos[1]);            
     }
     
    
    public void OkNotify(String annotationText) { //XEP-0145: Annotations
           Contact find = null;
           Contact current = (Contact)item;
           current.annotations = annotationText;
           
           JabberDataBlock query = new Iq(null, Iq.TYPE_SET, "notes" + current.bareJid);
           JabberDataBlock query_private = query.addChildNs("query", "jabber:iq:private");
           
             JabberDataBlock storage = query_private.addChildNs("storage", "storage:rosternotes");
               int size = midlet.BombusQD.sd.roster.contactList.contacts.size();
               synchronized (midlet.BombusQD.sd.roster.contactList.contacts) {
                  for(int i=0;i<size;i++){
                    find = (Contact)midlet.BombusQD.sd.roster.contactList.contacts.elementAt(i); 
                    if(find.annotations!=null){
                      JabberDataBlock note = storage.addChild("note",find.annotations);
                      note.setAttribute("jid",find.bareJid);
                    }
                  }
               }
           midlet.BombusQD.sd.roster.theStream.send(query);
           query=null;
           destroyView();
    }
    
    
    public void destroyView(){
        menuitems.removeAllElements();
        display.setCurrent(parentView);
    }
    
     public void eventOk(){
         try {
             MenuItem me=(MenuItem) getFocusedObject();
            destroyView();
            if (me==null) return;
            int index=action=me.index;
            doAction(index);
        } catch (Exception e) { }
    }

    private void doAction(final int index) {
        boolean isContact=( item instanceof Contact );
        Contact c = null;
        Group g = null;
        if (isContact) c=(Contact)item; else g=(Group) item;
        
        String to=null;
        midlet.BombusQD.cf.cursorPos[1]=cursor;
        if (isContact) to=(index<3)? c.getJid() : c.bareJid;
            switch (index) {
                case 0: // version
                    midlet.BombusQD.sd.roster.setQuerySign(true);
                    midlet.BombusQD.cf.flagQuerySign=true;
                    midlet.BombusQD.sd.roster.theStream.send(IqVersionReply.query(to));
                    break;
                case 86: // info
//#ifdef POPUPS
                    midlet.BombusQD.sd.roster.showInfo();
//#endif
                    break;
                case 250: //FEATURES
                   midlet.BombusQD.sd.roster.setQuerySign(true);
                   midlet.BombusQD.sd.roster.theStream.addBlockListener(new SoftwareInfo());
                   midlet.BombusQD.sd.roster.theStream.send(SoftwareInfo.querySend(c.getJid()));
                    break;
                case 1: // vCard
                    if (c.vcard!=null) {
                        if (c.getGroupType()==Groups.TYPE_SELF)
                            new VCardEdit(display, midlet.BombusQD.sd.roster, c.vcard);
                        else
                            new VCardView(display, midlet.BombusQD.sd.roster, c);
                        return;
                    }
                    if (c instanceof MucContact) {
                        MucContact mucContact=(MucContact)c;
                        String realjid=mucContact.realJid;
                        mucContact=null;
                        if (realjid==null)
                            VCard.request(c.bareJid, c.getJid());
                        else
                            VCard.request(realjid.substring(0,realjid.indexOf("/",realjid.indexOf("@"))), c.getJid());
                        realjid=null;
                    } else {
                        VCard.request(c.bareJid, c.getJid());
                    }
                    break;
                case 88:
                    c.clearVCard();
                    break;
                case 89:
                    int size = midlet.BombusQD.sd.roster.contactList.contacts.size();
                    for (int i = 0; i < size; ++i) {
                      Contact cntc = (Contact) midlet.BombusQD.sd.roster.contactList.contacts.elementAt(i);
                      if (cntc.vcard==null) continue;
                      cntc.clearVCard();
                    }
                    break;      
                case 90:
                    c.img_vcard = null;
                    break;
                case 91:
                    size = midlet.BombusQD.sd.roster.contactList.contacts.size();
                    for (int i = 0; i < size; ++i) {
                      Contact cntc = (Contact) midlet.BombusQD.sd.roster.contactList.contacts.elementAt(i);
                      if (cntc.img_vcard==null) continue;
                      cntc.img_vcard = null;
                    }
                    break;                
                case 2:
                    new ContactEdit(display, midlet.BombusQD.sd.roster, c );
                    return; //break;
                case 3: //subscription
                    new SubscriptionEdit(display, midlet.BombusQD.sd.roster, c);
                    return; //break;
                case 4:
                    new AlertBox(SR.get(SR.MS_DELETE_ASK), c.getNickJid(), display,  midlet.BombusQD.sd.roster, false) {
                        public void yes() {
                            midlet.BombusQD.sd.roster.deleteContact((Contact)item);
                        }
                        public void no() { }
                    };
                    return;
                case 6: // logoff
                    midlet.BombusQD.sd.roster.blockNotify(-111,10000); //block sounds to 10 sec
                    Presence presence = new Presence(
                    Constants.PRESENCE_OFFLINE, -1, "", null);
                    presence.setTo(c.getJid());
                    midlet.BombusQD.sd.roster.theStream.send( presence );
                    presence=null;
                    break;
                case 5: // logon
                    midlet.BombusQD.sd.roster.blockNotify(-111,10000); //block sounds to 10 sec
                    //querysign=true; displayStatus();
                    Presence presence2 = new Presence(midlet.BombusQD.sd.roster.myStatus, 0, "", null);
                    presence2.setTo(c.getJid());
                    midlet.BombusQD.sd.roster.theStream.send( presence2 );
                    presence2=null;                    
                    break;
                case 7: // Nick resolver
                    midlet.BombusQD.sd.roster.resolveNicknames(c.bareJid);
                    break;
//#if CHANGE_TRANSPORT
//#                 case 915: // change transport
//#                     new ChangeTransport(display, c.bareJid);
//#                     return;
//#endif
                case 21:
                    midlet.BombusQD.sd.roster.cleanupSearch();
                    break;
                case 1110:
                    switch(history.HistoryConfig.getInstance().historyTypeIndex) {
                      case 0:
                        c.getMessageList().getRmsData(3, null); //READ_ALL_DATA
                        break;
                      case 1:
                        //
                        break;
                      case 2:
                        //
                        break;
                    }
                    break;
//#ifdef SERVICE_DISCOVERY
                case 30:
                    new ServiceDiscovery(display, c.getJid(), "http://jabber.org/protocol/commands", false);
                    return;
//#endif
/*                case 1003: 
                    new RenameGroup(display, null, c);
                    return;
 */

		case 666: //Attention
			Message message = new Message(c.getJid(), SR.get(SR.LA_WAKEUP), SR.get(SR.LA_ATTENTION),false);
			message.setType("headline");
			message.addChildNs("attention","urn:xmpp:attention:0");
			midlet.BombusQD.sd.roster.theStream.send(message);
			break;
                case 889: //idle
                    midlet.BombusQD.sd.roster.setQuerySign(true);
                    midlet.BombusQD.sd.roster.theStream.send(IqLast.query(c.getJid(), "idle"));
                    break;
                case 890: //online
                    midlet.BombusQD.sd.roster.setQuerySign(true);
                    midlet.BombusQD.sd.roster.theStream.send(IqLast.query(c.bareJid, "online_"+c.getResource()));
                    break;
                case 894: //seen
                    midlet.BombusQD.sd.roster.setQuerySign(true);
                    midlet.BombusQD.sd.roster.theStream.send(IqLast.query(c.bareJid, "seen"));
                    break;
                case 891: //time
                    midlet.BombusQD.sd.roster.setQuerySign(true);
                    midlet.BombusQD.sd.roster.theStream.send(IqTimeReply.query(c.getJid()));
                    break;
                case 888:
                    new MIDPTextBox(display, SR.get(SR.MS_NEW), c.annotations==null?"":c.annotations, this, TextField.ANY,200);
                    break;
                case 887:
                    OkNotify(null);
                    break;

                case 900:
                    midlet.BombusQD.sd.roster.leaveAllMUCs();
                    break;
//#ifdef CLIPBOARD
//#                 case 892: //Copy JID
//#                         try {
//#                             if (c.bareJid!=null) {
//#                                 Msg msg = new Msg(Constants.MESSAGE_TYPE_SYSTEM,"JID", null, c.bareJid);
//#                                 midlet.BombusQD.clipboard.add(msg);
//#                                 msg = null;
//#                             }
//#                         } catch (Exception e) {/*no messages*/}
//#                     break;
//#endif
                case 893: //ping
                    try {
                        midlet.BombusQD.sd.roster.setQuerySign(true);
                        //c.setPing();
                        midlet.BombusQD.sd.roster.theStream.send(IqPing.query(c.getJid(), null));
                    } catch (Exception e) {/*no messages*/}
                    break;
                case 912: //send color scheme
                    String from=midlet.BombusQD.sd.account.toString();
                    String body=ColorTheme.getInstance().getSkin();
                    String id=String.valueOf((int) System.currentTimeMillis());

                    try {
                        midlet.BombusQD.sd.roster.sendMessage(c, id, body, null, null,false);
                        c.addMessage(new Msg(Constants.MESSAGE_TYPE_OUT,from,null,"scheme sended"));
                    } catch (Exception e) {
                        c.addMessage(new Msg(Constants.MESSAGE_TYPE_OUT,from,null,"scheme NOT sended"));
                        //e.printStackTrace();
                    }
                break;
//#ifdef CLIPBOARD
//#                 case 914: //send message from buffer
//#                     String body2=midlet.BombusQD.clipboard.getClipBoard();
//#                     if (body2.length()==0)
//#                         return;
//# 
//#                     String from2=midlet.BombusQD.sd.account.toString();
//# 
//#                     String id2=String.valueOf((int) System.currentTimeMillis());
//#                     Msg msg2=new Msg(Constants.MESSAGE_TYPE_OUT,from2,null,body2);
//#                     msg2.id=id2;
//#                     msg2.itemCollapsed=true;
//# 
//#                     try {
//#                         if (body2!=null && body2.length()>0) {
//#                             midlet.BombusQD.sd.roster.sendMessage(c, id2, body2, null, null,false);
//#                             
//#                             if (c.origin<Constants.ORIGIN_GROUPCHAT) c.addMessage(msg2);
//#                         }
//#                     } catch (Exception e) {
//#                         c.addMessage(new Msg(Constants.MESSAGE_TYPE_OUT,from2,null,"clipboard NOT sended"));
//#                     }
//#                     break;
//#endif
//#ifndef WMUC
                case 40: //invite
                    //new InviteForm(c, display);
                    if (c.jid!=null) {
                        new InviteForm(display, midlet.BombusQD.sd.roster, c);
                    } else {
                        MucContact mcJ=(MucContact) c;

                        if (mcJ.realJid!=null) {
                            boolean onlineConferences=false;
                            for (Enumeration cJ=midlet.BombusQD.sd.roster.getHContacts().elements(); cJ.hasMoreElements(); ) {
                                try {
                                    MucContact mcN=(MucContact)cJ.nextElement();
                                    if (mcN.origin==Constants.ORIGIN_GROUPCHAT && mcN.status==Constants.PRESENCE_ONLINE)
                                        onlineConferences=true;
                                } catch (Exception e) {}
                            }
                            if (onlineConferences) new InviteForm(display, midlet.BombusQD.sd.roster, mcJ);
                        }
                    }
                    return;
//#endif
                case 45: //direct presence
                    new StatusSelect(display, midlet.BombusQD.sd.roster, c);
                    return;
//#if (FILE_IO && FILE_TRANSFER)
                case 50: //send file
                    new TransferSendFile(display, midlet.BombusQD.sd.roster, c.getJid());
                    return;
//#endif
//#if FILE_TRANSFER
                case 51: //send photo
                    new TransferImage(display, midlet.BombusQD.sd.roster, c.getJid());
                    return;
//#endif
            }
//#ifndef WMUC
            if (c instanceof MucContact || g instanceof ConferenceGroup) {
                MucContact mc=(MucContact) c;
                
                String roomJid="";
                if (g instanceof ConferenceGroup) {
                    roomJid=((ConferenceGroup)g).confContact.getJid();
                }
                
                String myNick="";
                if (c instanceof MucContact) {
                    myNick=((ConferenceGroup)c.group).selfContact.getName();
                }
                
                switch (index) { // muc contact actions
                    case 10: // room config
                        new QueryConfigForm(display,roomJid);
                        break;
                    case 11: // owners
                    case 12: // admins
                    case 13: // members
                    case 14: // outcasts
                        new Affiliations(display, parentView, roomJid, (short)(index-10));
                        return;
                    case 22:
                        ((ConferenceGroup)g).leaveRoom();
                        break;
                    case 23:
                        ((ConferenceGroup)g).reEnterRoom();
                        return; //break;
                    case 46: //conference presence
                        new StatusSelect(display, midlet.BombusQD.sd.roster, ((ConferenceGroup)g).confContact);
                        return;
                    case 49:                  
                        new CommandForm(display,parentView,0,"Form",item,null);  
                        break;
                     case 8: // kick
                        new ConferenceQuickPrivelegeModify(display, midlet.BombusQD.sd.roster, mc, ConferenceQuickPrivelegeModify.KICK,myNick);
                        return;
                     case 9: // ban
                        new ConferenceQuickPrivelegeModify(display, midlet.BombusQD.sd.roster, mc, ConferenceQuickPrivelegeModify.OUTCAST,myNick);
                        return;
                     case 31: //grant voice and revoke moderator
                        new ConferenceQuickPrivelegeModify(display, midlet.BombusQD.sd.roster, mc, ConferenceQuickPrivelegeModify.PARTICIPANT,myNick); //
                        return;
                     case 32: //revoke voice
                        new ConferenceQuickPrivelegeModify(display, midlet.BombusQD.sd.roster, mc, ConferenceQuickPrivelegeModify.VISITOR,myNick);
                        return;
                     case 33: //grant moderator
                        new ConferenceQuickPrivelegeModify(display, midlet.BombusQD.sd.roster, mc, ConferenceQuickPrivelegeModify.MODERATOR,null); //
                        return;
                    case 35: //grant membership and revoke admin
                        new ConferenceQuickPrivelegeModify(display, midlet.BombusQD.sd.roster, mc, ConferenceQuickPrivelegeModify.MEMBER,null); //
                        return;
                    case 36: //revoke membership
                        new ConferenceQuickPrivelegeModify(display, midlet.BombusQD.sd.roster, mc, ConferenceQuickPrivelegeModify.NONE,null); //
                         return;
                    case 37: //grant admin and revoke owner
                        new ConferenceQuickPrivelegeModify(display, midlet.BombusQD.sd.roster, mc, ConferenceQuickPrivelegeModify.ADMIN,null); //
                        return;
                    case 38: //grant owner
                        new ConferenceQuickPrivelegeModify(display, midlet.BombusQD.sd.roster, mc, ConferenceQuickPrivelegeModify.OWNER,null); //
                        return;
//#ifdef REQUEST_VOICE		 
//#                 case 39: //request voice
//#                     new QueryRequestVoice(display, midlet.BombusQD.sd.roster, mc, ConferenceQuickPrivelegeModify.PARTICIPANT);
//#                     return;
//#endif
//#ifdef CLIPBOARD
//#                     case 892: //Copy JID
//#                         try {
//#                             if (mc.realJid!=null)
//#                                 midlet.BombusQD.clipboard.setClipBoard(mc.realJid);
//#                         } catch (Exception e) {}
//#                         break;
//#endif
             }
        } else {
//#endif
            Group sg=(Group)item;

            if (       sg.type!=Groups.TYPE_IGNORE 
                    && sg.type!=Groups.TYPE_NOT_IN_LIST
                    && sg.type!=Groups.TYPE_SEARCH_RESULT
                    && sg.type!=Groups.TYPE_SELF
                    && sg.type!=Groups.TYPE_TRANSP)
            {
                switch (index) {
                    case 1001: //rename
                        new RenameGroup(display, midlet.BombusQD.sd.roster, sg/*, null*/);
                        return;
                    case 1004: //delete
                        new AlertBox(SR.get(SR.MS_DELETE_GROUP_ASK), sg.getName(), display, midlet.BombusQD.sd.roster, false) {
                            public void yes() {
                                midlet.BombusQD.sd.roster.deleteGroup((Group)item);
                            }
                            public void no() {
                            }
                        };
                        return;
                }
            }
//#ifndef WMUC
         }
//#endif
     }
}
