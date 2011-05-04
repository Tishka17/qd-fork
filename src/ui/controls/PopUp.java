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
            startLine++;
        }
    }

    private void scrollUp() {
        if (scrollable==SCROLLABLE_UP || scrollable==SCROLLABLE_BOTH) {
            Vector lines=((PopUpElement)popUps.elementAt(0)).getMessage();
            if (lines.size()<1) return;
            startLine--;
        }
    }

    public boolean handleEvent(int keyCode) {
        if (scrollable>-1) {
            switch (keyCode) {
                case 2:
                case 4:
                    scrollUp();
                    return true;
                case 6:
                case 8:
                    scrollDown();
                    return true;
            }
        }
        next();
        if (keyCode==5)
            return true;

        return false;
    }
    
    public boolean handleEvent(int x, int y) {
        //int startX = widthBorder;
        //int endX = widthBorder + popUpWidth;
        
        //int startY = heightBorder;
        //int endY = heightBorder + popUpHeight;
        
        //if (x >= startX && x <= endX && y >= startY && y <= endY) {
            next();
        //}
        // we always process event if we are on screen
        return true;
    }

    public void clear() {
        /*if(size()>0)
            popUps.removeAllElements();*/
        // experimental
        popUps = null;
        popUps = new Vector(0);
    }

    private void drawAllStrings(Graphics g, int x, int y) {
        Vector lines=((PopUpElement)popUps.elementAt(0)).getMessage();
        if (lines.size()<1) return;

        int fh=getFontHeight();

        int pos=0;

        int size=lines.size();
        String line;
        for(int i=0;i<size;i++){
            if (pos>=startLine) {
                line = (String)lines.elementAt(i);
                if (line!=null && line.length()>0)  g.drawString(line, x, y, Graphics.TOP|Graphics.LEFT);
                y += fh;
            }
            pos++;
        }
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

    private int getARGB() {
      int ccolor = getColorBgnd();
      int red, green, blue;
      long tmp;
      int alpha_ = midlet.BombusQD.cf.popup_bgnd;
      red = ColorTheme.getRed(ccolor);
      green = ColorTheme.getGreen(ccolor);
      blue = ColorTheme.getBlue(ccolor);
      tmp = (alpha_ << 24) | (red << 16) | (green << 8) | blue;
      return (int)tmp;
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

         if(midlet.BombusQD.cf.popup_bgnd!=0){
          int alpha_=getARGB();
          int[] pixelArray = new int[width * height];
          int lengntp = pixelArray.length;
          for(int i = 0; i < lengntp; i++){
             pixelArray[i] = alpha_;
          }
           graph.drawRGB(pixelArray, 0, width, widthBorder, heightBorder, popUpWidth, popUpHeight, true);
           pixelArray = null;
           pixelArray = new int[0];
           graph.drawRect(widthBorder,heightBorder,popUpWidth,popUpHeight);
         }else{
          graph.setColor(getColorBgnd());
          graph.fillRect(widthBorder+1,heightBorder+1,popUpWidth-1,popUpHeight-1);             //fill
         }
          graph.setColor(getColorInk());
          graph.drawRect(widthBorder,heightBorder,popUpWidth,popUpHeight);                 //border
          graph.setFont(font);

        switch (scrollable) {
            case SCROLLABLE_UP:
                ri.drawImage(graph, 0x27, widthBorder+maxWdth-ri.getWidth(), heightBorder+popUpHeight-ri.getHeight());
                break;
            case SCROLLABLE_BOTH:
                ri.drawImage(graph, 0x25, widthBorder+maxWdth-ri.getWidth(), heightBorder+popUpHeight-ri.getHeight());
                break;
            case SCROLLABLE_DOWN:
                ri.drawImage(graph, 0x26, widthBorder+maxWdth-ri.getWidth(), heightBorder+popUpHeight-ri.getHeight());
                break;
        }


          drawAllStrings(graph, widthBorder+2, heightBorder+3);
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
