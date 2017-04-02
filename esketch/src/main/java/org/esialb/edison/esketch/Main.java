package org.esialb.edison.esketch;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.esialb.edison.sfo.SFOled;

public class Main {
	public static void main(String[] args) throws Exception {
		CompoundImage oleds = CompoundImage.get();
		Graphics2D oledsGraphics = oleds.createGraphics();
		oledsGraphics.setColor(Color.BLACK);
		oledsGraphics.fillRect(0, 0, oleds.getWidth(), oleds.getHeight());
		oleds.paint();
		
		Color penColor = Color.WHITE;
		BufferedImage canvas = new BufferedImage(oleds.getWidth(), oleds.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D canvasGraphics = canvas.createGraphics();
		boolean penDown = false;
		
		int x = 0;
		int y = 0;
		
		for(;;) {
			oledsGraphics.drawImage(canvas, 0, 0, null);
			oledsGraphics.setColor(Color.BLACK);
			oledsGraphics.fillOval(x-1, y-1, 3, 3);
			oledsGraphics.setColor(Color.WHITE);
			oledsGraphics.drawOval(x-2, y-2, 5, 5);
			oleds.paint();
			
			while(true) {
				int oy = y, ox = x;
				boolean opd = penDown;
				Color opc = penColor;
				if(SFOled.isUpPressed() && y > 0)
					y--;
				if(SFOled.isDownPressed() && y < 127)
					y++;
				if(SFOled.isLeftPressed() && x > 0)
					x--;
				if(SFOled.isRightPressed() && x < 255)
					x++;
				if(SFOled.isAPressed())
					penDown = !penDown;
				if(SFOled.isBPressed())
					penColor = (penColor == Color.WHITE) ? Color.BLACK : Color.WHITE;
				if(oy != y || ox != x || opd != penDown || opc != penColor)
					break;
				Thread.sleep(50);
			}
			
			if(penDown) {
				canvasGraphics.setColor(penColor);
				canvasGraphics.fillOval(x-1, y-1, 3, 3);
			}
		}
	}
}
