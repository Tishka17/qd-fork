/*
 * SmilePicker.java
 *
 * Created on 6.03.2005, 11:50
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

//#ifdef SMILES
package client;

import images.ImageList;
import message.MessageParser;
import images.SmilesIcons;
import locale.SR;
import colors.ColorTheme;
import java.util.Vector;
import ui.controls.Balloon;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.TextField;
import javax.microedition.lcdui.TextBox;
import ui.VirtualElement;
import ui.VirtualList;
import ui.controls.form.DefForm;

public class SmilePicker extends DefForm implements VirtualElement {
    private final static int CURSOR_HOFFSET=1;
    private final static int CURSOR_VOFFSET=1;

    private int imgCnt;
    private int xCnt;
    private int xLastCnt;
    private int xCursor;
    private int lines;

    private int lineHeight;
    private int imgWidth;

    private ImageList il;

    private int caretPos;

    private int realWidth=0;
    private int xBorder = 0;

    private Vector smileTable;

    private Object input;
    private boolean aniSmiles = false;

    public SmilePicker(int caretPos, Object input) {
         super(SR.get(SR.MS_SELECT));

         this.caretPos = caretPos;
         this.input = input;

         aniSmiles = midlet.BombusQD.cf.animatedSmiles;

         il = aniSmiles?SmilesIcons.getInstance():SmilesIcons.getStaticInstance();
//#ifdef SMILES
        smileTable=MessageParser.getInstance().getSmileTable();
//#endif

        imgCnt=smileTable.size();

        realWidth=getWidth()-scrollbar.getScrollWidth();

        imgWidth=il.getWidth()+(CURSOR_HOFFSET<<1);
        lineHeight = il.getHeight()+(CURSOR_VOFFSET<<1);

        xCnt= realWidth / imgWidth;

        lines=imgCnt/xCnt;
        xLastCnt=imgCnt-lines*xCnt;
        if (xLastCnt>0) lines++; else xLastCnt=xCnt;

        xBorder=(realWidth-(xCnt*imgWidth))/2;
    }

    int lineIndex;

    public int getItemCount(){ return lines; }
    public VirtualElement getItemRef(int index){ lineIndex=index; return this;}

    public int getVWidth(){ return 0; }
    public int getVHeight() { return lineHeight; }
    public int getColor(){ return ColorTheme.getColor(ColorTheme.LIST_INK); }
    public int getColorBGnd(){ return ColorTheme.getColor(ColorTheme.LIST_BGND); }

    public void onSelect(VirtualList view){
        selectSmile();
    }

    public void cmdOk() {
        selectSmile();
    }

    private void selectSmile() {
        if (input == null) {
            return;
        }
        String smile = " " + getTipString() + " ";
        if (input instanceof TextBox) {
            ((TextBox)input).insert(smile, caretPos);
        } else if (input instanceof TextField) {
            ((TextField)input).insert(smile, caretPos);
        }
        destroyView();
    }

    public void drawItem(VirtualList view, Graphics g, int ofs, boolean selected){
        int max=(lineIndex==lines-1)? xLastCnt:xCnt;
        for (int i=0;i<max;i++) {
            int index = lineIndex*xCnt + i;
            int x = xBorder + CURSOR_HOFFSET + i*imgWidth;
            if(aniSmiles){
                int hCenter = (imgWidth-il.getWidth(index))/2;
                int vCenter = (lineHeight-il.getHeight(index))/2;
                x+=hCenter;
                il.drawImage(g, index, x, vCenter);
            }else {
                il.drawImage(g, index, x, CURSOR_VOFFSET);
            }
        }
    }

    public void drawCursor (Graphics g, int x0, int y0, int width, int height){ //Tishka17
         int x=xBorder+(xCursor*imgWidth);
         g.setColor(getColorBGnd());
         g.fillRect(0,y0,width, height);
         super.drawCursor(g, x+x0,y0,imgWidth, lineHeight);
         getMainBarItem().setElementAt(getTipString(), 0);
     }

    protected void drawBalloon(final Graphics g, int balloon, final String text) {
        if (cursor==0) balloon+=lineHeight+Balloon.getHeight();
        int x=xBorder+(xCursor*imgWidth);
        g.translate(x, balloon);
        Balloon.draw(g, text);
    }

    public void pageLeft(){
        if (xCursor>0)
            xCursor--;
        else {
            if (cursor==0) {
                keyDwn();
                pageLeft();
                return;
            }
            xCursor=xCnt-1;
            keyUp();
            setRotator();
        }
    }
    public void pageRight(){
        if ( xCursor < ( (cursor<lines-1)?(xCnt-1):(xLastCnt-1) ) ) {
            xCursor++;
            setRotator();
        } else {
            if (cursor==lines-1) return;
            xCursor=0;
            keyDwn();
        }
    }
    public void keyDwn(){
        super.keyDwn();
        if (cursor!=lines-1)
            return;
        if (xCursor >= xLastCnt)
            xCursor=xLastCnt-1;
    }

    public void moveCursorEnd() {
        super.moveCursorEnd();
        xCursor=xLastCnt-1;
    }

    public void moveCursorHome() {
        super.moveCursorHome();
        xCursor=0;
    }

    public String getTipString() {
        return (String) smileTable.elementAt(cursor*xCnt+xCursor);
    }

//#ifdef TOUCH
    protected void pointerPressed(int x, int y) {
        super.pointerPressed(x,y);
        if (pointer_state != POINTER_SECOND &&
                pointer_state != POINTER_FIRST &&
                pointer_state != POINTER_NONE) {
            return;
        }
        if (x>=xCnt*imgWidth) return;
        if (pointer_state == POINTER_SECOND && xCursor!= x/imgWidth)
            pointer_state = POINTER_NONE;
        xCursor=x/imgWidth;
        setRotator();
        if (cursor!=lines-1) return;
        if (xCursor >= xLastCnt) xCursor=xLastCnt-1;
    }
//#endif

    public boolean isSelectable() { return true; }

    public boolean handleEvent(int keyCode) { return false; }
    
    public boolean handleEvent(int x, int y) {
        return false;
    }

    public int showGraphicsMenu() {
        return -1;
    }

    public String touchLeftCommand(){ return SR.get(SR.MS_SELECT); }
    public String touchRightCommand(){ return SR.get(SR.MS_BACK); }
}
//#endif
