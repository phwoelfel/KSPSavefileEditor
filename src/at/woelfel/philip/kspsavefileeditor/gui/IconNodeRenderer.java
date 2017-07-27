package at.woelfel.philip.kspsavefileeditor.gui;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import at.woelfel.philip.kspsavefileeditor.backend.Node;

class IconNodeRenderer extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = 148000L;

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		if(value instanceof Node){
			Icon icon = ((Node) value).getIcon();
			if (icon != null) {
				setIcon(icon);
			}
		}
		return this;
	}
}
