package eon.network;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import eon.general.Constant;
import eon.general.object;
import eon.graph.RouteSearching;

/**
 * @restructured by vxFury
 *
 */
public class Layer extends object {
	public static final String NodePairHyphen = "--";
	public static final String LinkHyphen = "-";

	private HashMap<String, Node> nodemap = null;
	private HashMap<String, Link> linkmap = null;
	private HashMap<String, NodePair> nodepairmap = null;

	private ArrayList<Node> nodelist = null;
	private ArrayList<Link> linklist = null;
	private ArrayList<NodePair> nodepairlist = null;

	public Layer(String name, int index, String comments) {
		super(name, index, comments);

		nodemap = new HashMap<String, Node>();
		linkmap = new HashMap<String, Link>();
		nodepairmap = new HashMap<String, NodePair>();

		nodelist = new ArrayList<Node>();
		linklist = new ArrayList<Link>();
		nodepairlist = new ArrayList<NodePair>();
	}

	public HashMap<String, Node> getNodeMap() {
		return nodemap;
	}

	public void setNodeMap(HashMap<String, Node> nodemap) {
		this.nodemap = nodemap;
	}

	public HashMap<String, Link> getLinkMap() {
		return linkmap;
	}

	public void setLinkMap(HashMap<String, Link> linkmap) {
		this.linkmap = linkmap;
	}

	public HashMap<String, NodePair> getNodepairMap() {
		return nodepairmap;
	}

	public void setNodepairMap(HashMap<String, NodePair> nodepairmap) {
		this.nodepairmap = nodepairmap;
	}

	public ArrayList<Node> getNodeList() {
		return nodelist;
	}

	public void setNodeList(ArrayList<Node> nodelist) {
		this.nodelist = nodelist;
	}

	public ArrayList<Link> getLinkList() {
		return linklist;
	}

	public void setLinkList(ArrayList<Link> linklist) {
		this.linklist = linklist;
	}

	public ArrayList<NodePair> getNodePairList() {
		return nodepairlist;
	}

	public void setNodePairList(ArrayList<NodePair> nodepairlist) {
		this.nodepairlist = nodepairlist;
	}

	public void addNode(Node node) {
		this.nodemap.put(node.getName(), node);
		this.nodelist.add(node);
		node.setAssociatedLayer(this);
	}

	public void removeNode(String nodename) {
		Node node = this.nodemap.get(nodename);
		node.setAssociatedLayer(null);
		String linkname = "";
		for (int i = 0; i < node.getAdjacentNodeList().size(); i++) {
			Node nodeB = node.getAdjacentNodeList().get(i);
			if (node.getIndex() < nodeB.getIndex()) {
				linkname = node.getName() + Layer.LinkHyphen + nodeB.getName();
			} else {
				linkname = nodeB.getName() + Layer.LinkHyphen + node.getName();
			}
			this.linkmap.remove(linkname);
			this.linklist.remove(linkname);
		}
		this.nodemap.remove(nodename);
		this.nodelist.remove(nodename);
	}

	public void addLink(Link link) {
		this.linkmap.put(link.getName(), link);
		this.linklist.add(link);
		link.setAssociatedLayer(this);
	}

	public void removeLink(String linkname) {
		this.linkmap.get(linkname).setAssociatedLayer(null);
		this.linkmap.remove(linkname);
		this.linklist.remove(linkname);
	}

	public void addNodepair(NodePair nodepair) {
		this.nodepairmap.put(nodepair.getName(), nodepair);
		this.nodepairlist.add(nodepair);
		nodepair.setAssociatedLayer(this);
	}

	public void removeNodepair(String nodepairname) {
		this.nodepairmap.get(nodepairname).setAssociatedLayer(null);
		this.nodepairmap.remove(nodepairname);
		this.nodepairlist.remove(nodepairname);
	}

	public void copyNodes(Layer layer) {
		HashMap<String, Node> map = layer.getNodeMap();
		Iterator<String> iter1 = map.keySet().iterator();
		while (iter1.hasNext()) {
			Node nodeA = (Node) (map.get(iter1.next()));
			Node nodeB = new Node(nodeA.getName(), nodeA.getIndex(), "", this, nodeA.getX(), nodeA.getY());
			this.addNode(nodeB);
		}
	}

	public void generateNodePairs() {
		HashMap<String, Node> map = this.getNodeMap();
		HashMap<String, Node> map2 = this.getNodeMap();
		Iterator<String> iter1 = map.keySet().iterator();
		while (iter1.hasNext()) {
			Node node1 = (Node) (map.get(iter1.next()));
			Iterator<String> iter2 = map2.keySet().iterator();
			while (iter2.hasNext()) {
				Node node2 = (Node) (map.get(iter2.next()));
				if (!node1.equals(node2)) {
					if (node1.getIndex() < node2.getIndex()) {
						String name = node1.getName() + Layer.NodePairHyphen + node2.getName();
						int index = this.nodepairmap.size();
						NodePair nodepair = new NodePair(name, index, "", this, node1, node2);
						this.addNodepair(nodepair);
					}
				}
			}
		}
	}

	public Link findLink(Node nodeA, Node nodeB) {
		String name;
		if (nodeA.getIndex() < nodeB.getIndex()) {
			name = nodeA.getName() + Layer.LinkHyphen + nodeB.getName();
		} else {
			name = nodeB.getName() + Layer.LinkHyphen + nodeA.getName();
		}

		return this.getLinkMap().get(name);
	}

	public Node findNode(String nodeName, Layer layer) {
		HashMap<String, Node> map = layer.getNodeMap();
		Iterator<String> iter = map.keySet().iterator();
		Node currentnode = null;
		while (iter.hasNext()) {
			currentnode = (Node) (map.get(iter.next()));
			if (currentnode.getName().endsWith(nodeName))
				break;
		}
		return currentnode;
	}

	public NodePair findNodepair(String nodepairName) {
		HashMap<String, NodePair> map = this.getNodepairMap();
		Iterator<String> iter = map.keySet().iterator();
		NodePair currentnodepair = null;
		while (iter.hasNext()) {
			currentnodepair = (NodePair) (map.get(iter.next()));
			if (currentnodepair.getName().endsWith(nodepairName))
				break;
		}
		return currentnodepair;
	}

	public void clearCost() {
		HashMap<String, Node> map = this.getNodeMap();
		Iterator<String> iter = map.keySet().iterator();
		while (iter.hasNext()) {
			Node node = (Node) (map.get(iter.next()));
			node.setCostFromSrc(Constant.MAXIUM);
		}
	}

	public void readTopology(String topologyFile) {
		String[] data = new String[10];
		File file = new File(topologyFile);
		BufferedReader bufRdr = null;
		try {
			bufRdr = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String line = null;
		int col = 0;
		try {
			line = bufRdr.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// read the first title line
		// read each line of text file
		try {
			boolean link = false;
			while ((line = bufRdr.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line, ",");
				while (st.hasMoreTokens()) {
					data[col] = st.nextToken();
					col++;
				}
				col = 0;
				String name = data[0];
				if (name.equals("Link")) {
					link = true;
				}
				// read nodes
				if (!link)// node operation
				{
					int x = Integer.parseInt(data[1]);
					int y = Integer.parseInt(data[2]);
					int index = this.getNodeMap().size();
					Node newnode = new Node(name, index, "", this, x, y);
					this.addNode(newnode);
				} else { // link operation
					if (!(name.equals("Link"))) {
						Node nodeA = this.getNodeMap().get(data[1]);
						Node nodeB = this.getNodeMap().get(data[2]);
						double length = Double.parseDouble(data[3]);
						double cost = Double.parseDouble(data[4]);
						int index = this.getLinkMap().size();

						if (nodeA.getIndex() < nodeB.getIndex()) {
							name = nodeA.getName() + Layer.LinkHyphen + nodeB.getName();
						} else {
							name = nodeB.getName() + Layer.LinkHyphen + nodeA.getName();
						}
						Link alink = new Link(name, index, "", this, nodeA, nodeB, length, cost);
						this.addLink(alink);

						// update the adjacent node list
						nodeA.addAdjacentNode(nodeB);
						nodeB.addAdjacentNode(nodeA);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			bufRdr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void copyLayer(Layer layer) {
		HashMap<String, Node> Copynodemap = new HashMap<String, Node>();

		HashMap<String, Node> map = layer.getNodeMap();
		Iterator<String> iter = map.keySet().iterator();
		while (iter.hasNext()) {
			Node node = (Node) (map.get(iter.next()));
			Copynodemap.put(node.getName(), node);

		}
		this.setNodeMap(Copynodemap);

		HashMap<String, Link> Copylinkmap = new HashMap<String, Link>();
		HashMap<String, Link> mapLink = this.getLinkMap();
		Iterator<String> iterLink = mapLink.keySet().iterator();

		while (iterLink.hasNext()) {
			Link link = (Link) (mapLink.get(iterLink.next()));

			Copylinkmap.put(link.getName(), link);
		}
		this.setLinkMap(Copylinkmap);
	}
	
	public static void searchKshortesPath(Layer layer, int k,double lengthLimit) {
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
}
