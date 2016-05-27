package work.utilities;

public class Utility {
	public static String toBinaryString(int x) {
		int[] buffer = new int[Integer.SIZE];
		
		for(int i = (Integer.SIZE - 1); i >= 0;i --) {
			buffer[i] = x >>> i & 0x1;
		}
		
		String binStr = "";
		for(int i = (Integer.SIZE - 1); i >= 0;i --) {
			binStr += buffer[i];
		}
		
		return binStr;
	}
	
	public static String toBinaryString(long x) {
		long[] buffer = new long[Long.SIZE];
		
		for(int i = (Long.SIZE - 1); i >= 0;i --) {
			buffer[i] = x >>> i & 0x1;
		}
		
		String binStr = "";
		for(int i = (Long.SIZE - 1); i >= 0;i --) {
			binStr += buffer[i];
		}
		
		return binStr;
	}
}