package caskman.polygonsim.screens;

import java.awt.Dimension;
import java.awt.Graphics2D;

import caskman.polygonsim.model.Vector;

public abstract class Item {

	public abstract void draw(Graphics2D g,float interpol);
	

	
	public abstract void manageInput(InputEvent e);
	
	public abstract void setPosition(Vector v);
	
	public abstract void setDims(Dimension d);
	
	public abstract Dimension getDims();
	
}
