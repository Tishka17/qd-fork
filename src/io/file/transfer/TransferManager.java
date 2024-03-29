/*
 * TransferManager.java
 *
 * Created on 28.10.2006, 17:00
 *
 * Copyright (c) 2005-2008, Eugene Stahov (evgs), http://bombus-im.org
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

//#if FILE_IO && FILE_TRANSFER
package io.file.transfer;

import ui.MainBar;
import java.util.Vector;
import menu.MenuListener;
import menu.Command;
import locale.SR;
import util.Time;
import ui.VirtualElement;
import ui.VirtualList;
import ui.GMenu;
import ui.GMenuConfig;
import ui.VirtualCanvas;

/**
 *
 * @author Evg_S
 */

public class TransferManager extends VirtualList implements MenuListener {

    private Vector taskList=new Vector(0);

    Command cmdDel;
    Command cmdClrF;
//#ifdef POPUPS
    Command cmdInfo;
//#endif

    public TransferManager() {
        super();

        cmdDel=new Command(SR.get(SR.MS_DECLINE), 0x41);
        cmdClrF=new Command(SR.get(SR.MS_HIDE_FINISHED), 0x41);
//#ifdef POPUPS
        cmdInfo=new Command(SR.get(SR.MS_INFO), 0x04);
//#endif

        setMainBarItem(new MainBar(2, null, SR.get(SR.MS_TRANSFERS), false));

        taskList=TransferDispatcher.getInstance().getTaskList();
    }

    public void commandState(){
        if (getItemCount()>0) {
            addCommand(cmdDel);
            addCommand(cmdClrF);
//#ifdef POPUPS
            addCommand(cmdInfo);
//#endif
        }
    }

    protected int getItemCount() { return taskList.size(); }

    protected VirtualElement getItemRef(int index) { return (VirtualElement) taskList.elementAt(index); }

    public void eventOk() {
        TransferTask t=(TransferTask) getFocusedObject();
        if (t!=null)
            if (t.isAcceptWaiting()) {
                new TransferAcceptFile(t).show();
            }
    }

    protected void keyClear() {
        if (getItemCount()>0) {
            synchronized (taskList) {
                TransferTask task=(TransferTask) taskList.elementAt(cursor);
                task.cancel();
                taskList.removeElementAt(cursor);
            }
        }
    }

    public void commandAction(Command c) {
        if (c==cmdClrF) {
            synchronized (taskList) {
                int i=0;
                while (i<taskList.size()) {
                    TransferTask task=(TransferTask) taskList.elementAt(i);
                    if (task.isStopped())
                        taskList.removeElementAt(i);
                    else
                        i++;
                }
            }
            //if (getItemCount()<1)
            midlet.BombusQD.sd.roster.setEventIcon(null);
            redraw();
        }
        if (c==cmdDel) {
            keyClear();
            //if (getItemCount()<=1){
            midlet.BombusQD.sd.roster.setEventIcon(null);
            //}
        }
//#ifdef POPUPS
        if (c==cmdInfo) cmdInfo();
//#endif
    }

    public void destroyView() {
        TransferDispatcher.getInstance().eventNotify();
        super.destroyView();
    }

    public String touchLeftCommand(){ return SR.get(SR.MS_MENU); }

    public void touchLeftPressed(){
        if (getItemCount()>0){
           showGraphicsMenu();
        }
    }
    public int showGraphicsMenu() {
        if (getItemCount()>0){
          commandState();
          menuItem = new GMenu(this, menuCommands);
          GMenuConfig.getInstance().itemGrMenu = GMenu.TRANSFER_MANAGER;
          redraw();
        }
        return GMenu.TRANSFER_MANAGER;
    }

//#ifdef POPUPS
    protected void keyPressed(int keyCode) {
        if (keyCode==VirtualCanvas.KEY_POUND) {
            cmdInfo();
        } else {
            super.keyPressed(keyCode);
        }
    }

    private void cmdInfo() {
        if (getItemCount()>0) {
            TransferTask t=(TransferTask) getFocusedObject();
            StringBuffer info=new StringBuffer();
            info.append(t.jid)
                .append('\n')
                .append(t.fileName)
                .append('\n')
                .append(t.fileSize)
                .append(" bytes");
            if (!t.description.equals(""))
                info.append('\n').append(t.description);
            if (t.isStarted() && t.started!=0)
                info.append("\nStarted: ").append(Time.dateTimeLocalString(t.started));
            if (t.isStopped() && t.finished!=0)
                info.append("\nFinished: ").append(Time.dateTimeLocalString(t.finished));
            if (t.errMsg!=null)
                info.append("\nError: ").append(t.errMsg);

            VirtualList.setWobble(1, null, info.toString());
        }
    }
//#endif
}
//#endif

