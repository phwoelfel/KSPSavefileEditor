package at.woelfel.philip.kspsavefileeditor.backend;

import java.util.ArrayList;

import javax.swing.tree.TreePath;

public class Node {
	
	private String mNodeName;
	private Node mParentNode;
	
	private ArrayList<Entry> mEntries;
	private ArrayList<Node> mSubNodes;
	
	private String NL = System.getProperty("line.separator");
	
	
	public Node(String name, Node parent) {
		mParentNode = parent;
		mNodeName = name;
		mEntries = new ArrayList<Entry>();
		mSubNodes = new ArrayList<Node>();
	}

	
	
	public String getNodeName() {
		return mNodeName;
	}

	public void setNodeName(String mNodeName) {
		this.mNodeName = mNodeName;
	}

	
	
	public Node getParentNode() {
		return mParentNode;
	}

	public void setParentNode(Node mParentNode) {
		this.mParentNode = mParentNode;
	}

	public boolean hasParent(){
		if(mParentNode!=null){
			return true;
		}
		return false;
	}
	


	public ArrayList<Entry> getEntries() {
		return mEntries;
	}

	public void setEntries(ArrayList<Entry> mEntries) {
		this.mEntries = mEntries;
	}
	
	public Entry getEntry(int id){
		return mEntries!=null?mEntries.get(id):null;
	}
	
	public void addEntry(Entry entry){
		mEntries.add(entry);
	}
	
	public void createEntry(String key, String value){
		Entry e = new Entry(this, key, value);
		addEntry(e);
	}
	
	public void removeEntry(Entry entry){
		mEntries.remove(entry);
	}
	
	public int getEntryCount(){
		return mEntries!=null?mEntries.size():0;
	}



	public ArrayList<Node> getSubNodes() {
		return mSubNodes;
	}

	public void setSubNodes(ArrayList<Node> mSubNodes) {
		this.mSubNodes = mSubNodes;
	}
	
	public Node getSubNode(int id){
		return mSubNodes!=null?mSubNodes.get(id):null;
	}
	
	public void addSubNode(Node node){
		if(mSubNodes!=null){
			mSubNodes.add(node);
		}
	}
	
	public void createSubNode(String nodeName){
		Node n = new Node(nodeName, this);
		addSubNode(n);
	}
	
	public void removeSubNode(Node node){
		if(mSubNodes!=null){
			mSubNodes.remove(node);
		}
	}
	
	public int getSubNodeCount(){
		return mSubNodes!=null?mSubNodes.size():0;
	}
	
	public boolean hasSubNodes(){
		if(mSubNodes == null || mSubNodes.size() == 0){
			return false;
		}
		return true;
	}
	
	
	@Override
	public String toString() {
		for(Entry e : getEntries()){
			if("name".equals(e.getKey()) || "part".equals(e.getKey())){
				return mNodeName +" (" +e.getValue() +")";
			}
		}
		return mNodeName;
	}
	
	public ArrayList<Object> getPathToRoot(){
		
		if(mParentNode==null){
			ArrayList<Object> path = new ArrayList<Object>();
			path.add(this);
			return path;
		}
		else{
			ArrayList<Object> path = mParentNode.getPathToRoot();
			path.add(this);
			return path;
		}
	}
	
	public TreePath getTreePathToRoot(){
		return new TreePath(getPathToRoot());
	}
	
	public String print(int tabs){
		StringBuilder sb = new StringBuilder();
		
		if(!"".equals(getNodeName())){ // empty node name means we don't want to save the node name to file (root node for craft or config files)
			appendLine(sb, tabs, getNodeName());
			appendLine(sb, tabs, "{");
		}
		else{
			tabs--; // decrease tabs so it isn't indented
		}
		for (Entry entry : getEntries()) {
			appendLine(sb, tabs+1, entry);
		}
		for (Node node : getSubNodes()){
			sb.append(node.print(tabs+1));
		}
		if(!"".equals(getNodeName())){
			appendLine(sb, tabs, "}");
		}
		
		return sb.toString();
	}
	
	private void appendLine(StringBuilder sb, int tabs, Object o){
		sb.append(getTabs(tabs));
		sb.append(o);
		sb.append(NL);
	}
	
	private String getTabs(int tabs){
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<tabs;i++){
			sb.append("\t");
		}
		return sb.toString();
	}
	
	public TreePath search(String search){
		// check yourself
		if(getNodeName().contains(search)){
			return getTreePathToRoot();
		}
		// check entries
		for (Entry entry : getEntries()) {
			if(entry.search(search)){
				ArrayList<Object> path = getPathToRoot();
				path.add(entry);
				return new TreePath(path.toArray());
			}
		}
		// check subnodes
		for (Node node : getSubNodes()) {
			TreePath tp = node.search(search);
			if(tp!=null){ // subnode found something and gives us path to root
				return tp;
			}
		}
		
		return null; // we didn't find anything
	}
}
