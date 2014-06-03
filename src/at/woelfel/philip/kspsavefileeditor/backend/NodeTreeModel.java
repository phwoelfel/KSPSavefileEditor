package at.woelfel.philip.kspsavefileeditor.backend;

import java.util.ArrayList;
import java.util.Collections;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class NodeTreeModel implements TreeModel {

	private Node mRootNode;
	private ArrayList<TreeModelListener> mTreeListener;
	
	public NodeTreeModel(Node rootNode) {
		mRootNode = rootNode;
		mTreeListener = new ArrayList<TreeModelListener>();
	}
	

	@Override
	public Object getChild(Object parent, int index) {
		if(parent!=null && parent instanceof Node){
			Node n = (Node)parent;
			if(index<n.getEntryCount()){ //first are the entries
				return n.getEntry(index);
			}
			else{
				return n.getSubNode(index-n.getEntryCount()); // then the subnodes follow
			}
		}
		return null;
		
	}

	@Override
	public int getChildCount(Object parent) {
		if(parent != null && parent instanceof Node){
			Node n = (Node)parent;
			return n.getSubNodeCount()+n.getEntryCount(); // just add subnode and entry count
		}
		return 0;
		
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		if(parent!=null && child!=null && parent instanceof Node){
			Node n = (Node)parent;
			if(n.getSubNodes().contains(child)){
				// subnode is child of parent
				return n.getSubNodes().indexOf(child);
			}
			else if(n.getEntries().contains(child)){
				// entry is child
				return n.getEntries().indexOf(child);
			}
		}
		return -1;
		
	}

	@Override
	public Object getRoot() {
		return mRootNode;
		
	}

	@Override
	public boolean isLeaf(Object node) {
		return node instanceof Entry;
		
	}
	
	@Override
	public void addTreeModelListener(TreeModelListener listener) {
		mTreeListener.add(listener);
	}

	@Override
	public void removeTreeModelListener(TreeModelListener listener) {
		mTreeListener.remove(listener);
	}
	
	public void fireTreeDataChanged(Entry e){
		TreeModelEvent event = getEvent(e);
		// TODO: add childs to event
		for(TreeModelListener l:mTreeListener){
			l.treeNodesChanged(event);
		}
	}
	
	public void fireTreeDataChanged(Node n){
		TreeModelEvent event= getEvent(n);
		for(TreeModelListener l:mTreeListener){
			l.treeNodesChanged(event);
		}
	}
	
	public void fireTreeStructureChanged(Node n){
		TreeModelEvent event = getEvent(n);
		for(TreeModelListener l:mTreeListener){
			l.treeStructureChanged(event);
		}
	}
	
	public void fireTreeStructureChanged(Entry e){
		TreeModelEvent event = getEvent(e);
		for(TreeModelListener l:mTreeListener){
			l.treeStructureChanged(event);
		}
	}
	
	private TreeModelEvent getEvent(Node n){
		ArrayList<Object> path = new ArrayList<Object>();
		if(n.hasParent()){
			n.getParentNode().getPathToRoot(path);
		}
		else{
			n.getPathToRoot(path);
		}
		Collections.reverse(path); // we get the path with the root node last
		
		return new TreeModelEvent(n, path.toArray());
	}
	
	private TreeModelEvent getEvent(Entry e){
		ArrayList<Object> path = new ArrayList<Object>();
		e.getParentNode().getPathToRoot(path);
		Collections.reverse(path); // we get the path with the root node last
		
		return new TreeModelEvent(e, path.toArray());
	}
	
	
	

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
		Node editNode = (Node)path.getLastPathComponent(); // should only be node, because leafes can't be edited
		editNode.setNodeName(""+newValue);
		fireTreeDataChanged(editNode);
	}


	public Node getRootNode() {
		return mRootNode;
	}


	public void setRootNode(Node mRootNode) {
		this.mRootNode = mRootNode;
		fireTreeStructureChanged(mRootNode);
	}

}
