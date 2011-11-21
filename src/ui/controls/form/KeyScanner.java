/*
 * KeyScanner.java
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

//#ifdef USER_KEYS
import client.Config;
import colors.ColorTheme;
import font.FontCache;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import ui.IconTextElement;
import ui.VirtualList;

/**
 *
 * @author Mars
 */
public class KeyScanner extends IconTextElement{
    protected String caption;
    
    private Font font;
    private Font captionFont;
    private int fontHeight;
    private int captionFontHeight;

    public boolean selected;
    public int keyCode;
    public boolean isLong;

    public KeyScanner( String caption){
        super( null);
        this.keyCode = 0;
        text = "";
        if( caption ==null)
            this.caption= "";
        else
            this.caption= caption;

        font = FontCache.getFont(false, Config.rosterFont);
        fontHeight = font.getHeight();
        itemHeight = fontHeight;

        captionFont = FontCache.getFont(true, Config.rosterFont);
        captionFontHeight = captionFont.getHeight();
        itemHeight += captionFontHeight;
    }

    public KeyScanner(String caption, int keyCode, boolean isLong) {
        this(caption);
        this.keyCode = keyCode;
        this.isLong = isLong;
        if (keyCode!=0) {
            if (isLong) text = "Long ("+ui.keys.UserKey.getKeyName(keyCode)+")";
            else  text = "("+ui.keys.UserKey.getKeyName(keyCode)+")";
        }
    }

    public int getCaptionLength( ){
        if( caption ==null) return 0;
        if( caption.length() ==0) return 0;
        return captionFont.stringWidth(caption);
    }

    public int getTextLength( ){
        if( text ==null) return 0;
        if( text.length() ==0) return 0;
        return font.stringWidth( text);
    }

    public String toString() {
        return getValue();
    }

    public void onSelect( VirtualList view){
        if( !selected){
            selected= true;
            text= "press your key";
        }
    }

    public boolean eventKeyPressed(int keyCode){
        if( selected){
            selected = false;
            this.keyCode = keyCode;
            this.isLong = false;
            text = "(" + ui.keys.UserKey.getKeyName(keyCode) +")";
            return true;
        }//ifel
        return false;
    }
    public boolean eventKeyLong(int keyCode){
        if( selected){
            selected = false;
            this.keyCode = keyCode;
            this.isLong = true;
            text = "Long (" + ui.keys.UserKey.getKeyName(keyCode) +")";
            return true;
        }//ifel
        return false;
    }
    public int getKeyCode( ){
        return keyCode;
    }

    public String getValue() {
        return text;
    }

    public int getVHeight() {
        return itemHeight;
    }

    public int getVWidth() {
        if (caption != null) {
            return captionFont.stringWidth(caption);
        }
        return -1;
    }

    public void drawItem(VirtualList view, Graphics g, int ofs, boolean sel) {
        int width = g.getClipWidth();
        int height = fontHeight;

        int oldColor = g.getColor();

        int xOffset = 0;
        int baseOffset = getOffset();

        int y = 0;
        if (caption != null) {
            xOffset = (getCaptionLength() > width) ? -ofs + baseOffset: baseOffset;
            g.setFont(captionFont);
            g.drawString(caption, xOffset, y, Graphics.TOP | Graphics.LEFT);
            y = captionFontHeight;
        }

        if (text.length() == 0) {
            width = width - midlet.BombusQD.cf.scrollWidth - 5;
            g.setColor(ColorTheme.getColor(ColorTheme.LIST_BGND));
            g.fillRect(5, y, width, height - 3);

            
            if (sel)
                g.setColor(ColorTheme.getColor(ColorTheme.CURSOR_OUTLINE));
            else 
                g.setColor(ColorTheme.getColor(ColorTheme.CONTROL_ITEM));
            g.drawRoundRect(5, y, width, height - 3, 8, 8); //Tishka17
        }

        g.setColor(oldColor);

        if (getTextLength() > 0) {
            xOffset = (getTextLength() > width) ? -ofs + baseOffset: baseOffset;
            g.setFont(font);
            g.drawString(toString(), xOffset, y, Graphics.TOP | Graphics.LEFT);
        }
    }
}
//#endif
