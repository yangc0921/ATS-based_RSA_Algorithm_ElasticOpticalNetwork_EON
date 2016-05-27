package work.PathMN;

import java.text.SimpleDateFormat;
import java.util.Date;

import eon.general.Modulation;
import eon.graph.RouteSearching;
import eon.network.Layer;
import eon.network.NodePair;
import work.utilities.Logger;
import work.utilities.Logger.Level;
import work.forAMPL.*;

/**
 * @author vxFury
 *
 */
public class dataForAMPL_ProtectionPathM1 {
	public static void dataForAMPL (String dataPath, String networkName, Strategy strategy) {
		Layer layer = new Layer("workLayer", 0, "");
		String topology = dataPath + "topology/" + networkName + ".csv";
		System.out.print(">>Importing Topology of Network(" + topology + ") ...\n");
		layer.readTopology(topology);
		layer.generateNodePairs();
		
		Logger NetDataLogger = new Logger(dataPath + "output/data/data_M1_" + strategy.getName() + "_" + networkName + ".dat");
		NetDataLogger.setLevel(Level.FATAL);
		
		System.out.println(">>Data for AMPL will be output to File(" + NetDataLogger.getName() + ") ...");
		
		dataForAMPL dfl = new dataForAMPL();
		
		dfl.outputSetOfLinks(layer, NetDataLogger);
		dfl.outputSetOfNodePairs(layer, NetDataLogger);
		
		if(strategy == Strategy.KSP) {
			dfl.searchKshortesPath(layer, 3, Modulation.BPSK.getTransDistance());
		} else if (strategy == Strategy.BFS) {
			RouteSearching rs = new RouteSearching();
			
			for(NodePair nodepair : layer.getNodePairList()) {
				rs.searchAllRoutes(nodepair.getSrcNode(),nodepair.getDesNode(),layer,null,9,Modulation.BPSK.getTransDistance(),nodepair.getRouteList());
			}
		}
		
		dfl.outputSetOfCandidateRoutes(layer, NetDataLogger);
		dfl.outputParamOfCapacityPerSlot(layer, NetDataLogger);
		dfl.outputParamOfTrafficDemand(layer, NetDataLogger);
		dfl.outputParamOfUnavailability(layer, NetDataLogger);
		dfl.outputParamOfDelta(layer, NetDataLogger);
	}
	
	public static void main(String[] args) {
		String dataPath = "./data/";
		String[] netName = { "cost239" };// { "n6s8", "smallnet", "cost239", "nsfnet", "usnet" };
		
		for (String net : netName) {
			long time = System.currentTimeMillis();
			SimpleDateFormat date = new SimpleDateFormat("MM/DD/YYYY HH:mm:ss");
			System.out.println("********************************************************************************");
			System.out.println("Topology File : " + net + ".csv\r\n" + date.format(new Date()) + "\r\n");
			
			Strategy strategy = Strategy.KSP;
			dataForAMPL(dataPath, net, strategy);

			System.out.println("");

			System.out.println(date.format(new Date()));
			System.out.println("End of dataForAMPL : " + (net + ".csv"));
			time = System.currentTimeMillis() - time;
			System.out.println("Time-consuming : " + time + " ms");
			System.out.println("********************************************************************************\r\n");
		}
	}
	
	public enum Strategy {
		BFS("BFS", 0), KSP("KSP", 1);
		private String name;
		private int index;

		private Strategy(String Name,int index) {
			this.name = Name;
			this.setIndex(index);
		}

		public String getName() {
			return name;
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}
	}
}