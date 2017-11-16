package entities;

import java.util.Random;

import apples.AudioLoader;
import apples.ImageLoader;
import apples.RandomInBounds;

public class Tile extends Entity {

	private static Tile[] tiles = new Tile[0];
	private boolean isSolid, hasBeenSeen = false, isBumpTile;
	private Type type;
	private Item item = null;
	private long spawnTime = System.currentTimeMillis();
	private Tile parentTile;
	private boolean isMovingRight;
	private boolean isMovingTile = false;
	private int numItems = 0;

	public Tile(double x, double y, double width, double height, Type type) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.setType(type);

		Tile[] newTiles = new Tile[tiles.length + 1];
		for (int i = 0; i < tiles.length; i++) {
			newTiles[i] = tiles[i];
		}
		newTiles[tiles.length] = this;
		tiles = newTiles;
	}

	public Tile(double x, double y, double width, double height, Type type, boolean isBumpTile) {
		this(x, y, width, height, type);
		this.isBumpTile = isBumpTile;
	}

	public void setType(Type type) {
		this.type = type;

		switch (type) {
		case BRICK:
			this.image = ImageLoader.brickImage;
			this.isSolid = true;
			break;
		case GROUND:
			this.image = ImageLoader.groundImage;
			this.isSolid = true;
			break;
		case QBLOCK:
			this.image = ImageLoader.qBlockImage;
			this.isSolid = true;
			break;
		case EBLOCK:
			this.image = ImageLoader.eBlockImage;
			this.isSolid = true;
			break;
		case STAIR:
			this.image = ImageLoader.stairImage;
			this.isSolid = true;
			break;
		case PIPE:
			this.image = ImageLoader.pipeImage;
			this.isSolid = true;
			break;
		case INVISIBLE:
			this.image = ImageLoader.blankImage;
			this.isSolid = false;
			break;
		case COIN:
			this.image = ImageLoader.coinImage;
			this.isSolid = false;
			break;
		case BRICK_BROKEN:
			randomizeParticle();
			this.isSolid = false;
			break;
		case MARIO_DEAD:
			this.image = ImageLoader.marioDeadImage;
			this.isSolid = false;
			break;
		case GOOMBA_CRUSHED:
			this.image = ImageLoader.goombaCrushedImage;
			this.isSolid = false;
			break;
		case GOOMBA_BUMPED:
			this.image = ImageLoader.goombaBumpedImage;
			this.isMovingTile = true;
			this.isSolid = false;
			break;
		case CLOUD:
			this.image = ImageLoader.cloudImage;
			this.isSolid = false;
			break;
		case HILL:
			this.image = ImageLoader.hillImage;
			this.isSolid = false;
			break;
		case BUSH:
			this.image = ImageLoader.bushImage;
			this.isSolid = false;
			break;
		case GOOMBA:
			this.image = ImageLoader.goombaImage;
			this.isSolid = true;
			this.isMovingRight = false;
			this.isMovingTile = true;
			this.setVelX(-1);
			break;
		case MUSHROOM:
			this.image = ImageLoader.mushroomImage;
			this.isSolid = false;
			this.isMovingRight = true;
			this.isMovingTile = true;
			this.setVelX(-1);
			break;
		case MUSHROOM_LIFE:
			this.image = ImageLoader.mushroomLifeImage;
			this.isSolid = false;
			this.isMovingRight = true;
			this.isMovingTile = true;
			this.setVelX(-1);
			break;
		default:
			this.image = ImageLoader.groundImage;
			this.isSolid = true;
			break;
		}

	}

	public boolean isMovingRight() {
		return isMovingRight;
	}

	public void setMovingRight(boolean isMovingRight) {
		this.isMovingRight = isMovingRight;
	}

	public Item getItem() {
		return item;
	}

	public int getNumItems() {
		return this.numItems;
	}

	public void setItem(Item item) {
		this.item = item;
		this.numItems = 1;
	}

	public void setNumItems(int numItems) {
		if (numItems >= 0)
			this.numItems = numItems;
		else
			System.out.println("invalid numItems");
	}

	public void setItem(Item item, int numItems) {
		this.item = item;
		this.numItems = numItems;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public boolean isBumpTile() {
		return isBumpTile;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	public Tile getParentTile() {
		return parentTile;
	}

	public void setParentTile(Tile parentTile) {
		this.parentTile = parentTile;
	}

	public boolean isMovingTile() {
		return isMovingTile;
	}

	public void bump() {
		AudioLoader.play("res/sound/bump.wav");
		Tile bumpTile = null;
		this.setVisible(false);
		if (this.type == Type.GOOMBA) {
			bumpTile = new Tile(this.x, this.y, this.width, this.height, Type.GOOMBA_BUMPED, true);
			bumpTile.setVelY(-10);
			bumpTile.setVelX(RandomInBounds.nextInt(-5, 5));
			bumpTile.setSolid(false);
			bumpTile.isMovingTile = true;
		} else {
			bumpTile = new Tile(this.x, this.y, this.width, this.height, this.type, true);
			bumpTile.setVelY(-5);
			bumpTile.setSolid(false);
		}
		bumpTile.setParentTile(this);
	}

	public void bump(boolean blockWasBroken) {
		Tile bumpTile = new Tile(this.x, this.y, this.width, this.height, this.type, true);
		bumpTile.setVisible(false);
		bumpTile.setVelY(-5);
		bumpTile.setSolid(false);
	}

	public void setHasBeenSeen(boolean hasBeenSeen) {
		this.hasBeenSeen = hasBeenSeen;
	}

	public boolean hasBeenSeen() {
		return hasBeenSeen;
	}

	public void toggleGoomba() {
		if (this.image == ImageLoader.goombaImage) {
			this.image = ImageLoader.goomba2Image;
		} else {
			this.image = ImageLoader.goombaImage;
		}
	}

	public enum Item {
		MUSHROOM, LIFE, FIRE_FLOWER, STAR, COIN
	}

	public enum Type {
		EBLOCK, QBLOCK, GROUND, BRICK, STAIR, PIPE, BRICK_BROKEN, 
		CLOUD, HILL, COIN, MARIO_DEAD, GOOMBA, GOOMBA_CRUSHED, 
		GOOMBA_BUMPED, BUSH, MUSHROOM, MUSHROOM_LIFE, INVISIBLE
	}

	private void randomizeParticle() {
		Random r = new Random();
		this.image = ImageLoader.load("res/brickBroken/" + (r.nextInt(5 - 1) + 1) + ".png");
	}

	public long getSpawnTime() {
		return spawnTime;
	}

	public void setSpawnTime(long spawnTime) {
		this.spawnTime = spawnTime;
	}

	public static void reset() {
		tiles = new Tile[0];
	}

	public Tile(double x, double y, double width, double height) {
		this(x, y, width, height, Type.BRICK);
	}

	public void setSolid(boolean isSolid) {
		this.isSolid = isSolid;
	}

	public boolean exists() {
		return exists;
	}

	public void setExists(boolean exists) {
		if (!exists) {
			this.isSolid = false;
			this.isVisible = false;
		}
		this.exists = exists;
	}

	public void destroy() {
		this.setExists(false);
	}

	public Type getType() {
		return type;
	}

	public double nextPosX() {
		return x + vel[0];
	}

	@Override
	public double nextPosY() {
		return y + vel[1];
	}

	public boolean willContain(Entity e) {
		// bottom edge
		for (double i = 2; i < 8; i++) {
			if (e.nextPosX() + e.getWidth() * (i / 10) >= this.nextPosX()
					&& e.nextPosX() + e.getWidth() * (i / 10) <= nextPosX() + width) {
				if (e.nextPosY() + e.getHeight() >= nextPosY() && e.nextPosY() + e.getHeight() <= nextPosY() + height) {
					return true;
				}
			}
		}

		// vertical scan
		for (double i = 2; i < 8; i++) {
			if (e.nextPosX() + e.getWidth() * (i / 10) >= nextPosX()
					&& e.nextPosX() + e.getWidth() * (i / 10) <= nextPosX() + width) {
				for (double j = 0; j < 10; j++) {
					if (e.nextPosY() + e.getHeight() * (i / 10) >= nextPosY()
							&& e.nextPosY() + e.getHeight() * (i / 10) <= nextPosY() + height) {
						return true;
					}
				}
			}
		}

		// top edge
		for (double i = 2; i < 8; i++) {
			if (e.nextPosX() + e.getWidth() * i / 10 >= nextPosX()
					&& e.nextPosX() + e.getWidth() * i / 10 <= nextPosX() + width) {
				if (e.nextPosY() >= nextPosY() && e.nextPosY() <= nextPosY() + height) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean willContainHorizontal(Entity e) {
		// right edge
		for (double i = 0; i < 10; i++) {
			if (e.nextPosX() + e.getWidth() >= nextPosX() && e.nextPosX() + e.getWidth() <= nextPosX() + width) {
				for (double j = 0; j < 10; j++) {
					if (e.nextPosY() + e.getHeight() * (i / 10) >= nextPosY()
							&& e.nextPosY() + e.getHeight() * (i / 10) <= nextPosY() + height) {
						return true;
					}
				}
			}
		}

		// left edge
		for (double i = 0; i < 10; i++) {
			if (e.nextPosX() >= nextPosX() && e.nextPosX() <= nextPosX() + width) {
				for (double j = 0; j < 10; j++) {
					if (e.nextPosY() + e.getHeight() * (i / 10) >= nextPosY()
							&& e.nextPosY() + e.getHeight() * (i / 10) <= nextPosY() + height) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean contains(double x, double y) {
		if (x >= this.x && x <= this.x + width) {
			if (y >= this.y && y <= this.y + height) {
				return true;
			}
		}
		return false;
	}

	public static Tile[] getTiles() {
		return tiles;
	}

	public boolean isSolid() {
		return isSolid;
	}

	public void setVelX(double x) {
		this.vel[0] = x;
	}

	public void setVelY(double y) {
		this.vel[1] = y;
	}

	public double getVelX() {
		return vel[0];
	}

	public double getVelY() {
		return vel[1];
	}

	public void setPos(double x, double y) {
		this.x = x;
		this.y = y;
	}
}
