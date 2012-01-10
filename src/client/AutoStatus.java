/*
 * AutoStatus.java
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
package client;

import com.alsutton.jabber.datablocks.Presence;
import java.util.Timer;
import java.util.TimerTask;
import locale.SR;
import midlet.BombusQD;

public final class AutoStatus {
    private static final long TIMER_INTERVAL = 5000L;
    
    private Timer timer;
    
    private long timeAwayEvent = 0;
    private long timeXaEvent = 0;

    private boolean isAwaySet = false;
    private boolean isXaSet = false;
    private int prevStatus = Presence.PRESENCE_ONLINE;

    private static AutoStatus instance;

    public static AutoStatus getInstance() {
        if (instance == null) {
            instance = new AutoStatus();
        }
        return instance;
    }

    private AutoStatus() {}

    public void start() {
        if (timer == null) {
//#ifdef DEBUG
//#             System.out.println("[AutoStatus] start called => started");
//#endif
            reset();
            TimerTask task = new TimerTask() {
                public void run() {
                    if (!BombusQD.sd.roster.isLoggedIn() || !Config.module_autostatus) {
                        return;
                    }
                    
                    long timeAwayRemained = System.currentTimeMillis() - timeAwayEvent;
                    if (!isAwaySet && timeAwayRemained > 0) {
                        setAutoAway(SR.get(SR.MS_AUTO_AWAY));
                    }

                    long timeXaRemained = System.currentTimeMillis() - timeXaEvent;
                    if (!isXaSet && timeXaRemained > 0) {
                        setAutoXa(SR.get(SR.MS_AUTO_XA));
                    }
                }
            };
            timer = new Timer();
            timer.schedule(task, 0, TIMER_INTERVAL);
//#ifdef DEBUG
//#         } else {
//#             System.out.println("[AutoStatus] start called => already started");
//#endif
        }
    }
    
    public void reset() {
        if (isAwaySet) {
            restoreStatus();
        }

        long delay = Config.getInstance().autoAwayDelay * 60 * 1000L;
        
        timeAwayEvent = delay + System.currentTimeMillis();
        timeXaEvent = (delay * 2) + System.currentTimeMillis();
    }

    public void stop() {
        if (timer != null) {
//#ifdef DEBUG
//#             System.out.println("[AutoStatus] stop called => stopped");
//#endif
            timer.cancel();
            timer = null;            
            if (isAwaySet) {
                restoreStatus();
            }
//#ifdef DEBUG
//#         } else {
//#             System.out.println("[AutoStatus] stop called => already stopped");
//#endif
        }
    }
    
    public boolean active() {
        return timer != null;
    }

    public void userActivity(int awayType) {
        if (!Config.module_autostatus) {
            return;
        }

        if (Config.autoAwayType == awayType) {
            reset();
        }
    }
    
    public void appLocked() {
        if (Config.module_autostatus 
                && Config.autoAwayType == Config.AWAY_LOCK) {
            setAutoAway("Auto Status on KeyLock since %t");
        }
    }
    
    public void appUnlocked() {
        if (Config.module_autostatus 
                && Config.autoAwayType == Config.AWAY_LOCK) {
            restoreStatus();
        }
    }
    
    private void setAutoAway(String msg) { 
        synchronized(this) {
//#ifdef DEBUG
//#             System.out.println("[AutoStatus] setAutoAway called");
//#endif
            int status = BombusQD.sd.roster.myStatus;
            if (status == Presence.PRESENCE_ONLINE || status == Presence.PRESENCE_CHAT) {
                prevStatus = status;
                isAwaySet = true;
                if (!Config.setAutoStatusMessage) {
                    BombusQD.sd.roster.sendPresence(Presence.PRESENCE_AWAY, msg);
                } else {
                    ExtendedStatus es = StatusList.getInstance().getStatus(Presence.PRESENCE_AWAY);
                    BombusQD.sd.roster.sendPresence(Presence.PRESENCE_AWAY, es.getMessage());
                }
            }
        }
    }

    private void setAutoXa(String msg) {
        synchronized(this) {
//#ifdef DEBUG
//#             System.out.println("[AutoStatus] setAutoXa called");
//#endif
            if (isAwaySet) {
                isXaSet = true;
                if (!Config.setAutoStatusMessage) {
                    BombusQD.sd.roster.sendPresence(Presence.PRESENCE_XA, msg);
                } else {
                    ExtendedStatus es = StatusList.getInstance().getStatus(Presence.PRESENCE_XA);
                    BombusQD.sd.roster.sendPresence(Presence.PRESENCE_XA, es.getMessage());
                }
            }
        }
    }

    private void restoreStatus() {
        synchronized(this) {
//#ifdef DEBUG
//#             System.out.println("[AutoStatus] restoreStatus called");
//#endif
            ExtendedStatus status = StatusList.getInstance().getStatus(prevStatus);
            BombusQD.sd.roster.sendPresence(prevStatus, status.getMessage());

            isAwaySet = false;
            isXaSet = false;
        }
    }
}
//#endif
