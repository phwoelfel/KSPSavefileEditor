package at.woelfel.philip.kspsavefileeditor.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import at.woelfel.philip.kspsavefileeditor.backend.Entry;
import at.woelfel.philip.kspsavefileeditor.backend.Node;
import at.woelfel.philip.kspsavefileeditor.backend.NodeTableModel;
import at.woelfel.philip.kspsavefileeditor.backend.NodeTreeModel;

public class MainGui extends JFrame implements TreeSelectionListener, ActionListener{
	
	private JTable mEntryTable;
	private JTree mNodeTree;
	
	private JMenuItem mFileOpenItem;
	private JMenuItem mFileSaveItem;
	
	private JMenuItem mEditNodeNewItem;
	private JMenuItem mEditNodeEditItem;
	private JMenuItem mEditNodeDeleteItem;
	
	private JMenuItem mEditEntryNewItem;
	private JMenuItem mEditEntryEditItem;
	private JMenuItem mEditEntryDeleteItem;
	
	private NodeTreeModel mNodeTreeModel;
	private NodeTableModel mNodeTableModel;
	
	private Node mRootNode;
	
	public MainGui() {
		// ################################## Temp Nodes ##################################
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
		
		
		// ################################## Window ##################################
		setTitle("KSP Savefile Editor");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1280, 700);
		setLayout(new GridBagLayout());
		
		
		// ################################## Menu ##################################
		// TODO: menu images
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		mFileOpenItem = initializeMenuItem(fileMenu, "Open...", this);
		mFileSaveItem = initializeMenuItem(fileMenu, "Save...", this);
		menuBar.add(fileMenu);
		
		JMenu editMenu = new JMenu("Edit");
		
		JMenu editNodeMenu = new JMenu("Node");
		mEditNodeNewItem = initializeMenuItem(editNodeMenu, "New", this);
		mEditNodeEditItem = initializeMenuItem(editNodeMenu, "Edit", this);
		mEditNodeDeleteItem = initializeMenuItem(editNodeMenu, "Delete", this);
		
		JMenu editEntryMenu = new JMenu("Entry");
		mEditEntryNewItem = initializeMenuItem(editEntryMenu, "New", this);
		mEditEntryEditItem = initializeMenuItem(editEntryMenu, "Edit", this);
		mEditEntryDeleteItem = initializeMenuItem(editEntryMenu, "Delete", this);
		
		editMenu.add(editNodeMenu);
		editMenu.add(editEntryMenu);
		menuBar.add(editMenu);
		
		setJMenuBar(menuBar);
		
		
		// ################################## Components ##################################
		
		GridBagConstraints c = new GridBagConstraints();
		//c.insets = new Insets(5, 5, 5, 5);
		c.weightx = 1;
		
		c.fill = GridBagConstraints.BOTH;
		
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 0.7;
		mNodeTreeModel = new NodeTreeModel(mRootNode);
		mNodeTree = new JTree(mNodeTreeModel);
		mNodeTree.addTreeSelectionListener(this);
		JScrollPane nodeTreeJSP = new JScrollPane(mNodeTree);
		add(nodeTreeJSP,c);
		
		createPopupMenu();
		
		
		c.gridx = 0;
		c.gridy = 1;
		c.weighty = 0.3;
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
	
	
	private JMenuItem initializeMenuItem(JMenu parent, String text, ActionListener al) {
		JMenuItem item = new JMenuItem(text);
		item.addActionListener(al);
		parent.add(item);
		return item;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO: actionlistener for menu items
		if (e.getSource() instanceof JMenuItem) {
			JMenuItem source = (JMenuItem) (e.getSource());
			String s = "Action event detected.\n" + "\tEvent source: " + source.getText() + " (an instance of " + source.getClass().getSimpleName() + ")";
			System.out.println(s);
		}
	}
	
	public void createPopupMenu() {
        JMenuItem menuItem;
 
        //Create the popup menu.
        JPopupMenu popup = new JPopupMenu();
        menuItem = new JMenuItem("Edit");
        menuItem.addActionListener(this);
        popup.add(menuItem);
        menuItem = new JMenuItem("Delete");
        menuItem.addActionListener(this);
        popup.add(menuItem);
 
        //Add listener to the text area so the popup menu can come up.
        MouseListener popupListener = new PopupListener(popup);
        mNodeTree.addMouseListener(popupListener);
    }
	
	class PopupListener extends MouseAdapter {
		JPopupMenu popup;

		PopupListener(JPopupMenu popupMenu) {
			popup = popupMenu;
		}

		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}

		private void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				int x = e.getX();
				int y = e.getY();
				JTree tree = (JTree)e.getSource();
				TreePath path = tree.getPathForLocation(x, y);
				if (path == null)
					return;	
				
				tree.setSelectionPath(path);
				Object lPC = path.getLastPathComponent();
				if(lPC instanceof Node){
					Node node = (Node) lPC;
					System.out.println("right click node");
				}
				if(lPC instanceof Entry){
					Entry entry = (Entry) lPC;
					System.out.println("right click entry");
				}

				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

}
