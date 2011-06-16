/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

/**
 *
 * @author tishka17
 */
//#ifdef TOUCH
public class KineticScroller  extends Thread {
    private static final int update_delta = 20;
    private static long inverse_acceleration = 20000;
    private long velocity = 0;
    private int y0=0, y1=0;
    private long t0=0, t1=0;
    
    private boolean  stopped = true;
    private VirtualList list;
    
    private KineticScroller() {
        super();
        start();
    }
    
    private static KineticScroller instance = new KineticScroller();
    
    public static KineticScroller getInstance() {
        if(instance==null)  
            instance = new KineticScroller();
        return instance;
    }
    public void initPosition(VirtualList list) {
        stopscrolling();
        y0=y1=list.win_top;
        t0=t1=System.currentTimeMillis();
        this.list = list;
    }
    
    public void updatePostion() {
        long t = System.currentTimeMillis(); 
        if ((t-t1) < 50 && y1!=y0)
            return;
        
        y0 = y1;
        y1 = list.win_top;
        t0 = t1;
        t1 = t;
    }
    
    public boolean startScroll() {
        if (VirtualList.pointer_state != VirtualList.POINTER_DRAG)
            return false;
        velocity = (y1-y0);
        if (t1 == t0 || velocity == 0)
            return false;
        stopscrolling();
        stopped = false;
        if (velocity>0) inverse_acceleration = -Math.abs(inverse_acceleration);
        else inverse_acceleration = Math.abs(inverse_acceleration);
        list.stopRotator();
        return true;
    }
    
    public void stopscrolling() {
        synchronized (this) {
            stopped = true;
            notify();
        }
    }
    
    private void update() {
        list.stickyWindow = false;
        long t = System.currentTimeMillis();
        long y = y1 + (y1 - y0) * (t - t1) / (t1 - t0) + (t - t1) * (t - t1) /2 / inverse_acceleration;
        long new_velocity = velocity + (t - t1)* (t1 - t0) / inverse_acceleration;
        if ((velocity>=0 && new_velocity<=0) || 
                (velocity<=0 && new_velocity>=0) || 
                y<0 || 
                y + list.getHeight() > list.getListHeight()) {
            stopscrolling();
            return;
        }
        list.win_top = (int)y;
        //list.stopRotator();
        list.redraw();
    }
    
    public void run()  {
        synchronized (this) {
            try {
                wait(update_delta);
            } catch (InterruptedException ex) {
            }
            while (true) {
                if (!stopped) update();
                try {  wait(update_delta);  } catch (Exception e) { stopscrolling(); break; }
            }
        }
    }
}
//#endif