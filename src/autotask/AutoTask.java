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
import ui.Time;
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

    private static final int SLEEPTIME = 5000;
    private static final int WAITTIME = 60;

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
    
    boolean vibrate;

    private int value;

    public AutoTask() {
	super(null);
    }

    public void startTask() {
        new Thread(this).start();
    }
    
    public void run() {
        isRunning = true;
        while (isRunning) {
            if (taskType == TASK_TYPE_DISABLED){
                 isRunning = false;
            }
            try {
                Thread.sleep(SLEEPTIME);
            } catch (Exception e) { break; }
            
            if (taskType==TASK_TYPE_TIMER) {
                if ((System.currentTimeMillis()-initTime)>waitTime) {
                    show();
                    isRunning=false;
                    taskType=TASK_TYPE_DISABLED;
                }
            } else if (taskType==TASK_TYPE_TIME) {
                if (Time.getHour()==startHour && Time.getMin()==startMin ) {
                    show();
                    isRunning=false;
                    taskType=TASK_TYPE_DISABLED;
                }
            } else {
                 isRunning=false;
                 taskType=TASK_TYPE_DISABLED;
            }  
        }
        while (isShowing) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                break;
            }
            ++value;
            if (value >= WAITTIME) {
                doAction();
                destroyView();
                break;
            }
            redraw();
        }
    }
    
    public void doAction() {
        switch (taskAction) {
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
                taskType=TASK_TYPE_TIMER;
                initTime=System.currentTimeMillis();
                startTask();
                BombusQD.sd.roster.connectionTerminated(new Exception(
                        SR.get(SR.MS_AUTOTASKS) + ": " + SR.get(SR.MS_RECONNECT)));
                break;
        }
    }

    public void cmdOk() {
        doAction();
        destroyView();
    }

    public void show() {
        isShowing = true;
        updateCaption();
        super.show();
    }

    public void destroyView() {
        isShowing = false;
        super.destroyView();
    }

    private void updateCaption() {
        String caption = "";

        switch (taskAction) {
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
        }
        caption += " [" + (WAITTIME-value) + "]";
        setMainBarItem(new MainBar(caption));
    }

    public void paint(Graphics g) {
            super.paint(g);

            updateCaption();

            int y = height / 2;            
            int itemWidth = width - (BORDER_WIDTH * 2);            
            int fillWidth = (itemWidth * value) / WAITTIME;

            g.setColor(ColorTheme.getColor(ColorTheme.PGS_REMAINED));
            g.fillRect(BORDER_WIDTH, y, itemWidth, PROGRESS_HEIGHT);

            g.setColor(ColorTheme.getColor(ColorTheme.PGS_COMPLETE_TOP));
            g.drawRect(BORDER_WIDTH, y, itemWidth, PROGRESS_HEIGHT);
            g.fillRect(BORDER_WIDTH, y, fillWidth, PROGRESS_HEIGHT);
    }
}
//#endif