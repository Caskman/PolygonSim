package caskman.polygonsim.screens;

import java.awt.Color;
import java.awt.Graphics2D;


public class LoadingScreen extends GameScreen {

	GameScreen[] screens;
	boolean isSlowLoad;
	ScreenManager manager;
	
	public static void load(ScreenManager manager,GameScreen[] screensToLoad,boolean isSlowLoad) {
		manager.exitAllScreens();
		
		LoadingScreen loading = new LoadingScreen(manager,isSlowLoad,screensToLoad);
		manager.addScreen(loading);
	}
	
	private LoadingScreen(ScreenManager manager,boolean isSlowLoad,GameScreen[] screensToLoad) {
		super(manager,true);
		screens = screensToLoad;
		this.isSlowLoad = isSlowLoad;
		this.manager = manager;
	}

	@Override
	public void update() {
		if (isSlowLoad) {
			//TODO
		} else {
			manager.removeScreen(this);
			for (GameScreen g : screens) {
				manager.addScreen(g);
			}
		}
	}

	@Override
	public void draw(Graphics2D g, float interpol) {
		if (isSlowLoad) {
			//TODO
		} else {
//			canvas.drawColor(Color.BLACK);
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, manager.getScreenDims().width, manager.getScreenDims().height);
		}
	}

	@Override
	public void manageInput(InputEvent e) {
		
	}


}
