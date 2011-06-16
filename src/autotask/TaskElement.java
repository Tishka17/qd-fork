/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package autotask;

import colors.ColorTheme;
import javax.microedition.lcdui.Graphics;
import locale.SR;
import midlet.BombusQD;
import ui.MainBar;
import util.Time;
import java.util.Vector;
import ui.controls.form.DefForm;
import ui.controls.AlertBox;

/**
 *
 * @author Mars
 */

public class TaskElement {
    public final static int TASK_TYPE_DISABLED = 0;
    public final static int TASK_TYPE_TIME = 1;
    public final static int TASK_TYPE_TIMER = 2;
    public final static int TASK_TYPE_CREATE = 3;
    public final static int TASK_TYPE_DELETE = 4;
    public final static int TASK_TYPE_RENAME = 5;

    public final static int TASK_ACTION_QUIT = 0;
    public final static int TASK_ACTION_CONFERENCE_QUIT = 1;
    public final static int TASK_ACTION_LOGOFF = 2;
    public final static int TASK_ACTION_RECONNECT = 3;
    public final static int TASK_ACTION_LOGIN = 4;
    public final static int TASK_ACTION_CONFERENCE_JOIN = 5;
    public final static int TASK_ACTION_REMINDER = 6;

    public int Type= TASK_TYPE_DISABLED;
    public int Action = TASK_ACTION_QUIT;
    public long StartMS = 0;
    public long WaitMS = 0;
    public int Hour = 0;
    public int Minute = 0;
    public boolean Once = true;
    public boolean isRunned = false;
    public String Name= "Default task name";
    public String Text= "Default reminder text";

    public int Type( ){
        return Type;
    }

    public void Type( int targ){
        Type= targ;
    }

    public String Name( ){
        return Name;
    }

    public void Name( String narg){
        Name= narg;
    }

    public String Text( ){
        return Text;
    }

    public void Text( String sarg){
        Text= sarg;
    }

    public int Action( ){
        return Action;
    }

    public void Action( int aarg){
        Action= aarg;
    }

    public boolean Once( ){
        return Once;
    }

    public void Once( boolean oarg){
        Once= oarg;
    }

    public boolean isRunned( ){
        return isRunned;
    }

    public void setRunned( boolean rarg){
        isRunned= rarg;
    }

    public int Hour( ){
        return Hour;
    }

    public int Minute( ){
        return Minute;
    }

    public void setTime( int harg, int marg){
        Hour= harg;
        Minute= marg;
    }

    public int Timer( ){
        return (int)(WaitMS /60000);
    }

    public void setTimer( int marg){
        StartMS= System.currentTimeMillis();
        WaitMS= marg *60000;
    }

    public boolean doTask( ){
        switch( Type){
            case TASK_TYPE_DISABLED:
                isRunned= false;
                return false;
                //break;
            case TASK_TYPE_TIMER:
                if( (System.currentTimeMillis() -StartMS) >=WaitMS){
                    StartMS= System.currentTimeMillis();
                    isRunned= false;
                    doAction( );
                    return false;
                }// if
                break;
            case TASK_TYPE_TIME:
                if( Time.getHour() == Hour && Time.getMin() == Minute){
                    isRunned= false;
                    doAction( );
                    return false;
                }// if
                break;
        }// switch
        if( Once && !isRunned){
            Type= TASK_TYPE_DISABLED;
            return false;
        }// if
        return true;
    }// doTask()

    public void doAction( ){
        String caption= "";
        AlertBox box;
        switch( Action){
            case TASK_ACTION_QUIT:
                caption = SR.get(SR.MS_AUTOTASK_QUIT_BOMBUSMOD);
                box= new AlertBox(caption, Name, AlertBox.BUTTONS_OK, 5);
                box.show();
                BombusQD.getInstance().notifyDestroyed();
                break;
            case TASK_ACTION_CONFERENCE_QUIT:
                caption = SR.get(SR.MS_AUTOTASK_QUIT_CONFERENCES);
                box= new AlertBox(caption, Name, AlertBox.BUTTONS_OK, 5);
                box.show();
                BombusQD.sd.roster.leaveAllMUCs();//Tishka17
                break;
            case TASK_ACTION_LOGOFF:
                caption = SR.get(SR.MS_AUTOTASK_LOGOFF);
                box= new AlertBox(caption, Name, AlertBox.BUTTONS_OK, 5);
                box.show();
                BombusQD.sd.roster.logoff(SR.get(SR.MS_AUTOTASKS) + ": " + SR.get(SR.MS_LOGOFF));
                break;
           case TASK_ACTION_RECONNECT:
                caption = SR.get(SR.MS_RECONNECT);
                box= new AlertBox(caption, Name, AlertBox.BUTTONS_OK, 5);
                box.show();
                try{
                    BombusQD.sd.roster.connectionTerminated(new Exception(SR.get(SR.MS_AUTOTASKS) + ": " + SR.get(SR.MS_RECONNECT)));
                    Thread.sleep( 1000);
                }catch( Exception e){ break;}
                BombusQD.sd.roster.logon(SR.get(SR.MS_AUTOTASKS) + ": " + SR.get(SR.MS_AUTOLOGIN));
                break;
            case TASK_ACTION_LOGIN:
                caption = SR.get(SR.MS_AUTOLOGIN);
                box= new AlertBox(caption, Name, AlertBox.BUTTONS_OK, 5);
                box.show();
                BombusQD.sd.roster.logon(SR.get(SR.MS_AUTOTASKS) + ": " + SR.get(SR.MS_AUTOLOGIN));
                break;
            case TASK_ACTION_CONFERENCE_JOIN:
                caption = SR.get(SR.MS_DO_AUTOJOIN);
                box= new AlertBox(caption, Name, AlertBox.BUTTONS_OK, 5);
                box.show();
                BombusQD.sd.roster.MUCsAutoJoin(SR.get(SR.MS_AUTOTASKS) + ": " + SR.get(SR.MS_DO_AUTOJOIN));
                break;
            case TASK_ACTION_REMINDER:
                caption = SR.get(SR.MS_DO_AUTOJOIN);
                box= new AlertBox(caption, Name, AlertBox.BUTTONS_OK, 5);
                box.show();
                BombusQD.sd.roster.MUCsAutoJoin(SR.get(SR.MS_AUTOTASKS) + ": " + SR.get(SR.MS_DO_AUTOJOIN));
                break;
        }// switch
    }// doAction()
}