package apples;

import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

final public class ImageLoader {

	public static Image blankImage = load("res/tile/blank.png");
	public static Image brickImage = load("res/tile/brick.png");
	public static Image bushImage = load("res/tile/bush.png");
	public static Image cloudImage = load("res/tile/cloud.png");
	public static Image coinImage = load("res/tile/coin.png");
	public static Image eBlockImage = load("res/tile/emptyBlock.png");
	public static Image qBlockImage = load("res/tile/questionBlock.png");
	public static Image groundImage = load("res/tile/ground.png");
	public static Image hillImage = load("res/tile/hill.png");
	public static Image pipeImage = load("res/tile/pipe.png");
	public static Image stairImage = load("res/tile/stair.png");

	// -------------------------------------------------------
	public static Image marioJump1Image = load("res/mario/jump1.png");
	public static Image marioJump2Image = load("res/mario/jump2.png");
	public static Image marioLeft1Image = load("res/mario/left1.png");
	public static Image marioLeft2Image = load("res/mario/left2.png");
	public static Image marioRight1Image = load("res/mario/right1.png");
	public static Image marioRight2Image = load("res/mario/right2.png");
	public static Image marioStillImage = load("res/mario/still.png");
	public static Image marioDeadImage = load("res/mario/dead.png");

	public static Image sMarioStillImage = load("res/mario/super/still1.png");
	public static Image sMarioRight1Image = load("res/mario/super/right1.png");
	public static Image sMarioRight2Image = load("res/mario/super/right2.png");
	public static Image sMarioLeft1Image = load("res/mario/super/left1.png");
	public static Image sMarioLeft2Image = load("res/mario/super/left2.png");
	public static Image sMarioJump1Image = load("res/mario/super/jump1.png");
	public static Image sMarioJump2Image = load("res/mario/super/jump2.png");
	// -------------------------------------------------------
	public static Image goombaImage = load("res/tile/goomba.png");
	public static Image goomba2Image = load("res/tile/goomba2.png");
	public static Image goombaCrushedImage = load("res/tile/goombaCrushed.png");
	public static Image goombaBumpedImage = load("res/tile/goombaBumped.png");
	public static Image mushroomImage = load("res/tile/mushroom.png");
	public static Image mushroomLifeImage = load("res/tile/mushroomLife.png");
	// -------------------------------------------------------
	public static Image cenaImage = load("res/cena.png");

	public ImageLoader() {

	}

	public static Image load(String path) {
		Image img = null;
		if (path.charAt(0) != '/') {
			path = "/" + path;
		}
		try {
			img = new ImageIcon(ImageIO.read(ImageLoader.class.getResourceAsStream(path))).getImage();
		} catch (IOException e) {
		}

		return img;

	}

}
