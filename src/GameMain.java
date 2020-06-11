
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

@SuppressWarnings("serial")
public class GameMain extends Canvas implements Runnable {
	/* The Rules of Life
	 * 1) Cells with 0 or 1 neighbors die of loneliness
	 * 2) Cells with 2 or 3 neighbors survive
	 * 3) Cells with 4 or more neighbors die of overcrowding
	 * 4) Cells with exactly 3 neighbors come back to life
	 */
	private Thread thread;
	public static boolean running = false;

	private MouseInput mouse;

	public static boolean paused = true;
	public static int gameSpeed = 200;
	private long updateCooldown = 0L;

	private static int cellSize = 20;
	private static int borderSize = 1;

	//can be changed based on preference, columns shouldn't be smaller than 20
	private static int rows = 30, cols = 55;

	private static int width = (borderSize + cellSize) * cols + borderSize, height = (borderSize + cellSize) * rows + borderSize + 5 * (borderSize + cellSize);

	public static Cell[][] grid = new Cell[(width - borderSize) / (borderSize + cellSize)][(height - borderSize) / (borderSize + cellSize)];
	GameObject[] uiButtons = new GameObject[7];
	public static boolean showNumbers = false;

	public static boolean[][] previousGrid = new boolean[grid.length][grid[0].length];
	public static int[][] neigCount = new int[grid.length][grid[0].length];

	public GameMain() {
		requestFocus();
		new Window(width, height, "Game Of Life", this);

		mouse = new MouseInput();
		addMouseListener(mouse);
		addMouseMotionListener(mouse);

		for (int i = 0; i < grid.length; i++)
			for (int j = 0; j < grid[i].length; j++)
				grid[i][j] = new Cell((borderSize + cellSize) * i + borderSize, 5 * (borderSize + cellSize) + (borderSize + cellSize) * j + borderSize, cellSize, false, mouse);

		uiButtons[0] = new PausePlayButton(width / 2 - PausePlayButton.width / 2, 8, mouse);
		uiButtons[1] = new ClearButton(width / 2 + 90, 22, mouse);
		uiButtons[2] = new GameSpeedButton(width / 2 - 210, 22, mouse);
		uiButtons[3] = new UndoButton(width / 2 - 370, 22, mouse);
		uiButtons[4] = new ShowNeighborCountButton(width / 2 + 250, 22, mouse);
		uiButtons[5] = new SaveButton(width / 2 - 520, 22, mouse);
		uiButtons[6] = new HelpButton(width / 2 + 520, 19, mouse);
	}

	private void tick() {
		//update each pixel
		for (int i = 0; i < grid.length; i++)
			for (int j = 0; j < grid[i].length; j++)
				if (grid[i][j] != null) 
					grid[i][j].tick();

		//save grid for undo
		if (mouse.getPressed() && mouse.getY() > 105) {
			for (int i = 0; i < grid.length; i++)
				for (int j = 0; j < grid[0].length; j++)
					previousGrid[i][j] = grid[i][j].isAlive();
		}

		//update top buttons
		for (int i = 0; i < uiButtons.length; i++)
			if (uiButtons[i] != null)
				uiButtons[i].tick();

		boolean[][] changes = new boolean[grid.length][grid[0].length];
		//apply rules to cells
		if (!paused) {
			long currentTime = System.currentTimeMillis();
			if (currentTime - updateCooldown >= gameSpeed) {
				updateCooldown = currentTime;
				for (int i = 0; i < grid.length; i++) 
					for (int j = 0; j < grid[i].length; j++)  
						changes[i][j] = grid[i][j].isAlive();

				int neighborCount = 0;
				for (int i = 0; i < grid.length; i++) {
					for (int j = 0; j < grid[i].length; j++) {
						if (grid[i][j] != null) {
							neighborCount = 0;
							for (int k = -1; k < 2; k++) {
								for (int l = -1; l < 2; l++) {
									//try catch is to avoid all the edge cases
									try {
										if (grid[i + k][l + j].isAlive())
											neighborCount++;
									}
									catch(ArrayIndexOutOfBoundsException e) {
										continue;
									}
								}
							}
							//previous loops counted the cell itself as it's own neighbor when k and l were both 0
							//so we need to deduct it from the neighbor count
							if (grid[i][j].isAlive())
								neighborCount--;

							if (neighborCount <= 1)
								changes[i][j] = false;
							else if (neighborCount == 2) {
								if (grid[i][j].isAlive())
									changes[i][j] = true;
								else
									changes[i][j] = false;
							}
							else if (neighborCount > 3)
								changes[i][j] = false;
							else if (neighborCount == 3)
								changes[i][j] = true;
						}

					}
				}
				//applies each cells fate at the same time
				for (int i = 0; i < changes.length; i++) 
					for (int j = 0; j < changes[i].length; j++) 
						grid[i][j].setAlive(changes[i][j]);


			}
		}

		for (int i = 0; i < neigCount.length; i++) 
			for (int j = 0; j < neigCount[i].length; j++) 
				neigCount[i][j] = 0;
		int neighborCount = 0;
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				neighborCount = 0;
				for (int k = -1; k < 2; k++) {
					for (int l = -1; l < 2; l++) {
						//try catch is to avoid all the edge cases
						try {
							if (grid[i + k][l + j].isAlive())
								neighborCount++;
						}
						catch(ArrayIndexOutOfBoundsException e) {
							continue;
						}
					}

				}
				if (grid[i][j].isAlive())
					neighborCount--;
				neigCount[i][j] = neighborCount;
			}
		}

	}

	private void render() {
		BufferStrategy bs=this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		Graphics g = bs.getDrawGraphics();
		//---draw begin---

		//background
		g.setColor(Color.black);
		g.fillRect(0, 0, width, height);

		//render cells
		for (int i = 0; i < grid.length; i++)
			for (int j = 0; j < grid[i].length; j++)
				if (grid[i][j] != null) 
					grid[i][j].render(g);

		//render grid
		g.setColor(new Color(66*7/10, 129*7/10, 44*7/10));
		for (int i = 0; i < height; i += (borderSize + cellSize)) 
			g.fillRect(0, i, width, borderSize);
		for (int i = 0; i < width; i += (borderSize + cellSize)) 
			g.fillRect(i, 0, borderSize, height);

		//render top menu
		g.setColor(new Color(66*7/10, 129*7/10, 44*7/10));
		g.fillRect(0, 0, width, 5 * (borderSize + cellSize));

		//render buttons
		for (int i = 0; i < uiButtons.length; i++)
			if (uiButtons[i] != null)
				uiButtons[i].render(g);

		//render neighbor counts
		if (showNumbers) {
			for (int i = 0; i < neigCount.length; i++) {
				for (int j = 0; j < neigCount[0].length; j++) {
					if (grid[i][j].isAlive() || (neigCount[i][j] != 0 && !grid[i][j].isAlive())) {

						if (grid[i][j].isAlive())
							g.setColor(Color.black);
						else 
							g.setColor(new Color(150, 180, 150));

						g.setFont(new Font("", Font.BOLD, 15));
						g.drawString(neigCount[i][j] + "", i * (cellSize + borderSize) + 7, j * (cellSize + borderSize) + 122);

						if (neigCount[i][j] == 3 && !grid[i][j].isAlive()) {
							g.setColor(Color.green);
							g.drawRect(i * (cellSize + borderSize) + 1, j * (cellSize + borderSize) + 106, cellSize - 1, cellSize);
						}
						if (grid[i][j].isAlive() && (neigCount[i][j] < 2 || neigCount[i][j] > 3)) {
							g.setColor(Color.red);
							g.drawRect(i * (cellSize + borderSize) + 1, j * (cellSize + borderSize) + 106, cellSize - 1, cellSize);
						}
					}
					else {
						g.setColor(Color.black);
						g.fillRect(i * (cellSize + borderSize) + 1, j * (cellSize + borderSize) + 106, cellSize, cellSize);
					}
				}
			}
		}

		if (HelpButton.mouseHover) {
			g.setColor(new Color(66*8/5, 129*8/5, 44*8/5));
			int[] xPoints = {HelpButton.helpX + 23, HelpButton.helpX + 23, HelpButton.helpX + HelpButton.width - 40};
			int[] yPoints = {HelpButton.helpY + 42, HelpButton.helpY + 95, HelpButton.helpY + 95};
			g.fillPolygon(xPoints, yPoints, 3);

			g.setColor(new Color(66*8/5, 129*8/5, 44*8/5));
			g.fillOval(HelpButton.helpX + 8, HelpButton.helpY + 17, HelpButton.width - 4, HelpButton.height - 4);

			g.setColor(Color.black);
			g.fillOval(HelpButton.helpX + 11, HelpButton.helpY + 20, HelpButton.width - 10, HelpButton.height - 10);

			g.setColor(new Color(66*8/5, 129*8/5, 44*8/5));
			g.setFont(new Font("", Font.BOLD, 25));
			g.drawString("?", HelpButton.helpX + 17, HelpButton.helpY + 42);

			g.setColor(new Color(66*8/5, 129*8/5, 44*8/5));
			g.fillRoundRect(HelpButton.helpX - 250, HelpButton.helpY + 90, 300, 360, 20, 20);

			g.setColor(Color.black);
			g.fillRoundRect(HelpButton.helpX - 245, HelpButton.helpY + 95, 290, 350, 20, 20);

			g.setColor(new Color(66*8/5, 129*8/5, 44*8/5));
			g.setFont(new Font("", Font.BOLD, 30));
			g.drawString("Rules of Life", HelpButton.helpX - 185, HelpButton.helpY + 130);
			g.fillRect(HelpButton.helpX - 250, HelpButton.helpY + 140, 300, 5);

			g.setColor(new Color(66*6/5, 129*6/5, 44*6/5));
			g.setFont(new Font("", Font.BOLD, 20));
			g.drawString("1- Cells with less than 2 alive", HelpButton.helpX - 235, HelpButton.helpY + 180);
			g.drawString("neighbors die of loneliness.", HelpButton.helpX - 235, HelpButton.helpY + 205);

			g.drawString("2- Cells with 2 or 3 alive", HelpButton.helpX - 235, HelpButton.helpY + 240);
			g.drawString("neighbors survive.", HelpButton.helpX - 235, HelpButton.helpY + 265);

			g.drawString("3- Cells with 4 or more alive", HelpButton.helpX - 235, HelpButton.helpY + 300);
			g.drawString("neighbors die because of", HelpButton.helpX - 235, HelpButton.helpY + 325);
			g.drawString("overcrowding.", HelpButton.helpX - 235, HelpButton.helpY + 350);

			g.drawString("4- Dead cells with exactly 3", HelpButton.helpX - 235, HelpButton.helpY + 380);
			g.drawString("neighbors will come back to", HelpButton.helpX - 235, HelpButton.helpY + 405);
			g.drawString("life.", HelpButton.helpX - 235, HelpButton.helpY + 430);
		}


		//---draw end---	
		g.dispose();
		bs.show();
	}

	public static void undo() {
		for (int i = 0; i < grid.length; i++)
			for (int j = 0; j < grid[0].length; j++) 
				grid[i][j].setAlive(previousGrid[i][j]);
	}

	public void run(){
		this.requestFocus();
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		long timer = System.currentTimeMillis();
		while(running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while(delta >= 1) {
				tick();
				delta--;
			}
			if(running) 
				render();

			if(System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
			}
		}
		stop();
	}

	public synchronized void start() {
		thread = new Thread(this);
		thread.start();
		running = true;
	}

	public synchronized void stop() {
		try {
			thread.join();
			running = false;
		} catch(InterruptedException e) {}
	}

	public static void main(String[] args) {
		new GameMain();	
	}

}
