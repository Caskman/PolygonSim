package caskman.polygonsim.screens;

import java.awt.Graphics2D;



public abstract class GameScreen {
	
	public ScreenState state;
	public ScreenManager manager;
	
	public GameScreen(ScreenManager manager,boolean isFullscreen) {
		this.manager = manager;
		this.state = ScreenState.VISIBLE;
	}
	
	public abstract boolean isFullscreen();
	
	public abstract void updateScreen();
	
	public abstract void drawScreen(Graphics2D g,float interpol);
	
	public abstract void manageInput(InputEvent e);
	
	public void exitScreen() {
		manager.removeScreen(this);
	}
}
