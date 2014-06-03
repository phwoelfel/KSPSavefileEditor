package at.woelfel.philip.kspsavefileeditor.backend;

import java.util.ArrayList;

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
	
	public ArrayList<Object> getPathToRoot(ArrayList<Object> path){
		path.add(this);
		if(mParentNode==null){
			return path;
		}
		else{
			return mParentNode.getPathToRoot(path);
		}
	}
	
	public String print(int tabs){
		StringBuilder sb = new StringBuilder();
		appendLine(sb, tabs, getNodeName());
		appendLine(sb, tabs, "{");
		
		for (Entry entry : getEntries()) {
			appendLine(sb, tabs+1, entry);
		}
		for (Node node : getSubNodes()){
			sb.append(node.print(tabs+1));
		}
		appendLine(sb, tabs, "}");
		
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
}
