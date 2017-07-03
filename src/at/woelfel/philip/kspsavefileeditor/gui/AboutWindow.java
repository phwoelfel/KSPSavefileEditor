package at.woelfel.philip.kspsavefileeditor.gui;

import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.TitledBorder;

public class AboutWindow extends JFrame {
	private static final long serialVersionUID = 136000L;
	public AboutWindow() {
		setTitle("About");
		setSize(520, 180);

		setLayout(new GridBagLayout());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JComponent con = (JComponent) getContentPane();
		con.setBorder(new TitledBorder("About"));
		
		JLabel text = new JLabel("<html>(c) Philip Woelfel &lt;philip[at]woelfel.at&gt;<br /><hr /><br />Icons:\n(C) 2012 Yusuke Kamiyamane. All rights reserved.<br />"+
				"These icons are licensed under a Creative Commons Attribution 3.0 License.<br />"+
				"<a href=\"http://creativecommons.org/licenses/by/3.0/\">http://creativecommons.org/licenses/by/3.0/</a></html>");
		add(text);
		
		setVisible(true);
	}
}
