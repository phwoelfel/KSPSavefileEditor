package at.woelfel.philip.kspsavefileeditor.backend;

import javax.swing.table.AbstractTableModel;

public class NodeTableModel extends AbstractTableModel{

	private Node mNode;
	
	public NodeTableModel(Node n) {
		mNode = n;
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
		// TODO: cell editing
		return false;
		
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		// TODO: saving cell values
	}


	public Node getNode() {
		return mNode;
	}

	public void setNode(Node mNode) {
		this.mNode = mNode;
		fireTableDataChanged();
	}
	
	

	

}
