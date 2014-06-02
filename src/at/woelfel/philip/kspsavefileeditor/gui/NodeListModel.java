package at.woelfel.philip.kspsavefileeditor.gui;

import javax.swing.AbstractListModel;

import at.woelfel.philip.kspsavefileeditor.backend.Node;

public class NodeListModel extends AbstractListModel {

	private Node mNode;
	
	private int mMode;
	public static final int MODE_SUBNODES = 1; // show subnodes in list
	public static final int MODE_ENTRIES = 2; // show entries in list
	
	public NodeListModel(int mode) {
		mMode = mode;
	}
	
	@Override
	public Object getElementAt(int index) {
		if(mNode != null){
			if(NodeListModel.MODE_SUBNODES == mMode){
				return mNode.getSubNode(index);
			}
			else if(NodeListModel.MODE_ENTRIES == mMode){
				return mNode.getEntry(index);
			}
		}
		return null;
	}

	@Override
	public int getSize() {
		if(mNode != null){
			if(NodeListModel.MODE_SUBNODES == mMode){
				return mNode.getSubNodeCount();
			}
			else if(NodeListModel.MODE_ENTRIES == mMode){
				return mNode.getEntryCount();
			}
		}
		return 0;

	}

	public Node getNode() {
		return mNode;
	}

	public void nodeUpdated(){
		if(NodeListModel.MODE_SUBNODES == mMode){
			fireContentsChanged(mNode, 0, mNode.getSubNodeCount());
		}
		else if(NodeListModel.MODE_ENTRIES == mMode){
			fireContentsChanged(mNode, 0, mNode.getEntryCount());
		}
	}
	
	public void setNode(Node mNode) {
		if(mNode != null){
			this.mNode = mNode;
			if(NodeListModel.MODE_SUBNODES == mMode){
				fireContentsChanged(mNode, 0, mNode.getSubNodeCount());
			}
			else if(NodeListModel.MODE_ENTRIES == mMode){
				fireContentsChanged(mNode, 0, mNode.getEntryCount());
			}
		}
	}

}
