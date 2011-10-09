/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package message.notification;
//#if Android
//# import android.app.Activity;
//# import android.app.NotificationManager;
//# import android.app.PendingIntent;
//# import android.content.Context;
//# import android.content.Intent;
//# import org.bombusqd.BombusQDActivity;
//# import org.bombusqd.R;
//# import client.Msg;
//#
//#
//# public class AndroidNotification implements Notificator {
//#
//#     private static final int NOTIFY_ID = 1;
//#
//#
//#     public void sendNotify(final String title, final String text) {
//#                 NotificationManager mNotificationManager = (NotificationManager) BombusQDActivity.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
//#                 long when = System.currentTimeMillis();
//#                 int icon = R.drawable.app_icon;
//#                 android.app.Notification notification = new android.app.Notification(icon, title, when);
//#                 Intent notificationIntent = new Intent(BombusQDActivity.getInstance(), AndroidNotification.class);
//#                 PendingIntent contentIntent = PendingIntent.getActivity(BombusQDActivity.getInstance(), 0, notificationIntent, 0);
//#                 notification.setLatestEventInfo(BombusQDActivity.getInstance().getApplicationContext(), title, Msg.clearNick(new StringBuffer(text)), contentIntent);
//#                 notification.flags |= android.app.Notification.FLAG_AUTO_CANCEL;
//#                 notification.defaults |= android.app.Notification.DEFAULT_LIGHTS;
//#                 //notification.defaults |= android.app.Notification.DEFAULT_VIBRATE;
//#                 mNotificationManager.notify(NOTIFY_ID, notification);
//#     }
//# }
//#endif