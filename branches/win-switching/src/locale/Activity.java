/*
 * Activity.java
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
//# package locale;
//# 
//# import client.Config;
//# import java.util.Hashtable;
//# import util.StringLoader;
//# 
//# public final class Activity {
//# 
//#     public   static String CATEGORY_drinking = loadString( "drinking" );
//#     public   static String ACTIVITY_having_a_beer = loadString( "having_a_beer" );
//#     public   static String ACTIVITY_having_coffee = loadString( "having_coffee" );
//#     public   static String ACTIVITY_having_tea = loadString( "having_tea" );
//# 
//# 
//#     public   static String CATEGORY_doing_chores = loadString( "doing_chores" );
//#     public   static String ACTIVITY_buying_groceries = loadString( "buying_groceries" );
//#     public   static String ACTIVITY_cleaning = loadString( "cleaning" );
//#     public   static String ACTIVITY_cooking = loadString( "cooking" );
//#     public   static String ACTIVITY_doing_maintenance = loadString( "doing_maintenance" );
//#     public   static String ACTIVITY_doing_the_dishes = loadString( "doing_the_dishes" );
//#     public   static String ACTIVITY_doing_the_laundry = loadString( "doing_the_laundry" );
//#     public   static String ACTIVITY_gardening = loadString( "gardening" );
//#     public   static String ACTIVITY_running_an_errand = loadString( "running_an_errand" );
//#     public   static String ACTIVITY_walking_the_dog = loadString( "walking_the_dog" );
//# 
//# 
//#     public   static String CATEGORY_eating = loadString( "eating" );
//#     public   static String ACTIVITY_having_a_snack = loadString( "having_a_snack" );
//#     public   static String ACTIVITY_having_breakfast = loadString( "having_breakfast" );
//#     public   static String ACTIVITY_having_dinner = loadString( "having_dinner" );
//#     public   static String ACTIVITY_having_lunch = loadString( "having_lunch" );
//# 
//#     public   static String CATEGORY_exercising = loadString( "exercising" );
//#     public   static String ACTIVITY_cycling = loadString( "cycling" );
//#     public   static String ACTIVITY_dancing = loadString( "dancing" );
//#     public   static String ACTIVITY_hiking = loadString( "hiking" );
//#     public   static String ACTIVITY_jogging = loadString( "jogging" );
//#     public   static String ACTIVITY_playing_sports = loadString( "playing_sports" );
//#     public   static String ACTIVITY_running = loadString( "running" );
//#     public   static String ACTIVITY_skiing = loadString( "skiing" );
//#     public   static String ACTIVITY_swimming = loadString( "swimming" );
//#     public   static String ACTIVITY_working_out = loadString( "working_out" );
//# 
//# 
//#     public   static String CATEGORY_grooming = loadString( "grooming" );
//#     public   static String ACTIVITY_at_the_spa = loadString( "at_the_spa" );
//#     public   static String ACTIVITY_brushing_teeth = loadString( "brushing_teeth" );
//#     public   static String ACTIVITY_getting_a_haircut = loadString( "getting_a_haircut" );
//#     public   static String ACTIVITY_shaving = loadString( "shaving" );
//#     public   static String ACTIVITY_taking_a_bath = loadString( "taking_a_bath" );
//#     public   static String ACTIVITY_taking_a_shower = loadString( "taking_a_shower" );
//# 
//#     public   static String CATEGORY_having_appointment = loadString( "having_appointment" );
//# 
//# 
//#     public   static String CATEGORY_inactive = loadString( "inactive" );
//#     public   static String ACTIVITY_day_off = loadString( "day_off" );
//#     public   static String ACTIVITY_hanging_out = loadString( "hanging_out" );
//#     public   static String ACTIVITY_hiding = loadString( "hiding" );
//#     public   static String ACTIVITY_on_vacation = loadString( "on_vacation" );
//#     public   static String ACTIVITY_praying = loadString( "praying" );
//#     public   static String ACTIVITY_scheduled_holiday = loadString( "scheduled_holiday" );
//#     public   static String ACTIVITY_sleeping = loadString( "sleeping" );
//#     public   static String ACTIVITY_thinking = loadString( "thinking" );
//# 
//# 
//#     public   static String CATEGORY_relaxing = loadString( "relaxing" );
//#     public   static String ACTIVITY_fishing = loadString( "fishing" );
//#     public   static String ACTIVITY_gaming = loadString( "gaming" );
//#     public   static String ACTIVITY_going_out = loadString( "going_out" );
//#     public   static String ACTIVITY_partying = loadString( "partying" );
//#     public   static String ACTIVITY_reading = loadString( "reading" );
//#     public   static String ACTIVITY_rehearsing = loadString( "rehearsing" );
//#     public   static String ACTIVITY_shopping = loadString( "shopping" );
//#     public   static String ACTIVITY_smoking = loadString( "smoking" );
//#     public   static String ACTIVITY_socializing = loadString( "socializing" );
//#     public   static String ACTIVITY_sunbathing = loadString( "sunbathing" );
//#     public   static String ACTIVITY_watching_tv = loadString( "watching_tv" );
//#     public   static String ACTIVITY_watching_a_movie = loadString( "watching_a_movie" );
//# 
//# 
//# 
//#     public   static String CATEGORY_talking = loadString( "talking" );
//#     public   static String ACTIVITY_in_real_life = loadString( "in_real_life" );
//#     public   static String ACTIVITY_on_the_phone = loadString( "on_the_phone" );
//#     public   static String ACTIVITY_on_video_phone = loadString( "on_video_phone" );
//# 
//# 
//#     public   static String CATEGORY_traveling = loadString( "traveling" );
//#     public   static String ACTIVITY_commuting = loadString( "commuting" );
//#    //public   static String ACTIVITY_cycling = loadString( "cycling" );
//#     public   static String ACTIVITY_driving = loadString( "driving" );
//#     public   static String ACTIVITY_in_a_car = loadString( "in_a_car" );
//#     public   static String ACTIVITY_on_a_bus = loadString( "on_a_bus" );
//#     public   static String ACTIVITY_on_a_plane = loadString( "on_a_plane" );
//#     public   static String ACTIVITY_on_a_train = loadString( "on_a_train" );
//#     public   static String ACTIVITY_on_a_trip = loadString( "on_a_trip" );
//#     public   static String ACTIVITY_walking = loadString( "walking" );
//# 
//# 
//#     public   static String CATEGORY_working = loadString( "working" );
//#     public   static String ACTIVITY_coding = loadString( "coding" );
//#     public   static String ACTIVITY_in_a_meeting = loadString( "in_a_meeting" );
//#     public   static String ACTIVITY_studying = loadString( "studying" );
//#     public   static String ACTIVITY_writing = loadString( "writing" );
//# 
//#     public   static String no_activity = loadString( "no activity" );
//# 
//#     public Activity() { }
//# 
//#     public static String getString(String key) {
//#         if (action==null) {
//#            String value=(String)action.get(key);
//#            return (value==null)?key:value;
//#         }
//#        return "error";
//#     }
//# 
//#     private static Hashtable action;
//# 
//#     public static final String[] NAMES = {
//#        "drinking",
//#          "having_a_beer",
//#          "having_coffee",
//#          "having_tea",
//#       "doing_chores",
//#          "buying_groceries",
//#          "cleaning",
//#          "cooking",
//#          "doing_maintenance",
//#          "doing_the_dishes",
//#          "doing_the_laundry",
//#          "gardening",
//#          "running_an_errand",
//#          "walking_the_dog",
//#       "eating",
//#          "having_a_snack",
//#          "having_breakfast",
//#          "having_dinner",
//#          "having_lunch",
//#        "exercising",
//#          "cycling",
//#          "dancing",
//#          "hiking",
//#          "jogging",
//#          "playing_sports",
//#          "running",
//#          "skiing",
//#          "swimming",
//#          "working_out",
//#        "grooming",
//#          "at_the_spa",
//#          "brushing_teeth",
//#          "getting_a_haircut",
//#          "shaving",
//#          "taking_a_bath",
//#          "taking_a_shower",
//#          "having_appointment",
//#        "inactive",
//#          "day_off",
//#          "hanging_out",
//#          "hiding",
//#          "on_vacation",
//#          "praying",
//#          "scheduled_holiday",
//#          "sleeping",
//#          "thinking",
//#        "relaxing",
//#          "fishing",
//#          "gaming",
//#          "going_out",
//#          "partying",
//#          "reading",
//#          "rehearsing",
//#          "shopping",
//#          "smoking",
//#          "socializing",
//#          "sunbathing",
//#          "watching_tv",
//#          "watching_a_movie",
//#        "talking",
//#          "in_real_life",
//#          "on_the_phone",
//#          "on_video_phone",
//#         "traveling",
//#          "commuting",
//#          "cycling",
//#          "driving",
//#          "in_a_car",
//#          "on_a_bus",
//#          "on_a_train",
//#          "on_a_plane",
//#          "on_a_trip",
//#          "walking",
//#         "working",
//#          "coding",
//#          "in_a_meeting",
//#          "studying",
//#          "writing"
//#     };
//# 
//#     public static byte getIconIndex(String str) {
//#         int len = NAMES.length;
//#         for (int i=0; i<len;i++) {
//#             if(str.indexOf(NAMES[i])>-1) return (byte)i;
//#         }
//#         return -1;
//#     }
//# 
//#     public static String loadString(String key) {
//#         if (action==null) {
//#             String file="/lang/"+midlet.BombusQD.cf.getInstance().lang+".activity.txt";
//#ifdef DEBUG_CONSOLE
//#             if(midlet.BombusQD.cf.debug) midlet.BombusQD.debug.add("::loadActivity->"+file,10);
//#endif
//#             //System.out.println(file);
//#             action=new StringLoader().hashtableLoader(file);
//#             if (action==null) {
//#                 action=new Hashtable(0);
//#             }
//#         }
//#         String value=(String)action.get(key);
//#         return (value==null)?key:value;
//#     }
//# 
//#     public static void loaded() {
//#         action.clear();
//#         action=null;
//#     }
//# }
//#endif
