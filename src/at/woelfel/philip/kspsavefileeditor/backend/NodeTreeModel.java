package at.woelfel.philip.kspsavefileeditor.backend;

import java.util.ArrayList;

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

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
	}

}
