package at.woelfel.philip.kspsavefileeditor.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import at.woelfel.philip.kspsavefileeditor.Tools;
import at.woelfel.philip.kspsavefileeditor.backend.ChangeListener;
import at.woelfel.philip.kspsavefileeditor.backend.Entry;
import at.woelfel.philip.kspsavefileeditor.backend.Logger;
import at.woelfel.philip.kspsavefileeditor.backend.Node;
import at.woelfel.philip.kspsavefileeditor.backend.NodeTableModel;
import at.woelfel.philip.kspsavefileeditor.backend.NodeTreeModel;
import at.woelfel.philip.kspsavefileeditor.backend.Parser;

@SuppressWarnings("serial")
public class TreeWindow extends JTree implements TreeSelectionListener, ChangeListener, ActionListener {

	private JTree mNodeTree;
	private NodeTreeModel mNodeTreeModel;
	private NodeTableModel mNodeTableModel;
	

	private ProgressScreen mProgressScreen;
	
	private Node mRootNode;
	
	private EntryEditor mEntryEditor;
	private NodeEditor mNodeEditor;
	
	
	private JPopupMenu mRCPopup; // Right Click Popup
	private JMenuItem mRCNewNodeMenu;
	private JMenuItem mRCNewEntryMenu;
	private JMenuItem mRCEditMenu;
	private JMenuItem mRCDeleteMenu;
	
	
	/**
	 * @param rootNode the root node of the tree
	 * @param nodeTableModel the table model of the table in which the entries should be displayed
	 */
	public TreeWindow(Node rootNode, NodeTableModel nodeTableModel) {
		
		mProgressScreen = new ProgressScreen("Parsing savefile...", this);
		
		mRootNode = rootNode;
		mNodeTableModel = nodeTableModel;
		
		mNodeTreeModel = new NodeTreeModel(mRootNode);
		mNodeTree = new JTree(mNodeTreeModel);
		mNodeTree.addTreeSelectionListener(this);
		mNodeTree.setEditable(true);
		mNodeTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		
		ImageIcon rocket = Tools.readImage("rocket.png");
		ImageIcon rocketFly = Tools.readImage("rocket-fly.png");
	    IconNodeRenderer renderer = new IconNodeRenderer();
	    //renderer.setLeafIcon(rocket);
	    renderer.setClosedIcon(rocket);
	    renderer.setOpenIcon(rocketFly);
	    mNodeTree.setCellRenderer(renderer);
	    mNodeTree.setCellEditor(new NonLeafTreeEditor(mNodeTree, renderer));
	    
	    
	    createPopupMenu();
		mEntryEditor = new EntryEditor();
		mEntryEditor.addChangeListener(this);
		
		mNodeEditor = new NodeEditor(mEntryEditor);
		mNodeEditor.addChangeListener(this);
		
		JScrollPane nodeTreeJSP = new JScrollPane(mNodeTree);
		add(nodeTreeJSP, BorderLayout.CENTER);
	}
	
	/**
	 * Loads and parses the File and shows it in this tree.
	 * @param f
	 * @param hasRoot if the file has a root node or not
	 */
	public void load(File f, final boolean hasRoot){
		final Parser p = new Parser(f);
		mProgressScreen.setVisible(true);
		Thread th = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					setRootNode(p.parse(hasRoot));
					mProgressScreen.setVisible(false);
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(TreeWindow.this, "Error parsing file!\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		th.start();	
	}
	
	/**
	 * Saves the current tree to the File.
	 * @param f
	 */
	public void save(File f){
		String content = mRootNode.print(0);
		try {
			FileWriter fw = new FileWriter(f);
			fw.write(content);
			fw.flush();
			fw.close();
		} catch (IOException e1) {
			e1.printStackTrace();
			System.err.println("Couldn't write file!\n" +e1);
		}
	}
	
	
	/**
	 * Searches the tree for the string and shows it if it's found.
	 * If nothing is found false is returned.
	 * @param search
	 * @return true if something is found, false if nothing can be found.
	 */
	public boolean search(String search){
		if(search != null && search.length()!=0){
			TreePath[] tp = getRootNode().multiSearch(search);
			if(tp!=null && tp.length > 0){
				Logger.log("found something: " +tp);
				if(tp.length>1){
					TreePath sel = (TreePath) JOptionPane.showInputDialog(this, "Found multiple results!\nChoose one:", "Multiple Results", JOptionPane.PLAIN_MESSAGE, null, tp, null);
					mNodeTree.setSelectionPath(sel);
					mNodeTree.scrollPathToVisible(sel);
				}
				else{
					mNodeTree.setSelectionPath(tp[0]);
					mNodeTree.scrollPathToVisible(tp[0]);
				}
				return true;
			}
			else{
				JOptionPane.showMessageDialog(this, "Didn't find anything!", "No Search Result", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		return false;
	}
	
	

	public void setSelection(TreePath sel){
		mNodeTree.setSelectionPath(sel);
		mNodeTree.scrollPathToVisible(sel);
	}
	
	public Node getRootNode() {
		return mRootNode;
	}

	public void setRootNode(Node mRootNode) {
		this.mRootNode = mRootNode;
		mNodeTreeModel.setRootNode(mRootNode);
	}
	

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		Object lPC = e.getPath().getLastPathComponent();
		if(lPC!=null && lPC instanceof Node){
			mNodeTableModel.setNode((Node)lPC);
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
	
	private void createPopupMenu() {
		mRCPopup = new JPopupMenu();
		JMenu rcNewMenu = new JMenu("New");
		mRCNewNodeMenu = Tools.initializeMenuItem(rcNewMenu, "Node", this);
		mRCNewEntryMenu = Tools.initializeMenuItem(rcNewMenu, "Entry", this);
		mRCPopup.add(rcNewMenu);
		mRCEditMenu = Tools.initializeMenuItem(mRCPopup, "Edit", this);
		mRCDeleteMenu = Tools.initializeMenuItem(mRCPopup, "Delete", this);
		
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
				
				if(tree.getSelectionCount()<=1){ // if we have nothing or only one node selected, set selection to current item
					tree.setSelectionPath(path);
				}
				
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JMenuItem source = (JMenuItem) (e.getSource());
		
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
			if(path!=null){
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
		}
		else if(source == mRCEditMenu){
			// get selected element
			TreePath path = mNodeTree.getSelectionPath();
			if(path!=null){
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
		}
		else if (source == mRCDeleteMenu) {
			// get selected element
			// TreePath path = mNodeTree.getSelectionPath();
			int yesno = JOptionPane.showConfirmDialog(this, "Do you really want to delete this Node/Entry?", "Are you sure?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if (yesno == JOptionPane.YES_OPTION) {
				TreePath[] paths = mNodeTree.getSelectionPaths();
				for (int i = 0; i < paths.length; i++) {
					TreePath path = paths[i];
					Object selection = path.getLastPathComponent();
					int pc = path.getPathCount();
					if (pc >= 2) { // we got at least two nodes --> remove from parent
						Node parent = (Node) path.getPathComponent(pc - 2);
						if (selection instanceof Entry) {
							parent.removeEntry((Entry) selection);
							onEntryRemoved((Entry) selection);
							selection = null;
						}
						else if (selection instanceof Node) {
							parent.removeSubNode((Node) selection);
							onNodeRemoved((Node) selection);
							selection = null;
						}
					}
					else { // we are deleting the root node!
							// TODO: delete the root node?
					}
				}
			}
		}
		
	}
}
