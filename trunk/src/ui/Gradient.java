/*
 * Gradient.java
 *
 * Created on 15.05.2008, 19:47
 *
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
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

//#ifdef GRADIENT
package ui;
import javax.microedition.lcdui.Graphics;

public class Gradient {
	public final static int VERTICAL=0;
	public final static int HORIZONTAL=1;
	public final static int MIXED_UP=2;
	public final static int MIXED_DOWN=3;
	public final static int CACHED_VERTICAL = 4;
	public final static int CACHED_HORIZONTAL = 5;

	private int x1;
	private int x2;

	private int y1;
	private int y2;

	private int redS;
	private int redE;

	private int greenS;
	private int greenE;

	private int blueS;
	private int blueE;

	private int type;
	private int[] points=null;

	public Gradient() {}

	public void update(int x1, int y1, int x2, int y2, int STARTRGB, int ENDRGB, int type) {
		int redS = STARTRGB >> 16 & 0xff;
		int redE = ENDRGB >> 16 & 0xff;
		int greenS = STARTRGB >> 8 & 0xff;
		int greenE = ENDRGB >> 8 & 0xff;
		int blueS = STARTRGB & 0xff;
		int blueE = ENDRGB & 0xff;
		boolean changed = false;
		if (points==null || 
				this.x1!=x1 || this.x2!=x2 ||  
				this.y1!=y1 || this.y2!=y2 || 
				this.redS!=redS || this.redE!=redE || 
				this.greenS!=greenS ||this.greenE!=greenE ||
				this.blueS!=blueS || this.blueE!=blueE|| this.type!=type) {
			changed = true;
		}
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
		this.redE = redE;
		this.redS = redS;
		this.blueE = blueE;
		this.blueS = blueS;
		this.greenE = greenE;
		this.greenS = greenS;
		this.type = type; 
		if ((type==MIXED_UP || type==MIXED_DOWN || type == CACHED_VERTICAL || type==CACHED_HORIZONTAL) && changed) {
			makePoints();
		}

	}
	public void paint(Graphics g) {            
		switch (type) {
			case VERTICAL:
				paintV(g); 
				break;
			case HORIZONTAL:
				paintH(g); 
				break;
			case CACHED_VERTICAL:
			case CACHED_HORIZONTAL:
			case MIXED_UP:
			case MIXED_DOWN:
				g.drawRGB(points, 0, x2-x1, x1, y1 , x2-x1, y2-y1, false);
				break;
		}            
	}

	public void paintHRoundRect(Graphics g, int R) {//Makasi
		int ds = 0;
		for(int i2 = y1; i2 <= y2 - 1; ++i2) {
			int ai[] = GradBackgr(redS, greenS, blueS, redE, greenE, blueE, i2, y1, y2 - 1);
			g.setColor(ai[0], ai[1], ai[2]);

			ds = 0;
			if ((R-i2+y1) > 0)
				ds = (int)(R+1-sqrt((i2+1-y1)*2*R-(i2+1-y1)*(i2+1-y1)));
			else if (R-y2+i2 > 0)
				ds = (int)(R+1-sqrt((y2-i2)*2*R-(y2-i2)*(y2-1-i2)));

			if (ds != 0)
				g.drawLine(x1+ds, i2, x2-ds - 1, i2);
			else
				g.drawLine(x1, i2, x2 - 1, i2);
		}
	}

	private int sqrt( int x ) {
		final byte iterations = 20;
		int a = 7;
		for( int i2 = 0; i2 < iterations; i2++ ) {
			if( a == 0 ) 
				break;
			a = ( a + x / a ) >> 1;
		}
		return a;
	}

	private void paintV(Graphics g) {
		for(int i2 = x1; i2 <= x2 - 1; ++i2) {
			int gCol[] = GradBackgr(redS, greenS, blueS, redE, greenE, blueE, i2, x1, x2 - 1);
			g.setColor(gCol[0], gCol[1], gCol[2]);
			g.drawLine(i2, y1, i2, y2);
		}
	}

	private void paintH(Graphics g) {
		for(int i2 = y1; i2 <= y2 - 1; ++i2) {
			int ai[] = GradBackgr(redS, greenS, blueS, redE, greenE, blueE, i2, y1, y2 - 1);
			g.setColor(ai[0], ai[1], ai[2]);
			g.drawLine(x1, i2, x2 - 1, i2);
		}
	}


	public void paintWidth(Graphics g, int width) {
		for(int i2 = y1; i2 <= y2 - 1; ++i2) {
			int ai[] = GradBackgr(redS, greenS, blueS, redE, greenE, blueE, i2, y1, y2 - 1);
			g.setColor(ai[0], ai[1], ai[2]);
			g.drawLine(x1, i2, width-1, i2);
		}
	}

	private int[] GradBackgr(int rS, int gS, int bS, int rE, int gE, int bE, int l1, int i2, int j2) {
		return new int[] { 
			(rE*(l1-i2)+rS*(j2-l1))/(j2-i2), 
				(gE*(l1-i2)+gS*(j2-l1))/(j2-i2), 
				(bE*(l1-i2)+bS*(j2-l1))/(j2-i2)
		};
	}

	private void makePoints() {
		int width = x2-x1;
		int height = y2-y1;

		points = new int[height*width];
		if (type == MIXED_DOWN || type == MIXED_UP) {
			int r,g,b,dist,diff,new_r,new_g,new_b,color = 0;
			int yS,yE,yD;

			int width2 = width/2;
			int width3 = width/3;

			int idx = 0;

			if (type==MIXED_UP) {
				yS=height;
				yE=1;
				yD=-1;
			} else {
				yS=0;
				yE=height-1;
				yD=1;
			}
			for (int y = yS; y != yE; y+=yD)
			{
				r = y * (redE - redS) / (height-1) + redS;
				g = y * (greenE - greenS) / (height-1) + greenS;
				b = y * (blueE - blueS) / (height-1) + blueS;
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
					points[idx++] = color;
				}
			}
		} else if (type == CACHED_VERTICAL) {               
			for (int x = x1; x < width; ++x) {
                            int ai[] = GradBackgr(redS, greenS, blueS, redE, greenE, blueE, x, x1, x2 - 1);
                            int color = (ai[0] << 16) | (ai[1] << 8) | (ai[2]);
                            for (int y = y1; y < height; ++y) {
                                    points[width * y + x] = color;
                            }
			}
		} else if (type == CACHED_HORIZONTAL) {    
			for (int y = y1; y < height; ++y) {
                            int ai[] = GradBackgr(redS, greenS, blueS, redE, greenE, blueE, y, y1, y2 - 1);
                            int color = (ai[0] << 16) | (ai[1] << 8) | (ai[2]);          
                            for (int x = x1; x < width; ++x) {
                                    points[width * y + x] = color;
                            }
			}
		}  
	}
}
//#endif
