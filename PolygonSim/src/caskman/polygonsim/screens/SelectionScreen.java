package caskman.polygonsim.screens;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import caskman.polygonsim.model.Vector;
import caskman.polygonsim.screens.OptionItem.OptionItemListener;

public class SelectionScreen extends GameScreen {
	
	private SelectionAction sa;
	private StackPanel panel;
	private String[] choices;
	
	public SelectionScreen(ScreenManager manager, boolean isFullscreen,String[] choices,SelectionAction sa) {
		super(manager, isFullscreen);
		this.sa = sa;
		this.choices = choices;
		
		initialize(choices);
	}
	
	private void initialize(String[] choices) {
		panel = new StackPanel();
		Dimension stackDims = new Dimension(7*manager.getScreenDims().width/8,3*manager.getScreenDims().height/8);
		panel.setDims(stackDims);
		panel.setPosition(new Vector((manager.getScreenDims().width - stackDims.width)/2,(manager.getScreenDims().height - stackDims.height)/2));
		panel.setOrientation(StackPanel.HORIZONTAL);
		
		for (int k = 0; k < choices.length; k++) {
			OptionItem i = new OptionItem();
			i.setText(choices[k]);
			i.setDims(new Dimension(200,50));
			i.addOptionItemListener(new SelectionListener(k));
			panel.add(i);
		}
	}
	
	private void selectionMade(int i) {
		sa.selectionMade(i);
		exitScreen();
	}

	@Override
	public void updateScreen() {
		
	}

	@Override
	public void drawScreen(Graphics2D g, float interpol) {
		g.setColor(Color.BLACK);
		g.fillRect((int)panel.getPosition().x, (int)panel.getPosition().y, panel.getDims().width, panel.getDims().height);
		g.setColor(Color.WHITE);
		g.drawRect((int)panel.getPosition().x, (int)panel.getPosition().y, panel.getDims().width, panel.getDims().height);
		panel.draw(g,interpol);
	}

	@Override
	public void manageInput(InputEvent e) {
		if (e.isKeyInput()) {
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
				exitScreen();
		} else
			panel.manageInput(e);
	}

	private class SelectionListener implements OptionItemListener {
		
		int selectionNumber;
		
		SelectionListener(int k) {
			selectionNumber = k;
		}
		
		@Override
		public void itemActivated() {
			selectionMade(selectionNumber);
		}
	}
	
	public interface SelectionAction {
		public void selectionMade(int i);
	}
	
}
