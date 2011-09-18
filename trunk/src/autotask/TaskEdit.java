/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
//#ifdef AUTOTASK
package autotask;

import locale.SR;
import ui.controls.form.CheckBox;
import ui.controls.form.DefForm;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.TextInput;
import ui.controls.form.NumberInput;
import ui.controls.form.SimpleString;
import ui.keys.UserActions;
import java.util.Vector;
import ui.IconTextElement;
import images.MenuIcons;
import images.ActionsIcons;
import images.RosterIcons;
import javax.microedition.lcdui.TextField;

/**
 *
 * @author ad
 */

public class TaskEdit extends DefForm {
    TaskList tl;
    TaskElement te;

    private DropChoiceBox atType;
    private DropChoiceBox atAction;

    private SimpleString atTimeDesc;
    private SimpleString atNotifyDesc;

    private NumberInput atDelay;
    private NumberInput atNotifyD;
    private NumberInput atMin;
    private NumberInput atHour;
    private TextInput atText;
    private TextInput atName;
    private CheckBox atOnce;
    private CheckBox atNbox;
    private CheckBox atVbox;
    private CheckBox atLbox;
    private CheckBox atSbox;

    boolean newTask;

    public TaskEdit(TaskList tl, TaskElement te) {

        super((te==null)?"Add new task":(te.toString()));

        this.tl= tl;
        //this.te = te;

	newTask= ( te ==null);
	if( newTask) te=new TaskElement();
	this.te= te;

        atName= new TextInput( SR.get(SR.MS_SHED_NAME), te.Name, TextField.ANY);

        atType= new DropChoiceBox( SR.get(SR.MS_SHED_TYPE));
            atType.append(new IconTextElement(SR.get(SR.MS_DISABLED), RosterIcons.getInstance(), RosterIcons.ICON_PLUGINBOX_UNCHECKED));
            atType.append(new IconTextElement(SR.get(SR.MS_BY_TIME_), ActionsIcons.getInstance(), ActionsIcons.ICON_TIME));
            atType.append(new IconTextElement(SR.get(SR.MS_BY_TIMER_), ActionsIcons.getInstance(), ActionsIcons.ICON_TIME));
        atType.setSelectedIndex( te.Type);

        atAction= new DropChoiceBox( SR.get(SR.MS_SHED_ACTION));
            //atAction.items= UserActions.getActionsList( UserActions.UA_TASKS);
            // не правильно работает ???
            atAction.append( new IconTextElement( SR.get(SR.MS_SHED_REMINDER), ActionsIcons.getInstance(), ActionsIcons.ICON_TIME));
            // нулевой элемент в планировщике - напоминалка (обрабатываем отдельно)
            Vector al= UserActions.getActionsList( UserActions.UA_TASKS);
            for( int i=1; i <al.size(); i++)
                atAction.append( al.elementAt( i));
        atAction.setSelectedIndex( te.Action);

        atOnce= new CheckBox( SR.get(SR.MS_SHED_EONCE), te.Once);

        atNbox= new CheckBox( SR.get(SR.MS_SHED_NOTIFICATION), te.Notify());
            atVbox= new CheckBox( SR.get(SR.MS_SHED_VIBRA), te.NotifyV());
            atLbox= new CheckBox( SR.get(SR.MS_SHED_LIGHT), te.NotifyL());
            atSbox= new CheckBox(SR.get(SR.MS_SHED_SOUND), te.NotifyS());

        atTimeDesc=new SimpleString(SR.get(SR.MS_AUTOTASK_TIME), true);

        atHour=new NumberInput(SR.get(SR.MS_AUTOTASK_HOUR), te.Hour, 0, 23);
        atMin=new NumberInput(SR.get(SR.MS_AUTOTASK_MIN), te.Minute, 0, 59);
        atDelay=new NumberInput(SR.get(SR.MS_AUTOTASK_DELAY), te.Timer, 1, 600);
        atNotifyD=new NumberInput( SR.get(SR.MS_SHED_PREEMPTION), te.NotifyD, 1, 15);

        atText=new TextInput( SR.get(SR.MS_SHED_TEXT), te.Text, TextField.ANY);
    }

    public void cmdOk() {
            te.Type = atType.getSelectedIndex();
            te.Action = atAction.getSelectedIndex();
            te.Notify( atVbox.getValue(), atLbox.getValue(), atSbox.getValue());
            te.Hour = atHour.getIntValue();
            te.Minute = atMin.getIntValue();
            te.NotifyD = atNotifyD.getIntValue();
            te.Name = atName.getValue();
            te.Text = atText.getValue();
            te.Once = atOnce.getValue();
            te.setTimer( atDelay.getIntValue());

            te.isRunned= (atType.getSelectedIndex() !=TaskElement.TASK_TYPE_DISABLED);
            te.isNotified= !atNbox.getValue();
        if (newTask) {
            TaskList.taskList.addElement( te);
        }

        tl.rmsUpdate();
        tl.commandState();
        destroyView();
    }

    public void destroyView() {
        AutoTask.getInstance().startTask();
        super.destroyView();
    }

    protected void beginPaint(){
        update();
    }

    private void update(){
        itemsList.removeAllElements();

        itemsList.addElement( atName);
        itemsList.addElement( atType);
        itemsList.addElement( atAction);

        switch( atType.getSelectedIndex()){
            case TaskElement.TASK_TYPE_TIME:
                itemsList.addElement( atOnce);
                if( atAction.getSelectedIndex() !=0){
                    itemsList.addElement( atNbox);
                    if( atNbox.getValue()){
                        itemsList.addElement( atVbox);
                        itemsList.addElement( atLbox);
                        itemsList.addElement( atSbox);
                        itemsList.addElement( atNotifyD);
                        itemsList.addElement( atText);
                    }// if
                }
                itemsList.addElement( atTimeDesc);
                itemsList.addElement( atHour);
                itemsList.addElement( atMin);
                if( atAction.getSelectedIndex() ==0)
                    itemsList.addElement(atText);
            break;
            case TaskElement.TASK_TYPE_TIMER:
                itemsList.addElement( atOnce);
                if( atAction.getSelectedIndex() !=0){
                    itemsList.addElement( atNbox);
                    if( atNbox.getValue()){
                        itemsList.addElement( atVbox);
                        itemsList.addElement( atLbox);
                        itemsList.addElement( atSbox);
                        itemsList.addElement( atNotifyD);
                        itemsList.addElement(atText);
                    }// if
                }
                itemsList.addElement(atDelay);
                if( atAction.getSelectedIndex() ==0)
                    itemsList.addElement(atText);
            break;
        }// switch
    }
}
//#endif