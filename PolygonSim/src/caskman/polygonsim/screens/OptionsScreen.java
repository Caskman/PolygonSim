package caskman.polygonsim.screens;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import caskman.polygonsim.Launcher;
import caskman.polygonsim.Parameters;
import caskman.polygonsim.Profiler;
import caskman.polygonsim.model.GameModel;
import caskman.polygonsim.model.Vector;
import caskman.polygonsim.screens.ButtonItem.ButtonItemListener;
import caskman.polygonsim.screens.SelectionScreen.SelectionAction;

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
		ButtonItem i;
		StackPanel sp = new StackPanel();
		Dimension stackDims = new Dimension(manager.getScreenDims().width/8,3*manager.getScreenDims().height/8);
		sp.setDims(stackDims);
		sp.setPosition(new Vector((manager.getScreenDims().width - stackDims.width)/2,3*manager.getScreenDims().height/8));
		sp.setOrientation(StackPanel.VERTICAL);
		optionItems.add(sp);
		
		
		LabelItem l = new LabelItem();
		l.setText("Options");
		l.setTextSize(40);
		l.setDims(new Dimension(200,50));
		l.setPosition(new Vector((manager.getScreenDims().width - l.getDims().width)/2,manager.getScreenDims().height/4));
		optionItems.add(l);
		
		i = new ButtonItem();
		i.setText("Change Window Mode");
		i.setTextSize(20);
		i.setDims(new Dimension(275,30));
		sp.add(i);
		
		i = new ButtonItem();
		i.setTextSize(20);
		i.setText("Change Dot Color");
		i.addOptionItemListener(new ButtonItemListener() {
			@Override
			public void itemActivated() {
				manager.addScreen(new SelectionScreen(manager,false,Parameters.getDotColorChoices(),new SelectionAction() {
					@Override
					public void selectionMade(int selection) {
						try {
							Parameters.DOT_COLOR = Parameters.string2Color(Parameters.getDotColorChoices()[selection]);
							Parameters.save();
						} catch (Exception e) {
							
						}
					}
				}));
			}
		});
		i.setDims(new Dimension(275,30));
		sp.add(i);
		
		i = new ButtonItem();
		i.setTextSize(20);
		i.setText("Change Line Color");
		i.addOptionItemListener(new ButtonItemListener() {
			@Override
			public void itemActivated() {
				manager.addScreen(new SelectionScreen(manager,false,Parameters.getLineColorChoices(),new SelectionAction() {
					@Override
					public void selectionMade(int selection) {
						try {
							Parameters.LINE_COLOR = Parameters.string2Color(Parameters.getLineColorChoices()[selection]);
							Parameters.save();
						} catch (Exception e) {
							
						}
					}
				}));
			}
		});
		i.setDims(new Dimension(275,30));
		sp.add(i);
		
		i = new ButtonItem();
		i.setText("Change Background Color");
		i.setTextSize(20);
		i.addOptionItemListener(new ButtonItemListener() {
			@Override
			public void itemActivated() {
				manager.addScreen(new SelectionScreen(manager,false,Parameters.getBackgroundColorChoices(),new SelectionAction() {
					@Override
					public void selectionMade(int selection) {
						try {
							Parameters.LINE_COLOR = Parameters.string2Color(Parameters.getBackgroundColorChoices()[selection]);
							Parameters.save();
						} catch (Exception e) {
							
						}
					}
				}));
			}
		});
		i.setDims(new Dimension(275,30));
		sp.add(i);
		
		i = new ButtonItem();
		i.setText("Exit");
		i.setDims(new Dimension(100,20));
		i.setTextSize(20);
		i.addOptionItemListener(new ButtonItemListener() {
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
		//Profiler.start();
//		Composite old = g.getComposite();
//		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5F));
//		g.setColor(Color.BLACK);
//		g.fillRect(0, 0, manager.getScreenDims().width, manager.getScreenDims().height);
//		g.setComposite(old);
		//Profiler.lapRestart("Fade Background");
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, manager.getScreenDims().width, manager.getScreenDims().height);
		
//		g.setColor(Color.WHITE);
//		g.drawRect(0, 0, manager.getScreenDims().width-1, manager.getScreenDims().height-1);
		
		Rectangle r = new Rectangle(manager.getScreenDims().width/8, manager.getScreenDims().height/8, 3*manager.getScreenDims().width/4, 3*manager.getScreenDims().height/4);
		g.setColor(Color.BLACK);
		g.fillRect(r.x,r.y,r.width,r.height);
		//Profiler.lapRestart("Draw Dark Background");
//		g.setColor(Color.WHITE);
//		g.drawRect(r.x,r.y,r.width,r.height);
		//Profiler.lapRestart("Draw Border");
		
		for (Item i : optionItems) {
			i.draw(g,interpol);
		}
		//Profiler.lapRestart("Draw Items");
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


	@Override
	public boolean isFullscreen() {
		return true;
	}
	
	

}
