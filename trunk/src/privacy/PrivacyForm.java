/*
 * PrivacyForm.java
 *
 * Created on 26.05.2008, 15:29 
 *
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
 *
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
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
 *
 */
//#ifdef PRIVACY
package privacy;

import client.Contact;
import client.Group;
import client.StaticData;
import java.util.Vector;
import javax.microedition.lcdui.TextField;
import locale.SR;
import ui.controls.form.CheckBox;
import ui.controls.form.DefForm;
import ui.controls.form.DropChoiceBox;
import ui.controls.form.TextInput;

/**
 *
 * @author EvgS
 */
public class PrivacyForm extends DefForm {
//#ifdef PLUGINS
//#     public static String plugin = new String("PLUGIN_PRIVACY");
//#endif  
    private PrivacyList targetList;
    private PrivacyItem item;
    
    DropChoiceBox choiceAction;
    DropChoiceBox choiceType;
    DropChoiceBox choiceSubscr;
    
    CheckBox messageStz;
    CheckBox presenceInStz;
    CheckBox presenceOutStz;
    CheckBox iqStz;

    TextInput textValue;
    
    int selectedAction=-1;
    String tValue="";
    
    private boolean subscrField=true;
    private int newSubscrPos=0;    

    public PrivacyForm(PrivacyItem item, PrivacyList plist) {
        super(SR.get(SR.MS_PRIVACY_RULE));
       
        this.item=item;
        targetList=plist;
        
        update();

    }
    
    private void update() {
        int len = 0;
        selectedAction=(selectedAction<0)?item.action:choiceAction.getSelectedIndex();
        tValue=(textValue!=null)?textValue.getValue():item.value;
        
        Object rfocus=StaticData.getInstance().roster.getFocusedObject();
        itemsList=null;
        itemsList=new Vector(0);

        choiceAction=new DropChoiceBox(SR.get(SR.MS_ACTION));
        len = PrivacyItem.actions.length;
        for(int i=0; i<len; i++){
            choiceAction.append(PrivacyItem.actions_[i]);
        }
        choiceAction.setSelectedIndex(selectedAction);
        addControl(choiceAction);

          messageStz=new CheckBox(PrivacyItem.stanzas_[0], item.messageStz); 
          presenceInStz=new CheckBox(PrivacyItem.stanzas_[1], item.presenceInStz); 
          presenceOutStz=new CheckBox(PrivacyItem.stanzas_[2], item.presenceOutStz); 
          iqStz=new CheckBox(PrivacyItem.stanzas_[3], item.iqStz); 
          
        addControl(messageStz);
        addControl(presenceInStz);
        addControl(presenceOutStz);
        addControl(iqStz);
        
        choiceType=new DropChoiceBox(SR.get(SR.MS_PRIVACY_TYPE));
        len = PrivacyItem.types.length;
        for(int i=0; i<len; i++){
            choiceType.append(PrivacyItem.types_[i]);
        }
        choiceType.setSelectedIndex(item.type);
        addControl(choiceType);
        newSubscrPos=itemsList.indexOf(choiceType)+1;
        
       
        choiceSubscr=new DropChoiceBox(SR.get(SR.MS_SUBSCRIPTION));
          len = PrivacyItem.subscrs.length;
          for(int i=0; i<len; i++) choiceSubscr.append(PrivacyItem.subscrs_[i]);
          
        if(item.value!=null){
         for (int i=0; i<len; i++) {
             if (item.value.equals(PrivacyItem.subscrs[i])) {
                choiceSubscr.setSelectedIndex(i);
                break;
             }
         }
        }

        textValue=new TextInput(SR.get(SR.MS_VALUE), tValue, TextField.ANY);//64, TextField.ANY);
        try{
         switch (selectedAction) {
            case 0: //jid
                if (targetList!=null) {
                    if (rfocus instanceof Contact) {
                        textValue.setValue(((Contact)rfocus).bareJid);
                    }
                }
                break;
            case 1: //group
                if (targetList!=null)
                    textValue.setValue(((rfocus instanceof Group)?(Group)rfocus:((Contact)rfocus).group).getName());
                break;
         }
         addControl(textValue);
         
        } catch (Exception e) { }
    }
    
    protected void beginPaint(){
        if (choiceType!=null) {
            if (choiceType.toString()==SR.get(SR.MS_SUBSCRIPTION)) {
                if (subscrField==false) {
                    itemsList.insertElementAt(choiceSubscr, newSubscrPos);
                    itemsList.removeElement(textValue);
                    subscrField=true;
                }else{ 
                //subscrField == TRUE when user �pen SUBSCR rule
                   if(!itemsList.contains(choiceSubscr)) addControl(choiceSubscr);
                   if(itemsList.contains(textValue)) itemsList.removeElement(textValue);
                   
                }
            } else {
                if (subscrField) {
                    if(itemsList.contains(choiceSubscr)) itemsList.removeElement(choiceSubscr);
                    if(!itemsList.contains(textValue)) addControl(textValue);
                    subscrField=false;
                }
            }
        }
    }

    public void cmdOk() {
        try {
            int type=choiceType.getSelectedIndex();
            String value=textValue.getValue();
            if (type==2) {
                value=PrivacyItem.subscrs[choiceSubscr.getSelectedIndex()];
            }
            if (type!=PrivacyItem.ITEM_ANY) 
            if (value.length()==0) return;

            item.action=choiceAction.getSelectedIndex();
            item.type=type;
            item.value=value;
            
            item.messageStz=messageStz.getValue();
            item.presenceInStz=presenceInStz.getValue();
            item.presenceOutStz=presenceOutStz.getValue();
            item.iqStz=iqStz.getValue();

            if (targetList!=null) {
                if (!targetList.rules.contains(item)) {
                    targetList.addRule(item);
                    item.order=targetList.rules.indexOf(item)*10;
                }
            }
        } catch (Exception e) { }
        destroyView();
    }
}
//#endif
