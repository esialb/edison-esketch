package org.esialb.edison.esketch;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.esialb.edison.sfo.OledImage;
import org.esialb.edison.sfo.SFOled;
import org.esialb.edison.sfo.TextImages;

public class Main {
	public static void main(String[] args) throws Exception {
		CompoundImage oleds = CompoundImage.get();
		Graphics2D oledsGraphics = oleds.createGraphics();
		oledsGraphics.setColor(Color.BLACK);
		oledsGraphics.fillRect(0, 0, oleds.getWidth(), oleds.getHeight());
		oleds.paint();
		
		OledImage sf = SFOled.createImage();
		Graphics2D sfGraphics = sf.createGraphics();
		sfGraphics.setFont(TextImages.FONT);
		
		Color penColor = Color.WHITE;
		BufferedImage canvas = new BufferedImage(oleds.getWidth(), oleds.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D canvasGraphics = canvas.createGraphics();
		boolean penDown = false;
		
		sfGraphics.setColor(Color.BLACK);
		sfGraphics.fillRect(0, 0, 64, 48);
		sfGraphics.setColor(Color.WHITE);
		sfGraphics.drawString(penColor == Color.WHITE ? "WHITE" : "BLACK", 0, 8);
		sfGraphics.drawString(penDown ? "DOWN" : "UP", 0, 16);
		
		int x = 64;
		int y = 32;
		
		int move = 1;
		
		sf.paint();
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
				if(SFOled.isUpPressed() && y > move - 1)
					y-=move;
				if(SFOled.isDownPressed() && y < 128 - move)
					y+=move;
				if(SFOled.isLeftPressed() && x > move - 1)
					x-=move;
				if(SFOled.isRightPressed() && x < 256 - move)
					x+=move;
				if(SFOled.isAPressed()) {
					penDown = !penDown;
					while(SFOled.isAPressed()) {
						if(SFOled.isBPressed())
							System.exit(0);
						Thread.sleep(50);
					}
					sfGraphics.setColor(Color.BLACK);
					sfGraphics.fillRect(0, 0, 64, 48);
					sfGraphics.setColor(Color.WHITE);
					sfGraphics.drawString(penColor == Color.WHITE ? "WHITE" : "BLACK", 0, 8);
					sfGraphics.drawString(penDown ? "DOWN" : "UP", 0, 16);
					sf.paint();
				}
				if(SFOled.isBPressed()) {
					penColor = (penColor == Color.WHITE) ? Color.BLACK : Color.WHITE;
					while(SFOled.isBPressed()) {
						if(SFOled.isAPressed())
							System.exit(0);
						Thread.sleep(50);
					}
					sfGraphics.setColor(Color.BLACK);
					sfGraphics.fillRect(0, 0, 64, 48);
					sfGraphics.setColor(Color.WHITE);
					sfGraphics.drawString(penColor == Color.WHITE ? "WHITE" : "BLACK", 0, 8);
					sfGraphics.drawString(penDown ? "DOWN" : "UP", 0, 16);
					sf.paint();
				}
				if(oy != y || ox != x || opd != penDown || opc != penColor) {
					if(!penDown && move < 10)
						move++;
					if(penDown)
						move = 1;
					break;
				}
				move = 1;
				Thread.sleep(50);
			}
			
			if(penDown) {
				canvasGraphics.setColor(penColor);
				canvasGraphics.fillOval(x-1, y-1, 3, 3);
			}
		}
	}
}
