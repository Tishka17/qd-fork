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
//#ifdef DETRANSLIT
//# import util.DeTranslit;
//# import client.Config;
//#endif
//#if (FILE_IO)
import io.file.FileIO;
import io.file.browse.Browser;
import io.file.browse.BrowserListener;
//#endif
import images.MenuIcons;
import images.camera.*;
import menu.Command;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.TextField;
import locale.SR;
import menu.MenuListener;
import util.Time;
import util.StringUtils;
import ui.controls.form.ImageItem;
import ui.controls.form.DefForm;
import ui.controls.form.SimpleString;
import ui.controls.form.TextInput;
import ui.controls.form.LinkString;
import ui.GMenu;
import ui.GMenuConfig;

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

        cmdPublish = new Command(SR.get(SR.MS_PUBLISH), 0x50);
        cmdRefresh = new Command(SR.get(SR.MS_REFRESH), 0x73);

//#if FILE_IO
        cmdLoadPhoto=new Command(SR.get(SR.MS_LOAD_PHOTO), MenuIcons.ICON_LOAD);
        cmdSavePhoto=new Command(SR.get(SR.MS_SAVE_PHOTO), MenuIcons.ICON_SAVE);
//#endif

        cmdDelPhoto = new Command(SR.get(SR.MS_CLEAR_PHOTO), 0x76);
        cmdCamera = new Command(SR.get(SR.MS_CAMERA), MenuIcons.ICON_CAMERA);

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


    public void commandAction(Command c) {
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
//#         if (Config.getInstance().autoDeTranslit) {
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
                    photoItem=new ImageItem(photoImg, vcard.getPhoto().length+" bytes");
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
        menuCommands.removeAllElements();

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
    }

    public String touchLeftCommand(){ return SR.get(SR.MS_MENU); }

    public void touchLeftPressed(){
        showGraphicsMenu();
    }
    public int showGraphicsMenu() {
        commandState();
        menuItem = new GMenu(this, menuCommands);
        GMenuConfig.getInstance().itemGrMenu = GMenu.VCARD_EDIT;
        redraw();
        return GMenu.VCARD_EDIT;
    }
}
