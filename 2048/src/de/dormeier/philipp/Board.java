package de.dormeier.philipp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Random;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @version 1.1 There may still be a bug when right-shifting which causes not all tiles that should merge to actually merge.
 * @author Philipp
 *
 */
public class Board extends JPanel {

	public static final int board_size = 4;
	
	private NumberTile[][] numbers = new NumberTile[board_size][board_size];
	private int[][] board = new int[board_size][board_size];
	private int[][] prev_board = new int[board_size][board_size];
	private int emptyCells;
	private int score;
	private boolean keyGotPressed;
	private boolean GAME_RUNNING;
	
	Random rand;
	Random randTile;
	
	private JPanel gameboard;
	private JLabel the_score;
	
	private JLabel l_game_over;
	private Font f_game_over;
	private final String s_game_over = "Game Over! Punkte: %6d";
	private final String html1 = "<html><center><body style='width: ";
    private final String html2 = "px'>";
	
	public Board() {
		setLayout(new BorderLayout());
		setFocusable(true);
		addKeyListener(new MyKeyListener());
		setBackground(Color.BLACK);
		
		rand = new Random();
		randTile = new Random();
		startNewGame();
	}
	
	/**
	 * Starts a new game by initializing all Components and runtime-
	 * changeable variables.
	 */
	private void startNewGame() {
		emptyCells = board_size*board_size;
		keyGotPressed = false;
		score = 0;
		
		initializeUI();
		initializeNumbers();
		refreshUI();
		
		GAME_RUNNING = true;
	}
	
	private void initializeUI() {
		this.removeAll();
		gameboard = new JPanel();
		gameboard.setPreferredSize(new Dimension(GameWindow.BLOCK_SIZE*4, GameWindow.BLOCK_SIZE*4));
		gameboard.setLayout(new GridLayout(board_size,board_size,NumberTile.borderSize, NumberTile.borderSize));
		gameboard.setBackground(Color.DARK_GRAY);
		gameboard.setVisible(true);
		gameboard.setEnabled(true);
		
		the_score = new JLabel();
		the_score.setPreferredSize(new Dimension(GameWindow.BLOCK_SIZE*board_size, GameWindow.BLOCK_SIZE/4));
		the_score.setForeground(Color.WHITE);
		the_score.setVisible(true);
		
		f_game_over = new Font("Helvetica", Font.BOLD, GameWindow.BLOCK_SIZE/3);
		
		l_game_over = new JLabel(s_game_over);
		l_game_over.setFont(f_game_over);
		l_game_over.setBackground(this.getBackground());
		l_game_over.setForeground(Color.WHITE);
		l_game_over.setVisible(true);
		l_game_over.setEnabled(false);
		
		add(gameboard, BorderLayout.CENTER);
		add(the_score, BorderLayout.NORTH);
		
	}
	
	/**
	 * Initializes all the GameTiles and places the 2 starting values.
	 */
	private void initializeNumbers() {
		for(int y = 0; y < board_size; y++) {
			for(int x = 0; x < board_size; x++) {
				board[y][x] = 1;
				numbers[y][x] = new NumberTile();
				gameboard.add(numbers[y][x], y, x);
			}
		}
		prev_board = Arrays.stream(board).map(int[]::clone).toArray(int[][]::new);
		placeNewValue();
		placeNewValue();
	}
	
	/**
	 * Update the game's logic.
	 */
	private void updateBoard(int dx, int dy) {
		int startX = 3; /* rightwards */
		int startY = 3; /* downwards */

		boolean[] merged = new boolean[board_size];
		
		/* tiles get shifted upwards. */
		if (dy == -1) {
			for(int x = 0; x < board_size; x++) /* for every column */
			{	
				for(int b = 0; b < merged.length; b++) {
					merged[b] = false;
				}
				startY = 1;
				while(startY < board_size) 		/* for every tile except the first */
				{		
					if(board[startY][x] > 1) 	/* if tile not empty */
					{		
						for(int i = startY + dy; i >= 0; i += dy) /* check every tile before the tile */
						{ 
							if(board[i][x] == 1) 				/* empty tile found */
							{ 
								board[i][x] = board[i-dy][x];
								board[i-dy][x] = 1;
							} 
							else if (board[i][x] == board[i-dy][x]) /* suitable tile found -> MERGE */
							{ 
								if(!merged[i] && !merged[i-dy]) {
									board[i][x] *= 2;
									score += board[i][x];
									board[i-dy][x] = 1;
									emptyCells++;
									merged[i] = true;
								}
							}
						}
					}
					startY -= dy;
				}
			}
		}
		
		/* tiles get shifted downwards. */
		else if (dy == 1) {
			for(int x = 0; x < board_size; x++) /* for every column */
			{	
				for(int b = 0; b < merged.length; b++) {
					merged[b] = false;
				}
				startY = 2;
				while(startY >= 0) 		/* for every tile except the first */
				{		
					if(board[startY][x] > 1) 	/* if tile not empty */
					{		
						for(int i = startY + dy; i < board_size; i += dy) /* check every tile before the tile */
						{ 
							if(board[i][x] == 1) 				/* empty tile found */
							{ 
								board[i][x] = board[i-dy][x];
								board[i-dy][x] = 1;
							} 
							else if (board[i][x] == board[i-dy][x]) /* suitable tile found -> MERGE */
							{ 
								if(!merged[i] && !merged[i-dy]) {
									board[i][x] *= 2;
									board[i-dy][x] = 1;
									score += board[i][x];
									emptyCells++;
									merged[i] = true;
								}
							}
						}
					}
					startY -= dy;
				}
			}
		}
		
		/* tiles get shifted rightwards. */
		else if(dx == 1) {
			for(int y = 0; y < board_size; y++) /* for every row */
			{	
				startX = 2;
				while(startX >= 0) 		/* for every tile except the first */
				{		
					if(board[y][startX] > 1) 	/* if tile not empty */
					{		
						for(int i = startX + dx; i < board_size; i += dx) /* check every tile before the tile */
						{ 
							if(board[y][i] == 1) 				/* empty tile found */
							{ 
								board[y][i] = board[y][i-dx];
								board[y][i-dx] = 1;
							} 
							else if (board[y][i] == board[y][i-dx]) /* suitable tile found -> MERGE */
							{ 
								if(!merged[i] && !merged[i-dx]) {
									board[y][i] *= 2;
									score += board[y][i];
									board[y][i-dx] = 1;
									emptyCells++;
									merged[i] = true;
								}
							}
						}
					}
					startX -= dx;
				}
			}
		}
		
		/* tiles get shifted leftwards. */
		else if(dx == -1) {
			for(int y = 0; y < board_size; y++) /* for every column */
			{	
				for(int b = 0; b < merged.length; b++) {
					merged[b] = false;
				}
				startX = 1;
				while(startX < board_size) 		/* for every tile except the first */
				{		
					if(board[y][startX] > 1) 	/* if tile not empty */
					{		
						for(int i = startX + dx; i >= 0; i += dx) /* check every tile before the tile */
						{ 
							if(board[y][i] == 1) 				/* empty tile found */
							{ 
								board[y][i] = board[y][i-dx];
								board[y][i-dx] = 1;
							} 
							else if (board[y][i] == board[y][i-dx]) /* suitable tile found -> MERGE */
							{ 
								if(!merged[i] && !merged[i-dx]) {
									board[y][i] *= 2;
									score += board[y][i];
									board[y][i-dx] = 1;
									emptyCells++;
									merged[i] = true;
								}
							}
						}
					}
					startX -= dx;
				}
			}
		}
	}
	
	/**
	 * Updates the UI of the board to represent the current state of
	 * the game logic. Pretty simple as of now!
	 */
	private void refreshUI() {
		for(int y = 0; y < board_size; y++) {
			for(int x = 0; x < board_size; x++) {
				numbers[board_size-1-y][x].updateTile(board[y][x]);
			}
		}
		the_score.setText(String.format("Punkte: %6d", score));
		repaint();
	}
	
	/**
	 * Find's an empty cell and places a random tile there.
	 * Only does this for the logic part of the game.
	 * Doesn't update the UI.
	 * If there's no empty tile left this also gets recognized here.
	 */
	private void placeNewValue() {
		int new_pos, value, i = 0,y,x;
		if(emptyCells > 0) {
			value = (randTile.nextInt(2) + 1) * 2;
			new_pos = rand.nextInt(emptyCells);
			for(y = 0; y < board_size; y++) {
				for(x = 0; x < board_size; x++) {
					if(board[y][x] == 1 && i == new_pos) {
						board[y][x] = value;
						++i;
					}
					if(board[y][x] == 1 && new_pos != 0)
						++i;
				}
			}
			--emptyCells;
		}
	}
	
	/**
	 * Handles all the stuff that needs to happen, after a move is made
	 * (= after a valid key has been pressed).
	 * UP	 -> dx =  0, dy = -1
	 * RIGHT -> dx =  1, dy =  0
	 * DOWN  -> dx =  0, dy =  1
	 * LEFT	 -> dx = -1, dy =  0
	 * @param dX The X-direction in which all the tiles move.
	 * @param dY The Y-direction in which all the tiles move.
	 */
	private void doMove(int dX, int dY) {
		prev_board = Arrays.stream(board).map(int[]::clone).toArray(int[][]::new);
		updateBoard(dX, dY);
		placeNewValue();
		refreshUI();

		if(isGameOver()) {
			gameOver();
		}
		keyGotPressed = false;
	}
	
	/**
	 * Reset the board one step.
	 */
	private void resetBoard() {
		board = Arrays.stream(prev_board).map(int[]::clone).toArray(int[][]::new);
		l_game_over.setVisible(false);
		l_game_over.setEnabled(false);
		gameboard.setVisible(true);
		the_score.setVisible(true);
		refreshUI();
		keyGotPressed = false;
	}
	
	/**
	 * Gets called when the current game is over. Constructs and displays
	 * the 'Game Over'-screen.
	 */
	private void gameOver() {
		GAME_RUNNING = false;
		this.removeAll();
		add(l_game_over, BorderLayout.CENTER);
		l_game_over.setText(html1 + (this.getWidth()-GameWindow.BLOCK_SIZE) + html2 + String.format(l_game_over.getText(), score));
		l_game_over.setEnabled(true);
		l_game_over.setVisible(true);
	}
	
	/**
	 * Determines whether the game is over.
	 * @return true if there is no emptyCell left AND there are no 2 neighbors with the same value.
	 */
	private boolean isGameOver() {
		if(emptyCells > 0) {
			return false;
		} else {
			for(int y = 0; y < board_size; y++) {
				for(int x = 0; x < board_size; x++) {
					/* check above */					
					if(y-1 >= 0 && board[y-1][x] == board[y][x])
						return false;
					/* check right */					
					if(x+1 < board_size && board[y][x+1] == board[y][x])
						return false;
					/* check below */					
					if(y+1 < board_size && board[y+1][x] == board[y][x])
						return false;
					/* check left */					
					if(x-1 >= 0 && board[y][x-1] == board[y][x])
						return false;
				}
			}
			return true;
		}
	}
	
	private class MyKeyListener extends KeyAdapter{
		
		public void keyPressed(KeyEvent k) {
			if(!keyGotPressed) {
				keyGotPressed = true;
				int key = k.getKeyCode();
				if(GAME_RUNNING) {
					switch(key) {
					case KeyEvent.VK_UP:
						doMove(0, -1);
						break;
					case KeyEvent.VK_RIGHT:
						doMove(1, 0);
						break;
					case KeyEvent.VK_DOWN:
						doMove(0, 1);
						break;
					case KeyEvent.VK_LEFT:
						doMove(-1, 0);
						break;
					}
				} else {
					if(key == KeyEvent.VK_SPACE)
						startNewGame();
					keyGotPressed = false;
				}
			}
		}
	}
}
