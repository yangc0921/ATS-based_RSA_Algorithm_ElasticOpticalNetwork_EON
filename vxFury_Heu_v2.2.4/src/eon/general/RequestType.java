package eon.general;

/**
 * @restructured by vxFury
 *
 */
public enum RequestType {
	ARRIVAL("ARRIVAL", 0), DEPARTURE("DEPARTURE", 1);

	String name;
	int index;

	RequestType(String name, int index) {
		this.name = name;
		this.index = index;
	}

	public String getName() {
		return name;
	}

	public int getIndex() {
		return index;
	}
}
