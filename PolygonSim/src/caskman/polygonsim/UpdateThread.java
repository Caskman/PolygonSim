package caskman.polygonsim;

import java.util.LinkedList;
import java.util.List;

import caskman.polygonsim.screens.ScreenManager;



public class UpdateThread extends Thread {

	private boolean running;
	public static int FPS = 25;
	private int TICKS_PER_FRAME = 1000 / FPS;
	private int MAX_FRAMESKIP = 5;
//	BufferStrategy bs;
	ScreenManager manager;
//	private long renderTime;
//	private long tempRenderTime;
	private long updateTime;
	private ThreadContext context;
 
	public UpdateThread(ThreadContext c,ScreenManager manager) {
		this.setName("Game Thread");
		this.manager = manager;
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
				
			
			
			
			framesSkipped = 0;
			while ((System.currentTimeMillis() > nextFrameTicks) && (framesSkipped < MAX_FRAMESKIP)) {
				updateTime = System.currentTimeMillis();
				update();
				updateTime = System.currentTimeMillis() - updateTime;
				
				nextFrameTicks += TICKS_PER_FRAME;
				
				framesSkipped++;
			}
			
//			long systemMil = System.currentTimeMillis();
//			long num = systemMil + TICKS_PER_FRAME - nextFrameTicks;
//			interpol= ((float)(num))/((float)(TICKS_PER_FRAME));
//			
//			tempRenderTime = System.currentTimeMillis();
//			render(interpol);
//			renderTime = System.currentTimeMillis() - tempRenderTime;
		}
		
	}
	
	private void update() {
		manager.updateScreens();
		List<RenderObject> renderList = new LinkedList<RenderObject>();
		manager.getRenderObjects(renderList);
		context.renderGroup = new RenderGroup(System.currentTimeMillis(),renderList);
	}
	
//	private void render(float interpol) {
//		Graphics2D g = null;
//		try {
//			g = (Graphics2D)bs.getDrawGraphics();
//			draw(g,interpol);
//			if (!bs.contentsLost())
//				bs.show();
////			g.dispose();
//		} finally {
//			if (g != null)
//				g.dispose();
//		}
//	}
	
//	private void draw(Graphics2D g,float interpol) {
//		manager.drawScreens(g,interpol);
//		drawFPSBreakdown(g,interpol);
////		g.drawString(getFPSBreakdown(), 0, 10);
//	}
	
//	private void drawFPSBreakdown(Graphics2D g,float interpol) {
////		return "Render: "+((int)((double)renderTime/(double)TICKS_PER_FRAME*100.0))+"% Update: "+((int)((double)updateTime/(double)TICKS_PER_FRAME*100.0F))+"%";
////		return "Render: "+(renderTime)+"ms\n Update: "+(updateTime)+"ms\n "+TICKS_PER_FRAME+"ms per frame";
//		g.setColor(Color.WHITE);
//		g.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,10));
//		g.drawString("Render: "+(renderTime)+"ms", 0, 20);
//		g.drawString("Update: "+(updateTime)+"ms", 0, 40);
//		g.drawString(TICKS_PER_FRAME+"ms per frame", 0, 60);
//	}
	
	
}
