/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

/**
 *
 * @author tishka17
 */
public class KineticScroller  extends Thread {
    private static final int update_delta = 10;
   // private static final long inverse_acceleration = 100000000;
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
        stop();
        y0=y1=list.win_top;
        t0=t1=System.currentTimeMillis();
        
        System.out.println("i0:" + y0);
        this.list = list;
    }
    
    public void updatePostion() {
        long t = System.currentTimeMillis(); 
        //System.out.println("u0:" + list.win_top + " at " + (t1-t0));
        if ((t-t1) < 50 && y1!=y0)
            return;
        
        y0 = y1;
        y1 = list.win_top;
        t0 = t1;
        t1 = t;
        //System.out.println("u1:" + y1 + " at " + (t1-t0));
    }
    
    public boolean startScroll() {
        if (VirtualList.pointer_state != VirtualList.POINTER_DRAG)
            return false;
        velocity = (y1-y0);
        if (t1 == t0 || velocity == 0)
            return false;
        stop();
        stopped = false;
        //start();
        return true;
    }
    
    public void stop() {
        System.out.println("Stop");
        synchronized (this) {
            //interrupt();
            stopped = true;
            notify();
        }
        System.out.println("Stop done");
    }
    
    private void update() {
        long t = System.currentTimeMillis();
        long y = y1 + (y1 - y0) * (t - t1) / (t1 - t0);
                /* + (t - t1) * (t - t1) /2 / inverse_acceleration*/
        System.out.println(y + " at " + (t-t1));
        long new_velocity = velocity;
                /*+ (t - t1)* (t1 - t0) / inverse_acceleration */
        if ((velocity>=0 && new_velocity<=0) || 
                (velocity<=0 && new_velocity>=0) || 
                y<0 || 
                y + list.getHeight() > list.getListHeight()) {
            stop();
            return;
        }
        list.win_top = (int)y;
        //FIXME: грязный хак по поиску хоть какого нибудь итема на экране
        list.stopRotator();
        list.redraw();
    }
    
    public void run()  {
        System.out.println("Run");
        synchronized (this) {
            try {
                wait(update_delta);
            } catch (InterruptedException ex) {
            }
            while (true) {
                if (!stopped) update();
                try {  wait(update_delta);  } catch (Exception e) { stop(); break; }
            }
        }
        System.out.println("Run ended");
    }
}
