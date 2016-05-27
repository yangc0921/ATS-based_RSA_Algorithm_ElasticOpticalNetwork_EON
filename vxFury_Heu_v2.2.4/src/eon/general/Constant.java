package eon.general;

import work.utilities.Logger.Level;

/**
 * @restructured by vxFury
 *
 */
public class Constant {
	public static int MAXIUM = 100000000;
	
	// Resource on fiber link
	public static double BandwidthPerSlot = 12.5; // in units of Gbps(Modulation : BPSK)
	public static int TotalSlotsNum = 320;
	
	// For Availability Research
	public final static double MTTR = 6.0;// Mean Time To Repair, 6 hours
	public final static double FIT = 1.0 / (Math.pow(10.0, 9.0));
	public final static double LAMBDA = 200.0 * FIT;
	public final static double MTTF = 1.0 / LAMBDA; // Mean Time To Failures
	public final static double MU = 1 / MTTR;
	
	public static int outMask = Level.FATAL.getMask()
								| Level.DEBUG.getMask();
}
