package caskman.polygonsim;

import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;

import caskman.polygonsim.model.GameModel;


public class Launcher {

	
	
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		try {
			GraphicsConfiguration gc = gd.getDefaultConfiguration();
			Frame f = new Frame(gc);
			f.setUndecorated(true);
			f.setIgnoreRepaint(true);
			gd.setFullScreenWindow(f);
//          if (gd.isDisplayChangeSupported()) {
//                chooseBestDisplayMode(gd);
//            }
			Dimension dims = Toolkit.getDefaultToolkit().getScreenSize();
			gd.setDisplayMode(new DisplayMode(dims.width,dims.height,32,0));
	  
			BufferStrategy bs;
			if ((bs = f.getBufferStrategy()) == null) {
				f.createBufferStrategy(2);
				bs = f.getBufferStrategy();
			}
			Rectangle bounds = f.getBounds();
			Dimension screenDims = new Dimension(bounds.width,bounds.height);
			GameModel model = new GameModel(screenDims);
			final MainThread main = new MainThread(bs,model);
			f.addWindowListener(new WindowListener() {
				public void windowActivated(WindowEvent arg0) {
				}
				public void windowClosed(WindowEvent arg0) {
				}
				public void windowClosing(WindowEvent arg0) {
					main.setRunning(false);
					System.exit(0);
				}
				public void windowDeactivated(WindowEvent arg0) {
				}
				public void windowDeiconified(WindowEvent arg0) {
				}
				public void windowIconified(WindowEvent arg0) {
				}
				public void windowOpened(WindowEvent arg0) {
				}
			});
			main.setRunning(true);
			main.start();
		} catch (Exception e) {
			e.printStackTrace();
			gd.setFullScreenWindow(null);
		} finally {
		}
	}

	
	
}
