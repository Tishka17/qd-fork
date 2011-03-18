/*
 * Constants.java
 *
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
import colors.ColorTheme;
import images.RosterIcons;
/**
 *
 * @author aqent
 */
public final class Constants {

     public final static String NS_ITEMS="http://jabber.org/protocol/disco#items";
     public final static String NS_INFO="http://jabber.org/protocol/disco#info";
     public final static String NS_REGS="jabber:iq:register";
     public final static String NS_SRCH="jabber:iq:search";
     public final static String NS_GATE="jabber:iq:gateway";
     public final static String NS_MUC="http://jabber.org/protocol/muc";
     public final static String NODE_CMDS="http://jabber.org/protocol/commands";
     
     public final static String PRS_OFFLINE="unavailable";
     public final static String PRS_ERROR="error";
     public final static String PRS_CHAT="chat";
     public final static String PRS_AWAY="away";
     public final static String PRS_XA="xa";
     public final static String PRS_DND="dnd";
     public final static String PRS_ONLINE="online";
     public final static String PRS_INVISIBLE="invisible";    
     
     public final static byte PRESENCE_ONLINE=0;
     public final static byte PRESENCE_CHAT=1;
     public final static byte PRESENCE_AWAY=2;
     public final static byte PRESENCE_XA=3;
     public final static byte PRESENCE_DND=4;
     public final static byte PRESENCE_OFFLINE=5;
     public final static byte PRESENCE_ASK=6;
     public final static byte PRESENCE_UNKNOWN=7;
     public final static byte PRESENCE_INVISIBLE=RosterIcons.ICON_INVISIBLE_INDEX;
     public final static byte PRESENCE_ERROR=RosterIcons.ICON_ERROR_INDEX;
     public final static byte PRESENCE_TRASH=RosterIcons.ICON_TRASHCAN_INDEX;
     public final static byte PRESENCE_AUTH=-1;
     public final static byte PRESENCE_AUTH_ASK=-2;
     public final static byte PRESENCE_SAME=-100;    
     
     
     public final static int getMessageColor(int messageType) {
         switch (messageType) {
             case MESSAGE_TYPE_IN: return ColorTheme.getColor(ColorTheme.MESSAGE_IN);
             case MESSAGE_TYPE_PRESENCE: return ColorTheme.getColor(ColorTheme.MESSAGE_PRESENCE);
             case MESSAGE_TYPE_OUT: return ColorTheme.getColor(ColorTheme.MESSAGE_OUT);
             case MESSAGE_TYPE_SUBJ:return ColorTheme.getColor(ColorTheme.MSG_SUBJ);
             case MESSAGE_TYPE_HEADLINE: return ColorTheme.getColor(ColorTheme.MESSAGE_IN);
             case MESSAGE_TYPE_AUTH: return ColorTheme.getColor(ColorTheme.MESSAGE_AUTH);
             case MESSAGE_TYPE_EVIL: return 0xFF0000;
             case MESSAGE_TYPE_HISTORY: return ColorTheme.getColor(ColorTheme.MESSAGE_HISTORY);
 //#ifdef JUICK.COM    
//#             case MESSAGE_TYPE_JUICK: return ColorTheme.getColor(ColorTheme.MESSAGE_IN);
 //#endif
         }
         return ColorTheme.getColor(ColorTheme.LIST_INK);
     }
         
    // without signaling
    public final static byte MESSAGE_TYPE_OUT=1;
    public final static byte MESSAGE_TYPE_PRESENCE=2;
    public final static byte MESSAGE_TYPE_HISTORY=3;
    // with signaling
    public final static byte MESSAGE_TYPE_IN=10;
    public final static byte MESSAGE_TYPE_HEADLINE=11;
    public final static byte MESSAGE_TYPE_ERROR=12;
    public final static byte MESSAGE_TYPE_SUBJ=13;
    public final static byte MESSAGE_TYPE_AUTH=14;
    public final static byte MESSAGE_TYPE_SYSTEM=15;
    public final static byte MESSAGE_TYPE_EVIL=16;
    //public final static byte MESSAGE_TYPE_GAME=17;
//#ifdef JUICK.COM    
//#     public final static byte MESSAGE_TYPE_JUICK=18;   
//#endif    
    

    public final static byte ORIGIN_ROSTER=0;
    public final static byte ORIGIN_ROSTERRES=1;
    public final static byte ORIGIN_CLONE=2;
    public final static byte ORIGIN_PRESENCE=3;
    public final static byte ORIGIN_GROUPCHAT=4;
//#ifndef WMUC
    public final static byte ORIGIN_GC_MEMBER=5;
    public final static byte ORIGIN_GC_MYSELF=6;
//#endif
    
    
    public final static byte INC_NONE=0;
    public final static byte INC_APPEARING=1;
    public final static byte INC_VIEWING=2;
    
    
    public final static byte AFFILIATION_MEMBER=1;
    public final static byte AFFILIATION_NONE=0;
    public final static byte ROLE_VISITOR=-1;
    public final static byte ROLE_PARTICIPANT=0;
    public final static byte ROLE_MODERATOR=1;    
    public final static byte AFFILIATION_OUTCAST=-1;
    public final static byte AFFILIATION_ADMIN=2;
    public final static byte AFFILIATION_OWNER=3;

    public final static byte GROUP_VISITOR=4;
    public final static byte GROUP_MEMBER=3;
    public final static byte GROUP_PARTICIPANT=2;
    public final static byte GROUP_MODERATOR=1;

//#ifdef TOUCH
    public final static byte POINTER_NONE=0;
    public final static byte POINTER_SECOND=1;
    public final static byte POINTER_SCROLLBAR=2;
    public final static byte POINTER_DRAG=3;
    public final static byte POINTER_DRAGLEFT=4;
    public final static byte POINTER_DRAGRIGHT=5;
    public final static byte POINTER_PANEL=6;
//#endif
}
