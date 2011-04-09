/*
 * AlertCustomizeForm.java
 *
 * Created on 26.05.2008, 13:12
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
 *
 */

package alert;

import midlet.Commands;
import xmpp.EntityCaps;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
import java.util.Vector;
import ui.EventNotify;
import ui.controls.form.SimpleString;
import ui.controls.form.CheckBox;
import ui.controls.form.DefForm;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.SpacerItem;
import ui.controls.form.TrackItem;
import ui.controls.form.NumberInput;
import util.StringLoader;
import java.util.Enumeration;
//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.CommandListener;
//# import javax.microedition.lcdui.Command;
//#else
//#endif
//#ifdef GRAPHICS_MENU
import menu.Command;
import ui.GMenu;
import ui.GMenuConfig;
//#endif
//#ifdef LIGHT_CONTROL
import light.*;
import ui.controls.form.LinkString;
//#endif

public class AlertCustomizeForm extends DefForm {
    private CheckBox statusBox;
    private CheckBox blinkBox;

    private DropChoiceBox MessageFile;
    private DropChoiceBox OnlineFile;
    private DropChoiceBox OfflineFile;
    private DropChoiceBox ForYouFile;
    private DropChoiceBox ComposingFile;
    private DropChoiceBox ConferenceFile;
    private DropChoiceBox StartUpFile;
    private DropChoiceBox OutgoingFile;
    private DropChoiceBox VIPFile;

    private CheckBox vibrateOnlyHighlited;
    private CheckBox IQNotify;

    private TrackItem sndVol;
    private NumberInput vibraLen;

    private NumberInput vibraRepeatCount;
    private NumberInput vibraRepeatPause;

    private DropChoiceBox AttentionFile;
    private CheckBox enableAttention;
    //#ifdef LIGHT_CONTROL
    LinkString linkLight;
    //#endif
    AlertCustomize ac;
    Vector files[];
    Vector fileNames;
    boolean isVibroProfile = false;

    private Command cmdSave;
    private Command cmdTest;
    private Command cmdTestVibro;

    public AlertCustomizeForm(Display display, Displayable pView) {
        super(display, pView, SR.get(SR.MS_NOTICES_OPTIONS));
        this.display=display;

        cmdSave=new Command(SR.get(SR.MS_SAVE), Command.OK, 1);
        cmdSave.setImg(0x44);

        cmdTest=new Command(SR.get(SR.MS_TEST_SOUND), Command.SCREEN, 2);
        cmdTest.setImg(0x54);

        cmdTestVibro=new Command(SR.get(SR.MS_TEST_VIBRATION), Command.SCREEN, 3);
        cmdTestVibro.setImg(0x43);

        isVibroProfile = (midlet.BombusQD.cf.profile==AlertProfile.ALL || midlet.BombusQD.cf.profile==AlertProfile.VIBRA);

        ac=AlertCustomize.getInstance();

        files=new StringLoader().stringLoader("/sounds/res.txt",3);
        fileNames=null;
        fileNames=new Vector(0);
	for (Enumeration file=files[2].elements(); file.hasMoreElements(); ) {
            fileNames.addElement((String)file.nextElement());
	}

        MessageFile=new DropChoiceBox(display, SR.get(SR.MS_MESSAGE_SOUND)); MessageFile.items=fileNames;
        MessageFile.setSelectedIndex(ac.soundsMsgIndex); itemsList.addElement(MessageFile);

        OnlineFile=new DropChoiceBox(display, SR.get(SR.MS_ONLINE_SOUND)); OnlineFile.items=fileNames;
        OnlineFile.setSelectedIndex(ac.soundOnlineIndex); itemsList.addElement(OnlineFile);

        OfflineFile=new DropChoiceBox(display, SR.get(SR.MS_OFFLINE_SOUND)); OfflineFile.items=fileNames;
        OfflineFile.setSelectedIndex(ac.soundOfflineIndex); itemsList.addElement(OfflineFile);

        ForYouFile=new DropChoiceBox(display, SR.get(SR.MS_MESSAGE_FOR_ME_SOUND)); ForYouFile.items=fileNames;
        ForYouFile.setSelectedIndex(ac.soundForYouIndex); itemsList.addElement(ForYouFile);

        ComposingFile=new DropChoiceBox(display, SR.get(SR.MS_COMPOSING_SOUND)); ComposingFile.items=fileNames;
        ComposingFile.setSelectedIndex(ac.soundComposingIndex); itemsList.addElement(ComposingFile);

        ConferenceFile=new DropChoiceBox(display, SR.get(SR.MS_CONFERENCE_SOUND)); ConferenceFile.items=fileNames;
        ConferenceFile.setSelectedIndex(ac.soundConferenceIndex); itemsList.addElement(ConferenceFile);

        StartUpFile=new DropChoiceBox(display, SR.get(SR.MS_STARTUP_SOUND)); StartUpFile.items=fileNames;
        StartUpFile.setSelectedIndex(ac.soundStartUpIndex); itemsList.addElement(StartUpFile);

        OutgoingFile=new DropChoiceBox(display, SR.get(SR.MS_OUTGOING_SOUND)); OutgoingFile.items=fileNames;
        OutgoingFile.setSelectedIndex(ac.soundOutgoingIndex); itemsList.addElement(OutgoingFile);

        VIPFile=new DropChoiceBox(display, SR.get(SR.MS_VIP_SOUND)); VIPFile.items=fileNames;
        VIPFile.setSelectedIndex(ac.soundVIPIndex); itemsList.addElement(VIPFile);

        enableAttention=new CheckBox(SR.get(SR.LA_ENABLE), ac.enableAttention); itemsList.addElement(enableAttention);
        AttentionFile=new DropChoiceBox(display, SR.get(SR.LA_SOUND)); AttentionFile.items=fileNames;
        AttentionFile.setSelectedIndex(ac.soundAttentionIndex); itemsList.addElement(AttentionFile);

        itemsList.addElement(new SimpleString(SR.get(SR.MS_SHOW_LAST_APPEARED_CONTACTS), true));
        statusBox=new CheckBox(SR.get(SR.MS_STATUS), midlet.BombusQD.cf.notifyPicture); itemsList.addElement(statusBox);
        blinkBox=new CheckBox(SR.get(SR.MS_BLINKING), midlet.BombusQD.cf.notifyBlink); itemsList.addElement(blinkBox);
        //soundBox=new CheckBox(SR.get(SR.MS_SOUND, cf.notifySound); itemsList.addElement(soundBox);

        if(isVibroProfile){
          itemsList.addElement(new SpacerItem(10));
          vibrateOnlyHighlited=new CheckBox(SR.get(SR.MS_VIBRATE_ONLY_HIGHLITED), ac.vibrateOnlyHighlited); itemsList.addElement(vibrateOnlyHighlited);
        }

        itemsList.addElement(new SimpleString(SR.get(SR.MS_SOUND_VOLUME), true));
        sndVol=new TrackItem(ac.soundVol/10, 10);
        itemsList.addElement(sndVol);

        if(isVibroProfile){
          vibraLen=new NumberInput(display, SR.get(SR.MS_VIBRATION_LEN) + "(1-5000)", Integer.toString(ac.vibraLen), 1, 5000);
          itemsList.addElement(vibraLen);

          vibraRepeatCount=new NumberInput(display, SR.get(SR.MS_VIBRATION_REPEAT), Integer.toString(ac.vibraRepeatCount), 1, 8);
          itemsList.addElement(vibraRepeatCount);

          vibraRepeatPause=new NumberInput(display, SR.get(SR.MS_VIBRATION_INTERVAL), Integer.toString(ac.vibraRepeatPause), 1, 400);
          itemsList.addElement(vibraRepeatPause);
        }

        itemsList.addElement(new SpacerItem(10));
        if(midlet.BombusQD.cf.userAppLevel==1) {
          IQNotify=new CheckBox(SR.get(SR.MS_SHOW_IQ_REQUESTS), midlet.BombusQD.cf.IQNotify); itemsList.addElement(IQNotify);
        }

        //#ifdef LIGHT_CONTROL
        itemsList.addElement(new SpacerItem(5));
        linkLight = new LinkString(SR.get(SR.L_CONFIG)) { public void doAction() {
            new LightConfigForm(midlet.BombusQD.getInstance().display, midlet.BombusQD.getInstance().display.getCurrent());
        } };
        itemsList.addElement(linkLight);
        //#endif

        attachDisplay(display);
        this.parentView=pView;
    }

    public void cmdSave() {
        ac.soundsMsgIndex=MessageFile.getSelectedIndex();
        ac.soundVol=sndVol.getValue()*10;
        if(isVibroProfile) {
            ac.vibraLen=Integer.parseInt(vibraLen.getValue());
            ac.vibraRepeatCount=Integer.parseInt(vibraRepeatCount.getValue());
            ac.vibraRepeatPause=Integer.parseInt(vibraRepeatPause.getValue());
        }
        ac.soundOnlineIndex=OnlineFile.getSelectedIndex();
        ac.soundOfflineIndex=OfflineFile.getSelectedIndex();
        ac.soundForYouIndex=ForYouFile.getSelectedIndex();
        ac.soundComposingIndex=ComposingFile.getSelectedIndex();
        ac.soundConferenceIndex=ConferenceFile.getSelectedIndex();
        ac.soundStartUpIndex=StartUpFile.getSelectedIndex();
        ac.soundOutgoingIndex=OutgoingFile.getSelectedIndex();
        ac.soundVIPIndex=VIPFile.getSelectedIndex();
        ac.soundAttentionIndex=AttentionFile.getSelectedIndex();
	ac.enableAttention=enableAttention.getValue();

        if(isVibroProfile) ac.vibrateOnlyHighlited=vibrateOnlyHighlited.getValue();

        ac.saveToStorage();

        midlet.BombusQD.cf.notifyPicture=statusBox.getValue();
        midlet.BombusQD.cf.notifyBlink=blinkBox.getValue();
        //cf.notifySound=soundBox.getValue();
        if(midlet.BombusQD.cf.userAppLevel==1) {
          midlet.BombusQD.cf.IQNotify=IQNotify.getValue();
        }

        //cf.saveToStorage();
        EntityCaps.initCaps();

        destroyView();
    }

    public void commandAction(Command c, Displayable d) {
        if (c==cmdTest)
            PlaySound(false);
        else if (c==cmdSave) {
            cmdSave();
        }else if(c==cmdTestVibro){
            PlaySound(true);
        }
        else super.commandAction(c, d);
    }

    private int playable() {
        if (cursor<9 || cursor==10) return cursor;
        return -1;
    }

    private void PlaySound(boolean vibration){
        if(vibration){
          ac.vibraRepeatCount=Integer.parseInt(vibraRepeatCount.getValue());
          ac.vibraRepeatPause=Integer.parseInt(vibraRepeatPause.getValue());
          new EventNotify(display, null, null, -1, Integer.parseInt(vibraLen.getValue()) ).startNotify();
          return;
        }
        int sound=playable();
        if (sound<0) return;

        int selectedSound=((DropChoiceBox)itemsList.elementAt(sound)).getSelectedIndex();

        String soundFile=(String)files[1].elementAt(selectedSound);
        String soundType=(String)files[0].elementAt(selectedSound);
        int soundVol=sndVol.getValue()*10;
//#ifdef DEBUG
//#         System.out.println(cursor+": "+sound+" "+soundFile+" "+soundType+" "+soundVol);
//#endif
        new EventNotify(display, soundType, soundFile, soundVol, 0).startNotify();
    }

    public void commandState(){
//#ifdef MENU_LISTENER
        menuCommands.removeAllElements();
//#endif
//#ifndef GRAPHICS_MENU
//#     super.commandState();
//#endif
        if (playable() > -1) {
            addCommand(cmdTest);
        }
        if (isVibroProfile) {
            addCommand(cmdTestVibro);
        }
        addCommand(cmdSave);
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

        menuItem = new GMenu(display, parentView, this,null, menuCommands);
        GMenuConfig.getInstance().itemGrMenu = GMenu.ALERT_CUSTOMIZE_FORM;
        redraw();
        return GMenu.ALERT_CUSTOMIZE_FORM;
    }
//#else
//#     public void touchLeftPressed(){
//#         showMenu();
//#     }
//#     public void showMenu() {
//#         commandState();
//#         new MyMenu(display, parentView, this, SR.get(SR.MS_NOTICES_OPTIONS), null, menuCommands);
//#    }
//#endif

//#endif
}
