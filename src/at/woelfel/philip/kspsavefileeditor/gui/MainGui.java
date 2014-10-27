package at.woelfel.philip.kspsavefileeditor.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.TreePath;

import at.woelfel.philip.kspsavefileeditor.backend.Node;
import at.woelfel.philip.kspsavefileeditor.backend.NodeTableModel;
import at.woelfel.philip.kspsavefileeditor.backend.Settings;
import at.woelfel.philip.tools.Logger;
import at.woelfel.philip.tools.Tools;

@SuppressWarnings("serial")
public class MainGui extends JFrame implements ActionListener, ItemListener, TreeSelectionListener{
	/*
	 * TODO: search & replace:
	 * 			gefundenen elemente anzeigen und dann mit checkboxen auswaehlen welche ersetzt werden sollen
	 */
	private JFileChooser mFileChooser;
	
	private JTabbedPane mTabPane;
	private JTable mEntryTable;
	private ArrayList<NodeTreeWindow> mTreeWindows;
	private JLabel mPathLabel;
	
	
	private JMenuItem mFileOpenSFSItem;
	private JMenuItem mFileOpenOtherItem;
	private JMenuItem mFileSaveItem;
	private JMenuItem mFileSettingsItem;
	
	private JMenuItem mEditSearchItem;
	
	private JMenuItem mAboutInfoItem;
	private JCheckBoxMenuItem mAboutDebugItem;
	private JCheckBoxMenuItem mAboutFileDebugItem;
	
	private NodeTableModel mNodeTableModel;

	
	public MainGui() {
		Logger.setEnabled(false);
		
		mFileChooser = new JFileChooser(Settings.getString(Settings.PREF_KSP_DIR, System.getProperty("user.home")));
		FileNameExtensionFilter filter = new FileNameExtensionFilter("KSP Save or Craft files (sfs, txt, craft, cfg)", "sfs", "txt", "craft", "cfg");
		mFileChooser.setFileFilter(filter);
		
			
		// ################################## Temp Nodes ##################################
		Node mTempNode;
		mTempNode = new Node("GAME", null);
		mTempNode.setIcon(Tools.readImage("nodes/game.png"));
		String[] tmpNodeNames = {"ACTIONGROUPS", "ACTIONS", "CREW", "EDITOR", "EVENTS", "FLIGHTSTATE", "MODULE", "ORBIT", "PART", "PLANETS", "RECRUIT", "ROSTER", "SCIENCE", "TRACKINGSTATION", "VESSEL", "VESSELS/FLAG", "VESSELS/BASE", "VESSELS/PROBE", "VESSELS/SPACEOBJECT"};
		for (int i = 0; i < tmpNodeNames.length; i++) {
			String tmpName = tmpNodeNames[i];
			Node tmpNode = new Node(tmpName, mTempNode);
			tmpNode.setIcon(Tools.readImage("nodes/" +tmpName.toLowerCase() +".png"));
			mTempNode.addSubNode(tmpNode);
		}
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
		setLayout(new BorderLayout());
		
		
		// ################################## Menu ##################################
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		mFileOpenSFSItem = Tools.initializeMenuItem(fileMenu, "Open...", this, Tools.readImage("load.png"));
		mFileOpenOtherItem = Tools.initializeMenuItem(fileMenu, "Open Craft...", this, Tools.readImage("load.png"));
		mFileSaveItem = Tools.initializeMenuItem(fileMenu, "Save...", this, Tools.readImage("save.png"));
		fileMenu.addSeparator();
		mFileSettingsItem = Tools.initializeMenuItem(fileMenu, "KSP Folder...", this);
		menuBar.add(fileMenu);
		
		JMenu editMenu = new JMenu("Edit");
		mEditSearchItem = Tools.initializeMenuItem(editMenu, "Search...", this, Tools.readImage("search.png"));
		menuBar.add(editMenu);
		
		JMenu aboutMenu = new JMenu("About");
		mAboutInfoItem = Tools.initializeMenuItem(aboutMenu, "Info", this, Tools.readImage("info.png"));
		mAboutDebugItem = Tools.initializeCheckboxMenuItem(aboutMenu, "Enable Debug", this);
		mAboutFileDebugItem = Tools.initializeCheckboxMenuItem(aboutMenu, "Enable File Debug", this);
		menuBar.add(aboutMenu);
		setJMenuBar(menuBar);
		
		
		// ################################## Components ##################################
		mNodeTableModel = new NodeTableModel();
		mEntryTable = new JTable(mNodeTableModel);
		JScrollPane entryTableJSP = new JScrollPane(mEntryTable);
		
		mTabPane = new JTabbedPane();
		mTreeWindows = new ArrayList<NodeTreeWindow>();
		addTreeWindow(mTempNode);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mTabPane, entryTableJSP);
		splitPane.setDividerLocation((screen.height/3)*2); // set to 2/3 of screen height
		add(splitPane, BorderLayout.CENTER);
		
		
		mPathLabel = new JLabel("Path");
		JPanel pathPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		pathPanel.add(mPathLabel);
		add(new JScrollPane(pathPanel), BorderLayout.NORTH);
		
		
		//add(nodeTreeJSP,c);
		//add(entryTableJSP, c);
		
		
		setVisible(true);
	}

	public void addTreeWindow(Node n){
		NodeTree tmpNT = new NodeTree(n, mNodeTableModel);
		String tabName = n.getNodeName();
		
		addTreeWindow(tmpNT, tabName);
	}
	
	public void addTreeWindow(File f, boolean hasRoot){
		NodeTree tmpNT = new NodeTree(f, hasRoot, mNodeTableModel);
		String tabName = f.getParentFile().getName()+"/"+f.getName();
		
		addTreeWindow(tmpNT, tabName);
	}
	
	public void addTreeWindow(NodeTree nt, String tabName){
		nt.addTreeSelectionListener(this);
		
		NodeTreeWindow tmpNTW = new NodeTreeWindow(nt);
		mTreeWindows.add(tmpNTW);
		
		mTabPane.addTab(tabName, Tools.readImage("rocket.png"), tmpNTW);
		mTabPane.setSelectedComponent(tmpNTW);
		
		int index = mTabPane.getSelectedIndex();
		mTabPane.setTabComponentAt(index, new CloseableTab(mTabPane, this));
	}
	
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JMenuItem) {
			menuClick(e);
		}
	}
	
	private void menuClick(ActionEvent e){
		JMenuItem source = (JMenuItem) (e.getSource());
		String s = "Action event detected.\n" + "\tEvent source: " + source.getText() + " (an instance of " + source.getClass().getSimpleName() + ")";
		Logger.log(s);
		
		
		if(source == mAboutInfoItem){
			new AboutWindow();
			return;
		}
		else if (source == mFileSettingsItem) {
			mFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnVal = mFileChooser.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				Settings.setString(Settings.PREF_KSP_DIR, mFileChooser.getSelectedFile().getAbsolutePath());
			}
			return;
		}
		else if (source == mFileOpenSFSItem) {
			loadFile(true);
			return;
		}
		else if (source == mFileOpenOtherItem) {
			loadFile(false);
			return;
		}
		
		NodeTreeWindow tw = getCurrentTreeWindow();
		if(tw!=null){
			if (source == mFileSaveItem) {
				showSaveDialog(tw.getNodeTree());
			}
			else if (source == mEditSearchItem) {
				String search = JOptionPane.showInputDialog(this, "Please enter search term:", "Search", JOptionPane.QUESTION_MESSAGE);
				tw.getNodeTree().search(search);
			}
		}
		else{
			JOptionPane.showMessageDialog(this, "Please select a tree window first!", "Error", JOptionPane.WARNING_MESSAGE);
		}
	}
	
	protected NodeTreeWindow getCurrentTreeWindow(){
		Component curWindow = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
		for (NodeTreeWindow tw : mTreeWindows) {
			if(curWindow == tw){
				return tw;
			}
		}
		return null;
	}
	
	protected void loadFile(boolean hasRoot){
		
		mFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int returnVal = mFileChooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File f = mFileChooser.getSelectedFile();
			Logger.log("You chose to open this file: " + f.getName());
			for (NodeTreeWindow tw : mTreeWindows){
				try {
					if(tw.getNodeTree().getFile() != null && f.getCanonicalPath().equals(tw.getNodeTree().getFile().getCanonicalPath())){
						int res = JOptionPane.showConfirmDialog(this, "File already opened! Do you want to open the file again?", "File already opened!", JOptionPane.YES_NO_CANCEL_OPTION);
						if(res == JOptionPane.YES_OPTION){
							break;
						}
						else if(res == JOptionPane.NO_OPTION){
							mTabPane.setSelectedComponent(tw);
							return;
						}
						else{
							return;
						}
					}
				} catch (IOException e) {
					//e.printStackTrace();
				}
			}
			// we came here, file isn't opened
			addTreeWindow(f, hasRoot);
			
		}
	}

	protected void showSaveDialog(NodeTree tw){
		if(tw!=null){
			mFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int returnVal = mFileChooser.showSaveDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File f = mFileChooser.getSelectedFile();
				Logger.log("You chose to save this file: " + f.getName());
				if(f.exists()){
					int res = JOptionPane.showConfirmDialog(this, "File exists! Do you want to overwrite the file?", "File exists!", JOptionPane.YES_NO_CANCEL_OPTION);
					if(res == JOptionPane.YES_OPTION){
						tw.saveAs(f);
					}
					else if(res == JOptionPane.NO_OPTION){
						// user choose no --> show save dialog again
						showSaveDialog(tw);
					}
					// cancel --> do nothing
				}
				else{
					tw.saveAs(f);
				}
			}
		}
		else{
			JOptionPane.showMessageDialog(this, "Please select a tree window to save the file!", "Error", JOptionPane.WARNING_MESSAGE);
		}
	}


	@Override
	public void itemStateChanged(ItemEvent e) {
		if(e.getSource() == mAboutDebugItem){
			if(e.getStateChange() == ItemEvent.DESELECTED){
				Logger.setEnabled(false);
			}
			else{
				Logger.setEnabled(true);
			}
		}
		else if(e.getSource() == mAboutFileDebugItem){
			if(e.getStateChange() == ItemEvent.DESELECTED){
				Logger.setFileEnabled(false);
			}
			else{
				mFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				int returnVal = mFileChooser.showSaveDialog(this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					try {
						Logger.setLogFile(mFileChooser.getSelectedFile());
						Logger.setFileEnabled(true);
					} catch (FileNotFoundException e1) {
						JOptionPane.showMessageDialog(this, "Error creating logfile!\n"+e1.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}
		
	}




	@Override
	public void valueChanged(TreeSelectionEvent e) {
		TreePath tp = e.getPath();
		if(tp!=null){
			mPathLabel.setText(pathToString(tp));
		}
	}
	
	public String pathToString(TreePath treePath){
		StringBuilder sb = new StringBuilder();
		// sb.append("Path: ");
		
		Object[] path = treePath.getPath();
		for (int i = 0; i < path.length; i++) {
			if(i==path.length-1){
				sb.append(path[i]);
			}
			else{
				sb.append(path[i]+" > ");
			}
		}
		
		return sb.toString();
	}

	public void removeTab(int i) {
		NodeTreeWindow ntw = (NodeTreeWindow)mTabPane.getComponentAt(i);
		mTabPane.remove(ntw);
		mTreeWindows.remove(ntw);
	}

	
	
	

}
