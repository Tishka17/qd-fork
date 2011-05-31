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
import client.contact.ChatInfo;
import message.MessageItem;
import javax.microedition.lcdui.TextField;
import locale.SR;
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
import ui.controls.AlertBox;
//#ifdef CLIPBOARD
import util.ClipBoard;
//#endif
import ui.input.InputTextBox;
import ui.input.InputTextBoxNotify;

public final class ContactMessageList extends MessageList implements InputTextBoxNotify {
    Contact contact;
    private boolean startSelection;

    private Vector msgs;

    public void destroy() {
        int size = messages.size();
        //System.out.println("    :::ContactMessageList destroy->"+contact+"->chat with "+size+" messages");
        for(int i = 0; i<size; ++i){
          MessageItem mi = (MessageItem)messages.elementAt(i);
          //System.out.println("    :::   destroyMessageItem#"+i);
          mi.destroy();
          mi = null;
        }
        //System.out.println("    :::ContactMessageList destroy->"+contact+"->cml->messages&msgs");
        messages.removeAllElements();
        contact = null;
        msgs.removeAllElements();
        msgs = null;
    }

//#ifdef HISTORY
    public void storeMessage(Msg msgObj) {
        synchronized (this) {
           HistoryStorage.addText(contact, msgObj);
       }
    }
//#endif

    public void updateMainBar(Contact contact){
        setMainBarItem(new MainBar(contact));
    }

    public ContactMessageList(Contact contact) {
        this.contact=contact;
        midlet.BombusQD.sd.roster.activeContact=contact;

//#ifdef SMILES
        smiles=midlet.BombusQD.cf.smiles;
//#else
//#         smiles=false;
//#endif

        //MainBar mainbar=new MainBar(contact);
        msgs = contact.getChatInfo().getMsgs();
        updateMainBar(contact);

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
                contact.getChatInfo().reEnumCounts();
            }
            contact.historyLoaded();
        }
//#endif
    }

    private ChatInfo getChatInfo() {
        return contact.getChatInfo();
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

        try {
            Msg msg=getMessageAt(cursor);
            if (msg.messageType==Msg.MESSAGE_TYPE_AUTH) {
                addCommand(Commands.cmdSubscribe);
                addCommand(Commands.cmdUnsubscribed);
            }
        } catch (Exception e) {}

        addCommand(Commands.cmdMessage);
        if (contact.origin != Contact.ORIGIN_GROUPCHAT) {
            addCommand(Commands.cmdActions);
        }

//#ifdef HISTORY
        if (contact.origin != Contact.ORIGIN_GROUPCHAT) {
            if (Config.module_history) {
                if (Config.historyTypeIndex == Config.HISTORY_RMS) {
                    addCommand(Commands.cmdHistory);
                }
            }
        }
//#endif

        if (contact.getChatInfo().getMessageCount()>0) {
//#ifndef WMUC
            if (contact instanceof MucContact && contact.origin==Contact.ORIGIN_GROUPCHAT
                    || contact.getJid().indexOf("juick@juick.com")>-1 ) {
                addCommand(Commands.cmdReply);
            }
//#endif
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
        MessageItem mi=(MessageItem) messages.elementAt(index);
        if (mi.msg.unread) {
            getChatInfo().readMessage(mi.msg);
        }
        return mi;
    }

    public Msg getMessage(int index) {
        return (Msg) msgs.elementAt(index);
    }

    private Msg getMessageAt(int index) {
        return (Msg) msgs.elementAt(index);
    }


    private void forceScrolling() { //by voffk
        if (Config.autoScroll && isShown()) {
            if (cursor >= (messages.size() - 2)) {
                moveCursorEnd();
            }
        }
    }

    public void deleteOldMessages() {
       cursor -= getChatInfo().getMessageCount() - midlet.BombusQD.cf.msglistLimit+1;
       if (cursor<0) cursor = 0;
       getChatInfo().deleteOldMessages(messages);
    }

    protected int getItemCount(){ return messages.size(); }

//#ifdef TOUCH
    protected void touchMainPanelPressed(int x, int y) {
        int zoneWidth = width / 4;

        if (x > zoneWidth && x < width - zoneWidth) {
            contact.getChatInfo().opened = false;
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
                int size = msgs.size();
                for (int i = 0; i < size; ++i) {
                    Msg msg = (Msg)msgs.elementAt(i);
                    if (msg.selected) {
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
            BombusQD.sd.roster.showHistory(this, contact);
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
            Msg mess = getMessage(cursor);
            mess.selected = !mess.selected;
            mess.search_word = !mess.search_word;
            mess.highlite = mess.selected;
            return;
        }

        if (!midlet.BombusQD.sd.roster.isLoggedIn()) {
            return;
        }

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
            Msg msg = new Msg(Msg.MESSAGE_TYPE_OUT, from, null, body);
            msg.id = id;
            msg.itemCollapsed = true;

            try {
                if (body != null && body.length() > 0) {
                    midlet.BombusQD.sd.roster.sendMessage(contact, id, body, null, null);
                    if (contact.origin < Contact.ORIGIN_GROUPCHAT) {
                        contact.addMessage(msg);
                    }
                }
            } catch (Exception e) {
                contact.addMessage(new Msg(Msg.MESSAGE_TYPE_OUT, from, null, SR.get(SR.MS_CLIPBOARD_SENDERROR)));
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
                m.search_word = false;
                m.highlite = false;
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
                m.search_word = true;
                m.highlite = true;
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

    private void answerMucContact(){
       Msg msg;
       boolean found=false;
       try {
           msg=getMessage(cursor);
           int size=midlet.BombusQD.sd.roster.contactList.contacts.size();
           Contact c;
           if(!found) {
             for(int i=0;i<size;i++){
              c=(Contact)midlet.BombusQD.sd.roster.contactList.contacts.elementAt(i);
              if (c instanceof MucContact){
                if(c.jid.getBareJid().equals(contact.jid.getBareJid()) && c.getNick().equals(msg.from)) found=(c.status!=5);
               }
             }
           }
       } catch (Exception e) { msg = null; }

       if(!found&&msg!=null) {
            AlertBox box = new AlertBox(msg.from, SR.get(SR.MS_ALERT_CONTACT_OFFLINE), AlertBox.BUTTONS_YESNO) {
                public void yes() { reply(true); }
            };
            box.show();
       } else reply(true);
       msg=null;
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
                    msg.messageType == Msg.MESSAGE_TYPE_OUT ||
                    msg.messageType == Msg.MESSAGE_TYPE_SUBJ) {
                newMessage();
            } else {
                String text;
//#ifdef JUICK.COM
               if(msg.messageType==Msg.MESSAGE_TYPE_JUICK){
                    text=util.StringUtils.replaceNickTags(msg.id);
               } else {
//#endif
                   text = msg.from+": ";
//#ifdef JUICK.COM
              }
//#endif
             if(contact.msgSuspended != null && check) {
               final String msgText = text;
               AlertBox box = new AlertBox(msg.from, SR.get(SR.MS_MSGBUFFER_NOT_EMPTY), AlertBox.BUTTONS_YESNO) {
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
        if (midlet.BombusQD.sd.roster.isLoggedIn()) {
            if (Config.createMessageByFive) {
                super.eventOk();
            } else {
                resumeMessage();
            }
        }
    }

    public void keyPressed(int keyCode) {
        if (gm.itemGrMenu < 0 
//#ifdef POPUPS 
                && getPopUp().size() == 0
//#endif
                ) {
            switch (keyCode) {
                case KEY_NUM5:
                    if (Config.createMessageByFive) {
                        answer();
                    } else {
                        super.eventOk();
                    }
                    break;
                case KEY_POUND:
                    if (!Config.createMessageByFive) {
                        answer();
                    } else {
                        // for message collapsing
                        super.eventOk();
                    }
                    break;
    //#ifdef SMILES
                case KEY_STAR:
                    if (getItemCount() > 0) {
                        ((MessageItem)getFocusedObject()).toggleSmiles(this);
                    }
                    break;
    //#endif
                default:
                    super.keyPressed(keyCode);
            }
        } else {
            super.keyPressed(keyCode);
        }
    }
    
    public void keyRepeated(int keyCode) {
        if (keyCode == KEY_NUM0) {
            clearReadedMessageList();
        } else {
            super.keyRepeated(keyCode);
        }
    }
    
    public void userKeyPressed(int keyCode) {
     if(midlet.BombusQD.cf.find_text){//next rev
          switch (keyCode) {
              case KEY_NUM4: {
                     if(found_count>0) found_count--;
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
              case KEY_NUM6: {
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
          }
       }
       else
       {
          switch (keyCode) {
            case KEY_NUM4:
                if (midlet.BombusQD.cf.useTabs)
                    midlet.BombusQD.sd.roster.searchActiveContact(contact, false);
                else
                    super.pageLeft();
                break;
            case KEY_NUM0:
                int size = midlet.BombusQD.sd.roster.contactList.contacts.size();
                Contact c;
                for(int i=0;i<size;i++){
                        c = (Contact)midlet.BombusQD.sd.roster.contactList.contacts.elementAt(i);
                        if (c.getNewMsgsCount()>0){
                            contact.getChatInfo().opened = false;
                            c.getMessageList().show();
                           break;
                        }
                    }
                  break;
            case KEY_NUM6:
                if (midlet.BombusQD.cf.useTabs)
                    midlet.BombusQD.sd.roster.searchActiveContact(contact, true);
                else
                    super.pageRight();
                break;
            case KEY_NUM3:
                try {
                    contact.getChatInfo().opened = false;
                    midlet.BombusQD.sd.roster.showActiveContacts(contact);
                } catch (NullPointerException e){}
                break;
            case KEY_NUM9:
                if (BombusQD.sd.roster.isLoggedIn()) {
                    quoteMessage();
                }
                break;
            }
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
            answerMucContact();
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

    /*public void resetMessages() {
        //System.out.println("resetMessages");
        messages.removeAllElements();
        Msg msg;
        MessageItem mi;
        for (int i = messages.size(); i < msgs.size(); ++i) {
            msg = (Msg)msgs.elementAt(i);
            mi = new MessageItem(msg, smiles);
            mi.setEven((messages.size() & 1) == 0);
            mi.parse(this);
            //mi.getColor();
            messages.addElement(mi);
        }
        mi = null;
    }*/

    public void addMessage(Msg msg) {
        ChatInfo chatInfo = contact.getChatInfo();
        
        chatInfo.addMessage(msg);
        if(chatInfo.opened || msg.messageType == Msg.MESSAGE_TYPE_OUT) {
            chatInfo.reEnumCounts();
        }
        MessageItem mi = new MessageItem(msg, smiles);
        mi.setEven((messages.size() & 1) == 0);
        mi.parse(this);
        messages.addElement(mi);
        mi = null;
        forceScrolling();
        redraw();
    }

    private void smartPurge(boolean presence) {
        int cur=cursor+1;
        try {
            if (msgs.size()>0){
                int virtCursor=msgs.size();
                boolean delete = false;
                int i=msgs.size();
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

        if(msgs.isEmpty()) {
          contact.clearVCard();
        }
        contact.getChatInfo().resetLastUnreadMessage();
    }

    public void destroyView(){
        if (contact!=null)
            contact.getChatInfo().opened = false;

        midlet.BombusQD.sd.roster.activeContact=null;
        midlet.BombusQD.sd.roster.reEnumRoster(); //to reset unread messages icon for this conference in roster
        midlet.BombusQD.sd.roster.show();
    }

    private void removeMessage(int msgIndex) {
        if (-1 == msgIndex) {
            return;
        }
        getChatInfo().readMessage(getMessage(msgIndex));
        getMessage(msgIndex).destroy();

          MessageItem mi = (MessageItem)messages.elementAt(msgIndex);
          mi.destroy();
          mi = null;
        msgs.removeElementAt(msgIndex);
        messages.removeElementAt(msgIndex);
    }

    public int showGraphicsMenu() {
         GMenuConfig.getInstance().itemGrMenu = GMenu.CONTACT_MSGS_LIST;
         commandState();
         menuItem = new GMenu(this, null, menuCommands, cmdfirstList, cmdsecondList, cmdThirdList);
         redraw();
        return GMenu.CONTACT_MSGS_LIST;
    }
}
