/*
 * XDataForm.java
 *
 * Created on 6 Май 2008 г., 0:28
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package xmpp.extensions;

import com.alsutton.jabber.JabberDataBlock;
import java.util.*;
import javax.microedition.lcdui.*;
import locale.SR;
import util.Strconv;
import com.alsutton.jabber.datablocks.Iq;

/**
 *
 * @author root
 */
public class XDataForm implements CommandListener {

    private Display display;
    private Displayable parentView;

    private Command cmdOk;
    private Command cmdCancel;

    Vector items;

    Form f;
    /** Creates a new instance of XDataForm */

    String id;
    String from;

    public XDataForm(Display display, JabberDataBlock data, String id, String from) {
        this.display=display;
        this.id = id;
        this.from = from;
        this.parentView=display.getCurrent();

        cmdOk=new Command(SR.get(SR.MS_SEND), Command.OK /*Command.SCREEN*/, 1);
        cmdCancel=new Command(SR.get(SR.MS_BACK), Command.BACK, 99);
//#ifdef DEBUG_CONSOLE
//#         midlet.BombusQD.debug.add("captcha<<< " + data.toString(),10);
//#endif

        JabberDataBlock challenge = data.findNamespace("captcha", "urn:xmpp:captcha");
        JabberDataBlock xdata = challenge.findNamespace("x","jabber:x:data");
        JabberDataBlock oob = data.findNamespace("x","jabber:x:oob");

        String url = oob.getChildBlockText("url");
        String title = xdata.getChildBlockText("title");

        Vector xData = xdata.getChildBlocks();

        if(null == title) title = "";

        f=new Form(title);
        items=new Vector(0);

        if(url.length() > 0) {
           TextField formItem = new TextField("URL", url, url.length(),  TextField.UNEDITABLE);
           f.append(formItem);
        }

        XDataField field;
        String msgcid = "";

        int size = xData.size();
        for (int i = 0; i < size; ++i) {
            JabberDataBlock ch=(JabberDataBlock)xData.elementAt(i);

            if (ch.getTagName().equals("instructions")) {
                f.append(ch.getText());
                f.append("\n");
                continue;
            };
            if (!ch.getTagName().equals("field")) continue;

            field = new XDataField(ch);
            items.addElement(field);

            if (field.hidden) continue;

            if (field.media != null) msgcid = field.mediaUri.substring(4);
            f.append(field.formItem);
        }

        JabberDataBlock dataImage = data.findNamespace("data", "urn:xmpp:bob");
        String cid = dataImage.getAttribute("cid");
        if(cid.indexOf(msgcid) == 0) {
            try{
               byte[] bytes=Strconv.fromBase64(dataImage.getText());
               Image img = Image.createImage(bytes, 0, bytes.length);
               f.append(new ImageItem(null, img, Item.LAYOUT_CENTER, null));
            } catch(OutOfMemoryError eom) {
//#ifdef DEBUG_CONSOLE
//#                 midlet.BombusQD.debug.add("XDataForm OutOfMem",10);
//#endif
            }
            catch (Exception e) { }
        }

        f.setCommandListener(this);
        f.addCommand(cmdOk);
        f.addCommand(cmdCancel);
        display.setCurrent(f);
    }

    public void XDataFormSubmit(JabberDataBlock form) {
        JabberDataBlock reply = new Iq(from, Iq.TYPE_SET, id);
        reply.addChildNs("captcha", "urn:xmpp:captcha").addChild(form);
        midlet.BombusQD.sd.roster.theStream.send(reply);
    }

    public void commandAction(Command command, Displayable displayable) {
        if (command==cmdOk) {
            JabberDataBlock resultForm=new JabberDataBlock("x", null, null);
            resultForm.setNameSpace("jabber:x:data");
            resultForm.setTypeAttribute("submit");

            for (Enumeration e=items.elements(); e.hasMoreElements(); ) {
                JabberDataBlock ch=((XDataField)e.nextElement()).constructJabberDataBlock();
                if (ch!=null) resultForm.addChild(ch);
            }
            XDataFormSubmit(resultForm);
        }
        display.setCurrent(parentView);
    }

}