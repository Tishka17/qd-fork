/*
 * Baloon.java 
 *
 * Created on 6.02.2006, 23:09
 *
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
 */

package ui.controls;

import client.Config;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import colors.ColorTheme;
import font.FontCache;
//#ifdef GRADIENT
import ui.Gradient;
//#endif

public class Balloon {   
    public static int getHeight(){
        return FontCache.getFont(false, Config.baloonFont).getHeight() + 1;
    }
//#ifdef GRADIENT
    private static Gradient bg= new Gradient();
//#endif   
    public static void draw(Graphics g, String text) {

    	if( text ==null) return;

        Font f = FontCache.getFont(false, Config.baloonFont);
        
        g.setFont(f);
        int height=getHeight();
        int width=f.stringWidth(text)+6;
        
        int y=height-g.getTranslateY();
        if (y<0) y=0;
        y-=height-1;
        g.translate(0, y);
        int color=ColorTheme.getColor(ColorTheme.BALLOON_BGND);
//#ifdef GRADIENT
        if (ColorTheme.getAlpha(color)!=0) {
          bg.update(3, 1, width+1, height, color, color, Gradient.CACHED_HORIZONTAL, 0);
          bg.paint(g);
        } else 
//#endif
        {
            g.setColor(color);
            g.fillRect(3, 1, width-2, height-2);
        }
        
        
        g.setColor(ColorTheme.getColor(ColorTheme.BALLOON_INK));
        g.drawRect(2, 0, width-1, height-1);
        g.drawString(text, 5, 1, Graphics.TOP | Graphics.LEFT);
    }
}