/*
 * PluginBox.java
 *
 * Created on 29.07.2009, 16:05
 *
 */

package ui.controls.form;

import colors.ColorTheme;
import client.Config;
import javax.microedition.lcdui.Graphics;
import ui.IconTextElement;
import ui.VirtualList;
import images.RosterIcons;
import javax.microedition.lcdui.Font;
/**
 *
 * @author aqent
 */
public class PluginBox extends IconTextElement {
    
    private boolean state=false;
    private String text="";
    private int edit = 0;
    private int colorItem;
    private Font font;
    int fH;


    public PluginBox(String text, boolean state,int edit) {
        super(RosterIcons.getInstance());
        this.text=text;
        this.state=state;
        this.edit=edit;
        colorItem=ColorTheme.getColor(ColorTheme.CONTROL_ITEM);
        switch(midlet.BombusQD.cf.graphicsMenuFont) {
            case 0: font = Font.getFont(Font.FACE_PROPORTIONAL,Font.STYLE_BOLD,Font.SIZE_MEDIUM); break;
            case 1: font = Font.getFont(Font.FACE_PROPORTIONAL,Font.STYLE_BOLD,Font.SIZE_SMALL); break;
            case 2: font = Font.getFont(Font.FACE_PROPORTIONAL,Font.STYLE_BOLD,Font.SIZE_LARGE); break;
        }
        fH = font.getHeight();
    }
    
    public String toString() { return text; }

    public void drawItem(VirtualList view, Graphics g, int ofs, boolean sel) {
       g.setFont(font);
       int offset= 4;
       if (il!=null) {
            if (getImageIndex()!=-1) {
                offset+=ilImageSize;
                il.drawImage(g, getImageIndex(), 2, imageYOfs);
            }
       }       
       g.clipRect(offset, 0, g.getClipWidth(), itemHeight);
       if (text!=null){
         g.drawString(text, offset-ofs, (itemHeight-fH)/2, Graphics.TOP|Graphics.LEFT);
       }
    }

    
    public void onSelect(VirtualList view){
        if(edit>0) {
           state=!state;
           Config cf = midlet.BombusQD.cf;
           switch(edit){
               case 1: cf.module_autostatus = !cf.module_autostatus; break;
               case 2: cf.userKeys = !cf.userKeys; break;               
               case 3: cf.module_avatars = !cf.module_avatars; break;
               case 4: cf.module_history = !cf.module_history; break;
               case 5: cf.module_ie = !cf.module_ie; break;
               case 6: cf.module_tasks = !cf.module_tasks; break;
               case 7: cf.module_classicchat = !cf.module_classicchat; break;
               case 8: cf.debug = !cf.debug; break;
           }
         }
    }
    
    public int getVHeight(){ 
        itemHeight=(ilImageSize>font.getHeight())?ilImageSize:font.getHeight();
		if (itemHeight < midlet.BombusQD.cf.minItemHeight)
			itemHeight = midlet.BombusQD.cf.minItemHeight;
        return itemHeight;
    }    
    
    public int getImageIndex(){ return edit>0 ? (state?0x36:0x37) : 0x36; }
    public boolean getValue() { return state; }
    public boolean isSelectable() { return true; }
}