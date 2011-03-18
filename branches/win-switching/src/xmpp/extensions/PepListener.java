/*
 * PepListener.java
 *
 * Created on 30.04.2008, 21:37
 *
 * Copyright (c) 2005-2007, Eugene Stahov (evgs), http://bombus-im.org
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
 */
//#ifdef PEP
package xmpp.extensions;

import client.*;
import mood.Moods;
import com.alsutton.jabber.*; 
import com.alsutton.jabber.datablocks.*;
import java.util.*;
import locale.SR;

public class PepListener implements JabberBlockListener{
    StaticData sd = StaticData.getInstance();
    Config cf = Config.getInstance();

    public void destroy() {
    }

    private static StringBuffer mood_=new StringBuffer(0);
    private static StringBuffer activity_=new StringBuffer(0);
    private static StringBuffer tune_=new StringBuffer(0);
     
    private static void clear(){
        mood_ = new StringBuffer(0);
        activity_ = new StringBuffer(0);
        tune_ = new StringBuffer(0);
    }
     
    public int blockArrived(JabberDataBlock data) {
        if (!(data instanceof Message)) return BLOCK_REJECTED;
        //if (!data.getTypeAttribute().equals("headline")) return BLOCK_REJECTED;
        
        boolean  tuneVaule=false;
        byte moodIndex=-1;        

        JabberDataBlock event=data.findNamespace("event", "http://jabber.org/protocol/pubsub#event");
        if (event==null) {         
            return BLOCK_REJECTED;
        }
        
        String from=data.getAttribute("from");
        String id=null;        
        String tag=null;
        String moodText = "";
        String activity_name="";
        String type="";
        
        clear();
        
        boolean hasActivity=false;
        if (cf.rcvactivity) {
            JabberDataBlock activity=extractEvent(event, "activity", "http://jabber.org/protocol/activity");
            if (activity!=null) {
                tag=null;
                String activityText=null;
                try {
                    int s = activity.getChildBlocks().size();
                    for (int i=0; i<s; i++) {
                        JabberDataBlock child=(JabberDataBlock)activity.getChildBlocks().elementAt(i);
                        tag=child.getTagName();
                        if(locale.Activity.getIconIndex(tag)>-1){
                           activity_name = tag;
                        }
                        tag=child.getTagName();
                        if (tag.equals("text")) continue;
                        activity_.append(locale.Activity.loadString(tag));
                        if (child.getChildBlocks()!=null){
                            activity_name =  ((JabberDataBlock) child.getChildBlocks().elementAt(0)).getTagName();
                            activity_.append(": ").append(
                                    locale.Activity.loadString(
                                        ((JabberDataBlock) child.getChildBlocks().elementAt(0)).getTagName()
                                        )
                                    );
                        }
                        id=activity.getParent().getAttribute("id");
                    }
                } catch (Exception ex) { }

                activityText=activity.getChildBlockText("text");
                if (activityText!=null){
                    if (activityText.length()>0) {
                        activity_.append('(')
                                .append(activityText)
                                .append(')');
                    }
                }
                hasActivity=true;

//#ifdef DEBUG
//#              System.out.println(from+": "+activity_.toString());
//#endif
                type=SR.get(SR.MS_USERACTIVITY);
            }
        }
        

        boolean  hasTune=false;
        if (cf.rcvtune) {
            JabberDataBlock tune=extractEvent(event, "tune", "http://jabber.org/protocol/tune");
            if (tune!=null) {
                if (tune.getChildBlocks()==null)
                    tune_.append("(silence.No music)");
                else {
                    String src=tune.getChildBlockText("source");
                    String len=tune.getChildBlockText("length");
                    String track=tune.getChildBlockText("track");
                    
                    if (track.length()>0) {
                        tune_.append(track)
                        .append(").");
                    }                     
                    
                    tune_.append(tune.getChildBlockText("title"))
                    .append(" - ")
                    .append(tune.getChildBlockText("artist"));

                    if (src.length()>0) {
                        tune_.append(" (")
                        .append(src)
                        .append(')');
                    }
                    hasTune=true;
                }
//#ifdef DEBUG
//#              System.out.println(from+": "+tune_.toString());
//#endif
                type=SR.get(SR.MS_USERTUNE);
            }
        }
        moodIndex=-1;
        JabberDataBlock mood=null;
        moodText=null;
        tag=null;
        boolean hasMood = false;
        
        if (cf.sndrcvmood) {
            mood=extractEvent(event, "mood", "http://jabber.org/protocol/mood");
            if (mood!=null) {
                try {
                    int s = mood.getChildBlocks().size();
                    for (int i=0; i<s; i++) {
                        JabberDataBlock child=(JabberDataBlock)mood.getChildBlocks().elementAt(i);                    
                        tag=child.getTagName();
                        if (tag.equals("text")) continue;

                        moodIndex=Moods.getInstance().getMoodIngex(tag);

                        id=mood.getParent().getAttribute("id");
                    }
                } catch (Exception ex) {
                    moodIndex=-1;//Moods.getInstance().getMoodIngex("-");
                }

                mood_.append(Moods.getInstance().getMoodLabel(moodIndex));
                moodText=mood.getChildBlockText("text");
                if (moodText!=null){
                    if (moodText.length()>0) {
                        mood_.append('(')
                                .append(moodText)
                                .append(')');
                    }
                }
                hasMood = true;
//#ifdef DEBUG
//#             System.out.println(from+": "+mood_.toString());
//#endif
                type=SR.get(SR.MS_USERMOOD);
            }
        }


        Vector hContacts=sd.roster.getHContacts();
        String msg="";
        //synchronized (hContacts) {
            Jid j=new Jid(from);
            int s = hContacts.size();
            for (int i=0; i<s; i++) {
                Contact c=(Contact)hContacts.elementAt(i);
                if (c.jid.equals(j, false)) {              
                    if (hasMood) {
                        c.pepMood=moodIndex;
                        c.pepMoodName=Moods.getInstance().getMoodLabel(moodIndex);
                        c.pepMoodText=moodText;
                        
                        if (c.getGroupType()==Groups.TYPE_SELF) {
                            if (id!=null) Moods.getInstance().myMoodId=id;
                            Moods.getInstance().myMoodName=tag;
                            Moods.getInstance().myMoodName=moodText;
                        }
                        msg = mood_.toString();
                    }
                    if (hasActivity) {
                        c.activity=activity_.toString();
                        c.activ = locale.Activity.getIconIndex(activity_name);
                        msg = activity_.toString();
                    }
                    if (hasTune) {
                        c.pepTune=true;
                        c.pepTuneText=tune_.toString();
                        msg = tune_.toString();
                    }
                    if(msg!=null && msg.length()>0 && c.getChatInfo().getMessageCount()>0) c.addMessage(new Msg(Constants.MESSAGE_TYPE_PRESENCE, from, type, msg));
                }
            }
        //}
        clear();      
        sd.roster.redraw();
        return BLOCK_PROCESSED;
    }
    
    JabberDataBlock extractEvent(JabberDataBlock data, String tagName, String xmlns) {
        JabberDataBlock items=data.getChildBlock("items");
        if (items==null) return null;
        if (!xmlns.equals(items.getAttribute("node"))) return null;
        JabberDataBlock item=items.getChildBlock("item");
        if (item==null) return new JabberDataBlock();
        return item.findNamespace(tagName, xmlns);
    }
}
//#endif
