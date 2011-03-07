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
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
import ui.controls.form.TrackItem;
import ui.controls.form.DefForm;
import ui.controls.form.LinkString;
import ui.controls.form.SpacerItem;
import ui.controls.form.SimpleString;
import util.StringLoader;
import ui.controls.AlertBox;

/**
 *
 * @author ad,aqent
 */
public class ColorConfigForm extends DefForm
//#if FILE_IO
        implements BrowserListener
//#endif
    {

//#ifdef COLOR_TUNE
    private LinkString configureColors;

    private LinkString invertColors;
    //#endif
//#if FILE_IO
    private LinkString loadFromFile;
    private LinkString saveToFile;

    String filePath;
    private int loadType=0;
//#endif

    private Vector[] files;

    private TrackItem skinFiles;

    private TrackItem argb_bgnd;
    private TrackItem gmenu_bgnd;
    private TrackItem popup_bgnd;
    private TrackItem cursor_bgnd;

    public ColorConfigForm(final Display display, Displayable pView) {
        super(display, pView, SR.get(SR.MS_COLOR_TUNE));

//#ifdef COLOR_TUNE
//#ifdef PLUGINS
//#             if (StaticData.getInstance().Colors) {
//#endif
        invertColors=new LinkString(SR.get(SR.MS_INVERT)) { public void doAction() { ColorTheme.invertSkin(); } };
        configureColors=new LinkString(SR.get(SR.MS_EDIT_COLORS)) { public void doAction() { new ColorsList(display);  } };
        itemsList.addElement(configureColors);
//#ifdef PLUGINS
//#             }
//#endif
//#endif

        try {
            files=new StringLoader().stringLoader("/themes/res.txt",2);
            if (files[0].size()>0) {
                Vector skins=new Vector(0);
                int ind=0;
                int size = files[0].size();
                SimpleString str;
		if (size>1) {
			for (int i=0; i<size; i++) {
			    skins.addElement((String)files[1].elementAt(i));
			    str = new SimpleString(Integer.toString(i).concat("-").concat((String)files[1].elementAt(i)), true);

			    itemsList.addElement(str);
			    if(midlet.BombusQD.cf.path_skin.indexOf((String)files[1].elementAt(i))>-1) ind = i;
			}
			skinFiles = new TrackItem(ind, skins.size() - 1, skins);
			itemsList.addElement(skinFiles);
		}
            }
        } catch (Exception e) {}

       argb_bgnd = new TrackItem(midlet.BombusQD.cf.argb_bgnd/10, 25);
       gmenu_bgnd = new TrackItem(midlet.BombusQD.cf.gmenu_bgnd/10, 25);
       popup_bgnd = new TrackItem(midlet.BombusQD.cf.popup_bgnd/10, 25);
       cursor_bgnd = new TrackItem(midlet.BombusQD.cf.cursor_bgnd/10, 25);
        itemsList.addElement(new SimpleString(SR.get(SR.MS_TRANSPARENT), true));
        itemsList.addElement(new SpacerItem(2));
        itemsList.addElement(new SimpleString(SR.get(SR.MS_BGND_MIDLET), true));
        itemsList.addElement(argb_bgnd);
        itemsList.addElement(new SimpleString(SR.get(SR.MS_GR_MENU), true));
        itemsList.addElement(gmenu_bgnd);
        itemsList.addElement(new SimpleString(SR.get(SR.MS_POPUPS), true));
        itemsList.addElement(popup_bgnd);
        itemsList.addElement(new SimpleString(SR.get(SR.MS_CURSOR_TR), true));
        itemsList.addElement(cursor_bgnd);
        itemsList.addElement(new SpacerItem(5));
//#if FILE_IO
        loadFromFile=new LinkString(SR.get(SR.MS_LOAD_FROM_FILE)) { public void doAction() { initBrowser(1); } };

        saveToFile=new LinkString(SR.get(SR.MS_SAVE_TO_FILE)) { public void doAction() { initBrowser(0); } };

//#endif

//#ifdef COLOR_TUNE
            itemsList.addElement(invertColors);
//#endif
//#ifdef FILE_IO
            itemsList.addElement(loadFromFile);
            itemsList.addElement(saveToFile);
//#endif
 //#ifdef COLOR_TUNE
        addControl(new LinkString(SR.get(SR.MS_CLEAR)) {
            public void doAction() {
                new AlertBox("Query", "Load lime theme?", display, parentView, true) {
                    public void yes() {
                        ColorTheme.loadSkin("/themes/default.txt", 1, true);
                    }

                    public void no() {
                        ColorTheme.init();
                        ColorTheme.saveToStorage();
                    }
                };
            }
        });
//#endif


        //moveCursorTo(getNextSelectableRef(-1));
        attachDisplay(display);
        this.parentView=pView;
    }

    public void cmdOk() {
       midlet.BombusQD.cf.argb_bgnd = argb_bgnd.getValue()*10;
       midlet.BombusQD.cf.gmenu_bgnd = gmenu_bgnd.getValue()*10;
       midlet.BombusQD.cf.popup_bgnd = popup_bgnd.getValue()*10;
       midlet.BombusQD.cf.cursor_bgnd = cursor_bgnd.getValue()*10;
       destroyView();
    }

    public void destroyView() {
        ColorTheme.saveToStorage();
        if(null != files)
            files = null;
	display.setCurrent(parentView);
    }

//#if FILE_IO
    public void initBrowser(int type) {
        loadType=type;
        if (type==0) {
            new Browser(null,display, this, this, true);
        } else if(type==1) {
            new Browser(null, display, this, this, false);
        }
    }

    public void BrowserFilePathNotify(String pathSelected) {
        if (loadType==0) {
            FileIO file=FileIO.createConnection(pathSelected+"skin.txt");
            file.fileWrite(ColorTheme.getSkin().getBytes());
        } else {
            ColorTheme.loadSkin(pathSelected, 0, true);
        }
    }
//#endif
}

