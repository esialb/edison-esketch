package org.esialb.edison.esketch;

import java.awt.image.BufferedImage;

import org.esialb.edison.sfo.I2COled;
import org.esialb.edison.sfo.Multiplexer;
import org.esialb.edison.sfo.OledImage;

import mraa.I2c;
import mraa.I2cMode;

public class CompoundImage extends BufferedImage {
	private static final CompoundImage instance = new CompoundImage();
	public static CompoundImage get() {
		return instance;
	}
	
	
	protected OledImage ul;
	protected OledImage ur;
	protected OledImage ll;
	protected OledImage lr;
	
	private CompoundImage() {
		super(256, 128, BufferedImage.TYPE_INT_RGB);
		
		I2c i2c = new I2c(1);
		i2c.frequency(I2cMode.I2C_HIGH);
		Multiplexer mx = new Multiplexer(i2c, (short) 0x70);
		
		I2COled oled;
		
		oled = new I2COled(i2c, mx.selector(1));
		oled.begin();
		ul = oled.createImage();
		ul.paint();
		
		oled = new I2COled(i2c, mx.selector(0));
		oled.begin();
		ur = oled.createImage();
		ur.paint();
		
		oled = new I2COled(i2c, mx.selector(3));
		oled.begin();
		ll = oled.createImage();
		ll.paint();
		
		oled = new I2COled(i2c, mx.selector(4));
		oled.begin();
		lr = oled.createImage();
		lr.paint();
	}

	public void paint() {
		ul.createGraphics().drawImage(this, 0, 0, null);
		ur.createGraphics().drawImage(this, -128, 0, null);
		ll.createGraphics().drawImage(this, 0, -64, null);
		lr.createGraphics().drawImage(this, -128, -64, null);
		
		if(ul.shouldPaint())
			ul.paint();
		if(ur.shouldPaint())
			ur.paint();
		if(ll.shouldPaint())
			ll.paint();
		if(lr.shouldPaint())
			lr.paint();
	}
	
}
