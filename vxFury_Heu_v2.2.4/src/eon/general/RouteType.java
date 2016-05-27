package eon.general;

/**
 * @restructured by vxFury
 *
 */
public enum RouteType {
	Ordinary("Ordinary", 0), Working("Working", 1), Protection("Protection", 2);

	String name;
	int index;

	RouteType(String name, int index) {
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
