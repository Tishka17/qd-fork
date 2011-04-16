/*
 * MessageItem.java
 *
 * Created on 21.01.2006, 23:17
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

package message; 

//import Client.Config;
import client.Config;
import client.Msg;
import images.RosterIcons;
//import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import colors.ColorTheme;
import ui.ComplexString;
import font.FontCache;
import util.Time;
import ui.VirtualElement;
import ui.VirtualList;

public class MessageItem
    implements VirtualElement//, MessageParser.MessageParserNotify 
{
    
    public Msg msg;
    Vector msgLines;
    private boolean even;
    private boolean smiles;
    private boolean partialParse=false;
    private int itemHeight = -1;
    
    /** Creates a new instance of MessageItem */
    public MessageItem(Msg msg, boolean showSmiles) {
	this.msg=msg;
        this.smiles=showSmiles;
        partialParse = msg.itemCollapsed;
    }

    public int getVHeight() { 
        if (msg==null) return 3;
        if (itemHeight<0) itemHeight=getFont().getHeight();
        if (msg.delivered) {
            int rh=RosterIcons.getInstance().getHeight();
            if (itemHeight<rh) return rh;
        }
	if (itemHeight<3) itemHeight = 3;
        return itemHeight; 
    }

    
    public Font getFont() {
        return FontCache.getFont(false, Config.msgFont);
    }

    public int getVWidth(){ 
        return -1;
    }     
    
    public int getColorBGnd() {
        return ColorTheme.getColor(even ? ColorTheme.LIST_BGND_EVEN : ColorTheme.LIST_BGND);
    }
    
    public int getColor() { return msg.getColor(); }
    
    public void parse(VirtualList view) {
        MessageParser.getInstance().parseMsg(this, view.getListWidth());
        updateHeight();
    }
    
    public void destroy(){
        int size = msgLines.size();
        ComplexString complexStr;
        for(int i = 0; i<size; ++i){
          complexStr = (ComplexString)msgLines.elementAt(i);
          complexStr.destroy();
        }
        msgLines.removeAllElements();
        msgLines = null;
        msg.destroy();
        msg = null;
        //System.out.println("    :::     messageItem-->destroyed:: size->" + size + " => " + msgLines);
    }

    public void drawItem(VirtualList view, Graphics g, int ofs, boolean selected) {
        if (msgLines == null) {
            parse(view);
            return;
        }
        int xorg = g.getTranslateX();
        int yorg = g.getTranslateY();
        
        g.translate(2, 0);

        int size = msg.itemCollapsed ? 1 : msgLines.size();
        for (int i = 0; i < size; ++i) {
            ComplexString string = (ComplexString)msgLines.elementAt(i);

            if (string.isEmpty()) {
                break;
            }
            int h = string.getVHeight();
            int cy = g.getClipY();

            if (cy <= h && cy + g.getClipHeight() > 0) {
                ofs = 0;
                boolean cols = (msg.itemCollapsed && msgLines.size() > 1);
                if (!Config.hideMessageIcon) {
                    if (i == 0 && !msg.isPresence() && !msg.MucChat) {
                        if (msg.delivered) {
                            RosterIcons.getInstance().drawImage(g, RosterIcons.ICON_DELIVERED_INDEX, 0, 0);
                        } else {
                            RosterIcons.getInstance().drawImage(g, RosterIcons.ICON_MESSAGE_INDEX, 0, 0);
                        }
                        ofs += RosterIcons.getInstance().getWidth() + 4;
                    }
                }
                if (cols) {
                    RosterIcons.getInstance().drawImage(g, RosterIcons.ICON_MSGCOLLAPSED_INDEX, 0, 0);
                    g.translate(8, 0);
                }
                string.drawItem(view, g, ofs, selected);
            }
            g.translate(0, h);
            if (msg.itemCollapsed) {
                break;
            }
        }

        g.translate(xorg - g.getTranslateX(), yorg - g.getTranslateY());

        if (msg.search_word) {
            int right = g.getClipX() + g.getClipWidth();
            RosterIcons.getInstance().drawImage(
                    g, RosterIcons.ICON_PRIVACY_ALLOW,
                    right - RosterIcons.getInstance().getWidth() - 3 - 16, 0);
        }
    }
    
    public void onSelect(VirtualList view) {
        msg.itemCollapsed=!msg.itemCollapsed;
        if (partialParse) {
            partialParse=false;
            parse(view);
        }
        updateHeight();
        view.redraw();
    }

    private void updateHeight() {
        int height = 0;
        int size = msg.itemCollapsed ? Math.min(msgLines.size(), 1) : msgLines.size();
        for (int i = 0; i < size; ++i) {
            height+=((ComplexString)msgLines.elementAt(i)).getVHeight();
        }
        itemHeight = height;
    }

    public Vector getUrlList() { 
        Vector urlList=new Vector(0);
        addUrls(msg.body, "http://", urlList);
        addUrls(msg.body, "https://", urlList);
        addUrls(msg.body, "tel:", urlList);
        addUrls(msg.body, "ftp://", urlList);
        addUrls(msg.body, "native:", urlList);
        return (urlList.size()==0)? null: urlList;
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
        this.even = even;
    }

    public String getTipString() {
        if (Time.utcTimeMillis() - msg.dateGmt> (86400000)) return msg.getDayTime();
        return msg.getTime();
    }
//#ifdef SMILES
    public void toggleSmiles(VirtualList view) {
        smiles=!smiles;
        if(!msg.itemCollapsed) parse(view);
        view.redraw();
    }
    
    final boolean smilesEnabled() { return smiles; }
//#endif

    public boolean isSelectable() { return true; }

    public boolean handleEvent(int keyCode) { return false; }
}
