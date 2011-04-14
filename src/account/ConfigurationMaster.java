/*
 * ConfigurationMaster.java
 *
 * Created on 5 ������ 2010 �., 23:02
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package account;

import ui.controls.AlertBox;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
import client.Config;
import midlet.BombusQD;

/**
 *
 * @author aqent
 */
public class ConfigurationMaster {
    private Displayable parentView;
    private byte type = 0;

    public ConfigurationMaster(Displayable parentView) {
        this.parentView = parentView;
        createAnswer();
    }

    private String[] text = {
//#ifdef CLIENTS_ICONS
        SR.get(SR.MS_SHOW_CLIENTS_ICONS),
//#endif
        SR.get(SR.MS_SHOW_STATUSES),
        SR.get(SR.MS_BOLD_FONT),
        SR.get(SR.MS_USE_FIVE_TO_CREATEMSG),
        SR.get(SR.MS_STORE_PRESENCE),
        SR.get(SR.MS_SHOW_TIME_IN_MSGS),
        SR.get(SR.MS_ANI_SMILES),
        SR.get(SR.MS_DELIVERY),
        SR.get(SR.MS_FILE_TRANSFERS),
        SR.get(SR.MS_CONFIRM_EXIT),
        SR.get(SR.MS_HISTORY),
        SR.get(SR.MS_SUCCESS)
    };

    private void doAction(byte type, boolean value) {
        Config config = Config.getInstance();
        switch (type) {
//#ifdef CLIENTS_ICONS
            case 0:
                config.showClientIcon = value;
                break;
//#endif
            case 1:
                config.rosterStatus = value;
                break;
            case 2:
                config.useBoldFont = value;
                break;
            case 3:
                config.createMessageByFive = value;
                break;
            case 4:
                config.storeConfPresence = value;
                break;
            case 5:
                config.showTimeInMsgs = value;
                break;
            case 6:
                config.animatedSmiles = value;
                break;
            case 7:
                config.eventDelivery = value;
                break;
            case 8:
                config.fileTransfer = value;
                break;
            case 9:
                config.queryExit = value;
                break;
            case 10:
                config.module_history = value;
                break;
        }
    }

    private void createAnswer() {
        int len = text.length;
        if (type >= len) {
            destroyView();
            return;
        }
        int num = type + 1;
        boolean end = (type == len - 1);
        String body = text[type] + (end ? '!' : '?');
        int pos = body.indexOf('%');
        if (pos > -1) {
            body = body.substring(0, pos) + '?';
        }
        AlertBox box =  new AlertBox(end ? SR.get(SR.MS_SUCCESS) : "Step " + num + "/" + len, body, true)  {
            public void yes() {
                doAction(type, true);
                type += 1;
                createAnswer();
            }

            public void no() {
                doAction(type, false);
                type += 1;
                createAnswer();
            }
        };
        box.show();
    }

    private void destroyView() {
        Config.getInstance().saveToStorage();
        BombusQD.setCurrentView(parentView);
    }
}
