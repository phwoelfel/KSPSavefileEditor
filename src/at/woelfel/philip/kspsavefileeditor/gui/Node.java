package at.woelfel.philip.kspsavefileeditor.gui;

import java.util.ArrayList;

public class Node {
	
	private String mNodeName;
	private Node mParentNode;
	
	private ArrayList<Entry> mEntries;
	private ArrayList<Node> mSubNodes;
	
	
	public Node(String name, Node parent) {
		mParentNode = parent;
		mNodeName = name;
	}


	/**
	 * @return the mNodeName
	 */
	public String getNodeName() {
		return mNodeName;
	}


	/**
	 * @param mNodeName the mNodeName to set
	 */
	public void setNodeName(String mNodeName) {
		this.mNodeName = mNodeName;
	}


	/**
	 * @return the mParentNode
	 */
	public Node getParentNode() {
		return mParentNode;
	}


	/**
	 * @param mParentNode the mParentNode to set
	 */
	public void setParentNode(Node mParentNode) {
		this.mParentNode = mParentNode;
	}


	/**
	 * @return the mEntries
	 */
	public ArrayList<Entry> getEntries() {
		return mEntries;
	}


	/**
	 * @param mEntries the mEntries to set
	 */
	public void setEntries(ArrayList<Entry> mEntries) {
		this.mEntries = mEntries;
	}
	
	
	public void addEntry(Entry entry){
		mEntries.add(entry);
	}
	
	public void removeEntry(Entry entry){
		mEntries.remove(entry);
	}


	/**
	 * @return the mSubNodes
	 */
	public ArrayList<Node> getSubNodes() {
		return mSubNodes;
	}


	/**
	 * @param mSubNodes the mSubNodes to set
	 */
	public void setSubNodes(ArrayList<Node> mSubNodes) {
		this.mSubNodes = mSubNodes;
	}
	
	public void addSubNode(Node node){
		mSubNodes.add(node);
	}
	
	public void removeSubNode(Node node){
		mSubNodes.remove(node);
	}
	
	
}
