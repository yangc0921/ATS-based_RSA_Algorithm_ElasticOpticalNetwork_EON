package eon.spectrum;

import java.util.ArrayList;

import eon.general.RequestType;
import eon.general.RouteType;
import eon.network.NodePair;
import eon.network.Route;

/**
 * @author vxFury
 *
 */
public class Request {
	private NodePair nodepair;
	private double requiredRate;
	private RequestType requestType = RequestType.ARRIVAL;
	
	private double arrivalTime;
	private double departTime;
	
	private ArrayList<Route> routeList = null;
	private ArrayList<ResourceOnLink> rolList = null;
	
	public Request(NodePair nodepair, double rate) {
		setNodePair(nodepair);
		setRequiredRate(rate);
		setRequestType(RequestType.ARRIVAL);
		
		routeList = new ArrayList<Route>();
		rolList = new ArrayList<ResourceOnLink>();
	}

	public Request(NodePair nodepair, int rate, double arrivaltime, double departtime, RequestType requestType) {
		setNodePair(nodepair);
		setRequiredRate(rate);
		setArrivalTime(arrivaltime);
		setDepartTime(departtime);
		setRequestType(requestType);

		routeList = new ArrayList<Route>();
		rolList = new ArrayList<ResourceOnLink>();
	}
	
	public ArrayList<Route> getRouteList(RouteType routeType) {
		ArrayList<Route> rlist = new ArrayList<Route>();
		
		for(Route route : getRouteList()) {
			if(route.getRouteType() == routeType) {
				rlist.add(route);
			}
		}
		
		return rlist;
	}

	public NodePair getNodePair() {
		return nodepair;
	}

	public void setNodePair(NodePair nodepair) {
		this.nodepair = nodepair;
	}

	public double getRequiredRate() {
		return requiredRate;
	}

	public void setRequiredRate(double requiredRate) {
		this.requiredRate = requiredRate;
	}

	public RequestType getRequestType() {
		return requestType;
	}

	public void setRequestType(RequestType requestType) {
		this.requestType = requestType;
	}

	public double getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(double arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public double getDepartTime() {
		return departTime;
	}

	public void setDepartTime(double departTime) {
		this.departTime = departTime;
	}
	
	public ArrayList<Route> getRouteList() {
		return routeList;
	}

	public void setRouteList(ArrayList<Route> routeList) {
		this.routeList = routeList;
	}

	public ArrayList<ResourceOnLink> getRolList() {
		return rolList;
	}

	public void setRolList(ArrayList<ResourceOnLink> rolList) {
		this.rolList = rolList;
	}
}