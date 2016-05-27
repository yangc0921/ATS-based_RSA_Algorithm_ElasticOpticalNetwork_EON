package eon.spectrum;

import eon.network.Link;

/**
 * @author vxFury
 *
 */
public class ResourceOnLink {
	private Link link;
	private int startIndex;
	private int slots;
	private Request request;
	
	public ResourceOnLink(Link link, int startIndex, int slots, Request request) {
		setLink(link);
		setStartIndex(startIndex);
		setSlots(slots);
		setRequest(request);
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
	
	public Link getLink() {
		return link;
	}
	
	public void setLink(Link link) {
		this.link = link;
	}

	public Request getRequest() {
		return request;
	}

	public void setRequest(Request request) {
		this.request = request;
	}
}