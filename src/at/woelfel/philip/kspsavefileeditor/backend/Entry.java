package at.woelfel.philip.kspsavefileeditor.backend;

public class Entry {

	private Node mParentNode;
	private String mKey;
	private String mValue;
	
	public Entry() {
		
	}
	
	public Entry(Node parentNode, String key, String value){
		mParentNode = parentNode;
		mKey = key;
		mValue = value;
	}

	/**
	 * @return the mKey
	 */
	public String getKey() {
		return mKey;
	}

	/**
	 * @param mKey the mKey to set
	 */
	public void setKey(String mKey) {
		this.mKey = mKey;
	}

	/**
	 * @return the mValue
	 */
	public String getValue() {
		return mValue;
	}

	/**
	 * @param mValue the mValue to set
	 */
	public void setValue(String mValue) {
		this.mValue = mValue;
	}
	
	@Override
	public String toString() {
		return mKey +" = " +mValue;
	}

	public Node getParentNode() {
		return mParentNode;
	}

	public void setParentNode(Node mParentNode) {
		this.mParentNode = mParentNode;
	}
	
	public boolean hasParentNode(){
		if(mParentNode!=null){
			return true;
		}
		return false;
	}
	
	public boolean search(String search){
		if(getKey().toLowerCase().contains(search.toLowerCase()) || getValue().toLowerCase().contains(search.toLowerCase())){
			return true;
		}
		return false;
	}
}
