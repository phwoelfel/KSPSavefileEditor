package at.woelfel.philip.kspsavefileeditor;

import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenuItem;

import at.woelfel.philip.kspsavefileeditor.backend.Logger;

public class Tools {

	private static Hashtable<String, ImageIcon> mImageCache;
	
	public static ImageIcon readImage(String fname) {
		if (Tools.mImageCache == null) {
			Tools.mImageCache = new Hashtable<String, ImageIcon>();
		}
		if (Tools.mImageCache.containsKey(fname)) {
			Logger.log("Returning image from cache: " + fname);
			return Tools.mImageCache.get(fname);
		}
		try {
			ImageIcon img = new ImageIcon(ImageIO.read(new File("img/" + fname)));
			Tools.mImageCache.put(fname, img);
			return img;
		} catch (Exception e) {
			try {
				ImageIcon img = new ImageIcon(ImageIO.read(MainClass.class.getResource("/img/" + fname)));
				Tools.mImageCache.put(fname, img);
				return img;
			} catch (Exception e1) {
				return null;
			}
		}
	}

	public static JMenuItem initializeMenuItem(JComponent parent, String text, ActionListener al, ImageIcon img) {
		JMenuItem item = new JMenuItem(text, img);
		item.addActionListener(al);
		parent.add(item);
		return item;
	}

	public static JMenuItem initializeMenuItem(JComponent parent, String text, ActionListener al) {
		JMenuItem item = new JMenuItem(text);
		item.addActionListener(al);
		parent.add(item);
		return item;
	}
	
	
	public static JCheckBoxMenuItem initializeCheckboxMenuItem(JComponent parent, String text, ItemListener al, ImageIcon img) {
		JCheckBoxMenuItem item = new JCheckBoxMenuItem(text, img);
		item.addItemListener(al);
		parent.add(item);
		return item;
	}

	public static JCheckBoxMenuItem initializeCheckboxMenuItem(JComponent parent, String text, ItemListener al) {
		JCheckBoxMenuItem item = new JCheckBoxMenuItem(text);
		item.addItemListener(al);
		parent.add(item);
		return item;
	}

	

}
