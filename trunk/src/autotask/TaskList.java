/*
 * TaskList.java
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

import java.util.Vector;

/**
 *
 * @author Mars
 */

public class TaskList{

    public static Vector taskList= new Vector(0);

    public static Vector TaskList(){
        if( taskList.size() ==0){
            taskList.addElement( new TaskElement());
/*            ((TaskElement)taskList.elementAt( 0)).Name( "Task 0");
            taskList.addElement( new TaskElement());
            ((TaskElement)taskList.elementAt( 1)).Name( "Task 1");
            taskList.addElement( new TaskElement());
            ((TaskElement)taskList.elementAt( 2)).Name( "Task 2");
*/
        }
        return taskList;
    }// TaskList()

    public static void CreateTask( int taskIndex){
        if( taskIndex >=0)
            taskList.insertElementAt( new TaskElement(), taskIndex);
            //taskList.addElement(new TaskElement());
    }

    public static void DeleteTask( int taskIndex){
        if( taskList.size() <2)
            return;
        taskList.removeElementAt( taskIndex);
        //taskList.removeElementAt( taskList.indexOf( taskList.lastElement()));
    }

    public static boolean checkTasks( ){
        boolean hasWaitingTasks= false;
        for( int ti= 0; ti <taskList.size(); ti++){
            if( ((TaskElement)taskList.elementAt( ti)).doTask())
                 hasWaitingTasks= true;
        }// for ti
        return hasWaitingTasks;
    }// checkTasks()
}