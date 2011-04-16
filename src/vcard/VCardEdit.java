/*
 * VCardEdit.java
 *
 * Created on 30 Май 2008 г., 9:25
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package vcard;

import client.StaticData;
//#if (FILE_IO)
//#ifdef DETRANSLIT
//# import util.DeTranslit;
//# import client.Config;
//#endif

import io.file.FileIO;
import io.file.browse.Browser;
import io.file.browse.BrowserListener;
//#endif
import images.MenuIcons;
import images.camera.*;

//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.CommandListener;
//# import javax.microedition.lcdui.Command;
//#else
import menu.Command;
//#endif
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.TextField;
import locale.SR;
import menu.MenuListener;
import midlet.BombusQD;

import util.Time;
import util.StringUtils;

import ui.controls.form.ImageItem;
import ui.controls.form.DefForm;
import ui.controls.form.SimpleString;
import ui.controls.form.TextInput;
import ui.controls.form.LinkString;

//#ifdef GRAPHICS_MENU
import ui.GMenu;
import ui.GMenuConfig;
//#endif

/**
 *
 * @author ad
 */

public class VCardEdit extends DefForm implements MenuListener, Runnable
//#if (FILE_IO)
        , BrowserListener
//#endif
        , CameraImageListener
{

    Command cmdPublish;
    Command cmdRefresh;
//#if FILE_IO
    Command cmdLoadPhoto;
    Command cmdSavePhoto;
//#endif
    Command cmdDelPhoto;
    Command cmdCamera;

    private VCard vcard;

    private ImageItem photoItem;

    private int st=-1;

    private SimpleString noPhoto=new SimpleString(SR.get(SR.MS_NO_PHOTO), false);
    private SimpleString badFormat=new SimpleString(SR.get(SR.MS_UNSUPPORTED_FORMAT), false);
    private SimpleString photoTooLarge=new SimpleString(SR.get(SR.MS_PHOTO_TOO_LARGE), false);

    private LinkString publish;

    public VCardEdit(VCard vcard) {
        super(SR.get(SR.MS_VCARD) + " " + vcard.getNickName());

        cmdPublish = new Command(SR.get(SR.MS_PUBLISH), Command.OK, 1);
        cmdPublish.setImg(0x50);

        cmdRefresh = new Command(SR.get(SR.MS_REFRESH), Command.SCREEN, 2);
        cmdRefresh.setImg(0x73);

//#if FILE_IO
        cmdLoadPhoto=new Command(SR.get(SR.MS_LOAD_PHOTO), Command.SCREEN, 3);
        cmdLoadPhoto.setImg(MenuIcons.ICON_LOAD_PHOTO);

        cmdSavePhoto=new Command(SR.get(SR.MS_SAVE_PHOTO), Command.SCREEN, 4);
        cmdSavePhoto.setImg(MenuIcons.ICON_SAVE_PHOTO);
//#endif

        cmdDelPhoto = new Command(SR.get(SR.MS_CLEAR_PHOTO), Command.SCREEN, 5);
        cmdDelPhoto.setImg(0x76);

        cmdCamera = new Command(SR.get(SR.MS_CAMERA), Command.SCREEN, 6);
        cmdCamera.setImg(MenuIcons.ICON_CAMERA);

        this.vcard=vcard;

        for (int index=0; index<vcard.getCount(); index++) {
            String data=vcard.getVCardData(index);
            String name=(String)VCard.vCardLabels.elementAt(index);
            //truncating large string
            if (data!=null) {
                if (data.length()>500)
                    data=data.substring(0, 494)+"<...>";
            }
            itemsList.addElement(new TextInput(name, data, null, TextField.ANY));
        }

        publish=new LinkString(SR.get(SR.MS_PUBLISH)) { public void doAction() { publish(); } };

        setPhoto();
    }

    public void publish() {
        for (int index=0; index<vcard.getCount(); index++) {
            try {
                String field=((TextInput)itemsList.elementAt(index)).getValue();
                if (field.length()==0) field=null;
                vcard.setVCardData(index, field);
             } catch (Exception ex) { }
        }
        new Thread(this).start();
        destroyView();
    }


    public void commandAction(Command c, Displayable d) {
        if (c==cmdRefresh) {
            VCard.request(vcard.getJid(), vcard.getId().substring(5));
            destroyView();
        }

//#if FILE_IO
        if (c==cmdLoadPhoto) {
            st=1;
            new Browser(null, this, false).show();
        }
        if (c==cmdSavePhoto) {
            st=2;
            new Browser(null, this, true).show();
        }
//#endif

        if (c==cmdCamera)
            new CameraImage(this);

        if (c==cmdDelPhoto) {
            vcard.dropPhoto();
            setPhoto();
        }
        if (c==cmdPublish)
            publish();
    }


    public void run() {
        StaticData.getInstance().roster.theStream.send(vcard.constructVCard());
    }

//#if (FILE_IO)
    public void BrowserFilePathNotify(String pathSelected) {
        if (st>0) {
            if (st==1) {
                try {
                    FileIO f=FileIO.createConnection(pathSelected);
                    vcard.photo=f.fileRead();
                    vcard.setPhotoType();
                    setPhoto();
                    redraw();
                } catch (Exception e) {
//#ifdef DEBUG
//#                     System.out.println("error on load");
//#endif
                }
            }
            if (st==2 & vcard.hasPhoto) {
                String filename = StringUtils.replaceBadChars(getNickDate());
                FileIO file=FileIO.createConnection(pathSelected+filename+vcard.getFileType());
                file.fileWrite(vcard.getPhoto());
            }
        }
    }

    private String getNickDate() {
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
        nickDate.append("_").append(Time.dayLocalString(Time.utcTimeMillis()).trim());
        return nickDate.toString();
    }
//#endif

    public void cameraImageNotify(byte[] capturedPhoto) {
        vcard.setPhoto(capturedPhoto);
        setPhoto();
    }

     private void setPhoto() {
        try {
            itemsList.removeElement(noPhoto);
            itemsList.removeElement(badFormat);
            itemsList.removeElement(photoItem);
            itemsList.removeElement(photoTooLarge);

            itemsList.removeElement(publish);
        } catch (Exception e) { }

         if (vcard.hasPhoto) {
            if (vcard.getPhoto().length==1) {
                vcard.setPhoto(null);
                itemsList.addElement(photoTooLarge);
            } else {
                try {
                    Image photoImg=Image.createImage(vcard.getPhoto(), 0,vcard.getPhoto().length);
                    photoItem=new ImageItem(photoImg, String.valueOf(vcard.getPhoto().length)+" bytes");
                    itemsList.addElement(photoItem);
                } catch (Exception e) {
                    itemsList.addElement(badFormat);
                }
            }
        } else {
            itemsList.addElement(noPhoto);
        }

        itemsList.addElement(publish);
    }

    public void commandState(){
//#ifdef MENU_LISTENER
        menuCommands.removeAllElements();
//#endif

//#ifdef GRAPHICS_MENU
        //super.commandState();
//#else
//#     super.commandState();
//#endif

        addCommand(cmdPublish); 
        addCommand(cmdRefresh);
//#if FILE_IO
        addCommand(cmdLoadPhoto);
        addCommand(cmdSavePhoto);
//#endif
        String cameraAvailable = System.getProperty("supports.video.capture");
        if (cameraAvailable != null) {
            if (cameraAvailable.startsWith("true")) {
                addCommand(cmdCamera); 
            }
        }
        addCommand(cmdDelPhoto);
//#ifndef GRAPHICS_MENU
//#      addCommand(cmdCancel);
//#endif
    }

//#ifdef MENU_LISTENER
    public String touchLeftCommand(){ return SR.get(SR.MS_MENU); }

 //#ifdef GRAPHICS_MENU
    public void touchLeftPressed(){
        showGraphicsMenu();
    }
    public int showGraphicsMenu() {
        commandState();
        menuItem = new GMenu(this, null, menuCommands);
        GMenuConfig.getInstance().itemGrMenu = GMenu.VCARD_EDIT;
        redraw();
        return GMenu.VCARD_EDIT;
    }
//#else
//#     public void touchLeftPressed(){
//#         showMenu();
//#     }
//#     public void showMenu() {
//#         commandState();
//#         new MyMenu(display, parentView, this, SR.get(SR.MS_PUBLISH), null, menuCommands);
//#    }
//#endif
//#endif
}
