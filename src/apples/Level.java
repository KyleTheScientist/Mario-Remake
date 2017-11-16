package apples;

import java.awt.Image;
import java.awt.image.BufferedImage;

public class Level {

	public Level() {
	}

	public static long[][] imageToMap(String path) {
		Image img = ImageLoader.load(path);

		BufferedImage image = (BufferedImage) img;

		long[][] map = new long[image.getWidth()][image.getHeight()];

		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				map[i][j] = image.getRGB(i, j);
			}
		}
		
		return map;
	}
}
