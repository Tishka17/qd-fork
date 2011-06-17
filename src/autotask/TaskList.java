/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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