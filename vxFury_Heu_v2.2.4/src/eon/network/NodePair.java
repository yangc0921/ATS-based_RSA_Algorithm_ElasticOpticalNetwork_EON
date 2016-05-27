package eon.network;

import java.util.ArrayList;

import eon.general.object;

/**
 * @restructured by vxFury
 *
 */
public class NodePair extends object {
	private Layer associatedLayer = null;

	private Node srcNode = null;
	private Node desNode = null;

	// list of k shortest routes associated with the nodepair
	private ArrayList<Route> routelist = null;

	private int leasthop;
	private int leastworkinghop;
	private int leastprotectionhop;

	private double rate = 0;

	public Node getSrcNode() {
		return srcNode;
	}

	public void setSrcNode(Node srcNode) {
		this.srcNode = srcNode;
	}

	public Node getDesNode() {
		return desNode;
	}

	public void setDesNode(Node desNode) {
		this.desNode = desNode;
	}

	private double NodepairProbability = 0;

	public double getNodepairProbability() {
		return NodepairProbability;
	}

	public void setNodepairProbability(double nodepairProbability) {
		NodepairProbability = nodepairProbability;
	}

	public NodePair(String name, int index, String comments, Layer associatedLayer, Node srcNode, Node desNode) {
		super(name, index, comments);
		this.associatedLayer = associatedLayer;
		this.srcNode = srcNode;
		this.desNode = desNode;
		this.routelist = new ArrayList<Route>();
	}

	public Layer getAssociatedLayer() {
		return associatedLayer;
	}

	public void setAssociatedLayer(Layer associatedLayer) {
		this.associatedLayer = associatedLayer;
	}

	public ArrayList<Route> getRouteList() {
		return routelist;
	}

	public void setRouteList(ArrayList<Route> routelist) {
		this.routelist = routelist;
	}

	public void addRoute(Route route) {
		this.routelist.add(route);
	}

	public void removeRoute(Route route) {
		this.routelist.remove(route);
	}

	public void removeRoute(int index) {
		for (int i = 0; i < this.routelist.size(); i++) {
			if (this.routelist.get(i).getIndex() == index) {
				this.routelist.remove(i);
				break;
			}
		}
	}

	public void removeRoute(String name) {
		for (int i = 0; i < this.routelist.size(); i++) {
			if (this.routelist.get(i).getName().equals(name)) {
				this.routelist.remove(i);
				break;
			}
		}
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public double getRate() {
		return rate;
	}

	public void setLeastHop(int hop) {
		this.leasthop = hop;
	}

	public int getLeastHop() {
		return leasthop;
	}

	public void setLeastWorkingHop(int hop) {
		this.leastworkinghop = hop;
	}

	public int getLeastWorkingHop() {
		return leastworkinghop;
	}

	public int getLeastProtectionHop() {
		return leastprotectionhop;
	}

	public void setLeastProtectionHop(int hop) {
		this.leastprotectionhop = hop;
	}
	
	// For VCAT
	public int getSlots() {
		int slots = 0;
		
		for(Route route : this.getRouteList()) {
			slots += route.getSlots();
		}
		
		return slots;
	}
	
	public double getNodepairAvailVCAT() {
		double nodepairAvailVCAT = 0;
		
		for (int k = 0; k < this.getRouteList().size(); k++) {
			Route route = this.getRouteList().get(k);
			nodepairAvailVCAT += ((double) route.getSlots()/ (double)this.getSlots()) * route.getAvailRouteVCAT(this);
		}

		return nodepairAvailVCAT;
	}
}
