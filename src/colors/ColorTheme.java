/*
 * ColorTheme.java
 *
 * Created on 22.05.2008, 22:39
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
package colors;

import io.NvStorage;
//#ifdef FILE_IO
import io.file.FileIO;
//#endif
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import midlet.BombusQD;
import util.StringLoader;
//#ifdef DETRANSLIT
//# import util.DeTranslit;
//#endif

/**
 *
 * @author ad,aqent
 */

public class ColorTheme {
    public final static byte BALLOON_INK = 0;
    public final static byte BALLOON_BGND = 1;
    public final static byte LIST_BGND = 2;
    public final static byte LIST_BGND_EVEN = 3;
    public final static byte LIST_INK = 4;
    public final static byte MSG_SUBJ = 5;
    public final static byte MSG_HIGHLIGHT = 6;
    public final static byte DISCO_CMD = 7;
    public final static byte BAR_BGND = 8;
    public final static byte BAR_BGND_BOTTOM = 9;
    public final static byte BAR_INK = 10;
    public final static byte CONTACT_DEFAULT = 11;
    public final static byte CONTACT_CHAT = 12;
    public final static byte CONTACT_AWAY = 13;
    public final static byte CONTACT_XA = 14;
    public final static byte CONTACT_DND = 15;
    public final static byte CONTACT_J2J = 16;
    public final static byte GROUP_INK = 17;
    public final static byte BLK_INK = 18;
    public final static byte BLK_BGND = 19;
    public final static byte MESSAGE_IN = 20;
    public final static byte MESSAGE_OUT = 21;
    public final static byte MESSAGE_PRESENCE = 22;
    public final static byte MESSAGE_AUTH = 23;
    public final static byte MESSAGE_HISTORY = 24;
    public final static byte MESSAGE_IN_S = 25;
    public final static byte MESSAGE_OUT_S = 26;
    public final static byte MESSAGE_PRESENCE_S = 27;
    public final static byte PGS_REMAINED = 28;
    public final static byte PGS_COMPLETE_TOP = 29;
    public final static byte PGS_COMPLETE_BOTTOM = 30;
    public final static byte PGS_INK = 31;
    public final static byte HEAP_TOTAL = 32;
    public final static byte HEAP_FREE = 33;
    public final static byte CURSOR_BGND = 34;
    public final static byte CURSOR_OUTLINE = 35;
    public final static byte SCROLL_BRD = 36;
    public final static byte SCROLL_BAR = 37;
    public final static byte SCROLL_BGND = 38;
    public final static byte POPUP_MESSAGE_INK = 39;
    public final static byte POPUP_MESSAGE_BGND = 40;
    public final static byte POPUP_SYSTEM_INK = 41;
    public final static byte POPUP_SYSTEM_BGND = 42;
    public final static byte SECOND_LINE = 43;
    public final static byte CONTROL_ITEM = 44;
    public final static byte GRADIENT_BGND_LEFT = 45;
    public final static byte GRADIENT_BGND_RIGHT = 46;
    public final static byte GRADIENT_CURSOR_1 = 47;
    public final static byte GRADIENT_CURSOR_2 = 48;
    public final static byte TRANSPARENCY_ARGB = 49;
    public final static byte GRAPHICS_MENU_BGNG_ARGB = 50;
    public final static byte GRAPHICS_MENU_FONT = 51;

    private final static int[] defColors = {
        // balloon ink, bg
        0x000000, 0xfcaaaa,
        // list bg, bge, ink
        0xfdfcfc, 0xf7e1e1, 0x000000,
        // msg s, hi
        0x470c49, 0x000000,
        // disco
        0x000080,
        // bar bg, bgb, ink
        0xff0000, 0x900000, 0xffffff,
        // contact d, c, a, x, dnd, j2j
        0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000042,
        // group ink
        0x000000,
        // blk ink, bg
        0xffffff, 0x11b3a9,
        // message i, o, p, a, h
        0x000000, 0xbd0d0d, 0x98999f, 0x98999f, 0x000000,
        // is, os, ps
        0x048586, 0x067a81, 0x008387,
        // pgs rem, ct, cb, ink
        0xffffff, 0xececec, 0xd4d4d4, 0x000000,
        // heap t, f (group grad t, b)
        //0xffffff, 0x6e25c8, OLD!!!
        0xccd6e2, 0xaeb1bb,
        // cursor bg, ol
        0xe9736d, 0x000000,
        // scroll bord, bar, bg
        0x790e0e, 0xc41616, 0x7f7575,
        // popup m/ink, m/bg, s/ink, s/bg
        0xffffff, 0x000000, 0xffffff, 0x000000,
        // secong contact line ink
        0x000000,
        // control item
        0x1ea5c5,
        // bg grad left, right
        0xffffff, 0xc9ddff,
        // cursor grad t, b
        0xffc2a5, 0xeba6b3,
        // transp argb
        0x01000000,
        // gmenu bg argb, ink
        0xf4f5c5, 0x000000
    };

    private static int[] colorsArray;

    private ColorTheme() {};

    public static void initColors() {
        colorsArray = null;
        colorsArray = new int[defColors.length];
        System.arraycopy(defColors, 0, colorsArray, 0, defColors.length);
    }

    public static int size() {
        return defColors.length;
    }

    private final static String[] items = {
        "BALLOON_INK", "BALLOON_BGND",
        "LIST_BGND", "LIST_BGND_EVEN", "LIST_INK",
        "MSG_SUBJ", "MSG_HIGHLIGHT",
        "DISCO_CMD",
        "BAR_BGND", "BAR_BGND_BOTTOM", "BAR_INK",
        "CONTACT_DEFAULT", "CONTACT_CHAT", "CONTACT_AWAY", "CONTACT_XA", "CONTACT_DND", "CONTACT_J2J",
        "GROUP_INK",
        "BLK_INK", "BLK_BGND",
        "MESSAGE_IN", "MESSAGE_OUT", "MESSAGE_PRESENCE", "MESSAGE_AUTH", "MESSAGE_HISTORY",
        "MESSAGE_IN_S", "MESSAGE_OUT_S", "MESSAGE_PRESENCE_S",
        "PGS_REMAINED", "PGS_COMPLETE_TOP", "PGS_COMPLETE_BOTTOM", "PGS_INK",
        "HEAP_TOTAL", "HEAP_FREE",
        "CURSOR_BGND", "CURSOR_OUTLINE",
        "SCROLL_BRD", "SCROLL_BAR", "SCROLL_BGND",
        "POPUP_MESSAGE_INK", "POPUP_MESSAGE_BGND", "POPUP_SYSTEM_INK", "POPUP_SYSTEM_BGND",
        "SECOND_LINE",
        "CONTROL_ITEM",
        "GRADIENT_BGND_LEFT", "GRADIENT_BGND_RIGHT",
        "GRADIENT_CURSOR_1", "GRADIENT_CURSOR_2",
        "TRANSPARENCY_ARGB", "GRAPHICS_MENU_BGNG_ARGB", "GRAPHICS_MENU_FONT"
    };

    public static void setColor(int id, int color) {
        colorsArray[id] = color;
    }

    public static int getColor(int id) {
        return colorsArray[id];
    }

    public static void invertSkin() {
        int size = colorsArray.length;
        for (int i = 0; i < size; ++i) {
            colorsArray[i] = (colorsArray[i] & 0xff000000) | ((~colorsArray[i]) & 0x00ffffff);//invert 3 bytes only, do not invert alpha
        }
        saveToStorage();
    }

//#if NICK_COLORS
    public static int strong(int color) {
        if (color == colorsArray[MESSAGE_IN]) {
            return colorsArray[MESSAGE_IN_S];
        } else if (color == colorsArray[MESSAGE_OUT]) {
            return colorsArray[MESSAGE_OUT_S];
        } else if (color == colorsArray[MESSAGE_PRESENCE]) {
            return colorsArray[MESSAGE_PRESENCE_S];
        }
        return color;
    }
//#endif

    private static Hashtable skin;
    private static String skinFile;
    private static int resourceType = 1;

    public static void loadFromStorage() {
        try {
            DataInputStream inputStream = NvStorage.ReadFileRecord("ColorDB", 0);
            for (int i = 0; i < size(); ++i) {
                colorsArray[i] = inputStream.readInt();
            }
            inputStream.close();
            inputStream = null;
        } catch (Exception e) {
        }
    }

    public static void saveToStorage() {
        DataOutputStream outputStream = NvStorage.CreateDataOutputStream();
        try {
            int size = colorsArray.length;
            for (int i = 0; i < size; ++i) {
                outputStream.writeInt(colorsArray[i]);
            }
        } catch (IOException e) {
        }
        NvStorage.writeFileRecord(outputStream, "ColorDB", 0, true);
    }

    public static String getSkin() {
        StringBuffer body = new StringBuffer("xmlSkin\t");
//#ifdef DETRANSLIT
//#        body.append(DeTranslit.translit(BombusQD.sd.account.getNickName()));
//#else
        body.append(BombusQD.sd.account.getNickName());
//#endif
        body.append("\r\n");
        for (int i = 0; i < size(); i++) {
            body.append(items[i])
                    .append('\t')
                    .append(getColorString(colorsArray[i]))
                    .append("\r\n");
        }
        return body.toString();
    }

    public static void loadSkin(String skinF, int resourceT, boolean save) {
        skinFile = skinF;
        resourceType = resourceT;
        try {
            colorsArray = null;
            colorsArray = new int[size()];
            for (int i = 0; i < size(); ++i) {
                int color = colorsArray[i];
                colorsArray[i] = loadInt(items[i], color);
            }
            if (save) {
                saveToStorage();
            }
        } catch (Exception e) {
        }
        skin = null;
        skinFile = null;
    }

    private static int loadInt(String key, int defaultColor) {
        if (skin == null) {
            switch (resourceType) {
//#if FILE_IO
                case 0: // from fs
                    FileIO f = FileIO.createConnection(skinFile);
                    try {
                        InputStream s=f.openInputStream();
                        skin = new StringLoader().hashtableLoader(s);
                    } catch (IOException e){
                        return defaultColor;
                    }
                    break;
//#endif
                case 1: // from jar
                    skin = new StringLoader().hashtableLoader(skinFile);
                    break;
                case 2: // from message
                    skin = new StringLoader().hashtableLoaderFromString(skinFile);
                    break;
            }
        }
        String value = (String)skin.get(key);
        if (null == value) {
            return defaultColor;
        }
        if (value.length()==10) {
            return (Integer.parseInt(value.substring(2,4), 16)<<24) | Integer.parseInt(value.substring(4), 16);
        } else {
            return  Integer.parseInt(value.substring(2), 16);
        }
    }

    public static String getColorString(int color) {
        StringBuffer ncolor = new StringBuffer("0x");
        String col = Integer.toHexString(color);
        int size = 6 - col.length();
        for (int i = 0; i < size; i++) {
            ncolor.append('0');
        }
        ncolor.append(col);
        return ncolor.toString();
    }

    public static int getRed(int color) {
        return ((color >> 16) & 0xFF);
    }

    public static int getGreen(int color) {
        return ((color >> 8) & 0xFF);
    }

    public static int getBlue(int color) {
        return (color & 0xFF);
    }
    public static int getAlpha(int color) {
        return ((color>>24) & 0xFF);
    }

    public static int getColor(int a, int r, int g, int b){
        return (a<<24) | (r<<16) | (g<<8) | b;
    }
    public static String colorToString(int cRed, int cGreen, int cBlue, int cAlpha) {
        StringBuffer color = new StringBuffer(10);
        color.append("0x")
                .append(expandHex(cAlpha))
                .append(expandHex(cRed))
                .append(expandHex(cGreen))
                .append(expandHex(cBlue));
        return color.toString();
    }

    public static String colorToString(int cRed, int cGreen, int cBlue) {
        StringBuffer color = new StringBuffer(8);
        color.append("0x")
                .append(expandHex(cRed))
                .append(expandHex(cGreen))
                .append(expandHex(cBlue));
        return color.toString();
    }

    public static String expandHex(int eVal) {
        String rVal = Integer.toHexString(eVal);
        if (rVal.length() == 1) {
            rVal = "0" + rVal;
        }

        return rVal;
    }
}
