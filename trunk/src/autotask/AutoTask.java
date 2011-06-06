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
import ui.controls.form.DefForm;

/**
 *
 * @author ad
 */

public class AutoTask extends DefForm implements Runnable {
    public final int TASK_TYPE_DISABLED = 0;
    public final int TASK_TYPE_TIME = 1;
    public final int TASK_TYPE_TIMER = 2;

    public final int TASK_ACTION_QUIT = 0;
    public final int TASK_ACTION_CONFERENCE_QUIT = 1;
    public final int TASK_ACTION_LOGOFF = 2;
    public final int TASK_ACTION_RECONNECT = 3;
    public final int TASK_ACTION_LOGIN = 4;
    public final int TASK_ACTION_CONFERENCE_JOIN = 5;
    public final int TASK_ACTION_MAXNUMBER = TASK_ACTION_CONFERENCE_JOIN +1;

    public class TaskArray {
        public int Type = TASK_TYPE_DISABLED;
        public int Action = TASK_ACTION_QUIT;
        public long StartMS = 0;
        public int WaitMS = 0;
        public int Hour = 0;
        public int Minute = 0;
        public boolean Once = true;
        public boolean isRunned = false;
    }

    public TaskArray taskArr[]= new TaskArray[TASK_ACTION_MAXNUMBER];
//    public int taskCurrent;

    private static final int SLEEPTIME = 5000;

/*    private static final int WAITTIME = 5;
    private static final int PROGRESS_HEIGHT = 5;
    private static final int BORDER_WIDTH = 10;
    public int taskType = TASK_TYPE_DISABLED;
    public int taskAction = TASK_ACTION_QUIT;
    
    public long initTime = System.currentTimeMillis();
    public int waitTime = 3600000;
    public int startHour = 0;
    public int startMin = 0;

    boolean isRunning;
    boolean isShowing;

    private int value;
*/
    public AutoTask() {
        super(null);
    }

    public void startTask() {
        new Thread(this).start();
    }

    public boolean checkTasks(){
        boolean hasWaitingTasks= true;
        for( int ti= TASK_ACTION_QUIT; ti<TASK_ACTION_MAXNUMBER; ti++){
            hasWaitingTasks= true;
            if( taskArr[ti].Type ==TASK_TYPE_DISABLED){
                taskArr[ti].isRunned= false;
                hasWaitingTasks= false;
                continue;
            }// if
                        
            if( taskArr[ti].Type ==TASK_TYPE_TIMER){
                if( (System.currentTimeMillis() -taskArr[ti].StartMS) >taskArr[ti].WaitMS){
//                    show( ti);
                    taskArr[ti].StartMS= System.currentTimeMillis();
                    taskArr[ti].isRunned= false;
                }
            }else if( taskArr[ti].Type ==TASK_TYPE_TIME){
                if( Time.getHour() >=taskArr[ti].Hour && Time.getMin() >=taskArr[ti].Minute){
//                    show( ti);
                    taskArr[ti].isRunned= false;
                }
            }// elif

            if( taskArr[ti].Once ==true && taskArr[ti].isRunned ==false){
                taskArr[ti].Type= TASK_TYPE_DISABLED;
                hasWaitingTasks= false;
                continue;
            }

            setCaption( ti);
            doAction( ti);
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
    
    public void doAction( int ti){
        switch( taskArr[ti].Action){
            case TASK_ACTION_QUIT:
                BombusQD.getInstance().notifyDestroyed();
                break;
//#ifndef WMUC
            case TASK_ACTION_CONFERENCE_QUIT:
                BombusQD.sd.roster.leaveAllMUCs();//Tishka17
                break;
//#endif
            case TASK_ACTION_LOGOFF:
                BombusQD.sd.roster.logoff(SR.get(SR.MS_AUTOTASKS) + ": " + SR.get(SR.MS_LOGOFF));
                break;
           case TASK_ACTION_RECONNECT:
                //taskType=TASK_TYPE_TIMER;
                //initTime=System.currentTimeMillis();
                //startTask();
                BombusQD.sd.roster.connectionTerminated(new Exception(SR.get(SR.MS_AUTOTASKS) + ": " + SR.get(SR.MS_RECONNECT)));
                try{
                    Thread.sleep( SLEEPTIME);
                }catch( Exception e){ break;}
                BombusQD.sd.roster.logon(SR.get(SR.MS_AUTOTASKS) + ": " + SR.get(SR.MS_AUTOLOGIN));
                break;
            case TASK_ACTION_LOGIN: 
                //initTime=System.currentTimeMillis();
                //startTask();
                BombusQD.sd.roster.logon(SR.get(SR.MS_AUTOTASKS) + ": " + SR.get(SR.MS_AUTOLOGIN));
                //BombusQD.sd.roster.logon(SR.get(SR.MS_AUTOTASKS) + ": " + "Login");
                break;
            case TASK_ACTION_CONFERENCE_JOIN:
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

    private void setCaption( int ti) {
        String caption = "";
        switch( taskArr[ti].Action){
            case TASK_ACTION_QUIT:
                caption = SR.get(SR.MS_AUTOTASK_QUIT_BOMBUSMOD);
                break;
            case TASK_ACTION_CONFERENCE_QUIT:
                caption = SR.get(SR.MS_AUTOTASK_QUIT_CONFERENCES);
                break;
            case TASK_ACTION_LOGOFF:
                caption = SR.get(SR.MS_AUTOTASK_LOGOFF);
                break;
            case TASK_ACTION_RECONNECT:
                caption = SR.get(SR.MS_RECONNECT);
                break;
            case TASK_ACTION_LOGIN:
//            caption = SR.get(SR.MS_AUTOTASK_LOGIN);
                caption = SR.get(SR.MS_AUTOLOGIN);
                break;
            case TASK_ACTION_CONFERENCE_JOIN:
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