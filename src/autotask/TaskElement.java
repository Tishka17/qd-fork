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

/**
 *
 * @author Mars
 */

public class TaskElement {
    public final static int TASK_TYPE_DISABLED = 0;
    public final static int TASK_TYPE_TIME = 1;
    public final static int TASK_TYPE_TIMER = 2;

    public final static int TASK_ACTION_QUIT = 0;
    public final static int TASK_ACTION_CONFERENCE_QUIT = 1;
    public final static int TASK_ACTION_LOGOFF = 2;
    public final static int TASK_ACTION_RECONNECT = 3;
    public final static int TASK_ACTION_LOGIN = 4;
    public final static int TASK_ACTION_CONFERENCE_JOIN = 5;

    public int Type= TASK_TYPE_DISABLED;
    public int Action = TASK_ACTION_QUIT;
    public long StartMS = 0;
    public long WaitMS = 0;
    public int Hour = 0;
    public int Minute = 0;
    public boolean Once = true;
    public boolean isRunned = false;

    public int Type( ){
        return Type;
    }

    public void Type( int targ){
        Type= targ;
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
        if( Type ==TASK_TYPE_DISABLED){
            isRunned= false;
            return false;
        }// if
        if( Type ==TASK_TYPE_TIMER){
            if( (System.currentTimeMillis() -StartMS) >=WaitMS){
                StartMS= System.currentTimeMillis();
                isRunned= false;
                doAction( );
                return false;
            }
        }else if( Type ==TASK_TYPE_TIME){
            if( Time.getHour() ==Hour && Time.getMin() ==Minute){
                    isRunned= false;
                    doAction( );
                    return false;
            }
        }// elif
        if( Once && !isRunned){
            Type= TASK_TYPE_DISABLED;
            return false;
        }// if
        return true;
    }// doTask()

    public void doAction( ){
        String caption= "";
        switch( Action){
            case TASK_ACTION_QUIT:
                caption = SR.get(SR.MS_AUTOTASK_QUIT_BOMBUSMOD);
                //setMainBarItem(new MainBar(caption));
                BombusQD.getInstance().notifyDestroyed();
                break;
            case TASK_ACTION_CONFERENCE_QUIT:
                caption = SR.get(SR.MS_AUTOTASK_QUIT_CONFERENCES);
                //setMainBarItem(new MainBar(caption));
                BombusQD.sd.roster.leaveAllMUCs();//Tishka17
                break;
            case TASK_ACTION_LOGOFF:
                caption = SR.get(SR.MS_AUTOTASK_LOGOFF);
                //setMainBarItem(new MainBar(caption));
                BombusQD.sd.roster.logoff(SR.get(SR.MS_AUTOTASKS) + ": " + SR.get(SR.MS_LOGOFF));
                break;
           case TASK_ACTION_RECONNECT:
                caption = SR.get(SR.MS_RECONNECT);
                //setMainBarItem(new MainBar(caption));
                BombusQD.sd.roster.connectionTerminated(new Exception(SR.get(SR.MS_AUTOTASKS) + ": " + SR.get(SR.MS_RECONNECT)));
                try{
                    Thread.sleep( 1000);
                }catch( Exception e){ break;}
                BombusQD.sd.roster.logon(SR.get(SR.MS_AUTOTASKS) + ": " + SR.get(SR.MS_AUTOLOGIN));
                break;
            case TASK_ACTION_LOGIN:
                caption = SR.get(SR.MS_AUTOLOGIN);
                //setMainBarItem(new MainBar(caption));
                BombusQD.sd.roster.logon(SR.get(SR.MS_AUTOTASKS) + ": " + SR.get(SR.MS_AUTOLOGIN));
                break;
            case TASK_ACTION_CONFERENCE_JOIN:
                caption = SR.get(SR.MS_DO_AUTOJOIN);
                //setMainBarItem(new MainBar(caption));
                BombusQD.sd.roster.MUCsAutoJoin(SR.get(SR.MS_AUTOTASKS) + ": " + SR.get(SR.MS_DO_AUTOJOIN));
                break;
        }// switch
    }// doAction()
}