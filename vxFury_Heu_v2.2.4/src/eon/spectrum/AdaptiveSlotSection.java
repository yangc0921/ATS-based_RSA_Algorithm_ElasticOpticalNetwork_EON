package eon.spectrum;

/**
 * @author vxFury
 *
 */
public class AdaptiveSlotSection {
	private int startIndex;
	private int slots;
	
	private double leftWeight = 0.0;
	private double rightWeight = 0.0;
	
	public AdaptiveSlotSection(int startIndex,int slots) {
		this.slots = slots;
		this.startIndex = startIndex;
	}
	
	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public int getSlots() {
		return slots;
	}

	public void setSlots(int slots) {
		this.slots = slots;
	}

	public double getLeftWeight() {
		return leftWeight;
	}

	public void setLeftWeight(double leftWeight) {
		this.leftWeight = leftWeight;
	}

	public double getRightWeight() {
		return rightWeight;
	}

	public void setRightWeight(double rightWeight) {
		this.rightWeight = rightWeight;
	}
}