package at.woelfel.philip.kspsavefileeditor.backend;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

public class NodeTableModel extends AbstractTableModel{

	private Node mNode;
	
	private ArrayList<ChangeListener> mChangeListener;
	
	public NodeTableModel() {
		mChangeListener = new ArrayList<ChangeListener>();
	}
	

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
		
	}

	@Override
	public int getColumnCount() {
		return 2;
		
	}

	@Override
	public String getColumnName(int columnIndex) {
		switch (columnIndex) {
			case 0:
				return "Key";
			case 1:
				return "Value";
			default:
				break;
		}
		return null;
		
	}

	@Override
	public int getRowCount() {
		return mNode!=null?mNode.getEntryCount():0;
		
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if(mNode!=null){
			switch (columnIndex) {
				case 0:
					return mNode.getEntry(rowIndex).getKey();
				case 1:
					return mNode.getEntry(rowIndex).getValue();
				default:
					break;
			}
		}
		return null;
		
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
		
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		Entry e = mNode.getEntry(rowIndex);
		if(columnIndex == 0){
			e.setKey(""+aValue);
		}
		else if(columnIndex == 1){
			e.setValue(""+aValue);
		}
		fireEntryChanged(e);
	}


	public Node getNode() {
		return mNode;
	}

	public void setNode(Node mNode) {
		this.mNode = mNode;
		fireTableDataChanged();
	}
	
	public void addChangeListener(ChangeListener cl){
		mChangeListener.add(cl);
	}
	
	public void removeChangeListener(ChangeListener cl){
		mChangeListener.remove(cl);
	}
	
	private void fireEntryChanged(Entry e){
		for(ChangeListener cl : mChangeListener){
			cl.onEntryChanged(e);
		}
	}
	
	private void fireEntryAdded(Entry e){
		for(ChangeListener cl : mChangeListener){
			cl.onEntryAdded(e);
		}
	}

	

}
