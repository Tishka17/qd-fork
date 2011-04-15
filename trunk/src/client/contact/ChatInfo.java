/*
 * ChatInfo.java
 *
 * Created on 20.07.2009, 22:32
 *
 * Copyright (c) 2005-2008, Vladimir Kryuko
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
 */

package client.contact;

import client.Msg;
import java.util.Vector;
import message.MessageItem;

/**
 *
 * @author Vladimir Krukov
 */
public final class ChatInfo {
    public final Vector msgs = new Vector(0);

    private int newMsgCnt=0;
    private int newHighLitedMsgCnt=0;
    public byte unreadType;
    public int lastUnread;
    public boolean opened = false;

    public void initMsgs() {
        msgs.removeAllElements();
        newMsgCnt = 0;
        newHighLitedMsgCnt = 0;
    }
    private byte calcUnreadType() {
        int size = msgs.size();
        for (int i=0; i<size; ++i){
            Msg m = (Msg)msgs.elementAt(i);
            if (m.unread) {
                //MESSAGE_TYPE_AUTH=14;
                if (m.messageType==14) return m.messageType;
            }
        }
        return 10;//MESSAGE_TYPE_IN=10;
    }
    public int getNewMessageCount() {
        return newMsgCnt;
    }
    public int getNewHighliteMessageCount() {
        return newHighLitedMsgCnt;
    }
    public int getMessageCount() {
        return msgs.size();
    }

    public boolean deleteOldMessages(Vector mirror) {
        int trash = getMessageCount() - midlet.BombusQD.cf.msglistLimit;
        Msg m;
        MessageItem mi;
        for (int i = 0; i <= trash; ++i) {
            m = (Msg)msgs.elementAt(0); m.destroy();
            mi = (MessageItem)mirror.elementAt(0); mi.destroy();

            msgs.removeElementAt(0);
            mirror.removeElementAt(0);
        }
        return true;
    }

    public void markDelivered(String id) {
        if (id==null) return;
        int size = msgs.size();
        for (int i=0; i<size; ++i){
            Msg m = (Msg)msgs.elementAt(i);
            if ((null != m.id) && m.id.equals(id)) {
                m.delivered=true;
            }
        }
    }
    public byte getUnreadMessageType() {
        return unreadType;
    }
    public boolean isActiveChat() {
        return (0 < getMessageCount()) && !isOnlyStatusMessage();

    }

    public boolean isFirstMessage() {
        return (0 == getMessageCount());
    }

    public boolean isOnlyStatusMessage() {
        return (1 == getMessageCount()) && ((Msg)msgs.firstElement()).isPresence();
    }
    public void setFirstMessage(Msg m) {
        msgs.setElementAt(m, 0);
    }
    public void addMessage(Msg m) {
        if (midlet.BombusQD.cf.createMessageByFive) m.itemCollapsed=false;
        msgs.addElement(m);
        if(m.isPresence()) return;
        //System.out.println("addMessage "+opened);
        if(opened){
           newMsgCnt = 0;
           newHighLitedMsgCnt = 0;
        }
        if (m.highlite) newHighLitedMsgCnt++;
        newMsgCnt++;
        if (m.unread) {
            lastUnread = getMessageCount() - 1;
            if (m.messageType > unreadType) unreadType = m.messageType;
        }
    }

    public void reEnumCounts() {
        if(newMsgCnt==0 && newHighLitedMsgCnt==0) return;
        newMsgCnt = 0;
        newHighLitedMsgCnt = 0;
    }

    public void reEnumChat() {
        if(newMsgCnt==0) return;
          int size = msgs.size();
          for (int i=0; i<size; ++i){
            Msg m = (Msg)msgs.elementAt(i);
            if (m.unread) m.unread = false;
            m = null;
        }
        newMsgCnt = 0;
        newHighLitedMsgCnt = 0;
        midlet.BombusQD.sd.roster.countNewMsgs();
    }

    public void readMessage(Msg msg) {
        if(!opened) return;
        if(newMsgCnt==0 && newHighLitedMsgCnt==0) return;
        //if (msg.unread) { //�������������
           reEnumChat();
           msg.unread = false;
           //MESSAGE_TYPE_AUTH=14;
           if ((14 == unreadType) && (14 == msg.messageType)) unreadType = calcUnreadType();
        //}
    }
    public void resetLastUnreadMessage() {
        lastUnread = 0;
    }
    public int getLastUnreadMessageIndex() {
        return lastUnread;
    }
    public final int firstUnread() {
        int unreadIndex=0;
        int size = msgs.size();
        for (int i=0; i<size; ++i){
            Msg m = (Msg)msgs.elementAt(i);
            if (((Msg)msgs.elementAt(i)).unread) {
                break;
            }
            unreadIndex++;
        }
        return unreadIndex;
    }

    public Vector getMsgs() {
        return msgs;
    }

    public void destroy() {
        int size = msgs.size();
        if(size==0) return;
        //System.out.println("    :::Contact->chatInfo->destroy vector msgs size -> " + size);
        Msg m;
        for(int i = 0; i < size; i++){
          m = (Msg)msgs.elementAt(i);
          //System.out.println("    :::  destroy->msg#"+i+": "+m.body+"/"+m.from+"-->NULL");
          m.destroy();
        }
        msgs.removeAllElements();
    }
}
