package eon.general;

import java.util.Random;

/**
 * @restructured by vxFury
 *
 */
public class xRandom {
	public static int uniformRandom(int min_value, int max_value, Random r) {
		int interval = max_value - min_value + 1;
		int x = r.nextInt(interval);
		return min_value + x;
	}

	public static double uniformRandom(Random r) {
		return r.nextDouble();
	}

	public static double expdev(Random r) {
		double dum;
		dum = uniformRandom(r);
		return (-Math.log(dum));
	}
}
