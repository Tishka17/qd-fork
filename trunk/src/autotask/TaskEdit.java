/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package autotask;

import locale.SR;
import ui.controls.form.CheckBox;
import ui.controls.form.DefForm;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.TextInput;
import ui.controls.form.NumberInput;
import ui.controls.form.SimpleString;
import ui.keys.UserActions;
import ui.keys.UserActions.userAct;
import java.util.Vector;
import ui.IconTextElement;
import images.MenuIcons;
import images.ActionsIcons;
import images.RosterIcons;
import javax.microedition.lcdui.TextField;
//import autotask.TaskList;

/**
 *
 * @author ad
 */

public class TaskEdit extends DefForm {
    private final TaskList tl;

    private DropChoiceBox atType;
    private DropChoiceBox atAction;

    private SimpleString atTimeDesc;
    private SimpleString atNotifyDesc;

    private NumberInput atDelay;
    private NumberInput atNotify;
    private NumberInput atMin;
    private NumberInput atHour;
    private TextInput atText;
    private TextInput atName;
    private CheckBox atOnce;
    private CheckBox atNbox;
    private CheckBox atVbox;
    private CheckBox atLbox;
    private CheckBox atSbox;
    private boolean notifyFlag;

    TaskElement te;

    Vector actList;

    boolean newKey;

    public TaskEdit(TaskList tl, TaskElement te) {

        super((te==null)?"Add new task":(te.toString()));

        this.tl= tl;

	this.te = te;
        //UserKeyExec.stopExecute( );

	newKey= ( te ==null);
	if (newKey) te=new TaskElement();
	this.te= te;

        atName= new TextInput( "Имя задачи", te.Name(), TextField.ANY);

        atType= new DropChoiceBox( "Тип задания");
            atType.append(new IconTextElement(SR.get(SR.MS_DISABLED), RosterIcons.getInstance(), RosterIcons.ICON_PLUGINBOX_UNCHECKED));
            atType.append(new IconTextElement(SR.get(SR.MS_BY_TIME_), ActionsIcons.getInstance(), ActionsIcons.ICON_TIME));
            atType.append(new IconTextElement(SR.get(SR.MS_BY_TIMER_), ActionsIcons.getInstance(), ActionsIcons.ICON_TIME));
        atType.setSelectedIndex( te.Type);

        atAction= new DropChoiceBox( "Действие");
        actList= null;
        actList= UserActions.getActionsList( 2);

        for (int i=0; i <actList.size(); i++) {
            atAction.append( actList.elementAt( i));
        }
        atAction.setSelectedIndex( te.Action);

        atOnce= new CheckBox("Выполнить однажды", te.Once);

        atNbox= new CheckBox( "Уведомления", te.Notify());
            atVbox= new CheckBox( "Вибрацией", te.NotifyV());
            atLbox= new CheckBox( "Миганием", te.NotifyL());
            atSbox= new CheckBox( "Гудком", te.NotifyS());

        atTimeDesc=new SimpleString(SR.get(SR.MS_AUTOTASK_TIME), true);

        atHour=new NumberInput(SR.get(SR.MS_AUTOTASK_HOUR), te.Hour(), 0, 23);
        atMin=new NumberInput(SR.get(SR.MS_AUTOTASK_MIN), te.Minute(), 0, 59);
        atDelay=new NumberInput(SR.get(SR.MS_AUTOTASK_DELAY), te.Timer(), 1, 600);
        atNotify=new NumberInput( "Упреждение (мин)", te.NotifyDelay(), 1, 15);

        atText=new TextInput( "Напомнить: текст", te.Text(), TextField.ANY);

        //itemsList.addElement( atType);
        //itemsList.addElement( atAction);
        //itemsList.addElement(keyCode);

        //moveCursorTo(getNextSelectableRef(-1));
    }

    public void cmdOk() {
            te.Type = atType.getSelectedIndex();
            te.Action = atAction.getSelectedIndex();
            te.Notify( atVbox.getValue(), atLbox.getValue(), atSbox.getValue());
            te.Hour = atHour.getIntValue();
            te.Minute = atMin.getIntValue();
            //te.NotifyL = inputStream.readBoolean();
            //te.NotifyS = inputStream.readBoolean();
            //te.NotifyV = inputStream.readBoolean();
            te.NotifyDelay = atNotify.getIntValue();
            te.Name = atName.getValue();
            te.Text = atText.getValue();
            te.Once = atOnce.getValue();
            //te.StartMS = inputStream.readLong();
            //te.WaitMS = inputStream.readLong();

        if (newKey) {
            tl.taskList.addElement( te);
        }

        tl.rmsUpdate();
        tl.commandState();
        destroyView();
    }

    public void destroyView() {
        //UserKeyExec.startExecute();
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
                //itemsList.addElement(actionType);
                if( atAction.getSelectedIndex() !=TaskElement.TASK_ACTION_REMINDER){
                    itemsList.addElement( atNbox);
                    if( atNbox.getValue()){
                        itemsList.addElement( atVbox);
                        itemsList.addElement( atLbox);
                        itemsList.addElement( atSbox);
                        itemsList.addElement( atNotify);
                    }// if
                }
                itemsList.addElement( atTimeDesc);
                itemsList.addElement( atHour);
                itemsList.addElement( atMin);
                if( atAction.getSelectedIndex() ==TaskElement.TASK_ACTION_REMINDER)
                    itemsList.addElement( atText);
            break;
            case TaskElement.TASK_TYPE_TIMER:
                itemsList.addElement( atOnce);
                //itemsList.addElement( atType);
                if( atAction.getSelectedIndex() !=TaskElement.TASK_ACTION_REMINDER){
                    itemsList.addElement( atNbox);
                    if( atNbox.getValue()){
                        itemsList.addElement( atVbox);
                        itemsList.addElement( atLbox);
                        itemsList.addElement( atSbox);
                        itemsList.addElement(atNotify);
                    }// if
                }
                itemsList.addElement(atDelay);
                if( atAction.getSelectedIndex() ==TaskElement.TASK_ACTION_REMINDER)
                    itemsList.addElement(atText);
            break;
        }// switch
    }
}