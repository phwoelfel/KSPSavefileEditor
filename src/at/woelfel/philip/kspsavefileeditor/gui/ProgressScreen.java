package at.woelfel.philip.kspsavefileeditor.gui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class ProgressScreen extends JFrame {

	private static JProgressBar mProgressBar;
	private JLabel mLabel;
	
	public ProgressScreen(String label, Component parent) {
		setAlwaysOnTop(true);
		setUndecorated(true);
		setSize(200,40);
		setLayout(new BorderLayout());
		
		mProgressBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
		mProgressBar.setStringPainted(true);
		add(mProgressBar, BorderLayout.SOUTH);
		
		mLabel = new JLabel(label);
		add(mLabel, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(null);
	}
	
	
	public void setLabel(String label){
		if(mLabel!=null){
			mLabel.setText(label);
		}
	}
	
	public static void updateProgressBar(final int progressBarValue){
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				if(mProgressBar!=null){
					mProgressBar.setValue(progressBarValue);
				}
			}
		});
	}
}
