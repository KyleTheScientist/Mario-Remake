package apples;
import java.util.Random;

public class RandomInBounds {
	static Random r = new Random();

	public static int nextInt(int min, int max) {
		return (r.nextInt(max - min) + min);
	}

	public static double nextDouble(double min, double max) {
		int accuracy = 1000;

		min *= accuracy;
		max *= accuracy;
		double x = (double) r.nextInt((int) max - (int) min) + (int) min;
		return x / accuracy;
	}
}
