/*
 * LightConfigForm.java
 */

package light;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
import java.util.Vector;
import midlet.Commands;
import ui.controls.form.SimpleString;
import ui.controls.form.CheckBox;
import ui.controls.form.DefForm;
import ui.controls.form.NumberInput;
import ui.controls.form.SpacerItem;
import ui.controls.form.TrackItem;
//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.CommandListener;
//# import javax.microedition.lcdui.Command;
//#else
//#endif
//#ifdef GRAPHICS_MENU
//#endif

public class LightConfigForm extends DefForm {

    private CheckBox config_enabled;

    private TrackItem light_idle;

    private TrackItem light_keypressed;
    private NumberInput light_keypressed_time;

    private TrackItem light_message;
    private NumberInput light_message_time;
    //private static CheckBox lightState;

    // переменный шаг подсветки (Марс)
    // массив значений для каждого шага
    private int light_lvarray[] =
        {0,1,2,3,5, 7,10,15,20,25,
        30,35,40,45,50, 60,70,80,90,100};
    // количество шагов
    private int lvarray_len = light_lvarray.length-1;
    private int lvarray_index;
    Vector lvsteps=new Vector();

    LightConfig light;

    private int indexByValue( int arr[], int val, int def) {
        int ret= -1;
        for( int i = 0; i < arr.length; i++)
            if( val ==arr[i]) return i;
            else if( def ==arr[i]) ret= i;
        return ret;
    }// indexByValue

    /** Creates a new instance of ConfigForm */
    public LightConfigForm(Display display, Displayable pView) {
        super(display, pView, SR.get(SR.L_CONFIG));

        for( int i = 0; i < lvarray_len; i++)
            lvsteps.addElement(light_lvarray);

        light=LightConfig.getInstance();

        config_enabled=new CheckBox(SR.get(SR.L_ENABLED), light.light_control);
        itemsList.addElement(config_enabled);

        itemsList.addElement(new SpacerItem(5));
        itemsList.addElement(new SimpleString(SR.get(SR.L_IDLE_VALUE), true));
        lvarray_index= indexByValue( light_lvarray, light.light_idle, 0);
        light_idle=new TrackItem(lvarray_index, lvarray_len);
        itemsList.addElement(light_idle);

        itemsList.addElement(new SpacerItem(5));
        itemsList.addElement(new SimpleString(SR.get(SR.L_KEYPRESS_VALUE), true));
        lvarray_index= indexByValue( light_lvarray, light.light_keypress, 50);
        light_keypressed=new TrackItem(lvarray_index, lvarray_len);
        itemsList.addElement(light_keypressed);

        light_keypressed_time=new NumberInput(display, SR.get(SR.L_KEYPRESS_TIMEOUT), Integer.toString(light.light_keypressed_time), 1, 600);
        itemsList.addElement(light_keypressed_time);

        itemsList.addElement(new SpacerItem(5));
        itemsList.addElement(new SimpleString(SR.get(SR.L_MESSAGE_VALUE), true));
        lvarray_index= indexByValue( light_lvarray, light.light_message, 100);
        light_message=new TrackItem(lvarray_index, lvarray_len);
        itemsList.addElement(light_message);

        light_message_time=new NumberInput(display, SR.get(SR.L_MESSAGE_TIMEOUT), Integer.toString(light.light_message_time), 1, 600);
        itemsList.addElement(light_message_time);

        /*
        itemsList.addElement(new SpacerItem(5));
        lightState = new CheckBox(SR.get(SR.MS_FLASHLIGHT), midlet.BombusQD.cf.lightState);
           if (phoneManufacturer==Config.SIEMENS || phoneManufacturer==Config.SIEMENS2
           || phoneManufacturer==Config.SONYE || phoneManufacturer==Config.NOKIA) itemsList.addElement(lightState);
         */

        attachDisplay(display);
        this.parentView=pView;
    }

    public void cmdOk() {
        light.light_control=config_enabled.getValue();
        light.light_idle=light_idle.getValue();
        //light.light_idle=light.light_idle*5;//округление
        // r94m - переменный шаг подсветки by Mars
        light.light_idle=light_lvarray[light.light_idle];
        light.light_keypress=light_keypressed.getValue();
        light.light_keypress=light_lvarray[light.light_keypress];
        light.light_keypressed_time=Integer.parseInt(light_keypressed_time.getValue());
        light.light_message=light_message.getValue();
        light.light_message=light_lvarray[light.light_message];
        light.light_message_time=Integer.parseInt(light_message_time.getValue());
        //midlet.BombusQD.cf.lightState=lightState.getValue();
        light.saveToStorage();
	CustomLight.switchOn(light.light_control);
        destroyView();
    }
}
