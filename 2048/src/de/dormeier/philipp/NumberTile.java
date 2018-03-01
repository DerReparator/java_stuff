package de.dormeier.philipp;

import java.awt.Color;

import javax.swing.JButton;

public class NumberTile extends JButton {

	/**
	 * The size of the border/2 between each number tile in pixels.
	 */
	public static final int borderSize = 3;
	
	public static final Color[] tileBg = {Color.DARK_GRAY,
			new Color(229, 202, 48),
			new Color(229, 222, 48),
			new Color(213, 229, 48),
			new Color(195, 229, 48),
			new Color(156, 229, 48),
			new Color(102, 229, 48),
			new Color(48, 229, 87),
			new Color(48, 229, 147),
			new Color(48, 229, 183),
			new Color(48, 229, 229),
			new Color(48, 195, 229),
			new Color(48, 162, 229)
	};
	
	public NumberTile() {
		setSize(GameWindow.BLOCK_SIZE-2*borderSize, GameWindow.BLOCK_SIZE-2*borderSize);
		setBackground(tileBg[0]);
		setEnabled(false);
		setVisible(true);
		setText("");
		setVisible(true);
	}
	
	private int log2(int n){
	    return 31 - Integer.numberOfLeadingZeros(n);
	}
	
	public void updateTile(int value) {
		if(value > 1 && value % 2 != 0) {
			throw new IllegalArgumentException("Der Tile-Wert wurde nicht richtig berechnet");
		} else {
			if(value > 1)
				setText(""+value);
			else
				setText("");
			setBackground(tileBg[log2(value)]);
		}
	}
}
