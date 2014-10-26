package at.woelfel.philip.kspsavefileeditor.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class CloseableTab extends JPanel {

	protected JTabbedPane mTabPane;

	public CloseableTab(JTabbedPane tabPane) {
		mTabPane = tabPane;
		setLayout(new FlowLayout(FlowLayout.CENTER, 2, 2));
		setOpaque(false);
		
		JLabel tabLabel = new JLabel() {
			public String getText() {
				int i = mTabPane.indexOfTabComponent(CloseableTab.this);
				if (i != -1) {
					return mTabPane.getTitleAt(i);
				}
				return null;
			}

			public Icon getIcon() {
				int i = mTabPane.indexOfTabComponent(CloseableTab.this);
				if (i != -1) {
					return mTabPane.getIconAt(i);
				}
				return null;
			}
		};
		// JLabel tabLabel = new JLabel();
		// tabLabel.setText(mTabPane.getTitleAt(mTabIndex));
		// tabLabel.setIcon(mTabPane.getIconAt(mTabIndex));
		add(tabLabel);

		JButton closeButton = new JButton("x");
		closeButton.setPreferredSize(new Dimension(17, 17));
		closeButton.setOpaque(false);
		closeButton.setContentAreaFilled(false);
		closeButton.setFocusable(false);
		closeButton.setToolTipText("close tab");
		closeButton.setBorder(BorderFactory.createRaisedBevelBorder());
        closeButton.setBorderPainted(false);
        closeButton.setRolloverEnabled(true);
        closeButton.addMouseListener(buttonMouseListener);
		closeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int i = mTabPane.indexOfTabComponent(CloseableTab.this);
				if (i != -1) {
					mTabPane.remove(i);
				}
			}
		});
		add(closeButton);
	}
	
	private final static MouseListener buttonMouseListener = new MouseAdapter() {
        public void mouseEntered(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(true);
            }
        }

        public void mouseExited(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(false);
            }
        }
    };

}
