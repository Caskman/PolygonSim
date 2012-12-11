package caskman.polygonsim.model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import caskman.polygonsim.InputListener;
import caskman.polygonsim.model.entities.Dot;
import caskman.polygonsim.model.entities.Explosion;
import caskman.polygonsim.model.entities.Mob;


public class GameModel {
	
	private Dimension screenDims;
	private Random r;
	private float dotRatio = .00005F;
	private float dotMaxVel = 20;
	private List<Mob> dots;
	private List<Mob> explosions;
	private QuadTree q;
	private Vector mousePosition;
	
	
	public GameModel(Dimension screenDims,InputListener il) {
		this.screenDims = screenDims;
		
		initialize(il);
	}
	
	public Dimension getScreenDims() {
		return screenDims;
	}
	
	public Random getRandom() {
		return r;
	}
	
	private void initialize(InputListener il) {
		r = new Random();
		int numBlocks = (int) (screenDims.width*screenDims.height*dotRatio);
		dots = new ArrayList<Mob>(numBlocks);
		
		for (int i = 0; i < numBlocks; i++) {
			float xPos = r.nextFloat()*screenDims.width;
			float yPos = r.nextFloat()*screenDims.height;
			float xVel = dotMaxVel*r.nextFloat() - dotMaxVel/2.0F;
			float yVel = dotMaxVel*r.nextFloat() - dotMaxVel/2.0F;
			dots.add(new Dot(this,xPos,yPos,xVel,yVel));
		}
		
		mousePosition = new Vector();
		
		il.addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(MouseEvent arg0) {
			}
			public void mouseMoved(MouseEvent e) {
				mousePosition = new Vector(e.getX(),e.getY());
			}
		});
		
		explosions = new ArrayList<Mob>();
		q = new QuadTree(screenDims,5);
	}
	
	public void update() {
		
		updateQuadTree();
		
		GameContext g = getGameContext();
		
		// reset all resolved flags
		for (Mob m : dots) {
			((Collidable)m).setResolved(false);
		}
		
		for (Mob m : dots) {
			m.updateMob(g);
		}
		for (Mob m : explosions) {
			m.updateMob(g);
		}
		
		for (Mob m : g.removals) {
			explosions.remove(m);
		}
		
		for (Mob m : g.additions) {
			if (m instanceof Explosion)
				explosions.add(m);
		}
		
		List<Collidable> toChange = q.retrieve(new ArrayList<Collidable>(), new Collidable() {
			public Rectangle getAABB() {
				return new Rectangle(mousePosition.x,mousePosition.y,1,1);
			}

			@Override
			public void setCollisionPosition(float percent) {
			}

			@Override
			public Vector getVelocity() {
				return null;
			}

			@Override
			public int getLargestDim() {
				return 0;
			}

			@Override
			public Rectangle getCollisionAABB() {
				return null;
			}

			@Override
			public void setResolved(boolean b) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public boolean isResolved() {
				// TODO Auto-generated method stub
				return false;
			}
		});
		for (Collidable c : toChange) {
			((Dot)c).setColor(Color.WHITE);
		}
		
//		if (r.nextFloat() < .1F)
//			explosions.add(new Explosion(this,r.nextFloat()*(float)screenDims.width,r.nextFloat()*(float)screenDims.height));
		
	}
	
	private GameContext getGameContext() {
		GameContext g = new GameContext();
		g.additions = new ArrayList<Mob>();
		g.removals = new ArrayList<Mob>();
		g.quadTree = q;
		return g;
	}
	
	private void updateQuadTree() {
		q.clear();
		for (Mob m : dots) {
			q.insert((Collidable)m);
		}
		
	}
	
	public void draw(Graphics g,float interpol) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, screenDims.width, screenDims.height);
		for (Mob m : dots) {
			m.drawMob(g,interpol);
		}
		for (Mob m : explosions) {
			m.drawMob(g,interpol);
		}
		q.draw(g);
	}
}
