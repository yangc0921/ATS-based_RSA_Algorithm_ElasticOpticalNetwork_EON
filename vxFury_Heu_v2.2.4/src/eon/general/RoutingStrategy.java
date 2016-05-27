package eon.general;

/**
 * @restructured by vxFury
 *
 */
public enum RoutingStrategy {
	BestFit("BestFit", 0),MinimalDiff("MinimalDiff", 1),AdvancedMinDiff("AdvancedMinDiff", 2),MaximalWeight("MaximalWeight", 3),FirstFit("FirstFit", 4), LeastCost("LeastCost", 5), ;

	String name;
	int index;

	RoutingStrategy(String name, int index) {
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
