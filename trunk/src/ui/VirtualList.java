/*
 * VirtualList.java
 *
 * Created on 30.01.2005, 14:46
 *
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

package ui;
import util.Time;
import colors.ColorTheme;
import font.FontCache;
import javax.microedition.lcdui.*;
import client.Config;
//#ifdef CLASSIC_CHAT
//# import client.SimpleItemChat;
//#endif
import client.StaticData;
import client.Contact;
//#ifdef FILE_IO
import io.file.FileIO;
//#endif
import locale.SR;
//#ifdef POPUPS
import ui.controls.PopUp;
//#endif
import ui.controls.Balloon;
import ui.controls.ScrollBar;
import util.StringUtils;
//#ifdef USER_KEYS
import ui.keys.UserKeyExec;
//#endif
import java.util.Vector;

//#ifdef MENU_LISTENER
import menu.Command;
import menu.MenuListener;
//#endif

public abstract class VirtualList extends CanvasEx {
//#ifdef TOUCH
    public final static byte POINTER_NONE=-1;
    public final static byte POINTER_FIRST=0;
    public final static byte POINTER_SECOND=1;
    public final static byte POINTER_SCROLLBAR=2;
    public final static byte POINTER_DRAG=3;
    public final static byte POINTER_DRAGLEFT=4;
    public final static byte POINTER_DRAGRIGHT=5;
    public final static byte POINTER_PANEL=6;
    public final static byte POINTER_LONG=7;
//#endif

    protected void focusedItem(int index) {}

    abstract protected int getItemCount();

    abstract protected VirtualElement getItemRef(int index);

    protected int getMainBarBGnd() { return ColorTheme.getColor(ColorTheme.BAR_BGND);}
    protected int getMainBarBGndBottom() { return ColorTheme.getColor(ColorTheme.BAR_BGND_BOTTOM);}

    private static StaticData sd=StaticData.getInstance();
    public static GMenuConfig gm = GMenuConfig.getInstance();

    private int stringHeight;

    private int iHeight;
    private int mHeight;

//#ifdef TOUCH
    public static int pointer_state = POINTER_NONE;
//#endif

//#ifdef GRADIENT
   Gradient fon;
//#endif

    private static boolean reverse=false;
    private static boolean paintTop=true;
    private static boolean paintBottom=true;

    public static int phoneManufacturer;
    public boolean isServiceDiscoWindow;

    public static void updatePanelsState() {
        switch (Config.panelsState) {
            case 0: paintTop=false; paintBottom=false; reverse=false; break;
            case 1: paintTop=true;  paintBottom=false; reverse=false; break;
            case 2: paintTop=true;  paintBottom=true;  reverse=false; break;
            case 3: paintTop=false; paintBottom=true;  reverse=false; break;
            case 4: paintTop=true;  paintBottom=false; reverse=true;  break;
            case 5: paintTop=true;  paintBottom=true;  reverse=true;  break;
            case 6: paintTop=false; paintBottom=true;  reverse=true;  break;
        }
    }

//#ifdef USER_KEYS
    private static final int USER_OTHER_KEY_PRESSED = 1;
    private static final int USER_STAR_KEY_PRESSED = 2;
    private static final int USER_KEY_EXECUTED = 3;

    private int additionKeyState = USER_OTHER_KEY_PRESSED;
//#endif

//#ifdef POPUPS
    public static PopUp popup;
    public static PopUp getPopUp() {
       if(null == popup) popup = new PopUp();
       return popup;
    }
    public static void setWobble(int type, String contact, String txt){
        txt = StringUtils.replaceNickTags(txt);
        getPopUp().addPopup(type, contact, txt);
    }
//#endif
    protected int getMainBarRGB() {return ColorTheme.getColor(ColorTheme.BAR_INK);}

    public void destroy() {
        if (null != mainbar) mainbar.destroy();
        mainbar = null;
        if (null != infobar) infobar.destroy();
        infobar = null;
    }


    public void eventOk(){
     try {
      ((VirtualElement)getFocusedObject()).onSelect(this);
       updateLayout();
       fitCursorByTop();
      } catch (Exception e) {}
    }



    public void eventLongOk(){
//#ifdef TEST
//#         drawTest = true;
//#endif
    }

    public void userKeyPressed(int keyCode){}

    public void userAdditionKeyPressed(int keyCode){}

    public static final short SIEMENS_GREEN=-11;

    public static final byte NOKIA_PEN=-50;

    public static final byte MOTOE680_VOL_UP=-9;
    public static final byte MOTOE680_VOL_DOWN=-8;
    public static final byte MOTOE680_REALPLAYER=-6;
    public static final byte MOTOE680_FMRADIO=-7;

    public static final short MOTOROLA_FLIP=-200;

    public static final byte SE_FLIPOPEN_JP6=-30;
    public static final byte SE_FLIPCLOSE_JP6=-31;
    public static final byte SE_GREEN=-10;

    public static final byte SIEMENS_FLIPOPEN=-24;
    public static final byte SIEMENS_FLIPCLOSE=-22;

    public static final byte SIEMENS_VOLUP=-13;
    public static final byte SIEMENS_VOLDOWN=-14;

    public static final byte SIEMENS_CAMERA=-20;
    public static final byte SIEMENS_MPLAYER=-21;

    public static byte keyClear=-8;
    public static short keyVolDown=0x1000;
    public static short greenKeyCode=SIEMENS_GREEN;

//#ifdef MEMORY_MONITOR
//#     public static boolean memMonitor;
//#endif

    protected boolean canBack=true;

    protected int cursor;

    protected boolean stickyWindow=true;

    private int itemLayoutY[]=new int[1];
    private int listHeight;

    private synchronized void updateLayout() {
        int size=getItemCount();
        if (size==0) {
            listHeight=0;
            return;
        }

        int y=0;
        int k=0;
        boolean cr4 = (0 == size%4);
        boolean cr2 = (0 == size%2);
        int layout[] = new int[size+1];

        if (cr4) {
             size = size>>2;
             for (int index=0; index<size; ++index) {
               y += getItemRef(k).getVHeight(); layout[ ++k ] = y;
               y += getItemRef(k).getVHeight(); layout[ ++k ] = y;
               y += getItemRef(k).getVHeight(); layout[ ++k ] = y;
               y += getItemRef(k).getVHeight(); layout[ ++k ] = y;
             }
        }
        else if(cr2) {
          size = size>>1;
          for (int index=0; index<size; ++index) {
            y += getItemRef(k).getVHeight(); layout[ ++k ] = y;
            y += getItemRef(k).getVHeight(); layout[ ++k ] = y;
          }
        }
        else {
          for (int index=0; index<size; ++index){
            y+=getItemRef(k).getVHeight();
            layout[++k]=y;
          }
        }
        listHeight=y;
        itemLayoutY=layout;
    }

    protected int getElementIndexAt(int yPos){
       try {
        int end=getItemCount()-1;
        if (end<0) return -1;
        int begin=0;
          while (end-begin>1) {
            int index=(end+begin)>>1;
            if(index==-1) index = 0;
            if (yPos<itemLayoutY[index]) end=index; else begin=index;
          }
          return (yPos<itemLayoutY[end])? begin:end;
       } catch (Exception e) {}
       return 0;
    }

    public int win_top;
    private int winHeight;

    protected int offset;

    protected boolean showBalloon;

    protected MainBar mainbar;
    protected MainBar infobar;

    private boolean wrapping = true;

    public static int startGPRS=-1;
    public static int offGPRS=0;

    private int itemBorder[];

    private int lastClickX;
    private int lastClickY;
    //private int lastClickItem;
    private long lastClickTime;

    public void enableListWrapping(boolean wrap) { this.wrapping=wrap; }

    public MainBar getMainBarItem() {return mainbar;}
    public void setMainBarItem(MainBar mainbar) { this.mainbar=mainbar; }

    public MainBar getInfoBarItem() {return infobar;}
    public void setInfoBarItem(MainBar infobar) { this.infobar=infobar; }

    public Object getFocusedObject() {
        try {
            return getItemRef(cursor);
        } catch (Exception e) { }
        return null;
    }

    //protected Display display;

    protected ScrollBar scrollbar;


   public static Image resizeImage(Image image, int w, int h) {
        int w0 = image.getWidth();
        int h0 = image.getHeight();
        int[] arrayOld = new int[w0*h0];
        int[] arrayNew = new int[w*h];
        image.getRGB(arrayOld, 0, w0, 0, 0, w0, h0);
        int wy=0;
        int wy1=0;
        for (int y = 0; y < h; y++) {
             wy=w*y;
             wy1=w0*(int)(y*h0/h); //thanks evgs :)
             for (int x = 0; x < w; x++) {
                   arrayNew[x+wy] = arrayOld[x*w0/w+wy1];
             }
        }
        arrayOld=null;
        return Image.createRGBImage(arrayNew, w, h, true);
   }

//#ifdef BACK_IMAGE
    private static Image bgndImage = null;

    public static Image getImage() {
	return bgndImage;
    }

    public static void createImage(boolean create) {
           if(create) {
               if (bgndImage != null) {
                   return;
               }
           }
           try {
               switch (Config.backImgType) {
                   case 0:
                   case 2:
                       bgndImage = null;
                       break;
                   case 1:
                       bgndImage = Image.createImage("/images/back.png");
                       break;
                   case 3:
                       bgndImage = Image.createImage("/images/bgnd.jpg");
                       break;
                   case 4:
                       FileIO f = FileIO.createConnection(Config.backImgPath);
                       bgndImage = Image.createImage(f.openInputStream());
                       f.close();
               }
           } catch (Exception e) {
//#ifdef DEBUG
//#                 e.printStackTrace();
//#endif
//#ifdef DEBUG_CONSOLE
//#               midlet.BombusQD.debug.add("VL -> createImage Exception: "+e.getMessage(),10);
//#endif
           }
    }
//#endif

    public VirtualList() {
//#ifdef BACK_IMAGE
        createImage(true);
//#endif


        if (phoneManufacturer==Config.WINDOWS) {
            setTitle("BombusQD");
        }

        updatePanelsState();

        itemBorder=null;
        itemBorder=new int[32];

        scrollbar=new ScrollBar();
//#ifdef TOUCH
        scrollbar.setHasPointerEvents(Config.isTouchPhone);
//#endif
        MainBar secondBar=new MainBar("", true);
        secondBar.addElement(null); //1
        secondBar.addRAlign();
        secondBar.addElement(null); //3
        setInfoBarItem(secondBar);

        stringHeight=FontCache.getFont(false, Config.rosterFont).getHeight();
//#if (USE_ROTATOR)
        TimerTaskRotate.startRotate(0, this);
//#endif
    }

    protected void showNotify() {
//#if (USE_ROTATOR)
        TimerTaskRotate.startRotate(-1, this);
//#endif
    }


    protected void sizeChanged(int w, int h) {
//#ifdef DEBUG_CONSOLE
//#         midlet.BombusQD.debug.add("VirtualList::sizeChanged " + width+"x"+height + "->"+w+"x"+h ,10);
//#endif
//#ifdef GRADIENT
        iHeight=0;
        mHeight=0;
//#endif
    }

    protected void beginPaint(){};


   /*
    int frames = 0;
    int showFrames = 0;
    long time_start = 0;
    long time_wait = 0;
    */

//#ifdef GRAPHICS_MENU
    public static GMenu menuItem;

    private void drawGraphicsMenu(final Graphics g) {
        if(null == menuItem) return;
        menuItem.paintCustom(g,gm.itemGrMenu);
    }

//#endif

    public void paint(Graphics g) {

        mHeight=0;
        iHeight=0;
        lastPaint = System.currentTimeMillis();
        /*
        if((time_wait-time_start)>=1000) {
            time_start = System.currentTimeMillis();
            //time_start - 1023msec
            //time_wait - 1025msec
            showFrames = frames;
            //System.out.println(frames + " per seconds.(FPS)");
            frames=0;
        }
        frames++;
         */


        //System.out.println("paint " + Thread.activeCount());
        //long s1 = System.currentTimeMillis();
//#ifdef POPUPS
        getPopUp().init(g, width, height);
//#endif
//#ifdef GRAPHICS_MENU
        if(midlet.BombusQD.cf.graphicsMenu) {
           if(null != menuItem) {
             if(gm.ml!=null && gm.itemGrMenu==-1) menuItem.select(gm.inMenuSelected);
           }
        }
//#endif

        beginPaint();

        //StaticData.getInstance().screenWidth=width;

        int list_bottom=0;
        itemBorder[0]=0;
        updateLayout();

        setAbsOrg(g, 0,0);

        g.setColor(ColorTheme.getColor(ColorTheme.LIST_BGND));
        g.fillRect(0, 0, width, height);

//#ifdef BACK_IMAGE
        if (Config.backImgType == 1) {
            if (null != bgndImage) {
                int imgW = bgndImage.getWidth();
                int imgH = bgndImage.getHeight();
                for (int xx = 0; xx < width; xx += imgW) {
                    for (int yy = 0; yy < height; yy += imgH) {
                        g.drawImage(bgndImage, xx, yy, Graphics.LEFT | Graphics.TOP);
                    }
                }
            }
        }
//#ifdef GRADIENT
        else if (Config.backImgType == 2) {
            fon = new Gradient(0, 0, width, height, ColorTheme.getColor(ColorTheme.GRADIENT_BGND_LEFT),
                    ColorTheme.getColor(ColorTheme.GRADIENT_BGND_RIGHT), true);
            fon.paint(g);
        }
//#endif
        else if (Config.backImgType == 3 || Config.backImgType == 4) {
            if (null != bgndImage) {
                g.drawImage(bgndImage, 0, 0, Graphics.LEFT | Graphics.TOP);
            }
        }
//#endif

        if (mainbar!=null)
            mHeight=mainbar.getVHeight(); // nokia fix

        if (infobar!=null) {
            setInfo();
            iHeight=infobar.getVHeight(); // nokia fix
        }

        if (paintTop) {
            if (reverse) {
                if (infobar!=null) {
                    iHeight=infobar.getVHeight();
                    itemBorder[0]=iHeight;
                }
            } else {
                if (mainbar!=null) {
                    itemBorder[0]=mHeight;
                }
            }
        }
        if (paintBottom) {
            if (reverse) {
                if (mainbar!=null)
                    list_bottom=mHeight;
            } else {
                list_bottom=iHeight;
            }
        }

        winHeight=height-itemBorder[0]-list_bottom;

        int count=getItemCount();

        boolean scroll=(listHeight>winHeight);

        if (count==0) {
            cursor=(cursor==-1)?-1:0;
            win_top=0;
        } else if (cursor>=count) {
            cursor=count-1;
            stickyWindow=true;
        }
        if (count>0 && stickyWindow) fitCursorByTop();

        int itemMaxWidth=(scroll) ?(width-scrollbar.getScrollWidth()) : (width);

        int itemIndex=getElementIndexAt(win_top);
        int displayedIndex=0;
        int displayedBottom=itemBorder[0];

        int baloon=-1;
        int itemYpos;
        int drawYpos;
        try {
            count = Math.min(count, itemLayoutY.length);//aspro
            if(itemIndex==-1) itemIndex = 0;
            VirtualElement el;
            int lh;
            while ((itemIndex < count) &&
                    ((itemYpos = itemLayoutY[itemIndex] - win_top) < winHeight)) {
                el=getItemRef(itemIndex);
                if(el == null) continue;
                drawYpos = itemBorder[0] + itemYpos;
                boolean sel=(itemIndex==cursor);

                lh = el.getVHeight();
                setAbsOrg(g,0,0);
                g.setClip(0, itemBorder[0], itemMaxWidth, winHeight);
                g.setColor(el.getColorBGnd());

                if (sel) {
                    drawCursor(g, 0, drawYpos, itemMaxWidth , lh);
                    baloon=drawYpos;
                } else {
//#ifdef BACK_IMAGE
                    if (bgndImage==null && Config.backImgType != 2) {
                        g.fillRect(0, drawYpos, itemMaxWidth, lh);
                    }
//#endif
                }
                g.translate(0, drawYpos);
                g.setColor(el.getColor());
                g.clipRect(0, 0, itemMaxWidth, lh);
		try {
		    el.drawItem(this, g, (sel)?offset:0, sel);
		} catch (Exception e) {
		}

                ++itemIndex;
		displayedBottom=itemBorder[++displayedIndex]=itemBorder[0]+itemYpos+lh;
            }
            el = null;
        } catch (Exception e) {
//#ifdef DEBUG
//#             System.out.println("Exception Vlist 1 -> "+e.getMessage()+" -> "+e.toString());
//#             e.printStackTrace();
//#endif
        }
        int clrH=height-displayedBottom;

        if ( clrH>0
//#ifdef BACK_IMAGE
                && (bgndImage==null && Config.backImgType!=2)
//#endif
                ) {
            setAbsOrg(g, 0,displayedBottom);
            g.setClip(0, 0, itemMaxWidth, clrH);
            g.setColor(ColorTheme.getColor(ColorTheme.LIST_BGND));
            g.fillRect(0, 0, itemMaxWidth, clrH);
        }

        if (scroll) {
            int correct = 0;
//#ifdef MEMORY_MONITOR
//#             correct = (memMonitor)?1:0;
//#endif
            setAbsOrg(g, 0, itemBorder[0]+correct);
            g.setClip(0, 0, width, winHeight);

	    scrollbar.setPostion(win_top-correct);
	    scrollbar.setSize(listHeight-correct);
	    scrollbar.setWindowSize(winHeight-correct);

	    scrollbar.draw(g);
        } else scrollbar.setSize(0);

        setAbsClip(g, width, height);

//#ifdef MEMORY_MONITOR
//#         if (memMonitor) drawHeapMonitor(g, itemBorder[0]); //heap monitor
//#endif

        if (paintTop) {
            if (reverse) {
                if (infobar!=null) {
                    drawInfoPanel(g, 0);
                }
            } else {
                if (mainbar != null) {
                    drawMainPanel(g, 0);
                }
              }
        }

	setAbsOrg(g,0,0);
        setAbsClip(g, width, height);
        if (paintBottom) {
            if (reverse) {
                if (mainbar!=null) {
                    drawMainPanel(g,height-mHeight);
                }
            } else {
                if (infobar!=null) {
                    drawInfoPanel(g,height-iHeight);
                }
            }
            setAbsClip(g, width, height);
        }

        if(gm.itemGrMenu>0 && midlet.BombusQD.cf.graphicsMenu){
          //showBalloon=false;
//#ifdef GRAPHICS_MENU
          drawGraphicsMenu(g);
//#endif
        }else{


          if (showBalloon) {
            if (midlet.BombusQD.cf.showBalloons) {
                String text=null;
                try {
                    text=((VirtualElement)getFocusedObject()).getTipString();
                } catch (Exception e) { }
                if (text!=null)
                    drawBalloon(g, baloon, text);
            }
          }
//#ifdef POPUPS
            drawPopUp(g);
//#endif
        }

        /*
        if (reconnectWindow.getInstance().isActive()) {
            if (reconnectTimeout>reconnectPos && reconnectPos!=0) {

                int strWidth=g.getFont().stringWidth(SR.get(SR.MS_RECONNECT));
                int progressWidth=(width/3)*2;
                progressWidth=(strWidth>progressWidth)?strWidth:progressWidth;
                int progressX=(width-progressWidth)/2;
                if (pb==null) pb=new Progress(progressX, height/2, progressWidth);
                int popHeight=pb.getHeight();
                g.setColor(ColorTheme.getColor(ColorTheme.POPUP_SYSTEM_BGND));
                g.fillRoundRect(progressX-2, (height/2)-(popHeight*2), progressWidth+4, (popHeight*2)+1, 6, 6);
                g.setColor(ColorTheme.getColor(ColorTheme.POPUP_SYSTEM_INK));
                g.drawRoundRect(progressX-2, (height/2)-(popHeight*2), progressWidth+4, (popHeight*2)+1, 6, 6);
                g.drawString(SR.get(SR.MS_RECONNECT), width/2, (height/2)-(popHeight*2), Graphics.TOP | Graphics.HCENTER);
                Progress.draw(g, reconnectPos*progressWidth/reconnectTimeout, reconnectString);
            }
        }
         */


/*
        long s2 = System.currentTimeMillis();
        int ws = g.getFont().stringWidth(Long.toString(s2-s1)+"msec") + 5;
        int fh = g.getFont().getHeight();
        int xpos = width/2-ws/2;
        g.setColor(255,255,0);
        g.fillRect(xpos,1,ws,fh);
        g.setColor(0,0,0);
        g.drawRect(xpos,1,ws-1,fh-1);
        g.drawString(Long.toString(s2-s1)+"msec", xpos+2, 2, g.LEFT|g.TOP);

        //draw FPS
        time_wait = System.currentTimeMillis();
        int ws = g.getFont().stringWidth(Long.toString(showFrames)+" fps") + 5;
        int fh = g.getFont().getHeight();
        int xpos = width/2-ws/2;
        g.setColor(255,255,0);
        g.fillRect(xpos,1,ws,fh);
        g.setColor(0,0,0);
        g.drawRect(xpos,1,ws-1,fh-1);
        g.drawString(Long.toString(showFrames)+" fps", xpos+2, 2, g.LEFT|g.TOP);
*/
//#ifdef TOUCH
	if (pointer_state == POINTER_LONG) {
	    int r=midlet.BombusQD.cf.minItemHeight;
	    if (r<1) r=10;
	    g.setColor(ColorTheme.getColor(ColorTheme.CURSOR_OUTLINE));
	    g.setStrokeStyle(Graphics.DOTTED);
	    g.drawArc(lastClickX-r-2, lastClickY-r-2, (r<<1)+4, (r<<1)+4, 0, 360);
	    g.setStrokeStyle(Graphics.SOLID);
	    g.drawArc(lastClickX-r, lastClickY-r, r<<1, r<<1, 0, 360);
	    g.drawArc(lastClickX-(r>>1), lastClickY-(r>>1), r, r, 180, 270);
	}
//#endif
    }

//#ifdef POPUPS
    protected void drawPopUp(final Graphics g) {
        setAbsOrg(g, 0, 0);
        getPopUp().paintCustom(g);
    }
//#endif

    private static int getARGB() {
      int ccolor = ColorTheme.getColor(ColorTheme.CURSOR_BGND);
      int red, green, blue;
      long tmp;
      int alpha = midlet.BombusQD.cf.cursor_bgnd;
      red = ColorTheme.getRed(ccolor);
      green = ColorTheme.getGreen(ccolor);
      blue = ColorTheme.getBlue(ccolor);
      tmp = (alpha << 24) | (red << 16) | (green << 8) | blue;
      return (int)tmp;
    }


    private static int[] cursorBgnd;
    private static int lastHeight = 0;
    private static int lastWidth = 0;
    private static int[] getCursorBgnd(int w, int h)
    {
      if(lastHeight == h && lastWidth == w) return cursorBgnd;
         int alpha_ = getARGB();
         cursorBgnd = null;
         cursorBgnd = new int[w * h];
         int lengntp = cursorBgnd.length;
         for(int i = 0; i < lengntp; ++i) cursorBgnd[i] = alpha_;
      lastHeight = h;
      lastWidth = w;
      return cursorBgnd;
    }

     protected void drawCursor (Graphics g, int x0, int y0, int width, int height) { //Tishka17
     if(midlet.BombusQD.cf.cursor_bgnd!=0) {
            cursorBgnd = getCursorBgnd(width,height);
            g.drawRGB(cursorBgnd, 0, width, x0 , y0 , width, height, true);
      }
     else
     {
//#ifdef GRADIENT
        if(midlet.BombusQD.cf.gradient_cursor){
             fon=new Gradient(x0, y0, width+x0, height+y0, ColorTheme.getColor(ColorTheme.GRADIENT_CURSOR_1),
                  ColorTheme.getColor(ColorTheme.GRADIENT_CURSOR_2), false);
             fon.paintHRoundRect(g, 4);
             g.setColor(ColorTheme.getColor(ColorTheme.CURSOR_OUTLINE));
             g.drawRoundRect(x0, y0, width-1, height-1, 8, 8);
             //fon.paint(g);
        }else
//#endif
        {
         int cursorBGnd=ColorTheme.getColor(ColorTheme.CURSOR_BGND);
         int cursorOutline=ColorTheme.getColor(ColorTheme.CURSOR_OUTLINE);
           g.setColor(cursorBGnd);
           g.fillRoundRect(x0, y0, width , height,8,8);
           g.setColor(cursorOutline);
           g.drawRoundRect(x0, y0, width-1, height-1, 8, 8);
         }
     }
   }


   public void pageLeft() {
        if (getItemCount()==0)
            return;
        try {
            stickyWindow=false;
            win_top-=winHeight;
            if (win_top<0) {
                win_top=0;
                //if (!getItemRef(0).isSelectable()) cursor=getNextSelectableRef(-1); else cursor=0;
                cursor=getNextSelectableRef(-1);
            }
            if (!cursorInWindow()) {
                if(cursor==-1) cursor = 0;
                cursor=getElementIndexAt(itemLayoutY[cursor]-winHeight);
                if (((VirtualElement)getFocusedObject()).getVHeight()<=winHeight)
                    fitCursorByTop();
            }
            setRotator();
        } catch (Exception e) { }
    }

    public void pageRight() {
        if (getItemCount()==0)
            return;
        try {
            stickyWindow=false;
            win_top+=winHeight;
            int endTop=listHeight-winHeight;
            if (endTop<win_top) {
                win_top= (listHeight<winHeight)? 0 : endTop;
                int lastItemNum=getItemCount()-1;
                if (getItemRef(lastItemNum).isSelectable()==false)
                    cursor=getPrevSelectableRef(lastItemNum);
                else
                    cursor=lastItemNum;
            } else {
                if (cursorInWindow()==false) {
                    if(cursor==-1) cursor = 0;
                    cursor=getElementIndexAt(itemLayoutY[cursor]+winHeight);//yPos
                    if (((VirtualElement)getFocusedObject()).getVHeight()<=winHeight)
                        fitCursorByTop();
                }
            }
            setRotator();
        } catch (Exception e) {}
    }

    public void keyUp() {
        if (getItemCount()==0)
            return;
        if (cursor<=0) {
            if (wrapping) {
                if (getItemRef(getItemCount()-1).isSelectable())
                    moveCursorEnd();
                else
                    moveCursorTo(getItemCount()-2);
            } else {
                itemPageUp();
            }
            setRotator();
            return;
        }

        if (itemPageUp()) return;
        //stickyWindow=true;
        if (getItemRef(cursor-1).isSelectable())
            cursor--;
        else
            cursor=getPrevSelectableRef(cursor);
        fitCursorByBottom();
        setRotator();
    }

    public void keyDwn() {
        if (getItemCount()==0)
            return;
	if (cursor==(getItemCount()-1)) {
            if (wrapping) {
                moveCursorHome();
            } else {
                itemPageDown();
            }
            setRotator();
            return;
        }
        if (itemPageDown()) {
            return;
        }
        stickyWindow=true;
        if (getItemRef(cursor+1).isSelectable()) {
            cursor++;
        } else {
            cursor=getNextSelectableRef(cursor);
        }
        setRotator();
    }

/*
    private static int reconnectPos=0;
    private static int reconnectTimeout=0;
    public static boolean reconnectRedraw=false;
    private static String reconnectString="";

    private Progress pb;

    public static void drawReconnect(int pos, int timeout, String reconnect) {
        reconnectPos=pos;
        reconnectTimeout=timeout;
        reconnectRedraw=true;
        reconnectString=reconnect;
    }
,*/

    private void setAbsClip(final Graphics g, int w, int h) {
        setAbsOrg(g, 0, 0);
        g.setClip(0,0, w, h);
    }



    protected void drawBalloon(final Graphics g, int balloon, final String text) {
        setAbsOrg(g,0,balloon);
        Balloon.draw(g, text);
    }

    public static void setAbsOrg(Graphics g, int x, int y){
        g.translate(x-g.getTranslateX(), y-g.getTranslateY());
    }

//#ifdef MEMORY_MONITOR
//#     private void drawHeapMonitor(final Graphics g, int y) {
//#             int ram=(int)(((long)Runtime.getRuntime().freeMemory()*width)/(long)Runtime.getRuntime().totalMemory());
//#             g.setColor(ColorTheme.getColor(ColorTheme.HEAP_TOTAL));  g.fillRect(0,y,width,1);
//#             g.setColor(ColorTheme.getColor(ColorTheme.HEAP_FREE));  g.fillRect(0,y,ram,1);
//#     }
//#endif

    private void drawMainPanel (final Graphics g, int y) {
        int h=mainbar.getVHeight();
        //g.setClip(0,y, width, h);
//#ifdef GRADIENT
         if (getMainBarBGnd()!=getMainBarBGndBottom()) {
            int c = midlet.BombusQD.cf.gradientBarLigth?1:-1;
            int[] backPic = getBarBgnd(width, h,
                    transformColorLight(getMainBarBGnd(), c*midlet.BombusQD.cf.gradientBarLight1),
                    transformColorLight(getMainBarBGndBottom(), c*midlet.BombusQD.cf.gradientBarLight2));
            g.drawRGB(backPic, 0, width, 0, y, width, h, false);//Tishka17
            backPic = null;
            backPic = new int[0];
         } else {
             g.setColor(getMainBarBGnd());
             g.fillRect(0, y, width, h);
         }
        if (midlet.BombusQD.cf.shadowBar) {
            int sh = (width <= height)?width:height;
            if (reverse) {
                sh = sh/50;
                   drawShadow(g,0,y-sh,width,sh,200,10);
            }
            else {
                sh = sh/40;
                   drawShadow(g,0,y+h,width,sh,10,200);
            }
        }

//#else
//#             g.setColor(getMainBarBGnd());
//#             g.fillRect(0, 0, width, h);
//#endif
        setAbsOrg(g, 0, y);
        g.setColor(getMainBarRGB());
        mainbar.drawItem(this, g,(phoneManufacturer==Config.NOKIA && !reverse)?17:0,false);
    }


    private static int envObj[] = new int[0];
    private final static int[] envelopMap = { //12x9
              1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
              1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1,
              1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1,
              1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 1,
              1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1,
              1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1,
              1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1,
              1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1,
              1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1
    };

    private void drawEnvelop(final Graphics g, int x, int y) {
        if(envObj.length == 0){
           int inputSize = 12 * 9;
           envObj = new int[inputSize];
             for(int index = 0; index < inputSize; ++index) {
               if(envelopMap[index] == 1)
                     envObj[index] = 0x000000 ;
               else envObj[index] = 0xffffff ;
             }
        }
        g.drawRGB(envObj, 0, 12, x, y , 12, 9, false);
    }

    private void drawTraffic(final Graphics g, boolean up, int y) {
        int pos= up ? width/2+4 : width/2-3;
        int pos2= up ? y-4 : y-2;
        //g.setColor((up)?0xff0000:0x00ff00);
        g.setColor(getMainBarRGB());
        g.drawLine(pos, y-5, pos, y-1);
        g.drawLine(pos-1, pos2, pos+1, pos2);
        g.fillRect(pos-2, y-3, 1, 1);
        g.fillRect(pos+2, y-3, 1, 1);

    }
//#ifndef MENU
    private void drawInfoPanel (final Graphics g, int y) {
        int h=infobar.getVHeight()+1;
//#ifdef GRADIENT
        if (getMainBarBGnd()!=getMainBarBGndBottom()) {//32,102
            int c = midlet.BombusQD.cf.gradientBarLigth?1:-1;
            int[] backPic = getInfoBarBgnd(width, h,
                    transformColorLight(getMainBarBGnd(), c*midlet.BombusQD.cf.gradientBarLight1),
                    transformColorLight(getMainBarBGndBottom(), c*midlet.BombusQD.cf.gradientBarLight2));
            g.drawRGB(backPic, 0, width, 0, y , width, h, false);//Tishka17
            backPic = null;
            backPic = new int[0];
        } else {
            g.setColor(getMainBarBGnd());
            g.fillRect(0, y, width, h);
        }
        if (midlet.BombusQD.cf.shadowBar) {
            int sh = (width <= height)?width:height;
            if (!reverse) {
                sh = sh/50;
                   drawShadow(g,0,y-sh,width,sh,200,10);
            }
            else {
                sh = sh/40;
                   drawShadow(g,0,y+h,width,sh,10,200);
            }
        }
//#else
//#             g.setColor(getMainBarBGnd());
//#             g.fillRect(0, 0, width, h);
//#endif
        if(midlet.BombusQD.sd.roster!=null) {
            if (midlet.BombusQD.sd.roster.messageCount>0) drawEnvelop(g , width/2 - 5, (h-15)/2 + y + 1);
        }
        if (System.currentTimeMillis()-sd.getTrafficIn()<2000) drawTraffic(g, false, (h-15)/2 + y + 15);
        if (System.currentTimeMillis()-sd.getTrafficOut()<2000) drawTraffic(g, true, (h-15)/2 + y + 15);
        setAbsOrg(g, 0, y);
        g.setColor(getMainBarRGB());
        infobar.drawItem(this, g,(phoneManufacturer==Config.NOKIA && reverse)?20:0,false);
    }
//#endif

   //Gradients from http://www.jimm.org/nightly/0.6.091008/jimm_src.zip

   private static int transformColorLight(int color, int light)
   {
		int r = (color & 0xFF) + light;
		int g = ((color & 0xFF00) >> 8) + light;
		int b = ((color & 0xFF0000) >> 16) + light;
		if (r < 0) r = 0;
		if (r > 255) r = 255;
		if (g < 0) g = 0;
		if (g > 255) g = 255;
		if (b < 0) b = 0;
		if (b > 255) b = 255;
		return r | (g << 8) | (b << 16);
   }


    private static int[] infoBarBackground;
    private static int lastInfoHeightChange = -1;
    private static int lastInfoWidthChange = -1;
    private static int infoBarLatestColor1 = -1;
    private static int infoBarLatestColor2 = -1;

    private static int[] getInfoBarBgnd(int width, int height, int color1, int color2)
    {
		if (lastInfoHeightChange==height && lastInfoWidthChange==width &&
                        color1 == infoBarLatestColor1 && color2 == infoBarLatestColor2 && infoBarBackground != null) return infoBarBackground;

		int width2 = width/2;
		int width3 = width/3;
		int idx = 0;
                int r,g,b,dist,diff,new_r,new_g,new_b,color = 0;

                lastInfoHeightChange=height;
		lastInfoWidthChange = width;
                infoBarBackground = new int[height*width];
		int r1 = ((color1 & 0xFF0000) >> 16);
		int g1 = ((color1 & 0x00FF00) >> 8);
		int b1 = (color1 & 0x0000FF);
		int r2 = ((color2 & 0xFF0000) >> 16);
		int g2 = ((color2 & 0x00FF00) >> 8);
		int b2 = (color2 & 0x0000FF);

		for (int y = height; y > 0; y--)
		  {
			r = y * (r2 - r1) / (height-1) + r1;
			g = y * (g2 - g1) / (height-1) + g1;
			b = y * (b2 - b1) / (height-1) + b1;
			for (int x = width; x > 0; x--)
			{
				dist = x-width2;
				if (dist < 0) dist = -dist;
				dist = width3-dist;
				if (dist < 0) dist = 0;
				diff = 96*dist/width3;

				new_r = r+diff;
				new_g = g+diff;
				new_b = b+diff;
				if (new_r < 0) new_r = 0;
				if (new_r > 255) new_r = 255;
				if (new_g < 0) new_g = 0;
				if (new_g > 255) new_g = 255;
				if (new_b < 0) new_b = 0;
				if (new_b > 255) new_b = 255;
				color = (new_r << 16) | (new_g << 8) | (new_b);
				infoBarBackground[idx++] = color;
			  }
		    }
	   infoBarLatestColor1 = color1;
           infoBarLatestColor2 = color2;
       return infoBarBackground;
    }


    private static int[] menuBarBackground;
    private static int lastHeightChange = -1;
    private static int lastWidthChange = -1;
    private static int barLatestColor1 = -1;
    private static int barLatestColor2 = -1;

    private void drawShadow(final Graphics g, int ox, int oy, int width, int height, int op1, int op2) {
        int []menuBarShadow = new int[width];
        int alpha, color;
        for (int y = 0; y < height; y++) {
	    alpha = ((op2*(height-y)+op1*y)/height) & 255;
            //color = (alpha << 24) | (0 << 16) | (0 << 8) | (0);
            color = alpha << 24;
            for (int x = 0; x < width; x++) {
                menuBarShadow[x] = color;
            }
            g.drawRGB(menuBarShadow, 0, width, ox, oy+y, width, 1, true);
        }

    }
    private static int[] getBarBgnd(int width, int height, int color1, int color2)
    {
		if (lastHeightChange == height && lastWidthChange == width &&
                        color1 == barLatestColor1 && color2 == barLatestColor2 && menuBarBackground != null) return menuBarBackground;

                lastHeightChange=height;
                lastWidthChange=width;
		menuBarBackground = new int[height*width];
		int r1 = ((color1 & 0xFF0000) >> 16);
		int g1 = ((color1 & 0x00FF00) >> 8);
		int b1 = (color1 & 0x0000FF);
		int r2 = ((color2 & 0xFF0000) >> 16);
		int g2 = ((color2 & 0x00FF00) >> 8);
		int b2 = (color2 & 0x0000FF);
		int width2 = width/2;
		int width3 = width/3;
		int idx = 0;
                int r,g,b,dist,diff,new_r,new_g,new_b,color = 0;
		  for (int y = 0; y < height; y++)
		  {
			r = y * (r2 - r1) / (height-1) + r1;
			g = y * (g2 - g1) / (height-1) + g1;
			b = y * (b2 - b1) / (height-1) + b1;
			for (int x = 0; x < width; x++)
			{
				dist = x-width2;
				if (dist < 0) dist = -dist;
				dist = width3-dist;
				if (dist < 0) dist = 0;
				diff = 96*dist/width3;

				new_r = r+diff;
				new_g = g+diff;
				new_b = b+diff;
				if (new_r < 0) new_r = 0;
				if (new_r > 255) new_r = 255;
				if (new_g < 0) new_g = 0;
				if (new_g > 255) new_g = 255;
				if (new_b < 0) new_b = 0;
				if (new_b > 255) new_b = 255;
				color = (new_r << 16) | (new_g << 8) | (new_b);
				menuBarBackground[idx++] = color;
			  }
		    }
	   barLatestColor1 = color1;
           barLatestColor2 = color2;
       return menuBarBackground;
    }


    public void moveCursorHome(){
        stickyWindow=true;
        if (cursor>0) cursor=getNextSelectableRef(-1);
        setRotator();
    }

    public void moveCursorEnd(){
        stickyWindow=true;
        int count=getItemCount();
        if (cursor>=0) cursor=(count==0)?0:count-1;
        setRotator();
    }

    public void moveCursorTo(int index){
        int count=getItemCount();
        if (index<0) index=0;
        if (index>=count) index=count-1;

        if (getItemRef(index).isSelectable()) cursor=index;
        stickyWindow=true;
        redraw();
    }

    public void moveCursorTo(int index, boolean force){
        int count=getItemCount();
        if (index<0) index=0;
        if (index>=count) index=count-1;
        cursor=index;
        stickyWindow=true;
        redraw();
    }

    protected void fitCursorByTop(){
        try {
            if(cursor==-1) cursor = 0;
            int top=itemLayoutY[cursor];
            if (top<win_top) win_top=top;
            if (((VirtualElement)getFocusedObject()).getVHeight()<=winHeight) {
                int bottom=itemLayoutY[cursor+1]-winHeight;
                if (bottom>win_top) win_top=bottom;
            }
            if (top>=win_top+winHeight) win_top=top;
        } catch (Exception e) { }
    }

    protected void fitCursorByBottom(){
        try {
            int bottom=itemLayoutY[cursor+1]-winHeight;
            if (bottom>win_top) win_top=bottom;
            if (((VirtualElement)getFocusedObject()).getVHeight()<=winHeight) {
                int top=itemLayoutY[cursor];
                if (top<win_top) win_top=top;
            }
            if (itemLayoutY[cursor+1]<=win_top) win_top=bottom;
        } catch (Exception e) {}
    }

    protected int kHold;

    protected void keyRepeated(int keyCode){

         if (keyCode==Config.SOFT_RIGHT || keyCode==')' || keyCode==Config.SOFT_LEFT || keyCode=='(' )
             return;
        key(keyCode);
    }
    protected void keyReleased(int keyCode) {
        kHold=0;
    }
    protected void keyPressed(int keyCode) {

        kHold=0;
        key(keyCode);
    }

    int old_win_top;
    private long lastPaint;

//#ifdef TOUCH
    protected void pointerPressed(int x, int y) {
        long clickTime=System.currentTimeMillis();
        lastClickTime=clickTime;
        lastClickX=x;
        lastClickY=y;
        pointer_state = POINTER_FIRST;
        old_win_top = win_top;
        if(gm.itemGrMenu>0){
            if(null != menuItem) {
                menuItem.pointerPressed(x, y);
                redraw();
            }
            return;
        } else if (getPopUp().size() > 0)  {
            if (popup.handleEvent(x, y)) {
                redraw();
                return;
            }
        }

        boolean on_panel = false;
        if (reverse) {
            if (mainbar!=null && paintBottom) {
                if (height - y < mHeight) {
                    on_panel = true;
                }
            }
            if (infobar!=null && paintTop) {
                if (y < iHeight) {
                    on_panel = true;
                }
            }
        //soft buttons drown on bottom
        } else {
            if (infobar!=null && paintBottom) {
                if (y > height-iHeight) {
                    on_panel = true;
                }
            }
            if (mainbar!=null && paintTop) {
                if (y < mHeight) {
                    on_panel = true;
                }
            }
        }
        if (on_panel) {
            pointer_state = POINTER_PANEL;
            return;
        }
        else if (scrollbar.pointerPressed(x, y, this)) {
            pointer_state = POINTER_SCROLLBAR;
            stickyWindow=false;
            return;
        }
        int i=0;
        while (i<32) {
        if (y<itemBorder[i]) break;
        i++;
        }
        if (i==0 || i==32) {
            return;
        }
        //System.out.println(i);
        int newcursor = getElementIndexAt(win_top)+i-1;
        if (cursor >= 0) {
            if (cursor != newcursor) {
                if (!on_panel) {
                    moveCursorTo(newcursor);
                }
                setRotator();
            }  else {
                pointer_state = POINTER_SECOND;
            }
        }

        //lastClickItem=cursor;

        if(cursor==-1) cursor = 0;
	setRotator();
        redraw();
   }


   protected void pointerDragged(int x, int y) {
      int old_state = pointer_state;
       long clickTime=System.currentTimeMillis();
       if(gm.itemGrMenu>0){
            if(null != menuItem) {
                menuItem.pointerPressed(x, y);
                if (clickTime-lastPaint>80) {
                    redraw();
                }
            }
            return;
      }

      if (pointer_state == POINTER_PANEL)
            return;
      if (pointer_state == POINTER_SCROLLBAR) {
            scrollbar.pointerDragged(x, y, this);
            if (clickTime-lastPaint>80) {
                    redraw();
            }
            stickyWindow=false;
            return;
      }

      win_top = old_win_top - y + lastClickY;
      if (x - lastClickX > 9 || lastClickX-x >9
              || y - lastClickY > 9 || lastClickY-y>9)
          pointer_state = POINTER_DRAG;

      if (win_top+winHeight>listHeight) win_top=listHeight-winHeight;
      if (win_top<0) win_top=0;
      stickyWindow=false;
      if (clickTime-lastPaint>70 || old_state!=pointer_state) {
                    redraw();
      }
      return;
    }

    protected void touchMainPanelPressed(int x, int y) {
    }

    protected void pointerReleased(int x, int y) {
        long clickTime=System.currentTimeMillis();
        if(gm.itemGrMenu>0){
            if(null != menuItem && y>lastClickY-7 && y<lastClickY+7) {
                menuItem.pointerReleased(x, y);
                redraw();
            }
            lastClickTime=clickTime;
            lastClickX=x;
            lastClickY=y;
	    pointer_state = POINTER_NONE;
	    redraw();
            return;
        }
        //soft buttons drown on top
        if (reverse) {
            if (mainbar!=null && paintBottom) {
                if (height - y < mHeight) {
                    if (pointer_state == POINTER_PANEL) touchMainPanelPressed(x, y);
		    pointer_state = POINTER_NONE;
		    redraw();
                    return;
                }
            }
            if (infobar!=null && paintTop) {
                if (y < iHeight) {
                    if (x < width/2-40) {
                        if (pointer_state == POINTER_PANEL)touchLeftPressed();
                    }else if (x>width/2+40){
                        if (pointer_state == POINTER_PANEL)touchRightPressed();
                    } else if (pointer_state == POINTER_PANEL)touchMiddlePressed();
		    pointer_state = POINTER_NONE;
		    redraw();
                    return;
                }
            }
        //soft buttons drown on bottom
        } else {
            if (infobar!=null && paintBottom) {
                if (y > height-iHeight) {
                    if (x < width/2-40) {
                        if (pointer_state == POINTER_PANEL)touchLeftPressed();
                    }else if (x>width/2+40){
                        if (pointer_state == POINTER_PANEL)touchRightPressed();
                    } else if (pointer_state == POINTER_PANEL)touchMiddlePressed();
                    stickyWindow=false;
		    pointer_state = POINTER_NONE;
		    redraw();
                    return;
                }
            }
            if (mainbar!=null && paintTop) {
                if (y < mHeight) {
                    if (pointer_state == POINTER_PANEL)touchMainPanelPressed(x, y);
		    pointer_state = POINTER_NONE;
		    redraw();
                    return;
                }
            }
        }
        if (pointer_state==POINTER_SCROLLBAR) scrollbar.pointerReleased(x, y, this);

        if (pointer_state == POINTER_FIRST || pointer_state==POINTER_SECOND || pointer_state==POINTER_LONG) {
            if (clickTime-lastClickTime>500 || pointer_state==POINTER_LONG) {
                y=0;
                eventLongOk();
            } else {
                if (pointer_state == POINTER_SECOND) {
                    VirtualElement element = (VirtualElement)getFocusedObject();
                    if (element == null || !element.handleEvent(x, y)) {
                        eventOk();
                    }
                }
            }
        }
	redraw();
	pointer_state = POINTER_NONE;
    }

//#endif //TOUCH

//#ifdef USER_KEYS
    // TODO укоротить
    private void additionKeyPressed(int keyCode) {
        switch (keyCode) {
            case KEY_NUM0: UserKeyExec.getInstance().commandExecute(0); break;
            case KEY_NUM1: UserKeyExec.getInstance().commandExecute(1); break;
            case KEY_NUM2: UserKeyExec.getInstance().commandExecute(2); break;
            case KEY_NUM3: UserKeyExec.getInstance().commandExecute(3); break;
            case KEY_NUM4: UserKeyExec.getInstance().commandExecute(4); break;
            case KEY_NUM5: UserKeyExec.getInstance().commandExecute(5); break;
            case KEY_NUM6: UserKeyExec.getInstance().commandExecute(6); break;
            case KEY_NUM7: UserKeyExec.getInstance().commandExecute(7); break;
            case KEY_NUM8: UserKeyExec.getInstance().commandExecute(8); break;
            case KEY_NUM9: UserKeyExec.getInstance().commandExecute(9); break;
            case KEY_POUND: UserKeyExec.getInstance().commandExecute(10); break;
        }
    }
//#endif

    private boolean sendEvent(int keyCode) {
        int code;
        try {
            switch (getGameAction(keyCode)) {
                case FIRE:
                    code = FIRE;
                    break;
                case UP:
                    code = UP;
                    break;
                case DOWN:
                    code = DOWN;
                    break;
                case LEFT:
                    code = LEFT;
                    break;
                case RIGHT:
                    code = RIGHT;
                    break;
                default:
                    code = keyCode;
                    break;
            }
        } catch (IllegalArgumentException e) {
            code = keyCode;
        }

//#ifdef POPUPS
        if (getPopUp().size()>0) {
            return popup.handleEvent(code);
        } else 
//#endif
        {
            VirtualElement element = (VirtualElement)getFocusedObject();
            if (element != null) {
                return element.handleEvent(code);
            }
        }
        return false;
    }
    
    private boolean sendEvent(int x, int y) {
        if (getPopUp().size() > 0) {
            return popup.handleEvent(x, y);
        }
        return false;
    }

/*
    public void reconnectYes() {
        reconnectWindow.getInstance().reconnect();
        //reconnectDraw=false;
        redraw();
    }

    public void reconnectNo() {
        reconnectWindow.getInstance().stopReconnect();
        //reconnectDraw=false;
        redraw();
    }
 */


//#ifdef MENU_LISTENER
    public Vector menuCommands=new Vector(0);

    public Vector cmdfirstList=new Vector(0);
    public Vector cmdsecondList=new Vector(0);
    public Vector cmdThirdList=new Vector(0);

    public boolean contains(Command command) {
        return menuCommands.contains(command);
    }

    public void addCommand(Command command) {
        if (menuCommands.indexOf(command)<0) menuCommands.addElement(command);
    }

    public void addInCommand(int countMenu,Command command) {
        if(midlet.BombusQD.cf.graphicsMenu){
          if(countMenu==1){
            if (cmdfirstList.indexOf(command)<0)cmdfirstList.addElement(command);
          } else if(countMenu==2){
            if (cmdsecondList.indexOf(command)<0) cmdsecondList.addElement(command);
          } else if(countMenu==3){
            if (cmdThirdList.indexOf(command)<0) cmdThirdList.addElement(command);
          }
        }else{
            //command.setIn();
            if (menuCommands.indexOf(command)<0) menuCommands.addElement(command);
        }
    }

    public void removeInCommand(int countMenu,Command command) {
       if(midlet.BombusQD.cf.graphicsMenu){
        if(countMenu==1){
          if (cmdfirstList.indexOf(command)<0)cmdfirstList.removeElement(command);
        } else if(countMenu==2){
          if (cmdsecondList.indexOf(command)<0) cmdsecondList.removeElement(command);
        } else if(countMenu==3){
          if (cmdThirdList.indexOf(command)<0) cmdThirdList.removeElement(command);
        }
       }else{
           menuCommands.removeElement(command);
       }
    }

    public void removeCommand(Command command) {
        menuCommands.removeElement(command);
    }

    public void touchLeftPressed(){
//#ifdef GRAPHICS_MENU
         gm.itemGrMenu = showGraphicsMenu();
         redraw();
//#else
//#         showMenu();
//#endif
    }


//#ifdef GRAPHICS_MENU
        public int showGraphicsMenu() { return -10; }
//#else
//#     public void showMenu() {};
//#endif

    public Command getCommand(int index) {
        if (index>menuCommands.size()-1) return null;
        return (Command) menuCommands.elementAt(index);
    }


    public void touchRightPressed(){
        destroyView();
    }
//#endif
    public void touchMiddlePressed(){
    }

    private void key(int keyCode) {
//#ifdef GRAPHICS_MENU
     if(gm.itemGrMenu>0 && midlet.BombusQD.cf.graphicsMenu ) { //�������� ����
         if(null != menuItem) menuItem.keyPressed(keyCode);
         redraw();
     } else {
        if (sendEvent(keyCode)) {
            redraw();
            return;
        }
//#ifdef POPUPS
        if (keyCode==greenKeyCode) {
            if (getPopUp().getContact()!=null) {
//#ifdef CLASSIC_CHAT
//#                    if(midlet.BombusQD.cf.module_classicchat){
//#                       new SimpleItemChat(sd.roster.getContact(popup.getContact(), false));
//#                    } else {
//#endif
                       Contact c = sd.roster.getContact(popup.getContact(), false);
                       if(c.getChatInfo().getMessageCount() <= 0){
                          midlet.BombusQD.sd.roster.showMsgEditor(c, c.msgSuspended);
                          return;
                       }
                       c.getMessageList().show();
//#ifdef CLASSIC_CHAT
//#                    }
//#endif
                popup.next();
                return;
            } else if (phoneManufacturer==Config.MOTO || phoneManufacturer==Config.NOKIA || phoneManufacturer==Config.NOKIA_9XXX) {
                keyGreen();
                return;
            }
        }
//#endif
//#ifdef MENU_LISTENER
        if (keyCode==Config.SOFT_LEFT || keyCode=='(') {
           /*
            if (reconnectWindow.getInstance().isActive()) {
                reconnectYes();
                return;
            }
            */
            gm.itemCursorIndex=0;
            gm.itemCursorIndexIn=0;
             touchLeftPressed();
            return;
        }
         if (keyCode==Config.SOFT_RIGHT || keyCode==')') {
           /*
            if (reconnectWindow.getInstance().isActive()) {
                reconnectNo();
                return;
            }
            */
             touchRightPressed();
            return;
         }
//#else
//#          if (keyCode==Config.SOFT_RIGHT) {
//#             if (phoneManufacturer!=Config.SONYE || phoneManufacturer==Config.SIEMENS || phoneManufacturer==Config.SIEMENS2 || phoneManufacturer==Config.MOTO) {
//#                if (canBack==true)
//#                     destroyView();
//#                 return;
//#             }
//#          }
//#endif
//#ifdef USER_KEYS
        if (Config.userKeys) {
            switch (additionKeyState) {
                case USER_OTHER_KEY_PRESSED:
                case USER_KEY_EXECUTED:
                    additionKeyState=(keyCode==KEY_STAR)?USER_STAR_KEY_PRESSED:USER_OTHER_KEY_PRESSED;
                    break;
                case USER_STAR_KEY_PRESSED:
                    additionKeyState=(keyCode!=KEY_STAR)?USER_KEY_EXECUTED:USER_STAR_KEY_PRESSED;
                    additionKeyPressed(keyCode);
                    break;
            }
        }
//#endif

    switch (keyCode) {
        case 0:
            break;
        case KEY_NUM1:
            moveCursorHome();
            break;
        case KEY_NUM2:
            keyUp();
            break;
        case KEY_NUM4:
            userKeyPressed(keyCode);
            break;
        case KEY_NUM6:
            userKeyPressed(keyCode);
            break;
        case KEY_NUM7:
            moveCursorEnd();
            break;
        case KEY_NUM8:
            keyDwn();
            break;
        case KEY_STAR:
            if (!isServiceDiscoWindow) {
                midlet.BombusQD.sd.roster.systemGC();
            }
//#ifdef POPUPS
            StringBuffer mem = new StringBuffer(0);
            mem.append(Time.getTimeWeekDay()).append("\nTraffic: ").append(getTraffic());
            if (midlet.BombusQD.cf.userAppLevel == 1) {
                mem.append('\n');

                long free = Runtime.getRuntime().freeMemory() >> 10;
                long total = Runtime.getRuntime().totalMemory() >> 10;
                long inUse = total - free;
                mem.append("QD use: ").append(inUse).append("kb ").append('(').append((100 * inUse / total)).append("%)\n").append("Stanzas(in/out): ").append(StaticData.incPacketCount).append("/").append(StaticData.outPacketCount);
            }
            setWobble(1, null, mem.toString());
//#endif
            break;
//#ifdef POPUPS
        case KEY_POUND:
            try {
                String text = ((VirtualElement)getFocusedObject()).getTipString();
                if (text != null) {
                    setWobble(1, null, text);
                }
            } catch (Exception e) {
            }
            break;
//#endif

        default:
            try {
                switch (getGameAction(keyCode)){
                    case UP:
                        keyUp();
                        break;
                    case DOWN:
                        keyDwn();
                        break;
                    case LEFT:
                        pageLeft();
                        break;
                    case RIGHT:
                        pageRight();
                        break;
                    case FIRE:
                        eventOk();
                        break;
                default:
                    if (keyCode==keyClear) { keyClear(); break; }
                    if (keyCode==keyVolDown) { moveCursorEnd(); break; }
                    if (keyCode=='5') {  eventOk(); break; }
                    if (keyCode==Config.KEY_BACK &&  canBack) { destroyView(); }
                    if (keyCode==greenKeyCode) { keyGreen(); }

                    userKeyPressed(keyCode);
                }
            } catch (Exception e) {
//#ifdef DEBUG
//#                 e.printStackTrace();
//#endif
            }
        }
        redraw();
     }
//#else
//#if DEBUG
//#    System.out.println(keyCode);
//#endif
//#ifdef POPUPS
//#         if (keyCode==greenKeyCode) {
//#             if (popup.getContact()!=null) {
//#                    if(midlet.BombusQD.cf.useClassicChat){
//#                       new SimpleItemChat(display,sd.roster,sd.roster.getContact(popup.getContact(), false));
//#                    }else{
//#                       new ContactMessageList(sd.roster.getContact(popup.getContact(), false),display);
//#                    }
//#                 popup.next();
//#                 return;
//#             } else if (phoneManufacturer==Config.MOTO || phoneManufacturer==Config.NOKIA || phoneManufacturer==Config.NOKIA_9XXX) {
//#                 keyGreen();
//#                 return;
//#             }
//#         }
//#endif
//#ifdef MENU_LISTENER
//#         if (keyCode==Config.SOFT_LEFT || keyCode=='(') {
//#             if (reconnectWindow.getInstance().isActive()) {
//#                 reconnectYes();
//#                 return;
//#             }
//#              touchLeftPressed();
//#             return;
//#         }
//#          if (keyCode==Config.SOFT_RIGHT || keyCode==')') {
//#             if (reconnectWindow.getInstance().isActive()) {
//#                 reconnectNo();
//#                 return;
//#             }
//#              touchRightPressed();
//#             return;
//#          }
//#else
//#          if (keyCode==Config.SOFT_RIGHT) {
//#             if (phoneManufacturer!=Config.SONYE || phoneManufacturer==Config.SIEMENS || phoneManufacturer==Config.SIEMENS2 || phoneManufacturer==Config.MOTO) {
//#                if (canBack==true)
//#                     destroyView();
//#                 return;
//#             }
//#          }
//#endif
//#         if (sendEvent(keyCode)) {
//#             redraw();
//#             return;
//#         }
//#ifdef USER_KEYS
//#         if (Config.userKeys) {
//#             switch (additionKeyState) {
//#                 case USER_OTHER_KEY_PRESSED:
//#                 case USER_KEY_EXECUTED:
//#                     additionKeyState=(keyCode==KEY_STAR)?USER_STAR_KEY_PRESSED:USER_OTHER_KEY_PRESSED;
//#                     break;
//#                 case USER_STAR_KEY_PRESSED:
//#                     additionKeyState=(keyCode!=KEY_STAR)?USER_KEY_EXECUTED:USER_STAR_KEY_PRESSED;
//#                     additionKeyPressed(keyCode);
//#                     break;
//#             }
//#         }
//#endif
//#
//#     switch (keyCode) {
//#         case 0:
//#             break;
//#         case KEY_NUM1:
//#             moveCursorHome();
//#             break;
//#         case KEY_NUM2:
//#             keyUp();
//#             break;
//#         case KEY_NUM4:
//#             userKeyPressed(keyCode);
//#             break;
//#         case KEY_NUM6:
//#             userKeyPressed(keyCode);
//#             break;
//#         case KEY_NUM7:
//# /*
//#             if(running_animation==true){
//#                 midlet.BombusQD.cf.flagQuerySign=false;
//#                 at.stop();
//#             }else{
//#                 midlet.BombusQD.cf.flagQuerySign=true;
//#                 at.start();
//#             }
//#  */
//#             moveCursorEnd();
//#             break;
//#         case KEY_NUM8:
//#             keyDwn();
//#             break;
//#         case KEY_STAR:
//#             System.gc();
//#             try { Thread.sleep(50); } catch (InterruptedException ex) { }
//#             break;
//#ifdef POPUPS
//#         case KEY_POUND:
//#             //if (midlet.BombusQD.cf.popUps) {
//#                 try {
//#                     String text=((VirtualElement)getFocusedObject()).getTipString();
//#                     if (text!=null) {
//#                         setWobble(1, null, text);
//#                     }
//#                 } catch (Exception e) { }
//#             //}
//#             break;
//#endif
//#
//#         default:
//#             try {
//#                 switch (getGameAction(keyCode)){
//#                     case UP:
//#                         keyUp();
//#                         break;
//#                     case DOWN:
//#                         keyDwn();
//#                         break;
//#                     case LEFT:
//#                         pageLeft();
//#                         break;
//#                     case RIGHT:
//#                         pageRight();
//#                         break;
//#                     case FIRE:
//#                         eventOk();
//#                         break;
//#                 default:
//#                     if (keyCode==keyClear) { keyClear(); break; }
//#                     if (keyCode==keyVolDown) { moveCursorEnd(); break; }
//#                     if (keyCode=='5') {  eventOk(); break; }
//#                     if (keyCode==Config.KEY_BACK /*&&  canBack==true*/) { destroyView(); }
//#                     if (keyCode==greenKeyCode) { keyGreen(); }
//#
//#                     userKeyPressed(keyCode);
//#                 }
//#             } catch (Exception e) {/* IllegalArgumentException @ getGameAction */}
//#         }
//#         redraw();
//#endif
    }



    public int getPrevSelectableRef(int curRef) {
        int prevRef=curRef;
        boolean process=true;
        while (process) {
            prevRef--;
            if (getItemRef(prevRef).isSelectable())
                break;
            if (prevRef==0 && wrapping)
                prevRef=getItemCount();
        }

        return prevRef;
    }

    public int getNextSelectableRef(int curRef) {
        int nextRef=curRef;
        boolean process=true;
        while (process) {
            nextRef++;
            if (nextRef==getItemCount() && wrapping)
                nextRef=0;
            if (getItemRef(nextRef).isSelectable())
                break;
        }

        return nextRef;
    }

    private boolean itemPageDown() {
        try {
            stickyWindow=false;
            if (((VirtualElement)getFocusedObject()).getVHeight()<=winHeight) {
                stickyWindow=true;
                return false;
            }

            if (!cursorInWindow()) {
                return false;
            }

            if(cursor==-1) cursor = 0;
            int remainder=itemLayoutY[cursor+1]-win_top;
            if (remainder<=winHeight) {
                return false;
            }
            if (remainder <= winHeight<<1 ) {
                win_top=remainder-winHeight+win_top+8;
                return true;
            }
            win_top+=winHeight-stringHeight;//-stringHeight;
            return true;
        } catch (Exception e) {}
        return false;
    }

    private boolean itemPageUp() {
        try {
            stickyWindow=false;
            if (((VirtualElement)getFocusedObject()).getVHeight()<=winHeight) {
                //stickyWindow=true;
                return false;
            }

            if (!cursorInWindow()) { return false; }

            if(cursor<0) cursor = 0;
            int remainder=win_top-itemLayoutY[cursor];
            if (remainder<=0) return false;
            if (remainder<=winHeight) {
                win_top=itemLayoutY[cursor];
                return true;
            }
            win_top-=winHeight-stringHeight;//-stringHeight;
            return true;
        } catch (Exception e) {}
        return false;
    }



    public boolean cursorInWindow(){
        try {
            if(cursor==-1) cursor = 0;
            int y1=itemLayoutY[cursor]-win_top;
            int y2=itemLayoutY[cursor+1]-win_top;
            if (y1>=winHeight) return false;
            if (y2>=0) return true;
        } catch (Exception e) { }
        return false;
    }

    protected void keyClear() {}
    protected void keyGreen() { eventOk(); }

    protected  void setRotator(){
//#if (USE_ROTATOR)
        try {
            if (getItemCount()<1) return;
            focusedItem(cursor);
        } catch (Exception e) { return; }

        if (cursor>=0) {
            int itemWidth=getItemRef(cursor).getVWidth();
            if (itemWidth>=width-scrollbar.getScrollWidth())
                itemWidth-=width/2;
            else
                itemWidth=0;
            TimerTaskRotate.startRotate(itemWidth, this);
        }
 //#endif
    }

    public int getListWidth() {
        return width - midlet.BombusQD.cf.scrollWidth - 2;
    }

    public static void sort(Vector sortVector, int itemType ,int sortType){
        try {
            int f,i;
            Contact find;
            switch(itemType){
                case 0:
                //ActiveContacts
                     switch(sortType){
                       case 0:
                       //by status
                          sort(sortVector);
                          break;
                       case 1:
                       //by messageCount
                         int nextCount = 0;
                         Contact c = null;
                         try {
                          Vector newSort = new Vector(0);
                            for (f = 0; f < sortVector.size(); ++f) {
                              int cIndex = 0;
                              for (i = 0; i < sortVector.size(); ++i) {
                                 find = (Contact)sortVector.elementAt(i);
                                 int msgNext = find.getChatInfo().getNewMessageCount();
                                 if(msgNext > nextCount){
                                    nextCount = msgNext;
                                    cIndex = sortVector.indexOf(find);
                                    //c = find; //<-BIG_BARA_BUM!!!
                                 }
                              }
                             c = (Contact)sortVector.elementAt(cIndex);
                             sortVector.removeElement(c);
                             newSort.insertElementAt(c,0);
                             nextCount = f = 0;
                            }
                            nextCount = newSort.size();
                            for (f = 0; f < nextCount; ++f) sortVector.insertElementAt(newSort.elementAt(f),0);
                            newSort = null;
                            find = null;
                            if(c!=null) c = null;
                         } catch(OutOfMemoryError eom) {
//#ifdef DEBUG_CONSOLE
//#                            if(midlet.BombusQD.cf.debug) {
//#                                midlet.BombusQD.debug.add("::VList->sort->contactByMsgs",10);
//#                            }
//#endif
                         } catch (Exception e) {}
                         break;
                     }
                    break;
                case 1: //Bookmarks
                    break;
            }
        } catch (Exception e) {
//#ifdef DEBUG
//#             e.printStackTrace();
//#endif
        }
    }


    public final static void sort(Vector sortVector){
        try {
                int f, i;
                IconTextElement left, right;
                if(sortVector == null) return;
                int size=sortVector.size();
                for (f = 1; f < size; f++) {
                    left=(IconTextElement)sortVector.elementAt(f);
                    right=(IconTextElement)sortVector.elementAt(f-1);
                    if ( left.compare(right) >=0 ) continue;
                    i = f-1;
                    while (i>=0){
                        right=(IconTextElement)sortVector.elementAt(i);
                        if (right.compare(left) <0) break;
                        sortVector.setElementAt(right,i+1);
                        i--;
                    }
                    sortVector.setElementAt(left,i+1);
                }
        } catch (Exception e) {
//#ifdef DEBUG
//#             e.printStackTrace();
//#endif
        }
    }

    public int getCursor() {
        return cursor;
    }



    public void setInfo() {
        getInfoBarItem().setElementAt((!Config.showTimeTraffic) ? touchLeftCommand() : Time.timeLocalString(Time.utcTimeMillis()), 1);
        getInfoBarItem().setElementAt((!Config.showTimeTraffic) ? touchRightCommand() : getTraffic(), 3);
    }

    public String getTraffic() {
	try {
	    long traffic = StaticData.getInstance().traffic;
	    return StringUtils.getSizeString(traffic)+midlet.BombusQD.sd.roster.theStream.getStreamStatsBar();
	} catch (java.lang.NullPointerException e) {
	    return "";
	}
    }

    public String touchLeftCommand(){ return (midlet.BombusQD.cf.oldSE)?SR.get(SR.MS_BACK):SR.get(SR.MS_MENU); }
    public String touchRightCommand(){ return (midlet.BombusQD.cf.oldSE)?SR.get(SR.MS_MENU):SR.get(SR.MS_BACK); }
    public void cmdCancel() {  destroyView();  }
}

//#if (USE_ROTATOR)
class TimerTaskRotate extends Thread{
    private int scrollLen;
    private int scroll; //wait before scroll * sleep
    private int balloon; // show balloon time

    private boolean scrollline;

    private VirtualList attachedList;

    private static TimerTaskRotate instance;
    //#ifdef TOUCH
    private static int holdCount = 0;
    //#endif

    private TimerTaskRotate() {
        start();
    }

    public static void startRotate(int max, VirtualList list){
        if(list==null) return;
        //Windows mobile J9 hanging test
        if (midlet.BombusQD.cf.phoneManufacturer==Config.WINDOWS) {
            list.showBalloon=true;
            list.offset=0;
            return;
        }
        if (instance==null)
            instance=new TimerTaskRotate();

        if (max<0) {
            //instance.destroyTask();
            list.offset=0;
            return;
        }

        //synchronized (instance) {
            list.offset=0;
            instance.scrollLen=max;
            instance.scrollline=(max>0);
            instance.attachedList=list;
            instance.balloon  = 20;
            instance.scroll   = 10;
       // }
    }

    public void run() {
        while (true) {
            try {  sleep(100);  } catch (Exception e) { instance=null; break; }
	    //#ifdef TOUCH
    	    if (holdCount==5 ) {
		if (attachedList.pointer_state == VirtualList.POINTER_FIRST || attachedList.pointer_state == VirtualList.POINTER_SECOND)
		attachedList.pointer_state = VirtualList.POINTER_LONG;
		holdCount=0;
		attachedList.redraw();
	    }
	    if (attachedList.pointer_state == VirtualList.POINTER_FIRST || attachedList.pointer_state == VirtualList.POINTER_SECOND) {
		holdCount++;
		continue;
	    } else {
                holdCount=0;
            }
	    //#endif
	    if (scroll==0) {
		if (instance.scroll() || instance.balloon())
		    try { attachedList.redraw(); } catch (Exception e) { instance=null; break; }
	    } else {
		scroll --;
	    }
        }
    }

    public boolean scroll() {
        //synchronized (this) {
            if (scrollline==false || attachedList==null || scrollLen<0)
                return false;
            if (attachedList.offset>=scrollLen) {
                scrollLen=-1; attachedList.offset=0; scrollline = false;
            } else
                attachedList.offset+=6;

            return true;
        //}
    }

    public boolean balloon() {
       // synchronized (this) {
            if (attachedList==null || balloon<0)
                return false;
            balloon--;
            attachedList.showBalloon=(balloon<20 && balloon>0);
            return true;
       // }
    }
}
//#endif
