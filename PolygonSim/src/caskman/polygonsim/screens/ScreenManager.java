package caskman.polygonsim.screens;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import caskman.polygonsim.model.Vector;



public class ScreenManager {
	
//	private Context context;
	private Dimension screenDims;
	private List<GameScreen> screens;
	private BlockingQueue<InputEvent> inputQueue;
	
	public ScreenManager(Dimension screenDims,InputListener il) {
//		this.context = context;
		this.screenDims = screenDims;
		inputQueue = new ArrayBlockingQueue<InputEvent>(10);
		
		screens = new ArrayList<GameScreen>();
		
		MyInputAdapter m = new MyInputAdapter();
		
		il.addMouseListener(m);
		il.addMouseMotionListener(m);
		il.addKeyListener(m);
	}
	
//	public Context getContext() {
//		return context;
//	}
	
	public Dimension getScreenDims() {
		return screenDims;
	}
	
	
	public void updateScreens() {
//		GameScreen g = screens.get(screens.size()-1);
		List<GameScreen> screensToInput =  new ArrayList<GameScreen>();
		for (int i = screens.size() - 1; i >= 0; i--) {
			if (screens.get(i).state != ScreenState.HIDDEN) {
				screensToInput.add(screens.get(i));
			}
		}
		
		GameScreen s1 = screens.get(screens.size()-1);
		while (!inputQueue.isEmpty()) {
			InputEvent e = inputQueue.poll();
//			for (GameScreen s : screensToInput) {
				s1.manageInput(e);
//			}
//			g.manageInput(e);
		}
		
		for (GameScreen s : screens) {
			s.updateScreen();
		}
	}
	
	public void drawScreens(Graphics2D g, float interpol) {
		for (GameScreen s : screens) {
			if (s.state != ScreenState.HIDDEN)
				s.drawScreen(g,interpol);
		}
	}
	
	public void manageInput(InputEvent e) {
		inputQueue.offer(e);
//		screens.get(screens.size()-1).manageInput(e);
	}
	
	public void addScreen(GameScreen g) {
		if (!screens.isEmpty()) {
			GameScreen s = screens.get(screens.size()-1);
			if (g.isFullscreen())
				s.state = ScreenState.HIDDEN;
			else 
				s.state = ScreenState.PARTIALLYCOVERED;
		}
		screens.add(g);
	}
	
	public void removeScreen(GameScreen g) {
		screens.remove(g);
		if (!screens.isEmpty())
			screens.get(screens.size()-1).state = ScreenState.VISIBLE;
	}
	
	public void exitAllScreens() {
		for (int i = 0; i < screens.size(); i++) {
			screens.set(i, null);
		}
		screens = new ArrayList<GameScreen>();
	}
	
	class MyInputAdapter extends MouseAdapter implements KeyListener {
		@Override
		public void mouseClicked(MouseEvent e) {
			inputQueue.offer(new InputEvent(new Vector(e.getPoint()),InputEvent.MOUSE_CLICKED,e.getButton()));
		}
		@Override
		public void mouseEntered(MouseEvent e) {
			inputQueue.offer(new InputEvent(new Vector(e.getPoint()),InputEvent.MOUSE_ENTERED,e.getButton()));
		}
		@Override
		public void mouseExited(MouseEvent e) {
			inputQueue.offer(new InputEvent(new Vector(e.getPoint()),InputEvent.MOUSE_EXITED,e.getButton()));
		}
		@Override
		public void mousePressed(MouseEvent e) {
			inputQueue.offer(new InputEvent(new Vector(e.getPoint()),InputEvent.MOUSE_PRESSED,e.getButton()));
			
		}
		@Override
		public void mouseReleased(MouseEvent e) {
			inputQueue.offer(new InputEvent(new Vector(e.getPoint()),InputEvent.MOUSE_RELEASED,e.getButton()));
		}
		@Override
		public void mouseDragged(MouseEvent e) {
			inputQueue.offer(new InputEvent(new Vector(e.getPoint()),InputEvent.MOUSE_DRAGGED,e.getButton()));
		}
		@Override
		public void mouseMoved(MouseEvent e) {
			inputQueue.offer(new InputEvent(new Vector(e.getPoint()),InputEvent.MOUSE_MOVED,e.getButton()));
		}
		@Override
		public void keyPressed(KeyEvent e) {
			inputQueue.offer(new InputEvent(InputEvent.KEY_PRESSED,e.getKeyCode()));
		}
		@Override
		public void keyReleased(KeyEvent e) {
			inputQueue.offer(new InputEvent(InputEvent.KEY_RELEASED,e.getKeyCode()));
		}
		@Override
		public void keyTyped(KeyEvent e) {
			inputQueue.offer(new InputEvent(InputEvent.KEY_TYPED,e.getKeyCode()));
		}
	}
	
}
