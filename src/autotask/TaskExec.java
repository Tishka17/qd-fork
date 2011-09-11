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
import ui.controls.AlertBox;

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
        if( te.isNotified)
            return;
       new AlertBox( te.toString(), te.Text, AlertBox.BUTTONS_OK, 0).show();
 //#ifdef LIGHT_CONTROL
        if( te.NotifyL())
            CustomLight.startBlinking();
//#endif
        if( te.NotifyV())
            Roster.playNotify( Roster.SOUND_ATTENTION);
        else if( te.NotifyS())         
            Roster.playNotify( Roster.SOUND_MESSAGE);

        te.isNotified= true;
    }// doNotify()

    public static void doAction( TaskElement te){
        if( te.Action ==0){
        // напоминалка
            new AlertBox( te.toString(), te.Text, AlertBox.BUTTONS_OK, 0).show();
        }else{
            UserActions.doActionByExtIndex( UserActions.UA_TASKS, te.Action, te.Text);
        }// ifel
        te.isRunned= false; // выполнили задание - очистили флаг
    }// doAction()

}
//#endif