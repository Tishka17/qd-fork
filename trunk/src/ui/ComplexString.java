/*
 * ComplexString.java
 *
 * Created on 12.03.2005, 0:35
 *
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
 */
package ui;

import client.Config;
import images.ImageList;
import font.*;
import java.util.*;
import javax.microedition.lcdui.*;
import colors.ColorTheme;

/**
 *
 * @author Eugene Stahov,aqent
 */
public class ComplexString implements VirtualElement {
    public final static int IMAGE = 0x00000000;
    public final static int COLOR = 0x01000000;
    public final static int RALIGN = 0x02000000;
    public final static int UNDERLINE = 0x03000000;
//#if NICK_COLORS
    public final static int NICK_ON = 0x04000000;
    public final static int NICK_OFF = 0x05000000;
//#endif
    public final static int BOLD = 0x06000000;
    public final static int SMILE = 0x07000000;
    protected Font font = FontCache.getFont(false, Config.msgFont);

    protected int height;
    protected int width;
    private ImageList imageList;
    private int colorBGnd;
    private int color;
    private boolean aniSmile;
    private Vector elementData = new Vector(0);

    /** Creates a new instance of ComplexString */
    public ComplexString() {
        color = ColorTheme.getColor(ColorTheme.LIST_INK);
        colorBGnd = ColorTheme.getColor(ColorTheme.LIST_BGND);
    }

    public ComplexString(ImageList imageList) {
        color = ColorTheme.getColor(ColorTheme.LIST_INK);
        colorBGnd = ColorTheme.getColor(ColorTheme.LIST_BGND);
        this.imageList = imageList;
    }

    public void destroy() {
        //System.out.println("    :::     complex-->elementData:: " + elementData.toString());
        imageList = null;
        elementData = null;
        //elementData = new Vector(0);
        //System.out.println("    :::     complex-->destroy:: " + start + " => 0");
    }

    private int imgHeight() {
        return (imageList == null) ? 0 : imageList.getHeight();
    }

    private int imgWidth() {
        return (imageList == null) ? 0 : imageList.getWidth();
    }

    public int getColor() {
        return color;
    }

    public int getColorBGnd() {
        return colorBGnd;
    }

    public void setColorBGnd(int color) {
        colorBGnd = color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void onSelect(VirtualList view) {

    };

    Font bold;
    char c1, c2;

    public void drawItem(VirtualList view, Graphics g, int offset, boolean selected) {
        boolean ralign = false;
        boolean underline = false;
//#if NICK_COLORS
        boolean nick = false;
//#endif
        int w = offset;
        int dw;
        int imageYOfs = ((getVHeight() - imgHeight()) >> 1);
        int fontYOfs = ((getVHeight() - font.getHeight()) >> 1);
        int imgWidth = imgWidth();

        int size = elementData.size();
        for (int index = 0; index < size; ++index) {
            if (elementData.elementAt(index) == null) {
                continue;
            }
            if (elementData.elementAt(index) instanceof String) {
//#if NICK_COLORS
                if (nick) {
                    int color = g.getColor();
                    //int randColor=randomColor();
                    bold = FontCache.getFont(true, font.getSize());
                    g.setFont(bold);

                    dw = 0;
                    int startDrw = 0;
                    int len = ((String)elementData.elementAt(index)).length();
                    if (((String)elementData.elementAt(index)).startsWith("<nick>")) {
                        len -= 6;//hotfix for <nick>,</nick>
                        startDrw = 6;
                    }

                    g.setColor(ColorTheme.strong(color));   /*(c1>255) ?*/ /* : color*/
                    dw = bold.substringWidth(((String)elementData.elementAt(index)), startDrw, len);

                    if (ralign) {
                        w -= dw;
                    }
                    g.drawSubstring(((String)elementData.elementAt(index)), startDrw, len, w, fontYOfs, Graphics.LEFT | Graphics.TOP);
                    if (!ralign) {
                        w += dw;
                    }

                    g.setColor(color);
                } else {
//#endif
                    g.setFont(font);
                    dw = font.stringWidth((String)elementData.elementAt(index));
                    if (ralign) {
                        w -= dw;
                    }
                    g.drawString((String)elementData.elementAt(index), w, fontYOfs, Graphics.LEFT | Graphics.TOP);
                    if (underline) {
                        int ff = font.getHeight();
                        if (ff > height) {
                            height = ff;
                        }
                        int y = height - 1;
                        g.drawLine(w, y - 1, w + dw, y - 1);
                        underline = false;
                    }
                    if (!ralign) {
                        w += dw;
                    }
//#if NICK_COLORS
                }
//#endif

            } else if (elementData.elementAt(index) instanceof StringBuffer) {
                g.setFont(font);
                StringBuffer sb = (StringBuffer)elementData.elementAt(index);
                int sbSize = sb.length();
                int sbWidth = font.stringWidth(sb.toString());
                char ch;
                if (ralign) {
                    w -= sbWidth;
                }
                int pos = w;
                for (int i = 0; i < sbSize; ++i) {
                    ch = sb.charAt(i);
                    g.drawChar(ch, pos, fontYOfs, Graphics.LEFT | Graphics.TOP);
                    pos += font.charWidth(ch);
                }
                if (!ralign) {
                    w += sbWidth;
                }
                sb = null;

            } else if ((elementData.elementAt(index) instanceof Integer)) { // image element or color
                int i = ((Integer)elementData.elementAt(index)).intValue();
                switch (i & 0xff000000) {
                    case IMAGE: {
                        if (imageList == null) {
                            break;
                        }
                        int iw = aniSmile ? imageList.getWidth(i) : imgWidth;
                        if (ralign) {
                            w -= iw;
                        }
                        imageList.drawImage(g, i, w, imageYOfs);
                        if (!ralign) {
                            w += iw;
                        }
                    }
                    break;
                    case SMILE: {
                        if (imageList == null) {
                            break;
                        }
                        i = i - SMILE;
                        int iw = aniSmile ? imageList.getWidth(i) : imgWidth;
                        imageYOfs = (getVHeight() - imageList.getHeight(i)) >> 1;
                        if (ralign) {
                            w -= iw;
                        }
                        imageList.drawImage(g, i, w, imageYOfs);
                        if (!ralign) {
                            w += iw;
                        }
                    }
                    break;
                    case COLOR:
                        g.setColor(0xFFFFFF & i);
                        break;
                    case RALIGN:
                        ralign = true;
                        w = g.getClipWidth() - 1;
                        break;
                    case UNDERLINE:
                        underline = true;
                        break;
//#if NICK_COLORS
                    case NICK_ON:
                        nick = true;
                        break;
                    case NICK_OFF:
                        nick = false;
                        break;
//#endif
                    }
            } else if (elementData.elementAt(index) instanceof VirtualElement) {
                int clipw = g.getClipWidth();
                int cliph = g.getClipHeight();
                ((VirtualElement)elementData.elementAt(index)).drawItem(view, g, 0, false);
                g.setClip(g.getTranslateX(), g.getTranslateY(), clipw, cliph);

            }
            // } // if ob!=null
        } // for
    }

    public int getVWidth() {
        if (width > 0) {
            return width;  // cached
        }
        int w = 0;
        int imgWidth = imgWidth();
        int elementCount = elementData.size();
        for (int index = 0; index < elementCount; ++index) {
            if (elementData.elementAt(index) != null) {
                if (elementData.elementAt(index) instanceof String) {
                    w += font.stringWidth((String)elementData.elementAt(index));
                } else if ((elementData.elementAt(index) instanceof Integer) && imageList != null) {
                    // image element or color
                    int i = (((Integer)elementData.elementAt(index)).intValue());
                    switch (i & 0xff000000) {
                        case IMAGE:
                            w += imgWidth;
                            break;
                    }
                } // Integer
            } // if ob!=null
        } // for
        return width = w;
    }

    public void setElementAt(Object obj, int index) {
        height = width = 0;
        if (index >= elementData.size()) {
            elementData.setSize(index + 1);
        }
        elementData.setElementAt(obj, index);
    }

    public void setSize(int size) {
        elementData.setSize(size);
    }

    public String elementAt(int index) {
        return (String)elementData.elementAt(index);
    }

    public boolean isEmpty() {
        return elementData.isEmpty();
    }

    public int getVHeight() {
        if (height != 0) {
            return height;
        }
        int elementCount = elementData.size();
        if (elementCount == 0) {
            return -1;
        }
        int h = 0;
        Object obj;
        for (int i = 0; i < elementCount; ++i) {
            obj = elementData.elementAt(i);
            if (obj == null) {
                continue;
            }
            if (obj instanceof String || obj instanceof StringBuffer) {
                h = font.getHeight();
            } else if (obj instanceof Integer) {
                int a = ((Integer)obj).intValue();
                if ((a & 0xff000000) == 0) {
                    h = imageList.getHeight();
                }
                if ((a & 0xff000000) == SMILE) {
                    h = imageList.getHeight(a - SMILE);
                }
            } else if (obj instanceof VirtualElement) {
                h = ((VirtualElement)obj).getVHeight();
            }
            if (h > height) {
                height = h;
            }
        }
        obj = null;
        return height;
    }

    public void addElement(Object obj) {
        height = width = 0;
        elementData.addElement(obj);
        obj = null;
    }

    public void addSmile(int imageIndex, int iw) {
        if ((imageIndex & 0xff000000) == 0) {
            imageIndex = imageIndex | SMILE;
        }
        addElement(new Integer(imageIndex));
        aniSmile = true;
    }

    public void addImage(int imageIndex) {
        addElement(new Integer(imageIndex));
        aniSmile = false;
    }

    public void addColor(int colorRGB) {
        addElement(new Integer(COLOR | colorRGB));
    }

    public void addRAlign() {
        addElement(new Integer(RALIGN));
    }

    public void addUnderline() {
        addElement(new Integer(UNDERLINE));
    }

    public void addBold() {
        addElement(new Integer(BOLD));
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public String getTipString() {
        return null;
    }

    public boolean isSelectable() {
        return true;
    }

    public boolean handleEvent(int keyCode) {
        return false;
    }
}
