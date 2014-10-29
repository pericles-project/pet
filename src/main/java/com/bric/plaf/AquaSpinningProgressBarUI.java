/*
 * @(#)AquaSpinningProgressBarUI.java
 *
 * $Date: 2012-07-03 01:10:05 -0500 (Tue, 03 Jul 2012) $
 *
 * Copyright (c) 2011 by Jeremy Wood.
 * All rights reserved.
 *
 * The copyright of this software is owned by Jeremy Wood. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Jeremy Wood. For details see accompanying license terms.
 * 
 * This software is probably, but not necessarily, discussed here:
 * http://javagraphics.java.net/
 * 
 * That site should also contain the most recent official version
 * of this software.  (See the SVN repository for more details.)
 */
package com.bric.plaf;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.util.Hashtable;

import javax.swing.JComponent;


/** Twelve short line segments that pulse in a clockwise direction.
 * 
 * <P>The line segments are painted in the component's foreground color,
 * with varying levels of opacity.  (When this UI is installed the component's
 * foreground color is set to Color.black.)
 * <P>Also the line segments will complete a full revolution in 500 milliseconds.
 * You can change this rate with the client property "period".  This is
 * the length (in milliseconds) this UI takes to complete a full
 * cycle.
 */
public class AquaSpinningProgressBarUI extends SpinningProgressBarUI {
	
	public static class Icon implements javax.swing.Icon {
		int width = 19;
		int height = 19;
		Color color = Color.gray;

		public Icon() {
		}
		
		public Icon(int width,int height,Color color) {
			this.width = width;
			this.height = height;
			this.color = color;
		}
		
		public Icon(int width,int height) {
			this.width = width;
			this.height = height;
		}

		public Icon(Color color) {
			this.color = color;
		}

		public int getIconHeight() {
			return height;
		}

		public int getIconWidth() {
			return width;
		}

		public void paintIcon(Component c, Graphics g0, int x, int y) {
			Graphics2D g = (Graphics2D)g0.create();
			g.translate(x, y);
			double sx = (width)/19.0;
			double sy = (height)/19.0;
			g.scale(sx, sy);
			float fraction = ((System.currentTimeMillis()%1000))/1000f;
			g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
					RenderingHints.VALUE_STROKE_PURE);
			AquaSpinningProgressBarUI.paint(
					g, 
					fraction, 
					color, 
					9, 
					9, 
					5, 
					8,
					1.9f);
			g.dispose();
		}
		
	}

	/** An Icon that resembles the spinning progress indicator in Aqua.
	 * This icon requires constant repainting in order animate correctly.
	 * 
	 */
	public static Icon ICON = new Icon();
	
	private static final Hashtable<Color, Color[]> foregroundTable = new Hashtable<Color, Color[]>();

	/** The default duration (in ms) it takes to complete a cycle.
	 * <BR>You can customize this by setting the client property "period" to an arbitrary
	 * positive number.
	 */
	public static final Long DEFAULT_PERIOD = new Long(500);

	private static final Line2D line = new Line2D.Float();
	
	/** Paints the 12 angular lines in a circle often used to indicate
	 * progress in the Aqua interface.
	 * <p>It is strongly recommended that you set the KEY_STROKE_CONTROL
	 * rendering hint to VALUE_STROKE_PURE to really achieve the desired
	 * look.
	 * 
	 * @param g the graphics to paint to
	 * @param fraction a fractional value between [0,1] indicating how far
	 * the angle has progress. As this value increases the highlighted line
	 * segment moves clockwise. The default behavior is for this value to iterate
	 * from [0,1] in approximately 1 second.
	 * @param foreground the color of the darkest line segment. All other line
	 * segments are calculated as translucent shades of this color.
	 * @param centerX the x-value of the center of this circle.
	 * @param centerY the y-value of the center of this circle.
	 * @param r1 the radius of one end point of a line segment in this circle. The default value is 5.
	 * @param r2 the radius of the other end point of a line segment in this circle. The default value is 8.
	 * @param strokeWidth the width of the stroke. The default value is 1.9f.
	 */
	public static void paint(Graphics2D g,float fraction,Color foreground,int centerX,int centerY,int r1,int r2,float strokeWidth) {

		if(fraction<0) throw new IllegalArgumentException("fraction ("+fraction+") must be within [0, 1]");
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		int i = (int)(fraction*12);

		Color[] colors = foregroundTable.get(foreground);
		if (colors == null) {
			int red = foreground.getRed();
			int green = foreground.getGreen();
			int blue = foreground.getBlue();
			colors = new Color[] {
					new Color(red,green,blue,255),
					new Color(red,green,blue,240),
					new Color(red,green,blue,225),
					new Color(red,green,blue,200),
					new Color(red,green,blue,160),
					new Color(red,green,blue,130),
					new Color(red,green,blue,115),
					new Color(red,green,blue,100),
					new Color(red,green,blue,90),
					new Color(red,green,blue,80),
					new Color(red,green,blue,70),
					new Color(red,green,blue,60)
					
			};
		}
		
		g.setStroke( new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_BEVEL));
		double theta;
		for (int a = 0; a < colors.length; a++) {
			g.setColor(colors[(i+a)%colors.length]);
			theta = -((double)a)/(colors.length)*Math.PI*2;
			line.setLine(centerX+r1*Math.cos(theta),
					centerY+r1*Math.sin(theta),
					centerX+r2*Math.cos(theta),
					centerY+r2*Math.sin(theta) );
			
			g.draw(line);
		}
	}

	@Override
	protected void paintForeground(Graphics2D g, JComponent jc,Dimension size) {

		/** This is only intended to be used in the SpinningProgressBarUIDemo. */
		Boolean useStrokeControl = (Boolean)jc.getClientProperty("useStrokeControl");
		if(useStrokeControl==null) useStrokeControl = Boolean.TRUE;
		if(useStrokeControl.booleanValue()) {
			g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
					RenderingHints.VALUE_STROKE_PURE);
		}

		Number period = (Number)jc.getClientProperty("period");
		if(period==null) period = DEFAULT_PERIOD;
		
		float f = ((float) (System.currentTimeMillis() % period.longValue())) / period.longValue();
		
		Number forcedValue = (Number)jc.getClientProperty("forcedFraction");
		if(forcedValue!=null)
			f = forcedValue.floatValue();
		
		paint(g, f, jc.getForeground(), size.width/2, size.height/2, 5, 8, 1.9f );
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		c.setForeground(Color.gray);
	}
	
	@Override
	public Dimension getPreferredSize(JComponent c) {
		return new Dimension(19, 19);
	}
}
