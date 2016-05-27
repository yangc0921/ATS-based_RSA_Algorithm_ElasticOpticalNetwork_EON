package eon.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

import eon.general.Constant;
import eon.network.*;

/**
 * @restructured by vxFury
 *
 */
public class RouteSearching {
	public void Dijkstras(Node srcNode, Node destNode, Layer layer, Route newRoute, SearchConstraint constraint) {
		ArrayList<Node> visitedNodeList = new ArrayList<Node>();

		// initialize all the node states
		for(Node node : layer.getNodeList()) {
			node.setTimesVisited(0);
			node.setParentNode(null);
			node.setCostFromSrc(Constant.MAXIUM);
			node.setHopFromSrc(Constant.MAXIUM);
		}
		
		// Initialization
		Node currentNode = srcNode;
		currentNode.setCostFromSrc(0);
		currentNode.setHopFromSrc(0);
		currentNode.setTimesVisited(2);
		
		if (constraint == null) {
			for (Node node : currentNode.getAdjacentNodeList()) {
				if (node.getTimesVisited() == 0) {
					Link link = layer.findLink(currentNode, node);
					node.setLengthFromSrc(currentNode.getLengthFromSrc() + link.getLength());
					node.setCostFromSrc(currentNode.getCostFromSrc() + link.getCost());
					node.setHopFromSrc(currentNode.getHopFromSrc() + 1);
					node.setTimesVisited(1);
					node.setParentNode(currentNode);
					visitedNodeList.add(node);
				}
			}
		} else {
			for (Node node : currentNode.getAdjacentNodeList()) {
				if (!constraint.getExcludedNodeList().contains(node)) {
					if (node.getTimesVisited() == 0) {
						Link link = layer.findLink(currentNode, node);
						if (!constraint.containsLink(link)) {
							node.setLengthFromSrc(currentNode.getLengthFromSrc() + link.getLength());
							node.setCostFromSrc(currentNode.getCostFromSrc() + link.getCost());
							node.setHopFromSrc(currentNode.getHopFromSrc() + 1);
							node.setTimesVisited(1);
							node.setParentNode(currentNode);
							visitedNodeList.add(node);
						}
					}
				}
			}
		}

		// find the node with the lowest cost from the visited node list
		currentNode = this.getLowestCostNode(visitedNodeList);
		if (currentNode != null) {
			while (!currentNode.equals(destNode)) {
				// set the current node double visited
				currentNode.setTimesVisited(2);
				// remove the node from the visited node list
				visitedNodeList.remove(currentNode);

				// navigate the adjacentghboring nodes of the current node
				if (constraint == null) {
					for (Node node : currentNode.getAdjacentNodeList()) {
						if (node.getTimesVisited() == 0) { // if the adjacent node is not visited
							Link link = layer.findLink(currentNode, node);
							node.setLengthFromSrc(currentNode.getLengthFromSrc() + link.getLength());
							node.setCostFromSrc(currentNode.getCostFromSrc() + link.getCost());
							node.setHopFromSrc(currentNode.getHopFromSrc() + 1);
							node.setTimesVisited(1);
							node.setParentNode(currentNode);
							visitedNodeList.add(node);
						} else if (node.getTimesVisited() == 1) { // if the adjacent node is first visited
							Link link = layer.findLink(currentNode, node);
							if (node.getCostFromSrc() > currentNode.getCostFromSrc() + link.getCost()) {
								// update the node status
								node.setCostFromSrc(currentNode.getCostFromSrc() + link.getCost());
								node.setParentNode(currentNode);
							}
						}
					}
				} else {
					for (Node node : currentNode.getAdjacentNodeList()) {
						if (!constraint.getExcludedNodeList().contains(node)) {
							if (node.getTimesVisited() == 0) { // if the adjacent node is not visited
								Link link = layer.findLink(currentNode, node);
								if (!constraint.containsLink(link)) {
									node.setCostFromSrc(currentNode.getCostFromSrc() + link.getCost());
									node.setHopFromSrc(currentNode.getHopFromSrc() + 1);
									node.setTimesVisited(1);
									node.setParentNode(currentNode);
									visitedNodeList.add(node);
								}
							} else if (node.getTimesVisited() == 1) { // if the adjacent node is first visited
								String name;
								if (currentNode.getIndex() < node.getIndex()) {
									name = currentNode.getName() + "-" + node.getName();
								} else {
									name = node.getName() + "-" + currentNode.getName();
								}
								Link link = layer.getLinkMap().get(name);
								if (!constraint.containsLink(link)) {
									if (node.getCostFromSrc() > currentNode.getCostFromSrc() + link.getCost()) {
										// update the node status
										node.setCostFromSrc(currentNode.getCostFromSrc() + link.getCost());
										node.setHopFromSrc(currentNode.getHopFromSrc() + 1);
										node.setParentNode(currentNode);
									}
								}
							}
						}
					}
				}

				// find the node with the lowest cost from the visited node list
				currentNode = this.getLowestCostNode(visitedNodeList);
				if (currentNode == null) {
					break;
				}
			}
		}
		// clear the route
		newRoute.getNodeList().clear();
		newRoute.getLinkList().clear();
		// add the visited nodes into the node list
		currentNode = destNode;
		if (destNode.getParentNode() != null) {
			newRoute.getNodeList().add(0, currentNode);
			newRoute.setCost(destNode.getCostFromSrc());

			double length = 0;
			while (currentNode != srcNode) {
				Link link = layer.findLink(currentNode, currentNode.getParentNode());
				newRoute.addLink(link);
				currentNode = currentNode.getParentNode();
				newRoute.getNodeList().add(0, currentNode);
				length += link.getLength();
			}
			newRoute.setLength(length);
			newRoute.setModulation(newRoute.autosetModulation());
		} else {
			newRoute = null;
		}
	}

	public void Kshortest(Node srcNode, Node destNode, Layer layer, int k, ArrayList<Route> routelist) {
		routelist.clear();
		SearchConstraint constraint = new SearchConstraint(layer);

		int num_found = 0; // number of found route
		while (true) {
			Route newRoute = new Route(layer,"", num_found, "");
			this.Dijkstras(srcNode, destNode, layer, newRoute, constraint);
			if (newRoute.getLinkList().size() > 0) {
				routelist.add(newRoute);
				constraint.addAllLinks(newRoute.getLinkList());
				num_found++;
				if (num_found == k) {
					break;
				}
			} else {
				break;
			}
		}
	}
	
	public void Kshortest(Node srcNode, Node destNode, Layer layer, int k, double lengthLimit, ArrayList<Route> routelist) {
		routelist.clear();
		SearchConstraint constraint = new SearchConstraint(layer);

		int num_found = 0; // number of found route
		while (true) {
			Route newRoute = new Route(layer, "", num_found, "");
			this.Dijkstras(srcNode, destNode, layer, newRoute, constraint);
			if (newRoute.getLinkList().size() > 0) {
				if (newRoute.getLength() <= lengthLimit) {
					routelist.add(newRoute);
					constraint.addAllLinks(newRoute.getLinkList());
					num_found++;
					if (num_found == k) {
						break;
					}
				}
			} else {
				break;
			}
		}
	}

	// find a node from the visited node list that is closest to the src node
	private Node getLowestCostNode(ArrayList<Node> visitedNodeList) {
		Node currentnode = null;
		double current_cost_to_desc = 100000000;
		for (Node node : visitedNodeList) {
			if (node.getCostFromSrc() < current_cost_to_desc) {
				currentnode = node;
				current_cost_to_desc = node.getCostFromSrc();
			}
		}
		return currentnode;
	}

	public Node getLowestCostNode(Layer layer) {
		Node currentnode = null;
		double current_cost_to_desc = Constant.MAXIUM;
		HashMap<String, Node> map = layer.getNodeMap();
		Iterator<String> iter = map.keySet().iterator();
		while (iter.hasNext()) {
			Node node = (Node) (map.get(iter.next()));
			if (node.getTimesVisited() == 0) {
				if (node.getCostFromSrc() < current_cost_to_desc) {
					currentnode = node;
					current_cost_to_desc = node.getCostFromSrc();
				}
			}
		}
		if (currentnode != null)
			currentnode.setTimesVisited(1);
		return currentnode;
	}
	
	public static boolean searchRoutes(Node curNode, Node prevNode, Node srcNode, Node desNode, SearchConstraint constraint, int hoplimit, double lengthLimit, Layer layer, Stack<Node> stack, ArrayList<Route> routeList) {
		Node nxtNode = null;
		
		if (curNode != null && prevNode != null ) {
			if(curNode == prevNode) {
				return false;
			} else {
				if(constraint != null) {
					Link link = layer.findLink(prevNode, curNode);
					if(constraint.containsLink(link)) {
						return false;
					}
				}
			}
		}
		
		if (curNode != null) {
			int i = 0;
			
			if(stack.size() >= hoplimit) {
				return false;
			}
			
			stack.push(curNode);
			
			if (curNode == desNode) {
				Route route = new Route(layer,"",routeList.size(),"");
				int size = stack.size();
				double length = 0,cost = 0;
				for(int j = 0;j < size;j ++) {
					route.getNodeList().add(stack.get(j));
					
					Link link = null;
					if(j != 0) {
						link = layer.findLink(stack.get(j - 1), stack.get(j));
						
						route.addLink(link);
						length += link.getLength();
						cost += link.getCost();
						
						stack.get(j).setCostFromSrc(stack.get(j - 1).getCostFromSrc() + link.getCost());
					}
				}
				
				if(length <= lengthLimit) {
					route.setLength(length);
					route.setCost(cost);
					route.setModulation(route.autosetModulation());
					routeList.add(route);
					return true;
				} else {
					return false;
				}
			} else {
				nxtNode = curNode.getAdjacentNodeList().get(i);
				while (nxtNode != null) {
					if (prevNode != null && (nxtNode == srcNode || nxtNode == prevNode || stack.contains(nxtNode))) {
						i ++;
						if (i >= curNode.getAdjacentNodeList().size()) {
							nxtNode = null;
						} else {
							nxtNode = curNode.getAdjacentNodeList().get(i);
						}
						continue;
					}
					
					if (searchRoutes(nxtNode, curNode, srcNode, desNode, constraint, hoplimit, lengthLimit, layer, stack, routeList)) {
						stack.pop();
					}
					
					i ++;
					if (i >= curNode.getAdjacentNodeList().size()) {
						nxtNode = null;
					} else {
						nxtNode = curNode.getAdjacentNodeList().get(i);
					}
				}
				
				stack.pop();
				return false;
			}
		} else {
			return false;
		}
	}
	
	public void searchAllRoutes(Node srcNode, Node desNode, Layer layer,SearchConstraint constraint, int hoplimit, double lengthLimit, ArrayList<Route> routeList) {
		Stack<Node> stack = new Stack<Node>();
		
		searchRoutes(srcNode, null, srcNode, desNode, constraint, hoplimit, lengthLimit, layer, stack, routeList);
		
		Collections.sort(routeList,new sortByLength());
	}
}

class sortByLength implements Comparator<Object> {
	public int compare(Object obj1,Object obj2) {
		Route route1 = (Route) obj1;
		Route route2 = (Route) obj2;
		
		return (int) (route1.getLength() - route2.getLength());
	}
}
