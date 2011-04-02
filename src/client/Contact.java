/*
 * Contact.java
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

//#ifndef WMUC
import conference.MucContact;
import client.contact.ChatInfo;
//#endif
//#ifdef CLIENTS_ICONS
import images.ClientsIcons;
//#endif
import javax.microedition.lcdui.Graphics;
//#ifdef PEP
import images.MoodIcons;
import images.ActivityIcons;
//#endif
import images.RosterIcons;
import colors.ColorTheme;
import vcard.VCard;
import ui.IconTextElement;
import javax.microedition.lcdui.Image;
import ui.VirtualList;

public class Contact extends IconTextElement {
    private static final int BLINK_COUNT = 10;

//#ifdef PEP
    public byte pepMood=-1;//0..127
    public String pepMoodName=null;
    public String pepMoodText=null;
    public boolean pepTune;
    public String pepTuneText=null;
//#ifdef PEP
    public String activity=null;
    public byte activ=-1;//0..127
//#endif
//#endif
    public String annotations=null;

    private String nick;
    private String statusString;

    public Jid jid;
    public String bareJid; // for roster/subscription manipulating
    public int priority = 0;
    public Group group;


    public String getNick() { return nick; }
    public String getStatus() { return statusString; }

    public void setNick(String value) { this.nick = value; }
    public void setStatus(String value) { this.statusString = value; }


    public boolean autoresponded=false;
    public boolean moveToLatest=false;

    public boolean acceptComposing;
    public boolean showComposing=false;

    public short deliveryType;

    public byte incomingState=0;//INC_NONE=0

    protected byte key0;
    protected String key1;

    public byte origin;

    public String subscr;
    public boolean ask_subscribe;

//#ifdef CLASSIC_CHAT
//#     public ClassicChat scroller = null;
//#endif

    public String msgSuspended;
    public String lastSendedMessage;

    public VCard vcard;
//#ifdef CLIENTS_ICONS
    public int client=-1;
    public String clientName=null;
//#endif

//#ifdef LOGROTATE
//#     public boolean redraw=false;
//#endif

    public String j2j;
    public String lang;
    public String version;

//#ifdef FILE_TRANSFER
    public boolean fileQuery;
//#endif

    private ChatInfo chatInfo = null;
    private ContactMessageList messageList = null;
    private ContactMessageList getML() {
        if (null == messageList) {
            messageList = new ContactMessageList(this);
        }
        return messageList;
    }
    public ContactMessageList getMessageList() {
        midlet.BombusQD.sd.roster.activeContact = this;
        setIncoming(0);
//#ifdef FILE_TRANSFER
        fileQuery=false;
//#endif
        if (0 < getChatInfo().getMessageCount()) {
            if(midlet.BombusQD.cf.savePos)
                getML().moveCursorTo(getCursor());
            else
                getML().moveCursorEnd();
        }
        chatInfo.opened = true;//chat open flag
        getML().updateMainBar(this);
        return getML();
    }

    public final ChatInfo getChatInfo() {
        if(chatInfo == null){
            chatInfo = new ChatInfo();
            chatInfo.initMsgs();
        }
        return chatInfo;
    }

    public void destroy() {
        if(!midlet.BombusQD.sd.roster.isLoggedIn()) return;
        if(chatInfo != null){
           chatInfo.destroy();
           chatInfo = null;
        }
        if (null != messageList) {
            messageList.destroy();
            messageList = null;
        }
        if(null != msgSuspended) msgSuspended = null;
        if(null != vcard) clearVCard();
//#ifdef CLIENTS_ICONS
        if(null != clientName) clientName = null;
        client = -1;
//#endif
        if(null != version) version = null;
        if(null != lang) lang = null;

        if(null != version) version = null;
        if(null != lastSendedMessage) lastSendedMessage = null;
    }

    private int fontHeight;

    int maxImgHeight;
    int cursorPos;

    protected Contact (){
        super(RosterIcons.getInstance());
        if(chatInfo != null) {
          chatInfo.initMsgs();
        }
//#ifdef CLASSIC_CHAT
//#         scroller=null;
//#endif
        key1="";
        maxImgHeight = imgHeight;
    }

    public Contact(final String Nick, final String sJid, final int Status, String subscr) {
        this();
        nick=Nick;
        jid= new Jid(sJid);
        status=Status;

        bareJid=sJid;
        this.subscr=subscr;

        setSortKey((null == Nick)?sJid:Nick);

        transport=RosterIcons.getInstance().getTransportIndex(jid.getTransport());
    }

    public int getColor() {
//#if USE_ROTATOR
        if (isnew>0){
            isnew--;
            return (isnew%2==0)?0xFF0000:0x0000FF;
        }
//#endif
        if (null != j2j) return ColorTheme.getColor(ColorTheme.CONTACT_J2J);

        return getMainColor();
    }

//#if USE_ROTATOR
    private byte isnew = 0;
    public void setNewContact() {
        this.isnew = BLINK_COUNT;
    }
//#endif

    public int getCursor() {
       return cursorPos;
    }

    public int getMainColor() {
        switch (status) {
            case Constants.PRESENCE_CHAT: return ColorTheme.getColor(ColorTheme.CONTACT_CHAT);
            case Constants.PRESENCE_AWAY: return ColorTheme.getColor(ColorTheme.CONTACT_AWAY);
            case Constants.PRESENCE_XA: return ColorTheme.getColor(ColorTheme.CONTACT_XA);
            case Constants.PRESENCE_DND: return ColorTheme.getColor(ColorTheme.CONTACT_DND);
        }
        return ColorTheme.getColor(ColorTheme.CONTACT_DEFAULT);
    }

    public int setCursor(int cursor) {
       cursorPos=cursor;
       return cursorPos;
    }

    public final int getNewMsgsCount() {
        if (Groups.TYPE_IGNORE == getGroupType()) return 0;
        if(chatInfo == null) return 0;
        return chatInfo.getNewMessageCount();
    }

    public final boolean hasNewMsgs() {
        return getNewMsgsCount() > 0;
    }

    public int getNewHighliteMsgsCount() {
        if (Groups.TYPE_IGNORE == getGroupType()) return 0;
        if(chatInfo == null) return 0;
        return chatInfo.getNewHighliteMessageCount();
    }

    public boolean active() {
        if(chatInfo == null) return false;
        return chatInfo.isActiveChat();
    }

    public final void setGroup(Group g) {
        if (null != group) group.removeContact(this);
        this.group = g;
        if (null != group) group.addContact(this);

    }

    public void setIncoming (int state) {
        if (!midlet.BombusQD.cf.IQNotify) return;

        byte i=0;
        switch (state){
            case Constants.INC_APPEARING:
                i=RosterIcons.ICON_APPEARING_INDEX;
                break;
            case Constants.INC_VIEWING:
                i=RosterIcons.ICON_VIEWING_INDEX;
                break;
        }
        incomingState=i;
    }

    public int compare(IconTextElement right){
        Contact c=(Contact) right;
        int cmp;
        if ((cmp=key0-c.key0) !=0) return cmp;
        if ((cmp=status-c.status) !=0) return cmp;
        if ((cmp=key1.compareTo(c.key1)) !=0) return cmp;
        if ((cmp=c.priority-priority) !=0) return cmp;
        return c.transport-transport;
    }

    public void addMessage(Msg m) {
        boolean first_replace=false;
        boolean first_msgreplace=false;
        if(chatInfo == null) getChatInfo();
        int msgCount = getChatInfo().getMessageCount();
        if (msgCount >= midlet.BombusQD.cf.msglistLimit) {
            getML().deleteOldMessages();
        }
        if (origin!=Constants.ORIGIN_GROUPCHAT) {
            if (m.isPresence()) first_replace = chatInfo.isOnlyStatusMessage();
            else {
                first_msgreplace = chatInfo.isFirstMessage();
                StringBuffer temp;

                if (m.body.startsWith("/me ")) {
                    temp = new StringBuffer(0);
//#if NICK_COLORS
                    temp.append("<nick>");
//#endif
                    temp.append((m.messageType==Constants.MESSAGE_TYPE_OUT)?midlet.BombusQD.sd.account.getNickName():getName());
                    if (Config.showNickNames) {
                        temp.append(" (");
                        temp.append(m.getTime());
                        temp.append(')');
                    }
//#if NICK_COLORS
                    temp.append("</nick>");
//#endif
                    temp.insert(0,'*');

                    temp.append(m.body.substring(3));
                    m.body=temp.toString().trim();
                    temp = new StringBuffer(0);
                } else if (Config.showNickNames) {
                    temp = new StringBuffer(0);
                    temp.append((m.messageType==Constants.MESSAGE_TYPE_OUT)?midlet.BombusQD.sd.account.getNickName():getName());
                        temp.append(" (");
                        temp.append(m.getTime());
                        temp.append(')');
                    if (m.subject!=null) temp.append(m.subject);
                    m.subject=temp.toString();
                    temp = new StringBuffer(0);
                }
                temp = null;
            }
        } else {
            status = Constants.PRESENCE_ONLINE;
        }
        /*
            midlet.BombusQD.debug.add("deleteOldMessages "+this+" ..",10);
            if (null != messageList) {
                midlet.BombusQD.debug.add("deleteOldMessages "+this+" ...",10);
                getML().deleteOldMessages( getChatInfo().getMessageCount() );

            }
         */

        if (first_replace) {
            chatInfo.setFirstMessage(m);
            if (null != messageList) {
                getML().resetMessages();
                getML().redraw();
            }
            return;
        }

        chatInfo.addMessage(m);


        if(chatInfo.opened || m.messageType == Constants.MESSAGE_TYPE_OUT) chatInfo.reEnumCounts();
        if (first_msgreplace){
            chatInfo.setFirstMessage(m);
            if (null != messageList) {
                getML().resetMessages();
                getML().redraw();
            }
        } else {
		if (null != messageList) {
		    getML().addMessage(m);
		} else if (!chatInfo.isOnlyStatusMessage()) {
		    getML().resetMessages();
		}
        }
        if (group.type!=Groups.TYPE_TRANSP && group.type!=Groups.TYPE_SEARCH_RESULT) {
          boolean allowLog = (origin<Constants.ORIGIN_GROUPCHAT);
          if (origin!=Constants.ORIGIN_GROUPCHAT && this instanceof MucContact) allowLog=false;
//#ifdef HISTORY
          if(allowLog) getML().storeMessage(m);
//#endif
        }
    }

    public boolean getFontIndex(){
       if (midlet.BombusQD.cf.useBoldFont && status<5) return true;
       if(chatInfo == null) return false;
       return chatInfo.isActiveChat();
    }

    public final String getName(){
        return (null == nick)?bareJid:nick;
    }

    public final String getJid() {
        return jid.getJid();
    }

    public final String getResource() {
        return jid.getResource();
    }

    public final String getNickJid() {
        if (null == nick) return bareJid;
        return nick+" <"+bareJid+">";
    }

    public final void purge() {
        chatInfo.initMsgs();
        if (null != messageList) messageList.destroy();
        messageList = null;
        clearVCard();
    }

    public final void clearVCard() {
        try {
            if (null != vcard) {
                vcard.clearVCard();
                vcard=null;
            }
        } catch (Exception e) { }
    }

    public final void setSortKey(String sortKey){
        key1=(sortKey==null)? "": sortKey.toLowerCase();
    }

    public String getTipString() {
        int nm=getNewMsgsCount();
        if (nm!=0)
            return String.valueOf(nm);
        if (nick!=null)
            return bareJid;
        return null;
    }

    public int getGroupType() {
        if (group==null) return 0;
        return group.type;
    }

    public void setStatus(int status) {
        setIncoming(0);
        this.status = status;
        if (status>=Constants.PRESENCE_OFFLINE) acceptComposing=false;
    }

    final void markDelivered(String id) {
        chatInfo.markDelivered(id);
        if (null != messageList) messageList.redraw();
    }

    public int getVWidth(){
        int wft = getFirstLength();
        if (midlet.BombusQD.cf.rosterStatus) {
            int sl = getSecondLength();
            wft = Math.max(wft, sl);
        }
//#ifdef CLIENTS_ICONS
        return wft + imgWidth + 4 + (hasClientIcon()?ClientsIcons.getInstance().getWidth():0);
//#else
//#         return wft + imgWidth + 4;
//#endif
    }

    public String toString() { return getFirstString(); }

    public int getSecondLength() {
        if (getSecondString()==null || getSecondString().equals("")) return 0;
        return getFont().stringWidth(getSecondString());
    }

    public int getFirstLength() {
        if (getFirstString()==null || getFirstString().equals("") ) return 0;
        return getFont().stringWidth(getFirstString());
    }

    public String getFirstString() {
        if (!midlet.BombusQD.cf.showResources) return (nick==null)?getJid():nick;
        else if (origin>Constants.ORIGIN_GROUPCHAT) return nick;
        else if (origin==Constants.ORIGIN_GROUPCHAT) return getJid();
        return (nick==null)?getJid():nick+jid.getResource();
    }

    public String getSecondString() {
        if (midlet.BombusQD.cf.rosterStatus){
            if (statusString!=null) return statusString;
//#if PEP
            return getMoodString();
//#endif
        }
        return null;
    }

    public boolean inGroup(Group ingroup) {  return group==ingroup;  }

    public int transport;
    public int status;
    public int offline_type=Constants.PRESENCE_UNKNOWN;
    public int getImageIndex() {
        if (showComposing) return RosterIcons.ICON_COMPOSING_INDEX;
        int st=(status==Constants.PRESENCE_OFFLINE)?offline_type:status;
        return (st < 8) ? st + transport : st;
    }

    public final int getSecImageIndex() {
        if (hasNewMsgs()) {
            return (Constants.MESSAGE_TYPE_AUTH == chatInfo.getUnreadMessageType())
                    ? RosterIcons.ICON_AUTHRQ_INDEX
                    : RosterIcons.ICON_MESSAGE_INDEX;
        }
        if (incomingState>0) return incomingState;
        return -1;
    }

//#ifdef PEP
    public String getMoodString() {
        StringBuffer mood=null;
        if (hasMood()) {
             mood=new StringBuffer(pepMoodName);
             if (pepMoodText!=null) {
                if (pepMoodText.length()>0) {
                     mood.append('(')
                         .append(pepMoodText)
                         .append(')');
                }
             }
        }
        return (mood!=null)?mood.toString():null;
    }
//#endif


    public int getVHeight(){
        fontHeight = getFont().getHeight();
        if(midlet.BombusQD.cf.simpleContacts) return fontHeight;
        int itemVHeight=0;
        if (getSecondString()!=null)
        {
            itemVHeight += fontHeight<<1;
            if(img_vcard!=null){
              if(img_vcard.getHeight()>=itemVHeight){
                itemVHeight = avatar_height + 5;
              }
            }
        } else {
          itemVHeight = (maxImgHeight>=fontHeight)?maxImgHeight:fontHeight;
          if(img_vcard!=null){
             if(img_vcard.getHeight()>=itemVHeight){
               itemVHeight = avatar_height + 5;
             }
          }
        }
		if (itemVHeight < midlet.BombusQD.cf.minItemHeight)
		itemVHeight = midlet.BombusQD.cf.minItemHeight;

        return itemVHeight;
    }


    public Image img_vcard=null;
    public boolean hasPhoto=false;

    public int avatar_width=0;
    public int avatar_height=0;

    public final void drawItem(VirtualList view, Graphics g, int ofs, boolean sel) {
        int imageIndex = getImageIndex();
        int w=g.getClipWidth();
        if(midlet.BombusQD.cf.simpleContacts) {
            itemHeight = getFont().getHeight();
            if (hasNewMsgs()) {
               w -= imgWidth;
               il.drawImage(g, RosterIcons.ICON_MESSAGE_INDEX, w, 0);
            }
            super.drawItem(view, g, ofs, sel);
            return;
        }

        int offset=4;
        int h=getVHeight();
        int xo=g.getClipX();
        int yo=g.getClipY();

        int xoffset = Config.contactXOffset;

        g.translate(xoffset, 0);
        w -= xoffset;

        int imgH = (h - imgHeight) >> 1;

        if(!Config.module_avatars) {
            img_vcard=null;
        }
        if (imageIndex>-1) {
            offset += imgHeight;
            il.drawImage(g, imageIndex, 2, imgH);
        }
        if(img_vcard!=null){
           int yy = (h - avatar_height) >> 1 ;
           int def = g.getColor();
           if(avatar_width==avatar_height){
             w = w-avatar_width - 4;
             g.drawImage(img_vcard,w,yy,Graphics.TOP|Graphics.LEFT);
             if(midlet.BombusQD.cf.showAvatarRect){
                g.setColor(0x000000);
                g.drawRect(w,yy,avatar_width,avatar_height);
             }
           } else {
              w = w-avatar_width - 4;
              g.drawImage(img_vcard,w,yy,Graphics.TOP|Graphics.LEFT);
              if(midlet.BombusQD.cf.showAvatarRect){
                g.setColor(0x000000);
                g.drawRect(w,yy,avatar_width,avatar_height);
              }
           }
           g.setColor(def);
        }
//#ifdef CLIENTS_ICONS
        if (hasClientIcon() && midlet.BombusQD.cf.showClientIcon ) {
             int clientImgSize=ClientsIcons.getInstance().getWidth();
             if(midlet.BombusQD.cf.iconsLeft){
                 offset+=clientImgSize;
             }else{
                 w-=clientImgSize;
             }
             ClientsIcons.getInstance().drawImage(g, client, midlet.BombusQD.cf.iconsLeft?imgHeight+2
                     :w
                     , (h-clientImgSize) >> 1 );
             //client==index
             if (maxImgHeight<clientImgSize) maxImgHeight=clientImgSize;
        }
//#endif
//#ifdef PEP
        if (hasMood()) {
            int moodImgSize=MoodIcons.getInstance().getWidth();
            w-=moodImgSize;
            MoodIcons.getInstance().drawImage(g, pepMood, w, (h-moodImgSize) >> 1 );
            if (maxImgHeight<moodImgSize) maxImgHeight=moodImgSize;
        }
//#ifdef PEP
        if (pepTune) {
            w -= imgHeight;
            il.drawImage(g, RosterIcons.ICON_PROFILE_INDEX+1, w,imgH);
        }
        if (hasActivity()) {
            int activitySize=ActivityIcons.getInstance().getWidth();
            w-=activitySize;
            ActivityIcons.getInstance().drawImage(g, activ, w, (h-activitySize) >> 1 );
            if (maxImgHeight<activitySize) maxImgHeight=activitySize;
        }
//#endif
//#endif

//#ifdef FILE_TRANSFER
        if (fileQuery) {
            w -= imgHeight;
            il.drawImage(g, RosterIcons.ICON_PROGRESS_INDEX, w,imgH);
        }
//#endif
        if (getSecImageIndex()>-1) {
            w -= imgHeight;
            il.drawImage(g, getSecImageIndex(), w,imgH);
        }

        int thisOfs=0;

        g.setClip(offset, yo, w-offset, h);
        thisOfs=(getFirstLength()>w)?-ofs+offset:offset;
        if ((thisOfs+getFirstLength())<0) thisOfs=offset;

        g.setFont(getFont());

        if (getSecondString()==null) {
            int y = (h - fontHeight) >> 1 ;
            g.drawString(getFirstString(), thisOfs , y, Graphics.TOP|Graphics.LEFT);
        }
        else {
            int y = (h - (fontHeight<<1)) >> 1 ;
            g.drawString(getFirstString(), thisOfs , y , Graphics.TOP|Graphics.LEFT);
            thisOfs=(getSecondLength()>w)?-ofs+offset:offset;
            g.setColor(ColorTheme.getColor(ColorTheme.SECOND_LINE));
            g.drawString(getSecondString(),thisOfs , y + fontHeight , Graphics.TOP|Graphics.LEFT);
        }
        g.setClip(xo, yo, w, h);
    }

//#ifdef CLIENTS_ICONS
    boolean hasClientIcon() {
        return (client>-1);
    }
//#endif

//#ifdef PEP
    boolean hasMood() {
        return (pepMood>-1 && pepMood<85);
    }
    boolean hasActivity() {
        if (activity!=null)
            if (activity.length()>0) return true;
        return false;
    }

//#endif
}
