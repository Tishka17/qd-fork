/*
 * TextInput.java
 *
 * Created on 19.05.2008, 23:01
 *
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
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

import client.Config;
import colors.ColorTheme;
import font.FontCache;
import io.NvStorage;
import java.io.DataInputStream;
import java.io.EOFException;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import ui.IconTextElement;
import ui.VirtualList;
import ui.input.InputTextBox;
import ui.input.InputTextBoxNotify;

/**
 *
 * @author ad
 */

public class TextInput extends IconTextElement implements InputTextBoxNotify {
    protected String text;
    protected String caption;

    public String id;
    private int boxType;

    private Font font;
    private Font captionFont;
    private int fontHeight;
    private int captionFontHeight;

    private int colorItem;
    private int colorBorder;
    private int colorBGnd;

    public TextInput(String caption, String text, int boxType) {
        this(caption, text, null, boxType);
    }

    public TextInput(String caption, String text, String id, int boxType) {
        super(null);
        this.caption = caption;
        this.id = id;
        this.boxType = boxType;

        colorItem = ColorTheme.getColor(ColorTheme.CONTROL_ITEM);
        colorBorder = ColorTheme.getColor(ColorTheme.CURSOR_OUTLINE);
        colorBGnd = ColorTheme.getColor(ColorTheme.LIST_BGND);

        font = FontCache.getFont(false, Config.rosterFont);
        fontHeight = font.getHeight();
        itemHeight = fontHeight;

        if (caption != null) {
            captionFont = FontCache.getFont(true, Config.rosterFont);
            captionFontHeight = captionFont.getHeight();
            itemHeight += captionFontHeight;
        }
        if (text == null && id != null) {
            String tempText = "";
            try {
                DataInputStream is = NvStorage.ReadFileRecord(id, 0);
                try {
                    tempText = is.readUTF();
                } catch (EOFException e) {
                    is.close();
                    is = null;
                }
            } catch (Exception e) {/* no history available */

            }
            this.text = (tempText == null) ? "" : tempText;
        } else {
            this.text = (text == null) ? "" : text;
        }
    }

    public int getCaptionLength() {
        if (caption == null) {
            return 0;
        }
        if (caption.length() == 0) {
            return 0;
        }
        return captionFont.stringWidth(caption);
    }

    public int getTextLength() {
        if (text == null) {
            return 0;
        }
        if (text.length() == 0) {
            return 0;
        }
        return font.stringWidth(text);
    }

    public String toString() {
        return (0 == getTextLength()) ? caption : getValue();
    } //Tishka17

    public void onSelect(VirtualList view) {
        //new EditBox(caption, text, this, boxType);
        InputTextBox input = new InputTextBox(caption, text, id, 255, boxType);
        input.setNotifyListener(this);
        input.show();
    }

    public void okNotify(String text) {
        this.text = text;
    }

    public String getValue() {
        return (text == null) ? "" : text;
    }

    public void setValue(String text) {
        this.text = (text == null) ? "" : text;
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

    public String getText() {
        return getValue();
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
            g.setColor(colorBGnd);
            g.fillRect(5, y, width, height - 3);

            g.setColor((sel) ? colorBorder : colorItem);
            g.drawRoundRect(5, y, width, height - 3, 8, 8); //Tishka17
        }

        g.setColor(oldColor);

        if (getTextLength() > 0) {
            xOffset = (getTextLength() > width) ? -ofs + baseOffset: baseOffset;
            g.setFont(font);
            g.drawString(getText(), xOffset, y, Graphics.TOP | Graphics.LEFT);
        }
    }

    public boolean isSelectable() {
        return true;
    }
}