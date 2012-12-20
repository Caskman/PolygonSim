package caskman.polygonsim.screens;

import caskman.polygonsim.model.Vector;

public class InputEvent {
	
	public final static int MOUSE_CLICKED = 0;
	public final static int MOUSE_DRAGGED = 1;
	public final static int MOUSE_ENTERED = 2;
	public final static int MOUSE_EXITED = 3;
	public final static int MOUSE_MOVED = 4;
	public final static int MOUSE_PRESSED = 5;
	public final static int MOUSE_RELEASED = 6;
	
	
	private int type;
	private Vector v;
	private int button;

	public InputEvent(Vector v,int type,int button) {
		this.v = v;
		this.type = type;
		this.button = button;
	}
	
	public int getType() {
		return type;
	}
	
	public Vector getVector() {
		return v;
	}
	
	public boolean isMouseInput() {
		return type >= MOUSE_CLICKED && type <= MOUSE_RELEASED;
	}
	
	public int getButton() {
		return button;
	}

}
