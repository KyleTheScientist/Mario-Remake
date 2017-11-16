package entities;

import java.awt.Image;

import apples.AudioLoader;
import apples.ImageLoader;

public class Player extends Entity {

	private static int numLives;
	private boolean isOnGround, hasJumped, has2Jumped, isDead = false, isSuper = false, wasDamaged = false,
			canMove = true;
	private static Image plyRight1 = ImageLoader.marioRight1Image, plyRight2 = ImageLoader.marioRight2Image;
	private static Image plyLeft1 = ImageLoader.marioLeft1Image, plyLeft2 = ImageLoader.marioLeft2Image;

	private static Image sRight1 = ImageLoader.sMarioRight1Image, sRight2 = ImageLoader.sMarioRight2Image;
	private static Image sLeft1 = ImageLoader.sMarioLeft1Image, sLeft2 = ImageLoader.sMarioLeft2Image;
	private int coins = 0;
	private long damagedTime, jumpTime = 0;

	public Player(double x, double y, double width, double height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.isPlayer = true;
		this.image = ImageLoader.marioStillImage;
	}

	public Player(double x, double y, double width, double height, boolean isSuper) {
		this(x, y, width, height);
		this.isSuper = isSuper;
		this.image = ImageLoader.marioStillImage;
	}

	public void setIsOnGround(boolean isOnGround) {
		if (isOnGround) {
			hasJumped = false;
			has2Jumped = false;
		}
		this.isOnGround = isOnGround;
	}

	public void setSuper(boolean isSuper) {
		if (isSuper && !this.isSuper) {
			this.setY(y - width);
			this.setHeight(height * 2);
			this.image = ImageLoader.sMarioStillImage;
			this.setDamaged(true);
			AudioLoader.play("res/sound/powerup.wav");
		} else if (!isSuper && this.isSuper) {
			this.setY(y + width / 2);
			this.setHeight(height / 2);
			this.image = ImageLoader.marioStillImage;
			this.setDamaged(true);
			AudioLoader.play("res/sound/powerdown.wav");

		} else if (this.isSuper && isSuper) {
			AudioLoader.play("res/sound/getCoin.wav");
		}
		this.isSuper = isSuper;
	}

	public int getNumLives() {
		return numLives;
	}

	public static void setNumLives(int newNumLives) {
		if (numLives >= 0) {
			numLives = newNumLives;
		}else{
			System.out.println("Invalid numLives");
		}
	}

	public boolean canMove() {
		return this.canMove;
	}

	public void setCanMove(boolean canMove) {
		this.canMove = canMove;
	}

	public boolean wasDamaged() {
		return wasDamaged;
	}

	public void setDamaged(boolean wasDamaged) {
		if (wasDamaged) {
			this.damagedTime = System.currentTimeMillis();
		}
		this.wasDamaged = wasDamaged;
	}

	public long getDamagedTime() {
		return damagedTime;
	}
	
	public long getJumpTime() {
		return jumpTime;
	}
	
	public void setJumpTime(long jumpTime) {
		this.jumpTime = jumpTime;
	}


	public void setWidth(double width) {
		this.width = width;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public boolean isSuper() {
		return isSuper;
	}

	public void kill() {
		isDead = true;
		numLives--;
		this.image = ImageLoader.marioDeadImage;
	}

	public boolean isDead() {
		return isDead;
	}

	public int getCoins() {
		return coins;
	}

	public void addCoin(int numCoins) {
		this.coins += numCoins;
	}

	public void setCoins(int coins) {
		this.coins = coins;
	}

	public void setSize(double width, double height) {
		this.width = width;
		this.height = height;
	}

	public boolean isOnGround() {
		return isOnGround;
	}

	public void setHasJumped(boolean hasJumped) {
		this.hasJumped = hasJumped;
	}

	public void setHas2Jumped(boolean has2Jumped) {
		this.has2Jumped = has2Jumped;
	}

	public boolean has2Jumped() {
		return has2Jumped;
	}

	public boolean hasJumped() {
		return hasJumped;
	}

	public Player() {
		this(0, 0, 10, 10);
	}

	public void invertVelX() {
		setVelX(-getVelX());
	}

	public void invertVelY() {
		setVelY(-getVelY());
	}

	public void addVelX(double x) {
		vel[0] += x;
	}

	public void addVelY(double y) {
		vel[1] += y;

	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	@Override
	public double nextPosX() {
		return x + vel[0];
	}

	@Override
	public double nextPosY() {
		return y + vel[1];
	}

	@Override
	public void move(double x, double y) {
		this.x += x;
		this.y += y;
	}

	public void setPos(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double[] getVel() {
		return vel;
	}

	public double getVelX() {
		return vel[0];
	}

	public double getVelY() {
		return vel[1];
	}

	public void setVelX(double x) {
		this.vel[0] = x;
	}

	public void setVelY(double y) {
		this.vel[1] = y;
	}

	public void setVel(double[] vel) {
		this.vel = vel;
	}

	public void setVel(double x, double y) {
		this.vel[0] = x;
		this.vel[1] = y;
	}

	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

	public void toggleRightImage() {
		if (canMove) {
			if (!isSuper) {
				if (image == plyRight1) {
					setImage(plyRight2);
				} else {
					setImage(plyRight1);
				}
			} else if (isSuper) {
				if (image == sRight1) {
					setImage(sRight2);
				} else {
					setImage(sRight1);
				}
			}
		}
	}

	@Override
	public void setImage(Image image) {
		this.image = image;
	}

	public void toggleLeftImage() {
		if (canMove) {
			if (!isSuper) {
				if (image == plyLeft1) {
					setImage(plyLeft2);
				} else {
					setImage(plyLeft1);
				}
			} else if (isSuper) {
				if (image == sLeft1) {
					setImage(sLeft2);
				} else {
					setImage(sLeft1);
				}
			}
		}
	}

}
