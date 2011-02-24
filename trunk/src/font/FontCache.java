/*
 * FontCache.java
 *
 * Created on 5.09.2008, 9:54
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

package font;

import javax.microedition.lcdui.Font;
import client.Config;
/**
 *
 * @author ad,aqent
 */
public class FontCache {

    private static Font small;
    private static Font smallBold;
    
    private static Font middle;
    private static Font middleBold;
    
    private static Font big;
    private static Font bigBold;
    
    public final static int smallSize=Font.SIZE_SMALL;
    public final static int middleSize=Font.SIZE_MEDIUM;
    public final static int bigSize=Font.SIZE_LARGE;
    
    public final static int plain=Font.STYLE_PLAIN;
    public final static int bold=Font.STYLE_BOLD;
    public final static int cursiv=Font.STYLE_ITALIC;
    
    public static int roster=0;
    public static int msg=0;
    public static int bar=0;
    public static int baloon=0;

    public final static int face=Font.FACE_PROPORTIONAL;    
    public final static int face_system=Font.FACE_SYSTEM;
    
    private final static Font getSmallBoldFont() {
        if (smallBold==null) smallBold=Font.getFont(face, Config.getInstance().cursivUse?(bold|cursiv):bold, smallSize);
        return smallBold;
    }
    private final static Font getMiddleBoldFont() {
        if (middleBold==null) middleBold=Font.getFont(face, Config.getInstance().cursivUse?(bold|cursiv):bold, middleSize);
        return middleBold;
    }
    private final static Font getBigBoldFont() {
        if (bigBold==null) bigBold=Font.getFont(face, Config.getInstance().cursivUse?(bold|cursiv):bold, bigSize);
        return bigBold;
    }    
    
    
    private final static Font getSmallFont() {
        if (small==null) small=Font.getFont(face, Config.getInstance().cursivUse?(plain|cursiv):plain, smallSize);
        return small;
    }    
    private final static Font getMiddleFont() {
        if (middle==null) middle=Font.getFont(face, Config.getInstance().cursivUse?(plain|cursiv):plain, middleSize);
        return middle;
    }
    private final static Font getBigFont() {
        if (big==null) big=Font.getFont(face, Config.getInstance().cursivUse?(plain|cursiv):plain, bigSize);
        return big;
    }

    public static Font getRosterFont(boolean isBold) {
        return FontCache.getFont(isBold, FontCache.roster);
    }

    public static Font contactSecondLineFont = FontCache.getFont(false, baloon);
    
    public final static Font getFont(boolean isBold, int size) {//bold,cursiv
        switch (size) {
            case smallSize:
                return (isBold)?getSmallBoldFont():getSmallFont();
            case middleSize:
                return (isBold)?getMiddleBoldFont():getMiddleFont();
            case bigSize:
                return (isBold)?getBigBoldFont():getBigFont();
        }
        return getSmallFont();
    }
    //public final static void resetCache() { roster=msg=bar=baloon=0; }
}
