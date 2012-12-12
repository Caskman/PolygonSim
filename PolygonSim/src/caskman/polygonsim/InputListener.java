package caskman.polygonsim;

import java.awt.Component;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class InputListener {
	
	Component c;
	
	public InputListener(Component comp) {
		c = comp;
	}
	
	public void addMouseMotionListener(MouseMotionListener l) {
		c.addMouseMotionListener(l);
	}
	
	public void addMouseListener(MouseListener l) {
		c.addMouseListener(l);
	}
	
}
