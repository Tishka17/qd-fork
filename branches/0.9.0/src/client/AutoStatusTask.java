/*
 * AutoStatusTask.java
 *
 * Created on 12.06.2007, 3:12
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
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
//#ifdef AUTOSTATUS 
//# package client;
//# 
//# import midlet.BombusQD;
//# 
//# public final class AutoStatusTask implements Runnable {
//#     private boolean stop;
//#     private long timeAwayEvent=0;
//#     private long timeXaEvent=0;
//#     private Thread thread;
//#     
//#     public AutoStatusTask(boolean restart) {
//#         destroyTask();
//#         if(thread != null) {
//#            thread = null;
//#         }
//#         thread = new Thread(this);
//#         thread.setPriority(Thread.MIN_PRIORITY);
//#         thread.start();
//#     }
//#     
//#     public void setTimeEvent(long delay){
//#         timeAwayEvent=delay+System.currentTimeMillis();
//#         timeXaEvent=(delay*2)+timeAwayEvent;
//#     }
//# 
//#     public void destroyTask(){
//#         stop=false;
//#     }
//# 
//#     public void run() {
//#         while (!stop) {
//#             try {
//#                 Thread.sleep(5000);
//#             } catch (InterruptedException ex) { 
//#                 stop = true;
//#             }
//#             if (timeAwayEvent == 0 && timeXaEvent == 0) {
//#                 continue;
//#             }
//#             
//#             long timeAwayRemained=System.currentTimeMillis()-timeAwayEvent;
//#             long timeXaRemained=System.currentTimeMillis()-timeXaEvent;
//# 
//#             if (timeAwayRemained > 0 && timeAwayEvent != 0) {
//#                 timeAwayEvent = 0;
//#                 BombusQD.sd.roster.setAutoAway();
//#             }
//# 
//#             if (timeXaRemained>0 && timeAwayEvent == 0) {
//#                 timeXaEvent = 0;
//#                 BombusQD.sd.roster.setAutoXa();
//#             }
//#         }
//#     }
//# 
//# }
//#endif