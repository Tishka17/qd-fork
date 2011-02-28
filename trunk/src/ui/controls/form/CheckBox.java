package ui.controls.form;

import images.RosterIcons;
import ui.IconTextElement;
import javax.microedition.lcdui.*;
import java.util.Vector;
import util.StringUtils;
import ui.GMenuConfig;
import ui.VirtualList;

/**
 *
 * @author ad,aqent
 */

public class CheckBox extends IconTextElement {
    private boolean isChecked = false;

    private String tip;
    private String text;

    private Vector tipLines;

    private GMenuConfig gm = GMenuConfig.getInstance();

    public CheckBox(String text, boolean isChecked) {
        super(RosterIcons.getInstance());
        if (text.indexOf("%") > -1) {
            this.tip = text.substring(text.indexOf("%"));
            this.text = text.substring(0, text.indexOf("%"));

            tipLines = StringUtils.parseBoxString(this.tip, gm.phoneWidth - 50, getFont());
        } else {
            this.text = text;
            this.tip = null;
        }
        this.isChecked = isChecked;
    }

    public String toString() {
        return text;
    }

    public void onSelect(VirtualList view) {
        isChecked = !isChecked;
    }

    public int getImageIndex() {
        return isChecked ? 0x57 : 0x56;
    }

    public void drawItem(VirtualList view, Graphics g, int ofs, boolean sel) {
        g.setFont(getFont());

        int xOffset = getOffset();
        if (null != il) {
            if (getImageIndex() != -1) {
                il.drawImage(g, getImageIndex(), xOffset, (itemHeight - imgHeight) / 2);
                xOffset += imgHeight;
            }
        }

        if ((tip != null && !isChecked) || (tip == null)) {
            g.clipRect(xOffset, 0, g.getClipWidth(), itemHeight);

            String str = toString();
            if (null != str) {
                int yOffset = getFont().getHeight();
                g.drawString(str, xOffset - ofs, (itemHeight - yOffset) / 2, Graphics.TOP | Graphics.LEFT);
            }
        } else {
            int scrollW = midlet.BombusQD.cf.scrollWidth;

            int fontHeight = getFont().getHeight();
            int size = tipLines.size();

            g.clipRect(xOffset, 0, g.getClipWidth(), getVHeight());

            int helpHeight = fontHeight * (size - 1);
            g.drawString(text, xOffset - ofs, 0, Graphics.TOP | Graphics.LEFT);

            g.setColor(0xFFFFFF);
            g.fillRoundRect(xOffset, fontHeight + 2, gm.phoneWidth - 30 - scrollW, helpHeight, 9, 9);
            g.setColor(0x000000);
            g.drawRoundRect(xOffset, fontHeight + 2, gm.phoneWidth - 30 - scrollW, helpHeight, 9, 9);
            g.setColor(0x000000);

            int y = 0;
            for (int i = 0; i < size; i++) {
                g.drawString((String)tipLines.elementAt(i), xOffset + 3, y + 2, Graphics.TOP | Graphics.LEFT);
                y += fontHeight;
            }
        }
    }

    public int getVHeight() {
        int fontHeight = getFont().getHeight();
        if (isChecked && tip != null) {
            itemHeight = fontHeight * (tipLines.size() + 1);
        } else {
            itemHeight = fontHeight;
        }
        if (itemHeight < il.getHeight()) {
            itemHeight = il.getHeight();
        }
        if (itemHeight < midlet.BombusQD.cf.minItemHeight) {
            itemHeight = midlet.BombusQD.cf.minItemHeight;
        }
        return itemHeight;
    }

    public boolean getValue() {
        return isChecked;
    }

    public boolean isSelectable() {
        return true;
    }

    public boolean handleEvent(int keyCode) {
        switch (keyCode) {
            case 12:
            case 5:
                isChecked = !isChecked;
                return true;
        }
        return false;
    }
}
