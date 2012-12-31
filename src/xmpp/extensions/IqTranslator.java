/*
 * IqTranslate.java
 *
 * Created on 23.Nov.2012, 21:15
 *
 * Copyright (c) 2012, Andrey Nikiforov (Mars)
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
 */

//#ifdef TRANSLATE
package xmpp.extensions;

import client.Contact;
import client.Msg;
import client.Roster;
import client.StaticData;
import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.*;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.TextField;
import javax.microedition.lcdui.TextBox;
import locale.SR;
import util.Time;

/**
 *
 * @author Mars
 */

/*
<iq type="get" to="mozzzgi@localhost/BQDIQT" id="abcde">
<query xmlns="bombusQD:iq:translate">
текст for перевода
text для translation
</query>
</iq>

<iq from="mozzzgi@localhost/BQDIQT" type="result" to="admin@localhost/Psi+" id="abcde">
<query xmlns="bombusQD:iq:translate" translator="google"> the text for translation text for translation </query>
</iq>
*/


public class IqTranslator implements JabberBlockListener {
    public static final String BQDIQT= "bombusQD:iq:translate";
    protected static Object o= null;
    public static String bot= "bqdiqt@jabbon.ru/BQDIQT";
    public static String sLang= "en";
    public static String tLang= "ru";
    public static String sLangR= "ru";
    public static String tLangR= "en";

    public IqTranslator( ){ }
    public void destroy( ){ }

    public static JabberDataBlock query( String id, String text, boolean rev){
        JabberDataBlock result=new Iq( bot, Iq.TYPE_GET, id);
        result.addChildNs("query", BQDIQT).setText(text);
        result.getChildBlock("query").setAttribute("sLang", rev?sLangR:sLang);
        result.getChildBlock("query").setAttribute("tLang", rev?tLangR:tLang);
        return result;
    }

    public static JabberDataBlock queryAndPaste( Object obj, String text){
        o= obj;
        //((TextBox)o).getTicker().setString( "Translation!");
        return query( BQDIQT, text, true);
    }

    public int blockArrived( JabberDataBlock data) {
        if (!(data instanceof Iq)) return BLOCK_REJECTED;
        String type=data.getTypeAttribute();

        if (type.equals("result")) {
            JabberDataBlock query=data.findNamespace("query", BQDIQT);
            if (query==null) return BLOCK_REJECTED;

            String body=null;
            JabberDataBlock tm=data.getChildBlock("query");
            if (tm !=null) body=tm.getText();

            if( (o instanceof Object) 
                //&& data.getAttribute("id") ==BQDIQT
            ){
                ((TextBox)o).setString( body);
                //((TextBox)o).getTicker().setString( "Done");
                o= null;
                return BLOCK_PROCESSED;
            }// if

            Roster roster=StaticData.getInstance().roster;
            Contact c=roster.getContact( data.getAttribute("id"), false);
            c.setIncoming(Roster.INC_VIEWING);

            roster.querysign=false;
            Msg m=new Msg(Msg.PRESENCE, "", "", body);
            roster.messageStore(c, m);
            roster.redraw();
            return BLOCK_PROCESSED;
        }
        return BLOCK_REJECTED;
    }
}
//#endif //TRANSLATE