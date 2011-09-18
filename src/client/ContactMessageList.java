/*
 * ContactMessageList.java
 *
 * Created on 19.02.2005, 23:54
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
//#ifndef WMUC
import conference.MucContact;
//#endif
import javax.microedition.lcdui.TextField;
import locale.SR;
//#ifdef JUICK.COM
import xmpp.extensions.JuickModule;
//#endif
import ui.MainBar;
import java.util.Vector;
import ui.VirtualElement;
import ui.VirtualList;
import menu.Command;
//#ifdef ARCHIVE
import archive.MessageArchive;
//#endif
import ui.GMenu;
import ui.GMenuConfig;
//#ifdef HISTORY
import history.HistoryStorage;
//#endif
import message.MessageList;
import midlet.BombusQD;
import midlet.Commands;
import ui.VirtualCanvas;
import ui.controls.AlertBox;
//#ifdef CLIPBOARD
import ui.controls.form.SpacerItem;
import util.ClipBoard;
//#endif
import ui.input.InputTextBox;
import ui.input.InputTextBoxNotify;

public final class ContactMessageList extends MessageList implements InputTextBoxNotify {
    Contact contact;
    private boolean startSelection;
    
    private int newMsgCnt = 0;
    private int newHighLitedMsgCnt = 0;
    public byte unreadType;
    public int lastUnread;

    public ContactMessageList(Contact contact) {
//#ifdef DEBUG
//#         System.out.println("Create MessageList for " + contact.getNickJid());
//#endif
        this.contact = contact;
        updateMainBar(contact);
        
        midlet.BombusQD.sd.roster.activeContact = contact;

        cursor=0;
        contact.setIncoming(0);
//#ifdef FILE_TRANSFER
        contact.fileQuery=false;
//#endif
        enableListWrapping(false);
        
//#ifdef HISTORY
        if (Config.getInstance().loadLastMsgCount > 0 && Config.module_history
                && !contact.isHistoryLoaded()) {
            Vector vector = HistoryStorage.getLastMessages(
                    contact, Config.getInstance().loadLastMsgCount);

            if (vector != null) {
                int size = vector.size();
                for (int i = 0; i < size; ++i) {
                    addMessage((Msg)vector.elementAt(i));
                }
                reEnumCounts();
            }
            contact.historyLoaded();
        }
//#endif
    }
    
    public void show() {
        BombusQD.sd.roster.activeContact = contact;
        contact.setIncoming(0);
//#ifdef FILE_TRANSFER
        contact.fileQuery = false;
//#endif
        updateMainBar(contact);
        super.show();
    }
    
//#ifdef HISTORY
    public void storeMessage(Msg msg) {
        synchronized (this) {
           HistoryStorage.addText(contact, msg);
       }
    }
//#endif

    public void updateMainBar(Contact contact){
        setMainBarItem(new MainBar(contact));
    }

    public void commandState(){
        menuCommands.removeAllElements();
        cmdfirstList.removeAllElements();
        cmdsecondList.removeAllElements();
        cmdThirdList.removeAllElements();

        if (contact==null) return;
        if (startSelection) {
            addCommand(Commands.cmdSelect);
        }
        if (contact.msgSuspended != null) {
            addCommand(Commands.cmdResume);
        }

        if (getItemCount() > 0) {
            Msg msg = getSelectedMessage();
            if (msg.getType() == Msg.AUTH) {
                addCommand(Commands.cmdSubscribe);
                addCommand(Commands.cmdUnsubscribed);
            }
        }

        final boolean isConference = contact.origin == Contact.ORIGIN_GROUPCHAT;
        final boolean isMucContact = contact instanceof MucContact;
        final boolean isJuickContact = contact.getJid().indexOf("juick@juick.com") != -1;

        addCommand(Commands.cmdMessage);
        if (isConference || isJuickContact) {
            addCommand(Commands.cmdReply);
        }
        if (getItemCount() > 0) {
//#ifdef JUICK.COM
            if (isJuickContact) {
                addCommand(Commands.cmdJuickMenu);

                addInCommand(1, Commands.cmdJuickLastPopular);
                addInCommand(1, Commands.cmdJuickLastMsgs);
                addInCommand(1, Commands.cmdJuickSubscribe);
                addInCommand(1, Commands.cmdJuickUnsubscribe);
                addInCommand(1, Commands.cmdJuickSendPM);
                addInCommand(1, Commands.cmdJuickUsersMsgs);
            }
//#endif
        }

         addCommand(Commands.cmdActions);

//#ifdef HISTORY
        if (Config.module_history && contact.hasHistory()) {
            if (Config.historyTypeIndex == Config.HISTORY_RMS) {
                addCommand(Commands.cmdHistory);
            }
        }
//#endif

        if (getItemCount() > 0) {
            addCommand(Commands.cmdQuote);
            addCommand(Commands.cmdMyService);

            addInCommand(3, Commands.cmdPurge);
            addInCommand(3, Commands.cmdAddSearchQuery);
            addInCommand(3, Commands.cmdClrPresences);

            if (!startSelection) {
                addInCommand(3, Commands.cmdSelect);
            }

//#ifdef ARCHIVE
            addCommand(Commands.cmdArch);
//#endif

//#ifdef CLIPBOARD
            if (Config.useClipBoard) {
                addCommand(Commands.cmdCopy);
                if (!ClipBoard.isEmpty()) {
                    addCommand(Commands.cmdCopyPlus);
                    addCommand(Commands.cmdPaste);
                }
            }
//#endif
            if (hasScheme()) {
                addInCommand(3, Commands.cmdxmlSkin);
            }
            if (hasUrl()) {
                addCommand(Commands.cmdUrl);
            }
        }

//#ifdef CLIPBOARD
        if (Config.useClipBoard && !ClipBoard.isEmpty()) {
            addInCommand(3, Commands.cmdSendBuffer);
        }
//#endif
    }

    public void showNotify(){
        midlet.BombusQD.sd.roster.activeContact=contact;
        super.showNotify();
    }

    public VirtualElement getItemRef(int index) {
        try {
            Msg mi = (Msg)messages.elementAt(index);
            if (mi.isUnread()) {
                readMessage(mi);
            }
            return mi;
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }
    
    public int getMessageCount() {
        return getItemCount();
    }

    private void forceScrolling() { //by voffk
        if (Config.autoScroll && isShown()) {
            if (cursor >= (messages.size() - 2)) {
                moveCursorEnd();
            }
        }
    }

    public void deleteOldMessages() {
        int endPos = getMessageCount() - midlet.BombusQD.cf.msglistLimit;
        for (int i = 0; i <= endPos; ++i) {
            messages.removeElementAt(0);
        }
    }

//#ifdef TOUCH
    protected void touchMainPanelPressed(int x, int y) {
        int zoneWidth = width / 4;
        if (x > zoneWidth && x < width - zoneWidth) {
            BombusQD.sd.roster.showActiveContacts(contact);
        } else if (x < zoneWidth) {
            BombusQD.sd.roster.searchActiveContact(contact, false);
        } else {
            BombusQD.sd.roster.searchActiveContact(contact, true);
        }
    }
//#endif

    public void commandAction(Command c) {
        if (c == Commands.cmdClrPresences) {
            smartPurge(true);
            return;
        } else if (c == Commands.cmdPurge) {
            if (messages.isEmpty()) {
                return;
            }
            if (startSelection) {
                int size = getMessageCount();
                for (int i = 0; i < size; ++i) {
                    Msg msg = getMessage(i);
                    if (msg.isSelected()) {
                        removeMessage(i);
                    }
                }
                startSelection = false;
            } else {
                clearReadedMessageList();
            }
            return;
//#ifdef HISTORY
        } else if (c == Commands.cmdHistory) {
            new history.HistoryViewer(contact).show();
//#endif
//#ifdef ARCHIVE
        } else if (c == Commands.cmdArch) {
            try {
                MessageArchive.store(util.StringUtils.replaceNickTags(getMessage(cursor)));
            } catch (Exception e) {/*no messages*/

            }
//#endif
        } else if (c == Commands.cmdSelect) {
            startSelection = true;
            Msg msg = getMessage(cursor);
            if (msg.isSelected()) {
                msg.reset();
            } else {
                msg.highlite();
            }
            return;
        }

        if (!midlet.BombusQD.sd.roster.isLoggedIn()) {
            return;
        }
//#ifdef JUICK.COM
        if (c == Commands.cmdJuickLastMsgs){
            JuickModule.LastMessages();
        }
        else if (c == Commands.cmdJuickLastPopular){
        JuickModule.ShowMyFeed();
        }
//#endif
        if (c == Commands.cmdMessage) {
            newMessage();
//#ifdef CLIPBOARD
        } else if (c == Commands.cmdPaste) {
            showMsgEdit(ClipBoard.getClipBoard());
//#endif
        } else if (c == Commands.cmdResume) {
            resumeMessage();
        } else if (c == Commands.cmdQuote) {
            quoteMessage();
        } else if (c == Commands.cmdReply) {
            answer();
        } else if (c == Commands.cmdAddSearchQuery) {
            InputTextBox input = new InputTextBox(SR.get(SR.MS_SEARCH), null, 30, TextField.ANY);
            input.setNotifyListener(this);
            input.show();
        } else if (c == Commands.cmdActions) {
            BombusQD.sd.roster.showActionsMenu(contact);
        } else if (c == Commands.cmdSubscribe) {
            midlet.BombusQD.sd.roster.doSubscribe(contact);
        } else if (c == Commands.cmdUnsubscribed) {
            midlet.BombusQD.sd.roster.sendPresence(contact.bareJid, "unsubscribed", null, false);
//#ifdef CLIPBOARD
        } else if (c == Commands.cmdSendBuffer) {
            String from = midlet.BombusQD.sd.account.toString();
            String body = ClipBoard.getClipBoard();

            String id = String.valueOf((int) System.currentTimeMillis());
            Msg msg = new Msg(Msg.OUTGOING, from, null, body);
            msg.setId(id);
            msg.collapse();

            try {
                if (body != null && body.length() > 0) {
                    midlet.BombusQD.sd.roster.sendMessage(contact, id, body, null, null);
                    if (contact.origin < Contact.ORIGIN_GROUPCHAT) {
                        contact.addMessage(msg);
                    }
                }
            } catch (Exception e) {
                contact.addMessage(new Msg(Msg.OUTGOING, from, null, SR.get(SR.MS_CLIPBOARD_SENDERROR)));
            }
            redraw();
//#endif
        } else {
            super.commandAction(c);
        }
    }

    private String searchQuery;

    public void okNotify(String searchQuery) {
        if (searchQuery.length() > 0) {
            this.searchQuery = searchQuery;
            findString(searchQuery);
        }
    }

    public void clearReadedMessageList() {
        smartPurge(false);
        cursor=0;
        moveCursorHome();
        redraw();
    }

    private Vector vectorfound = new Vector(0);
    private int found_count=0;

    private void clearResults() {
        moveCursorEnd();
        for (int i = 0; i < (cursor + 1); ++i) {
            Msg m = getMessage(i);
            if (m.toString().indexOf(searchQuery) > -1) {
                m.reset();
            }
        }
        midlet.BombusQD.cf.find_text = false;
        moveCursorHome();
    }

    public void findString(String query) {
        moveCursorEnd();
        for (int i = 0; i < (cursor + 1); ++i) {
            Msg m = getMessage(i);
            if (m.toString().indexOf(query) > -1) {
                // FIXME элементы не удаляются.
                vectorfound.addElement(Integer.toString(i));
                m.found();
                m.highlite();
            }
        }
        if (vectorfound.size() > 0) {
            int cursor_index = Integer.parseInt(vectorfound.elementAt(found_count).toString());
            moveCursorTo(cursor_index, true);
            midlet.BombusQD.cf.find_text = true;
        } else {
            midlet.BombusQD.cf.find_text = false;
            moveCursorHome();
        }
    }

    private void showMsgEdit(String msgText){
        contact.msgSuspended = null;
        midlet.BombusQD.sd.roster.showMsgEditor(contact, msgText);
    }

    private void reply(boolean check) {
        if (!midlet.BombusQD.sd.roster.isLoggedIn()) return;

        try {
            Msg msg = getMessage(cursor);
            if (msg == null ||
                    msg.getType() == Msg.OUTGOING ||
                    msg.getType() == Msg.SUBJECT) {
                newMessage();
            } else {
                String text;
//#ifdef JUICK.COM
               if(msg.getType() == Msg.JUICK){
                    text=util.StringUtils.replaceNickTags(msg.getId());
               } else {
//#endif
                   text = msg.getFrom()+": ";
//#ifdef JUICK.COM
              }
//#endif
             if(contact.msgSuspended != null && !contact.msgSuspended.equals(text) && check) {
               final String msgText = text;
               AlertBox box = new AlertBox(msg.getFrom(), SR.get(SR.MS_MSGBUFFER_NOT_EMPTY), AlertBox.BUTTONS_YESNO) {
                    public void yes() { showMsgEdit(msgText); }
                    public void no()  { resumeMessage(); }
               };
               box.show();
               return;
             } else {
                showMsgEdit(text);
             }
          }
        } catch (Exception e) {/*no messages*/}
    }
    
    public void eventOk() {
        if (Config.createMessageByFive) {
//#ifdef TOUCH
            if (pointer_state!=POINTER_NONE) 
                answer();
            else
//#endif
                resumeMessage();
        } else {
            super.eventOk();
        }
    }

    public void eventLongOk(){
        if (!Config.getInstance().createMessageByFive) {
            answer();
        } else {
            // for message collapsing
            super.eventOk();
        }
    }

    protected void keyClear(){
        if (!messages.isEmpty()) clearReadedMessageList();
    }
    
    public void keyGreen() {
        resumeMessage();
    }

    public void keyPressed(int keyCode) {
        if (gm.itemGrMenu < 0 
//#ifdef POPUPS 
                && getPopUp().size() == 0
//#endif
                ) {
            switch (keyCode) {
                case VirtualCanvas.KEY_NUM5:
                    if (Config.createMessageByFive) {
                        answer();
                    } else {
                        super.eventOk();
                    }
                    break;
                case VirtualCanvas.KEY_POUND:
                    if (Config.createMessageByFive) {
                        super.eventOk();// for message collapsing
                    } else {
                        answer();
                    }
                    break;
//#ifdef SMILES
                case VirtualCanvas.KEY_STAR:
                    if (getItemCount() > 0) {
                        ((Msg)getFocusedObject()).toggleSmiles(this);
                        redraw();
                    }
                    break;
//#endif
            case VirtualCanvas.KEY_NUM4:
                if(midlet.BombusQD.cf.find_text) {
                    if(found_count>0) 
                        found_count--;
                    else {
//#ifdef POPUPS
                          VirtualList.setWobble(1, null, SR.get(SR.MS_END_SEARCH));
//#endif
                          clearResults();
                    }
                    int cursor_index = Integer.parseInt(vectorfound.elementAt(found_count).toString());
                    moveCursorTo(cursor_index, true);
                    redraw();
                    break;
                }
                if (midlet.BombusQD.cf.useTabs)
                    midlet.BombusQD.sd.roster.searchActiveContact(contact, false);
                else
                    super.pageLeft();
                break;
            case VirtualCanvas.KEY_NUM0:
                int size = midlet.BombusQD.sd.roster.contactList.contacts.size();
                Contact c;
                for(int i=0;i<size;i++){
                      c = (Contact)midlet.BombusQD.sd.roster.contactList.contacts.elementAt(i);
                      if (c.getNewMessageCount() > 0){
                           c.getMessageList().show();
                           break;
                      }
                }
                break;
            case VirtualCanvas.KEY_NUM6:
                if(midlet.BombusQD.cf.find_text){
                    if(found_count<vectorfound.size()-1) found_count++;
                    else {
//#ifdef POPUPS
                        VirtualList.setWobble(1, null, SR.get(SR.MS_END_SEARCH));
//#endif
                        clearResults();
                    }
                    int cursor_index = Integer.parseInt(vectorfound.elementAt(found_count).toString());
                    moveCursorTo(cursor_index, true);
                    redraw();
                    break;
                }
                if (midlet.BombusQD.cf.useTabs)
                    midlet.BombusQD.sd.roster.searchActiveContact(contact, true);
                else
                    super.pageRight();
                break;
            case VirtualCanvas.KEY_NUM3:
                try {
                    midlet.BombusQD.sd.roster.showActiveContacts(contact);
                } catch (NullPointerException e){}
                break;
            case VirtualCanvas.KEY_NUM9:
                if (BombusQD.sd.roster.isLoggedIn()) {
                    quoteMessage();
                }
                break;
            default:
                super.keyPressed(keyCode);
                break;
            }
        } else {
            super.keyPressed(keyCode);
        }
    }

    public void switchChat( ){
        Vector contacts= midlet.BombusQD.sd.roster.contactList.contacts;
        for( int i= 0; i < contacts.size(); i++){
            if( ((Contact)contacts.elementAt( i)).getNewMessageCount() > 0){
                ((Contact)contacts.elementAt( i)).getMessageList().show();
            }
        }
    }// switchChat

    public boolean keyLong(int keyCode) {
        switch (keyCode) {
            case VirtualCanvas.NAVIKEY_FIRE:
                if (Config.createMessageByFive) {
                    super.eventOk();
                } else {
                    keyGreen();
                }
                return true;
            case VirtualCanvas.KEY_NUM5:
                if (Config.createMessageByFive) {
                    super.eventOk();
                } else {
                    answer();
                }
                return true;
            case VirtualCanvas.KEY_NUM0:
                clearReadedMessageList();
                return true;
            default:
                return super.keyLong(keyCode);
        }
    }
    
    public void newMessage() {
        if (midlet.BombusQD.sd.roster.isLoggedIn()) {
            showMsgEdit(null);
        }
    }
   
    public void resumeMessage() {
        if (midlet.BombusQD.sd.roster.isLoggedIn()) {
            showMsgEdit(contact.msgSuspended);
        }
    }

    private void answer() {
//#ifndef WMUC
        if (contact instanceof MucContact && contact.origin == Contact.ORIGIN_GROUPCHAT) {
           Msg msg;
           boolean found=false;
           try {
               msg=getMessage(cursor);
               int size=midlet.BombusQD.sd.roster.contactList.contacts.size();
               Contact c;
               for(int i=0;i<size;i++){
                   c = (Contact)midlet.BombusQD.sd.roster.contactList.contacts.elementAt(i);
                   if (c instanceof MucContact){
                       if((c.origin==Contact.ORIGIN_GROUPCHAT && c.jid.getBareJid().equals(msg.getFrom())) 
                               || (c.jid.getBareJid().equals(contact.jid.getBareJid()) && c.getName().equals(msg.getFrom()))) 
                           found = (c.status!=5);
                   }
               }
           } catch (Exception e) { msg = null; }

           if(!found && msg!=null) {
               AlertBox box = new AlertBox(msg.getFrom(), SR.get(SR.MS_ALERT_CONTACT_OFFLINE), AlertBox.BUTTONS_YESNO) {
                   public void yes() { reply(true); }
               };
               box.show();
           } else {
               reply(true);
           }
           msg=null;
           return;
//#ifdef JUICK.COM
        } else {
            if (contact.getJid().indexOf("juick@juick.com") > -1) {
                reply(false);
                return;
            }
//#endif
        }
//#endif
        resumeMessage();
    }

    public void touchRightPressed(){
        if (midlet.BombusQD.cf.oldSE) {
            showGraphicsMenu();
        } else{
            destroyView();
        }
    }

    public void touchMiddlePressed(){
        resumeMessage();
    }
    
    public void touchLeftPressed(){
        if (midlet.BombusQD.cf.oldSE) {
            destroyView();
        } else {
            showGraphicsMenu();
        }
    }

    private void quoteMessage() {
        try {
            String msg=new StringBuffer(0)
                .append((char)0xab) //
                .append(util.StringUtils.quoteString(
                    util.StringUtils.replaceNickTags(getMessage(cursor))))
                .append((char)0xbb)
                .append("\n")
                .toString();
                showMsgEdit(msg);
            msg=null;
        } catch (Exception e) {/*no messages*/}
    }

    public void addMessage(Msg msg) {
        int msgCount = getItemCount();
        if (msgCount >= midlet.BombusQD.cf.msglistLimit) {
            deleteOldMessages();
        }
        msg.setEven((messages.size() & 1) == 0);
        messages.addElement(msg);
        if(!msg.isPresence()) {
            if(isShown()) {
               newMsgCnt = 0;
               newHighLitedMsgCnt = 0;
            }
            if (msg.isHighlite()) {
                newHighLitedMsgCnt++;
            }
            newMsgCnt++;
            if (msg.isUnread()) {
                lastUnread = getMessageCount() - 1;
                if (msg.getType() > unreadType) {
                    unreadType = msg.getType();
                }
            }
        }
        if(isShown() || msg.getType() == Msg.OUTGOING) {
            reEnumCounts();
        }
        forceScrolling();
        redraw();
    }

    private void smartPurge(boolean presence) {
        int cur=cursor+1;
        try {
            if (getItemCount() > 0){
                int virtCursor=getItemCount();
                boolean delete = false;
                int i=getItemCount();
                while (true) {
                    if (i<0) break;
                    if (i<cur) {
                        if (delete == false) {
                            //System.out.println("not found else");
                            if(presence){
                                if(getMessage(virtCursor).isPresence()){
                                    removeMessage(virtCursor);
                                    delete=true;
                                }
                            }
                            else if (getMessage(virtCursor).dateGmt+1000<System.currentTimeMillis()) {
                                //System.out.println("can delete: "+ delPos);
                                removeMessage(virtCursor);
                                //delPos--;
                                delete=true;
                            }
                        } else {
                            //System.out.println("delete: "+ delPos);
                            if(presence) {
                                if(getMessage(virtCursor).isPresence()) removeMessage(virtCursor);
                            }
                            else removeMessage(virtCursor);
                            //delPos--;
                        }
                    }
                    virtCursor--;
                    i--;
                }
            }
        } catch (Exception e) { }

        if(getItemCount() == 0) {
          contact.clearVCard();
        }
        resetLastUnreadMessage();
    }

    public void updateSeparator() {
        if (messages.isEmpty()) {
            return;
        }
        Msg m;
        //удаление старого разделителя
        int size = getItemCount();
        for (int i = size-1; i >=0; i--) {
            m = getMessage(i);
            if ((null != m.getId()) && m.getId().equals("spacer")) {
                messages.removeElement(m);
                if (cursor>i)
                    cursor--;
                break;
            }
        }
        if (messages.isEmpty()) {
            return;
        }
        //добавление нового разделителя
        m = new Msg(Msg.PRESENCE, null, "");
        m.setId("spacer");
        m.read();
        messages.addElement(m);
        if (m.attachment==null) m.attachment = new SpacerItem();
    }
    
    public void destroyView(){
        updateSeparator();
        midlet.BombusQD.sd.roster.activeContact=null;
        midlet.BombusQD.sd.roster.reEnumRoster(); //to reset unread messages icon for this conference in roster
        midlet.BombusQD.sd.roster.show();
    }

    private void removeMessage(int msgIndex) {
        if (-1 == msgIndex) {
            return;
        }
        messages.removeElementAt(msgIndex);
    }

    public int showGraphicsMenu() {
         GMenuConfig.getInstance().itemGrMenu = GMenu.CONTACT_MSGS_LIST;
         commandState();
         menuItem = new GMenu(this, null, menuCommands, cmdfirstList, cmdsecondList, cmdThirdList);
         redraw();
        return GMenu.CONTACT_MSGS_LIST;
    }


    public void readMessage(Msg msg) {
        if (!isShown()) {
            return;
        }
        if (newMsgCnt == 0 && newHighLitedMsgCnt == 0) {
            return;
        }
        reEnumChat();
        msg.read();
        if ((14 == unreadType) && (14 == msg.getType())) {
            unreadType = calcUnreadType();
        }
    }
    
    public void markDelivered(String id) {
        if (id == null) {
            return;
        }
        Msg m;
        int size = getItemCount();
        for (int i = 0; i < size; ++i) {
            m = getMessage(i);
            if ((null != m.getId()) && m.getId().equals(id)) {
                m.setDelivered(true);
            }
        }
    }
    
    private byte calcUnreadType() {
        Msg m;
        int size = getItemCount();
        for (int i = 0; i < size; ++i) {
            m = getMessage(i);
            if (m.isUnread()) {
                if (m.getType() == 14) {
                    return m.getType();
                }
            }
        }
        return 10;
    }
    
    public void reEnumCounts() {
        /*if (newMsgCnt == 0 && newHighLitedMsgCnt == 0) {
            return;
        }*/
        newMsgCnt = 0;
        newHighLitedMsgCnt = 0;
    }
    
    public void reEnumChat() {
        if (newMsgCnt == 0) {
            return;
        }
        Msg m;
        int size = getItemCount();
        for (int i = 0; i < size; ++i) {
            m = getMessage(i);
            m.read();
        }
        newMsgCnt = 0;
        newHighLitedMsgCnt = 0;
        midlet.BombusQD.sd.roster.countNewMsgs();
    }
    
    public void resetLastUnreadMessage() {
        lastUnread = 0;
    }
    
    public int getNewMessageCount() {
        return newMsgCnt;
    }

    public int getNewHighliteMessageCount() {
        return newHighLitedMsgCnt;
    }
    
    public byte getUnreadMessageType() {
        return unreadType;
    }
}
