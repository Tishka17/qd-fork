/*
 * CheckBox.java 
 *
 * Created on 19.05.2008, 22:16
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

package ui.controls.form;

import colors.ColorTheme;
import images.RosterIcons;
import ui.IconTextElement;
import javax.microedition.lcdui.*;
import java.util.Vector;
import util.StringUtils;
import ui.GMenuConfig;
import ui.VirtualList;
/**
 *
 * @author ad,aqent
 */
public class SimpleTextBox 
        extends IconTextElement {
    
    private boolean state=false;
    private String text="";
    private String text_undo="";    
    private boolean otherWindow=false;    
    private int colorItem;
    Vector checkBox = new Vector(0);
    GMenuConfig gm = GMenuConfig.getInstance();
    
    public SimpleTextBox(String text, boolean state,boolean otherWindow) {
        super(null);
        if(text.indexOf("%")>-1){
          this.text=text.substring(text.indexOf("%"),text.length());
          this.text_undo=text.substring(0,text.indexOf("%"));
        }else{
          this.text=text;
        }
        this.state=state;
        this.otherWindow=otherWindow;
        colorItem=ColorTheme.getColor(ColorTheme.CONTROL_ITEM);
        checkBox.addElement(StringUtils.parseBoxString(this.text, gm.phoneWidth - 50, getFont()));
    }
    
    public SimpleTextBox(String text, boolean state) {
        super(null);
        if(text.indexOf("%")>-1){
          this.text=text.substring(text.indexOf("%"),text.length());
          this.text_undo=text.substring(0,text.indexOf("%"));
        }else{
          this.text=text;
        }
        this.state=state;
        this.otherWindow=false;
        colorItem=ColorTheme.getColor(ColorTheme.CONTROL_ITEM);
        checkBox.addElement(StringUtils.parseBoxString(this.text, gm.phoneWidth - 50, getFont()));
    }
    
    public String toString() { return text; }
    public void onSelect(VirtualList view) {
        state=!state;
    }

    //public int getImageIndex(){ return state?0x57:0x56; }
    
    int fontHeight=getFont().getHeight();
    
    public void drawItem(VirtualList view, Graphics g, int ofs, boolean sel){
       g.setFont(getFont());
       int offset=4;

       if (toString()!=null){
        Vector lines=(Vector)checkBox.elementAt(0);   
        int size=lines.size(); 
        g.clipRect(0, 0, g.getClipWidth(), getVHeight());
        int y = 0;
         if(state){
              int helpHeight=0;
              
              if(toString().indexOf("%")>-1){
                    helpHeight += fontHeight*(size-1);
                    g.drawString(text_undo, offset-ofs, y, Graphics.TOP|Graphics.LEFT);                    
                    g.setColor(0xffffff);
                    g.fillRoundRect(15,fontHeight+2,gm.phoneWidth-30,helpHeight,9,9);
                    g.setColor(0x000000);
                    g.drawRoundRect(15,fontHeight+2,gm.phoneWidth-30,helpHeight,9,9);  
                    g.setColor(0x000000);
                 for(int i=0;i<size;i++){ 
                   if(i==0){
                      g.drawString((String)lines.elementAt(i),3, y, Graphics.TOP|Graphics.LEFT);
                   }else{
                      g.drawString((String)lines.elementAt(i),18 , y + 2, Graphics.TOP|Graphics.LEFT);   
                   }
                   y += fontHeight;
                 }
              }else{
                   g.drawString(toString(), offset-ofs, fontYOfs, Graphics.TOP|Graphics.LEFT);
              }
         } else{
              if(toString().indexOf("%")>-1){
                 g.drawString(text_undo, offset-ofs, fontYOfs, Graphics.TOP|Graphics.LEFT);  
              }else{
                  g.drawString(toString(), offset-ofs, fontYOfs, Graphics.TOP|Graphics.LEFT);
              }
         }         
       }       
    }    

    public int getVWidth(){ 
        return -1;
    }
    
    public int getVHeight(){ 
        Vector str=(Vector)checkBox.elementAt(0);
        if(state){
           if (str.size()<1) {
             return fontHeight;
           }else{
              if(toString().indexOf("%")>-1){
                return fontHeight*str.size() + 7;  
              }else{
                return fontHeight;   
              }
           }            
        }else{
           return fontHeight;
        }
    }
    public boolean getValue() { return state; }
    public boolean isSelectable() { return true; }
}