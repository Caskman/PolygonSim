package caskman.polygonsim.model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import caskman.polygonsim.model.entities.Dot;


public class GameModel {
	
	private Dimension screenDims;
	private Random r;
	private float blockRatio = .0001F;
	private float blockMaxVel = 30;
	private List<Mob> blocks;
	
	public GameModel(Dimension screenDims) {
		this.screenDims = screenDims;
		
		initialize();
	}
	
	public Dimension getScreenDims() {
		return screenDims;
	}
	
	private void initialize() {
		r = new Random();
		int numBlocks = (int) (screenDims.width*screenDims.height*blockRatio);
		blocks = new ArrayList<Mob>(numBlocks);
		
		for (int i = 0; i < numBlocks; i++) {
			float xPos = r.nextFloat()*screenDims.width;
			float yPos = r.nextFloat()*screenDims.height;
			float xVel = blockMaxVel*r.nextFloat() - blockMaxVel/2.0F;
			float yVel = blockMaxVel*r.nextFloat() - blockMaxVel/2.0F;
			blocks.add(new Dot(this,xPos,yPos,xVel,yVel));
		}
		
	}
	
	public void update() {
		
		initializeQuadTree();
		
		for (Mob m : blocks) {
			m.update(null);
		}
	}
	
	private void initializeQuadTree() {
		
	}
	
	public void draw(Graphics g,float interpol) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, screenDims.width, screenDims.height);
		for (Mob m : blocks) {
			m.draw(g,interpol);
		}
	}
}
