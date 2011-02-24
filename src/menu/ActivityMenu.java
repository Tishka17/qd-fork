package menu;

/*
 * ActivityMenu.java
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
 
 
//#ifdef PEP 
//# package Menu;
//# import ui.controls.form.MultiLine;
//# import Client.Config;
//# import Client.StaticData;
//# import javax.microedition.lcdui.*;
//# import locale.Activity;
//# import images.ActivityIcons;
//# 
//# public class ActivityMenu extends Menu {
//#     
//#     Config cf;
//#     StaticData sd=StaticData.getInstance();
//#     Activity ac = new Activity();
//#     
//#      private String[] activity = {
//#        ac.CATEGORY_drinking ,             ac.ACTIVITY_having_a_beer,
//#                                           ac.ACTIVITY_having_coffee,ac.ACTIVITY_having_tea,
//#        
//#        ac.CATEGORY_doing_chores,          ac.ACTIVITY_buying_groceries,ac.ACTIVITY_cleaning,ac.ACTIVITY_cooking,ac.ACTIVITY_doing_maintenance,
//#                                           ac.ACTIVITY_doing_the_dishes,ac.ACTIVITY_doing_the_laundry,ac.ACTIVITY_gardening,
//#                                           ac.ACTIVITY_running_an_errand,ac.ACTIVITY_walking_the_dog,
//#        
//#        ac.CATEGORY_eating,                ac.ACTIVITY_having_a_snack,ac.ACTIVITY_having_breakfast,
//#                                           ac.ACTIVITY_having_dinner,ac.ACTIVITY_having_lunch,
//#                
//#        ac.CATEGORY_exercising,            ac.ACTIVITY_cycling,ac.ACTIVITY_dancing,ac.ACTIVITY_hiking,ac.ACTIVITY_jogging,
//#                                           ac.ACTIVITY_playing_sports,ac.ACTIVITY_running,ac.ACTIVITY_skiing,ac.ACTIVITY_swimming,
//#                                           ac.ACTIVITY_working_out,
//#        
//#        ac.CATEGORY_grooming,              ac.ACTIVITY_at_the_spa,ac.ACTIVITY_brushing_teeth,ac.ACTIVITY_getting_a_haircut,
//#                                           ac.ACTIVITY_shaving,ac.ACTIVITY_taking_a_bath,ac.ACTIVITY_taking_a_shower,
//#        
//#        ac.CATEGORY_having_appointment,
//#        
//#        
//#        ac.CATEGORY_inactive,              ac.ACTIVITY_day_off,ac.ACTIVITY_hanging_out,ac.ACTIVITY_hiding,ac.ACTIVITY_on_vacation,
//#                                           ac.ACTIVITY_praying,ac.ACTIVITY_scheduled_holiday,ac.ACTIVITY_sleeping,ac.ACTIVITY_thinking,
//#        
//#        ac.CATEGORY_relaxing,              ac.ACTIVITY_fishing,ac.ACTIVITY_gaming,ac.ACTIVITY_going_out,ac.ACTIVITY_partying,
//#                                           ac.ACTIVITY_reading,ac.ACTIVITY_rehearsing,ac.ACTIVITY_shopping,ac.ACTIVITY_smoking, ac.ACTIVITY_socializing,
//#                                           ac.ACTIVITY_sunbathing,ac.ACTIVITY_watching_tv,ac.ACTIVITY_watching_a_movie,
//#        
//#        ac.CATEGORY_talking,               ac.ACTIVITY_in_real_life,ac.ACTIVITY_on_the_phone,ac.ACTIVITY_on_video_phone,
//#        
//#        
//#        ac.CATEGORY_traveling,             ac.ACTIVITY_commuting,ac.ACTIVITY_cycling,ac.ACTIVITY_driving,ac.ACTIVITY_in_a_car,
//#                                           ac.ACTIVITY_on_a_bus,ac.ACTIVITY_on_a_plane,ac.ACTIVITY_on_a_train,ac.ACTIVITY_on_a_trip,
//#                                           ac.ACTIVITY_walking,
//#        
//#        ac.CATEGORY_working,               ac.ACTIVITY_coding,ac.ACTIVITY_in_a_meeting,ac.ACTIVITY_studying,ac.ACTIVITY_writing
//#                
//#     };  
//#             
//#     public ActivityMenu(Display display, Displayable pView) {
//#         super(locale.SR.get(locale.SR.MS_ACTIVITY), ActivityIcons.getInstance(),null);
//# 
//#         cf=Config.getInstance();
//#         boolean connected=sd.roster.isLoggedIn();
//# 
//#         for(int i=0; i<=77; i++) {
//#           if(i==0||i==4||i==14||i==19
//#                ||i==29||i==36||i==37
//#                   ||i==46 || i==59||i==63||i==73) {
//#               addItem("--"+activity[i] ,i, i, false);
//#           }else{
//#             addItem(activity[i] ,i, i, true);
//#           }
//#         }
//#         addItem(ac.no_activity, 100);
//#         moveCursorTo(Config.getInstance().cursorPos[2]);        
//#         attachDisplay(display);
//#         this.parentView=pView;
//#     }
//#     
//#     
//#     private String category = null;
//#     private String descr = null;    
//#     private int type=-1;
//# 
//# 
//#     public void eventOk(){
//#         destroyView();
//#         boolean connected= ( sd.roster.isLoggedIn() );
//#         MenuItem me=(MenuItem) getFocusedObject();
//#         if (me==null)  return;
//#         int index=me.index;
//#         int type = 0;
//#         Config.getInstance().cursorPos[2]=cursor;
//#         
//#         System.out.println(index);
//#           if(index==0||index==4||index==14||index==19
//#                ||index==29||index==36||index==37
//#                   ||index==46 || index==59||index==63||index==73||index==100) {
//#             //descr = null;
//#           }else{
//#              descr = ac.NAMES[index];
//#           }
//#             
//#             if(index>=0 && index<4){ type=1; }
//#             else if(index>=4 && index<14){ type=2; }    
//#             else if(index>=14 && index<19){ type=3; }            
//#             else if(index>=19 && index<29){ type=4; }            
//#             else if(index>=29 && index<36){ type=5; }
//#             else if(index==36){ type=6; }
//#             else if(index>=37 && index<46){ type=7; }            
//#             else if(index>=46 && index<59){ type=8; }    
//#             else if(index>=59 && index<63){ type=9; }            
//#             else if(index>=63 && index<73){ type=10; }
//#             else if(index>=73 && index<78){ type=11; }
//#             else if(index==100){ type=12; }
//# 
//# 
//#       switch (type) {
//#                 case 1: category="drinking"; break;
//#                 case 2: category="doing_chores"; break;
//#                 case 3: category="eating";  break;
//#                 case 4: category="exercising"; break;
//#                 case 5: category="grooming"; break;
//#                 case 6: category="having_appointment"; break;
//#                 case 7: category="inactive"; break;
//#                 case 8: category="relaxing"; break;
//#                 case 9: category="talking"; break; 
//#                 case 10: category="traveling"; break;  
//#                 case 11: category="working"; break;    
//#                 case 12: category=null; break;                                                                
//#       }
//#       if(type>0){
//#                   System.out.println(type);
//#           new ActivityText(display,parentView,category,descr);
//#           return;
//#          //publishTune(category,descr);
//#       }
//#     }
//#  }
//#endif
*/
 