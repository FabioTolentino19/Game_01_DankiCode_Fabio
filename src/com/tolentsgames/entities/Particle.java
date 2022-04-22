package com.tolentsgames.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

import com.tolentsgames.main.Game;
import com.tolentsgames.world.Camera;

public class Particle extends Entity {
	
	public int lifeTime = 10;
	public int curLife = 0;
	
	public int spd = 2;
	public double dx = 0;
	public double dy = 0;
	public int partColor;

	public Particle(int x, int y, int width, int height, BufferedImage sprite, int pColor) {
		super(x, y, width, height, sprite);
		
		dx = new Random().nextGaussian();
		dy = new Random().nextGaussian();
		partColor = pColor;
		
	}
	
	public void tick() {
		x += dx*spd;
		y += dy*spd;
		curLife++;
		if(curLife == lifeTime) {
			Game.entities.remove(this);
		}
		
	}
	
	public void render(Graphics g) {
		
		switch(partColor) {
		
		case 0: g.setColor(Color.red);
				break;
				
		case 1: g.setColor(Color.gray);
				break;
		
		}
		g.fillRect(this.getX() - Camera.x, this.getY() - Camera.y, width, height);
	}

}
