/*
 * AutoTask.java 
 *
 * Created on 20.03.2008, 19:51
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
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
 * @author ad
 */

public class AutoTask extends DefForm implements Runnable {
    
    public static final int TASK_MAXNUMBER = 8;

    private static final int SLEEPTIME = 5000;

    public Vector taskList= new Vector(0);

    public AutoTask() {
        super(null);
        for( int ti= 0; ti<TASK_MAXNUMBER; ti++){
            taskList.addElement(new TaskElement());
        }
    }

    public void startTask() {
        new Thread(this).start();
    }

    public boolean checkTasks(){
        boolean hasWaitingTasks= true;
        for( int ti= 0; ti<TASK_MAXNUMBER; ti++){
            TaskElement taskelem= (TaskElement)taskList.elementAt(ti);
            hasWaitingTasks= true;
            if( taskelem.Type ==TaskElement.TASK_TYPE_DISABLED){
                taskelem.isRunned= false;
                hasWaitingTasks= false;
                continue;
            }// if
                        
            if( taskelem.Type ==TaskElement.TASK_TYPE_TIMER){
                if( (System.currentTimeMillis() -taskelem.StartMS) >taskelem.WaitMS){
//                    show( ti);
                    taskelem.StartMS= System.currentTimeMillis();
                    taskelem.isRunned= false;
                }
            }else if( taskelem.Type ==TaskElement.TASK_TYPE_TIME){
                if( Time.getHour() >=taskelem.Hour && Time.getMin() >=taskelem.Minute){
//                    show( ti);
                    taskelem.isRunned= false;
                }
            }// elif

            if( taskelem.Once ==true && taskelem.isRunned ==false){
                taskelem.Type= TaskElement.TASK_TYPE_DISABLED;
                hasWaitingTasks= false;
                continue;
            }

            setCaption( taskelem);
            doAction( taskelem);
//            destroyView();
        }// for ti
        return hasWaitingTasks;
    }// checkTasks()
    
    public void run(){
        while( checkTasks())
            try{
                Thread.sleep( SLEEPTIME);
            }catch( Exception e){ break;}
    }// run()
    
    public void doAction( TaskElement taskelem){
        switch( taskelem.Action){
            case TaskElement.TASK_ACTION_QUIT:
                BombusQD.getInstance().notifyDestroyed();
                break;
//#ifndef WMUC
            case TaskElement.TASK_ACTION_CONFERENCE_QUIT:
                BombusQD.sd.roster.leaveAllMUCs();//Tishka17
                break;
//#endif
            case TaskElement.TASK_ACTION_LOGOFF:
                BombusQD.sd.roster.logoff(SR.get(SR.MS_AUTOTASKS) + ": " + SR.get(SR.MS_LOGOFF));
                break;
           case TaskElement.TASK_ACTION_RECONNECT:
                //taskType=TASK_TYPE_TIMER;
                //initTime=System.currentTimeMillis();
                //startTask();
                BombusQD.sd.roster.connectionTerminated(new Exception(SR.get(SR.MS_AUTOTASKS) + ": " + SR.get(SR.MS_RECONNECT)));
                try{
                    Thread.sleep( SLEEPTIME);
                }catch( Exception e){ break;}
                BombusQD.sd.roster.logon(SR.get(SR.MS_AUTOTASKS) + ": " + SR.get(SR.MS_AUTOLOGIN));
                break;
            case TaskElement.TASK_ACTION_LOGIN:
                //initTime=System.currentTimeMillis();
                //startTask();
                BombusQD.sd.roster.logon(SR.get(SR.MS_AUTOTASKS) + ": " + SR.get(SR.MS_AUTOLOGIN));
                //BombusQD.sd.roster.logon(SR.get(SR.MS_AUTOTASKS) + ": " + "Login");
                break;
            case TaskElement.TASK_ACTION_CONFERENCE_JOIN:
                BombusQD.sd.roster.MUCsAutoJoin(SR.get(SR.MS_AUTOTASKS) + ": " + SR.get(SR.MS_DO_AUTOJOIN));
                //BombusQD.sd.roster.MUCsAutoJoin(SR.get(SR.MS_AUTOTASKS) + ": " + "Join");
                break;
        }
    }

    public void show() {
//        isShowing = true;
//        updateCaption( ti);
        super.show();
    }

    public void destroyView() {
//        isShowing = false;
        super.destroyView();
    }

    private void setCaption( TaskElement taskelem) {
        String caption = "";
        switch( taskelem.Action){
            case TaskElement.TASK_ACTION_QUIT:
                caption = SR.get(SR.MS_AUTOTASK_QUIT_BOMBUSMOD);
                break;
            case TaskElement.TASK_ACTION_CONFERENCE_QUIT:
                caption = SR.get(SR.MS_AUTOTASK_QUIT_CONFERENCES);
                break;
            case TaskElement.TASK_ACTION_LOGOFF:
                caption = SR.get(SR.MS_AUTOTASK_LOGOFF);
                break;
            case TaskElement.TASK_ACTION_RECONNECT:
                caption = SR.get(SR.MS_RECONNECT);
                break;
            case TaskElement.TASK_ACTION_LOGIN:
//            caption = SR.get(SR.MS_AUTOTASK_LOGIN);
                caption = SR.get(SR.MS_AUTOLOGIN);
                break;
            case TaskElement.TASK_ACTION_CONFERENCE_JOIN:
//            caption = SR.get(SR.MS_AUTOTASK_JOIN_CONFERENCES);
                caption = SR.get(SR.MS_DO_AUTOJOIN);
                break;
        }
//        caption += " [" + (WAITTIME-value) + "]";
        setMainBarItem(new MainBar(caption));
    }

    public void paint(Graphics g) {
            super.paint(g);

/*            updateCaption();

            int y = height / 2;            
            int itemWidth = width - (BORDER_WIDTH * 2);            
            int fillWidth = (itemWidth * value) / WAITTIME;

            g.setColor(ColorTheme.getColor(ColorTheme.PGS_REMAINED));
            g.fillRect(BORDER_WIDTH, y, itemWidth, PROGRESS_HEIGHT);

            g.setColor(ColorTheme.getColor(ColorTheme.PGS_COMPLETE_TOP));
            g.drawRect(BORDER_WIDTH, y, itemWidth, PROGRESS_HEIGHT);
            g.fillRect(BORDER_WIDTH, y, fillWidth, PROGRESS_HEIGHT);
 */
    }

}
//#endif