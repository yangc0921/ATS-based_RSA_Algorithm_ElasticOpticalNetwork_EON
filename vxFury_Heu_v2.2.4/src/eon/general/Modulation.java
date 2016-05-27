package eon.general;

/**
 * @restructured by vxFury
 *
 */
public enum Modulation {
	QAM8("8QAM", 3, 1000.0), QPSK("QPSK", 2, 2000.0), BPSK("BPSK", 1, 4000.0), UNARRIVAL("UNARRIVAL", 0, 0.0);

	private String Name;
	private int SpectrumEfficiency;
	private double transDistance;

	private Modulation(String Name, int SpectrumEfficiency, double transDistance) {
		this.Name = Name;
		this.SpectrumEfficiency = SpectrumEfficiency;
		this.transDistance = transDistance;
	}
	
	public static int getRequiredSlots(double rate,Modulation modulation) {
		return (int) Math.ceil(rate/modulation.getCapacity());
	}

	public String getName() {
		return Name;
	}

	public int getSpectrumEfficiency() {
		return SpectrumEfficiency;
	}

	public double getTransDistance() {
		return transDistance;
	}

	public double getCapacity() {
		return SpectrumEfficiency * Constant.BandwidthPerSlot;
	}
}
