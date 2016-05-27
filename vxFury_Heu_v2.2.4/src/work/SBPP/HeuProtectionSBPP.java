package work.SBPP;

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
import work.forAMPL.dataForAMPL;
import work.utilities.Logger;
import work.utilities.Logger.Level;

/**
 * @author vxFury
 *
 */
public class HeuProtectionSBPP {
	public static ArrayList<Request> arrivedRequestList = new ArrayList<Request>();

	public void availabilityEnhancing_ProtectionSBPP_MD(String dataPath, String networkName, int minRate, int maxRate) {
		// Part 1 Data Preparation
		// TODO NOTE :
		Layer layer = new Layer("workLayer", 0, "");
		String topology = dataPath + "topology/" + networkName + ".csv";
		System.out.print(">>Importing Topology of  Network(" + topology + ") ...\n");
		layer.readTopology(topology);
		layer.generateNodePairs();
		
		Logger sysLogger = new Logger(dataPath + "output/system/system_" + networkName + ".log");
		if(sysLogger != null) {
			sysLogger.setLevel(Level.DEBUG);
		}
		
		// Part 2 Routing and Spectrum Allocation
		// TODO NOTE :
		System.out.println(">>Routing and Spectrum Allocation, the allocation information will be output to File(" + sysLogger.getName() + ") ...");
		long time = System.currentTimeMillis();
		for (NodePair nodepair : layer.getNodePairList()) {
			int rate = xRandom.uniformRandom(minRate,maxRate,new Random());
			Request req = new Request(nodepair, rate);
			nodepair.setRate(rate);
			
			boolean wp = false,pp = false;
			
			RouteSearching rs = new RouteSearching();
			
			RoutingAndSpectrumAllocation rsa = new RoutingAndSpectrumAllocation();
			
			ArrayList<Route> routeList = new ArrayList<Route>();
			routeList.clear();
			rs.searchAllRoutes(nodepair.getSrcNode(), nodepair.getDesNode(), layer, null, 6, Modulation.BPSK.getTransDistance(), routeList);
			
			for(Link link : layer.getLinkList()) {
				link.spectrumCheck();
				//link.outputUnusedSlotSections(null);
			}
			
			for(Route tmpRoute : routeList) {
				tmpRoute.setRate(rate);
				tmpRoute.setRouteType(RouteType.Working);
				
				if(rsa.SpectrumAllocating_MinimalDiff(req, tmpRoute) == 0) {
					wp = true;
					
					req.getRouteList().add(tmpRoute);
					nodepair.getRouteList().add(tmpRoute);
					
					break;
				}
			}
			
			for(Link link : layer.getLinkList()) {
				link.spectrumRestore(20);
			}
			
			if(wp) {
				SearchConstraint constraint = new SearchConstraint(layer);
				constraint.addAllLinks(req.getRouteList().get(0).getLinkList());
				
				routeList.clear();
				
				rs.searchAllRoutes(nodepair.getSrcNode(), nodepair.getDesNode(), layer, constraint, 10, Modulation.BPSK.getTransDistance(), routeList);
				
				for(Route tmpRoute : routeList) {
					tmpRoute.setRate(rate);
					tmpRoute.setRouteType(RouteType.Protection);
					
					if(rsa.SpectrumAllocating_MinimalDiff(req, tmpRoute) == 0) {
						pp = true;
						
						req.getRouteList().add(tmpRoute);
						nodepair.getRouteList().add(tmpRoute);
						
						break;
					}
				}
				
				if(wp && pp) {
					for(Link link : req.getRouteList().get(0).getLinkList()) {
						link.spectrumOccupy(req.getRouteList().get(0).getStartIndex(), req.getRouteList().get(0).getSlots(), req);
					}
					
					for(Link link : req.getRouteList().get(1).getLinkList()) {
						link.spectrumOccupy_Shareable(req.getRouteList().get(1).getStartIndex(), req.getRouteList().get(1).getSlots(), req, 20);
					}
				} else {
					nodepair.getRouteList().clear();
				}
			}
		}
		time = System.currentTimeMillis() - time;
		System.out.println("Time-consuming(RSA) : " + time + " ms");
		
		// Part 3 Availability Calculation
		// TODO NOTE :
		Logger rstLogger = new Logger(dataPath + "output/result/result_" + networkName + ".txt");
		if(rstLogger != null) {
			rstLogger.setLevel(Level.FATAL);
		}
		System.out.println(">>Output the result to File(" + rstLogger.getName() + ") ...");
		outResult_Availability(layer,rstLogger);
		Logger cmpLogger = new Logger(dataPath + "output/result/cmp_" + networkName + ".txt");
		if(cmpLogger != null) {
			cmpLogger.setLevel(Level.FATAL);
		}
		outputResult_SpectrumAllocationForComparation(layer,cmpLogger);
		
		for(Link link : layer.getLinkList()) {
			link.spectrumRestore(20);
			//link.outputUnusedSlotSections(null);
		}
		
		Logger.logln("", null);
		
//		Logger chkLogger = new Logger(dataPath + "output/result/rst_" + networkName + ".dat");
//		rstLogger.setLevel(Level.FATAL);
//		outputResultToCheck(layer,chkLogger);
	}
	
	public void availabilityEnhancing_ProtectionSBPP_AMD(String dataPath, String networkName, int minRate, int maxRate) {
		// Part 1 Data Preparation
		// TODO NOTE :
		Layer layer = new Layer("workLayer", 0, "");
		String topology = dataPath + "topology/" + networkName + ".csv";
		System.out.print(">>Importing Topology of  Network(" + topology + ") ...\n");
		layer.readTopology(topology);
		layer.generateNodePairs();
		
		Logger sysLogger = new Logger(dataPath + "output/system/system_" + networkName + ".log");
		if(sysLogger != null) {
			sysLogger.setLevel(Level.DEBUG);
		}
		
		int minFragment = (int) Math.ceil(minRate/Modulation.QPSK.getCapacity());
		
		// Part 2 Routing and Spectrum Allocation
		// TODO NOTE :
		System.out.println(">>Routing and Spectrum Allocation, the allocation information will be output to File(" + sysLogger.getName() + ") ...");
		long time = System.currentTimeMillis();
		for (NodePair nodepair : layer.getNodePairList()) {
			int rate = xRandom.uniformRandom(minRate,maxRate,new Random());
			Request req = new Request(nodepair, rate);
			nodepair.setRate(rate);
			
			boolean wp = false,pp = false;
			
			RouteSearching rs = new RouteSearching();
			
			RoutingAndSpectrumAllocation rsa = new RoutingAndSpectrumAllocation();
			
			ArrayList<Route> routeList = new ArrayList<Route>();
			routeList.clear();
			rs.searchAllRoutes(nodepair.getSrcNode(), nodepair.getDesNode(), layer, null, 24, Modulation.BPSK.getTransDistance(), routeList);
			
			for(Link link : layer.getLinkList()) {
				link.spectrumCheck();
			}
			
			for(Route tmpRoute : routeList) {
				tmpRoute.setRate(rate);
				tmpRoute.setRouteType(RouteType.Working);
				
				if(rsa.SpectrumAllocating_AdvancedMinDiff(req, tmpRoute,minFragment) == 0) {
					wp = true;
					
					req.getRouteList().add(tmpRoute);
					nodepair.getRouteList().add(tmpRoute);
					
					break;
				}
			}
			
			for(Link link : layer.getLinkList()) {
				link.spectrumRestore(20);
			}
			
			if(wp) {
				SearchConstraint constraint = new SearchConstraint(layer);
				constraint.addAllLinks(req.getRouteList().get(0).getLinkList());
				
				routeList.clear();
				
				rs.searchAllRoutes(nodepair.getSrcNode(), nodepair.getDesNode(), layer, constraint, 24, Modulation.BPSK.getTransDistance(), routeList);
				
				for(Route tmpRoute : routeList) {
					tmpRoute.setRate(rate);
					tmpRoute.setRouteType(RouteType.Protection);
					
					if(rsa.SpectrumAllocating_AdvancedMinDiff(req, tmpRoute,minFragment) == 0) {
						pp = true;
						
						req.getRouteList().add(tmpRoute);
						nodepair.getRouteList().add(tmpRoute);
						
						break;
					}
				}
				
				if(wp && pp) {
					for(Link link : req.getRouteList().get(0).getLinkList()) {
						link.spectrumOccupy(req.getRouteList().get(0).getStartIndex(), req.getRouteList().get(0).getSlots(), req);
					}
					
					for(Link link : req.getRouteList().get(1).getLinkList()) {
						link.spectrumOccupy_Shareable(req.getRouteList().get(1).getStartIndex(), req.getRouteList().get(1).getSlots(), req, 20);
					}
				} else {
					nodepair.getRouteList().clear();
				}
			}
		}
		time = System.currentTimeMillis() - time;
		System.out.println("Time-consuming(RSA) : " + time + " ms");
		
		// Part 3 Availability Calculation
		// TODO NOTE :
		Logger rstLogger = new Logger(dataPath + "output/result/result_" + networkName + ".txt");
		if(rstLogger != null) {
			rstLogger.setLevel(Level.FATAL);
		}
		System.out.println(">>Output the result to File(" + rstLogger.getName() + ") ...");
		outResult_Availability(layer,rstLogger);
		Logger cmpLogger = new Logger(dataPath + "output/result/cmp_" + networkName + ".txt");
		if(cmpLogger != null) {
			cmpLogger.setLevel(Level.FATAL);
		}
		outputResult_SpectrumAllocationForComparation(layer,cmpLogger);
		
		for(Link link : layer.getLinkList()) {
			link.spectrumRestore(20);
			//link.outputUnusedSlotSections(null);
		}
		
		Logger.logln("", null);
		
//		Logger chkLogger = new Logger(dataPath + "output/result/rst_" + networkName + ".dat");
//		rstLogger.setLevel(Level.FATAL);
//		outputResultToCheck(layer,chkLogger);
	}
	
	public static void outResult_Availability(Layer layer, Logger logger) {
		double[] unava = new double[layer.getNodePairList().size()];
		for(NodePair nodepair : layer.getNodePairList()) {
			unava[nodepair.getIndex()] = 1.0;
			
			if(nodepair.getRouteList().size() > 0) {
				for(Route route : nodepair.getRouteList()) {
					unava[nodepair.getIndex()] *= 1 - route.getAvailRouteSingle();
				}
			}
		}
		
		double awBand = 0.0;
		double establishedBand = 0.0;
		double blockedBand = 0.0;
		
		for (NodePair nodepair : layer.getNodePairList()) {
			if (nodepair.getRouteList().size() != 0) {
				awBand += nodepair.getRate() * (1 - unava[nodepair.getIndex()]);
				establishedBand += nodepair.getRate();
			} else {
				blockedBand += nodepair.getRate();
			}
		}
		
		Logger.logln("" + (1 - awBand / establishedBand) + " " + establishedBand + " " + awBand + " " + blockedBand,logger);
		
		Logger.logln("Unavailability : " + (1 - awBand / establishedBand),null);
	}
	
	public void outputResult_SpectrumAllocation(Layer layer,Logger logger) {
		String format = "Route,Length,Rate,Modulation,StartIndex,Slots,EndIndex,Availability";
		Logger.logln(format,logger);
		
		for(NodePair nodepair : layer.getNodePairList()) {
			if(nodepair.getRouteList().size() != 0) {
				for(Route route : nodepair.getRouteList()) {
					Route.outputRoute(route, logger);
					String msg = "," + route.getLength() + "," + route.getRate() + "," + route.getModulation().getName() + "," + route.getStartIndex() + "," + route.getSlots() + "," + (route.getStartIndex() + route.getSlots() - 1) + "," + route.getAvailRouteSingle();
					Logger.logln(msg,logger);
				}
			}
		}
	}
	
	public void outputResult_SpectrumAllocationForComparation(Layer layer,Logger logger) {
		String format = "Route StartIndex EndIndex";
		Logger.logln(format,logger);
		
		for(NodePair nodepair : layer.getNodePairList()) {
			if(nodepair.getRouteList().size() != 0) {
				for(Route route : nodepair.getRouteList()) {
					Route.outputRoute(route, logger);
					String msg = " " + route.getStartIndex() + " " + (route.getStartIndex() + route.getSlots() - 1);
					Logger.logln(msg,logger);
				}
				
				Logger.logln("", logger);
			}
		}
	}
	
	public void outputResultToCheck(Layer layer, Logger logger) {
		dataForAMPL dfl = new dataForAMPL();
		
		dfl.outputSetOfLinks(layer, logger);
		dfl.outputSetOfNodePairs(layer, logger);
		
		dfl.outputSetOfCandidateRoutes(layer, logger);
		dfl.outputParamOfDelta(layer, logger);
		
		Logger.logln("param sI :=",logger);
		for (NodePair nodepair : layer.getNodePairList()) {
			for (Route route : nodepair.getRouteList()) {
				Logger.log(nodepair.getName() + " ",logger);
				Route.outputRoute(route, logger);
				Logger.logln(" " + route.getStartIndex(),logger);
			}
		}
		Logger.logln(";\r\n",logger);
		
		Logger.logln("param eI :=",logger);
		for (NodePair nodepair : layer.getNodePairList()) {
			for (Route route : nodepair.getRouteList()) {
				Logger.log(nodepair.getName() + " ",logger);
				Route.outputRoute(route, logger);
				Logger.logln(" " + (route.getStartIndex() + route.getSlots() - 1),logger);
			}
		}
		Logger.logln(";\r\n",logger);
		
		Logger.logln("param share :=",logger);
		for (NodePair nodepair : layer.getNodePairList()) {
			for (Route route : nodepair.getRouteList()) {
				Logger.log(nodepair.getName() + " ",logger);
				Route.outputRoute(route, logger);
				Logger.logln(" " + ((route.getRouteType() == RouteType.Working) ? (0) : (1)),logger);
			}
		}
		Logger.logln(";\r\n",logger);
	}
}
