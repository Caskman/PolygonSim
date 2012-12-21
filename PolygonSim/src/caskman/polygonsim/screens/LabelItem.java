package caskman.polygonsim.screens;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.List;

import caskman.polygonsim.model.Vector;

public class LabelItem extends Item {
		
		private String text;
		private int textSize;
		private Dimension dims;
		Vector position;
		Vector textPosition;
		
		public LabelItem() {
			position = textPosition = null;
			dims = null;
			textSize = 12;
		}
		
		public void setText(String s) {
			text = s;
		}
		
		public void setTextSize(int size) {
			textSize = size;
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
			
//			g.drawRect((int)position.x, (int)position.y, dims.width, dims.height);
			g.drawString(text,textPosition.x,textPosition.y);
		}
		
		@Override
		public void manageInput(InputEvent e) {
			
		}
			
		
		private Font getFont() {
			return new Font(Font.SANS_SERIF,Font.PLAIN,textSize);
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
		
		public interface OptionItemListener {
			public void itemActivated();
		}

		@Override
		public Vector getPosition() {
			return position;
		}

	}
