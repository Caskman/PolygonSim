package caskman.polygonsim;

import java.awt.Graphics;
import java.awt.image.BufferStrategy;

import caskman.polygonsim.model.GameModel;



public class MainThread extends Thread {

	private boolean running;
	private int FPS = 25;
	private int TICKS_PER_FRAME = 1000 / FPS;
	private int MAX_FRAMESKIP = 5;
	BufferStrategy bs;
	GameModel model;
 
	public MainThread(BufferStrategy bs,GameModel model) {
		this.setName("Game Thread");
		this.bs = bs;
		this.model = model;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public void run() {
		long ticks;
		long nextFrameTicks = System.currentTimeMillis();
		int framesSkipped;
		float interpol;
		Graphics g = null;
		
		while (running) {
				
			
			
			// try locking the canvas for exclusive pixel editing on the surface
			
			framesSkipped = 0;
			while ((System.currentTimeMillis() > nextFrameTicks) && (framesSkipped < MAX_FRAMESKIP)) {
				model.update();
				
				nextFrameTicks += TICKS_PER_FRAME;
				
				framesSkipped++;
			}
			
			long systemMil = System.currentTimeMillis();
			long num = systemMil + TICKS_PER_FRAME - nextFrameTicks;
			interpol= ((float)(num))/((float)(TICKS_PER_FRAME));
//			interpol = ((float)(System.currentTimeMillis() + TICKS_PER_FRAME - nextFrameTicks))/((float)(TICKS_PER_FRAME));
			
			
			try {
				g = bs.getDrawGraphics();
				model.draw(g,interpol);
				if (!bs.contentsLost())
					bs.show();
//				g.dispose();
			} finally {
				if (g != null)
					g.dispose();
			}
		}
		
	}
	
}
