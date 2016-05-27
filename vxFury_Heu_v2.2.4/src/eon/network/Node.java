package eon.network;

import java.util.ArrayList;

import eon.general.Constant;
import eon.general.object;

/**
 * @restructured by vxFury
 *
 */
public class Node extends object {
	private Layer associatedLayer = null;
	private ArrayList<Node> adjacentNodeList = null;

	private double costFromSrc = Constant.MAXIUM;
	private int hopFromSrc = Constant.MAXIUM;
	private double lengthFromSrc = Constant.MAXIUM;

	private int timesVisited = 0;
	private Node parentNode = null;

	private int x;
	private int y;

	private double BlockingProbability = 0;
	private double TrafficLoad = 0;

	public Node(String name, int index, String comments, Layer associatedLayer, int x, int y) {
		super(name, index, comments);
		this.associatedLayer = associatedLayer;
		this.adjacentNodeList = new ArrayList<Node>();
		this.x = x;
		this.y = y;
	}
	
	public double getTrafficLoad() {
		return TrafficLoad;
	}

	public void setTrafficLoad(double nodeTrafficLoad) {
		TrafficLoad = nodeTrafficLoad;
	}

	public double getBlockingProbability() {
		return BlockingProbability;
	}

	public void setBlockingProbability(double nodeBlockingProbability) {
		BlockingProbability = nodeBlockingProbability;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public Layer getAssociatedLayer() {
		return associatedLayer;
	}

	public void setAssociatedLayer(Layer associatedLayer) {
		this.associatedLayer = associatedLayer;
	}

	public ArrayList<Node> getAdjacentNodeList() {
		return adjacentNodeList;
	}

	public void setAdjacentNodeList(ArrayList<Node> adjacentNodeList) {
		this.adjacentNodeList = adjacentNodeList;
	}

	public double getCostFromSrc() {
		return costFromSrc;
	}

	public void setCostFromSrc(double costFromSrc) {
		this.costFromSrc = costFromSrc;
	}

	public int getHopFromSrc() {
		return hopFromSrc;
	}

	public void setHopFromSrc(int hopFromSrc) {
		this.hopFromSrc = hopFromSrc;
	}

	public double getLengthFromSrc() {
		return lengthFromSrc;
	}

	public void setLengthFromSrc(double lengthFromSrc) {
		this.lengthFromSrc = lengthFromSrc;
	}

	public int getTimesVisited() {
		return timesVisited;
	}

	public void setTimesVisited(int status) {
		this.timesVisited = status;
	}

	public Node getParentNode() {
		return parentNode;
	}

	public void setParentNode(Node parentNode) {
		this.parentNode = parentNode;
	}

	public void addAdjacentNode(Node node) {
		this.adjacentNodeList.add(node);
	}

	public void removeAdjacentNode(int index) {
		adjacentNodeList.remove(index);
	}

	public void removeAdjacentNode(Node node) {
		this.adjacentNodeList.remove(node);
	}

	public int getDegree() {
		return this.adjacentNodeList.size();
	}
}
