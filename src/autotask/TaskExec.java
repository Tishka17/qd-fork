/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
//#ifdef AUTOTASK
package autotask;

import ui.keys.UserActions;
import client.Roster;
//#ifdef LIGHT_CONTROL
import light.CustomLight;
//#endif

/**
 *
 * @author Mars
 */
public class TaskExec {

    public TaskExec(){

    }

    public static void doNotify( TaskElement te){
        if( !te.Notify())
            return;
//#ifdef LIGHT_CONTROL
        if( te.NotifyL())
            CustomLight.startBlinking();
//#endif
        if( te.NotifyV())
            ;
            //Roster.playNotify( Roster.SOUND_ATTENTION);
        else if( te.NotifyS())
            ;
            //Roster.playNotify( Roster.SOUND_MESSAGE);
            // уведомления получаются бесконечные....
    }// doNotify()

    public static void doAction( TaskElement te){
        UserActions.doActionByExtIndex( UserActions.UA_TASKS, te.Action, te.Text);
    }// doAction()

}
//#endif