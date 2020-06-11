import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class SaveButton extends GameObject {
	private MouseInput mouse;
	private int mouseX, mouseY;

	private boolean mouseHover = false;

	public static int width = 120, height = 60;

	private long pressCooldown = 0L;

	public SaveButton(int x, int y, MouseInput mouse) {
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
						System.out.println("save");
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
		g.setColor(Color.DARK_GRAY);
		g.fillRoundRect(x, y, width, height, 20, 20);

		if (!mouseHover) {
			g.setColor(Color.black);
			g.fillRoundRect(x + 5, y + 5, width - 10, height - 10, 10, 10);

			g.setColor(Color.DARK_GRAY);
			g.setFont(new Font("", Font.BOLD, 35));
			g.drawString("Save", x + 18, y + 40);
		}
		else {
			g.setColor(Color.black);
			g.fillRoundRect(x + 5, y + 5, width - 10, height - 10, 10, 10);

			g.setColor(Color.DARK_GRAY);
			g.setFont(new Font("", Font.BOLD, 35));
			g.drawString("Save", x + 18, y + 40);
		}
	}
}
