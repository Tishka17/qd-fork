/*
 * Message.java
 *
 * Created on 21.01.2006, 23:17
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
package client;

import colors.ColorTheme;
import font.FontCache;
import images.RosterIcons;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import message.MessageParser;
import midlet.BombusQD;
import ui.ComplexString;
import ui.VirtualElement;
import ui.VirtualList;
import util.Time;
import ui.controls.form.SpacerItem;

public final class Msg implements VirtualElement {
    // without signaling
    public final static byte OUTGOING = 1;
    public final static byte PRESENCE = 2;
    public final static byte HISTORY = 3;

    // with signaling
    public final static byte INCOMING = 10;
    public final static byte HEADLINE = 11;
    public final static byte ERROR = 12;
    public final static byte SUBJECT = 13;
    public final static byte AUTH = 14;
    public final static byte SYSTEM = 15;
    public final static byte JUICK = 18;

    private boolean highlite;
    private byte type;
    private boolean isMucMsg;

    private String from;
    private String subject;
    private String body;

    public long dateGmt;
    private boolean delivered;
    private String id;
    public boolean found;
    private boolean unread = false;
    private boolean collapsed;
    private boolean selected;
    private int verticalOffset = 0;
    
    public Vector msgLines;
    private boolean isEven;
//#if SMILES
    private boolean smiles;
//#endif
    public VirtualElement attachment=null;
    
    public Msg(byte type, String from, String body) {
        this(type, from, null, body);
    }

    public Msg(byte type, String from, String subj, String body) {
        this.type = type;
        this.from = from;
        this.body = body;
        this.subject = subj;
        this.dateGmt = Time.utcTimeMillis();
        this.id = null;
        
        if (type >= INCOMING) {
            unread = true;
        }
        if (type == PRESENCE) {
            collapsed = Config.getInstance().showCollapsedPresences;
        } else if (type == HEADLINE) {
            collapsed = true;
        } else if (body != null && type != SUBJECT) {
            if (body.length() > midlet.BombusQD.cf.messageLimit) {
                collapsed = true;
            }
        }
//#if SMILES        
        this.smiles = Config.getInstance().smiles;
//#endif
    }
    
    public Msg(DataInputStream is) throws IOException {
        from = is.readUTF();
        body = is.readUTF();
        dateGmt = is.readLong();
        type = INCOMING;
        try {
            subject = is.readUTF();
        } catch (Exception e) {
            subject = null;
        }
    }

    public void serialize(DataOutputStream os) throws IOException {
        os.writeUTF(from);
        os.writeUTF(body);
        os.writeLong(dateGmt);
        if (subject != null) {
            os.writeUTF(subject);
        }
    }
    
    public boolean isCollapsed() {
        return collapsed;
    }
    
    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isDelivered() {
        return delivered;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isUnread() {
        return unread;
    }
    
    public void read() {
        unread = false;
    }

    public boolean isItemCollapsed() {
        return collapsed;
    }

    public void expand() {
        collapsed = false;
    }
    
    public void collapse() {
        collapsed = true;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getTime() {
        return Time.timeLocalString(dateGmt);
    }

    public String getDayTime() {
        return Time.dayLocalString(dateGmt) + " " + Time.timeLocalString(dateGmt);
    }

    public void setDayTime(String date) { //ArchiveTemplates
        this.dateGmt = Time.dateStringToLong(date);
    }
    
    public boolean isHighlite() {
        return highlite;
    }
    
    public void highlite() {
        highlite = true;
    }
    
    public void found() {
        found = true;
    }
    
    public void reset() {
        highlite = false;
        found = false;
    }

    public byte getType() {
        return type;
    }
    
    public void setType(byte type) {
        this.type = type;
    }

    public boolean isMucMsg() {
        return isMucMsg;
    }

    public void setMucChat(boolean isMucMsg) {
        this.isMucMsg = isMucMsg;
    }

    public void setDayTime(long dateGmt) {
        this.dateGmt = dateGmt;
    }

    //memory leak
    public String toString() {
        StringBuffer buf = new StringBuffer();
        if (isMucMsg || isPresence() || !(Config.showNickNames && subject != null)) {
            if (Config.showTimeInMsgs
            && !("spacer".equals(getId()))
            ) {
                buf.append('[');
                if ((Time.utcTimeMillis() - dateGmt) > 86400000) {
                    buf.append(getDayTime());
                } else {
                    buf.append(getTime());
                }
                buf.append("] ");
            }
        }
        buf.append(body);
        return buf.toString();
    }

    public boolean isPresence() {
        return type == PRESENCE;
    }

    public int getVHeight() { 
        if (msgLines == null) {
            parse();
        }
        int height = 0;
        int size = collapsed ? Math.min(msgLines.size(), 1) : msgLines.size();
        if (!msgLines.isEmpty()) {
            height = ((ComplexString)msgLines.elementAt(0)).getVHeight();
            if (!isMucMsg && !Config.getInstance().hideMessageIcon && !isPresence() && !isMucMsg) {
                height = Math.max(height, RosterIcons.getInstance().getHeight());
            }
            for (int i = 1; i < size; ++i) {
                height += ((ComplexString)msgLines.elementAt(i)).getVHeight();
            }
        }
        if (attachment!=null) {
            height+=attachment.getVHeight();
        }
        //FIXME: грязный хак для разделителя сообщений
        if (!("spacer".equals(id)) && height<Config.getInstance().minItemHeight) {
            verticalOffset = (Config.getInstance().minItemHeight - height)>>1;
            height = Config.getInstance().minItemHeight;
        } else {
            verticalOffset = 0;
        }
        return height; 
    }

    public Font getFont() {
        return FontCache.getFont(false, Config.msgFont);
    }

    public int getVWidth(){ 
        return -1;
    }     
    
    public int getColorBGnd() {
        return ColorTheme.getColor(isEven ? ColorTheme.LIST_BGND_EVEN : ColorTheme.LIST_BGND);
    }
    
    public int getColor() {
        if (selected || highlite) {
            return ColorTheme.getColor(ColorTheme.MSG_HIGHLIGHT);
        }

        switch (type) {
            case INCOMING:
            case HEADLINE:
//#ifdef JUICK.COM
            case JUICK:
//#endif
                return ColorTheme.getColor(ColorTheme.MESSAGE_IN);
            case PRESENCE:
                return ColorTheme.getColor(ColorTheme.MESSAGE_PRESENCE);
            case OUTGOING:
                return ColorTheme.getColor(ColorTheme.MESSAGE_OUT);
            case SUBJECT:
                return ColorTheme.getColor(ColorTheme.MSG_SUBJ);
            case AUTH:
                return ColorTheme.getColor(ColorTheme.MESSAGE_AUTH);
            case HISTORY:
                return ColorTheme.getColor(ColorTheme.MESSAGE_HISTORY);
        }
        return ColorTheme.getColor(ColorTheme.LIST_INK);
    }
    
    public void parse() {
        MessageParser.getInstance().parseMsg(this, BombusQD.sd.canvas.getAvailWidth());
        //updateHeight();
    }

    public void drawItem(VirtualList view, Graphics g, int ofs, boolean selected) {

        int xorg = g.getTranslateX();
        int yorg = g.getTranslateY();
        
        g.translate(2, verticalOffset);

        boolean showMessageIcon = !(Config.hideMessageIcon || isPresence() || isMucMsg);
        if (!msgLines.isEmpty()) {
            int size = collapsed ? 1 : msgLines.size();
            for (int i = 0; i < size; ++i) {
                ComplexString string = (ComplexString)msgLines.elementAt(i);

                if (string.isEmpty()) {
                    break;
                }
                int h = string.getVHeight();
                int cy = g.getClipY();

                if (cy <= h && cy + g.getClipHeight() > 0) {
                    ofs = 0;
                    boolean cols = (collapsed && msgLines.size() > 1);
                    if (showMessageIcon && i == 0) {
                        h = Math.max(h, RosterIcons.getInstance().getHeight());
                        if (delivered) {
                            RosterIcons.getInstance().drawImage(g, RosterIcons.ICON_DELIVERED_INDEX, 0, 0);
                        } else {
                            RosterIcons.getInstance().drawImage(g, RosterIcons.ICON_MESSAGE_INDEX, 0, 0);
                        }
                        ofs += RosterIcons.getInstance().getWidth() + 4;
                    }
                    if (cols) {
                        RosterIcons.getInstance().drawImage(g, RosterIcons.ICON_MSGCOLLAPSED_INDEX, 0, 0);
                        if (!showMessageIcon) {
                            g.translate(RosterIcons.getInstance().getWidth(), 0);
                        }
                    }
                    string.drawItem(view, g, ofs, selected);
                }
                g.translate(0, h);
                if (collapsed) {
                    break;
                }
            }
        }
        if (attachment!=null) {
            attachment.drawItem(view, g, ofs, selected);
        }

        g.translate(xorg - g.getTranslateX(), yorg - g.getTranslateY());

        if (found) {
            int right = g.getClipX() + g.getClipWidth();
            RosterIcons.getInstance().drawImage(
                    g, RosterIcons.ICON_PRIVACY_ALLOW,
                    right - RosterIcons.getInstance().getWidth() - 3 - 16, 0);
        }
    }
    
    public void onSelect(VirtualList view) {
        collapsed = !collapsed;

        if (!collapsed) {
            parse();
        }
        view.redraw();
    }

    public Vector getUrlList() { 
        Vector urlList=new Vector(0);
        addUrls(body, "http://", urlList);
        addUrls(body, "https://", urlList);
        addUrls(body, "tel:", urlList);
        addUrls(body, "ftp://", urlList);
        addUrls(body, "native:", urlList);
        return (urlList.isEmpty())? null: urlList;
    }
    
    private void addUrls(String text, String addString, Vector urlList) {
        int pos=0;
        int len=text.length();
        while (pos<len) {
            int head=text.indexOf(addString, pos);
            if (head>=0) {
                pos=head;
                
                while (pos<len) {
                    char c=text.charAt(pos);
                    if (c==' ' || c==0x09 || c==0x0d || c==0x0a || c==0xa0 || c==')' )  
                        break;
                    pos++;
                }
                urlList.addElement(text.substring(head, pos));
                
            } else break;
        }
    }
    
    public void setEven(boolean even) {
        this.isEven = even;
    }

    public String getTipString() {
        if (Time.utcTimeMillis() - dateGmt> (86400000)) {
            return getDayTime();
        }
        return getTime();
    }

//#ifdef SMILES
    public void toggleSmiles(VirtualList view) {
        smiles = !smiles;
        if(!collapsed) {
            parse();
        }
    }
    
    public boolean smilesEnabled() { 
        return smiles; 
    }
//#endif

    public boolean isSelectable() { 
        return !((body==null || body.length()==0) && (subject ==null || subject.length()==0) && (attachment==null || attachment instanceof SpacerItem)); 
    }

    public boolean eventKeyPressed(int keyCode) { 
        return false;
    }
    
    public boolean eventKeyLong(int keyCode) {
        return false;
    }    
//#ifdef TOUCH      
    public boolean eventPointerPressed(int x, int y) {
        return false;
    }
//#endif
}
