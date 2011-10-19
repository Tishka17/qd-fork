/*
 * Browser.java
 *
 * Created on 26.09.2006, 23:42
 *
 * Copyright (c) 2005-2008, Eugene Stahov (evgs), http://bombus-im.org
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
package io.file.browse;

import client.StaticData;
import menu.MenuListener;
import menu.Command;
import ui.MainBar;
import images.RosterIcons;
import io.file.FileIO;
import java.util.Enumeration;
import java.util.Vector;
import locale.SR;
import ui.IconTextElement;
import ui.VirtualElement;
import ui.VirtualList;
import ui.GMenu;
import ui.GMenuConfig;
import ui.controls.AlertBox;

/**
 *
 * @author evgs
 */

public class Browser extends VirtualList implements MenuListener {
    public static final int UNKNOWN_FILE = -1;
    public static final int TEXT_FILE = 0;
    public static final int IMAGE_FILE = 1;
    public static final int SOUND_FILE = 2;    

    private Vector dir;

    Command cmdOk;
    Command cmdSelect;
    Command cmdView;
    Command cmdRoot;
    Command cmdDelete;
    Command cmdExit;
    //Command cmdLoadPngSkin=new Command("Load Resource from File", Command.BACK, 100);

    private String path;
    private BrowserListener browserListener;

    private boolean getDirectory;

    /** Creates a new instance of Browser */
    public Browser(String path, BrowserListener browserListener, boolean getDirectory) {
        super();

        cmdOk=new Command(SR.get(SR.MS_BROWSE), 0x43);
        cmdSelect=new Command(SR.get(SR.MS_SELECT), 0x60);
        cmdView=new Command(SR.get(SR.MS_VIEW), 0x77);
        cmdRoot=new Command(SR.get(SR.MS_ROOT), 0x15);
        cmdDelete=new Command(SR.get(SR.MS_DELETE), 0x41);
        cmdExit=new Command(SR.get(SR.MS_CANCEL), 0x33);

        this.browserListener=browserListener;
	this.getDirectory=getDirectory;
        this.path=(path==null)?StaticData.getInstance().previousPath:path;

        // test for empty path
        if (path==null) path="";

        setMainBarItem(new MainBar(2, null, null, false));

        menuCommands.removeAllElements();

        addCommand(cmdOk);
        if (getDirectory) {
            addCommand(cmdSelect);
        } else {
            addCommand(cmdView);
        }
        addCommand(cmdDelete);
        addCommand(cmdRoot);

        addCommand(cmdExit);

        // trim filename
        int l=path.lastIndexOf('/');
        if (l<0)
            path="";
        else
            path=path.substring(0,l+1);

        chDir(path);
    }

    protected int getItemCount() { return dir.size(); }

    protected VirtualElement getItemRef(int index) { return (VirtualElement) dir.elementAt(index); }

    public void cmdCancel() {
        if (!chDir("../")) {
            destroyView();
            return;
        }
        redraw();
    }

    public void commandAction(Command command) {
        if (command==cmdRoot) {
            path="";
            chDir(path);
        } else if (command==cmdOk) {
            eventOk();
        } else if (command==cmdSelect) {
            String f=((FileItem)getFocusedObject()).name;
            if (f.endsWith("/")) {
                if (f.startsWith("../")) {
                    f="";
                }
                if (browserListener==null) {
                    return;
                }
                destroyView();

                browserListener.BrowserFilePathNotify(path+f);
                browserListener = null;
            }
            //todo: choose directory here, drop ../
        } else if (command==cmdDelete) {
            AlertBox box = new AlertBox( "Alert", SR.get(SR.MS_DELETE) + '?' , AlertBox.BUTTONS_YESNO) {
               public void yes() { fileDelete(); }
            };
            box.show();
        } else if (command==cmdView) {
            showFile();
        } else if (command==cmdExit) { 
            destroyView(); 
        }
    }

    public void destroyView(){
        StaticData.getInstance().previousPath=path;
        super.destroyView();
    }

     private boolean chDir(String relativePath) {
        String focus="";
         if (relativePath.startsWith("/")) {
             path=relativePath;
         } else if (relativePath.startsWith("../")) {
            if (path.length()==0) return false;
            if (path.length()==1) {
                path="";
            } else {
                int remainderPos=path.lastIndexOf('/', path.length()-2) + 1;
                focus=path.substring(remainderPos);
                path=path.substring(0, 1+path.lastIndexOf('/', path.length()-2));
            }
        } else {
            //if (path.length()==0) path="/";
             path+=relativePath;
        }
        readDirectory(this.path);
        sort(dir);

        for (int i=0; i<dir.size(); i++) {
            if ( ((FileItem)dir.elementAt(i)).name.equals(focus) ) {
                moveCursorTo(i);
                return true;
            }
        }
        moveCursorHome();
        return true;
     }

    private void readDirectory(String name) {
        getMainBarItem().setElementAt((path.endsWith("/"))?path.substring(0, path.length()-1):path, 0);
        dir=new Vector();
         try {
            FileIO f=FileIO.createConnection(name);
            Enumeration files=f.fileList(getDirectory).elements();
            while (files.hasMoreElements())
                dir.addElement( new FileItem((String) files.nextElement()) );

        } catch (Exception ex) {
            dir.addElement( new FileItem("../(Restricted Access)"));
//#ifdef DEBUG
//#             ex.printStackTrace();
//#endif
        }
    }

    public void fileDelete() {
        String f=((FileItem)getFocusedObject()).name;
        if (f.endsWith("/"))
            return;
        try {
            FileIO fio=FileIO.createConnection(path+f);
            fio.delete();
            fio.close();
            dir.removeElement(getFocusedObject());
            redraw();
        } catch (Exception e) {
//#ifdef DEBUG
//#             e.printStackTrace();
//#endif
        }
    }

    public void showFile() {
        FileItem fi=(FileItem)getFocusedObject();
        if (fi.getType() != UNKNOWN_FILE){
            new ShowFile(path + fi.name, fi.getType()).show();
        }
    }

    public void eventOk() {
        String f=((FileItem)getFocusedObject()).name;
        if (!f.endsWith("/")) {
            if (browserListener==null) {
                showFile();
                return;
            }
            destroyView();

            browserListener.BrowserFilePathNotify(path+f);
            browserListener = null;
            return;
        }
        if (!chDir(f)) {
            destroyView();
            return;
        }

        redraw();
    }

    private static class FileItem extends IconTextElement {

        public String name;
        private int type = UNKNOWN_FILE;

        public FileItem(String name) {
            super(RosterIcons.getInstance());
            this.name=name;
            //TODO: file icons
            imageindex=name.endsWith("/")? RosterIcons.ICON_COLLAPSED_INDEX: RosterIcons.ICON_PROFILE_INDEX;

            String ext=name.substring(name.lastIndexOf('.')+1).toLowerCase();
            String imgs="png.bmp.jpg.jpeg.gif";
            String snds="wav.mid.amr.wav.mp3.aac";
            String txts="txt.log";

            if (txts.indexOf(ext) >= 0) {
                imageindex = RosterIcons.ICON_PRIVACY_ACTIVE;
                type = TEXT_FILE;
            } else if (imgs.indexOf(ext) >= 0) {
                imageindex = 0x57;
                type = IMAGE_FILE;
            } else if (snds.indexOf(ext) >= 0) {
                imageindex = 0x33;
                type = SOUND_FILE;
                return;
            }
        }

        public String toString() { return (name.endsWith("/"))?name.substring(0, name.length()-1):name; }

        public int compare(IconTextElement right){
            FileItem fileItem=(FileItem) right;

            int cpi=imageindex-fileItem.imageindex;
            if (cpi==0) cpi=name.compareTo(fileItem.name);
            return cpi;
        }

        public int getType() {
            return type;
        }
    }

    public int showGraphicsMenu() {
       // commandState();
        menuItem = new GMenu(this, menuCommands);
        GMenuConfig.getInstance().itemGrMenu = GMenu.BROWSER;
        return GMenu.BROWSER;
    }

    public void touchRightPressed() { cmdCancel(); }
}
//#endif
