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
import images.ActionsIcons;
import images.RosterIcons;
import java.util.Vector;

/**
 * @author Mars
 */

public class AutoTaskForm extends DefForm {

    private DropChoiceBox taskNumber;
    private DropChoiceBox taskType;
    private DropChoiceBox actionType;
//    private DropChoiceBox notifyType;

    private SimpleString autoTaskTimeDesc;
    private SimpleString autoTaskNotifyDesc;

    private NumberInput autoTaskDelay;
    private NumberInput autoTaskNotify;
    private NumberInput autoTaskMin;
    private NumberInput autoTaskHour;
    private TextInput autoTaskText;
    private TextInput autoTaskName;
    private CheckBox autoTaskOnce;
    private CheckBox notifyNbox;
    private CheckBox notifyVbox;
    private CheckBox notifyLbox;
    private CheckBox notifySbox;
    private boolean notifyFlag;

    private Vector tl= TaskList.TaskList();
    private AutoTask at= new AutoTask();

    private static int taskIndex;
    private int typeIndex;
    private int actionIndex;
    //private int notifyIndex;

    public AutoTaskForm(){
        super(SR.get(SR.MS_AUTOTASKS));
        taskIndex= 0;

        taskNumber= new DropChoiceBox( "Имя задачи");
            for( int ti= 0; ti <tl.size(); ti++)
                taskNumber.append( ((TaskElement)tl.elementAt(ti)).Name());

        autoTaskOnce= new CheckBox( "Выполнить однажды", ((TaskElement)tl.elementAt(taskIndex)).Once());

        taskType= new DropChoiceBox( "Тип задания");
            taskType.append(new IconTextElement(SR.get(SR.MS_DISABLED), RosterIcons.getInstance(), RosterIcons.ICON_PLUGINBOX_UNCHECKED));
            taskType.append(new IconTextElement(SR.get(SR.MS_BY_TIME_), ActionsIcons.getInstance(), ActionsIcons.ICON_TIME));
            taskType.append(new IconTextElement(SR.get(SR.MS_BY_TIMER_), ActionsIcons.getInstance(), ActionsIcons.ICON_TIME));
//        taskType.append(SR.get(SR.MS_BY_TIME_));
            taskType.append(new IconTextElement("Создать новое", MenuIcons.getInstance(), MenuIcons.ICON_ADD));
//        taskType.append(SR.get(SR.MS_BY_TIMER_));
            taskType.append(new IconTextElement("Удалить это", MenuIcons.getInstance(), MenuIcons.ICON_REMOVE));
//        taskType.append(SR.get(SR.MS_BY_TIMER_));
            taskType.append(new IconTextElement("Переименовать", ActionsIcons.getInstance(), ActionsIcons.ICON_RENAME));

        actionType=new DropChoiceBox(SR.get(SR.MS_AUTOTASK_ACTION_TYPE));
            actionType.append(new IconTextElement(SR.get(SR.MS_AUTOTASK_QUIT_BOMBUSMOD), MenuIcons.getInstance(), MenuIcons.ICON_QUIT));
            actionType.append(new IconTextElement(SR.get(SR.MS_AUTOTASK_QUIT_CONFERENCES), ActionsIcons.getInstance(), ActionsIcons.ICON_LEAVE));
            actionType.append(new IconTextElement(SR.get(SR.MS_AUTOTASK_LOGOFF), ActionsIcons.getInstance(), ActionsIcons.ICON_OFF));
            actionType.append(new IconTextElement(SR.get(SR.MS_BREAK_CONECTION), MenuIcons.getInstance(), MenuIcons.ICON_RECONNECT));
//        actionType.append(SR.get(SR.MS_AUTOTASK_LOGIN));
            actionType.append(new IconTextElement(SR.get(SR.MS_AUTOLOGIN), ActionsIcons.getInstance(), ActionsIcons.ICON_ON));
//        actionType.append(SR.get(SR.MS_AUTOTASK_JOIN_CONFERENCES));
            actionType.append(new IconTextElement(SR.get(SR.MS_DO_AUTOJOIN), ActionsIcons.getInstance(), ActionsIcons.ICON_SET_STATUS));
//        actionType.append(SR.get(SR.MS_AUTOTASK_REMINDER));
            actionType.append(new IconTextElement( "Напомнить", ActionsIcons.getInstance(), ActionsIcons.ICON_VOICE));

//        notifyType= new DropChoiceBox(SR.get(SR.MS_AUTOTASK_NOTIFY));
/*        notifyType= new DropChoiceBox( "Уведомления");
            notifyType.append(new IconTextElement( "Выключены", RosterIcons.getInstance(), RosterIcons.ICON_PLUGINBOX_UNCHECKED));
            notifyType.append(new IconTextElement( "Вибрацией", ActionsIcons.getInstance(), ActionsIcons.ICON_TIME));
            notifyType.append(new IconTextElement( "Миганием", ActionsIcons.getInstance(), ActionsIcons.ICON_TIME));
            notifyType.append(new IconTextElement( "Гудком", ActionsIcons.getInstance(), ActionsIcons.ICON_VOICE));
*/
        //notifyFlag= ((TaskElement)tl.elementAt(taskIndex)).Notify();
        notifyNbox= new CheckBox( "Уведомления", ((TaskElement)tl.elementAt(taskIndex)).Notify());
            notifyVbox= new CheckBox( "Вибрацией", ((TaskElement)tl.elementAt(taskIndex)).NotifyV());
            notifyLbox= new CheckBox( "Миганием", ((TaskElement)tl.elementAt(taskIndex)).NotifyL());
            notifySbox= new CheckBox( "Гудком", ((TaskElement)tl.elementAt(taskIndex)).NotifyS());

        autoTaskTimeDesc=new SimpleString(SR.get(SR.MS_AUTOTASK_TIME), true);

        autoTaskHour=new NumberInput(SR.get(SR.MS_AUTOTASK_HOUR), ((TaskElement)tl.elementAt(taskIndex)).Hour(), 0, 23);
        autoTaskMin=new NumberInput(SR.get(SR.MS_AUTOTASK_MIN), ((TaskElement)tl.elementAt(taskIndex)).Minute(), 0, 59);
        autoTaskDelay=new NumberInput(SR.get(SR.MS_AUTOTASK_DELAY), ((TaskElement)tl.elementAt(taskIndex)).Timer(), 1, 600);
        autoTaskNotify=new NumberInput( "Упреждение (мин)", ((TaskElement)tl.elementAt(taskIndex)).NotifyDelay(), 1, 15);

        autoTaskText=new TextInput( "Напомнить: текст", ((TaskElement)tl.elementAt(taskIndex)).Text(), TextField.ANY);
        autoTaskName= new TextInput( "Новое имя", ((TaskElement)tl.elementAt(taskIndex)).Name(), TextField.ANY);

        actionIndex= ((TaskElement)tl.elementAt(taskIndex)).Action();
        typeIndex= ((TaskElement)tl.elementAt(taskIndex)).Type();
//        notifyIndex= ((TaskElement)tl.elementAt(taskIndex)).Notify();

        taskNumber.setSelectedIndex(taskIndex);
        taskType.setSelectedIndex(typeIndex);
        actionType.setSelectedIndex(actionIndex);
        //notifyType.setSelectedIndex(notifyIndex);

        update();
    }

    public void cmdOk( ){
        if( typeIndex ==TaskElement.TASK_TYPE_CREATE){
            typeIndex= TaskElement.TASK_TYPE_DISABLED;
            TaskList.CreateTask( taskIndex);

            taskNumber.insertAt( ((TaskElement)tl.elementAt(taskIndex)).Name(), taskIndex);
//            taskIndex++;

            actionIndex= ((TaskElement)tl.elementAt(taskIndex)).Action();
            typeIndex= ((TaskElement)tl.elementAt(taskIndex)).Type();
            //notifyFlag= ((TaskElement)tl.elementAt(taskIndex)).Notify();

            taskNumber.setSelectedIndex(taskIndex);
            taskType.setSelectedIndex(typeIndex);
            actionType.setSelectedIndex(actionIndex);
            //notifyType.setSelectedIndex(notifyIndex);
            //update();
            destroyView();
            return;
        }else if( typeIndex ==TaskElement.TASK_TYPE_DELETE){
//            typeIndex= TaskElement.TASK_TYPE_DISABLED;
            if( taskIndex >0){
                TaskList.DeleteTask( taskIndex);
                taskNumber.removeAt( taskIndex);
                taskIndex--;

                actionIndex= ((TaskElement)tl.elementAt(taskIndex)).Action();
                typeIndex= ((TaskElement)tl.elementAt(taskIndex)).Type();
                //notifyIndex= ((TaskElement)tl.elementAt(taskIndex)).Notify();
            }

            taskNumber.setSelectedIndex(taskIndex);
            taskType.setSelectedIndex(typeIndex);
            actionType.setSelectedIndex(actionIndex);
            //notifyType.setSelectedIndex(notifyIndex);
//            update();
            destroyView();
            return;
        }else if( typeIndex ==TaskElement.TASK_TYPE_RENAME){
            typeIndex= TaskElement.TASK_TYPE_DISABLED;

            taskNumber.items.setElementAt( ((TaskElement)tl.elementAt(taskIndex)).Name(), taskIndex);

            taskNumber.setSelectedIndex(taskIndex);
            taskType.setSelectedIndex(typeIndex);
            actionType.setSelectedIndex(actionIndex);
            //notifyType.setSelectedIndex(notifyIndex);
            //update();
            destroyView();
            return;
        }// elif

        taskIndex= taskNumber.getSelectedIndex();
        typeIndex= taskType.getSelectedIndex();
        actionIndex= actionType.getSelectedIndex();
        //notifyIndex= notifyType.getSelectedIndex();

        ((TaskElement)tl.elementAt(taskIndex)).Action( actionIndex);
        ((TaskElement)tl.elementAt(taskIndex)).Type( typeIndex);
        ((TaskElement)tl.elementAt(taskIndex)).Notify( notifyNbox.getValue());
        ((TaskElement)tl.elementAt(taskIndex)).NotifyV( notifyVbox.getValue());
        ((TaskElement)tl.elementAt(taskIndex)).NotifyL( notifyVbox.getValue());
        ((TaskElement)tl.elementAt(taskIndex)).NotifyS( notifyVbox.getValue());
        ((TaskElement)tl.elementAt(taskIndex)).setTime( autoTaskHour.getIntValue(), autoTaskMin.getIntValue());
        ((TaskElement)tl.elementAt(taskIndex)).setTimer( autoTaskDelay.getIntValue());
        ((TaskElement)tl.elementAt(taskIndex)).NotifyDelay( autoTaskNotify.getIntValue());
        ((TaskElement)tl.elementAt(taskIndex)).Once( autoTaskOnce.getValue());
        ((TaskElement)tl.elementAt(taskIndex)).Text( autoTaskText.getValue( ));
        ((TaskElement)tl.elementAt(taskIndex)).Name( autoTaskName.getValue( ));

        ((TaskElement)tl.elementAt(taskIndex)).setRunned( true);

        at.startTask();
        destroyView();
    }

    protected void beginPaint(){
            typeIndex= taskType.getSelectedIndex();
            actionIndex= actionType.getSelectedIndex();
            //notifyIndex= notifyType.getSelectedIndex();

            ((TaskElement)tl.elementAt(taskIndex)).Action( actionIndex);
            ((TaskElement)tl.elementAt(taskIndex)).Type( typeIndex);
            ((TaskElement)tl.elementAt(taskIndex)).Notify( notifyNbox.getValue());
            ((TaskElement)tl.elementAt(taskIndex)).NotifyV( notifyVbox.getValue());
            ((TaskElement)tl.elementAt(taskIndex)).NotifyL( notifyVbox.getValue());
            ((TaskElement)tl.elementAt(taskIndex)).NotifyS( notifyVbox.getValue());
            ((TaskElement)tl.elementAt(taskIndex)).Once( autoTaskOnce.getValue());
            ((TaskElement)tl.elementAt(taskIndex)).setTime( autoTaskHour.getIntValue(), autoTaskMin.getIntValue());
            ((TaskElement)tl.elementAt(taskIndex)).setTimer( autoTaskDelay.getIntValue());
            ((TaskElement)tl.elementAt(taskIndex)).NotifyDelay( autoTaskNotify.getIntValue());
            ((TaskElement)tl.elementAt(taskIndex)).Text( autoTaskText.getValue( ));
            ((TaskElement)tl.elementAt(taskIndex)).Name( autoTaskName.getValue( ));

        if( taskIndex !=taskNumber.getSelectedIndex()){
            taskIndex= taskNumber.getSelectedIndex();

            actionIndex= ((TaskElement)tl.elementAt(taskIndex)).Action();
            typeIndex= ((TaskElement)tl.elementAt(taskIndex)).Type();
            //notifyIndex= ((TaskElement)tl.elementAt(taskIndex)).Notify();

            taskType.setSelectedIndex(typeIndex);
            actionType.setSelectedIndex(actionIndex);
            //notifyType.setSelectedIndex(notifyIndex);
            autoTaskHour.setIntValue( ((TaskElement)tl.elementAt(taskIndex)).Hour());
            autoTaskMin.setIntValue( ((TaskElement)tl.elementAt(taskIndex)).Minute());
            autoTaskDelay.setIntValue( ((TaskElement)tl.elementAt(taskIndex)).Timer());
            autoTaskNotify.setIntValue( ((TaskElement)tl.elementAt(taskIndex)).NotifyDelay());
            autoTaskOnce.setValue( ((TaskElement)tl.elementAt(taskIndex)).Once());
            notifyNbox.setValue( ((TaskElement)tl.elementAt(taskIndex)).Notify());
            notifyVbox.setValue( ((TaskElement)tl.elementAt(taskIndex)).NotifyV());
            notifyLbox.setValue( ((TaskElement)tl.elementAt(taskIndex)).NotifyL());
            notifySbox.setValue( ((TaskElement)tl.elementAt(taskIndex)).NotifyS());
            autoTaskText.setValue( ((TaskElement)tl.elementAt(taskIndex)).Text());
            autoTaskName.setValue( ((TaskElement)tl.elementAt(taskIndex)).Name());
        }
        update();
    }

    private void update(){
        itemsList.removeAllElements();

        itemsList.addElement(taskNumber);
        itemsList.addElement(taskType);
        //if( true){        }

       switch( typeIndex){
            case TaskElement.TASK_TYPE_TIME:
                itemsList.addElement(autoTaskOnce);
                itemsList.addElement(actionType);
                if( actionIndex !=TaskElement.TASK_ACTION_REMINDER){
                    itemsList.addElement(notifyNbox);
                    if( notifyNbox.getValue()){
                        itemsList.addElement(notifyVbox);
                        itemsList.addElement(notifyLbox);
                        itemsList.addElement(notifySbox);
                        itemsList.addElement(autoTaskNotify);
                    }// if
                }
                itemsList.addElement(autoTaskTimeDesc);
                itemsList.addElement(autoTaskHour);
                itemsList.addElement(autoTaskMin);
                if( actionIndex ==TaskElement.TASK_ACTION_REMINDER)
                    itemsList.addElement(autoTaskText);
            break;
            case TaskElement.TASK_TYPE_TIMER:
                itemsList.addElement(autoTaskOnce);
                itemsList.addElement(actionType);
                if( actionIndex !=TaskElement.TASK_ACTION_REMINDER){
                    itemsList.addElement(notifyNbox);
                    if( notifyNbox.getValue()){
                        itemsList.addElement(notifyVbox);
                        itemsList.addElement(notifyLbox);
                        itemsList.addElement(notifySbox);
                        itemsList.addElement(autoTaskNotify);
                    }// if
                }
                itemsList.addElement(autoTaskDelay);
                if( actionIndex ==TaskElement.TASK_ACTION_REMINDER)
                    itemsList.addElement(autoTaskText);
            break;
            //case TaskElement.TASK_TYPE_CREATE:
            case TaskElement.TASK_TYPE_RENAME:
                //typeIndex= TaskElement.TASK_TYPE_DISABLED;
                //autoTaskName.setValue( ((TaskElement)tl.elementAt(taskIndex)).Name());
                itemsList.addElement( autoTaskName);
            break;
        }// switch
    }
}
//#endif
