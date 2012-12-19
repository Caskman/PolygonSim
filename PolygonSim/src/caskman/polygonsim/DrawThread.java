package caskman.polygonsim;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.util.List;



public class DrawThread extends Thread {

	private boolean running;
	private int FPS = ThreadContext.FPS;
	private int TICKS_PER_FRAME = 1000 / FPS;
	private int MAX_FRAMESKIP = 5;
	BufferStrategy bs;
	private long renderTime;
	private long tempRenderTime;
//	private long updateTime;
	private ThreadContext context;
 
	public DrawThread(ThreadContext c,BufferStrategy bs) {
		this.setName("Draw Thread");
		this.bs = bs;
		context = c;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public void run() {
		long nextFrameTicks = System.currentTimeMillis();
		int framesSkipped;
		float interpol;
		
		while (running) {
				
			
			
			
//			framesSkipped = 0;
//			while ((System.currentTimeMillis() > nextFrameTicks) && (framesSkipped < MAX_FRAMESKIP)) {
////				updateTime = System.currentTimeMillis();
////				update();
////				updateTime = System.currentTimeMillis() - updateTime;
//				
//				nextFrameTicks += TICKS_PER_FRAME;
//				
//				framesSkipped++;
//			}
			
			RenderGroup g = context.renderGroup;
			if (g == null)
				continue;
			
			long systemMil = System.currentTimeMillis();
			long num = systemMil - g.getCreationTime();
			interpol = ((float)(num))/((float)(TICKS_PER_FRAME));
			
			tempRenderTime = System.currentTimeMillis();
			render(interpol,g.getRenderObjectList());
			renderTime = System.currentTimeMillis() - tempRenderTime;
		}
		
	}
	
	
	private void render(float interpol,List<RenderObject> renderList) {
		Graphics2D g = null;
		try {
			g = (Graphics2D)bs.getDrawGraphics();
			draw(g,interpol,renderList);
			if (!bs.contentsLost())
				bs.show();
//			g.dispose();
		} finally {
			if (g != null)
				g.dispose();
		}
	}
	
	private void draw(Graphics2D g,float interpol,List<RenderObject> renderList) {
		for (RenderObject r : renderList) {
			r.render(g,interpol);
		}
//		manager.drawScreens(g,interpol);
		drawFPSBreakdown(g,interpol);
//		g.drawString(getFPSBreakdown(), 0, 10);
	}
	
	private void drawFPSBreakdown(Graphics2D g,float interpol) {
//		return "Render: "+((int)((double)renderTime/(double)TICKS_PER_FRAME*100.0))+"% Update: "+((int)((double)updateTime/(double)TICKS_PER_FRAME*100.0F))+"%";
//		return "Render: "+(renderTime)+"ms\n Update: "+(updateTime)+"ms\n "+TICKS_PER_FRAME+"ms per frame";
		g.setColor(Color.WHITE);
		g.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,10));
		g.drawString("Render: "+(renderTime)+"ms", 0, 40);
//		g.drawString("Update: "+(updateTime)+"ms", 0, 40);
//		g.drawString(TICKS_PER_FRAME+"ms per frame", 0, 60);
	}
	
	
}
