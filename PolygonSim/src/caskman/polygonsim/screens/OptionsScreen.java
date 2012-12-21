package caskman.polygonsim.screens;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.List;

import caskman.polygonsim.model.GameModel;
import caskman.polygonsim.model.Vector;
import caskman.polygonsim.Launcher;

public class OptionsScreen extends GameScreen {
	
	private GameModel model;
	private OptionItem[] optionItems;

	public OptionsScreen(ScreenManager manager, boolean isFullscreen,GameModel gameModel) {
		super(manager, isFullscreen);
		model = gameModel;
		
		initialize();
	}
	
	private void initialize() {
		optionItems = new OptionItem[2];
		OptionItem i;
		
		i = new OptionItem();
		i.setText("Options");
		i.setTextSize(40);
		i.setCenterPosition(new Vector(manager.getScreenDims().width/2,manager.getScreenDims().height/4));
		optionItems[0] = i;
		
		i = new OptionItem();
		i.setText("Exit");
		i.setTextSize(20);
		i.setCenterPosition(new Vector(manager.getScreenDims().width/2,3*manager.getScreenDims().height/4));
		i.addOptionItemListener(new OptionItemListener() {
			@Override
			public void itemActivated() {
				Launcher.exit();
			}
		});
		optionItems[1] = i;
		
		
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
		
		for (OptionItem i : optionItems) {
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
		
		for (OptionItem i : optionItems) {
			i.manageInput(e);
		}
	}
	
	private class OptionItem {
		
		private String text;
		private int textSize;
		private Vector centerPosition;
		private List<OptionItemListener> listeners;
		private Dimension dims;
		Vector position;
		Vector textPosition;
		
		public OptionItem() {
			listeners = new LinkedList<OptionItemListener>();
		}
		
		public void setText(String s) {
			text = s;
		}
		
		public void setTextSize(int size) {
			textSize = size;
		}
		
		public void setCenterPosition(Vector v) {
			centerPosition = v;
		}
		
		public void addOptionItemListener(OptionItemListener l) {
			listeners.add(l);
		}
		
		private void updateTextDimsAndPosition(Graphics2D g) {
			FontMetrics m = g.getFontMetrics();
			int padding = 4;
			dims = new Dimension(m.stringWidth(text) + padding,m.getAscent() + padding);
			position = new Vector(centerPosition.x - dims.width/2,centerPosition.y - dims.height/2);
			textPosition = new Vector(position.x + padding/2,centerPosition.y + m.getDescent() + m.getLeading() + padding/2);
		}
		
		public void draw(Graphics2D g,float interpol) {
			g.setColor(Color.WHITE);
			g.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,textSize));
			updateTextDimsAndPosition(g);
			
//			g.drawRect((int)position.x, (int)position.y, dims.width, dims.height);
			g.drawString(text,textPosition.x,textPosition.y);
		}
		
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
		
		
	}
	
	private interface OptionItemListener {
		public void itemActivated();
	}

}
