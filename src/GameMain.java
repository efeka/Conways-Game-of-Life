import java.awt.Canvas;
import java.awt.Color;
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

	public static boolean paused = true;
	public static int gameSpeed = 200;
	private long updateCooldown = 0L;

	private static int cellSize = 20;
	private static int borderSize = 1;
	private static int rows = 30, cols = 50;

	private static int width = (borderSize + cellSize) * cols + borderSize, height = (borderSize + cellSize) * rows + borderSize + 5 * (borderSize + cellSize);

	public static Cell[][] grid = new Cell[(width - borderSize) / (borderSize + cellSize)][(height - borderSize) / (borderSize + cellSize)];
	GameObject[] uiButtons = new GameObject[3]; 

	public GameMain() {
		requestFocus();
		new Window(width, height, "Game Of Life", this);

		MouseInput mouse = new MouseInput();
		addMouseListener(mouse);
		addMouseMotionListener(mouse);

		for (int i = 0; i < grid.length; i++)
			for (int j = 0; j < grid[i].length; j++)
				grid[i][j] = new Cell((borderSize + cellSize) * i + borderSize, 5 * (borderSize + cellSize) + (borderSize + cellSize) * j + borderSize, cellSize, false, mouse);

		uiButtons[0] = new PausePlayButton(width / 2 - PausePlayButton.width / 2, 8, mouse);
		uiButtons[1] = new ClearButton(width / 2 + 90, 22, mouse);
		uiButtons[2] = new GameSpeedButton(width / 2 - 210, 22, mouse);
	}

	private void tick() {
		//update each pixel
		for (int i = 0; i < grid.length; i++)
			for (int j = 0; j < grid[i].length; j++)
				if (grid[i][j] != null) 
					grid[i][j].tick();

		//update top buttons
		for (int i = 0; i < uiButtons.length; i++)
			if (uiButtons[i] != null)
				uiButtons[i].tick();

		//apply rules to cells
		if (!paused) {
			long currentTime = System.currentTimeMillis();
			if (currentTime - updateCooldown >= gameSpeed) {
				updateCooldown = currentTime;
				boolean[][] changes = new boolean[grid.length][grid[0].length];
				for (int i = 0; i < grid.length; i++) 
					for (int j = 0; j < grid[i].length; j++)  
						changes[i][j] = grid[i][j].isAlive();

				for (int i = 0; i < grid.length; i++) {
					for (int j = 0; j < grid[i].length; j++) {
						if (grid[i][j] != null) {
							int neighborCount = 0;
							//try catch is to avoid all the edge cases
							for (int k = -1; k < 2; k++) {
								for (int l = -1; l < 2; l++) {
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

		for (int i = 0; i < uiButtons.length; i++)
			if (uiButtons[i] != null)
				uiButtons[i].render(g);

		//---draw end---
		g.dispose();
		bs.show();
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
