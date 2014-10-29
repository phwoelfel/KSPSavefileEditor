package at.woelfel.philip.kspsavefileeditor.backend;

import java.util.ArrayList;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import at.woelfel.philip.tools.Logger;

public class NodeTreeModel implements TreeModel, TreeWillExpandListener {

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
			if(n.isExpanded()){
				return n.getSubNodeCount()+n.getEntryCount(); // just add subnode and entry count
			}
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
		if(event!=null){
			for(TreeModelListener l:mTreeListener){
				l.treeStructureChanged(event);
			}
		}
	}
	
	public void fireTreeStructureChanged(Entry e){
		TreeModelEvent event = getEvent(e);
		for(TreeModelListener l:mTreeListener){
			l.treeStructureChanged(event);
		}
	}
	
	private TreeModelEvent getEvent(Node n){
		ArrayList<Object> path = null;
		if(n != null){
			if(n.hasParent()){
				path = n.getParentNode().getPathToRoot();
			}
			else{
				path = n.getPathToRoot();
			}
			Logger.log("path: " +path);
			return new TreeModelEvent(n, path.toArray());
		}
		return null;
	}
	
	private TreeModelEvent getEvent(Entry e){
		ArrayList<Object> path = e.getParentNode().getPathToRoot();
		Logger.log("path: " +path);
		
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


	@Override
	public void treeWillCollapse(TreeExpansionEvent e) throws ExpandVetoException {
	}


	@Override
	public void treeWillExpand(TreeExpansionEvent e) throws ExpandVetoException {
		TreePath tp = e.getPath();
		if(tp!=null){
			TreeBaseNode lpc = (TreeBaseNode) tp.getLastPathComponent();
			if(lpc!=null){
				lpc.isExpanded(true);
			}
		}
	}
	
	public void cleanup(){
		mRootNode = null;
		mTreeListener.clear();
	}

}
