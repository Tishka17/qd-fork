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

import java.util.Vector;
import menu.Command;
import midlet.BombusQD;

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
    
    public final static int NO_BGND = 0;
    public final static int JIMM_BGND = 1;
    public final static int GRADIENT_BGND = 2;
    public final static int BGND_FROM_JAR = 3;
    public final static int BGND_FROM_FS = 4;

    protected void focusedItem(int index) {}

    abstract protected int getItemCount();

    abstract protected VirtualElement getItemRef(int index);

    protected int getPanelBackgroundTop() { return ColorTheme.getColor(ColorTheme.BAR_BGND);}
    protected int getPanelBackgroundBottom() { return  ColorTheme.getColor(ColorTheme.BAR_BGND_BOTTOM);}

    private static StaticData sd=StaticData.getInstance();
    public static GMenuConfig gm = GMenuConfig.getInstance();

    private int stringHeight;

    private int iHeight;
    private int mHeight;

//#ifdef TOUCH
    public static int pointer_state = POINTER_NONE;
//#endif

//#ifdef GRADIENT
   private static Gradient cursorGradient = new Gradient();
   private static Gradient listGradient = new Gradient();
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

//#ifdef MEMORY_MONITOR
//#     public static boolean memMonitor;
//#endif

    protected boolean canBack=true;

    protected int cursor;

    protected boolean stickyWindow=true;

    private int itemLayoutY[]=new int[1];
    private int listHeight;

    public int getListHeight() {
        return listHeight;
    }
    public synchronized void updateLayout() {
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
        if (create) {
            if (bgndImage != null) {
                return;
            }
        }
        try {
            switch (Config.backImgType) {
                case NO_BGND:
                case GRADIENT_BGND:
                    bgndImage = null;
                    break;
                case JIMM_BGND:
                    Image image = Image.createImage("/images/back.png");
                    int imgW = image.getWidth();
                    int imgH = image.getHeight();
                    
                    int windowH = BombusQD.sd.canvas.getHeight();
                    int windowW = BombusQD.sd.canvas.getWidth();
                    
                    bgndImage = Image.createImage(windowW, windowH);
                    Graphics g = bgndImage.getGraphics();
                    for (int x = 0; x < windowW; x += imgW) {
                        for (int y = 0; y < windowH; y += imgH) {
                            g.drawImage(image, x, y, Graphics.LEFT | Graphics.TOP);
                        }
                    }
                    break;
                case BGND_FROM_JAR:
                    bgndImage = Image.createImage("/images/bgnd.jpg");
                    break;
                case BGND_FROM_FS:
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

    public static GMenu menuItem;

    public void paint(Graphics g) {
        mHeight=0;
        iHeight=0;

//#ifdef POPUPS
        getPopUp().init(g, width, height);
//#endif
        if(midlet.BombusQD.cf.graphicsMenu) {
           if(null != menuItem) {
             if(gm.ml!=null && gm.itemGrMenu==-1) menuItem.select(gm.inMenuSelected);
           }
        }

        beginPaint();

        //StaticData.getInstance().screenWidth=width;

        int list_bottom=0;
        itemBorder[0]=0;
        updateLayout();

        setAbsOrg(g, 0,0);

        g.setColor(ColorTheme.getColor(ColorTheme.LIST_BGND));
        g.fillRect(0, 0, width, height);

//#ifdef BACK_IMAGE
        switch (Config.backImgType) {
//#ifdef GRADIENT
            case GRADIENT_BGND:
                listGradient.update(0, 0, width, height, 
                        ColorTheme.getColor(ColorTheme.GRADIENT_BGND_LEFT),
                        ColorTheme.getColor(ColorTheme.GRADIENT_BGND_RIGHT), 
                        Gradient.CACHED_VERTICAL, 0);
                listGradient.paint(g);
                break;
//#endif
            case JIMM_BGND:
            case BGND_FROM_FS:
            case BGND_FROM_JAR:
                if (null != bgndImage) {
                    g.drawImage(bgndImage, 0, 0, Graphics.LEFT | Graphics.TOP);
                }
                break;
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
//#ifdef BACK_IMAGE
                if (Config.backImgType==NO_BGND && el.getColorBGnd()!=ColorTheme.getColor(ColorTheme.LIST_BGND)) 
//#endif
                {
                    g.fillRect(0, drawYpos, itemMaxWidth, lh);;
                }
                if (sel) {
                    drawCursor(g, 0, drawYpos, itemMaxWidth , lh);
                    baloon=drawYpos;
                }
                g.translate(0, drawYpos);
                g.setColor(el.getColor());
                g.clipRect(0, 0, itemMaxWidth, lh);
		try {
		    el.drawItem(this, g, (sel)?offset:0, sel);
		} catch (Exception e) {
		}

                ++itemIndex;
                itemBorder[++displayedIndex]=itemBorder[0]+itemYpos+lh;
            }
            el = null;
        } catch (Exception e) {
//#ifdef DEBUG
//#             System.out.println("Exception Vlist 1 -> "+e.getMessage()+" -> "+e.toString());
//#             e.printStackTrace();
//#endif
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
            if(null == menuItem) return;
            menuItem.paint(g);
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
 
     protected void drawCursor (Graphics g, int x0, int y0, int width, int height) { //Tishka17
//#ifdef GRADIENT
        if(midlet.BombusQD.cf.gradient_cursor 
//#ifdef TOUCH
                && !kinetic.isScrolling() && pointer_state!=POINTER_DRAG && pointer_state!=POINTER_FIRST && pointer_state!=POINTER_LONG
//#endif
                ){
             cursorGradient.update(x0, y0, width+x0, height+y0, ColorTheme.getColor(ColorTheme.GRADIENT_CURSOR_1),
                  ColorTheme.getColor(ColorTheme.GRADIENT_CURSOR_2), Gradient.CACHED_HORIZONTAL, 5);
             //cursorGradient.paintHRoundRect(g, 4);
             cursorGradient.paint(g);
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

    public void keyDown() {
        if (getItemCount()==0)
            return;
	if (cursor==(getItemCount()-1)) {
            if (!itemPageDown() && wrapping) {
                moveCursorHome();
            }
            setRotator();
            return;
        }
        if (itemPageDown()) {
            return;
        }
        int old_cursor = cursor;
        if (getItemRef(cursor+1).isSelectable()) {
            cursor++;
        } else {
            cursor=getNextSelectableRef(cursor);
        }
        if (cursor!=old_cursor)
            stickyWindow=true;
            setRotator();
    }

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
    
    private void drawMainPanel (final Graphics g, int y) {
        int h=mainbar.getVHeight();
//#ifdef GRADIENT
         if (getPanelBackgroundTop()!=getPanelBackgroundBottom()) {
            if (midlet.BombusQD.cf.gradientBarVertical) 
                MainBarBG.update(0, y, width, y+h+1, getPanelBackgroundBottom(), getPanelBackgroundTop(), 
                    Gradient.CACHED_HORIZONTAL, 0);
            else
                MainBarBG.update(0, y, width, y+h+1, getPanelBackgroundTop(), getPanelBackgroundBottom(), 
                    Gradient.MIXED_DOWN, 0);
            
            MainBarBG.paint(g);
            if (midlet.BombusQD.cf.shadowBar) {
                int sh = (width <= height)?width:height;
                if (reverse) {
                    sh = sh/50;
                    MainBarShadow.update(0, y-sh, width, y, 10<<24, 200<<24, Gradient.CACHED_HORIZONTAL, 0);
                    MainBarShadow.paint(g);
                }
                else {
                    sh = sh/40;
                    MainBarShadow.update(0, y+h, width, y+h+sh, 200<<24, 10<<24, Gradient.CACHED_HORIZONTAL, 0);
                    MainBarShadow.paint(g);
                }
            }
         } else 
//#endif
        {
            g.setColor(getPanelBackgroundTop());
            g.fillRect(0, y, width, h);
        }
        setAbsOrg(g, 0, y);
        g.setColor(getMainBarRGB());
        mainbar.drawItem(this, g,(phoneManufacturer==Config.NOKIA && !reverse)?17:0,false);
    }

    private void drawInfoPanel (final Graphics g, int y) {
        int h=infobar.getVHeight()+1;
//#ifdef GRADIENT
        if (getPanelBackgroundTop()!=getPanelBackgroundBottom()) {
            InfoBarBG.update(0, y, width, y+h,
                    getPanelBackgroundTop(),
                    getPanelBackgroundBottom(),
                    midlet.BombusQD.cf.gradientBarVertical?Gradient.CACHED_HORIZONTAL:Gradient.MIXED_UP, 0);
            InfoBarBG.paint(g);            
            if (midlet.BombusQD.cf.shadowBar) {
                int sh = (width <= height)?width:height;
                if (reverse) {
                    sh = sh/40;
                    InfoBarShadow.update(0, y+h, width, y+h+sh, 200<<24, 10<<24, Gradient.CACHED_HORIZONTAL, 0);
                    InfoBarShadow.paint(g);
                }
                else {
                    sh = sh/50;
                    InfoBarShadow.update(0, y-sh, width, y, 10<<24, 200<<24, Gradient.CACHED_HORIZONTAL, 0);
                    InfoBarShadow.paint(g);
                }
            }
        } else 
//#endif
        {
            g.setColor(getPanelBackgroundTop());
            g.fillRect(0, y, width, h);
        }
        if(midlet.BombusQD.sd.roster!=null) {
            if (midlet.BombusQD.sd.roster.messageCount>0) {
                drawEnvelop(g , width/2 - 5, (h-15)/2 + y + 1);
            }
        }
        if (System.currentTimeMillis()-sd.getTrafficIn()<2000) {
            drawTraffic(g, false, (h-15)/2 + y + 15);
        }
        if (System.currentTimeMillis()-sd.getTrafficOut()<2000) {
            drawTraffic(g, true, (h-15)/2 + y + 15);
        }
        setAbsOrg(g, 0, y);
        g.setColor(getMainBarRGB());
        infobar.drawItem(this, g,(phoneManufacturer==Config.NOKIA && reverse)?20:0,false);
    }

//#ifdef GRADIENT
    private static Gradient InfoBarBG=new Gradient();
    private static Gradient MainBarBG=new Gradient();
    private static Gradient InfoBarShadow=new Gradient();
    private static Gradient MainBarShadow=new Gradient();
//#endif
    
    public void moveCursorHome(){
        stickyWindow=true;
        if (cursor>0) cursor=getNextSelectableRef(-1);
        setRotator();
    }

    public void moveCursorEnd(){
        stickyWindow=true;
        cursor=getPrevSelectableRef(getItemCount());
        setRotator();
    }

    public void moveCursorTo(int index){
        int count=getItemCount();
	if (count<=0) return;
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

    int old_win_top;

//#ifdef TOUCH
    KineticScroller kinetic = KineticScroller.getInstance();
    
    protected void pointerPressed(int x, int y) {
        stickyWindow=false;
        kinetic.initPosition(this);
        lastClickTime=System.currentTimeMillis();
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

        if(cursor==-1) cursor = 0;
	setRotator();
        redraw();
   }


    private int old_drag_x=-1;
    private int old_drag_y=-1;
    protected void pointerDragged(int x, int y) {
        stickyWindow=false;
        if (pointer_state == POINTER_LONG)
            return;
        if (3 > Math.abs(old_drag_x - x)  && 3 > Math.abs(old_drag_y - y)) {
            return;
        }
        old_drag_x = x;
        old_drag_y = y;
        if(gm.itemGrMenu>0){
            if(null != menuItem) {
                menuItem.pointerPressed(x, y);
                redraw();
            }
            return;
        }

        if (pointer_state == POINTER_PANEL)
            return;
        if (pointer_state == POINTER_SCROLLBAR) {
            scrollbar.pointerDragged(x, y, this);
            redraw();
            return;
        }

        win_top = old_win_top - y + lastClickY;
        if (x - lastClickX > 9 || lastClickX-x >9
              || y - lastClickY > 9 || lastClickY-y>9) {
          pointer_state = POINTER_DRAG;
          kinetic.updatePostion();
        }

        if (win_top+winHeight>listHeight) win_top=listHeight-winHeight;
        if (win_top<0) win_top=0;
        redraw();
        return;
    }

    protected void touchMainPanelPressed(int x, int y) {
    }

    protected void pointerReleased(int x, int y) {
        stickyWindow=false;
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
//#ifdef USE_ROTATOR
            if (pointer_state==POINTER_LONG)
//#else
//#             if (clickTime-lastClickTime>500)
//#endif
            {
                y=0;
                eventLongOk();
            } else {
                if (pointer_state == POINTER_SECOND) {
                    VirtualElement element = (VirtualElement)getFocusedObject();
                    if (element != null && !element.eventPointerPressed(x, y)) {
                        eventOk();
                    }
                }
            }
        }
	redraw();
        //kinetic.updatePostion();
        kinetic.startScroll();
	pointer_state = POINTER_NONE;
    }

//#endif //TOUCH

    protected boolean sendEvent(int keyCode) {
        if(gm.itemGrMenu>0 && midlet.BombusQD.cf.graphicsMenu ) {
            if(null != menuItem) menuItem.keyPressed(keyCode);
            return true;
        }
//#ifdef POPUPS
        if (getPopUp().size()>0) {
            return popup.handleEvent(keyCode);
        } else 
//#endif
        {
            VirtualElement element = (VirtualElement)getFocusedObject();
            if (element != null) {
                return element.eventKeyPressed(keyCode);
            }
        }
        return false;
    }
    private boolean sendLongEvent(int keyCode) {
//#ifdef POPUPS
        if (getPopUp().size()>0) {
            return popup.handleEvent(keyCode);
        } else 
//#endif
        {
            VirtualElement element = (VirtualElement)getFocusedObject();
            if (element != null) {
                return element.eventKeyLong(keyCode);
            }
        }
        return false;    
    }
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

    public void addInCommand(int countMenu, Command command) {
        if(midlet.BombusQD.cf.graphicsMenu){
          if(countMenu==1){
            if (cmdfirstList.indexOf(command)<0)cmdfirstList.addElement(command);
          } else if(countMenu==2){
            if (cmdsecondList.indexOf(command)<0) cmdsecondList.addElement(command);
          } else if(countMenu==3){
            if (cmdThirdList.indexOf(command)<0) cmdThirdList.addElement(command);
          }
        }else{
            if (menuCommands.indexOf(command)<0) menuCommands.addElement(command);
        }
    }

    public void removeInCommand(int countMenu, Command command) {
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

    public void touchLeftPressed() {
         gm.itemGrMenu = showGraphicsMenu();
         redraw();
    }

    public int showGraphicsMenu() { return -10; }

    public Command getCommand(int index) {
        if (index>menuCommands.size()-1) return null;
        return (Command) menuCommands.elementAt(index);
    }


    public void touchRightPressed(){
        destroyView();
    }

    public void touchMiddlePressed(){
    }
     protected boolean keyLong(int keyCode) {
         if (sendLongEvent(keyCode)) {
            redraw();
            return true;
         }
         return false;
     }   
    protected void keyPressed(int keyCode) {        
     if (sendEvent(keyCode)) {
            redraw();
            return;
     }
     switch (keyCode) {
        case VirtualCanvas.CALL_KEY: {
            keyGreen();
            return;
        }
        case VirtualCanvas.LEFT_SOFT: {
            gm.itemCursorIndex=0;
            gm.itemCursorIndexIn=0;
            touchLeftPressed();
            return;
        }
        case VirtualCanvas.RIGHT_SOFT: {
            touchRightPressed();
            return;
        }
        case VirtualCanvas.KEY_NUM1:
            moveCursorHome();
            break;
        case VirtualCanvas.KEY_NUM2:
            keyUp();
            break;
        case VirtualCanvas.VOLPLUS_KEY: 
        case VirtualCanvas.KEY_NUM4:
            pageLeft();
            break;
        case VirtualCanvas.VOLMINUS_KEY: 
        case VirtualCanvas.KEY_NUM6:
            pageRight();
            break;
        case VirtualCanvas.KEY_NUM7:
            moveCursorEnd();
            break;
        case VirtualCanvas.KEY_NUM8:
            keyDown();
            break;
        case VirtualCanvas.KEY_STAR:
            if (!isServiceDiscoWindow) {
                midlet.BombusQD.sd.roster.systemGC();
            } 
//#ifdef POPUPS
            StringBuffer mem = new StringBuffer(0);
            mem.append(Time.getTimeWeekDay()).append("\nTraffic: ").append(getTraffic());
            if (midlet.BombusQD.cf.userAppLevel == 1) {
                long free = Runtime.getRuntime().freeMemory() >> 10;
                long total = Runtime.getRuntime().totalMemory() >> 10;
                long inUse = total - free;
                mem.append("\nQD use: ").append(inUse).
                        append("kb  (").append((100 * inUse / total)).
                        append("%)\nStanzas(in/out): ").append(StaticData.incPacketCount).
                        append('/').append(StaticData.outPacketCount);
            }
            setWobble(1, null, mem.toString());
//#endif
            break;
//#ifdef POPUPS
        case VirtualCanvas.KEY_POUND:
            try {
                String text = ((VirtualElement)getFocusedObject()).getTipString();
                if (text != null) {
                    setWobble(1, null, text);
                }
            } catch (Exception e) {
            }
            break;
//#endif

        case VirtualCanvas.NAVIKEY_UP:
            keyUp();
            break;
        case VirtualCanvas.NAVIKEY_DOWN:
            keyDown();
            break;
        case VirtualCanvas.NAVIKEY_LEFT:
            pageLeft();
            break;
        case VirtualCanvas.NAVIKEY_RIGHT:
            pageRight();
            break;
        case VirtualCanvas.NAVIKEY_FIRE:
        case VirtualCanvas.KEY_NUM5: 
            eventOk();
            break;
        case VirtualCanvas.CLEAR_KEY: 
            keyClear();
            break;
        case VirtualCanvas.CLOSE_KEY: 
            if (canBack) 
                destroyView();
            break;
        }
        redraw();
    }

    public int getPrevSelectableRef(int curRef) {
        int prevRef=curRef;
        boolean process=true;
        while (process) {
            prevRef--;
            if (getItemRef(prevRef).isSelectable())
                break;
            if (prevRef==0 ) {
                if (wrapping)
                    prevRef=getItemCount();
                else 
                    return curRef;
            }
        }

        return prevRef;
    }

    public int getNextSelectableRef(int curRef) {
        int nextRef=curRef;
        boolean process=true;
        while (process) {
            nextRef++;
            if (nextRef==getItemCount()) {
                if (wrapping)
                    nextRef=0;
                else 
                    return curRef;
            }
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
    protected  void stopRotator(){
//#if (USE_ROTATOR)
        TimerTaskRotate.stopping();
 //#endif
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
                                 int msgNext = find.getNewMessageCount();
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
		if (VirtualList.pointer_state == VirtualList.POINTER_FIRST || VirtualList.pointer_state == VirtualList.POINTER_SECOND)
		VirtualList.pointer_state = VirtualList.POINTER_LONG;
		holdCount=0;
		attachedList.redraw();
		new EventNotify(null, null, -1, 30).startNotify();
	    }
	    if (VirtualList.pointer_state == VirtualList.POINTER_FIRST || VirtualList.pointer_state == VirtualList.POINTER_SECOND) {
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
    
    public static void stopping() {
        instance.scrollline = false;
        instance.balloon = -1;
        instance.scroll = 0;
    }
}
//#endif
