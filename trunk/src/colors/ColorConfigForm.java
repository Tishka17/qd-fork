/*
 * ColorConfigForm.java
 *
 * Created on 25.05.2008, 14:05
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
//#if FILE_IO
import io.file.FileIO;
import io.file.browse.Browser;
import io.file.browse.BrowserListener;
//#endif
import java.util.Vector;
import locale.SR;
import ui.controls.form.TrackItem;
import ui.controls.form.DefForm;
import ui.controls.form.LinkString;
import ui.controls.form.SpacerItem;
import ui.controls.form.SimpleString;
import util.StringLoader;
import ui.controls.AlertBox;
import ui.controls.form.ColorThemeSelector;

/**
 *
 * @author ad,aqent
 */

public class ColorConfigForm extends DefForm
//#if FILE_IO
        implements BrowserListener
//#endif
    {

//#if FILE_IO
    private int loadType=0;
//#endif

    private ColorThemeSelector skinFiles;

    private TrackItem argb_bgnd;
    private TrackItem gmenu_bgnd;
    private TrackItem popup_bgnd;
    private TrackItem cursor_bgnd;

    public ColorConfigForm() {
        super(SR.get(SR.MS_COLOR_TUNE));

        Vector[] files = new StringLoader().stringLoader("/themes/res.txt", 2);
        if (files != null) {
            int size = files[0].size();

            if (size > 0) {
                skinFiles = new ColorThemeSelector(SR.get(SR.MS_COLOR_SCHEMES));

                for (int i = 0; i < size; ++i) {
                    String themeName = (String)files[1].elementAt(i);
                    String themePath = (String)files[0].elementAt(i);

                    skinFiles.append(themeName, themePath);
                }

                addControl(skinFiles);
            }
        }

//#ifdef COLOR_TUNE
        addControl(new LinkString(SR.get(SR.MS_EDIT_COLORS)) {
            public void doAction() {
                new ColorsList().show();
            }

        });
//#endif

        addControl(new SimpleString(SR.get(SR.MS_TRANSPARENT), true));
        addControl(new SpacerItem(2));

        addControl(new SimpleString(SR.get(SR.MS_BGND_MIDLET), true));
        argb_bgnd = new TrackItem(midlet.BombusQD.cf.argb_bgnd/10, 25);
        addControl(argb_bgnd);

        addControl(new SimpleString(SR.get(SR.MS_GR_MENU), true));
        gmenu_bgnd = new TrackItem(midlet.BombusQD.cf.gmenu_bgnd/10, 25);
        addControl(gmenu_bgnd);

        addControl(new SimpleString(SR.get(SR.MS_POPUPS), true));
        popup_bgnd = new TrackItem(midlet.BombusQD.cf.popup_bgnd/10, 25);
        addControl(popup_bgnd);

        addControl(new SimpleString(SR.get(SR.MS_CURSOR_TR), true));
        cursor_bgnd = new TrackItem(midlet.BombusQD.cf.cursor_bgnd/10, 25);
        addControl(cursor_bgnd);

        addControl(new SpacerItem(5));

//#ifdef COLOR_TUNE
        addControl(new LinkString(SR.get(SR.MS_INVERT)) {
            public void doAction() {
                ColorTheme.invertSkin();
            }

        });

//#ifdef FILE_IO
        addControl(new LinkString(SR.get(SR.MS_LOAD_FROM_FILE)) {
            public void doAction() {
                initBrowser(1);
            }

        });
        addControl(new LinkString(SR.get(SR.MS_SAVE_TO_FILE)) {
            public void doAction() {
                initBrowser(0);
            }

        });
//#endif
        addControl(new LinkString(SR.get(SR.MS_CLEAR)) {
            public void doAction() {
                AlertBox box = new AlertBox("Query", "Load lime theme?", true) {
                    public void yes() {
                        ColorTheme.loadSkin("/themes/default.txt", 1, true);
                    }

                    public void no() {
                        ColorTheme.initColors();
                        ColorTheme.saveToStorage();
                    }
                };
                box.setParentView(getParentView());
                box.show();
            }
        });
//#endif
    }

    public void cmdOk() {
        midlet.BombusQD.cf.argb_bgnd = argb_bgnd.getValue() * 10;
        midlet.BombusQD.cf.gmenu_bgnd = gmenu_bgnd.getValue() * 10;
        midlet.BombusQD.cf.popup_bgnd = popup_bgnd.getValue() * 10;
        midlet.BombusQD.cf.cursor_bgnd = cursor_bgnd.getValue() * 10;
        destroyView();
    }

    public void destroyView() {
        ColorTheme.saveToStorage();
        super.destroyView();
    }

//#if FILE_IO
    public void initBrowser(int type) {
        loadType=type;
        if (type == 0) {
            new Browser(null, this, true).show();
        } else if(type == 1) {
            new Browser(null, this, false).show();
        }
    }

    public void BrowserFilePathNotify(String path) {
        if (loadType == 0) {
            FileIO file=FileIO.createConnection(path + "skin.txt");
            file.fileWrite(ColorTheme.getSkin().getBytes());
        } else {
            ColorTheme.loadSkin(path, 0, true);
        }
    }
//#endif
}

