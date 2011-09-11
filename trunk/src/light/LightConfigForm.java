/*
 * LightControlForm.java
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

//#if LIGHT_CONTROL
package light;

import client.Config;
import locale.SR;
import ui.controls.form.SimpleString;
import ui.controls.form.CheckBox;
import ui.controls.form.DefForm;
import ui.controls.form.LightControlItem;
import ui.controls.form.NumberInput;
import ui.controls.form.SpacerItem;
import ui.controls.form.TrackItem;

public class LightConfigForm extends DefForm {
    private static final int SPACE_HEIGHT = 5;

    private CheckBox config_enabled;
    private CheckBox config_oneLight;
    private TrackItem lightIdle;

    private LightControlItem lightKeyPress;
    private NumberInput lightKeyPressTime;

    private LightControlItem lightMessage;
    private NumberInput lightMessageTime;

    private LightControlItem lightPresence;
    private NumberInput lightPresenceTime;

    private LightControlItem lightError;
    private NumberInput lightErrorTime;

    private LightControlItem lightConnect;
    private NumberInput lightConnectTime;

    private LightControlItem lightBlink;
    private NumberInput lightBlinkTime;

    public LightConfigForm() {
        super(SR.get(SR.L_CONFIG));

        config_enabled = new CheckBox(SR.get(SR.L_ENABLED), Config.lightControl);
        config_oneLight= new CheckBox( "Одна яркость для всех случаев", Config.oneLight);
        lightIdle = new LightControlItem(Config.lightIdle);

        lightKeyPress = new LightControlItem(Config.lightKeyPress);
        lightKeyPressTime = new NumberInput(SR.get(SR.L_KEYPRESS_TIMEOUT), Config.lightKeyPressTime, 1, 600);
        lightMessage = new LightControlItem(Config.lightMessage);
        lightMessageTime = new NumberInput(SR.get(SR.L_MESSAGE_TIMEOUT), Config.lightMessageTime, 1, 600);
        lightPresence = new LightControlItem(Config.lightPresence);
        lightPresenceTime = new NumberInput(SR.get(SR.MS_PRESENCE_TIMEOUT), Config.lightPresenceTime, 1, 600);
        lightConnect = new LightControlItem(Config.lightConnect);
        lightConnectTime = new NumberInput(SR.get(SR.MS_CONNECT_TIMEOUT), Config.lightConnectTime, 1, 600);
        lightError = new LightControlItem(Config.lightError);
        lightErrorTime = new NumberInput(SR.get(SR.MS_ERROR_TIMEOUT), Config.lightErrorTime, 1, 600);
        lightBlink = new LightControlItem(Config.lightBlink);
        lightBlinkTime = new NumberInput(SR.get(SR.MS_BLINK_TIMEOUT), Config.lightBlinkTime, 1, 600);
    }

    public void cmdOk() {
        Config.lightControl = config_enabled.getValue();
        Config.oneLight = config_oneLight.getValue();

        // light.light_idle=light.light_idle*5;//округление
        // r94m - переменный шаг подсветки by Mars
        Config.lightIdle = lightIdle.getValue();

        Config.lightKeyPress = lightKeyPress.getValue();
        Config.lightKeyPressTime = lightKeyPressTime.getIntValue();

        Config.lightMessage = lightMessage.getValue();
        Config.lightMessageTime = lightMessageTime.getIntValue();

        Config.lightPresence = lightPresence.getValue();
        Config.lightPresenceTime = lightPresenceTime.getIntValue();

        Config.lightError = lightError.getValue();
        Config.lightErrorTime = lightErrorTime.getIntValue();

        Config.lightConnect = lightConnect.getValue();
        Config.lightConnectTime = lightConnectTime.getIntValue();

        Config.lightBlink = lightBlink.getValue();
        Config.lightBlinkTime = lightBlinkTime.getIntValue();

        CustomLight.switchOn(Config.lightControl);
        destroyView();
    }

    protected void beginPaint(){
        update();
    }

    private void update(){
        itemsList.removeAllElements();

        addControl(config_enabled);
        addControl(config_oneLight);
        addControl(new SpacerItem(SPACE_HEIGHT));
        addControl(new SimpleString(SR.get(SR.L_IDLE_VALUE), true));
        addControl(lightIdle);

        if( config_oneLight.getValue())
            return;

        addControl(new SpacerItem(SPACE_HEIGHT));
        addControl(new SimpleString(SR.get(SR.L_KEYPRESS_VALUE), true));
        addControl(lightKeyPress);
        addControl(lightKeyPressTime);
        addControl(new SpacerItem(SPACE_HEIGHT));
        addControl(new SimpleString(SR.get(SR.L_MESSAGE_VALUE), true));
        addControl(lightMessage);
        addControl(lightMessageTime);
        addControl(new SpacerItem(SPACE_HEIGHT));
        addControl(new SimpleString(SR.get(SR.MS_PRESENCE_VALUE), true));
        addControl(lightPresence);
        addControl(lightPresenceTime);
        addControl(new SpacerItem(SPACE_HEIGHT));
        addControl(new SimpleString(SR.get(SR.MS_CONNECT_VALUE), true));
        addControl(lightConnect);
        addControl(lightConnectTime);
        addControl(new SpacerItem(SPACE_HEIGHT));
        addControl(new SimpleString(SR.get(SR.MS_ERROR_VALUE), true));
        addControl(lightError);
        addControl(lightErrorTime);
        addControl(new SimpleString(SR.get(SR.MS_BLINK_VALUE), true));
        addControl(lightBlink);
        addControl(lightBlinkTime);
    }

}
//#endif
