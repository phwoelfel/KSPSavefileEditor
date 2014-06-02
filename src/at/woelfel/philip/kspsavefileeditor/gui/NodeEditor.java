package at.woelfel.philip.kspsavefileeditor.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import at.woelfel.philip.kspsavefileeditor.backend.ChangeListener;
import at.woelfel.philip.kspsavefileeditor.backend.Node;

public class NodeEditor extends JFrame implements ActionListener {

	private Node mNode;
	private Node mParentNode;
	
	private JTextField mNameField;
	private JButton mCancelButton;
	private JButton mSaveButton;
	private JList mSubnodeList;
	private JList mEntryList;
	
	private ArrayList<ChangeListener> mChangeListener;
	
	private int mEditorMode;
	private static final int MODE_NEW=1;
	private static final int MODE_EDIT=2;
	
	
	public NodeEditor() {
		
		// TODO: lists for subnodes and entries
		
		mChangeListener = new ArrayList<ChangeListener>();
		
		setTitle("Node Editor");
		setSize(400, 150);

		setLayout(new GridBagLayout());
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		JComponent con = (JComponent) getContentPane();
		con.setBorder(new TitledBorder("Node"));
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		JLabel nameLabel = new JLabel("Name");
		add(nameLabel, c);
		
		
		
		c.gridx = 0;
		c.gridy = 1;
		mNameField = new JTextField(30);
		add(mNameField, c);
		
		
		
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth=1;
		mCancelButton = new JButton("Cancel");
		mCancelButton.addActionListener(this);
		add(mCancelButton, c);
		
		c.gridx = 1;
		c.gridy = 2;
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
	
	private void fireNodeAdded(Node n){
		for(ChangeListener cl : mChangeListener){
			cl.onNodeAdded(n);
		}
	}
}
