package caskman.polygonsim.screens;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.css.Rect;

import caskman.polygonsim.model.Vector;


public class MainMenuScreen extends GameScreen {
	
	private List<MenuItem> menuItems;
	
	public MainMenuScreen(ScreenManager manager) {
		super(manager,false);
		initialize();
	}
	
	private void initialize() {
		menuItems = new ArrayList<MenuItem>();
		MenuItem m;
		
		m = new MenuItem();
		m.text = "Start";
		m.setTextSize(20);
		m.isButton = true;
//		m.dims = new Dimension(100,50);
		m.dims = null;
		m.position = null;
		m.centerPosition = new Vector(manager.getScreenDims().width/2,2*(manager.getScreenDims().height)/3);
		m.addMenuItemListener(new MenuItemListener() {
			public void itemActivated() {
				GameScreen[] screens = {new MainGameScreen(manager,true)};
				LoadingScreen.load(manager,screens,false);
			}
		});
		menuItems.add(m);
		
		m = new MenuItem();
		m.text = "PolygonSim";
		m.setTextColor(Color.GREEN);
		m.isButton = false;
		m.setTextSize(50);
//		Rect r = m.getTextBounds();
//		m.dims = new Dimension(r.width(),r.height());
		m.dims = null;
		m.position = null;
		m.centerPosition = new Vector(manager.getScreenDims().width/2,(manager.getScreenDims().height)/4);
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
		for (MenuItem m : menuItems) {
			m.draw(g,interpol);
		}
	}

	@Override
	public void manageInput(InputEvent e) {
		if (e.isKeyInput()) {
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
				System.exit(0);
		}
		for (MenuItem m : menuItems) {
			m.manageInput(e);
		}
	}

	
}
