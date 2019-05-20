import java.io.File;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
public class openFile {
	File selectedFile;
	JFileChooser jfc = new JFileChooser();
	
	
	public void pickMe() {
		FileNameExtensionFilter filter = new FileNameExtensionFilter("csv files", "csv");
		jfc.setFileFilter(filter);
		int returnValue = jfc.showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			selectedFile = jfc.getSelectedFile();
			//System.out.println(selectedFile.getAbsolutePath());
		}
	}

}
