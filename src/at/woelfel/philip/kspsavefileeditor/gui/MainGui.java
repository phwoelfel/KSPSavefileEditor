package at.woelfel.philip.kspsavefileeditor.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

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
import at.woelfel.philip.kspsavefileeditor.backend.Parser;

@SuppressWarnings("serial")
public class MainGui extends JFrame implements ActionListener{
	
	private JFileChooser mFileChooser;
	
	private JTable mEntryTable;
	private TreeWindow mTreeWindow;
	
	private JMenuItem mFileOpenSFSItem;
	private JMenuItem mFileOpenOtherItem;
	private JMenuItem mFileSaveItem;
	
	private JMenuItem mEditSearchItem;
	
	private JMenuItem mAboutInfoItem;
	private JMenuItem mAboutDebugItem;
	private JMenuItem mAboutFileDebugItem;
	

	
	private NodeTableModel mNodeTableModel;
	
	
	private Node mRootNode;

	
	public MainGui() {
		Logger.setEnabled(false);
		
		mFileChooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("KSP Save or Craft files (sfs, txt, craft, cfg)", "sfs", "txt", "craft", "cfg");
		mFileChooser.setFileFilter(filter);
		
			
		// ################################## Temp Nodes ##################################
		mRootNode = new Node("GAME", null);
		mRootNode.setIcon(Tools.readImage("nodes/game.png"));
		String[] tmpNodeNames = {"ACTIONGROUPS", "ACTIONS", "CREW", "EDITOR", "EVENTS", "FLIGHTSTATE", "MODULE", "ORBIT", "PART", "PLANETS", "RECRUIT", "ROSTER", "SCIENCE", "TRACKINGSTATION", "VESSEL", "VESSELS/FLAG", "VESSELS/BASE", "VESSELS/PROBE", "VESSELS/SPACEOBJECT"};
		for (int i = 0; i < tmpNodeNames.length; i++) {
			String tmpName = tmpNodeNames[i];
			Node tmpNode = new Node(tmpName, mRootNode);
			tmpNode.setIcon(Tools.readImage("nodes/" +tmpName.toLowerCase() +".png"));
			mRootNode.addSubNode(tmpNode);
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
		
		mTreeWindow = new TreeWindow(mRootNode, mNodeTableModel);
		
		mNodeTableModel.addChangeListener(mTreeWindow);
		
		JScrollPane nodeTreeJSP = new JScrollPane(mTreeWindow);
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
		
		
		setVisible(true);
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
			mFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int returnVal = mFileChooser.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				Logger.log("You chose to open this file: " + mFileChooser.getSelectedFile().getName());
				mTreeWindow.load(mFileChooser.getSelectedFile(), true);
			}
		}
		else if (source == mFileOpenOtherItem) {
			mFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int returnVal = mFileChooser.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				Logger.log("You chose to open this file: " + mFileChooser.getSelectedFile().getName());
				mTreeWindow.load(mFileChooser.getSelectedFile(), false);
			}
		}
		else if (source == mFileSaveItem) {
			mFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnVal = mFileChooser.showSaveDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				Logger.log("You chose to save this file: " + mFileChooser.getSelectedFile().getName());
				mTreeWindow.save(mFileChooser.getSelectedFile());

			}
		}
		else if (source == mEditSearchItem) {
			String search = JOptionPane.showInputDialog(this, "Please enter search term", "Search", JOptionPane.QUESTION_MESSAGE);
			if(!mTreeWindow.search(search)){
				// TODO show error here or in TreeWindow.search()?
			}
		}
	}
	
	

	public Node getRootNode() {
		return mRootNode;
	}

	public void setRootNode(Node mRootNode) {
		this.mRootNode = mRootNode;
		mTreeWindow.setRootNode(mRootNode);
		mNodeTableModel.setNode(null);
	}
	
	

}
