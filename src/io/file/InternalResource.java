package io.file;

import java.io.InputStream;
//#if Android
//# import org.bombusqd.BombusQDActivity;
//# import java.io.IOException;
//#else
import midlet.BombusQD;
//#endif

/**
 *
 * @author Totktonada
 */
public class InternalResource {
    public static InputStream getResourceAsStream(String resource) {
        InputStream in = null;

//#if Android
//#         try {
//#             in = BombusQDActivity.getInstance().getAssets().open(resource.substring(1));
//#         } catch (IOException e) {
//#         }
//#else
    	in = BombusQD.getInstance().getClass().getResourceAsStream(resource);
//#endif

        return in;
    }
}
