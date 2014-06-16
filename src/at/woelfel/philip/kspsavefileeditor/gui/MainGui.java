package at.woelfel.philip.kspsavefileeditor.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import at.woelfel.philip.kspsavefileeditor.backend.ChangeListener;
import at.woelfel.philip.kspsavefileeditor.backend.Entry;
import at.woelfel.philip.kspsavefileeditor.backend.Logger;
import at.woelfel.philip.kspsavefileeditor.backend.Node;
import at.woelfel.philip.kspsavefileeditor.backend.NodeTableModel;
import at.woelfel.philip.kspsavefileeditor.backend.NodeTreeModel;
import at.woelfel.philip.kspsavefileeditor.backend.Parser;

public class MainGui extends JFrame implements TreeSelectionListener, ActionListener, ChangeListener{
	
	private JFileChooser mFileChooser;
	
	private JTable mEntryTable;
	private JTree mNodeTree;
	
	private JMenuItem mFileOpenSFSItem;
	private JMenuItem mFileOpenOtherItem;
	private JMenuItem mFileSaveItem;
	
	private JMenuItem mEditSearchItem;
	
	private JMenuItem mAboutInfoItem;
	private JMenuItem mAboutDebugItem;
	private JMenuItem mAboutFileDebugItem;
	
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
		Logger.setEnabled(false);
		
		mFileChooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("KSP Save or Craft files (sfs, txt, craft, cfg)", "sfs", "txt", "craft", "cfg");
		mFileChooser.setFileFilter(filter);
		
		// ################################## Temp Nodes ##################################
		/*mRootNode = new Node("Node 0", null);
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
		
		Parser p = new Parser("persistent_small.sfs");
		mRootNode = p.parse();
		*/
		// ################################## Window ##################################
		setTitle("KSP Savefile Editor");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		//setSize((int)(screen.getWidth()-(screen.getWidth()/10)), (int)(screen.getHeight()-(screen.getHeight()/10)));
		setSize(screen);
		setLayout(new GridBagLayout());
		
		
		// ################################## Menu ##################################
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		mFileOpenSFSItem = initializeMenuItem(fileMenu, "Open...", this, readImage("load.png"));
		mFileOpenOtherItem = initializeMenuItem(fileMenu, "Open Craft...", this, readImage("load.png"));
		mFileSaveItem = initializeMenuItem(fileMenu, "Save...", this, readImage("save.png"));
		menuBar.add(fileMenu);
		
		JMenu editMenu = new JMenu("Edit");
		mEditSearchItem = initializeMenuItem(editMenu, "Search...", this, readImage("search.png"));
		menuBar.add(editMenu);
		
		JMenu aboutMenu = new JMenu("About");
		mAboutInfoItem = initializeMenuItem(aboutMenu, "Info", this, readImage("info.png"));
		mAboutDebugItem = initializeMenuItem(aboutMenu, "Enable Debug", this);
		mAboutFileDebugItem = initializeMenuItem(aboutMenu, "Enable File Debug", this);
		menuBar.add(aboutMenu);
		setJMenuBar(menuBar);
		
		
		// ################################## Components ##################################
		
		mNodeTreeModel = new NodeTreeModel(mRootNode);
		mNodeTree = new JTree(mNodeTreeModel);
		mNodeTree.addTreeSelectionListener(this);
		mNodeTree.setEditable(true);
		mNodeTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		
		ImageIcon rocket = readImage("rocket.png");
		ImageIcon rocketFly = readImage("rocket-fly.png");
	    DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
	    //renderer.setLeafIcon(rocket);
	    renderer.setClosedIcon(rocket);
	    renderer.setOpenIcon(rocketFly);
	    mNodeTree.setCellRenderer(renderer);
	    mNodeTree.setCellEditor(new NonLeafTreeEditor(mNodeTree, renderer));
	    
		
		mNodeTableModel = new NodeTableModel();
		mNodeTableModel.addChangeListener(this);
		mEntryTable = new JTable(mNodeTableModel);
		
		JScrollPane nodeTreeJSP = new JScrollPane(mNodeTree);
		//add(nodeTreeJSP,c);
		JScrollPane entryTableJSP = new JScrollPane(mEntryTable);
		//add(entryTableJSP, c);
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, nodeTreeJSP, entryTableJSP);
		splitPane.setDividerLocation(500);
		GridBagConstraints splitC = new GridBagConstraints();
		splitC.fill = GridBagConstraints.BOTH;
		splitC.gridx = 0;
		splitC.gridy = 0;
		splitC.weightx = 1;
		splitC.weighty = 1;
		add(splitPane, splitC);
		
		
		createPopupMenu();
		mEntryEditor = new EntryEditor();
		mEntryEditor.addChangeListener(this);
		
		mNodeEditor = new NodeEditor(mEntryEditor);
		mNodeEditor.addChangeListener(this);
		
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
		Logger.log(s);
		
		
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
		else if(source == mAboutInfoItem){
			new AboutWindow();
		}
		else if(source == mAboutDebugItem){
			if(Logger.isEnabled()){
				Logger.setEnabled(false);
				mAboutDebugItem.setText("Enable Debug");
			}
			else{
				Logger.setEnabled(true);
				mAboutDebugItem.setText("Disable Debug");
			}
		}
		else if(source == mAboutFileDebugItem){
			if(Logger.isFileEnabled()){
				Logger.setFileEnabled(false);
				mAboutFileDebugItem.setText("Enable File Debug");
			}
			else{
				mFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				int returnVal = mFileChooser.showSaveDialog(this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					try {
						Logger.setLogFile(mFileChooser.getSelectedFile());
					} catch (FileNotFoundException e1) {
						JOptionPane.showMessageDialog(this, "Error creating logfile!\n"+e1.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
				Logger.setFileEnabled(true);
				mAboutFileDebugItem.setText("Disable File Debug");
				
			}
		}
		else if (source == mFileOpenSFSItem) {
			mFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int returnVal = mFileChooser.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				Logger.log("You chose to open this file: " + mFileChooser.getSelectedFile().getName());
				Parser p = new Parser(mFileChooser.getSelectedFile());
				try {
					setRootNode(p.parse(true));
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(this, "Error parsing file!\n"+e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		else if (source == mFileOpenOtherItem) {
			mFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int returnVal = mFileChooser.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				Logger.log("You chose to open this file: " + mFileChooser.getSelectedFile().getName());
				Parser p = new Parser(mFileChooser.getSelectedFile());
				try {
					setRootNode(p.parse(false));
				} catch (Exception e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(this, "Error parsing file!\n"+e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		else if (source == mFileSaveItem) {
			mFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnVal = mFileChooser.showSaveDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				Logger.log("You chose to save this file: " + mFileChooser.getSelectedFile().getName());
				String content = mRootNode.print(0);
				try {
					FileWriter fw = new FileWriter(mFileChooser.getSelectedFile());
					fw.write(content);
					fw.flush();
					fw.close();
				} catch (IOException e1) {
					e1.printStackTrace();
					System.err.println("Couldn't write file!\n" +e1);
				}

			}
		}
		else if (source == mEditSearchItem) {
			String search = JOptionPane.showInputDialog(this, "Please enter search term", "Search", JOptionPane.QUESTION_MESSAGE);
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
					
				}
				else{
					JOptionPane.showMessageDialog(this, "Didn't find anything!", "No Search Result", JOptionPane.ERROR_MESSAGE);
				}
			}
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
				
				if(tree.getSelectionCount()<=1){ // if we have nothing or only one node selected, set selection to current item
					tree.setSelectionPath(path);
				}
				
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

	public Node getRootNode() {
		return mRootNode;
	}

	public void setRootNode(Node mRootNode) {
		this.mRootNode = mRootNode;
		mNodeTreeModel.setRootNode(mRootNode);
		mNodeTableModel.setNode(null);
	}
	
	public ImageIcon readImage(String fname) {
		try {
			return new ImageIcon(ImageIO.read(new File("img/" + fname)));
		} catch (Exception e) {
			try {
				return new ImageIcon(ImageIO.read(getClass().getResource("/img/" + fname)));
			} catch (Exception e1) {

				return null;
			}
		}
	}

}
