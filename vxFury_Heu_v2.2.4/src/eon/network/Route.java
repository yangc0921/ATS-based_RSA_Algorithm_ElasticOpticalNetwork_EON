package eon.network;

import java.util.ArrayList;

import eon.general.Modulation;
import eon.general.RouteType;
import eon.general.object;
import eon.network.Route;
import work.utilities.Logger;

/**
 * @restructured by vxFury
 *
 */
public class Route extends object {
	private Layer associatedLayer = null;
	private RouteType routeType = RouteType.Ordinary;

	private ArrayList<Node> nodelist = null;
	private ArrayList<Link> linklist = null;
	
	private ArrayList<Integer> linkmask = null;

	private ArrayList<Integer> slotsList = null;

	private double length = 0.0;
	private Modulation modulation = Modulation.UNARRIVAL;

	private double cost;
	private double rate = 0;

	private int startIndex;
	private int slots;

	public Route(Layer associatedLayer, String name, int index, String comments) {
		super(name, index, comments);
		this.associatedLayer = associatedLayer;
		nodelist = new ArrayList<Node>();
		linklist = new ArrayList<Link>();
		slotsList = new ArrayList<Integer>();
		
		initLinkMask();
	}

	public Route(Layer associatedLayer, String name, int index, String comments, RouteType routeType) {
		super(name, index, comments);
		this.associatedLayer = associatedLayer;
		nodelist = new ArrayList<Node>();
		linklist = new ArrayList<Link>();
		slotsList = new ArrayList<Integer>();
		this.routeType = routeType;
		
		initLinkMask();
	}

	public Route(Layer associatedLayer, String name, int index, String comments, ArrayList<Link> linklist) {
		super(name, index, comments);
		this.associatedLayer = associatedLayer;
		nodelist = new ArrayList<Node>();
		slotsList = new ArrayList<Integer>();
		this.setLinkList(linklist);
		this.setSlots(0);
		
		initLinkMask();
	}
	
	public void initLinkMask() {
		int llsize = associatedLayer.getLinkList().size();
		int size = ((llsize >>> 5) + ((llsize & 0x1F) == 0 ? (0) : (1)));
		
		linkmask = new ArrayList<Integer>();
		
		for(int index = 0; index < size; index ++) {
			linkmask.add(0x0);
		}
	}
	
	public void addLink(Link link) {
		linklist.add(link);
		
		int index = link.getIndex() >>> 5;
		int offset = link.getIndex() & 0x1F;
		
		int status = linkmask.get(index);
		int check = 0x1 << offset;
		linkmask.set(index, status | check);
	}
	
	public void removeLink(Link link) {
		linklist.remove(link);
		
		int index = link.getIndex() >>> 5;
		int offset = link.getIndex() & 0x1F;
		
		int status = linkmask.get(index);
		int check = ~(0x1 << offset);
		linkmask.set(index, status & check);
	}
	
	public boolean containsLink(Link link) {
		int index = link.getIndex() >>> 5;
		int offset = link.getIndex() & 0x1F;
		return (linkmask.get(index) & (0x1 << offset)) != 0;
	}

	public Layer getAssociatedLayer() {
		return associatedLayer;
	}

	public void setAssociatedLayer(Layer associatedLayer) {
		this.associatedLayer = associatedLayer;
	}

	public RouteType getRouteType() {
		return routeType;
	}

	public void setRouteType(RouteType routeType) {
		this.routeType = routeType;
	}

	public void setLinkList(ArrayList<Link> linklist) {
		this.linklist = linklist;
	}

	public ArrayList<Link> getLinkList() {
		return linklist;
	}

	public void setNodeList(ArrayList<Node> nodelist) {
		this.nodelist = nodelist;
	}

	public ArrayList<Node> getNodeList() {
		return nodelist;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public void setSlots(int slotsnum) {
		this.slots = slotsnum;
	}

	public int getSlots() {
		return slots;
	}

	public void setSlotsList(ArrayList<Integer> slotsList) {
		this.slotsList = slotsList;
	}

	public ArrayList<Integer> getSlotsList() {
		return slotsList;
	}

	public void setStartIndex(int startindex) {
		this.startIndex = startindex;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public Modulation autosetModulation() {
		Modulation mod = Modulation.UNARRIVAL;
		for (Modulation modulation : Modulation.values()) {
			if (this.length <= modulation.getTransDistance()) {
				mod = modulation;
				break;
			}
		}

		return mod;
	}

	public Modulation getModulation() {
		return modulation;
	}

	public void setModulation(Modulation modulation) {
		this.modulation = modulation;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}
	
	public static void outputRoute(Route route, Logger logger) {
		int i = 1;
		for (Node node : route.getNodeList()) {
			if (i < route.getNodeList().size()) {
				Logger.log(node.getName() + "-",logger);
			} else {
				Logger.log(node.getName(),logger);
			}
			i++;
		}
	}
	
	// TODO : For availability calculation
	public double getAvailRouteSingle() {
		double availRouteSingle = 1.0;
		ArrayList<Link> linklist = this.getLinkList();
		for (Link link : linklist) {
			availRouteSingle *= link.getAvailLinkSingle();
		}
		return availRouteSingle;
	}
	
	public double getAvailRouteVCAT(NodePair nodepair) {
		ArrayList<Link> linklist = this.getLinkList();
		double availRouteVCAT = 1.0;
		for (Link link : linklist) {
			availRouteVCAT *= link.getAvailLinkVCAT(nodepair, this);
		}

		return availRouteVCAT;
	}
}
