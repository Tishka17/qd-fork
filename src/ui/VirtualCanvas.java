/*
 * VirtualCanvas.java
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

package ui;

//#ifdef AUTOSTATUS
import client.AutoStatus;
//#endif
import client.Config;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
//#ifdef LIGHT_CONTROL
import light.CustomLight;
//#endif
import midlet.BombusQD;
//#ifdef USER_KEYS
import ui.keys.UserKeyExec;
//#endif

public class VirtualCanvas extends Canvas  implements CommandListener {
    private CanvasEx canvas;

    private Image offscreen;
    public static boolean isPainting;

    public VirtualCanvas() {
        super();

//#ifdef TOUCH
        Config.isTouchPhone = hasPointerEvents();
//#endif

        setFullScreenMode(Config.fullscreen);

        if(!isDoubleBuffered()) {
            offscreen = Image.createImage(getWidth(), getHeight());
        }
        /* probably for nokia if not full screen
        addCommand(ok);
        addCommand(cancel);
        setCommandListener(this);*/
    }

    /* private Command ok=new Command("Ok", Command.OK, 1); 
    private Command cancel=new Command("Cancel", Command.CANCEL, 2); */
    
    public void commandAction(Command c, Displayable s) {
      /*  if (c==ok) {
            keyPressed(LEFT_SOFT);
            keyReleased(LEFT_SOFT);
        } else if (c==cancel) {
            keyPressed(RIGHT_SOFT);
            keyReleased(RIGHT_SOFT);
        }*/
    }
    
    public void show(CanvasEx canvas) {
        this.canvas = canvas;
        if (isShown()) {
            this.canvas.showNotify();
            repaint();
        } else {
            BombusQD.setCurrentView(this);
        }
    }

    public void showNotify() {
        setFullScreenMode(Config.fullscreen);
        
        canvas.showNotify();
    }

    protected void sizeChanged(int w, int h) {
        canvas.sizeChanged(w, h);

        canvas.width = w;
        canvas.height = h;

        if(!isDoubleBuffered()) {
            offscreen = Image.createImage(w, h);
        }
    }
    
    public int getAvailWidth() {
        return getWidth() - Config.getInstance().scrollWidth - 2;
    }

    public CanvasEx getCanvas() {
        return canvas;
    }

    protected void paint(Graphics g) {
        isPainting = true;
        // from bm2
        canvas.width = getWidth();
        canvas.height = getHeight();
        if(!isDoubleBuffered()) {
            Graphics graphics = offscreen.getGraphics();
            canvas.paint(graphics);
            g.drawImage(offscreen, 0, 0, Graphics.TOP | Graphics.LEFT);
        } else {
            canvas.paint(g);
        }
        isPainting = false;
    }

    private static int kHold = 0; //Клавиша, удержание которой сработало
    private static long pressed_time=0;
    
    protected boolean canRepeatKey(int code) {
        return code==NAVIKEY_RIGHT || code==NAVIKEY_LEFT || code==NAVIKEY_UP
                || code==NAVIKEY_DOWN || code==KEY_NUM2 || code==KEY_NUM8
                //|| (!midlet.BombusQD.cf.useTabs && (code==KEY_NUM4 || code==KEY_NUM6)) 
                || code==VOLMINUS_KEY || code==VOLPLUS_KEY;
    }
    
    
    protected void keyPressed(int code) {
        pressed_time = System.currentTimeMillis();
        kHold = 0;
        code = getKey(code);
//#ifdef LIGHT_CONTROL
        CustomLight.keyPressed();
//#endif
//#ifdef AUTOSTATUS
        AutoStatus.getInstance().userActivity(Config.AWAY_IDLE);
//#endif
        if (canRepeatKey(code)) {
            pressed_time = 0;
//#ifdef USER_KEYS
            if( UserKeyExec.getInstance().getCommandByKey(code, false)) { //is UserKeyExec succesfully processed this key
                return;
            }
//#endif
            canvas.keyPressed(code);
        }
    }

    protected void keyRepeated(int code) {
        code = getKey(code);
//#ifdef LIGHT_CONTROL
        CustomLight.keyPressed();
//#endif
//#ifdef AUTOSTATUS
        AutoStatus.getInstance().userActivity(Config.AWAY_IDLE);
//#endif
        if (canRepeatKey(code)) {
//#ifdef USER_KEYS
            if( UserKeyExec.getInstance().getCommandByKey(code, false)) { //is UserKeyExec succesfully processed this key
                pressed_time = 0;
                return;
            }
//#endif
            canvas.keyPressed(code);
        } else if (kHold!=code) {
//#ifdef USER_KEYS
            if( UserKeyExec.getInstance().getCommandByKey(code, true)) {//is UserKeyExec succesfully processed this long key
                kHold = code;
                pressed_time = 0;
                return;
            }
//#endif
            if (canvas.keyLong(code)) {
                pressed_time = 0;
                kHold = code;
            }
        }
    }

    protected void keyReleased(int code) {
        code = getKey(code);
//#ifdef LIGHT_CONTROL
        CustomLight.keyPressed();
//#endif
//#ifdef AUTOSTATUS
        AutoStatus.getInstance().userActivity(Config.AWAY_IDLE);
//#endif
        //check if key was pressed in this Displayable and still need to be processed
        if (pressed_time == 0)
            return;
        else if (kHold!=code) {
//#ifdef USER_KEYS
            if( UserKeyExec.getInstance().getCommandByKey(code, false)) //is UserKeyExec succesfully processed this key
                return;
//#endif
            canvas.keyPressed(code);
        }
        pressed_time = 0;
        kHold=0;
    }

    protected void pointerPressed(int x, int y) {
//#ifdef LIGHT_CONTROL
        CustomLight.keyPressed();
//#endif
//#ifdef AUTOSTATUS
        AutoStatus.getInstance().userActivity(Config.AWAY_IDLE);
//#endif
        canvas.pointerPressed(x, y);
    }

    protected void pointerDragged(int x, int y) {
        canvas.pointerDragged(x, y);
    }

    protected void pointerReleased(int x, int y) {
        canvas.pointerReleased(x, y);
    }

    //распознавание известных кодов клавиш
    //thanks to Vladimir Kryukov
    public static final int LEFT_SOFT  = 0x00100000;
    public static final int RIGHT_SOFT = 0x00100001;
    public static final int CLEAR_KEY  = 0x00100002;
    public static final int CLOSE_KEY  = 0x00100003;
    public static final int CALL_KEY   = 0x00100004;
    public static final int CAMERA_KEY = 0x00100005;
    public static final int ABC_KEY    = 0x00100006;
    public static final int VOLPLUS_KEY  = 0x00100007;
    public static final int VOLMINUS_KEY = 0x00100008;
    public static final int NAVIKEY_RIGHT = 0x00100009;
    public static final int NAVIKEY_LEFT  = 0x0010000A;
    public static final int NAVIKEY_UP    = 0x0010000B;
    public static final int NAVIKEY_DOWN  = 0x0010000C;
    public static final int NAVIKEY_FIRE  = 0x0010000D;
    public static final int UNUSED_KEY    = 0x0010000F; 

    private int getKey(int code) {
//#ifdef ANDROID
//#             if (-8 == code) {
//#                 return CLOSE_KEY;
//#             }
//#             else if (-84 == code) {
//#                 return CALL_KEY;
//#             }
//#             else if (-82 == code) {
//#                 return LEFT_SOFT;
//#             }
//#             else if (127 == code) {
//#                 return CLEAR_KEY;
//#             }
//#             else if (-24 == code) {
//#                 return VOLPLUS_KEY;
//#             }
//#             else if (-25 == code) {
//#                 return VOLMINUS_KEY;
//#             }
//#             else if (80 == code) {
//#                 return CAMERA_KEY;
//#         }
//#endif      
        String strCode = null;
        try {
            strCode = getKeyName(code);
            if (null != strCode) {
                strCode = strCode.replace('_', ' ').toLowerCase();
            }
        } catch(IllegalArgumentException e) {
        }

        if (null != strCode) {
            if ("soft1".equals(strCode)
                    || "soft 1".equals(strCode)
                    || "softkey 1".equals(strCode)
                    || strCode.startsWith("left soft")) {
                return LEFT_SOFT;
            }
            if ("soft2".equals(strCode)
                    || "soft 2".equals(strCode)
                    || "softkey 4".equals(strCode)
                    || strCode.startsWith("right soft")) {
                return RIGHT_SOFT;
            }
            if ("on/off".equals(strCode) || ("back").equals(strCode)) {
                return CLOSE_KEY;
            }
            if (("clear").equals(strCode)) {
                return CLEAR_KEY;
            }
//            if ("soft3".equals(strCode)) {
//                return MIDDLE_SOFT;
//            }
            if (("send").equals(strCode)) {
                return CALL_KEY;
            }
            if (("select").equals(strCode) || ("ok").equals(strCode)
                    || "fire".equals(strCode) || "navi-center".equals(strCode)
                    || "enter".equals(strCode)) {
                return NAVIKEY_FIRE;
            }
            if ("start".equals(strCode)) {
                return CALL_KEY;
            }
            if ("up".equals(strCode) || "navi-up".equals(strCode)
                    || "up arrow".equals(strCode)) {
                return NAVIKEY_UP;
            }
            if ("down".equals(strCode) || "navi-down".equals(strCode)
                    || "down arrow".equals(strCode)) {
                return NAVIKEY_DOWN;
            }
            if ("left".equals(strCode) || "navi-left".equals(strCode)
                    || "left arrow".equals(strCode) || "sideup".equals(strCode)) {
                return NAVIKEY_LEFT;
            }
            if ("right".equals(strCode) || "navi-right".equals(strCode)
                    || "right arrow".equals(strCode) || "sidedown".equals(strCode)) {
                return NAVIKEY_RIGHT;
            }
        }
        if(code == -6 || code == -21 || code == 21 || code == 105
                || code == -202 || code == 113 || code == 57345
                || code == 0xFFBD) {
            return LEFT_SOFT;
        }
        if (code == -7 || code == -22 || code == 22 || code == 106
                || code == -203 || code == 112 || code == 57346
                || code == 0xFFBB) {
            return RIGHT_SOFT;
        }
        if (-41 == code) { // Alcatel-OT-800/1.0
            return NAVIKEY_FIRE;
        }
        if (-5 == code) {
            return NAVIKEY_FIRE;
        }
        if (63557 == code) { // nokia e63
            return NAVIKEY_FIRE;
        }
        if (code == -8) {
            return CLEAR_KEY;
        }
        if ((-11 == code) || (-12 == code)) {
            return CLOSE_KEY;
        }
        if ((-26 == code) || (-24 == code)) {
            return CAMERA_KEY;
        }
        if (code == -10) {
            return CALL_KEY;
        }
        if (code == -50 || code == 1048582) {
            return ABC_KEY;
        }
        if (code == -36) {
            return VOLPLUS_KEY;
        }
        if (code == -37) {
            return VOLMINUS_KEY;
        }
        if ((code<KEY_NUM0 || code>KEY_NUM9) && code!=KEY_POUND && code!=KEY_STAR)
            try {// getGameAction can raise exception
                int action = getGameAction(code);
                switch (action) {
                    case Canvas.RIGHT: return NAVIKEY_RIGHT;
                    case Canvas.LEFT:  return NAVIKEY_LEFT;
                    case Canvas.UP:    return NAVIKEY_UP;
                    case Canvas.DOWN:  return NAVIKEY_DOWN;
                    case Canvas.FIRE:  return NAVIKEY_FIRE;
                }
            } catch(Exception e) {
            }
        switch (code) {
            // lat
            case 'm': return KEY_NUM0;
            case 'r': return KEY_NUM1;
            case 't': return KEY_NUM2;
            case 'y': return KEY_NUM3;
            case 'f': return KEY_NUM4;
            case 'g': return KEY_NUM5;
            case 'h': return KEY_NUM6;
            case 'v': return KEY_NUM7;
            case 'b': return KEY_NUM8;
            case 'n': return KEY_NUM9;
            case 'j': return KEY_POUND;
            case 'u': return KEY_STAR;
            // rus
            case 1100: return KEY_NUM0;
            case 1082: return KEY_NUM1;
            case 1077: return KEY_NUM2;
            case 1085: return KEY_NUM3;
            case 1072: return KEY_NUM4;
            case 1087: return KEY_NUM5;
            case 1088: return KEY_NUM6;
            case 1084: return KEY_NUM7;
            case 1080: return KEY_NUM8;
            case 1090: return KEY_NUM9;
            case 1086: return KEY_POUND;
            case 1075: return KEY_STAR;
        }
        return code;
    }
}
