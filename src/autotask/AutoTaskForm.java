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

import ui.controls.form.DropChoiceBox;
import ui.controls.form.NumberInput;
import ui.controls.form.SimpleString;
import ui.controls.form.DefForm;
import locale.SR;
import ui.IconTextElement;
import images.MenuIcons;

/**
 *
 * @author ad
 */

public class AutoTaskForm extends DefForm {
    int hour =0;
    int min  =0;
    int wait =1;

    private DropChoiceBox taskType;
    private DropChoiceBox actionType;

    private SimpleString autoTaskTimeDesc;

    private NumberInput autoTaskDelay;

    private NumberInput autoTaskMin;
    private NumberInput autoTaskHour;
    
    private AutoTask at=midlet.BombusQD.sd.autoTask;

//    private int actionIndex;
    private int taskIndex;
    private int typeIndex;

    public AutoTaskForm() {
        super(SR.get(SR.MS_AUTOTASKS));

        taskIndex= at.TASK_ACTION_QUIT;
        typeIndex= at.taskArr[taskIndex].Type;

        hour= at.taskArr[taskIndex].Hour;
        min= at.taskArr[taskIndex].Minute;
        wait= at.taskArr[taskIndex].WaitMS/60000;
        
        taskType= new DropChoiceBox(SR.get(SR.MS_AUTOTASK_TYPE));
        taskType.append(SR.get(SR.MS_DISABLED));
        taskType.append(SR.get(SR.MS_BY_TIME_));
        taskType.append(SR.get(SR.MS_BY_TIMER_));
        taskType.setSelectedIndex(typeIndex);

        actionType=new DropChoiceBox(SR.get(SR.MS_AUTOTASK_ACTION_TYPE));
        actionType.append(new IconTextElement(SR.get(SR.MS_AUTOTASK_QUIT_BOMBUSMOD),MenuIcons.getInstance(),MenuIcons.ICON_QUIT));
        actionType.append(SR.get(SR.MS_AUTOTASK_QUIT_CONFERENCES));
        actionType.append(SR.get(SR.MS_AUTOTASK_LOGOFF));
        actionType.append(SR.get(SR.MS_BREAK_CONECTION));
//        actionType.append(SR.get(SR.MS_AUTOTASK_LOGIN));
        actionType.append(SR.get(SR.MS_AUTOLOGIN));
//        actionType.append(SR.get(SR.MS_AUTOTASK_JOIN_CONFERENCES));
        actionType.append(SR.get(SR.MS_DO_AUTOJOIN));
        actionType.setSelectedIndex(taskIndex);
        
        autoTaskTimeDesc=new SimpleString(SR.get(SR.MS_AUTOTASK_TIME), true);

        autoTaskHour=new NumberInput(SR.get(SR.MS_AUTOTASK_HOUR), hour, 0, 23);
        autoTaskMin=new NumberInput(SR.get(SR.MS_AUTOTASK_MIN), min, 0, 59);
        autoTaskDelay=new NumberInput(SR.get(SR.MS_AUTOTASK_DELAY), wait, 1, 600);
        
        itemsList.addElement(taskType);
        itemsList.addElement(actionType);
        
        update();
    }

    public void cmdOk( ){
        taskIndex= actionType.getSelectedIndex();
        at.taskArr[taskIndex].Type= taskType.getSelectedIndex();
        if( at.taskArr[taskIndex].Type ==1){
            at.taskArr[taskIndex].Hour= autoTaskHour.getIntValue();
            at.taskArr[taskIndex].Minute =autoTaskMin.getIntValue();
        }else if( at.taskArr[taskIndex].Type ==2){
            at.taskArr[taskIndex].WaitMS =autoTaskDelay.getIntValue()*60000;
            at.taskArr[taskIndex].StartMS =System.currentTimeMillis();
        }// elif
//        if (at.taskType!=0)
        at.startTask();
        destroyView();
    }
    
    protected void beginPaint(){
        if(typeIndex!=taskType.getSelectedIndex()) {
            typeIndex=taskType.getSelectedIndex();       
            update();
        }
    }

    private void update(){
        itemsList.removeElement(autoTaskTimeDesc);
        itemsList.removeElement(autoTaskHour);
        itemsList.removeElement(autoTaskMin);
        itemsList.removeElement(autoTaskDelay);
        
        if (typeIndex==1) {
            itemsList.addElement(autoTaskTimeDesc);
            itemsList.addElement(autoTaskHour);
            itemsList.addElement(autoTaskMin);
        } else if (typeIndex==2) {
            itemsList.addElement(autoTaskDelay);
        }
    }
}
//#endif
