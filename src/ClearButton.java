import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class ClearButton extends GameObject {

	private MouseInput mouse;
	private int mouseX, mouseY;

	public static int width = 120, height = 60;

	private long pressCooldown = 0L;

	public ClearButton(int x, int y, MouseInput mouse) {
		super(x, y);
		this.mouse = mouse;
		mouseX = mouse.getX();
		mouseY = mouse.getY();
	}

	public void tick() {
		mouseX = mouse.getX();
		mouseY = mouse.getY();

		if (x <= mouseX && x + width >= mouseX) {
			if (y <= mouseY && y + height >= mouseY) {
				if (mouse.getPressed()) {
					long currentTime = System.currentTimeMillis();
					if (currentTime - pressCooldown >= 200) {
						pressCooldown = currentTime;
						for (int i = 0; i < GameMain.grid.length; i++)
							for (int j = 0; j < GameMain.grid[i].length; j++) 	
								GameMain.grid[i][j].setAlive(false);
						GameMain.paused = true;
					}
				}
			}
		}

	}

	public void render(Graphics g) {
		g.setColor(new Color(66, 129, 44));
		g.fillRoundRect(x, y, width, height, 20, 20);

		g.setColor(Color.black);
		g.fillRoundRect(x + 5, y + 5, width - 10, height - 10, 10, 10);

		g.setColor(new Color(66*6/5, 129*6/5, 44*6/5));
		g.setFont(new Font("", Font.BOLD, 35));
		g.drawString("Clear", x + 15, y + 40);
	}

}
