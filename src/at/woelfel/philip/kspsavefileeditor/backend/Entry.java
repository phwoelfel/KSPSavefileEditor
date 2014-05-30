package at.woelfel.philip.kspsavefileeditor.backend;

public class Entry {

	private String mKey;
	private String mValue;
	
	public Entry() {
		
	}
	
	public Entry(String key, String value){
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
	
}
