package caskman.polygonsim.model.entities;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import caskman.polygonsim.model.GameContext;
import caskman.polygonsim.model.GameModel;
import caskman.polygonsim.model.Mob;
import caskman.polygonsim.model.Vector;

public class Explosion extends Mob {
	  
	private final static int NUM_PARTICLES = 30;
	private final static int MAX_SPEED = 30;
	private final static float DECAY_FACTOR = 1.08F;
	private final static float PERCENT_ALPHA = 0.55F; // percentage of time explosion is fully visible
	private final static int MAX_DURATION = 25;
	private final static int ALPHA_DELTA = ((int)((0xff/MAX_DURATION)/PERCENT_ALPHA)) << 24;
	private final static int VISIBLE_DURATION = (int) (MAX_DURATION*PERCENT_ALPHA);
	
	private List<Particle> particles;
	private int duration;

	public Explosion(GameModel model,float x, float y) {
		super(model);
		
		position = new Vector(x,y);
		particles = new ArrayList<Particle>();
		Particle p;
		Random r = model.getRandom();
		Vector vel;
		
		for (int i = 0; i < NUM_PARTICLES; i++) {
			vel = new Vector(r.nextFloat()-0.5F,r.nextFloat()-0.5F);
			p = new Particle(model,position.x,position.y,r.nextFloat(),vel,Color.WHITE);
			particles.add(p);
		}
		
		duration = 0;
	}

	@Override
	public void draw(Graphics g, float interpol) {
		for (Particle p : particles) {
			p.draw(g,interpol);
		}
	}

	@Override
	public void update(GameContext g) {
		duration++;
		if (duration >= MAX_DURATION) {
			g.removals.add(this);
			return;
		}
		
		for (Particle p : particles) {
			p.update(g);
		}
	}
	
	private class Particle extends Mob {
		
		Dimension dims = new Dimension(2,2);
		Color color;
		
		public Particle(GameModel model,float xPos,float yPos,float speed,Vector direction,Color color) {
			super(model);
			
			position = new Vector(xPos,yPos);
			float actualSpeed = MAX_SPEED*speed;
			velocity = new Vector(direction.x*actualSpeed,direction.y*actualSpeed);
			this.color = color;
			
		}

		@Override
		public void draw(Graphics g, float interpol) {
//			int radius = dims.width>>1; // divided by 2
//			canvas.drawCircle((position.x + velocity.x*interpol) - radius, (position.y + velocity.y * interpol) - radius, radius, paint);
//			g.drawCircle((position.x + velocity.x*interpol), (position.y + velocity.y * interpol), dims.width>>1);
			g.setColor(color);
			g.fillArc((int)(position.x + velocity.x*interpol), (int)(position.y + velocity.y*interpol), dims.width, dims.height, 0, 360);
		}

		@Override
		public void update(GameContext g) {
			position.x += velocity.x;
			position.y += velocity.y;
			velocity.x = velocity.x/(DECAY_FACTOR);
			velocity.y = velocity.y/(DECAY_FACTOR);
//			int half = SPLATTER_SIZE>>1;
//			g.backdrop.drawCircle(position.x+half, position.y+half, SPLATTER_SIZE, parentPaint);
//			if (duration > VISIBLE_DURATION) 
//				paint.setColor(paint.getColor()-((int)ALPHA_DELTA));
		}
		
	}
	

}
