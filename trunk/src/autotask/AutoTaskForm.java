/*
 * AutoTaskForm.java 
 *
 * Created on 20.03.2008, 19:52
 *
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

//#if AUTOTASK
package autotask;

import javax.microedition.lcdui.TextField;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.NumberInput;
import ui.controls.form.TextInput;
import ui.controls.form.SimpleString;
import ui.controls.form.CheckBox;
import ui.controls.form.DefForm;
import locale.SR;
import ui.IconTextElement;
import images.MenuIcons;

/**
 *
 * @author ad
 */

public class AutoTaskForm extends DefForm {

    private DropChoiceBox taskNumber;
    private DropChoiceBox taskType;
    private DropChoiceBox actionType;

    private SimpleString autoTaskTimeDesc;

    private NumberInput autoTaskDelay;
    private NumberInput autoTaskMin;
    private NumberInput autoTaskHour;
    private TextInput autoTaskText;
    private CheckBox autoTaskOnce;

    private AutoTask at;

    private int taskIndex;
    private int typeIndex;
    private int actionIndex;

    int h,m,t,a,i;

    public AutoTaskForm() {
        super(SR.get(SR.MS_AUTOTASKS));
        if( at ==null){
            at= new AutoTask();
            taskIndex= 0;
            typeIndex= 0;
            actionIndex= 0;
        }// if

        taskNumber= new DropChoiceBox("Task name");
        for( int ti= 0; ti <at.taskList.size(); ti++)
            taskNumber.append( ((TaskElement)at.taskList.elementAt(ti)).Name());

        autoTaskOnce= new CheckBox( "Run once", ((TaskElement)at.taskList.elementAt(taskIndex)).Once());

        taskType= new DropChoiceBox(SR.get(SR.MS_AUTOTASK_TYPE));
        taskType.append(SR.get(SR.MS_DISABLED));
        taskType.append(SR.get(SR.MS_BY_TIME_));
        taskType.append(SR.get(SR.MS_BY_TIMER_));
//        taskType.append(SR.get(SR.MS_BY_TIME_));
        taskType.append( "Create Task");
//        taskType.append(SR.get(SR.MS_BY_TIMER_));
        taskType.append( "Delete Task");
//        taskType.append(SR.get(SR.MS_BY_TIMER_));
        taskType.append( "Rename Task");

        actionType=new DropChoiceBox(SR.get(SR.MS_AUTOTASK_ACTION_TYPE));
        actionType.append(new IconTextElement(SR.get(SR.MS_AUTOTASK_QUIT_BOMBUSMOD),MenuIcons.getInstance(),MenuIcons.ICON_QUIT));
        actionType.append(new IconTextElement(SR.get(SR.MS_AUTOTASK_QUIT_CONFERENCES),MenuIcons.getInstance(),MenuIcons.ICON_CONFERENCE));
        actionType.append(new IconTextElement(SR.get(SR.MS_AUTOTASK_LOGOFF),MenuIcons.getInstance(),MenuIcons.ICON_QUIT));
        actionType.append(new IconTextElement(SR.get(SR.MS_BREAK_CONECTION),MenuIcons.getInstance(),MenuIcons.ICON_RECONNECT));
//        actionType.append(SR.get(SR.MS_AUTOTASK_LOGIN));
        actionType.append(new IconTextElement(SR.get(SR.MS_AUTOLOGIN),MenuIcons.getInstance(),MenuIcons.ICON_QUIT));
//        actionType.append(SR.get(SR.MS_AUTOTASK_JOIN_CONFERENCES));
        actionType.append(new IconTextElement(SR.get(SR.MS_DO_AUTOJOIN),MenuIcons.getInstance(),MenuIcons.ICON_CONFERENCE));
//        actionType.append(SR.get(SR.MS_AUTOTASK_REMINDER));
        actionType.append(new IconTextElement( "Reminder" ,MenuIcons.getInstance(),MenuIcons.ICON_CONFERENCE));

        autoTaskTimeDesc=new SimpleString(SR.get(SR.MS_AUTOTASK_TIME), true);

        autoTaskHour=new NumberInput(SR.get(SR.MS_AUTOTASK_HOUR), ((TaskElement)at.taskList.elementAt(taskIndex)).Hour(), 0, 23);
        autoTaskMin=new NumberInput(SR.get(SR.MS_AUTOTASK_MIN), ((TaskElement)at.taskList.elementAt(taskIndex)).Minute(), 0, 59);
        autoTaskDelay=new NumberInput(SR.get(SR.MS_AUTOTASK_DELAY), ((TaskElement)at.taskList.elementAt(taskIndex)).Timer(), 1, 600);

        autoTaskText=new TextInput( "Reminder text", ((TaskElement)at.taskList.elementAt(taskIndex)).Text(), TextField.ANY);

        actionIndex= ((TaskElement)at.taskList.elementAt(taskIndex)).Action();
        typeIndex= ((TaskElement)at.taskList.elementAt(taskIndex)).Type();

        taskNumber.setSelectedIndex(taskIndex);
        taskType.setSelectedIndex(typeIndex);
        actionType.setSelectedIndex(actionIndex);

        itemsList.addElement(taskNumber);
        itemsList.addElement(autoTaskOnce);
        itemsList.addElement(taskType);
        itemsList.addElement(actionType);

/*        h= ((TaskElement)at.taskList.elementAt(taskIndex)).Hour();
        m= ((TaskElement)at.taskList.elementAt(taskIndex)).Minute();
        t= ((TaskElement)at.taskList.elementAt(taskIndex)).Timer();
*/
        update();
    }

    public void cmdOk( ){
        if( typeIndex ==TaskElement.TASK_TYPE_CREATE){
            typeIndex= TaskElement.TASK_TYPE_DISABLED;
            at.CreateTask( taskIndex);
            taskNumber.append( ((TaskElement)at.taskList.lastElement()).Name());
            taskIndex++;

            actionIndex= ((TaskElement)at.taskList.elementAt(taskIndex)).Action();
            typeIndex= ((TaskElement)at.taskList.elementAt(taskIndex)).Type();

            taskNumber.setSelectedIndex(taskIndex);
            taskType.setSelectedIndex(typeIndex);
            actionType.setSelectedIndex(actionIndex);
            return;
        }else if( typeIndex ==TaskElement.TASK_TYPE_DELETE){
            typeIndex= TaskElement.TASK_TYPE_DISABLED;
            if( taskIndex >0){
                at.DeleteTask( taskIndex);
                taskNumber.removeAt( taskIndex);
                taskIndex--;
            }

            actionIndex= ((TaskElement)at.taskList.elementAt(taskIndex)).Action();
            typeIndex= ((TaskElement)at.taskList.elementAt(taskIndex)).Type();

            taskNumber.setSelectedIndex(taskIndex);
            taskType.setSelectedIndex(typeIndex);
            actionType.setSelectedIndex(actionIndex);
            return;
        }// elif

        taskIndex= taskNumber.getSelectedIndex();
        typeIndex= taskType.getSelectedIndex();
        actionIndex= actionType.getSelectedIndex();

/*        h= autoTaskHour.getIntValue();
        m= autoTaskMin.getIntValue();
        t= autoTaskDelay.getIntValue();
*/
        ((TaskElement)at.taskList.elementAt(taskIndex)).Action( actionIndex);
        ((TaskElement)at.taskList.elementAt(taskIndex)).Type( typeIndex);
        ((TaskElement)at.taskList.elementAt(taskIndex)).setTime( autoTaskHour.getIntValue(), autoTaskMin.getIntValue());
        ((TaskElement)at.taskList.elementAt(taskIndex)).setTimer( autoTaskDelay.getIntValue());
        ((TaskElement)at.taskList.elementAt(taskIndex)).Once( autoTaskOnce.getValue());

        ((TaskElement)at.taskList.elementAt(taskIndex)).setRunned( true);

        at.startTask();
        destroyView();
    }

    protected void beginPaint(){
            typeIndex= taskType.getSelectedIndex();
            actionIndex= actionType.getSelectedIndex();
/*            h= autoTaskHour.getIntValue();
            m= autoTaskMin.getIntValue();
            t= autoTaskDelay.getIntValue();
*/
            ((TaskElement)at.taskList.elementAt(taskIndex)).Action( actionIndex);
            ((TaskElement)at.taskList.elementAt(taskIndex)).Type( typeIndex);
            ((TaskElement)at.taskList.elementAt(taskIndex)).Once( autoTaskOnce.getValue());
            ((TaskElement)at.taskList.elementAt(taskIndex)).setTime( autoTaskHour.getIntValue(), autoTaskMin.getIntValue());
            ((TaskElement)at.taskList.elementAt(taskIndex)).setTimer( autoTaskDelay.getIntValue());

        if( taskIndex !=taskNumber.getSelectedIndex()){
            taskIndex= taskNumber.getSelectedIndex();

            actionIndex= ((TaskElement)at.taskList.elementAt(taskIndex)).Action();
            typeIndex= ((TaskElement)at.taskList.elementAt(taskIndex)).Type();
/*            h= ((TaskElement)at.taskList.elementAt(taskIndex)).Hour();
            m= ((TaskElement)at.taskList.elementAt(taskIndex)).Minute();
            t= ((TaskElement)at.taskList.elementAt(taskIndex)).Timer();
*/
            taskType.setSelectedIndex(typeIndex);
            actionType.setSelectedIndex(actionIndex);
            autoTaskHour.setIntValue( ((TaskElement)at.taskList.elementAt(taskIndex)).Hour());
            autoTaskMin.setIntValue( ((TaskElement)at.taskList.elementAt(taskIndex)).Minute());
            autoTaskDelay.setIntValue( ((TaskElement)at.taskList.elementAt(taskIndex)).Timer());
            autoTaskOnce.setValue( ((TaskElement)at.taskList.elementAt(taskIndex)).Once());
        }
        update();
    }

    private void update(){
        itemsList.removeElement(autoTaskText);
        itemsList.removeElement(autoTaskDelay);
        itemsList.removeElement(autoTaskTimeDesc);
        itemsList.removeElement(autoTaskHour);
        itemsList.removeElement(autoTaskMin);

        if( typeIndex ==TaskElement.TASK_TYPE_TIME){
            itemsList.addElement(autoTaskTimeDesc);
            itemsList.addElement(autoTaskHour);
            itemsList.addElement(autoTaskMin);
            if( actionIndex ==TaskElement.TASK_ACTION_REMINDER)
                itemsList.addElement(autoTaskText);
        }else if( typeIndex ==TaskElement.TASK_TYPE_TIMER){
            itemsList.addElement(autoTaskDelay);
            if( actionIndex ==TaskElement.TASK_ACTION_REMINDER)
                itemsList.addElement(autoTaskText);
        }
    }
}
//#endif
