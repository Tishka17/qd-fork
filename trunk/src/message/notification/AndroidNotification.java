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
//# import client.Roster;
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
//#                 int icon = R.drawable.chat;
//#                 if (midlet.BombusQD.sd.roster.highliteMessageCount<1) {
//#                     return;
//#                 }else{
//#                 android.app.Notification notification = new android.app.Notification(icon, "Новых сообщений: "+midlet.BombusQD.sd.roster.highliteMessageCount, when);
//#                 Intent notificationIntent = new Intent(BombusQDActivity.getInstance(), BombusQDActivity.class);
//#                 PendingIntent contentIntent = PendingIntent.getActivity(BombusQDActivity.getInstance(), 0, notificationIntent, 0);
//#                 notification.setLatestEventInfo(BombusQDActivity.getInstance().getApplicationContext(), "У вас есть сообщения...", "Непрочитано сообщений: "+midlet.BombusQD.sd.roster.highliteMessageCount, contentIntent);
//#                 notification.flags |= android.app.Notification.FLAG_AUTO_CANCEL;
//#                 notification.ledARGB = 0xffffa500;
//#                 notification.ledOnMS = 300;
//#                 notification.ledOffMS = 1000;
//#                 notification.flags |= android.app.Notification.FLAG_SHOW_LIGHTS;
//#                 notification.number=midlet.BombusQD.sd.roster.highliteMessageCount;
//#                 mNotificationManager.notify(NOTIFY_ID, notification);
//#                 }
//#     }
//# }
//#endif