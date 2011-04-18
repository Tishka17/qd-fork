/*
 * EntityCaps.java
 *
 * Created on 17 �?юнь 2007 г., 2:58
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package xmpp;

import client.*; 
import info.Version;
import alert.AlertCustomize;
import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Iq;
import com.ssttr.crypto.SHA1;
import locale.SR;
import java.util.Vector;

/**
 *
 * @author Evg_S,aqent
 */
public class EntityCaps implements JabberBlockListener{
    
    /** Creates a new instance of EntityCaps */
    public EntityCaps() {
        initCaps();
    }
    
    public void destroy() {
        ver = null;
    }
    
    public int blockArrived(JabberDataBlock data) {
        if (!(data instanceof Iq)) return BLOCK_REJECTED;
        if (!data.getTypeAttribute().equals("get")) return BLOCK_REJECTED;
        JabberDataBlock query=data.findNamespace("query", "http://jabber.org/protocol/disco#info");
        if (query==null) return BLOCK_REJECTED;
        String node=query.getAttribute("node");

        if (node!=null)
            if (!node.equals(BOMBUS_NAMESPACE+"#"+calcVerHash()))
                return BLOCK_REJECTED;

        JabberDataBlock result=new Iq(data.getAttribute("from"), Iq.TYPE_RESULT, data.getAttribute("id"));
        result.addChild(query);

        JabberDataBlock identity=query.addChild("identity", null);
        identity.setAttribute("category", BOMBUS_ID_CATEGORY);
        identity.setAttribute("type", BOMBUS_ID_TYPE);
        identity.setAttribute("name", "BombusQD");
        int size = features.size();
        for (int i=0; i<size; i++) {
            query.addChild("feature", null).setAttribute("var", (String) features.elementAt(i));
        }
        
        StaticData.getInstance().roster.theStream.send(result);
        result=null;
        identity=null;
        return BLOCK_PROCESSED;
    }
	
    public static String ver=null;

    public static String calcVerHash() {
        if (ver!=null) return ver;
        if (features.size()<1)
            initCaps();
        
        SHA1 sha1=new SHA1();
        sha1.init();
        
        //indentity
	sha1.updateASCII(BOMBUS_ID_CATEGORY+"/"+BOMBUS_ID_TYPE+"//");
        sha1.updateASCII(Version.NAME);
        sha1.updateASCII("<");
        
        for (int i=0; i<features.size(); i++) {
            sha1.updateASCII((String) features.elementAt(i));
            sha1.updateASCII("<");
        }
        
        sha1.finish();
        ver=sha1.getDigestBase64();
        
        return ver;
    }

    public static JabberDataBlock presenceEntityCaps() {
        JabberDataBlock c=new JabberDataBlock("c", null, null);
        c.setAttribute("xmlns", "http://jabber.org/protocol/caps");
        c.setAttribute("node", BOMBUS_NAMESPACE);//+'#'+Version.getVersionNumber());
        c.setAttribute("ver", calcVerHash());
        c.setAttribute("hash", "sha-1");
	System.out.println("HASH::: "+calcVerHash());
        return c;
    }
    
    private final static String BOMBUS_NAMESPACE=Version.getUrl()+"/caps";
    private final static String BOMBUS_ID_CATEGORY="client";
    private final static String BOMBUS_ID_TYPE="mobile";

    public static void initCaps() {
        features=null;
        features=new Vector(0);
        
        //features MUST be sorted
//#ifdef PEP
        if (midlet.BombusQD.cf.rcvactivity) {
            features.addElement("http://jabber.org/protocol/activity");
            features.addElement("http://jabber.org/protocol/activity+notify");
        }
//#endif
        features.addElement("http://jabber.org/protocol/caps");

        if (midlet.BombusQD.cf.eventComposing)
            features.addElement("http://jabber.org/protocol/chatstates"); //xep-0085
//#if SERVICE_DISCOVERY && ADHOC
        if (midlet.BombusQD.cf.adhoc)
            features.addElement("http://jabber.org/protocol/commands"); //xep-0050
//#endif
        features.addElement("http://jabber.org/protocol/disco#info");

        features.addElement("http://jabber.org/protocol/evil");//XEP-0076: Malicious Stanzas
//#if FILE_IO && FILE_TRANSFER
        if (midlet.BombusQD.cf.fileTransfer) {
            features.addElement("http://jabber.org/protocol/ibb");
        }
//#endif
//#ifdef PEP
         if (midlet.BombusQD.cf.sndrcvmood) {
            features.addElement("http://jabber.org/protocol/mood");
            features.addElement("http://jabber.org/protocol/mood+notify");
         }
//#endif
//#ifndef WMUC
        features.addElement("http://jabber.org/protocol/muc");
//#endif
//#if FILE_IO && FILE_TRANSFER
        if (midlet.BombusQD.cf.fileTransfer) {
            features.addElement("http://jabber.org/protocol/si");
            features.addElement("http://jabber.org/protocol/si/profile/file-transfer");
        }
//#endif
//#ifdef PEP
         if (midlet.BombusQD.cf.rcvtune) {
              features.addElement("http://jabber.org/protocol/tune");
              features.addElement("http://jabber.org/protocol/tune+notify");
         }
//#endif

        
        features.addElement("jabber:iq:time"); //DEPRECATED
        features.addElement("jabber:iq:version");
        features.addElement("jabber:x:data");
         //"jabber:x:event", //DEPRECATED
	if (AlertCustomize.getInstance().enableAttention)
		features.addElement("urn:xmpp:attention:0");//XEP-0224: Attention
        features.addElement("urn:xmpp:ping");
        if (midlet.BombusQD.cf.eventDelivery) 
            features.addElement("urn:xmpp:receipts"); //xep-0184

    }

    private static Vector features=new Vector(0);
}
