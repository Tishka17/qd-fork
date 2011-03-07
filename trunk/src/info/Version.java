/*
 * Version.java
 *
 * Created on 23.04.2005, 22:44
 * Copyright (c) 2005-2008, Eugene Stahov (evgs), http://bombus-im.org
 *
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
 *
 */

package info;

import client.Config;
import locale.SR;

public class Version {
    public static String version = "$MIDLETVERSION$ (r$BOMBUSVERSION$)" + midlet.BombusQD.cf.getStringProperty("build", "") ;
    public final static String NAME="BombusQD";
    public final static String BOMBUS_SITE_URL="http://bombusmod-qd.wen.ru";

    public static String getBuildNum () {
        String build=Config.getInstance().getStringProperty("BombusQD-Build", "0");
        return (build!=null && !build.equals("0"))?" ["+build+"]":"";
    }

    public static String getVersionLang() {
        return version+" ("+SR.get(SR.MS_IFACELANG)+")"+getBuildNum();
    }

    public static String getVersionNumber() { return version; }
    public static String getName() { return NAME; }
    public static String getNameVersion() { return NAME+" "+version; }

    public static String getUrl() { return BOMBUS_SITE_URL; }


}
