/*
 * TaskElement.java
 *
 * Copyright (c) 2011, Mars
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

package autotask;

import locale.SR;
import midlet.BombusQD;
import util.Time;
import ui.controls.AlertBox;
import alert.AlertCustomize;
import client.Roster;
import ui.EventNotify;
//#ifdef LIGHT_CONTROL
import light.CustomLight;
//#endif

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

    public final static int TASK_NOTIFY_OFF = 0;
    public final static int TASK_NOTIFY_ON = 1;
    public final static int TASK_NOTIFY_VIBRO = 1;
    public final static int TASK_NOTIFY_LIGHT = 2;
    public final static int TASK_NOTIFY_SOUND = 3;

    public int Type= TASK_TYPE_DISABLED;
    public int Action = TASK_ACTION_QUIT;
    public boolean Notify = false;
    public boolean NotifyV = false;
    public boolean NotifyL = false;
    public boolean NotifyS = false;
    public long StartMS = 0;
    public long WaitMS = 0;
    public int Hour = 0;
    public int Minute = 0;
    public int NotifyDelay = 0;
    public boolean Once = true;
    public boolean isRunned = false;
    public String Name= "Имя по умолчанию";
//    public String Name= SR.get(SR.MS_AUTOTASK_DEFAULTNAME);
    public String Text= "Текст по умолчанию";
//    public String Name= SR.get(SR.MS_AUTOTASK_DEFAULTTEXT);

    public int NotifyDelay( ){
        return NotifyDelay;
    }

    public void NotifyDelay( int narg){
        NotifyDelay= narg;
    }

    public int Type( ){
        return Type;
    }

    public void Type( int targ){
        Type= targ;
    }

    public boolean Notify( ){
        return Notify;
    }

    public void Notify( boolean narg){
        Notify= narg;
    }

    public boolean NotifyV( ){
        return NotifyV;
    }

    public void NotifyV( boolean narg){
        NotifyV= narg;
    }

    public boolean NotifyL( ){
        return NotifyL;
    }

    public void NotifyL( boolean narg){
        NotifyL= narg;
    }

    public boolean NotifyS( ){
        return NotifyS;
    }

    public void NotifyS( boolean narg){
        NotifyS= narg;
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
                if( (System.currentTimeMillis() -StartMS) >(WaitMS -60000*NotifyDelay)){
                    doNotify();
                }
                if( (System.currentTimeMillis() -StartMS) >WaitMS){
                    StartMS= System.currentTimeMillis();
                    isRunned= false;
                    doAction( );
                    return false;
                }// if
                break;
            case TASK_TYPE_TIME:
                if( (Time.getHour()*60 +Time.getMin()) ==(Hour*60 +Minute -NotifyDelay)){
                    doNotify();
                }
                if( Time.getHour() ==Hour && Time.getMin() ==Minute){
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

    public void doNotify( ){
        if( !Notify)
            return;
//#ifdef LIGHT_CONTROL
        if( NotifyL)
            CustomLight.startBlinking();
//#endif
        if( NotifyV)
                Roster.playNotify( Roster.SOUND_ATTENTION);
        else if( NotifyS)
            Roster.playNotify( Roster.SOUND_MESSAGE);
    }// doNotify()

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
//                caption= SR.get(SR.MS_AUTOTASK_JOIN_CONFERENCES);
                box= new AlertBox(caption, Name, AlertBox.BUTTONS_OK, 5);
                box.show();
                BombusQD.sd.roster.MUCsAutoJoin(SR.get(SR.MS_AUTOTASKS) + ": " + SR.get(SR.MS_DO_AUTOJOIN));
                break;
            case TASK_ACTION_REMINDER:
                caption = SR.get(SR.MS_DO_AUTOJOIN);
//                caption= SR.get(SR.MS_AUTOTASK_REMINDER);
                box= new AlertBox( Name, Text, AlertBox.BUTTONS_OK, 0);
                box.show();
                //BombusQD.sd.roster.MUCsAutoJoin(SR.get(SR.MS_AUTOTASKS) + ": " + SR.get(SR.MS_DO_AUTOJOIN));
                break;
        }// switch
    }// doAction()
}