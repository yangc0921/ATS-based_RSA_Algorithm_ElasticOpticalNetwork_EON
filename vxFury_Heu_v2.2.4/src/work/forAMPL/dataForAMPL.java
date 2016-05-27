package work.forAMPL;

import java.util.ArrayList;

import eon.network.Layer;
import eon.network.Link;
import eon.network.Node;
import eon.network.NodePair;
import eon.graph.RouteSearching;
import eon.network.Route;
import work.utilities.Logger;

/**
 * @author vxFury
 *
 */
public class dataForAMPL {
	public void outputSetOfNodes(Layer layer, Logger logger) {
		Logger.logln("set Nodes :=", logger);
		for (Node node : layer.getNodeList()) {
			Logger.logln(node.getName(),logger);
		}
		Logger.logln(";\n",logger);
	}

	public void outputSetOfNodePairs(Layer layer, Logger logger) {
		Logger.logln("set NP :=",logger);
		for (NodePair nodepair : layer.getNodePairList()) {
			Logger.logln(nodepair.getName(),logger);
		}
		Logger.logln(";\n",logger);
	}

	public void outputSetOfLinks(Layer layer, Logger logger) {
		Logger.logln("set E :=",logger);
		for (Link link : layer.getLinkList()) {
			Logger.logln(link.getName(),logger);
		}
		Logger.logln(";\n",logger);
	}

	public void searchKshortesPath(Layer layer, int k) {
		for (NodePair nodepair : layer.getNodePairList()) {
			ArrayList<Route> routelist = new ArrayList<Route>();

			RouteSearching workpathsearch = new RouteSearching();
			workpathsearch.Kshortest(nodepair.getSrcNode(), nodepair.getDesNode(), layer, k, routelist);

			nodepair.setLeastWorkingHop(routelist.get(0).getLinkList().size());
			nodepair.setLeastProtectionHop(routelist.get(1).getLinkList().size());
			nodepair.setLeastHop(routelist.get(0).getLinkList().size());
			nodepair.setRouteList(routelist);
		}
	}
	
	public void searchKshortesPath(Layer layer, int k,double lengthLimit) {
		for (NodePair nodepair : layer.getNodePairList()) {
			ArrayList<Route> routelist = new ArrayList<Route>();

			RouteSearching workpathsearch = new RouteSearching();
			workpathsearch.Kshortest(nodepair.getSrcNode(), nodepair.getDesNode(), layer, k, lengthLimit, routelist);

			nodepair.setLeastWorkingHop(routelist.get(0).getLinkList().size());
			nodepair.setLeastProtectionHop(routelist.get(1).getLinkList().size());
			nodepair.setLeastHop(routelist.get(0).getLinkList().size());
			nodepair.setRouteList(routelist);
		}
	}

	public void outputRoute(Route route, Logger logger) {
		int i = 1;
		for (Node node : route.getNodeList()) {
			if (i < route.getNodeList().size()) {
				Logger.log(node.getName() + "-",logger);
			} else {
				Logger.log(node.getName(),logger);
			}
			i++;
		}
	}
	
	public void outputSetOfCandidateRoutes(Layer layer, Logger logger) {
		for (NodePair nodepair : layer.getNodePairList()) {
			Logger.logln("set R[" + nodepair.getName() + "] :=",logger);
			for (Route route : nodepair.getRouteList()) {
				outputRoute(route, logger);
				Logger.logln("",logger);
			}
			Logger.logln(";",logger);
		}
		Logger.logln("",logger);
	}
	
	public void outputSetOfTraversedLinks(Layer layer, Logger logger) {
		for (NodePair nodepair : layer.getNodePairList()) {
			for (Route route : nodepair.getRouteList()) {
				Logger.log("set TraversedLinks[" + nodepair.getName() + " ",logger);
				outputRoute(route, logger);
				Logger.logln("] :=",logger);

				for (Link link : route.getLinkList()) {
					Logger.logln(link.getName(),logger);
				}
				Logger.logln(";",logger);
			}
		}
		Logger.logln("",logger);
	}
	
	public void outputParamOfCapacityPerSlot(Layer layer, Logger logger) {
		Logger.logln("param Cap := ",logger);
		for(NodePair nodepair : layer.getNodePairList()) {
			for (Route route : nodepair.getRouteList()) {
				Logger.log(nodepair.getName() + " ",logger);
				outputRoute(route, logger);
				Logger.logln(" " + route.getModulation().getCapacity(),logger);
			}
		}
		Logger.logln(";\r\n",logger);
	}
	
	public void outputParamOfUnavailability(Layer layer,Logger logger) {
		Logger.logln("param uaR:= ",logger);
		for (NodePair nodepair : layer.getNodePairList()) {
			for (Route route : nodepair.getRouteList()) {
				Logger.log(nodepair.getName() + " ",logger);
				Route.outputRoute(route, logger);
				Logger.logln(" " + (1 - route.getAvailRouteSingle()),logger);
			}
		}
		Logger.logln(";\r\n",logger);
	}
	
	public boolean IsLinkToRoute(Route route, Link link) {
		boolean key = false;
		for (Link link_tmp : route.getLinkList()) {
			if (link_tmp.getName() == link.getName()) {
				key = true;
				break;
			}
		}

		return key;
	}

	public void outputParamOfDelta(Layer layer, Logger logger) {
		Logger.logln("param delta :=",logger);
		for (Link link : layer.getLinkList()) {
			for (NodePair nodepair : layer.getNodePairList()) {
				for (Route route : nodepair.getRouteList()) {
					Logger.log(link.getName() + " " + (nodepair.getName()) + " ",logger);
					outputRoute(route, logger);
					
					Logger.logln(" " + (IsLinkToRoute(route, link) ? (1) : (0)),logger);
				}
			}
		}
		Logger.logln(";\r\n",logger);
	}
	
	public void outputParamOfTrafficDemand(Layer layer, Logger logger) {
		Logger.logln("param TD :=",logger);
		for (NodePair nodepair : layer.getNodePairList()) {
			Logger.logln(nodepair.getName() + " " + nodepair.getRate(),logger);
		}
		Logger.logln(";\r\n",logger);
	}
	
	public int traverseCommonLink (Route route1,Route route2) {
		int rst = 0;
		
		for(Link link1 : route1.getLinkList()) {
			for(Link link2 : route2.getLinkList()) {
				if(link1.getName().compareTo(link2.getName()) == 0) {
					rst = 1;
				}
			}
		}
		
		return rst;
	}
	
	public int isLinkToRoutes(Link link,Route route1,Route route2) {
		int rst = 0;
		
		if(IsLinkToRoute(route1,link) && IsLinkToRoute(route2,link)) {
			rst = 1;
		}
		
		return rst;
	}
	
	public void outputParamOfIsLinkToRoutes(Layer layer,Logger logger) {
		Logger.logln("param IsLinkToRoutes :=",logger);
		for(Link link : layer.getLinkList()) {
			for(NodePair nodepair1 : layer.getNodePairList()) {
				for(Route route1 : nodepair1.getRouteList()) {
					for(NodePair nodepair2 : layer.getNodePairList()) {
						if(nodepair2.getName().compareTo(nodepair1.getName()) != 0) {
							for(Route route2 : nodepair2.getRouteList()) {
								String msg = link.getName() + " " + nodepair1.getName() + " ";
								Logger.log(msg,logger);
								outputRoute(route1,logger);
								msg = " " + nodepair2.getName() + " ";
								Logger.log(msg,logger);
								outputRoute(route2,logger);
								msg = " " + isLinkToRoutes(link,route1,route2);
								Logger.logln(msg,logger);
							}
						}
					}
				}
			}
		}
		Logger.logln(";\r\n",logger);
	}
	
	public void outputParamOfIsRouteShareLinks(Layer layer, Logger logger) {
		Logger.logln("param IsRouteShareLinks :=",logger);
		for(NodePair nodepair1 : layer.getNodePairList()) {
			for(Route route1 : nodepair1.getRouteList()) {
				for(NodePair nodepair2 : layer.getNodePairList()) {
					if(nodepair2.getName().compareTo(nodepair1.getName()) != 0) {
						for(Route route2 : nodepair2.getRouteList()) {
							String msg = nodepair1.getName() + " ";
							Logger.log(msg,logger);
							outputRoute(route1,logger);
							msg = " " + nodepair2.getName() + " ";
							Logger.log(msg,logger);
							outputRoute(route2,logger);
							msg = " " + traverseCommonLink(route1,route2);
							Logger.logln(msg,logger);
						}
					}
				}
			}
		}
		Logger.logln(";\r\n",logger);
	}
}
