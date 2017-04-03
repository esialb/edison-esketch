package org.esialb.edison.esketch;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.esialb.edison.sfo.I2cOled;
import org.esialb.edison.sfo.Multiplexer;
import org.esialb.edison.sfo.OledImage;
import org.esialb.edison.sfo.SFOled;
import org.esialb.edison.sfo.TextImages;

import mraa.I2c;
import mraa.I2cMode;

public class Main {
	private static CompoundImage oleds;
	private static Graphics2D oledsGraphics;
	private static OledImage sf;
	private static OledImage sf2;
	private static Graphics2D sfGraphics;
	private static Graphics2D sf2Graphics;
	private static Color penColor;
	private static BufferedImage canvas;
	private static Graphics2D canvasGraphics;
	private static boolean penDown;
	private static boolean showPen;
	
	public static void main(String[] args) throws Exception {
		I2c i2c = new I2c(1);
		i2c.frequency(I2cMode.I2C_FAST);

		
		oleds = new CompoundImage(i2c);
		oledsGraphics = oleds.createGraphics();
		oledsGraphics.setColor(Color.BLACK);
		oledsGraphics.fillRect(0, 0, oleds.getWidth(), oleds.getHeight());
		oleds.paint();
		
		sf = SFOled.createImage();
		sfGraphics = sf.createGraphics();
		sfGraphics.setFont(TextImages.FONT);
		
		I2cOled sf2Oled = new I2cOled(i2c, new Multiplexer(i2c, (short) 0x70).selector(2));
		sf2Oled.begin();
		sf2 = sf2Oled.createImage();
		sf2Graphics = sf2.createGraphics();
		sf2Graphics.setFont(TextImages.FONT.deriveFont(14f));

		penColor = Color.WHITE;
		canvas = new BufferedImage(oleds.getWidth(), oleds.getHeight(), BufferedImage.TYPE_INT_RGB);
		canvasGraphics = canvas.createGraphics();
		penDown = false;
		showPen = true;
		
		paintSf();
		
		int x = 64;
		int y = 32;
		
		int move = 1;
		
		sf.paint();
		for(;;) {
			oledsGraphics.drawImage(canvas, 0, 0, null);
			if(showPen) {
				oledsGraphics.setColor(Color.BLACK);
				oledsGraphics.fillOval(x-1, y-1, 3, 3);
				oledsGraphics.setColor(Color.WHITE);
				oledsGraphics.drawOval(x-2, y-2, 5, 5);
			}
			oleds.paint();
			
			input: while(true) {
				int oy = y, ox = x;
				boolean opd = penDown, osp = showPen;
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
					while(SFOled.isAPressed()) {
						if(SFOled.isBPressed()) {
							while(SFOled.isAPressed() || SFOled.isBPressed())
								Thread.sleep(50);
							canvasGraphics.setColor(penColor);
							canvasGraphics.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
							break input;
						}
						Thread.sleep(50);
					}
					penDown = !penDown;
					paintSf();
				}
				if(SFOled.isBPressed()) {
					while(SFOled.isBPressed()) {
						if(SFOled.isAPressed()) {
							while(SFOled.isAPressed() || SFOled.isBPressed())
								Thread.sleep(50);
							canvasGraphics.setColor(penColor);
							canvasGraphics.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
							break input;
						}
						Thread.sleep(50);
					}
					penColor = (penColor == Color.WHITE) ? Color.BLACK : Color.WHITE;
					paintSf();
				}
				if(SFOled.isSelectPressed()) {
					while(SFOled.isSelectPressed())
						Thread.sleep(50);
					showPen = !showPen;
				}
				if(oy != y || ox != x || opd != penDown || opc != penColor || osp != showPen) {
					if(!penDown && (oy != y || ox != x)) {
						move = Math.min(10, move + 1);
					} else
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
	
	private static void paintSf() {
		sfGraphics.setColor(Color.BLACK);
		sfGraphics.fillRect(0, 0, sf.getWidth(), sf.getHeight());
		sfGraphics.setColor(Color.WHITE);
		sfGraphics.drawString(penColor == Color.WHITE ? "WHITE" : "BLACK", 0, sf.getHeight() - sfGraphics.getFontMetrics().getDescent() - 1);
		sfGraphics.drawString(penDown ? "DOWN" : "UP", 0, sfGraphics.getFontMetrics().getAscent());
		sf.paint();
		
		sf2Graphics.setColor(Color.BLACK);
		sf2Graphics.fillRect(0, 0, sf2.getWidth(), sf2.getHeight());
		sf2Graphics.setColor(Color.WHITE);
		sf2Graphics.drawString(penColor == Color.WHITE ? "Color: WHITE" : "Color: BLACK", 0, sf2.getHeight() - sf2Graphics.getFontMetrics().getDescent() - 1);
		sf2Graphics.drawString(penDown ? "Pen: DOWN" : "Pen: UP", 0, sf2Graphics.getFontMetrics().getAscent());
		sf2.paint();
	}
}
