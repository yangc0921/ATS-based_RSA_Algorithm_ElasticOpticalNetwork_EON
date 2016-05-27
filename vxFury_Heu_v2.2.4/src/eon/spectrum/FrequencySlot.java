package eon.spectrum;

import java.util.ArrayList;

/**
 * @author vxFury
 *
 */
public class FrequencySlot {
	private int index;
	private int flags = 0x0;
	
	private ArrayList<Request> requestList = new ArrayList<Request>();
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	
	public FrequencySlot(int index) {
		this.index = index;
	}
	
	public ArrayList<Request> getRequestList() {
		return requestList;
	}
	
	public void setRequestList(ArrayList<Request> requestList) {
		this.requestList = requestList;
	}
	
	public void clearShareable() {
		flags &= 0xBFFFFFFF;
	}
	
	public void setShareable() {
		flags |= 0x40000000;
	}

	public boolean isShareable() {
		return (flags & 0x40000000) != 0;
	}
}