import java.awt.Color;
import java.awt.Graphics;

public class PausePlayButton extends GameObject {

	private MouseInput mouse;
	private int mouseX, mouseY;

	public static int width = 100, height = 90;

	private long pressCooldown = 0L;

	public PausePlayButton(int x, int y, MouseInput mouse) {
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
						GameMain.paused = !GameMain.paused;
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

		if (!GameMain.paused) {
			g.setColor(new Color(66*6/5, 129*6/5, 44*6/5));
			g.fillRect(x + 25, y + 13, 20, 65);
			g.fillRect(x + width - 25 - 20, y + 13, 20, 65);
		}
		else {
			g.setColor(new Color(66*6/5, 129*6/5, 44*6/5));
			int[] xPoints = {x + 25, x + 25, x + width - 20};
			int[] yPoints = {y + 13, y + 78, y + (78 + 13) / 2};
			g.fillPolygon(xPoints, yPoints, 3);
		}
	}

}
