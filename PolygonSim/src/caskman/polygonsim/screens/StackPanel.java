package caskman.polygonsim.screens;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.List;

import caskman.polygonsim.model.Vector;

public class StackPanel extends Item {

	List<Item> items;
	public final static int VERTICAL = 0;
	public final static int HORIZONTAL = 1;
	private int orientation;
	private Dimension dims;
	private Vector position;
	private boolean isConstructed;
	
	public StackPanel() {
		items = new LinkedList<Item>();
		orientation = HORIZONTAL;
		position = null;
		dims = null;
		isConstructed = false;
	}
	
	@Override
	public void setDims(Dimension d) {
		dims = d;
	}
	
	@Override
	public void setPosition(Vector v) {
		position = v;
	}
	
	public void setOrientation(int n) {
		orientation = n;
	}
	
	private void construct() {
		if (orientation == HORIZONTAL) {
			int space = dims.width / items.size();
			for (int i = 0; i < items.size(); i++) {
				Dimension itemDims = items.get(i).getDims();
				items.get(i).setPosition(new Vector(space/2 + space*i - itemDims.width/2 + position.x,position.y + (dims.height - itemDims.height)/2));
			}
		} else if (orientation == VERTICAL) {
			int space = dims.height / items.size();
			for (int i = 0; i < items.size(); i++) {
				Dimension itemDims = items.get(i).getDims();
				items.get(i).setPosition(new Vector(position.x + (dims.width - itemDims.width)/2,space/2 + space*i - itemDims.height/2 + position.y));
			}
		}
		isConstructed = true;
	}
	
	@Override
	public void draw(Graphics2D g,float interpol) {
		if (!isConstructed)
			construct();
		
//		g.setColor(Color.WHITE);
//		g.drawRect((int)position.x, (int)position.y, dims.width, dims.height);
		for (Item i : items) {
			i.draw(g,interpol);
		}
	}
	
	public void add(Item i) {
		items.add(i);
	}

	@Override
	public void manageInput(InputEvent e) {
		for (Item i : items) {
			i.manageInput(e);
		}
	}


	@Override
	public Dimension getDims() {
		return dims;
	}

}
