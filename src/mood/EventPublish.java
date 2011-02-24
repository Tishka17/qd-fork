/*
 * EventPublish.java
 *
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
//# package mood;
//# import javax.microedition.lcdui.Display;
//# import javax.microedition.lcdui.Displayable;
//# import javax.microedition.lcdui.TextField;
//# import com.alsutton.jabber.JabberDataBlock;
//# import com.alsutton.jabber.datablocks.Iq;
//# import mood.UserActivityResult;
//# import client.StaticData;
//# import ui.VirtualList;
//# 
//# public class EventPublish {
//#     
//#     private Display display;    
//#     
//#     /**
//#      * Creates a new instance of EventPublish
//#      */
//#     public EventPublish() {
//#     }
//#  /*   
//#     
//#     public void publishActivity(final String category, final String descr,String text) {
//#         String sid="publish-action";
//#         JabberDataBlock setMood=new Iq(null, Iq.TYPE_SET, sid);
//#         JabberDataBlock action=setMood.addChildNs("pubsub", "http://jabber.org/protocol/pubsub").addChild("publish", null);
//#         action.setAttribute("node", "http://jabber.org/protocol/activity");
//#         JabberDataBlock item=action.addChild("item", null);
//# 
//#             JabberDataBlock act=item.addChildNs("activity", "http://jabber.org/protocol/activity");
//#          if(category.length()>1){
//#             JabberDataBlock one = act.addChild(category, null);//relaxing
//#             if(descr.length()>1){
//#                 one.addChild(descr,null);
//#             } //partying
//#             if(text.length()>1){
//#                 act.addChild("text",text);
//#             }
//#          }
//#          try {
//#             StaticData.getInstance().roster.theStream.send(setMood);
//#             setMood=null;
//#             action=null;
//#             item=null;
//#          } catch (Exception e) {e.printStackTrace(); }   
//#          //Config.getInstance().saveToStorage();//?
//#    }              
//#     
//#     
//#     public void publishMood(final String moodText, final String moodName) {
//#         String sid="publish-mood";
//#         JabberDataBlock setMood=new Iq(null, Iq.TYPE_SET, sid);
//#         JabberDataBlock action=setMood.addChildNs("pubsub", "http://jabber.org/protocol/pubsub") .addChild( (moodText!=null)?"publish":"retract", null);
//#         action.setAttribute("node", "http://jabber.org/protocol/mood");
//#         JabberDataBlock item=action.addChild("item", null);
//#         item.setAttribute("id", Moods.getInstance().myMoodId);
//# 
//#         if (moodName.length()>1) {
//#             JabberDataBlock mood=item.addChildNs("mood", "http://jabber.org/protocol/mood");
//#               mood.addChild(moodName, null);            
//#             if(moodText.length()>1){
//#               mood.addChild("text",moodText);   
//#             }
//# 
//#         } else {
//#             item.addChild("retract", null);
//#             action.setAttribute("notify","1");
//#         }
//#         try {
//#             StaticData.getInstance().roster.theStream.send(setMood);
//#             setMood=null;
//#             action=null;
//#             item=null;
//#         } catch (Exception e) {e.printStackTrace(); }
//#         //Config.getInstance().saveToStorage();//?
//#    } 
//# 
//# 
//#     
//#     public void publishMusic(final String trackName, final int track) {
//# /*<iq type='set'
//#     from='stpeter@jabber.org/14793c64-0f94-11dc-9430-000bcd821bfb'
//#     id='tunes123'>
//#   <pubsub xmlns='http://jabber.org/protocol/pubsub'>
//#     <publish node='http://jabber.org/protocol/tune'>
//#       <item>
//#         <tune xmlns='http://jabber.org/protocol/tune'>
//#           <artist>Yes</artist>
//#           <length>686</length>
//#           <rating>8</rating>
//#           <source>Yessongs</source>
//#           <title>Heart of the Sunrise</title>
//#           <track>3</track>
//#           <uri>http://www.yesworld.com/lyrics/Fragile.html#9</uri>
//#         </tune>
//#       </item>
//#     </publish>
//#   </pubsub>
//# </iq>
//#      
//#         String sid="publish-tune";
//#         JabberDataBlock setMood=new Iq(null, Iq.TYPE_SET, sid);
//#         JabberDataBlock action=setMood.addChildNs("pubsub", "http://jabber.org/protocol/pubsub").addChild("publish", null);
//#         action.setAttribute("node", "http://jabber.org/protocol/tune");
//#         JabberDataBlock item=action.addChild("item", null);
//# 
//#             JabberDataBlock act=item.addChildNs("tune", "http://jabber.org/protocol/tune");
//#             act.addChild("title","QD-mp3");
//#             act.addChild("artist",trackName);
//#             act.addChild("track",Integer.toString(track));
//#          try {
//#             StaticData.getInstance().roster.theStream.addBlockListener(new UserTuneResult(display, sid));
//#             //HARDCODE
//#             StaticData.getInstance().roster.theStream.send(setMood);
//#             setMood=null;
//#             action=null;
//#             item=null;
//#          } catch (Exception e) {e.printStackTrace(); }   
//#          //Config.getInstance().saveToStorage();         
//#    }     
//#     
//#     
//#     public void stopMusic() {
//# /*<iq type='set'
//#     from='stpeter@jabber.org/14793c64-0f94-11dc-9430-000bcd821bfb'
//#     id='tunes345'>
//#   <pubsub xmlns='http://jabber.org/protocol/pubsub'>
//#     <publish node='http://jabber.org/protocol/tune'>
//#       <item>
//#         <tune xmlns='http://jabber.org/protocol/tune'/>
//#       </item>
//#     </publish>
//#   </pubsub>
//# </iq>
//#      
//#         String sid="stop-tune";
//#         JabberDataBlock setMood=new Iq(null, Iq.TYPE_SET, sid);
//#         JabberDataBlock action=setMood.addChildNs("pubsub", "http://jabber.org/protocol/pubsub").addChild("publish", null);
//#         action.setAttribute("node", "http://jabber.org/protocol/tune");
//#         JabberDataBlock item=action.addChild("item", null);
//# 
//#             JabberDataBlock act=item.addChildNs("tune", "http://jabber.org/protocol/tune");
//#             
//#         VirtualList.setWobble(1, null, "Music Stopped!)");                   
//# 
//#          try {
//#             StaticData.getInstance().roster.theStream.addBlockListener(new UserTuneResult(display, sid));            
//#             StaticData.getInstance().roster.theStream.send(setMood);
//#             setMood=null;
//#             action=null;
//#             item=null;
//#          } catch (Exception e) {e.printStackTrace(); }   
//#          //Config.getInstance().saveToStorage();         
//#    }     
//#   */    
//#     
//#     
//# }
//#endif