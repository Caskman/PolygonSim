package caskman.polygonsim;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ResultsFrame extends JFrame implements Results{
	JTextArea text;
	JScrollPane pane;

	public ResultsFrame() {

		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screen = tk.getScreenSize();
		double f = 0.75;
		Dimension frame = new Dimension((int) (screen.width * f),
				(int) (screen.height * f));
		Point frame_location = new Point((screen.width - frame.width) / 2,
				(screen.height - frame.height) / 2);

		this.setSize(frame);
		this.setLocation(frame_location);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(null);
		this.setResizable(false);

		Dimension text_dim = new Dimension(frame.width - 10, frame.height - 31);

		text = new JTextArea();
		text.setSize(text_dim);
		text.setLocation(0, 0);
		text.setEditable(false);
		text.setVisible(true);

		pane = new JScrollPane(text);
		pane.setSize(text_dim.width - 5, text_dim.height - 5);
		pane.setLocation(0, 0);
		pane.setVisible(true);

		this.add(pane);

		this.setVisible(true);
	}

	@Override
	public void print(Object o) {
		text.append(o.toString());
	}

}