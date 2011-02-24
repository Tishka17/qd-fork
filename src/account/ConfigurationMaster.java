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
/**
 *
 * @author aqent
 */
public class ConfigurationMaster {
    
    private Display display;
    private Displayable parentView;
    private AlertBox alert;
    private Config cf = midlet.BombusQD.cf;    
    private byte type = 0;    
    
    public ConfigurationMaster(Display display, Displayable parentView) {
       this.display = display; 
       this.parentView = parentView;
       createAnswer();
    }

    private String[] text = {
       SR.get(SR.MS_SHOW_CLIENTS_ICONS),
       SR.get(SR.MS_SHOW_STATUSES),
       SR.get(SR.MS_BOLD_FONT),
       
       SR.get(SR.MS_USE_FIVE_TO_CREATEMSG),
       SR.get(SR.MS_STORE_PRESENCE),
       SR.get(SR.MS_SHOW_PRS_TIME),
       SR.get(SR.MS_AUTOSCROLL),
       SR.get(SR.MS_ANI_SMILES),
       
       SR.get(SR.MS_DELIVERY),
       SR.get(SR.MS_FILE_TRANSFERS),
       
       SR.get(SR.MS_CONFIRM_EXIT),
       
       //SR.get(SR.MS_GR_MENU),
       SR.get(SR.MS_historyStr),
       
       SR.get(SR.MS_SUCCESS)
    };  

    private void doAction(byte type, boolean value){
        switch(type){ //some action..
            case 0: /*System.out.println("MS_SHOW_CLIENTS_ICONS->" + value);*/ cf.showClientIcon = value; break;
            case 1: /*System.out.println("MS_SHOW_STATUSES->" + value);*/ cf.rosterStatus = value; break;
            case 2: /*System.out.println("MS_BOLD_FONT->" + value);*/ cf.useBoldFont = value; break;
            case 3: /*System.out.println("MS_USE_FIVE_TO_CREATEMSG->" + value);*/ cf.createMessageByFive = value; break;
            case 4: /*System.out.println("MS_STORE_PRESENCE->" + value);*/ cf.storeConfPresence = value; break;
            case 5: /*System.out.println("MS_SHOW_PRS_TIME->" + value);*/ cf.timePresence = value; break;
            case 6: /*System.out.println("MS_AUTOSCROLL->" + value);*/ cf.autoScroll = value; break;            
            case 7: /*System.out.println("MS_ANI_SMILES->" + value);*/ cf.animatedSmiles = value; break;
            case 8: /*System.out.println("MS_DELIVERY->" + value);*/ cf.eventDelivery = value; break;
            case 9: /*System.out.println("MS_FILE_TRANSFERS->" + value);*/ cf.fileTransfer = value; break;

            case 10: /*System.out.println("MS_CONFIRM_EXIT->" + value);*/ cf.queryExit = value; break;
            //case 11: /*System.out.println("MS_GR_MENU->" + value);*/ cf.graphicsMenu = value; break;
            case 11: /*System.out.println("MS_historyStr->" + value);*/ cf.module_history = value; break;
        }
    }    
    
    private void createAnswer() {
       int len = text.length;
       if(type >= len) {
           destroyView();
           return;
       }
       int num = type + 1;
       boolean end = (type == len-1);
       String body = text[type] + ( end ? '!':'?' );
       int pos = body.indexOf('%');
       if(pos > -1) {
          body = body.substring(0,pos) + '?';
       }
       alert = new AlertBox( end ? SR.get(SR.MS_SUCCESS) : "Step " + num + "/" + len, body , display, parentView, true) {
           public void yes() {
              doAction(type, true);  type += 1;
              createAnswer();
           }
           public void no() { 
              doAction(type, false); type += 1;
              createAnswer();              
           }
       };
    }
    
    private void destroyView() {
        alert = null;
        cf.saveToStorage();
        display.setCurrent(parentView);
    }

}
