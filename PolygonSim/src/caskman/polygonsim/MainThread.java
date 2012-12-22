package caskman.polygonsim;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferStrategy;

import caskman.polygonsim.screens.ScreenManager;



public class MainThread extends Thread {

	private boolean running;
	public static int FPS = 25;
	private int TICKS_PER_FRAME = 1000 / FPS;
	private int MAX_FRAMESKIP = 5;
	private int TRAILING_AVG_LIMIT = 50;
	BufferStrategy bs;
	ScreenManager manager;
	private long renderTime;
	private long tempRenderTime;
	private long updateTime;
	private int[] renderTimes;
	private int[] updateTimes;
	private int renderTimeCount;
	private int updateTimeCount;
	
 
	public MainThread(BufferStrategy bs,ScreenManager manager) {
		this.setName("Game Thread");
		this.bs = bs;
		this.manager = manager;
		renderTimes = new int[TRAILING_AVG_LIMIT];
		updateTimes = new int[TRAILING_AVG_LIMIT];
		renderTimeCount = 0;
		updateTimeCount = 0;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public void run() {
		long nextFrameTicks = System.currentTimeMillis();
		int framesSkipped;
		float interpol;
		
		while (running) {
			framesSkipped = 0;
//			Profiler.start();
			while ((System.currentTimeMillis() > nextFrameTicks) && (framesSkipped < MAX_FRAMESKIP)) {
				updateTime = System.nanoTime();
				update();
				updateTime = System.nanoTime() - updateTime;
				addUpdateEntry(updateTime);
				
				nextFrameTicks += TICKS_PER_FRAME;
				
				framesSkipped++;
			}
//			Profiler.lapRestart("Update");
			
			long systemMil = System.currentTimeMillis();
			long num = systemMil + TICKS_PER_FRAME - nextFrameTicks;
			interpol= ((float)(num))/((float)(TICKS_PER_FRAME));
			
			tempRenderTime = System.nanoTime();
			render(interpol);
			renderTime = System.nanoTime() - tempRenderTime;
			addRenderEntry(renderTime);
//			Profiler.lap("Draw");
		}
	}
	
	private void addRenderEntry(long entry) {
		renderTimes[renderTimeCount++] = (int)entry;
		renderTimeCount = renderTimeCount%TRAILING_AVG_LIMIT;
	}
	
	private void addUpdateEntry(long entry) {
		updateTimes[updateTimeCount++] = (int)entry;
		updateTimeCount = updateTimeCount%TRAILING_AVG_LIMIT;
	}
	
	private int getRenderTime() {
		int sum = 0;
		for (int i : renderTimes) {
			sum += i;
		}
		
		long avg = sum/renderTimes.length;
		return (int)(avg/1000000L);
	}
	
	private int getUpdateTime() {
		int sum = 0;
		for (int i : updateTimes) {
			sum += i;
		}
		
		long avg = sum/updateTimes.length;
		return (int)(avg/1000000L);
	}
	
	private void update() {
		manager.updateScreens();
	}
	
	private void render(float interpol) {
		Graphics2D g = null;
		try {
			g = (Graphics2D)bs.getDrawGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
//			Profiler.lapRestart("Obtain Graphics");
			draw(g,interpol);
//			Profiler.lapRestart("Draw Content");
			if (!bs.contentsLost())
				bs.show();
//			Profiler.lapRestart("Show Content");
//			g.dispose();
		} finally {
			if (g != null)
				g.dispose();
//			Profiler.lapRestart("Dispose Graphics");
		}
	}
	
	private void draw(Graphics2D g,float interpol) {
		manager.drawScreens(g,interpol);
		drawFPSBreakdown(g,interpol);
//		g.drawString(getFPSBreakdown(), 0, 10);
	}
	
	private void drawFPSBreakdown(Graphics2D g,float interpol) {
//		return "Render: "+((int)((double)renderTime/(double)TICKS_PER_FRAME*100.0))+"% Update: "+((int)((double)updateTime/(double)TICKS_PER_FRAME*100.0F))+"%";
//		return "Render: "+(renderTime)+"ms\n Update: "+(updateTime)+"ms\n "+TICKS_PER_FRAME+"ms per frame";
		g.setColor(Color.WHITE);
		g.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,10));
		g.drawString("Render: "+(getRenderTime())+"ms", 0, 20);
		g.drawString("Update: "+(getUpdateTime())+"ms", 0, 40);
		g.drawString(TICKS_PER_FRAME+"ms per frame", 0, 60);
	}
	
	
}
