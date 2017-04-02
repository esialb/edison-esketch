package org.esialb.edison.esketch;

import java.awt.Color;
import java.awt.Graphics2D;

public class Main {
	public static void main(String[] args) {
		CanvasImage i = CanvasImage.get();
		Graphics2D g = i.createGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, i.getWidth(), i.getHeight());
		g.setColor(Color.WHITE);
		g.fillRect(64, 32, 128, 64);
		i.paint();
	}
}
