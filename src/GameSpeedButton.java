import java.awt.Color;
import java.awt.Graphics;

public class GameSpeedButton extends GameObject {

	private MouseInput mouse;
	private int mouseX, mouseY;
	
	private boolean mouseHover = false;

	public static int width = 120, height = 60;

	private long pressCooldown = 0L;

	private int state = 1;

	public GameSpeedButton(int x, int y, MouseInput mouse) {
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
						state++;
						state %= 3;
						if (state == 0)
							GameMain.gameSpeed = 700;
						else if (state == 1)
							GameMain.gameSpeed = 200;
						else if (state == 2)
							GameMain.gameSpeed = 50;
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
		if (!mouseHover)
			g.setColor(new Color(66, 129, 44));
		else
			g.setColor(new Color(66*8/5, 129*8/5, 44*8/5));
		g.fillRoundRect(x, y, width, height, 20, 20);

		g.setColor(Color.black);
		g.fillRoundRect(x + 5, y + 5, width - 10, height - 10, 10, 10);

		if (!mouseHover)
			g.setColor(new Color(66, 129, 44));
		else
			g.setColor(new Color(66*8/5, 129*8/5, 44*8/5));
		int[] xPoints = {x + 16, x + 16, x + width - 74};
		int[] yPoints = {y + 13, y + 45, y + (13 + 45) / 2};

		int[] xPoints1 = {x + 45, x + 45, x + width - 45};
		int[] yPoints1 = {y + 13, y + 45, y + (13 + 45) / 2};

		int[] xPoints2 = {x + 75, x + 75, x + width - 16};
		int[] yPoints2 = {y + 13, y + 45, y + (13 + 45) / 2};
		
		if (state == 0) { 
			g.fillPolygon(xPoints, yPoints, 3);
			g.drawPolygon(xPoints1, yPoints1, 3);
			g.drawPolygon(xPoints2, yPoints2, 3);
		}
		else if (state == 1) { 
			g.fillPolygon(xPoints, yPoints, 3);
			g.fillPolygon(xPoints1, yPoints1, 3);
			g.drawPolygon(xPoints2, yPoints2, 3);
		}
		else if (state == 2) { 
			g.fillPolygon(xPoints, yPoints, 3);
			g.fillPolygon(xPoints1, yPoints1, 3);
			g.fillPolygon(xPoints2, yPoints2, 3);
		}
		
		
	}

}
