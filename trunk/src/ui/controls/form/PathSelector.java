/*
 * PathSelector.java
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

//#ifdef FILE_IO
package ui.controls.form;

import client.Config;
import font.FontCache;
import io.file.browse.Browser;
import io.file.browse.BrowserListener;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import locale.SR;
import ui.IconTextElement;
import ui.VirtualList;

public final class PathSelector extends IconTextElement implements BrowserListener {
    public static final int TYPE_FILE = 0;
    public static final int TYPE_DIR = 1;

    private String caption;
    private String path;

    private Font font;
    private Font capFont;

    private int capFontH;
    private int fontH;

    private int type;

    public PathSelector(String caption, String path, int type) {
        super(null);

        this.caption = caption;
        this.path = path;

        this.capFont = FontCache.getFont(true, Config.rosterFont);
        this.capFontH = capFont.getHeight();

        this.font = FontCache.getFont(false, Config.rosterFont);
        this.fontH = font.getHeight();

        this.type = type;
    }

    public String toString() {
        return caption;
    }

    public String getValue() {
        return path;
    }

    public int getVHeight() {
        if (0 == itemHeight) {
            itemHeight = fontH + capFontH + 1;
        }
        return itemHeight;
    }

    public void drawItem(VirtualList view, Graphics g, int ofs, boolean sel) {
        g.setFont(capFont);

        int xOffset = getOffset();

        g.clipRect(xOffset, 0, g.getClipWidth(), itemHeight);
        g.drawString(caption, xOffset, 0, Graphics.TOP | Graphics.LEFT);

        g.setFont(font);
        String pathStr;

        boolean isPathSpecified = !(path == null || path.length() == 0);
        if (isPathSpecified) {
            pathStr = path;
        } else {
            pathStr = SR.get(SR.MS_PATH_NOT_SPECIFIED);
        }

        int stringWidth = font.stringWidth(pathStr);
        final int yOffset = fontH;
        int lineOffset = yOffset * 2;

        g.drawString(pathStr, xOffset, yOffset, Graphics.TOP | Graphics.LEFT);
        if (isPathSpecified) {
            g.drawLine(xOffset, lineOffset, stringWidth + 3, lineOffset);
        }
    }

    public void onSelect(VirtualList view) {
        switch (type) {
            case TYPE_FILE:
                new Browser(null, this, false).show();
                break;
            case TYPE_DIR:
                new Browser(null, this, true).show();
                break;
        }        
    }

    public void BrowserFilePathNotify(String path) {
        this.path = path;
    }
}
//#endif
