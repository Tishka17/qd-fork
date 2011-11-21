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

import xmpp.EntityCaps;
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
import menu.Command;
import ui.GMenu;
import ui.GMenuConfig;
//#ifdef LIGHT_CONTROL
import light.LightConfigForm;
import ui.controls.form.LinkString;
//#endif
import menu.MenuListener;

public class AlertCustomizeForm extends DefForm implements MenuListener {
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

    AlertCustomize ac;
    Vector files[];
    Vector fileNames;
    boolean isVibroProfile = false;

    private Command cmdSave;
    private Command cmdTest;
    private Command cmdTestVibro;

    public AlertCustomizeForm() {
        super(SR.get(SR.MS_NOTICES_OPTIONS));

        cmdSave=new Command(SR.get(SR.MS_SAVE), 0x44);
        cmdTest=new Command(SR.get(SR.MS_TEST_SOUND), 0x54);
        cmdTestVibro=new Command(SR.get(SR.MS_TEST_VIBRATION), 0x43);

        isVibroProfile = (midlet.BombusQD.cf.currentAlertProfile==AlertProfile.ALL || midlet.BombusQD.cf.currentAlertProfile==AlertProfile.VIBRA);

        ac=AlertCustomize.getInstance();

        files=new StringLoader().stringLoader("/sounds/res.txt",3);
        fileNames=null;
        fileNames=new Vector(0);
	for (Enumeration file=files[2].elements(); file.hasMoreElements(); ) {
            fileNames.addElement((String)file.nextElement());
	}

        MessageFile=new DropChoiceBox(SR.get(SR.MS_MESSAGE_SOUND)); MessageFile.items=fileNames;
        MessageFile.setSelectedIndex(ac.soundsMsgIndex);
        addControl(MessageFile);

        OnlineFile=new DropChoiceBox(SR.get(SR.MS_ONLINE_SOUND)); OnlineFile.items=fileNames;
        OnlineFile.setSelectedIndex(ac.soundOnlineIndex);
        addControl(OnlineFile);

        OfflineFile=new DropChoiceBox(SR.get(SR.MS_OFFLINE_SOUND)); OfflineFile.items=fileNames;
        OfflineFile.setSelectedIndex(ac.soundOfflineIndex);
        addControl(OfflineFile);

        ForYouFile=new DropChoiceBox(SR.get(SR.MS_MESSAGE_FOR_ME_SOUND)); ForYouFile.items=fileNames;
        ForYouFile.setSelectedIndex(ac.soundForYouIndex);
        addControl(ForYouFile);

        ComposingFile=new DropChoiceBox(SR.get(SR.MS_COMPOSING_SOUND)); ComposingFile.items=fileNames;
        ComposingFile.setSelectedIndex(ac.soundComposingIndex);
        addControl(ComposingFile);

        ConferenceFile=new DropChoiceBox(SR.get(SR.MS_CONFERENCE_SOUND)); ConferenceFile.items=fileNames;
        ConferenceFile.setSelectedIndex(ac.soundConferenceIndex);
        addControl(ConferenceFile);

        StartUpFile=new DropChoiceBox(SR.get(SR.MS_STARTUP_SOUND)); StartUpFile.items=fileNames;
        StartUpFile.setSelectedIndex(ac.soundStartUpIndex);
        addControl(StartUpFile);

        OutgoingFile=new DropChoiceBox(SR.get(SR.MS_OUTGOING_SOUND)); OutgoingFile.items=fileNames;
        OutgoingFile.setSelectedIndex(ac.soundOutgoingIndex);
        addControl(OutgoingFile);

        VIPFile=new DropChoiceBox(SR.get(SR.MS_VIP_SOUND)); VIPFile.items=fileNames;
        VIPFile.setSelectedIndex(ac.soundVIPIndex);
        addControl(VIPFile);

        enableAttention=new CheckBox(SR.get(SR.LA_ENABLE), ac.enableAttention);
        addControl(enableAttention);
        AttentionFile=new DropChoiceBox(SR.get(SR.LA_SOUND)); AttentionFile.items=fileNames;
        AttentionFile.setSelectedIndex(ac.soundAttentionIndex);
        addControl(AttentionFile);

        addControl(new SimpleString(SR.get(SR.MS_SHOW_LAST_APPEARED_CONTACTS), true));

        statusBox=new CheckBox(SR.get(SR.MS_STATUS), midlet.BombusQD.cf.notifyPicture);
        addControl(statusBox);

        blinkBox=new CheckBox(SR.get(SR.MS_BLINKING), midlet.BombusQD.cf.notifyBlink);
        addControl(blinkBox);


        if(isVibroProfile){
            addControl(new SpacerItem(10));
            vibrateOnlyHighlited=new CheckBox(SR.get(SR.MS_VIBRATE_ONLY_HIGHLITED), ac.vibrateOnlyHighlited);
            addControl(vibrateOnlyHighlited);
        }

        addControl(new SimpleString(SR.get(SR.MS_SOUND_VOLUME), true));
        sndVol=new TrackItem(ac.soundVol/10, 10);
        addControl(sndVol);

        if(isVibroProfile){
          vibraLen=new NumberInput(SR.get(SR.MS_VIBRATION_LEN) + "(1-5000)", ac.vibraLen, 1, 5000);
          addControl(vibraLen);

          vibraRepeatCount=new NumberInput(SR.get(SR.MS_VIBRATION_REPEAT), ac.vibraRepeatCount, 1, 8);
          addControl(vibraRepeatCount);

          vibraRepeatPause=new NumberInput(SR.get(SR.MS_VIBRATION_INTERVAL), ac.vibraRepeatPause, 1, 400);
          addControl(vibraRepeatPause);
        }

        addControl(new SpacerItem(10));
        if(midlet.BombusQD.cf.userAppLevel==1) {
          IQNotify=new CheckBox(SR.get(SR.MS_SHOW_IQ_REQUESTS), midlet.BombusQD.cf.IQNotify); addControl(IQNotify);
        }

//#ifdef LIGHT_CONTROL
        addControl(new SpacerItem(5));
        addControl(new LinkString(SR.get(SR.L_CONFIG)) { public void doAction() {
            new LightConfigForm().show();
        }});
//#endif
    }

    public void cmdSave() {
        ac.soundsMsgIndex=MessageFile.getSelectedIndex();
        ac.soundVol=sndVol.getValue()*10;
        if(isVibroProfile) {
            ac.vibraLen=vibraLen.getIntValue();
            ac.vibraRepeatCount=vibraRepeatCount.getIntValue();
            ac.vibraRepeatPause=vibraRepeatPause.getIntValue();
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

    public void commandAction(Command c) {
        if (c==cmdTest)
            PlaySound(false);
        else if (c==cmdSave) {
            cmdSave();
        }else if(c==cmdTestVibro){
            PlaySound(true);
        }
    }

    private int playable() {
        if (cursor<9 || cursor==10) return cursor;
        return -1;
    }

    private void PlaySound(boolean vibration){
        if(vibration){
          ac.vibraRepeatCount=vibraRepeatCount.getIntValue();
          ac.vibraRepeatPause=vibraRepeatPause.getIntValue();
          new EventNotify(null, null, -1, vibraLen.getIntValue()).startNotify();
          return;
        }
        int sound=playable();
        if (sound<0) return;

        int selectedSound=((DropChoiceBox)itemsList.elementAt(sound)).getSelectedIndex();

        String soundFile=(String)files[1].elementAt(selectedSound);
        String soundType=(String)files[0].elementAt(selectedSound);
        int soundVol=sndVol.getValue()*10;
        new EventNotify(soundType, soundFile, soundVol, 0).startNotify();
    }

    public void commandState(){
        menuCommands.removeAllElements();
        if (playable() > -1) {
            addCommand(cmdTest);
        }
        if (isVibroProfile) {
            addCommand(cmdTestVibro);
        }
        addCommand(cmdSave);
    }

    public String touchLeftCommand(){ return SR.get(SR.MS_MENU); }

    public void touchLeftPressed(){
        showGraphicsMenu();
    }

    public int showGraphicsMenu() {
        commandState();

        menuItem = new GMenu(this, menuCommands);
        GMenuConfig.getInstance().itemGrMenu = GMenu.ALERT_CUSTOMIZE_FORM;
        redraw();
        return GMenu.ALERT_CUSTOMIZE_FORM;
    }
}
