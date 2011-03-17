/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

//#ifdef FILE_IO
package ui.controls.form;

import font.FontCache;
import io.file.browse.Browser;
import io.file.browse.BrowserListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import locale.SR;
import midlet.BombusQD;
import ui.IconTextElement;
import ui.VirtualList;

/**
 *
 * @author esprit
 */

public final class PathSelector extends IconTextElement implements BrowserListener {
    private String caption;
    private String path;

    private Font font;
    private Font capFont;

    private int capFontH;
    private int fontH;

    private boolean open;

    public PathSelector(String caption, String path, boolean open) {
        super(null);

        this.caption = caption;
        this.path = path;

        this.capFont = FontCache.getFont(true, FontCache.roster);
        this.capFontH = capFont.getHeight();

        this.font = FontCache.getFont(false, FontCache.roster);
        this.fontH = font.getHeight();

        this.open = open;
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

        boolean isPathSpecified = !(path == null || path.equals(""));
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
        Displayable d = BombusQD.getInstance().display.getCurrent();
        new Browser(null, BombusQD.getInstance().display, d, this, !open);
    }

    public void BrowserFilePathNotify(String path) {
        this.path = path;
    }
}
//#endif
