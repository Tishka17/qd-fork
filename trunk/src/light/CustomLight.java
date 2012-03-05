/*
 * Light.java
 *
 * Light-control module.
 *
 * Usage:
 * <CODE>
 * CustomLight.setLightMode(CustomLight.ACTION_NONE);
 * </CODE>
 *
 * @author Vladimir Krukov
 */

//#if LIGHT_CONTROL
package light;

import client.Config;
import javax.microedition.lcdui.*;
import midlet.BombusQD;
//import javax.microedition.midlet.*;
//import com.motorola.funlight.*;
import com.siemens.mp.game.*;
import com.nokia.mid.ui.*;
//import com.samsung.util.*;
import java.util.*;

/**
 * Class for platform-independent light control.
 * 
 * @author Vladimir Krukov
 */
public final class CustomLight extends TimerTask {
    private static final int MAX_BLICKS_COUNT = 5;

    private static CustomLight instance = new CustomLight(null);
    private Timer timer;

    private static final byte LIGHT_NONE               = 0;
    private static final byte LIGHT_SIEMENS            = 1;
    private static final byte LIGHT_NOKIA              = 2;
    private static final byte LIGHT_MOTOROLA_FUNLIGHT  = 3;
//    private static final byte LIGHT_SAMSUNG            = 4;
//    private static final byte LIGHT_LG                 = 5;
    private static final byte LIGHT_MIDP20             = 6;

    //states
    static final byte IDLE = 0;
    static final byte MESSAGE = 1;
    static final byte KEYPRESSED = 2;
    static final byte ERROR = 3;
    static final byte PRESENCE = 4;
    static final byte CONNECT = 5;
    static final byte BLINK0 = 100;//no light, use this only if previous state is lighting
    static final byte BLINK1 = 101;//light
    static final byte BLINK2 = 102;//no light
    static final byte BLINK3 = 103;//light again

     /* If system windows opened only one state is possible - `keypressed'
     **/

    private static int light  = detectMode();
    private byte state = CONNECT;
    private byte previous_state = IDLE;
    private int tick = 10;
    private int previous_tick =0;
    private int blinks = 0;

    private static final int INTERVAL = 1000;
    
    public static void setLightMode(final byte m) {
        if (null != instance) {
            instance.setMode(m);
        }
    }

    public static void keyPressed() {
        if (null != instance) {
            instance.setMode(KEYPRESSED);
        }
    }
    public static void message() {
        if (null != instance && instance.state<BLINK0) {
            instance.setMode(MESSAGE);
        }
    }
    public static void presence() {
        if (null != instance) {
            instance.setMode(PRESENCE);
        }
    }
    public static void error() {
        if (null != instance) {
            instance.setMode(ERROR);
        }
    }
    public static void startBlinking() {
        if (null != instance) {
            instance.setMode(BLINK0);
        }
    }


    //returns if current displayable is system one
    private static boolean isSystem() {
        return !(BombusQD.sd.canvas.isShown());
    }

    //returns number of ticks should be made at a certain state
    private int getMaxTickCount(final byte m) {
        switch (m) {
            case IDLE:
                return 32767;
            case MESSAGE:
                return Math.max(1, Config.lightMessageTime);
            case KEYPRESSED:
                return Math.max(1, Config.lightKeyPressTime);
            case ERROR:
                return Math.max(1, Config.lightErrorTime);
            case PRESENCE:
                return Math.max(1, Config.lightPresenceTime);
            case BLINK0:
            case BLINK1:
                return Math.max(1, Config.lightBlinkTime);
        }
        return 32767;
    }

    //changes mode
    private synchronized void setMode(final byte m) {
        //not controlling light or system window or minimized
        if (!Config.lightControl || isSystem() || BombusQD.isMinimized()) {
            return;
        }
        if (m!=KEYPRESSED && m!=CONNECT && m!=IDLE && (state==IDLE || state==KEYPRESSED)) {
            previous_state = state;
            previous_tick = tick;
        }
        
        tick = getMaxTickCount(m);
        //same state as before
        if (m == state) {
            return;
        }
        if (m==BLINK0 && getLightValue(state) < Config.lightBlink / 2)
            state=BLINK1;
        else
            state = m;
	blinks = MAX_BLICKS_COUNT;
        setLight();
    }

    private synchronized void setLight() {
        if( Config.oneLight)
            setLight(getLightValue(IDLE));
        else
            setLight(getLightValue(state));
    }

    public void run() {
        if (!Config.lightControl || isSystem() || BombusQD.isMinimized()) {
            return;
        }
        setLight();
        if (state==IDLE || isSystem())
            return;
        if (0 < tick) {
            tick--;
            return;
        }

        switch (state) {
            case MESSAGE:
            case PRESENCE:
            case BLINK3:
            case ERROR:
                state = previous_state;
                tick = previous_tick;
                break;
            case KEYPRESSED:
            case CONNECT:
                state = IDLE;
                break;
            case BLINK0:
                state = BLINK1;
                tick = getMaxTickCount(state);
		if (blinks<=0)
			state = IDLE;
                break;
            case BLINK1:
                state = BLINK0;
                tick = getMaxTickCount(state);
		blinks--;
                break;
            default:
                state = IDLE;
                break;
        }
    }

    private void setLight(int value) {
        if ((value > 100) || (value < 0)) {
            return;
        }
        // дает слабое ежесекундное мерцание экрана
        // на новых нокиях - отключено (Марс)
        /*if ((value > 0) && light==LIGHT_NOKIA) {
            setLight(0);
        }*/
        try {
            switch (light) {                
                case LIGHT_MOTOROLA_FUNLIGHT:
                    /*int curBrightness = value * 255 / 100;
                    int c = curBrightness + (curBrightness << 8) + (curBrightness << 16);
                    // 1 - Display
                    // 2 - Keypad
                    // 3 - Sideband

                    FunLight.getRegion(1).setColor(c);
                    FunLight.getRegion(1).getControl();
//                    if (value > 0) {
//                        FunLight.getRegion(1).getControl();
//                    } else {
//                        FunLight.getRegion(1).releaseControl();
//                        Jimm.getJimm().getDisplay().flashBacklight(0x00000000);
//                    }
                    break;#*/
                case LIGHT_SIEMENS:
                    if (value > 0) {
                        Light.setLightOn();
                    } else {
                        Light.setLightOff();
                    }
                    break;
                case LIGHT_NOKIA:
                    DeviceControl.setLights(0, value);
                    break;

//                case LIGHT_SAMSUNG:
//                    if (value > 0) {
//                        LCDLight.on(0x7FFFFFFF);
//                    } else {
//                        LCDLight.off();
//                    }
//                    break;
                case LIGHT_MIDP20:
                    if (value > 0) {
                        BombusQD.getInstance().display.flashBacklight(0x7FFFFFFF);
                    } else {
                        BombusQD.getInstance().display.flashBacklight(0x00000000);
                    }
                    break;
            }
        } catch (Exception e) {
        }
    }
        
    private static int detectMode() {
        try {
            Class.forName("com.nokia.mid.ui.DeviceControl");
            return LIGHT_NOKIA;
        } catch (Exception e) {
        }
//        try {
//            Class.forName("com.samsung.util.LCDLight");
//            return LIGHT_SAMSUNG;
//        } catch (Exception e) {
//        }

        try {
            Class.forName("com.siemens.mp.game.Light");
            return LIGHT_SIEMENS;
        } catch (Exception e) {
        }

        /*try {
            Class.forName("com.motorola.funlight.FunLight");
            if (FunLight.getRegions() != null) {
                String prod = System.getProperty("funlights.product");
                // #sijapp cond.if modules_DEBUGLOG is "true" #
                DebugLog.println("moto light product = " + (prod == null ? "null" : prod));
                // #sijapp cond.end #
                if ("E380".equals(prod) || "V600".equals(prod) || "E390".equals(prod)) {
                    return LIGHT_MOTOROLA_FUNLIGHT;
                }
            }
        } catch (Exception e) {
        }*/
        return LIGHT_MIDP20;
    }
    
    private int getLightValue(byte state) {
        switch (state) {
            case IDLE:
                return Config.lightIdle;
            case PRESENCE:
                return Config.lightPresence;
            case KEYPRESSED:
                return Config.lightKeyPress;
            case CONNECT:
                return Config.lightConnect;
            case MESSAGE:
                return Config.lightMessage;
            case ERROR:
                return Config.lightError;
            case BLINK0:
                return 0;
            case BLINK1:
                return Config.lightBlink;
            case BLINK2:
                return 0;
            case BLINK3:
                return Config.lightBlink;
        }
        return 100;
    }

    /** Creates a new instance of Light */
    private CustomLight(Timer timer) {
        this.timer = timer;
        if (null != timer) {
            timer.scheduleAtFixedRate(this, 0, INTERVAL);
        }
    }
    public static void switchOn(boolean on) {
        final boolean worked = (null != instance.timer);
        if (worked) {
            if (!on) {
                instance.timer.cancel();
                instance.setLight(40);
                instance = new CustomLight(null);
            }
        } else {
            if (on) {
                instance = new CustomLight(new Timer());
            }
        }
    }
}
//#endif
