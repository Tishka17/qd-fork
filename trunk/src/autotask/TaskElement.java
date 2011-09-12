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

//#ifdef AUTOTASK
package autotask;

import images.RosterIcons;
import io.NvStorage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import ui.IconTextElement;
import util.Time;

/**
 *
 * @author Mars
 */

public class TaskElement extends IconTextElement {

    public final static String storage="atask_db";

    public final static int TASK_TYPE_DISABLED = 0;
    public final static int TASK_TYPE_TIME = 1;
    public final static int TASK_TYPE_TIMER = 2;

    public final static int TASK_ACTION_REMINDER = 6;

    public final static int TASK_NOTIFY_VIBRO = 1;
    public final static int TASK_NOTIFY_LIGHT = 2;
    public final static int TASK_NOTIFY_SOUND = 4;

    public int Type= TASK_TYPE_DISABLED;
    public int Action = 0;
    public int Notify = 0;
    public int WaitMin = 0;
        // обратный отсчет таймера в мин
    public int Timer = 0;
        // период таймера в мин
    public int Hour = 0;
    public int Minute = 0;
    public int NotifyD = 0;
        // упреждение уведомления в мин
    public boolean Once = true;

    public boolean isRunned = false;
        // если ИСТИНА - задача запущена и ЖДЕТ исполнения
    public boolean isNotified = false; 
        // если ИСТИНА - задача ВЫПОЛНИЛА уведомление

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
        if( isRunned) return "~ "+Name;
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
            te.NotifyD = inputStream.readInt();
            te.WaitMin = inputStream.readInt();
            te.Timer = inputStream.readInt();
        } catch (IOException e) {
            /*e.printStackTrace();*/
            te= null;
        }
        return te;
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
            outputStream.writeInt( NotifyD);
            outputStream.writeInt( WaitMin);
            outputStream.writeInt( Timer);
        } catch (IOException e) { }
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
        return (( Notify & TASK_NOTIFY_VIBRO) !=0);
    }

    public boolean NotifyL( ){
        return (( Notify & TASK_NOTIFY_LIGHT) !=0);
    }

    public boolean NotifyS( ){
        return (( Notify & TASK_NOTIFY_SOUND) !=0);
    }

    public void setTime( int harg, int marg){
        Hour= harg;
        Minute= marg;
    }

    public void setTimer( int marg){
        Timer= marg;
        WaitMin= Timer;
    }

    public boolean doTask( ){
    // собственно проверка условий выполнения/уведомления задач
    // вызывается из потока АвтоТаск.ран() через метод ТаскЛист.ЧекТаскс()
    // ОДИН раз в минуту точно в ее начале... теоретически...
    // возврат истины указывает АвтоТаск.ран(), что эта задача еще должна
    // быть выполнена, и завершаться потоку рано...
        switch( Type){
            case TASK_TYPE_DISABLED:
                isRunned= false;
                return false;
                //break;
            case TASK_TYPE_TIMER:
                WaitMin--;
                if( isRunned && WaitMin ==NotifyD){
                    TaskExec.doNotify( this);
                }
                if( WaitMin ==0){
                    TaskExec.doAction( this);
                    WaitMin= Timer;
                }// if
                break;
            case TASK_TYPE_TIME:
                if( isRunned && (Time.getHour()*60 +Time.getMin()) ==(Hour*60 +Minute -NotifyD)){
                    TaskExec.doNotify( this);
                }
                if( Time.getHour() ==Hour && Time.getMin() ==Minute){
                    TaskExec.doAction( this);
                }// if
                break;
        }// switch
        if( Once && !isRunned){
        // если задача одноразовая и не ждет - отключаем ее
            Type= TASK_TYPE_DISABLED;
            return false;
        }// if
        if( !Once){
        // если не одноразовая - поддерживаем ее в режиме ожидания
            isNotified= false;
            isRunned= true;
        }// if
        return true;
    }// doTask()
}
//#endif
