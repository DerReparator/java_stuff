package logic.snake;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class Game extends JPanel implements ActionListener
{

	private javax.swing.Timer timer;
	private Random rand = new Random();
	
	/* Prerequisites. */
	private static final int INITIAL_SNAKE_LENGTH = 3;
	private static final int min_wall_offset = 4;
	private final int delay = 150;
	private int length;
	
	/* Definitions of important values. */
	public static final char BORDER = 'X';
	public static final char EMPTY = ' ';
	public static final char BODY = 'o';
	public static final char HEAD = '0';
	public static final char FOOD = 'x';
	/** The pixel width and height of one Object. */
	public static final int entity_size = 25;
	public static final Color bg = Color.BLACK;
	
	/** Stores if a key has been pressed since the last update of the snakes position. */
	private boolean key_pressed;
	private char upcoming_key;
	
	private Point[] snake;
	
	enum Direction
	{
		NORTH(-1, 0), EAST(0, 1), SOUTH(1, 0), WEST(0, -1);

		int dY;
		int dX;

		Direction(int dY, int dX)
		{
			this.dY = dY;
			this.dX = dX;
		}
	};

	public char[][] spielfeld;
	/* The direction the snake is facing. */
	Direction dir;
	private GameState status;
	private JLabel infobar;
	
	/** The number of the tiles on the board. */
	private int SIZE_X, SIZE_Y;
	/** The current size of a tile in pixels. */
	private int entity_size_x, entity_size_y;

	public Game(int sizeX, int sizeY)
	{
		timer = new javax.swing.Timer(delay, this);
		timer.setDelay(delay);
		rand = new Random();
		
		infobar = new JLabel();
		add(infobar, BorderLayout.NORTH);
		
		setDoubleBuffered(true);
		
		this.SIZE_X = sizeX;
		this.SIZE_Y = sizeY;
		
		spielfeld = new char[sizeY][sizeX];	
		addKeyListener(new SnakeListener());
		setFocusable(true);	

		restartGame(sizeX, sizeY);
	}
	
	private void restartGame(int sizeX, int sizeY) {
		timer.restart();
		snake = new Point[(sizeX-2)*(sizeY-2)];
		key_pressed = false;
		infobar.setVisible(true);
		initSpielfeld();	
		
		placeSnake();
		length = INITIAL_SNAKE_LENGTH;
		generateFood();
		status = GameState.RUNNING;
	}
	
	private void initSpielfeld()
	{
		for (int x = 0; x < SIZE_X; x++)
			for (int y = 1; y < SIZE_Y - 1; y++)
				spielfeld[y][x] = EMPTY;
		
		/* Initialize the border around the Spielfeld. */
		for (int x = 0; x < SIZE_X; x++)
		{
			spielfeld[0][x] = BORDER;
			spielfeld[SIZE_Y - 1][x] = BORDER;
		}

		for (int y = 1; y < SIZE_Y - 1; y++)
		{
			spielfeld[y][0] = BORDER;
			spielfeld[y][SIZE_X - 1] = BORDER;
		}
	}
	
	enum Entity
	{
		BORDER(Color.RED), EMPTY(Game.bg), BODY(Color.GREEN), HEAD(Color.YELLOW), FOOD(Color.BLUE);

		Color c;

		Entity(Color colour)
		{
			this.c = colour;
		}
	};
	
	private void move() {
		int dX = dir.dX;
		int dY = dir.dY;
		if(!checkCollision(dX, dY)) {
			Point head = snake[0];
			int i = length-1;
			spielfeld[snake[i].y][snake[i].x] = EMPTY;
			while(i > 0) {
				snake[i].move(snake[i-1].x, snake[i-1].y);
				i--;
			}
			snake[0].move(head.x + dX, head.y + dY);
			if(spielfeld[snake[0].y][snake[0].x] == FOOD) eat();
			drawSnake();
		}
	}
	
	/** Places a new food item on a random free position. */
	private void generateFood()
	{
		int foodX; 
		int foodY;
		do {
			foodX = rand.nextInt(SIZE_X-2)+1;
			foodY = rand.nextInt(SIZE_Y-2)+1;
		}
		while(spielfeld[foodY][foodX] != EMPTY); // Generiere mögliche Food-Stücke bis Postion != Wurm gefunden.		
		spielfeld[foodY][foodX] = FOOD; // actually places the new food piece.
		infobar.setText(String.format("Länge: %3d Steuerung: W = hoch, S = runter, D = rechts, A = links, P = Pause", length));
	}
	
	/** Places the snake at a valid random position. */
	private void placeSnake() {
		int startY, startX;

		/* This if-Statement has the problem that it could block snake lengths that
		 * f.e. could go well in vertical direction but not in horizontal direction.
		 */
		if(INITIAL_SNAKE_LENGTH > SIZE_X-min_wall_offset-1 || INITIAL_SNAKE_LENGTH > SIZE_Y-min_wall_offset-1)
			throw new IllegalArgumentException("Parameter INITIAL_SNAKE_LENGTH too big!");
		
		if(min_wall_offset >= (SIZE_X-2)/2 || min_wall_offset >= (SIZE_Y/2)) {
			throw new IllegalArgumentException("Parameter min_wall_offset too big!");
		}else {
			// Generate starting point
			do {
				startX = rand.nextInt(SIZE_X - min_wall_offset - 1) + min_wall_offset + 1;
				startY = rand.nextInt(SIZE_Y - min_wall_offset - 1) + min_wall_offset + 1;
		
			} while (
					startX - INITIAL_SNAKE_LENGTH < 1 ||
					startX + INITIAL_SNAKE_LENGTH > SIZE_X - 2 ||
					startY - INITIAL_SNAKE_LENGTH < 1 ||
					startY + INITIAL_SNAKE_LENGTH > SIZE_Y-2
					);
			// Generate starting heading
			dir = Direction.values()[rand.nextInt(Direction.values().length)];
			
			switch(dir) {
			case NORTH:
				for(int i = 0; i < INITIAL_SNAKE_LENGTH; i++)
					snake[i] = new Point(startX+i*Direction.NORTH.dX, startY+i*Direction.NORTH.dY);
				break;
			case EAST:
				for(int i = 0; i < INITIAL_SNAKE_LENGTH; i++)
					snake[i] = new Point(startX+i*Direction.EAST.dX, startY+i*Direction.EAST.dY);
				break;
			case SOUTH:
				for(int i = 0; i < INITIAL_SNAKE_LENGTH; i++)
					snake[i] = new Point(startX+i*Direction.SOUTH.dX, startY+i*Direction.SOUTH.dY);
				break;
			case WEST:
				for(int i = 0; i < INITIAL_SNAKE_LENGTH; i++)
					snake[i] = new Point(startX+i*Direction.WEST.dX, startY+i*Direction.WEST.dY);
				break;
			}
		}
	}
	
	/** Handles everything regarding eating a piece of food. */
	private void eat()
	{
		snake[length] = new Point(snake[length-1].x, snake[length-1].y);
		length++;
		generateFood();	
	}

	/** Pushes the logic information of the snake array to the 'spielfeld' to get drawn. */
	private void drawSnake() {
		int i = 1;
		spielfeld[snake[0].y][snake[0].x] = HEAD;
		while(snake[i] != null) {
			spielfeld[snake[i].y][snake[i].x] = BODY;
			i++;
		}
	}
	
	private boolean checkCollision(int dX, int dY) {
		int newX = snake[0].x + dX;
		int newY = snake[0].y + dY;
		
		switch(spielfeld[newY][newX]) {
		case BODY:
		case HEAD:
			status = GameState.CRASHED_SNAKE;
			return true;
		case BORDER:
			status = GameState.CRASHED_BORDER;
			return true;
		default:
			return false;
		}
	}
	
	/** Handles displaying a GameOver screen.
	 * @param reason A short description why it's "Game Over".
	 */
	private void gameOver(String reason, Graphics2D g2d) {
		g2d.setColor(bg);
		g2d.fillRect(0, 0, getWidth(), getHeight());
		
		infobar.setVisible(false);
		
		String msg = "Game Over! " + reason;
		String res = "Drücke [Leertaste] um neu zu starten";
		String sco = "Punktzahl: "+ length;
		Font go = new Font("Helvetica", Font.BOLD, 16);
		FontMetrics metr = getFontMetrics(go);
		
		g2d.setColor(Color.white);
		g2d.setFont(go);
		g2d.drawString(msg, (getWidth()-metr.stringWidth(msg))/2, getHeight()/2);
		g2d.drawString(res, (getWidth()-metr.stringWidth(res))/2, getHeight()/2 + metr.getHeight());
		g2d.drawString(sco, (getWidth()-metr.stringWidth(sco))/2, getHeight()/2 + metr.getHeight()*3);
		//timer.stop();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		key_pressed = false;
		switch(upcoming_key) {
		case 'w':
			dir = Direction.NORTH; break;
		case 'd':
			dir = Direction.EAST; break;
		case 's':
			dir = Direction.SOUTH; break;
		case 'a':
			dir = Direction.WEST; break;
		}
		
		if(status != GameState.PAUSED) {
			move(); // Collision checking is done in move()
			repaint();
		}
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		entity_size_x = this.getWidth() / SIZE_X;
		entity_size_y = this.getHeight() / SIZE_Y;
		switch (status)
		{
		case PAUSED:
		case RUNNING:
			g2d.setBackground(Entity.EMPTY.c);
			for (int y = 0; y < spielfeld.length; y++)
			{
				for (int x = 0; x < spielfeld[y].length; x++)
				{
					switch (spielfeld[y][x])
					{
					case Game.BORDER:
					{
						g2d.setColor(Entity.BORDER.c);
						g2d.fillRect(x*entity_size_x, y*entity_size_y, entity_size_x, entity_size_y);
						break;
					}
					case Game.EMPTY:
					{
						g2d.setColor(Entity.EMPTY.c);
						g2d.fillRect(x*entity_size_x, y*entity_size_y, entity_size_x, entity_size_y);
						break;
					}
					case Game.BODY:
					{
						g2d.setColor(bg);
						g2d.fillRect(x*entity_size_x, y*entity_size_y, entity_size_x, entity_size_y);
						g2d.setColor(Entity.BODY.c);
						g2d.fillRoundRect(x*entity_size_x, y*entity_size_y, entity_size_x, entity_size_y, entity_size_x/3, entity_size_y/3);
						break;
					}
					case Game.HEAD:
					{
						g2d.setColor(Entity.HEAD.c);
						g2d.fillRect(x*entity_size_x, y*entity_size_y, entity_size_x, entity_size_y);
						break;
					}
					case Game.FOOD:
					{
						g2d.setColor(Entity.FOOD.c);
						g2d.fillOval(x*entity_size_x, y*entity_size_y, entity_size_x, entity_size_y);
						break;
					}
					}
				}
			}
			break;
		case CRASHED_SNAKE:
			gameOver("Du hast dich selbst berührt.", g2d);
			break;
		case CRASHED_BORDER:
			gameOver("Du hast eine Wand berührt.", g2d);
			break;
		case EXIT:
			System.exit(0);
			break;
		}
	}
	
	class SnakeListener extends KeyAdapter {
	
		@Override
		public void keyPressed(KeyEvent e) {
			if (!key_pressed) {
				key_pressed = true;
				switch(e.getKeyChar()){
				case 'w':
					if(dir != Direction.SOUTH) upcoming_key = 'w';
					break;
				case 'd':
					if(dir != Direction.WEST) upcoming_key = 'd';
					break;			
				case 's':
					if(dir != Direction.NORTH) upcoming_key = 's';
					break;			
				case 'a':
					if(dir != Direction.EAST) upcoming_key = 'a';
					break;
				case 'p':
					if(status != GameState.PAUSED) {
						status = GameState.PAUSED;
					} else {
						status = GameState.RUNNING;
					}
					break;
				case 'e':
					status = GameState.EXIT;
					break;
				case ' ':
					if(status == GameState.CRASHED_BORDER || status == GameState.CRASHED_SNAKE)
						restartGame(SIZE_X, SIZE_Y);
				}
			}
		}
	}
}