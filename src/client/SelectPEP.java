/*
 * SelectPEP.java
 *
 * Copyright (c) 2005-2008, Eugene Stahov (evgs), http://bombus-im.org
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

import images.ImageList;
import font.FontCache;
import images.MoodIcons;
import images.ActivityIcons;
import locale.SR;
import colors.ColorTheme;
import java.util.Vector;
import ui.controls.Balloon;
import javax.microedition.lcdui.TextField;
import javax.microedition.lcdui.Graphics;
import mood.Moods;
import mood.MoodPublishResult;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Iq;
import locale.Activity;
import midlet.BombusQD;
import ui.VirtualElement;
import ui.VirtualList;
import ui.controls.form.DefForm;
import ui.input.InputTextBox;
import ui.input.InputTextBoxNotify;

/**
 *
 * @author evgs,aqent
 */

public final class SelectPEP extends DefForm implements VirtualElement, InputTextBoxNotify {
    private final static byte CURSOR_HOFFSET=1;
    private final static byte CURSOR_VOFFSET=1;

    private int lineIndex;
    private int xCnt;
    private int xLastCnt;
    private int xCursor;
    private int lines;

    private int lineHeight;
    private int lineWidth;
    private int imgHeight;
    private int imgWidth;

    private ImageList il;
    private int realWidth=0;
    private int xBorder = 0;

    private String[] activity = {
       Activity.CATEGORY_drinking ,
       Activity.ACTIVITY_having_a_beer,
       Activity.ACTIVITY_having_coffee,
       Activity.ACTIVITY_having_tea,

       Activity.CATEGORY_doing_chores,
       Activity.ACTIVITY_buying_groceries,
       Activity.ACTIVITY_cleaning,
       Activity.ACTIVITY_cooking,
       Activity.ACTIVITY_doing_maintenance,
       Activity.ACTIVITY_doing_the_dishes,
       Activity.ACTIVITY_doing_the_laundry,
       Activity.ACTIVITY_gardening,
       Activity.ACTIVITY_running_an_errand,
       Activity.ACTIVITY_walking_the_dog,

       Activity.CATEGORY_eating,
       Activity.ACTIVITY_having_a_snack,
       Activity.ACTIVITY_having_breakfast,
       Activity.ACTIVITY_having_dinner,
       Activity.ACTIVITY_having_lunch,

       Activity.CATEGORY_exercising,
       Activity.ACTIVITY_cycling,
       Activity.ACTIVITY_dancing,
       Activity.ACTIVITY_hiking,
       Activity.ACTIVITY_jogging,
       Activity.ACTIVITY_playing_sports,
       Activity.ACTIVITY_running,
       Activity.ACTIVITY_skiing,
       Activity.ACTIVITY_swimming,
       Activity.ACTIVITY_working_out,

       Activity.CATEGORY_grooming,
       Activity.ACTIVITY_at_the_spa,
       Activity.ACTIVITY_brushing_teeth,
       Activity.ACTIVITY_getting_a_haircut,
       Activity.ACTIVITY_shaving,
       Activity.ACTIVITY_taking_a_bath,
       Activity.ACTIVITY_taking_a_shower,

       Activity.CATEGORY_having_appointment,

       Activity.CATEGORY_inactive,
       Activity.ACTIVITY_day_off,
       Activity.ACTIVITY_hanging_out,
       Activity.ACTIVITY_hiding,
       Activity.ACTIVITY_on_vacation,
       Activity.ACTIVITY_praying,
       Activity.ACTIVITY_scheduled_holiday,
       Activity.ACTIVITY_sleeping,
       Activity.ACTIVITY_thinking,

       Activity.CATEGORY_relaxing,
       Activity.ACTIVITY_fishing,
       Activity.ACTIVITY_gaming,
       Activity.ACTIVITY_going_out,
       Activity.ACTIVITY_partying,
       Activity.ACTIVITY_reading,
       Activity.ACTIVITY_rehearsing,
       Activity.ACTIVITY_shopping,
       Activity.ACTIVITY_smoking,
       Activity.ACTIVITY_socializing,
       Activity.ACTIVITY_sunbathing,
       Activity.ACTIVITY_watching_tv,
       Activity.ACTIVITY_watching_a_movie,

       Activity.CATEGORY_talking,
       Activity.ACTIVITY_in_real_life,
       Activity.ACTIVITY_on_the_phone,
       Activity.ACTIVITY_on_video_phone,

       Activity.CATEGORY_traveling,
       Activity.ACTIVITY_commuting,
       Activity.ACTIVITY_cycling,
       Activity.ACTIVITY_driving,
       Activity.ACTIVITY_in_a_car,
       Activity.ACTIVITY_on_a_bus,
       Activity.ACTIVITY_on_a_train,
       Activity.ACTIVITY_on_a_plane,
       Activity.ACTIVITY_on_a_trip,
       Activity.ACTIVITY_walking,

       Activity.CATEGORY_working,
       Activity.ACTIVITY_coding,
       Activity.ACTIVITY_in_a_meeting,
       Activity.ACTIVITY_studying,
       Activity.ACTIVITY_writing
    };

    private static Vector pep;
    private boolean isMood;

    public void show(boolean isMood) {
        this.isMood = isMood;
        if(isMood) {
          il = MoodIcons.getInstance();
          pep = null;
          pep = Moods.getInstance().moodValue;
        }
        else {
           il = ActivityIcons.getInstance();
            pep = new Vector(0);
            String text;
            for(int i=0; i<=77; i++) {
              if(i==0||i==4||i==14||i==19
                 ||i==29||i==36||i==37
                    ||i==46 || i==59||i==63||i==73) {
                text = '*' + activity[i];
                pep.addElement(text);
              }
              else {
                text = activity[i];
                pep.addElement(text);
              }
            }
           pep.addElement(Activity.no_activity);
        }

        int imgCnt = pep.size();
        realWidth = getWidth()-scrollbar.getScrollWidth();
        
        imgWidth = il.getWidth()+(CURSOR_HOFFSET*2);
        imgHeight = il.getHeight()+(CURSOR_VOFFSET*2);
        
        lineWidth = Math.max(imgWidth, Config.getInstance().minItemHeight);
        lineHeight = Math.max(imgHeight, Config.getInstance().minItemHeight);
        
        xCnt = realWidth / lineWidth;

        lines=imgCnt/xCnt;
        xLastCnt=imgCnt-lines*xCnt;
        if (xLastCnt>0) lines++; else xLastCnt=xCnt;

        xBorder=(realWidth-(xCnt*lineWidth))/2;

        updateMainBar();
        super.show();
     }

    public SelectPEP() {
        super(null);
    }

    public int getItemCount(){ return lines; }
    public VirtualElement getItemRef(int index){ lineIndex=index; return this;}

    public int getVWidth(){ return 0; }
    public int getVHeight() { return lineHeight; }
    public int getColor(){ return ColorTheme.getColor(ColorTheme.LIST_INK); }
    public int getColorBGnd(){ return ColorTheme.getColor(ColorTheme.LIST_BGND); }
    public String getTipString() { return (String) pep.elementAt(cursor*xCnt+xCursor); }
    public void onSelect(VirtualList view){  select();  }

    public void updateMainBar() {
        getMainBarItem().setElementAt(getTipString(), 0);
    }

    public void select() {
      if(isMood){
          if( ((String)Moods.getInstance().moodValue.lastElement()).equals(getTipString()) ) okNotify(null);
          else {
            InputTextBox input = new InputTextBox(SR.get(SR.MS_USERMOOD), Moods.getInstance().myMoodText, 100, TextField.ANY);
            input.setNotifyListener(this);
            input.show();
          }
      } else {
          publishActivity();
      }
    }

    private String category = null;
    private String descr;

    public void publishActivity(){
        descr = null;
        int index = pep.indexOf(getTipString());
        int type = 0;

          if(index==0||index==4||index==14||index==19
               ||index==29||index==36||index==37
                  ||index==46 || index==59||index==63||index==73||index==78) {
             //descr=null;
          } else{
             descr = Activity.NAMES[index];
          }

            if(index>=0 && index<4){ type=1; }
            else if(index>=4 && index<14){ type=2; }
            else if(index>=14 && index<19){ type=3; }
            else if(index>=19 && index<29){ type=4; }
            else if(index>=29 && index<36){ type=5; }
            else if(index==36){ type=6; }
            else if(index>=37 && index<46){ type=7; }
            else if(index>=46 && index<59){ type=8; }
            else if(index>=59 && index<63){ type=9; }
            else if(index>=63 && index<73){ type=10; }
            else if(index>=73 && index<78){ type=11; }
            else if(index==78){ type=12; }


        switch (type) {
                case 1: category="drinking"; break;
                case 2: category="doing_chores"; break;
                case 3: category="eating";  break;
                case 4: category="exercising"; break;
                case 5: category="grooming"; break;
                case 6: category="having_appointment"; break;
                case 7: category="inactive"; break;
                case 8: category="relaxing"; break;
                case 9: category="talking"; break;
                case 10: category="traveling"; break;
                case 11: category="working"; break;
                case 12: category=null; break;
        }
        if(type>0){
          new ActivityText(category, descr, getTipString()).show();
          return;
        }
    }

    public void okNotify(String moodText) {
        int index = pep.indexOf(getTipString());
        String moodName = Moods.getInstance().getMoodName(index);
        publishMood(moodText, moodName);
        BombusQD.sd.roster.show();
    }

    public void publishMood(final String moodText, final String moodName) {
        BombusQD.cf.moodName=moodName;
        BombusQD.cf.moodText=moodText;

        String sid = "publish-mood";
        JabberDataBlock setMood = new Iq(null, Iq.TYPE_SET, sid);
        JabberDataBlock action = setMood.addChildNs("pubsub","http://jabber.org/protocol/pubsub").addChild((moodText!=null)?"publish":"retract",null);
        action.setAttribute("node", "http://jabber.org/protocol/mood");
        JabberDataBlock item = action.addChild("item", null);
        item.setAttribute("id", Moods.getInstance().myMoodId);

        if (moodText!=null) {
            JabberDataBlock mood = item.addChildNs("mood", "http://jabber.org/protocol/mood");
            mood.addChild(moodName, null);
            mood.addChild("text",moodText);
        } else {
            item.addChild("retract", null);
            action.setAttribute("notify","1");
        }
        try {
            BombusQD.sd.roster.theStream.addBlockListener(new MoodPublishResult(sid));
            BombusQD.sd.roster.theStream.send(setMood);
            setMood=null;
            action=null;
            item=null;
        } catch (Exception e) {
//#ifdef DEBUG
//#             e.printStackTrace();
//#endif
        }
       BombusQD.cf.saveToStorage();//?
   }

    public void drawItem(VirtualList view, Graphics g, int ofs, boolean selected){
        int max=(lineIndex==lines-1)? xLastCnt:xCnt;
        int index = 0;
        int x = 0;
        int y = 0;
        for (int i=0;i<max;i++) {
            index = lineIndex * xCnt + i;
            x = xBorder + CURSOR_HOFFSET + i * lineWidth + (lineWidth - imgWidth) / 2;
            y = CURSOR_VOFFSET + (lineHeight - imgHeight) / 2;
            il.drawImage(g, index, x, y);
        }
    }

    public void drawCursor (Graphics g, int x0, int y0, int width, int height){ //Tishka17
         int x=xBorder+(xCursor*lineWidth);
         g.setColor(getColorBGnd());
         g.fillRect(0,y0,width, height);
         super.drawCursor(g, x+x0,y0,lineWidth, lineHeight);

         updateMainBar();
     }

    protected void drawBalloon(final Graphics g, int balloon, final String text) {
        if (cursor==0) balloon+=lineHeight+Balloon.getHeight();
        int x=xBorder+(xCursor * lineWidth);
        int widthItem = FontCache.getFont(false, Config.baloonFont).stringWidth(text);
        int width = g.getClipWidth() - 10;
        if( widthItem + x > width) { //fix autoCorrect
           x = width - widthItem;
        }
        g.translate(x, balloon);
        Balloon.draw(g, text);
    }

    public void pageLeft(){
        if (xCursor>0) xCursor--;
        else {
            if (cursor==0) {
                keyDwn();
                pageLeft();
                return;
            }
            xCursor=xCnt-1;
            keyUp();
            setRotator();
        }
    }

    public void pageRight(){
        if ( xCursor < ( (cursor<lines-1)?(xCnt-1):(xLastCnt-1) ) ) {
            xCursor++;
            setRotator();
        } else {
            if (cursor==lines-1) return;
            xCursor=0;
            keyDwn();
        }
    }

    public void keyDwn(){
        super.keyDwn();
        if (cursor!=lines-1) return;
        if (xCursor >= xLastCnt) xCursor=xLastCnt-1;
    }

    public void cmdOk() {
        select();
    }

    public void moveCursorEnd() {
        super.moveCursorEnd();
        xCursor=xLastCnt-1;
    }

    public void moveCursorHome() {
        super.moveCursorHome();
        xCursor=0;
    }

//#ifdef TOUCH
    protected void pointerPressed(int x, int y) {
        super.pointerPressed(x,y);
        if (pointer_state != POINTER_SECOND &&
                pointer_state != POINTER_FIRST &&
                pointer_state != POINTER_NONE) {
            return;
        }
        if (x>=xCnt * lineWidth) return;
        if (pointer_state == POINTER_SECOND && xCursor!= x / lineWidth)
            pointer_state = POINTER_NONE;
        xCursor=x / lineWidth;
        setRotator();
        if (cursor!=lines-1) return;
        if (xCursor >= xLastCnt) {
            xCursor=xLastCnt-1;
        }
    }
//#endif

    public boolean isSelectable() {
        return true;
    }

    public boolean handleEvent(int keyCode) {
        return false;
    }
    
    public boolean handleEvent(int x, int y) {
        return false;
    } 

    public String touchLeftCommand() {
        return SR.get(SR.MS_SELECT);
    }
}
//#endif
