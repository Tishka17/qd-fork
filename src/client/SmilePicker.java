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

package client;
import images.ImageList;
import message.MessageParser;
import images.SmilesIcons;
import locale.SR;
import colors.ColorTheme;
import ui.*;
import java.util.Vector;
import ui.controls.Balloon;

//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.CommandListener;
//# import javax.microedition.lcdui.Command;
//#else
import menu.MenuListener;
import menu.Command;
//#endif

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.TextField;
import javax.microedition.lcdui.TextBox;
//#ifdef GRAPHICS_MENU        
import ui.GMenu;
//#endif   

public class SmilePicker  
        extends VirtualList 
        implements 
//#ifndef MENU_LISTENER
//#         CommandListener,
//#else
        MenuListener,
//#endif
        VirtualElement
{

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
    
    Command cmdCancel;
    Command cmdOk;
     
    private Vector smileTable;

    private TextField tf;
    private TextBox tb;
    private boolean aniSmiles = false;
    
     /** Creates a new instance of SmilePicker */
    public SmilePicker(Display display, Displayable pView, int caretPos, TextField tf, TextBox tb) {
         super(display);
         this.caretPos=caretPos;

         cmdCancel=new Command(SR.get(SR.MS_CANCEL),Command.BACK,99);
         cmdOk=new Command(SR.get(SR.MS_SELECT),Command.OK,1);
         
         if(midlet.BombusQD.cf.msgEditType>0){
           this.tf=tf;
         }else{
           this.tb=tb;
         };
         
         setMainBarItem(new MainBar(locale.SR.get(locale.SR.MS_SELECT)));
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

        commandState();
        this.parentView=pView;
    }
    
    public void commandState() {
//#ifdef MENU_LISTENER
        menuCommands.removeAllElements();
//#endif
        addCommand(cmdOk); cmdOk.setImg(0x43);
        //addCommand(cmdCancel);
        
        setCommandListener(this);
    }
    
    int lineIndex;
    
    public int getItemCount(){ return lines; }
    public VirtualElement getItemRef(int index){ lineIndex=index; return this;}

    public int getVWidth(){ return 0; }
    public int getVHeight() { return lineHeight; }
    public int getColor(){ return ColorTheme.getColor(ColorTheme.LIST_INK); }
    public int getColorBGnd(){ return ColorTheme.getColor(ColorTheme.LIST_BGND); }
    public void onSelect(VirtualList view){
        try {
//#ifdef RUNNING_MESSAGE
           if(midlet.BombusQD.cf.msgEditType>0){
             tf.insert(" "+getTipString()+" ", caretPos);
           }else{
             tb.insert(" "+getTipString()+" ", caretPos);
           };            
//#else
//#             t.insert(getTipString() , caretPos);
//#endif
        } catch (Exception e) { /*e.printStackTrace();*/  }
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
    
    public void commandAction(Command c, Displayable d){
        if (c==cmdCancel) {
            destroyView();
            return;
        }
        if (c==cmdOk) { eventOk(); }
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
        if (pointer_state != client.Constants.POINTER_SECOND && pointer_state != client.Constants.POINTER_NONE)
            return;
        if (x>=xCnt*imgWidth) return;
        if (pointer_state == client.Constants.POINTER_SECOND && xCursor!= x/imgWidth)
            pointer_state = client.Constants.POINTER_NONE;
        xCursor=x/imgWidth;
        setRotator();
        if (cursor!=lines-1) return;
        if (xCursor >= xLastCnt) xCursor=xLastCnt-1;
    }
//#endif
    
    public void userKeyPressed(int keyCode) {
        switch (keyCode) {
            case KEY_NUM3 :
                super.pageLeft(); keyDwn(); break;
            case KEY_NUM9:
                super.pageRight(); break;
            case KEY_NUM4:
                pageLeft(); break;
            case KEY_NUM6:
                pageRight(); break;
        }
        super.userKeyPressed(keyCode);
    }

    public boolean isSelectable() { return true; }
    
    public boolean handleEvent(int keyCode) { return false; }
    
//#ifdef MENU_LISTENER
    
//#ifdef GRAPHICS_MENU        
    public int showGraphicsMenu() {
        commandState();
        menuItem = new GMenu(display, parentView, this, null, menuCommands);        
        GMenuConfig.getInstance().itemGrMenu=GMenu.SMILE_PEAKER;
        return GMenu.SMILE_PEAKER;
    }
//#else
//#     public void showMenu(){ eventOk(); } 
//#endif     

     
    public String touchLeftCommand(){ return SR.get(SR.MS_SELECT); }
    public String touchRightCommand(){ return SR.get(SR.MS_BACK); }
//#endif
}
