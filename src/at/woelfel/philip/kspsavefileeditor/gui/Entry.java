package at.woelfel.philip.kspsavefileeditor.gui;

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
	public String getmKey() {
		return mKey;
	}

	/**
	 * @param mKey the mKey to set
	 */
	public void setmKey(String mKey) {
		this.mKey = mKey;
	}

	/**
	 * @return the mValue
	 */
	public String getmValue() {
		return mValue;
	}

	/**
	 * @param mValue the mValue to set
	 */
	public void setmValue(String mValue) {
		this.mValue = mValue;
	}
	
}
