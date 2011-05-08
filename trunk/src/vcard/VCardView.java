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
import util.Time;
//#endif
//#ifdef DETRANSLIT
//# import util.DeTranslit;
//#endif
//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.CommandListener;
//# import javax.microedition.lcdui.Command;
//#else
import menu.Command;
//#endif
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
import menu.MenuListener;
import midlet.Commands;
import ui.GMenu;
import ui.GMenuConfig;
import ui.MainBar;
import ui.VirtualElement;
//#endif

/**
 *
 * @author ad,aqent
 */
public class VCardView extends DefForm implements MenuListener
//#if FILE_IO
        , BrowserListener
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

        cmdRefresh = new Command(SR.get(SR.MS_REFRESH), 0x10);
//#if FILE_IO
        cmdSavePhoto = new Command(SR.get(SR.MS_SAVE_PHOTO), MenuIcons.ICON_SAVE);
//#endif
        cmdDelPhoto = new Command(SR.get(SR.MS_CLEAR_PHOTO), 0x41);
        cmdDelVcard = new Command(SR.get(SR.MS_DELETE_VCARD), 0x41);

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
                        addControl(new MultiLine(name, data));
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
//#ifdef AVATARS
        if (contact != null) {
            contact.hasPhoto = vcard.hasPhoto;
        }
//#endif

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
//#ifdef AVATARS
                    if (contact != null) {
                        if (Config.module_avatars) {
                            contact.setImageAvatar(photoImg);
                        }
                    }
//#endif
                    //photoImg = null;
                }
            } catch (Exception e) {
                addControl(badFormat);
            }
        } else {
            addControl(noPhoto);
        }
    }

    public void commandAction(Command c) {
        if (c == cmdDelVcard) {
            this.contact.clearVCard();
        } else if (c == cmdDelPhoto) {
            vcard.dropPhoto();
//#ifdef AVATARS
            this.contact.img_vcard = null;
//#endif
            setPhoto();
        } else if (c == cmdRefresh) {
            refreshVCard();
//#if FILE_IO
        } else if (c == cmdSavePhoto) {
            new Browser(null, this, true).show();
//#endif
//#ifdef CLIPBOARD
        } else if (c == Commands.cmdCopy) {
            /*String lineValue = ((MultiLine)getFocusedObject()).getValue();

            if (lineValue != null) {
                ClipBoard.setClipBoard(lineValue);
            }*/
            String str = ((VirtualElement)getFocusedObject()).toString();
            if (str != null) {
                ClipBoard.setClipBoard(str);
            }
        } else if (c == Commands.cmdCopyPlus) {
            /*String lineValue = ((MultiLine)getFocusedObject()).getValue();

            if (lineValue != null) {
                ClipBoard.addToClipBoard(lineValue);
            }*/
            String str = ((VirtualElement)getFocusedObject()).toString();
            if (str != null) {
                ClipBoard.addToClipBoard(str);
            }
//#endif
        }
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
//#         if (Config.getInstance().autoDeTranslit) {
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
        menuItem = new GMenu(this, menuCommands);
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
