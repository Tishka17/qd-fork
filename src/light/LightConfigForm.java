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
        addControl(config_enabled);

        addControl(new SpacerItem(SPACE_HEIGHT));

        addControl(new SimpleString(SR.get(SR.L_IDLE_VALUE), true));
        lightIdle = new LightControlItem(Config.lightIdle);
        addControl(lightIdle);

        addControl(new SpacerItem(SPACE_HEIGHT));

        addControl(new SimpleString(SR.get(SR.L_KEYPRESS_VALUE), true));
        lightKeyPress = new LightControlItem(Config.lightKeyPress);
        addControl(lightKeyPress);

        lightKeyPressTime = new NumberInput(SR.get(SR.L_KEYPRESS_TIMEOUT), Config.lightKeyPressTime, 1, 600);
        addControl(lightKeyPressTime);

        addControl(new SpacerItem(SPACE_HEIGHT));

        addControl(new SimpleString(SR.get(SR.L_MESSAGE_VALUE), true));
        lightMessage = new LightControlItem(Config.lightMessage);
        addControl(lightMessage);

        lightMessageTime = new NumberInput(SR.get(SR.L_MESSAGE_TIMEOUT), Config.lightMessageTime, 1, 600);
        addControl(lightMessageTime);

        addControl(new SpacerItem(SPACE_HEIGHT));

        addControl(new SimpleString("Presence screen brightness", true));
        lightPresence = new LightControlItem(Config.lightPresence);
        addControl(lightPresence);

        lightPresenceTime = new NumberInput("Presence idle timeout", Config.lightPresenceTime, 1, 600);
        addControl(lightPresenceTime);

        addControl(new SpacerItem(SPACE_HEIGHT));

        addControl(new SimpleString("Connect screen brightness", true));
        lightConnect = new LightControlItem(Config.lightConnect);
        addControl(lightConnect);

        lightConnectTime = new NumberInput("Connect idle timeout", Config.lightConnectTime, 1, 600);
        addControl(lightConnectTime);

        addControl(new SpacerItem(SPACE_HEIGHT));

        addControl(new SimpleString("Error screen brightness", true));
        lightError = new LightControlItem(Config.lightError);
        addControl(lightError);

        lightErrorTime = new NumberInput("Error idle timeout", Config.lightErrorTime, 1, 600);
        addControl(lightErrorTime);

        addControl(new SimpleString("Blink brightness", true));
        lightBlink = new LightControlItem(Config.lightBlink);
        addControl(lightBlink);

        lightBlinkTime = new NumberInput("Blink timeout", Config.lightBlinkTime, 1, 600);
        addControl(lightBlinkTime);
    }

    public void cmdOk() {
        Config.lightControl = config_enabled.getValue();

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
}
//#endif
