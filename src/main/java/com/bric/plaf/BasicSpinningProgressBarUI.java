/*
 * @(#)BasicSpinningProgressBarUI.java
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
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;

import javax.swing.JComponent;

/** Two spinning arrows.
 * 
 * <P>The arrows are painted in the component's foreground color.  (When
 * this UI is installed the component's foreground color is set to
 * Color.darkGray.)
 * <P>Also the arrows will complete a full revolution in 2000 milliseconds.
 * You can change this rate with the client property "period".  This is
 * the length (in milliseconds) this UI takes to complete a full
 * cycle.
 */
public class BasicSpinningProgressBarUI extends SpinningProgressBarUI {

	/** The default duration (in ms) it takes to complete a cycle.
	 * <BR>You can customize this by setting the client property "period" to an arbitrary
	 * positive number.
	 */
	public static final Long DEFAULT_PERIOD = new Long(2000);
	private static final float PI = (float)Math.PI;
	private static final int[] x = new int[] {8, 8, 11};
	private static final int[] y = new int[] {0, 6, 3};
	private static final BasicStroke stroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);

	private AffineTransform transform = new AffineTransform();
	private Arc2D arc = new Arc2D.Float(3, 3, 10, 10, 65, 140, Arc2D.OPEN);
	private GeneralPath path = new GeneralPath();
	
	@Override
	protected synchronized void paintForeground(Graphics2D g,JComponent jc,Dimension size) {

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
			
		/** This is only intended to be used in the SpinningProgressBarUIDemo. */
		Boolean useStrokeControl = (Boolean)jc.getClientProperty("useStrokeControl");
		if(useStrokeControl==null) useStrokeControl = Boolean.TRUE;
		if(useStrokeControl.booleanValue()) {
			g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
					RenderingHints.VALUE_STROKE_PURE);
		}
		
		Number period = (Number)jc.getClientProperty("period");
		if(period==null) period = DEFAULT_PERIOD;
		
		g.setStroke(stroke);
		float t = System.currentTimeMillis()%period.longValue();
		float f = t / (period.longValue()) * 2 * PI;
		
		Number forcedValue = (Number)jc.getClientProperty("forcedFraction");
		if(forcedValue!=null)
			f = forcedValue.floatValue();

		g.setColor(jc.getForeground());

		for(int k = 0; k<2; k++) {
			transform.setToRotation(f+ k*Math.PI, size.width/2, size.height/2);
	
			path.reset();
			path.moveTo(x[0],y[0]);
			path.lineTo(x[1],y[1]);
			path.lineTo(x[2],y[2]);
			path.lineTo(x[0],y[0]);
			path.transform(transform);
			
			g.fill(path);
			
			path.reset();
			path.append(arc.getPathIterator(transform), false);
			g.draw(path);
		}
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		c.setForeground(Color.darkGray);
	}
}
