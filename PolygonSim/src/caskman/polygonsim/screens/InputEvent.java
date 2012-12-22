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
	public final static int KEY_PRESSED = 7;
	public final static int KEY_RELEASED = 8;
	public final static int KEY_TYPED = 9;
	
	
	
	private int type;
	private Vector v;
	private int button;
	private int keycode;
	private char keyChar;

	public InputEvent(int type,int keycode,char keyChar) {
		this.type = type;
		this.keycode = keycode;
		this.keyChar = keyChar;
	}
	
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

	public char getKeyChar() {
		return keyChar;
	}
	
	public int getKeyCode() {
		return keycode;
	}

	public boolean isKeyInput() {
		return type == KEY_PRESSED || type == KEY_TYPED || type == KEY_RELEASED;
	}
}
