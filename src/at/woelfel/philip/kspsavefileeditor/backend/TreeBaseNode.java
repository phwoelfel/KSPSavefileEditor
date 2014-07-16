package at.woelfel.philip.kspsavefileeditor.backend;

/**
 * Dummy base class for Node and Entry.
 */
public class TreeBaseNode {
	protected Node mParentNode;
	
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
}
