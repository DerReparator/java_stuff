package graphics.snake;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import logic.snake.Game;
public class GameWindow extends JFrame
{
	public GameWindow(int sizeX, int sizeY)
	{
		/* JFrame initializations. */
		setTitle("Fusion Snake");
		setMinimumSize(new Dimension(sizeX * Game.entity_size, sizeY * Game.entity_size));
		setSize(sizeX * Game.entity_size, sizeY * Game.entity_size);
		setBackground(Color.BLACK);
		setLocationRelativeTo(null);
		add(new Game(sizeX, sizeY));//, BorderLayout.CENTER);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame exe = new GameWindow(25, 20);
				exe.setVisible(true);
			}
		});
	}
}