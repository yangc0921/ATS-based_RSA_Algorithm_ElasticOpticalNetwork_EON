package eon.network;

import java.util.ArrayList;

import eon.general.Constant;
import eon.general.bitProcess;
import eon.general.object;
import eon.spectrum.AdaptiveSlotSection;
import eon.spectrum.FrequencySlot;
import eon.spectrum.Request;
import eon.spectrum.RoutingAndSpectrumAllocation;
import work.utilities.Logger;

/**
 * @restructured by vxFury
 *
 */
public class Link extends object {
	private Layer associatedLayer = null;
	private Node nodeA = null;	// node A
	private Node nodeB = null;	// node B
	private double length = 0;	// physical distance
	private double cost = 0;	// the cost of the link
	
	private int status = 0;		// the visited status
	
	private ArrayList<Integer> resourceList = null;
	
	private ArrayList<FrequencySlot> slotList = null;

	public Link(String name, int index, String comments, Layer associatedLayer, Node nodeA, Node nodeB, double length, double cost) {
		super(name, index, comments);
		this.associatedLayer = associatedLayer;
		this.nodeA = nodeA;
		this.nodeB = nodeB;
		this.length = length;
		this.cost = cost;
		this.status = 0;
		
		resourceList = new ArrayList<Integer>();
		initResourceList(resourceList);
		
		slotList = new ArrayList<FrequencySlot>();
		initSlotList(slotList);
	}

	public Layer getAssociatedLayer() {
		return associatedLayer;
	}

	public void setAssociatedLayer(Layer associatedLayer) {
		this.associatedLayer = associatedLayer;
	}

	public Node getNodeA() {
		return nodeA;
	}

	public void setNodeA(Node nodeA) {
		this.nodeA = nodeA;
	}

	public Node getNodeB() {
		return nodeB;
	}

	public void setNodeB(Node nodeB) {
		this.nodeB = nodeB;
	}

	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public ArrayList<Integer> getResourceList() {
		return resourceList;
	}

	public void setResourceList(ArrayList<Integer> resourceList) {
		this.resourceList = resourceList;
	}

	public ArrayList<FrequencySlot> getSlotList() {
		return slotList;
	}

	public void setSlotList(ArrayList<FrequencySlot> slotList) {
		this.slotList = slotList;
	}
	
	public void initSlotList(ArrayList<FrequencySlot> slotList) {
		slotList.clear();
		for (int i = 0; i < Constant.TotalSlotsNum; i++) {
			FrequencySlot slot = new FrequencySlot(i);
			slotList.add(i, slot);
		}
	}
	
	public void initResourceList(ArrayList<Integer> slotSectionList) {
		int size = (Constant.TotalSlotsNum >>> 5) + (((Constant.TotalSlotsNum & 0x1F) == 0) ? (0) : (1));
		
		for(int i = 0;i < size; i ++) {
			Integer element;
			if((i == size - 1) && ((Constant.TotalSlotsNum & 0x1F) != 0)) {
				int remain = Constant.TotalSlotsNum - ((size - 1) << 5);
				element = ((int) (Math.pow(2, remain) - 1));
				
				slotSectionList.add(i, element);
			} else {
				element = 0xFFFFFFFF;
				slotSectionList.add(i, element);
			}
		}
	}
	
	public void set(int slotIndex) {
		int index = slotIndex >>> 5 + (((slotIndex & 0x1F) == 0) ? (0) : (1));
		int offset = slotIndex & 0x1F;
		
		int check = this.resourceList.get(index) | (0x1 << offset);
		
		this.resourceList.set(index, check);
	}
	
	public void clear(int slotIndex) {
		int index = slotIndex >>> 5 + (((slotIndex & 0x1F) == 0) ? (0) : (1));
		int offset = slotIndex & 0x1F;
		
		int check = this.resourceList.get(index) & (~(0x1 << offset));
		
		this.resourceList.set(index, check);
	}
	
	public void spectrumCheck() {
		for(FrequencySlot slot : slotList) {
			if(slot.isShareable()) {
				if(slot.getRequestList().size() == 0) {
					slot.clearShareable();
					set(slot.getIndex());
				} else {
					clear(slot.getIndex());
				}
			}
		}
	}
	
	public void spectrumRestore(int shareablity) {
		for(FrequencySlot slot : slotList) {
			if(slot.isShareable()) {
				if(slot.getRequestList().size() < shareablity) {
					set(slot.getIndex());
				} else {
					clear(slot.getIndex());
				}
			}
		}
	}
	
	public void spectrumOccupy_Shareable(int startIndex, int slots, Request request, int shareablity) {
		for(int i = startIndex; i < startIndex + slots;i ++) {
			FrequencySlot slot = this.slotList.get(i);
			slot.setShareable();
			slot.getRequestList().add(request);
			
			if(slot.getRequestList().size() >= shareablity) {
				clear(i);
			}
		}
	}
	
	public void spectrumRelease_Shareable(int startIndex, int slots, Request request, int shareablity) {
		for(int i = startIndex; i < startIndex + slots;i ++) {
			FrequencySlot slot = this.slotList.get(i);
			slot.getRequestList().remove(request);
			
			if(slot.getRequestList().size() < shareablity) {
				set(i);
			}
			
			if(slot.getRequestList().size() == 0) {
				slot.clearShareable();
			}
		}
	}
	
	public void spectrumOccupy(int startIndex, int slots, Request request) {
		int endIndexP = startIndex + slots;
		int offsetStart = startIndex & 0x1F;
		int indexStart = startIndex >>> 5;
		int offsetEnd = endIndexP & 0x1F;
		int indexEnd = endIndexP >>> 5;
		
		if(indexStart == indexEnd) {
			// resource required in the same Integer-Section
			Integer status = resourceList.get(indexStart);
			
			int check = ~(((0x1 << offsetStart) - 1) ^ ((0x01 << offsetEnd) - 1));
			resourceList.set(indexStart, status & check);
		} else {
			Integer status = resourceList.get(indexStart);
			int check = (0x1 << offsetStart) - 1;
			resourceList.set(indexStart, status & check);
			
			for(int index = indexStart + 1; index < indexEnd; index ++) {
				resourceList.set(index, 0x0);
			}
			
			if(offsetEnd != 0) {
				status = resourceList.get(indexEnd);
				
				check = ~((0x1 << offsetEnd) - 1);
				
				resourceList.set(indexEnd, status & check);
			}
		}
		
		for(int i = startIndex; i < startIndex + slots;i ++) {
			this.slotList.get(i).getRequestList().add(request);
		}
	}
	
	public void spectrumRelease(int startIndex, int slots, Request request) {
		int endIndexP = startIndex + slots;
		int offsetStart = startIndex & 0x1F;
		int indexStart = startIndex >>> 5;
		int offsetEnd = endIndexP & 0x1F;
		int indexEnd = endIndexP >>> 5;
		
		if(indexStart == indexEnd) {
			// resource required in the same Integer-Section
			Integer status = resourceList.get(indexStart);
			
			int check = ((0x1 << offsetStart) - 1) ^ ((0x01 << offsetEnd) - 1);
			resourceList.set(indexStart, status | check);
		} else {
			Integer status = resourceList.get(indexStart);
			int check = ~((0x1 << offsetStart) - 1);
			resourceList.set(indexStart, status | check);
			
			for(int index = indexStart + 1; index < indexEnd; index ++) {
				resourceList.set(index, 0xFFFFFFFF);
			}
			
			if(offsetEnd != 0) {
				status = resourceList.get(indexEnd);
				
				check = (0x1 << offsetEnd) - 1;
				
				resourceList.set(indexEnd, status | check);
			}
		}
		
		for(int i = startIndex; i < startIndex + slots;i ++) {
			this.slotList.get(i).getRequestList().remove(request);
		}
	}
	
	public void outputUnusedSlotSections(Logger logger) {
		RoutingAndSpectrumAllocation rsa = new RoutingAndSpectrumAllocation();
		ArrayList<AdaptiveSlotSection> ats = rsa.getAdaptiveSlotSections(this.getResourceList(), 1);
		
		if(ats.size() > 0) {
			Logger.logln("Unused Slot Section(s) for link(" + this.getName() + ") :", logger);
			
			for(AdaptiveSlotSection ss : ats) {
				Logger.logln("" + ss.getStartIndex() + " ~ " + (ss.getStartIndex() + ss.getSlots() - 1),logger);
			}
		} else {
			Logger.logln("All Slots for link(" + this.getName() + ") were used.", logger);
		}
		
		int unused = 0;
		for(Integer ss : this.getResourceList()) {
			unused += bitProcess.bitCount_StaticTable(ss);
		}
		Logger.logln("Unused Slots for link(" + this.getName() + ") :" + unused,logger);
	}
	
	private static double MTTR = Constant.MTTR;
	private static double MTTF = Constant.MTTF;
	
	public double getAvailLinkSingle() {
		return MTTF / (MTTF + MTTR * this.getLength());
	}
	
	public double getAvailLinkVCAT(NodePair nodepair, Route route){
		double slotRemain = (double) (nodepair.getSlots() - route.getSlots());
		double availLinkVCAT = (MTTF * nodepair.getSlots() + MTTR * this.getLength() * slotRemain)
				/ ((MTTF + MTTR * this.getLength()) * nodepair.getSlots());
		
		return availLinkVCAT;
	}
}
