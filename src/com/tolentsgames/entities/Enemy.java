package com.tolentsgames.entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.tolentsgames.main.Game;
import com.tolentsgames.main.Sound;
import com.tolentsgames.world.AStar;
import com.tolentsgames.world.Camera;
import com.tolentsgames.world.Vector2i;
import com.tolentsgames.world.World;

public class Enemy extends Entity {
	
	public double speed = 0.4;
	
	private int frames = 0, maxFrames = 10, index = 0, maxIndex = 1;
	
	private BufferedImage[] sprites;
	
	private int life = 10;
	
	private boolean isDamaged = false;
	private int damageFrames = 10, damageCurrent = 0;

	public Enemy(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, null);
		sprites = new BufferedImage[2];
		sprites[0] = Game.spritesheet.getSprite(112, 16, 16, 16);
		sprites[1] = Game.spritesheet.getSprite(128, 16, 16, 16);
	}
	
	public void tick() {
	//	if(Game.rand.nextInt(100) < 50) {
/*		if(this.calculateDistance(this.getX(), this.getY(), Game.player.getX(), Game.player.getY()) < 40) {
			if(isColiddingWithPlayer() == false) {
				if((int)x < Game.player.getX() && World.isFree((int)(x+speed), this.getY())
						&& !isColidding((int)(x+speed), this.getY())) {
					x += speed;
				}
				else if((int)x > Game.player.getX() && World.isFree((int)(x-speed), this.getY())
						&& !isColidding((int)(x-speed), this.getY())) {
					x -= speed;
				}
				
				if((int)y < Game.player.getY() && World.isFree(this.getX(), (int)(y+speed))
						&& !isColidding(this.getX(), (int)(y+speed))) {
					y += speed;
				}
				else if((int)y > Game.player.getY() && World.isFree(this.getX(), (int)(y-speed))
						&& !isColidding(this.getX(),(int)(y-speed))) {
					y -= speed;
				}
		//	} este if � para fazer a velocidade dos inimigos rand�mica est� desabilitado
			} else {
				// Colidindo Enemy com Player
				if(Game.rand.nextInt(100) < 10) {
					Sound.hurtEffect.play(Sound.effectsVolume);
					Game.player.life -= Game.rand.nextInt(3);
					Game.player.isDamaged = true;
				}
			}
			
			frames++;
			if(frames == maxFrames) {
				frames = 0;
				index++;
				if(index > maxIndex)
					index = 0;
			}
						
		} else {
			//Aqui � o espa�o para executar quando os inimigos est�o distantes e n�o est�o indo atras do player
			} */
		depth = 0;
		if(!isColiddingWithPlayer()) {
			if(path == null || path.size() == 0) {
				Vector2i start = new Vector2i((int)(x/16),(int)(y/16));
				Vector2i end = new Vector2i((int)(Game.player.x/16),(int)(Game.player.y/16));
				path = AStar.findPath(Game.world, start, end);
			}	
		} else {
			// Colidindo Enemy com Player
			if(Game.rand.nextInt(100) < 5) {
				Sound.hurtEffect.play(Sound.effectsVolume);
				Game.player.life -= Game.rand.nextInt(3);
				Game.player.isDamaged = true;
			}
		}
		
		if(Game.rand.nextInt(100) < 50)
			followPath(path);
		if(Game.rand.nextInt(100) < 10) {
			Vector2i start = new Vector2i((int)(x/16),(int)(y/16));
			Vector2i end = new Vector2i((int)(Game.player.x/16),(int)(Game.player.y/16));
			path = AStar.findPath(Game.world, start, end);
		}
		frames++;
		if(frames == maxFrames) {
			frames = 0;
			index++;
			if(index > maxIndex)
				index = 0;
		}
		
		collidingBullet();
		
		if(life <= 0) {
			destroySelf();
			return;
		}
		
		if(isDamaged) {
			this.damageCurrent++;
			if(this.damageCurrent == this.damageFrames) {
				this.damageCurrent = 0;
				this.isDamaged = false;
			}
		}
			
	}
	
	public void destroySelf() {
		Game.enemies.remove(this);
		Game.entities.remove(this);
	}
	
	public void collidingBullet() {
		for(int i = 0; i < Game.bullets.size(); i++) {
			Entity e = Game.bullets.get(i);
			if( e instanceof BulletShoot) {
				if(Entity.isColidding(this, e)) {
					isDamaged = true;
					life--;
					Sound.hurtEffectEnemy.play(Sound.effectsVolume);
					Game.bullets.remove(i);
					World.generateParticles(50, this.getX(), this.getY(), 0);
					return;
				}
			}
		}
	}
	
	public boolean isColiddingWithPlayer() {
		Rectangle enemyCurrent = new Rectangle(this.getX(), this.getY(), World.TILE_SIZE, World.TILE_SIZE);
		Rectangle player = new Rectangle(Game.player.getX(), Game.player.getY(), World.TILE_SIZE, World.TILE_SIZE);
		return enemyCurrent.intersects(player);
	}
	
	public void render(Graphics g) {
		if(!isDamaged)
			g.drawImage(sprites[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
		else
			g.drawImage(Entity.ENEMY_FEEDBACK, this.getX() - Camera.x, this.getY() - Camera.y, null);
	}

}
