package org.anidev.frcds.pc.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JToggleButton;
import org.anidev.utils.Utils;

class EnhancedToggleButton extends JToggleButton implements
		ItemListener {
	private Color origColor;
	private Color selectedColor;
	private EnhancedToggleButton.BorderCollapse borderCollapse;
	private Paint borderColor=null;
	private static final int BORDER_SIZE=4;
	private static final int BORDER_PADDING=2;
	private static final int BORDER_TRANSLATE=BORDER_SIZE+BORDER_PADDING;

	public EnhancedToggleButton(String text,Color overlayColor,
			double selectedAlpha,EnhancedToggleButton.BorderCollapse border) {
		super(text);
		this.borderCollapse=border;
		this.origColor=getBackground();
		selectedColor=Utils.calcAlpha(selectedAlpha,overlayColor,origColor);
		setFont(new Font("Arial",Font.BOLD,12));
		addItemListener(this);
		changeState(isSelected());
		Object borderPref=Utils.getNimbusPref("nimbusBorder",this);
		if(borderPref!=null&&borderPref instanceof Paint) {
			borderColor=(Paint)borderPref;
		}
	}

	public void setSelected(boolean selected) {
		super.setSelected(selected);
		changeState(selected);
	}

	public void changeState(boolean selected) {
		if(selected) {
			setBackground(selectedColor);
			setForeground(null);
		} else {
			setBackground(null);
			setForeground(selectedColor);
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		Rectangle bounds=g.getClipBounds();
		switch(borderCollapse) {
		case BOTTOM:
			g.translate(0,BORDER_TRANSLATE);
			break;
		case TOP:
			g.translate(0,-BORDER_TRANSLATE);
			break;
		default:
		}
		super.paintComponent(g);
		g.setClip(bounds);
		if(borderColor!=null&&g instanceof Graphics2D) {
			Graphics2D g2d=(Graphics2D)g;
			g2d.setPaint(borderColor);
			int minX=bounds.x+BORDER_SIZE;
			int maxX=bounds.x+bounds.width-BORDER_SIZE;
			int minY=bounds.y+BORDER_SIZE;
			int maxY=bounds.y+bounds.height-BORDER_SIZE;
			switch(borderCollapse) {
			case BOTTOM:
				g.drawLine(minX,maxY-BORDER_PADDING,maxX,maxY
						-BORDER_PADDING);
				break;
			case TOP:
				g.drawLine(minX,minY+BORDER_PADDING,maxX,minY
						+BORDER_PADDING);
				break;
			case NONE:
			}
		}
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if(!(e.getSource() instanceof EnhancedToggleButton)) {
			return;
		}
		EnhancedToggleButton button=(EnhancedToggleButton)e.getSource();
		button.changeState(e.getStateChange()==ItemEvent.SELECTED);
	}

	public static enum BorderCollapse {
		NONE,
		BOTTOM,
		TOP;
	}
}