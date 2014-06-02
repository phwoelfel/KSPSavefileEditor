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
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import at.woelfel.philip.kspsavefileeditor.backend.Entry;
import at.woelfel.philip.kspsavefileeditor.backend.ChangeListener;
import at.woelfel.philip.kspsavefileeditor.backend.Node;

public class EntryEditor extends JFrame implements ActionListener {

	private Entry mEntry;
	private Node mParentNode;
	
	private JTextField mKeyField;
	private JTextField mValueField;
	private JButton mCancelButton;
	private JButton mSaveButton;
	
	private ArrayList<ChangeListener> mChangeListener;
	
	private int mEditorMode;
	private static final int MODE_NEW=1;
	private static final int MODE_EDIT=2;
	
	
	public EntryEditor() {
		
		mChangeListener = new ArrayList<ChangeListener>();
		
		setTitle("Entry Editor");
		setSize(400, 150);

		setLayout(new GridBagLayout());
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		JComponent con = (JComponent) getContentPane();
		con.setBorder(new TitledBorder("Entry"));
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = 0;
		JLabel keyLabel = new JLabel("Key");
		add(keyLabel, c);
		
		c.gridx = 1;
		c.gridy = 0;
		JLabel valueLabel = new JLabel("Value");
		add(valueLabel, c);
		
		c.gridx = 0;
		c.gridy = 1;
		mKeyField = new JTextField(15);
		add(mKeyField, c);
		
		c.gridx = 1;
		c.gridy = 1;
		mValueField = new JTextField(15);
		add(mValueField, c);
		
		
		c.gridx = 0;
		c.gridy = 2;
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
		setEntry(new Entry(parent, "", ""));
		
		setVisible(true);
	}
	
	public void showForEdit(Entry entry){
		mEditorMode = MODE_EDIT;
		setEntry(entry);
		
		setVisible(true);
	}
	
	public Entry getEntry() {
		return mEntry;
	}

	public void setEntry(Entry mEntry) {
		if(mEntry!=null){
			this.mEntry = mEntry;
			mKeyField.setText(mEntry.getKey());
			mValueField.setText(mEntry.getValue());
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == mCancelButton){
			setVisible(false);
		}
		else if(e.getSource() == mSaveButton){
			if(MODE_NEW == mEditorMode){
				mEntry.setKey(mKeyField.getText());
				mEntry.setValue(mValueField.getText());
				mParentNode.addEntry(mEntry);
				fireEntryAdded(mEntry);
				
				setVisible(false);
			}
			else if(MODE_EDIT == mEditorMode){
				mEntry.setKey(mKeyField.getText());
				mEntry.setValue(mValueField.getText());
				fireEntryChanged(mEntry);
				
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
}
