package at.woelfel.philip.kspsavefileeditor;

import java.io.File;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import at.woelfel.philip.kspsavefileeditor.backend.Logger;
import at.woelfel.philip.kspsavefileeditor.gui.MainGui;

public class MainClass {

	private static Hashtable<String, ImageIcon> mImageCache;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MainGui mainGui = new MainGui();
	}
	
	public static ImageIcon readImage(String fname) {
		if(mImageCache == null){
			mImageCache = new Hashtable<String, ImageIcon>();
		}
		if(mImageCache.containsKey(fname)){
			Logger.log("Returning image from cache: " +fname);
			return mImageCache.get(fname);
		}
		try {
			ImageIcon img = new ImageIcon(ImageIO.read(new File("img/" + fname)));
			mImageCache.put(fname, img);
			return img;
		} catch (Exception e) {
			try {
				ImageIcon img = new ImageIcon(ImageIO.read(MainClass.class.getResource("/img/" + fname)));
				mImageCache.put(fname, img);
				return img;
			} catch (Exception e1) {
				return null;
			}
		}
	}
	
	
}
