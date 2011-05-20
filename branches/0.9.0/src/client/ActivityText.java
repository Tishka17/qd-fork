/*
 * ActivityText.java
 *
 * Created on 11.12.2005, 20:43
 * Copyright (c) 2009, Alexej Kotov (aqent), http://bombusmod-qd.wen.ru
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
 *
 */
//#ifdef PEP
package client;

import ui.controls.form.TextInput;
import ui.controls.form.DefForm;
import javax.microedition.lcdui.TextField;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Iq;
import midlet.BombusQD;
import mood.ActivityPublishResult;
import ui.controls.form.SimpleString;

public class ActivityText extends DefForm {
    private static final String ACTIVITY_RECENT_ID = "activity-text";

    private TextInput text;
    private String cat="";
    private String descr="";

    public ActivityText(String category,String descr, String title) {
        super(title);

        this.cat=category;
        this.descr=descr;

        if(cat==null && descr==null){
          addControl(new SimpleString(locale.Activity.no_activity + "?", true));
          BombusQD.cf.actCat=null;
          BombusQD.cf.actText=null;
          BombusQD.cf.actDescr=null;
        }

        if(cat!=null && descr!=null){
          text=new TextInput(locale.SR.get(locale.SR.MS_MESSAGE), "", ACTIVITY_RECENT_ID, TextField.ANY);
          itemsList.addElement(text);
        }

        if(cat!=null && descr==null){
           addControl(new SimpleString(locale.SR.get(locale.SR.MS_PUBLISH)+"?", true));
           BombusQD.cf.actDescr=null;
        }
    }

    public void cmdOk() {
        String msgtext="";
        if(cat!=null && descr!=null){
          msgtext = text.getValue();
           if(msgtext.length()<1){
              msgtext = null;
           }
        }else{
           msgtext = null;
        }
        publishActivity(cat,descr,msgtext);
        BombusQD.sd.roster.show();
    }

    private void publishActivity(final String category, final String descr,String text) {
        String sid="publish-action";
        JabberDataBlock setActivity=new Iq(null, Iq.TYPE_SET, sid);
        JabberDataBlock action=setActivity.addChildNs("pubsub", "http://jabber.org/protocol/pubsub").addChild("publish", null);
        action.setAttribute("node", "http://jabber.org/protocol/activity");
        JabberDataBlock item=action.addChild("item", null);

            JabberDataBlock act=item.addChildNs("activity", "http://jabber.org/protocol/activity");
         if(cat!=null){
            BombusQD.cf.actCat=category;
            JabberDataBlock one = act.addChild(category, null);//relaxing

            if(descr!=null){
                BombusQD.cf.actDescr=descr;
                one.addChild(descr,null);
            }
            if(text!=null){
                BombusQD.cf.actText=text;
                act.addChild("text",text);
            }
         }
         try {
            BombusQD.sd.roster.theStream.addBlockListener(new ActivityPublishResult(sid));
            BombusQD.sd.roster.theStream.send(setActivity);
            BombusQD.cf.saveToStorage();
            setActivity=null;
            action=null;
         } catch (Exception e) {
//#ifdef DEBUG
//#             e.printStackTrace();
//#endif
         }
    }
}
//#endif
