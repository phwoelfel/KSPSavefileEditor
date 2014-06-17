package at.woelfel.philip.kspsavefileeditor.gui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

public class ProgressScreen extends JFrame {

	private static JProgressBar mProgressBar;
	
	public ProgressScreen(Component parent) {
		setAlwaysOnTop(true);
		setUndecorated(true);
		setSize(200,40);
		setLayout(new BorderLayout());
		
		mProgressBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
		mProgressBar.setStringPainted(true);
		add(mProgressBar, BorderLayout.SOUTH);
		
		JLabel textLabel = new JLabel("Parsing savefile...");
		add(textLabel, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(parent);
	}
	
	
	
	public static void updateProgressBar(final int progressBarValue){
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				mProgressBar.setValue(progressBarValue);
				
			}
		});
	}
}
