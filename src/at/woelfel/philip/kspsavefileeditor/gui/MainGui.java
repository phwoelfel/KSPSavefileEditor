package at.woelfel.philip.kspsavefileeditor.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;

import at.woelfel.philip.kspsavefileeditor.backend.Node;
import at.woelfel.philip.kspsavefileeditor.backend.NodeTableModel;
import at.woelfel.philip.kspsavefileeditor.backend.Settings;
import at.woelfel.philip.tools.Logger;
import at.woelfel.philip.tools.Tools;

@SuppressWarnings("serial")
public class MainGui extends JFrame implements ActionListener, ItemListener{
	/*
	 * TODO: search & replace:
	 * 			gefundenen elemente anzeigen und dann mit checkboxen auswaehlen welche ersetzt werden sollen
	 */
	private JFileChooser mFileChooser;
	
	private JTable mEntryTable;
	private TreeWindow mPrimaryTreeWindow;
	private TreeWindow mSecondaryTreeWindow;
	
	
	private JMenuItem mFileOpenSFSItem;
	private JMenuItem mFileOpenOtherItem;
	private JMenuItem mFileSaveItem;
	private JMenuItem mFileSettingsItem;
	
	private JMenuItem mEditSearchItem;
	
	private JMenuItem mAboutInfoItem;
	private JCheckBoxMenuItem mAboutDebugItem;
	private JCheckBoxMenuItem mAboutFileDebugItem;
	

	
	private NodeTableModel mNodeTableModel;
	
	
	private Node mPrimaryRootNode;
	private Node mSecondaryRootNode;

	
	public MainGui() {
		Logger.setEnabled(false);
		
		mFileChooser = new JFileChooser(Settings.getString(Settings.PREF_KSP_DIR, System.getProperty("user.home")));
		FileNameExtensionFilter filter = new FileNameExtensionFilter("KSP Save or Craft files (sfs, txt, craft, cfg)", "sfs", "txt", "craft", "cfg");
		mFileChooser.setFileFilter(filter);
		
			
		// ################################## Temp Nodes ##################################
		mPrimaryRootNode = new Node("GAME", null);
		mPrimaryRootNode.setIcon(Tools.readImage("nodes/game.png"));
		String[] tmpNodeNames = {"ACTIONGROUPS", "ACTIONS", "CREW", "EDITOR", "EVENTS", "FLIGHTSTATE", "MODULE", "ORBIT", "PART", "PLANETS", "RECRUIT", "ROSTER", "SCIENCE", "TRACKINGSTATION", "VESSEL", "VESSELS/FLAG", "VESSELS/BASE", "VESSELS/PROBE", "VESSELS/SPACEOBJECT"};
		for (int i = 0; i < tmpNodeNames.length; i++) {
			String tmpName = tmpNodeNames[i];
			Node tmpNode = new Node(tmpName, mPrimaryRootNode);
			tmpNode.setIcon(Tools.readImage("nodes/" +tmpName.toLowerCase() +".png"));
			mPrimaryRootNode.addSubNode(tmpNode);
		}
		mSecondaryRootNode = new Node("GAME", null);
		mSecondaryRootNode.setIcon(Tools.readImage("nodes/game.png"));
		for (int i = 0; i < tmpNodeNames.length; i++) {
			String tmpName = tmpNodeNames[i];
			Node tmpNode = new Node(tmpName, mPrimaryRootNode);
			tmpNode.setIcon(Tools.readImage("nodes/" +tmpName.toLowerCase() +".png"));
			mSecondaryRootNode.addSubNode(tmpNode);
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
		setLayout(new GridBagLayout());
		
		
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
		
		mPrimaryTreeWindow = new TreeWindow(mPrimaryRootNode, mNodeTableModel);
		JScrollPane primaryTreeJSP = new JScrollPane(mPrimaryTreeWindow);
		mSecondaryTreeWindow = new TreeWindow(mSecondaryRootNode, mNodeTableModel);
		JScrollPane secondaryTreeJSP = new JScrollPane(mSecondaryTreeWindow);
		
		JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, primaryTreeJSP, secondaryTreeJSP);
		mainSplitPane.setDividerLocation((screen.width/4)*3); // set to 3/4 of screen width
		
		
		
				
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mainSplitPane, entryTableJSP);
		splitPane.setDividerLocation((screen.height/3)*2); // set to 2/3 of screen height
		GridBagConstraints splitC = new GridBagConstraints();
		splitC.fill = GridBagConstraints.BOTH;
		splitC.gridx = 0;
		splitC.gridy = 0;
		splitC.weightx = 1;
		splitC.weighty = 1;
		add(splitPane, splitC);
		
		
		
		//add(nodeTreeJSP,c);
		//add(entryTableJSP, c);
		
		
		setVisible(true);
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
		}
		else if (source == mFileOpenSFSItem) {
			Component curWindow = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
			
			mFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int returnVal = mFileChooser.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				Logger.log("You chose to open this file: " + mFileChooser.getSelectedFile().getName());
				if(curWindow == mPrimaryTreeWindow){
					mPrimaryTreeWindow.load(mFileChooser.getSelectedFile(), true);
				}
				else if(curWindow == mSecondaryTreeWindow){
					mSecondaryTreeWindow.load(mFileChooser.getSelectedFile(), true);
				}
				else{
					JOptionPane.showMessageDialog(this, "Please select a tree window to load the file!", "Error", JOptionPane.WARNING_MESSAGE);
				}
			}
		}
		else if (source == mFileOpenOtherItem) {
			Component curWindow = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
			
			mFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int returnVal = mFileChooser.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				Logger.log("You chose to open this file: " + mFileChooser.getSelectedFile().getName());
				if(curWindow == mPrimaryTreeWindow){
					mPrimaryTreeWindow.load(mFileChooser.getSelectedFile(), false);
				}
				else if(curWindow == mSecondaryTreeWindow){
					mSecondaryTreeWindow.load(mFileChooser.getSelectedFile(), false);
				}
				else{
					JOptionPane.showMessageDialog(this, "Please select a tree window to load the file!", "Error", JOptionPane.WARNING_MESSAGE);
				}
			}
		}
		else if (source == mFileSaveItem) {
			Component curWindow = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
			showSaveDialog(curWindow);
		}
		else if (source == mFileSettingsItem) {
			mFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnVal = mFileChooser.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				Settings.setString(Settings.PREF_KSP_DIR, mFileChooser.getSelectedFile().getAbsolutePath());
			}
		}
		else if (source == mEditSearchItem) {
			Component curWindow = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
			
			String search = JOptionPane.showInputDialog(this, "Please enter search term", "Search", JOptionPane.QUESTION_MESSAGE);
			if(curWindow == mPrimaryTreeWindow){
				mPrimaryTreeWindow.search(search);
			}
			else if(curWindow == mSecondaryTreeWindow){
				mSecondaryTreeWindow.search(search);
			}
			else{
				JOptionPane.showMessageDialog(this, "Please select a tree window to search!", "Error", JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	protected void showSaveDialog(Component curWindow){
		mFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int returnVal = mFileChooser.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File f = mFileChooser.getSelectedFile();
			Logger.log("You chose to save this file: " + f.getName());
			if(f.exists()){
				int res = JOptionPane.showConfirmDialog(this, "File exists! Do you want to overwrite the file?", "File exists!", JOptionPane.YES_NO_CANCEL_OPTION);
				if(res == JOptionPane.YES_OPTION){
					doSave(f, curWindow);
				}
				else if(res == JOptionPane.NO_OPTION){
					// user choose no --> show save dialog again
					showSaveDialog(curWindow);
				}
				// cancel --> do nothing
			}
			else{
				doSave(f, curWindow);
			}
		}
	}

	protected void doSave(File f, Component curWindow){
		if(curWindow == mPrimaryTreeWindow){
			mPrimaryTreeWindow.save(mFileChooser.getSelectedFile());
		}
		else if(curWindow == mSecondaryTreeWindow){
			mSecondaryTreeWindow.save(mFileChooser.getSelectedFile());
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
	
	

	
	
	

}
