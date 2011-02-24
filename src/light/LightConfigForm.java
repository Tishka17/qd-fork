/*
 * LightConfigForm.java
 */ 

package light;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
import java.util.Vector;
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
import menu.MenuListener;
import menu.Command;
import menu.MyMenu;
//#endif
//#ifdef GRAPHICS_MENU        
//# import ui.GMenu;
//# import ui.GMenuConfig;
//#endif

public class LightConfigForm
        extends DefForm {
    
    private Display display;

    private CheckBox config_enabled;

    private TrackItem light_idle;

    private TrackItem light_keypressed;
    private NumberInput light_keypressed_time;

    private TrackItem light_message;
    private NumberInput light_message_time;
    //private static CheckBox lightState;    

    
    LightConfig light;

    /** Creates a new instance of ConfigForm */
    public LightConfigForm(Display display, Displayable pView) {
        super(display, pView, SR.get(SR.L_CONFIG));
        this.display=display;

        light=LightConfig.getInstance();

        config_enabled=new CheckBox(SR.get(SR.L_ENABLED), light.light_control);
        itemsList.addElement(config_enabled);

        itemsList.addElement(new SpacerItem(10));
        itemsList.addElement(new SimpleString(SR.get(SR.L_IDLE_VALUE), true));
        light_idle=new TrackItem(light.light_idle/10, 10);
        itemsList.addElement(light_idle);

        itemsList.addElement(new SpacerItem(10));
        itemsList.addElement(new SimpleString(SR.get(SR.L_KEYPRESS_VALUE), true));
        light_keypressed=new TrackItem(light.light_keypress/10, 10);
        itemsList.addElement(light_keypressed);
        light_keypressed_time=new NumberInput(display, SR.get(SR.L_KEYPRESS_TIMEOUT), Integer.toString(light.light_keypressed_time), 1, 600);
        itemsList.addElement(light_keypressed_time);

        itemsList.addElement(new SpacerItem(10));
        itemsList.addElement(new SimpleString(SR.get(SR.L_MESSAGE_VALUE), true));
        light_message=new TrackItem(light.light_message/10, 10);
        itemsList.addElement(light_message);
        light_message_time=new NumberInput(display, SR.get(SR.L_MESSAGE_TIMEOUT), Integer.toString(light.light_message_time), 1, 600);
        itemsList.addElement(light_message_time);

        /*
        itemsList.addElement(new SpacerItem(5));
        lightState = new CheckBox(SR.get(SR.MS_FLASHLIGHT), midlet.BombusQD.cf.lightState);
           if (phoneManufacturer==Config.SIEMENS || phoneManufacturer==Config.SIEMENS2 
           || phoneManufacturer==Config.SONYE || phoneManufacturer==Config.NOKIA) itemsList.addElement(lightState);  
         */
        
        commandState();

        attachDisplay(display);
        this.parentView=pView;
    }
    
    public void commandState() {
//#ifdef MENU_LISTENER
        menuCommands.removeAllElements();
//#endif
        addCommand(midlet.BombusQD.commands.cmdOk); //cmdOk.setImg(0x43);
        addCommand(cmdCancel);
    }
   
    public void cmdOk() {
        light.light_control=config_enabled.getValue();
        light.light_idle=light_idle.getValue();
        light.light_idle=light.light_idle*10;//округление
        light.light_keypress=light_keypressed.getValue();
        light.light_keypress=light.light_keypress*10;
        light.light_keypressed_time=Integer.parseInt(light_keypressed_time.getValue());
        light.light_message=light_message.getValue();
        light.light_message=light.light_message*10;
        light.light_message_time=Integer.parseInt(light_message_time.getValue());
        //midlet.BombusQD.cf.lightState=lightState.getValue();
        light.saveToStorage();
	CustomLight.switchOn(light.light_control);
        destroyView();
    }
           
}
