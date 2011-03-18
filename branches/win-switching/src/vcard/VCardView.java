/*
 * VCardView.java
 *
 * Created on 25.05.2008, 21:27
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
package vcard;

import client.Config;
import client.Contact;
//#if FILE_IO
import images.MenuIcons;
import io.file.FileIO;
import io.file.browse.Browser;
import io.file.browse.BrowserListener;
import util.StringUtils;
import ui.Time;
//#endif
//#ifdef DETRANSLIT
//# import util.DeTranslit;
//#endif
import javax.microedition.lcdui.Displayable;
//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.CommandListener;
//# import javax.microedition.lcdui.Command;
//#else
import menu.Command;
//#endif
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Image;
import locale.SR;
import ui.controls.form.DefForm;
import ui.controls.form.ImageItem;
import ui.controls.form.MultiLine;
import ui.controls.form.SimpleString;
import ui.controls.form.LinkString;
//#ifdef CLIPBOARD
import util.ClipBoard;
//#endif
import midlet.BombusQD;
import javax.microedition.io.ConnectionNotFoundException;
//#ifdef GRAPHICS_MENU
import midlet.Commands;
import ui.GMenu;
import ui.GMenuConfig;
import ui.MainBar;
//#endif

/**
 *
 * @author ad,aqent
 */
public class VCardView extends DefForm
//#if FILE_IO
        implements BrowserListener
//#endif
{
    private VCard vcard;
    private ImageItem photoItem;

    private SimpleString endVCard = new SimpleString(SR.get(SR.MS_END_OF_VCARD), false);
    private SimpleString noVCard = new SimpleString(SR.get(SR.MS_NO_VCARD), true);
    private SimpleString noPhoto = new SimpleString(SR.get(SR.MS_NO_PHOTO), false);
    private SimpleString badFormat = new SimpleString(SR.get(SR.MS_UNSUPPORTED_FORMAT), false);
    private SimpleString photoTooLarge = new SimpleString(SR.get(SR.MS_PHOTO_TOO_LARGE), false);

    private String url;

    private Command cmdRefresh;
//#if FILE_IO
    private Command cmdSavePhoto;
//#endif
    private Command cmdDelPhoto;
    private Command cmdDelVcard;

    private Contact contact;

    public VCardView(VCard vcard) {
        this(null, vcard);
    }

    public VCardView(Contact contact) {
        this(contact, contact.vcard);
    }

    public VCardView(Contact contact, final VCard vcard) {
        super(null);

        cmdRefresh = new Command(SR.get(SR.MS_REFRESH), Command.SCREEN, 3);
        cmdRefresh.setImg(0x10);
//#if FILE_IO
        cmdSavePhoto = new Command(SR.get(SR.MS_SAVE_PHOTO), Command.SCREEN, 4);
        cmdSavePhoto.setImg(MenuIcons.ICON_SAVE_PHOTO);
//#endif
        cmdDelPhoto = new Command(SR.get(SR.MS_CLEAR_PHOTO), Command.SCREEN, 5);
        cmdDelPhoto.setImg(0x41);

        cmdDelVcard = new Command(SR.get(SR.MS_DELETE_VCARD), Command.SCREEN, 6);
        cmdDelVcard.setImg(0x41);

        this.contact = contact;
        this.vcard = vcard;

        if (contact != null) {
            setMainBarItem(new MainBar(contact.getNickJid()));
        } else {
            setMainBarItem(new MainBar(vcard.getNickName()));
        }

        if (vcard.isEmpty()) {
            addControl(noVCard);
        } else {
            setPhoto();

            int count = vcard.getCount();

            for (int index = 0; index < count; ++index) {
                String data = vcard.getVCardData(index);
                String name = (String)VCard.vCardLabels.elementAt(index);

                if (data != null && name != null) {
                    if (!VCard.vCardFields.elementAt(index).equals("URL")) {
                        MultiLine item = new MultiLine(name, data, super.superWidth);
                        item.setSelectable(true);
                        addControl(item);
                    } else {
                        url = data;
                        LinkString nData = new LinkString(url) {
                            public void doAction() {
                                try {
                                    BombusQD.getInstance().platformRequest(url);
                                } catch (ConnectionNotFoundException ex) {
                                    //ex.printStackTrace();
                                }
                            }
                        };
                        addControl(nData);
                    }
                }
            }
        }

        addControl(endVCard);
        addControl(new LinkString(SR.get(SR.MS_REFRESH)) {
                public void doAction() {
                    refreshVCard();
                }
        });
    }

    private void setPhoto() {
        if (contact != null) {
            contact.hasPhoto = vcard.hasPhoto;
        }

        try {
            removeControl(noPhoto);
            removeControl(badFormat);
            removeControl(photoItem);
            removeControl(photoTooLarge);
        } catch (Exception e) {
        }

        if (vcard.hasPhoto) {
            try {
                int length = vcard.getPhoto().length;
                if (length == 1) {
                    vcard.setPhoto(null);
                    addControl(photoTooLarge);
                } else {
                    Image photoImg = Image.createImage(vcard.getPhoto(), 0, length);
                    photoItem = new ImageItem(photoImg, "minimized, size: " + String.valueOf(length) + "b.");
                    if (length > 10240) {
                        photoItem.collapsed = true;
                    }
                    insertControl(photoItem, 0);
                    if (contact != null) {
                        if (Config.module_avatars) {
                            //int width = photoImg.getWidth();
                            //int height = photoImg.getHeight();
                            midlet.BombusQD.sd.roster.setImageAvatar(contact, photoImg);
                        }
                    }
                    //photoImg = null;
                }
            } catch (Exception e) {
                addControl(badFormat);
            }
        } else {
            addControl(noPhoto);
        }
    }

    public void commandAction(Command c, Displayable d) {
        if (c == cmdDelVcard) {
            this.contact.clearVCard();
        } else if (c == cmdDelPhoto) {
            vcard.dropPhoto();
            this.contact.img_vcard = null;
            setPhoto();
        } else if (c == cmdRefresh) {
            refreshVCard();
//#if FILE_IO
        } else if (c == cmdSavePhoto) {
            new Browser(null, this, true).show();
//#endif
//#ifdef CLIPBOARD
        } else if (c == Commands.cmdCopy) {
            String lineValue = ((MultiLine)getFocusedObject()).getValue();

            if (lineValue != null) {
                ClipBoard.setClipBoard(lineValue);
            }
        } else if (c == Commands.cmdCopyPlus) {
            String lineValue = ((MultiLine)getFocusedObject()).getValue();

            if (lineValue != null) {
                ClipBoard.addToClipBoard(lineValue);
            }
//#endif
        }

        //super.commandAction(c, d);
    }

    private void refreshVCard() {
        VCard.request(vcard.getJid(), vcard.getId().substring(5));
        destroyView();
    }

//#if FILE_IO
    public void BrowserFilePathNotify(String pathSelected) {
        if (vcard.hasPhoto) {
            //System.out.println(photoType+"->"+getFileType(photoType));
            String filename = StringUtils.replaceBadChars(getNickDate());
            FileIO file = FileIO.createConnection(pathSelected + filename + vcard.getFileType());
            file.fileWrite(vcard.getPhoto());
        }
    }

    public String getNickDate() {
        StringBuffer nickDate = new StringBuffer();
        nickDate.append("photo_");
//#ifdef DETRANSLIT
//#         String userName=(vcard.getNickName()!=null)?vcard.getNickName():vcard.getJid();
//#         if (Config.getInstance().transliterateFilenames) {
//#             nickDate.append(DeTranslit.translit(userName));
//#         } else {
//#             nickDate.append(userName);
//#         }
//#else
        if (vcard.getNickName() != null) {
            nickDate.append(vcard.getNickName());
        } else {
            nickDate.append(vcard.getJid());
        }
//#endif
        nickDate.append('_').append(Time.dayLocalString(Time.utcTimeMillis()).trim());
        return nickDate.toString();
    }
//#endif

    public void commandState() {
//#ifdef MENU_LISTENER
        menuCommands.removeAllElements();
//#endif

//#ifdef GRAPHICS_MENU
        //super.commandState();
//#else
//#     super.commandState();
//#endif
//        removeCommand(Commands.cmdOk);
//        removeCommand(cmdCancel);

        if (vcard != null) {
            if (vcard.hasPhoto) {
//#if FILE_IO
                addCommand(cmdSavePhoto);
//#endif
                if (contact != null) {
                    addCommand(cmdDelPhoto);
                }
            }
            if (contact != null) {
                addCommand(cmdDelVcard);
            }
//#ifdef CLIPBOARD
            if (Config.getInstance().useClipBoard) {
                addCommand(Commands.cmdCopy);
                if (!ClipBoard.isEmpty()) {
                    addCommand(Commands.cmdCopyPlus);
                }
            }
//#endif
        }
        addCommand(cmdRefresh);
//#ifndef GRAPHICS_MENU
//#      addCommand(cmdCancel);
//#endif
    }

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
        GMenuConfig.getInstance().itemGrMenu = GMenu.VCARD_VIEW;
        redraw();
        return GMenu.VCARD_VIEW;
    }
//#else
//#     public void touchLeftPressed(){
//#         showMenu();
//#     }
//#
//#     public void showMenu() {
//#         commandState();
//#         new MyMenu(display, parentView, this, SR.get(SR.MS_VCARD), null, menuCommands);
//#    }
//#endif

//#endif
}
