import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class ClearButton extends GameObject {

	private MouseInput mouse;
	private int mouseX, mouseY;

	private boolean mouseHover = false;

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
							for (int j = 0; j < GameMain.grid[i].length; j++) {	
								GameMain.grid[i][j].setAlive(false);
								GameMain.neigCount[i][j] = 0;
							}
						GameMain.paused = true;
						mouseHover = false;
					}
				}
				else
					mouseHover = true;
			}
			else
				mouseHover = false;
		}
		else
			mouseHover = false;

	}

	public void render(Graphics g) {

		if(!mouseHover) {
			g.setColor(new Color(66, 129, 44));
			g.fillRoundRect(x, y, width, height, 20, 20);

			g.setColor(Color.black);
			g.fillRoundRect(x + 5, y + 5, width - 10, height - 10, 10, 10);

			g.setColor(new Color(66*6/5, 129*6/5, 44*6/5));
			g.setFont(new Font("", Font.BOLD, 35));
			g.drawString("Clear", x + 17, y + 42);
		}
		else {
			g.setColor(new Color(66*8/5, 129*8/5, 44*8/5));
			g.fillRoundRect(x, y, width, height, 20, 20);

			g.setColor(Color.black);
			g.fillRoundRect(x + 5, y + 5, width - 10, height - 10, 10, 10);

			g.setColor(new Color(66*8/5, 129*8/5, 44*8/5));
			g.setFont(new Font("", Font.BOLD, 35));
			g.drawString("Clear", x + 17, y + 42);
		}
	}

}
