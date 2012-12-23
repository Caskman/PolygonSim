package caskman.polygonsim.screens;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import caskman.polygonsim.Launcher;
import caskman.polygonsim.model.Vector;
import caskman.polygonsim.screens.ButtonItem.ButtonItemListener;


public class MainMenuScreen extends GameScreen {
	
	private List<Item> menuItems;
	
	public MainMenuScreen(ScreenManager manager) {
		super(manager,false);
		initialize();
	}
	
	private void initialize() {
		menuItems = new ArrayList<Item>();
		Item m;
		
		m = new ButtonItem();
		((ButtonItem)m).setText("Start");
		((ButtonItem)m).setTextSize(20);
		((ButtonItem)m).setDims(new Dimension(100,30));
		((ButtonItem)m).setPosition(new Vector((manager.getScreenDims().width - ((ButtonItem)m).getDims().width)/2,3*manager.getScreenDims().height/4));
		((ButtonItem)m).addButtonItemListener(new ButtonItemListener() {
			public void itemActivated() {
				GameScreen[] screens = {new MainGameScreen(manager,true)};
				LoadingScreen.load(manager,screens,false);
			}
		});
		menuItems.add(m);
		
		m = new LabelItem();
		((LabelItem)m).setText("PolygonSim");
		((LabelItem)m).setTextSize(50);
//		Rect r = m.getTextBounds();
//		m.dims = new Dimension(r.width(),r.height());
		((LabelItem)m).setDims(new Dimension(200,50));
		((LabelItem)m).setPosition(new Vector((manager.getScreenDims().width - ((LabelItem)m).getDims().width)/2,manager.getScreenDims().height/4));
		menuItems.add(m);
		
	}
	
	@Override
	public void updateScreen() {
		
	}

	@Override
	public void drawScreen(Graphics2D g, float interpol) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, manager.getScreenDims().width, manager.getScreenDims().height);
//		Vector zero = new Vector();
		for (Item m : menuItems) {
			m.draw(g,interpol);
		}
	}

	@Override
	public void manageInput(InputEvent e) {
		if (e.isKeyInput()) {
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
				Launcher.exit();
		}
		for (Item m : menuItems) {
			m.manageInput(e);
		}
	}


	@Override
	public boolean isFullscreen() {
		return true;
	}

	
}
