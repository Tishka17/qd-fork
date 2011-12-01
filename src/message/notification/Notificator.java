/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package message.notification;
//#if Android
//# import android.app.NotificationManager;
//#endif
/**
 *
 * @author Vitaly
 */
public interface Notificator {

    public void sendNotify(String title, String text);
//#if Android
//#     public NotificationManager getNotificationManager();
//#endif

}