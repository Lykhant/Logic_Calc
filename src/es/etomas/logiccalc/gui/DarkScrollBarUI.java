package es.etomas.logiccalc.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class DarkScrollBarUI extends BasicScrollBarUI{
	
	@Override
	protected JButton createDecreaseButton(int orientation) {
		JButton button = new JButton();
		return button;
	}
	
	@Override
	protected JButton createIncreaseButton(int orientation) {
		JButton button = new JButton();
		button.setBackground(CalcFrame.gray1);
		return button;
	}
	
	protected void configureScrollBarColors()
    {
        thumbHighlightColor = CalcFrame.gray2;
        thumbLightShadowColor = CalcFrame.gray2;
        thumbDarkShadowColor = CalcFrame.gray2;
        thumbColor = CalcFrame.gray2;
        trackColor = CalcFrame.gray3;
        trackHighlightColor = CalcFrame.gray2;
        
    }
	
	protected Dimension getMinimumThumbSize() {
		return new Dimension(5,5);
	}
	protected Dimension getMaximumThumbSize() {
		return new Dimension(700,700);
	}
	
//	@Override
//	protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
//		((Graphics2D) g).setColor(CalcFrame.gray2);
//		((Graphics2D) g).fillRect(0, 0, r.width, 200);
//	}
	
//	@Override
//	protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
//		((Graphics2D) g).setColor(CalcFrame.gray1);
//		((Graphics2D) g).fillRect(0, 0, r.width, 200);
//	}
}
