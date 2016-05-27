package eon.spectrum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import eon.general.Constant;
import eon.general.Modulation;
import eon.general.RouteType;
import eon.graph.RouteSearching;
import eon.graph.SearchConstraint;
import eon.network.Layer;
import eon.network.Link;
import eon.network.Node;
import eon.network.Route;

/**
 * @author vxFury
 *
 */
public class RoutingAndSpectrumAllocation {
	public Route RoutingAndSpectrumAllocating_FirstFit(Layer layer ,ArrayList<SlotWindow> swpList, Request request,SearchConstraint constraint, int hoplimit, Modulation modulation,RouteType routeType) {
		Route newroute = null;
		
		loop:
		for (SlotWindow currentSWP : swpList) {
			int startindex = currentSWP.getStartIndex();
			int endindex = currentSWP.getEndIndex();
			
			if(currentSWP.getUnshareableRouteList().size() > 0) {
				for (Route route : currentSWP.getUnshareableRouteList()) {
					for (Link link : route.getLinkList()) {
						if (!currentSWP.getExcludedLinkList().contains(link)) {
							currentSWP.getExcludedLinkList().add(link);
						}
					}
				}
			}
			
			newroute = new Route(layer,"", 0, "");
			Node srcnode = request.getNodePair().getSrcNode();
			Node desnode = request.getNodePair().getDesNode();
			
			SearchConstraint pathconstraint = new SearchConstraint(layer);
			pathconstraint.addAllLinks(currentSWP.getExcludedLinkList());
			
			if(constraint != null) {
				// 3.exclude link(s) and node(s) included in the constraint
				if(constraint.__getExcludedLinkList().size() != 0) {
					for(Link link : constraint.__getExcludedLinkList()) {
						if(!pathconstraint.containsLink(link)) {
							pathconstraint.addLink(link);
						}
					}
				}
				if(constraint.getExcludedNodeList().size() != 0) {
					for(Node node : constraint.getExcludedNodeList()) {
						if(!pathconstraint.getExcludedNodeList().contains(node)) {
							pathconstraint.getExcludedNodeList().add(node);
						}
					}
				}
			}
			
			RouteSearching routesearch = new RouteSearching();
			routesearch.Dijkstras(srcnode, desnode, layer, newroute, pathconstraint);
			
			if(newroute.getLinkList().size() != 0) {
				newroute.setRouteType(routeType);
				newroute.setStartIndex(startindex);
				newroute.setSlots(endindex + 1 - startindex);
				newroute.setModulation(modulation);
				
				if(hoplimit <= 0) {
					if (newroute.getLength() < modulation.getTransDistance()) {
						break loop;
					}
				} else {
					if(newroute.getLinkList().size() <= hoplimit) {
						if (newroute.getLength() < modulation.getTransDistance()) {
							break loop;
						}
					}
				}
				
				newroute = null;
			} else {
				newroute = null;
			}
		}
		
		return newroute;
	}
	
	public Route RoutingAndSpectrumAllocating(Layer layer, Request request, RouteType routeType, Modulation modulation, SearchConstraint constraint) {
		Route newroute = new Route(layer,"", 0, "", routeType);
		
		Node srcNode = request.getNodePair().getSrcNode();
		
		int slots = (int) Math.ceil(request.getRequiredRate()/modulation.getCapacity());
		
		ArrayList<Integer> resourceListForSWP = new ArrayList<Integer>();
		
		int size = (Constant.TotalSlotsNum >>> 5) + (((Constant.TotalSlotsNum & 0x1F) == 0) ? (0) : (1));
		
		for(int i = 0;i < size; i ++) {
			Integer resource = 0x0;
			
			for(Node adjNode : srcNode.getAdjacentNodeList()) {
				Link link = layer.findLink(srcNode, adjNode);
				
				if(!constraint.containsLink(link)) {
					resource |= link.getResourceList().get(i);
				}
			}
			
			resourceListForSWP.add(i,resource);
		}
		
		ArrayList<AdaptiveSlotSection> atsList = getAdaptiveSlotSections(resourceListForSWP,slots);
		Collections.sort(atsList,new sortBySlots());
		
		if(atsList.size() > 0) {
			ArrayList<SlotWindow> swpList = new ArrayList<SlotWindow>();
			
			for(AdaptiveSlotSection ads : atsList) {
				SlotWindow sw = new SlotWindow("",atsList.size(),"",0,0);
				
				if(ads.getSlots() > slots) {
					for(int sI = ads.getStartIndex(); sI < ads.getStartIndex() + ads.getSlots() - slots + 1; sI ++) {
						sw.setStartIndex(sI);
						sw.setEndIndex(sI + slots - 1);
						swpList.add(sw);
					}
				} else {
					sw.setStartIndex(ads.getStartIndex());
					sw.setEndIndex(ads.getStartIndex() + ads.getSlots() - 1);
					swpList.add(sw);
				}
			}
			
			newroute = RoutingAndSpectrumAllocating_FirstFit(layer, swpList, request, constraint, size, modulation, routeType);
		} else {
			newroute = null;
		}
		
		if(newroute.getLinkList().size() == 0) {
			newroute = null;
		}
		
		return newroute;
	}
	
	public int SpectrumAllocating_MinimalDiff(Request request,Route route) {
		int rst = -1;
		
		int slots = (int) Math.ceil(route.getRate()/route.getModulation().getCapacity());
		route.setSlots(slots);
		
		ArrayList<AdaptiveSlotSection> atsList = getAvailableAdaptiveSlotSectionStrips(route,slots);
		Collections.sort(atsList,new sortBySlots());
		
		if(atsList.size() > 0) {
			rst = 0;
			
			route.setStartIndex(atsList.get(0).getStartIndex());
		}
		
		return rst;
	}
	
	public int SpectrumAllocating_AdvancedMinDiff(Request request,Route route, int minFragmentSize) {
		int rst = -1;
		
		int slots = (int) Math.ceil(route.getRate()/route.getModulation().getCapacity());
		route.setSlots(slots);
		
		ArrayList<AdaptiveSlotSection> atsList = getAvailableAdaptiveSlotSectionStrips(route,slots);
		Collections.sort(atsList,new sortBySlots());
		
		if(atsList.size() > 0) {
			rst = 0;
			
			for(AdaptiveSlotSection ss : atsList) {
				if(Math.abs(ss.getSlots() - slots) >= minFragmentSize) {
					route.setStartIndex(ss.getStartIndex());
					break;
				} else {
					rst = -1;
				}
			}
			
			if(rst == -1) {
				rst = 0;
				
				route.setStartIndex(atsList.get(0).getStartIndex());
			}
		}
		
		return rst;
	}
	
	public ArrayList<AdaptiveSlotSection> getAvailableAdaptiveSlotSectionStrips(Route route,int minimalSlots) {
		ArrayList<Integer> availableResourceList = new ArrayList<Integer>();
		
		if(minimalSlots <= 0) {
			minimalSlots = 1;
		}
		
		int size = (Constant.TotalSlotsNum >>> 5) + (((Constant.TotalSlotsNum & 0x1F) == 0) ? (0) : (1));
		
		for(int i = 0;i < size; i ++) {
			Integer commonResource = 0xFFFFFFFF;
			for(Link link : route.getLinkList()) {
				commonResource &= link.getResourceList().get(i);
			}
			
			availableResourceList.add(i,commonResource);
		}
		
		return getAdaptiveSlotSections(availableResourceList, minimalSlots);
	}
	
	// Adaptive Slot-Section(s)
	public ArrayList<AdaptiveSlotSection> getAdaptiveSlotSections(ArrayList<Integer> resourceList, int minimalSlots) {
		ArrayList<AdaptiveSlotSection> ats = new ArrayList<AdaptiveSlotSection>();
		
		if(minimalSlots <= 0) {
			minimalSlots = 1;
		}
		
		int size = resourceList.size(),index = 0;
		
		int offset = 0, bits = 0;
		for(Integer resource : resourceList) {
			for(int i = 0; i < 32; i ++) {
				if((resource & 0x1) != 0) {
					bits ++;
				} else {
					if(bits >= minimalSlots) {
						ats.add(new AdaptiveSlotSection(offset - bits,bits));
					}
					bits = 0;
				}
				
				resource >>>= 1;
				offset ++;
			}
			
			index ++;
			
			if((index == size) && (bits >= minimalSlots)) {
				ats.add(new AdaptiveSlotSection(offset - bits,bits));
			}
		}
		
		return ats;
	}
	
	public static int getBitsFlanked(ArrayList<Integer> resourceList, int index, int range) {
		int bits = 0;
		
		if(range == 0) {
			return 0;
		}
		
		int min, max;
		
		if(range < 0) {
			min = Math.max(0, index + range);
			max = index - 1;
			
			for(int i = max;i >= min; i --) {
				int tmpIndex = i >>> 5;
				int tmpOffset = i & 0x1F;
				
				int check = 0x1 << tmpOffset;
				if((resourceList.get(tmpIndex) & check) != 0x0) {
					bits ++;
				} else {
					break;
				}
			}
		} else {
			min = index + 1;
			max = Math.min(Constant.TotalSlotsNum, index + range);
			
			for(int i = min;i <= max; i ++) {
				int tmpIndex = i >>> 5;
				int tmpOffset = i & 0x1F;
				
				int check = 0x1 << tmpOffset;
				if((resourceList.get(tmpIndex) & check) != 0x0) {
					bits ++;
				} else {
					break;
				}
			}
		}
		
		return bits;
	}
}

class sortBySlots implements Comparator<Object> {
	public int compare(Object obj1,Object obj2) {
		AdaptiveSlotSection ads1 = (AdaptiveSlotSection) obj1;
		AdaptiveSlotSection ads2 = (AdaptiveSlotSection) obj2;
		
		return (ads1.getSlots() - ads2.getSlots());
	}
}

