package eon.graph;

import java.util.ArrayList;

import eon.network.*;

/**
 * @restructured by vxFury
 *
 */
public class SearchConstraint {
	private Layer associatedLayer = null;
	private ArrayList<Node> excludedNodelist = null;
	private ArrayList<Link> excludedLinklist = null;
	
	private ArrayList<Integer> linkmask = null;

	public SearchConstraint(Layer associatedLayer) {
		this.associatedLayer = associatedLayer;
		
		excludedLinklist = new ArrayList<Link>();
		excludedNodelist = new ArrayList<Node>();
		
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
		excludedLinklist.add(link);
		
		int index = link.getIndex() >>> 5;
		int offset = link.getIndex() & 0x1F;
		
		int status = linkmask.get(index);
		int check = 0x1 << offset;
		linkmask.set(index, status | check);
	}
	
	public void removeLink(Link link) {
		excludedLinklist.remove(link);
		
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

	public ArrayList<Node> getExcludedNodeList() {
		return excludedNodelist;
	}

	public void setExcludedNodeList(ArrayList<Node> excludedNodeList) {
		this.excludedNodelist = excludedNodeList;
	}
	
	public void addAllLinks(ArrayList<Link> linkList) {
		for(Link link : linkList) {
			addLink(link);
		}
	}
	
	public ArrayList<Link> __getExcludedLinkList() {
		return excludedLinklist;
	}

	public void __setExcludedLinkList(ArrayList<Link> excludedLinklist) {
		this.excludedLinklist = excludedLinklist;
	}
}
