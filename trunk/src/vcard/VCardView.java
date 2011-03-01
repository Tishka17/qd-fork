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
import io.file.FileIO;
import io.file.browse.Browser;
import io.file.browse.BrowserListener;
import util.StringUtils;
import ui.Time;
//#endif
//#ifdef CLIPBOARD
//# import util.ClipBoard;
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
//# import util.ClipBoard;
//#endif
import midlet.BombusQD;
import javax.microedition.io.ConnectionNotFoundException;
import ui.ImageList;
//#ifdef GRAPHICS_MENU
//# import ui.GMenu;
//# import ui.GMenuConfig;
//#endif
/**
 *
 * @author ad,aqent
 */
public class VCardView
    extends DefForm
//#if FILE_IO
        implements BrowserListener
//#endif
    {

    private VCard vcard;
    private ImageItem photoItem;

    private SimpleString endVCard=new SimpleString(SR.get(SR.MS_END_OF_VCARD), false);
    private SimpleString noVCard=new SimpleString(SR.get(SR.MS_NO_VCARD), true);
    private SimpleString noPhoto=new SimpleString(SR.get(SR.MS_NO_PHOTO), false);
    private SimpleString badFormat=new SimpleString(SR.get(SR.MS_UNSUPPORTED_FORMAT), false);
    private SimpleString photoTooLarge=new SimpleString(SR.get(SR.MS_PHOTO_TOO_LARGE), false);


    private LinkString refresh;

    private String url="";

//#ifdef CLIPBOARD
//#     Command cmdCopy;
//#     Command cmdCopyPlus;
//#endif
    Command cmdRefresh;
//#if FILE_IO
    Command cmdSavePhoto;
//#endif
    Command cmdDelPhoto;
    Command cmdDelVcard;

    /** Creates a new instance of VCardView */
    private Contact c;

    public VCardView(Display display, Displayable pView, Contact contact) {
        super(display, pView, contact.getNickJid());
        this.display=display;

//#ifdef CLIPBOARD
//#         cmdCopy      = new Command(SR.get(SR.MS_COPY), Command.SCREEN, 1);
//#         cmdCopyPlus  = new Command("+ "+SR.get(SR.MS_COPY), Command.SCREEN, 2);
//#endif
        cmdRefresh   = new Command(SR.get(SR.MS_REFRESH), Command.SCREEN, 3);
//#if FILE_IO
        cmdSavePhoto = new Command(SR.get(SR.MS_SAVE_PHOTO), Command.SCREEN,4);
//#endif
        cmdDelPhoto  = new Command(SR.get(SR.MS_CLEAR_PHOTO), Command.SCREEN,5);
        cmdDelVcard  = new Command(SR.get(SR.MS_DELETE_VCARD), Command.SCREEN,6);

        this.vcard=contact.vcard;
        this.c=contact;

        refresh=new LinkString(SR.get(SR.MS_REFRESH)) { public void doAction() { VCard.request(vcard.getJid(), vcard.getId().substring(5)); destroyView(); } };

        if (vcard.isEmpty()) {
            itemsList.addElement(noVCard);
            itemsList.addElement(refresh);
        } else {
            setPhoto();
            int count = vcard.getCount();
            String data="";
            String name="";
            for (int index=0; index<count; index++) {
                data = vcard.getVCardData(index);
                name=(String)VCard.vCardLabels.elementAt(index);
                if (data!=null && name!=null) {
                    if (!VCard.vCardFields.elementAt(index).equals("URL")) {
                        MultiLine nData=new MultiLine(name, data, super.superWidth);
                        nData.selectable=true;
                        itemsList.addElement(nData);
                        nData=null;
                    } else {
                        url=data;
                        LinkString nData=new LinkString(url) { public void doAction() {
                                try {BombusQD.getInstance().platformRequest(url);
                                } catch (ConnectionNotFoundException ex) {
                                    ex.printStackTrace();
                                } } };
                        itemsList.addElement(nData);
                        nData=null;
                    }
                }
            }
            itemsList.addElement(endVCard);
            itemsList.addElement(refresh);
        }

        commandState();

        attachDisplay(display);
        this.parentView=pView;
    }

    public void destroyView(){
        endVCard = null;
        noVCard = null;
        noPhoto = null;
        badFormat = null;
        photoTooLarge = null;
        photoItem = null;
        super.destroyView();
    }

     private void setPhoto() {
        c.hasPhoto = vcard.hasPhoto;
        try {
            itemsList.removeElement(noPhoto);
            itemsList.removeElement(badFormat);
            itemsList.removeElement(photoItem);
            itemsList.removeElement(photoTooLarge);
        } catch (Exception e) { }

         if (vcard.hasPhoto) {
            try {
                int length=vcard.getPhoto().length;
                if (length==1) {
                    vcard.setPhoto(null);
                    itemsList.addElement(photoTooLarge);
                } else {
                    Image photoImg=Image.createImage(vcard.getPhoto(), 0, length);
                    photoItem=new ImageItem(photoImg, "minimized, size: "+String.valueOf(length)+"b.");
                    if (length>10240)
                        photoItem.collapsed=true;
                    itemsList.insertElementAt(photoItem, 0);
                    if(Config.getInstance().module_avatars) {
                        int width = photoImg.getWidth();
                        int height = photoImg.getHeight();
                        midlet.BombusQD.sd.roster.setImageAvatar(c,photoImg);
                    }
                    photoImg = null;
                }
            } catch (Exception e) {
                itemsList.addElement(badFormat);
            }
        } else {
            itemsList.addElement(noPhoto);
        }
     }


    public void commandAction(Command c, Displayable d) {
        if (c==cmdDelVcard){
            this.c.clearVCard();
        }
        if (c==cmdDelPhoto) {
            vcard.dropPhoto();
            this.c.img_vcard=null;
            setPhoto();
        }
        if (c==cmdRefresh) {
            VCard.request(vcard.getJid(), vcard.getId().substring(5));
            destroyView();
            return;
        }
//#if FILE_IO
        if (c==cmdSavePhoto) {
            new Browser(null, display, this, this, true);
        }
//#endif
//#ifdef CLIPBOARD
//#         if (c == cmdCopy) {
//#             String lineValue = ((MultiLine)getFocusedObject()).getValue();
//# 
//#             if (lineValue != null) {
//#                  ClipBoard.setClipBoard(lineValue);
//#             }
//#         } else if (c == cmdCopyPlus) {
//#             String lineValue = ((MultiLine)getFocusedObject()).getValue();
//# 
//#             if (lineValue != null) {
//#                  ClipBoard.addToClipBoard(lineValue);
//#             }
//#         }
//#endif
        super.commandAction(c, d);
    }

//#if FILE_IO
    public void BrowserFilePathNotify(String pathSelected) {
        if (vcard.hasPhoto) {
            //System.out.println(photoType+"->"+getFileType(photoType));
            String filename = StringUtils.replaceBadChars(getNickDate());
            FileIO file=FileIO.createConnection(pathSelected+filename+vcard.getFileType());
            file.fileWrite(vcard.getPhoto());
        }
    }

    public String getNickDate() {
        StringBuffer nickDate=new StringBuffer();
        nickDate.append("photo_");
//#ifdef DETRANSLIT
//#         String userName=(vcard.getNickName()!=null)?vcard.getNickName():vcard.getJid();
//#         if (Config.getInstance().transliterateFilenames) {
//#             nickDate.append(DeTranslit.translit(userName));
//#         } else {
//#             nickDate.append(userName);
//#         }
//#else
         if (vcard.getNickName()!=null) {
             nickDate.append(vcard.getNickName());
         } else nickDate.append(vcard.getJid());
//#endif
        nickDate.append('_').append(Time.dayLocalString(Time.utcTimeMillis()).trim());
        return nickDate.toString();
    }
//#endif




    public void commandState(){
//#ifdef MENU_LISTENER
        menuCommands.removeAllElements();
//#endif

//#ifdef GRAPHICS_MENU
//#         //super.commandState();
//#else
    super.commandState();
//#endif
        removeCommand(midlet.BombusQD.commands.cmdOk);
        removeCommand(cmdCancel);

        if (vcard!=null) {
            if (vcard.hasPhoto) {
//#if FILE_IO
                addCommand(cmdSavePhoto); cmdSavePhoto.setImg(0x15);
//#endif
                addCommand(cmdDelPhoto); cmdDelPhoto.setImg(0x41);
            }
            addCommand(cmdDelVcard);
//#ifdef CLIPBOARD
//#             if (Config.getInstance().useClipBoard) {
//#                 addCommand(cmdCopy); cmdCopy.setImg(0x13);
//#                 if (!ClipBoard.isEmpty())
//#                     addCommand(cmdCopyPlus); cmdCopyPlus.setImg(0x23);
//#             }
//#endif
        }
        addCommand(cmdRefresh); cmdRefresh.setImg(0x10);
//#ifndef GRAPHICS_MENU
     addCommand(cmdCancel);
//#endif
    }


//#ifdef MENU_LISTENER
    public String touchLeftCommand(){ return SR.get(SR.MS_MENU); }

//#ifdef GRAPHICS_MENU
//#     public void touchLeftPressed(){
//#         showGraphicsMenu();
//#     }
//#     public int showGraphicsMenu() {
//#         commandState();
//#         menuItem = new GMenu(display, parentView, this, null, menuCommands);
//#         GMenuConfig.getInstance().itemGrMenu = GMenu.VCARD_VIEW;
//#         redraw();
//#         return GMenu.VCARD_VIEW;
//#     }
//#else
    public void touchLeftPressed(){
        showMenu();
    }

    public void showMenu() {
        commandState();
        new MyMenu(display, parentView, this, SR.get(SR.MS_VCARD), null, menuCommands);
   }
//#endif


//#endif


}

