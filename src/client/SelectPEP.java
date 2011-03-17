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
import menu.ActivityText;
import locale.SR;
import colors.ColorTheme;
import ui.*;
import java.util.Vector;
import ui.controls.Balloon;
//#ifndef MENU_LISTENER
//# import javax.microedition.lcdui.CommandListener;
//# import javax.microedition.lcdui.Command;
//#else
import menu.MenuListener;
import menu.Command;
//#endif
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.TextField;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
//#ifdef GRAPHICS_MENU        
import ui.GMenu;
//#endif   
import mood.Moods;
import mood.MoodPublishResult;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Iq;
import locale.Activity;
import midlet.BombusQD;

/**
 *
 * @author evgs,aqent
 */

public final class SelectPEP extends VirtualList implements  
        MenuListener, VirtualElement, MIDPTextBox.TextBoxNotify
{

    private final static byte CURSOR_HOFFSET=1;
    private final static byte CURSOR_VOFFSET=1;
   
    private int lineIndex;
    private int imgCnt;
    private int xCnt;
    private int xLastCnt;
    private int xCursor;
    private int lines;
    private int lineHeight;
    private int imgWidth;
    private ImageList il;
    private int realWidth=0;
    private int xBorder = 0;
    
    
    Command cmdCancel;
    Command cmdOk;
     
    
    final static Activity ac = new Activity();
    final static String[] activity = {
       ac.CATEGORY_drinking ,             ac.ACTIVITY_having_a_beer,
                                          ac.ACTIVITY_having_coffee,ac.ACTIVITY_having_tea,
       
       ac.CATEGORY_doing_chores,          ac.ACTIVITY_buying_groceries,ac.ACTIVITY_cleaning,ac.ACTIVITY_cooking,ac.ACTIVITY_doing_maintenance,
                                          ac.ACTIVITY_doing_the_dishes,ac.ACTIVITY_doing_the_laundry,ac.ACTIVITY_gardening,
                                          ac.ACTIVITY_running_an_errand,ac.ACTIVITY_walking_the_dog,
       
       ac.CATEGORY_eating,                ac.ACTIVITY_having_a_snack,ac.ACTIVITY_having_breakfast,
                                          ac.ACTIVITY_having_dinner,ac.ACTIVITY_having_lunch,
               
       ac.CATEGORY_exercising,            ac.ACTIVITY_cycling,ac.ACTIVITY_dancing,ac.ACTIVITY_hiking,ac.ACTIVITY_jogging,
                                          ac.ACTIVITY_playing_sports,ac.ACTIVITY_running,ac.ACTIVITY_skiing,ac.ACTIVITY_swimming,
                                          ac.ACTIVITY_working_out,
       
       ac.CATEGORY_grooming,              ac.ACTIVITY_at_the_spa,ac.ACTIVITY_brushing_teeth,ac.ACTIVITY_getting_a_haircut,
                                          ac.ACTIVITY_shaving,ac.ACTIVITY_taking_a_bath,ac.ACTIVITY_taking_a_shower,
       
       ac.CATEGORY_having_appointment,
       
       
       ac.CATEGORY_inactive,              ac.ACTIVITY_day_off,ac.ACTIVITY_hanging_out,ac.ACTIVITY_hiding,ac.ACTIVITY_on_vacation,
                                          ac.ACTIVITY_praying,ac.ACTIVITY_scheduled_holiday,ac.ACTIVITY_sleeping,ac.ACTIVITY_thinking,
       
       ac.CATEGORY_relaxing,              ac.ACTIVITY_fishing,ac.ACTIVITY_gaming,ac.ACTIVITY_going_out,ac.ACTIVITY_partying,
                                          ac.ACTIVITY_reading,ac.ACTIVITY_rehearsing,ac.ACTIVITY_shopping,ac.ACTIVITY_smoking, ac.ACTIVITY_socializing,
                                          ac.ACTIVITY_sunbathing,ac.ACTIVITY_watching_tv,ac.ACTIVITY_watching_a_movie,
       
       ac.CATEGORY_talking,               ac.ACTIVITY_in_real_life,ac.ACTIVITY_on_the_phone,ac.ACTIVITY_on_video_phone,
       
       
       ac.CATEGORY_traveling,             ac.ACTIVITY_commuting,ac.ACTIVITY_cycling,ac.ACTIVITY_driving,ac.ACTIVITY_in_a_car,
                                          ac.ACTIVITY_on_a_bus,ac.ACTIVITY_on_a_train,ac.ACTIVITY_on_a_plane,ac.ACTIVITY_on_a_trip,
                                          ac.ACTIVITY_walking,
       
       ac.CATEGORY_working,               ac.ACTIVITY_coding,ac.ACTIVITY_in_a_meeting,ac.ACTIVITY_studying,ac.ACTIVITY_writing
               
    };  
    
    private static Vector pep;
    private boolean isMood;
    
    public void show(Displayable pView,boolean isMood) {
        
        mainbar = new MainBar(locale.SR.get(SR.MS_SELECT));
        setMainBarItem(mainbar);
        
        cmdCancel=new Command(SR.get(SR.MS_CANCEL),Command.BACK,99);
        cmdOk=new Command(SR.get(SR.MS_SELECT),Command.OK,1);
        
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
           pep.addElement(ac.no_activity);
        }
        
        imgCnt = pep.size();
        realWidth = getWidth()-scrollbar.getScrollWidth();
        imgWidth = il.getWidth()+(CURSOR_HOFFSET*2);
        lineHeight = il.getHeight()+(CURSOR_VOFFSET*2);
        xCnt = realWidth / imgWidth;
        
        lines=imgCnt/xCnt;
        xLastCnt=imgCnt-lines*xCnt;
        if (xLastCnt>0) lines++; else xLastCnt=xCnt;

        xBorder=(realWidth-(xCnt*imgWidth))/2;
        attachDisplay(display);
        this.parentView=pView;
    }
    
    
     /** Creates a new instance of SelectPEP */
    
    private MainBar mainbar;
    public SelectPEP(Display display) {
         mainbar = new MainBar(locale.SR.get(SR.MS_SELECT));
         setMainBarItem(mainbar);
         this.display = display;
    }
    
    public void commandState() {
//#ifdef MENU_LISTENER
        menuCommands.removeAllElements();
//#endif
        addCommand(cmdOk); cmdOk.setImg(0x43);
        setCommandListener(this);
    }
    

    public int getItemCount(){ return lines; }
    public VirtualElement getItemRef(int index){ lineIndex=index; return this;}
    
    public int getVWidth(){ return 0; }
    public int getVHeight() { return lineHeight; }
    public int getColor(){ return ColorTheme.getColor(ColorTheme.LIST_INK); }
    public int getColorBGnd(){ return ColorTheme.getColor(ColorTheme.LIST_BGND); }
    public String getTipString() { return (String) pep.elementAt(cursor*xCnt+xCursor); }
    public void onSelect(VirtualList view){  select();  }
    
    
    public void select() {
      if(isMood){
          if( ((String)Moods.getInstance().moodValue.lastElement()).equals(getTipString()) ) OkNotify(null); 
          else {
            midlet.BombusQD.cf.cursorPos[3]=cursor;            
            new MIDPTextBox(display, SR.get(SR.MS_USERMOOD), Moods.getInstance().myMoodText, this, TextField.ANY, 100);
          }
      } else publishActivity();
    }


    
    //******************************USER ACTIVITY PUBLISH******************************
    private String category = null;
    private String descr;    
    private int type=-1;
    
    public void publishActivity(){
        descr = null;
        int index = pep.indexOf(getTipString());
        int type = 0;
        midlet.BombusQD.cf.cursorPos[2]=cursor;
        
          if(index==0||index==4||index==14||index==19
               ||index==29||index==36||index==37
                  ||index==46 || index==59||index==63||index==73||index==78) {
             //descr=null;
          } else{
             descr = ac.NAMES[index];
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
          new ActivityText(display, BombusQD.sd.roster, category, descr, getTipString() );
          return;
        }
    }
    //******************************USER ACTIVITY PUBLISH******************************
    
    
    
    
    //******************************USER MOOD PUBLISH******************************
    public void OkNotify(String moodText) {
        int index = pep.indexOf(getTipString());
        String moodName = Moods.getInstance().getMoodName(index);
        publishMood(moodText, moodName);
        destroyView();
        midlet.BombusQD.sd.roster.showRoster();
    }
    
    
    public void publishMood(final String moodText, final String moodName) {
        midlet.BombusQD.cf.moodName=moodName;
        midlet.BombusQD.cf.moodText=moodText;
        
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
            midlet.BombusQD.sd.roster.theStream.addBlockListener(new MoodPublishResult(display, sid));           
            midlet.BombusQD.sd.roster.theStream.send(setMood);
            setMood=null;
            action=null;
            item=null;
        } catch (Exception e) {
            e.printStackTrace();
        }
       midlet.BombusQD.cf.saveToStorage();//?
   } 
    //******************************USER MOOD PUBLISH******************************
    
        
    
    public void drawItem(VirtualList view, Graphics g, int ofs, boolean selected){
        int max=(lineIndex==lines-1)? xLastCnt:xCnt;
        int index = 0;
        int x = 0;
        for (int i=0;i<max;i++) {
            index = lineIndex*xCnt + i;
            x = xBorder + CURSOR_HOFFSET + i*imgWidth;
            il.drawImage(g, index, x, CURSOR_VOFFSET);
        }
    }

    public void drawCursor (Graphics g, int x0, int y0, int width, int height){ //Tishka17
         int x=xBorder+(xCursor*imgWidth);
         g.setColor(getColorBGnd());
         g.fillRect(0,y0,width, height);
         super.drawCursor(g, x+x0,y0,imgWidth, lineHeight);
     } 

    protected void drawBalloon(final Graphics g, int balloon, final String text) {
        if (cursor==0) balloon+=lineHeight+Balloon.getHeight();
        int x=xBorder+(xCursor*imgWidth);
        int widthItem = FontCache.getFont(false, FontCache.baloon).stringWidth(text);
        int width = g.getClipWidth() - 10;
        if( widthItem + x > width) { //fix autoCorrect
           int dif = x - width;
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
    
    public void commandAction(Command c, Displayable d){
        if (c==cmdCancel) {
            destroyView();
            return;
        }
        if (c==cmdOk) eventOk();
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
        if (pointer_state != client.Constants.POINTER_SECOND && pointer_state != client.Constants.POINTER_NONE)
            return;
        if (x>=xCnt*imgWidth) return;
        if (pointer_state == client.Constants.POINTER_SECOND && xCursor!= x/imgWidth)
            pointer_state = client.Constants.POINTER_NONE;
        xCursor=x/imgWidth;
        setRotator();
        if (cursor!=lines-1) return;
        if (xCursor >= xLastCnt) xCursor=xLastCnt-1;
    }
//#endif

    public void userKeyPressed(int keyCode) {
        switch (keyCode) {
            case KEY_NUM3 :
                super.pageLeft(); keyDwn(); break;
            case KEY_NUM9:
                super.pageRight(); break;
            case KEY_NUM4:
                pageLeft();
                break;
            case KEY_NUM6:
                pageRight(); 
                break;
        }
        super.userKeyPressed(keyCode);
    }

    public boolean isSelectable() { 
        return true;
    }
    public boolean handleEvent(int keyCode) {
        return false;
    }
    
//#ifdef MENU_LISTENER
    
//#ifdef GRAPHICS_MENU        
    public int showGraphicsMenu() {
        commandState();
        menuItem = new GMenu(display, parentView, this, null, menuCommands);        
        GMenuConfig.getInstance().itemGrMenu=555;
        return 555;
    }
//#else
//#     public void showMenu(){ eventOk(); } 
//#endif     

     
    public String touchLeftCommand(){ return SR.get(SR.MS_SELECT); }
    public String touchRightCommand(){ return SR.get(SR.MS_BACK); }
//#endif
}
//#endif
