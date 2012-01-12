/*
 * PopUp.java
 *
 * Created on 2.02.2007, 0:19
 *
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
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
 */

//#ifdef POPUPS
package ui.controls;

import client.Config;
import colors.ColorTheme;
import images.RosterIcons;
import java.util.Vector;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import font.FontCache;
//#ifdef GRADIENT
import ui.Gradient;
//#endif
import ui.VirtualCanvas;
import util.StringUtils;

public class PopUp {
    public final static int TYPE_SYSTEM = 1;
    public final static int TYPE_MESSAGE = 2;

    private final static int COLOR_ALERT_INK = 0xffffff;
    private final static int COLOR_ALERT_BGND = 0xff0000;

    private int popUpHeight, popUpWidth, widthBorder, heightBorder;
    private int border=8;
    private int padding=4;

    private Font font;

    private int width;
    private int height;

    private Vector popUps;
//#ifdef GRADIENT
    private static Gradient bg = new Gradient();
//#endif
    private final static int  SCROLLABLE_NONE=-1;
    private final static int  SCROLLABLE_DOWN=0;
    private final static int  SCROLLABLE_BOTH=1;
    private final static int  SCROLLABLE_UP=2;

    private int maxWdth;

    private int startLine=0;

    public int scrollable=SCROLLABLE_NONE;

    private RosterIcons ri;

    synchronized public void addPopup(int type, String contact, String message){
        if (message!=null) {
            font=FontCache.getFont(false, Config.baloonFont);//den_po
            popUps.addElement(new PopUpElement(type, contact, StringUtils.parseMessage(message, width-border-padding, font)));
	}
//#ifdef DEBUG
//# //	System.out.println("added message to array = "+message);
//#endif
    }

    public PopUp() {
         popUps = new Vector(0);
         font=FontCache.getFont(false, Config.baloonFont);
         ri=RosterIcons.getInstance();
    }

    public void init(Graphics g, int width, int height) {
        this.height=height;
        this.width=width;
    }

    public String getContact() {
        if(size()>0)
            return ((PopUpElement)popUps.elementAt(0)).getContact();
        return null;
    }

    public int size() {
        if(null == popUps) return -1;
        return popUps.size();
    }

    public void next() {
        if(size()>0) {
            popUps.removeElementAt(0);
            scrollable=SCROLLABLE_NONE;
            startLine=0;
        }
    }

    private void scrollDown() {
        if (scrollable==SCROLLABLE_DOWN || scrollable==SCROLLABLE_BOTH) {
            Vector lines=((PopUpElement)popUps.elementAt(0)).getMessage();
            if (lines.size()<1) return;
            int dl=popUpHeight/getFontHeight();
            startLine+=dl-1;
            if (startLine>lines.size()-dl)
                startLine = lines.size()-dl;
        }
    }

    private void scrollUp() {
        if (scrollable==SCROLLABLE_UP || scrollable==SCROLLABLE_BOTH) {
            Vector lines=((PopUpElement)popUps.elementAt(0)).getMessage();
            if (lines.size()<1) return;
            int dl=popUpHeight/getFontHeight();
            startLine-=dl-1;
            if (startLine<0)
                startLine = 0;
        }
    }

    public boolean handleEvent(int keyCode) {
        switch (keyCode) {
            case VirtualCanvas.NAVIKEY_LEFT:
            case VirtualCanvas.NAVIKEY_UP:
            case VirtualCanvas.KEY_NUM2:
            case VirtualCanvas.KEY_NUM4:
                scrollUp();
                return true;
            case VirtualCanvas.NAVIKEY_RIGHT:
            case VirtualCanvas.NAVIKEY_DOWN:
            case VirtualCanvas.KEY_NUM8:
            case VirtualCanvas.KEY_NUM6:
                scrollDown();
                return true;
        }
        next();
        // we always process event if we are on screen
        return true;
    }
    
    public boolean handleEvent(int x, int y) {
        int dy=Math.min(midlet.BombusQD.cf.minItemHeight, getFontHeight());
        if (scrollable!=-1 && y<heightBorder+dy) {
            scrollUp();
        } else if (scrollable!=-1 && y>heightBorder+popUpHeight-dy) {
            scrollDown();
        } else {
            next();
        }
        // we always process event if we are on screen
        return true;
    }

    public void clear() {
        popUps = null;
        popUps = new Vector(0);
    }

    private int getFontHeight() {
        return font.getHeight();
    }

    private int getHeight() {
        Vector message=((PopUpElement)popUps.elementAt(0)).getMessage();
        return getFontHeight()*(message.size()-startLine);
    }

    private int getStrWidth(String string) {
        return font.stringWidth(string);
    }

    private int getMaxWidth() {
        Vector lines=((PopUpElement)popUps.elementAt(0)).getMessage();

        int length=0;

        if (lines.size()<1) return length;

	for (int line=0; line<lines.size(); ) {
            String string=(String) lines.elementAt(line);
            length=(length>getStrWidth(string))?length:getStrWidth(string);
            line++;
	}
        return length;
    }

    private int getColorInk() {
        int type=((PopUpElement)popUps.elementAt(0)).getType();
        switch (type) {
            case TYPE_SYSTEM:
                return ColorTheme.getColor(ColorTheme.POPUP_SYSTEM_INK);
            case TYPE_MESSAGE:
                return ColorTheme.getColor(ColorTheme.POPUP_MESSAGE_INK);
        }
        return COLOR_ALERT_INK;
    }

    private int getColorBgnd() {
        int type=((PopUpElement)popUps.elementAt(0)).getType();
        switch (type) {
            case TYPE_SYSTEM:
                return ColorTheme.getColor(ColorTheme.POPUP_SYSTEM_BGND);
            case TYPE_MESSAGE:
                return ColorTheme.getColor(ColorTheme.POPUP_MESSAGE_BGND);
        }
        return COLOR_ALERT_BGND;
    }

    public void paintCustom(Graphics graph) {
	if(size()<1)
	    return;

        scrollable=(startLine>0)?SCROLLABLE_UP:SCROLLABLE_NONE;

        maxWdth=getMaxWidth();

        popUpWidth=(maxWdth>(width-border))?width-border:maxWdth+padding;
        widthBorder=(maxWdth>popUpWidth)?border/2:(width-popUpWidth)/2;

        int stringsHeight=getHeight();

        if (stringsHeight>height) {
            scrollable=(startLine>0)?SCROLLABLE_BOTH:SCROLLABLE_DOWN;

            heightBorder=0;
            popUpHeight=height;
        } else {
            popUpHeight=stringsHeight+padding;
            heightBorder=(height-popUpHeight)/2;
        }

        int color=getColorBgnd();
//#ifdef GRADIENT
        if (ColorTheme.getAlpha(color)!=0) {
          bg.update(widthBorder+1, heightBorder+1, widthBorder+popUpWidth, heightBorder+popUpHeight, color, color, Gradient.CACHED_HORIZONTAL, 0);
          bg.paint(graph);
        } else 
//#endif
        {
          graph.setColor(color);
          graph.fillRect(widthBorder+1, heightBorder+1, popUpWidth-1, popUpHeight-1);             //fill
        }
        graph.setColor(getColorInk());
        graph.drawRect(widthBorder, heightBorder, popUpWidth, popUpHeight);                 //border
        graph.setFont(font);

        switch (scrollable) {
            case SCROLLABLE_UP:
                ri.drawImage(graph, RosterIcons.ICON_ARROW_LEFT, widthBorder+maxWdth-ri.getWidth(), heightBorder+popUpHeight-ri.getHeight());
                break;
            case SCROLLABLE_BOTH:
                ri.drawImage(graph, RosterIcons.ICON_ARROW_RIGHTLEFT, widthBorder+maxWdth-ri.getWidth(), heightBorder+popUpHeight-ri.getHeight());
                break;
            case SCROLLABLE_DOWN:
                ri.drawImage(graph, RosterIcons.ICON_ARROW_RIGHT, widthBorder+maxWdth-ri.getWidth(), heightBorder+popUpHeight-ri.getHeight());
                break;
        }

        Vector lines=((PopUpElement)popUps.elementAt(0)).getMessage();
        if (lines.size()<1) return;

        int fh=getFontHeight();

        int pos=0;
        int y=heightBorder+3;
        int size=lines.size();
        String line;
        for(int i=0;i<size;i++){
            if (pos>=startLine) {
                line = (String)lines.elementAt(i);
                if (line!=null && line.length()>0)  graph.drawString(line, widthBorder+2, y, Graphics.TOP|Graphics.LEFT);
                y += fh;
            }
            pos++;
        }
    }

    private static class PopUpElement {
        private int type;
        private String from;
        private Vector message;

        private PopUpElement(int type, String from, Vector message) {
            this.from=from;
            this.type=type;
            this.message=message;
        }

        public int getType() { return type; }
        public Vector getMessage() { return message; }
        public String getContact() { return from; }
    }
}
//#endif
