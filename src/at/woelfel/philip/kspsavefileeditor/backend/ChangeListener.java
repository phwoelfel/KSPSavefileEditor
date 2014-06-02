package at.woelfel.philip.kspsavefileeditor.backend;

public interface ChangeListener {

	public void onEntryChanged(Entry e);
	public void onNodeChanged(Node n);
	
	public void onEntryAdded(Entry e);
	public void onNodeAdded(Node n);
	
	public void onEntryRemoved(Entry e);
	public void onNodeRemoved(Node n);
}
