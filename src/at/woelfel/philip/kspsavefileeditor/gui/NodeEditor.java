package at.woelfel.philip.kspsavefileeditor.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;

import at.woelfel.philip.kspsavefileeditor.backend.ChangeListener;
import at.woelfel.philip.kspsavefileeditor.backend.Entry;
import at.woelfel.philip.kspsavefileeditor.backend.Node;

public class NodeEditor extends JFrame implements ActionListener {

	private Node mNode;
	private Node mParentNode;
	
	private EntryEditor mEntryEditor;
	
	private JTextField mNameField;
	private JButton mCancelButton;
	private JButton mSaveButton;
	
	private JList mSubNodeList;
	private NodeListModel mSubNodeListModel;
	
	private JList mEntryList;
	private NodeListModel mEntryListModel;
	
	private JButton mNodeEditButton;
	private JButton mNodeDeleteButton;
	private JButton mEntryEditButton;
	private JButton mEntryDeleteButton;
	
	private ArrayList<ChangeListener> mChangeListener;
	
	private int mEditorMode;
	private static final int MODE_NEW=1;
	private static final int MODE_EDIT=2;
	
	
	public NodeEditor(EntryEditor editor) {
		mEntryEditor = editor;
		
		mChangeListener = new ArrayList<ChangeListener>();
		
		setTitle("Node Editor");
		setSize(600, 300);

		setLayout(new GridBagLayout());
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		JComponent con = (JComponent) getContentPane();
		con.setBorder(new TitledBorder("Node"));
		
		GridBagConstraints c = new GridBagConstraints();
		
		
		c.gridx = 0;
		c.gridy = 0;
		JLabel nameLabel = new JLabel("Name");
		add(nameLabel, c);
		
		
		
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 2;
		mNameField = new JTextField(10);
		add(mNameField, c);
		
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.weightx = 0.2;
		mSubNodeListModel = new NodeListModel(NodeListModel.MODE_SUBNODES);
		mSubNodeList = new JList(mSubNodeListModel);
		mSubNodeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		add(new JScrollPane(mSubNodeList), c);
		
		
		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 2;
		c.weightx = 0.8;
		mEntryListModel = new NodeListModel(NodeListModel.MODE_ENTRIES);
		mEntryList = new JList(mEntryListModel);
		mEntryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		add(new JScrollPane(mEntryList), c);
		
		
		/*c.gridx = 0;
		c.gridy = 2;
		mNodeEditButton = new JButton("Edit", new ImageIcon("img/edit.png"));
		mNodeEditButton.addActionListener(this);
		add(mNodeEditButton, c);*/
		
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 1;
		c.weightx = 1;
		c.gridx = 0;
		c.gridy = 2;
		mNodeDeleteButton = new JButton("Delete", readImage("delete.png"));
		mNodeDeleteButton.addActionListener(this);
		add(mNodeDeleteButton, c);
		
		
		
		c.gridx = 1;
		c.gridy = 2;
		mEntryEditButton = new JButton("Edit", readImage("edit.png"));
		mEntryEditButton.addActionListener(this);
		add(mEntryEditButton, c);
		
		
		c.gridx = 2;
		c.gridy = 2;
		mEntryDeleteButton = new JButton("Delete", readImage("delete.png"));
		mEntryDeleteButton.addActionListener(this);
		add(mEntryDeleteButton, c);
		
		
		c.insets = new Insets(10, 0, 0, 0);
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 1;
		mCancelButton = new JButton("Cancel");
		mCancelButton.addActionListener(this);
		add(mCancelButton, c);
		
		c.gridx = 1;
		c.gridy = 3;
		c.gridwidth=2;
		mSaveButton = new JButton("Save");
		mSaveButton.addActionListener(this);
		add(mSaveButton, c);
	}

	
	public void showForNew(Node parent){
		mEditorMode = MODE_NEW;
		mParentNode = parent;
		setNode(new Node("", parent));
		
		setVisible(true);
	}
	
	public void showForEdit(Node node){
		mEditorMode = MODE_EDIT;
		setNode(node);
		
		setVisible(true);
	}
	
	public void setNode(Node n){
		if(n!=null){
			mNode = n;
			mNameField.setText(mNode.getNodeName());
			mSubNodeListModel.setNode(mNode);
			mEntryListModel.setNode(mNode);
		}
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == mCancelButton){
			setVisible(false);
		}
		else if(e.getSource() == mSaveButton){
			if(MODE_NEW == mEditorMode){
				mNode.setNodeName(mNameField.getText());
				mParentNode.addSubNode(mNode);
				fireNodeAdded(mNode);
				
				setVisible(false);
			}
			else if(MODE_EDIT == mEditorMode){
				mNode.setNodeName(mNameField.getText());
				fireNodeChanged(mNode);
				
				setVisible(false);
			}
		}
		else if(e.getSource() == mEntryDeleteButton){
			if(mEntryList.getSelectedValue() != null){
				int yesno = JOptionPane.showConfirmDialog(this, "Do you really want to delete this Entry?", "Are you sure?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				if(yesno == JOptionPane.YES_OPTION){
					Entry entry = (Entry) mEntryList.getSelectedValue();
					mNode.removeEntry(entry);
					mEntryListModel.nodeUpdated();
					fireEntryRemoved(entry);
				}
			}
		}
		else if(e.getSource() == mEntryEditButton){
			if(mEntryList.getSelectedValue() != null){
				Entry entry = (Entry) mEntryList.getSelectedValue();
				mEntryEditor.showForEdit(entry);
			}
		}
		else if(e.getSource() == mNodeDeleteButton){
			if(mSubNodeList.getSelectedValue() != null){
				int yesno = JOptionPane.showConfirmDialog(this, "Do you really want to delete this Node?", "Are you sure?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				if(yesno == JOptionPane.YES_OPTION){
					Node node = (Node) mSubNodeList.getSelectedValue();
					mNode.removeSubNode(node);
					mSubNodeListModel.nodeUpdated();
					fireNodeRemoved(node);
				}
			}
		}
		
	}
	
	public void addChangeListener(ChangeListener cl){
		mChangeListener.add(cl);
	}
	
	public void removeChangeListener(ChangeListener cl){
		mChangeListener.remove(cl);
	}
	
	private void fireNodeChanged(Node n){
		for(ChangeListener cl : mChangeListener){
			cl.onNodeChanged(n);
		}
	}
	
	private void fireNodeRemoved(Node n){
		for(ChangeListener cl : mChangeListener){
			cl.onNodeRemoved(n);
		}
	}
	
	private void fireNodeAdded(Node n){
		for(ChangeListener cl : mChangeListener){
			cl.onNodeAdded(n);
		}
	}
	
	private void fireEntryRemoved(Entry e){
		for(ChangeListener cl : mChangeListener){
			cl.onEntryRemoved(e);
		}
	}
	
	
	private void fireEntryChanged(Entry e){
		for(ChangeListener cl : mChangeListener){
			cl.onEntryChanged(e);
		}
	}
	
	private void fireEntryAdded(Entry e){
		for(ChangeListener cl : mChangeListener){
			cl.onEntryAdded(e);
		}
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
