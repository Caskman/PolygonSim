package caskman.polygonsim.screens;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import caskman.polygonsim.Launcher;
import caskman.polygonsim.model.GameModel;
import caskman.polygonsim.model.Vector;

public class OptionsScreen extends GameScreen {
	
	private GameModel model;
	private List<Item> optionItems;

	public OptionsScreen(ScreenManager manager, boolean isFullscreen,GameModel gameModel) {
		super(manager, isFullscreen);
		model = gameModel;
		
		initialize();
	}
	
	private void initialize() {
		optionItems = new ArrayList<Item>();
		OptionItem i;
		StackPanel sp = new StackPanel();
		Dimension stackDims = new Dimension(manager.getScreenDims().width/8,3*manager.getScreenDims().height/8);
		sp.setDims(stackDims);
		sp.setPosition(new Vector((manager.getScreenDims().width - stackDims.width)/2,3*manager.getScreenDims().height/8));
		sp.setOrientation(StackPanel.VERTICAL);
		optionItems.add(sp);
		
		
		i = new OptionItem();
		i.setText("Options");
		i.setTextSize(40);
		i.setDims(new Dimension(200,50));
		i.setPosition(new Vector((manager.getScreenDims().width - i.getDims().width)/2,manager.getScreenDims().height/4));
		optionItems.add(i);
		
		i = new OptionItem();
		i.setText("Change Window Mode");
		i.setDims(new Dimension(150,20));
		sp.add(i);
		
		i = new OptionItem();
		i.setText("Change Dot Color");
		i.setDims(new Dimension(150,20));
		sp.add(i);
		
		i = new OptionItem();
		i.setText("Change Line Color");
		i.setDims(new Dimension(150,20));
		sp.add(i);
		
		i = new OptionItem();
		i.setText("Change Background Color");
		i.setDims(new Dimension(150,20));
		sp.add(i);
		
		i = new OptionItem();
		i.setText("Exit");
		i.setDims(new Dimension(100,20));
		i.setTextSize(20);
		i.addOptionItemListener(new OptionItemListener() {
			@Override
			public void itemActivated() {
				Launcher.exit();
			}
		});
		sp.add(i);
		
		
	}

	@Override
	public void updateScreen() {
		
	}

	@Override
	public void drawScreen(Graphics2D g, float interpol) {
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5F));
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, manager.getScreenDims().width, manager.getScreenDims().height);
		g.setComposite(AlphaComposite.SrcOver);
		
		Rectangle r = new Rectangle(manager.getScreenDims().width/8, manager.getScreenDims().height/8, 3*manager.getScreenDims().width/4, 3*manager.getScreenDims().height/4);
		g.setColor(Color.BLACK);
		g.fillRect(r.x,r.y,r.width,r.height);
		g.setColor(Color.WHITE);
		g.drawRect(r.x,r.y,r.width,r.height);
		
		for (Item i : optionItems) {
			i.draw(g,interpol);
		}
	}

	@Override
	public void manageInput(InputEvent e) {
		if (e.isKeyInput()) {
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE && e.getType() == InputEvent.KEY_PRESSED) {
				exitScreen();
				model.setPaused(false);
			}
		}
		
		for (Item i : optionItems) {
			i.manageInput(e);
		}
	}
	
	private class OptionItem extends Item {
		
		private String text;
		private int textSize;
//		private Vector centerPosition;
		private List<OptionItemListener> listeners;
		private Dimension dims;
		Vector position;
		Vector textPosition;
		
		public OptionItem() {
			listeners = new LinkedList<OptionItemListener>();
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
		
//		@Override
//		public void setCenterPosition(Vector v) {
//			centerPosition = v;
//		}
		
		public void addOptionItemListener(OptionItemListener l) {
			listeners.add(l);
		}
		
//		@Override
//		public void updateDimsandPositions() {
//			FontMetrics m = new MyFontMetrics(getFont());
//			int padding = 4;
//			dims = new Dimension(m.stringWidth(text) + padding,m.getAscent() + padding);
//			position = new Vector(centerPosition.x - dims.width/2,centerPosition.y - dims.height/2);
//			textPosition = new Vector(position.x + padding/2,centerPosition.y + m.getDescent() + m.getLeading() + padding/2);
//		}
		
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
			if (dims == null)
				return;
			if (e.isMouseInput()) {
				if (e.getType() != InputEvent.MOUSE_PRESSED)
					return;
			} else return;
			Vector v = e.getVector();
			if (v.x > position.x && v.x < (position.x+dims.width) && v.y > position.y && v.y < (position.y + dims.height))
				notifyListeners();
		}
			
		private void notifyListeners() {
			for (OptionItemListener l : listeners) {
				l.itemActivated();
			}
		}
		
		private Font getFont() {
			return new Font(Font.SANS_SERIF,Font.PLAIN,textSize);
		}
		
		private class MyFontMetrics extends FontMetrics {

			public MyFontMetrics(Font f) {
				super(f);
			}
			
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
	}
	
	private interface OptionItemListener {
		public void itemActivated();
	}

}
