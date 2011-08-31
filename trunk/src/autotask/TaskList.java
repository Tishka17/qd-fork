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

import io.NvStorage;
import java.io.DataOutputStream;
import java.util.Vector;
import locale.SR;
import ui.MainBar;
import ui.VirtualElement;
import ui.VirtualList;
import menu.MenuListener;
import menu.Command;
import ui.GMenu;
import ui.GMenuConfig;
import autotask.TaskElement;

public class TaskList extends VirtualList implements MenuListener {
//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_USER_KEYS");
//#endif

    public static Vector taskList;

    Command cmdSave;
    Command cmdAdd;
    Command cmdEdit;
    Command cmdDel;

    public TaskList() {
        super();

        cmdSave=new Command(SR.get(SR.MS_SAVE), 0x44);
        cmdAdd=new Command("Добавить задачу", 0x47);
        cmdEdit=new Command(SR.get(SR.MS_EDIT),0x40);
        cmdDel=new Command(SR.get(SR.MS_DELETE),0x41);

        setMainBarItem(new MainBar("Планировщик"));

        if( taskList ==null) taskList= new Vector(0);
        int index=0;
        TaskElement te= null;
        do {
            te=TaskElement.createFromStorage(index);
            if (te!=null) {
                taskList.addElement( te);
                index++;
             }
        } while (te!=null);

    }

    void commandState(){
        menuCommands.removeAllElements();

        addCommand(cmdSave);
        addCommand(cmdAdd);
        if (getItemCount() > 0) {
            addCommand(cmdEdit);
            addCommand(cmdDel);
        }
    }

    public VirtualElement getItemRef(int Index) {
        return (VirtualElement)taskList.elementAt(Index);
    }

    protected int getItemCount() {
        return taskList.size();
    }

    public void commandAction(Command c){
        if (c==cmdSave) {
            rmsUpdate();
            destroyView();
        }
        if (c==cmdEdit)
            new TaskEdit(this, (TaskElement)getFocusedObject()).show();
        if (c==cmdAdd)
            new TaskEdit(this, null).show();
        if (c==cmdDel) {
            taskList.removeElement(getFocusedObject());

            rmsUpdate();
            moveCursorHome();
            redraw();
        }
    }

    public void eventOk(){
        new TaskEdit( this, (TaskElement)getFocusedObject()).show();
    }

    void rmsUpdate(){
        DataOutputStream outputStream=NvStorage.CreateDataOutputStream();

        for( int i=0; i<taskList.size(); i++) {
            ((TaskElement)taskList.elementAt(i)).saveToDataOutputStream(outputStream);
        }

        NvStorage.writeFileRecord(outputStream, TaskElement.storage, 0, true);
    }

    public int showGraphicsMenu() {
        commandState();
        menuItem = new GMenu(this, menuCommands);
        GMenuConfig.getInstance().itemGrMenu = GMenu.USERKEYSLIST;
        return GMenu.USERKEYSLIST;
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
