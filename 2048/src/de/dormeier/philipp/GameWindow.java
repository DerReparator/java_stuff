package de.dormeier.philipp;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class GameWindow extends JFrame {

	public static final int BLOCK_SIZE = 60;
	
	public GameWindow() {
		setTitle("Fusion 2048");
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.add(new Board(),BorderLayout.CENTER);
		pack();
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
