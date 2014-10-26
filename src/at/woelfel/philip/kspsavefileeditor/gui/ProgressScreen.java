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
	private static JLabel mLabel;
	private static ProgressScreen mInstance;
	
	private ProgressScreen(String label, Component parent) {
		setAlwaysOnTop(true);
		setUndecorated(true);
		//setSize(200,40);
		setLayout(new BorderLayout());
		
		mProgressBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
		mProgressBar.setStringPainted(true);
		add(mProgressBar, BorderLayout.SOUTH);
		
		mLabel = new JLabel(label);
		add(mLabel, BorderLayout.CENTER);
		
		pack();
		setLocationRelativeTo(parent);
	}
	
	
	public static void showProgress(String label, Component parent){
		if(mInstance == null){
			mInstance = new ProgressScreen(label, parent);
		}
		else{
			mInstance.setLabel(label);
			mInstance.pack();
			mInstance.setLocationRelativeTo(parent);
		}
		mInstance.setVisible(true);
	}
	
	public static void hideProgress(){
		mInstance.setVisible(false);
	}
	
	
	private void setLabel(String label){
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
