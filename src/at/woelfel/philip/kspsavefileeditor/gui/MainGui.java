package at.woelfel.philip.kspsavefileeditor.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import at.woelfel.philip.kspsavefileeditor.backend.ChangeListener;
import at.woelfel.philip.kspsavefileeditor.backend.Entry;
import at.woelfel.philip.kspsavefileeditor.backend.Node;
import at.woelfel.philip.kspsavefileeditor.backend.NodeTableModel;
import at.woelfel.philip.kspsavefileeditor.backend.NodeTreeModel;

public class MainGui extends JFrame implements TreeSelectionListener, ActionListener, ChangeListener{
	
	private JTable mEntryTable;
	private JTree mNodeTree;
	
	private JMenuItem mFileOpenItem;
	private JMenuItem mFileSaveItem;
	
	private JMenuItem mAboutInfoItem;
	
	private JPopupMenu mRCPopup; // Right Click Popup
	private JMenuItem mRCNewNodeMenu;
	private JMenuItem mRCNewEntryMenu;
	private JMenuItem mRCEditMenu;
	private JMenuItem mRCDeleteMenu;
	
	private NodeTreeModel mNodeTreeModel;
	private NodeTableModel mNodeTableModel;
	
	private EntryEditor mEntryEditor;
	private NodeEditor mNodeEditor;
	
	private Node mRootNode;
	
	public MainGui() {
		// ################################## Temp Nodes ##################################
		mRootNode = new Node("Node 0", null);
		for(int i=0;i<10;i++){
			mRootNode.createEntry("entry1" +((char)(97+i)), "value1" +((char)(97+i)));
		}
		for(int i=0;i<10;i++){
			Node subnode = new Node("Node 0"+i, mRootNode);
			for(int j=0;j<10;j++){
				subnode.createEntry("entry0"+i+((char)(97+j)), "value0"+i+((char)(97+j)));
			}
			mRootNode.addSubNode(subnode);
		
			for(int j=0;j<10;j++){
				Node subsubnode = new Node("Node0"+i+""+j, subnode);
				for(int k=0;k<10;k++){
					subsubnode.createEntry("entry0"+i+""+j+((char)(97+k)), "value0"+i+""+j+((char)(97+k)));
				}
				subnode.addSubNode(subsubnode);
			}
		}
		
		
		// ################################## Window ##################################
		setTitle("KSP Savefile Editor");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1280, 700);
		setLayout(new GridBagLayout());
		
		
		// ################################## Menu ##################################
		// TODO: menu images
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		mFileOpenItem = initializeMenuItem(fileMenu, "Open...", this, new ImageIcon("img/load.png"));
		mFileSaveItem = initializeMenuItem(fileMenu, "Save...", this, new ImageIcon("img/save.png"));
		menuBar.add(fileMenu);
		
		JMenu aboutMenu = new JMenu("About");
		mAboutInfoItem = initializeMenuItem(aboutMenu, "Info", this, new ImageIcon("img/info.png"));
		menuBar.add(aboutMenu);
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
		mNodeTree.setEditable(true);
		mNodeTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		
		ImageIcon rocket = new ImageIcon("img/rocket.png");
		ImageIcon rocketFly = new ImageIcon("img/rocket-fly.png");
	    DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
	    //renderer.setLeafIcon(rocket);
	    renderer.setClosedIcon(rocket);
	    renderer.setOpenIcon(rocketFly);
	    mNodeTree.setCellRenderer(renderer);
	    mNodeTree.setCellEditor(new NonLeafTreeEditor(mNodeTree, renderer));
	    
		
		
		JScrollPane nodeTreeJSP = new JScrollPane(mNodeTree);
		add(nodeTreeJSP,c);
		
		
		
		createPopupMenu();
		
		mEntryEditor = new EntryEditor();
		mEntryEditor.addChangeListener(this);
		
		mNodeEditor = new NodeEditor(mEntryEditor);
		mNodeEditor.addChangeListener(this);
		
		c.gridx = 0;
		c.gridy = 1;
		c.weighty = 0.3;
		mNodeTableModel = new NodeTableModel();
		mNodeTableModel.addChangeListener(this);
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
	
	
	private JMenuItem initializeMenuItem(JComponent parent, String text, ActionListener al) {
		JMenuItem item = new JMenuItem(text);
		item.addActionListener(al);
		parent.add(item);
		return item;
	}
	
	private JMenuItem initializeMenuItem(JComponent parent, String text, ActionListener al, ImageIcon img) {
		JMenuItem item = new JMenuItem(text, img);
		item.addActionListener(al);
		parent.add(item);
		return item;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() instanceof JMenuItem) {
			menuClick(e);
		}
	}
	
	private void menuClick(ActionEvent e){
		// TODO: actionlistener for menu items
		JMenuItem source = (JMenuItem) (e.getSource());
		String s = "Action event detected.\n" + "\tEvent source: " + source.getText() + " (an instance of " + source.getClass().getSimpleName() + ")";
		System.out.println(s);
		
		
		// TODO: move right click to function
		if(source == mRCNewNodeMenu){
			// get selected element
			TreePath path = mNodeTree.getSelectionPath(); 
			Object selection = path.getLastPathComponent();
			// create new node and add it with selection as parent
			Node parent=null;
			if(selection instanceof Entry){
				parent = ((Entry)selection).getParentNode();
			}
			else if(selection instanceof Node){
				parent = (Node)selection;
			}
			mNodeEditor.showForNew(parent);
		}
		else if(source == mRCNewEntryMenu){
			// get selected element
			TreePath path = mNodeTree.getSelectionPath(); 
			Object selection = path.getLastPathComponent();
			// create new entry and add it with selection as parent
			Node parent=null;
			if(selection instanceof Entry){
				parent = ((Entry)selection).getParentNode();
			}
			else if(selection instanceof Node){
				parent = (Node)selection;
			}
			mEntryEditor.showForNew(parent);
		}
		else if(source == mRCEditMenu){
			// get selected element
			TreePath path = mNodeTree.getSelectionPath(); 
			Object selection = path.getLastPathComponent();
			if(selection instanceof Entry){
				// edit Entry
				mEntryEditor.showForEdit((Entry)selection);
			}
			else if(selection instanceof Node){
				// edit Node
				mNodeEditor.showForEdit((Node)selection);
			}
		}
		else if(source == mRCDeleteMenu){
			// get selected element
			TreePath path = mNodeTree.getSelectionPath(); 
			Object selection = path.getLastPathComponent();
			int yesno = JOptionPane.showConfirmDialog(this, "Do you really want to delete this Node/Entry?", "Are you sure?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if(yesno == JOptionPane.YES_OPTION){
				int pc = path.getPathCount();
				if(pc>=2){ // we got at least two nodes --> remove from parent
					Node parent = (Node) path.getPathComponent(pc-2);
					if(selection instanceof Entry){
						parent.removeEntry((Entry)selection);
						onEntryRemoved((Entry)selection);
						selection = null;
					}
					else if(selection instanceof Node){
						parent.removeSubNode((Node)selection);
						onNodeRemoved((Node)selection);
						selection = null;
					}
				}
				else{ // we are deleting the root node!
					
				}
			}
		}
		else if(source == mAboutInfoItem){
			new AboutWindow();
		}
	}
	
	private void createPopupMenu() {
		mRCPopup = new JPopupMenu();
		JMenu rcNewMenu = new JMenu("New");
		mRCNewNodeMenu = initializeMenuItem(rcNewMenu, "Node", this);
		mRCNewEntryMenu = initializeMenuItem(rcNewMenu, "Entry", this);
		mRCPopup.add(rcNewMenu);
		mRCEditMenu = initializeMenuItem(mRCPopup, "Edit", this);
		mRCDeleteMenu = initializeMenuItem(mRCPopup, "Delete", this);
		
		MouseListener popupListener = new PopupListener(mRCPopup);
		mNodeTree.addMouseListener(popupListener);
	}
	
	class PopupListener extends MouseAdapter {
		JPopupMenu popup;

		PopupListener(JPopupMenu popupMenu) {
			popup = popupMenu;
		}

		public void mousePressed(MouseEvent e) {
			showPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			showPopup(e);
		}

		private void showPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				int x = e.getX();
				int y = e.getY();
				JTree tree = (JTree)e.getSource();
				TreePath path = tree.getPathForLocation(x, y);
				if (path == null)
					return;	
				
				if(path.getLastPathComponent() instanceof Node){
					Node n = (Node)path.getLastPathComponent();
					ArrayList<Object> nl = new ArrayList<Object>();
					n.getPathToRoot(nl);
					System.out.println(nl);
				}
				
				tree.setSelectionPath(path);
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	@Override
	public void onEntryChanged(Entry e) {
		mNodeTreeModel.fireTreeStructureChanged(e);
	}

	@Override
	public void onNodeChanged(Node n) {
		mNodeTreeModel.fireTreeStructureChanged(n);
	}

	@Override
	public void onEntryAdded(Entry e) {
		mNodeTreeModel.fireTreeStructureChanged(e);
	}

	@Override
	public void onNodeAdded(Node n) {
		mNodeTreeModel.fireTreeStructureChanged(n);
	}

	@Override
	public void onEntryRemoved(Entry e) {
		mNodeTreeModel.fireTreeStructureChanged(e);
	}

	@Override
	public void onNodeRemoved(Node n) {
		mNodeTreeModel.fireTreeStructureChanged(n);
	}

}
