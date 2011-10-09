/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package message.notification;

import client.Config;

/**
 *
 * @author Vitaly
 */
public abstract class Notification {
    private static Notificator notifier;
    public static Notificator getNotificator() {
        if (notifier == null) {
//#if ANDROID
//#             notifier = new AndroidNotification();
//#endif
/**            switch (Config.getInstance().phoneManufacturer) {
                case Config.SONYE:
                    notifier = new SEMCNotificator();
                    break;
            }
*/
        }
        return notifier;
    }
}