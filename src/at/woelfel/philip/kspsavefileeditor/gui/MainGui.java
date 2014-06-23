package at.woelfel.philip.kspsavefileeditor.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;

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

import at.woelfel.philip.kspsavefileeditor.Tools;
import at.woelfel.philip.kspsavefileeditor.backend.Logger;
import at.woelfel.philip.kspsavefileeditor.backend.Node;
import at.woelfel.philip.kspsavefileeditor.backend.NodeTableModel;

@SuppressWarnings("serial")
public class MainGui extends JFrame implements ActionListener{
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
	
	private JMenuItem mEditSearchItem;
	
	private JMenuItem mAboutInfoItem;
	private JMenuItem mAboutDebugItem;
	private JMenuItem mAboutFileDebugItem;
	

	
	private NodeTableModel mNodeTableModel;
	
	
	private Node mPrimaryRootNode;
	private Node mSecondaryRootNode;

	
	public MainGui() {
		Logger.setEnabled(false);
		
		mFileChooser = new JFileChooser();
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
		menuBar.add(fileMenu);
		
		JMenu editMenu = new JMenu("Edit");
		mEditSearchItem = Tools.initializeMenuItem(editMenu, "Search...", this, Tools.readImage("search.png"));
		menuBar.add(editMenu);
		
		JMenu aboutMenu = new JMenu("About");
		mAboutInfoItem = Tools.initializeMenuItem(aboutMenu, "Info", this, Tools.readImage("info.png"));
		mAboutDebugItem = Tools.initializeMenuItem(aboutMenu, "Enable Debug", this);
		mAboutFileDebugItem = Tools.initializeMenuItem(aboutMenu, "Enable File Debug", this);
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
			
			mFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnVal = mFileChooser.showSaveDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				Logger.log("You chose to save this file: " + mFileChooser.getSelectedFile().getName());
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
	
	

	
	
	

}
