package org.bombusqd;

import org.microemu.microedition.ImplementationInitialization;
import java.util.Map;
import android.content.Intent;
import android.widget.Toast;

/**
 * @author Totktonada
 */

public class BombusQDInitialization implements ImplementationInitialization {

    /*
     * (non-Javadoc)
     *
     * @see org.microemu.microedition.ImplementationInitialization#registerImplementation()
     */
    public void registerImplementation(Map parameters) {
    }

    /*
     * (non-Javadoc)
     *
     * @see org.microemu.microedition.ImplementationInitialization#notifyMIDletStart()
     */
    public void notifyMIDletStart() {
    }

    /*
     * (non-Javadoc)
     *
     * @see org.microemu.microedition.ImplementationInitialization#notifyMIDletDestroyed()
     */
    public void notifyMIDletDestroyed() {
		//BombusQDActivity.getInstance().stopService(new Intent(BombusQDActivity.getInstance(), BombusQDService.class)); //its temporarily
		BombusQDActivity.getInstance().finish();
    }

}
