package at.woelfel.philip.kspsavefileeditor;



import at.woelfel.philip.kspsavefileeditor.gui.MainGui;

public class MainClass {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		 javax.swing.SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	            	MainGui mainGui = new MainGui();
	            }
		 });
	}

}
