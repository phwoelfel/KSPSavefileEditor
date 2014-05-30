package at.woelfel.philip.kspsavefileeditor.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import at.woelfel.philip.kspsavefileeditor.backend.Node;
import at.woelfel.philip.kspsavefileeditor.backend.NodeTableModel;
import at.woelfel.philip.kspsavefileeditor.backend.NodeTreeModel;

public class MainGui extends JFrame implements TreeSelectionListener{
	
	private JTable mEntryTable;
	private JTree mNodeTree;
	
	private NodeTreeModel mNodeTreeModel;
	private NodeTableModel mNodeTableModel;
	
	private Node mRootNode;
	
	public MainGui() {
		mRootNode = new Node("Node1", null);
		mRootNode.createEntry("entry1a", "value1a");
		mRootNode.createEntry("entry1b", "value1b");
		
		Node node11 = new Node("Node11", mRootNode);
		node11.createEntry("entry11a", "value11a");
		mRootNode.addSubNode(node11);
		
		Node node111 = new Node("Node111", node11);
		node111.createEntry("entry111a", "value111a");
		node11.addSubNode(node111);
		
		Node node12 = new Node("Node12", mRootNode);
		mRootNode.addSubNode(node12);
		
		node11.createEntry("entry11b", "value11b");
		
		
		
		setTitle("KSP Savefile Editor");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1280, 700);
		setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		//c.insets = new Insets(5, 5, 5, 5);
		c.weightx = 1;
		c.weighty = 70;
		c.fill = GridBagConstraints.BOTH;
		
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 30;
		mNodeTreeModel = new NodeTreeModel(mRootNode);
		mNodeTree = new JTree(mNodeTreeModel);
		mNodeTree.addTreeSelectionListener(this);
		JScrollPane nodeTreeJSP = new JScrollPane(mNodeTree);
		add(nodeTreeJSP,c);
		
		
		c.gridx = 0;
		c.gridy = 1;
		mNodeTableModel = new NodeTableModel(null);
		mEntryTable = new JTable(mNodeTableModel);
		JScrollPane entryTableJSP = new JScrollPane(mEntryTable);
		add(entryTableJSP, c);
		
		setVisible(true);
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		Object lPC = e.getPath().getLastPathComponent();
		if(lPC!=null && lPC instanceof Node){
			mNodeTableModel.setNode((Node)lPC);
		}
	}
	
}
