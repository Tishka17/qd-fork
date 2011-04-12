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

import client.Config;
import javax.microedition.lcdui.Font;

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

    public final static int SMALL = Font.SIZE_SMALL;
    public final static int MEDIUM = Font.SIZE_MEDIUM;
    public final static int LARGE = Font.SIZE_LARGE;

    public final static int PLAIN = Font.STYLE_PLAIN;
    public final static int BOLD = Font.STYLE_BOLD;
    public final static int ITALIC = Font.STYLE_ITALIC;

    private static Font getSmallBoldFont() {
        if (smallBold == null) {
            smallBold = Font.getFont(Font.FACE_PROPORTIONAL, Config.useItalic ? (BOLD | ITALIC) : BOLD, SMALL);
        }
        return smallBold;
    }

    private static Font getMiddleBoldFont() {
        if (middleBold == null) {
            middleBold = Font.getFont(Font.FACE_PROPORTIONAL, Config.useItalic ? (BOLD | ITALIC) : BOLD, MEDIUM);
        }
        return middleBold;
    }

    private static Font getBigBoldFont() {
        if (bigBold == null) {
            bigBold = Font.getFont(Font.FACE_PROPORTIONAL, Config.useItalic ? (BOLD | ITALIC) : BOLD, LARGE);
        }
        return bigBold;
    }

    private static Font getSmallFont() {
        if (small == null) {
            small = Font.getFont(Font.FACE_PROPORTIONAL, Config.useItalic ? ITALIC : PLAIN, SMALL);
        }
        return small;
    }

    private static Font getMiddleFont() {
        if (middle == null) {
            middle = Font.getFont(Font.FACE_PROPORTIONAL, Config.useItalic ? ITALIC : PLAIN, MEDIUM);
        }
        return middle;
    }

    private static Font getBigFont() {
        if (big == null) {
            big = Font.getFont(Font.FACE_PROPORTIONAL, Config.useItalic ? ITALIC : PLAIN, LARGE);
        }
        return big;
    }

    public static Font getFont(boolean isBold, int size) {
        switch (size) {
            case SMALL:
                return (isBold) ? getSmallBoldFont() : getSmallFont();
            case MEDIUM:
                return (isBold) ? getMiddleBoldFont() : getMiddleFont();
            case LARGE:
                return (isBold) ? getBigBoldFont() : getBigFont();
        }
        return getSmallFont();
    }
}
