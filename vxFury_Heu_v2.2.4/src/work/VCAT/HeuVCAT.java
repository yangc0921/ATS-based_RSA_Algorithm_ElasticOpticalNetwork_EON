package work.VCAT;

import eon.general.Modulation;
import eon.general.xRandom;

import java.util.ArrayList;
import java.util.Random;

import eon.network.Layer;
import eon.network.Link;
import eon.network.NodePair;
import eon.network.Route;
import work.utilities.Logger;
import work.utilities.Logger.Level;
import eon.spectrum.Request;
import eon.spectrum.ResourceOnLink;
import eon.spectrum.RoutingAndSpectrumAllocation;

/**
 * @author vxFury
 *
 */
public class HeuVCAT {
	public void availabilityEnhancing_VCAT(String dataPath, String networkName, int k,int minRate,int maxRate) {
		// Part 1 Data Preparation
		// TODO NOTE :
		Layer layer = new Layer("workLayer", 0, "");
		String topology = dataPath + "topology/" + networkName + ".csv";
		System.out.print(">>Importing Topology of Network(" + topology + ") ...\n");
		layer.readTopology(topology);
		layer.generateNodePairs();
		
		Logger sysLogger = new Logger(dataPath + "output/system/system_" + networkName + "_" + k + "_" + minRate + "_" + maxRate + ".log");
		sysLogger.setLevel(Level.ERROR);
		
		Layer.searchKshortesPath(layer, k,Modulation.BPSK.getTransDistance());
		
		double[] sumLength = new double[layer.getNodePairList().size()];
		
		for(NodePair nodepair : layer.getNodePairList()) {
			sumLength[nodepair.getIndex()] = 0;
			for(Route route : nodepair.getRouteList()) {
				sumLength[nodepair.getIndex()] += route.getLength();
			}
		}
		
		long time = System.currentTimeMillis();
		// Part 2 Routing and Spectrum Allocation
		// TODO NOTE :
		System.out.println(">>Routing and Spectrum Allocation, the allocation information will be output to File(" + sysLogger.getName() + ") ...");
		for(NodePair nodepair : layer.getNodePairList()){
			int rate = xRandom.uniformRandom(minRate,maxRate,new Random());
			nodepair.setRate(rate);
			Request request = new Request(nodepair, rate);
			
			boolean fail = false;
			
			ArrayList<ResourceOnLink> rolList = new ArrayList<ResourceOnLink>();
			loop:
			for (Route route : nodepair.getRouteList()) {
				RoutingAndSpectrumAllocation rsa = new RoutingAndSpectrumAllocation();
				
				double curRate = rate * nodepair.getRouteList().get(nodepair.getRouteList().size() - 1 - route.getIndex()).getLength()/sumLength[nodepair.getIndex()];
				route.setRate(curRate);
				
				int rst = rsa.SpectrumAllocating_MinimalDiff(request, route);
				
				if(rst == 0) {
					for(Link link : route.getLinkList()) {
						ResourceOnLink rol = new ResourceOnLink(link,route.getStartIndex(),route.getSlots(),request);
						rolList.add(rol);
						link.spectrumOccupy(route.getStartIndex(), route.getSlots(), request);
					}
				} else {
					fail = true;
					break loop;
				}
			}
			
			if(fail) {
				//release all resource allocated before
				for(ResourceOnLink rol : rolList) {
					rol.getLink().spectrumRelease(rol.getStartIndex(), rol.getSlots(), request);
				}
				nodepair.getRouteList().clear();
			}
		}
		time = System.currentTimeMillis() - time;
		System.out.println("Time-consuming(RSA) : " + time + " ms");
		
		// Part 3 Availability Calculation
		// TODO NOTE :
		Logger rstLogger = new Logger(dataPath + "output/system/system_" + networkName + "_" + k + "_" + minRate + "_" + maxRate + ".log");
		rstLogger.setLevel(Level.FATAL);
		System.out.println(">>Availability Calculation, the result will be output to File(" + rstLogger.getName() + ") ...");
		outputResult_Availability(layer, null);
		
		outputResult_SpectrumAllocation(layer,rstLogger);
		
		for(Link link : layer.getLinkList()) {
			link.outputUnusedSlotSections(sysLogger);
		}
		
		Logger.logln("", null);
	}
	
	public void outputResult_Availability(Layer layer, Logger logger) {
		double totalEstablishedBandwidth = 0;
		double Bandwidth_AvaWeghit = 0;
		double average_unavailability = 0;
		
		for(NodePair nodepair : layer.getNodePairList()) {		
			if(nodepair.getRouteList().size() != 0) {
				totalEstablishedBandwidth += nodepair.getRate();
				Bandwidth_AvaWeghit += nodepair.getRate() * nodepair.getNodepairAvailVCAT();
			} else {
				
			}
		}
		average_unavailability = (1 - Bandwidth_AvaWeghit / totalEstablishedBandwidth);
		Logger.logln("total Established Bandwidth : " + totalEstablishedBandwidth, logger);
		Logger.logln("Average network unavailability is " + average_unavailability, logger);
	}
	
	public void outputResult_SpectrumAllocation(Layer layer,Logger logger) {
		String format = "Route StartIndex EndIndex";
		Logger.logln(format,logger);
		
		for(NodePair nodepair : layer.getNodePairList()) {
			if(nodepair.getRouteList().size() != 0) {
				for(Route route : nodepair.getRouteList()) {
					if(route.getSlots() > 0) {
						Route.outputRoute(route, logger);
						String msg = " " + route.getStartIndex() + " " + (route.getStartIndex() + route.getSlots() - 1);
						Logger.logln(msg,logger);
					}
				}
			}
			
			Logger.logln("",logger);
		}
	}
}