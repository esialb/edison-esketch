package org.esialb.edison.esketch;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.esialb.edison.sfo.OledImage;
import org.esialb.edison.sfo.SFOled;
import org.esialb.edison.sfo.TextImages;

public class Main {
	private static CompoundImage oleds;
	private static Graphics2D oledsGraphics;
	private static OledImage sf;
	private static Graphics2D sfGraphics;
	private static Color penColor;
	private static BufferedImage canvas;
	private static Graphics2D canvasGraphics;
	private static boolean penDown;
	
	public static void main(String[] args) throws Exception {
		oleds = CompoundImage.get();
		oledsGraphics = oleds.createGraphics();
		oledsGraphics.setColor(Color.BLACK);
		oledsGraphics.fillRect(0, 0, oleds.getWidth(), oleds.getHeight());
		oleds.paint();
		
		sf = SFOled.createImage();
		sfGraphics = sf.createGraphics();
		sfGraphics.setFont(TextImages.FONT);
		
		penColor = Color.WHITE;
		canvas = new BufferedImage(oleds.getWidth(), oleds.getHeight(), BufferedImage.TYPE_INT_RGB);
		canvasGraphics = canvas.createGraphics();
		penDown = false;
		
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
			
			input: while(true) {
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
						if(SFOled.isBPressed()) {
							while(SFOled.isAPressed() || SFOled.isBPressed())
								Thread.sleep(50);
							canvasGraphics.setColor(Color.WHITE);
							canvasGraphics.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
							break input;
						}
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
						if(SFOled.isAPressed()) {
							while(SFOled.isAPressed() || SFOled.isBPressed())
								Thread.sleep(50);
							canvasGraphics.setColor(Color.BLACK);
							canvasGraphics.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
							break input;
						}
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
