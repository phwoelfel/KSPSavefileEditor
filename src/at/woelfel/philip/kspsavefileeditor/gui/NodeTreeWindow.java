package at.woelfel.philip.kspsavefileeditor.gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class NodeTreeWindow extends JPanel {

	protected JScrollPane mScrollPane;
	protected NodeTree mNodeTree;
	
	public NodeTreeWindow(NodeTree nt){
		mNodeTree = nt;
		setLayout(new BorderLayout());
		
		mScrollPane = new JScrollPane(mNodeTree);
		add(mScrollPane, BorderLayout.CENTER);
	}
	
	
	public NodeTree getNodeTree(){
		return mNodeTree;
	}
}
