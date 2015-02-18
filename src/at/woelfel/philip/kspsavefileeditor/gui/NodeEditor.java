package at.woelfel.philip.kspsavefileeditor.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;

import at.woelfel.philip.kspsavefileeditor.backend.ChangeListener;
import at.woelfel.philip.kspsavefileeditor.backend.Entry;
import at.woelfel.philip.kspsavefileeditor.backend.Node;

@SuppressWarnings("serial")
public class NodeEditor extends JFrame implements ActionListener {

	private Node mNode;
	private Node mParentNode;

	private EntryEditor mEntryEditor;

	private JTextField mNameField;
	private JButton mCancelButton;
	private JButton mSaveButton;

	JSplitPane contentPane;

	private JList mSubNodeList;
	private NodeListModel mSubNodeListModel;

	private JList mEntryList;
	private NodeListModel mEntryListModel;

	private JButton mNodeDeleteButton;
	private JButton mEntryEditButton;
	private JButton mEntryDeleteButton;

	private ArrayList<ChangeListener> mChangeListener;

	private int mEditorMode;
	private static final int MODE_NEW = 1;
	private static final int MODE_EDIT = 2;

	public NodeEditor(EntryEditor editor) {
		mEntryEditor = editor;

		createGUI();

		mChangeListener = new ArrayList<ChangeListener>();

	}

	// always a good idea to have a separate method for creating the GUI. Keeps things cleaner.
	private void createGUI() {
		setTitle("Node Editor");
		setSize(800, 450);

		setLayout(new GridBagLayout());
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		JComponent con = (JComponent) getContentPane();
		con.setBorder(new TitledBorder("Node"));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(2, 2, 2, 2);

		JPanel namePanel = new JPanel(new GridBagLayout());
		// namePanel.setBorder(BorderFactory.createLineBorder(Color.red));

		// GridBagConstraints variables repeated for the sake of readability.
		// (Also helps to reduce unintentional errors)

		// NamePanel:

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.NONE;
		JLabel nameLabel = new JLabel("Name");
		namePanel.add(nameLabel, gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		mNameField = new JTextField(30);
		namePanel.add(mNameField, gbc);
		gbc.weightx = 0;

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 3;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(namePanel, gbc);
		// ^NamePanel

		contentPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		contentPane.setDividerLocation(0.3);
		contentPane.setBorder(BorderFactory.createTitledBorder(""));

		// LeftPanel:
		JPanel leftPanel = new JPanel(new GridBagLayout());
		// leftPanel.setBorder(BorderFactory.createLineBorder(Color.blue));//

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		mSubNodeListModel = new NodeListModel(NodeListModel.MODE_SUBNODES);
		mSubNodeList = new JList(mSubNodeListModel);
		mSubNodeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		leftPanel.add(new JScrollPane(mSubNodeList), gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.NONE;
		mNodeDeleteButton = new JButton("Delete", readImage("delete.png"));
		mNodeDeleteButton.addActionListener(this);
		leftPanel.add(mNodeDeleteButton, gbc);

		// add a dummy label to soak up unused space
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		leftPanel.add(new JLabel(), gbc);

		contentPane.setLeftComponent(leftPanel);
		// ^LeftPanel

		// RightPanel:
		JPanel rightPanel = new JPanel(new GridBagLayout());
		// rightPanel.setBorder(BorderFactory.createLineBorder(Color.green));

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 3;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		mEntryListModel = new NodeListModel(NodeListModel.MODE_ENTRIES);
		mEntryList = new JList(mEntryListModel);
		mEntryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		rightPanel.add(new JScrollPane(mEntryList), gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.NONE;
		mEntryEditButton = new JButton("Edit", readImage("edit.png"));
		mEntryEditButton.addActionListener(this);
		rightPanel.add(mEntryEditButton, gbc);

		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.NONE;
		mEntryDeleteButton = new JButton("Delete", readImage("delete.png"));
		mEntryDeleteButton.addActionListener(this);
		rightPanel.add(mEntryDeleteButton, gbc);

		// add a dummy label to soak up unused space
		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		rightPanel.add(new JLabel(), gbc);

		contentPane.setRightComponent(rightPanel);

		// ^RightPanel

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		add(contentPane, gbc);

		// ButtonPanel:
		JPanel buttonPanel = new JPanel(new GridBagLayout());

		// buttonPanel.setBorder(BorderFactory.createLineBorder(Color.red));

		gbc.insets = new Insets(10, 2, 2, 2);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.NONE;
		mCancelButton = new JButton("Cancel");
		mCancelButton.addActionListener(this);
		buttonPanel.add(mCancelButton, gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.NONE;
		mSaveButton = new JButton("Save");
		mSaveButton.addActionListener(this);
		buttonPanel.add(mSaveButton, gbc);

		// add a dummy label to soak up unused space
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		buttonPanel.add(new JLabel(), gbc);

		gbc.insets = new Insets(10, 2, 2, 2);
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		add(buttonPanel, gbc);
		// ^ButtonPanel

	}

	public void showForNew(Node parent) {
		mEditorMode = MODE_NEW;
		mParentNode = parent;
		setNode(new Node("", parent));

		setVisible(true);
	}

	public void showForEdit(Node node) {
		mEditorMode = MODE_EDIT;
		setNode(node);

		setVisible(true);
	}

	public void setNode(Node n) {
		if (n != null) {
			mNode = n;
			mNameField.setText(mNode.getNodeName());
			mSubNodeListModel.setNode(mNode);
			mEntryListModel.setNode(mNode);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == mCancelButton) {
			setVisible(false);
		}
		else if (e.getSource() == mSaveButton) {
			if (MODE_NEW == mEditorMode) {
				mNode.setNodeName(mNameField.getText());
				mParentNode.addSubNode(mNode);
				fireNodeAdded(mNode);

				setVisible(false);
			}
			else if (MODE_EDIT == mEditorMode) {
				mNode.setNodeName(mNameField.getText());
				fireNodeChanged(mNode);

				setVisible(false);
			}
		}
		else if (e.getSource() == mEntryDeleteButton) {
			if (mEntryList.getSelectedValue() != null) {
				int yesno = JOptionPane.showConfirmDialog(this, "Do you really want to delete this Entry?", "Are you sure?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				if (yesno == JOptionPane.YES_OPTION) {
					Entry entry = (Entry) mEntryList.getSelectedValue();
					mNode.removeEntry(entry);
					mEntryListModel.nodeUpdated();
					fireEntryRemoved(entry);
				}
			}
		}
		else if (e.getSource() == mEntryEditButton) {
			if (mEntryList.getSelectedValue() != null) {
				Entry entry = (Entry) mEntryList.getSelectedValue();
				mEntryEditor.showForEdit(entry);
			}
		}
		else if (e.getSource() == mNodeDeleteButton) {
			if (mSubNodeList.getSelectedValue() != null) {
				int yesno = JOptionPane.showConfirmDialog(this, "Do you really want to delete this Node?", "Are you sure?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				if (yesno == JOptionPane.YES_OPTION) {
					Node node = (Node) mSubNodeList.getSelectedValue();
					mNode.removeSubNode(node);
					mSubNodeListModel.nodeUpdated();
					fireNodeRemoved(node);
				}
			}
		}

	}

	public void addChangeListener(ChangeListener cl) {
		mChangeListener.add(cl);
	}

	public void removeChangeListener(ChangeListener cl) {
		mChangeListener.remove(cl);
	}

	private void fireNodeChanged(Node n) {
		for (ChangeListener cl : mChangeListener) {
			cl.onNodeChanged(n);
		}
	}

	private void fireNodeRemoved(Node n) {
		for (ChangeListener cl : mChangeListener) {
			cl.onNodeRemoved(n);
		}
	}

	private void fireNodeAdded(Node n) {
		for (ChangeListener cl : mChangeListener) {
			cl.onNodeAdded(n);
		}
	}

	private void fireEntryRemoved(Entry e) {
		for (ChangeListener cl : mChangeListener) {
			cl.onEntryRemoved(e);
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
