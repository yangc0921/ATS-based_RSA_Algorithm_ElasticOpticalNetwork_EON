package eon.spectrum;

import java.util.ArrayList;

import eon.network.Layer;
import eon.network.Link;
import eon.network.Route;

/**
 * @restructured by vxFury
 *
 */
public class SlotWindow extends Layer {
	private int startIndex;
	private int endIndex;
	
	private ArrayList<Link> excludedLinkList = null;
	private ArrayList<Route> unshareableRouteList;
	
	public SlotWindow(String name, int index, String comments, int startIndex, int endIndex) {
		super(name, index, comments);
		this.excludedLinkList = new ArrayList<Link>();
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		this.unshareableRouteList = new ArrayList<Route>();
	}

	public void RemoveConstrantLinks() {
		for (Link routeLinks : this.excludedLinkList) {
			routeLinks.getNodeA().addAdjacentNode(routeLinks.getNodeB());
		}
	}

	public void initSlotWindowPlane() {
		for (Link routeLinks : this.excludedLinkList) {
			routeLinks.getNodeA().removeAdjacentNode(routeLinks.getNodeB());
		}
	}

	public void addExcludedLinks(Route Route) {
		for (Link routeLinks : Route.getLinkList()) {
			this.excludedLinkList.add(routeLinks);
		}
	}

	public int getStartIndex() {
		return startIndex;
	}

	public int getEndIndex() {
		return endIndex;
	}

	public ArrayList<Route> getUnshareableRouteList() {
		return unshareableRouteList;
	}

	public void setUnshareableRouteList(ArrayList<Route> unshareableRouteList) {
		this.unshareableRouteList = unshareableRouteList;
	}
	
	public ArrayList<Link> getExcludedLinkList() {
		return excludedLinkList;
	}

	public void setExcludedLinkList(ArrayList<Link> excludedLinkList) {
		this.excludedLinkList = excludedLinkList;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public void setEndIndex(int endIndex) {
		this.endIndex = endIndex;
	}
}
