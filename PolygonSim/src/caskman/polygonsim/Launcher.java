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

import caskman.polygonsim.screens.InputListener;
import caskman.polygonsim.screens.MainMenuScreen;
import caskman.polygonsim.screens.ScreenManager;


public class Launcher {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Parameters.load();
		Profiler.initialize(false);
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
//		System.setProperty("awt.useSystemAAFontSettings","on");
//		System.setProperty("swing.aatext", "true");
		try {
			GraphicsConfiguration gc = gd.getDefaultConfiguration();
			Frame f = new Frame(gc);
			f.setUndecorated(true);
			f.setIgnoreRepaint(true);
			Dimension dims = Toolkit.getDefaultToolkit().getScreenSize();
			if (!Parameters.WINDOWED)
				gd.setFullScreenWindow(f);
			else {
				f.setLocation(0, 0);
				f.setSize(dims);
				f.setVisible(true);
			}
//          if (gd.isDisplayChangeSupported()) {
//                chooseBestDisplayMode(gd);
//            }
			if (!Parameters.WINDOWED)
				gd.setDisplayMode(new DisplayMode(dims.width,dims.height,32,0));
	  
			BufferStrategy bs;
			if ((bs = f.getBufferStrategy()) == null) {
				f.createBufferStrategy(2);
				bs = f.getBufferStrategy();
			}
			Rectangle bounds = f.getBounds();
			Dimension screenDims = new Dimension(bounds.width,bounds.height);
//			GameModel model = new GameModel(screenDims,new InputListener(f));
			ScreenManager manager = new ScreenManager(screenDims,new InputListener(f));
			manager.addScreen(new MainMenuScreen(manager));
			final MainThread main = new MainThread(bs,manager);
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

	public static void exit() {
		System.exit(0);
	}
	
}
