/*
 * MessageParser.java
 *
 * Created on 6.02.2005, 19:38
 * Copyright (c) 2005-2008, Eugene Stahov (evgs), http://bombus-im.org
 * Copyright (c) 2009, Alexej Kotov (aqent), http://bombusmod-qd.wen.ru
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

package message; 

//#ifdef SMILES
import images.SmilesIcons;
//#endif
import font.FontCache;
import java.io.*;
import javax.microedition.lcdui.Font;
import colors.ColorTheme;
import java.util.*;
import ui.*;
//import Client.Msg;
//import Client.Config;
import util.Strconv;
import images.RosterIcons;

public final class MessageParser // implements Runnable
{
    private final static int BOLD=-3;
    private final static byte URL=-2;
    private final static byte NOSMILE=-1;
    //Config cf = Config.getInstance();
    private Vector smileTable;

    private Leaf root;
    private Leaf emptyRoot;
    
    // Singleton
    private static MessageParser instance=null;
    
    private int width; // window width
//#ifdef SMILES 
    private ImageList smileImages;
//#endif
    
    //private Vector tasks=new Vector();
    //private Thread thread;
    
    boolean wordsWrap;
    private static String wrapSeparators=" .,-=/\\;:+()[]<>~!@#%^_&";
    public static boolean animated = false;
    
    public static void restart() {
       if(instance!=null) instance = null;
       animated = midlet.BombusQD.cf.animatedSmiles;
//#ifdef CONSOLE 
//#        //midlet.BombusQD.debug.add("::restart()"+(animated?"/images/smiles/ani_smiles.txt":"/images/smiles/smiles.txt"),10);//se error
//#endif
       getInstance();
    }
    
    public static MessageParser getInstance() {
        if (instance==null)
            instance=new MessageParser(animated?"/images/smiles/ani_smiles.txt":"/images/smiles/smiles.txt");
        return instance;
    }

//#ifdef SMILES 
    public Vector getSmileTable() { return smileTable; }
//#endif
    
    private static class Leaf {
        public int smile=NOSMILE;
        private String smileChars;
        private Vector child;

        public Leaf() {
            child=new Vector(0);
            smileChars=new String("");
        }
        
        public Leaf findChild(char c){
            int index=smileChars.indexOf(c);
            return (index==-1)?null:(Leaf)child.elementAt(index);
        }

        private void addChild(char c, Leaf child){
            this.child.addElement(child);
            smileChars=smileChars+c;
        }
    }
  
    private void addSmile(Leaf rootSmile, String smile, int index) {
	Leaf p=rootSmile;
	Leaf p1;
	
	int len=smile.length();
	for (int i=0; i<len; i++) {
	    char c=smile.charAt(i);
	    p1=p.findChild(c);
	    if (p1==null) {
		p1=new Leaf();
		p.addChild((char)c,p1);
	    }
	    p=p1;
	}
	p.smile=index;
    }

    
    public void parseMsg(MessageItem messageItem,  int width) {
            wordsWrap=midlet.BombusQD.cf.textWrap==1;
            messageItem.msgLines=new Vector(0);
//#ifdef SMILES
            this.smileImages = midlet.BombusQD.cf.animatedSmiles?SmilesIcons.getInstance():SmilesIcons.getStaticInstance();
//#endif
            if (null != messageItem.msg.subject) {//fixes by aspro
             parseMessage(messageItem, width, messageItem.msg.subject, true);
            }
            parseMessage(messageItem, width, messageItem.msg.toString(), false);
            //messageItem.notifyRepaint();
    }

    
    private MessageParser(String res) {
        smileTable=null;
        smileTable=new Vector(0);
        root=new Leaf();
//#ifdef SMILES
        StringBuffer s=new StringBuffer(10);
        try { // generic errors
            int strnumber=0;
            boolean strhaschars=false;
            boolean endline=false;

            InputStream in=this.getClass().getResourceAsStream(res);

            boolean firstSmile=true;
            
            int c;
            while (true) {
                c=in.read();
                if (c<0) break;
                switch (c) {
                    case 0x0d:
                    case 0x0a:
                        if (strhaschars) 
                            endline=true; else break;
                    case 0x09:
                        String smile=Strconv.convCp1251ToUnicode(s.toString());
                        if (firstSmile) smileTable.addElement(smile);

                        addSmile(root, smile, strnumber);

                        s = new StringBuffer(0);
                        firstSmile=false;

                        break;
                    default:
                        s.append((char)c);
                        strhaschars=true;
                }
                if (endline) {
                    endline=strhaschars=false;
                    strnumber++;
                    firstSmile=true;
                }
            }
            s = new StringBuffer(0);
            in.close();
            in=null;
        } catch (Exception e) {
            s = new StringBuffer(0);
        }
//#endif
        
 	addSmile(root, "http://", URL);
        addSmile(root, "tel:",URL);
        addSmile(root, "ftp://",URL);
        addSmile(root, "https://",URL);
        addSmile(root, "native:",URL);
//#if NICK_COLORS
        addSmile(root, "<nick>", ComplexString.NICK_ON);
        addSmile(root, "</nick>", ComplexString.NICK_OFF);
//#endif
        
        emptyRoot=new Leaf();
	addSmile(emptyRoot, "http://", URL);
        addSmile(emptyRoot, "tel:",URL);
        addSmile(emptyRoot, "ftp://",URL);
        addSmile(emptyRoot, "https://",URL);
        addSmile(emptyRoot, "native:",URL);

//#if NICK_COLORS
        addSmile(emptyRoot, "<nick>", ComplexString.NICK_ON);
        addSmile(emptyRoot, "</nick>", ComplexString.NICK_OFF);
//#endif
    }
    
    private static StringBuffer s;
    
    private void parseMessage(final MessageItem task, final int windowWidth, String txt, boolean isSubj) {//fixes by aspro
      synchronized(this) {
//long s1 = System.currentTimeMillis();          
        if (null == txt) return;

        Vector lines=task.msgLines;
        boolean singleLine=task.msg.itemCollapsed;
        
        boolean underline=false;
        
        Leaf smileRoot=emptyRoot;
//#ifdef SMILES
        if (task.smilesEnabled() && !isSubj) smileRoot = root;
//#endif
        
        s = new StringBuffer(0);

        int w=0;
        if (!task.msg.MucChat) {
            if (!midlet.BombusQD.cf.useLowMemory_iconmsgcollapsed)
                w+=RosterIcons.getInstance().getWidth()+4;
        }
        else if (!task.msg.highlite && !isSubj)
            w+=11;
        int wordWidth=0;
        int wordStartPos=0;
//#ifdef SMILES
        ComplexString l=new ComplexString(smileImages);
//#else
//#             ComplexString l=new ComplexString();
//#endif
        lines.addElement(l);
        
        Font f=getFont((task.msg.highlite || isSubj));
        l.setFont(f);

        int color=ColorTheme.getColor(isSubj ? ColorTheme.MSG_SUBJ : ColorTheme.LIST_INK);
        l.setColor(color);
        
        int pos=0;
        int textLength = txt.length();
        while (pos < textLength) {
            int smileIndex=-1;
            int smileStartPos=pos;
            int smileEndPos=pos;
            char c = txt.charAt(pos);
            
            if (underline) {
                switch (c) {
                    case ' ':
                    case 0x09:
                    case 0x0d:
                    case 0x0a:
                    case 0xa0:
                    case ')':
                        underline=false;
                        if (wordStartPos!=pos) {
                            s.append(txt.substring(wordStartPos, pos));
                            wordStartPos = pos;
                            w += wordWidth;
                            wordWidth=0;
                        }
                        if (s.length()>0) {
                            l.addUnderline();
                            l.addElement(s.toString());
                            s = new StringBuffer(0);
                        }
                }

            } else {
                Leaf smileLeaf = smileRoot.findChild(c);
                if (null != smileLeaf) {
                    pos++;
                    while (pos < textLength) {
                        char ch = txt.charAt(pos);

                        smileLeaf = smileLeaf.findChild(ch);
                        if (null == smileLeaf) {
                            break;
                        }
                        if (-1 != smileLeaf.smile) {
                            smileIndex = smileLeaf.smile;
                            smileEndPos = pos;
                        }
                        pos++;
                    }
                    if (-1 == smileIndex) {
                        pos = smileStartPos;
                    }
                }
                
//#ifdef SMILES
                if (0 <= smileIndex) {
                    if (wordStartPos!=smileStartPos) {
                        s.append(txt.substring(wordStartPos, smileStartPos));
                        w += wordWidth;
                        wordWidth = 0;
                    }
                    if (s.length() > 0) {
                        if (underline)
                            l.addUnderline();
                        l.addElement(s.toString());
                    }
                    s = new StringBuffer(0);
                    int iw=(smileIndex<0x01000000)? smileImages.getWidth(smileIndex) : 0;
                    if (w+iw>windowWidth) {
                        if (singleLine) return;
                        l=new ComplexString(smileImages);
                        lines.addElement(l);
                        l.setColor(color);
                        l.setFont(f);
                        w=0;
                    }
                    l.addSmile(smileIndex,iw); w+=iw;
                    pos=smileEndPos;
                    wordStartPos=pos+1;
                    pos++;
                    continue;
                }
//#endif
                
                if (smileIndex==URL) {
                    if (s.length()>0) {
                        l.addElement(s.toString());
                        s = new StringBuffer(0);
                    }
                    underline=true;
                    
                }
                pos = smileStartPos;
            }
            
            
            int cw = f.charWidth(c);
            if (0x20 != c) {
                boolean newline = ( c==0x0d || c==0x0a );
                if (newline || wordWidth + cw > windowWidth) {
                    s.append(txt.substring(wordStartPos,pos));
                    w += wordWidth;
                    wordWidth = 0;
                    wordStartPos = pos;
                    if (newline) wordStartPos++;
                }
                if (newline || w + wordWidth + cw > windowWidth) {
                    if (underline) l.addUnderline();
                    l.addElement(s.toString());
                    s = new StringBuffer(0); w = 0;
                    
                    
                    if (c == 0xa0) l.setColor(ColorTheme.getColor(ColorTheme.MSG_HIGHLIGHT));


//#ifdef SMILES
                    l=new ComplexString(smileImages);
//#else
//#                    l=new ComplexString();
//#endif
                    lines.addElement(l);
                    
                    l.setColor(color);
                    if (singleLine) return;
                    l.setFont(f);
                }
            }
            if (c > 0x1f) wordWidth += cw;
            if (c == 0x09) c  = 0x20;
//****************************
            

            if (-1 != wrapSeparators.indexOf(c) || !wordsWrap) {
                if (pos > wordStartPos)
                    s.append(txt.substring(wordStartPos,pos));
                if (c>0x1f) s.append(c);
                w+=wordWidth;
                wordStartPos=pos+1;
                wordWidth=0;
            }
            pos++;
        }
        if (wordStartPos!=pos)
            s.append(txt.substring(wordStartPos,pos));
        if (s.length()>0) {
            if (underline) l.addUnderline();
            l.addElement(s.toString());
        }
        if (l.isEmpty())
            lines.removeElementAt(lines.size()-1);
        smileRoot=null;
        lines=null;
      }
//long s2 = System.currentTimeMillis();  
//System.out.println("parse "+(s2-s1)+" msec");
    }
/*
parse speed for 7K symbols
 *
36r
parse 52-56 msec
 *
aspro
parse 37-40 msec
 *
aspro + 37r
parse 20-24 msec
 */    
    
    public Font getFont(boolean bold) {
        return FontCache.getFont(bold, FontCache.msg);
    }

}