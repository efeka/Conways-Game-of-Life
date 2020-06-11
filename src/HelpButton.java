import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class HelpButton extends GameObject {

	private MouseInput mouse;
	private int mouseX, mouseY;

	public static boolean mouseHover = false;

	public static int width = 35, height = 35;
	public static int helpX, helpY;

	public HelpButton(int x, int y, MouseInput mouse) {
		super(x, y);
		this.mouse = mouse;
		mouseX = mouse.getX();
		mouseY = mouse.getY();
		helpX = x;
		helpY = y;
	}

	public void tick() {
		mouseX = mouse.getX();
		mouseY = mouse.getY();

		if (x <= mouseX && x + width >= mouseX) {
			if (y + 15 <= mouseY && y + 15 + height >= mouseY)
				mouseHover = true;
			else
				mouseHover = false;
		}
		else
			mouseHover = false;

	}

	public void render(Graphics g) {

		if(!mouseHover) {
			g.setColor(new Color(66*6/5, 129*6/5, 44*6/5));
			g.fillOval(x + 8, y + 17, width - 4, height - 4);

			g.setColor(Color.black);
			g.fillOval(x + 11, y + 20, width - 10, height - 10);

			g.setColor(new Color(66*6/5, 129*6/5, 44*6/5));
			g.setFont(new Font("", Font.BOLD, 25));
			g.drawString("?", x + 17, y + 42);
		}
		else {
			g.setColor(new Color(66*8/5, 129*8/5, 44*8/5));
			int[] xPoints = {x + 23, x + 23, x + width - 40};
			int[] yPoints = {y + 42, y + 95, y + 95};
			g.fillPolygon(xPoints, yPoints, 3);

			g.setColor(new Color(66*8/5, 129*8/5, 44*8/5));
			g.fillOval(x + 8, y + 17, width - 4, height - 4);

			g.setColor(Color.black);
			g.fillOval(x + 11, y + 20, width - 10, height - 10);

			g.setColor(new Color(66*8/5, 129*8/5, 44*8/5));
			g.setFont(new Font("", Font.BOLD, 25));
			g.drawString("?", x + 17, y + 42);

			g.setColor(new Color(66*8/5, 129*8/5, 44*8/5));
			g.fillRoundRect(x - 250, y + 90, 300, 360, 20, 20);

			g.setColor(Color.black);
			g.fillRoundRect(x - 245, y + 95, 290, 350, 20, 20);

			g.setColor(new Color(66*8/5, 129*8/5, 44*8/5));
			g.setFont(new Font("", Font.BOLD, 30));
			g.drawString("Rules of Life", x - 185, y + 130);
			g.fillRect(x - 250, y + 140, 300, 5);

			g.setColor(new Color(66*6/5, 129*6/5, 44*6/5));
			g.setFont(new Font("", Font.BOLD, 20));
			g.drawString("1- Cells with less than 2 alive", x - 235, y + 180);
			g.drawString("neighbors die of loneliness.", x - 235, y + 205);

			g.drawString("2- Cells with 2 or 3 alive", x - 235, y + 240);
			g.drawString("neighbors survive.", x - 235, y + 265);
			
			g.drawString("3- Cells with 4 or more alive", x - 235, y + 300);
			g.drawString("neighbors die because of", x - 235, y + 325);
			g.drawString("overcrowding.", x - 235, y + 350);
			
			g.drawString("4- Dead cells with exactly 3", x - 235, y + 380);
			g.drawString("neighbors will come back to", x - 235, y + 405);
			g.drawString("life.", x - 235, y + 430);
		}
	}

}
