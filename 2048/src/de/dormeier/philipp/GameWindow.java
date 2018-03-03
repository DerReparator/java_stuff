package de.dormeier.philipp;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class GameWindow extends JFrame {

	public static final int BLOCK_SIZE = 96;
	private Board b;
	
	public GameWindow() {
		setTitle("Fusion 2048");
		setResizable(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		b = new Board();
		this.add(b,BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(null);
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {			
			public void run() {
				JFrame exe = new GameWindow();
				exe.setVisible(true);
			}		
		});
	}
}
