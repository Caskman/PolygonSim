package caskman.polygonsim.screens;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.List;

import caskman.polygonsim.model.Vector;

public class ButtonItem extends Item {
		
		private String text;
		private int textSize;
		private List<ButtonItemListener> listeners;
		private Dimension dims;
		Vector position;
		Vector textPosition;
		boolean isPressed;
		private int textStyle;
		
		public ButtonItem() {
			listeners = new LinkedList<ButtonItemListener>();
			position = textPosition = null;
			dims = null;
			textSize = 12;
			isPressed = false;
			textStyle = Font.BOLD;
		}
		
		public void setText(String s) {
			text = s;
		}
		
		public void setTextSize(int size) {
			textSize = size;
		}
		
		public void addOptionItemListener(ButtonItemListener l) {
			listeners.add(l);
		}
		
		private void updateTextPosition(FontMetrics m) {
			Dimension textDims = new Dimension(m.stringWidth(text),m.getHeight());
			textPosition = new Vector(position.x + (dims.width - textDims.width)/2,position.y + (dims.height - textDims.height)/2 + m.getAscent());
		}
		
		@Override
		public void draw(Graphics2D g,float interpol) {
//			if (position == null)
//				updateDimsandPositions();
			g.setColor(Color.WHITE);
			g.setFont(getFont());
			updateTextPosition(g.getFontMetrics());
			
			g.setColor((isPressed)?Color.WHITE:Color.BLACK);
			g.fillRect((int)position.x, (int)position.y, dims.width, dims.height);
			
			g.setColor(Color.WHITE);
			g.drawRect((int)position.x, (int)position.y, dims.width, dims.height);
			
			g.setColor((isPressed)?Color.BLACK:Color.WHITE);
			g.drawString(text,textPosition.x,textPosition.y);
		}
		
		@Override
		public void manageInput(InputEvent e) {
			if (e.isMouseInput()) {
				Vector v = e.getVector();
				if (e.getType() == InputEvent.MOUSE_PRESSED) {
					if (isWithinDims(v))
						isPressed = true;
				}
				if (e.getType() == InputEvent.MOUSE_RELEASED) {
					if (isPressed && (isWithinDims(v)))
						notifyListeners();
					isPressed = false;
				}
			}
		}
		
		private boolean isWithinDims(Vector v) {
			return v.x > position.x && v.x < (position.x+dims.width) && v.y > position.y && v.y < (position.y + dims.height);
		}
		
		private void notifyListeners() {
			for (ButtonItemListener l : listeners) {
				l.itemActivated();
			}
		}
		
		private Font getFont() {
			return new Font(Font.SANS_SERIF,textStyle,textSize);
		}
		
		@Override
		public void setPosition(Vector v) {
			position = v;
		}

		@Override
		public void setDims(Dimension d) {
			dims = d;
		}

		@Override
		public Dimension getDims() {
			return dims;
		}
		
		public interface ButtonItemListener {
			public void itemActivated();
		}

		@Override
		public Vector getPosition() {
			return position;
		}

	}
