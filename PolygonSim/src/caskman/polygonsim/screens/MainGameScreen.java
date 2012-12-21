package caskman.polygonsim.screens;

import java.awt.Graphics2D;

import caskman.polygonsim.model.GameModel;

public class MainGameScreen extends GameScreen {

	GameModel model;
	
	public MainGameScreen(ScreenManager manager, boolean isFullscreen) {
		super(manager, isFullscreen);
		model = new GameModel(manager,manager.getScreenDims());
	}

	@Override
	public void updateScreen() {
		model.update();
	}

	@Override
	public void drawScreen(Graphics2D g, float interpol) {
		model.draw(g, interpol);
	}

	@Override
	public void manageInput(InputEvent e) {
		model.manageInput(e);
	}


	@Override
	public boolean isFullscreen() {
		return true;
	}

}


