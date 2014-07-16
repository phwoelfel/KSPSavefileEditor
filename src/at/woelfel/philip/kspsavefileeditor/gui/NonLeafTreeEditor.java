package at.woelfel.philip.kspsavefileeditor.gui;

import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import at.woelfel.philip.kspsavefileeditor.backend.Node;

class NonLeafTreeEditor extends DefaultTreeCellEditor {
	public NonLeafTreeEditor(JTree tree, DefaultTreeCellRenderer renderer) {
		super(tree, renderer);
	}

	@Override
	public boolean isCellEditable(EventObject event) {
		if (!super.isCellEditable(event)) {
			return false;
		}
		if (event != null && event.getSource() instanceof JTree && event instanceof MouseEvent) {
			MouseEvent mouseEvent = (MouseEvent) event;
			JTree tree = (JTree) event.getSource();
			TreePath path = tree.getPathForLocation(mouseEvent.getX(), mouseEvent.getY());
			return path.getLastPathComponent() instanceof Node;
		}
		return false;
	}
}