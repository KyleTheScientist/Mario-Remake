package apples;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

import entities.Entity;
import entities.Player;
import entities.Tile;

public class Juego extends JPanel implements Runnable, KeyListener {
	private static final long serialVersionUID = 1L;

	private int blockHeight = 50, blockWidth = 50;
	private int width = 25 * 50, height = 14 * 50;
	private Dimension dim = new Dimension(width, height);

	private String title = "Player.java";
	private JFrame frame;
	private Key UP, DOWN, LEFT, RIGHT;
	private Player ply;
	private Tile[] floorTiles;
	private double gravity = .9;
	private double curGravity = gravity;
	private double jumpHeight = 20, speed = 10, walkSpeed = 100;
	private double curSpeed = speed;
	private Tile edgeMarker = new Tile(-1, -1, 1, 1);
	private Image plyImage, blockImage;
	Random r = new Random();
	Color bgc = new Color(0, 170, 255);
	private boolean cena = false, flappyMario = false, doubleJump = false, lowGravity = false;

	public Juego() {
		frame = new JFrame(title);
		frame.setResizable(false);
		frame.setSize(dim);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);

		this.setMinimumSize(dim);
		this.setMaximumSize(dim);
		this.setPreferredSize(dim);
		this.addKeyListener(this);
		this.setBackground(Color.BLACK);
		frame.add(this);
		frame.pack();

		this.requestFocusInWindow();
		LEFT = new Key();
		UP = new Key();
		RIGHT = new Key();
		DOWN = new Key();
		int tileSize = 20;

		long[][] mapArray = Level.imageToMap("/res/map.png");
		generateMapFromArray(mapArray);
		Player.setNumLives(3);

	}

	// PAINT METHOD
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;

		for (Entity e : Entity.getEnts()) {
			if (e.isPlayer()) {
				g2.setColor(Color.red);
				if (!ply.isDead()) {
					if (cena) {
						g2.drawImage(ImageLoader.cenaImage, e.getIntX(), e.getIntY(), e.getIntWidth(), e.getIntHeight(),
								this);
					} else {
						g2.drawImage(ply.getImage(), e.getIntX(), e.getIntY(), e.getIntWidth(), e.getIntHeight(), this);
					}
				}
			} else if (e.exists() && e.isVisible()) {
				g2.drawImage(e.getImage(), e.getIntX(), e.getIntY(), e.getIntWidth(), e.getIntHeight(), this);
			}

		}
		g2.setColor(Color.WHITE);
		g2.setFont(new Font("TimesNewRoman", 25, 25));
		g2.drawImage(ImageLoader.coinImage, width - 60 - blockWidth, 10, blockWidth - 10, blockHeight - 10, this);
		g2.drawImage(ImageLoader.marioStillImage, width - 60 - blockWidth, 10 + 50, blockWidth - 10, blockHeight - 10,
				this);

		if (ply != null) {
			g2.drawString(" x " + ply.getCoins(), width - 70, 40);
			g2.drawString(" x " + ply.getNumLives(), width - 70, 40 + 50);
			if (cena)
				g2.drawString("JOHN CENA", 10, 20);
			if (ply.isSuper())
				g2.drawString("Super Mario \tenabled", 10, 40);
			if (doubleJump)
				g2.drawString("Double Jump \tenabled", 10, 60);
			if (flappyMario)
				g2.drawString("\"Flappy Mario\" \tenabled", 10, 80);
			if (lowGravity)
				g2.drawString("\"Low Gravity\" \tenabled", 10, 100);
		}

	}

	@Override
	public void run() {

		int lastUpTimes = 0;
		long lastRunTime = System.currentTimeMillis();
		boolean facingRight = true;
		while (true) {

			long curTime = System.currentTimeMillis();

			if (!ply.isDead() && curTime - lastRunTime > walkSpeed + 50 && !ply.hasJumped()) {
				if (!ply.isSuper()) {
					ply.setImage(ImageLoader.marioStillImage);
				} else {
					ply.setImage(ImageLoader.sMarioStillImage);
				}
				lastRunTime = curTime;
			}
			if (ply.wasDamaged()) {
				if (curTime - ply.getDamagedTime() > 1000) {
					ply.setCanMove(true);
					curSpeed = speed;
				}

				if (curTime - ply.getDamagedTime() > 1500) {
					ply.setDamaged(false);
					for (Tile t : Tile.getTiles()) {
						if (t.getType() == Tile.Type.GOOMBA) {
							t.setSolid(true);
						}
					}
				} else {
					if (curTime - ply.getDamagedTime() < 250
							|| (curTime - ply.getDamagedTime() > 500 && curTime - ply.getDamagedTime() < 750)) {
						ply.setCanMove(false);
						curSpeed = 0;
						ply.setImage(ImageLoader.blankImage);
					} else if (curTime - ply.getDamagedTime() < 500 && curTime - ply.getDamagedTime() > 250
							|| curTime - ply.getDamagedTime() < 1000 && curTime - ply.getDamagedTime() > 750) {
						if (ply.isSuper()) {
							ply.setImage(ImageLoader.sMarioStillImage);
						} else {
							ply.setImage(ImageLoader.marioStillImage);
						}
					}
					for (Tile t : Tile.getTiles()) {
						if (t.getType() == Tile.Type.GOOMBA) {
							t.setSolid(false);
						}
					}
				}
			}

			boolean hasCollidedLeft = false, hitLeftEdge;
			Tile cTile = null;

			if (edgeMarker.getX() > 0) {
				hitLeftEdge = true;
			} else if (ply.getX() > width / 2) {
				hitLeftEdge = false;
			} else if (ply.getX() < width / 2) {
				hitLeftEdge = true;
			} else {
				hitLeftEdge = false;
			}

			if (!ply.isDead()) {
				// LEFT BUTTON--------------------
				if (LEFT.isPressed()) {
					facingRight = false;
					if (edgeMarker.getX() + curSpeed > 0) {
						hitLeftEdge = true;
					}
					for (Tile t : Tile.getTiles()) {
						for (double i = 1; i < 9; i++) {
							if (t.isSolid() && t.exists()) {
								if (t.contains(ply.getX() - curSpeed, ply.getY() + ply.getHeight() * (i / 10))
										&& !t.contains(ply.getX() + ply.getWidth() - curSpeed,
												ply.getY() + ply.getHeight() * (i / 10))) {
									hasCollidedLeft = true;
									cTile = t;
								}
							}
						}
					}

					if (cTile != null && cTile.getType() == Tile.Type.GOOMBA && !ply.isDead() && cTile.exists()) {
						if (!ply.wasDamaged())
							killPlayer();
					}

					// Left Edge
					if (hitLeftEdge && !hasCollidedLeft) {
						ply.setVelX(-curSpeed);
						moveEdgePoint();
						if (curTime - lastRunTime > walkSpeed && !ply.hasJumped()) {
							ply.toggleLeftImage();
							lastRunTime = curTime;
						}
					}
					if (hitLeftEdge && hasCollidedLeft) {
						ply.setVelX(0);
						ply.setX(cTile.getX() + cTile.getWidth());

					}

					// Non-Left Edge

					if (!hitLeftEdge && !hasCollidedLeft) {
						Tile[] ti = Tile.getTiles();
						for (int i = 0; i < ti.length; i++) {
							ti[i].move(curSpeed, 0);
							repaint();
						}
						if (curTime - lastRunTime > walkSpeed && !ply.hasJumped()) {
							ply.toggleLeftImage();
							lastRunTime = curTime;
						}
					}

					if (!hitLeftEdge && hasCollidedLeft) {
						double d = cTile.getX() + cTile.getWidth() - ply.getX();
						for (Tile t : Tile.getTiles()) {
							t.move(-d, 0);
							repaint();
						}
					}

				} else {
					ply.setVelX(0);
				}

				// RIGHT BUTTON--------------------
				boolean hasCollidedRight = false;
				cTile = null;
				if (RIGHT.isPressed()) {
					facingRight = true;
					if (ply.getX() > width / 2) {
						hitLeftEdge = false;
						ply.setX(width / 2);
					}
					for (Tile t : Tile.getTiles()) {
						for (double i = 1; i < 9; i++) {
							if (t.isSolid() && t.exists()) {
								if (t.contains(ply.getX() + ply.getWidth() + curSpeed,
										ply.getY() + ply.getHeight() * (i / 10))
										&& !t.contains(ply.getX() + curSpeed,
												ply.getY() + ply.getHeight() * (i / 10))) {
									hasCollidedRight = true;
									cTile = t;
								}
							}
						}
					}

					if (cTile != null && cTile.getType() == Tile.Type.GOOMBA && !ply.isDead() && cTile.exists()) {
						if (!ply.wasDamaged())
							killPlayer();
					}

					// Left Edge
					if (hitLeftEdge && !hasCollidedRight) {
						ply.setVelX(curSpeed);
						if (curTime - lastRunTime > walkSpeed && !ply.hasJumped()) {
							ply.toggleRightImage();
							lastRunTime = curTime;
						}
					}
					if (hitLeftEdge && hasCollidedRight) {
						ply.setVelX(0);
						ply.setX(cTile.getX() - ply.getWidth() - 1);
					}

					// Non-Left Edge
					if (!hitLeftEdge && !hasCollidedRight) {
						Tile[] ti = Tile.getTiles();
						for (int i = 0; i < ti.length; i++) {
							ti[i].move(-curSpeed, 0);
							repaint();
						}
						if (curTime - lastRunTime > walkSpeed && !ply.hasJumped()) {
							ply.toggleRightImage();
							lastRunTime = curTime;
						}
					}

					if (!hitLeftEdge && hasCollidedRight) {
						double d = ply.getX() + ply.getWidth() - cTile.getX();
						for (Tile t : Tile.getTiles()) {
							t.move(d, 0);
							repaint();
						}

					}

				}

				// Up Button
				if (UP.isPressed()) {
					if (ply.canMove()) {
						if (!ply.isSuper()) {
							if (facingRight) {
								ply.setImage(ImageLoader.marioJump1Image);
							} else {
								ply.setImage(ImageLoader.marioJump2Image);
							}
						} else {
							if (facingRight) {
								ply.setImage(ImageLoader.sMarioJump1Image);
							} else {
								ply.setImage(ImageLoader.sMarioJump2Image);
							}
						}

						int curUpTimes = UP.numTimesPressed;

						if (!ply.isOnGround()) {
							ply.setHasJumped(true);
						}
						if (!flappyMario) {
							if (!ply.hasJumped() && !ply.has2Jumped()) {
								ply.setVelY(-jumpHeight);
								ply.setHasJumped(true);
								lastUpTimes = curUpTimes;
								AudioLoader.play("res/sound/jumpSmall.wav");
							} else if (doubleJump && ply.hasJumped() && !ply.has2Jumped() && curUpTimes > lastUpTimes) {
								ply.setVelY(0);
								ply.setVelY(-jumpHeight);
								ply.setHas2Jumped(true);
								AudioLoader.play("res/sound/jumpSmall.wav");
							}
						} else {
							if (curUpTimes > lastUpTimes) {
								ply.setVelY(0);
								ply.addVelY(-jumpHeight);
								lastUpTimes = curUpTimes;
								AudioLoader.play("res/sound/jumpSmall.wav");
							}
						}
					}
				}

				if (!ply.isOnGround() && ply.canMove()) {
					ply.addVelY(curGravity);
				}
			}
			// Vertical Collisions

			boolean isOnGround = false;
			for (Tile t : Tile.getTiles()) {
				if (t.isSolid() || t.getType() == Tile.Type.INVISIBLE) {
					if (t.willContain(ply) && ply.getY() < t.getY() && !ply.isOnGround() && t.exists()) {
						if (t.getType() != Tile.Type.INVISIBLE) {
							ply.setPos(ply.getX(), t.getY() - ply.getHeight());
							ply.setVelY(0);
							isOnGround = true;
							if (t.getType() == Tile.Type.GOOMBA && t.exists()) {
								t.destroy();
								AudioLoader.play("res/sound/bump.wav");
								ply.setVelY(-15);
								new Tile(t.getX(), t.getY(), blockWidth, blockHeight, Tile.Type.GOOMBA_CRUSHED);
							}
						}
					} else if (t.willContain(ply) && ply.getVelY() < 0 && !ply.isOnGround() && t.exists()) {
						ply.setPos(ply.getX(), t.getY() + t.getHeight());
						ply.setVelY(0);
						if (t.getType() == Tile.Type.BRICK) {
							if (ply.isSuper() && t.getItem() == null) {
								t.bump(true);
								for (int i = 0; i < 5; i++) {
									Tile ti = new Tile(t.getX() + t.getWidth() / 2, t.getY() + t.getHeight() / 2, 25,
											25, Tile.Type.BRICK_BROKEN);
									ti.setVelX(RandomInBounds.nextInt(-5, 5));
									ti.setVelY(RandomInBounds.nextInt(-20, -10));
								}
								t.destroy();
								AudioLoader.play("res/sound/brickBreak.wav");
							} else {
								t.bump();
							}
						}
						// Player Bumps Item Tile
						if (t.getItem() != null) {
							if (t.getNumItems() > 0) {
								t.setNumItems(t.getNumItems() - 1);
								Tile ti;
								switch (t.getItem()) {
								case COIN:
									AudioLoader.play("res/sound/getCoin.wav");
									ti = new Tile(t.getX(), t.getY() - blockHeight - 20, blockWidth, blockHeight,
											Tile.Type.COIN);
									ti.setVelY(-15);
									ply.addCoin(1);
									break;
								case MUSHROOM:
									AudioLoader.play("res/sound/powerupAppears.wav");
									ti = new Tile(t.getX(), t.getY() - blockHeight - 20, blockWidth, blockHeight,
											Tile.Type.MUSHROOM);
									break;
								case LIFE:
									AudioLoader.play("res/sound/powerupAppears.wav");
									ti = new Tile(t.getX(), t.getY() - blockHeight - 20, blockWidth, blockHeight,
											Tile.Type.MUSHROOM_LIFE);
									break;
								default:

								}
								if (t.getNumItems() == 0) {
									if (t.getType() == Tile.Type.QBLOCK || t.getType() == Tile.Type.INVISIBLE)
										t.setType(Tile.Type.EBLOCK);
								}
							}

						}
						if (t.getType() != Tile.Type.GOOMBA && t.exists()) {
							t.bump();
						}
					}
				}
			}
			ply.setIsOnGround(isOnGround);

			// Moving Tiles
			for (Tile t : Tile.getTiles()) {
				if (t.getType() == Tile.Type.COIN) {
					t.setVelY(t.getVelY() + curGravity);
					if (curTime - t.getSpawnTime() > 500) {
						t.destroy();
					}
				}
				if (t.getType() == Tile.Type.BRICK_BROKEN) {
					t.setVelY(t.getVelY() + curGravity);
					if (curTime - t.getSpawnTime() > 1000) {
						t.destroy();
					}
				}
				if (t.getType() == Tile.Type.MARIO_DEAD) {
					t.setVelY(t.getVelY() + curGravity);
					if (curTime - t.getSpawnTime() > 1000) {
						reset();
					}
				}
				if (t.getType() == Tile.Type.GOOMBA_CRUSHED) {
					if (curTime - t.getSpawnTime() > 300) {
						t.destroy();
					}
				}

				// Goomba gets bumped
				if (t.isBumpTile() && t.exists()) {
					t.setVelY(t.getVelY() + curGravity);
					for (Tile ti : Tile.getTiles()) {
						if (ti.exists() && !ti.isBumpTile() && ti.getType() == Tile.Type.GOOMBA) {
							if (t.willContain(ti)) {
								ti.destroy();
								ti.bump();
							}
						}
					}
					if (t.getType() != Tile.Type.GOOMBA_BUMPED) {
						if (t.getParentTile() != null && t.nextPosY() > t.getParentTile().getY()) {
							t.destroy();
							t.getParentTile().setVisible(true);
						}
					} else {
						if (t.getY() > height) {
							t.destroy();
						}
					}
				}

				// Mushroom Touches Player(GOOMBA/POWERUP)
				if (t.getType() == Tile.Type.GOOMBA || t.getType() == Tile.Type.MUSHROOM
						|| t.getType() == Tile.Type.MUSHROOM_LIFE) {
					t.setVelY(t.getVelY() + curGravity);
					for (double i = 0; i < 10; i++) {
						if (t.contains(ply.getX(), ply.getY() + ply.getHeight() * (i / 10))
								&& !t.contains(ply.getX() + ply.getWidth(), ply.getY() + ply.getHeight() * (i / 10))) {
							if (t.getType() == Tile.Type.GOOMBA) {
								if (!ply.isDead() && t.exists())
									if (!ply.isDead() && t.exists())
										if (!ply.wasDamaged() && t.getType() == Tile.Type.GOOMBA)
											killPlayer();
							} else if (t.exists() && t.getType() == Tile.Type.MUSHROOM) {
								t.destroy();
								ply.setSuper(true);
							} else if (t.exists() && t.getType() == Tile.Type.MUSHROOM_LIFE) {
								t.destroy();
								ply.setNumLives(ply.getNumLives() + 1);
								AudioLoader.play("res/sound/1Up.wav");
							}
						} else if (t.contains(ply.getX() + ply.getWidth(), ply.getY() + ply.getHeight() * (i / 10))
								&& !t.contains(ply.getX(), ply.getY() + ply.getHeight() * (i / 10))) {
							if (t.getType() == Tile.Type.GOOMBA) {
								if (!ply.isDead() && t.exists())
									if (!ply.wasDamaged()) {
										killPlayer();
									}
							} else if (t.exists() && t.getType() == Tile.Type.MUSHROOM) {
								t.destroy();
								ply.setSuper(true);
							} else if (t.exists() && t.getType() == Tile.Type.MUSHROOM_LIFE) {
								t.destroy();
								ply.setNumLives(ply.getNumLives() + 1);
								AudioLoader.play("res/sound/1Up.wav");
							}
						}
					}

					if (curTime - t.getSpawnTime() > walkSpeed && t.getType() == Tile.Type.GOOMBA) {
						t.toggleGoomba();
						t.setSpawnTime(curTime);
					}

					// Goomba/Mushroom Physics
					for (Tile ti : Tile.getTiles()) {
						boolean isUnderTile = false;
						if (ti.isSolid() && ti.willContain(t) && t.getY() < ti.getY()
								&& ti.getType() != Tile.Type.GOOMBA) {
							t.setPos(t.getX(), ti.getY() - t.getHeight());
							t.setVelY(0);
							isUnderTile = true;
						}
						if (t != ti && ti.isSolid() && ti.willContainHorizontal(t) && !ti.isMovingTile()
								&& !isUnderTile) {
							if (t.getX() <= ti.getX()) {
								t.setPos(ti.getX() - t.getWidth(), t.getY());
							} else {
								t.setPos(ti.getX() + ti.getWidth(), t.getY());
							}
							if (t.isMovingRight()) {
								t.setMovingRight(false);
							} else {
								t.setMovingRight(true);
							}
						}
					}

					if (t.exists()) {
						if (!ply.isDead() && !ply.wasDamaged() && t.getType() == Tile.Type.GOOMBA) {
							if (t.isMovingRight()) {
								t.setVelX(1);
							} else {
								t.setVelX(-1);
							}
						} else if (!ply.isDead() && !ply.wasDamaged()) {
							if (t.isMovingRight()) {
								t.setVelX(2);
							} else {
								t.setVelX(-2);
							}
						} else {
							t.setVelX(0);
						}
					}
				}
			}

			// Fall Off
			if (ply.getY() > height && !ply.isDead()) {
				ply.kill();
				reset();
			}

			// Left-Edge Collision
			if (ply.nextPosX() < 0) {
				ply.setPos(0, ply.getY());
				ply.setVelX(0);
			}

			// Move
			for (Tile t : Tile.getTiles()) {
				if (t.getX() > 0 && t.getX() < width) {
					t.setHasBeenSeen(true);
				}

				if (t.hasBeenSeen()) {
					t.move(t.getVelX(), t.getVelY());
				}
			}

			if (ply.canMove()) {
				ply.move(ply.getVelX(), ply.getVelY());
			}

			// Functional---------------------
			repaint();
			try {
				Thread.sleep(17);
			} catch (InterruptedException e1) {
			}

		}
	}

	public void killPlayer() {
		if (!ply.isSuper()) {
			AudioLoader.play("res/sound/bump.wav");
			ply.kill();
			Tile ti = new Tile(ply.getX(), ply.getY(), ply.getWidth(), ply.getHeight(), Tile.Type.MARIO_DEAD);
			repaint();
			ti.setVelY(-10);
		} else {
			ply.setSuper(false);
		}
	}

	public void reset() {
		setBackground(Color.black);
		Entity.reset();
		AudioLoader.play("res/sound/gameOverExtra.wav");

		try {
			Thread.sleep(3750);

		} catch (InterruptedException e) {
		}
		generateMapFromArray(Level.imageToMap("/res/map.png"));
	}

	public void moveEdgePoint() {
		for (Tile t : Tile.getTiles()) {
			if (t != edgeMarker) {
				t.move(-edgeMarker.getX() - 1, 0);
			}
		}
		edgeMarker.move(-edgeMarker.getX() - 1, 0);
	}

	// Map Generation
	public void generateMapFromArray(long[][] mapArray) {

		Tile[] hills = new Tile[20];
		for (int i = 0; i < hills.length; i++) {
			if (i < 3) {
				hills[i] = new Tile(r.nextInt(width), height - 90 * 3, 180 * 3, 90 * 3, Tile.Type.HILL);
			} else {
				hills[i] = new Tile(r.nextInt(width * 10) + width, height - 90 * 3, 180 * 3, 90 * 3, Tile.Type.HILL);
			}
		}
		for (int i = 0; i < hills.length; i++) {
			for (int j = 0; j < hills.length; j++) {
				while (hills[i] != hills[j]
						&& Math.abs(hills[i].getX() - hills[j].getX()) < r.nextInt(300 - 100) + 100) {
					hills[j].move(1, 0);
				}
			}
		}

		Tile[] bushes = new Tile[30];
		for (int i = 0; i < bushes.length; i++) {
			if (i < 3) {
				bushes[i] = new Tile(r.nextInt(width / blockWidth) * blockWidth, height - 32 * 2 - blockHeight, 96 * 2,
						32 * 2, Tile.Type.BUSH);
			} else {
				bushes[i] = new Tile(RandomInBounds.nextInt(0, 211 * blockWidth), height - 32 * 2 - blockHeight, 96 * 2,
						32 * 2, Tile.Type.BUSH);
			}
		}

		Tile[] clouds = new Tile[20];
		for (int i = 0; i < clouds.length; i++) {
			if (i < 3) {
				clouds[i] = new Tile(r.nextInt(width), r.nextInt(height / 3), 35 * 3, 24 * 3, Tile.Type.CLOUD);
			} else {
				clouds[i] = new Tile(r.nextInt(width * 10) + width, r.nextInt(height / 3), 35 * 3, 24 * 3,
						Tile.Type.CLOUD);
			}
		}

		for (int i = 0; i < mapArray.length; i++) {
			for (int j = 0; j < mapArray[i].length; j++) {
				this.setBackground(bgc);
				if (mapArray[i][j] == -16777216) {
					new Tile(i * blockWidth, j * blockHeight, blockWidth, blockHeight, Tile.Type.GROUND);
				} else if (mapArray[i][j] == -1) {
					new Tile(i * blockWidth, j * blockHeight, blockWidth, blockHeight, Tile.Type.BRICK);
				} else if (mapArray[i][j] == -65536) {
					Tile q = new Tile(i * blockWidth, j * blockHeight, blockWidth, blockHeight, Tile.Type.QBLOCK);
					q.setItem(Tile.Item.COIN);
				} else if (mapArray[i][j] == -16711681) {
					Tile q = new Tile(i * blockWidth, j * blockHeight, blockWidth, blockHeight, Tile.Type.QBLOCK);
					q.setItem(Tile.Item.MUSHROOM);
				} else if (mapArray[i][j] == -65281) {
					new Tile(i * blockWidth, j * blockHeight, blockWidth, blockHeight, Tile.Type.GOOMBA);
				} else if (mapArray[i][j] == -16711936) {
					new Tile(i * blockWidth, j * blockHeight, blockWidth * 2, blockHeight * 8, Tile.Type.PIPE);
				} else if (mapArray[i][j] == -16776961) {
					new Tile(i * blockWidth, j * blockHeight, blockWidth, blockHeight, Tile.Type.STAIR);
				} else if (mapArray[i][j] == -2236963) {
					Tile b = new Tile(i * blockWidth, j * blockHeight, blockWidth, blockHeight, Tile.Type.BRICK);
					b.setItem(Tile.Item.COIN);
					b.setNumItems(10);
				} else if (mapArray[i][j] == -6710887) {
					Tile b = new Tile(i * blockWidth, j * blockHeight, blockWidth, blockHeight, Tile.Type.INVISIBLE);
					b.setItem(Tile.Item.LIFE);
				} else if (mapArray[i][j] != 16777215) {
					System.out.println("bogie " + mapArray[i][j]);
				}
			}
		}

		for (int g = 0; g < 10; g++) {
			for (Tile t : Tile.getTiles()) {
				if (t.getType() == Tile.Type.BUSH) {
					boolean[] bushPoints = new boolean[20];
					boolean isSupported = false;
					do {
						for (Tile ti : Tile.getTiles()) {
							for (int i = 0; i < bushPoints.length; i++) {
								if (ti.isSolid() && t != ti
										&& ti.contains(t.getX() + t.getWidth() * ((double) i / bushPoints.length),
												t.getY() + t.getHeight() + 1)) {
									bushPoints[i] = true;
								}
							}
						}
						for (boolean b : bushPoints) {
							if (!b) {
								t.move(-blockWidth, 0);
								isSupported = false;
								break;
							} else {
								isSupported = true;
							}
						}
					} while (!isSupported);
				}
			}
		}

		edgeMarker = new Tile(-1, -1, 1, 1);
		ply = new Player(blockWidth, height - blockHeight * 2, blockWidth / 12 * 12, blockHeight / 16 * 16);
	}

	public static void main(String[] args) {
		new Thread(new Juego()).start();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			LEFT.setPressed(true);
		}
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			UP.setPressed(true);
		}
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			RIGHT.setPressed(true);
		}
		if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			DOWN.setPressed(true);
		}
		if (e.getKeyCode() == KeyEvent.VK_L) {
			RIGHT.togglePressed();
		}
		if (e.getKeyCode() == KeyEvent.VK_J) {
			LEFT.togglePressed();
		}
		if (e.getKeyCode() == KeyEvent.VK_I) {
			UP.togglePressed();
		}
		if (e.getKeyCode() == KeyEvent.VK_K) {
			DOWN.togglePressed();
		}

		if (e.getKeyCode() == KeyEvent.VK_C) {
			if (cena) {
				cena = false;
				AudioLoader.play("res/sound/bump.wav");
			} else {
				AudioLoader.play("res/sound/cena.wav");
				cena = true;
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_F) {
			if (flappyMario) {
				flappyMario = false;
				AudioLoader.play("res/sound/bump.wav");
			} else {
				AudioLoader.play("res/sound/getCoin.wav");
				flappyMario = true;
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_D) {
			if (doubleJump) {
				doubleJump = false;
				AudioLoader.play("res/sound/bump.wav");
			} else {
				AudioLoader.play("res/sound/getCoin.wav");
				doubleJump = true;
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_S) {
			if (ply.isSuper()) {
				ply.setSuper(false);
			} else {
				ply.setSuper(true);
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_G) {
			if (lowGravity) {
				AudioLoader.play("res/sound/bump.wav");
				lowGravity = false;
				curGravity = gravity;
			} else {
				AudioLoader.play("res/sound/getCoin.wav");
				lowGravity = true;
				curGravity = .3;
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_P) {
			if (curSpeed != speed) {
				AudioLoader.play("res/sound/bump.wav");
				curSpeed = speed;
			} else {
				AudioLoader.play("res/sound/getCoin.wav");
				curSpeed = speed * 3;
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			System.exit(0);
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			LEFT.setPressed(false);
		}
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			UP.setPressed(false);
		}
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			RIGHT.setPressed(false);
		}
		if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			DOWN.setPressed(false);
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	public class Key {
		private boolean isPressed;
		private int numTimesPressed = 0;

		public void setPressed(boolean isPressed) {
			if (isPressed) {
				numTimesPressed++;
			}
			this.isPressed = isPressed;
		}

		public void togglePressed() {
			if (this.isPressed) {
				this.isPressed = false;
			} else {
				this.isPressed = true;
			}
		}

		public boolean isPressed() {
			return isPressed;
		}
	}

}
