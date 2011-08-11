/*
 * ColorThemeSelector.java
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

import colors.ColorTheme;
import java.util.Vector;
import ui.VirtualCanvas;

public class ColorThemeSelector extends DropChoiceBox {
    private Vector paths;

    public ColorThemeSelector(String caption) {
        super(caption);

        paths = new Vector();
    }

    public void append(String name, String path) {
        paths.addElement(path);
        super.append(name);
    }

    public void setSelectedIndex(int index) {
        this.index = index;

        String themePath = (String)paths.elementAt(index);

        ColorTheme.loadSkin(themePath, 1, false);
    }

    public boolean eventKeyPressed(int keyCode) {
        switch (keyCode) {
            case VirtualCanvas.NAVIKEY_LEFT:
            case VirtualCanvas.KEY_NUM4:
                if (--index < 0) {
                    index = 0;
                }
                setSelectedIndex(index);
                return true;
            case VirtualCanvas.NAVIKEY_RIGHT:
            case VirtualCanvas.KEY_NUM6:
                if (++index > items.size() - 1) {
                    index = items.size() - 1;
                }
                setSelectedIndex(index);
                return true;
        }
        return false;
    }
}
