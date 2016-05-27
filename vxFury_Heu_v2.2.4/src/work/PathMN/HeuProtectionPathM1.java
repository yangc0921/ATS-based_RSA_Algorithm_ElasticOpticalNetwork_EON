package work.PathMN;

import java.util.ArrayList;
import java.util.Random;

import eon.network.Layer;
import eon.network.Link;
import eon.network.NodePair;
import eon.general.Modulation;
import eon.general.RouteType;
import eon.general.xRandom;
import eon.graph.RouteSearching;
import eon.graph.SearchConstraint;
import eon.spectrum.Request;
import eon.spectrum.RoutingAndSpectrumAllocation;
import eon.network.Route;
import work.utilities.Logger;
import work.utilities.Logger.Level;

/**
 * @author vxFury
 *
 */
public class HeuProtectionPathM1 {
	public void availabilityEnhancing_ProtectionPathM1_MD(String dataPath, String networkName, int M, int minRate, int maxRate) {
		// Part 1 Data Preparation
		// TODO NOTE :
		Layer layer = new Layer("workLayer", 0, "");
		String topology = dataPath + "topology/" + networkName + ".csv";
		System.out.print(">>Importing Topology of  Network(" + topology + ") ...\n");
		layer.readTopology(topology);
		layer.generateNodePairs();
		
		Logger sysLogger = new Logger(dataPath + "output/system/system_" + networkName + ".log");
		sysLogger.setLevel(Level.FATAL);

		// Part 2 Routing and Spectrum Allocation
		// TODO NOTE :
		System.out.println(">>Routing and Spectrum Allocation, the allocation information will be output to File(" + sysLogger.getName() + ") ...");
		long time = System.currentTimeMillis();
		for (NodePair nodepair : layer.getNodePairList()) {
			int rate = xRandom.uniformRandom(minRate,maxRate,new Random());
			Request req = new Request(nodepair, rate);
			nodepair.setRate(rate);
			
			RouteSearching rs = new RouteSearching();
			
			Route route = new Route(layer,"",0,"");
			rs.Dijkstras(nodepair.getSrcNode(), nodepair.getDesNode(), layer, route, null);
			
			RoutingAndSpectrumAllocation rsa = new RoutingAndSpectrumAllocation();
			route.setRate(rate);
			route.setRouteType(RouteType.Working);
			if(rsa.SpectrumAllocating_MinimalDiff(req, route) == 0) {
				req.getRouteList().add(route);
				nodepair.getRouteList().add(route);
				
				for(Link link : route.getLinkList()) {
					link.spectrumOccupy(route.getStartIndex(), route.getSlots(), req);
				}
			} else {
				ArrayList<Route> routeList = new ArrayList<Route>();
				rs.searchAllRoutes(nodepair.getSrcNode(), nodepair.getDesNode(), layer, null, 5, Modulation.BPSK.getTransDistance(), routeList);
				
				for(Route tmpRoute : routeList) {
					tmpRoute.setRate(rate);
					tmpRoute.setRouteType(RouteType.Working);
					
					if(rsa.SpectrumAllocating_MinimalDiff(req, route) == 0) {
						req.getRouteList().add(tmpRoute);
						nodepair.getRouteList().add(tmpRoute);
						
						for(Link link : tmpRoute.getLinkList()) {
							link.spectrumOccupy(tmpRoute.getStartIndex(), tmpRoute.getSlots(), req);
						}
						
						break;
					}
				}
			}
		}
		
		
		for(int t =0 ; t < M; t ++) {
			for (NodePair nodepair : layer.getNodePairList()) {
				boolean wp = false;
				
				SearchConstraint constraint = new SearchConstraint(layer);
				
				if(nodepair.getRouteList().size() > 0) {
					wp = true;
					
					for(Route route : nodepair.getRouteList()) {
						for(Link link : route.getLinkList()) {
							if(! constraint.containsLink(link)) {
								constraint.addLink(link);
							}
						}
					}
				}
				
				if (wp == true) {
					int rate = (int) nodepair.getRate();
					Request req = new Request(nodepair, rate);
					
					ArrayList<Route> routeList = new ArrayList<Route>();
					
					RouteSearching rs = new RouteSearching();
					
					rs.searchAllRoutes(nodepair.getSrcNode(), nodepair.getDesNode(), layer, constraint, 7, Modulation.BPSK.getTransDistance(), routeList);
					
					if(routeList.size() > 0) {
						for(Route route : routeList) {
							RoutingAndSpectrumAllocation rsa = new RoutingAndSpectrumAllocation();
							
							route.setRate(rate);
							route.setRouteType(RouteType.Protection);
							
							if(rsa.SpectrumAllocating_MinimalDiff(req, route) == 0) {
								req.getRouteList().add(route);
								nodepair.getRouteList().add(route);
								
								for(Link link : route.getLinkList()) {
									link.spectrumOccupy(route.getStartIndex(), route.getSlots(), req);
								}
								
								break;
							}
						}
					}
				}
			}
		}
		time = System.currentTimeMillis() - time;
		System.out.println("Time-consuming(RSA) : " + time + " ms");
		
		// Part 3 Availability Calculation
		// TODO NOTE :
		Logger rstLogger = new Logger(dataPath + "output/result/result_" + networkName + ".txt");
		rstLogger.setLevel(Level.FATAL);
		System.out.println(">>Availability Calculation, the result will be output to File(" + rstLogger.getName() + ") ...");
		outputResult_Availability(layer,rstLogger);
		
		Logger cmpLogger = new Logger(dataPath + "output/result/cmp_" + networkName + "_" + M + "_" + 1 + ".txt");
		cmpLogger.setLevel(Level.FATAL);
		outputResult_SpectrumAllocation(layer,cmpLogger);
		
//		for(Link link : layer.getLinkList()) {
//			link.outputUnusedSlotSections(sysLogger);
//		}
		
		Logger.logln("", null);
	}
	
	public void availabilityEnhancing_ProtectionPathM1_AMD(String dataPath, String networkName, int M, int minRate, int maxRate) {
		// Part 1 Data Preparation
		// TODO NOTE :
		Layer layer = new Layer("workLayer", 0, "");
		String topology = dataPath + "topology/" + networkName + ".csv";
		System.out.print(">>Importing Topology of  Network(" + topology + ") ...\n");
		layer.readTopology(topology);
		layer.generateNodePairs();
		
		int minFragment = (int) Math.ceil(minRate/Modulation.BPSK.getCapacity());
		
		Logger sysLogger = new Logger(dataPath + "output/system/system_" + networkName + ".log");
		sysLogger.setLevel(Level.FATAL);

		// Part 2 Routing and Spectrum Allocation
		// TODO NOTE :
		System.out.println(">>Routing and Spectrum Allocation, the allocation information will be output to File(" + sysLogger.getName() + ") ...");
		long time = System.currentTimeMillis();
		for (NodePair nodepair : layer.getNodePairList()) {
			int rate = xRandom.uniformRandom(minRate,maxRate,new Random());
			Request req = new Request(nodepair, rate);
			nodepair.setRate(rate);
			
			RouteSearching rs = new RouteSearching();
			
			Route route = new Route(layer,"",0,"");
			rs.Dijkstras(nodepair.getSrcNode(), nodepair.getDesNode(), layer, route, null);
			
			RoutingAndSpectrumAllocation rsa = new RoutingAndSpectrumAllocation();
			route.setRate(rate);
			route.setRouteType(RouteType.Working);
			if(rsa.SpectrumAllocating_AdvancedMinDiff(req, route,minFragment) == 0) {
				req.getRouteList().add(route);
				nodepair.getRouteList().add(route);
				
				for(Link link : route.getLinkList()) {
					link.spectrumOccupy(route.getStartIndex(), route.getSlots(), req);
				}
			} else {
				ArrayList<Route> routeList = new ArrayList<Route>();
				rs.searchAllRoutes(nodepair.getSrcNode(), nodepair.getDesNode(), layer, null, layer.getNodeList().size(), Modulation.BPSK.getTransDistance(), routeList);
				
				for(Route tmpRoute : routeList) {
					tmpRoute.setRate(rate);
					tmpRoute.setRouteType(RouteType.Working);
					
					if(rsa.SpectrumAllocating_AdvancedMinDiff(req, route,minFragment) == 0) {
						req.getRouteList().add(tmpRoute);
						nodepair.getRouteList().add(tmpRoute);
						
						for(Link link : tmpRoute.getLinkList()) {
							link.spectrumOccupy(tmpRoute.getStartIndex(), tmpRoute.getSlots(), req);
						}
						
						break;
					}
				}
			}
		}
		
		
		for(int t =0 ; t < M; t ++) {
			for (NodePair nodepair : layer.getNodePairList()) {
				boolean wp = false;
				
				SearchConstraint constraint = new SearchConstraint(layer);
				
				if(nodepair.getRouteList().size() > 0) {
					wp = true;
					
					for(Route route : nodepair.getRouteList()) {
						for(Link link : route.getLinkList()) {
							if(! constraint.containsLink(link)) {
								constraint.addLink(link);
							}
						}
					}
				}
				
				if (wp == true) {
					int rate = (int) nodepair.getRate();
					Request req = new Request(nodepair, rate);
					
					ArrayList<Route> routeList = new ArrayList<Route>();
					
					RouteSearching rs = new RouteSearching();
					
					rs.searchAllRoutes(nodepair.getSrcNode(), nodepair.getDesNode(), layer, constraint, layer.getNodeList().size(), Modulation.BPSK.getTransDistance(), routeList);
					
					if(routeList.size() > 0) {
						for(Route route : routeList) {
							RoutingAndSpectrumAllocation rsa = new RoutingAndSpectrumAllocation();
							
							route.setRate(rate);
							route.setRouteType(RouteType.Protection);
							
							if(rsa.SpectrumAllocating_AdvancedMinDiff(req, route,minFragment) == 0) {
								req.getRouteList().add(route);
								nodepair.getRouteList().add(route);
								
								for(Link link : route.getLinkList()) {
									link.spectrumOccupy(route.getStartIndex(), route.getSlots(), req);
								}
								
								break;
							}
						}
					}
				}
			}
		}
		time = System.currentTimeMillis() - time;
		System.out.println("Time-consuming(RSA) : " + time + " ms");
		
		// Part 3 Availability Calculation
		// TODO NOTE :
		Logger rstLogger = new Logger(dataPath + "output/result/result_" + networkName + ".txt");
		rstLogger.setLevel(Level.FATAL);
		System.out.println(">>Availability Calculation, the result will be output to File(" + rstLogger.getName() + ") ...");
		outputResult_Availability(layer,rstLogger);
		
		Logger cmpLogger = new Logger(dataPath + "output/result/cmp_" + networkName + "_" + M + "_" + 1 + ".txt");
		cmpLogger.setLevel(Level.FATAL);
		outputResult_SpectrumAllocation(layer,cmpLogger);
		
		for(Link link : layer.getLinkList()) {
			link.outputUnusedSlotSections(sysLogger);
		}
		
		Logger.logln("", null);
	}
	
	public void outputResult_SpectrumAllocation(Layer layer,Logger logger) {
		String format = "Route StartIndex EndIndex";
		Logger.logln(format,logger);
		
		for(NodePair nodepair : layer.getNodePairList()) {
			if(nodepair.getRouteList().size() != 0) {
				for(Route route : nodepair.getRouteList()) {
					Route.outputRoute(route, logger);
					String msg = " " + route.getStartIndex() + " " + (route.getStartIndex() + route.getSlots() - 1);
					Logger.logln(msg,logger);
				}
				
				Logger.logln("",logger);
			}
		}
	}
	
	public void outputResult_Availability(Layer layer, Logger logger) {
		double[] unava = new double[layer.getNodePairList().size()];
		for (NodePair nodepair : layer.getNodePairList()) {
			if (nodepair.getRouteList().size() != 0) {
				unava[nodepair.getIndex()] = 1;
				for (Route route : nodepair.getRouteList()) {
					unava[nodepair.getIndex()] *= 1 - route.getAvailRouteSingle();
				}
			}
		}
		
		double awBand = 0.0;
		double establishedBand = 0.0;
		double blockedBand = 0.0;
		
		for (NodePair nodepair : layer.getNodePairList()) {
			boolean est = nodepair.getRouteList().size() > 0;
			
			if(est) {
				awBand += nodepair.getRate() * (1 - unava[nodepair.getIndex()]);
				establishedBand += nodepair.getRate();
			} else {
				blockedBand += nodepair.getRate();
			}
		}
		
		Logger.logln("" + (1 - awBand / establishedBand) + " " + establishedBand + " " + awBand + " " + blockedBand,logger);
		
		Logger.logln("Unavailability : " + (1 - awBand / establishedBand),null);
	}
}
