import java.awt.Color;
import java.awt.Graphics;

public class Cell extends GameObject {

	private int size;
	private boolean alive; 
	private int mouseX, mouseY;
	private MouseInput mouse;

	private long pressCooldown = 0L;

	public Cell(int x, int y, int size, boolean alive, MouseInput mouse) {
		super(x, y);
		this.size = size;
		this.alive = alive;
		this.mouse = mouse;
		this.mouseX = mouse.getX();
		this.mouseY = mouse.getY();
	}

	public void tick() {
		if (mouse.getPressed()) {
			long currentTime = System.currentTimeMillis();
			if (currentTime - pressCooldown >= 200) {
				pressCooldown = currentTime;
				this.mouseX = mouse.getX();
				this.mouseY = mouse.getY();

				if (x <= mouseX && x + size >= mouseX)
					if (y <= mouseY && y + size >= mouseY) {
						GameMain.paused = true;
						alive = !alive;
					}
			}
		}
	}

	public void render(Graphics g) {
		if (!alive)
			return;
		
		g.setColor(new Color(77, 254, 0));
		g.fillRect(x, y, size, size);
	}

	public void setAlive(boolean alive) { this.alive = alive; }
	public boolean isAlive() { return alive; }

}
