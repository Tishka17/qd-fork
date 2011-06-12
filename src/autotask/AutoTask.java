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

/* * * * * * * * * * *
 * @author ad        *
 * Revrited by Mars  *
 * * * * * * * * * * */

public class AutoTask extends DefForm implements Runnable {
    
    public static final int TASK_MAXNUMBER = 8;

    private static final int SLEEPTIME = 5000;

    Vector taskList= null;

    public AutoTask() {
        super( null);
        if( taskList ==null){
            taskList= new Vector(0);
            taskList.addElement(new TaskElement());
//            ((TaskElement)taskList.elementAt(0)).Name( "Task 0");
/*            taskList.addElement(new TaskElement());
            ((TaskElement)taskList.elementAt(1)).Name( "Task 1");
            taskList.addElement(new TaskElement());
            ((TaskElement)taskList.elementAt(2)).Name( "Task 2");
*/
        }
    }

    public void startTask(){
        new Thread(this).start();
    }

    public void CreateTask( int taskIndex){
        if( taskIndex >=0)
            //taskList.insertElementAt(new TaskElement(), taskIndex);
            taskList.addElement(new TaskElement());
    }

    public void DeleteTask( int taskIndex){
        if( taskList.size() <=1)
            return;
        //taskList.removeElementAt( taskIndex);
        taskList.removeElementAt( taskList.indexOf( taskList.lastElement()));
    }

    public boolean checkTasks(){
        boolean hasWaitingTasks= false;
        for( int ti= 0; ti <taskList.size(); ti++){
            if( ((TaskElement)taskList.elementAt(ti)).doTask())
                 hasWaitingTasks= true;
        }// for ti
        return hasWaitingTasks;
    }// checkTasks()
    
    public void run(){
        while( checkTasks())
            try{
                Thread.sleep( SLEEPTIME);
            }catch( Exception e){ 
                break;
            }
    }// run()
}
//#endif