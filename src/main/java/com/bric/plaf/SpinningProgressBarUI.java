/*
 * @(#)SpinningProgressBarUI.java
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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.ref.WeakReference;

import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.Timer;
import javax.swing.plaf.ProgressBarUI;

public abstract class SpinningProgressBarUI extends ProgressBarUI {

	private static final String REPAINTER_KEY = "SpinningProgressBarUI.repainter";
	
	public static final boolean isMac = System.getProperty("os.name").toLowerCase().indexOf("mac")!=-1;
	
	/** Creates a spinning progress bar.
	 * On Mac this will resemble Aqua, on other platforms
	 * this will be two chasing arrows.
	 */
	public static JProgressBar create() {
		JProgressBar bar = new JProgressBar();
		ProgressBarUI ui;
		if(isMac) {
			ui = new AquaSpinningProgressBarUI();
		} else {
			ui = new BasicSpinningProgressBarUI();
		}
		bar.setUI(ui);
		return bar;
	}
	
	/** Returns the number of milliseconds between
	 * calls to repaint.
	 * <P>This should be a fixed value that does not change.
	 */
	public int getRepaintDelay() {
		return 1000/24;
	}

	/** @return <code>getPreferredSize(c)</code> */
	@Override
	public Dimension getMaximumSize(JComponent c) {
		return getPreferredSize(c);
	}

	/** @return <code>getPreferredSize(c)</code> */
	@Override
	public Dimension getMinimumSize(JComponent c) {
		return getPreferredSize(c);
	}

	/** @return 16x16 */
	@Override
	public Dimension getPreferredSize(JComponent c) {
		return new Dimension(16, 16);
	}

	/**
	 * Sets up the timer and <code>ChangeListener</code> to make sure this
	 * idler repaints appropriately.
	 */
	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		Timer timer = new Timer(getRepaintDelay(),new RepaintListener(c));
		c.putClientProperty(REPAINTER_KEY, timer);
		timer.start();
	}

	@Override
	public void paint(Graphics g0, JComponent jc) {
		Graphics2D g = (Graphics2D) g0.create();
		paintBackground(g,jc);
		
		Dimension d = getPreferredSize(jc);
		double sx = ((double)jc.getWidth())/((double)d.width);
		double sy = ((double)jc.getHeight())/((double)d.height);
		double scale = Math.min(sx,sy);
		g.scale(scale,scale);
		
		paintForeground(g,jc,d);
		g.dispose();
	}
	
	protected void paintBackground(Graphics2D g,JComponent jc) {
		if(jc.isOpaque()) {
			g.setColor(jc.getBackground());
			g.fillRect(0, 0, jc.getWidth(), jc.getHeight());
		}
	}

	/** Paints the foreground
	 * 
	 * @param g the graphics to paint to.
	 * @param jc the component to paint.
	 * @param size the dimensions to paint to.
	 * Assume these are the dimensions of the component you
	 * are painting (the Graphics2D has been transformed to
	 * work within these dimensions).
	 */
	protected abstract void paintForeground(Graphics2D g,JComponent jc,Dimension size);

	/** Disarms the timer and removes the <code>ChangeListener</code> */
	@Override
	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);
		Timer timer = (Timer) c.getClientProperty(REPAINTER_KEY);
		timer.stop();
		c.repaint();
	}

	static class RepaintListener implements ActionListener {
		WeakReference<JComponent> reference;

		public RepaintListener(JComponent c) {
			reference = new WeakReference<JComponent>(c);
		}

		public void actionPerformed(ActionEvent e) {
			JComponent jc = reference.get();
			if(jc!=null)
				jc.repaint();
		}
	}
}
