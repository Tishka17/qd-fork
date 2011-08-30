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

import images.RosterIcons;
import io.NvStorage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import locale.SR;
import ui.IconTextElement;
import ui.VirtualCanvas;

import locale.SR;
import midlet.BombusQD;
import util.Time;
import ui.controls.AlertBox;
import client.Roster;
//#ifdef LIGHT_CONTROL
import light.CustomLight;
//#endif

/**
 *
 * @author Mars
 */

public class TaskElement extends IconTextElement {

    public final static String storage="atask_db";

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
    public int Notify = 0;
    //public boolean NotifyV = false;
    //public boolean NotifyL = false;
    //public boolean NotifyS = false;
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

    public TaskElement() {
        super(RosterIcons.getInstance());
    }

    public static TaskElement loadTaskElement(int index){
        TaskElement te=TaskElement.createFromStorage(index);
        return te;
    }

    public String toString(){
        //StringBuffer s=new StringBuffer();
        return Name;
    }

    public static TaskElement createFromStorage(int index) {
        TaskElement te=null;
        DataInputStream is=NvStorage.ReadFileRecord(storage, 0);
        if (is==null) return null;
        try {
            do {
                if (is.available()==0) {te=null; break;}
                te=createFromDataInputStream(is);
                index--;
            } while (index>-1);
            is.close();
        } catch (Exception e) { }
        return te;
    }

    public static TaskElement createFromDataInputStream(DataInputStream inputStream){
        TaskElement te=new TaskElement();
        try {
            te.Name = inputStream.readUTF();
            te.Type = inputStream.readInt();
            te.Action = inputStream.readInt();
            te.Text = inputStream.readUTF();
            te.Once = inputStream.readBoolean();
            te.Notify = inputStream.readInt();
            te.Hour = inputStream.readInt();
            te.Minute = inputStream.readInt();
            //te.NotifyL = inputStream.readBoolean();
            //te.NotifyS = inputStream.readBoolean();
            //te.NotifyV = inputStream.readBoolean();
            te.NotifyDelay = inputStream.readInt();
            te.StartMS = inputStream.readLong();
            te.WaitMS = inputStream.readLong();
        } catch (IOException e) { /*e.printStackTrace();*/ }

        return te; //(te.Type ==TASK_TYPE_DISABLED)?null:te;
    }

    public void saveToDataOutputStream(DataOutputStream outputStream){
        try {
            outputStream.writeUTF( Name);
            outputStream.writeInt( Type);
            outputStream.writeInt( Action);
            outputStream.writeUTF( Text);
            outputStream.writeBoolean( Once);
            outputStream.writeInt( Notify);
            outputStream.writeInt( Hour);
            outputStream.writeInt( Minute);
            //te.NotifyL = outputStream.writeBoolean();
            //te.NotifyS = outputStream.writeBoolean();
            //te.NotifyV = outputStream.writeBoolean();
            outputStream.writeInt( NotifyDelay);
            outputStream.writeLong( StartMS);
            outputStream.writeLong( WaitMS);
        } catch (IOException e) { }
    }

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
        return (Notify !=0);
    }

    public void Notify( boolean varg, boolean larg, boolean sarg){
        Notify= 0;
        Notify+= ((varg)?1:0);//<<0;
        Notify+= ((larg)?1:0)<<1;
        Notify+= ((sarg)?1:0)<<2;
    }

    public boolean NotifyV( ){
        return (( Notify & 1) !=0);
    }

    public void NotifyV( boolean narg){
        //NotifyV= narg;
    }

    public boolean NotifyL( ){
        return (( Notify & 2) !=0);
    }

    public void NotifyL( boolean narg){
        //NotifyL= narg;
    }

    public boolean NotifyS( ){
        return (( Notify & 4) !=0);
    }

    public void NotifyS( boolean narg){
        //NotifyS= narg;
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
        if( !Notify())
            return;
//#ifdef LIGHT_CONTROL
        if( NotifyL())
            CustomLight.startBlinking();
//#endif
        if( NotifyV())
                Roster.playNotify( Roster.SOUND_ATTENTION);
        else if( NotifyS())
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
