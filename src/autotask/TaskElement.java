/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package autotask;

/**
 *
 * @author Mars
 */

public class TaskElement {
    public final static int TASK_TYPE_DISABLED = 0;
    public final static int TASK_TYPE_TIME = 1;
    public final static int TASK_TYPE_TIMER = 2;

    public final static int TASK_ACTION_QUIT = 0;
    public final static int TASK_ACTION_CONFERENCE_QUIT = 1;
    public final static int TASK_ACTION_LOGOFF = 2;
    public final static int TASK_ACTION_RECONNECT = 3;
    public final static int TASK_ACTION_LOGIN = 4;
    public final static int TASK_ACTION_CONFERENCE_JOIN = 5;

    public int Type= TASK_TYPE_DISABLED;
    public int Action = TASK_ACTION_QUIT;
    public long StartMS = 0;
    public int WaitMS = 0;
    public int Hour = 0;
    public int Minute = 0;
    public boolean Once = true;
    public boolean isRunned = false;
}