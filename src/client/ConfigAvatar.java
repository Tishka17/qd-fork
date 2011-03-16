/*
 * ConfigAvatar.java
 *
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
 *
 */

package client;

//#ifdef FILE_IO
import io.file.browse.Browser;
import io.file.browse.BrowserListener;
//#endif
//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.CommandListener;
//# import javax.microedition.lcdui.Command;
//#else
import menu.MenuListener;
import menu.Command;
import menu.MyMenu;
//#endif
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextField;
import locale.SR;
import ui.controls.form.CheckBox;
import ui.controls.form.DefForm;
import ui.controls.form.TextInput;
//#ifdef GRAPHICS_MENU
import ui.GMenu;
import ui.GMenuConfig;
//#endif
import ui.controls.form.NumberInput;
import ui.controls.form.SpacerItem;
import javax.microedition.lcdui.Image;
import images.ImageList;
import ui.controls.form.LinkString;
import java.util.Vector;
//#ifdef FILE_IO
import io.file.FileIO;
//#endif
import util.StringUtils;
import ui.controls.form.SimpleString;

public class ConfigAvatar extends DefForm
//#ifdef FILE_IO
        implements BrowserListener
//#endif
{

//#ifdef FILE_IO
    private Command cmdPath;
//#endif
    private Command cmdOkey;
    private NumberInput maxAvatarHeight;
    private NumberInput maxAvatarWidth;
    private CheckBox auto_queryPhoto;
    private CheckBox showAvatarRect;
//#ifdef FILE_IO
    private CheckBox autoload_FSPhoto;
    private CheckBox autoSaveVcard;
    private TextInput avatarFolder;
//#endif
    private Config config;
    public ConfigAvatar(Display display, Displayable pView) {
        super(display, pView, SR.get(SR.MS_AVATARS));

//#ifdef FILE_IO
        cmdPath = new Command(SR.get(SR.AVATAR_FOLDER), Command.SCREEN, 1);
        cmdPath.setImg(0x34);
//#endif
        cmdOkey = new Command(SR.get(SR.MS_OK), Command.SCREEN, 3);
        cmdOkey.setImg(0x43);

        config = Config.getInstance();

        if (config.userAppLevel == 1) {
            auto_queryPhoto = new CheckBox(SR.get(SR.MS_AUTOLOAD_VCARD), config.auto_queryPhoto);
            itemsList.addElement(auto_queryPhoto);

//#ifdef FILE_IO
            autoload_FSPhoto = new CheckBox(SR.get(SR.MS_AUTOLOAD_VCARD_FROMFS), config.autoload_FSPhoto);
            itemsList.addElement(autoload_FSPhoto);
//#endif
            showAvatarRect = new CheckBox(SR.get(SR.AVATAR_DRAW_RECT), config.showAvatarRect);
            itemsList.addElement(showAvatarRect);
            itemsList.addElement(new SpacerItem(5));
        }

        maxAvatarHeight = new NumberInput(display, SR.get(SR.MS_MAX_AVATAR_HEIGHT), Integer.toString(config.maxAvatarHeight), 12, 100);
        itemsList.addElement(maxAvatarHeight);

        itemsList.addElement(new SpacerItem(5));
        maxAvatarWidth = new NumberInput(display, SR.get(SR.MS_MAX_AVATAR_WIDTH), Integer.toString(config.maxAvatarWidth), 12, 100);
        itemsList.addElement(maxAvatarWidth);

//#ifdef FILE_IO
        if (config.userAppLevel == 1) {
            itemsList.addElement(new SpacerItem(10));
            autoSaveVcard = new CheckBox(SR.get(SR.AVATAR_AUTOSAVE_FS), config.autoSaveVcard);
            avatarFolder = new TextInput(display, SR.get(SR.AVATAR_FOLDER), config.msgAvatarPath, null, TextField.ANY);
            itemsList.addElement(autoSaveVcard);
            itemsList.addElement(avatarFolder);
        }
//#endif

//#ifdef FILE_IO
        if (config.userAppLevel == 1) {
            addControl(new LinkString(SR.get(SR.MS_UPDATE)) {

                public void doAction() {
                    config.maxAvatarHeight = Integer.parseInt(maxAvatarHeight.getValue());
                    config.maxAvatarWidth = Integer.parseInt(maxAvatarWidth.getValue());
                    long s1 = System.currentTimeMillis();

                    int loadingAvatars_roster = applyAvatars(true);
                    int loadingAvatars_muc = applyAvatars(false);
                    UpdateAvatarsOnline();

                    if (loadingAvatars_roster > -1 && loadingAvatars_muc > -1) {
                        long s2 = System.currentTimeMillis();
                        addControl(new SpacerItem(10));
                        addControl(new SimpleString("Update Success!", true));
                        addControl(new SimpleString("Time: " + Long.toString(s2 - s1) + " msec", true));
                        addControl(new SpacerItem(10));
                        repaint();
                    }
                }

            });
        }
//#endif

        addCommand(cmdOkey);

        commandState();
        moveCursorTo(0);

        attachDisplay(display);
        this.parentView = pView;
    }

//#ifdef FILE_IO
    public void BrowserFilePathNotify(String pathSelected) {
        avatarFolder.setValue(pathSelected);
    }
//#endif

    public void commandAction(Command command, Displayable displayable) {
//#ifdef FILE_IO
        if (command == cmdPath) {
            new Browser(null, display, this, this, true);
            return;
        }
//#endif
        if (command == cmdOkey) {
            cmdOk();
        }
        destroyView();
    }

    public void cmdOk() {
        if (config.userAppLevel == 1) {
            config.auto_queryPhoto = auto_queryPhoto.getValue();
//#ifdef FILE_IO
            config.autoload_FSPhoto = autoload_FSPhoto.getValue();
//#endif
            config.showAvatarRect = showAvatarRect.getValue();
        }

        int maxAvHeight = Integer.parseInt(maxAvatarHeight.getValue());
        int maxAvWidth = Integer.parseInt(maxAvatarWidth.getValue());

        if (maxAvHeight != config.maxAvatarHeight || maxAvWidth != config.maxAvatarWidth) {
            config.maxAvatarHeight = maxAvHeight;
            config.maxAvatarWidth = maxAvWidth;
        }
//#ifdef FILE_IO
        if (config.userAppLevel == 1) {
            config.autoSaveVcard = autoSaveVcard.getValue();
            config.msgAvatarPath = avatarFolder.getValue();
        }
//#endif

        destroyView();
    }

    public void commandState() {
//#ifdef MENU_LISTENER
        menuCommands.removeAllElements();
//#endif

//#ifndef GRAPHICS_MENU
//#     super.commandState();
//#endif
        removeCommand(cmdCancel);
//#ifdef FILE_IO
        if (config.userAppLevel == 1) {
            addCommand(cmdPath);
        }
//#endif
        addCommand(cmdOkey);
//#ifndef GRAPHICS_MENU
//#      addCommand(cmdCancel);
//#endif
    }

//#ifdef FILE_IO

    public int applyAvatars(boolean isRoster) {
        Contact c = null;
        int countLoadedImages = 0;
        try {
            FileIO f = FileIO.createConnection(config.msgAvatarPath);
            Vector e = f.fileList(false);
            int size1 = e.size() - 1;
            int size = midlet.BombusQD.sd.roster.contactList.contacts.size();

            StaticData.getInstance().roster.errorLog(e.toString());
            for (int i = 0; i < size1; i++) {
                for (int j = 0; j < size; j++) {
                    c = (Contact)midlet.BombusQD.sd.roster.contactList.contacts.elementAt(j);
                    if (isRoster) {
                        String checkBareJid = StringUtils.replaceBadChars("roster_" + c.bareJid);
                        String fsName = e.elementAt(i).toString();
                        if (fsName.startsWith("roster") && fsName.indexOf(checkBareJid) > -1) {
                            if (createContactImageFS(c, fsName)) {
                                countLoadedImages += 1;
                                checkBareJid = null;
                                fsName = null;
                                Thread.sleep(50);
                                break;
                            }
                            ;
                        }
                    } else {
                        String checkNick = StringUtils.replaceBadChars("muc_" + c.getNick());
                        int len = checkNick.length() - 1;
                        String fsName = e.elementAt(i).toString();
                        if (fsName.startsWith("muc") && fsName.indexOf(checkNick) > -1) {
                            if (createContactImageFS(c, fsName)) {
                                countLoadedImages += 1;
                                checkNick = null;
                                fsName = null;
                                Thread.sleep(50);
                                break;
                            }
                            ;
                        }
                    }
                }
            }
            return countLoadedImages;
        } catch (Exception e) {
        }
        return -1;
    }

    private boolean createContactImageFS(Contact c, String name) {
        Image photoImg = null;
        byte[] b;
        int len = 0;

        try {
            FileIO f = FileIO.createConnection(config.msgAvatarPath + name);
            b = f.fileRead();

            len = b.length;
            if (c.hasPhoto == false) {
                try {
                    photoImg = Image.createImage(b, 0, len);
                    int newW = photoImg.getWidth();
                    int newH = photoImg.getHeight();
                    while (newW > config.maxAvatarWidth || newH > config.maxAvatarHeight) {
                        newW -= (newW * 10) / 100;
                        newH -= (newH * 10) / 100;
                    }
                    c.img_vcard = resizeImage(photoImg, newW, newH);
                    c.avatar_width = newW;
                    c.avatar_height = newH;
                } catch (OutOfMemoryError eom) {
                    StaticData.getInstance().roster.errorLog("createContactImage: OutOfMemoryError " + c.getJid());
                } catch (Exception e) {
                    StaticData.getInstance().roster.errorLog("createContactImage: Exception " + c.getJid());
                }
            }
        } catch (Exception e) {
        }
        return true;
    }

    public void UpdateAvatarsOnline() {
        Image photoImg = null;
        int size = midlet.BombusQD.sd.roster.contactList.contacts.size();
        ImageList il = new ImageList();
        Contact c = null;
        int countAv = 0;
        for (int i = 0; i < size; i++) {
            c = (Contact)midlet.BombusQD.sd.roster.contactList.contacts.elementAt(i);
            if (c.hasPhoto) {
                try {
                    photoImg = Image.createImage(c.vcard.getPhoto(), 0, c.vcard.getPhoto().length);
                    int newW = photoImg.getWidth();
                    int newH = photoImg.getHeight();
                    while (newW > config.maxAvatarWidth || newH > config.maxAvatarHeight) {
                        newW -= (newW * 10) / 100;
                        newH -= (newH * 10) / 100;
                    }
                    c.img_vcard = resizeImage(photoImg, newW, newH);
                    c.avatar_width = newW;
                    c.avatar_height = newH;
                    countAv += 1;
                } catch (OutOfMemoryError eom) {
                    //StaticData.getInstance().roster.errorLog("UpdateAvatars_menu: OutOfMemoryError "+c.getJid());
                } catch (Exception e) {
                    //StaticData.getInstance().roster.errorLog("UpdateAvatars_menu: Exception load vcard "+c.getJid());
                }
            }
        }
    }
//#endif

//#ifdef MENU_LISTENER
    public String touchLeftCommand() {
        return SR.get(SR.MS_MENU);
    }

//#ifdef GRAPHICS_MENU
    public void touchLeftPressed() {
        showGraphicsMenu();
    }

    public int showGraphicsMenu() {
        commandState();
        menuItem = new GMenu(display, parentView, this, null, menuCommands);
        GMenuConfig.getInstance().itemGrMenu = GMenu.HISTORY_CONFIG;
        redraw();
        return GMenu.HISTORY_CONFIG;
    }

//#else
//#     public void touchLeftPressed(){
//#         showMenu();
//#     }
//# 
//#     public void showMenu() {
//#         commandState();
//#         new MyMenu(display, parentView, this, SR.get(SR.MS_HISTORY_OPTIONS), null, menuCommands);
//#    }
//#endif
//#endif
}
